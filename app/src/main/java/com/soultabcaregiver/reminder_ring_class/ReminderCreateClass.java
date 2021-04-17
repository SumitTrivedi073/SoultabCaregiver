package com.soultabcaregiver.reminder_ring_class;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.Calender.CalenderModel.AllAppointmentModel;
import com.soultabcaregiver.activity.Calender.CalenderModel.AllEventModel;
import com.soultabcaregiver.activity.Calender.CalenderModel.ReminderBean;
import com.soultabcaregiver.activity.docter.DoctorModel.MainCat;
import com.soultabcaregiver.reminder_ring_class.ReminderBroadcastReceiver;
import com.soultabcaregiver.reminder_ring_class.model.AlarmModel;
import com.soultabcaregiver.reminder_ring_class.model.AlarmSetModel;
import com.soultabcaregiver.reminder_ring_class.model.AlarmSharePreferenceModel;
import com.soultabcaregiver.reminder_ring_class.model.PersonalAlarmModel;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReminderCreateClass {

    String TAG = this.getClass().getSimpleName();

    private AlarmSharePreferenceModel alarmSharePreferenceModel;
    private PersonalAlarmModel personalAlarmModel;
    private Activity activity;
    ArrayList<Integer> Reminder_ID_array = new ArrayList<>();

    public static ReminderCreateClass instance;

    public ReminderCreateClass(Activity activity) {
        this.activity = activity;
        instance = ReminderCreateClass.this;

        GetPersonalReminderList();

    }

    public static ReminderCreateClass getInstance() {
        return instance;
    }

    private void ReminderSet(String dateSet, int id, AlarmModel alarmModel) {
        Reminder_ID_array.add(id);

        boolean isRepeatAlarm = false;
        long repeatTime = 0;
        Intent intent = new Intent(activity, ReminderBroadcastReceiver.class);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(Utility.yyyy_mm_dd_hh_mm_aa.parse(dateSet));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        alarmModel.setmDate(calendar.getTimeInMillis());
        alarmModel.setAlarmId(id);
//        intent.putExtra(IntentKey.Send_alarmData, alarmModel);
        Bundle bundle = new Bundle();
        bundle.putSerializable(APIS.Send_alarmData, alarmModel);
        intent.putExtra("bundle", bundle);


        boolean isWorking = (PendingIntent.getBroadcast(activity, id, intent, PendingIntent.FLAG_NO_CREATE) != null);
        if (!isWorking) {
            //  ////Log.e("alarm", "is working");
            AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(activity,
                    id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }
        if (alarmModel.getAlarmType().equalsIgnoreCase("Every Day")) {
            isRepeatAlarm = true;
            repeatTime = AlarmManager.INTERVAL_DAY;
        } else if (alarmModel.getAlarmType().equalsIgnoreCase("Every Week")) {
            isRepeatAlarm = true;
            repeatTime = 7 * AlarmManager.INTERVAL_DAY;
        } else if (alarmModel.getAlarmType().equalsIgnoreCase("Every Year")) {
            isRepeatAlarm = true;
            repeatTime = 365 * AlarmManager.INTERVAL_DAY;
        }


        PendingIntent sender = PendingIntent.getBroadcast(activity, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager mAlarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            if (isRepeatAlarm) {
                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), repeatTime, sender);
            } else {
                mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            }
        } else {
            if (Build.VERSION.SDK_INT >= 19) {
                if (isRepeatAlarm) {
                    mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(), repeatTime, sender);
                } else {
                    mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                }
            } else {
                if (isRepeatAlarm) {
                    mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(), repeatTime, sender);
                } else {
                    mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                }
            }
        }
        //        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        Log.i("Alarm Manager", "AlarmListAdapter.setAlarm(" + id + ", '" + alarmModel.getSnooze() + "', " + calendar.getTimeInMillis() + ")");
//        }
    }

    public void DeleteReminderlogout() {
        Log.e("Reminder_ID_arraySSS", String.valueOf(Reminder_ID_array.size()));
        for (int i = 0; i < Reminder_ID_array.size(); i++) {
            Log.e("Reminder_ID", String.valueOf(Reminder_ID_array.get(i)));

            ReminderDelete(Reminder_ID_array.get(i));

        }
    }


    private void ReminderDelete(int id) {
        Intent intent = new Intent(activity, ReminderBroadcastReceiver.class);

        boolean isWorking = (PendingIntent.getBroadcast(activity, id, intent, PendingIntent.FLAG_NO_CREATE) != null);
        if (isWorking) {
            Log.e("alarm_delete", "true");
            AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(activity,
                    id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);

        }
    }


    private boolean compareDate(String date) {
        try {
            Calendar calendar = Calendar.getInstance();
            boolean graterThanDate;
            String todayDate = Utility.yyyy_MM_dd.format(calendar.getTime());


            Date convertedDate = Utility.yyyy_MM_dd.parse(String.valueOf(Utility.dd_MM_yyyy.parse(date)));
            Date convertedDate2 = Utility.yyyy_MM_dd.parse(todayDate);

            if (convertedDate.equals(convertedDate2)) {
                graterThanDate = true;  // If two dates are equal.
            } else // If start date is after the end date.
                graterThanDate = convertedDate.after(convertedDate2);
            return graterThanDate;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;

    }

    private boolean compareDateTime(String date) {
        try {
            Calendar calendar = Calendar.getInstance();
            boolean graterThanDate;
            String todayDate = Utility.yyyy_mm_dd_hh_mm_ss.format(calendar.getTime());
            String selectDate = Utility.yyyy_mm_dd_hh_mm_ss.format(Utility.yyyy_mm_dd_hh_mm_aa.parse(date));

            Date selectDateDate = Utility.yyyy_mm_dd_hh_mm_ss.parse(selectDate);
            Date todayDateDate = Utility.yyyy_mm_dd_hh_mm_ss.parse(todayDate);

            //Log.e("selectDateDate", String.valueOf(selectDateDate));
            //Log.e("todayDateDate", String.valueOf(todayDateDate));

            if (String.valueOf(selectDateDate).equals(String.valueOf(todayDateDate))) {
                graterThanDate = true;  // If two dates are equal.
            } else // If start date is after the end date.
            {
                assert selectDateDate != null;
                graterThanDate = selectDateDate.after(todayDateDate);
            }

            return graterThanDate;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;

    }



    //    PersonalReminder Methods
    private void GetPersonalReminderList() {
        final String TAG = "GetReminderList";
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("user_id", Utility.getSharedPreferences(activity,APIS.user_id));
            mainObject.put("device_type", "android");

            //Log.e(TAG, "GetPersonalReminderList======>" + mainObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.EVENTLIST, mainObject,
                response -> {
                    //Log.e(TAG, "GetReminderList response=" + response.toString());
                    try {
                        AllEventModel allEventModel = new Gson().fromJson(response.toString(), AllEventModel.class);
                        if (allEventModel.getStatus().equalsIgnoreCase("true")) {
                            try {

                                ArrayList<ReminderBean> reminderBeanArrayList = new ArrayList<>();

                                if (allEventModel.getResponse().getActivities().getReminders() != null) {
                                    if (allEventModel.getResponse().getActivities().getReminders().size() > 0) {

                                        //Log.e(TAG, "Reminders_size" + allEventModel.getResponse().getActivities().getReminders().size());


                                        for (int i = 0; i < allEventModel.getResponse().getActivities().getReminders().size(); i++) {
                                            ReminderBean reminderBean = allEventModel.getResponse().getActivities().getReminders().get(i);
                                            reminderBean.setAppointment(false);
                                            reminderBean.setRepeat("1");
                                            reminderBeanArrayList.add(reminderBean);

                                        }
                                    }
                                }


                                if (allEventModel.getResponse().getActivities().getAppointments() != null) {
                                    if (allEventModel.getResponse().getActivities().getAppointments().size() > 0) {

                                        //Log.e(TAG, "Appointment_size" + allEventModel.getResponse().getActivities().getAppointments().size());


                                        for (int i = 0; i < allEventModel.getResponse().getActivities().getAppointments().size(); i++) {
                                            AllAppointmentModel allAppointmentModel = allEventModel.getResponse().getActivities().getAppointments().get(i);

                                            ReminderBean reminderBean = new ReminderBean();
                                            reminderBean.setId(allAppointmentModel.getId());
                                            reminderBean.setTitle(allAppointmentModel.getDoctorName());
                                            reminderBean.setDate(allAppointmentModel.getSelectedDate());
                                            reminderBean.setTime(allAppointmentModel.getScheduleTime());
                                            reminderBean.setUserId(allAppointmentModel.getUserId());
                                            reminderBean.setReminderBefore(String.valueOf(24 * 60));
                                            reminderBean.setDoctoraddress(allAppointmentModel.getDoctoraddress());
                                            reminderBean.setReminder(allAppointmentModel.getReminder());
                                            reminderBean.setRepeat("Once");
                                            reminderBean.setAppointment(true);
                                            reminderBeanArrayList.add(reminderBean);
                                        }


                                    }

                                }
                                PersonalReminderAbilityCheck(reminderBeanArrayList);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (Utility.GetPersonalAlarmSetModel(activity) != null) {
                                PersonalReminderDeleteAbilityCheck(new ArrayList<>());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> VolleyLog.d(TAG, "Error: " + error.getMessage())) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                return params;
            }

        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void PersonalReminderDeleteAbilityCheck(List<ReminderBean> medicineListDatumList) {
        personalAlarmModel = Utility.GetPersonalAlarmSetModel(activity);

        boolean isTag = false;
        for (int j = 0; j < personalAlarmModel.getAlarmSetModelList().size(); j++) {

            if (medicineListDatumList.size() != 0) {
                for (int i = 0; i < medicineListDatumList.size(); i++) {

                    if (personalAlarmModel.getAlarmSetModelList().get(j).getItemId()
                            .equalsIgnoreCase(medicineListDatumList.get(i).getId())) {

                        isTag = true;
                        break;
                    }
                }
            }

            if (!isTag) {
                int uniqueId = personalAlarmModel.getAlarmSetModelList().get(j).getAlarmId();
                ReminderDelete(uniqueId);
            }
        }
        if (medicineListDatumList.size() != 0) {
            Utility.SetPersonalAlarmSetModel(activity, new PersonalAlarmModel());

        }

    }

    private void PersonalReminderAbilityCheck(List<ReminderBean> reminderBeanList) {
        try {
            if (Utility.GetPersonalAlarmSetModel(activity) != null) {
                PersonalReminderDeleteAbilityCheck(reminderBeanList);
            }
            List<AlarmSetModel> alarmSetModelArrayList = new ArrayList<>();

            if (reminderBeanList != null) {

                if (reminderBeanList.size() > 0) {

                    for (int i = 0; i < reminderBeanList.size(); i++) {
                        ReminderBean reminderBean = reminderBeanList.get(i);

                        int uniqueId = Integer.parseInt(reminderBean.getId())
                                * Integer.parseInt(reminderBean.getId());

                        boolean isSelectedReminder = true;

                        if (reminderBean.getRepeat().equalsIgnoreCase("Once")) {
                            isSelectedReminder = compareDate(reminderBean.getDate());
                        }

                        if (isSelectedReminder) {
                            Calendar now = Calendar.getInstance();
                            now.setTime(Utility.yyyy_mm_dd_hh_mm_aa.parse(reminderBean.getDate() + " " + reminderBean.getTime()));
                            now.add(Calendar.MINUTE, -Integer.parseInt(reminderBean.getReminderBefore()));

                            String completeDate = Utility.yyyy_mm_dd_hh_mm_aa.format(now.getTime());

                            if (compareDateTime(completeDate)) {
                                AlarmModel alarmModel = new AlarmModel();
                                alarmModel.setItemId(reminderBean.getId());
                                alarmModel.setmTitle(reminderBean.getTitle());
                                alarmModel.setActualDate(reminderBean.getDate());
                                alarmModel.setActualTime(Utility.hh_mm_aa.format(now.getTime()));
                                alarmModel.setAlarmType(reminderBean.getRepeat());
                                if (reminderBean.isAppointment()) {
                                    alarmModel.setAlarmDescription(reminderBean.getDoctoraddress());
                                    alarmModel.setAlarmFrom("Appointment");
                                    ReminderSet(completeDate,  uniqueId, alarmModel);
                                } else {

                                    //Log.e("Snooze", reminderBean.getSnooze());
                                    //Log.e("For_time", reminderBean.getFor_time());
                                    //Log.e("Alarm_Count", String.valueOf(ActAddReminder.Alarm_Count));
                                    //Log.e("completeDate", completeDate);
                                    if (reminderBean.getSnooze().equals("false")) {
                                        if (compareDateTime(completeDate)) {
                                            AlarmModel alarmModel1 = new AlarmModel();
                                            alarmModel1.setItemId(reminderBean.getId());
                                            alarmModel1.setmTitle(reminderBean.getTitle());
                                            alarmModel1.setActualDate(reminderBean.getDate());
                                            alarmModel1.setSnooze("false");
                                            alarmModel1.setRinging_time(reminderBean.getFor_time());
                                            alarmModel1.setActualTime(Utility.hh_mm_aa.format(now.getTime()));
                                            alarmModel1.setAlarmType(reminderBean.getRepeat());
                                            alarmModel1.setAlarmFrom("Personal Reminder");
                                            ReminderSet(completeDate,  uniqueId, alarmModel1);
                                            Log.e("personalReminder_false", reminderBean.getTitle());

                                        } else {
                                            ReminderDelete(uniqueId);
                                        }

                                    } else {


                                        //Log.e("Snooze", reminderBean.getSnooze());

                                        //Log.e("completeDate", completeDate);
                                        if (compareDateTime(completeDate)) {
                                            AlarmModel alarmModel2 = new AlarmModel();
                                            alarmModel2.setItemId(reminderBean.getId());
                                            alarmModel2.setmTitle(reminderBean.getTitle());
                                            alarmModel2.setActualDate(reminderBean.getDate());
                                            alarmModel2.setSnooze("true");
                                            alarmModel2.setRinging_time(reminderBean.getFor_time());
                                            alarmModel2.setRepeting_time(Integer.parseInt(String.valueOf(reminderBean.getAfter_time())));
                                            alarmModel2.setActualTime(Utility.hh_mm_aa.format(now.getTime()));
                                            alarmModel2.setAlarmType(reminderBean.getRepeat());
                                            alarmModel2.setAlarmFrom("Personal Reminder");
                                            ReminderSet(completeDate,  uniqueId, alarmModel2);
                                            Log.e("personalReminder_true", reminderBean.getTitle());

                                        } else {
                                            ReminderDelete(uniqueId);
                                        }
                                    }
                                }


                            } else {
                                ReminderDelete(uniqueId);
                            }

                        } else {
                            ReminderDelete(uniqueId);
                        }
                        AlarmSetModel alarmSetModel = new AlarmSetModel();
                        alarmSetModel.setAlarmId(uniqueId);
                        alarmSetModel.setItemId(reminderBean.getId());
                        alarmSetModelArrayList.add(alarmSetModel);

                    }


                }
            }

            personalAlarmModel = new PersonalAlarmModel();
            personalAlarmModel.setAlarmSetModelList(alarmSetModelArrayList);
            Utility.SetPersonalAlarmSetModel(activity, personalAlarmModel);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}



