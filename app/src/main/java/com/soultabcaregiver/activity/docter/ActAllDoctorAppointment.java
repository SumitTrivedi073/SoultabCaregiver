package com.soultabcaregiver.activity.docter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.soultabcaregiver.Model.DiloagBoxCommon;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.docter.DoctorModel.DoctorAppointmentList;
import com.soultabcaregiver.activity.docter.adapter.DoctorAppointedListAdptr;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.sinch_calling.BaseActivity;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActAllDoctorAppointment extends BaseActivity implements View.OnClickListener, DoctorAppointedListAdptr.AppointedDocSelectionListener {

    Context mContext;
    FloatingActionButton lyBack_card;
    RelativeLayout /*rlSelectAll, rlDelAppointedDoc,*/ rlAddAppointedDoc;
    List<DoctorAppointmentList.Response.AppointmentDatum> arAppointedDoc = new ArrayList<>();
    RecyclerView rvAppointedDoc;
    ImageView ivAllSelect;
    boolean checkAllSel = false;
    TextView tvNodata;
    String TAG = getClass().getSimpleName();
    SearchView search_edittext;
    DoctorAppointedListAdptr adapter;
    ImageView iv_remove;
    RelativeLayout rl_select_remove;
    String sAppointedDocId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appintment);

        mContext = this;
        InitCompo();
        Listener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetAppointedDocRecond();
    }

    private void GetAppointedDocRecond() {
        if (Utility.isNetworkConnected(this)) {
            checkAllSel = false;
            ivAllSelect.setImageResource(R.drawable.uncheck);
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

            Log.e("mainObject", String.valueOf(mainObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog( getResources().getString(R.string.Loading));
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
                                rl_select_remove.setVisibility(View.VISIBLE);
                                tvNodata.setVisibility(View.GONE);

                                adapter = new DoctorAppointedListAdptr(mContext, arAppointedDoc, 2, rl_select_remove, tvNodata);
                                adapter.setAppointedDocSelection(ActAllDoctorAppointment.this);
                                rvAppointedDoc.setAdapter(adapter);

                            } else {
                                rl_select_remove.setVisibility(View.GONE);
                                tvNodata.setVisibility(View.VISIBLE);
                                checkAllSel = false;
                                ivAllSelect.setImageResource(R.drawable.uncheck);
                            }

                        } else {
                            tvNodata.setText(getResources().getString(R.string.no_data_found));
                            rl_select_remove.setVisibility(View.GONE);
                            tvNodata.setVisibility(View.VISIBLE);
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
        lyBack_card.setOnClickListener(this);
        //  rlAddAppointedDoc.setOnClickListener(this);
        iv_remove.setOnClickListener(this);
        ivAllSelect.setOnClickListener(this);

        ImageView searchIcon = search_edittext.findViewById(R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_search));
        searchIcon.setColorFilter(getResources().getColor(R.color.themecolor));

        ImageView searchClose = search_edittext.findViewById(R.id.search_close_btn);
        searchClose.setColorFilter(getResources().getColor(R.color.themecolor));


        EditText searchEditText = search_edittext.findViewById(R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.themecolor));
        searchEditText.setHintTextColor(getResources().getColor(R.color.themecolor));
        searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen._14sdp));

        search_edittext.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        search_edittext = findViewById(R.id.search_edittext);
        lyBack_card = findViewById(R.id.lyBack_card);
        //  rlAddAppointedDoc = findViewById(R.id.rl_add);
        tvNodata = findViewById(R.id.tv_no_data);
        rl_select_remove = findViewById(R.id.rl_select_remove);
        iv_remove = findViewById(R.id.iv_remove);
        ivAllSelect = findViewById(R.id.iv_select);
        rvAppointedDoc = findViewById(R.id.rv_reminder);


        rvAppointedDoc.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvAppointedDoc.addItemDecoration(itemDecoration);


        search_edittext.setFocusableInTouchMode(true);
        search_edittext.requestFocus();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyBack_card:
                finish();
                break;
            case R.id.iv_select:
                if (arAppointedDoc.size() > 0) {
                    if (checkAllSel) {
                        checkAllSel = false;
                        ivAllSelect.setImageResource(R.drawable.uncheck);
                    } else {
                        checkAllSel = true;
                        ivAllSelect.setImageResource(R.drawable.checked);
                    }
                    SelectAll();
                }
                break;
            case R.id.iv_remove:
                if (arAppointedDoc.size() > 0) {
                    DeleteAppointment();
                }
                break;
        }
    }

    public void SelectAll() {
        for (int i = 0; i < arAppointedDoc.size(); i++) {
            if (checkAllSel) {
                arAppointedDoc.get(i).setCheck(true);
            } else {
                arAppointedDoc.get(i).setCheck(false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void DeleteAppointment() {
        for (int i = 0; i < arAppointedDoc.size(); i++) {
            if (arAppointedDoc.get(i).isCheck()) {
                if (TextUtils.isEmpty(sAppointedDocId)) {
                    sAppointedDocId = arAppointedDoc.get(i).getAppointmentId();
                } else {
                    sAppointedDocId = sAppointedDocId + "," + arAppointedDoc.get(i).getAppointmentId();
                }
            }
        }
        Log.e("", "sAppointedDocId= " + sAppointedDocId);

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
                if (Utility.isNetworkConnected(ActAllDoctorAppointment.this)) {
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
//        String url = "http://votivelaravel.in/carepro/api/Users/reminderdelete";
        showProgressDialog( getResources().getString(R.string.Loading));
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

    @Override
    public void AppointedDocSelection(List<DoctorAppointmentList.Response.AppointmentDatum> appointedDocBeanList, boolean isSearch) {
        if (isSearch) {
            if (appointedDocBeanList.size() == 0) {
                rl_select_remove.setVisibility(View.GONE);
            } else {
                rl_select_remove.setVisibility(View.VISIBLE);
            }
        } else {
            int selectedCount = 0;
            for (int i = 0; i < appointedDocBeanList.size(); i++) {
                if (appointedDocBeanList.get(i).isCheck()) {
                    selectedCount++;
                }
            }
            if (selectedCount == appointedDocBeanList.size()) {
                checkAllSel = true;
                ivAllSelect.setImageResource(R.drawable.checked);
            } else {
                checkAllSel = false;
                ivAllSelect.setImageResource(R.drawable.uncheck);
            }
        }
        adapter.notifyDataSetChanged();
    }
}





