package com.soultabcaregiver.activity.login_module;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.Model.LoginModel;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.WebService.ApiTokenAuthentication;
import com.soultabcaregiver.activity.main_screen.MainActivity;
import com.soultabcaregiver.companion.CompanionMainActivity;
import com.soultabcaregiver.sendbird_calls.SendBirdAuthentication;
import com.soultabcaregiver.sendbird_calls.utils.PrefUtils;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

import static com.soultabcaregiver.utils.Utility.ShowToast;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
	
	private static final int IMMEDIATE_APP_UPDATE_REQ_CODE = 124;
	
	private final String TAG = getClass().getSimpleName();
	
	Context mContext;
	
	TextView tvLogin, tvForgot, tv_rem_pass;
	
	EditText etEmail, etPass;
	
	Switch tbRemPass;
	
	CheckBox view_pwd1;
	
	String FirebaseToken = "";
	
	AlertDialog alertDialog;
	
	private AppUpdateManager appUpdateManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		mContext = this;
		
		init();
		Listener();
		
	}
	
	private void init() {
		tvLogin = findViewById(R.id.tv_login);
		tvForgot = findViewById(R.id.tv_forgot_pass);
		etEmail = findViewById(R.id.et_email);
		etPass = findViewById(R.id.et_pass);
		tbRemPass = findViewById(R.id.tb_rem);
		view_pwd1 = findViewById(R.id.view_pwd1);
		tv_rem_pass = findViewById(R.id.tv_rem_pass);
		
		FirebaseApp.initializeApp(this);
		if (Utility.getSharedPreferences2(mContext, APIS.save_email) != null) {
			if (Utility.getSharedPreferences2(mContext, APIS.save_email).equals("true")) {
				etEmail.setText(Utility.getSharedPreferences2(mContext, APIS.Caregiver_email));
				tbRemPass.setChecked(true);
			} else {
				tbRemPass.setChecked(false);
				
			}
		}
		
		int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
		if (resultCode == ConnectionResult.SUCCESS) {
			
			FirebaseMessaging.getInstance().getToken().addOnCompleteListener(
					new OnCompleteListener<String>() {
						@Override
						public void onComplete(
								@NonNull com.google.android.gms.tasks.Task<String> task) {
							if (!task.isSuccessful()) {
								Log.w(TAG, "Fetching FCM registration token failed",
										task.getException());
								return;
							}
							
							// Get new FCM registration token
							FirebaseToken = task.getResult();
							Log.e("newToken", FirebaseToken);
							PrefUtils.setPushToken(FirebaseToken);
							SendBirdAuthentication.registerPushToken(FirebaseToken, e -> {
							
							});
						}
						
					});
		}
	}
	
	private void Listener() {
		tvLogin.setOnClickListener(this);
		tvForgot.setOnClickListener(this);
		tv_rem_pass.setOnClickListener(this);
		
		view_pwd1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked) {
					
					etPass.setTransformationMethod(new PasswordTransformationMethod());
					
				} else {
					
					// hide password
					etPass.setTransformationMethod(null);
				}
				
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
			if (resultCode == RESULT_CANCELED) {
				Utility.ShowToast(mContext, "Application update is required");
			} else if (resultCode == RESULT_OK) {
				Utility.ShowToast(mContext, "App update success");
			} else {
				checkUpdate();
			}
			//In app Update link
			//https://www.section.io/engineering-education/android-application-in-app-update-using
			// -android-studio/
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
		checkUpdate();
	}
	
	private void checkUpdate() {
		
		Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
		
		appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
			if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
					AppUpdateType.IMMEDIATE)) {
				startUpdateFlow(appUpdateInfo);
			} else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
				startUpdateFlow(appUpdateInfo);
			}
		});
	}
	
	private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
		try {
			appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this,
					IMMEDIATE_APP_UPDATE_REQ_CODE);
		} catch (IntentSender.SendIntentException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
			
			case R.id.tv_rem_pass:
				tbRemPass.setChecked(!tbRemPass.isChecked());
				break;
			case R.id.tv_forgot_pass:
				startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
				break;
			
			case R.id.tv_login:
				Login();
				break;
			
		}
		
	}
	
	private void Login() {
		if (Utility.isNetworkConnected(LoginActivity.this)) {
			if (etEmail.getText().toString().trim().isEmpty()) {
				Utility.ShowToast(mContext, getResources().getString(R.string.valid_email));
			} else if (etPass.getText().toString().trim().isEmpty()) {
				Utility.ShowToast(mContext, getResources().getString(R.string.valid_pass));
			} else if (!Utility.isvalidatePassword(etPass.getText().toString().trim())) {
				Utility.ShowToast(mContext, getResources().getString(R.string.password_not_valid));
			} else if (etPass.getText().toString().trim().length() < 8) {
				Utility.ShowToast(mContext, getResources().getString(R.string.password_notvalid));
			} else {
				GetLogin();
			}
		} else {
			Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
		}
	}
	
	private void GetLogin() {
		JSONObject mainObject = new JSONObject();
		
		try {
			mainObject.put("email", etEmail.getText().toString().trim());
			mainObject.put("password", etPass.getText().toString().trim());
			mainObject.put("device_type", "android");
			mainObject.put("user_roll", 4);
			mainObject.put("device_token", FirebaseToken);
			
			Log.e("Login_mainObject", String.valueOf(mainObject));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		System.out.println(mainObject.toString());
		showProgressDialog(getResources().getString(R.string.Loading));
		JsonObjectRequest jsonObjReq =
				new JsonObjectRequest(Request.Method.POST, APIS.BASEURL + APIS.LOGINAPI,
						mainObject,
						new Response.Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								
								Log.e("response", response.toString());
								LoginModel loginModel =
										new Gson().fromJson(response.toString(), LoginModel.class);
								
								if (String.valueOf(loginModel.getStatusCode()).equals("200")) {
									
									if (tbRemPass.isChecked()) {
										Utility.setSharedPreference2(mContext,
												APIS.Caregiver_email,
												loginModel.getResponse().getEmail());
										Utility.setSharedPreference2(mContext, APIS.save_email,
												"true");
										
									} else {
										Utility.clearSpecificSharedPreference(mContext,
												APIS.Caregiver_email);
										Utility.clearSpecificSharedPreference(mContext,
												APIS.save_email);
									}
									
									String encodeValue = Base64.encodeToString(
											loginModel.getResponse().getCaregiver_id().getBytes(),
											Base64.NO_WRAP);
									
									Utility.setSharedPreference(mContext, APIS.EncodeUser_id,
											encodeValue);
									
									Utility.setSharedPreference(mContext, APIS.user_id,
											loginModel.getResponse().getId());
									Utility.setSharedPreference(mContext, APIS.caregiver_id,
											loginModel.getResponse().getCaregiver_id());
									
									Utility.setSharedPreference(mContext, APIS.is_companion,
											loginModel.getResponse().getIs_companion());
									
									Utility.setSharedPreference(mContext, APIS.Caregiver_name,
											loginModel.getResponse().getName());
									Utility.setSharedPreference(mContext, APIS.Caregiver_lastname,
											loginModel.getResponse().getLastname());
									Utility.setSharedPreference(mContext, APIS.Caregiver_email,
											loginModel.getResponse().getEmail());
									Utility.setSharedPreference(mContext, APIS.user_email,
											loginModel.getResponse().getUser_email_address());
									
									Utility.setSharedPreference(mContext, APIS.user_name,
											loginModel.getResponse().getParent_user());
									
									Utility.setSharedPreference(mContext, APIS.Caregiver_mobile,
											loginModel.getResponse().getMobile());
									Utility.setSharedPreference(mContext, APIS.profile_image,
											loginModel.getResponse().getProfileImage());
									
									Utility.setSharedPreference(mContext, APIS.is_40plus_user,
											loginModel.getResponse().getIs_40plus_user());
									Utility.setSharedPreference(mContext, APIS.is_40plus_userID,
											loginModel.getResponse().getPlus40_userId());
									Utility.setSharedPreference(mContext,
											APIS.Caregiver_countrycode,
											loginModel.getResponse().getCountrycode());
									Utility.setSharedPreference(mContext, APIS.Caregiver_username,
											loginModel.getResponse().getCaregiver_username());
									
									Utility.setSharedPreference(mContext,
											APIS.dashbooard_hide_Show,
											loginModel.getResponse().getPermission().getDashboardNew());
									
									Utility.setSharedPreference(mContext, APIS.doctor_hide_show,
											loginModel.getResponse().getPermission().getAppointmentList());
									
									Utility.setSharedPreference(mContext,
											APIS.dailyroutine_hideshow,
											loginModel.getResponse().getPermission().getDailyroutine());
									
									Utility.setSharedPreference(mContext, APIS.calender_hideshow,
											loginModel.getResponse().getPermission().getShowActivities());
									
									Utility.setSharedPreference(mContext, APIS.APITokenValue,
											loginModel.getResponse().getJwt_token());
									
									Utility.setSharedPreference(mContext, APIS.RefressTokenValue,
											loginModel.getResponse().getRefresh_token());
									
									if (loginModel.getResponse().getIsSendBirdUser().equals("0")) {
										updateSendBirdFlag();
									} else {
										authenticateInSendBird();
									}
								} else {
									hideProgressDialog();
									ShowAlertResponse(loginModel.getMessage(), "0");
									
								}
								
							}
						}, new Response.ErrorListener() {
					
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.d(TAG, "Error: " + error.getMessage());
						error.printStackTrace();
						hideProgressDialog();
					}
				}) {
					@Override
					public Map<String, String> getHeaders() throws AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();
						params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
						params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
						return params;
					}
					
				};
		AppController.getInstance().addToRequestQueue(jsonObjReq);
	}
	
	private void updateSendBirdFlag() {
		JSONObject mainObject = new JSONObject();
		try {
			mainObject.put("user_id", Utility.getSharedPreferences(this, APIS.caregiver_id));
			mainObject.put("is_sendbird_user", "1");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
				APIS.BASEURL + APIS.update_caregiver_flags, mainObject, response -> {
			authenticateInSendBird();
		}, error -> {
			VolleyLog.d(TAG, "Error: " + error.getMessage());
			hideProgressDialog();
			if (error.networkResponse!=null) {
				if (String.valueOf(error.networkResponse.statusCode).equals(APIS.APITokenErrorCode)||String.valueOf(error.networkResponse.statusCode).equals(APIS.APITokenErrorCode2)) {
					ApiTokenAuthentication.refrehToken(mContext, updatedToken -> {
						if (updatedToken == null) {
						} else {
							updateSendBirdFlag();
							
						}
					});
				}else {
					Utility.ShowToast(
							mContext,
							getString(R.string.something_went_wrong));
				}
			}
		}) {
			@Override
			public Map<String, String> getHeaders() {
				Map<String, String> params = new HashMap<String, String>();
				params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
				params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
				params.put(APIS.APITokenKEY,
						Utility.getSharedPreferences(mContext, APIS.APITokenValue));
				
				return params;
			}
			
		};
		AppController.getInstance().addToRequestQueue(jsonObjReq);
		jsonObjReq.setShouldCache(false);
		jsonObjReq.setRetryPolicy(
				new DefaultRetryPolicy(10000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}
	
	private void authenticateInSendBird() {
		SendBirdAuthentication.authenticate(mContext,
				Utility.getSharedPreferences(mContext, APIS.caregiver_id),
				Utility.getSharedPreferences(mContext, APIS.Caregiver_name),
				Utility.getSharedPreferences(mContext, APIS.profile_image), isSuccess -> {
					hideProgressDialog();
					if (isSuccess) {
						
						if (Utility.getSharedPreferences(mContext, APIS.is_companion).equals("0")) {
							Intent intent = new Intent(mContext, MainActivity.class);
							startActivity(intent);
							finishAffinity();
						} else {
							Intent intent = new Intent(mContext, CompanionMainActivity.class);
							startActivity(intent);
							finishAffinity();
						}
						//	ShowAlertResponse(loginModel.getMessage(), "1");
					} else {
						ShowToast(mContext, "Sendbird Auth " + "Failed");
					}
				});
	}
	
	private void ShowAlertResponse(String message, String value) {
		LayoutInflater inflater =
				(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.send_successfully_layout, null);
		final AlertDialog.Builder builder =
				new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
		
		builder.setView(layout);
		builder.setCancelable(false);
		alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		alertDialog.show();
		
		TextView OK_txt = layout.findViewById(R.id.OK_txt);
		TextView title_txt = layout.findViewById(R.id.title_txt);
		
		title_txt.setText(message);
		
		OK_txt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (value.equals("1")) {
					
					Intent intent = new Intent(mContext, MainActivity.class);
					startActivity(intent);
					finishAffinity();
				}
				
				alertDialog.dismiss();
				
			}
		});
		
	}
	
}


