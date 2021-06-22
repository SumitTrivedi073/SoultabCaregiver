package com.soultabcaregiver.activity.docter;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.soultabcaregiver.Model.DiloagBoxCommon;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.docter.DoctorModel.AppointmentRequestModel;
import com.soultabcaregiver.activity.docter.DoctorModel.DoctorAppointmentList;
import com.soultabcaregiver.sinch_calling.BaseActivity;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class UpdateDoctorAppointmentActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    public static final int RequestPermissionCode = 3;
    private final String TAG = getClass().getSimpleName();
    Context mContext;
    TextView tvDocNm, txt_doctor_address, txt_mobile_number, tvDate, tv_time, txt_doctor_email, txt_fax, txt_Portal, cancel_appointment_btn;
    RelativeLayout back_btn;
    Switch tbDocAppTog;

    RelativeLayout rlDate, rl_time;
    String myFormat = "MM-dd-yyyy";
    SimpleDateFormat sdf;
    String myFormat1 = "yyyy-MM-dd";//for webservice
    SimpleDateFormat sdf1;
    String sDate = "";
    TextView tvMakeAppoint;

    Calendar myCalendar;
    String AppointmentId,DoctorID;
    String curDate = "";
    
    LinearLayout main_call_layout, call_end_layout;
    TextView call_state;
    AlertDialog alertDialog;
    LinearLayout btn_call, decline_call;
    DoctorAppointmentList.Response.AppointmentDatum appointmentDatum;
    String sSelTimeId = "", sSelTimeNm = "", sSelDateId = "", setToggle = "", sSelTimeSlotNm = "";
    
    private Context context;
    private int Year, Month, Day;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_appointment);

        mContext = this;
        context = this;
        InitCompo();
        GetValuFromIntent();
        Listener();
    }

    private void InitCompo() {
        back_btn = findViewById(R.id.back_btn);
        tvDocNm = findViewById(R.id.txt_doctor_name);
        txt_doctor_address = findViewById(R.id.txt_doctor_address);
        txt_mobile_number = findViewById(R.id.txt_mobile_number);

        tvDocNm = findViewById(R.id.txt_doctor_name);
        txt_doctor_address = findViewById(R.id.txt_doctor_address);
        txt_mobile_number = findViewById(R.id.txt_mobile_number);
        txt_doctor_email = findViewById(R.id.txt_doctor_email);

        tbDocAppTog = findViewById(R.id.tb_doc_appoint);
        rlDate = findViewById(R.id.rl_date);
        tvDate = findViewById(R.id.tv_date);
        tv_time = findViewById(R.id.tv_time);
        rl_time = findViewById(R.id.rl_time);
        tvMakeAppoint = findViewById(R.id.tv_make_appoi);
        txt_fax = findViewById(R.id.txt_fax);
        txt_Portal = findViewById(R.id.txt_Portal);
        cancel_appointment_btn = findViewById(R.id.cancel_appointment_btn);

        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        sdf1 = new SimpleDateFormat(myFormat1, Locale.US);

        myCalendar = Calendar.getInstance();
        curDate = sdf.format(calendar.getTime());
    
    
    }

  /*  private void GetValuFromIntent() {

        appointmentDatum = (DoctorAppointmentList.Response.AppointmentDatum) getIntent().getSerializableExtra(APIS.DocListItem);

        assert appointmentDatum != null;
        AppointmentId = String.valueOf(appointmentDatum.getAppointmentId());
        tvDocNm.setText(appointmentDatum.getDoctorName());
        txt_doctor_address.setText(appointmentDatum.getDoctorAddress());
        txt_mobile_number.setText(appointmentDatum.getDoctorMobile());
        txt_fax.setText(appointmentDatum.getFax());
        txt_Portal.setText(appointmentDatum.getWebsite());
        if (appointmentDatum.getEmail() != null) {
            txt_doctor_email.setText(appointmentDatum.getEmail());
        }

        tvDate.setText(String.valueOf(Utility.ChangeDateFormat("yyyy-MM-dd", "MM-dd-yyyy", appointmentDatum.getDate())));
        sSelDateId = appointmentDatum.getDate();


        if (String.valueOf(appointmentDatum.getAppointmentsReminder()).equals("1")) {
            tbDocAppTog.setChecked(true);
        } else {
            tbDocAppTog.setChecked(false);
        }


        tv_time.setText(appointmentDatum.getTime());
        sSelTimeId = appointmentDatum.getTime();

    }
*/

    private void GetValuFromIntent() {


        if (!String.valueOf(getIntent().getStringExtra("diff_")).equals("2")) {
            appointmentDatum = (DoctorAppointmentList.Response.AppointmentDatum) getIntent().getSerializableExtra(APIS.DocListItem);

            assert appointmentDatum != null;
            AppointmentId = String.valueOf(appointmentDatum.getAppointmentId());
            DoctorID = String.valueOf(appointmentDatum.getDoctor_id());
            tvDocNm.setText(appointmentDatum.getDoctorName());
            txt_doctor_address.setText(appointmentDatum.getDoctorAddress());
            txt_mobile_number.setText(appointmentDatum.getDoctorMobile());
            txt_fax.setText(appointmentDatum.getFax());
            txt_Portal.setText(appointmentDatum.getWebsite());
            if (appointmentDatum.getEmail() != null) {
                txt_doctor_email.setText(appointmentDatum.getEmail());
            }

            tvDate.setText(String.valueOf(Utility.ChangeDateFormat("yyyy-MM-dd", "MM-dd-yyyy", appointmentDatum.getDate())));
            sSelDateId = appointmentDatum.getDate();


            tbDocAppTog.setChecked(String.valueOf(appointmentDatum.getAppointmentsReminder()).equals("1"));


            tv_time.setText(appointmentDatum.getTime());
            sSelTimeId = appointmentDatum.getTime();

        } else {
            AppointmentId = getIntent().getStringExtra("id");
            DoctorID = getIntent().getStringExtra("Doctor_id");
            GetDoctorApointmentDetail();
        }
    }

    private void GetDoctorApointmentDetail() {
        final String TAG = "Get Doc details";
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("appointment_id", AppointmentId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("", "show_appointments= " + mainObject.toString());
        showProgressDialog(getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.DOC_APPOIN_DETAILS_API, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Get Doc details response=" + response.toString());
                        hideProgressDialog();

                        try {
                            String code = response.getString("status_code");
                            if (code.equals("200")) {
                                try {
                                    JSONArray jsArray = response.getJSONObject("response").getJSONArray("appointment_data");
                                    if (jsArray != null && jsArray.length() > 0) {

                                        JSONObject jsDocAvaiTimeMain = jsArray.getJSONObject(0);
                                        tvDocNm.setText(jsDocAvaiTimeMain.getString("doctor_name"));
                                        txt_doctor_address.setText(jsDocAvaiTimeMain.getString("doctor_address"));
                                        txt_mobile_number.setText(jsDocAvaiTimeMain.getString("contact"));

                                        tvDate.setText(String.valueOf(Utility.ChangeDateFormat("yyyy-MM-dd", "MM-dd-yyyy",jsDocAvaiTimeMain.getString("date_id") )));

                                        sSelDateId = jsDocAvaiTimeMain.getString("date_id");
                                        Log.e("sSelDateId", sSelDateId);


                                        tbDocAppTog.setChecked(jsDocAvaiTimeMain.getString("reminder").equals("1"));


                                        tv_time.setText(jsDocAvaiTimeMain.getString("time_id"));
                                        sSelTimeId = jsDocAvaiTimeMain.getString("time_id");


                                        if (!TextUtils.isEmpty(getIntent().getStringExtra("Doctor_Fax"))) {
                                            txt_fax.setText(getIntent().getStringExtra("Doctor_Fax"));
                                        }

                                        if (!TextUtils.isEmpty(getIntent().getStringExtra("Doctor_Website"))) {
                                            txt_Portal.setText(getIntent().getStringExtra("Doctor_Website"));
                                        }

                                        if (!TextUtils.isEmpty(getIntent().getStringExtra("Doctor_Email"))) {
                                            txt_doctor_email.setText(getIntent().getStringExtra("Doctor_Email"));
                                        }

                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (code.equals("403")) {
                                logout_app(response.getString("message"));
                            } else {

                                Utility.ShowToast(mContext, response.getString("message"));
                                hideProgressDialog();
                                finish();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext, APIS.EncodeUser_id));
                return params;
            }

        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }



    private void Listener() {
        rlDate.setOnClickListener(this);
        rl_time.setOnClickListener(this);
        tvMakeAppoint.setOnClickListener(this);
        back_btn.setOnClickListener(this);
        cancel_appointment_btn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.rl_date:
                ChooseDate();
                break;

            case R.id.rl_time:
                chooseTimePicker();
                break;

            case R.id.tv_make_appoi:
                if (Utility.isNetworkConnected(mContext)) {

                    if (sdf.format(new Date()).equals((tvDate.getText().toString()))) {
                        Log.e("equal true", "equal true");


                        if (TextUtils.isEmpty(sSelTimeId)) {
                            Utility.ShowToast(mContext, getResources().getString(R.string.doctor_appointment_time));

                        } else if (TextUtils.isEmpty(sSelDateId)) {

                            Utility.ShowToast(mContext, getResources().getString(R.string.doctor_appointment_date));
                        } else {
                            GetDocAvailableTime(1);
                        }
                    } else {

                        if (TextUtils.isEmpty(sSelTimeId)) {
                            Utility.ShowToast(mContext, getResources().getString(R.string.doctor_appointment_time));
                        } else if (TextUtils.isEmpty(sSelDateId)) {
                            Utility.ShowToast(mContext, getResources().getString(R.string.doctor_appointment_date));
                        } else {
                            GetDocAvailableTime(1);
                        }

                    }
                } else {
                    Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
                }

                break;
            case R.id.cancel_appointment_btn:
                GetDocAvailableTime(2);
                break;
        }
    }

    private void AccessCall(String number, String name) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.assistent_layout,
                null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        btn_call = layout.findViewById(R.id.btn_call);
        decline_call = layout.findViewById(R.id.decline_call);
        call_state = layout.findViewById(R.id.callState);
        main_call_layout = layout.findViewById(R.id.main_call_layout);
        call_end_layout = layout.findViewById(R.id.call_end_layout);
        TextView title_txt = layout.findViewById(R.id.title_txt);
        CircleImageView user_img = layout.findViewById(R.id.user_img);
        RelativeLayout close_window = layout.findViewById(R.id.close_window);

        title_txt.setText(getResources().getString(R.string.Call_caregiver) + " " + name + " ?");

        alertDialog.show();

        close_window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        call_end_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    
            }
        });

    }

    private void ChooseDate() {
        try {

            calendar = Calendar.getInstance();
            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, mDay);

            Locale locale = getResources().getConfiguration().locale;
            Locale.setDefault(locale);
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(UpdateDoctorAppointmentActivity.this, mYear, mMonth, mDay);
            //datePickerDialog.showYearPickerFirst(true);
            datePickerDialog.setMinDate(calendar);
            datePickerDialog.setThemeDark(false);
            datePickerDialog.setAccentColor(Color.parseColor("#2b7ab9"));
            datePickerDialog.show(getFragmentManager(), "DatePickerDialog");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


        StringBuilder Date2 = new StringBuilder();
        Date2.delete(0, Date2.length());
        Date2.append((monthOfYear + 1) < 10 ? "0" : "")
                .append((monthOfYear + 1))
                .append("-").append((dayOfMonth < 10 ? "0" : "")).append(dayOfMonth).append("-").append(year);
        sSelDateId = Utility.yyyy_MM_dd.format(calendar.getTime());
        tvDate.setText(Date2);

        Log.e("date", sSelDateId);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    public void chooseTimePicker() {
        try {
            // Get Current Time
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DialogTheme,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                            if(calendar.before(GregorianCalendar.getInstance())){
                                Utility.ShowToast(mContext,getResources().getString(R.string.select_future_time));
                            } else {
                                Calendar datetime=Calendar.getInstance();
                                datetime.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                datetime.set(Calendar.MINUTE,minute);
                                tv_time.setText(Utility.hh_mm_aa.format(calendar.getTime()));

                                sSelTimeId = Utility.hh_mm_aa.format(calendar.getTime());

                            }
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
            timePickerDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void GetDocAvailableTime(int diff) {
        if (Utility.isNetworkConnected(this)) {
            switch (diff) {
                case 1:
                    UpdateDocAppointment();
                    break;

                case 2:
                    final DiloagBoxCommon diloagBoxCommon = Alertmessage(mContext, mContext.getResources().getString(R.string.cancel_appointment)
                            , mContext.getResources().getString(R.string.are_you_sure_cancel_appointment)
                            , mContext.getResources().getString(R.string.no_text)
                            , mContext.getResources().getString(R.string.yes_text));
                    diloagBoxCommon.getTextView().setOnClickListener(v1 -> {
                        diloagBoxCommon.getDialog().dismiss();

                        CancelDocAppointment();
                    });

                    break;

            }

        } else {

            Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));

        }

    }

    private void CancelDocAppointment() {
        final String TAG = "Delete AppointedDoc";
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("appointment_id", AppointmentId);
            mainObject.put("user_id", Utility.getSharedPreferences(mContext, APIS.user_id));
            mainObject.put("caregiver_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));

            Log.e(TAG, "appointmentCancel======>" + mainObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showProgressDialog(getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.DELETE_DOC_APPOIN_API, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Cancel AppointedDoc response=" + response.toString());
                        hideProgressDialog();
                        try {
                            String code = response.getString("status_code");
                            if (code.equals("200")) {

                                Utility.ShowToast(mContext, getResources().getString(R.string.cancel_appointment_successfully));

                                onBackPressed();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext, APIS.EncodeUser_id));
                return params;
            }

        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    private void UpdateDocAppointment() {

        final String TAG = "UpdateDocAppointment";
        JSONObject mainObject = new JSONObject();
        try {

            mainObject.put("appointment_id", AppointmentId);
            mainObject.put("user_id", Utility.getSharedPreferences(mContext, APIS.user_id));
            mainObject.put("caregiver_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));
            mainObject.put("date_id", sSelDateId);
            mainObject.put("time_id", sSelTimeId);

            if (tbDocAppTog.isChecked()) {

                mainObject.put("reminder", "1");

            } else {

                mainObject.put("reminder", "0");

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "UpdateDocAppointment:input data=  " + mainObject.toString());
        showProgressDialog(getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.DOC_UPDATE_APPOIN_API, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "UpdateDocAppointment response=" + response.toString());

                        hideProgressDialog();

                        AppointmentRequestModel requestModel = new Gson().fromJson(response.toString(), AppointmentRequestModel.class);

                        try {

                            if (requestModel.getStatusCode() == 200) {

                                Utility.ShowToast(mContext, requestModel.getMessage());

                                DoctorConectingPopup();

                            } else if (String.valueOf(requestModel.getStatusCode()).equals("403")) {
                                logout_app(requestModel.getMessage());
                            } else {

                                Utility.ShowToast(mContext, requestModel.getMessage());
                                hideProgressDialog();
                                finish();

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            hideProgressDialog();

                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext, APIS.EncodeUser_id));
                return params;
            }

        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    private void DoctorConectingPopup() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.doctor_function_popup,
                null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);

        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        RelativeLayout Call_btn = layout.findViewById(R.id.Call_btn);
        RelativeLayout sendFax_btn = layout.findViewById(R.id.sendFax_btn);
        RelativeLayout Portal = layout.findViewById(R.id.Portal);
        RelativeLayout close_popup = layout.findViewById(R.id.close_popup);

        if (TextUtils.isEmpty(txt_mobile_number.getText().toString())) {
            Call_btn.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) sendFax_btn.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            sendFax_btn.setLayoutParams(params);

            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) Portal.getLayoutParams();
            params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            Portal.setLayoutParams(params2);
        }


        if (TextUtils.isEmpty(txt_Portal.getText().toString())) {
            Portal.setVisibility(View.GONE);
            Call_btn.setVisibility(View.GONE);
          /*  RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) Call_btn.getLayoutParams();
            params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            Call_btn.setLayoutParams(params1);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) sendFax_btn.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            sendFax_btn.setLayoutParams(params);*/
        }

        if (TextUtils.isEmpty(txt_fax.getText().toString())) {
            sendFax_btn.setVisibility(View.GONE);
            Call_btn.setVisibility(View.GONE);
           /* RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) Call_btn.getLayoutParams();
            params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            Call_btn.setLayoutParams(params1);*/

            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) Portal.getLayoutParams();
            params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            Portal.setLayoutParams(params2);

        }

        if (!TextUtils.isEmpty(txt_Portal.getText().toString()) && !TextUtils.isEmpty(txt_fax.getText().toString())) {

           /* RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) Call_btn.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            Call_btn.setLayoutParams(params);*/
            Call_btn.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) Portal.getLayoutParams();
            params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            Portal.setLayoutParams(params1);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) sendFax_btn.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            sendFax_btn.setLayoutParams(params);
        }

        if (!TextUtils.isEmpty(txt_fax.getText().toString()) && !TextUtils.isEmpty(txt_mobile_number.getText().toString())) {
            Call_btn.setVisibility(View.GONE);
           /* RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) Portal.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            Portal.setLayoutParams(params);*/
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) Portal.getLayoutParams();
            params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            Portal.setLayoutParams(params1);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) sendFax_btn.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            sendFax_btn.setLayoutParams(params);
        }

        Call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (!TextUtils.isEmpty(txt_mobile_number.getText().toString())) {
                    AccessCall(txt_mobile_number.getText().toString().trim(), appointmentDatum.getDoctorName());
                } else {
                    Utility.ShowToast(mContext, getResources().getString(R.string.mobile_unavailable));
                    finish();
                }
            }
        });

        sendFax_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (!TextUtils.isEmpty(txt_fax.getText().toString())) {

                    if (Utility.isNetworkConnected(UpdateDoctorAppointmentActivity.this)) {
                        SendFax();
                    } else {

                        Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
                    }

                } else {
                    Utility.ShowToast(mContext, getResources().getString(R.string.Fax_unavailable));
                    finish();
                }

            }
        });

        Portal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(txt_Portal.getText().toString())) {
                    Intent intent = new Intent(mContext, SocialActivity.class);
                    intent.putExtra("webUrl", txt_Portal.getText().toString().trim());
                    startActivity(intent);
                    finish();
                } else {
                    Utility.ShowToast(mContext, getResources().getString(R.string.Portal_unavailable));
                    finish();

                }

                alertDialog.dismiss();

            }
        });

        close_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                onBackPressed();
            }
        });


    }

    private void SendFax() {

        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("doctor_id", DoctorID);
            mainObject.put("dr_appointment_id", AppointmentId);
            mainObject.put("userid", Utility.getSharedPreferences(mContext, APIS.user_id));
            mainObject.put("caregiver_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "SendFax:input data=  " + mainObject.toString());
        showProgressDialog(getResources().getString(R.string.Loading));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.DoctorSendFaxAPI, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "SendFax response=" + response.toString());

                        hideProgressDialog();

                        AppointmentRequestModel requestModel = new Gson().fromJson(response.toString(), AppointmentRequestModel.class);

                        Utility.ShowToast(mContext, getResources().getString(R.string.fax_Send_success));
                        onBackPressed();
                        finish();


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext, APIS.EncodeUser_id));

                return params;
            }

        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
