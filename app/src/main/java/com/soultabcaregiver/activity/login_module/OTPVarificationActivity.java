package com.soultabcaregiver.activity.login_module;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.sinch_calling.BaseActivity;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OTPVarificationActivity extends BaseActivity implements View.OnClickListener {

    String TAG = getClass().getSimpleName();
    
    Context mContext;
    
    EditText otp1, otp2, otp3, otp4;
    
    TextView chronometer, resend_otp, otp_verify_btn;
    
    RelativeLayout need_help_relative;
    
    String email;
    
    AlertDialog alertDialog;
    
    LinearLayout btn_call, decline_call;
    
    LinearLayout main_call_layout, call_end_layout;
    
    TextView call_state;
    
    //AudioPlayer mAudioPlayer;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_varification);
        
        mContext = this;
        //mAudioPlayer = new AudioPlayer(this);

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        chronometer = findViewById(R.id.chronometer);
        resend_otp = findViewById(R.id.resend_otp);
        need_help_relative = findViewById(R.id.need_help_relative);
        otp_verify_btn = findViewById(R.id.otp_verify_btn);

        email = getIntent().getStringExtra("email");
        Log.e("email",email);

        countdownFunction();
        listner();
        
    }

    private void listner() {
        resend_otp.setOnClickListener(this);
        otp_verify_btn.setOnClickListener(this);
        need_help_relative.setOnClickListener(this);
        otp1.addTextChangedListener(new GenericTextWatcher(otp1));
        otp2.addTextChangedListener(new GenericTextWatcher(otp2));
        otp3.addTextChangedListener(new GenericTextWatcher(otp3));
        otp4.addTextChangedListener(new GenericTextWatcher(otp4));

    }

    CountDownTimer countDownTimer;
    public void countdownFunction() {

        if(countDownTimer!=null)
            countDownTimer.cancel();

        countDownTimer = new CountDownTimer(60*10000, 1000) {

            public void onTick(long millisUntilFinished) {

                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                resend_otp.setVisibility(View.GONE);
                chronometer.setVisibility(View.VISIBLE);
                if(minutes>0){
                    String text = getResources().getString(R.string.otp_expire)
                    +" "+ String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                    Log.e("text",text);
                    chronometer.setText(Html.fromHtml(text));
                }else{
                    String text = getResources().getString(R.string.otp_expire)
                            + " "+ String.format("%d sec",
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                    chronometer.setText(Html.fromHtml(text));
                    Log.e("text",text);
                }


            }

            public void onFinish() {
                chronometer.setVisibility(View.GONE);
                resend_otp.setVisibility(View.VISIBLE);
            }
        };
        countDownTimer.start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resend_otp:
                if (Utility.isNetworkConnected(mContext)) {
                    ForgotPassword();
                } else {
                    Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
                }

                break;

            case R.id.otp_verify_btn:
                if (Utility.isNetworkConnected(mContext)) {

                    GetVerify();
                } else {
                    Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
                }
                break;

            case R.id.need_help_relative:

                AccessCall();

                break;

        }

    }

    public void GetVerify() {
        final String TAG = "GetVerify";
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("email", email);
            mainObject.put("otp", (otp1.getText().toString().trim() + otp2.getText().toString().trim()
                    + otp3.getText().toString().trim() + otp4.getText().toString().trim()));

            Log.e("mainObject", mainObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        showProgressDialog(getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.VERIFYOTPAPI, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response=" + response.toString());
                        hideProgressDialog();
                        try {
                            String code = response.getString("status_code");
                            if (code.equals("200")) {

                                ShowAlertResponse(response.getString("message"),"1");

                            }else {
                                ShowAlertResponse(response.getString("message"), "0");
                                otp1.getText().clear();
                                otp2.getText().clear();
                                otp3.getText().clear();
                                otp4.getText().clear();
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
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
               params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);return params;
            }

        };
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    private void ForgotPassword() {

        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("email", email);
            mainObject.put("type", "4");

            Log.e("Forgot_password", mainObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog(getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.FORGOTAPI, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "forgot response=" + response.toString());
                        hideProgressDialog();
                        try {
                            String code = response.getString("status_code");
                            if (code.equals("200")) {
                                ShowAlertResponse( mContext.getResources().getString(R.string.otp_send_successfully),"0");
                            } else {
                                String msg = response.getString("message");
                                ShowAlertResponse(response.getString("message"), "0");

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
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);return params;
            }

        };
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void ShowAlertResponse(String message, String value) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.send_successfully_layout,
                null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();


        TextView OK_txt = layout.findViewById(R.id.OK_txt);
        TextView title_txt = layout.findViewById(R.id.title_txt);

        title_txt.setText(message);


        OK_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value.equals("1")){
                    Intent i = new Intent(OTPVarificationActivity.this, ChangePasswordActivity.class);
                    i.putExtra("email", email);
                    startActivity(i);
                    finish();
                }else {
                    alertDialog.dismiss();
                }
            }
        });

    }


    private void AccessCall() {

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
        RelativeLayout close_window = layout.findViewById(R.id.close_window);



        alertDialog.show();

        close_window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        call_end_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class GenericTextWatcher implements TextWatcher {

        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {
                case R.id.otp1:
                    if (text.length() == 1)
                        otp2.requestFocus();
                    break;
                case R.id.otp2:
                    if (text.length() == 1)
                        otp3.requestFocus();
                    else if (text.length() == 0)
                        otp1.requestFocus();
                    break;
                case R.id.otp3:
                    if (text.length() == 1)
                        otp4.requestFocus();
                    else if (text.length() == 0)
                        otp2.requestFocus();
                    break;
                case R.id.otp4:
                    if (text.length() == 0)
                        otp3.requestFocus();
                    break;
            }
        }
    }
}
