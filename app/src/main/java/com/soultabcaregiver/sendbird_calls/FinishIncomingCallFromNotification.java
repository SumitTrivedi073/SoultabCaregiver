package com.soultabcaregiver.sendbird_calls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class FinishIncomingCallFromNotification extends BroadcastReceiver {

    IncomingCallActivity incomingCallActivity;
    @Override
    public void onReceive(Context context, Intent intent) {
        incomingCallActivity = IncomingCallActivity.instance;


        if (incomingCallActivity!=null) {
            IncomingCallActivity.getInstance().finish();
        }

    }
}
