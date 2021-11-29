package com.soultabcaregiver.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.WebService.ApiTokenAuthentication;
import com.soultabcaregiver.search.models.UserSearchResultResponse;
import com.soultabcaregiver.sendbird_calls.SendbirdCallService;
import com.soultabcaregiver.sendbird_chat.ChatHelper;
import com.soultabcaregiver.sendbird_chat.ConversationFragment;
import com.soultabcaregiver.sendbird_chat.model.UserDetailModel;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.widget.LinearLayoutCompat;

public class UserProfileActivity extends BaseActivity {
	
	UserSearchResultResponse.UserSearchResultModel userModel;
	
	ImageView profilePic;
	
	TextView userNameText, acceptBtn, rejectBtn;
	
	LinearLayoutCompat connectionLayout, interactLayout;
	
	ImageView messageBtn, callBtn, videoCallBtn;
	
	String connectedStatus, ID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users_profile);
		
		findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
		
		profilePic = findViewById(R.id.profilePic);
		userNameText = findViewById(R.id.userNameText);
		acceptBtn = findViewById(R.id.acceptBtn);
		rejectBtn = findViewById(R.id.rejectBtn);
		connectionLayout = findViewById(R.id.connectionLayout);
		interactLayout = findViewById(R.id.interactLayout);
		
		messageBtn = findViewById(R.id.messageBtn);
		callBtn = findViewById(R.id.call_caregiver);
		videoCallBtn = findViewById(R.id.VideoCall_caregiver);
		
		if (getIntent().getExtras() != null) {
			if (getIntent().getParcelableExtra("userModel")!=null) {
				userModel = getIntent().getParcelableExtra("userModel");
			} else {
				ID = getIntent().getStringExtra("ID");
				GettingUserData();
				Log.e("ID", ID);
			}
		}
		
		callBtn.setOnClickListener(v -> voiceCall(userModel.getId(), userModel.getName(),
				userModel.getIsSendbirdUser()));
		
		videoCallBtn.setOnClickListener(v -> videoCall(userModel.getId(), userModel.getName(),
				userModel.getIsSendbirdUser()));
		
		messageBtn.setOnClickListener(v -> OpenChatScreen(userModel.getId(), userModel.getName(),
				userModel.getIsSendbirdUser()));
		
		if (userModel != null) {
			connectedStatus = userModel.getConnected();
		}
		acceptBtn.setOnClickListener(v -> {
			if (connectedStatus == null || connectedStatus.isEmpty() || connectedStatus.toLowerCase().equals(
					SearchUsersAdapter.UsersConnectedStatus.Decline.toString())
					||connectedStatus.toLowerCase().equals(
					SearchUsersAdapter.UsersConnectedStatus.NotConnected.toString())) {
				inviteMemberAPI(Integer.parseInt(userModel.getId()));
			} else if (connectedStatus.toLowerCase().equals(
					SearchUsersAdapter.UsersConnectedStatus.Connected.toString())) {
				removeFriendAPI(Integer.parseInt(userModel.getId()));
			} else if (connectedStatus.toLowerCase().equals(
					SearchUsersAdapter.UsersConnectedStatus.Requested.toString())) {
				acceptInviteAPI(Integer.parseInt(userModel.getId()));
			}
		});
		
		rejectBtn.setOnClickListener(v -> {
			if (connectedStatus.toLowerCase().equals(
					SearchUsersAdapter.UsersConnectedStatus.Requested.toString())) {
				rejectInviteAPI(Integer.parseInt(userModel.getId()));
			}
		});
		
		if (userModel != null) {
			setupData();
		}
		
	}
	
	private void GettingUserData() {
		showProgressDialog(getResources().getString(R.string.Loading));
		
		StringRequest stringRequest =
				new StringRequest(Request.Method.POST, APIS.BASEURL + APIS.UserDetailById,
						new Response.Listener<String>() {
							@Override
							public void onResponse(String response) {
								hideProgressDialog();
								Log.e("response", response);
								UserDetailModel userDetailModel =
										new Gson().fromJson(response, UserDetailModel.class);
								if (String.valueOf(userDetailModel.getStatusCode()).equals("200")) {
									
									UserDetailModel.Response model =
											(UserDetailModel.Response) userDetailModel.getResponse().get(
													0);
									UserSearchResultResponse.UserSearchResultModel
											userSearchResultModel = model.copyToUserDetailModel();
									
								/*	for (int i=0; i<userDetailModel.getResponse().size();i++){
										
										UserSearchResultResponse.UserSearchResultModel = n
									}*/
									
									userModel = userSearchResultModel;
									connectedStatus = userModel.getConnected();
									if (userModel != null) {
										setupData();
									}
									
								} else if (String.valueOf(userDetailModel.getStatusCode()).equals(
										"403")) {
									logout_app(userDetailModel.getMessage());
								} else {
									
									Utility.ShowToast(UserProfileActivity.this,
											userDetailModel.getMessage());
								}
								
								
							}
						}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("error", error.toString());
						
						hideProgressDialog();
						if (error.networkResponse != null) {
							if (String.valueOf(error.networkResponse.statusCode).equals(APIS.APITokenErrorCode)||String.valueOf(error.networkResponse.statusCode).equals(
									APIS.APITokenErrorCode2)) {
								ApiTokenAuthentication.refrehToken(UserProfileActivity.this,
										updatedToken -> {
											if (updatedToken == null) {
											} else {
												GettingUserData();
												
											}
										});
							} else {
								Utility.ShowToast(UserProfileActivity.this,
										getResources().getString(R.string.something_went_wrong));
							}
						}
					}
				}) {
					@Override
					public Map<String, String> getHeaders() throws AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();
						params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
						params.put(APIS.HEADERKEY2,
								Utility.getSharedPreferences(UserProfileActivity.this,
										APIS.EncodeUser_id));
						params.put(APIS.APITokenKEY,
								Utility.getSharedPreferences(UserProfileActivity.this,
										APIS.APITokenValue));
						
						Log.e("dahsbord_param", String.valueOf(params));
						return params;
					}
					
					@Override
					protected Map<String, String> getParams() {
						Map<String, String> params = new HashMap<String, String>();
						params.put("id", ID);
						
						return params;
					}
					
				};
		AppController.getInstance().addToRequestQueue(stringRequest);
		stringRequest.setShouldCache(false);
		stringRequest.setRetryPolicy(
				new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		
	}
	
	public void voiceCall(String calleeID, String name, String isSendbirdUser) {
		if (Utility.isNetworkConnected(this)) {
			if (isSendbirdUser.equals("1")) {
				
				if (!TextUtils.isEmpty(calleeID)) {
					SendbirdCallService.dial(this, calleeID, name, false, false, null);
				}
			} else {
				Utility.ShowToast(this, getString(R.string.person_not_registerd));
				
			}
		} else {
			Utility.ShowToast(this, getResources().getString(R.string.net_connection));
		}
		
	}
	
	public void videoCall(String calleeID, String name, String isSendbirdUser) {
		
		if (Utility.isNetworkConnected(this)) {
			if (isSendbirdUser.equals("1")) {
				if (!TextUtils.isEmpty(calleeID)) {
					SendbirdCallService.dial(this, calleeID, name, true, false, null);
				}
			} else {
				Utility.ShowToast(this, getString(R.string.person_not_registerd));
			}
		} else {
			Utility.ShowToast(this, getResources().getString(R.string.net_connection));
		}
		
	}
	
	private void OpenChatScreen(String id, String name, String isSendbirdUser) {
		if (Utility.isNetworkConnected(this)) {
			ArrayList<String> ids = new ArrayList<>();
			ids.add(Utility.getSharedPreferences(this, APIS.caregiver_id));
			ids.add(id);
			ChatHelper.createGroupChannel(ids, true, groupChannel -> {
				Log.e("channel", "" + groupChannel.getUrl());
				if (isSendbirdUser.equals("1")) {
					Utility.loadFragment(this,
							ConversationFragment.newInstance(groupChannel.getUrl(), false), true,
							ConversationFragment.class.getSimpleName());
					
					
				} else {
					Utility.ShowToast(this, getString(R.string.person_not_registerd));
				}
			});
		} else {
			Utility.ShowToast(this, getResources().getString(R.string.net_connection));
		}
	}
	
	private void inviteMemberAPI(int connectionId) {
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
							userModel.setConnected(
									SearchUsersAdapter.UsersConnectedStatus.Pending.toString());
							setupData();
						}
						Utility.ShowToast(UserProfileActivity.this, response.optString("message"));
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
								Utility.getSharedPreferences(UserProfileActivity.this,
										APIS.APITokenValue));
						return params;
					}
					
				};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjectRequest);
	}
	
	private void removeFriendAPI(int connectionId) {
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
							userModel.setConnected(
									SearchUsersAdapter.UsersConnectedStatus.Decline.toString());
							setupData();
						}
						Utility.ShowToast(UserProfileActivity.this, "Unfriend Successfully");
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
								Utility.getSharedPreferences(UserProfileActivity.this,
										APIS.APITokenValue));
						return params;
					}
					
				};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjectRequest);
	}
	
	private void acceptInviteAPI(int connectionId) {
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
							userModel.setConnected(
									SearchUsersAdapter.UsersConnectedStatus.Connected.toString());
							setupData();
						}
						Utility.ShowToast(UserProfileActivity.this, response.optString("message"));
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
								Utility.getSharedPreferences(UserProfileActivity.this,
										APIS.APITokenValue));
						return params;
					}
					
				};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjectRequest);
	}
	
	private void rejectInviteAPI(int connectionId) {
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
							userModel.setConnected(
									SearchUsersAdapter.UsersConnectedStatus.NotConnected.toString());
							setupData();
						}
						Utility.ShowToast(UserProfileActivity.this, response.optString("message"));
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
								Utility.getSharedPreferences(UserProfileActivity.this,
										APIS.APITokenValue));
						return params;
					}
					
				};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjectRequest);
	}
	
	private void setupData() {
		userNameText.setText(userModel.getName());
		
		RequestOptions options =
				new RequestOptions().centerCrop().dontAnimate().fitCenter().placeholder(
						R.drawable.user_img).error(R.drawable.user_img);
		
		Glide.with(this).load(userModel.getProfileImage()).apply(options).into(profilePic);
		
		interactLayout.setVisibility(View.GONE);
		rejectBtn.setVisibility(View.GONE);
		connectedStatus = userModel.getConnected();
		
		/*if (connectedStatus == null || connectedStatus.isEmpty() || connectedStatus.toLowerCase().equals(
				SearchUsersAdapter.UsersConnectedStatus.Decline.toString())
				||connectedStatus.toLowerCase().equals(
				SearchUsersAdapter.UsersConnectedStatus.NotConnected.toString())) {
			acceptBtn.setText(getString(R.string.invite_member));
			rejectBtn.setVisibility(View.GONE);
		} */
		if (connectedStatus == null || connectedStatus.isEmpty()||connectedStatus.toLowerCase().equals(
				SearchUsersAdapter.UsersConnectedStatus.NotConnected.toString())) {
			acceptBtn.setText(getString(R.string.invite_member));
			rejectBtn.setVisibility(View.GONE);
			
		} else if (connectedStatus.toLowerCase().equals(
				SearchUsersAdapter.UsersConnectedStatus.Decline.toString())) {
			acceptBtn.setText(getString(R.string.invite_member));
			acceptBtn.setAlpha(0.5f);
			acceptBtn.setEnabled(false);
			acceptBtn.setClickable(false);
			acceptBtn.setBackground(getResources().getDrawable(R.drawable.blue_disable_btn));
			rejectBtn.setVisibility(View.GONE);
		}else if (connectedStatus.toLowerCase().equals(
				SearchUsersAdapter.UsersConnectedStatus.Connected.toString())) {
			
			if (userModel.getId().equals(Utility.getSharedPreferences(getApplicationContext(),
					APIS.user_id))){
				
				acceptBtn.setVisibility(View.GONE);
				interactLayout.setVisibility(View.VISIBLE);
			}else {
				acceptBtn.setText(getString(R.string.remove));
				interactLayout.setVisibility(View.VISIBLE);
			}
			
		} else if (connectedStatus.toLowerCase().equals(
				SearchUsersAdapter.UsersConnectedStatus.Pending.toString())) {
			acceptBtn.setText(getString(R.string.connection_requested));
		} else if (connectedStatus.toLowerCase().equals(
				SearchUsersAdapter.UsersConnectedStatus.Requested.toString())) {
			rejectBtn.setVisibility(View.VISIBLE);
		}
	}
	
}
