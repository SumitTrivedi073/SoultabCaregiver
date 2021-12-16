package com.soultabcaregiver.activity.todotask.fragemets;

import static android.app.Activity.RESULT_CANCELED;
import static com.soultabcaregiver.utils.photoFileUtils.getPath;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
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
import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.BuildConfig;
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
import com.soultabcaregiver.sendbird_chat.utils.FileUtils;
import com.soultabcaregiver.sendbird_chat.utils.PhotoViewerActivity;
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
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class TodoTaskDetailFragment extends BaseFragment {
	
	private final String TAG = getClass().getSimpleName();
	
	private static final String DATE_FORMAT_FOR_DISPLAY = "MM/dd/yyyy", DATE_FORMAT_FOR_API =
			"yyyy-MM-dd";
	
	public static final int REQUEST_IMAGE_CAPTURE = 101, PICK_FROM_FILE = 6, PICK_DOCUMENTS = 8;
	
	private int editCommentPosition = -1;
	
	private int taskPosition;
	
	private String editCommentId = "";
	
	private Uri imageUri;
	
	private ImageView ivBack;
	
	private TextView tvStartDate, tvEndDate, tvViewComments, tvViewLogs, tvCommentsCount,
			tvAddComments, tvNoCaregiverAssigned, tvNoAttachmentsAdded, tvEditComment,
			tvCancelEditComment, etStatusOfTask;
	
	private EditText etTaskTitle, etTaskDescription, etAddComment, etEditComment;
	
	private RecyclerView rcvAssignedCaregiver, rcvAttachments, rcvComments, rcvActivities;
	
	private NestedScrollView nsRecyclerView;
	
	private CardView cvUpdateTask;
	
	private LinearLayout llStartDate, llEndDate, llComments, llNoComments, llNoActivities,
			llActivities, llAddComment, llEditComment;
	
	private Spinner spinnerTaskStatus;
	
	private TaskListModel.TaskData taskData = null;
	
	private List<String> taskStatus;
	
	private List<String> deletedAttachments = new ArrayList<>();
	
	private ArrayList<String> selectedCaregivers = new ArrayList<>();
	
	private ArrayList<TaskCaregiversModel> tempCaregiverName = new ArrayList<>();
	
	private ArrayList<TaskCommentListModel.Response> taskComments = new ArrayList<>();
	
	private ArrayList<TaskActivitiesModel.Response> taskActivities = new ArrayList<>();
	
	private ArrayList<TaskAttachmentsModel> taskAttachmentsList = new ArrayList<>();
	
	private AssignedToCaregiverAdapter caregiverAdapter;
	
	private AttachmentsAdapter attachmentsAdapter;
	
	private TaskCommentsAdapter taskCommentsAdapter;
	
	private TaskActivitiesAdapter taskActivitiesAdapter;
	
       boolean isDeleteattechment = false;
	
	private AssignedToCaregiverAdapter.OnCaregiverItemClickListener onCaregiverItemClickListener =
			new AssignedToCaregiverAdapter.OnCaregiverItemClickListener() {
				@Override
				public void onAddCaregiverClick() {
					showCaregiverNamesDialog();
				}
				
				@Override
				public void onRemoveCareGiverClick(String caregiverId, int caregiversCount) {
					selectedCaregivers.remove(caregiverId);
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
				public void removeAttachment(int attachmentsSize,
				                             TaskAttachmentsModel attachment) {
				
					if (attachment.getIsFromGallery() == 0) {
						Log.e("getFileName",attachment.getFileName());
						deletedAttachments.add(attachment.getFileName());
						
					}
					if (attachmentsSize == 1) {
						tvNoAttachmentsAdded.setVisibility(View.VISIBLE);
					}
				}
				
				@Override
				public void onPreviewClick(TaskAttachmentsModel attachment) {
					if (attachment.getFileExtension().contains(
							"jpg") || attachment.getFileExtension().contains(
							"jpeg") || attachment.getFileExtension().contains(
							"png") || attachment.getFileExtension().contains("gif")) {
						Intent i = new Intent(getActivity(), PhotoViewerActivity.class);
						String url;
						if (attachment.getIsFromGallery() == 0) {
							url = BuildConfig.taskImageUrl + attachment.getFilePath();
						} else {
							url = attachment.getFilePath();
						}
						i.putExtra("url", url);
						i.putExtra("type", attachment.getMimeType());
						startActivity(i);
					} else if (attachment.getIsFromGallery() == 0) {
						if (attachment.getFileExtension().contains(
								"pdf") || attachment.getFileExtension().contains(
								"doc") || attachment.getFileExtension().contains("docx")) {
							String url = BuildConfig.taskImageUrl + attachment.getFilePath();
							showDownloadConfirmDialog(attachment.getFileName(), url);
						}
					}
				}
			};
	
	private void showDownloadConfirmDialog(String name, String url) {
		if (permissionStorage(0)) {
			new AlertDialog.Builder(getActivity()).setMessage("Download file?").setPositiveButton(
					R.string.download, (dialog, which) -> {
						if (which == DialogInterface.BUTTON_POSITIVE) {
							FileUtils.downloadFile(getActivity(), url, name);
						}
					}).setNegativeButton(R.string.cancel_text, null).show();
		}
	}
	
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
	
	public TodoTaskDetailFragment(TaskListModel.TaskData data, int position) {
		this.taskData = data;
		this.taskPosition = position;
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
	
	// TODO: 12/3/2021 for restrict emoji enter in edit text
	public static class EmojiFilter {
		
		public static InputFilter[] getFilter() {
			InputFilter EMOJI_FILTER = new InputFilter() {
				@Override
				public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
				                           int dstart, int dend) {
					for (int index = start; index < end; index++) {
						int type = Character.getType(source.charAt(index));
						if (type == Character.SURROGATE || type == Character.NON_SPACING_MARK || type == Character.OTHER_SYMBOL) {
							return "";
						}
					}
					return null;
				}
			};
			return new InputFilter[]{EMOJI_FILTER};
		}
		
	}
	
	private void init(View view) {
		ivBack = view.findViewById(R.id.ivBackDetail);
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
		nsRecyclerView = view.findViewById(R.id.nsRecyclerView);
		llStartDate = view.findViewById(R.id.llStartDate);
		llEndDate = view.findViewById(R.id.llEndDate);
		llComments = view.findViewById(R.id.llComments);
		llNoComments = view.findViewById(R.id.llNoComments);
		llNoActivities = view.findViewById(R.id.llNoActivities);
		llActivities = view.findViewById(R.id.llActivities);
		llAddComment = view.findViewById(R.id.llAddComment);
		llEditComment = view.findViewById(R.id.llEditComment);
		etTaskTitle.setFilters(EmojiFilter.getFilter());
		etTaskDescription.setFilters(EmojiFilter.getFilter());
		etAddComment.setFilters(EmojiFilter.getFilter());
		etEditComment.setFilters(EmojiFilter.getFilter());
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
		ivBack.setOnClickListener(view -> requireActivity().onBackPressed());
		
		cvUpdateTask.setOnClickListener(view -> editTask());
		
		llStartDate.setOnClickListener(view -> {
			//				openDatePickerDialog(true);
		});
		llEndDate.setOnClickListener(view -> openDatePickerDialog(false, taskData.getStartDate()));
	
		tvAddComments.setOnClickListener(v -> {
			String newComment = etAddComment.getText().toString().trim();
			Log.e(TAG, "onClick: " + newComment);
			if (!newComment.isEmpty()) {
				addNewComment(newComment, taskData.getId());
			}
		});
		tvViewComments.setOnClickListener(v -> {
			tvViewComments.setVisibility(View.GONE);
			tvViewLogs.setVisibility(View.VISIBLE);
			llComments.setVisibility(View.VISIBLE);
			llActivities.setVisibility(View.GONE);
		});
		tvViewLogs.setOnClickListener(v -> {
			tvViewComments.setVisibility(View.VISIBLE);
			tvViewLogs.setVisibility(View.GONE);
			llComments.setVisibility(View.GONE);
			llActivities.setVisibility(View.VISIBLE);
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
		tvEditComment.setOnClickListener(v -> {
			String editedComment = etEditComment.getText().toString().trim();
			if (!editedComment.isEmpty()) {
				editComment(editedComment, editCommentPosition, editCommentId);
			}
		});
		tvCancelEditComment.setOnClickListener(v -> {
			editCommentId = "";
			llAddComment.setVisibility(View.VISIBLE);
			llEditComment.setVisibility(View.GONE);
		});
		etStatusOfTask.setOnClickListener(view -> spinnerTaskStatus.performClick());
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
		Log.e(TAG, "setupTaskData: " + taskData.getAttachments());
	}
	
	private void editTask() {
		if (etTaskTitle.getText().toString().trim().isEmpty()) {
			Utility.ShowToast(getActivity(),
					getResources().getString(R.string.todo_task_title_alert));
		} else if (etTaskDescription.getText().toString().trim().isEmpty()) {
			Utility.ShowToast(getActivity(),
					getResources().getString(R.string.todo_task_description_alert));
		} /*else if (tvStartDate.getText().toString().trim().isEmpty()) {
			Utility.ShowToast(getActivity(),
					getResources().getString(R.string.todo_task_start_date));
		} */ else if (tvEndDate.getText().toString().trim().isEmpty()) {
			Utility.ShowToast(getActivity(),
					getResources().getString(R.string.todo_task_end_date));
		} else if (caregiverAdapter.getCaregiversCount() == 1) {
			Utility.ShowToast(getActivity(),
					getResources().getString(R.string.todo_task_add_caregiver));
		} else {
			showProgressDialog(requireActivity(),
					requireActivity().getResources().getString(R.string.Loading));
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
										requireActivity().onBackPressed();
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
								params.put("end_date",
									getFormattedDate(tvEndDate.getText().toString().trim(),
											DATE_FORMAT_FOR_DISPLAY, DATE_FORMAT_FOR_API));
							params.put("assign_to", getAssignedCaregiversId());
							params.put("del_attachments", getDeleteAttachment());
							params.put("task_id", taskData.getId());
							params.put("task_status", etStatusOfTask.getText().toString());
							Log.e(TAG, "getParams: " + params);
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
		showProgressDialog(requireActivity(),
				requireActivity().getResources().getString(R.string.Loading));
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
		showProgressDialog(requireActivity(),
				requireActivity().getResources().getString(R.string.Loading));
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
							// TODO: 11/16/2021 add default caregiver for filter task by caregiver
							//  name..
							TaskCaregiversModel model = new TaskCaregiversModel();
							model.setId(
									Utility.getSharedPreferences(getActivity(),
											APIS.caregiver_id));
							model.setLastname("");
							model.setName("Me");
							String profileUrl =
									Utility.getSharedPreferences(getActivity(),
											APIS.profile_image);
							model.setProfileImage(
									profileUrl.substring(profileUrl.lastIndexOf("/") + 1,
											profileUrl.length()));
							tempCaregiverName.add(0, model);
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
		showProgressDialog(requireActivity(),
				requireActivity().getResources().getString(R.string.Loading));
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
										Intent intent = new Intent("task_list_filter");
										intent.putExtra("position", taskPosition);
										intent.putExtra("comment_count", taskComments.size());
										getActivity().sendBroadcast(intent);
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
		showProgressDialog(requireActivity(),
				requireActivity().getResources().getString(R.string.Loading));
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
									Intent intent = new Intent("task_list_filter");
									intent.putExtra("position", taskPosition);
									intent.putExtra("comment_count", taskComments.size());
									getActivity().sendBroadcast(intent);
								} else {
									hideProgressDialog();
									Utility.ShowToast(getActivity(), deleteCommentModel.getMessage());
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
		AppController.getInstance().addToRequestQueue(stringRequest);
	}

	
	private String getAssignedCaregiversId() {
		return selectedCaregivers.toString().replace("[", "").replace("]", "");
	}
	
	private String getDeleteAttachment() {
	/*	if (isDeleteattechment) {
			return "";
		}else {
			return deletedAttachments.toString().replace("[", "").replace("]", "").replace(" ", "");
		}*/
		return deletedAttachments.toString().replace("[", "").replace("]", "").replace(" ", "");
		
	}
	
	private void setupSelectedCaregivers() {
		List<String> caregiversId =
				Arrays.asList(taskData.getAssignTo().replace(" ", "").split(","));
		selectedCaregivers = new ArrayList<>();
		Log.e(TAG, "setupSelectedCaregivers: " + tempCaregiverName.size());
		for (int i = 0; i < tempCaregiverName.size(); i++) {
			TaskCaregiversModel model = tempCaregiverName.get(i);
			if (caregiversId.contains(model.getId())) {
				selectedCaregivers.add(model.getId());
			}
		}
		caregiverAdapter.update(getSelectedCaregivers(selectedCaregivers));
		setCaregiverAssignedOrNot();
	}
	
	private void setUpActivitiesData(int size) {
		//		tvCommentsCount.setText(size + " Comments");
		if (size > 0) {
			nsRecyclerView.setVisibility(View.VISIBLE);
			rcvActivities.setVisibility(View.VISIBLE);
			llNoActivities.setVisibility(View.GONE);
		} else {
			nsRecyclerView.setVisibility(View.GONE);
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
	

	
	private void setupSelectedAttachments() {
		taskAttachmentsList = new ArrayList<>();
		if (taskData.getAttachments() != null&& !TextUtils.isEmpty(taskData.getAttachments())) {
			List<String> attachments =
					Arrays.asList(taskData.getAttachments().replace(" ", "").split(","));
			for (int i = 0; i < attachments.size(); i++) {
				TaskAttachmentsModel attachment = new TaskAttachmentsModel();
				String fileName = attachments.get(i);
				attachment.setFilePath(fileName);
				attachment.setFileName(fileName);
				String fileExtension =
						fileName.substring(fileName.lastIndexOf("."), fileName.length());
				attachment.setFileExtension(fileExtension);
				attachment.setMimeType(getMimeTypeFromFileExtension(fileExtension));
				attachment.setIsFromGallery(0);
				taskAttachmentsList.add(attachment);
			}
		}
		attachmentsAdapter.update(taskAttachmentsList);
		setAttachmentAddedOrNot();
	}
	
	private String getMimeTypeFromFileExtension(String fileExtension) {
		String mimType = "";
		if (fileExtension.toLowerCase().contains("jpg") || fileExtension.toLowerCase().contains(
				"png") || fileExtension.toLowerCase().contains("jpeg")) {
			mimType = "image/jpeg";
		} else if (fileExtension.toLowerCase().contains(".gif")) {
			mimType = "image/gif";
		} else if (fileExtension.toLowerCase().contains("pdf")) {
			mimType = "application/pdf";
		} else if (fileExtension.toLowerCase().contains("doc")) {
			mimType = "application/msword";
		} else if (fileExtension.toLowerCase().contains(".docx")) {
			mimType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		}
		return mimType;
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
		LinearLayout llGallery = bottomSheetDialog.findViewById(R.id.llGallery);
		LinearLayout llCamera = bottomSheetDialog.findViewById(R.id.llCamera);
		LinearLayout llDocuments = bottomSheetDialog.findViewById(R.id.llDocuments);
		llGallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bottomSheetDialog.dismiss();
				galleryIntent();
			}
		});
		llCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bottomSheetDialog.dismiss();
				cameraIntent();
			}
		});
		llDocuments.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				bottomSheetDialog.dismiss();
				documentIntent();
			}
		});
		bottomSheetDialog.show();
	}
	
	private void openDatePickerDialog(boolean isStartDate, String minDate) {
		try {
			Calendar calendar = Calendar.getInstance();
			DatePickerDialog datePickerDialog =
					new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view, int year, int month,
						                      int dayOfMonth) {
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
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date pasTime = dateFormat.parse(minDate);
			datePickerDialog.getDatePicker().setMinDate(pasTime.getTime());
			datePickerDialog.show();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private void updateStartDate(String startDate) {
		tvStartDate.setText(startDate);
	}
	
	private void updateEndDate(String endDate) {
		tvEndDate.setText(endDate);
	}
	
	private void galleryIntent() {
		Intent intent = new Intent();
		intent.setType("*/*");
		intent.putExtra(Intent.EXTRA_MIME_TYPES,
				new String[]{"image/jpeg", "image/png", "image/gif"});
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_FILE);
	}
	
	private void documentIntent() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.putExtra(Intent.EXTRA_MIME_TYPES,
				new String[]{"application/pdf","application/msword",
						"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
						"image/jpeg", "image/png", "image/gif"});
		startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_DOCUMENTS);
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
			case PICK_DOCUMENTS:
				try {
					Uri mImageCaptureUri = data.getData();
					Hashtable<String, Object> info =
							FileUtils.getFileInfo(getActivity(), mImageCaptureUri);
					Log.e(TAG, "onActivityResult: " + info);
					String mimeType = (String) info.get("mime");
					String filName = (String) info.get("name");
					String filePath = (String) info.get("path");
					String extension =
							filePath.substring(filePath.lastIndexOf("."), filePath.length());
					TaskAttachmentsModel attachment = new TaskAttachmentsModel();
					attachment.setFileExtension(extension);
					attachment.setFileName(filName);
					attachment.setFilePath(filePath);
					attachment.setMimeType(mimeType);
					attachment.setIsFromGallery(1);
					attachmentsAdapter.add(attachment);
					setAttachmentAddedOrNot();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case PICK_FROM_FILE:
				try {
					Uri mImageCaptureUri = data.getData();
					Hashtable<String, Object> info =
							FileUtils.getFileInfo(getActivity(), mImageCaptureUri);
					Log.e(TAG, "onActivityResult: " + info);
					String mimeType = (String) info.get("mime");
					String filName = (String) info.get("name");
					String filePath = (String) info.get("path");
					String extension =
							filePath.substring(filePath.lastIndexOf("."), filePath.length());
					String path = getPath(getActivity(), mImageCaptureUri); // From Gallery
					TaskAttachmentsModel attachment = new TaskAttachmentsModel();
					attachment.setFileExtension(extension);
					attachment.setFileName(filName);
					attachment.setFilePath(path);
					attachment.setMimeType(mimeType);
					attachment.setIsFromGallery(1);
					attachmentsAdapter.add(attachment);
					setAttachmentAddedOrNot();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case REQUEST_IMAGE_CAPTURE:
				try {
					String path = getPath(getActivity(), imageUri); // From Gallery
					Hashtable<String, Object> info = FileUtils.getFileInfo(getActivity(),
							imageUri);
					Log.e(TAG, "onActivityResult: " + info);
					String mimeType = (String) info.get("mime");
					String filName = (String) info.get("name");
					String filePath = (String) info.get("path");
					String extension =
							filePath.substring(filePath.lastIndexOf("."), filePath.length());
					TaskAttachmentsModel attachment = new TaskAttachmentsModel();
					attachment.setFileExtension(extension);
					attachment.setFileName(filName);
					attachment.setFilePath(path);
					attachment.setMimeType(mimeType);
					attachment.setIsFromGallery(1);
					attachmentsAdapter.add(attachment);
					setAttachmentAddedOrNot();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
		}
	}
	
	
	private ArrayList<TaskCaregiversModel> getSelectedCaregivers(
			ArrayList<String> selectedCaregivers) {
		ArrayList<TaskCaregiversModel> caregivers = new ArrayList<>();
		for (int i = 0; i < selectedCaregivers.size(); i++) {
			for (TaskCaregiversModel model : tempCaregiverName) {
				if (model.getId().equalsIgnoreCase(selectedCaregivers.get(i))) {
					caregivers.add(model);
				}
			}
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
		FileInputStream input = null;
		File file = new File(aFilePath);
		if (file.exists())
			try {
				input = new FileInputStream(file);
				int len = (int) file.length();
				byte[] data = new byte[len];
				int count, total = 0;
				while ((count = input.read(data, total, len - total)) > 0)
					total += count;
				return data;
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (input != null)
					try {
						input.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
			}
		return null;
	}
	
	private boolean permissionStorage(int code) {
		if (ContextCompat.checkSelfPermission(getActivity(),
				Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0 || ContextCompat.checkSelfPermission(
				getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == 0) {
			return true;
		}
		ActivityCompat.requestPermissions(getActivity(),
				new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
						Manifest.permission.READ_EXTERNAL_STORAGE},
				code);
		return false;
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (permissions.length == 0) {
			return;
		}
		boolean allPermissionsGranted = true;
		if (grantResults.length > 0) {
			for (int grantResult : grantResults) {
				if (grantResult != PackageManager.PERMISSION_GRANTED) {
					allPermissionsGranted = false;
					break;
				}
			}
		}
		if (!allPermissionsGranted) {
			boolean somePermissionsForeverDenied = false;
			for (String permission : permissions) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
						permission)) {
					switch (requestCode) {
						case 0:
							ActivityCompat.requestPermissions(getActivity(),
									new String[]{"android.permission.WRITE_EXTERNAL_STORAGE",
											"android.permission.READ_EXTERNAL_STORAGE"},
									0);
							break;
						case 1:
							ActivityCompat.requestPermissions(getActivity(),
									new String[]{Manifest.permission.CAMERA,
											Manifest.permission.WRITE_EXTERNAL_STORAGE,
											Manifest.permission.READ_EXTERNAL_STORAGE},
									1);
							break;
					}
				} else {
					if (ActivityCompat.checkSelfPermission(getActivity(),
							permission) == PackageManager.PERMISSION_GRANTED) {
					} else {
						//set to never ask again
						Log.e("set to never ask again", permission);
						somePermissionsForeverDenied = true;
					}
				}
			}
			if (somePermissionsForeverDenied) {
				final androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder =
						new androidx.appcompat.app.AlertDialog.Builder(getActivity());
				alertDialogBuilder.setTitle("Permissions Required").setMessage(
						"please allow permission for storage.").setPositiveButton("Ok",
						(dialog, which) -> {
							Intent intent =
									new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
									Uri.fromParts("package", getActivity().getPackageName(),
											null));
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}).setNegativeButton("Cancel", (dialog, which) -> {
				}).setCancelable(false).create().show();
			}
		} else {
			switch (requestCode) {
				//act according to the request code used while requesting the permission(s).
			}
		}
	}
	
}