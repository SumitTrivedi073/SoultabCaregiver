package com.soultabcaregiver.sendbird_calls.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sendbird.calls.DirectCallLog;

public class BroadcastUtils {
	
	private static final String TAG = "BroadcastUtils";
	
	public static final String INTENT_ACTION_ADD_CALL_LOG =
			"com.soultab.caregiver.sendbird.intent.action.ADD_CALL_LOG";
	
	public static final String INTENT_ACTION_NEW_CHAT_MESSAGE =
			"com.soultab.caregiver.sendbird.intent.action.NEW_CHAT_MESSAGE";
	
	public static final String INTENT_EXTRA_CALL_LOG = "call_log";
	
	public static final String INTENT_EXTRA_CHAT_MESSAGE_BODY = "message_body";
	
	public static final String INTENT_EXTRA_CHAT_CHANNEL_URL = "channel_url";
	
	public static void sendCallLogBroadcast(Context context, DirectCallLog callLog) {
		if (context != null && callLog != null) {
			Log.i(TAG, "[BroadcastUtils] sendCallLogBroadcast()");
			
			Intent intent = new Intent(INTENT_ACTION_ADD_CALL_LOG);
			intent.putExtra(INTENT_EXTRA_CALL_LOG, callLog);
			context.sendBroadcast(intent);
		}
	}
	
	public static void sendNewMessageBroadCast(Context context, String messageBody,
	                                           String channelUrl) {
		Intent intent = new Intent(INTENT_ACTION_NEW_CHAT_MESSAGE);
		intent.putExtra(INTENT_EXTRA_CHAT_MESSAGE_BODY, messageBody);
		intent.putExtra(INTENT_EXTRA_CHAT_CHANNEL_URL, channelUrl);
		context.sendBroadcast(intent);
	}
}
