package com.soultabcaregiver.activity.shopping;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.shopping.adapter.ShoppingListAdapter;
import com.soultabcaregiver.activity.shopping.model.ShoppingCategoryModel;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingCategoryActivity extends BaseActivity implements View.OnClickListener {
	
	public Context mContext;
	
	RecyclerView shopping_category_list;
	
	RelativeLayout ly_back;
	
	String TAG = ShoppingCategoryActivity.class.toString();
	
	List<ShoppingCategoryModel.Response.ProductCategoryDatum> categoryDatumList;
	
	ShoppingListAdapter shoppingListAdapter;
	
	RelativeLayout tvNodata_relative, back_btn;
	
	TextView NoDataFoundtxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_category);
		
		FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
		Bundle bundle = new Bundle();
		bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, getLocalClassName().trim());
		bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, getLocalClassName().trim());
		mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
		
		mContext = this;
		shopping_category_list = findViewById(R.id.shopping_category_list);
		NoDataFoundtxt = findViewById(R.id.NoDataFoundtxt);
		tvNodata_relative = findViewById(R.id.tvNodata_relative);
		back_btn = findViewById(R.id.back_btn);
		
		back_btn.setOnClickListener(this);
		
		GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
		shopping_category_list.setLayoutManager(gridLayoutManager);
		if (Utility.isNetworkConnected(mContext)) {
			GetShoppingCategories();
		} else {
			
			Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
			
		}
	}
	
	private void GetShoppingCategories() {
		showProgressDialog(getResources().getString(R.string.Loading));
		StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST,
				APIS.BASEURL + APIS.ShoppingProductCateogry_list, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				
				ShoppingCategoryModel articlesCategoryModel =
						new Gson().fromJson(response, ShoppingCategoryModel.class);
				if (String.valueOf(articlesCategoryModel.getStatusCode()).equals("200")) {
					
					tvNodata_relative.setVisibility(View.GONE);
					shopping_category_list.setVisibility(View.VISIBLE);
					categoryDatumList =
							articlesCategoryModel.getResponse().getProductCategoryData();
					if (categoryDatumList.size() != 0) {
						shoppingListAdapter = new ShoppingListAdapter(mContext, categoryDatumList);
						shopping_category_list.setAdapter(shoppingListAdapter);
						
						is40plususer();
					} else {
						hideProgressDialog();
						NoDataFoundtxt.setText(getResources().getString(R.string.no_data_found));
						tvNodata_relative.setVisibility(View.VISIBLE);
						shopping_category_list.setVisibility(View.GONE);
						
					}
					
				} else {
					hideProgressDialog();
					NoDataFoundtxt.setText(getResources().getString(R.string.no_data_found));
					tvNodata_relative.setVisibility(View.VISIBLE);
					shopping_category_list.setVisibility(View.GONE);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.d(TAG, "Error: " + error.getMessage());
				hideProgressDialog();
				NoDataFoundtxt.setText(getResources().getString(R.string.no_data_found));
				tvNodata_relative.setVisibility(View.VISIBLE);
				shopping_category_list.setVisibility(View.GONE);
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
		AppController.getInstance().addToRequestQueue(jsonObjectRequest);
		jsonObjectRequest.setShouldCache(false);
		jsonObjectRequest.setRetryPolicy(
				new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}
	
	private void is40plususer() {
		
		showProgressDialog(mContext.getResources().getString(R.string.Loading));
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
				APIS.BASEURL40Plus + APIS.isplus40userexist + Utility.getSharedPreferences(mContext,
						APIS.Caregiver_email), null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				Log.e(TAG, "is40plususerresponse=" + response.toString());
				hideProgressDialog();
				try {
					if (response.getString("id") != null && !TextUtils.isEmpty(
							response.getString("id"))) {
						
						Utility.setSharedPreference(mContext, APIS.is_40plus_user, "1");
						Utility.setSharedPreference(mContext, APIS.is_40plus_userID,
								response.getString("id"));
						
						
					} else {
						Utility.ShowToast(mContext, response.getString("message"));
						hideProgressDialog();
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.d(TAG, "Error: " + error.getMessage());
				error.printStackTrace();
				hideProgressDialog();
				
			}
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
				params.put("Authorization", "Basic YWRtaW46TW9iaXZAIzEyMw==");
				
				return params;
			}
			
			
		};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjReq);
		jsonObjReq.setShouldCache(false);
		jsonObjReq.setRetryPolicy(
				new DefaultRetryPolicy(10000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_btn:
				finish();
				break;
		}
	}
}