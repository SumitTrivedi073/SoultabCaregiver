package com.soultabcaregiver.sendbird_group_call;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.sendbird.calls.SendBirdCall;
import com.soultabcaregiver.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class SendBirdGroupCallService extends Service {
	
	private static final int NOTIFICATION_ID = 2;
	
	private static final String TAG = "GroupCallService";
	
	public static final String EXTRA_CALL_STATE = "call_state";
	
	public static final String EXTRA_GROUP_NAME = "group_name";
	
	public static final String EXTRA_ROOM_ID = "group_room_id";
	
	public static final String EXTRA_DO_DIAL = "do_dial";
	
	public static final String EXTRA_DO_ACCEPT = "do_accept";
	
	public static final String EXTRA_DO_END = "do_end";
	
	private final GroupCallData mServiceData = new GroupCallData();
	
	private final IBinder mBinder = new CallBinder();
	
	private Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "[CallService] onCreate()");
		
		mContext = this;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "[CallService] onStartCommand()");
		
		mServiceData.groupName = intent.getStringExtra(EXTRA_GROUP_NAME);
		mServiceData.callState = (STATE) intent.getSerializableExtra(EXTRA_CALL_STATE);
		mServiceData.doDial = intent.getBooleanExtra(EXTRA_DO_DIAL, false);
		mServiceData.doAccept = intent.getBooleanExtra(EXTRA_DO_ACCEPT, false);
		mServiceData.roomId = intent.getStringExtra(EXTRA_ROOM_ID);
		
		updateNotification(mServiceData);
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	private static Intent getCallActivityIntent(Context context, GroupCallData groupCallData,
	                                            boolean doEnd) {
		final Intent intent;
		
		intent = new Intent(context, GroupCallActivity.class);
		intent.putExtra(EXTRA_CALL_STATE, groupCallData.callState);
		intent.putExtra(EXTRA_DO_DIAL, groupCallData.doDial);
		intent.putExtra(EXTRA_DO_ACCEPT, groupCallData.doAccept);
		intent.putExtra(EXTRA_ROOM_ID, groupCallData.roomId);
		
		intent.putExtra(EXTRA_DO_END, doEnd);
		intent.addFlags(
				Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		return intent;
	}
	
	public static void startService(Context context, String groupName, String roomId,
	                                boolean doDial) {
		if (context != null) {
			Intent intent = new Intent(context, SendBirdGroupCallService.class);
			
			intent.putExtra(EXTRA_CALL_STATE, STATE.STATE_OUTGOING);
			intent.putExtra(EXTRA_GROUP_NAME, groupName);
			intent.putExtra(EXTRA_ROOM_ID, roomId);
			intent.putExtra(EXTRA_DO_DIAL, doDial);
			
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				context.startForegroundService(intent);
			} else {
				context.startService(intent);
			}
		}
	}
	
	public static void stopService(Context context) {
		if (context != null) {
			Intent intent = new Intent(context, SendBirdGroupCallService.class);
			context.stopService(intent);
		}
	}
	
	public void updateNotification(@NonNull GroupCallData callData) {
		mServiceData.set(callData);
		startForeground(NOTIFICATION_ID, getNotification(mServiceData));
	}
	
	private Notification getNotification(@NonNull GroupCallData groupCallData) {
		final String content;
		content = mContext.getString(R.string.calls_notification_video_calling_content_to,
				groupCallData.groupName);
		
		final int currentTime = (int) System.currentTimeMillis();
		final String channelId = mContext.getPackageName() + currentTime;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			String channelName = mContext.getString(R.string.app_name);
			NotificationChannel channel = new NotificationChannel(channelId, channelName,
					NotificationManager.IMPORTANCE_LOW);
			
			NotificationManager notificationManager =
					mContext.getSystemService(NotificationManager.class);
			if (notificationManager != null) {
				notificationManager.createNotificationChannel(channel);
			}
		}
		
		Intent callIntent = getCallActivityIntent(mContext, groupCallData, false);
		PendingIntent callPendingIntent =
				PendingIntent.getActivity(mContext, (currentTime + 1), callIntent, 0);
		
		Intent endIntent = getCallActivityIntent(mContext, groupCallData, true);
		PendingIntent endPendingIntent =
				PendingIntent.getActivity(mContext, (currentTime + 2), endIntent, 0);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId);
		builder.setContentTitle(groupCallData.groupName).setContentText(content).setSmallIcon(
				R.drawable.notification_icon).setLargeIcon(
				BitmapFactory.decodeResource(mContext.getResources(),
						R.drawable.notification_icon)).setPriority(NotificationCompat.PRIORITY_LOW);
		
		builder.setContentIntent(callPendingIntent);
		
		if (SendBirdCall.getOngoingCallCount() > 0) {
			if (groupCallData.doAccept) {
				builder.addAction(new NotificationCompat.Action(0,
						mContext.getString(R.string.calls_notification_decline),
						endPendingIntent));
				builder.addAction(new NotificationCompat.Action(0,
						mContext.getString(R.string.calls_notification_accept),
						callPendingIntent));
			} else {
				builder.setContentIntent(callPendingIntent);
				builder.addAction(new NotificationCompat.Action(0,
						mContext.getString(R.string.calls_notification_end), endPendingIntent));
			}
		}
		
		return builder.build();
	}
	
	public enum STATE {
		STATE_ACCEPTING, STATE_OUTGOING, STATE_CONNECTED, STATE_ENDING, STATE_ENDED
	}
	
	static class GroupCallData {
		
		String roomId;
		
		String groupName;
		
		STATE callState;
		
		boolean doDial;
		
		boolean doAccept;
		
		GroupCallData() {
		}
		
		void set(GroupCallData data) {
			this.groupName = data.groupName;
			this.callState = data.callState;
			this.doDial = data.doDial;
			this.doAccept = data.doAccept;
		}
	}
	
	class CallBinder extends Binder {
		
		SendBirdGroupCallService getService() {
			return SendBirdGroupCallService.this;
		}
	}
}
