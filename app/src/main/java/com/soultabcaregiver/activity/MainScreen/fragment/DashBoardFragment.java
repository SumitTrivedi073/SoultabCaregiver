package com.soultabcaregiver.activity.MainScreen.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.soultabcaregiver.Model.DiloagBoxCommon;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.MainScreen.MainActivity;
import com.soultabcaregiver.activity.MainScreen.adapter.BarChartAdapter;
import com.soultabcaregiver.activity.MainScreen.model.ChartModel;
import com.soultabcaregiver.reminder_ring_class.ReminderCreateClass;
import com.soultabcaregiver.sinch_calling.BaseFragment;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashBoardFragment extends BaseFragment implements View.OnClickListener {

    View view;
    Context mContext;
    RelativeLayout logout;
    LineChart lineChart;
    RecyclerView bar_chart_list;
    ProgressDialog progressDialog;
    LinearLayout name_event_linear;
    CardView compliance_card;
    LineDataSet lineDataSet, lineDataSet2, lineDataSet3, lineDataSet4, lineDataSet5;
    LineData data;
    CheckBox weekly_chart, three_month_chart, six_month_chart, twelve_month_chart;
    TextView today_txt, lastweek_txt, lastmonth_txt, good_morning_txt, user_name_txt,
            compliance_count_txt, compliance_name_txt, no_data_txt, last_seen_txt;
    MainActivity mainActivity;
    Calendar calendar;
    ChartModel chartModel;
    String chart_value_data = "week";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dash_board, container, false);

        mainActivity = MainActivity.instance;
        lineChart = (LineChart) view.findViewById(R.id.lineChart);
        bar_chart_list = view.findViewById(R.id.bar_chart_list);
        compliance_name_txt = view.findViewById(R.id.compliance_name_txt);
        compliance_count_txt = view.findViewById(R.id.compliance_count_txt);
        today_txt = view.findViewById(R.id.today_txt);
        lastweek_txt = view.findViewById(R.id.lastweek_txt);
        lastmonth_txt = view.findViewById(R.id.lastmonth_txt);
        name_event_linear = view.findViewById(R.id.name_event_linear);
        logout = view.findViewById(R.id.logout);
        good_morning_txt = view.findViewById(R.id.good_morning_txt);
        user_name_txt = view.findViewById(R.id.user_name_txt);
        no_data_txt = view.findViewById(R.id.no_data_txt);
        last_seen_txt = view.findViewById(R.id.last_seen_txt);
        compliance_card = view.findViewById(R.id.compliance_card);
        weekly_chart = view.findViewById(R.id.weekly_Chart);
        three_month_chart = view.findViewById(R.id.three_month_chart);
        six_month_chart = view.findViewById(R.id.six_month_chart);
        twelve_month_chart = view.findViewById(R.id.twelve_month_chart);

        calendar = Calendar.getInstance();

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, mDay);
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);


        if (timeOfDay >= 0 && timeOfDay < 12) {
            good_morning_txt.setText(getResources().getString(R.string.good_morning));

        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            good_morning_txt.setText(getResources().getString(R.string.good_afternoon));

        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            good_morning_txt.setText(getResources().getString(R.string.good_evening));

        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            good_morning_txt.setText(getResources().getString(R.string.good_night));
        }

        user_name_txt.setText(Utility.getSharedPreferences(mContext, APIS.Caregiver_name) + " " + Utility.getSharedPreferences(mContext, APIS.Caregiver_lastname));


        if (Utility.isNetworkConnected(mContext)) {
            ChartAPI(chart_value_data);

        } else {
            Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
        }

        listner();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        today_txt.setBackgroundColor(getResources().getColor(R.color.muzli_color));
        lastweek_txt.setBackgroundColor(getResources().getColor(R.color.white));
        lastmonth_txt.setBackgroundColor(getResources().getColor(R.color.white));

        today_txt.setTextColor(getResources().getColor(R.color.white));
        lastweek_txt.setTextColor(getResources().getColor(R.color.blackish));
        lastmonth_txt.setTextColor(getResources().getColor(R.color.blackish));

    }

    private void listner() {
        today_txt.setOnClickListener(this);
        lastweek_txt.setOnClickListener(this);
        lastmonth_txt.setOnClickListener(this);
        logout.setOnClickListener(this);

        weekly_chart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Utility.isNetworkConnected(mContext)) {
                    if(isChecked) {

                        weekly_chart.setChecked(true);
                        three_month_chart.setChecked(false);
                        six_month_chart.setChecked(false);
                        twelve_month_chart.setChecked(false);
                        chart_value_data = "week";
                        ChartAPI2(chart_value_data);
                    }
                } else {
                    Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
                }

            }
        });
        three_month_chart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Utility.isNetworkConnected(mContext)) {
                    if(isChecked) {

                        weekly_chart.setChecked(false);
                        three_month_chart.setChecked(true);
                        six_month_chart.setChecked(false);
                        twelve_month_chart.setChecked(false);
                        chart_value_data = "3month";

                        ChartAPI2(chart_value_data);
                    }
                } else {
                    Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
                }
            }
        });
        six_month_chart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Utility.isNetworkConnected(mContext)) {
                    if(isChecked) {

                        weekly_chart.setChecked(false);
                        three_month_chart.setChecked(false);
                        six_month_chart.setChecked(true);
                        twelve_month_chart.setChecked(false);
                        chart_value_data = "6month";
                        ChartAPI2(chart_value_data);
                    }

                } else {
                    Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
                }
            }
        });
        twelve_month_chart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Utility.isNetworkConnected(mContext)) {

                    if(isChecked) {
                        twelve_month_chart.setChecked(true);
                        weekly_chart.setChecked(false);
                        three_month_chart.setChecked(false);
                        six_month_chart.setChecked(false);
                        chart_value_data = "12month";
                        ChartAPI2("12month");
                    }
                } else {
                    Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
                }
            }
        });


    }

    private void ChartAPI(String chart_value_data) {
        showProgressDialog(mContext, getResources().getString(R.string.Loading));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIS.BASEURL + APIS.LineChartAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        Log.e("response", response);
                        chartModel = new Gson().fromJson(response, ChartModel.class);
                        if (String.valueOf(chartModel.getOk()).equals("1")) {

                            getChartData(chartModel);

                        } else {

                            lineChart.setVisibility(View.GONE);
                            bar_chart_list.setVisibility(View.GONE);

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", error.toString());

                        hideProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", Utility.getSharedPreferences(mContext, APIS.user_id));
                params.put("range", chart_value_data);
                /*params.put("user_id", "878");
                params.put("range", "week");*/

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(stringRequest);
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    private void ChartAPI2(String chart_value_data) {
        showProgressDialog(mContext, getResources().getString(R.string.Loading));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIS.BASEURL + APIS.LineChartAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        Log.e("response", response);
                        chartModel = new Gson().fromJson(response, ChartModel.class);
                        final HashMap<Integer, String> numMap = new HashMap<>();
                        if (String.valueOf(chartModel.getOk()).equals("1")) {

                            getChartData(chartModel);

                        } else {

                            lineChart.setVisibility(View.GONE);
                            bar_chart_list.setVisibility(View.GONE);

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", error.toString());

                        hideProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", Utility.getSharedPreferences(mContext, APIS.user_id));
                params.put("range", chart_value_data);
                /*params.put("user_id", "878");
                params.put("range", "week");*/

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(stringRequest);
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    private void getChartData(ChartModel chartModel) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        lineDataSet = null;
        lineDataSet2 = null;
        lineDataSet3 = null;
        lineDataSet4 = null;
        lineDataSet5 = null;


        if (chartModel.getData().getLineChart().size() > 0 && chartModel.getData().getxLabel().size() > 0) {

            lineChart.setVisibility(View.VISIBLE);
            for (int i = 0; i < chartModel.getData().getLineChart().size(); i++) {
                if (chartModel.getData().getLineChart().get(i).getName().equals("Spirituality")) {
                    if (chartModel.getData().getLineChart().get(i).getYaxis().size() > 0) {
                        lineDataSet = new LineDataSet(datavalue1(chartModel.getData().getLineChart().get(i).getYaxis()), chartModel.getData().getLineChart().get(i).getName());
                    }
                }

                if (chartModel.getData().getLineChart().get(i).getName().equals("Personal")) {
                    if (chartModel.getData().getLineChart().get(i).getYaxis().size() > 0) {
                        lineDataSet2 = new LineDataSet(datavalue1(chartModel.getData().getLineChart().get(i).getYaxis()), chartModel.getData().getLineChart().get(i).getName());
                    }
                }
                if (chartModel.getData().getLineChart().get(i).getName().equals("Splash")) {
                    if (chartModel.getData().getLineChart().get(i).getYaxis().size() > 0) {
                        lineDataSet3 = new LineDataSet(datavalue1(chartModel.getData().getLineChart().get(i).getYaxis()), chartModel.getData().getLineChart().get(i).getName());
                    }
                }
                if (chartModel.getData().getLineChart().get(i).getName().equals("Yoga")) {
                    if (chartModel.getData().getLineChart().get(i).getYaxis().size() > 0) {
                        lineDataSet4 = new LineDataSet(datavalue1(chartModel.getData().getLineChart().get(i).getYaxis()), chartModel.getData().getLineChart().get(i).getName());
                    }
                }
                if (chartModel.getData().getLineChart().get(i).getName().equals("Social")) {
                    if (chartModel.getData().getLineChart().get(i).getYaxis().size() > 0) {
                        lineDataSet5 = new LineDataSet(datavalue1(chartModel.getData().getLineChart().get(i).getYaxis()), chartModel.getData().getLineChart().get(i).getName());
                    }
                }

            }

            if (chartModel.getData().getxLabel().size() > 0) {
                final ArrayList<String> xAxisLabel = new ArrayList<>();
                for (int k = 0; k < chartModel.getData().getxLabel().size(); k++) {
                    xAxisLabel.add(String.valueOf(chartModel.getData().getxLabel().get(k)));
                }
                lineChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisLabel));

            }



            if (lineDataSet != null && lineDataSet2 != null && lineDataSet3 != null && lineDataSet4 != null && lineDataSet5 != null) {
                lineDataSet.setLineWidth(3f);
                lineDataSet2.setLineWidth(3f);
                lineDataSet3.setLineWidth(3f);
                lineDataSet4.setLineWidth(3f);
                lineDataSet5.setLineWidth(3f);

                dataSets.add(lineDataSet);
                dataSets.add(lineDataSet2);
                dataSets.add(lineDataSet3);
                dataSets.add(lineDataSet4);
                dataSets.add(lineDataSet5);

                lineDataSet.setColor(Color.parseColor("#800080"));
                lineDataSet2.setColor(Color.GREEN);
                lineDataSet3.setColor(Color.BLUE);
                lineDataSet4.setColor(Color.YELLOW);
                lineDataSet5.setColor(Color.RED);

                data = new LineData(dataSets);


                lineChart.getAxisLeft().setTextColor(getResources().getColor(R.color.white));
                lineChart.getAxisRight().setTextColor(getResources().getColor(R.color.white));
                lineChart.getXAxis().setTextColor(getResources().getColor(R.color.white));
                lineChart.getLegend().setTextColor(getResources().getColor(R.color.white));
                lineChart.getDescription().setTextColor(getResources().getColor(R.color.white));


                colorchange(lineDataSet, lineDataSet2, lineDataSet3, lineDataSet4, lineDataSet5);

            }

        if (lineDataSet != null && lineDataSet2 != null && lineDataSet3 != null && lineDataSet4 != null && lineDataSet5 != null) {

                lineChart.setData(data);
                lineChart.invalidate();

            }


        }else {
            lineChart.setVisibility(View.GONE);

        }

        if (chartModel.getData().getBarChart().size() > 0) {
            bar_chart_list.setVisibility(View.VISIBLE);
            BarChartAdapter careGiverListAdapter = new BarChartAdapter(mContext, chartModel.getData().getBarChart());
            bar_chart_list.setHasFixedSize(true);
            bar_chart_list.setAdapter(careGiverListAdapter);
        } else {
            bar_chart_list.setVisibility(View.GONE);
        }


            try {

                last_seen_txt.setText(chartModel.getData().getDeviceData().getPrimaryUsername() + " Last Seen "
                        + Utility.EEEhh_mm_aa.format(Utility.yyyy_mm_dd_hh_mm_ss.parse(chartModel.getData().getDeviceData().getDeviceLastOnline())));
            } catch (Exception e) {
                e.printStackTrace();
            }

        if (chartModel.getData().getCompliance().getDaily().getType() != null) {

            name_event_linear.setVisibility(View.VISIBLE);
            no_data_txt.setVisibility(View.GONE);
            compliance_name_txt.setText(chartModel.getData().getCompliance().getDaily().getType());
            compliance_count_txt.setText(chartModel.getData().getCompliance().getDaily().getCount());
        } else {
            name_event_linear.setVisibility(View.GONE);
            no_data_txt.setVisibility(View.VISIBLE);
        }

    }

    private void colorchange(LineDataSet lineDataSet, LineDataSet lineDataSet2, LineDataSet
            lineDataSet3, LineDataSet lineDataSet4, LineDataSet lineDataSet5) {

        lineDataSet.setHighLightColor(Color.parseColor("#BEBEBE"));
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setValueTextColor(getResources().getColor(R.color.white));
        lineDataSet.setValueTextSize(5f);


        lineDataSet2.setHighLightColor(Color.parseColor("#BEBEBE"));
        lineDataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet2.setValueTextColor(getResources().getColor(R.color.white));
        lineDataSet2.setValueTextSize(5f);


        lineDataSet3.setHighLightColor(Color.parseColor("#BEBEBE"));
        lineDataSet3.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet3.setValueTextColor(getResources().getColor(R.color.white));
        lineDataSet3.setValueTextSize(5f);


        lineDataSet4.setHighLightColor(Color.parseColor("#BEBEBE"));
        lineDataSet4.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet4.setValueTextColor(getResources().getColor(R.color.white));
        lineDataSet4.setValueTextSize(5f);


        lineDataSet5.setHighLightColor(Color.parseColor("#BEBEBE"));
        lineDataSet5.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet5.setValueTextColor(getResources().getColor(R.color.white));
        lineDataSet5.setValueTextSize(5f);

    }

    private ArrayList<Entry> datavalue1(List<String> yAxis) {

        ArrayList<Entry> datavalue = new ArrayList<Entry>();

        try {
            if (yAxis.size() > 0) {
                for (int j = 0; j < yAxis.size(); j++) {

                    String yAxisValue = yAxis.get(j).trim();

                    float yflot = Float.parseFloat(yAxisValue);

                    datavalue.add(new Entry(j, yflot));


                }


            }
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }


        return datavalue;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.today_txt:
                today_txt.setBackgroundColor(getResources().getColor(R.color.muzli_color));
                lastweek_txt.setBackgroundColor(getResources().getColor(R.color.white));
                lastmonth_txt.setBackgroundColor(getResources().getColor(R.color.white));

                today_txt.setTextColor(getResources().getColor(R.color.white));
                lastweek_txt.setTextColor(getResources().getColor(R.color.blackish));
                lastmonth_txt.setTextColor(getResources().getColor(R.color.blackish));

                Log.e("getDaily", String.valueOf(chartModel.getData().getCompliance().getDaily()));
                if (chartModel != null) {
                    if (chartModel.getData().getCompliance().getDaily().getType() != null) {

                        name_event_linear.setVisibility(View.VISIBLE);
                        no_data_txt.setVisibility(View.GONE);
                        compliance_name_txt.setText(chartModel.getData().getCompliance().getDaily().getType());
                        compliance_count_txt.setText(chartModel.getData().getCompliance().getDaily().getCount());
                    } else {
                        name_event_linear.setVisibility(View.GONE);
                        no_data_txt.setVisibility(View.VISIBLE);
                    }
                } else {
                    name_event_linear.setVisibility(View.GONE);
                    no_data_txt.setVisibility(View.VISIBLE);

                }

                break;

            case R.id.lastweek_txt:

                today_txt.setBackgroundColor(getResources().getColor(R.color.white));
                lastweek_txt.setBackgroundColor(getResources().getColor(R.color.muzli_color));
                lastmonth_txt.setBackgroundColor(getResources().getColor(R.color.white));

                today_txt.setTextColor(getResources().getColor(R.color.blackish));
                lastweek_txt.setTextColor(getResources().getColor(R.color.white));
                lastmonth_txt.setTextColor(getResources().getColor(R.color.blackish));
                if (chartModel != null) {
                    if (chartModel.getData().getCompliance().getWeekly().getType() != null) {

                        name_event_linear.setVisibility(View.VISIBLE);
                        no_data_txt.setVisibility(View.GONE);
                        compliance_name_txt.setText(chartModel.getData().getCompliance().getWeekly().getType());
                        compliance_count_txt.setText(chartModel.getData().getCompliance().getWeekly().getCount());
                    } else {
                        name_event_linear.setVisibility(View.GONE);
                        no_data_txt.setVisibility(View.VISIBLE);
                    }
                } else {
                    name_event_linear.setVisibility(View.GONE);
                    no_data_txt.setVisibility(View.VISIBLE);

                }

                break;

            case R.id.lastmonth_txt:

                today_txt.setBackgroundColor(getResources().getColor(R.color.white));
                lastweek_txt.setBackgroundColor(getResources().getColor(R.color.white));
                lastmonth_txt.setBackgroundColor(getResources().getColor(R.color.muzli_color));

                today_txt.setTextColor(getResources().getColor(R.color.blackish));
                lastweek_txt.setTextColor(getResources().getColor(R.color.blackish));
                lastmonth_txt.setTextColor(getResources().getColor(R.color.white));


                if (chartModel != null) {
                    if (chartModel.getData().getCompliance().getMonthly().getType() != null) {

                        name_event_linear.setVisibility(View.VISIBLE);
                        no_data_txt.setVisibility(View.GONE);
                        compliance_name_txt.setText(chartModel.getData().getCompliance().getMonthly().getType());
                        compliance_count_txt.setText(chartModel.getData().getCompliance().getMonthly().getCount());
                    } else {
                        name_event_linear.setVisibility(View.GONE);
                        no_data_txt.setVisibility(View.VISIBLE);
                    }
                } else {
                    name_event_linear.setVisibility(View.GONE);
                    no_data_txt.setVisibility(View.VISIBLE);

                }
                break;

            case R.id.logout:
                final DiloagBoxCommon diloagBoxCommon = Alertmessage(mContext, getResources().getString(R.string.logout)
                        , getResources().getString(R.string.are_you_sure_you_want_to_logout)
                        , getResources().getString(R.string.no_text)
                        , getResources().getString(R.string.yes_text));
                diloagBoxCommon.getTextView().setOnClickListener(v1 -> {
                    ReminderCreateClass.getInstance().DeleteReminderlogout();

                    if (mainActivity != null) {
                        diloagBoxCommon.getDialog().dismiss();
                        mainActivity.stopButtonClicked();
                        Utility.clearSharedPreference(mContext);

                    }

                });
                break;
        }
    }



}
