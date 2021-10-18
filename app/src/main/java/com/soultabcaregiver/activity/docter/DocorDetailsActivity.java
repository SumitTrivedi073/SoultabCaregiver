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
import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.Model.TwilioTokenModel;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.WebService.ApiTokenAuthentication;
import com.soultabcaregiver.activity.docter.DoctorModel.AppointmentRequestModel;
import com.soultabcaregiver.activity.docter.DoctorModel.DoctorListModel;
import com.soultabcaregiver.twillovoicecall.VoiceActivity;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DocorDetailsActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private final String TAG = getClass().getSimpleName();
    Context mContext;
    String id, TwilioAccessToken;
    DoctorListModel.Response.DoctorDatum docListBean;
    TextView tvDocNm, txt_doctor_address, txt_mobile_number, tvDate, txt_fax, txt_Portal, txt_doctor_email;
    Switch tbDocAppTog;
    RelativeLayout rlDate, rl_time, back_btn;
    String myFormat = "MM-dd-yyyy";
    SimpleDateFormat sdf;
    String myFormat1 = "yyyy-MM-dd"; //for webservice
    SimpleDateFormat sdf1;
    TextView tvMakeAppoint, tv_time;
    LinearLayout main_call_layout, call_end_layout;
    TextView call_state;
    AlertDialog alertDialog;
    LinearLayout btn_call, decline_call;
    String sSelTimeId = "", sSelTimeNm = "", sSelDateId = "";

    private Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dotor_detail);

        mContext = this;
        InitCompo();
        GetValuFromIntent();
        Listener();
    }


    private void InitCompo() {
        back_btn = findViewById(R.id.back_btn);
        tvDocNm = findViewById(R.id.txt_doctor_name);
        txt_doctor_address = findViewById(R.id.txt_doctor_address);
        txt_mobile_number = findViewById(R.id.txt_mobile_number);
        txt_doctor_email = findViewById(R.id.txt_doctor_email);
        tbDocAppTog = findViewById(R.id.tb_doc_appoint);
        rlDate = findViewById(R.id.rl_date);
        tvDate = findViewById(R.id.tv_date);
        txt_fax = findViewById(R.id.txt_fax);
        txt_Portal = findViewById(R.id.txt_Portal);
        rl_time = findViewById(R.id.rl_time);
        tvMakeAppoint = findViewById(R.id.tv_make_appoi);
        tv_time = findViewById(R.id.tv_time);

        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        sdf1 = new SimpleDateFormat(myFormat1, Locale.US);
    }


    private void GetValuFromIntent() {


        docListBean = (DoctorListModel.Response.DoctorDatum) getIntent().getSerializableExtra(APIS.DocListItem);

        assert docListBean != null;
        id = String.valueOf(docListBean.getId());
        tvDocNm.setText(docListBean.getName());
        txt_doctor_address.setText(docListBean.getAddress());
        txt_mobile_number.setText(docListBean.getContact());
        txt_fax.setText(docListBean.getFaxNum());
        txt_Portal.setText(docListBean.getPortal());


        if (docListBean.getDoctorEmail() != null) {
            txt_doctor_email.setText(docListBean.getDoctorEmail());
        }
        tvDate.setText(sdf.format(calendar.getTime()));
        sSelDateId = sdf1.format(calendar.getTime());


        tv_time.setText(Utility.hh_mm_aa.format(calendar.getTime()));
        sSelTimeId = Utility.hh_mm_aa.format(calendar.getTime());

    }


    private void Listener() {

        back_btn.setOnClickListener(this);
        rlDate.setOnClickListener(this);
        rl_time.setOnClickListener(this);
        tvMakeAppoint.setOnClickListener(this);


    }

    @Override
    public void onBackPressed() {
        Utility.SetAvailableDate(mContext, null);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.rl_date:
                if (Utility.getSharedPreferences(mContext, APIS.doctor_hide_show).equals(APIS.Edit)) {

                    ChooseDate();
                } else {
                    Utility.ShowToast(mContext, mContext.getResources().getString(R.string.only_view_permission));
                }
                break;

            case R.id.rl_time:
                if (Utility.getSharedPreferences(mContext, APIS.doctor_hide_show).equals(APIS.Edit)) {

                    chooseTimePicker();
                } else {
                    Utility.ShowToast(mContext, mContext.getResources().getString(R.string.only_view_permission));
                }
                break;


            case R.id.tv_make_appoi:
                if (Utility.getSharedPreferences(mContext, APIS.doctor_hide_show).equals(APIS.Edit)) {

                    if (Utility.isNetworkConnected(mContext)) {
                        if (sdf.format(new Date()).equals((tvDate.getText().toString()))) {
                            Log.e("equal true", "equal true");


                            if (TextUtils.isEmpty(sSelTimeId)) {
                                Utility.ShowToast(mContext, getResources().getString(R.string.doctor_appointment_time));

                            } else if (TextUtils.isEmpty(sSelDateId)) {

                                Utility.ShowToast(mContext, getResources().getString(R.string.doctor_appointment_date));
                            } else {
                                try {
                                    Calendar now = Calendar.getInstance();
                                    now.setTime(Utility.yyyy_mm_dd_hh_mm_aa.parse(
                                            sSelDateId + " " + sSelTimeId));
                                    String completeDate =
                                            Utility.yyyy_mm_dd_hh_mm_aa.format(now.getTime());
                                    if (compareDateTime(completeDate)) {
                                        Log.e("sSelTimeId", sSelTimeId);
                                        GetDocAvailableTime(2);
                                    } else {
                                        Utility.ShowToast(mContext,
                                                getString(R.string.select_future_time));

                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {

                            if (TextUtils.isEmpty(sSelTimeId)) {
                                Utility.ShowToast(mContext, getResources().getString(R.string.doctor_appointment_time));
                            } else if (TextUtils.isEmpty(sSelDateId)) {
                                Utility.ShowToast(mContext, getResources().getString(R.string.doctor_appointment_date));
                            } else {
                                try {
                                    Calendar now = Calendar.getInstance();
                                    now.setTime(Utility.yyyy_mm_dd_hh_mm_aa.parse(
                                            sSelDateId + " " + sSelTimeId));
                                    String completeDate =
                                            Utility.yyyy_mm_dd_hh_mm_aa.format(now.getTime());
                                    if (compareDateTime(completeDate)) {
                                        Log.e("sSelTimeId", sSelTimeId);
                                        GetDocAvailableTime(2);
                                    } else {
                                        Utility.ShowToast(mContext,
                                                getString(R.string.select_future_time));

                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
                    }
                } else {
                    Utility.ShowToast(mContext, mContext.getResources().getString(R.string.only_view_permission));
                }


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
                alertDialog.dismiss();
                finish();
            }
        });

        call_end_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
                finish();
            }

        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VoiceActivity.class);
                intent.putExtra("Contact", number);
                intent.putExtra("TwilioAccessToken", TwilioAccessToken);
                startActivity(intent);
                alertDialog.dismiss();
                finish();
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
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(DocorDetailsActivity.this, mYear, mMonth, mDay);
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
                            if (calendar.before(GregorianCalendar.getInstance())) {
                                Utility.ShowToast(mContext, getResources().getString(R.string.select_future_time));
                            } else {
                                Calendar datetime = Calendar.getInstance();
                                datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                datetime.set(Calendar.MINUTE, minute);
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
                case 2:

                    GetAccessToken();

                    break;
            }

        } else {

            Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
        }

    }


    private void GetAccessToken() {

        final String TAG = "MakeDocAppointment";
        showProgressDialog(getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                APIS.BASEURL + APIS.TwilioAccessToken, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "MakeDocAppointment response=" + response.toString());

                        TwilioTokenModel requestModel = new Gson().fromJson(response.toString(), TwilioTokenModel.class);

                        try {
                            if (requestModel.getStatusCode() == 200) {
                                TwilioAccessToken = requestModel.getResponse();
                                MakeDocAppointment();

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
                if (error.networkResponse!=null) {
                    if (String.valueOf(error.networkResponse.statusCode).equals(APIS.APITokenErrorCode)) {
                        ApiTokenAuthentication.refrehToken(mContext, updatedToken -> {
                            if (updatedToken == null) {
                            } else {
                                GetAccessToken();
                    
                            }
                        });
                    }else {
                        Utility.ShowToast(
                                mContext,
                                mContext.getResources().getString(R.string.something_went_wrong));
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext, APIS.EncodeUser_id));
                params.put(APIS.APITokenKEY,
                        Utility.getSharedPreferences(mContext, APIS.APITokenValue));
    
                return params;
            }

        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    private void MakeDocAppointment() {

        final String TAG = "MakeDocAppointment";

        JSONObject mainObject = new JSONObject();

        try {

            mainObject.put("doctor_id", id);
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
        Log.e(TAG, "MAKE_APPOIN_API=  " + mainObject.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.DOC_APPOIN_API, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "MakeDocAppointment response=" + response.toString());
                        hideProgressDialog();

                        AppointmentRequestModel requestModel = new Gson().fromJson(response.toString(), AppointmentRequestModel.class);

                        try {
                            if (requestModel.getStatusCode() == 200) {

                                Utility.ShowToast(mContext, requestModel.getMessage());

                                DoctorConectingPopup(requestModel.getResponse().getId());

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
    
                if (error.networkResponse!=null) {
                    if (String.valueOf(error.networkResponse.statusCode).equals(APIS.APITokenErrorCode)) {
                        ApiTokenAuthentication.refrehToken(mContext, updatedToken -> {
                            if (updatedToken == null) {
                            } else {
                                MakeDocAppointment();
                    
                            }
                        });
                    }else {
                        Utility.ShowToast(
                                mContext,
                                getResources().getString(R.string.something_went_wrong));
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext, APIS.EncodeUser_id));
                params.put(APIS.APITokenKEY,
                        Utility.getSharedPreferences(mContext, APIS.APITokenValue));
    
                return params;
            }

        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void DoctorConectingPopup(String appointment_id) {
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

            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) Call_btn.getLayoutParams();
            params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            Call_btn.setLayoutParams(params1);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) sendFax_btn.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            sendFax_btn.setLayoutParams(params);
        }

        if (TextUtils.isEmpty(txt_fax.getText().toString())) {
            sendFax_btn.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) Call_btn.getLayoutParams();
            params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            Call_btn.setLayoutParams(params1);

            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) Portal.getLayoutParams();
            params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            Portal.setLayoutParams(params2);

        }

        if (!TextUtils.isEmpty(txt_Portal.getText().toString()) && !TextUtils.isEmpty(txt_fax.getText().toString())) {

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) Call_btn.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            Call_btn.setLayoutParams(params);


        }

        if (!TextUtils.isEmpty(txt_fax.getText().toString()) && !TextUtils.isEmpty(txt_mobile_number.getText().toString())) {

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) Portal.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            Portal.setLayoutParams(params);

        }

        Call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (!TextUtils.isEmpty(txt_mobile_number.getText().toString())) {
                    AccessCall(txt_mobile_number.getText().toString().trim(), docListBean.getName());
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

                    if (Utility.isNetworkConnected(DocorDetailsActivity.this)) {
                        SendFax(appointment_id);
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
                alertDialog.dismiss();
                if (!TextUtils.isEmpty(txt_Portal.getText().toString())) {
                    Intent intent = new Intent(mContext, SocialActivity.class);
                    intent.putExtra("webUrl", docListBean.getPortal());
                    intent.putExtra("title", docListBean.getName());
                    startActivity(intent);
                    finish();
                } else {
                    Utility.ShowToast(mContext, getResources().getString(R.string.Portal_unavailable));
                    finish();

                }


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

    private void SendFax(String appointment_id) {

        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("doctor_id", docListBean.getId());
            mainObject.put("dr_appointment_id", appointment_id);
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
                if (error.networkResponse!=null) {
                    if (String.valueOf(error.networkResponse.statusCode).equals(APIS.APITokenErrorCode)) {
                        ApiTokenAuthentication.refrehToken(mContext, updatedToken -> {
                            if (updatedToken == null) {
                            } else {
                                SendFax(appointment_id);
                    
                            }
                        });
                    }else {
                        Utility.ShowToast(
                                mContext,
                                mContext.getResources().getString(R.string.something_went_wrong));
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext, APIS.EncodeUser_id));
                params.put(APIS.APITokenKEY,
                        Utility.getSharedPreferences(mContext, APIS.APITokenValue));
    
                return params;
            }

        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private boolean compareDateTime(String date) {
        try {
            Calendar calendar = Calendar.getInstance();
            boolean graterThanDate;
            String todayDate = Utility.yyyy_mm_dd_hh_mm_ss.format(calendar.getTime());
            String selectDate =
                    Utility.yyyy_mm_dd_hh_mm_ss.format(Utility.yyyy_mm_dd_hh_mm_aa.parse(date));

            Date selectDateDate = Utility.yyyy_mm_dd_hh_mm_ss.parse(selectDate);
            Date todayDateDate = Utility.yyyy_mm_dd_hh_mm_ss.parse(todayDate);

            Log.e("selectDateDate", String.valueOf(selectDateDate));
            Log.e("todayDateDate", String.valueOf(todayDateDate));

            if (String.valueOf(selectDateDate).equals(String.valueOf(todayDateDate))) {
                graterThanDate = true;  // If two dates are equal.
            } else // If start date is after the end date.
            {
                assert selectDateDate != null;
                graterThanDate = selectDateDate.after(todayDateDate);
            }

            return graterThanDate;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;

    }
}
