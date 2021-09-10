package com.soultabcaregiver.activity.daily_routine.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.daily_routine.model.DailyRoutineModel;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DailyRoutineFragment extends BaseFragment {

    public static DailyRoutineFragment instance;
    private final String TAG = getClass().getSimpleName();
    View view;
    Context mContext;
    CheckBox Morning_meal, Morning_snak, Morning_Medicine, Morning_Walk, Morning_Yoga, Morning_Meditation, Morning_Nap,
            Noon_meal, Noon_snak, Noon_Medicine, Noon_Walk, Noon_Yoga, Noon_Meditation, Noon_Nap,
            Evening_meal, Evening_snak, Evening_Medicine, Evening_Walk, Evening_Yoga, Evening_Meditation, Evening_Nap,
            Dinner_meal, Dinner_snak, Dinner_Medicine, Dinner_Walk, Dinner_Yoga, Dinner_Meditation, Dinner_Nap,
            BedTime_meal, BedTime_snak, BedTime_Medicine, BedTime_Walk, BedTime_Yoga, BedTime_Meditation, BedTime_Nap;

    String Morning_meal_str, Morning_snak_str, Morning_Medicine_str, Morning_Walk_str, Morning_Yoga_str, Morning_Meditation_str, Morning_Nap_str,
            Noon_meal_str, Noon_snak_str, Noon_Medicine_str, Noon_Walk_str, Noon_Yoga_str, Noon_Meditation_str, Noon_Nap_str,
            Evening_meal_str, Evening_snak_str, Evening_Medicine_str, Evening_Walk_str, Evening_Yoga_str, Evening_Meditation_str, Evening_Nap_str,
            Dinner_meal_str, Dinner_snak_str, Dinner_Medicine_str, Dinner_Walk_str, Dinner_Yoga_str, Dinner_Meditation_str, Dinner_Nap_str,
            BedTime_meal_str, BedTime_snak_str, BedTime_Medicine_str, BedTime_Walk_str, BedTime_Yoga_str, BedTime_Meditation_str, BedTime_Nap_str;

    String Morning, Noon, Evening, Dinner, BedTime;
    FloatingActionButton Submit_btn;
    List<String> Morninglist = new ArrayList<>();
    List<String> Noonlist = new ArrayList<>();
    List<String> Eveninglist = new ArrayList<>();
    List<String> Dinnerlist = new ArrayList<>();
    List<String> BedTimelist = new ArrayList<>();
    Calendar calendar;
    RelativeLayout show_daily_routine_Relative, hide_daily_routine_Relative;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_daily_routine, container, false);

        calendar = Calendar.getInstance();
        instance = DailyRoutineFragment.this;
        init();
        StringValue();
        Listner();
        dailyroutine_hideshow(Utility.getSharedPreferences(mContext,APIS.dailyroutine_hideshow));


        return view;
    }

    private void init() {
        Submit_btn = view.findViewById(R.id.Submit_btn);
        Morning_meal = view.findViewById(R.id.Morning_meal);
        Morning_snak = view.findViewById(R.id.Morning_snak);
        Morning_Medicine = view.findViewById(R.id.Morning_Medicine);
        Morning_Walk = view.findViewById(R.id.Morning_Walk);
        Morning_Yoga = view.findViewById(R.id.Morning_Yoga);
        Morning_Meditation = view.findViewById(R.id.Morning_Meditation);
        Morning_Nap = view.findViewById(R.id.Morning_Nap);
        Noon_meal = view.findViewById(R.id.Noon_meal);
        Noon_snak = view.findViewById(R.id.Noon_snak);
        Noon_Medicine = view.findViewById(R.id.Noon_Medicine);
        Noon_Walk = view.findViewById(R.id.Noon_Walk);
        Noon_Yoga = view.findViewById(R.id.Noon_Yoga);
        Noon_Meditation = view.findViewById(R.id.Noon_Meditation);
        Noon_Nap = view.findViewById(R.id.Noon_Nap);
        Evening_meal = view.findViewById(R.id.Evening_meal);
        Evening_snak = view.findViewById(R.id.Evening_snak);
        Evening_Medicine = view.findViewById(R.id.Evening_Medicine);
        Evening_Walk = view.findViewById(R.id.Evening_Walk);
        Evening_Yoga = view.findViewById(R.id.Evening_Yoga);
        Evening_Meditation = view.findViewById(R.id.Evening_Meditation);
        Evening_Nap = view.findViewById(R.id.Evening_Nap);
        Dinner_meal = view.findViewById(R.id.Dinner_meal);
        Dinner_snak = view.findViewById(R.id.Dinner_snak);
        Dinner_Medicine = view.findViewById(R.id.Dinner_Medicine);
        Dinner_Walk = view.findViewById(R.id.Dinner_Walk);
        Dinner_Yoga = view.findViewById(R.id.Dinner_Yoga);
        Dinner_Meditation = view.findViewById(R.id.Dinner_Meditation);
        Dinner_Nap = view.findViewById(R.id.Dinner_Nap);
        BedTime_meal = view.findViewById(R.id.BedTime_meal);
        BedTime_snak = view.findViewById(R.id.BedTime_snak);
        BedTime_Medicine = view.findViewById(R.id.BedTime_Medicine);
        BedTime_Walk = view.findViewById(R.id.BedTime_Walk);
        BedTime_Yoga = view.findViewById(R.id.BedTime_Yoga);
        BedTime_Meditation = view.findViewById(R.id.BedTime_Meditation);
        BedTime_Nap = view.findViewById(R.id.BedTime_Nap);
        show_daily_routine_Relative = view.findViewById(R.id.show_daily_routine_Relative);
        hide_daily_routine_Relative = view.findViewById(R.id.hide_daily_routine_Relative);

    }

    private void StringValue() {
        Morning_meal_str = getResources().getString(R.string.Meal);
        Morning_snak_str = getResources().getString(R.string.Snack);
        Morning_Medicine_str = getResources().getString(R.string.Medicine);
        Morning_Walk_str = getResources().getString(R.string.Walk);
        Morning_Yoga_str = getResources().getString(R.string.Yoga);
        Morning_Meditation_str = getResources().getString(R.string.Meditation);
        Morning_Nap_str = getResources().getString(R.string.Nap);

        Noon_meal_str = getResources().getString(R.string.Meal);
        Noon_snak_str = getResources().getString(R.string.Snack);
        Noon_Medicine_str = getResources().getString(R.string.Medicine);
        Noon_Walk_str = getResources().getString(R.string.Walk);
        Noon_Yoga_str = getResources().getString(R.string.Yoga);
        Noon_Meditation_str = getResources().getString(R.string.Meditation);
        Noon_Nap_str = getResources().getString(R.string.Nap);

        Evening_meal_str = getResources().getString(R.string.Meal);
        Evening_snak_str = getResources().getString(R.string.Snack);
        Evening_Medicine_str = getResources().getString(R.string.Medicine);
        Evening_Walk_str = getResources().getString(R.string.Walk);
        Evening_Yoga_str = getResources().getString(R.string.Yoga);
        Evening_Meditation_str = getResources().getString(R.string.Meditation);
        Evening_Nap_str = getResources().getString(R.string.Nap);

        Dinner_meal_str = getResources().getString(R.string.Meal);
        Dinner_snak_str = getResources().getString(R.string.Snack);
        Dinner_Medicine_str = getResources().getString(R.string.Medicine);
        Dinner_Walk_str = getResources().getString(R.string.Walk);
        Dinner_Yoga_str = getResources().getString(R.string.Yoga);
        Dinner_Meditation_str = getResources().getString(R.string.Meditation);
        Dinner_Nap_str = getResources().getString(R.string.Nap);

        BedTime_meal_str = getResources().getString(R.string.Meal);
        BedTime_snak_str = getResources().getString(R.string.Snack);
        BedTime_Medicine_str = getResources().getString(R.string.Medicine);
        BedTime_Walk_str = getResources().getString(R.string.Walk);
        BedTime_Yoga_str = getResources().getString(R.string.Yoga);
        BedTime_Meditation_str = getResources().getString(R.string.Meditation);
        BedTime_Nap_str = getResources().getString(R.string.Nap);

    }

    private void Listner() {
        Submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.getSharedPreferences(mContext, APIS.dailyroutine_hideshow).equals(APIS.Edit)) {

                    CheckedListner();
                }else {
                    Utility.ShowToast(mContext,mContext.getResources().getString(R.string.only_view_permission));
                }
            }
        });

    }


    public void dailyroutine_hideshow(String dailyroutine) {
        if (dailyroutine.equals(APIS.Hide)) {
            show_daily_routine_Relative.setVisibility(View.GONE);
            hide_daily_routine_Relative.setVisibility(View.VISIBLE);
        } else if (dailyroutine.equals(APIS.View)) {
            show_daily_routine_Relative.setVisibility(View.VISIBLE);
            hide_daily_routine_Relative.setVisibility(View.GONE);
            GetDailyRoutineData();
        }else if (dailyroutine.equals(APIS.Edit)) {
            show_daily_routine_Relative.setVisibility(View.VISIBLE);
            hide_daily_routine_Relative.setVisibility(View.GONE);
            Submit_btn.setVisibility(View.VISIBLE);
         }
    }

    private void CheckedListner() {
        Morninglist = new ArrayList<>();
        Noonlist = new ArrayList<>();
        Eveninglist = new ArrayList<>();
        Dinnerlist = new ArrayList<>();
        BedTimelist = new ArrayList<>();

        if (Morning_meal.isChecked()) {
            Morninglist.add(Morning_meal_str);
        } else {
            if (Morninglist.size() > 0) {
                RemoveFromArray(Morninglist, Morning_meal_str, 1);
            }
        }

        if (Morning_snak.isChecked()) {
            Morninglist.add(Morning_snak_str);
        } else {
            if (Morninglist.size() > 0) {
                RemoveFromArray(Morninglist, Morning_snak_str, 1);
            }
        }
        if (Morning_Medicine.isChecked()) {
            Morninglist.add(Morning_Medicine_str);
        } else {
            if (Morninglist.size() > 0) {
                RemoveFromArray(Morninglist, Morning_Medicine_str, 1);
            }
        }
        if (Morning_Walk.isChecked()) {
            Morninglist.add(Morning_Walk_str);
        } else {
            if (Morninglist.size() > 0) {
                RemoveFromArray(Morninglist, Morning_Walk_str, 1);
            }
        }
        if (Morning_Yoga.isChecked()) {
            Morninglist.add(Morning_Yoga_str);
        } else {
            if (Morninglist.size() > 0) {
                RemoveFromArray(Morninglist, Morning_Yoga_str, 1);
            }
        }
        if (Morning_Meditation.isChecked()) {
            Morninglist.add(Morning_Meditation_str);
        } else {
            if (Morninglist.size() > 0) {
                RemoveFromArray(Morninglist, Morning_Meditation_str, 1);
            }
        }
        if (Morning_Nap.isChecked()) {
            Morninglist.add(Morning_Nap_str);
        } else {
            if (Morninglist.size() > 0) {
                RemoveFromArray(Morninglist, Morning_Nap_str, 1);
            }
        }


        if (Noon_meal.isChecked()) {
            Noonlist.add(Noon_meal_str);
        } else {
            if (Noonlist.size() > 0) {
                RemoveFromArray(Noonlist, Noon_meal_str, 2);
            }
        }

        if (Noon_snak.isChecked()) {
            Noonlist.add(Noon_snak_str);
        } else {
            if (Noonlist.size() > 0) {
                RemoveFromArray(Noonlist, Noon_snak_str, 2);
            }
        }
        if (Noon_Medicine.isChecked()) {
            Noonlist.add(Noon_Medicine_str);
        } else {
            if (Noonlist.size() > 0) {
                RemoveFromArray(Noonlist, Noon_Medicine_str, 2);
            }
        }
        if (Noon_Walk.isChecked()) {
            Noonlist.add(Noon_Walk_str);
        } else {
            if (Noonlist.size() > 0) {
                RemoveFromArray(Noonlist, Noon_Walk_str, 2);
            }
        }
        if (Noon_Yoga.isChecked()) {
            Noonlist.add(Noon_Yoga_str);
        } else {
            if (Noonlist.size() > 0) {
                RemoveFromArray(Noonlist, Noon_Yoga_str, 2);
            }
        }
        if (Noon_Meditation.isChecked()) {
            Noonlist.add(Noon_Meditation_str);
        } else {
            if (Noonlist.size() > 0) {
                RemoveFromArray(Noonlist, Noon_Meditation_str, 2);
            }
        }
        if (Noon_Nap.isChecked()) {
            Noonlist.add(Noon_Nap_str);
        } else {
            if (Noonlist.size() > 0) {
                RemoveFromArray(Noonlist, Noon_Nap_str, 2);
            }
        }


        if (Evening_meal.isChecked()) {
            Eveninglist.add(Evening_meal_str);
        } else {
            if (Eveninglist.size() > 0) {
                RemoveFromArray(Eveninglist, Evening_meal_str, 3);
            }
        }
        if (Evening_snak.isChecked()) {
            Eveninglist.add(Evening_snak_str);
        } else {
            if (Eveninglist.size() > 0) {
                RemoveFromArray(Eveninglist, Evening_snak_str, 3);
            }
        }
        if (Evening_Medicine.isChecked()) {
            Eveninglist.add(Evening_Medicine_str);
        } else {
            if (Eveninglist.size() > 0) {
                RemoveFromArray(Eveninglist, Evening_Medicine_str, 3);
            }
        }
        if (Evening_Walk.isChecked()) {
            Eveninglist.add(Evening_Walk_str);
        } else {
            if (Eveninglist.size() > 0) {
                RemoveFromArray(Eveninglist, Evening_Walk_str, 3);
            }
        }
        if (Evening_Yoga.isChecked()) {
            Eveninglist.add(Evening_Yoga_str);
        } else {
            if (Eveninglist.size() > 0) {
                RemoveFromArray(Eveninglist, Evening_Yoga_str, 3);
            }
        }
        if (Evening_Meditation.isChecked()) {
            Eveninglist.add(Evening_Meditation_str);
        } else {
            if (Eveninglist.size() > 0) {
                RemoveFromArray(Eveninglist, Evening_Meditation_str, 3);
            }
        }
        if (Evening_Nap.isChecked()) {
            Eveninglist.add(Evening_Nap_str);
        } else {
            if (Eveninglist.size() > 0) {
                RemoveFromArray(Eveninglist, Evening_Nap_str, 3);
            }
        }


        if (Dinner_meal.isChecked()) {
            Dinnerlist.add(Dinner_meal_str);
        } else {
            if (Dinnerlist.size() > 0) {
                RemoveFromArray(Dinnerlist, Dinner_meal_str, 4);
            }
        }


        if (Dinner_snak.isChecked()) {
            Dinnerlist.add(Dinner_snak_str);
        } else {
            if (Dinnerlist.size() > 0) {
                RemoveFromArray(Dinnerlist, Dinner_snak_str, 4);
            }
        }

        if (Dinner_Medicine.isChecked()) {
            Dinnerlist.add(Dinner_Medicine_str);
        } else {
            if (Dinnerlist.size() > 0) {
                RemoveFromArray(Dinnerlist, Dinner_Medicine_str, 4);
            }
        }
        if (Dinner_Walk.isChecked()) {
            Dinnerlist.add(Dinner_Walk_str);
        } else {
            if (Dinnerlist.size() > 0) {
                RemoveFromArray(Dinnerlist, Dinner_Walk_str, 4);
            }
        }
        if (Dinner_Yoga.isChecked()) {
            Dinnerlist.add(Dinner_Yoga_str);
        } else {
            if (Dinnerlist.size() > 0) {
                RemoveFromArray(Dinnerlist, Dinner_Yoga_str, 4);
            }
        }
        if (Dinner_Meditation.isChecked()) {
            Dinnerlist.add(Dinner_Meditation_str);
        } else {
            if (Dinnerlist.size() > 0) {
                RemoveFromArray(Dinnerlist, Dinner_Meditation_str, 4);
            }
        }
        if (Dinner_Nap.isChecked()) {
            Dinnerlist.add(Dinner_Nap_str);
        } else {
            if (Dinnerlist.size() > 0) {
                RemoveFromArray(Dinnerlist, Dinner_Nap_str, 4);
            }
        }


        if (BedTime_meal.isChecked()) {
            BedTimelist.add(BedTime_meal_str);
        } else {
            if (BedTimelist.size() > 0) {
                RemoveFromArray(BedTimelist, BedTime_meal_str, 5);
            }
        }

        if (BedTime_snak.isChecked()) {
            BedTimelist.add(BedTime_snak_str);
        } else {
            if (BedTimelist.size() > 0) {
                RemoveFromArray(BedTimelist, BedTime_snak_str, 5);
            }
        }

        if (BedTime_Medicine.isChecked()) {
            BedTimelist.add(BedTime_Medicine_str);
        } else {
            if (BedTimelist.size() > 0) {
                RemoveFromArray(BedTimelist, BedTime_Medicine_str, 5);
            }
        }


        if (BedTime_Walk.isChecked()) {
            BedTimelist.add(BedTime_Walk_str);
        } else {
            if (BedTimelist.size() > 0) {
                RemoveFromArray(BedTimelist, BedTime_Walk_str, 5);
            }
        }
        if (BedTime_Yoga.isChecked()) {
            BedTimelist.add(BedTime_Yoga_str);
        } else {
            if (BedTimelist.size() > 0) {
                RemoveFromArray(BedTimelist, BedTime_Yoga_str, 5);
            }
        }
        if (BedTime_Meditation.isChecked()) {
            BedTimelist.add(BedTime_Meditation_str);
        } else {
            if (BedTimelist.size() > 0) {
                RemoveFromArray(BedTimelist, BedTime_Meditation_str, 5);
            }
        }
        if (BedTime_Nap.isChecked()) {
            BedTimelist.add(BedTime_Nap_str);
        } else {
            if (BedTimelist.size() > 0) {
                RemoveFromArray(BedTimelist, BedTime_Nap_str, 5);
            }
        }

        String[] morningStringArray = new String[Morninglist.size()];
        morningStringArray = Morninglist.toArray(morningStringArray);
        Morning = Arrays.toString(morningStringArray);

        String[] NoonStringArray = new String[Noonlist.size()];
        NoonStringArray = Noonlist.toArray(NoonStringArray);
        Noon = Arrays.toString(NoonStringArray);


        String[] EveningStringArray = new String[Eveninglist.size()];
        EveningStringArray = Eveninglist.toArray(EveningStringArray);
        Evening = Arrays.toString(EveningStringArray);


        String[] DinnerStringArray = new String[Dinnerlist.size()];
        DinnerStringArray = Dinnerlist.toArray(DinnerStringArray);
        Dinner = Arrays.toString(DinnerStringArray);


        String[] BedTymStringArray = new String[BedTimelist.size()];
        BedTymStringArray = BedTimelist.toArray(BedTymStringArray);
        BedTime = Arrays.toString(BedTymStringArray);

        Morning = SplitString(Morning);
        Noon = SplitString(Noon);
        Evening = SplitString(Evening);
        Dinner = SplitString(Dinner);
        BedTime = SplitString(BedTime);

        if (!Morning.equals("") || !Noon.equals("") || !Evening.equals("") || !Dinner.equals(
                "") || !BedTime.equals("")) {
            SubmitDailyRoutine();
        } else {
            Utility.ShowToast(mContext, getString(R.string.please_fill_daily_routine_form));
        }
    }

    private String SplitString(String splitvalue) {

        splitvalue = splitvalue.replace("[", "");
        splitvalue = splitvalue.replace("]", "");

        return splitvalue;

    }

    private void RemoveFromArray(List<String> list, String morning_meal_str, int diff) {
        switch (diff) {
            case 1:

                for (int i = 0; i < list.size(); i++) {

                    if (morning_meal_str.equals(list.get(i))) {
                        Morninglist.remove(i);

                    }
                }

                break;

            case 2:

                for (int i = 0; i < list.size(); i++) {

                    if (morning_meal_str.equals(list.get(i))) {
                        Noonlist.remove(i);
                    }

                }

                break;

            case 3:

                for (int i = 0; i < list.size(); i++) {

                    if (morning_meal_str.equals(list.get(i))) {
                        Eveninglist.remove(i);
                    }

                }

                break;

            case 4:

                for (int i = 0; i < list.size(); i++) {

                    if (morning_meal_str.equals(list.get(i))) {
                        Dinnerlist.remove(i);
                    }

                }


            case 5:

                for (int i = 0; i < list.size(); i++) {

                    if (morning_meal_str.equals(list.get(i))) {
                        BedTimelist.remove(i);
                    }

                }

                break;

        }

    }


    private void GetDailyRoutineData() {
        JSONObject mainObject = new JSONObject();

        try {

            mainObject.put("user_id", Utility.getSharedPreferences(mContext, APIS.user_id));
            mainObject.put("caregiver_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));
            mainObject.put("date", Utility.yyyy_MM_dd.format(calendar.getTime()));
            //mainObject.put("date", "2021-03-15");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "GetDailyRoutineData_API=  " + mainObject.toString());

            showProgressDialog(mContext, getResources().getString(R.string.Loading));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.GetDailyRoutineAPI, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "MakeDocAppointment response=" + response.toString());

                        hideProgressDialog();
                        DailyRoutineModel dailyRoutineModel = new Gson().fromJson(response.toString(), DailyRoutineModel.class);

                        try {
                            if (dailyRoutineModel.getStatusCode() == 200) {


                                Log.e("morning", String.valueOf(dailyRoutineModel.getResponse().getDailyRoutineData().getMorning().size()));
                                if (dailyRoutineModel.getResponse().getDailyRoutineData().getMorning() != null) {
                                    if (dailyRoutineModel.getResponse().getDailyRoutineData().getMorning().size() > 0) {

                                        CheckForloop(1, dailyRoutineModel.getResponse().getDailyRoutineData().getMorning());
                                    }
                                }

                                Log.e("Noon", String.valueOf(dailyRoutineModel.getResponse().getDailyRoutineData().getNoon().size()));

                                if (dailyRoutineModel.getResponse().getDailyRoutineData().getNoon() != null) {

                                    if (dailyRoutineModel.getResponse().getDailyRoutineData().getNoon().size() > 0) {
                                        CheckForloop(2, dailyRoutineModel.getResponse().getDailyRoutineData().getNoon());
                                    }
                                }


                                Log.e("Evening", String.valueOf(dailyRoutineModel.getResponse().getDailyRoutineData().getEvening().size()));
                                if (dailyRoutineModel.getResponse().getDailyRoutineData().getEvening() != null) {

                                    if (dailyRoutineModel.getResponse().getDailyRoutineData().getEvening().size() > 0) {
                                        CheckForloop(3, dailyRoutineModel.getResponse().getDailyRoutineData().getEvening());
                                    }
                                }

                                Log.e("Dinner", String.valueOf(dailyRoutineModel.getResponse().getDailyRoutineData().getDinner().size()));
                                if (dailyRoutineModel.getResponse().getDailyRoutineData().getDinner() != null) {

                                    if (dailyRoutineModel.getResponse().getDailyRoutineData().getDinner().size() > 0) {
                                        CheckForloop(4, dailyRoutineModel.getResponse().getDailyRoutineData().getDinner());
                                    }

                                }
                                Log.e("Bedtime", String.valueOf(dailyRoutineModel.getResponse().getDailyRoutineData().getBedtime().size()));
                                if (dailyRoutineModel.getResponse().getDailyRoutineData().getBedtime() != null) {

                                    if (dailyRoutineModel.getResponse().getDailyRoutineData().getBedtime().size() > 0) {
                                        CheckForloop(5, dailyRoutineModel.getResponse().getDailyRoutineData().getBedtime());
                                    }
                                }
                            } else if (String.valueOf(dailyRoutineModel.getStatusCode()).equals("403")) {
                                logout_app(dailyRoutineModel.getMessage());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
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
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void CheckForloop(int diff, List<String> list) {

        switch (diff) {
            case 1:

                for (int i = 0; i < list.size(); i++) {
                    if (Morning_meal_str.equals(list.get(i))) {
                        Morning_meal.setChecked(true);
                    }
                    if (Morning_snak_str.equals(list.get(i))) {
                        Morning_snak.setChecked(true);
                    }

                    if (Morning_Medicine_str.equals(list.get(i))) {
                        Morning_Medicine.setChecked(true);
                    }

                    if (Morning_Walk_str.equals(list.get(i))) {
                        Morning_Walk.setChecked(true);
                    }

                    if (Morning_Yoga_str.equals(list.get(i))) {
                        Morning_Yoga.setChecked(true);
                    }
                    if (Morning_Meditation_str.equals(list.get(i))) {
                        Morning_Meditation.setChecked(true);
                    }
                    if (Morning_Nap_str.equals(list.get(i))) {
                        Morning_Nap.setChecked(true);
                    }

                }

                break;
            case 2:
                for (int i = 0; i < list.size(); i++) {
                    if (Noon_meal_str.equals(list.get(i))) {
                        Noon_meal.setChecked(true);
                    }
                    if (Noon_snak_str.equals(list.get(i))) {
                        Noon_snak.setChecked(true);
                    }

                    if (Noon_Medicine_str.equals(list.get(i))) {
                        Noon_Medicine.setChecked(true);
                    }

                    if (Noon_Walk_str.equals(list.get(i))) {
                        Noon_Walk.setChecked(true);
                    }

                    if (Noon_Yoga_str.equals(list.get(i))) {
                        Noon_Yoga.setChecked(true);
                    }
                    if (Noon_Meditation_str.equals(list.get(i))) {
                        Noon_Meditation.setChecked(true);
                    }
                    if (Noon_Nap_str.equals(list.get(i))) {
                        Noon_Nap.setChecked(true);
                    }

                }

                break;

            case 3:
                for (int i = 0; i < list.size(); i++) {
                    if (Evening_meal_str.equals(list.get(i))) {
                        Evening_meal.setChecked(true);
                    }
                    if (Evening_snak_str.equals(list.get(i))) {
                        Evening_snak.setChecked(true);
                    }

                    if (Evening_Medicine_str.equals(list.get(i))) {
                        Evening_Medicine.setChecked(true);
                    }

                    if (Evening_Walk_str.equals(list.get(i))) {
                        Evening_Walk.setChecked(true);
                    }

                    if (Evening_Yoga_str.equals(list.get(i))) {
                        Evening_Yoga.setChecked(true);
                    }
                    if (Evening_Meditation_str.equals(list.get(i))) {
                        Evening_Meditation.setChecked(true);
                    }
                    if (Evening_Nap_str.equals(list.get(i))) {
                        Evening_Nap.setChecked(true);
                    }

                }

                break;

            case 4:
                for (int i = 0; i < list.size(); i++) {
                    Log.e("Dinner_meal_str", list.get(i));
                    if (Dinner_meal_str.equals(list.get(i))) {
                        Dinner_meal.setChecked(true);
                    }
                    if (Dinner_snak_str.equals(list.get(i))) {
                        Dinner_snak.setChecked(true);
                    }

                    if (Dinner_Medicine_str.equals(list.get(i))) {
                        Dinner_Medicine.setChecked(true);
                    }

                    if (Dinner_Walk_str.equals(list.get(i))) {
                        Dinner_Walk.setChecked(true);
                    }

                    if (Dinner_Yoga_str.equals(list.get(i))) {
                        Dinner_Yoga.setChecked(true);
                    }
                    if (Dinner_Meditation_str.equals(list.get(i))) {
                        Dinner_Meditation.setChecked(true);
                    }
                    if (Dinner_Nap_str.equals(list.get(i))) {
                        Dinner_Nap.setChecked(true);
                    }

                }
                break;

            case 5:


                for (int i = 0; i < list.size(); i++) {
                    if (BedTime_meal_str.equals(list.get(i))) {
                        BedTime_meal.setChecked(true);
                    }
                    if (BedTime_snak_str.equals(list.get(i))) {
                        BedTime_snak.setChecked(true);
                    }

                    if (BedTime_Medicine_str.equals(list.get(i))) {
                        BedTime_Medicine.setChecked(true);
                    }

                    if (BedTime_Walk_str.equals(list.get(i))) {
                        BedTime_Walk.setChecked(true);
                    }

                    if (BedTime_Yoga_str.equals(list.get(i))) {
                        BedTime_Yoga.setChecked(true);
                    }
                    if (BedTime_Meditation_str.equals(list.get(i))) {
                        BedTime_Meditation.setChecked(true);
                    }
                    if (BedTime_Nap_str.equals(list.get(i))) {
                        BedTime_Nap.setChecked(true);
                    }

                }

                break;
        }
    }

    private void SubmitDailyRoutine() {
        final String TAG = "GetDeviceVerification";
        JSONObject mainObject = new JSONObject();

        try {

            mainObject.put("morning", Morning);
            mainObject.put("evening", Evening);
            mainObject.put("noon", Noon);
            mainObject.put("dinner", Dinner);
            mainObject.put("bedtime", BedTime);
            mainObject.put("user_id", Utility.getSharedPreferences(mContext, APIS.user_id));
            mainObject.put("caregiver_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));

            Log.e("SubmitDailyRoutine_data", String.valueOf(mainObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        System.out.println(mainObject.toString());
        showProgressDialog(mContext, getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.DailyRoutine, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "response=" + response.toString());
                        hideProgressDialog();
                        try {
                            String code = response.getString("status_code");
                            Log.e(TAG, "response=" + code);
                            String Message = response.getString("message");

                            if (code.equals("200")) {

                                //  Utility.ShowToast(mContext, Message);
                                ShowAlertResponse(Message);

                            } else if (code.equals("403")) {

                            } else {
                                ShowAlertResponse(Message);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                error.printStackTrace();
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

    }


    private void ShowAlertResponse(String message) {
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

            }
        });

    }


}
