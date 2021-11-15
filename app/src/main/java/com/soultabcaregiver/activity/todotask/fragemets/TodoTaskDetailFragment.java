package com.soultabcaregiver.activity.todotask.fragemets;

import static android.app.Activity.RESULT_CANCELED;
import static com.soultabcaregiver.utils.photoFileUtils.getPath;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.telecom.TelecomManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.WebService.ApiTokenAuthentication;
import com.soultabcaregiver.activity.alert.model.CareGiverListModel;
import com.soultabcaregiver.activity.todotask.adapter.AssignedToCaregiverAdapter;
import com.soultabcaregiver.activity.todotask.adapter.AttachmentsAdapter;
import com.soultabcaregiver.activity.todotask.adapter.CaregiverListForTaskAdapter;
import com.soultabcaregiver.activity.todotask.adapter.TaskActivitiesAdapter;
import com.soultabcaregiver.activity.todotask.adapter.TaskCommentsAdapter;
import com.soultabcaregiver.activity.todotask.model.AddTaskCommentModel;
import com.soultabcaregiver.activity.todotask.model.CreateTaskModel;
import com.soultabcaregiver.activity.todotask.model.DeleteCommentModel;
import com.soultabcaregiver.activity.todotask.model.TaskActivitiesModel;
import com.soultabcaregiver.activity.todotask.model.TaskAttachmentsModel;
import com.soultabcaregiver.activity.todotask.model.TaskCaregiversModel;
import com.soultabcaregiver.activity.todotask.model.TaskCommentListModel;
import com.soultabcaregiver.activity.todotask.model.TaskListModel;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.CustomProgressDialog;
import com.soultabcaregiver.utils.Utility;
import com.soultabcaregiver.utils.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class TodoTaskDetailFragment extends Fragment {
	
	private final String TAG = getClass().getSimpleName();
	
	private static final String DATE_FORMAT_FOR_DISPLAY = "MM/dd/yyyy", DATE_FORMAT_FOR_API =
			"yyyy-MM-dd";
	
	public static final int REQUEST_IMAGE_CAPTURE = 101, PICK_FROM_FILE = 6;
	
	private int editCommentPosition = -1;
	
	private String editCommentId = "";
	
	private Uri imageUri;
	
	private ImageView ivBack;
	
	private TextView tvStartDate, tvEndDate, tvViewComments, tvViewLogs, tvCommentsCount,
			tvAddComments, tvNoCaregiverAssigned, tvNoAttachmentsAdded, tvEditComment,
			tvCancelEditComment, etStatusOfTask;
	
	private EditText etTaskTitle, etTaskDescription, etAddComment, etEditComment;
	
	private RecyclerView rcvAssignedCaregiver, rcvAttachments, rcvComments, rcvActivities;
	
	private CardView cvUpdateTask;
	
	private LinearLayout llStartDate, llEndDate, llComments, llNoComments, llNoActivities,
			llActivities, llAddComment, llEditComment;
	
	private Spinner spinnerTaskStatus;
	
	private TaskListModel.TaskData taskData = null;
	
	private List<String> taskStatus;
	
	private ArrayList<Integer> selectedCaregivers = new ArrayList<>();
	
	private ArrayList<TaskCaregiversModel> tempCaregiverName = new ArrayList<>();
	
	private ArrayList<TaskCommentListModel.Response> taskComments = new ArrayList<>();
	
	private ArrayList<TaskActivitiesModel.Response> taskActivities = new ArrayList<>();
	
	private ArrayList<TaskAttachmentsModel> taskAttachmentsList = new ArrayList<>();
	
	private AssignedToCaregiverAdapter caregiverAdapter;
	
	private AttachmentsAdapter attachmentsAdapter;
	
	private TaskCommentsAdapter taskCommentsAdapter;
	
	private TaskActivitiesAdapter taskActivitiesAdapter;
	
	private CustomProgressDialog progressDialog;
	
	private AssignedToCaregiverAdapter.OnCaregiverItemClickListener onCaregiverItemClickListener =
			new AssignedToCaregiverAdapter.OnCaregiverItemClickListener() {
				@Override
				public void onAddCaregiverClick() {
					showCaregiverNamesDialog();
				}
				
				@Override
				public void onRemoveCareGiverClick(int position, int caregiversCount) {
					selectedCaregivers.remove(position - 1);
					if (caregiversCount == 1) {
						tvNoCaregiverAssigned.setVisibility(View.VISIBLE);
					}
				}
			};
	
	private AttachmentsAdapter.OnAttachmentItemClickListeners attachmentItemClickListeners =
			new AttachmentsAdapter.OnAttachmentItemClickListeners() {
				@Override
				public void onAddAttachmentClick() {
					showAttachmentOptionsDialog();
				}
				
				@Override
				public void removeAttachment(int attachmentsSize) {
					if (attachmentsSize == 1) {
						tvNoAttachmentsAdded.setVisibility(View.VISIBLE);
					}
				}
			};
	
	private TaskCommentsAdapter.OnCommentItemClickListeners onCommentItemClickListeners =
			new TaskCommentsAdapter.OnCommentItemClickListeners() {
				@Override
				public void onEditClick(int position, TaskCommentListModel.Response comment) {
					editCommentPosition = position;
					editCommentId = comment.getId();
					etEditComment.setText(comment.getComment());
					llAddComment.setVisibility(View.GONE);
					llEditComment.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onDeleteClick(int position, TaskCommentListModel.Response comment) {
					deleteTaskComment(position, comment.getTaskId(), comment.getId());
				}
				
				@Override
				public void onSaveCommentClick(int position,
				                               TaskCommentListModel.Response comment) {
				}
			};
	
	public TodoTaskDetailFragment(TaskListModel.TaskData data) {
		this.taskData = data;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_todo_task_detail, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		listeners();
	}
	
	private void init(View view) {
		ivBack = view.findViewById(R.id.ivBack);
		cvUpdateTask = view.findViewById(R.id.cvUpdateTask);
		spinnerTaskStatus = view.findViewById(R.id.spinnerTaskStatus);
		tvStartDate = view.findViewById(R.id.tvStartDate);
		tvEndDate = view.findViewById(R.id.tvEndDate);
		tvViewComments = view.findViewById(R.id.tvViewComments);
		tvViewLogs = view.findViewById(R.id.tvViewLogs);
		tvCommentsCount = view.findViewById(R.id.tvCommentsCount);
		tvAddComments = view.findViewById(R.id.tvAddComments);
		tvNoCaregiverAssigned = view.findViewById(R.id.tvNoCaregiverAssigned);
		tvNoAttachmentsAdded = view.findViewById(R.id.tvNoAttachmentsAdded);
		tvEditComment = view.findViewById(R.id.tvEditComment);
		tvCancelEditComment = view.findViewById(R.id.tvCancelEditComment);
		etStatusOfTask = view.findViewById(R.id.etStatusOfTask);
		etTaskDescription = view.findViewById(R.id.etTaskDescription);
		etTaskTitle = view.findViewById(R.id.etTaskTitle);
		etAddComment = view.findViewById(R.id.etAddComment);
		etEditComment = view.findViewById(R.id.etEditComment);
		rcvAssignedCaregiver = view.findViewById(R.id.rcvAssignedCaregiver);
		rcvAttachments = view.findViewById(R.id.rcvAttachments);
		rcvComments = view.findViewById(R.id.rcvComments);
		rcvActivities = view.findViewById(R.id.rcvActivities);
		llStartDate = view.findViewById(R.id.llStartDate);
		llEndDate = view.findViewById(R.id.llEndDate);
		llComments = view.findViewById(R.id.llComments);
		llNoComments = view.findViewById(R.id.llNoComments);
		llNoActivities = view.findViewById(R.id.llNoActivities);
		llActivities = view.findViewById(R.id.llActivities);
		llAddComment = view.findViewById(R.id.llAddComment);
		llEditComment = view.findViewById(R.id.llEditComment);
		ChipsLayoutManager chipsLayoutManager =
				ChipsLayoutManager.newBuilder(getActivity()).setChildGravity(
						Gravity.TOP).setScrollingEnabled(true).setGravityResolver(
						new IChildGravityResolver() {
							@Override
							public int getItemGravity(int position) {
								return Gravity.CENTER;
							}
						}).setOrientation(ChipsLayoutManager.HORIZONTAL).setRowStrategy(
						ChipsLayoutManager.STRATEGY_DEFAULT).withLastRow(true).build();
		rcvAssignedCaregiver.setLayoutManager(chipsLayoutManager);
		rcvAssignedCaregiver.addItemDecoration(new SpacingItemDecoration(16, 16));
		caregiverAdapter =
				new AssignedToCaregiverAdapter(getActivity(), onCaregiverItemClickListener);
		rcvAssignedCaregiver.setAdapter(caregiverAdapter);
		ChipsLayoutManager attachments =
				ChipsLayoutManager.newBuilder(getActivity()).setChildGravity(
						Gravity.TOP).setScrollingEnabled(true).setGravityResolver(
						new IChildGravityResolver() {
							@Override
							public int getItemGravity(int position) {
								return Gravity.CENTER;
							}
						}).setOrientation(ChipsLayoutManager.HORIZONTAL).setRowStrategy(
						ChipsLayoutManager.STRATEGY_DEFAULT).withLastRow(true).build();
		rcvAttachments.setLayoutManager(attachments);
		rcvAttachments.addItemDecoration(new SpacingItemDecoration(16, 16));
		attachmentsAdapter = new AttachmentsAdapter(getActivity(), attachmentItemClickListeners);
		rcvAttachments.setAdapter(attachmentsAdapter);
		rcvComments.setLayoutManager(new LinearLayoutManager(getActivity()));
		taskCommentsAdapter = new TaskCommentsAdapter(taskComments, onCommentItemClickListeners);
		rcvComments.setAdapter(taskCommentsAdapter);
		rcvActivities.setLayoutManager(new LinearLayoutManager(getActivity()));
		taskActivitiesAdapter = new TaskActivitiesAdapter(taskActivities);
		rcvActivities.setAdapter(taskActivitiesAdapter);
		taskStatus = Arrays.asList(
				getActivity().getResources().getStringArray(R.array.todo_task_status));
		ArrayAdapter<String> adapter =
				new ArrayAdapter<>(getActivity(), R.layout.row_spinner_status, R.id.tvStatus,
						taskStatus);
		spinnerTaskStatus.setAdapter(adapter);
		spinnerTaskStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				etStatusOfTask.setText(taskStatus.get(position));
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		setCaregiverAssignedOrNot();
		setAttachmentAddedOrNot();
		setupTaskData();
		if (Utility.isNetworkConnected(getActivity())) {
			getTaskComments();
			getTaskActivities();
			getAllCaregiversDetails();
		}
	}
	
	private void listeners() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				requireActivity().onBackPressed();
			}
		});
		cvUpdateTask.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				editTask();
			}
		});
		llStartDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				openDatePickerDialog(true);
			}
		});
		llEndDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				openDatePickerDialog(false);
			}
		});
		tvAddComments.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String newComment = etAddComment.getText().toString().trim();
				Log.e(TAG, "onClick: " + newComment);
				if (!newComment.isEmpty()) {
					addNewComment(newComment, taskData.getId());
				}
			}
		});
		tvViewComments.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvViewComments.setVisibility(View.GONE);
				tvViewLogs.setVisibility(View.VISIBLE);
				llComments.setVisibility(View.VISIBLE);
				llActivities.setVisibility(View.GONE);
			}
		});
		tvViewLogs.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvViewComments.setVisibility(View.VISIBLE);
				tvViewLogs.setVisibility(View.GONE);
				llComments.setVisibility(View.GONE);
				llActivities.setVisibility(View.VISIBLE);
			}
		});
		etAddComment.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String comment = s.toString().trim();
				if (comment.length() == 0) {
					tvAddComments.setEnabled(false);
					tvAddComments.setAlpha(0.5f);
				} else {
					tvAddComments.setEnabled(true);
					tvAddComments.setAlpha(1.0f);
				}
			}
		});
		etEditComment.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String comment = s.toString().trim();
				if (comment.length() == 0) {
					tvEditComment.setEnabled(false);
					tvEditComment.setAlpha(0.5f);
				} else {
					tvEditComment.setEnabled(true);
					tvEditComment.setAlpha(1.0f);
				}
			}
		});
		tvEditComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String editedComment = etEditComment.getText().toString().trim();
				if (!editedComment.isEmpty()) {
					editComment(editedComment, editCommentPosition, editCommentId);
				}
			}
		});
		tvCancelEditComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editCommentId = "";
				llAddComment.setVisibility(View.VISIBLE);
				llEditComment.setVisibility(View.GONE);
			}
		});
		etStatusOfTask.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				spinnerTaskStatus.performClick();
			}
		});
	}
	
	private void setupTaskData() {
		etTaskTitle.setText(taskData.getTitle());
		etTaskDescription.setText(taskData.getDescription());
		tvStartDate.setText(getFormattedDate(taskData.getStartDate(), DATE_FORMAT_FOR_API,
				DATE_FORMAT_FOR_DISPLAY));
		tvEndDate.setText(getFormattedDate(taskData.getEndDate(), DATE_FORMAT_FOR_API,
				DATE_FORMAT_FOR_DISPLAY));
		//		etStatusOfTask.setText(taskData.getTaskStatus());
		spinnerTaskStatus.setSelection(taskStatus.indexOf(taskData.getTaskStatus()));
		setupSelectedAttachments();
		Log.e(TAG, "setupTaskData: " + taskData.getAssignTo());
	}
	
	private void editTask() {
		if (etTaskTitle.getText().toString().trim().isEmpty()) {
			Utility.ShowToast(getActivity(),
					getResources().getString(R.string.todo_task_title_alert));
		} else if (etTaskDescription.getText().toString().trim().isEmpty()) {
			Utility.ShowToast(getActivity(),
					getResources().getString(R.string.todo_task_description_alert));
		} else if (tvStartDate.getText().toString().trim().isEmpty()) {
			Utility.ShowToast(getActivity(),
					getResources().getString(R.string.todo_task_start_date));
		} else if (tvEndDate.getText().toString().trim().isEmpty()) {
			Utility.ShowToast(getActivity(),
					getResources().getString(R.string.todo_task_end_date));
		} else if (caregiverAdapter.getCaregiversCount() == 1) {
			Utility.ShowToast(getActivity(),
					getResources().getString(R.string.todo_task_add_caregiver));
		} else {
			showProgressDialog(getResources().getString(R.string.Loading));
			final VolleyMultipartRequest multipartRequest =
					new VolleyMultipartRequest(Request.Method.POST,
							APIS.BASEURL + APIS.CREATE_TODO_TASK_LIST,
							new Response.Listener<NetworkResponse>()////Place user for
							{
								@Override
								public void onResponse(NetworkResponse response) {
									String resultResponse = new String(response.data);
									Log.e(TAG, "add test report onResponse: >>" + resultResponse);
									hideProgressDialog();
									CreateTaskModel taskModel = new Gson().fromJson(resultResponse,
											CreateTaskModel.class);
									if (taskModel.getStatusCode() == 200) {
										Intent intent =
												new Intent(APIS.INTENT_FILTER_REFRESH_TASK_LIST);
										getActivity().sendBroadcast(intent);
										//										finish();
									} else {
										Utility.ShowToast(getActivity(), taskModel.getMessage());
									}
								}
							}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							VolleyLog.d(TAG, "Error: " + error.getMessage());
							hideProgressDialog();
							if (error.networkResponse != null) {
								if (String.valueOf(error.networkResponse.statusCode).equals(
										APIS.APITokenErrorCode) || String.valueOf(
										error.networkResponse.statusCode).equals(
										APIS.APITokenErrorCode2)) {
									ApiTokenAuthentication.refrehToken(getActivity(),
											updatedToken -> {
												if (updatedToken == null) {
												} else {
													Log.e("UpdatedToken2", updatedToken);
													editTask();
												}
											});
								}
							} else {
								Utility.ShowToast(getActivity(),
										getResources().getString(R.string.something_went_wrong));
							}
							error.getMessage();
							Log.e(TAG, "onErrorResponse: >>" + error.toString());
							hideProgressDialog();
						}
					}) {
						@Override
						public Map<String, String> getHeaders() {
							Map<String, String> params = new HashMap<>();
							params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
							params.put(APIS.APITokenKEY,
									Utility.getSharedPreferences(getActivity(),
									APIS.APITokenValue));
							return params;
						}
						
						@Override
						protected Map<String, String> getParams() {
							Map<String, String> params = new HashMap<String, String>();
							params.put("title", etTaskTitle.getText().toString().trim());
							params.put("description",
									etTaskDescription.getText().toString().trim());
							params.put("start_date",
									getFormattedDate(tvStartDate.getText().toString().trim(),
											DATE_FORMAT_FOR_DISPLAY, DATE_FORMAT_FOR_API));
							params.put("end_date",
									getFormattedDate(tvEndDate.getText().toString().trim(),
											DATE_FORMAT_FOR_DISPLAY, DATE_FORMAT_FOR_API));
							params.put("assign_to", getAssignedCaregiversId());
							params.put("task_id", taskData.getId());
							params.put("task_status", etStatusOfTask.getText().toString());
							return params;
						}
						
						@Override
						protected Map<String, DataPart> getByteData() {
							Map<String, DataPart> params = new HashMap<>();
							int attachmentsCount = 0;
							taskAttachmentsList = attachmentsAdapter.getAttachments();
							for (int i = 0; i < taskAttachmentsList.size(); i++) {
								TaskAttachmentsModel model = taskAttachmentsList.get(i);
								if (model.getIsFromGallery() == 1) {
									byte[] image = readBytesFromFile(model.getFilePath());
									params.put("attachments[" + attachmentsCount + "]",
											new DataPart(model.getFileName(), image,
													model.getMimeType()));
									attachmentsCount++;
								}
							}
							return params;
						}
					};
			multipartRequest.setShouldCache(false);
			multipartRequest.setTag(TAG);
			multipartRequest.setRetryPolicy(
					new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
							DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			AppController.getInstance().addToRequestQueue(multipartRequest);
		}
	}
	
	private void editComment(String editedComment, int editCommentPosition, String editCommentId) {
		showProgressDialog(getResources().getString(R.string.Loading));
		StringRequest stringRequest =
				new StringRequest(Request.Method.POST, APIS.BASEURL + APIS.ADD_NEW_TASK_COMMENT,
						new Response.Listener<String>() {
							@Override
							public void onResponse(String response) {
								Log.e(TAG, "onResponse: " + response);
								hideProgressDialog();
								AddTaskCommentModel addTaskCommentModel =
										new Gson().fromJson(response, AddTaskCommentModel.class);
								if (addTaskCommentModel.getStatusCode() == 200) {
									TaskCommentListModel.Response comment =
											addTaskCommentModel.getResponse();
									if (comment != null) {
										taskComments.set(editCommentPosition, comment);
										taskCommentsAdapter.updateComment(editCommentPosition,
												comment);
										etEditComment.setText("");
										llEditComment.setVisibility(View.GONE);
										llAddComment.setVisibility(View.VISIBLE);
										setUpCommentsData(taskComments.size());
									}
									etAddComment.setText("");
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
										editComment(editedComment, editCommentPosition,
												editCommentId);
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
					
					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();
						params.put("task_id", taskData.getId());
						params.put("comment", editedComment);
						params.put("comment_id", editCommentId);
						return params;
					}
				};
		AppController.getInstance().addToRequestQueue(stringRequest);
	}
	
	private void getAllCaregiversDetails() {
		showProgressDialog(getResources().getString(R.string.Loading));
		JSONObject mainObject = new JSONObject();
		try {
			mainObject.put("caregiverr_id",
					Utility.getSharedPreferences(getActivity(), APIS.caregiver_id));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JsonObjectRequest jsonObjReq =
				new JsonObjectRequest(Request.Method.POST, APIS.BASEURL + APIS.CaregiverListAPI,
						mainObject, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d(TAG, "Get To CareGiver response=" + response.toString());
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
							setupSelectedCaregivers();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.d(TAG, "Error: " + error.getMessage());
						hideProgressDialog();
						if (error.networkResponse != null) {
							if (String.valueOf(error.networkResponse.statusCode).equals(
									APIS.APITokenErrorCode) || String.valueOf(
									error.networkResponse.statusCode).equals(
									APIS.APITokenErrorCode2)) {
								ApiTokenAuthentication.refrehToken(getActivity(), updatedToken -> {
									if (updatedToken == null) {
									} else {
										Log.e("UpdatedToken2", updatedToken);
										getAllCaregiversDetails();
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
						params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
						params.put(APIS.APITokenKEY,
								Utility.getSharedPreferences(getActivity(), APIS.APITokenValue));
						return params;
					}
				};
		AppController.getInstance().addToRequestQueue(jsonObjReq);
	}
	
	private void getTaskComments() {
		StringRequest stringRequest =
				new StringRequest(Request.Method.POST, APIS.BASEURL + APIS.GET_TODO_COMMENT_LIST,
						new Response.Listener<String>() {
							@Override
							public void onResponse(String response) {
								Log.e(TAG, "onResponse: " + response);
								TaskCommentListModel taskCommentListModel =
										new Gson().fromJson(response, TaskCommentListModel.class);
								if (taskCommentListModel.getStatusCode() == 200) {
									taskComments = new ArrayList<>();
									taskComments =
											(ArrayList<TaskCommentListModel.Response>) taskCommentListModel.getResponse();
									if (taskComments.size() > 0) {
										setUpCommentsData(taskComments.size());
										taskCommentsAdapter.updateComments(taskComments);
									} else {
										setUpCommentsData(taskComments.size());
									}
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
										getTaskComments();
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
					
					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();
						params.put("task_id", taskData.getId());
						return params;
					}
				};
		AppController.getInstance().addToRequestQueue(stringRequest);
	}
	
	private void getTaskActivities() {
		StringRequest stringRequest =
				new StringRequest(Request.Method.POST, APIS.BASEURL + APIS.GET_TODO_ACTIVITY_LIST,
						new Response.Listener<String>() {
							@Override
							public void onResponse(String response) {
								Log.e(TAG, "onResponse: " + response);
								TaskActivitiesModel taskCommentListModel =
										new Gson().fromJson(response, TaskActivitiesModel.class);
								if (taskCommentListModel.getStatusCode() == 200) {
									taskActivities = new ArrayList<>();
									taskActivities =
											(ArrayList<TaskActivitiesModel.Response>) taskCommentListModel.getResponse();
									if (taskActivities.size() > 0) {
										setUpActivitiesData(taskActivities.size());
										taskActivitiesAdapter.updateActivities(taskActivities);
									} else {
										setUpActivitiesData(taskActivities.size());
									}
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
										getTaskActivities();
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
					
					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();
						params.put("task_id", taskData.getId());
						return params;
					}
				};
		AppController.getInstance().addToRequestQueue(stringRequest);
	}
	
	private void addNewComment(String taskComment, String taskId) {
		showProgressDialog(getResources().getString(R.string.Loading));
		StringRequest stringRequest =
				new StringRequest(Request.Method.POST, APIS.BASEURL + APIS.ADD_NEW_TASK_COMMENT,
						new Response.Listener<String>() {
							@Override
							public void onResponse(String response) {
								Log.e(TAG, "onResponse: " + response);
								hideProgressDialog();
								AddTaskCommentModel addTaskCommentModel =
										new Gson().fromJson(response, AddTaskCommentModel.class);
								if (addTaskCommentModel.getStatusCode() == 200) {
									TaskCommentListModel.Response comment =
											addTaskCommentModel.getResponse();
									if (comment != null) {
										taskComments.add(comment);
										taskCommentsAdapter.addComment(comment);
										setUpCommentsData(taskComments.size());
									}
									etAddComment.setText("");
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
										addNewComment(taskComment, taskId);
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
					
					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();
						params.put("task_id", taskId);
						params.put("comment", taskComment);
						return params;
					}
				};
		AppController.getInstance().addToRequestQueue(stringRequest);
	}
	
	private void deleteTaskComment(int position, String taskId, String commentId) {
		showProgressDialog(getResources().getString(R.string.Loading));
		StringRequest stringRequest =
				new StringRequest(Request.Method.POST, APIS.BASEURL + APIS.DELETE_TASK_COMMENT,
						new Response.Listener<String>() {
							@Override
							public void onResponse(String response) {
								hideProgressDialog();
								DeleteCommentModel deleteCommentModel =
										new Gson().fromJson(response, DeleteCommentModel.class);
								if (deleteCommentModel.getStatusCode() == 200) {
									taskComments.remove(position);
									taskCommentsAdapter.remove(position);
									setUpCommentsData(taskComments.size());
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
								ApiTokenAuthentication.refrehToken(getActivity(), updatedToken -> {
									if (updatedToken == null) {
									} else {
										Log.e("UpdatedToken2", updatedToken);
										deleteTaskComment(position, taskId, commentId);
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
					
					@Nullable
					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();
						params.put("comment_id", commentId);
						params.put("task_id", taskId);
						return params;
					}
				};
		RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
		requestQueue.add(stringRequest);
	}
	
	private String getAssignedCaregiversId() {
		ArrayList<String> selectedCaregiverId = new ArrayList<>();
		for (int i = 0; i < selectedCaregivers.size(); i++) {
			TaskCaregiversModel caregiversModel = tempCaregiverName.get(selectedCaregivers.get(i));
			selectedCaregiverId.add(caregiversModel.getId());
		}
		return selectedCaregiverId.toString().replace("[", "").replace("]", "");
	}
	
	private void setupSelectedCaregivers() {
		List<String> caregiversId =
				Arrays.asList(taskData.getAssignTo().replace(" ", "").split(","));
		selectedCaregivers = new ArrayList<>();
		for (int i = 0; i < tempCaregiverName.size(); i++) {
			TaskCaregiversModel model = tempCaregiverName.get(i);
			if (caregiversId.contains(model.getId())) {
				selectedCaregivers.add(i);
			}
		}
		caregiverAdapter.update(getSelectedCaregivers(selectedCaregivers));
		setCaregiverAssignedOrNot();
	}
	
	private void setUpActivitiesData(int size) {
		//		tvCommentsCount.setText(size + " Comments");
		if (size > 0) {
			rcvActivities.setVisibility(View.VISIBLE);
			llNoActivities.setVisibility(View.GONE);
		} else {
			rcvActivities.setVisibility(View.GONE);
			llNoActivities.setVisibility(View.VISIBLE);
		}
	}
	
	private void setUpCommentsData(int size) {
		tvCommentsCount.setText(size + " Comments");
		if (size > 0) {
			rcvComments.setVisibility(View.VISIBLE);
			llNoComments.setVisibility(View.GONE);
		} else {
			rcvComments.setVisibility(View.GONE);
			llNoComments.setVisibility(View.VISIBLE);
		}
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
	
	private void setupSelectedAttachments() {
		Log.e(TAG, "setupSelectedAttachments: " + taskData.getAttachments());
		taskAttachmentsList = new ArrayList<>();
		if (taskData.getAttachments() != null) {
			List<String> attachments =
					Arrays.asList(taskData.getAttachments().replace(" ", "").split(","));
			for (int i = 0; i < attachments.size(); i++) {
				TaskAttachmentsModel attachment = new TaskAttachmentsModel();
				attachment.setFilePath("");
				attachment.setFileName(attachments.get(i));
				attachment.setFileExtension("");
				attachment.setMimeType("");
				attachment.setIsFromGallery(0);
				taskAttachmentsList.add(attachment);
			}
		}
		attachmentsAdapter.update(taskAttachmentsList);
		setAttachmentAddedOrNot();
	}
	
	private void showCaregiverNamesDialog() {
		Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_task_by_names);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		Objects.requireNonNull(dialog.getWindow()).setLayout(displayMetrics.widthPixels - 200,
				Toolbar.LayoutParams.WRAP_CONTENT);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		TextView tvCancel = dialog.findViewById(R.id.tvCancel);
		TextView tvHeader = dialog.findViewById(R.id.tvHeader);
		TextView tvSubmit = dialog.findViewById(R.id.tvSubmit);
		RecyclerView rcvNames = dialog.findViewById(R.id.rcvNames);
		tvHeader.setText("Assign To");
		rcvNames.setLayoutManager(new LinearLayoutManager(getActivity()));
		CaregiverListForTaskAdapter adapter =
				new CaregiverListForTaskAdapter(getActivity(), tempCaregiverName,
						selectedCaregivers, true);
		rcvNames.setAdapter(adapter);
		tvSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				selectedCaregivers = adapter.getSelectedCaregivers();
				caregiverAdapter.update(getSelectedCaregivers(selectedCaregivers));
				setCaregiverAssignedOrNot();
			}
		});
		tvCancel.setOnClickListener(v -> dialog.dismiss());
		dialog.show();
	}
	
	private void showAttachmentOptionsDialog() {
		final BottomSheetDialog bottomSheetDialog =
				new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
		bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bottomSheetDialog.setContentView(R.layout.pick_img_layout_todo_task);
		bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		TextView tvDialogHeader = bottomSheetDialog.findViewById(R.id.tvDialogHeader);
		Typeface typeface = ResourcesCompat.getFont(getActivity(), R.font.muli_bold);
		tvDialogHeader.setTypeface(typeface);
		tvDialogHeader.setText("Add Attachment");
		LinearLayout gallery = bottomSheetDialog.findViewById(R.id.ivGallery);
		LinearLayout camera = bottomSheetDialog.findViewById(R.id.ivCamera);
		gallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bottomSheetDialog.dismiss();
				galleryIntent();
			}
		});
		camera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bottomSheetDialog.dismiss();
				cameraIntent();
			}
		});
		bottomSheetDialog.show();
	}
	
	private void openDatePickerDialog(boolean isStartDate) {
		Calendar calendar = Calendar.getInstance();
		DatePickerDialog datePickerDialog =
				new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
						calendar.set(Calendar.YEAR, year);
						calendar.set(Calendar.MONTH, month);
						calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						SimpleDateFormat sdf =
								new SimpleDateFormat(DATE_FORMAT_FOR_DISPLAY, Locale.US);
						String date = sdf.format(calendar.getTime());
						if (isStartDate) {
							updateStartDate(date);
						} else {
							updateEndDate(date);
						}
					}
				}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
						calendar.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
		datePickerDialog.show();
	}
	
	private void updateStartDate(String startDate) {
		tvStartDate.setText(startDate);
	}
	
	private void updateEndDate(String endDate) {
		tvEndDate.setText(endDate);
	}
	
	private void galleryIntent() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);//
		startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_FILE);
	}
	
	private void cameraIntent() {
		ContentValues values = new ContentValues();
		imageUri = getActivity().getApplicationContext().getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			return;
		}
		Log.e("TAG", "onActivityResult: " + requestCode);
		taskAttachmentsList = new ArrayList<>();
		switch (requestCode) {
			case PICK_FROM_FILE:
				try {
					Uri mImageCaptureUri = data.getData();
					String path = getPath(getActivity(), mImageCaptureUri); // From Gallery
					TaskAttachmentsModel attachment = new TaskAttachmentsModel();
					attachment.setFileExtension(".png");
					attachment.setFileName(new File(path).getName());
					attachment.setFilePath(path);
					attachment.setMimeType("image/jpeg");
					attachmentsAdapter.add(attachment);
					setAttachmentAddedOrNot();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case REQUEST_IMAGE_CAPTURE:
				try {
					String path = getPath(getActivity(), imageUri); // From Gallery
					TaskAttachmentsModel attachment = new TaskAttachmentsModel();
					attachment.setFileExtension(".png");
					attachment.setFileName(new File(path).getName());
					attachment.setFilePath(path);
					attachment.setMimeType("image/jpeg");
					attachmentsAdapter.add(attachment);
					setAttachmentAddedOrNot();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
		}
	}
	
	private ArrayList<TaskCaregiversModel> getSelectedCaregivers(
			ArrayList<Integer> selectedCaregivers) {
		ArrayList<TaskCaregiversModel> caregivers = new ArrayList<>();
		for (int i = 0; i < selectedCaregivers.size(); i++) {
			caregivers.add(tempCaregiverName.get(selectedCaregivers.get(i)));
		}
		return caregivers;
	}
	
	private void setCaregiverAssignedOrNot() {
		int caregiversCount = caregiverAdapter.getCaregiversCount();
		if (caregiversCount > 1) {
			tvNoCaregiverAssigned.setVisibility(View.GONE);
		} else {
			tvNoCaregiverAssigned.setVisibility(View.VISIBLE);
		}
	}
	
	private void setAttachmentAddedOrNot() {
		int attachmentsCount = attachmentsAdapter.getAttachmentCount();
		if (attachmentsCount > 1) {
			tvNoAttachmentsAdded.setVisibility(View.GONE);
		} else {
			tvNoAttachmentsAdded.setVisibility(View.VISIBLE);
		}
	}
	
	private String getFormattedDate(String endDate, String sourceFormat, String resultFormat) {
		SimpleDateFormat sourceDateFormat = new SimpleDateFormat(sourceFormat);
		SimpleDateFormat formattedDateFormat = new SimpleDateFormat(resultFormat);
		Date sourceDate = null;
		try {
			sourceDate = sourceDateFormat.parse(endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return formattedDateFormat.format(sourceDate);
	}
	
	public static byte[] readBytesFromFile(String aFilePath) {
		FileInputStream fio = null;
		byte[] bytesArray = null;
		try {
			File file = new File(aFilePath);
			bytesArray = new byte[(int) file.length()];
			//read file into bytes[]
			fio = new FileInputStream(file);
			fio.read(bytesArray);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fio != null) {
				try {
					fio.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bytesArray;
	}
	
}