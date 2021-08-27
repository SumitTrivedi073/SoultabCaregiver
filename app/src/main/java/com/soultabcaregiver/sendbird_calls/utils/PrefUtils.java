package com.soultabcaregiver.sendbird_calls.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static com.soultabcaregiver.WebService.APIS.SENDBIRD_APP_ID;

public class PrefUtils {
	
	public static SharedPreferences instance = null;
	
	public static void init(Context context) {
		if (instance == null) {
			instance = getSharedPreferences(context);
		}
	}
	
	private static final String PREF_NAME = "sendbird_calls";
	
	private static final String PREF_KEY_APP_ID = "app_id";
	private static final String PREF_KEY_USER_ID = "user_id";
	private static final String PREF_KEY_ACCESS_TOKEN = "access_token";
	private static final String PREF_KEY_CALLEE_ID = "callee_id";
	private static final String PREF_KEY_PUSH_TOKEN = "push_token";
	
	private static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void setAppId(Context context, String appId) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_KEY_APP_ID, appId).apply();
    }

    public static String getAppId(Context context) {
        return getSharedPreferences(context).getString(PREF_KEY_APP_ID, SENDBIRD_APP_ID);
    }
	
	public static void setUserId(Context context, String userId) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putString(PREF_KEY_USER_ID, userId).apply();
	}
	
	public static String getUserId(Context context) {
		return getSharedPreferences(context).getString(PREF_KEY_USER_ID, "");
	}
	
	//    public static void setAccessToken(Context context, String accessToken) {
	//        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
	//        editor.putString(PREF_KEY_ACCESS_TOKEN, accessToken).apply();
	//    }
	//
	//    public static String getAccessToken(Context context) {
	//        return getSharedPreferences(context).getString(PREF_KEY_ACCESS_TOKEN, "");
	//    }
	
	public static String getPushToken() {
		return instance.getString(PREF_KEY_PUSH_TOKEN, "");
	}
	
	public static void setPushToken(String pushToken) {
		SharedPreferences.Editor editor = instance.edit();
		editor.putString(PREF_KEY_PUSH_TOKEN, pushToken).apply();
	}
}
