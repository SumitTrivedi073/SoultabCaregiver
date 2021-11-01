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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.WebService.ApiTokenAuthentication;
import com.soultabcaregiver.activity.docter.DoctorModel.DoctorCategoryModel;
import com.soultabcaregiver.activity.docter.DoctorModel.DoctorListModel;
import com.soultabcaregiver.activity.docter.adapter.MyDoctorListAdapter;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

public class MyDoctorListFragment extends BaseFragment implements MyDoctorListAdapter.AppointedDocSelectionListener {

    public static MyDoctorListFragment instance;
    View view;
    Context mContext;
    SearchView doctor_search;
    MyDoctorListAdapter adapter;
    RecyclerView doctor_list;
    NestedScrollView nested_scrollview;
    List<DoctorListModel.Response.DoctorDatum> doctorlist = new ArrayList<>();
    TextView tvNodata;
    DoctorCategoryModel.Response.CategoryDatum docCatBean;
    RelativeLayout search_relative;
    private int lastPage = 1;
    private String mMaxoffset = "";
    DoctorListFragment doctorListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_doctor_list, container, false);

        instance = MyDoctorListFragment.this;
        doctorListFragment = DoctorListFragment.instance;
        init();

        return  view;

    }

    private void init() {
        doctor_list = view.findViewById(R.id.my_doctor_list);
        tvNodata = view.findViewById(R.id.tv_no_data_doc_list);
        doctor_search = view.findViewById(R.id.doctor_search);
        search_relative = view.findViewById(R.id.search_relative);


        search_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doctor_search.setFocusableInTouchMode(true);
                doctor_search.requestFocus();
                doctor_search.onActionViewExpanded();

            }
        });
    
        ImageView searchIcon = doctor_search.findViewById(R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_search));
        searchIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.themecolor));
    
        ImageView searchClose = doctor_search.findViewById(R.id.search_close_btn);
        searchClose.setColorFilter(ContextCompat.getColor(mContext, R.color.themecolor));
    
        EditText searchEditText = doctor_search.findViewById(R.id.search_src_text);
        searchEditText.setTextColor(ContextCompat.getColor(mContext, R.color.themecolor));
        searchEditText.setHintTextColor(ContextCompat.getColor(mContext, R.color.themecolor));
        searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelSize(R.dimen._14sdp));
    
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

        searchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doctor_search.onActionViewCollapsed();
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

    @Override
    public void onResume() {
        super.onResume();
        if (Utility.isNetworkConnected(mContext)) {
         GetDocList();
        } else {

            Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
        }

    }

    public void GetDocList() {
        final String TAG = "doctor list";
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("page_no", lastPage);
            mainObject.put("user_id", Utility.getSharedPreferences(mContext,APIS.caregiver_id));

            Log.e(TAG, "Doctor list_mainObject====>" + mainObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog(mContext, getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.GETMYDOCLISTAPI, mainObject,
                response -> {
                    Log.d(TAG, "doctor list response=" + response.toString());
                    hideProgressDialog();
                    DoctorListModel doctorListModel = new Gson().fromJson(response.toString(),
                            DoctorListModel.class);

                    if (String.valueOf(doctorListModel.getStatusCode()).equals("200")) {

                        doctorlist = doctorListModel.getResponse().getDoctorData();

                        mMaxoffset = String.valueOf(doctorListModel.getPages());
                        if (doctorlist.size() > 0) {
                            adapter = new MyDoctorListAdapter(getActivity(), doctorlist, tvNodata);
                            adapter.DocSelection(MyDoctorListFragment.this);
                            adapter.DocFavSelection(MyDoctorListFragment.this);
                            doctor_list.setAdapter(adapter);
                            tvNodata.setVisibility(View.GONE);
                            doctor_list.setVisibility(View.VISIBLE);
                        } else {
                            doctor_list.setVisibility(View.GONE);
                            tvNodata.setVisibility(View.VISIBLE);

                        }
                    } else if (String.valueOf(doctorListModel.getStatusCode()).equals("403")) {
                        logout_app(doctorListModel.getMessage());
                    } else {
                        tvNodata.setText(doctorListModel.getMessage());
                        tvNodata.setVisibility(View.VISIBLE);
                        doctor_list.setVisibility(View.GONE);
                    }
                }, error -> {
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            hideProgressDialog();
    
            if (error.networkResponse!=null) {
                if (String.valueOf(error.networkResponse.statusCode).equals(APIS.APITokenErrorCode)||String.valueOf(error.networkResponse.statusCode).equals(APIS.APITokenErrorCode2)) {
                    ApiTokenAuthentication.refrehToken(mContext, updatedToken -> {
                        if (updatedToken == null) {
                        } else {
                            GetDocList();
                    
                        }
                    });
                }else {
                    Utility.ShowToast(
                            mContext,
                            getResources().getString(R.string.something_went_wrong));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext,APIS.EncodeUser_id));
                params.put(APIS.APITokenKEY,
                        Utility.getSharedPreferences(mContext, APIS.APITokenValue));
    
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
            mainObject.put("user_id", Utility.getSharedPreferences(mContext,APIS.caregiver_id));

            Log.e(TAG, "Doctor list_mainObject====>" + mainObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

       // showProgressDialog(mContext, getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.GETMYDOCLISTAPI, mainObject,
                response -> {
                    Log.d(TAG, "doctor list response=" + response.toString());
                    hideProgressDialog();
                    DoctorListModel doctorListModel = new Gson().fromJson(response.toString(),
                            DoctorListModel.class);

                    if (String.valueOf(doctorListModel.getStatusCode()).equals("200")) {

                        doctorlist = doctorListModel.getResponse().getDoctorData();

                        mMaxoffset = String.valueOf(doctorListModel.getPages());
                        if (doctorlist.size() > 0) {
                            adapter = new MyDoctorListAdapter(getActivity(), doctorlist, tvNodata);
                            adapter.DocSelection(MyDoctorListFragment.this);
                            adapter.DocFavSelection(MyDoctorListFragment.this);
                            doctor_list.setAdapter(adapter);
                            tvNodata.setVisibility(View.GONE);
                            doctor_list.setVisibility(View.VISIBLE);
                        } else {
                            doctor_list.setVisibility(View.GONE);
                            tvNodata.setVisibility(View.VISIBLE);

                        }
                    } else if (String.valueOf(doctorListModel.getStatusCode()).equals("403")) {
                        logout_app(doctorListModel.getMessage());
                    } else {
                        tvNodata.setText(doctorListModel.getMessage());
                        tvNodata.setVisibility(View.VISIBLE);
                        doctor_list.setVisibility(View.GONE);
                    }
                }, error -> {
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            hideProgressDialog();
    
            if (error.networkResponse!=null) {
                if (String.valueOf(error.networkResponse.statusCode).equals(APIS.APITokenErrorCode)||String.valueOf(error.networkResponse.statusCode).equals(APIS.APITokenErrorCode2)) {
                    ApiTokenAuthentication.refrehToken(mContext, updatedToken -> {
                        if (updatedToken == null) {
                        } else {
                            GetDocList2();
                    
                        }
                    });
                }else {
                    Utility.ShowToast(
                            mContext,
                            getResources().getString(R.string.something_went_wrong));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext,APIS.EncodeUser_id));
                params.put(APIS.APITokenKEY,
                        Utility.getSharedPreferences(mContext, APIS.APITokenValue));
    
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

    @Override
    public void DocFavListener(DoctorListModel.Response.DoctorDatum DocBeanList, int position) {
        if (Utility.isNetworkConnected(mContext)) {

            AddFavoritDoctor(DocBeanList.getId(), DocBeanList.getFavorite(), position);
        } else {
            Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
        }
    }

    public void AddFavoritDoctor(String doctor_id, String favorite, int position) {
        final String TAG = "ActAllPhoto";
        JSONObject mainObject = new JSONObject();

        try {
            mainObject.put("user_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));
            mainObject.put("doctor_id", doctor_id);
            mainObject.put("favorite", "0");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "addFavDoctor====>" + mainObject.toString());
        showProgressDialog(mContext,getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.GETMYFAVDOCAPI, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "AddFavoritDoctor response=" + response.toString());
                        hideProgressDialog();
                        try {
                            String code = response.getString("status_code");
                            if (code.equals("200")) {

                                try {
                                    String message = response.getString("response");

                                    Utility.ShowToast(mContext, message);

                                    doctorlist.remove(position);
                                    adapter.notifyDataSetChanged();
                                    DoctorListFragment.getInstance().GetDocList2();

                                    if (doctorlist.size() > 0) {
                                        tvNodata.setVisibility(View.GONE);
                                        doctor_list.setVisibility(View.VISIBLE);
                                    } else {
                                        tvNodata.setText(message);
                                        tvNodata.setVisibility(View.VISIBLE);
                                        doctor_list.setVisibility(View.GONE);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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
                if (error.networkResponse!=null) {
                    if (String.valueOf(error.networkResponse.statusCode).equals(APIS.APITokenErrorCode)||String.valueOf(error.networkResponse.statusCode).equals(APIS.APITokenErrorCode2)) {
                        ApiTokenAuthentication.refrehToken(mContext, updatedToken -> {
                            if (updatedToken == null) {
                            } else {
                                AddFavoritDoctor(doctor_id,favorite,position);
                    
                            }
                        });
                    }else {
                        Utility.ShowToast(
                                mContext,
                                getResources().getString(R.string.something_went_wrong));
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext,APIS.EncodeUser_id));
                params.put(APIS.APITokenKEY,
                        Utility.getSharedPreferences(mContext, APIS.APITokenValue));
    
                return params;
            }

        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    public static MyDoctorListFragment getInstance() {
        return instance;
    }

}