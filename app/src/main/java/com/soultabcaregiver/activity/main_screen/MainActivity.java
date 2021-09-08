package com.soultabcaregiver.activity.main_screen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.sendbird.android.SendBird;
import com.sendbird.calls.DirectCallLog;
import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.alert.fragment.AlertFragment;
import com.soultabcaregiver.activity.alert.model.AlertCountModel;
import com.soultabcaregiver.activity.calender.fragment.CalenderFragment;
import com.soultabcaregiver.activity.daily_routine.fragment.DailyRoutineFragment;
import com.soultabcaregiver.activity.docter.fragment.DoctorFragment;
import com.soultabcaregiver.activity.login_module.LoginActivity;
import com.soultabcaregiver.activity.main_screen.fragment.DashBoardFragment;
import com.soultabcaregiver.activity.shopping.ShoppingCategoryActivity;
import com.soultabcaregiver.sendbird_calls.SendbirdCallService;
import com.soultabcaregiver.sendbird_calls.utils.BroadcastUtils;
import com.soultabcaregiver.talk.TalkFragment;
import com.soultabcaregiver.talk.TalkHolderFragment;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.soultabcaregiver.sendbird_chat.ConversationFragment.EXTRA_GROUP_CHANNEL_URL;

public class MainActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
                                                          GoogleApiClient.OnConnectionFailedListener {
	
	private static final int REQUEST_CODE_PERMISSION = 2;
	
	private static final int IMMEDIATE_APP_UPDATE_REQ_CODE = 124;
	
	public static MainActivity instance;
	
	private final String TAG = getClass().getSimpleName();
	
	Context mContext;
	
	FloatingActionButton video_call, shopping_btn;
	
	Location mLastLocation;
	
	boolean isLocationEnabled;
	
	double lat, lon;
	
	LatLng loc;
	
	BottomNavigationView navigationView;
	
	BottomNavigationItemView itemView;
	
	View badge;
	
	TextView tv_badge;
	
	AlertFragment alertFragment;
	
	TalkFragment talkFragment;
	
	private BroadcastReceiver receiver;
	
	private String CityZipCode;
	
	private GoogleApiClient googleApiClient;
	
	private BroadcastReceiver mReceiver;
	
	private Timer tmrStartEng;
	
	private AppUpdateManager appUpdateManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mContext = this;
		buildGoogleApiClient();
		
		instance = MainActivity.this;
		
		navigationView = findViewById(R.id.bottom_navigation);
		video_call = findViewById(R.id.video_call);
		shopping_btn = findViewById(R.id.shopping_btn);
		BottomNavigationViewHelper.removeShiftMode(navigationView);
		
		BottomNavigationMenuView bottomNavigationMenuView =
				(BottomNavigationMenuView) navigationView.getChildAt(0);
		View v = bottomNavigationMenuView.getChildAt(1);
		itemView = (BottomNavigationItemView) v;
		
		badge = LayoutInflater.from(this).inflate(R.layout.homescreen_count,
				bottomNavigationMenuView, false);
		tv_badge = badge.findViewById(R.id.notification_badge);
		
		registerReceiver();
		Alert_countAPI();
		listner();
		
		int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
		if (resultCode == ConnectionResult.SUCCESS) {
			
			FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
					new OnSuccessListener<InstanceIdResult>() {
						@Override
						public void onSuccess(InstanceIdResult instanceIdResult) {
							// Get new Instance ID token
							String FirebaseToken = instanceIdResult.getToken();
							Log.e("newToken", FirebaseToken);
							
						}
					}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					e.printStackTrace();
				}
			});
		}
		
		if (getIntent().hasExtra(EXTRA_GROUP_CHANNEL_URL)) {
			checkForCurrentScreen(getIntent().getStringExtra(EXTRA_GROUP_CHANNEL_URL));
		}
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, final Intent intent) {
				
				String action = intent.getAction();
				
				if (action.matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
					LocationManager lm =
							(LocationManager) getApplicationContext().getSystemService(
							Context.LOCATION_SERVICE);
					
					try {
						isLocationEnabled = Utility.isLocationEnabled(getApplicationContext());
						if (!isLocationEnabled) {
							Utility.buildAlertMessageNoGps(MainActivity.this);
						}
					} catch (Exception ignored) {
					}
				}
			}
		};
		
		// register events
		getApplicationContext().registerReceiver(receiver,
				new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
		
		
	}
	
	@Override
	public void onStop() {
		super.onStop();
		LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(receiver);
		if (null != tmrStartEng) {
			tmrStartEng.cancel();
			tmrStartEng = null;
			Log.e("Timer", "Stop");
			
		}
		unregisterReceiver();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (null != tmrStartEng) {
			tmrStartEng.cancel();
			tmrStartEng = null;
			
			Log.e("Timer", "Destroy");
			
		}
		unregisterReceiver();
	}
	
	@RequiresApi (api = Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onConnected(@Nullable Bundle bundle) {
		if (ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
				this,
				Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
		if (mLastLocation != null) {
			
			if (loc == null) {
				lat = mLastLocation.getLatitude();
				lon = mLastLocation.getLongitude();
				
				loc = new LatLng(lat, lon);
				
				Geocoder geocoder = new Geocoder(MainActivity.this);
				
				try {
					List<Address> addressList =
							geocoder.getFromLocation(mLastLocation.getLatitude(),
									mLastLocation.getLongitude(), 1);
					if (addressList != null && addressList.size() > 0) {
						
						if (addressList.get(0).getAddressLine(0) != null && addressList.get(
								0).getCountryName() != null) {
							String locality = addressList.get(0).getAddressLine(0);
							String country = addressList.get(0).getCountryName();
							if (!locality.isEmpty() && !country.isEmpty())
								CityZipCode = addressList.get(0).getPostalCode();
							
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		
		
	}
	
	@Override
	public void onConnectionSuspended(int i) {
	
	}
	
	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
			if (resultCode == RESULT_CANCELED) {
				//	Toast.makeText(getApplicationContext(),"Update canceled by user! Result Code:
				//	" + resultCode, Toast.LENGTH_LONG).show();
				Utility.ShowToast(mContext, "please update and enjoy the app");
			} else if (resultCode == RESULT_OK) {
				Utility.ShowToast(mContext, "App update success");
			} else {
				//Toast.makeText(getApplicationContext(),"Update Failed! Result Code: " +
				// resultCode, Toast.LENGTH_LONG).show();
				checkUpdate();
			}
			//In app Update link
			//https://www.section.io/engineering-education/android-application-in-app-update-using
			// -android-studio/
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (null != tmrStartEng) {
			tmrStartEng.cancel();
			tmrStartEng = null;
			Log.e("Timer", "Pause");
			
		}
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.hasExtra(EXTRA_GROUP_CHANNEL_URL)) {
			checkForCurrentScreen(intent.getStringExtra(EXTRA_GROUP_CHANNEL_URL));
		} else {
			super.onNewIntent(intent);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//   new ReminderCreateClass(MainActivity.this);
		appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
		checkUpdate();
	}
	
	public static MainActivity getInstance() {
		return instance;
	}
	
	private void buildGoogleApiClient() {
		
		googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(
				this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
		googleApiClient.connect();
	}
	
	private void registerReceiver() {
		Log.i(TAG, "[MainActivity] registerReceiver()");
		
		if (mReceiver != null) {
			return;
		}
		
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.i(TAG, "[MainActivity] onReceive()");
				
				DirectCallLog callLog = (DirectCallLog) intent.getSerializableExtra(
						BroadcastUtils.INTENT_EXTRA_CALL_LOG);
				if (callLog != null) {
					/*HistoryFragment historyFragment = (HistoryFragment) mMainPagerAdapter
					.getItem(1);
					historyFragment.addLatestCallLog(callLog);*/
				}
			}
		};
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BroadcastUtils.INTENT_ACTION_ADD_CALL_LOG);
		registerReceiver(mReceiver, intentFilter);
	}
	
	public void Alert_countAPI() {
		
		JSONObject mainObject = new JSONObject();
		try {
			mainObject.put("user_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));
			
			Log.e(TAG, "AlertCount API========>" + mainObject.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			
		}
		
		JsonObjectRequest jsonObjReq =
				new JsonObjectRequest(Request.Method.POST, APIS.BASEURL + APIS.AlertCount,
						mainObject, response -> {
					Log.e(TAG, "AlertCount response=" + response.toString());
					hideProgressDialog();
					
					AlertCountModel alertCountModel =
							new Gson().fromJson(response.toString(), AlertCountModel.class);
					
					if (String.valueOf(alertCountModel.getStatusCode()).equals("200")) {
						
						alertFragment = AlertFragment.instance;
						if (alertFragment != null) {
							alertFragment.GetAlertList(mContext);
							
						}
						
						Utility.setSharedPreference(mContext, APIS.BadgeCount,
								String.valueOf(alertCountModel.getResponse().getUnreadCount()));
						updateBadgeCount();
						
						
					} else if (String.valueOf(alertCountModel.getStatusCode()).equals("403")) {
						logout_app(alertCountModel.getMessage());
					} else {
						Utility.ShowToast(mContext, alertCountModel.getMessage());
					}
					
				}, error -> {
					VolleyLog.d(TAG, "Error: " + error.getMessage());
					hideProgressDialog();
				}) {
					@Override
					public Map<String, String> getHeaders() {
						Map<String, String> params = new HashMap<>();
						params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
						params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
						params.put(APIS.HEADERKEY2,
								Utility.getSharedPreferences(mContext, APIS.EncodeUser_id));
						
						return params;
					}
					
				};
		AppController.getInstance().addToRequestQueue(jsonObjReq);
		jsonObjReq.setShouldCache(false);
		jsonObjReq.setRetryPolicy(
				new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		
		
	}
	
	public void updateBadgeCount() {
		int alertCount = Integer.parseInt(Utility.getSharedPreferences(mContext, APIS.BadgeCount));
		int unreadMessageCount = SendBird.getSubscribedTotalUnreadMessageCount();
		
		int totalMessageCount = alertCount + unreadMessageCount;
		itemView.removeView(badge);
		if (totalMessageCount > 9) {
			tv_badge.setText("9+");
		} else {
			tv_badge.setText(String.valueOf(totalMessageCount));
		}
		itemView.addView(badge);
		
	}
	
	public void updatebadge() {
		
		alertFragment = AlertFragment.instance;
		if (alertFragment != null) {
			alertFragment.AlertCountUpdate();
			
		}
		
		
	}
	
	private void checkForCurrentScreen(String channelUrl) {
		navigationView.setSelectedItemId(R.id.navigation_talk);
		//i commented this code because of this it's not reflected to conversation screen
		//it's reflected to chat list screen

		/*Fragment f1 = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if (f1 instanceof TalkHolderFragment) {
			TalkHolderFragment talkHolderFragment = (TalkHolderFragment) f1;
			Fragment f2 =
					talkHolderFragment.getChildFragmentManager().findFragmentById(R.id.container);
			talkHolderFragment.navigateToConversationFragment(channelUrl);
			
			
		} else {
			loadTalkHolderFragment(channelUrl);
		}*/
		loadTalkHolderFragment(channelUrl);
		
	}
	
	private void listner() {
		
		navigationView.setOnNavigationItemSelectedListener(
				new BottomNavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(@NonNull MenuItem item) {
						FragmentManager fm = getFragmentManager();
						switch (item.getItemId()) {
							
							case R.id.navigation_dashboard:
								video_call.setVisibility(View.VISIBLE);
								shopping_btn.setVisibility(View.VISIBLE);
								Utility.loadFragment(MainActivity.this, new DashBoardFragment(),
										false, null);
								
								return true;
							
							case R.id.navigation_appointment:
								
								video_call.setVisibility(View.GONE);
								shopping_btn.setVisibility(View.GONE);
								Utility.loadFragment(MainActivity.this, new DoctorFragment(),
										false,
										null);
								return true;
							
							case R.id.navigation_dailyroutine:
								
								video_call.setVisibility(View.GONE);
								shopping_btn.setVisibility(View.GONE);
								Utility.loadFragment(MainActivity.this, new DailyRoutineFragment(),
										false, null);
								
								break;
							case R.id.navigation_talk:
								loadTalkHolderFragment(null);
								
								return true;
							case R.id.navigation_calender:
								
								video_call.setVisibility(View.GONE);
								shopping_btn.setVisibility(View.GONE);
								Utility.loadFragment(MainActivity.this, new CalenderFragment(),
										false, null);
								break;
						}
						return true;
					}
					
				});
		
		navigationView.setSelectedItemId(R.id.navigation_dashboard);
		
		video_call.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Utility.isNetworkConnected(mContext)) {
					if (!(ActivityCompat.checkSelfPermission(MainActivity.this,
							Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) || !(ActivityCompat.checkSelfPermission(
							MainActivity.this,
							Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) || !(ActivityCompat.checkSelfPermission(
							MainActivity.this,
							Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) || !(ActivityCompat.checkSelfPermission(
							MainActivity.this,
							Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) || !(ActivityCompat.checkSelfPermission(
							MainActivity.this,
							Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
						ActivityCompat.requestPermissions(MainActivity.this,
								new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
										Manifest.permission.ACCESS_COARSE_LOCATION,
										Manifest.permission.CAMERA,
										Manifest.permission.WRITE_EXTERNAL_STORAGE,
										Manifest.permission.RECORD_AUDIO},
								REQUEST_CODE_PERMISSION);
						
						
					} else {
						openPlaceCallActivity();
					}
					
				} else {
					Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
				}
			}
		});
		
		shopping_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ShoppingCategoryActivity.class);
				startActivity(intent);
			}
		});
		
	}
	
	private void unregisterReceiver() {
		Log.i(TAG, "[MainActivity] unregisterReceiver()");
		
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
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
	
	private void loadTalkHolderFragment(String channelUrl) {
		video_call.setVisibility(View.GONE);
		shopping_btn.setVisibility(View.GONE);
		Utility.loadFragment(MainActivity.this, TalkHolderFragment.newInstance(channelUrl), false,
				null);
	}
	
	private void openPlaceCallActivity() {
		//		ArrayList<String> ids = new ArrayList<>();
		//		ids.add(Utility.getSharedPreferences(this, APIS.caregiver_id));
		//		ids.add(Utility.getSharedPreferences(this, APIS.user_id));
		//		ChatHelper.createGroupChannel(ids, true, groupChannel -> {
		//			Log.e("channel", "" + groupChannel.getUrl());
		//			Intent intent = new Intent(this, ConversationFragment.class);
		//			intent.putExtra(EXTRA_GROUP_CHANNEL_URL, groupChannel.getUrl());
		//			intent.putExtra(EXTRA_CALLEE_ID, Utility.getSharedPreferences(this, APIS
		//			.user_id));
		//			startActivity(intent);
		//		});
		SendbirdCallService.dial(this, Utility.getSharedPreferences(this, APIS.user_id),
				Utility.getSharedPreferences(this, APIS.user_name), true, false, null);
	}
	
	//to kill the current session of SinchService
	public void stopButtonClicked() {
		Intent intent = new Intent(mContext, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		finish();
	}
	
	private static class BottomNavigationViewHelper {
		
		@SuppressLint ("RestrictedApi")
		static void removeShiftMode(BottomNavigationView view) {
			BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
			try {
				Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
				shiftingMode.setAccessible(true);
				shiftingMode.setBoolean(menuView, false);
				shiftingMode.setAccessible(false);
				for (int i = 0; i < menuView.getChildCount(); i++) {
					BottomNavigationItemView item =
							(BottomNavigationItemView) menuView.getChildAt(i);
					item.setShifting(false);
					// set once again checked value, so view will be updated
					item.setChecked(item.getItemData().isChecked());
				}
			} catch (NoSuchFieldException e) {
				Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
			} catch (IllegalAccessException e) {
				Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
			}
		}
	}
	
}
