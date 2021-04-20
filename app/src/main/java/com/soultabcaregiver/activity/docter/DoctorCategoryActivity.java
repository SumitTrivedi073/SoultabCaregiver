package com.soultabcaregiver.activity.docter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.docter.DoctorModel.DoctorCategoryModel;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.sinch_calling.BaseActivity;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DoctorCategoryActivity extends BaseActivity implements View.OnClickListener {

    Context mContext;
    FloatingActionButton lyBack_card;
    CustomGridAdapter docCatAdpt;
    GridView gvDocCat;
    TextView tvNodata;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_doctor_category);


        mContext = this;
        init();
        Listener();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.lyBack_card) {
            finish();
        }

    }

    private void Listener() {
        lyBack_card.setOnClickListener(this);
    }

    private void init() {
        gvDocCat = findViewById(R.id.gv_doc_cat);
        lyBack_card = findViewById(R.id.lyBack_card);
        tvNodata = findViewById(R.id.tv_no_data_doc_cat);
        CallDocCat();
    }

    public void CallDocCat() {

        if (Utility.isNetworkConnected(this)) {
            GetDocCate();
        } else {
            Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
        }

    }

    public void GetDocCate() {
        final String TAG = "GetDoctor";
       showProgressDialog(getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.GETDOCCATAPI, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "doc cat response=" + response.toString());
                        hideProgressDialog();

                        DoctorCategoryModel doctorCategoryModel = new Gson().fromJson(response.toString(), DoctorCategoryModel.class);
                        try {
                            if (doctorCategoryModel.getStatusCode() == 200) {
                                if (doctorCategoryModel.getResponse().getCategoryData() != null &&
                                        doctorCategoryModel.getResponse().getCategoryData().size() > 0) {
                                    tvNodata.setVisibility(View.GONE);
                                    docCatAdpt = new CustomGridAdapter(mContext,
                                            doctorCategoryModel.getResponse().getCategoryData());
                                    gvDocCat.setAdapter(docCatAdpt);

                                }
                            } else {
                                tvNodata.setText(doctorCategoryModel.getMessage());
                                tvNodata.setVisibility(View.VISIBLE);
                                Utility.ShowToast(mContext, doctorCategoryModel.getMessage());

                            }
                        } catch (Exception e) {
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
    }

    public class CustomGridAdapter extends BaseAdapter {
        Context context;
        private List<DoctorCategoryModel.Response.CategoryDatum> arDocCatIn;
        private LayoutInflater inflater;

        CustomGridAdapter(Context context_, List<DoctorCategoryModel.Response.CategoryDatum> arPhoto_) {
            arDocCatIn = arPhoto_;
            context = context_;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return arDocCatIn.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder = new Holder();
            final View rowView;
            rowView = inflater.inflate(R.layout.doc_category_item, null);

            holder.lyMain = rowView.findViewById(R.id.ly_call);
            holder.ivDocCat = rowView.findViewById(R.id.iv_call);
            holder.tvTitle = rowView.findViewById(R.id.tv_call);

            FrameLayout.LayoutParams params;
//        if (itemPosition % 2 ==0){
            params = new FrameLayout.LayoutParams((int) (Utility.getScreenWidth(context) / 2.2)
                    , (int) (Utility.getScreenWidth(context) / 2.2));

            if (position % 2 == 0) {
                params.setMargins(0
                        , context.getResources().getDimensionPixelOffset(R.dimen._10sdp),
                        context.getResources().getDimensionPixelOffset(R.dimen._5sdp),
                        0);
            } else {
                params.setMargins(context.getResources().getDimensionPixelOffset(R.dimen._5sdp)
                        , context.getResources().getDimensionPixelOffset(R.dimen._10sdp),
                        0,
                        0);
            }



            holder.lyMain.setLayoutParams(params);


            final DoctorCategoryModel.Response.CategoryDatum docCatBean = arDocCatIn.get(position);
            if (!TextUtils.isEmpty(docCatBean.getIcon())) {
                Glide.with(context).load(docCatBean.getIcon()).placeholder(R.drawable.place_holder_photo).into(holder.ivDocCat);
            }
            holder.tvTitle.setText(docCatBean.getName());
           /* holder.lyMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, DoctorListActivity.class).putExtra(APIS.DocItemListItem, docCatBean));
                }
            });*/

            return rowView;
        }

        class Holder {
            ImageView ivDocCat;
            LinearLayout lyMain;
            TextView tvTitle;
        }

    }

}
