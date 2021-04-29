package com.soultabcaregiver.reminder_ring_class;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.reminder.AddReminderActivity;
import com.soultabcaregiver.reminder_ring_class.model.AlarmModel;


public class ReminderBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "Alarm manager";
    AlarmModel alarmModel;
    MediaPlayer mp;
    private Ringtone ringtone;
    SharedPreferences shp;
    public   static Handler  mHandler;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Alarm manager", "AlarmReceiver.onReceive('" + intent.getSerializableExtra(APIS.Send_alarmData) + "')");
        shp = context.getSharedPreferences("TEXT", 0);



        Bundle bundle = intent.getBundleExtra("bundle");
        if (bundle != null) {
            alarmModel = (AlarmModel) bundle.getSerializable(APIS.Send_alarmData);
        }
        if (alarmModel != null) {
            if (alarmModel.getAlarmFrom().equalsIgnoreCase("Personal Reminder")) {
                if (alarmModel.getSnooze()!=null){
                if (alarmModel.getSnooze().equals("0")) {

                    Intent newIntent = new Intent(context, ActAlarmNotification.class);
                    newIntent.putExtra(APIS.Send_alarmData, alarmModel);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(newIntent);

                } else {
                    if (mHandler != null) {
                        mHandler.removeCallbacksAndMessages(null);
                    }
                    Intent newIntent = new Intent(context, ActAlarmNotification.class);
                    newIntent.putExtra(APIS.Send_alarmData, alarmModel);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(newIntent);

                    String Repetingtime = String.valueOf(alarmModel.getRepeting_time());
                    TimerStart(context, Repetingtime);


                }

            }

            } else if (alarmModel.getAlarmFrom().equalsIgnoreCase("Appointment")) {
                Intent   newIntent = new Intent(context, ActAppoinmentAlarmNotification.class);
                newIntent.putExtra(APIS.Send_alarmData, alarmModel);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                context.startActivity(newIntent);

            }
        }
    }

    private void TimerStart(Context context, String repetingtime) {

        if (repetingtime != null) {

            if (repetingtime.equals("5")) {
                mHandler=new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Log.e("Timer Time","5 * 60 * 1000");
                        if (AddReminderActivity.Alarm_Count <= Integer.parseInt(alarmModel.getRinging_time())) {
                            Log.e("Alarm_Count before", String.valueOf(AddReminderActivity.Alarm_Count));
                            if (AddReminderActivity.Alarm_Count != 0) {

                                AddReminderActivity.Alarm_Count = AddReminderActivity.Alarm_Count - 1;
                                Log.e("Alarm_Count if", String.valueOf(AddReminderActivity.Alarm_Count));
                                Intent newIntent = new Intent(context, ActAlarmNotification.class);
                                newIntent.putExtra(APIS.Send_alarmData, alarmModel);
                                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                context.startActivity(newIntent);
                                TimerStart(context, repetingtime);

                            } else {
                                mHandler.removeCallbacksAndMessages(null);
                                Log.e("Alarm_Count else", String.valueOf(AddReminderActivity.Alarm_Count));
                            }
                        }
                    }
                }, 5 * 60 * 1000);
            }
            if (repetingtime.equals("10")) {
                mHandler=new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Timer Time","10 * 60 * 1000");
                        if (AddReminderActivity.Alarm_Count <= Integer.parseInt(alarmModel.getRinging_time())) {
                            Log.e("Alarm_Count before", String.valueOf(AddReminderActivity.Alarm_Count));
                            if (AddReminderActivity.Alarm_Count != 0) {

                                AddReminderActivity.Alarm_Count = AddReminderActivity.Alarm_Count - 1;
                                Log.e("Alarm_Count if", String.valueOf(AddReminderActivity.Alarm_Count));
                                Intent newIntent = new Intent(context, ActAlarmNotification.class);
                                newIntent.putExtra(APIS.Send_alarmData, alarmModel);
                                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                context.startActivity(newIntent);
                                TimerStart(context, repetingtime);

                            } else {
                                mHandler.removeCallbacksAndMessages(null);
                                Log.e("Alarm_Count else", String.valueOf(AddReminderActivity.Alarm_Count));
                            }
                        }
                    }
                }, 10 * 60 * 1000);
            }
               if (repetingtime.equals("15")) {
                mHandler=new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Timer Time","15 * 60 * 1000");
                        if (AddReminderActivity.Alarm_Count <= Integer.parseInt(alarmModel.getRinging_time())) {
                            Log.e("Alarm_Count before", String.valueOf(AddReminderActivity.Alarm_Count));
                            if (AddReminderActivity.Alarm_Count != 0) {

                                AddReminderActivity.Alarm_Count = AddReminderActivity.Alarm_Count - 1;
                                Log.e("Alarm_Count if", String.valueOf(AddReminderActivity.Alarm_Count));
                                Intent newIntent = new Intent(context, ActAlarmNotification.class);
                                newIntent.putExtra(APIS.Send_alarmData, alarmModel);
                                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                context.startActivity(newIntent);
                                TimerStart(context, repetingtime);

                            } else {
                                mHandler.removeCallbacksAndMessages(null);
                                Log.e("Alarm_Count else", String.valueOf(AddReminderActivity.Alarm_Count));
                            }
                        }
                    }
                }, 15 * 60 * 1000);
            }
              if (repetingtime.equals("30")) {
                mHandler=new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Timer Time","30 * 60 * 1000");
                        if (AddReminderActivity.Alarm_Count <= Integer.parseInt(alarmModel.getRinging_time())) {
                            Log.e("Alarm_Count before", String.valueOf(AddReminderActivity.Alarm_Count));
                            if (AddReminderActivity.Alarm_Count != 0) {

                                AddReminderActivity.Alarm_Count = AddReminderActivity.Alarm_Count - 1;
                                Log.e("Alarm_Count if", String.valueOf(AddReminderActivity.Alarm_Count));
                                Intent newIntent = new Intent(context, ActAlarmNotification.class);
                                newIntent.putExtra(APIS.Send_alarmData, alarmModel);
                                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                context.startActivity(newIntent);
                                TimerStart(context, repetingtime);

                            } else {
                                mHandler.removeCallbacksAndMessages(null);
                                Log.e("Alarm_Count else", String.valueOf(AddReminderActivity.Alarm_Count));
                            }
                        }
                    }
                }, 30 * 60 * 1000);
            }
        } else {
            mHandler=new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("Timer Time2","5 * 60 * 1000");
                    if (AddReminderActivity.Alarm_Count <= Integer.parseInt(alarmModel.getRinging_time())) {
                        Log.e("Alarm_Count before", String.valueOf(AddReminderActivity.Alarm_Count));
                        if (AddReminderActivity.Alarm_Count != 0) {

                            AddReminderActivity.Alarm_Count = AddReminderActivity.Alarm_Count - 1;
                            Log.e("Alarm_Count if", String.valueOf(AddReminderActivity.Alarm_Count));
                            Intent newIntent = new Intent(context, ActAlarmNotification.class);
                            newIntent.putExtra(APIS.Send_alarmData, alarmModel);
                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            context.startActivity(newIntent);
                            TimerStart(context, repetingtime);

                        } else {
                            mHandler.removeCallbacksAndMessages(null);
                            Log.e("Alarm_Count else", String.valueOf(AddReminderActivity.Alarm_Count));
                        }
                    }
                }
            }, 5 * 60 * 1000);
        }


    }



}
