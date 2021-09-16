package com.soultabcaregiver.companion;

import static com.soultabcaregiver.sendbird_chat.ConversationFragment.EXTRA_GROUP_CHANNEL_URL;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.sendbird.android.SendBird;
import com.sendbird.calls.DirectCallLog;
import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.alert.fragment.AlertFragment;
import com.soultabcaregiver.activity.alert.model.AlertCountModel;
import com.soultabcaregiver.sendbird_calls.utils.BroadcastUtils;
import com.soultabcaregiver.talk.TalkHolderFragment;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CompanionMainActivity extends BaseActivity {

    public static CompanionMainActivity instance;

    private final String TAG = getClass().getSimpleName();

    Context mContext;

    boolean isLocationEnabled;

    BottomNavigationView navigationView;

    BottomNavigationItemView itemView;

    View badge;

    TextView tv_badge;

    AlertFragment alertFragment;

    private BroadcastReceiver receiver;

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion_main);

        mContext = this;
        instance = CompanionMainActivity.this;

        navigationView = findViewById(R.id.companion_bottom_navigation);

        //clear all notification
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        BottomNavigationViewHelper.removeShiftMode(navigationView);

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) navigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(0);
        itemView = (BottomNavigationItemView) v;

        badge = LayoutInflater.from(this).inflate(R.layout.homescreen_count,
                bottomNavigationMenuView, false);
        tv_badge = badge.findViewById(R.id.notification_badge);

        registerReceiver();
        listner();

        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
                    new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            // Get new Instance ID token
                            String FirebaseToken = instanceIdResult.getToken();
                            Log.e("newToken", FirebaseToken);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }

        if (getIntent().hasExtra(EXTRA_GROUP_CHANNEL_URL)) {
            checkForCurrentScreen(getIntent().getStringExtra(EXTRA_GROUP_CHANNEL_URL));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {

                String action = intent.getAction();

                if (action.matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                    LocationManager lm =
                            (LocationManager) getApplicationContext().getSystemService(
                                    Context.LOCATION_SERVICE);

                    try {
                        isLocationEnabled = Utility.isLocationEnabled(getApplicationContext());
                        if (!isLocationEnabled) {
                            Utility.buildAlertMessageNoGps(CompanionMainActivity.this);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        };

        // register events
        getApplicationContext().registerReceiver(receiver,
                new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));


    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(CompanionMainActivity.this).unregisterReceiver(receiver);
        unregisterReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra(EXTRA_GROUP_CHANNEL_URL)) {
            checkForCurrentScreen(intent.getStringExtra(EXTRA_GROUP_CHANNEL_URL));
        } else {
            super.onNewIntent(intent);
        }
    }

    public static CompanionMainActivity getInstance() {
        return instance;
    }

    private void registerReceiver() {
        Log.i(TAG, "[MainActivity] registerReceiver()");

        if (mReceiver != null) {
            return;
        }

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "[MainActivity] onReceive()");

                DirectCallLog callLog = (DirectCallLog) intent.getSerializableExtra(
                        BroadcastUtils.INTENT_EXTRA_CALL_LOG);
                if (callLog != null) {

                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastUtils.INTENT_ACTION_ADD_CALL_LOG);
        registerReceiver(mReceiver, intentFilter);
    }

    private void checkForCurrentScreen(String channelUrl) {
        navigationView.setSelectedItemId(R.id.navigation_talk);
        loadTalkHolderFragment(channelUrl);
    }

    private void listner() {

        navigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentManager fm = getFragmentManager();
                        switch (item.getItemId()) {

                            case R.id.navigation_talk:
                                loadTalkHolderFragment(null);

                                return true;

                        }
                        return true;
                    }

                });

        navigationView.setSelectedItemId(R.id.navigation_talk);

    }

    private void unregisterReceiver() {
        Log.i(TAG, "[MainActivity] unregisterReceiver()");

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private void loadTalkHolderFragment(String channelUrl) {
        Utility.loadFragment(CompanionMainActivity.this, TalkHolderFragment.newInstance(channelUrl), false,
                null);
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
                    BottomNavigationItemView item =
                            (BottomNavigationItemView) menuView.getChildAt(i);
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

    public void Alert_countAPI() {

        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("user_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));

            Log.e(TAG, "AlertCount API========>" + mainObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjReq =
                new JsonObjectRequest(Request.Method.POST, APIS.BASEURL + APIS.AlertCount,
                        mainObject, response -> {
                    Log.e(TAG, "AlertCount response=" + response.toString());
                    hideProgressDialog();

                    AlertCountModel alertCountModel =
                            new Gson().fromJson(response.toString(), AlertCountModel.class);

                    if (String.valueOf(alertCountModel.getStatusCode()).equals("200")) {

                        alertFragment = AlertFragment.instance;
                        if (alertFragment != null) {
                            alertFragment.GetAlertList(mContext);

                        }

                        Utility.setSharedPreference(mContext, APIS.BadgeCount,
                                String.valueOf(alertCountModel.getResponse().getUnreadCount()));
                        updateBadgeCount();


                    } else if (String.valueOf(alertCountModel.getStatusCode()).equals("403")) {
                        logout_app(alertCountModel.getMessage());
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
                        params.put(APIS.HEADERKEY2,
                                Utility.getSharedPreferences(mContext, APIS.EncodeUser_id));

                        return params;
                    }

                };
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(
                new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }

    public void updateBadgeCount() {
        int alertCount = Integer.parseInt(Utility.getSharedPreferences(mContext, APIS.BadgeCount));
        int unreadMessageCount = SendBird.getSubscribedTotalUnreadMessageCount();

        int totalMessageCount = alertCount + unreadMessageCount;
        itemView.removeView(badge);
        if (totalMessageCount > 9) {
            tv_badge.setText("9+");
        } else {
            tv_badge.setText(String.valueOf(totalMessageCount));
        }
        itemView.addView(badge);

    }

    public void updatebadge() {

        alertFragment = AlertFragment.instance;
        if (alertFragment != null) {
            alertFragment.AlertCountUpdate();

        }


    }

}
