package com.soultabcaregiver.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.search.models.UserSearchResultResponse;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PendingRequestsFragment extends Fragment {
	
	RelativeLayout progressLayout;
	
	RecyclerView recycler;
	
	SearchUsersAdapter searchUsersAdapter;
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pending_requests, container, false);
		setupUI(view);
		return view;
	}
	
	@Override
	public void onViewCreated(@NonNull @NotNull View view,
	                          @Nullable @org.jetbrains.annotations.Nullable
			                          Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		fetchPendingRequests();
	}
	
	public static PendingRequestsFragment newInstance() {
		return new PendingRequestsFragment();
	}
	
	private void fetchPendingRequests() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("user_id",
					Utility.getSharedPreferences(getContext(), APIS.caregiver_id));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JsonObjectRequest jsonObjectRequest =
				new JsonObjectRequest(Request.Method.POST,
						APIS.BASEURL + APIS.fetchPendingRequests,
						jsonObject, response -> {
					Log.e("API response", response.toString());
					progressLayout.setVisibility(View.GONE);
					recycler.setVisibility(View.VISIBLE);
					try {
						String code = response.getString("status_code");
						Log.e("response=", code);
						if (code.equals("200")) {
							UserSearchResultResponse resultResponse =
									new Gson().fromJson(response.toString(),
											UserSearchResultResponse.class);
							List<UserSearchResultResponse.UserSearchResultModel> modelList =
									resultResponse.getResponse().getResultData();
							List<UserSearchResultResponse.UserSearchResultModel> newUsersList =
									new ArrayList<>();
							if (!modelList.isEmpty()) {
								for (UserSearchResultResponse.UserSearchResultModel userSearchResultModel : modelList) {
									userSearchResultModel.setConnected(
											SearchUsersAdapter.UsersConnectedStatus.Requested.toString());
									newUsersList.add(userSearchResultModel);
								}
								searchUsersAdapter.setUsersList(newUsersList);
							}
						} else {
						
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
								Utility.getSharedPreferences(getContext(), APIS.APITokenValue));
						return params;
					}
					
				};
		// Adding request to request queue
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjectRequest);
	}
	
	private void setupUI(View view) {
		progressLayout = view.findViewById(R.id.progressLayout);
		recycler = view.findViewById(R.id.pendingRequestsRecycler);
		recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		searchUsersAdapter =
				new SearchUsersAdapter(new SearchUsersAdapter.SearchUserClickHandler() {
					@Override
					public void acceptClickListener(
							UserSearchResultResponse.UserSearchResultModel userModel,
							int position) {
						acceptInviteAPI(Integer.parseInt(userModel.getId()), position);
					}
					
					@Override
					public void rejectClickListener(
							UserSearchResultResponse.UserSearchResultModel userModel,
							int position) {
						rejectInviteAPI(Integer.parseInt(userModel.getId()), position);
					}
					
					@Override
					public void removeClickListener(
							UserSearchResultResponse.UserSearchResultModel userModel,
							int position) {
						
					}
					
					@Override
					public void inviteMember(
							UserSearchResultResponse.UserSearchResultModel userModel,
							int position) {
						
					}
					
					@Override
					public void viewUserDetails(
							UserSearchResultResponse.UserSearchResultModel userModel,
							int position) {
						
					}
				});
		recycler.setAdapter(searchUsersAdapter);
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
						Utility.ShowToast(getContext(), response.optString("message"));
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
								Utility.getSharedPreferences(getContext(), APIS.APITokenValue));
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
						Utility.ShowToast(getContext(), response.optString("message"));
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
								Utility.getSharedPreferences(getContext(), APIS.APITokenValue));
						return params;
					}
					
				};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjectRequest);
	}
}
