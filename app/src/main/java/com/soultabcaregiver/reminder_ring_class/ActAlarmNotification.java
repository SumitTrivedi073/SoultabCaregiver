package com.soultabcaregiver.reminder_ring_class;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
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
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.SplashActivity;
import com.soultabcaregiver.reminder_ring_class.model.AlarmModel;
import com.soultabcaregiver.utils.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ActAlarmNotification extends Activity {
    private final String TAG = "AlarmMe";
    private final long[] mVibratePattern = {0, 500, 500};
    SharedPreferences shp;
    SimpleDateFormat hh_mm_aa = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
    Calendar myCalendar;
    //    MediaPlayer mMediaPlayer;
    Notification notification;
    AudioManager am;
    private Ringtone mRingtone;
    private Vibrator mVibrator;
    private boolean mVibrate = true;
    private Uri mAlarmSound;
    private long mPlayTime = 60;
    private Timer mTimer = null;
    private AlarmModel mAlarm;
    //    private DateTime mDateTime;
    private TextView mTextView;
    private TextView alarm_time_text;
    private TextView alarm_date_text;



    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.act_alarm_notification);


        mTextView = findViewById(R.id.alarm_title_text);
        alarm_time_text = findViewById(R.id.alarm_time_text);
        alarm_date_text = findViewById(R.id.alarm_date_text);

        myCalendar = Calendar.getInstance();
        shp = getSharedPreferences("TEXT", 0);

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        readPreferences();
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


        stop();
        start(intent);
    }

    private void start(Intent intent) {
        try {
            mAlarm = (AlarmModel) intent.getSerializableExtra(APIS.Send_alarmData);

            Log.i(TAG, "AlarmNotification.start('" + mAlarm.getmTitle() + "')");


            mTextView.setText(mAlarm.getmTitle());

            alarm_time_text.setText(Utility.EEE_dd_MMM.format(Utility.yyyy_MM_dd.parse(mAlarm.getActualDate())));

            //  alarm_time_text.setText(hh_mm_aa.format(myCalendar.getTime()));
            alarm_date_text.setText(hh_mm_aa.format(myCalendar.getTime()));

            PlayTimerTask mTimerTask = new PlayTimerTask();
            mTimer = new Timer();
            mTimer.schedule(mTimerTask, mPlayTime);

            RingPlay();
            if (mVibrate) {
                mVibrator.vibrate(mVibratePattern, 0);
            }




        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void RingPlay() {
        mRingtone.play();

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        Log.e("RingerMode", String.valueOf(am.getRingerMode()));
        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                Log.i("MyApp", "Silent mode");
                addNotification(mAlarm);

                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                Log.i("MyApp", "Vibrate mode");
                addNotification(mAlarm);

                break;
            case AudioManager.RINGER_MODE_NORMAL:
                Log.i("MyApp", "Normal mode");
                break;
        }

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

    private void readPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mAlarmSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sound1);
        Log.e("SoundFileName2", "sound1");


        mVibrate = prefs.getBoolean("vibrate_pref", true);
        mPlayTime = (long) Integer.parseInt(prefs.getString("alarm_play_time_pref", "30")) * 1000;

        final String packageName = getPackageName();

        mRingtone = RingtoneManager.getRingtone(getApplicationContext(), mAlarmSound);

        if (mVibrate) {
            mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }

        start(getIntent());
    }

    private void addNotification(AlarmModel alarm) {


        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Date date = new Date(alarm.getmDate());
        String formatted = Utility.hh_mm_aa.format(date);

        int notificationId = (int) System.currentTimeMillis();
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mChannel.setLightColor(this.getResources().getColor(R.color.themecolor));
            mChannel.enableLights(true);
            mChannel.setDescription("Missed alarm: " + formatted);
            notificationManager.createNotificationChannel(mChannel);
        }
        int color = 0x008000;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.main_logo).
                        setColor(this.getResources().getColor(R.color.themecolor))
                .setContentTitle(alarm.getmTitle())
                .setContentText("Missed alarm: " + formatted);
        Intent intent = new Intent(this, SplashActivity.class);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(SplashActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                notificationId,
                PendingIntent.FLAG_IMMUTABLE
        );

        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        notificationManager.notify(notificationId, mBuilder.build());


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
        }
    }


}
