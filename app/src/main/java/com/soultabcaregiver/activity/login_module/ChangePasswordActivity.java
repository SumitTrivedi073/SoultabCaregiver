package com.soultabcaregiver.activity.login_module;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.calender.CalenderModel.CommonResponseModel;
import com.soultabcaregiver.sinch_calling.BaseActivity;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends BaseActivity {

    Context mContext;
    EditText et_new_pass, et_confirm_password;
    TextView proceed_btn;
    CheckBox view_pwd1,view_pwd2;
    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mContext = this;

        init();
        listner();
    }


    private void init() {
        et_new_pass = findViewById(R.id.et_new_pass);
        et_confirm_password = findViewById(R.id.et_confirm_password);
        view_pwd1 = findViewById(R.id.view_pwd1);
        view_pwd2 = findViewById(R.id.view_pwd2);
        proceed_btn = findViewById(R.id.proceed_btn);

    }

    private void listner() {
        view_pwd1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    et_new_pass.setTransformationMethod(new PasswordTransformationMethod());
                } else {
                    // hide password
                    et_new_pass.setTransformationMethod(null);
                }

            }
        });

        view_pwd2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    et_confirm_password.setTransformationMethod(new PasswordTransformationMethod());
                } else {
                    // hide password
                    et_confirm_password.setTransformationMethod(null);
                }

            }
        });

        proceed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_new_pass.getText().toString().trim().isEmpty()) {
                   Utility.ShowToast(mContext,getResources().getString(R.string.enter_new_password));
                } else if (!Utility.isvalidatePassword(et_new_pass.getText().toString().trim())) {
                    Utility.ShowToast(mContext,getResources().getString(R.string.enter_valid_new_password));
                } else if (et_confirm_password.getText().toString().trim().isEmpty()) {
                    Utility.ShowToast(mContext,getResources().getString(R.string.enter_confirm_password));

                } else if (!et_new_pass.getText().toString().trim().equals(et_confirm_password.getText().toString().trim())) {
                    Utility.ShowToast(mContext,getResources().getString(R.string.passowrd_not_match));

                }else {
                    if (Utility.isNetworkConnected(mContext)) {
                        ChangePassword();
                    }else {
                        Utility.ShowToast(mContext,getResources().getString(R.string.net_connection));
                    }
                }
            }
        });
    }

    private void ChangePassword() {
        showProgressDialog(getResources().getString(R.string.Loading));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIS.BASEURL + APIS.ChangePasswordAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        Log.e("response", response);
                        CommonResponseModel commonResponseModel = new Gson().fromJson(response, CommonResponseModel.class);
                        if (String.valueOf(commonResponseModel.getStatusCode()).equals("200")) {

                            ShowAlertResponse( commonResponseModel.getMessage(),"1");


                        } else {
                            ShowAlertResponse( commonResponseModel.getMessage(),"0");


                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", error.toString());

                        hideProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", getIntent().getStringExtra("email"));
                params.put("password", et_new_pass.getText().toString().trim());
                params.put("confirm_password", et_confirm_password.getText().toString().trim());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(stringRequest);
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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
                    Intent i = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }else {
                    alertDialog.dismiss();
                }
            }
        });

    }

}
