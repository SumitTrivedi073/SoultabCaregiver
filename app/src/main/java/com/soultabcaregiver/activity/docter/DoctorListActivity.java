package com.soultabcaregiver.activity.docter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.docter.DoctorModel.DoctorCategoryModel;
import com.soultabcaregiver.activity.docter.DoctorModel.DoctorListModel;
import com.soultabcaregiver.activity.docter.adapter.DoctorListAdapter;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.sinch_calling.BaseActivity;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorListActivity extends BaseActivity implements View.OnClickListener, DoctorListAdapter.AppointedDocSelectionListener {

    Context mContext;
    ArrayList<DoctorCategoryModel.Response.CategoryDatum> arDoc = new ArrayList<>();
    SearchView search_edittext;
    DoctorListAdapter adapter;
    RecyclerView rvDoc;
    NestedScrollView nested_scrollview;
    FloatingActionButton lyBack_card;
    List<DoctorListModel.Response.DoctorDatum> doctorlist = new ArrayList<>();
    TextView tvNodata;
    DoctorCategoryModel.Response.CategoryDatum docCatBean;
    ImageView close_img, search_img, iv_search;
    private int lastPage = 1;
    private String mMaxoffset = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = this;
        GetIntentData();
        InitCompo();
        Listener();
        if (Utility.isNetworkConnected(this)) {
            GetDocList();
        } else {

            Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
        }


    }

    private void GetIntentData() {
        docCatBean = (DoctorCategoryModel.Response.CategoryDatum) getIntent().getSerializableExtra(APIS.DocItemListItem);

    }


    private void InitCompo() {
        lyBack_card = findViewById(R.id.lyBack_card);
        rvDoc = findViewById(R.id.rv_doc);
        search_edittext = findViewById(R.id.search_edittext);
        tvNodata = findViewById(R.id.tv_no_data_doc_list);

        rvDoc.setHasFixedSize(true);
        rvDoc.setLayoutManager(new LinearLayoutManager(this));

        search_edittext.setFocusableInTouchMode(true);
        search_edittext.requestFocus();


    }

    private void Listener() {

        lyBack_card.setOnClickListener(this);


        ImageView searchIcon = search_edittext.findViewById(R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_search));
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


        if (nested_scrollview != null) {
            nested_scrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (v.getChildAt(v.getChildCount() - 1) != null) {
                        if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) && scrollY > oldScrollY) {
                            lastPage = lastPage + 1;
                            if (Integer.parseInt(mMaxoffset) >= lastPage) {
                                Log.e("mMaxoffset2", String.valueOf(mMaxoffset));
                                Log.e("lastPage", String.valueOf(lastPage));
                                GetDocList2();
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

       showProgressDialog(getResources().getString(R.string.Loading));
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
                            adapter = new DoctorListAdapter(this, doctorlist, tvNodata);
                            adapter.DocSelection(DoctorListActivity.this);

                            rvDoc.setAdapter(adapter);
                            tvNodata.setVisibility(View.GONE);
                            rvDoc.setVisibility(View.VISIBLE);
                        } else {
                            rvDoc.setVisibility(View.GONE);
                            tvNodata.setVisibility(View.VISIBLE);

                        }
                    } else {
                        tvNodata.setText(doctorListModel.getMessage());
                        tvNodata.setVisibility(View.VISIBLE);
                        rvDoc.setVisibility(View.GONE);
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

       showProgressDialog(getResources().getString(R.string.Loading));
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

                            adapter = new DoctorListAdapter(this, doctorlist, tvNodata);
                            adapter.DocSelection(DoctorListActivity.this);

                            rvDoc.setAdapter(adapter);
                            tvNodata.setVisibility(View.GONE);
                            rvDoc.setVisibility(View.VISIBLE);
                        } else {
                            rvDoc.setVisibility(View.GONE);
                            tvNodata.setVisibility(View.VISIBLE);

                        }
                    } else {
                        tvNodata.setText(doctorListModel.getMessage());
                        tvNodata.setVisibility(View.VISIBLE);
                        rvDoc.setVisibility(View.GONE);
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
    public void onClick(View v) {
        if (v.getId() == R.id.lyBack_card) {
            finish();
        }
    }


    @Override
    public void DocSelectionListener(List<DoctorListModel.Response.DoctorDatum> DocBeanList, boolean isSearch) {

    }
}
