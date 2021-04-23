package com.soultabcaregiver.activity.login_module;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.soultabcaregiver.Model.LoginModel;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.MainScreen.MainActivity;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.sinch_calling.BaseActivity;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private  final String TAG = getClass().getSimpleName();
    Context mContext;
    TextView tvLogin, tvForgot,tv_rem_pass;
    EditText etEmail, etPass;
    Switch tbRemPass;
    CheckBox view_pwd1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;

        init();
        Listener();

    }


    private void init() {
        tvLogin = findViewById(R.id.tv_login);
        tvForgot = findViewById(R.id.tv_forgot_pass);
        etEmail = findViewById(R.id.et_email);
        etPass = findViewById(R.id.et_pass);
        tbRemPass = findViewById(R.id.tb_rem);
        view_pwd1 = findViewById(R.id.view_pwd1);
        tv_rem_pass = findViewById(R.id.tv_rem_pass);

        if (Utility.getSharedPreferences2(mContext,APIS.save_email)!=null){
            if (Utility.getSharedPreferences2(mContext,APIS.save_email).equals("true")){
                etEmail.setText(Utility.getSharedPreferences2(mContext,APIS.Caregiver_email));
            }
        }
    }

    private void Listener() {
        tvLogin.setOnClickListener(this);
        tvForgot.setOnClickListener(this);
        tv_rem_pass.setOnClickListener(this);

        view_pwd1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {

                    etPass.setTransformationMethod(new PasswordTransformationMethod());

                } else {


                    // hide password
                    etPass.setTransformationMethod(null);
                }

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tv_rem_pass:
                if (tbRemPass.isChecked()) {
                    tbRemPass.setChecked(false);
                } else {
                    tbRemPass.setChecked(true);
                }
                break;
            case R.id.tv_forgot_pass:
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                break;

            case R.id.tv_login:
                Login();
                break;

        }

    }

    private void Login() {
        if (Utility.isNetworkConnected(LoginActivity.this)) {
            if (etEmail.getText().toString().trim().isEmpty()) {

                Utility.ShowToast(mContext, getResources().getString(R.string.valid_email));


            } else if (etPass.getText().toString().trim().isEmpty()) {

                Utility.ShowToast(mContext, getResources().getString(R.string.valid_pass));

            } else if (!Utility.isvalidatePassword(etPass.getText().toString().trim())) {
                Utility.ShowToast(mContext, getResources().getString(R.string.password_not_valid));

            } else if (etPass.getText().toString().trim().length() < 8) {
                Utility.ShowToast(mContext, getResources().getString(R.string.password_notvalid));

            } else {
               GetLogin();
            }
        } else {

            Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));


        }
    }

    private void GetLogin() {
        JSONObject mainObject = new JSONObject();

        try {
            mainObject.put("email", etEmail.getText().toString().trim());
            mainObject.put("password", etPass.getText().toString().trim());
            mainObject.put("device_type", "android");
            mainObject.put("user_roll", 4);


            Log.e("Login_mainObject", String.valueOf(mainObject));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        System.out.println(mainObject.toString());
       showProgressDialog(getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.LOGINAPI, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        hideProgressDialog();

                        LoginModel loginModel = new Gson().fromJson(response.toString(),LoginModel.class);

                        if (String.valueOf(loginModel.getStatusCode()).equals("200")){

                            if (tbRemPass.isChecked()){
                                Utility.setSharedPreference2(mContext,APIS.Caregiver_email,loginModel.getResponse().getEmail());
                                Utility.setSharedPreference2(mContext,APIS.save_email,"true");

                            }

                            Utility.setSharedPreference(mContext,APIS.user_id,loginModel.getResponse().getId());
                            Utility.setSharedPreference(mContext,APIS.caregiver_id,loginModel.getResponse().getCaregiver_id());
                            Utility.setSharedPreference(mContext,APIS.Caregiver_name,loginModel.getResponse().getName());
                            Utility.setSharedPreference(mContext,APIS.Caregiver_lastname,loginModel.getResponse().getLastname());
                            Utility.setSharedPreference(mContext,APIS.Caregiver_email,loginModel.getResponse().getEmail());
                            Utility.setSharedPreference(mContext,APIS.user_email,loginModel.getResponse().getUser_email_address());

                            Utility.setSharedPreference(mContext,APIS.Caregiver_mobile,loginModel.getResponse().getMobile());
                            Utility.setSharedPreference(mContext,APIS.profile_image,loginModel.getResponse().getProfileImage());

                            Intent intent = new Intent(mContext, MainActivity.class);
                            startActivity(intent);
                            finish();


                        }else {
                            Utility.ShowToast(mContext,loginModel.getMessage());
                        }

                        }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                error.printStackTrace();
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
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

}
