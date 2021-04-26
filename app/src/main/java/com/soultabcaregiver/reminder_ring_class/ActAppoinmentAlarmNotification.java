package com.soultabcaregiver.reminder_ring_class;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.main_screen.MainActivity;
import com.soultabcaregiver.activity.docter.UpdateDoctorAppointmentActivity;
import com.soultabcaregiver.reminder_ring_class.model.AlarmModel;
import com.soultabcaregiver.utils.Utility;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

public class ActAppoinmentAlarmNotification extends Activity {
    private final String TAG = "AlarmMe";

    private Ringtone mRingtone;
    private Vibrator mVibrator;
    private final long[] mVibratePattern = {0, 500, 500};
    private boolean mVibrate = true;
    private Uri mAlarmSound;
    private long mPlayTime = 60;
    private Timer mTimer = null;
    private AlarmModel mAlarm;
    //    private DateTime mDateTime;
    private TextView mTextView;
    private TextView alarm_time_text;
    private TextView alarm_date_text;
    private TextView alarm_description_text;

//    MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.act_appointment_alarm_notification);

        mTextView = findViewById(R.id.alarm_title_text);
        alarm_time_text = findViewById(R.id.alarm_time_text);
        alarm_date_text = findViewById(R.id.alarm_date_text);
        alarm_description_text = findViewById(R.id.alarm_description_text);

        readPreferences();
        mRingtone = RingtoneManager.getRingtone(getApplicationContext(), mAlarmSound);
        if (mVibrate)
            mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        start(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "AlarmNotification.onDestroy()");

        stop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "AlarmNotification.onNewIntent()");

        addNotification(mAlarm);

        stop();
        start(intent);
    }

    private void start(Intent intent) {
        mAlarm = (AlarmModel) intent.getSerializableExtra(APIS.Send_alarmData);

        Log.i(TAG, "AlarmNotification.start('" + mAlarm.getmTitle() + "')");

//        Calendar calendar=Calendar.getInstance();
//        try {
//            calendar.setTime(IntentKey.yyyy_mm_dd_hh_mm_aa.parse(mAlarm.getActualTimeToShow()));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        mTextView.setText(mAlarm.getmTitle());
        alarm_description_text.setText(mAlarm.getAlarmDescription());
        try {
            alarm_time_text.setText(Utility.EEE_dd_MMM.format(Utility.yyyy_MM_dd.parse(mAlarm.getActualDate())));
            alarm_date_text.setText(mAlarm.getActualTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        PlayTimerTask mTimerTask = new PlayTimerTask();
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, mPlayTime);
        mRingtone.play();
        if (mVibrate)
            mVibrator.vibrate(mVibratePattern, 0);
    }

    private void stop() {
        Log.i(TAG, "AlarmNotification.stop()");

        mTimer.cancel();
        mRingtone.stop();
        if (mVibrate)
            mVibrator.cancel();
    }

    public void onDismissClick(View view) {
        finish();
    }

    public void onRepeatReminderClick(View view) {
        repearAlarmFrom3Hours();
    }

    public void repearAlarmFrom3Hours() {
        Intent intent = new Intent(this, ReminderBroadcastReceiver.class);

        Calendar calendar = Calendar.getInstance();
        try {

            String completeDate = mAlarm.getActualDate()
                    + " " + /*IntentKey.hh_mm_aa.format(*/mAlarm.getActualTime();

            calendar.setTime(Utility.yyyy_mm_dd_hh_mm_aa.parse(completeDate));
            System.out.println(calendar.getTime());

            calendar.add(Calendar.HOUR, -3);

            mAlarm.setmDate(calendar.getTimeInMillis());
            System.out.println(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(APIS.Send_alarmData, mAlarm);
        intent.putExtra("bundle", bundle);
        PendingIntent sender = PendingIntent.getBroadcast(this, mAlarm.getAlarmId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), calendar.getTimeInMillis(), sender);
        Log.i("Alarm Manager", "AlarmListAdapter.setAlarm(" + mAlarm.getAlarmId() + ", '"
                + mAlarm.getmTitle() + "', " + calendar.getTimeInMillis() + ")");
    }

    public void onReScheduleClick(View view) {
        Intent mINTENT = new Intent(this, UpdateDoctorAppointmentActivity.class);//for update appointed doc
        mINTENT.putExtra("id", mAlarm.getItemId());
        mINTENT.putExtra("diff_", "1");
        startActivity(mINTENT);
    }

    private void readPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mAlarmSound = Uri.parse(prefs.getString("alarm_sound_pref", "DEFAULT_RINGTONE_URI"));
        mVibrate = prefs.getBoolean("vibrate_pref", true);
        mPlayTime = (long) Integer.parseInt(prefs.getString("alarm_play_time_pref", "30")) * 1000;
    }

    private void addNotification(AlarmModel alarm) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification;
        PendingIntent activity;
        Intent intent;

        Log.i(TAG, "AlarmNotification.addNotification(" + alarm.getAlarmId() + ", '" + alarm.getmTitle() + "', '" /*+ mDateTime.formatDetails(alarm)*/ + "')");

        intent = new Intent(this.getApplicationContext(), MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        activity = PendingIntent.getActivity(this, alarm.getAlarmId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification_bar);
        contentView.setTextViewText(R.id.tv_notification_title, alarm.getmTitle());
        contentView.setTextViewText(R.id.tv_notification_description, "Missed this Medicine ");

//        contentView.setOnClickFillInIntent(R.id.tv_notification_dismiss);
        int notificationId = new Random().nextInt(); // just use a counter in some util class...

        Notification.Builder builder = new Notification.Builder(this).setContent(contentView);

        Notification notification = builder.setContentIntent(activity).build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("alarmme_01");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(
                    "alarmme_01",
                    "NotificationDemo",
                    IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notificationId, notification);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class PlayTimerTask extends TimerTask {
        @Override
        public void run() {
            Log.i(TAG, "AlarmNotification.PalyTimerTask.run()");
            addNotification(mAlarm);
            finish();
            repearAlarmFrom3Hours();
        }
    }
}
