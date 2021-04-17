package com.soultabcaregiver.activity.docter;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;
import com.soultabcaregiver.Model.DiloagBoxCommon;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.docter.DoctorModel.AppointmentRequestModel;
import com.soultabcaregiver.activity.docter.DoctorModel.DoctorAppointmentList;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.sinch_calling.BaseActivity;
import com.soultabcaregiver.utils.Utility;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class UpdateDoctorAppointmentActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    public static final int RequestPermissionCode = 3;
    private final String TAG = getClass().getSimpleName();
    Context mContext;
    TextView tvDocNm, tvAdd, tvContact, tvDate, tv_time, tvDocEmail, txt_fax,txt_Portal, cancel_appointment_btn;
    FloatingActionButton lyBack_card;
    Switch tbDocAppTog;

    LinearLayout rlDate, rl_time;
    String myFormat = "MM/dd/yyyy";
    SimpleDateFormat sdf;
    String myFormat1 = "yyyy-MM-dd";//for webservice
    SimpleDateFormat sdf1;
    String sDate = "";
    TextView tvMakeAppoint;

    Calendar myCalendar;
    String AppointmentId;
    String curDate = "";
    CircleImageView ivDocPic;
    SinchClient sinchClient;
    LinearLayout main_call_layout, call_end_layout;
    TextView call_state;
    AlertDialog alertDialog;
    LinearLayout btn_call, decline_call;
    DoctorAppointmentList.Response.AppointmentDatum appointmentDatum;
    String sSelTimeId = "", sSelTimeNm = "", sSelDateId = "", setToggle = "", sSelTimeSlotNm = "";
    private Call calling;
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
        lyBack_card = findViewById(R.id.lyBack_card);
        tvDocNm = findViewById(R.id.txt_doctor_name);
        tvAdd = findViewById(R.id.txt_second);
        tvContact = findViewById(R.id.txt_third);
        tbDocAppTog = findViewById(R.id.tb_doc_appoint);
        ivDocPic = findViewById(R.id.iv_doc_pic_update_appoint);
        rlDate = findViewById(R.id.rl_date);
        tvDate = findViewById(R.id.tv_date);
        tvDocEmail = findViewById(R.id.txt_five);
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

        if (Utility.isNetworkConnected(mContext)) {
            sinchClient = Sinch.getSinchClientBuilder().context(mContext)
                    .applicationKey(mContext.getResources().getString(R.string.sinch_applicationKey))
                    .applicationSecret(mContext.getResources().getString(R.string.sinch_application_secreat))
                    .environmentHost(mContext.getResources().getString(R.string.sinch_enviorement_host))
                    .userId(mContext.getResources().getString(R.string.sinch_user_id))
                    .build();

            sinchClient.setSupportCalling(true);
            sinchClient.setSupportActiveConnectionInBackground(true);
            sinchClient.startListeningOnActiveConnection();
            sinchClient.start();

            sinchClient.addSinchClientListener(new SinchClientListener() {

                public void onClientStarted(SinchClient client) {

                }

                public void onClientStopped(SinchClient client) {
                    Log.e("callended", "Call Stop");

                }

                public void onClientFailed(SinchClient client, SinchError error) {
                    Log.e("callended", "Call failed");

                }

                public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration registrationCallback) {
                }

                public void onLogMessage(int level, String area, String message) {
                }
            });


            sinchClient.getCallClient().addCallClientListener(new CallClientListener() {
                @Override
                public void onIncomingCall(CallClient callClient, Call call) {
                    calling = call;

                    call.answer();
                    call.addCallListener(new SinchCallListener());
                    main_call_layout.setVisibility(View.GONE);
                    call_end_layout.setVisibility(View.VISIBLE);
                }
            });
        } else {
            Utility.ShowToast(mContext, mContext.getResources().getString(R.string.net_connection));
        }

    }

    private void GetValuFromIntent() {

        appointmentDatum = (DoctorAppointmentList.Response.AppointmentDatum) getIntent().getSerializableExtra(APIS.DocListItem);

        assert appointmentDatum != null;
        AppointmentId = String.valueOf(appointmentDatum.getAppointmentId());
        tvDocNm.setText(appointmentDatum.getDoctorName());
        tvAdd.setText(appointmentDatum.getDoctorAddress());
        tvContact.setText(appointmentDatum.getDoctorMobile());
        txt_fax.setText(appointmentDatum.getFax());
        txt_Portal.setText(appointmentDatum.getWebsite());
        if (appointmentDatum.getEmail() != null) {
            tvDocEmail.setText(appointmentDatum.getEmail());
        }

        tvDate.setText(appointmentDatum.getDate());
        sSelDateId = String.valueOf(Utility.ChangeDateFormat("MM-dd-yyyy","yyyy-MM-dd",appointmentDatum.getDate()));
        Log.e("sSelDateId", sSelDateId);


        if (String.valueOf(appointmentDatum.getAppointmentsReminder()).equals("1")) {
            tbDocAppTog.setChecked(true);
        } else {
            tbDocAppTog.setChecked(false);
        }


        tv_time.setText(appointmentDatum.getTime());
        sSelTimeId = appointmentDatum.getTime();

    }


    private void Listener() {
        rlDate.setOnClickListener(this);
        rl_time.setOnClickListener(this);
        tvMakeAppoint.setOnClickListener(this);
        lyBack_card.setOnClickListener(this);
        cancel_appointment_btn.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyBack_card:
                finish();
                break;
            case R.id.rl_date:
                ChooseDate();
                break;

            case R.id.rl_time:
                chooseTimePicker();
                break;

            case R.id.tv_make_appoi:

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
                    if (calling != null) {
                        calling.hangup();
                        calling = null;
                        alertDialog.dismiss();
                    } else {
                        alertDialog.dismiss();
                    }
                    finish();
                }
        });

        call_end_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (calling != null) {
                        calling.hangup();
                        calling = null;
                        alertDialog.dismiss();
                    } else {
                        alertDialog.dismiss();
                    }
                    finish();
            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calling == null) {
                    call_state.setText(mContext.getResources().getString(R.string.connecting));

                    calling = sinchClient.getCallClient().callPhoneNumber(number);
                    main_call_layout.setVisibility(View.GONE);
                    call_end_layout.setVisibility(View.VISIBLE);
                    calling.addCallListener(new SinchCallListener());

                }
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
                            tv_time.setText(Utility.hh_mm_aa.format(calendar.getTime()));

                            sSelTimeId = Utility.hh_mm_aa.format(calendar.getTime());
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
                    final DiloagBoxCommon diloagBoxCommon = Utility.Alertmessage(mContext, mContext.getResources().getString(R.string.cancel_appointment)
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
            mainObject.put("appointment_id", appointmentDatum.getAppointmentId());
            mainObject.put("user_id", Utility.getSharedPreferences(mContext,APIS.user_id));
            mainObject.put("caregiver_id", Utility.getSharedPreferences(mContext,APIS.caregiver_id));

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

                                Utility.ShowToast(mContext,getResources().getString(R.string.cancel_appointment_successfully));

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
            mainObject.put("user_id", Utility.getSharedPreferences(mContext,APIS.user_id));
mainObject.put("caregiver_id", Utility.getSharedPreferences(mContext,APIS.caregiver_id));
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

                            } else {

                                Utility.ShowToast(mContext, requestModel.getMessage());
                                hideProgressDialog();
                                finish();

                            }

                        } catch (Exception e) {
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
                return params;
            }

        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    private void DoctorConectingPopup(){
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

        if (TextUtils.isEmpty(tvContact.getText().toString())) {
            Call_btn.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)sendFax_btn.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            sendFax_btn.setLayoutParams(params);
        }


        if (TextUtils.isEmpty(txt_Portal.getText().toString())) {
            Portal.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)sendFax_btn.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            sendFax_btn.setLayoutParams(params);
        }

        if (TextUtils.isEmpty(txt_fax.getText().toString())) {
            sendFax_btn.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(txt_Portal.getText().toString())&&!TextUtils.isEmpty(txt_fax.getText().toString())) {

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)Call_btn.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            Call_btn.setLayoutParams(params);
        }

        if (!TextUtils.isEmpty(txt_fax.getText().toString())&&!TextUtils.isEmpty(tvContact.getText().toString())) {

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)Portal.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            Portal.setLayoutParams(params);
        }

        Call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (!TextUtils.isEmpty(tvContact.getText().toString())) {
                    AccessCall(appointmentDatum.getDoctorMobile(), appointmentDatum.getDoctorName());
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
                alertDialog.dismiss();
               /* if (!TextUtils.isEmpty(txt_Portal.getText().toString())) {
                    Intent intent = new Intent(mContext, ActSocialActivity.class);
                    intent.putExtra("webUrl", appointmentDatum.getWebsite());
                    intent.putExtra("title", getResources().getString(R.string.social_web));
                    intent.putExtra(IntentKey.FromWhere, IntentKey.isAppsWebs);
                    startActivity(intent);
                    finish();
                }else {
                    Utility.ShowToast(mContext, getResources().getString(R.string.Portal_unavailable));
                    finish();

                }
*/

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
            mainObject.put("doctor_id", appointmentDatum.getDoctor_id());
            mainObject.put("dr_appointment_id", appointmentDatum.getAppointmentId());
           mainObject.put("user_id", Utility.getSharedPreferences(mContext,APIS.user_id));
mainObject.put("caregiver_id", Utility.getSharedPreferences(mContext,APIS.caregiver_id));} catch (JSONException e) {
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

                        try {
                            if (String.valueOf(requestModel.getOk()).equals("1")) {
                                Utility.ShowToast(mContext, requestModel.getMessage());
                                onBackPressed();
                                finish();
                            } else {
                                Utility.ShowToast(mContext, requestModel.getMessage());
                                onBackPressed();
                                finish();

                            }

                        } catch (Exception e) {
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
                return params;
            }

        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    private class SinchCallListener implements CallListener {

        //the call is ended for any reason
        @Override
        public void onCallEnded(Call endedCall) {
            calling = null; //no longer a current call
            main_call_layout.setVisibility(View.VISIBLE); //change text on button
            call_end_layout.setVisibility(View.GONE);
            call_state.setText(""); //empty call state
            //hardware volume buttons should revert to their normal function
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

            Log.e("callended", String.valueOf(endedCall));
        }

        //call is connected
        @Override
        public void onCallEstablished(Call establishedCall) {
            //change the call state in the view
            call_state.setText(mContext.getResources().getString(R.string.connected));
            //the hardware volume buttons should control the voice stream volume
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        //call is trying to connect
        @Override
        public void onCallProgressing(Call progressingCall) {
            //set call state to "ringing" in the view
            call_state.setText(mContext.getResources().getString(R.string.ringing));
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            //intentionally left empty
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
