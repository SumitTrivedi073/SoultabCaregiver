package com.soultabcaregiver.companion.fragment;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;
import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.Model.UpdateProfileModel;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static com.soultabcaregiver.utils.photoFileUtils.getPath;
import static com.soultabcaregiver.utils.photoFileUtils.rotateImage;

public class ProfileFragment extends BaseFragment implements View.OnClickListener,
                                                             OnCountryPickerListener {
	
	public static final int RequestPermissionCode = 3, REQUEST_IMAGE_CAPTURE = 101,
			PICK_FROM_FILE =
			6;
	
	private final String TAG = getClass().getSimpleName();
	
	private final int sortBy = com.mukesh.countrypicker.CountryPicker.SORT_BY_NONE;
	
	Context mContext;
	
	View view;
	
	RelativeLayout BackBtn;
	
	CircleImageView CompanionImg;
	
	EditText Firstname, Lastname, MobileNumber;
	
	TextView Email, CountryCode, UserName, Password,ChangePassword, SaveChange;
	
	LinearLayout CountryLL;
	
	Bitmap UserBitmap;
	
	AlertDialog AlertDialog;
	
	Uri ImageUri;
	
	private com.mukesh.countrypicker.CountryPicker CountryPicker;
	
	private String FileData = "", ButtonClick = "";
	
	CheckBox view_pwd1;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.countryLL:
				
				showPicker();
				break;
			
			case R.id.companion_img:
				
				FileData = "";
				//  openFolder();
				if (ContextCompat.checkSelfPermission(mContext,
						Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
					// Camera permission granted
					selectImage();
				} else {
					
					if (ButtonClick.equals("")) {
						ButtonClick = "1";
						CheakPermissions();
					} else if (ButtonClick.equals("1")) {
						ButtonClick = "2";
						AccessCamera();
					} else {
						AccessCamera();
					}
				}
				
				break;
				
			case R.id.back_btn:
				
				requireActivity().onBackPressed();
				
			break;
			
			case R.id.btn_SubmitProfile:
				if (TextUtils.isEmpty(Firstname.getText().toString().trim())) {
					Utility.ShowToast(mContext, getResources().getString(R.string.hint_usr));
				} else if (Lastname.getText().toString().isEmpty()) {
					Utility.ShowToast(mContext, getResources().getString(R.string.hint_usr2));
					
				} else if (CountryCode.getText().toString().isEmpty()) {
					Utility.ShowToast(mContext, getResources().getString(R.string.select_country_code));
					
				} else if (MobileNumber.getText().toString().isEmpty()) {
					Utility.ShowToast(mContext, getResources().getString(R.string.enter_phone_number));
					
				} else if (!Utility.isValidMobile(MobileNumber.getText().toString())) {
					Utility.ShowToast(mContext, getResources().getString(R.string.valid_phone1));
					
				} else {
				
				}
				break;
		}
	}
	
	private void showPicker() {
		com.mukesh.countrypicker.CountryPicker.Builder builder =
				new com.mukesh.countrypicker.CountryPicker.Builder().with(
						(AppCompatActivity) mContext).listener(this);
		builder.canSearch(true);
		builder.sortBy(sortBy);
		CountryPicker = builder.build();
		CountryPicker.showDialog((AppCompatActivity) mContext);
		
		
	}
	
	private void selectImage() {
		
		LayoutInflater inflater =
				(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.pick_img_layout, null);
		final AlertDialog.Builder builder =
				new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
		
		builder.setView(layout);
		builder.setCancelable(true);
		AlertDialog = builder.create();
		AlertDialog.setCanceledOnTouchOutside(true);
		AlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		AlertDialog.getWindow().setGravity(Gravity.BOTTOM);
		AlertDialog.show();
		
		LinearLayout Gallery = layout.findViewById(R.id.Gallery);
		LinearLayout Camera = layout.findViewById(R.id.Camera);
		
		Gallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.dismiss();
				galleryIntent();
			}
		});
		
		Camera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.dismiss();
				cameraIntent();
			}
		});
		
		
	}
	
	private void CheakPermissions() {
		if (!(ActivityCompat.checkSelfPermission(mContext,
				Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) || !(ActivityCompat.checkSelfPermission(
				mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
			ActivityCompat.requestPermissions(((AppCompatActivity) mContext),
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
		AlertDialog = builder.create();
		AlertDialog.setCanceledOnTouchOutside(false);
		AlertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
		
		TextView Cancle = layout.findViewById(R.id.btn_cancle);
		TextView btn_Ok = layout.findViewById(R.id.btn_OK);
		
		AlertDialog.show();
		
		Cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.dismiss();
			}
		});
		
		btn_Ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.dismiss();
				CheakPermissions();
			}
		});
	}
	
	private void galleryIntent() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);//
		startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_FILE);
	}
	
	private void cameraIntent() {
		
		ContentValues values = new ContentValues();
		ImageUri =
				mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						values);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
		startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			
			return;
		}
		Log.e(TAG, "onActivityResult: " + requestCode);
		
		switch (requestCode) {
			
			case PICK_FROM_FILE:
				try {
					Uri mImageCaptureUri = data.getData();
					String path = getPath(mContext, mImageCaptureUri); // From Gallery
					
					UserBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),
							mImageCaptureUri);
					
					if (path == null) {
						path = mImageCaptureUri.getPath(); // From File Manager
					}
					FileData = path;
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
								R.drawable.user_img).into(CompanionImg);
						
						
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("Error======>", e.toString());
				}
				break;
			
			case REQUEST_IMAGE_CAPTURE:
				try {
					Bitmap bitmap =
							MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),
							ImageUri);
					
					UserBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),
							ImageUri);
					
					String path = getPath(mContext, ImageUri); // From Gallery
					
					if (path == null) {
						path = data.getData().getPath(); // From File Manager
					}
					FileData = path;
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
								R.drawable.user_img).into(CompanionImg);
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					
					Log.e("Error======>", e.toString());
				}
				
				break;
			
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_profile, container, false);
		
		Init(view);
		listner();
		
		if (Utility.isNetworkConnected(mContext)) {
			getUser();
		} else {
			
			Utility.ShowToast(mContext,
					mContext.getResources().getString(R.string.net_connection));
		}
		
		return view;
	}
	
	private void Init(View view) {
		BackBtn = view.findViewById(R.id.back_btn);
		CompanionImg = view.findViewById(R.id.companion_img);
		Firstname = view.findViewById(R.id.txt_first_name);
		Lastname = view.findViewById(R.id.txt_last_name);
		Email = view.findViewById(R.id.email_address);
		CountryCode = view.findViewById(R.id.countryCodetxt);
		CountryLL = view.findViewById(R.id.CountryLL);
		MobileNumber = view.findViewById(R.id.mobile_number);
		UserName = view.findViewById(R.id.username);
		Password = view.findViewById(R.id.password_ext);
		view_pwd1 = view.findViewById(R.id.view_pwd1);
		ChangePassword = view.findViewById(R.id.changePassword);
		SaveChange = view.findViewById(R.id.btn_SubmitProfile);
	}
	
	private void listner() {
		CompanionImg.setOnClickListener(this);
		SaveChange.setOnClickListener(this);
		CountryLL.setOnClickListener(this);
		BackBtn.setOnClickListener(this);
		
		view_pwd1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked) {
					
					Password.setTransformationMethod(new PasswordTransformationMethod());
					
				} else {
					
					// hide password
					Password.setTransformationMethod(null);
				}
				
			}
		});
	}
	
	@Override
	public void onSelectCountry(Country country) {
		CountryCode.setText(country.getDialCode());
	}
	
	private void getUser() {
		JSONObject mainObject = new JSONObject();
		try {
			mainObject.put("user_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		showProgressDialog(mContext,getResources().getString(R.string.Loading));
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
				APIS.BASEURL + APIS.GetCompanionDetail, mainObject,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d(TAG, "response=" + response.toString());
						hideProgressDialog();
						try {
							UpdateProfileModel
									updateProfileModel = new Gson().fromJson(response.toString(), UpdateProfileModel.class);
							
							if (updateProfileModel.getStatusCode() == 200) {
								
								Firstname.setText(updateProfileModel.getResponse().getUserData().getName());
								Lastname.setText(updateProfileModel.getResponse().getUserData().getLastname());
								Email.setText(updateProfileModel.getResponse().getUserData().getEmail());
								UserName.setText(updateProfileModel.getResponse().getUserData().getUsername());
								MobileNumber.setText(updateProfileModel.getResponse().getUserData().getPhone());
								CountryCode.setText(updateProfileModel.getResponse().getUserData().getCountrycode());
								
								
								
								Glide.with(mContext).load(APIS.CaregiverImageURL+updateProfileModel.getResponse().getUserData().getProfileImage())
										.placeholder(R.drawable.user_img).into(CompanionImg);
								
							} else {
								String msg = response.getString("message");
								
								Utility.ShowToast(mContext, msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.d(TAG, "Error: " + error.getMessage());
				hideProgressDialog();
			}
		}) {
			@Override
			public Map<String, String> getHeaders() {
				Map<String, String> params = new HashMap<>();
				params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
				params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
				params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext,APIS.EncodeUser_id));
				
				return params;
			}
			
		};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjReq);
	}


}