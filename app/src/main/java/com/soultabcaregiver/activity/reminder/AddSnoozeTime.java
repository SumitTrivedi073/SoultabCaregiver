package com.soultabcaregiver.activity.reminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.soultabcaregiver.R;

public class AddSnoozeTime extends AppCompatActivity {

    RadioButton five_minute_rb, ten_minute_rb, fifteen_minute_rb, therty_minute_rb, three_times_rb, five_times_rb, Continuese_rb;
    RelativeLayout five_minute, ten_minute, fifteen_minute, therty_minute, three_times, five_times, Continues,
            btn_submit_snooze;
    SwitchCompat Snooze_on;
    TextView Snooze_on_txt;
    FloatingActionButton ly_back;

    SharedPreferences shp;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_snooze_time);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, getLocalClassName().trim());
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, getLocalClassName().trim());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        shp = getSharedPreferences("TEXT", 0);
        five_minute_rb = findViewById(R.id.five_minute_rb);
        ten_minute_rb = findViewById(R.id.ten_minute_rb);
        fifteen_minute_rb = findViewById(R.id.fifteen_minute_rb);
        therty_minute_rb = findViewById(R.id.therty_minute_rb);
        three_times_rb = findViewById(R.id.three_times_rb);
        five_times_rb = findViewById(R.id.five_times_rb);
        Continuese_rb = findViewById(R.id.Continuese_rb);

        five_minute = findViewById(R.id.five_minute);
        ten_minute = findViewById(R.id.ten_minute);
        fifteen_minute = findViewById(R.id.fifteen_minute);
        therty_minute = findViewById(R.id.therty_minute);
        three_times = findViewById(R.id.three_times);
        five_times = findViewById(R.id.five_times);
        Continues = findViewById(R.id.Continues_relative);

        Snooze_on = findViewById(R.id.Snooze_on);
        Snooze_on_txt = findViewById(R.id.Snooze_on_txt);
        ly_back = findViewById(R.id.lyBack_card);
        btn_submit_snooze = findViewById(R.id.btn_submit_snooze);



        if (getIntent().getStringExtra("Snooze_minute")!=null){
            String test = getIntent().getStringExtra("Snooze_minute");
            Log.e("test",test);
            switch (test) {
                case "5":
                    five_minute_rb.setChecked(true);
                    break;
                case "10":
                    ten_minute_rb.setChecked(true);
                    break;
                case "15":
                    fifteen_minute_rb.setChecked(true);
                    break;
                case "30":
                    therty_minute_rb.setChecked(true);
                    break;
            }

        }

        if (getIntent().getStringExtra("Snooze_times")!=null){
            String test = getIntent().getStringExtra("Snooze_times");
            Log.e("test2",test);
            switch (test) {
                case "3":
                    three_times_rb.setChecked(true);
                    break;
                case "5":
                    five_times_rb.setChecked(true);
                    break;
                case "10":
                    Continuese_rb.setChecked(true);
                    break;
            }

        }
        if (getIntent().getStringExtra("Snooze_on")!=null){
            if (shp.getString("Snooze_on","").equals("true")){
                Snooze_on.setChecked(true);
                Snooze_on_txt.setText(getResources().getString(R.string.On));

            }else {
                Snooze_on.setChecked(false);
                Snooze_on_txt.setText(getResources().getString(R.string.Off));

            }

        }

        Snooze_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Snooze_on_txt.setText(getResources().getString(R.string.On));
                } else {
                    Snooze_on_txt.setText(getResources().getString(R.string.Off));
                }
            }
        });



        five_minute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (five_minute_rb.isChecked()){
                    five_minute_rb.setChecked(false);
                    ten_minute_rb.setChecked(false);
                    fifteen_minute_rb.setChecked(false);
                    therty_minute_rb.setChecked(false);
                }else {
                    five_minute_rb.setChecked(true);
                    ten_minute_rb.setChecked(false);
                    fifteen_minute_rb.setChecked(false);
                    therty_minute_rb.setChecked(false);
                }
            }
        });



        ten_minute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ten_minute_rb.isChecked()){
                    five_minute_rb.setChecked(false);
                    ten_minute_rb.setChecked(false);
                    fifteen_minute_rb.setChecked(false);
                    therty_minute_rb.setChecked(false);
                }else {
                    five_minute_rb.setChecked(false);
                    ten_minute_rb.setChecked(true);
                    fifteen_minute_rb.setChecked(false);
                    therty_minute_rb.setChecked(false);
                }
            }
        });





        fifteen_minute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (five_minute_rb.isChecked()){
                    five_minute_rb.setChecked(false);
                    ten_minute_rb.setChecked(false);
                    fifteen_minute_rb.setChecked(false);
                    therty_minute_rb.setChecked(false);
                }else {
                    five_minute_rb.setChecked(false);
                    ten_minute_rb.setChecked(false);
                    fifteen_minute_rb.setChecked(true);
                    therty_minute_rb.setChecked(false);
                }
            }
        });





        therty_minute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (five_minute_rb.isChecked()){
                    five_minute_rb.setChecked(false);
                    ten_minute_rb.setChecked(false);
                    fifteen_minute_rb.setChecked(false);
                    therty_minute_rb.setChecked(false);
                }else {
                    five_minute_rb.setChecked(false);
                    ten_minute_rb.setChecked(false);
                    fifteen_minute_rb.setChecked(false);
                    therty_minute_rb.setChecked(true);
                }
            }
        });



        three_times.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (three_times_rb.isChecked()){

                    three_times_rb.setChecked(false);
                    five_times_rb.setChecked(false);
                    Continuese_rb.setChecked(false);
                }else {
                    three_times_rb.setChecked(true);
                    five_times_rb.setChecked(false);
                    Continuese_rb.setChecked(false);
                }
            }
        });


        five_times.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (five_times_rb.isChecked()){

                    three_times_rb.setChecked(false);
                    five_times_rb.setChecked(false);
                    Continuese_rb.setChecked(false);
                }else {
                    three_times_rb.setChecked(false);
                    five_times_rb.setChecked(true);
                    Continuese_rb.setChecked(false);
                }
            }
        });



        Continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Continuese_rb.isChecked()){

                    three_times_rb.setChecked(false);
                    five_times_rb.setChecked(false);
                    Continuese_rb.setChecked(false);
                }else {
                    three_times_rb.setChecked(false);
                    five_times_rb.setChecked(false);
                    Continuese_rb.setChecked(true);
                }
            }
        });


        btn_submit_snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor= shp.edit();
                if (Snooze_on.isChecked()) {

                    editor.putString("Snooze_on","true");

                    if (five_minute_rb.isChecked()) {
                        editor.putString("Snooze_minute","5");
                    } else if (ten_minute_rb.isChecked()) {
                        editor.putString("Snooze_minute","10");
                    } else if (fifteen_minute_rb.isChecked()) {
                        editor.putString("Snooze_minute","15");
                    } else if (therty_minute_rb.isChecked()) {
                        editor.putString("Snooze_minute","30");
                    }


                    if (three_times_rb.isChecked()) {
                        editor.putString("Snooze_times","3");
                    } else if (five_times_rb.isChecked()) {
                        editor.putString("Snooze_times","5");
                    } else if (Continuese_rb.isChecked()) {
                        editor.putString("Snooze_times","10");
                    }

                    editor.commit();

                }else {
                    editor.putString("Snooze_on","false");
                    editor.commit();

                }

                onBackPressed();
            }
        });
        ly_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
}
