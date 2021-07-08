package com.soultabcaregiver.sendbird_chat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.alert.model.CareGiverListModel;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CreateGroupFragment extends BaseFragment {
	
	private CreateGroupUsersAdapter adapter;
	
	private ProgressBar progressDialog;
	
	private CreateGroupUsersAdapter.OnItemCheckedChangeListener checkedChangeListener;
	
	private List<String> mSelectedIds = new ArrayList<>();
	
	private RecyclerView recyclerView;
	
	private View dataLayout, noDataLayout;
	
	private TextView backButton, backButtonNoData;
	
	private TextView createGroupButton;
	
	private EditText searchEditText;
	
	private EditText groupNameEditText;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_create_group, container, false);
		checkedChangeListener = (user, checked) -> {
			if (checked) {
				mSelectedIds.add(user.getId());
			} else {
				mSelectedIds.remove(user.getId());
			}
		};
		adapter = new CreateGroupUsersAdapter(checkedChangeListener);
		initUI(view);
		return view;
	}
	
	public void initUI(View view) {
		progressDialog = view.findViewById(R.id.progressBar);
		recyclerView = view.findViewById(R.id.recycler);
		noDataLayout = view.findViewById(R.id.noDataLayout);
		dataLayout = view.findViewById(R.id.dataLayout);
		backButton = view.findViewById(R.id.backButton);
		backButtonNoData = view.findViewById(R.id.backButtonNoData);
		createGroupButton = view.findViewById(R.id.createButton);
		searchEditText = view.findViewById(R.id.searchEditText);
		groupNameEditText = view.findViewById(R.id.groupNameET);
		
		searchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				adapter.updateFilter(s.toString());
			}
		});
		
		backButton.setOnClickListener(v -> getActivity().onBackPressed());
		backButtonNoData.setOnClickListener(v -> getActivity().onBackPressed());
		
		createGroupButton.setOnClickListener(v -> {
			if (groupNameEditText.getText().toString().isEmpty()) {
				Utility.showSnackBar(getView(), getString(R.string.enter_group_name));
				return;
			}
			if (mSelectedIds.isEmpty()) {
				Utility.showSnackBar(getView(), getString(R.string.select_group_name));
				return;
			}
			
			ChatHelper.createGroupChannel(mSelectedIds, true, groupChannel -> {
				groupChannel.updateChannel(groupNameEditText.getText().toString(), "", "",
						(groupChannel1, e) -> {
							Log.e("tag", "channel created");
						});
			});
			
		});
		
	}
	
	@Override
	public void onViewCreated(@NonNull @NotNull View view,
	                          @Nullable @org.jetbrains.annotations.Nullable
			                          Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setupRecyclerView();
		if (Utility.isNetworkConnected(getContext())) {
			getCaregiverList();//for list data
		} else {
			Utility.ShowToast(getContext(), getResources().getString(R.string.net_connection));
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
	}
	
	private void setupRecyclerView() {
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		recyclerView.setAdapter(adapter);
	}
	
	private void getCaregiverList() {
		
		progressDialog.setVisibility(View.VISIBLE);
		
		JSONObject mainObject = new JSONObject();
		try {
			mainObject.put("caregiverr_id",
					Utility.getSharedPreferences(getContext(), APIS.caregiver_id));
		} catch (JSONException e) {
			e.printStackTrace();
			
		}
		
		JsonObjectRequest jsonObjReq =
				new JsonObjectRequest(Request.Method.POST, APIS.BASEURL + APIS.CaregiverListAPI,
						mainObject, response -> {
					progressDialog.setVisibility(View.GONE);
					
					CareGiverListModel careGiverProfileModel =
							new Gson().fromJson(response.toString(), CareGiverListModel.class);
					
					if (String.valueOf(careGiverProfileModel.getStatusCode()).equals("200")) {
						
						if (careGiverProfileModel.getResponse().size() > 0) {
							adapter.setCaregiverList(careGiverProfileModel.getResponse());
						} else {
							//Empty View here
							hideViews();
						}
					} else if (String.valueOf(careGiverProfileModel.getStatusCode()).equals(
							"403")) {
						logout_app(careGiverProfileModel.getMessage());
					} else {
						Utility.ShowToast(getContext(), careGiverProfileModel.getMessage());
					}
					
				}, error -> {
					progressDialog.setVisibility(View.GONE);
				}) {
					@Override
					public Map<String, String> getHeaders() {
						Map<String, String> params = new HashMap<>();
						params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
						params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
						params.put(APIS.HEADERKEY2,
								Utility.getSharedPreferences(getContext(), APIS.EncodeUser_id));
						return params;
					}
					
				};
		AppController.getInstance().addToRequestQueue(jsonObjReq);
		jsonObjReq.setShouldCache(false);
		jsonObjReq.setRetryPolicy(
				new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		
	}
	
	private void hideViews() {
		dataLayout.setVisibility(View.GONE);
		noDataLayout.setVisibility(View.VISIBLE);
	}
	
	public static CreateGroupFragment newInstance() {
		return new CreateGroupFragment();
	}
	
	
}
