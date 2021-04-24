package com.soultabcaregiver.activity.docter.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.docter.DoctorModel.DoctorAppointmentList;
import com.soultabcaregiver.activity.docter.adapter.DoctorAppointedListAdptr;
import com.soultabcaregiver.sinch_calling.BaseFragment;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorAppointmentFragment extends BaseFragment {

    public static DoctorAppointmentFragment instance;
    View view;
    Context mContext;
    FloatingActionButton lyBack_card;
    List<DoctorAppointmentList.Response.AppointmentDatum> arAppointedDoc = new ArrayList<>();
    RecyclerView doctor_appointment_list;
    TextView tvNodata;
    String TAG = getClass().getSimpleName();
    SearchView doctor_Appointment_search;
    DoctorAppointedListAdptr adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_doctor_appointment, container, false);

        instance = DoctorAppointmentFragment.this;
        InitCompo();
        Listener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        GetAppointedDocRecond();
    }

    public void GetAppointedDocRecond() {
        if (Utility.isNetworkConnected(mContext)) {
            GetAppointedDocList();
        } else {
            Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
        }

    }

    private void GetAppointedDocList() {

        arAppointedDoc = new ArrayList<>();
        final String TAG = "Get Appointed Doc";
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("user_id", Utility.getSharedPreferences(mContext, APIS.user_id));
            mainObject.put("device_type", "android");
            mainObject.put("caregiver_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));

            Log.e("mainObject", String.valueOf(mainObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.ALLAPPOINTED_DOC_API, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Get Appointed Doc response=" + response.toString());
                        hideProgressDialog();
                        arAppointedDoc = new ArrayList<>();

                        DoctorAppointmentList doctorAppointmentList = new Gson().fromJson(response.toString(), DoctorAppointmentList.class);

                        if (String.valueOf(doctorAppointmentList.getStatusCode()).equals("200")) {
                            arAppointedDoc = doctorAppointmentList.getResponse().getAppointmentData();
                            Collections.reverse(arAppointedDoc);

                            if (arAppointedDoc.size() > 0) {
                                tvNodata.setVisibility(View.GONE);
                                doctor_appointment_list.setVisibility(View.VISIBLE);
                                adapter = new DoctorAppointedListAdptr(mContext, arAppointedDoc, 2, tvNodata);
                                doctor_appointment_list.setAdapter(adapter);

                            } else {
                                tvNodata.setText(mContext.getResources().getString(R.string.no_data_found));
                                tvNodata.setVisibility(View.VISIBLE);
                                doctor_appointment_list.setVisibility(View.GONE);
                            }

                        } else {
                            tvNodata.setText(getResources().getString(R.string.no_data_found));
                            tvNodata.setVisibility(View.VISIBLE);
                            doctor_appointment_list.setVisibility(View.GONE);
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

    private void Listener() {
        ImageView searchIcon = doctor_Appointment_search.findViewById(R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_search));
        searchIcon.setColorFilter(getResources().getColor(R.color.themecolor));

        ImageView searchClose = doctor_Appointment_search.findViewById(R.id.search_close_btn);
        searchClose.setColorFilter(getResources().getColor(R.color.themecolor));


        EditText searchEditText = doctor_Appointment_search.findViewById(R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.themecolor));
        searchEditText.setHintTextColor(getResources().getColor(R.color.themecolor));
        searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen._14sdp));

        doctor_Appointment_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter != null) {
                    adapter.getFilter().filter(query);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });


    }

    private void InitCompo() {
        doctor_Appointment_search = view.findViewById(R.id.doctor_Appointment_search);
        tvNodata = view.findViewById(R.id.tv_no_data_doc_appointment_list);
        doctor_appointment_list = view.findViewById(R.id.doctor_appointment_list);

    }



    /*public void DeleteAppointment() {
        if (TextUtils.isEmpty(sAppointedDocId)) {

            Utility.ShowToast(mContext, getResources().getString(R.string.select_doctor_appoint));
        } else {
            alertmessage();
        }
    }

    private void alertmessage() {

        final DiloagBoxCommon diloagBoxCommon = Utility.Alertmessage(mContext, getResources().getString(R.string.delete_Appointment)
                , mContext.getResources().getString(R.string.are_you_sure_you_want_to_delete_appointment)
                , mContext.getResources().getString(R.string.no_text)
                , mContext.getResources().getString(R.string.yes_text));
        diloagBoxCommon.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diloagBoxCommon.getDialog().dismiss();
                if (Utility.isNetworkConnected(mContext)) {
                    DeletAppointment();
                } else {

                    Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
                }
            }
        });
    }

    public void DeletAppointment() {

        final String TAG = "Delete AppointedDoc";
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("appointment_id", sAppointedDocId);
            mainObject.put("user_id", Utility.getSharedPreferences(mContext,APIS.user_id));
            mainObject.put("caregiver_id", Utility.getSharedPreferences(mContext,APIS.caregiver_id));

            Log.e(TAG, "appointmentdelete======>" + mainObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.DELETE_DOC_APPOIN_API, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Delete AppointedDoc response=" + response.toString());
                        hideProgressDialog();
                        try {
                            String code = response.getString("status_code");
                            if (code.equals("200")) {

                                Utility.ShowToast(mContext, response.getJSONObject("response")
                                        .getString("appointment_data"));

                                GetAppointedDocRecond();
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
*/
}
