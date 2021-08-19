package com.soultabcaregiver.sendbird_chat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.alert.model.CareGiverListModel;
import com.soultabcaregiver.sendbird_chat.model.GroupChatImageModel;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;
import com.soultabcaregiver.utils.VolleyMultipartRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static com.soultabcaregiver.utils.photoFileUtils.getPath;

public class CreateGroupFragment extends BaseFragment {
	
	private static final int RequestPermissionCode = 3, REQUEST_IMAGE_CAPTURE = 101,
			PICK_IMAGE_GALLERY = 1;
	
	private static final String EXTRA_IS_GROUP = "extra_is_group";
	
	private boolean isForGroupChat = false;
	
	private CreateGroupUsersAdapter adapter;
	
	private ProgressBar progressDialog;
	
	private CreateGroupUsersAdapter.OnItemTapListener onItemTapListener;
	
	private final String TAG = getClass().getSimpleName();
	
	private RecyclerView recyclerView;
	
	private View dataLayout, noDataLayout;
	
	private TextView backButton, backButtonNoData;
	
	private TextView createGroupButton;
	
	private EditText searchEditText;
	
	private EditText groupNameEditText;
	
	private final List<String> mSelectedIds = new ArrayList<>();
	
	Uri imageUri;
	
	private CircleImageView ic_group_img;
	
	private String filedata = "", Buttonclick, chat_image = "";
	
	private Bitmap UserBitmap;
	
	private Context mContext;
	
	private AlertDialog alertDialog;
	
	private List<String> mUplaodImageByteList = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_create_group, container, false);
		
		mContext = getActivity();
		onItemTapListener = (user, checked) -> {
			if (user.getIsSendBirdUser().equals("0")) {
				Utility.ShowToast(getContext(), getString(R.string.update_application_for_chat));
				return;
			}
			if (isForGroupChat) {
				if (checked) {
					mSelectedIds.add(user.getId());
				} else {
					mSelectedIds.remove(user.getId());
				}
			} else {
				ArrayList<String> ids = new ArrayList<>();
				ids.add(user.getId());
				createChatChannel(ids);
			}
		};
		
		isForGroupChat = getArguments().getBoolean(EXTRA_IS_GROUP, false);
		
		adapter = new CreateGroupUsersAdapter(onItemTapListener, isForGroupChat);
		initUI(view);
		return view;
	}
	
	private void createChatChannel(List<String> ids) {
		showProgressDialog(getContext(), getString(R.string.creating_group));
		ChatHelper.createGroupChannel(ids, !isForGroupChat, groupChannel -> {
			groupChannel.updateChannel(groupNameEditText.getText().toString(), "", "",
					(groupChannel1, e) -> {
						hideProgressDialog();
						getActivity().onBackPressed();
						Log.e("tag", "channel created");
					});
		});
		
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
		ic_group_img = view.findViewById(R.id.ic_group_img);
		
		LinearLayout groupNameLayout = view.findViewById(R.id.groupNameLayout);
		if (isForGroupChat) {
			groupNameLayout.setVisibility(View.VISIBLE);
		} else {
			groupNameLayout.setVisibility(View.GONE);
		}
		
		LinearLayout buttonLayout = view.findViewById(R.id.buttonLayout);
		if (isForGroupChat) {
			buttonLayout.setVisibility(View.VISIBLE);
		} else {
			buttonLayout.setVisibility(View.GONE);
		}
		
		TextView titleText = view.findViewById(R.id.titleText);
		if (isForGroupChat) {
			titleText.setText(getString(R.string.add_new_group));
		} else {
			titleText.setText(getString(R.string.start_new_chat));
		}
		
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
			
			if (mSelectedIds.size() == 1) {
				Utility.showSnackBar(getView(), getString(R.string.select_more_than_one_member));
				return;
			}
			
			if (UserBitmap != null) {
				showProgressDialog(getContext(), getString(R.string.creating_group));
				UploadGroupImage();
				
			} else {
				showProgressDialog(getContext(), getString(R.string.creating_group));
				ChatHelper.createGroupChannel(mSelectedIds, false, groupChannel -> {
					groupChannel.updateChannel(groupNameEditText.getText().toString(), "", "",
							(groupChannel1, e) -> {
								hideProgressDialog();
								requireActivity().onBackPressed();
								Log.e("tag", "channel created");
							});
				});
			}
		});
		
		ic_group_img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				filedata = "";
				//  openFolder();
				if (ContextCompat.checkSelfPermission(mContext,
						Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
					// Camera permission granted
					selectImage();
				} else {
					
					if (Buttonclick.equals("")) {
						Buttonclick = "1";
						CheakPermissions();
					} else if (Buttonclick.equals("1")) {
						Buttonclick = "2";
						AccessCamera();
					} else {
						AccessCamera();
					}
				}
			}
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
		
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
				APIS.BASEURL + APIS.CaregiverListAPIForCreateGroup, mainObject, response -> {
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
			} else if (String.valueOf(careGiverProfileModel.getStatusCode()).equals("403")) {
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
	
	public static CreateGroupFragment newInstance(boolean isForGroupChat) {
		CreateGroupFragment groupFragment = new CreateGroupFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean(EXTRA_IS_GROUP, isForGroupChat);
		groupFragment.setArguments(bundle);
		return groupFragment;
	}
	
	private void selectImage() {
		
		LayoutInflater inflater =
				(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.pick_img_layout, null);
		final AlertDialog.Builder builder =
				new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
		
		builder.setView(layout);
		builder.setCancelable(true);
		alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		alertDialog.getWindow().setGravity(Gravity.BOTTOM);
		alertDialog.show();
		
		LinearLayout Gallery = layout.findViewById(R.id.Gallery);
		LinearLayout Camera = layout.findViewById(R.id.Camera);
		
		Gallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				galleryIntent();
			}
		});
		
		Camera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				cameraIntent();
			}
		});
		
		
	}
	
	private void CheakPermissions() {
		if (!(ActivityCompat.checkSelfPermission(mContext,
				Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) || !(ActivityCompat.checkSelfPermission(
				mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
			ActivityCompat.requestPermissions(getActivity(),
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
							Manifest.permission.CAMERA},
					RequestPermissionCode);
			
			
		}
	}
	
	private void AccessCamera() {
		
		LayoutInflater inflater =
				(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.cam_permission, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		
		builder.setView(layout);
		builder.setCancelable(false);
		alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
		
		TextView Cancle = layout.findViewById(R.id.btn_cancle);
		TextView btn_Ok = layout.findViewById(R.id.btn_OK);
		
		alertDialog.show();
		
		Cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		
		btn_Ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				CheakPermissions();
			}
		});
	}
	
	private void galleryIntent() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);//
		startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_IMAGE_GALLERY);
	}
	
	private void cameraIntent() {
		
		ContentValues values = new ContentValues();
		imageUri = getActivity().getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
	}
	
	@RequiresApi (api = Build.VERSION_CODES.KITKAT)
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			
			return;
		}
		Log.e(TAG, "onActivityResult: " + requestCode);
		mUplaodImageByteList = new ArrayList<>();
		
		switch (requestCode) {
			
			case PICK_IMAGE_GALLERY:
				try {
					Uri mImageCaptureUri = data.getData();
					String path = getPath(mContext, mImageCaptureUri); // From Gallery
					
					UserBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),
							mImageCaptureUri);
					
					if (path == null) {
						path = mImageCaptureUri.getPath(); // From File Manager
					}
					filedata = path;
					Log.e("Activity", "PathHolder22= " + path);
					String filename = path.substring(path.lastIndexOf("/") + 1);
					String file;
					if (filename.indexOf(".") > 0) {
						file = filename.substring(0, filename.lastIndexOf("."));
					} else {
						file = "";
					}
					if (TextUtils.isEmpty(file)) {
						Utility.ShowToast(mContext, "File not valid");
					} else {
						
						mUplaodImageByteList.clear();
						mUplaodImageByteList = new ArrayList<>();
						mUplaodImageByteList.add(path);
						
						ExifInterface ei = new ExifInterface(path);
						int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
								ExifInterface.ORIENTATION_UNDEFINED);
						
						Bitmap rotatedBitmap = null;
						switch (orientation) {
							
							case ExifInterface.ORIENTATION_ROTATE_90:
								rotatedBitmap = rotateImage(UserBitmap, 90);
								break;
							
							case ExifInterface.ORIENTATION_ROTATE_180:
								rotatedBitmap = rotateImage(UserBitmap, 180);
								break;
							
							case ExifInterface.ORIENTATION_ROTATE_270:
								rotatedBitmap = rotateImage(UserBitmap, 270);
								break;
							
							case ExifInterface.ORIENTATION_NORMAL:
							default:
								rotatedBitmap = UserBitmap;
						}
						
						Glide.with(mContext).load(rotatedBitmap).placeholder(
								R.drawable.ic_group_blue).into(ic_group_img);
						
						
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("Error======>", e.toString());
				}
				break;
			
			case REQUEST_IMAGE_CAPTURE:
				try {
					Bitmap bitmap =
							MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
									imageUri);
					
					UserBitmap =
							MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
									imageUri);
					
					String path = getPath(mContext, imageUri); // From Gallery
					
					if (path == null) {
						path = data.getData().getPath(); // From File Manager
					}
					filedata = path;
					Log.e("Activity", "PathHolder22= " + path);
					
					String filename = path.substring(path.lastIndexOf("/") + 1);
					String file;
					if (filename.indexOf(".") > 0) {
						file = filename.substring(0, filename.lastIndexOf("."));
					} else {
						file = "";
					}
					if (TextUtils.isEmpty(file)) {
						Utility.ShowToast(mContext, "File not valid");
					} else {
						
						Log.e("filedata", filedata);
						Log.e("filename", filename);
						Log.e("file", file);
						
						mUplaodImageByteList.clear();
						mUplaodImageByteList = new ArrayList<>();
						mUplaodImageByteList.add(path);
						
						ExifInterface ei = new ExifInterface(path);
						int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
								ExifInterface.ORIENTATION_UNDEFINED);
						
						Bitmap rotatedBitmap = null;
						switch (orientation) {
							
							case ExifInterface.ORIENTATION_ROTATE_90:
								rotatedBitmap = rotateImage(UserBitmap, 90);
								break;
							
							case ExifInterface.ORIENTATION_ROTATE_180:
								rotatedBitmap = rotateImage(UserBitmap, 180);
								break;
							
							case ExifInterface.ORIENTATION_ROTATE_270:
								rotatedBitmap = rotateImage(UserBitmap, 270);
								break;
							
							case ExifInterface.ORIENTATION_NORMAL:
							default:
								rotatedBitmap = UserBitmap;
						}
						
						Glide.with(mContext).load(rotatedBitmap).placeholder(
								R.drawable.ic_group_blue).into(ic_group_img);
						
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					
					Log.e("Error======>", e.toString());
				}
				
				break;
			
		}
	}
	
	public static Bitmap rotateImage(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
				true);
		
	}
	
	private void UploadGroupImage() {
		//showProgressDialog(mContext,getResources().getString(R.string.Loading));
		final VolleyMultipartRequest multipartRequest =
				new VolleyMultipartRequest(Request.Method.POST,
						APIS.BASEURL + APIS.GroupChatProfileImage,
						new Response.Listener<NetworkResponse>()
								////Place user for service
						{
							@Override
							public void onResponse(NetworkResponse response) {
								//	hideProgressDialog();
								try {
									String resultResponse = new String(response.data);
									Log.e("response_profile_update", resultResponse);
									
									GroupChatImageModel groupChatImageModel =
											new Gson().fromJson(resultResponse,
													GroupChatImageModel.class);
									
									if (String.valueOf(groupChatImageModel.getStatusCode()).equals(
											"200")) {
										chat_image = groupChatImageModel.getResponse().getImage();
										
										ChatHelper.createGroupChannel(mSelectedIds, false,
												groupChannel -> {
													groupChannel.updateChannel(
															groupNameEditText.getText().toString(),
															chat_image, "", (groupChannel1, e) -> {
																hideProgressDialog();
																requireActivity().onBackPressed();
																Log.e("tag", "channel created");
															});
												});
									} else {
										hideProgressDialog();
										
									}
									
									
								} catch (Exception e) {
									e.printStackTrace();
									
								}
							}
							
						}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						error.getMessage();
						Log.e(TAG, "onErrorResponse: >>" + error.toString());
						hideProgressDialog();
					}
				}) {
					@Override
					public Map<String, String> getHeaders() {
						Map<String, String> params = new HashMap<>();
						params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
						
						return params;
					}
					
					@Override
					protected Map<String, DataPart> getByteData() {
						Map<String, DataPart> params = new HashMap<>();
						long imagename = System.currentTimeMillis();
						
						params.put("group_image", new DataPart(imagename + ".jpg",
								Utility.getFileDataFromDraw(mContext, ic_group_img.getDrawable()),
								"image/jpeg"));
						
						return params;
					}
					
					@Override
					protected Map<String, String> getParams() {
						Map<String, String> params = new HashMap<String, String>();
						params.put("prev_image", chat_image);
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
