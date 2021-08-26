package com.soultabcaregiver.activity.alert.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.alert.activity.CaregiverListActivity;
import com.soultabcaregiver.activity.alert.adapter.AlertAdapter;
import com.soultabcaregiver.activity.alert.model.AlertModel;
import com.soultabcaregiver.activity.calender.CalenderModel.CommonResponseModel;
import com.soultabcaregiver.activity.main_screen.MainActivity;
import com.soultabcaregiver.talk.TalkFragment;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class AlertFragment extends BaseFragment {

    private final String TAG = getClass().getSimpleName();
    Context mContext;
    View view;
    RecyclerView alert_list;
    TextView no_data_txt;
    FloatingActionButton create_alert_btn;
    CardView blank_card;
    
    MainActivity mainActivity;
    
    TalkFragment talkFragment;
    
    public static AlertFragment instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_alert, container, false);
    
        alert_list = view.findViewById(R.id.alert_list);
        no_data_txt = view.findViewById(R.id.no_data_txt);
        create_alert_btn = view.findViewById(R.id.create_alert_btn);
        blank_card = view.findViewById(R.id.blank_card);
    
        mainActivity = MainActivity.instance;
        instance = AlertFragment.this;
    
        create_alert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    
                Intent intent = new Intent(mContext, CaregiverListActivity.class);
                startActivity(intent);
            
            }
        });
    
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utility.isNetworkConnected(mContext)) {
            GetAlertList(mContext);//for list data
            AlertCountUpdate();
        } else {
            Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
        }
    }
    
    public void GetAlertList(Context mContext) {
        
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("user_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));
            
            Log.e(TAG, "Alert API========>" + mainObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            
        }
        
        //showProgressDialog(mContext, mContext.getResources().getString(R.string.Loading));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.AlertListAPI, mainObject,
                response -> {
                    Log.e(TAG, "Alertlist response=" + response.toString());
                    hideProgressDialog();

                    AlertModel alertModel = new Gson().fromJson(response.toString(), AlertModel.class);

                    if (String.valueOf(alertModel.getStatusCode()).equals("200")) {

                        if (alertModel.getData().getCaregiverData().size() > 0) {

                            alert_list.setVisibility(View.VISIBLE);
                            no_data_txt.setVisibility(View.GONE);
                            blank_card.setVisibility(View.GONE);
                            AlertAdapter alertAdapter = new AlertAdapter(mContext, alertModel.getData().getCaregiverData());
                            alert_list.setHasFixedSize(true);
                            alert_list.setAdapter(alertAdapter);
    
    
                        } else {
                            alert_list.setVisibility(View.GONE);
                            no_data_txt.setVisibility(View.VISIBLE);
                            blank_card.setVisibility(View.VISIBLE);
                        }
                    } else if (String.valueOf(alertModel.getStatusCode()).equals("403")) {
                        logout_app(alertModel.getMessage());
                    } else {
                        Utility.ShowToast(mContext, alertModel.getMessage());
                        alert_list.setVisibility(View.GONE);
                        no_data_txt.setVisibility(View.VISIBLE);
                        blank_card.setVisibility(View.VISIBLE);

                    }

                }, error -> {
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            hideProgressDialog();
            alert_list.setVisibility(View.GONE);
            no_data_txt.setVisibility(View.VISIBLE);
            blank_card.setVisibility(View.VISIBLE);
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext, APIS.EncodeUser_id));
                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }
    
    public void AlertCountUpdate() {
        
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("user_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));
            
            Log.e(TAG, "CaregiverList API========>" + mainObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.AlertCountUpdate, mainObject,
                response -> {
                    Log.e(TAG, "Caregiverlist response=" + response.toString());
                    hideProgressDialog();

                    CommonResponseModel alertCountModel = new Gson().fromJson(response.toString(),
                            CommonResponseModel.class);

                    if (String.valueOf(alertCountModel.getStatusCode()).equals("200")) {

                        if (mainActivity != null) {
                            mainActivity.Alert_countAPI();
                        }

                    }else if (String.valueOf(alertCountModel.getStatusCode()).equals("403")) {
                        logout_app(alertCountModel.getMessage());
                    }else {
                        Utility.ShowToast(mContext, alertCountModel.getMessage());
                    }

                }, error -> {
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            hideProgressDialog();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext, APIS.EncodeUser_id));

                return params;

            }

        };
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }
    
}
