package com.soultabcaregiver.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import static com.soultabcaregiver.Base.BaseActivity.BroadcastStringforAction;

public class InternetBrodcastService extends Service {
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("inside","onCreate");
	}
	

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not Yet Implement");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handler.post(runnable);
		return START_STICKY;
	}
	
	public boolean isOnline (Context context){
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		
		if (activeNetwork!=null && activeNetwork.isConnectedOrConnecting()) {
			return true;
		}else {
			return false;
		}
		
	}
	
	Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
		
			handler.postDelayed(runnable,1*1000 - SystemClock.elapsedRealtime()%1000);
			Intent BroadCastIntent = new Intent();
			BroadCastIntent.setAction(BroadcastStringforAction);
			BroadCastIntent.putExtra("online_status",""+isOnline(InternetBrodcastService.this));
			sendBroadcast(BroadCastIntent);
			
			
		}
	};
}
