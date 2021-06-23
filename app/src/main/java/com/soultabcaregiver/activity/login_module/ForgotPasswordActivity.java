package com.soultabcaregiver.activity.login_module;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends BaseActivity {

    Context mContext;
    EditText et_email;
    TextView proceed_btn;
    AlertDialog alertDialog;
    RelativeLayout back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mContext = this;

        et_email = findViewById(R.id.et_email);
        proceed_btn = findViewById(R.id.proceed_btn);
        back_btn = findViewById(R.id.back_btn);

        if (Utility.getSharedPreferences2(mContext,APIS.save_email)!=null){
            if (Utility.getSharedPreferences2(mContext,APIS.save_email).equals("true")){
                et_email.setText(Utility.getSharedPreferences2(mContext,APIS.Caregiver_email));
            }
        }


        proceed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_email.getText().toString().isEmpty()){
                    Utility.ShowToast(mContext,getResources().getString(R.string.hint_email));
                }else if (!Utility.isValidEmail(et_email.getText().toString().trim())){
                    Utility.ShowToast(mContext,getResources().getString(R.string.valid_email_address));
                    
                }else {

                    if (Utility.isNetworkConnected(mContext)){
                        ForgotPassword();
                    }else {
                        Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
                    }
                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void ForgotPassword() {
        final  String TAG = getClass().getSimpleName();
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("email", et_email.getText().toString().trim());
            mainObject.put("type", "4");

            Log.e("Forgot_password",mainObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog(getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL+APIS.FORGOTAPI, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "forgot response="+response.toString());
                        hideProgressDialog();
                        try {
                            String code=response.getString("status_code");
                            if(code.equals("200")){
                                String msg=response.getString("message");
                                ShowAlertResponse();
                            }else {
                                String msg=response.getString("message");
                                Utility.ShowToast(mContext,msg);

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
                Map<String,String> params = new HashMap<String, String>();
                params.put(APIS.HEADERKEY,APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1,APIS.HEADERVALUE1);
                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void ShowAlertResponse() {
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

        title_txt.setText(mContext.getResources().getString(R.string.otp_send_successfully));

        OK_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent i = new Intent(ForgotPasswordActivity.this, OTPVarificationActivity.class);
                i.putExtra("email",et_email.getText().toString().trim());
                startActivity(i);
                finish();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
