package com.soultabcaregiver.search;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.search.models.UserSearchResultResponse;
import com.soultabcaregiver.sendbird_calls.SendbirdCallService;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.widget.LinearLayoutCompat;

public class UserProfileActivity extends BaseActivity {
	
	UserSearchResultResponse.UserSearchResultModel userModel;
	
	ImageView profilePic;
	
	TextView userNameText, acceptBtn, rejectBtn;
	
	LinearLayoutCompat connectionLayout, interactLayout;
	
	ImageView messageBtn, callBtn, videoCallBtn;
	
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
		
		userModel = getIntent().getParcelableExtra("userModel");
		
		callBtn.setOnClickListener(v -> voiceCall(userModel.getId(), userModel.getName()));
		videoCallBtn.setOnClickListener(v -> videoCall(userModel.getId(), userModel.getName()));
		
		if (userModel != null) {
			setupData();
		}
		
	}
	
	private void setupData() {
		userNameText.setText(userModel.getName());
		
		RequestOptions options =
				new RequestOptions().centerCrop().dontAnimate().fitCenter().placeholder(
						R.drawable.user_img).error(R.drawable.user_img);
		
		Glide.with(this).load(APIS.CaregiverImageURL + userModel.getProfileImage()).apply(
				options).into(profilePic);
		
		interactLayout.setVisibility(View.GONE);
		rejectBtn.setVisibility(View.GONE);
		String connectedStatus = userModel.getConnected();
		
		if (connectedStatus == null || connectedStatus.isEmpty() || connectedStatus.toLowerCase().equals(
				SearchUsersAdapter.UsersConnectedStatus.Decline.toString())) {
			acceptBtn.setText(getString(R.string.invite_member));
			rejectBtn.setVisibility(View.GONE);
		} else if (connectedStatus.toLowerCase().equals(
				SearchUsersAdapter.UsersConnectedStatus.Connected.toString())) {
			acceptBtn.setText(getString(R.string.remove));
			interactLayout.setVisibility(View.VISIBLE);
		} else if (connectedStatus.toLowerCase().equals(
				SearchUsersAdapter.UsersConnectedStatus.Pending.toString())) {
			acceptBtn.setText(getString(R.string.connection_requested));
		} else if (connectedStatus.toLowerCase().equals(
				SearchUsersAdapter.UsersConnectedStatus.Requested.toString())) {
			rejectBtn.setVisibility(View.VISIBLE);
		}
		
		acceptBtn.setOnClickListener(v -> {
			if (connectedStatus == null || connectedStatus.isEmpty() || connectedStatus.toLowerCase().equals(
					SearchUsersAdapter.UsersConnectedStatus.Decline.toString())) {
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
	
	public void getChannelAnd() {
	
	}
	
	public void voiceCall(String calleeID, String name) {
		if (Utility.isNetworkConnected(this)) {
			SendbirdCallService.dial(this, calleeID, name, false, false, null);
		} else {
			Utility.ShowToast(this, getResources().getString(R.string.net_connection));
		}
	}
	
	public void videoCall(String calleeID, String name) {
		if (Utility.isNetworkConnected(this)) {
			SendbirdCallService.dial(this, calleeID, name, true, false, null);
		} else {
			Utility.ShowToast(this, getResources().getString(R.string.net_connection));
		}
	}
	
}
