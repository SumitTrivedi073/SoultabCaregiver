package com.soultabcaregiver.activity.docter.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.docter.DoctorModel.DoctorCategoryModel;
import com.soultabcaregiver.activity.docter.DoctorModel.DoctorListModel;
import com.soultabcaregiver.activity.docter.adapter.DoctorListAdapter;
import com.soultabcaregiver.sinch_calling.BaseFragment;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorListFragment extends BaseFragment implements DoctorListAdapter.AppointedDocSelectionListener{

    View view;
    Context mContext;
    SearchView doctor_search;
    DoctorListAdapter adapter;
    RecyclerView doctor_list;
    NestedScrollView nested_scrollview;
    List<DoctorListModel.Response.DoctorDatum> doctorlist = new ArrayList<>();
    TextView tvNodata;
    DoctorCategoryModel.Response.CategoryDatum docCatBean;

    private int lastPage = 1;
    private String mMaxoffset = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_doctor_list, container, false);

         init();
        if (Utility.isNetworkConnected(mContext)) {
            GetDocList();
        } else {

            Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
        }

         return view;
    }

    private void init() {
        doctor_list = view.findViewById(R.id.doctor_list);
        tvNodata = view.findViewById(R.id.tv_no_data_doc_list);
        doctor_search = view.findViewById(R.id.doctor_search);

        ImageView searchIcon = doctor_search.findViewById(R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_search));
        searchIcon.setColorFilter(getResources().getColor(R.color.themecolor));

        ImageView searchClose = doctor_search.findViewById(R.id.search_close_btn);
        searchClose.setColorFilter(getResources().getColor(R.color.themecolor));


        EditText searchEditText = doctor_search.findViewById(R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.themecolor));
        searchEditText.setHintTextColor(getResources().getColor(R.color.themecolor));
        searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen._14sdp));

        doctor_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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



        if (nested_scrollview != null) {
            nested_scrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (v.getChildAt(v.getChildCount() - 1) != null) {
                        if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) && scrollY > oldScrollY) {
                            lastPage = lastPage + 1;
                            if (Integer.parseInt(mMaxoffset) >= lastPage) {
                                if (Utility.isNetworkConnected(mContext)) {
                                    GetDocList();
                                } else {

                                    Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
                                }
                            } else {
                                Utility.ShowToast(mContext, "Sorry, no more data available!");
                            }
                        }
                    }
                }
            });

        }
    }

    public void GetDocList() {
        final String TAG = "doctor list";
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("page_no", lastPage);

            Log.e(TAG, "Doctor list_mainObject====>" + mainObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog(mContext,getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.GETDOCLISTAPI, mainObject,
                response -> {
                    Log.d(TAG, "doctor list response=" + response.toString());
                    hideProgressDialog();
                    DoctorListModel doctorListModel = new Gson().fromJson(response.toString(),
                            DoctorListModel.class);

                    if (String.valueOf(doctorListModel.getStatusCode()).equals("200")) {

                        doctorlist = doctorListModel.getResponse().getDoctorData();

                        mMaxoffset = String.valueOf(doctorListModel.getPages());
                        if (doctorlist.size() > 0) {
                            adapter = new DoctorListAdapter(getActivity(), doctorlist, tvNodata);
                            adapter.DocSelection(DoctorListFragment.this);

                            doctor_list.setAdapter(adapter);
                            tvNodata.setVisibility(View.GONE);
                            doctor_list.setVisibility(View.VISIBLE);
                        } else {
                            doctor_list.setVisibility(View.GONE);
                            tvNodata.setVisibility(View.VISIBLE);

                        }
                    } else {
                        tvNodata.setText(doctorListModel.getMessage());
                        tvNodata.setVisibility(View.VISIBLE);
                        doctor_list.setVisibility(View.GONE);
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
                return params;
            }
        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void GetDocList2() {
        final String TAG = "doctor list";
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("page_no", lastPage);

            Log.e(TAG, "Doctor list_mainObject====>" + mainObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog(mContext,getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.GETDOCLISTAPI, mainObject,
                response -> {
                    Log.d(TAG, "doctor list response=" + response.toString());
                    hideProgressDialog();
                    DoctorListModel doctorListModel = new Gson().fromJson(response.toString(),
                            DoctorListModel.class);

                    if (String.valueOf(doctorListModel.getStatusCode()).equals("200")) {


                        if (doctorListModel.getResponse().getDoctorData().size() > 0) {

                            List<DoctorListModel.Response.DoctorDatum> doctorlistdata2 = new ArrayList<>();

                            doctorlistdata2.addAll(doctorListModel.getResponse().getDoctorData());
                            doctorlist.addAll(doctorlistdata2);

                            adapter = new DoctorListAdapter(getActivity(), doctorlist, tvNodata);
                            adapter.DocSelection(DoctorListFragment.this);

                            doctor_list.setAdapter(adapter);
                            tvNodata.setVisibility(View.GONE);
                            doctor_list.setVisibility(View.VISIBLE);
                        } else {
                            doctor_list.setVisibility(View.GONE);
                            tvNodata.setVisibility(View.VISIBLE);

                        }
                    } else {
                        tvNodata.setText(doctorListModel.getMessage());
                        tvNodata.setVisibility(View.VISIBLE);
                        doctor_list.setVisibility(View.GONE);
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
    public void DocSelectionListener(List<DoctorListModel.Response.DoctorDatum> DocBeanList, boolean isSearch) {

    }
}
