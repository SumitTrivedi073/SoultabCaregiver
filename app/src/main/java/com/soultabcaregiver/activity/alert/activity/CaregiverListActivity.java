package com.soultabcaregiver.activity.alert.activity;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.alert.adapter.CareGiverListAdapter;
import com.soultabcaregiver.activity.alert.model.CareGiverListModel;
import com.soultabcaregiver.sinch_calling.BaseActivity;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CaregiverListActivity extends BaseActivity {

    private  final String TAG = getClass().getSimpleName();
    Context mContext;
    TextView nodata_txt;
    RecyclerView caregiver_list;
    RelativeLayout back_btn;
    CardView blank_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_list);

        mContext = this;
        caregiver_list =  findViewById(R.id.caregiver_list);
        nodata_txt = findViewById(R.id.nodata_txt);
        back_btn = findViewById(R.id.back_btn);
        blank_card = findViewById(R.id.blank_card);

        if (Utility.isNetworkConnected(mContext)) {
            GetCaregiverList();//for list data
        } else {
            Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
        }

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void GetCaregiverList() {
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("caregiverr_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));

            Log.e(TAG, "CaregiverList API========>" + mainObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();

        }

        showProgressDialog(getResources().getString(R.string.Loading));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.CaregiverListAPI, mainObject,
                response -> {
                    Log.e(TAG, "Caregiverlist response=" + response.toString());
                    hideProgressDialog();

                    CareGiverListModel  careGiverProfileModel = new Gson().fromJson(response.toString(),
                            CareGiverListModel.class);

                    if (String.valueOf(careGiverProfileModel.getStatusCode()).equals("200")){

                        if (careGiverProfileModel.getResponse().size()>0){

                            caregiver_list.setVisibility(View.VISIBLE);
                            nodata_txt.setVisibility(View.GONE);
                            blank_card.setVisibility(View.GONE);
                            CareGiverListAdapter careGiverListAdapter = new CareGiverListAdapter(mContext, careGiverProfileModel.getResponse());
                            caregiver_list.setHasFixedSize(true);
                            caregiver_list.setAdapter(careGiverListAdapter);


                        }else {
                            caregiver_list.setVisibility(View.GONE);
                            nodata_txt.setVisibility(View.VISIBLE);
                            blank_card.setVisibility(View.VISIBLE);
                        }
                    } else{
                        Utility.ShowToast(mContext,careGiverProfileModel.getMessage());
                        caregiver_list.setVisibility(View.GONE);
                        nodata_txt.setVisibility(View.VISIBLE);
                        blank_card.setVisibility(View.VISIBLE);
                    }

                }, error -> {
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            hideProgressDialog();
            caregiver_list.setVisibility(View.GONE);
            nodata_txt.setVisibility(View.VISIBLE);
            blank_card.setVisibility(View.VISIBLE);
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                return params;
            }

        };
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
