package com.soultabcaregiver.sendbird_calls;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.handler.DirectCallListener;
import com.soultabcaregiver.R;
import com.soultabcaregiver.sinch_calling.BaseActivity;

import org.jetbrains.annotations.NotNull;

import static com.soultabcaregiver.sendbird_calls.SendbirdCallService.EXTRA_CALLEE_ID_TO_DIAL;
import static com.soultabcaregiver.sendbird_calls.SendbirdCallService.EXTRA_CALL_ID;
import static com.soultabcaregiver.sendbird_calls.SendbirdCallService.EXTRA_CALL_STATE;
import static com.soultabcaregiver.sendbird_calls.SendbirdCallService.EXTRA_DO_ACCEPT;
import static com.soultabcaregiver.sendbird_calls.SendbirdCallService.EXTRA_DO_DIAL;
import static com.soultabcaregiver.sendbird_calls.SendbirdCallService.EXTRA_DO_END;
import static com.soultabcaregiver.sendbird_calls.SendbirdCallService.EXTRA_DO_LOCAL_VIDEO_START;
import static com.soultabcaregiver.sendbird_calls.SendbirdCallService.EXTRA_IS_VIDEO_CALL;


public class IncomingCallActivity extends BaseActivity {
	
	private SendbirdCallService.ServiceData mServiceData;
	
	private DirectCall mDirectCall;
	public static IncomingCallActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incoming_call_screen);
		instance = IncomingCallActivity.this;

		setServiceData();
		
		TextView userName = findViewById(R.id.remoteUser);
		userName.setText(mServiceData.remoteNicknameOrUserId);
		
		try {
			mDirectCall = SendBirdCall.getCall(mServiceData.callId);
			mDirectCall.setListener(new DirectCallListener() {
				@Override
				public void onConnected(@NotNull DirectCall directCall) {
					finish();
				}
				
				@Override
				public void onEnded(@NotNull DirectCall directCall) {
					finish();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		findViewById(R.id.answerButton).setOnClickListener(v -> {
			finish();
			startActivity(getCallActivityIntent());
		});
		
		findViewById(R.id.declineButton).setOnClickListener(v -> {
			SendBirdCall.getCall(mServiceData.callId).end();
			finish();
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getIntent().getBooleanExtra("callEnded", false)) {
			finish();
		}
	}

	private void setServiceData() {
		mServiceData = new SendbirdCallService.ServiceData();
		mServiceData.isHeadsUpNotification = true;
		mServiceData.remoteNicknameOrUserId = getIntent().getExtras().getString("userName");
		mServiceData.callState = CallActivity.STATE.STATE_ACCEPTING;
		mServiceData.callId = getIntent().getExtras().getString("callId");
		mServiceData.isVideoCall = getIntent().getExtras().getBoolean("isVideoCall");
		mServiceData.calleeIdToDial = getIntent().getExtras().getString("userId");
		mServiceData.doDial = false;
		mServiceData.doAccept = false;
		mServiceData.doLocalVideoStart = false;
		
		
	}
	
	private Intent getCallActivityIntent() {
		final Intent intent;
		
		if (mServiceData.isVideoCall) {
			intent = new Intent(this, VideoCallActivity.class);
		} else {
			intent = new Intent(this, VoiceCallActivity.class);
		}
		
		intent.putExtra(EXTRA_CALL_STATE, mServiceData.callState);
		intent.putExtra(EXTRA_CALL_ID, mServiceData.callId);
		intent.putExtra(EXTRA_IS_VIDEO_CALL, mServiceData.isVideoCall);
		intent.putExtra(EXTRA_CALLEE_ID_TO_DIAL, mServiceData.calleeIdToDial);
		intent.putExtra(EXTRA_DO_DIAL, mServiceData.doDial);
		intent.putExtra(EXTRA_DO_ACCEPT, true);
		intent.putExtra(EXTRA_DO_LOCAL_VIDEO_START, mServiceData.doLocalVideoStart);
		intent.putExtra(EXTRA_DO_END, false);
		intent.addFlags(
				Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		return intent;
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra("callEnded", false)) {
			//TODO Generate the missed-call notification
			finish();
		}
	}
	public static IncomingCallActivity getInstance() {
		return instance;
	}
	public void finishCall(){
		Log.e("Finish_Screen","True");
		finish();
		if (mDirectCall != null) {
			mDirectCall.end();
		}
	}
}
