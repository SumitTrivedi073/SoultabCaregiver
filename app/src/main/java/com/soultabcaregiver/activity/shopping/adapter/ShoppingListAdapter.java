package com.soultabcaregiver.activity.shopping.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.docter.SocialActivity;
import com.soultabcaregiver.activity.shopping.model.ShoppingCategoryModel;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.CustomProgressDialog;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import static com.android.volley.VolleyLog.TAG;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> implements Filterable, OnCountryPickerListener {
	
	private final int sortBy = com.mukesh.countrypicker.CountryPicker.SORT_BY_NONE;
	
	private final List<ShoppingCategoryModel.Response.ProductCategoryDatum> arSearch;
	
	private final Context mcontext;
	
	AlertDialog alertDialog;
	
	TextView countryCodeTv;
	
	String CountryCode;
	
	private List<ShoppingCategoryModel.Response.ProductCategoryDatum> ShoppingCategoryList;
	
	private CustomProgressDialog progressDialog;
	
	private com.mukesh.countrypicker.CountryPicker countryPicker;
	
	public ShoppingListAdapter(Context context_,
	                           List<ShoppingCategoryModel.Response.ProductCategoryDatum> mMusicList_) {
		this.ShoppingCategoryList = mMusicList_;
		this.mcontext = context_;
		this.arSearch = new ArrayList<>();
		this.arSearch.addAll(ShoppingCategoryList);
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		View contactView = inflater.inflate(R.layout.shopping_category_item, parent, false);
		return new ViewHolder(contactView);
	}
	
	// Involves populating data into the item through holder
	@Override
	public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
		FrameLayout.LayoutParams params;
		//        if (itemPosition % 2 ==0){
		params = new FrameLayout.LayoutParams((int) (Utility.getScreenWidth(mcontext) / 2.2),
				(int) (Utility.getScreenWidth(mcontext) / 2.2));
		
		if (position % 2 == 0) {
			params.setMargins(0, mcontext.getResources().getDimensionPixelOffset(R.dimen._10sdp),
					mcontext.getResources().getDimensionPixelOffset(R.dimen._5sdp), 0);
		} else {
			params.setMargins(mcontext.getResources().getDimensionPixelOffset(R.dimen._5sdp),
					mcontext.getResources().getDimensionPixelOffset(R.dimen._10sdp), 0, 0);
		}
		
		viewHolder.ll_layout_categoryItem.setLayoutParams(params);
		
		final ShoppingCategoryModel.Response.ProductCategoryDatum categoryDatum =
				ShoppingCategoryList.get(position);
		
		viewHolder.ll_layout_categoryItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String firstname = Utility.getSharedPreferences(mcontext, APIS.Caregiver_name);
				String lastname = Utility.getSharedPreferences(mcontext, APIS.Caregiver_lastname);
				String email = Utility.getSharedPreferences(mcontext, APIS.Caregiver_email);
				String telephone = Utility.getSharedPreferences(mcontext, APIS.Caregiver_mobile);
				
				if (Utility.getSharedPreferences(mcontext,
						APIS.is_40plus_user) != null && !TextUtils.isEmpty(
						Utility.getSharedPreferences(mcontext, APIS.is_40plus_user))) {
					if (Utility.getSharedPreferences(mcontext, APIS.is_40plus_user).equals("0")) {
						ShowDetailPopup(categoryDatum.getWebUrl(), firstname, lastname, email,
								telephone);
					} else {
						Intent intent = new Intent(mcontext, SocialActivity.class);
						intent.putExtra("webUrl",
								categoryDatum.getWebUrl() + "?userId=" + Utility.getSharedPreferences(
										mcontext, APIS.is_40plus_userID));
						intent.putExtra("title",
								mcontext.getResources().getString(R.string.plusmart));
						mcontext.startActivity(intent);
					}
					
				} else {
					ShowDetailPopup(categoryDatum.getWebUrl(), firstname, lastname, email,
							telephone);
					
				}
				
			}
		});
		
		if (categoryDatum.getId() != null) {
			viewHolder.tv_name_categoryItem.setText(categoryDatum.getName());
		}
		
		if (!TextUtils.isEmpty(categoryDatum.getCategoryIcon())) {
			Glide.with(mcontext).load(categoryDatum.getCategoryIcon()).placeholder(
					R.drawable.place_holder_photo).into(viewHolder.iv_image_categoryItem);
		}
	}
	
	private void ShowDetailPopup(String webUrl, String firstname, String lastname, String email,
	                             String telephone) {
		LayoutInflater inflater =
				(LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.shopping_profile_setup, null);
		final AlertDialog.Builder builder =
				new AlertDialog.Builder(mcontext, R.style.MyDialogTheme);
		
		builder.setView(layout);
		builder.setCancelable(true);
		alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		alertDialog.getWindow().setGravity(Gravity.CENTER);
		alertDialog.show();
		
		EditText txt_firstname = layout.findViewById(R.id.txt_firstname);
		EditText last_name_txt = layout.findViewById(R.id.last_name_txt);
		TextView UserName_txt = layout.findViewById(R.id.username_txt);
		TextView email_address_txt = layout.findViewById(R.id.email_address_txt);
		EditText enter_telephone_txt = layout.findViewById(R.id.enter_telephone_txt);
		EditText enter_password_txt = layout.findViewById(R.id.et_pass);
		CheckBox iv_show_pass = layout.findViewById(R.id.iv_show_pass);
		TextView no_txt = layout.findViewById(R.id.no_txt);
		TextView yes_txt = layout.findViewById(R.id.yes_txt);
		LinearLayout countryLL = layout.findViewById(R.id.countryLL);
		countryCodeTv = layout.findViewById(R.id.countryCodeTv);
		
		txt_firstname.setText(firstname);
		last_name_txt.setText(lastname);
		email_address_txt.setText(email);
		enter_telephone_txt.setText(telephone);
		UserName_txt.setText(Utility.getSharedPreferences(mcontext, APIS.Caregiver_username));
		
		countryCodeTv.setText(Utility.getSharedPreferences(mcontext, APIS.Caregiver_countrycode));
		CountryCode = Utility.getSharedPreferences(mcontext, APIS.Caregiver_countrycode);
		
		iv_show_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked) {
					
					enter_password_txt.setTransformationMethod(new PasswordTransformationMethod());
					
				} else {
					
					// hide password
					enter_password_txt.setTransformationMethod(null);
				}
				
			}
		});
		
		countryLL.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				showPicker();
			}
		});
		
		yes_txt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (txt_firstname.getText().toString().isEmpty()) {
					Utility.ShowToast(mcontext,
							mcontext.getResources().getString(R.string.hint_usr));
				} else if (last_name_txt.getText().toString().isEmpty()) {
					Utility.ShowToast(mcontext,
							mcontext.getResources().getString(R.string.hint_usr2));
					
				} else if (UserName_txt.getText().toString().isEmpty()) {
					Utility.ShowToast(mcontext,
							mcontext.getResources().getString(R.string.enter_user_name));
					
				} else if (email_address_txt.getText().toString().isEmpty()) {
					Utility.ShowToast(mcontext,
							mcontext.getResources().getString(R.string.enter_your_email_address));
					
				} else if (enter_telephone_txt.getText().toString().isEmpty()) {
					Utility.ShowToast(mcontext,
							mcontext.getResources().getString(R.string.hint_phone));
					
				} else if (!Utility.isValidMobile(enter_telephone_txt.getText().toString())) {
					Utility.ShowToast(mcontext,
							mcontext.getResources().getString(R.string.valid_phone1));
					
				} else if (enter_password_txt.getText().toString().isEmpty()) {
					Utility.ShowToast(mcontext,
							mcontext.getResources().getString(R.string.valid_pass));
					
				} else if (!Utility.isvalidatePassword(enter_password_txt.getText().toString())) {
					Utility.ShowToast(mcontext,
							mcontext.getResources().getString(R.string.valid_pass1));
					
				} else {
					
					alertDialog.dismiss();
					Createplusmart_user(txt_firstname.getText().toString().trim(),
							last_name_txt.getText().toString().trim(),
							UserName_txt.getText().toString().trim(),
							email_address_txt.getText().toString().trim(),
							countryCodeTv.getText().toString().trim() + enter_telephone_txt.getText().toString(),
							enter_password_txt.getText().toString(), webUrl);
				}
				
			}
		});
		
		no_txt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		
		
	}
	
	private void showPicker() {
		com.mukesh.countrypicker.CountryPicker.Builder builder =
				new com.mukesh.countrypicker.CountryPicker.Builder().with(mcontext).listener(
						ShoppingListAdapter.this);
		builder.canSearch(true);
		builder.sortBy(sortBy);
		countryPicker = builder.build();
		countryPicker.showDialog((AppCompatActivity) mcontext);
		
		
	}
	
	public void Createplusmart_user(String firstname, String lastname, String UserName,
	                                String emailAddress, String telephone, String password,
	                                String webUrl) {
		JSONObject mainObject = new JSONObject();
		try {
			mainObject.put("email", emailAddress);
			mainObject.put("username", UserName);
			mainObject.put("phonenumber", telephone);
			mainObject.put("password", password);
			mainObject.put("first_name", firstname);
			mainObject.put("last_name", lastname);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.e("", "Create40PlusUser " + mainObject.toString());
		showProgressDialog(mcontext.getResources().getString(R.string.Loading));
		JsonObjectRequest jsonObjReq =
				new JsonObjectRequest(Request.Method.POST, APIS.BASEURL40Plus + APIS.plus40Signup,
						mainObject, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.e(TAG, "response=" + response.toString());
						
						try {
							if (response.getString("username") != null && !TextUtils.isEmpty(
									response.getString("username"))) {
								
								Utility.setSharedPreference(mcontext, APIS.is_40plus_userID,
										response.getString("id"));
								UpdateUserProfile(webUrl);
								
							} else {
								Utility.ShowToast(mcontext, response.getString("message"));
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
	
	public void showProgressDialog(String message) {
		if (progressDialog == null)
			progressDialog = new CustomProgressDialog(mcontext, message);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	public void UpdateUserProfile(String webUrl) {
		JSONObject mainObject = new JSONObject();
		try {
			mainObject.put("user_id", Utility.getSharedPreferences(mcontext, APIS.caregiver_id));
			mainObject.put("is_40plus_user", "1");
			
			Log.e("Forgot_password", mainObject.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//showProgressDialog(mcontext.getResources().getString(R.string.Loading));
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
				APIS.BASEURL + APIS.update_caregiver_flags, mainObject,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d(TAG, "forgot response=" + response.toString());
						hideProgressDialog();
						try {
							String code = response.getString("status_code");
							if (code.equals("200")) {
								
								Utility.setSharedPreference(mcontext, APIS.is_40plus_user, "1");
								Intent intent = new Intent(mcontext, SocialActivity.class);
								intent.putExtra("webUrl",
										webUrl + "?userId=" + Utility.getSharedPreferences(mcontext,
												APIS.is_40plus_userID));
								intent.putExtra("title",
										mcontext.getResources().getString(R.string.plusmart));
								mcontext.startActivity(intent);
								
								
							} else {
								String msg = response.getString("message");
								Utility.ShowToast(mcontext, msg);
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
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
				params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
				return params;
			}
			
		};
		AppController.getInstance().addToRequestQueue(jsonObjReq);
		jsonObjReq.setShouldCache(false);
		jsonObjReq.setRetryPolicy(
				new DefaultRetryPolicy(10000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		
	}
	
	public void hideProgressDialog() {
		if (progressDialog != null)
			progressDialog.dismiss();
	}
	
	@Override
	public long getItemId(int position) {
		return super.getItemId(position);
	}
	
	@Override
	public int getItemCount() {
		return ShoppingCategoryList.size();
	}
	
	@Override
	public void onSelectCountry(Country country) {
		countryCodeTv.setText(country.getDialCode());
		CountryCode = String.valueOf(country.getDialCode());
	}
	
	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence charSequence) {
				String charString = charSequence.toString();
				if (charString.isEmpty()) {
					ShoppingCategoryList = arSearch;
				} else {
					List<ShoppingCategoryModel.Response.ProductCategoryDatum> filteredList =
							new ArrayList<>();
					for (ShoppingCategoryModel.Response.ProductCategoryDatum row : arSearch) {
						
						// name match condition. this might differ depending on your requirement
						// here we are looking for name or phone number match
						if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
							filteredList.add(row);
						}
					}
					
					ShoppingCategoryList = filteredList;
				}
				
				FilterResults filterResults = new FilterResults();
				filterResults.values = ShoppingCategoryList;
				return filterResults;
			}
			
			@Override
			protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
				ShoppingCategoryList =
						(ArrayList<ShoppingCategoryModel.Response.ProductCategoryDatum>) filterResults.values;
				
				notifyDataSetChanged();
			}
		};
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		
		ImageView iv_image_categoryItem;
		
		TextView tv_name_categoryItem;
		
		LinearLayout ll_layout_categoryItem;
		
		public ViewHolder(View itemView) {
			super(itemView);
			iv_image_categoryItem = itemView.findViewById(R.id.iv_image_categoryItem);
			tv_name_categoryItem = itemView.findViewById(R.id.tv_name_categoryItem);
			ll_layout_categoryItem = itemView.findViewById(R.id.ll_layout_categoryItem);
			
		}
	}
	
	
}
