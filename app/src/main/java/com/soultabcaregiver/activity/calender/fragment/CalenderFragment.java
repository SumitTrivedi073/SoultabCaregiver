package com.soultabcaregiver.activity.calender.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.calender.CalenderModel.AllEventModel;
import com.soultabcaregiver.activity.calender.CalenderModel.ReminderBean;
import com.soultabcaregiver.activity.calender.adapter.CustomEventAdapter;
import com.soultabcaregiver.activity.reminder.AddReminderActivity;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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
        //  new ReminderCreateClass(getActivity());
    
        if (Utility.isNetworkConnected(mContext)) {
            GetAllEventAPI(FromDate, TODate);//for list data
        } else {
            Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
        }
    
        daily.setBackgroundColor(ContextCompat.getColor(mContext, R.color.muzli_color));
        weekly.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        Monthly.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
    
        daily.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        weekly.setTextColor(ContextCompat.getColor(mContext, R.color.blackish));
        Monthly.setTextColor(ContextCompat.getColor(mContext, R.color.blackish));
    
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
    
            case R.id.curret_date_txt:
        
                calendarPopup();
        
                break;
    
            case R.id.daily:
                daily.setBackgroundColor(ContextCompat.getColor(mContext, R.color.muzli_color));
                weekly.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                Monthly.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        
                daily.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                weekly.setTextColor(ContextCompat.getColor(mContext, R.color.blackish));
                Monthly.setTextColor(ContextCompat.getColor(mContext, R.color.blackish));
        
                Daily_select = true;
                Weekly_select = false;
                Monthly_select = false;
        
                FromDate = Utility.yyyy_MM_dd.format(calendar.getTime());
                TODate = Utility.yyyy_MM_dd.format(calendar.getTime());
        
                try {
        
                    curret_date_txt.setText(Utility.MMM_dd_yyyy.format(Utility.yyyy_MM_dd.parse(
                            FromDate)) + " - " + Utility.MMM_dd_yyyy.format(
                            Utility.yyyy_MM_dd.parse(TODate)));
            
                } catch (ParseException e) {
                    e.printStackTrace();
                }
        
                GetAllEventAPI(FromDate, TODate);
        
                break;
    
            case R.id.weekly:
        
                daily.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                weekly.setBackgroundColor(ContextCompat.getColor(mContext, R.color.muzli_color));
                Monthly.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        
                daily.setTextColor(ContextCompat.getColor(mContext, R.color.blackish));
                weekly.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                Monthly.setTextColor(ContextCompat.getColor(mContext, R.color.blackish));
        
                Daily_select = false;
                Weekly_select = true;
                Monthly_select = false;


              /*  try {
                    TODate = Utility.getCalculatedDate(FromDate, "yyyy-MM-dd", 7);

                    curret_date_txt.setText(Utility.MMM_dd_yyyy.format(Utility.yyyy_MM_dd.parse
                    (FromDate)) + " - "
                            + Utility.MMM_dd_yyyy.format(Utility.yyyy_MM_dd.parse(TODate)));

                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
    
                LocalDate today = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    today = LocalDate.parse(Utility.yyyy_MM_dd.format(calendar.getTime()));
        
                    // Go backward to get Monday
                    LocalDate monday = today;
                    while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
                        monday = monday.minusDays(1);
                    }
        
                    // Go forward to get Sunday
                    LocalDate sunday = today;
                    while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
                        sunday = sunday.plusDays(1);
                    }
        
                    FromDate = monday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    TODate = sunday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
                    try {
                        curret_date_txt.setText(Utility.MMM_dd_yyyy.format(Utility.yyyy_MM_dd.parse(
                                FromDate)) + " - " + Utility.MMM_dd_yyyy.format(
                                Utility.yyyy_MM_dd.parse(TODate)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
    
                }
        
                GetAllEventAPI(FromDate, TODate);
        
                break;
    
            case R.id.Monthly:
        
                daily.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                weekly.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                Monthly.setBackgroundColor(ContextCompat.getColor(mContext, R.color.muzli_color));
        
                daily.setTextColor(ContextCompat.getColor(mContext, R.color.blackish));
                weekly.setTextColor(ContextCompat.getColor(mContext, R.color.blackish));
                Monthly.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        
                Daily_select = false;
                Weekly_select = false;
                Monthly_select = true;
        
                try {
                    // TODate = Utility.getCalculatedDate(FromDate, "yyyy-MM-dd", 30);
            
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        
                        LocalDate today1 = LocalDate.now();
        
                        LocalDate firstdayofmonth = today1.withDayOfMonth(1);
                        System.out.println("First day: " + firstdayofmonth);
        
                        LocalDate lastdayofmonth = today1.withDayOfMonth(today1.lengthOfMonth());
                        System.out.println("Last day: " + lastdayofmonth);
                        FromDate =
                                firstdayofmonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        TODate = lastdayofmonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
                    }
                    curret_date_txt.setText(Utility.MMM_dd_yyyy.format(Utility.yyyy_MM_dd.parse(
                            FromDate)) + " - " + Utility.MMM_dd_yyyy.format(
                            Utility.yyyy_MM_dd.parse(TODate)));
    
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
        
                    try {
                        curret_date_txt.setText(Utility.MMM_dd_yyyy.format(Utility.yyyy_MM_dd.parse(
                                FromDate)) + " - " + Utility.MMM_dd_yyyy.format(
                                Utility.yyyy_MM_dd.parse(TODate)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
        
                }
                if (Weekly_select) {
                    /*try {
                        TODate = Utility.getCalculatedDate(FromDate, "yyyy-MM-dd", 7);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }*/
        
                    LocalDate today = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        today = LocalDate.parse(Utility.yyyy_MM_dd.format(calendar.getTime()));
            
                        // Go backward to get Monday
                        LocalDate monday = today;
                        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
                            monday = monday.minusDays(1);
                        }
            
                        // Go forward to get Sunday
                        LocalDate sunday = today;
                        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
                            sunday = sunday.plusDays(1);
                        }
            
                        FromDate = monday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        TODate = sunday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            
                    }
                }
                if (Monthly_select) {
                   /* try {
                        TODate = Utility.getCalculatedDate(FromDate, "yyyy-MM-dd", 30);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }*/
    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        
                        LocalDate today1 = LocalDate.now();
        
                        LocalDate firstdayofmonth = today1.withDayOfMonth(1);
                        System.out.println("First day: " + firstdayofmonth);
        
                        LocalDate lastdayofmonth = today1.withDayOfMonth(today1.lengthOfMonth());
                        System.out.println("Last day: " + lastdayofmonth);
                        FromDate =
                                firstdayofmonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        TODate = lastdayofmonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
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
                        if (String.valueOf(allEventModel.getStatusCode()).equals("200")) {
                            if (allEventModel.getResponse().getActivities().getReminders() != null &&
                                    allEventModel.getResponse().getActivities().getReminders().size() > 0) {
                                arRemin = allEventModel.getResponse().getActivities().getReminders();
                            }
                            for (int i = 0; i < allEventModel.getResponse().getActivities().getAppointments().size(); i++) {
                                ReminderBean reminderBean = new ReminderBean();

                                reminderBean.setId(allEventModel.getResponse().getActivities().getAppointments().get(i).getId());
                                reminderBean.setTitle(getResources().getString(R.string.appointment));//title
                                reminderBean.setDate(allEventModel.getResponse().getActivities().getAppointments().get(i).getSelectedDate());
                                reminderBean.setTime(allEventModel.getResponse().getActivities().getAppointments().get(i).getScheduleTime());//time
                                reminderBean.setAppointment(true);
                                reminderBean.setDoctor_id(allEventModel.getResponse().getActivities().getAppointments().get(i).getDoctor_id());
                                reminderBean.setDoctor_Email(allEventModel.getResponse().getActivities().getAppointments().get(i).getEmail());
                                reminderBean.setDoctor_Fax(allEventModel.getResponse().getActivities().getAppointments().get(i).getFax());
                                reminderBean.setDoctor_Website(allEventModel.getResponse().getActivities().getAppointments().get(i).getWebsite());

                                arRemin.add(reminderBean);
                            }
                            tvNodata.setVisibility(View.GONE);
                            CustomEventAdapter adapter = new CustomEventAdapter(mContext, arRemin,  tvNodata, FromDate2);
                            rvReminder.setAdapter(adapter);

                        }else if (String.valueOf(allEventModel.getStatusCode()).equals("403")) {
                            logout_app(response.getString("message"));
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
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext,APIS.EncodeUser_id));

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}
