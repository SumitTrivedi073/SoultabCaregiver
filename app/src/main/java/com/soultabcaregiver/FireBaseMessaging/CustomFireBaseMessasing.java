package com.soultabcaregiver.FireBaseMessaging;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.SinchHelpers;
import com.sinch.android.rtc.calling.CallNotificationResult;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.main_screen.MainActivity;
import com.soultabcaregiver.activity.login_module.LoginActivity;
import com.soultabcaregiver.sinch_calling.SinchService;
import com.soultabcaregiver.utils.Utility;

import java.util.List;
import java.util.Map;


public class CustomFireBaseMessasing extends FirebaseMessagingService {

    public static final String MyNoti = "CHANNEL_ID";
    private static final String TAG = "MyFirebaseMsgService";
    public int count = 0;
    Intent intent;
    MainActivity mainActivity;
    boolean AppInBackground;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        AppInBackground = isAppIsInBackground(this);

        try {
            if (remoteMessage != null && remoteMessage.getNotification().getBody() != null) {
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
                Log.d(TAG, "Message Notification Title  : " + remoteMessage.getNotification().getTitle());
                if (!String.valueOf(remoteMessage.getNotification().getTitle()).equals("SoulTab Caregiver")) {

                    getNotifaction(remoteMessage.getNotification().getTitle(),
                            remoteMessage.getNotification().getBody());

                } else {
                    if (AppInBackground) {
                        getNotifaction(remoteMessage.getNotification().getTitle(),
                                remoteMessage.getNotification().getBody());

                    }
                }

                Log.e("remote_msg==", remoteMessage.getNotification().getBody());
            }


            if (remoteMessage != null && remoteMessage.getData().size() > 0) {

                count = count + 1;
                Log.e("remote_msg_size==", remoteMessage.getData().toString());
                if (!String.valueOf(remoteMessage.getNotification().getTitle()).equals("SoulTab Caregiver")) {

                    createNotificationChannel();
                    getNotifaction(remoteMessage.getNotification().getTitle(),
                            remoteMessage.getNotification().getBody());

                } else {
                    if (AppInBackground) {
                        getNotifaction(remoteMessage.getNotification().getTitle(),
                                remoteMessage.getNotification().getBody());

                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FCM_Error_Msg", e.getMessage());
        }

        Map data = remoteMessage.getData();

        NotificationResult result = SinchHelpers.queryPushNotificationPayload(getApplicationContext(), data);
        if (result.isValid() && result.isCall()) {
            CallNotificationResult callResult = result.getCallResult();
            Log.d(TAG, "queryPushNotificationPayload() -> display name: " + result.getDisplayName());
            if (callResult != null) {
                Log.d(TAG, "queryPushNotificationPayload() -> headers: " + result.getCallResult().getHeaders());
                Log.d(TAG, "queryPushNotificationPayload() -> remote user ID: " + result.getCallResult().getRemoteUserId());
            }
        }

        // Mandatory: forward payload to the SinchClient.
        if (SinchHelpers.isSinchPushPayload(remoteMessage.getData())) {
            new ServiceConnection() {
                private Map payload;

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    if (payload != null) {
                        SinchService.SinchServiceInterface sinchService = (SinchService.SinchServiceInterface) service;
                        if (sinchService != null) {
                            NotificationResult result = sinchService.relayRemotePushNotificationPayload(payload);
                            if (result.isValid() && result.isCall()) {
                                // Optional: handle result, e.g. show a notification or similar.
                            }

                            if (!TextUtils.isEmpty(Utility.getSharedPreferences(getApplicationContext(),APIS.Caregiver_email))) {
                                sinchService.startClient(Utility.getSharedPreferences(getApplicationContext(),APIS.Caregiver_email));

                            }
                        }
                    }
                    payload = null;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                }

                public void relayMessageData(Map<String, String> data) {
                    payload = data;

                    if (AppInBackground) {
                        getNotifaction(remoteMessage.getNotification().getTitle(),
                                remoteMessage.getNotification().getBody());

                        getApplicationContext().bindService(new Intent(getApplicationContext(), SinchService.class), this, BIND_AUTO_CREATE);
                    }
                }
            }.relayMessageData(data);
        }


    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.e("onNewToken", s);

    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(MyNoti, name, importance);
            channel.setDescription(description);
            channel.canShowBadge();
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }


    private void getNotifaction(String title, String body) {

        if (!TextUtils.isEmpty(Utility.getSharedPreferences(this, APIS.user_id))) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Default";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyNoti)
                .setSmallIcon(R.drawable.main_logo)
                .setContentTitle(title)
                .setContentText(body).setAutoCancel(true).setContentIntent(pendingIntent);
        ;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(MyNoti, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        ;}
        manager.notify(0, builder.build());

        AppInBackground = isAppIsInBackground(this);
        if (!AppInBackground) {

            if (!TextUtils.isEmpty(Utility.getSharedPreferences(this, APIS.user_id))) {
                mainActivity = MainActivity.instance;
                if (mainActivity != null) {
                    mainActivity.Alert_countAPI();
                }
            }

        }

    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
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

}
