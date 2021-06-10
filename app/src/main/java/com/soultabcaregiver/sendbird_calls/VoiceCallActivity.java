package com.soultabcaregiver.sendbird_calls;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.sendbird.calls.AcceptParams;
import com.sendbird.calls.AudioDevice;
import com.sendbird.calls.CallOptions;
import com.sendbird.calls.DialParams;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;
import com.soultabcaregiver.R;
import com.soultabcaregiver.sendbird_calls.utils.TimeUtils;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.soultabcaregiver.utils.Utility.ShowToast;

public class VoiceCallActivity extends CallActivity {
	
	
	private static final String TAG = "VoiceCallActivity";
	
	private Timer mCallDurationTimer;
	
	//+ Views
	private ImageView mImageViewSpeakerphone;
	//- Views
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "[VoiceCallActivity] onDestroy()");
		cancelCallDurationTimer();
	}
	
	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_voice_call;
	}
	
	@Override
	protected void startCall(boolean amICallee) {
		CallOptions callOptions = new CallOptions();
		callOptions.setAudioEnabled(mIsAudioEnabled);
		
		if (amICallee) {
			Log.i(TAG, "[VoiceCallActivity] accept()");
			if (mDirectCall != null) {
				mDirectCall.accept(new AcceptParams().setCallOptions(callOptions));
			}
		} else {
			Log.i(TAG, "[VoiceCallActivity] dial()");
			mDirectCall = SendBirdCall.dial(
					new DialParams(mCalleeIdToDial).setVideoCall(mIsVideoCall).setCallOptions(
							callOptions), (call, e) -> {
						if (e != null) {
							Log.i(TAG, "[VoiceCallActivity] dial() => e: " + e.getMessage());
							if (e.getMessage() != null) {
								ShowToast(mContext, e.getMessage());
							}
							
							finishWithEnding(e.getMessage());
							return;
						}
						
						Log.i(TAG, "[VoiceCallActivity] dial() => OK");
						updateCallService();
					});
			setListener(mDirectCall);
		}
	}
	
	@Override
	protected void setAudioDevice(AudioDevice currentAudioDevice,
	                              Set<AudioDevice> availableAudioDevices) {
	}
	
	@Override
	protected void initViews() {
		super.initViews();
		Log.i(TAG, "[VoiceCallActivity] initViews()");
		
		mImageViewSpeakerphone = findViewById(R.id.image_view_speakerphone);
	}
	
	@Override
	protected void setViews() {
		super.setViews();
		
		mImageViewSpeakerphone.setOnClickListener(view -> {
			if (mDirectCall != null) {
				mImageViewSpeakerphone.setSelected(!mImageViewSpeakerphone.isSelected());
				if (mImageViewSpeakerphone.isSelected()) {
					mDirectCall.selectAudioDevice(AudioDevice.SPEAKERPHONE, e -> {
						if (e != null) {
							mImageViewSpeakerphone.setSelected(false);
						}
					});
				} else {
					mDirectCall.selectAudioDevice(AudioDevice.WIRED_HEADSET, e -> {
						if (e != null) {
							mDirectCall.selectAudioDevice(AudioDevice.EARPIECE, null);
						}
					});
				}
			}
		});
		
	}
	
	@SuppressLint ("SourceLockedOrientationActivity")
	@TargetApi (18)
	@Override
	protected boolean setState(STATE state, DirectCall call) {
		if (!super.setState(state, call)) {
			return false;
		}
		
		switch (mState) {
			case STATE_ACCEPTING:
				cancelCallDurationTimer();
				break;
			
			case STATE_CONNECTED: {
				setInfo(call, "");
				mLinearLayoutInfo.setVisibility(View.VISIBLE);
				setCallDurationTimer(call);
				break;
			}
			
			case STATE_ENDING:
			case STATE_ENDED: {
				cancelCallDurationTimer();
				break;
			}
		}
		return true;
	}
	
	private void cancelCallDurationTimer() {
		if (mCallDurationTimer != null) {
			mCallDurationTimer.cancel();
			mCallDurationTimer = null;
		}
	}
	
	private void setCallDurationTimer(final DirectCall call) {
		if (mCallDurationTimer == null) {
			mCallDurationTimer = new Timer();
			mCallDurationTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(() -> {
						String callDuration = TimeUtils.getTimeString(call.getDuration());
						mTextViewStatus.setText(callDuration);
					});
				}
			}, 0, 1000);
		}
	}
	
	
}
