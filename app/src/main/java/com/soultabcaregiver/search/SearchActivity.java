package com.soultabcaregiver.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.search.models.UserSearchResultResponse;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchActivity extends BaseActivity {
	
	private static final int TRIGGER_AUTO_COMPLETE = 100;
	
	private static final long AUTO_COMPLETE_DELAY = 300;
	
	private static final String TAG = "SearchActivity";
	
	RecyclerView usersRecyclerView;
	
	AutoCompleteTextView searchTextBox;
	
	ImageView searchButton;
	
	JsonObjectRequest searchRequest;
	
	SearchUsersTextBoxAdapter searchUsersTextBoxAdapter;
	
	SearchUsersAdapter searchUsersAdapter;
	
	private final SearchUsersAdapter.SearchUserClickHandler searchUserClickHandler =
			new SearchUsersAdapter.SearchUserClickHandler() {
				@Override
				public void acceptClickListener(
						UserSearchResultResponse.UserSearchResultModel userModel, int position) {
					acceptInviteAPI(Integer.parseInt(userModel.getId()), position);
				}
				
				@Override
				public void rejectClickListener(
						UserSearchResultResponse.UserSearchResultModel userModel, int position) {
					rejectInviteAPI(Integer.parseInt(userModel.getId()), position);
				}
				
				@Override
				public void removeClickListener(
						UserSearchResultResponse.UserSearchResultModel userModel, int position) {
					removeFriendAPI(Integer.parseInt(userModel.getId()), position);
				}
				
				@Override
				public void inviteMember(UserSearchResultResponse.UserSearchResultModel userModel,
				                         int position) {
					inviteMemberAPI(Integer.parseInt(userModel.getId()), position);
				}
				
				@Override
				public void viewUserDetails(
						UserSearchResultResponse.UserSearchResultModel userModel, int position) {
					redirectToUserProfileActivity(userModel);
				
				}
			};
	
	List<UserSearchResultResponse.UserSearchResultModel> usersList = new ArrayList<>();
	
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
		
		usersRecyclerView = findViewById(R.id.usersRecyclerView);
		searchTextBox = findViewById(R.id.searchTextBox);
		searchButton = findViewById(R.id.searchButton);
		
		setupUI();
	}
	
	public static Intent intentFor(Context context) {
		return new Intent(context, SearchActivity.class);
	}
	
	private void setupUI() {
		
		usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		
		searchUsersAdapter = new SearchUsersAdapter(searchUserClickHandler);
		usersRecyclerView.setAdapter(searchUsersAdapter);
		RecyclerView.ItemDecoration itemDecoration = new SpaceItemDecoration(5);
		usersRecyclerView.addItemDecoration(itemDecoration);
		
		handler = new Handler(msg -> {
			if (msg.what == TRIGGER_AUTO_COMPLETE) {
				if (!TextUtils.isEmpty(searchTextBox.getText())) {
					searchUsers(searchTextBox.getText().toString());
				}
			}
			return false;
		});
		
		searchButton.setOnClickListener(v -> {
		
				Utility.hideKeyboard(SearchActivity.this);
				searchTextBox.dismissDropDown();
				usersRecyclerView.setVisibility(View.VISIBLE);
			if (usersList!=null&&usersList.size()>0) {
				searchUsersAdapter.setUsersList(usersList);
			}
		});
		
		searchTextBox.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				usersRecyclerView.setVisibility(View.GONE);
				handler.removeMessages(TRIGGER_AUTO_COMPLETE);
				handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		searchUsersTextBoxAdapter =
				new SearchUsersTextBoxAdapter(this, this :: redirectToUserProfileActivity);
		searchTextBox.setAdapter(searchUsersTextBoxAdapter);
	}
	
	private void redirectToUserProfileActivity(
			UserSearchResultResponse.UserSearchResultModel model) {
		Intent intent = new Intent(SearchActivity.this, UserProfileActivity.class);
		intent.putExtra("userModel", model);
		startActivity(intent);
	}
	
	public void searchUsers(String searchQuery) {
		
		Log.e(TAG, "SearchQuery == " + searchQuery);
		
		if (searchRequest != null && !searchRequest.isCanceled()) {
			Log.e(TAG, "Search Request Cancelled");
			searchRequest.cancel();
		}
		
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("name", searchQuery);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		searchRequest = new JsonObjectRequest(Request.Method.POST,
				APIS.BASEURL + APIS.searchUsersCaregivers, jsonObject, response -> {
			Log.e("API response", response.toString());
			try {
				String code = response.getString("status_code");
				Log.e("response=", code);
				if (code.equals("200")) {
					Gson gson = new Gson();
					UserSearchResultResponse resultResponse =
							gson.fromJson(response.toString(), UserSearchResultResponse.class);
					usersList = resultResponse.getResponse().getResultData();
					if (usersList != null && !usersList.isEmpty()) {
						searchUsersTextBoxAdapter.setUsersList(usersList);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}, Throwable :: printStackTrace) {
			@Override
			public Map<String, String> getHeaders() {
				Map<String, String> params = new HashMap<>();
				params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
				params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
				params.put(APIS.APITokenKEY,
						Utility.getSharedPreferences(SearchActivity.this, APIS.APITokenValue));
				return params;
			}
			
		};
		// Adding request to request queue
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(searchRequest);
	}
	
	private void inviteMemberAPI(int connectionId, int position) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("connection_id", connectionId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JsonObjectRequest jsonObjectRequest =
				new JsonObjectRequest(Request.Method.POST, APIS.BASEURL + APIS.sendFriendRequest,
						jsonObject, response -> {
					
					Log.e("API response", response.toString());
					try {
						if (response.optInt("status_code") == 200) {
							searchUsersAdapter.updateItemStatus(position,
									SearchUsersAdapter.UsersConnectedStatus.Pending);
						}
						Utility.ShowToast(SearchActivity.this, response.optString("message"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}, Throwable :: printStackTrace) {
					@Override
					public Map<String, String> getHeaders() {
						Map<String, String> params = new HashMap<>();
						params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
						params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
						params.put(APIS.APITokenKEY,
								Utility.getSharedPreferences(SearchActivity.this,
										APIS.APITokenValue));
						return params;
					}
					
				};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjectRequest);
	}
	
	private void acceptInviteAPI(int connectionId, int position) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("connection_id", connectionId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JsonObjectRequest jsonObjectRequest =
				new JsonObjectRequest(Request.Method.POST, APIS.BASEURL + APIS.acceptFriendRequest,
						jsonObject, response -> {
					Log.e("API response", response.toString());
					try {
						if (response.optInt("status_code") == 200) {
							searchUsersAdapter.updateItemStatus(position,
									SearchUsersAdapter.UsersConnectedStatus.Connected);
						}
						Utility.ShowToast(SearchActivity.this, response.optString("message"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}, Throwable :: printStackTrace) {
					@Override
					public Map<String, String> getHeaders() {
						Map<String, String> params = new HashMap<>();
						params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
						params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
						params.put(APIS.APITokenKEY,
								Utility.getSharedPreferences(SearchActivity.this,
										APIS.APITokenValue));
						return params;
					}
					
				};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjectRequest);
	}
	
	private void rejectInviteAPI(int connectionId, int position) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("connection_id", connectionId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JsonObjectRequest jsonObjectRequest =
				new JsonObjectRequest(Request.Method.POST, APIS.BASEURL + APIS.rejectFriendRequest,
						jsonObject, response -> {
					Log.e("API response", response.toString());
					try {
						if (response.optInt("status_code") == 200) {
							searchUsersAdapter.updateItemStatus(position,
									SearchUsersAdapter.UsersConnectedStatus.NotConnected);
						}
						Utility.ShowToast(SearchActivity.this, response.optString("message"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}, Throwable :: printStackTrace) {
					@Override
					public Map<String, String> getHeaders() {
						Map<String, String> params = new HashMap<>();
						params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
						params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
						params.put(APIS.APITokenKEY,
								Utility.getSharedPreferences(SearchActivity.this,
										APIS.APITokenValue));
						return params;
					}
					
				};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjectRequest);
	}
	
	private void removeFriendAPI(int connectionId, int position) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("connection_id", connectionId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JsonObjectRequest jsonObjectRequest =
				new JsonObjectRequest(Request.Method.POST, APIS.BASEURL + APIS.removeFriendRequest,
						jsonObject, response -> {
					Log.e("API response", response.toString());
					try {
						if (response.optInt("status_code") == 200) {
							searchUsersAdapter.updateItemStatus(position,
									SearchUsersAdapter.UsersConnectedStatus.NotConnected);
						}
						Utility.ShowToast(SearchActivity.this, response.optString("message"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}, Throwable :: printStackTrace) {
					@Override
					public Map<String, String> getHeaders() {
						Map<String, String> params = new HashMap<>();
						params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
						params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
						params.put(APIS.APITokenKEY,
								Utility.getSharedPreferences(SearchActivity.this,
										APIS.APITokenValue));
						return params;
					}
					
				};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjectRequest);
	}
}