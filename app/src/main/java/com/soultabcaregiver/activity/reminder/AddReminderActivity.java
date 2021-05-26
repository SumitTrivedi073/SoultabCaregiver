package com.soultabcaregiver.activity.reminder;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sinch.gson.Gson;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.calender.CalenderModel.CommonResponseModel;
import com.soultabcaregiver.activity.calender.CalenderModel.ReminderBean;
import com.soultabcaregiver.activity.reminder.adapter.CustomPopupAdapter;
import com.soultabcaregiver.activity.reminder.model.BeforeTimeModel;
import com.soultabcaregiver.sinch_calling.BaseActivity;
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
import java.util.Objects;

public class AddReminderActivity extends BaseActivity implements View.OnClickListener {


    public static final int REQUEST_Snooze = 102;
    public static int Alarm_Count;
    String TAG = getClass().getSimpleName();
    EditText et_title;
    TextView tvWhenDate, tvWhenTime, tv_text, tv_snooze_txt;
    TextView tvRepeat, tvBeforRemindTxt, btn_submit_reminder;
    RelativeLayout tv_snooze_layout, back_btn;
    Calendar myCalendar;
    String sTitle = "", sWhenDate = "", sWhenTime = "", sReminder = "", sRepeat = "", value;
    int valBefore = 0;
    SwitchCompat snooze_on;
    boolean istbbeforeRemindTog = true;
    List<BeforeTimeModel> beforeTimeModelList;
    List<BeforeTimeModel> repeatTimeModelList;
    SharedPreferences shp;
    AlertDialog alertDialog;
    String date;
    Context mContext;
    boolean update_reminder;
    ReminderBean reminderModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        mContext = this;

        shp = getSharedPreferences("TEXT", 0);

        // reminderCreateClass = new ReminderCreateClass(AddReminderActivity.this);

        InitCompo();
        Listener();
        SetPreValue();

    }

    private void InitCompo() {
        tvBeforRemindTxt = findViewById(R.id.tv_reminder_txt);
        tvRepeat = findViewById(R.id.tv_repeat_txt);
        et_title = findViewById(R.id.et_title);
        tvWhenDate = findViewById(R.id.tv_when_date);
        tvWhenTime = findViewById(R.id.tv_when_time);
        tv_text = findViewById(R.id.tv_text);
        tv_snooze_layout = findViewById(R.id.tv_snooze_layout);
        snooze_on = findViewById(R.id.Snooze_on);
        tv_snooze_txt = findViewById(R.id.tv_snooze_txt);
        btn_submit_reminder = findViewById(R.id.btn_submit_reminder);
        back_btn = findViewById(R.id.back_btn);

        snooze_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = shp.edit();
                if (isChecked) {
                    editor.putString("Snooze_on", "true");


                    editor.commit();

                } else {
                    editor.putString("Snooze_on", "false");

                    editor.commit();
                }

            }
        });
    }

    private void Listener() {
        tvRepeat.setOnClickListener(this);
        tvBeforRemindTxt.setOnClickListener(this);
        btn_submit_reminder.setOnClickListener(this);
        tvWhenDate.setOnClickListener(this);
        tvWhenTime.setOnClickListener(this);
        tv_snooze_layout.setOnClickListener(this);
        back_btn.setOnClickListener(this);
    }

    public void SetPreValue() {
        update_reminder = getIntent().getBooleanExtra(APIS.Update_reminder, false);
        reminderModel = (ReminderBean) getIntent().getSerializableExtra(APIS.ReminderModel);
        myCalendar = Calendar.getInstance();

        if (!update_reminder) {
            tv_text.setText(getResources().getString(R.string.add_personal_reminder));
//            btn_submit_reminder.setText(getResources().getString(R.string.add_remind));


            SharedPreferences.Editor editor = shp.edit();
            editor.putString("Snooze_minute", "5");
            editor.putString("Snooze_times", "3");
            editor.putString("Snooze_on", "true");
            editor.commit();
            tv_snooze_txt.setText(getResources().getString(R.string.five_minute) + " , " + shp.getString("Snooze_times", "") + getResources().getString(R.string.times));
            snooze_on.setChecked(true);


            tvWhenDate.setText(Utility.EEE_dd_MMM_yyyy.format(myCalendar.getTime()));
            tvWhenTime.setText(Utility.hh_mm_aa.format(myCalendar.getTime()));

            sWhenDate = Utility.yyyy_MM_dd.format(myCalendar.getTime());
        } else {
            SharedPreferences.Editor editor = shp.edit();
            tv_text.setText(getResources().getString(R.string.update_personal_reminder));
            //          btn_submit_reminder.setText(getResources().getString(R.string.update_remind));
            String finalTime = reminderModel.getDate() + " " + reminderModel.getTime();
            try {
                myCalendar.setTime(Utility.yyyy_mm_dd_hh_mm_aa.parse(finalTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Log.e("reminder_id", reminderModel.getId());

            et_title.setText(reminderModel.getTitle());

            if (getIntent().getStringExtra("calender") != null) {
                if (getIntent().getStringExtra("calender").equals("calender")) {
                    date = parseDateToddMMyyyy3(reminderModel.getDate());

                }
            } else {
                date = parseDateToddMMyyyy(reminderModel.getDate());
            }


            //   date = parseDateToddMMyyyy3("2020-02-20");

            sWhenDate = parseDateToddMMyyyy2(date);
            Log.e("sWhenDate", sWhenDate);
            tvWhenDate.setText(date);
            tvWhenTime.setText(reminderModel.getTime());

            if (reminderModel.getAfter_time() != null) {

                tv_snooze_txt.setText(reminderModel.getAfter_time() + getResources().getString(R.string.minute));
                editor.putString("Snooze_minute", reminderModel.getAfter_time());
                editor.commit();
            }
            if (reminderModel.getFor_time() != null) {
                String value = tv_snooze_txt.getText().toString();

                tv_snooze_txt.setText(value + " , " + reminderModel.getFor_time() + getResources().getString(R.string.times));
                editor.putString("Snooze_times", reminderModel.getFor_time());
                editor.commit();
            }

            if (reminderModel.getSnooze() != null) {

                if (reminderModel.getSnooze().equals("1")) {
                    snooze_on.setChecked(true);
                    editor.putString("Snooze_on", "true");
                } else {
                    snooze_on.setChecked(false);
                    editor.putString("Snooze_on", "false");
                }
                editor.commit();

            }


        }


        listOfBeforeAlarm(reminderModel);

    }

    @Override
    protected void onResume() {
        shp = getSharedPreferences("TEXT", 0);
        if (shp.getString("Snooze_on", "") != null) {


            if (shp.getString("Snooze_on", "").equals("true")) {
                snooze_on.setChecked(true);
                tv_snooze_txt.setText(shp.getString("Snooze_minute", "") + " minute " + " , " + shp.getString("Snooze_times", "") + " times");
            } else if (shp.getString("Snooze_on", "").equals("false")) {
                snooze_on.setChecked(false);
                tv_snooze_txt.setText(shp.getString("Snooze_minute", "") + " minute " + " , " + shp.getString("Snooze_times", "") + " times");

            }
        }

        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.back_btn:
                onBackPressed();
                break;

            case R.id.tv_when_date:
                ChooseDate();
                break;
            case R.id.tv_when_time:
                chooseTimePicker();
                break;
            case R.id.tv_reminder_txt:
                DlgBeforRemind();
                break;
            case R.id.tv_repeat_txt:
                DlgRepeatRemind();
                break;
            case R.id.tv_snooze_layout:
                Intent i = new Intent(getApplicationContext(), AddSnoozeTime.class);
                i.putExtra("Snooze_minute", shp.getString("Snooze_minute", ""));
                i.putExtra("Snooze_times", shp.getString("Snooze_times", ""));
                i.putExtra("Snooze_on", shp.getString("Snooze_on", ""));
                startActivity(i);
                break;
            case R.id.btn_submit_reminder:
                if (validFields()) {
                    if (Utility.isNetworkConnected(AddReminderActivity.this)) {
                        SetAlarmVal();
                    } else {

                        Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));

                    }
                }
                break;
        }
    }


    public boolean validFields() {
        boolean check = true;
        sTitle = et_title.getText().toString().trim();

        sWhenTime = Utility.hh_mm_aa.format(myCalendar.getTime());

        if (TextUtils.isEmpty(sTitle)) {
            et_title.setError(getResources().getString(R.string.hint_reminder_title));
            check = false;
        } else if (TextUtils.isEmpty(sReminder)) {

            Utility.ShowToast(mContext, getResources().getString(R.string.txt_reminder1));


            check = false;
        }
        return check;
    }

    public void AddReminder() {
        final String TAG = "addReminder";
        JSONObject mainObject = new JSONObject();
        String apiAccording = null;


        try {
            mainObject.put("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
            mainObject.put("device_type", "android");
            mainObject.put("title", sTitle);

            mainObject.put("date", sWhenDate);//2019-02-08
            mainObject.put("time", sWhenTime.toUpperCase());//"1.20 AM"
            mainObject.put("user_id", Utility.getSharedPreferences(mContext, APIS.user_id));
            mainObject.put("caregiver_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));

            mainObject.put("reminder_before", String.valueOf(valBefore));


            if (sRepeat.equals(getResources().getString(R.string.Once))) {
                mainObject.put("repeat", "Once");

            } else if (sRepeat.equals(getResources().getString(R.string.Every_Day))) {
                mainObject.put("repeat", "Every Day");
            } else if (sRepeat.equals(getResources().getString(R.string.Every_Week))) {
                mainObject.put("repeat", "Every Week");
            } else if (sRepeat.equals(getResources().getString(R.string.Every_Year))) {
                mainObject.put("repeat", "Every Year");
            }


            if (shp.getString("Snooze_minute", "") != null) {
                mainObject.put("after_time", shp.getString("Snooze_minute", ""));

            }
            if (shp.getString("Snooze_times", "") != null) {
                mainObject.put("for_time", shp.getString("Snooze_times", ""));
                Alarm_Count = Integer.parseInt(shp.getString("Snooze_times", ""));

            }

            if (shp.getString("Snooze_on", "") != null) {

                if (shp.getString("Snooze_on", "").equals("true")) {
                    mainObject.put("snooze", "1");
                } else {
                    mainObject.put("snooze", "0");
                }
            }

            if (update_reminder) {
                mainObject.put("reminder_id", reminderModel.getId());
                apiAccording = APIS.UPDATEREMINDERAPI;
            } else {
                apiAccording = APIS.ADDREMINDERAPI;
            }
            Log.e("addReminder Enter data=", "=======> " + mainObject.toString() + "==========>" + apiAccording);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog(getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + apiAccording, mainObject,
                response -> {
                    try {
                        Log.d(TAG, "addReminder response=" + response.toString());
                        hideProgressDialog();
                        CommonResponseModel commonResponseModel = new Gson().fromJson(response.toString(), CommonResponseModel.class);
                        if (commonResponseModel.getStatus().equalsIgnoreCase("true")) {


                            if (update_reminder) {
                                ShowAlertResponse(getResources().getString(R.string.update_reminder_successfully), "1");

                            } else {
                                ShowAlertResponse(commonResponseModel.getMessage(), "1");
                            }

                   
                        } else if (String.valueOf(commonResponseModel.getStatusCode()).equals("403")) {
                            logout_app(commonResponseModel.getMessage());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext, APIS.EncodeUser_id));

                return params;
            }

        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 8, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void ShowAlertResponse(String message, String value) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.send_successfully_layout,
                null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();


        TextView OK_txt = layout.findViewById(R.id.OK_txt);
        TextView title_txt = layout.findViewById(R.id.title_txt);

        title_txt.setText(message);


        OK_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                onBackPressed();
            }
        });

    }


    public void SetAlarmVal() {

        AddReminder();

    }


    private void chooseTimePicker() {
        try {
            // Get Current Time
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DialogTheme,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            myCalendar.set(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                    myCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                            tvWhenTime.setText(Utility.hh_mm_aa.format(myCalendar.getTime()));
                        }
                    }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), false);
            timePickerDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void ChooseDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, (view, year, month, dayOfMonth) -> {
            myCalendar.set(year, month, dayOfMonth, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE));
            tvWhenDate.setText(Utility.EEE_dd_MMM_yyyy.format(myCalendar.getTime()));
            sWhenDate = Utility.yyyy_MM_dd.format(myCalendar.getTime());
        },
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();


    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "MM-dd-yyyy";
        String outputPattern = "EEE, MMM dd yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String parseDateToddMMyyyy3(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "EEE, MMM dd yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String parseDateToddMMyyyy2(String time) {
        String inputPattern = "EEE, MMM dd yyyy";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(Objects.requireNonNull(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    private void listOfBeforeAlarm(ReminderBean reminderModel) {

        beforeTimeModelList = new ArrayList<>();
        for (int i = 0; i < getResources().getStringArray(R.array.beforTime_reminder).length; i++) {
            BeforeTimeModel beforeTimeModel = new BeforeTimeModel();
            if (reminderModel == null) {
                if (i == 0) {
                    beforeTimeModel.setSelection(true);
                } else {
                    beforeTimeModel.setSelection(false);
                }
            } else {
                if (Integer.parseInt(reminderModel.getReminderBefore()) == getResources().getIntArray(R.array.beforTime_value_reminder)[i]) {
                    beforeTimeModel.setSelection(true);

                } else {
                    beforeTimeModel.setSelection(false);

                }
            }

            beforeTimeModel.setTimeName(getResources().getStringArray(R.array.beforTime_reminder)[i]);
            beforeTimeModel.setTimeValue(getResources().getIntArray(R.array.beforTime_value_reminder)[i]);
            beforeTimeModelList.add(beforeTimeModel);
        }
        for (int i = 0; i < beforeTimeModelList.size(); i++) {
            if (beforeTimeModelList.get(i).isSelection()) {
                tvBeforRemindTxt.setText(beforeTimeModelList.get(i).getTimeName());
                sReminder = beforeTimeModelList.get(i).getTimeName();
                valBefore = beforeTimeModelList.get(i).getTimeValue();
            }
        }

        repeatTimeModelList = new ArrayList<>();
        for (int i = 0; i < getResources().getStringArray(R.array.repeat_reminder).length; i++) {
            BeforeTimeModel beforeTimeModel = new BeforeTimeModel();
            if (reminderModel == null) {
                if (i == 0) {
                    beforeTimeModel.setSelection(true);
                } else {
                    beforeTimeModel.setSelection(false);
                }
            } else {
                if (reminderModel.getRepeat().equals("Once")) {
                    if (getResources().getString(R.string.Once).equalsIgnoreCase(getResources().getStringArray(R.array.repeat_reminder)[i])) {
                        beforeTimeModel.setSelection(true);
                    } else {
                        beforeTimeModel.setSelection(false);
                    }
                    Log.e("Repeat_once======>", reminderModel.getRepeat() + "===============>" + getResources().getStringArray(R.array.repeat_reminder)[i]);

                } else if (reminderModel.getRepeat().equals("Every Day")) {
                    if (getResources().getString(R.string.Every_Day).equalsIgnoreCase(getResources().getStringArray(R.array.repeat_reminder)[i])) {
                        beforeTimeModel.setSelection(true);
                    } else {
                        beforeTimeModel.setSelection(false);
                    }
                    Log.e("RepeatEvery_Day======>", reminderModel.getRepeat() + "===============>" + getResources().getStringArray(R.array.repeat_reminder)[i]);

                } else if (reminderModel.getRepeat().equals("Every Week")) {
                    if (getResources().getString(R.string.Every_Week).equalsIgnoreCase(getResources().getStringArray(R.array.repeat_reminder)[i])) {
                        beforeTimeModel.setSelection(true);
                    } else {
                        beforeTimeModel.setSelection(false);
                    }
                    Log.e("RepeatEvery_Week======>", reminderModel.getRepeat() + "===============>" + getResources().getStringArray(R.array.repeat_reminder)[i]);

                } else if (reminderModel.getRepeat().equals("Every Year")) {
                    if (getResources().getString(R.string.Every_Year).equalsIgnoreCase(getResources().getStringArray(R.array.repeat_reminder)[i])) {
                        beforeTimeModel.setSelection(true);
                    } else {
                        beforeTimeModel.setSelection(false);
                    }
                    Log.e("RepeatEvery_Year======>", reminderModel.getRepeat() + "===============>" + getResources().getStringArray(R.array.repeat_reminder)[i]);

                }


            }

            beforeTimeModel.setTimeName(getResources().getStringArray(R.array.repeat_reminder)[i]);
            beforeTimeModel.setTimeValue(0);
            repeatTimeModelList.add(beforeTimeModel);
        }

        for (int i = 0; i < repeatTimeModelList.size(); i++) {
            if (repeatTimeModelList.get(i).isSelection()) {
                tvRepeat.setText(repeatTimeModelList.get(i).getTimeName());
                sRepeat = repeatTimeModelList.get(i).getTimeName();

            }
        }
    }

    private void DlgBeforRemind() {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.custom_popup_befor_remind);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Switch tbbeforeRemindTog;
        TextView cancel, ok;
        RecyclerView rvbeforeReminder;

        ok = dialog.findViewById(R.id.ok);
        cancel = dialog.findViewById(R.id.cancel);
        tbbeforeRemindTog = dialog.findViewById(R.id.tb_remind);
        tbbeforeRemindTog.setChecked(istbbeforeRemindTog);
        rvbeforeReminder = dialog.findViewById(R.id.rv_repeat_reminder);
        CustomPopupAdapter adapter = new CustomPopupAdapter(this, beforeTimeModelList, false);
        adapter.setBeforeTimeListener(beforeTimeModel -> {
            for (int i = 0; i < beforeTimeModelList.size(); i++) {
                beforeTimeModelList.get(i).setSelection(false);
                if (beforeTimeModel.getTimeName().equalsIgnoreCase(beforeTimeModelList.get(i).getTimeName())) {
                    beforeTimeModel.setSelection(true);
                    beforeTimeModelList.set(i, beforeTimeModel);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        rvbeforeReminder.setAdapter(adapter);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelectOne = false;
                for (int i = 0; i < beforeTimeModelList.size(); i++) {
                    if (beforeTimeModelList.get(i).isSelection()) {
                        isSelectOne = true;
                        sReminder = beforeTimeModelList.get(i).getTimeName();
                        valBefore = beforeTimeModelList.get(i).getTimeValue();
                    }
                }
                if (isSelectOne) {
                    tvBeforRemindTxt.setText(sReminder);
                    dialog.dismiss();
                } else {

                    Utility.ShowToast(mContext, getResources().getString(R.string.please_select_one_option));


                }
            }
        });
        dialog.show();
    }

    private void DlgRepeatRemind() {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.custom_popup_repeat_new);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        RecyclerView rvRepeatReminder;
        TextView ok, cancel;
        ok = dialog.findViewById(R.id.ok);
        cancel = dialog.findViewById(R.id.cancel);

        rvRepeatReminder = dialog.findViewById(R.id.rv_repeat_repeat);
        CustomPopupAdapter adapter = new CustomPopupAdapter(this, repeatTimeModelList, true);
        adapter.setBeforeTimeListener(beforeTimeModel -> {
            for (int i = 0; i < repeatTimeModelList.size(); i++) {
                repeatTimeModelList.get(i).setSelection(false);
                if (beforeTimeModel.getTimeName().equalsIgnoreCase(repeatTimeModelList.get(i).getTimeName())) {
                    beforeTimeModel.setSelection(true);
                    repeatTimeModelList.set(i, beforeTimeModel);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        rvRepeatReminder.setAdapter(adapter);

        cancel.setOnClickListener(v -> dialog.dismiss());

        ok.setOnClickListener(v -> {
            for (int i = 0; i < repeatTimeModelList.size(); i++) {
                if (repeatTimeModelList.get(i).isSelection()) {
                    sRepeat = repeatTimeModelList.get(i).getTimeName();
                    tvRepeat.setText(repeatTimeModelList.get(i).getTimeName());

                }
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
