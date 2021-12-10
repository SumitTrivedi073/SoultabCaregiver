package com.soultabcaregiver.activity.shopping;

import static com.soultabcaregiver.WebService.APIS.ShoppingAuthorizationKey;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.WebService.ApiTokenAuthentication;
import com.soultabcaregiver.activity.shopping.adapter.ShoppingListAdapter;
import com.soultabcaregiver.activity.shopping.model.ShoppingCategoryModel;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingCategoryActivity extends BaseActivity implements View.OnClickListener {
	
	public Context mContext;
	
	RecyclerView shopping_category_list;
	
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
				hideProgressDialog();
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
						
						//is40plususer();
					} else {
						
						NoDataFoundtxt.setText(getResources().getString(R.string.no_data_found));
						tvNodata_relative.setVisibility(View.VISIBLE);
						shopping_category_list.setVisibility(View.GONE);
						
					}
					
				} else {
					
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
				if (error.networkResponse!=null) {
					if (String.valueOf(error.networkResponse.statusCode).equals(APIS.APITokenErrorCode)||String.valueOf(error.networkResponse.statusCode).equals(APIS.APITokenErrorCode2)) {
						ApiTokenAuthentication.refrehToken(mContext, updatedToken -> {
							if (updatedToken == null) {
							} else {
								GetShoppingCategories();
							}
						});
					}} else {
					Utility.ShowToast(mContext,
							mContext.getResources().getString(R.string.something_went_wrong));
				}
			}
		}) {
			@Override
			public Map<String, String> getHeaders() {
				Map<String, String> params = new HashMap<>();
				params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
				params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
				params.put(APIS.APITokenKEY, Utility.getSharedPreferences(mContext, APIS.APITokenValue));
				
				return params;
			}
		};
		AppController.getInstance().addToRequestQueue(jsonObjectRequest);
		jsonObjectRequest.setShouldCache(false);
		jsonObjectRequest.setRetryPolicy(
				new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}
	
	private void is40plususer() {
		RequestQueue requestQueue = Volley.newRequestQueue(this);
		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
				APIS.BASEURL40Plus + APIS.isplus40userexist + Utility.getSharedPreferences(mContext,
						APIS.Caregiver_email), null, new Response.Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				hideProgressDialog();
				JSONArray jsonArray = response;
				System.out.println("response==========>" + response);
				String id = "";
				try {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						id = jsonObject.getString("id");
					}
					Utility.setSharedPreference(mContext, APIS.is_40plus_user, "1");
					Utility.setSharedPreference(mContext, APIS.is_40plus_userID, id);
					
				} catch (Exception w) {
					Toast.makeText(ShoppingCategoryActivity.this, w.getMessage(),
							Toast.LENGTH_LONG).show();
					System.out.println("Error==========>" + w.getMessage());
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				hideProgressDialog();
				Toast.makeText(ShoppingCategoryActivity.this, error.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
				//Development
				//	params.put("Authorization", "Basic YWRtaW46TW9iaXYxMjNAIw==");
				//Production
				params.put("Authorization", ShoppingAuthorizationKey);
			

				return params;
			}
			
			
		};
		requestQueue.add(jsonArrayRequest);
		
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