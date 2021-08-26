package com.soultabcaregiver.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.sendbird.android.SendBird;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.handler.DirectCallListener;
import com.sendbird.calls.handler.SendBirdCallListener;
import com.soultabcaregiver.FireBaseMessaging.CustomFireBaseMessaging;
import com.soultabcaregiver.R;
import com.soultabcaregiver.sendbird_calls.SendbirdCallService;
import com.soultabcaregiver.sendbird_calls.utils.BroadcastUtils;
import com.soultabcaregiver.sendbird_calls.utils.PrefUtils;
import com.soultabcaregiver.sendbird_chat.utils.PushUtils;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.UUID;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import static com.soultabcaregiver.WebService.APIS.SENDBIRD_APP_ID;

public class AppController extends MultiDexApplication {
	
	private static AppController mInstance;
	
	private static Context mContext;
	
	public static final String VERSION = "1.4.0";
	
	
	public static final String TAG = AppController.class.getSimpleName();
	
	private RequestQueue mRequestQueue;
	
	private ImageLoader mImageLoader;
	
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		PrefUtils.init(this);
		EmojiManager.install(new GoogleEmojiProvider());
		SendBird.init(SENDBIRD_APP_ID, getApplicationContext());
		initSendBirdCall(SENDBIRD_APP_ID);
		PushUtils.registerPushHandler(new CustomFireBaseMessaging());
	}


	public boolean initSendBirdCall(String appId) {
		Log.i(TAG, "[BaseApplication] initSendBirdCall(appId: " + appId + ")");
		Context context = getApplicationContext();

		if (TextUtils.isEmpty(appId)) {
			appId = SENDBIRD_APP_ID;
		}

		if (SendBirdCall.init(context, appId)) {
			SendBirdCall.removeAllListeners();
			SendBirdCall.addListener(UUID.randomUUID().toString(), new SendBirdCallListener() {
				@Override
				public void onRinging(DirectCall call) {
					int ongoingCallCount = SendBirdCall.getOngoingCallCount();
					Log.i(TAG, "[BaseApplication] onRinging() => callId: " + call.getCallId() + ", getOngoingCallCount(): " + ongoingCallCount);

					if (ongoingCallCount >= 2) {
						call.end();
						return;
					}

					call.setListener(new DirectCallListener() {
						@Override
						public void onConnected(DirectCall call) {
						}

						@Override
						public void onEnded(DirectCall call) {
							int ongoingCallCount = SendBirdCall.getOngoingCallCount();
							Log.i(TAG, "[BaseApplication] onEnded() => callId: " + call.getCallId() + ", getOngoingCallCount(): " + ongoingCallCount);


							BroadcastUtils.sendCallLogBroadcast(context, call.getCallLog());

							if (ongoingCallCount == 0) {
								SendbirdCallService.stopService(context);
							}
						}
					});

					SendbirdCallService.onRinging(context, call);
				}
			});

			SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.DIALING,
					R.raw.dialing);
			SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RINGING,
					R.raw.ringing);
			SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTING,
					R.raw.reconnecting);
			SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTED,
					R.raw.reconnected);
			return true;
		}
		return false;
	}
	public static Context getContext() {
		return mContext;
	}
	
	public static synchronized AppController getInstance() {
		return mInstance;
	}
	
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}
	
	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		
		return mRequestQueue;
	}
	
	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}
	
	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
	
	
}