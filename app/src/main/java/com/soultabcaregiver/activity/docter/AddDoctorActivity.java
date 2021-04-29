package com.soultabcaregiver.activity.docter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.login_module.ForgotPasswordActivity;
import com.soultabcaregiver.activity.login_module.OTPVarificationActivity;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.sinch_calling.BaseActivity;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AddDoctorActivity extends BaseActivity implements View.OnClickListener, OnCountryPickerListener {

    private final String TAG = getClass().getSimpleName();
    Context mContext;
    RelativeLayout back_btn;
    EditText txt_doctor_name, txt_Address, txt_mobile_number, txt_email, txt_fax, txt_portal;
    TextView tv_add_doctor;
    TextView countryCodeTv;
    LinearLayout countryLL;
    private com.mukesh.countrypicker.CountryPicker countryPicker;
    private int sortBy = com.mukesh.countrypicker.CountryPicker.SORT_BY_NONE;
    private String filedata = "",CountryCode;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);


        mContext = this;

        init();

    }

    private void init() {
        txt_doctor_name = findViewById(R.id.txt_doctor_name);
        txt_Address = findViewById(R.id.txt_doctor_address);
        txt_mobile_number = findViewById(R.id.txt_mobile_number);
        txt_email = findViewById(R.id.txt_doctor_email);
        txt_fax = findViewById(R.id.txt_fax);
        txt_portal = findViewById(R.id.txt_Portal);
        back_btn = findViewById(R.id.back_btn);
        tv_add_doctor = findViewById(R.id.btn_add_doctor);
        countryCodeTv = findViewById(R.id.countryCodeTv);
        countryLL = findViewById(R.id.countryLL);

        tv_add_doctor.setOnClickListener(this);
        //  Doctor_img.setOnClickListener(this);
        back_btn.setOnClickListener(this);

        countryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPicker();
            }
        });
    }

    private void showPicker() {
        com.mukesh.countrypicker.CountryPicker.Builder builder =
                new com.mukesh.countrypicker.CountryPicker.Builder().with(AddDoctorActivity.this)
                        .listener(AddDoctorActivity.this);
        builder.canSearch(true);
        builder.sortBy(sortBy);
        countryPicker = builder.build();
        countryPicker.showDialog(AddDoctorActivity.this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.back_btn:
                onBackPressed();
                break;

            case R.id.btn_add_doctor:
                if (Utility.isNetworkConnected(mContext)){
                AddDoctor();
                }else {
                   Utility.ShowToast(mContext,getResources().getString(R.string.net_connection));
                }
                break;
        }
    }

    private void AddDoctor() {

        if (TextUtils.isEmpty(txt_doctor_name.getText().toString())) {
            Utility.ShowToast(mContext, getResources().getString(R.string.enter_doctor_name));
        } else if (TextUtils.isEmpty(txt_Address.getText().toString())) {
            Utility.ShowToast(mContext, getResources().getString(R.string.address));
        }else if (TextUtils.isEmpty(CountryCode)){
            Utility.ShowToast(mContext, getResources().getString(R.string.select_country_code));

        }else if (TextUtils.isEmpty(txt_mobile_number.getText().toString())) {
            Utility.ShowToast(mContext, getResources().getString(R.string.enter_mobile_number));
        } else if (!Utility.isValidMobile(txt_mobile_number.getText().toString())) {

            Utility.ShowToast(mContext, getResources().getString(R.string.enter_valid_mobile_number));
        } else if (TextUtils.isEmpty(txt_email.getText().toString())) {
            Utility.ShowToast(mContext, getResources().getString(R.string.hint_email));

        } else if (!Utility.isValidEmail(txt_email.getText().toString())) {
            Utility.ShowToast(mContext, getResources().getString(R.string.valid_email));
        } else if (TextUtils.isEmpty(txt_fax.getText().toString())) {
            Utility.ShowToast(mContext, getResources().getString(R.string.enter_Fax));
        } else if (TextUtils.isEmpty(txt_portal.getText().toString())) {
            Utility.ShowToast(mContext, getResources().getString(R.string.enter_Portal));
        } else if (!Utility.isValidUrl(txt_portal.getText().toString())) {
            Utility.ShowToast(mContext, getResources().getString(R.string.enter_valid_Portal));
        } else {
            AddDoctorAPI();
        }

    }

    private void AddDoctorAPI() {

        JSONObject mainObject = new JSONObject();

        try {
            mainObject.put("doctor_name", txt_doctor_name.getText().toString());
            mainObject.put("doctor_email", txt_email.getText().toString());
            mainObject.put("doctor_address", txt_Address.getText().toString());
            mainObject.put("doctor_mob_no", countryCodeTv.getText().toString()+txt_mobile_number.getText().toString());
            mainObject.put("fax_num", txt_fax.getText().toString());
            mainObject.put("website", txt_portal.getText().toString());

            Log.e("mainObject", String.valueOf(mainObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }

       showProgressDialog(getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.Add_Doctor_API, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "Get test report response=" + response.toString());

                        hideProgressDialog();
                        try {
                            String code = response.getString("status_code");
                            if (String.valueOf(code).equals("200")) {

                               ShowAlertResponse(response.getString("message"));
                            }else if (String.valueOf(code).equals("403")) {
                                logout_app(response.getString("message"));
                            }else {
                                ShowAlertResponse(response.getString("message"));

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
                Map<String, String> params = new HashMap<>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext,APIS.EncodeUser_id));
               return params;
            }

        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void ShowAlertResponse(String message) {
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
                alertDialog.dismiss();
                onBackPressed();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onSelectCountry(Country country) {
        countryCodeTv.setText(country.getDialCode());
        CountryCode = country.getDialCode();
    }
}
