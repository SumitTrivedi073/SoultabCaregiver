package com.soultabcaregiver.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;
import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.login_module.LoginActivity;
import com.soultabcaregiver.activity.main_screen.MainActivity;
import com.soultabcaregiver.companion.CompanionMainActivity;
import com.soultabcaregiver.sendbird_calls.SendBirdAuthentication;
import com.soultabcaregiver.sendbird_chat.ConversationFragment;
import com.soultabcaregiver.utils.Utility;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class SplashActivity extends BaseActivity {
	
	private static final int REQUEST_CODE_PERMISSION = 2;
	
	public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 2323;
	
	Context mContext;
	
	String User_id;
	
	private long back_pressed;
	RelativeLayout mlayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mContext = this;
		
		mlayout = findViewById(R.id.mlayout);
		User_id = Utility.getSharedPreferences(mContext, APIS.user_id);
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		int TIME_DELAY = 2000;
		if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
			super.onBackPressed();
			finish();
		} else {
			
			//      Support.ShowToast(this, getResources().getString(R.string.press_Again));
		}
		back_pressed = System.currentTimeMillis();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (Settings.canDrawOverlays(mContext)) {
					changeScreen();
				}
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		final LocationManager manager =
				(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		boolean gps_enabled = false;
		boolean network_enabled = false;
		
		try {
			gps_enabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		
		try {
			network_enabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}
		if (!gps_enabled && !network_enabled) {
			Utility.buildAlertMessageNoGps(mContext);
		} else {
			
			checkPermissions();
		}
	}
	
	public void checkPermissions() {
		if (!(ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) || !(ActivityCompat.checkSelfPermission(
				this,
				Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) || !(ActivityCompat.checkSelfPermission(
				this,
				Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) || !(ActivityCompat.checkSelfPermission(
				this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) || !(ActivityCompat.checkSelfPermission(
				this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
							Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA
							, Manifest.permission.WRITE_EXTERNAL_STORAGE,
							Manifest.permission.RECORD_AUDIO},
					REQUEST_CODE_PERMISSION);
			
			
		} else {
			changeScreen();
			//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings
			//			.canDrawOverlays(
			//					mContext)) {
			//				requestPermission();
			//			} else {
			
			//			}
		}
	}
	
	private void changeScreen() {
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				if (TextUtils.isEmpty(User_id)) {
					Intent intent = new Intent(mContext, LoginActivity.class);
					startActivity(intent);
					finish();
				} else {
					
					if (Utility.isNetworkConnected(mContext)) {
						SendBirdAuthentication.autoAuthenticate(mContext, userId -> {
							if (userId == null) {
								Utility.ShowToast(mContext, "Sendbird Auth Failed");
							}
							if (!TextUtils.isEmpty(Utility.getSharedPreferences(mContext, APIS.is_companion))) {
								
								if (Utility.getSharedPreferences(mContext, APIS.is_companion).equals("0")) {
									
									Intent intent = new Intent(mContext, MainActivity.class);
									if (getIntent().hasExtra(ConversationFragment.EXTRA_GROUP_CHANNEL_URL)) {
										intent.putExtra(ConversationFragment.EXTRA_GROUP_CHANNEL_URL,
												getIntent().getExtras().getString(
														ConversationFragment.EXTRA_GROUP_CHANNEL_URL));
									}
									startActivity(intent);
									finish();
								} else {
									Intent intent = new Intent(mContext, CompanionMainActivity.class);
									if (getIntent().hasExtra(ConversationFragment.EXTRA_GROUP_CHANNEL_URL)) {
										intent.putExtra(ConversationFragment.EXTRA_GROUP_CHANNEL_URL,
												getIntent().getExtras().getString(
														ConversationFragment.EXTRA_GROUP_CHANNEL_URL));
									}
									startActivity(intent);
									finish();
								}
							}
							
						});
					}else {
						Snackbar.make(mlayout, R.string.net_connection,
								Snackbar.LENGTH_INDEFINITE).setAction(R.string.OK,
								new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										// Request the permission
										if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
											Intent panelIntent = new
													Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
											startActivityForResult(panelIntent, 0);
										} else {
											// for previous android version
											WifiManager wifiManager = (WifiManager)
													getApplicationContext().getSystemService(WIFI_SERVICE);
											wifiManager.setWifiEnabled(true);
										}
									}
								}).show();
					}
				}
			}
			
		}, 3000);
	}
	
	@RequiresApi (api = Build.VERSION_CODES.M)
	private void requestPermission() {
		
		String manufacturer = "xiaomi";
		if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
				intent.setClassName("com.miui.securitycenter",
						"com.miui.permcenter.permissions.PermissionsEditorActivity");
				intent.putExtra("extra_pkgname", getPackageName());
				startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
			}
		} else {
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
					Uri.parse("package:" + getPackageName()));
			startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
		}
	}
}