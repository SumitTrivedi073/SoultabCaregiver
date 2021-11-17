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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.soultabcaregiver.activity.todotask.model.CreateTaskModel;
import com.soultabcaregiver.activity.todotask.model.TaskAttachmentsModel;
import com.soultabcaregiver.activity.todotask.model.TaskCaregiversModel;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class CreateNewToDoTaskFragment extends Fragment {
	
	private final String TAG = getClass().getSimpleName();
	
	private static final String DATE_FORMAT_FOR_DISPLAY = "MM/dd/yyyy", DATE_FORMAT_FOR_API =
			"yyyy-MM-dd";
	
	public static final int REQUEST_IMAGE_CAPTURE = 101, PICK_FROM_FILE = 6;
	
	private ImageView ivBack;
	
	private TextView tvStartDate, tvEndDate, tvNoAttachmentsAdded, tvNoCaregiverAssigned;
	
	private EditText etTaskDescription, etTaskTitle;
	
	private Uri imageUri;
	
	private LinearLayout llStartDate, llEndDate;
	
	private CardView clCreateTask;
	
	private RecyclerView rcvAttachments, rcvAssignedCaregiver;
	
	private ArrayList<TaskAttachmentsModel> taskAttachmentsList = new ArrayList<>();
	
	private ArrayList<TaskCaregiversModel> tempCaregiverName = new ArrayList<>();
	
	private ArrayList<Integer> selectedCaregivers = new ArrayList<>();
	
	private AssignedToCaregiverAdapter caregiverAdapter;
	
	private AttachmentsAdapter attachmentsAdapter;
	
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
	
	private AttachmentsAdapter.OnAttachmentItemClickListeners onAttachmentItemClickListeners =
			new AttachmentsAdapter.OnAttachmentItemClickListeners() {
				@Override
				public void onAddAttachmentClick() {
					showAttachmentOptionsDialog();
				}
				
				@Override
				public void removeAttachment(int attachmentsSize,
				                             TaskAttachmentsModel attachment) {
					if (attachmentsSize == 1) {
						tvNoAttachmentsAdded.setVisibility(View.VISIBLE);
					}
				}
			};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_create_new_to_do_task, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		listeners();
	}
	
	private void init(View view) {
		ivBack = view.findViewById(R.id.ivBack);
		tvStartDate = view.findViewById(R.id.tvStartDate);
		tvEndDate = view.findViewById(R.id.tvEndDate);
		etTaskDescription = view.findViewById(R.id.etTaskDescription);
		etTaskTitle = view.findViewById(R.id.etTaskTitle);
		rcvAttachments = view.findViewById(R.id.rcvAttachments);
		rcvAssignedCaregiver = view.findViewById(R.id.rcvAssignedCaregiver);
		tvNoAttachmentsAdded = view.findViewById(R.id.tvNoAttachmentsAdded);
		tvNoCaregiverAssigned = view.findViewById(R.id.tvNoCaregiverAssigned);
		llStartDate = view.findViewById(R.id.llStartDate);
		llEndDate = view.findViewById(R.id.llEndDate);
		clCreateTask = view.findViewById(R.id.clCreateTask);
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
		attachmentsAdapter = new AttachmentsAdapter(getActivity(), onAttachmentItemClickListeners);
		rcvAttachments.setAdapter(attachmentsAdapter);
		getAllCaregiversDetails();
	}
	
	private void listeners() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				requireActivity().onBackPressed();
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
		clCreateTask.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				createTask();
			}
		});
	}
	
	private void createTask() {
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
									hideProgressDialog();
									String resultResponse = new String(response.data);
									Log.e(TAG, "onResponse: " + resultResponse);
									CreateTaskModel taskModel = new Gson().fromJson(resultResponse,
											CreateTaskModel.class);
									if (taskModel.getStatusCode() == 200) {
										Intent intent =
												new Intent(APIS.INTENT_FILTER_REFRESH_TASK_LIST);
										getActivity().sendBroadcast(intent);
										requireActivity().onBackPressed();
									} else {
										Utility.ShowToast(getActivity(), taskModel.getMessage());
									}
								}
							}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							VolleyLog.d("TAG", "Error: " + error.getMessage());
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
													createTask();
												}
											});
								}
							} else {
								Utility.ShowToast(getActivity(),
										getResources().getString(R.string.something_went_wrong));
							}
							error.getMessage();
							Log.e("TAG", "onErrorResponse: >>" + error.toString());
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
									getFormattedDate(tvStartDate.getText().toString().trim(),
											DATE_FORMAT_FOR_DISPLAY, DATE_FORMAT_FOR_API));
							params.put("assign_to", getAssignedCaregiversId());
							return params;
						}
						
						@Override
						protected Map<String, DataPart> getByteData() {
							Map<String, DataPart> params = new HashMap<>();
							taskAttachmentsList = attachmentsAdapter.getAttachments();
							for (int i = 0; i < taskAttachmentsList.size(); i++) {
								TaskAttachmentsModel model = taskAttachmentsList.get(i);
								byte[] image = readBytesFromFile(model.getFilePath());
								params.put("attachments[" + i + "]",
										new DataPart(model.getFileName(), image,
												model.getMimeType()));
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
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
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
						Map<String, String> params = new HashMap<>();
						params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
						params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
						params.put(APIS.HEADERKEY2,
								Utility.getSharedPreferences(getActivity(), APIS.EncodeUser_id));
						params.put(APIS.APITokenKEY,
								Utility.getSharedPreferences(getActivity(), APIS.APITokenValue));
						return params;
					}
				};
		AppController.getInstance().addToRequestQueue(jsonObjReq);
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
	
	private ArrayList<TaskCaregiversModel> getSelectedCaregivers(
			ArrayList<Integer> selectedCaregivers) {
		ArrayList<TaskCaregiversModel> caregivers = new ArrayList<>();
		for (int i = 0; i < selectedCaregivers.size(); i++) {
			caregivers.add(tempCaregiverName.get(selectedCaregivers.get(i)));
		}
		return caregivers;
	}
	
	private String getAssignedCaregiversId() {
		ArrayList<String> selectedCaregiverId = new ArrayList<>();
		for (int i = 0; i < selectedCaregivers.size(); i++) {
			TaskCaregiversModel caregiversModel = tempCaregiverName.get(selectedCaregivers.get(i));
			selectedCaregiverId.add(caregiversModel.getId());
		}
		return selectedCaregiverId.toString().replace("[", "").replace("]", "");
	}
	
	private void setCaregiverAssignedOrNot() {
		int caregiversCount = caregiverAdapter.getCaregiversCount();
		if (caregiversCount > 1) {
			tvNoCaregiverAssigned.setVisibility(View.GONE);
		} else {
			tvNoCaregiverAssigned.setVisibility(View.VISIBLE);
		}
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
	
	private String getFormattedDate(String date, String sourceFormat, String resultFormat) {
		SimpleDateFormat sourceDateFormat = new SimpleDateFormat(sourceFormat);
		SimpleDateFormat formattedDateFormat = new SimpleDateFormat(resultFormat);
		Date sourceDate = null;
		try {
			sourceDate = sourceDateFormat.parse(date);
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
	//	private void setCaregiverAssignedOrNot() {
	//		int caregiversCount = caregiverAdapter.getCaregiversCount();
	//		if (caregiversCount > 1) {
	//			tvNoCaregiverAssigned.setVisibility(View.GONE);
	//		} else {
	//			tvNoCaregiverAssigned.setVisibility(View.VISIBLE);
	//		}
	//	}
	
	private void setAttachmentAddedOrNot() {
		int attachmentsCount = attachmentsAdapter.getAttachmentCount();
		if (attachmentsCount > 1) {
			tvNoAttachmentsAdded.setVisibility(View.GONE);
		} else {
			tvNoAttachmentsAdded.setVisibility(View.VISIBLE);
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
	
}