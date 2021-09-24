package com.soultabcaregiver.companion;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.sendbird.android.SendBird;
import com.sendbird.calls.DirectCallLog;
import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.alert.fragment.AlertFragment;
import com.soultabcaregiver.activity.alert.model.AlertCountModel;
import com.soultabcaregiver.companion.fragment.ProfileFragment;
import com.soultabcaregiver.sendbird_calls.utils.BroadcastUtils;
import com.soultabcaregiver.talk.TalkHolderFragment;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.soultabcaregiver.sendbird_chat.ConversationFragment.EXTRA_GROUP_CHANNEL_URL;

public class CompanionMainActivity extends BaseActivity {
	
	public static CompanionMainActivity instance;
	
	private final String TAG = getClass().getSimpleName();
	
	Context mContext;
	
	boolean isLocationEnabled;
	
	AlertFragment alertFragment;
	
	private BroadcastReceiver receiver;
	
	private BroadcastReceiver mReceiver;
	
	private RelativeLayout companionDetailLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_companion_main);
		
		mContext = this;
		instance = CompanionMainActivity.this;
		
		//clear all notification
		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
		
		registerReceiver();
		
		int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
		if (resultCode == ConnectionResult.SUCCESS) {
			
			FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
					instanceIdResult -> {
						// Get new Instance ID token
						String FirebaseToken = instanceIdResult.getToken();
						Log.e("newToken", FirebaseToken);
						
					}).addOnFailureListener(e -> e.printStackTrace());
		}
		
		setupUI();
		
		if (getIntent().hasExtra(EXTRA_GROUP_CHANNEL_URL)) {
			checkForCurrentScreen(getIntent().getStringExtra(EXTRA_GROUP_CHANNEL_URL));
		} else {
			loadTalkHolderFragment(null);
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
					try {
						isLocationEnabled = Utility.isLocationEnabled(getApplicationContext());
						if (!isLocationEnabled) {
							Utility.buildAlertMessageNoGps(CompanionMainActivity.this);
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
		LocalBroadcastManager.getInstance(CompanionMainActivity.this).unregisterReceiver(receiver);
		unregisterReceiver();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.hasExtra(EXTRA_GROUP_CHANNEL_URL)) {
			checkForCurrentScreen(intent.getStringExtra(EXTRA_GROUP_CHANNEL_URL));
		} else {
			super.onNewIntent(intent);
		}
	}
	
	public static CompanionMainActivity getInstance() {
		return instance;
	}
	
	public void hideCompanionDetailLayout() {
		companionDetailLayout.setVisibility(View.GONE);
	}
	
	public void showCompanionDetailLayout() {
		companionDetailLayout.setVisibility(View.VISIBLE);
	}
	
	@SuppressLint ("SetTextI18n")
	private void setupUI() {
		
		companionDetailLayout = findViewById(R.id.companionDetailsLayout);
		
		TextView goodMorningText = findViewById(R.id.good_morning_txt);
		TextView companionNameText = findViewById(R.id.user_name_txt);
		CircleImageView profilePic = findViewById(R.id.profilePic);
		
		profilePic.setOnClickListener(
				v -> Utility.loadFragment(CompanionMainActivity.this, new ProfileFragment(), true,
						ProfileFragment.class.getSimpleName()));
		
		Glide.with(this).load(Utility.getSharedPreferences(this, APIS.profile_image)).
				placeholder(R.drawable.user_img).into(profilePic);
		
		companionNameText.setText(Utility.getSharedPreferences(this,
				APIS.Caregiver_name) + " " + Utility.getSharedPreferences(mContext,
				APIS.Caregiver_lastname));
		
		Calendar calendar = Calendar.getInstance();
		int mDay = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, mDay);
		int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
		if (timeOfDay < 12) {
			goodMorningText.setText(getResources().getString(R.string.good_morning));
		} else if (timeOfDay < 16) {
			goodMorningText.setText(getResources().getString(R.string.good_afternoon));
		} else if (timeOfDay < 21) {
			goodMorningText.setText(getResources().getString(R.string.good_evening));
		} else {
			goodMorningText.setText(getResources().getString(R.string.good_evening));
		}
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
				
				}
			}
		};
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BroadcastUtils.INTENT_ACTION_ADD_CALL_LOG);
		registerReceiver(mReceiver, intentFilter);
	}
	
	private void checkForCurrentScreen(String channelUrl) {
		loadTalkHolderFragment(channelUrl);
	}
	
	private void unregisterReceiver() {
		Log.i(TAG, "[MainActivity] unregisterReceiver()");
		
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}
	
	private void loadTalkHolderFragment(String channelUrl) {
		Utility.loadFragment(CompanionMainActivity.this,
				TalkHolderFragment.newInstance(channelUrl),
				false, null);
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
	}
	
}
