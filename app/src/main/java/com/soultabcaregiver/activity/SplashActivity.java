package com.soultabcaregiver.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.MainScreen.MainActivity;
import com.soultabcaregiver.activity.login_module.LoginActivity;
import com.soultabcaregiver.sinch_calling.BaseActivity;
import com.soultabcaregiver.utils.Utility;

public class SplashActivity extends BaseActivity {

    private static final int REQUEST_CODE_PERMISSION = 2;
    Context mContext;
    String User_id;
    private long back_pressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;

        User_id = Utility.getSharedPreferences(mContext, APIS.user_id);

    }


    @Override
    protected void onResume() {
        super.onResume();
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled && !network_enabled) {
            Utility.buildAlertMessageNoGps(mContext);
        } else {

            checkPermissions();
        }
    }

    public void checkPermissions() {
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) ||
                !(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)||
                !(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED)||
                !(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)||
                !(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                            ,Manifest.permission.CAMERA
                            ,Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ,Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSION);


        }else {
            Log.e("Screen_change","True");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    Utility.clearSpecificSharedPreference(mContext, Utility.SinchServiceConnected);

                    if (TextUtils.isEmpty(User_id)) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }


                }

            }, 3000);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int TIME_DELAY = 2000;
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {

            //      Support.ShowToast(this, getResources().getString(R.string.press_Again));
        }
        back_pressed = System.currentTimeMillis();
    }
}