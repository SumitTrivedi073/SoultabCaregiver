package com.soultabcaregiver.activity.todotask.fragemets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.WebService.ApiTokenAuthentication;
import com.soultabcaregiver.activity.todotask.adapter.TodoTaskNotificationsAdapter;
import com.soultabcaregiver.activity.todotask.model.TodoNotificationModel;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.CustomProgressDialog;
import com.soultabcaregiver.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NotificationsFragment extends Fragment {
	
	private ImageView ivBack;
	
	private TextView tvNoNotification;
	
	private RecyclerView rcvNotifications;
	
	private TodoTaskNotificationsAdapter adapter;
	
	private ArrayList<TodoNotificationModel.TaskNotification> taskNotificationList =
			new ArrayList<>();
	
	private CustomProgressDialog progressDialog;
	
	public NotificationsFragment() {
		// Required empty public constructor
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_notifications, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		listeners();
	}
	
	private void init(View view) {
		ivBack = view.findViewById(R.id.ivBack);
		tvNoNotification = view.findViewById(R.id.tvNoNotification);
		rcvNotifications = view.findViewById(R.id.rcvNotifications);
		rcvNotifications.setLayoutManager(new LinearLayoutManager(getActivity()));
		rcvNotifications.addItemDecoration(
				new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
		adapter = new TodoTaskNotificationsAdapter(taskNotificationList);
		rcvNotifications.setAdapter(adapter);
		getNotifications();
	}
	
	private void listeners() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				requireActivity().onBackPressed();
			}
		});
	}
	
	private void getNotifications() {
		showProgressDialog(getResources().getString(R.string.Loading));
		StringRequest stringRequest =
				new StringRequest(Request.Method.POST, APIS.BASEURL + APIS.TODO_TASK_NOTIFICATION,
						new Response.Listener<String>() {
							@Override
							public void onResponse(String response) {
								TodoNotificationModel taskCountModel =
										new Gson().fromJson(response, TodoNotificationModel.class);
								Log.e("TAG", "onResponse: " + response);
								if (taskCountModel.getStatusCode() == 200) {
									if (taskCountModel.getResponse() != null) {
										taskNotificationList =
												(ArrayList<TodoNotificationModel.TaskNotification>) taskCountModel.getResponse().
														getTaskNotificationList();
										if (taskNotificationList.size() > 0) {
											adapter.updateNotificationData(taskNotificationList);
											rcvNotifications.setVisibility(View.VISIBLE);
											tvNoNotification.setVisibility(View.GONE);
										} else {
											tvNoNotification.setVisibility(View.VISIBLE);
											rcvNotifications.setVisibility(View.GONE);
										}
										hideProgressDialog();
									} else {
										hideProgressDialog();
										Utility.ShowToast(getActivity(),
												taskCountModel.getMessage());
									}
								} else {
									hideProgressDialog();
									Utility.ShowToast(getActivity(), taskCountModel.getMessage());
								}
							}
						}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						hideProgressDialog();
						Log.e("error", error.toString());
						if (error.networkResponse != null) {
							if (String.valueOf(error.networkResponse.statusCode).equals(
									APIS.APITokenErrorCode) || String.valueOf(
									error.networkResponse.statusCode).equals(
									APIS.APITokenErrorCode2)) {
								ApiTokenAuthentication.refrehToken(getActivity(), updatedToken -> {
									if (updatedToken == null) {
									} else {
										Log.e("UpdatedToken2", updatedToken);
										getNotifications();
									}
								});
							}
						} else {
							Utility.ShowToast(getActivity(),
									getResources().getString(R.string.something_went_wrong));
						}
					}
				}) {
					@Override
					public Map<String, String> getHeaders() throws AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();
						params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
						params.put(APIS.APITokenKEY,
								Utility.getSharedPreferences(getActivity(), APIS.APITokenValue));
						return params;
					}
				};
		AppController.getInstance().addToRequestQueue(stringRequest);
		
	}
	
	public void showProgressDialog(String message) {
		if (progressDialog == null)
			progressDialog = new CustomProgressDialog(getActivity(), message);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	public void hideProgressDialog() {
		if (progressDialog != null)
			progressDialog.dismiss();
	}
	
}