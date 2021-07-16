package com.soultabcaregiver.FireBaseMessaging;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.SendBirdPushHandler;
import com.sendbird.android.SendBirdPushHelper;
import com.sendbird.calls.SendBirdCall;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.SplashActivity;
import com.soultabcaregiver.activity.login_module.LoginActivity;
import com.soultabcaregiver.activity.main_screen.MainActivity;
import com.soultabcaregiver.sendbird_calls.IncomingCallActivity;
import com.soultabcaregiver.sendbird_calls.SendBirdAuthentication;
import com.soultabcaregiver.sendbird_calls.utils.BroadcastUtils;
import com.soultabcaregiver.sendbird_calls.utils.PrefUtils;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class CustomFireBaseMessasing extends SendBirdPushHandler {
	
	private static final String TAG = "MyFirebaseMsgService";
	
	private static final AtomicReference<String> pushToken = new AtomicReference<>();
	
	public static final String MyNoti = "CHANNEL_ID";
	
	public int count = 0;
	
	Intent intent;
	
	MainActivity mainActivity;
	
	boolean AppInBackground;
	
	@Override
	public void onNewToken(String token) {
		super.onNewToken(token);
		if (!token.isEmpty()) {
			Log.e("NEW_TOKEN", token);
			SendBirdAuthentication.registerPushToken(token, e -> {
				if (e == null) {
					// save token here
					PrefUtils.setPushToken(token);
					pushToken.set(token);
				}
			});
		}
	}
	
	@Override
	protected void onMessageReceived(Context context, RemoteMessage remoteMessage) {
		
		AppInBackground = isAppIsInBackground(context);
		
		try {
			String channelUrl;
			if (remoteMessage.getData().containsKey("sendbird")) {
				JSONObject sendBird = new JSONObject(remoteMessage.getData().get("sendbird"));
				JSONObject channel = (JSONObject) sendBird.get("channel");
				String calleeId = sendBird.getJSONObject("sender").optString("id");
				channelUrl = (String) channel.get("channel_url");
				
				SendBird.markAsDelivered(channelUrl);
				
				if (AppInBackground) {
					sendNotification(context, remoteMessage.getData().get("message"), channelUrl,
							calleeId);
				} else {
					BroadcastUtils.sendNewMessageBroadCast(context,
							remoteMessage.getData().get("message"), channelUrl);
				}
			} else if (SendBirdCall.handleFirebaseMessageData(remoteMessage.getData())) {
				checkAuthentication(context, remoteMessage);
			} else if (remoteMessage.getData().size() > 0) {
				count = count + 1;
				if (!String.valueOf(remoteMessage.getNotification().getTitle()).equals(
						"SoulTab Caregiver")) {
					createNotificationChannel(context);
					getNotification(context, remoteMessage.getNotification().getTitle(),
							remoteMessage.getNotification().getBody());
				} else {
					if (AppInBackground) {
						getNotification(context, remoteMessage.getNotification().getTitle(),
								remoteMessage.getNotification().getBody());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("FCM_Error_Msg", e.getMessage());
		}
		
		
	}
	
	@Override
	protected boolean alwaysReceiveMessage() {
		return true;
	}
	
	private boolean isAppIsInBackground(Context context) {
		boolean isInBackground = true;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
			List<ActivityManager.RunningAppProcessInfo> runningProcesses =
					am.getRunningAppProcesses();
			for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
				if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					for (String activeProcess : processInfo.pkgList) {
						if (activeProcess.equals(context.getPackageName())) {
							isInBackground = false;
						}
					}
				}
			}
		} else {
			List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
			ComponentName componentInfo = taskInfo.get(0).topActivity;
			if (componentInfo.getPackageName().equals(context.getPackageName())) {
				isInBackground = false;
			}
		}
		return isInBackground;
	}
	
	/**
	 * Create and show a simple notification containing the received FCM message.
	 *
	 * @param messageBody FCM message body received.
	 */
	public static void sendNotification(Context context, String messageBody, String channelUrl,
	                                    String calleeId) {
		NotificationManager notificationManager =
				(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		
		final String CHANNEL_ID = "CHANNEL_ID";
		if (Build.VERSION.SDK_INT >= 26) {  // Build.VERSION_CODES.O
			NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "CHANNEL_NAME",
					NotificationManager.IMPORTANCE_HIGH);
			notificationManager.createNotificationChannel(mChannel);
		}
		
		Intent intent = new Intent(context, SplashActivity.class);
		intent.putExtra("groupChannelUrl", channelUrl);
		intent.putExtra("calleeId", calleeId);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent =
				PendingIntent.getActivity(context, 0 /* Request code */, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
		
		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(
						R.drawable.launcher_icon2).setColor(ContextCompat.getColor(context,
						R.color.colorPrimary))  // small icon background color
						.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
								R.drawable.launcher_icon2)).setContentTitle(
						context.getResources().getString(R.string.app_name)).setAutoCancel(
						true).setSound(defaultSoundUri).setPriority(
						Notification.PRIORITY_MAX).setDefaults(
						Notification.DEFAULT_ALL).setContentIntent(pendingIntent);
		
		//here the condition if to show message or not
		if (true) {
			notificationBuilder.setContentText(messageBody);
		} else {
			notificationBuilder.setContentText("Somebody sent you a message.");
		}
		
		notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
	}
	
	private void checkAuthentication(Context context, RemoteMessage remoteMessage) {
		if (SendBirdCall.getCurrentUser() == null) {
			SendBirdAuthentication.autoAuthenticate(context, userId -> {
				if (userId == null) {
					return;
				} else {
					handleIncomingCall(context, remoteMessage);
				}
			});
		} else {
			handleIncomingCall(context, remoteMessage);
		}
	}
	
	private void createNotificationChannel(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = context.getString(R.string.app_name);
			String description = context.getString(R.string.app_name);
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(MyNoti, name, importance);
			channel.setDescription(description);
			channel.canShowBadge();
			NotificationManager notificationManager =
					context.getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
			
		}
	}
	
	private void getNotification(Context context, String title, String body) {
		
		if (!TextUtils.isEmpty(Utility.getSharedPreferences(context, APIS.user_id))) {
			intent = new Intent(context, MainActivity.class);
		} else {
			intent = new Intent(context, LoginActivity.class);
		}
		
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent =
				PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		String channelId = "Default";
		NotificationCompat.Builder builder =
				new NotificationCompat.Builder(context, MyNoti).setSmallIcon(
						R.drawable.notification_icon).setContentTitle(title).setContentText(
						body).setAutoCancel(true).setContentIntent(pendingIntent);
		NotificationManager manager =
				(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(MyNoti, "Default channel",
					NotificationManager.IMPORTANCE_DEFAULT);
			manager.createNotificationChannel(channel);
		}
		manager.notify(0, builder.build());
		
		AppInBackground = isAppIsInBackground(context);
		if (!AppInBackground) {
			
			if (!TextUtils.isEmpty(Utility.getSharedPreferences(context, APIS.user_id))) {
				mainActivity = MainActivity.instance;
				if (mainActivity != null) {
					mainActivity.Alert_countAPI();
				}
			}
			
		}
		
	}
	
	private void handleIncomingCall(Context context, RemoteMessage remoteMessage) {
		try {
			if (remoteMessage.getData().get("sendbird_call") != null) {
				JSONObject callObj = new JSONObject(
						Objects.requireNonNull(remoteMessage.getData().get("sendbird_call")));
				boolean isVideoCall = Objects.requireNonNull(
						Objects.requireNonNull(callObj.optJSONObject("command")).optJSONObject(
								"payload")).optBoolean("is_video_call");
				if (!callObj.getJSONObject("command").getJSONObject("payload").has("ended_call")) {
					
					String callId = Objects.requireNonNull(
							Objects.requireNonNull(callObj.optJSONObject("command")).optJSONObject(
									"payload")).optString("call_id");
					JSONObject callerObj =
							callObj.getJSONObject("command").getJSONObject("payload").getJSONObject(
									"caller");
					Intent incomingCallIntent = new Intent(context, IncomingCallActivity.class);
					String userId = callerObj.getString("user_id");
					String userName = callerObj.getString("nickname");
					incomingCallIntent.putExtra("userId", userId);
					incomingCallIntent.putExtra("userName", userName);
					incomingCallIntent.putExtra("isVideoCall", isVideoCall);
					incomingCallIntent.putExtra("callId", callId);
					incomingCallIntent.addFlags(
							Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
					context.startActivity(incomingCallIntent);
				} else {
					Intent incomingCallIntent = new Intent(context, IncomingCallActivity.class);
					incomingCallIntent.addFlags(
							Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
					incomingCallIntent.putExtra("callEnded", true);
					context.startActivity(incomingCallIntent);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("FCM_Error_Msg", e.getMessage());
		}
	}
	
	public static void getPushToken(ITokenResult listener) {
		String token = pushToken.get();
		if (!TextUtils.isEmpty(token)) {
			listener.onPushTokenReceived(token, null);
			return;
		}
		
		SendBirdPushHelper.getPushToken((newToken, e) -> {
			if (listener != null) {
				listener.onPushTokenReceived(newToken, e);
			}
			
			if (e == null) {
				pushToken.set(newToken);
			}
		});
	}
	
	public interface ITokenResult {
		
		void onPushTokenReceived(String pushToken, SendBirdException e);
	}
	
}
