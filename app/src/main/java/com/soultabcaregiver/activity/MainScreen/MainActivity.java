package com.soultabcaregiver.activity.MainScreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.Alert.fragment.AlertFragment;
import com.soultabcaregiver.activity.Alert.model.AlertCountModel;
import com.soultabcaregiver.activity.Calender.fragment.CalenderFragment;
import com.soultabcaregiver.activity.login_module.LoginActivity;
import com.soultabcaregiver.activity.MainScreen.fragment.DashBoardFragment;
import com.soultabcaregiver.activity.daily_routine.fragment.DailyRoutineFragment;
import com.soultabcaregiver.activity.docter.fragment.DoctorFragment;
import com.soultabcaregiver.reminder_ring_class.ReminderCreateClass;
import com.soultabcaregiver.sinch_calling.BaseActivity;
import com.soultabcaregiver.sinch_calling.CallScreenActivity;
import com.soultabcaregiver.sinch_calling.SinchService;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, SinchService.StartFailedListener {

    private static final int REQUEST_CODE_PERMISSION = 2;
    public static MainActivity instance;
    Context mContext;
    FloatingActionButton video_call;
    Location mLastLocation;
    boolean isLocationEnabled;
    double lat, lon;
    LatLng loc;
    BottomNavigationView navigationView;
    BottomNavigationItemView itemView;
    View badge;
    TextView tv_badge;
    private BroadcastReceiver receiver;
    private String CityZipCode;
    private GoogleApiClient googleApiClient;
    private String TAG = getClass().getSimpleName();
    private Timer tmrStartEng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        buildGoogleApiClient();

        instance = MainActivity.this;
        navigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        video_call = findViewById(R.id.video_call);
        BottomNavigationViewHelper.removeShiftMode(navigationView);

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) navigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(3);
        itemView = (BottomNavigationItemView) v;

        badge = LayoutInflater.from(this)
                .inflate(R.layout.homescreen_count, bottomNavigationMenuView, false);
        tv_badge = badge.findViewById(R.id.notification_badge);

        Alert_countAPI();

        listner();
    }

    private void listner() {

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fm = getFragmentManager();
                switch (item.getItemId()) {


                    case R.id.navigation_dashboard:
                        video_call.setVisibility(View.VISIBLE);
                       Utility.loadFragment(MainActivity.this, new DashBoardFragment(), false, null);

                        return true;

                    case R.id.navigation_appointment:

                        video_call.setVisibility(View.GONE);
                        Utility.loadFragment(MainActivity.this, new DoctorFragment(), false, null);
                        return true;

                    case R.id.navigation_dailyroutine:

                        video_call.setVisibility(View.GONE);
                        Utility.loadFragment(MainActivity.this, new DailyRoutineFragment(), false, null);

                        break;
                    case R.id.navigation_alert:
                        video_call.setVisibility(View.GONE);
                        Utility.loadFragment(MainActivity.this, new AlertFragment(), false, null);

                        return true;
                    case R.id.navigation_calender:

                        video_call.setVisibility(View.GONE);
                        Utility.loadFragment(MainActivity.this, new CalenderFragment(), false, null);
                        break;
                }
                return true;
            }

        });

        navigationView.setSelectedItemId(R.id.navigation_dashboard);

        video_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetworkConnected(mContext)) {
                    if (!(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) ||
                            !(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) ||
                            !(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_GRANTED) ||
                            !(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) ||
                            !(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                                    == PackageManager.PERMISSION_GRANTED)) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                        , Manifest.permission.CAMERA
                                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        , Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSION);


                    } else {
                        if (Utility.getSharedPreferences(mContext, Utility.SinchServiceConnected) == null &&
                                TextUtils.isEmpty(Utility.getSharedPreferences(mContext, Utility.SinchServiceConnected))) {

                            if (!getSinchServiceInterface().isStarted()) {
                                getSinchServiceInterface().startClient(Utility.getSharedPreferences(mContext, APIS.Caregiver_email));
                                //   showProgressDialog(getResources().getString(R.string.Loading));

                            } else {
                                openPlaceCallActivity();
                            }

                        } else {
                            openPlaceCallActivity();
                        }
                    }

                } else {
                    Utility.ShowToast(mContext, getResources().getString(R.string.net_connection));
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {

                String action = intent.getAction();

                if (action.matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                    LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

                    try {
                        isLocationEnabled = Utility.isLocationEnabled(getApplicationContext());
                        if (!isLocationEnabled) {
                            Utility.buildAlertMessageNoGps(MainActivity.this);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        };

        // register events
        getApplicationContext().registerReceiver(receiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));


    }

    @Override
    protected void onResume() {
        super.onResume();
        new ReminderCreateClass(MainActivity.this);

        TimerStart();

    }


    private void buildGoogleApiClient() {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        if (mLastLocation != null) {

            if (loc == null) {
                lat = mLastLocation.getLatitude();
                lon = mLastLocation.getLongitude();

                loc = new LatLng(lat, lon);

                Geocoder geocoder = new Geocoder(MainActivity.this);

                try {
                    List<Address> addressList = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                    if (addressList != null && addressList.size() > 0) {

                        Log.e("addressList", String.valueOf(addressList));
                        if (addressList.get(0).getAddressLine(0) != null && addressList.get(0).getCountryName() != null) {
                            String locality = addressList.get(0).getAddressLine(0);
                            String country = addressList.get(0).getCountryName();
                            if (!locality.isEmpty() && !country.isEmpty())
                                CityZipCode = addressList.get(0).getPostalCode();

                            if (!getSinchServiceInterface().isStarted()) {
                                getSinchServiceInterface().startClient(Utility.getSharedPreferences(mContext, APIS.Caregiver_email));

                            }


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        Log.e("error", error.toString());
        hideProgressDialog();
    }

    @Override
    public void onStarted() {
        Log.e("Connected", "ServiceConnected");
        Utility.setSharedPreference(mContext, Utility.SinchServiceConnected, "1");

    }

    private void openPlaceCallActivity() {
        Utility.setSharedPreference(mContext, Utility.SinchServiceConnected, "1");

        Call call = getSinchServiceInterface().callUserVideo(Utility.getSharedPreferences(mContext, APIS.user_email));
        String callId = call.getCallId();

        Intent callScreen = new Intent(this, CallScreenActivity.class);

        if (TextUtils.isEmpty(Utility.getSharedPreferences(mContext, APIS.user_name))) {
            callScreen.putExtra(SinchService.CALL_ID, callId);
            callScreen.putExtra(SinchService.CALLER_NAME, Utility.getSharedPreferences(mContext, APIS.user_email));

            startActivity(callScreen);
        } else {
            callScreen.putExtra(SinchService.CALL_ID, callId);
            callScreen.putExtra(SinchService.CALLER_NAME, Utility.getSharedPreferences(mContext, APIS.user_name));

            startActivity(callScreen);

        }
    }

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
    }


    //to kill the current session of SinchService
    public void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void TimerStart() {

        if (null == tmrStartEng) {
            tmrStartEng = new Timer();
            tmrStartEng.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.e("Timer", "Working fine");

                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Alert_countAPI2();
                            }
                        });
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Alert_countAPI2();

                            }
                        });
                    }
                }

            }, 30000, 30000);
        }
    }

    private void Alert_countAPI2() {

        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("user_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));

            Log.e(TAG, "AlertCount API========>" + mainObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();

        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.AlertCount, mainObject,
                response -> {
                    Log.e(TAG, "AlertCount response=" + response.toString());
                    hideProgressDialog();

                    AlertCountModel alertCountModel = new Gson().fromJson(response.toString(),
                            AlertCountModel.class);

                    if (String.valueOf(alertCountModel.getStatusCode()).equals("200")) {
                        itemView.removeView(badge);
                        if (alertCountModel.getResponse().getUnreadCount() > 9) {
                            tv_badge.setText("9+");
                            itemView.addView(badge);

                        } else {

                            tv_badge.setText(String.valueOf(alertCountModel.getResponse().getUnreadCount()));
                            itemView.addView(badge);
                        }
                    } else {
                        Utility.ShowToast(mContext, alertCountModel.getMessage());
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

    public void Alert_countAPI() {

        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("user_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));

            Log.e(TAG, "AlertCount API========>" + mainObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.AlertCount, mainObject,
                response -> {
                    Log.e(TAG, "AlertCount response=" + response.toString());
                    hideProgressDialog();

                    AlertCountModel alertCountModel = new Gson().fromJson(response.toString(),
                            AlertCountModel.class);

                    if (String.valueOf(alertCountModel.getStatusCode()).equals("200")) {
                        itemView.removeView(badge);
                        if (alertCountModel.getResponse().getUnreadCount() > 9) {
                            tv_badge.setText("9+");
                            itemView.addView(badge);

                        } else {

                            tv_badge.setText(String.valueOf(alertCountModel.getResponse().getUnreadCount()));
                            itemView.addView(badge);
                        }
                    } else {
                        Utility.ShowToast(mContext, alertCountModel.getMessage());
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

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(receiver);
        if (null != tmrStartEng) {
            tmrStartEng.cancel();
            tmrStartEng = null;
            Log.e("Timer", "Stop");

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        if (null != tmrStartEng) {
            tmrStartEng.cancel();
            tmrStartEng = null;

            Log.e("Timer", "Destroy");

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != tmrStartEng) {
            tmrStartEng.cancel();
            tmrStartEng = null;
            Log.e("Timer", "Pause");

        }

    }

    private static class BottomNavigationViewHelper {

        @SuppressLint("RestrictedApi")
        static void removeShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    item.setShifting(false);
                    // set once again checked value, so view will be updated
                    item.setChecked(item.getItemData().isChecked());
                }
            } catch (NoSuchFieldException e) {
                Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
            } catch (IllegalAccessException e) {
                Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
            }
        }
    }


}
