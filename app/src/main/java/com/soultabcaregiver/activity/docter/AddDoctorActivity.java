package com.soultabcaregiver.activity.docter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.sinch_calling.BaseActivity;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AddDoctorActivity extends BaseActivity implements View.OnClickListener, OnCountryPickerListener {

    public static final int RequestPermissionCode = 3, REQUEST_IMAGE_CAPTURE = 101, PICK_FROM_FILE = 6;
    private final String TAG = getClass().getSimpleName();
    Context mContext;
    FloatingActionButton lyBack_card;
    EditText txt_doctor_name, txt_Address, txt_mobile_number, txt_email, txt_fax, txt_portal;
    TextView tv_add_doctor;
    TextView countryCodeTv;
    LinearLayout countryLL;
    private com.mukesh.countrypicker.CountryPicker countryPicker;
    private int sortBy = com.mukesh.countrypicker.CountryPicker.SORT_BY_NONE;
    private String filedata = "",CountryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);


        mContext = this;

        init();

    }

    private void init() {
        txt_doctor_name = findViewById(R.id.txt_doctor_name);
        txt_Address = findViewById(R.id.txt_second);
        txt_mobile_number = findViewById(R.id.txt_third);
        txt_email = findViewById(R.id.txt_five);
        txt_fax = findViewById(R.id.txt_fax);
        txt_portal = findViewById(R.id.txt_Portal);
        lyBack_card = findViewById(R.id.lyBack_card);
        tv_add_doctor = findViewById(R.id.tv_add_doctor);
        countryCodeTv = findViewById(R.id.countryCodeTv);
        countryLL = findViewById(R.id.countryLL);

        tv_add_doctor.setOnClickListener(this);
        //  Doctor_img.setOnClickListener(this);
        lyBack_card.setOnClickListener(this);

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

            case R.id.lyBack_card:
                onBackPressed();
                break;

            case R.id.tv_add_doctor:
                AddDoctor();
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
                            if (code.equals("200")) {

                                Utility.ShowToast(mContext, response.getString("message"));
                                onBackPressed();
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
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
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
