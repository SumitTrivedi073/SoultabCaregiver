package com.soultabcaregiver.activity.calender.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.calender.CalenderModel.AllEventModel;
import com.soultabcaregiver.activity.calender.CalenderModel.ReminderBean;
import com.soultabcaregiver.activity.calender.adapter.CustomEventAdapter;
import com.soultabcaregiver.activity.reminder.AddReminderActivity;
import com.soultabcaregiver.sinch_calling.BaseFragment;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalenderFragment extends BaseFragment implements View.OnClickListener {

    Context mContext;
    View view;
    RecyclerView rvReminder;
    TextView tvNodata, daily, weekly, Monthly, curret_date_txt;
    FloatingActionButton reminder_btn;
    ArrayList<String> eventDays;
    String FromDate = "", FromDate2 = "", TODate = "", TODate2 = "";
    Calendar calendar, calendar1;
    AlertDialog alertDialog;
    boolean isFirstTimeCalendar = true, Daily_select = true, Weekly_select = false, Monthly_select = false;
    List<ReminderBean> arRemin;
    boolean isFirstTimeShowLoader = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calender, container, false);

        calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, mDay);

        FromDate2 = Utility.MM_dd_yyyy.format(calendar.getTime());
        TODate2 = Utility.MM_dd_yyyy.format(calendar.getTime());
        FromDate = Utility.yyyy_MM_dd.format(calendar.getTime());
        TODate = Utility.yyyy_MM_dd.format(calendar.getTime());

        init();
        Listener();

        return view;
    }

    private void init() {
        tvNodata = view.findViewById(R.id.tv_no_data);
        rvReminder = view.findViewById(R.id.rv_event);
        curret_date_txt = view.findViewById(R.id.curret_date_txt);
        daily = view.findViewById(R.id.daily);
        weekly = view.findViewById(R.id.weekly);
        Monthly = view.findViewById(R.id.Monthly);
        reminder_btn = view.findViewById(R.id.reminder_btn);


        curret_date_txt.setText(Utility.MMM_dd_yyyy.format(calendar.getTime()) + " - "
                + Utility.MMM_dd_yyyy.format(calendar.getTime()));
    }

    @Override
    public void onResume() {
        super.onResume();
        // new ReminderCreateClass(getActivity());

        if (Utility.isNetworkConnected(mContext)) {
            GetAllEventAPI(FromDate, TODate);//for list data
        } else {
            Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
        }

        daily.setBackgroundColor(getResources().getColor(R.color.muzli_color));
        weekly.setBackgroundColor(getResources().getColor(R.color.white));
        Monthly.setBackgroundColor(getResources().getColor(R.color.white));

        daily.setTextColor(getResources().getColor(R.color.white));
        weekly.setTextColor(getResources().getColor(R.color.blackish));
        Monthly.setTextColor(getResources().getColor(R.color.blackish));

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.curret_date_txt:

                calendarPopup();

                break;


            case R.id.daily:
                daily.setBackgroundColor(getResources().getColor(R.color.muzli_color));
                weekly.setBackgroundColor(getResources().getColor(R.color.white));
                Monthly.setBackgroundColor(getResources().getColor(R.color.white));

                daily.setTextColor(getResources().getColor(R.color.white));
                weekly.setTextColor(getResources().getColor(R.color.blackish));
                Monthly.setTextColor(getResources().getColor(R.color.blackish));


                Daily_select = true;
                Weekly_select = false;
                Monthly_select = false;

                TODate = Utility.yyyy_MM_dd.format(calendar.getTime());


                try {

                    curret_date_txt.setText(Utility.MMM_dd_yyyy.format(Utility.yyyy_MM_dd.parse(FromDate)) + " - "
                            + Utility.MMM_dd_yyyy.format(Utility.yyyy_MM_dd.parse(TODate)));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                GetAllEventAPI(FromDate, TODate);

                break;

            case R.id.weekly:

                daily.setBackgroundColor(getResources().getColor(R.color.white));
                weekly.setBackgroundColor(getResources().getColor(R.color.muzli_color));
                Monthly.setBackgroundColor(getResources().getColor(R.color.white));

                daily.setTextColor(getResources().getColor(R.color.blackish));
                weekly.setTextColor(getResources().getColor(R.color.white));
                Monthly.setTextColor(getResources().getColor(R.color.blackish));


                Daily_select = false;
                Weekly_select = true;
                Monthly_select = false;


                try {
                    TODate = Utility.getCalculatedDate(FromDate, "yyyy-MM-dd", 7);

                    curret_date_txt.setText(Utility.MMM_dd_yyyy.format(Utility.yyyy_MM_dd.parse(FromDate)) + " - "
                            + Utility.MMM_dd_yyyy.format(Utility.yyyy_MM_dd.parse(TODate)));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Log.e("TODate", TODate);

                GetAllEventAPI(FromDate, TODate);

                break;

            case R.id.Monthly:

                daily.setBackgroundColor(getResources().getColor(R.color.white));
                weekly.setBackgroundColor(getResources().getColor(R.color.white));
                Monthly.setBackgroundColor(getResources().getColor(R.color.muzli_color));

                daily.setTextColor(getResources().getColor(R.color.blackish));
                weekly.setTextColor(getResources().getColor(R.color.blackish));
                Monthly.setTextColor(getResources().getColor(R.color.white));


                Daily_select = false;
                Weekly_select = false;
                Monthly_select = true;

                try {
                    TODate = Utility.getCalculatedDate(FromDate, "yyyy-MM-dd", 30);

                    curret_date_txt.setText(Utility.MMM_dd_yyyy.format(Utility.yyyy_MM_dd.parse(FromDate)) + " - "
                            + Utility.MMM_dd_yyyy.format(Utility.yyyy_MM_dd.parse(TODate)));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                GetAllEventAPI(FromDate, TODate);

                break;

            case R.id.reminder_btn:

                Intent intent = new Intent(mContext, AddReminderActivity.class);
                startActivity(intent);

                break;

        }
    }

    private void Listener() {
        daily.setOnClickListener(this);
        weekly.setOnClickListener(this);
        Monthly.setOnClickListener(this);
        reminder_btn.setOnClickListener(this);
        curret_date_txt.setOnClickListener(this);
    }


    private void calendarPopup() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.calender_popup,
                null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);

        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent_black);


        CalendarView calendarView = layout.findViewById(R.id.calender_view);
        TextView Ok = layout.findViewById(R.id.ok);
        TextView cancel = layout.findViewById(R.id.cancel);

        calendar1 = Calendar.getInstance();
        int mYear = calendar1.get(Calendar.YEAR);
        int mMonth = calendar1.get(Calendar.MONTH);
        int mDay = calendar1.get(Calendar.DAY_OF_MONTH);
        calendar1.set(Calendar.DAY_OF_MONTH, mDay);

        long endOfMonth = calendar1.getTimeInMillis();
        calendarView.setMaxDate(endOfMonth);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month,
                                            int dayOfMonth) {

                calendar1 = new GregorianCalendar(year, month, dayOfMonth);
                FromDate2 = Utility.MM_dd_yyyy.format(calendar1.getTime());
                FromDate = Utility.yyyy_MM_dd.format(calendar1.getTime());


                if (Daily_select) {

                    TODate = Utility.yyyy_MM_dd.format(calendar1.getTime());
                }
                if (Weekly_select) {
                    try {
                        TODate = Utility.getCalculatedDate(FromDate, "yyyy-MM-dd", 7);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (Monthly_select) {
                    try {
                        TODate = Utility.getCalculatedDate(FromDate, "yyyy-MM-dd", 30);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


            }
        });


        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    curret_date_txt.setText(Utility.MMM_dd_yyyy.format(Utility.yyyy_MM_dd.parse(FromDate)) + " - "
                            + Utility.MMM_dd_yyyy.format(Utility.yyyy_MM_dd.parse(TODate)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                GetAllEventAPI(FromDate, TODate);

                alertDialog.dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


    }

    private void GetAllEventAPI(String sSelDate, String TODate) {
        arRemin = new ArrayList<>();
        final String TAG = "GetAllEventAPI";
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("user_id", Utility.getSharedPreferences(mContext, APIS.user_id));
            mainObject.put("from_date", sSelDate);
            mainObject.put("to_date", TODate);


            Log.e(TAG, "GET ALL EVENT API========>" + mainObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();

        }
        if (isFirstTimeShowLoader)
            showProgressDialog(mContext, getResources().getString(R.string.Loading));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.EVENTLIST, mainObject,
                response -> {
                    Log.e(TAG, "GetReminderList response=" + response.toString());
                    hideProgressDialog();
                    isFirstTimeShowLoader = false;
                    try {
                        AllEventModel allEventModel = new Gson().fromJson(response.toString(), AllEventModel.class);
                        if (allEventModel.getStatus().equalsIgnoreCase("true")) {
                            arRemin = allEventModel.getResponse().getActivities().getReminders();

                            for (int i = 0; i < allEventModel.getResponse().getActivities().getAppointments().size(); i++) {
                                ReminderBean reminderBean = new ReminderBean();

                                reminderBean.setId(allEventModel.getResponse().getActivities().getAppointments().get(i).getId());
                                reminderBean.setTitle(getResources().getString(R.string.appointment));//title
                                reminderBean.setDate(allEventModel.getResponse().getActivities().getAppointments().get(i).getSelectedDate());
                                reminderBean.setTime(allEventModel.getResponse().getActivities().getAppointments().get(i).getScheduleTime());//time
                                reminderBean.setAppointment(true);
                                arRemin.add(reminderBean);
                            }


                            tvNodata.setVisibility(View.GONE);
                            CustomEventAdapter adapter = new CustomEventAdapter(mContext, arRemin, tvNodata, Utility.MMM_dd_yyyy.format(Utility.yyyy_MM_dd.parse(FromDate)));
                            rvReminder.setAdapter(adapter);

                        } else {
                            rvReminder.setAdapter(null);
                            tvNodata.setVisibility(View.VISIBLE);
                            tvNodata.setText(getResources().getString(R.string.no_activity_scheduled) + " " + Utility.MMM_dd_yyyy.format(Utility.yyyy_MM_dd.parse(FromDate)));

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            hideProgressDialog();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}