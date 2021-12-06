package com.soultabcaregiver.activity.todotask.fragemets;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.BuildConfig;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.WebService.ApiTokenAuthentication;
import com.soultabcaregiver.activity.alert.adapter.CareGiverListAdapter;
import com.soultabcaregiver.activity.alert.model.CareGiverListModel;
import com.soultabcaregiver.activity.daily_routine.fragment.DailyRoutineOptionFragment;
import com.soultabcaregiver.activity.todotask.adapter.CaregiversNamesAdapter;
import com.soultabcaregiver.activity.todotask.adapter.TodoFilterListAdapter;
import com.soultabcaregiver.activity.todotask.adapter.TodoTaskListAdapter;
import com.soultabcaregiver.activity.todotask.model.TaskCaregiversModel;
import com.soultabcaregiver.activity.todotask.model.TaskCountModel;
import com.soultabcaregiver.activity.todotask.model.TaskListModel;
import com.soultabcaregiver.sendbird_chat.ConversationFragment;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.CustomProgressDialog;
import com.soultabcaregiver.utils.HorizontalSpacingItemDecorationUtils;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class TodoTaskListFragment extends BaseFragment {
	
	private RecyclerView rcvTodoFilter, rcvTasks;
	
	private TextView noTask, tvTaskByName;
	
	private ImageView ivBack, ivNotifications;
	
	private AppCompatCheckBox cbTaskByName;
	
	private CardView cvCreateTask;
	
	private ArrayList<TaskCountModel.ToDoFilterModel> todoTagsFilterList = new ArrayList<>();
	
	private ArrayList<TaskListModel.TaskData> taskList = new ArrayList<>();
	
	private ArrayList<TaskCaregiversModel> tempCaregiverName = new ArrayList<>();
	
	private ArrayList<String> selectedCaregivers = new ArrayList<>();
	
	private TodoFilterListAdapter filterListAdapter;
	
	private TodoTaskListAdapter todoTaskListAdapter;
	
	private String caregiversId = null, filterStatus = "All";
	
	private CustomProgressDialog progressDialog;
	
	private int todoCount, progressCount, doneCount, totalCount;
	
	private TodoFilterListAdapter.OnFilterItemClickListener onFilterItemClickListener =
			new TodoFilterListAdapter.OnFilterItemClickListener() {
				@Override
				public void onFilterItemClick(int position,
				                              TaskCountModel.ToDoFilterModel filterModel) {
					filterStatus = filterModel.getTagName();
					getTaskLists(true, filterStatus, caregiversId);
				}
			};
	
	private TodoTaskListAdapter.OnTodoTaskClickListeners todoTaskClickListeners =
			new TodoTaskListAdapter.OnTodoTaskClickListeners() {
				@Override
				public void onTodoTaskClick(int position, TaskListModel.TaskData data) {
					Utility.addFragment(requireActivity(), new TodoTaskDetailFragment(data, position),
							true, TodoTaskDetailFragment.class.getSimpleName());
				}
			};
	
	
	
	private BroadcastReceiver filterList = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int selectedPosition = intent.getIntExtra("position", -1);
			int comment_count = intent.getIntExtra("comment_count", -1);
			if (selectedPosition > -1) {
				todoTaskListAdapter.updateCommentCount(selectedPosition, comment_count);
			}
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_todo_task_list, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		listener();
		requireActivity().registerReceiver(filterList, new IntentFilter("task_list_filter"));
	}
	
	private void init(View view) {
		noTask = view.findViewById(R.id.noTask);
		ivBack = view.findViewById(R.id.ivBack);
		ivNotifications = view.findViewById(R.id.ivNotifications);
		cvCreateTask = view.findViewById(R.id.cvCreateTask);
		rcvTodoFilter = view.findViewById(R.id.rcvTodoFilter);
		rcvTasks = view.findViewById(R.id.rcvTasks);
		cbTaskByName = view.findViewById(R.id.cbTaskByName);
		tvTaskByName = view.findViewById(R.id.tvTaskByName);
		initFiltersData();
		rcvTodoFilter.setLayoutManager(
				new LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false));
		rcvTodoFilter.addItemDecoration(new HorizontalSpacingItemDecorationUtils(1, 8, true));
		filterListAdapter =
				new TodoFilterListAdapter(todoTagsFilterList, onFilterItemClickListener);
		rcvTodoFilter.setAdapter(filterListAdapter);
		rcvTasks.setLayoutManager(new LinearLayoutManager(requireActivity()));
		todoTaskListAdapter =
				new TodoTaskListAdapter(requireActivity(), taskList, todoTaskClickListeners);
		rcvTasks.setAdapter(todoTaskListAdapter);
		getTaskCounts();
		getCaregiverList();
	}
	
	private void listener() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				requireActivity().onBackPressed();
			}
		});
		ivNotifications.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utility.addFragment(requireActivity(), new NotificationsFragment(), true,
						NotificationsFragment.class.getSimpleName());
			}
		});
		cvCreateTask.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utility.addFragment(requireActivity(), new CreateNewToDoTaskFragment(), true,
						CreateNewToDoTaskFragment.class.getSimpleName());
			}
		});
		tvTaskByName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showCaregiverNamesDialog();
			}
		});
	}
	
	private void getCaregiverList() {
		JSONObject mainObject = new JSONObject();
		try {
			mainObject.put("caregiverr_id",
					Utility.getSharedPreferences(requireActivity(), APIS.caregiver_id));
			Log.e("TAG", "CaregiverList API========>" + mainObject.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		showProgressDialog(requireActivity(),requireActivity().getResources().getString(R.string.Loading));
		JsonObjectRequest jsonObjReq =
				new JsonObjectRequest(Request.Method.POST, APIS.BASEURL + APIS.CaregiverListAPI,
						mainObject, response -> {
					Log.e("TAG", "Caregiverlist response=" + response.toString());
					hideProgressDialog();
					CareGiverListModel careGiverProfileModel =
							new Gson().fromJson(response.toString(), CareGiverListModel.class);
					tempCaregiverName = new ArrayList<>();
					if (careGiverProfileModel.getStatusCode() == 200) {
						if (careGiverProfileModel.getResponse() != null && careGiverProfileModel.getResponse().size() > 0) {
							ArrayList<CareGiverListModel.Response> caregivers =
									(ArrayList<CareGiverListModel.Response>) careGiverProfileModel.getResponse();
							for (int i = 0; i < caregivers.size(); i++) {
								CareGiverListModel.Response model = caregivers.get(i);
								TaskCaregiversModel caregiver = new TaskCaregiversModel();
								caregiver.setId(model.getId());
								caregiver.setLastname(model.getLastname());
								caregiver.setName(model.getName());
								caregiver.setProfileImage(model.getProfileImage());
								tempCaregiverName.add(caregiver);
							}
						}
					}
					// TODO: 11/16/2021 add default caregiver for filter task by caregiver name..
					TaskCaregiversModel model = new TaskCaregiversModel();
					model.setId(Utility.getSharedPreferences(requireActivity(), APIS.caregiver_id));
					model.setLastname("");
					model.setName("Me");
					model.setProfileImage(
							Utility.getSharedPreferences(requireActivity(), APIS.profile_image));
					tempCaregiverName.add(0, model);
				}, error -> {
					VolleyLog.d("TAG", "Error: " + error.getMessage());
					hideProgressDialog();
					if (error.networkResponse != null) {
						if (String.valueOf(error.networkResponse.statusCode).equals(
								APIS.APITokenErrorCode) || String.valueOf(
								error.networkResponse.statusCode).equals(APIS.APITokenErrorCode2)) {
							ApiTokenAuthentication.refrehToken(requireActivity(), updatedToken -> {
								if (updatedToken == null) {
								} else {
									getCaregiverList();
								}
							});
						} else {
							Utility.ShowToast(requireActivity(),
									getResources().getString(R.string.something_went_wrong));
						}
					}
				}) {
					@Override
					public Map<String, String> getHeaders() {
						Map<String, String> params = new HashMap<>();
						params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
						params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
						params.put(APIS.HEADERKEY2,
								Utility.getSharedPreferences(requireActivity(), APIS.EncodeUser_id));
						params.put(APIS.APITokenKEY,
								Utility.getSharedPreferences(requireActivity(), APIS.APITokenValue));
						return params;
					}
				};
		AppController.getInstance().addToRequestQueue(jsonObjReq);
		jsonObjReq.setShouldCache(false);
		jsonObjReq.setRetryPolicy(
				new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}
	
	@Override
	public void onResume() {
		super.onResume();
		cbTaskByName.setChecked(false);
		getTaskCounts();
	}
	
	private void getTaskCounts() {
		showProgressDialog(requireActivity(),requireActivity().getResources().getString(R.string.Loading));
		StringRequest stringRequest =
				new StringRequest(Request.Method.POST, APIS.BASEURL + APIS.TODO_TASK_STATUS_COUNTS,
						new Response.Listener<String>() {
							@Override
							public void onResponse(String response) {
								Log.e(TodoTaskListFragment.class.getSimpleName(),
										"onResponse: " + "taskCount" + response);
								TaskCountModel taskCountModel =
										new Gson().fromJson(response, TaskCountModel.class);
								if (taskCountModel.getStatusCode() == 200) {
									todoTagsFilterList = new ArrayList<>();
									todoCount = taskCountModel.getResponse().getTodoCount();
									progressCount =
											taskCountModel.getResponse().getInprogressCount();
									doneCount = taskCountModel.getResponse().getDoneCount();
									totalCount = todoCount + progressCount + doneCount;
									getTaskLists(false, filterStatus, caregiversId);
								} else {
									hideProgressDialog();
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
								ApiTokenAuthentication.refrehToken(requireActivity(), updatedToken -> {
									if (updatedToken == null) {
									} else {
										Log.e("UpdatedToken2", updatedToken);
										getTaskCounts();
									}
								});
							}
						} else {
							Utility.ShowToast(requireActivity(),
									getResources().getString(R.string.something_went_wrong));
						}
					}
				}) {
					@Override
					public Map<String, String> getHeaders() throws AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();
						params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
						if (BuildConfig.DEBUG) {
							Log.e("TAG", "API KEy: " + Utility.getSharedPreferences(requireActivity(),
									APIS.APITokenValue));
						}
						params.put(APIS.APITokenKEY,
								Utility.getSharedPreferences(requireActivity(), APIS.APITokenValue));
						return params;
					}
				};
		AppController.getInstance().addToRequestQueue(stringRequest);
	}
	
	private void getTaskLists(boolean isShowProgress, String statusNameForFilter,
	                          String caregiverIds) {
		if (isShowProgress) {
			showProgressDialog(requireActivity(),
					requireActivity().getResources().getString(R.string.Loading));
		}
		StringRequest stringRequest =
				new StringRequest(Request.Method.POST, APIS.BASEURL + APIS.TODO_TASK_LIST,
						new Response.Listener<String>() {
							@Override
							public void onResponse(String response) {
								Log.e("TAG", "onResponse: " + response);
								hideProgressDialog();
								TaskListModel taskData =
										new Gson().fromJson(response, TaskListModel.class);
								if (taskData.getStatusCode() == 200) {
									taskList = new ArrayList<>();
									if (taskData.getResponse() != null) {
										taskList =
												(ArrayList<TaskListModel.TaskData>) taskData.getResponse();
										todoTaskListAdapter.updateData(taskList);
										if (statusNameForFilter != null) {
											setUpFilterCountData(statusNameForFilter,
													taskList.size());
										}
										if (taskList.size() > 0) {
											noTask.setVisibility(View.GONE);
											rcvTasks.setVisibility(View.VISIBLE);
										} else {
											noTask.setVisibility(View.VISIBLE);
											rcvTasks.setVisibility(View.GONE);
										}
									} else {
										if (statusNameForFilter != null) {
											setUpFilterCountData(statusNameForFilter, 0);
										}
										noTask.setVisibility(View.VISIBLE);
										rcvTasks.setVisibility(View.GONE);
									}
								} else {
									hideProgressDialog();
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
								ApiTokenAuthentication.refrehToken(requireActivity(), updatedToken -> {
									if (updatedToken == null) {
									} else {
										Log.e("UpdatedToken2", updatedToken);
										getTaskLists(isShowProgress, statusNameForFilter,
												caregiverIds);
									}
								});
							}
						} else {
							Utility.ShowToast(requireActivity(),
									getResources().getString(R.string.something_went_wrong));
						}
					}
				}) {
					@Override
					public Map<String, String> getHeaders() throws AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();
						params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
						params.put(APIS.APITokenKEY,
								Utility.getSharedPreferences(requireActivity(), APIS.APITokenValue));
						return params;
					}
					
					@Nullable
					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();
						if (!statusNameForFilter.equalsIgnoreCase("All")) {
							params.put("task_status", statusNameForFilter);
						}
						if (caregiverIds != null) {
							params.put("caregiver_ids", caregiverIds);
						}
						Log.e("TAG", "getParams: " + params);
						return params;
					}
				};
		AppController.getInstance().addToRequestQueue(stringRequest);
	}
	
	private void setUpFilterCountData(final String statusNameForFilter, int size) {
		ArrayList<TaskCountModel.ToDoFilterModel> toDoFilterModels = new ArrayList<>();
		switch (statusNameForFilter) {
			case "All":
				toDoFilterModels.add(new TaskCountModel.ToDoFilterModel("All", size));
				toDoFilterModels.add(new TaskCountModel.ToDoFilterModel("To do", todoCount));
				toDoFilterModels.add(
						new TaskCountModel.ToDoFilterModel("Inprogress", progressCount));
				toDoFilterModels.add(new TaskCountModel.ToDoFilterModel("Done", doneCount));
				break;
			case "To do":
				toDoFilterModels.add(new TaskCountModel.ToDoFilterModel("All", totalCount));
				toDoFilterModels.add(new TaskCountModel.ToDoFilterModel("To do", size));
				toDoFilterModels.add(
						new TaskCountModel.ToDoFilterModel("Inprogress", progressCount));
				toDoFilterModels.add(new TaskCountModel.ToDoFilterModel("Done", doneCount));
				break;
			case "Inprogress":
				toDoFilterModels.add(new TaskCountModel.ToDoFilterModel("All", totalCount));
				toDoFilterModels.add(new TaskCountModel.ToDoFilterModel("To do", todoCount));
				toDoFilterModels.add(new TaskCountModel.ToDoFilterModel("Inprogress", size));
				toDoFilterModels.add(new TaskCountModel.ToDoFilterModel("Done", doneCount));
				break;
			case "Done":
				toDoFilterModels.add(new TaskCountModel.ToDoFilterModel("All", totalCount));
				toDoFilterModels.add(new TaskCountModel.ToDoFilterModel("To do", todoCount));
				toDoFilterModels.add(
						new TaskCountModel.ToDoFilterModel("Inprogress", progressCount));
				toDoFilterModels.add(new TaskCountModel.ToDoFilterModel("Done", size));
				break;
		}
		filterListAdapter.updateData(toDoFilterModels);
	}
	
	private void showCaregiverNamesDialog() {
		Dialog dialog = new Dialog(requireActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_task_by_names);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		Objects.requireNonNull(dialog.getWindow()).setLayout(displayMetrics.widthPixels - 200,
				Toolbar.LayoutParams.WRAP_CONTENT);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		TextView tvCancel = dialog.findViewById(R.id.tvCancel);
		TextView tvHeader = dialog.findViewById(R.id.tvHeader);
		TextView tvSubmit = dialog.findViewById(R.id.tvSubmit);
		RecyclerView rcvNames = dialog.findViewById(R.id.rcvNames);
		tvHeader.setText("View Tasks By Names");
		rcvNames.setLayoutManager(new LinearLayoutManager(requireActivity()));
		CaregiversNamesAdapter caregiversNamesAdapter =
				new CaregiversNamesAdapter(requireActivity(), tempCaregiverName, selectedCaregivers);
		rcvNames.setAdapter(caregiversNamesAdapter);
		tvCancel.setOnClickListener(v -> dialog.dismiss());
		tvSubmit.setOnClickListener(v -> {
			dialog.dismiss();
			selectedCaregivers = caregiversNamesAdapter.getSelectedCaregivers();
			caregiversId =
					selectedCaregivers.toString().replace("[", "").replace("]", "").replace(" ",
							"");
			if (caregiversId.length() > 0) {
				cbTaskByName.setChecked(true);
			} else {
				cbTaskByName.setChecked(false);
			}
			getTaskLists(true, filterStatus, caregiversId);
		});
		dialog.show();
	}
	
	private void initFiltersData() {
		todoTagsFilterList = new ArrayList<>();
		todoTagsFilterList.add(new TaskCountModel.ToDoFilterModel("All", 0));
		todoTagsFilterList.add(new TaskCountModel.ToDoFilterModel("To do", 0));
		todoTagsFilterList.add(new TaskCountModel.ToDoFilterModel("Inprogress", 0));
		todoTagsFilterList.add(new TaskCountModel.ToDoFilterModel("Done", 0));
	}
	
	
	
}