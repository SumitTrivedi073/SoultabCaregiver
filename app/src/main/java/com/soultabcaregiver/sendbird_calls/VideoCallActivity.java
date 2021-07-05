package com.soultabcaregiver.sendbird_calls;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sendbird.calls.AcceptParams;
import com.sendbird.calls.AudioDevice;
import com.sendbird.calls.CallOptions;
import com.sendbird.calls.DialParams;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.DirectCallUserRole;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.SendBirdChatOptions;
import com.sendbird.calls.SendBirdVideoView;
import com.soultabcaregiver.R;

import org.webrtc.RendererCommon;

import java.util.Set;

import static com.soultabcaregiver.utils.Utility.ShowToast;


public class VideoCallActivity extends CallActivity {
	
	private static final String TAG = "VideoCallActivity";
	
	private boolean mIsVideoEnabled;
	
	//+ Views
	private SendBirdVideoView mVideoViewFullScreen;
	
	private View mViewConnectingVideoViewFullScreenFg;
	
	private RelativeLayout mRelativeLayoutVideoViewSmall;
	
	private SendBirdVideoView mVideoViewSmall;
	
	private ImageView mImageViewCameraSwitch;
	
	private ImageView mImageViewVideoOff;
	//- Views
	
	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_video_call;
	}
	
	@Override
	protected void setAudioDevice(AudioDevice currentAudioDevice,
	                              Set<AudioDevice> availableAudioDevices) {
		if (currentAudioDevice == AudioDevice.SPEAKERPHONE) {
			mImageViewBluetooth.setSelected(false);
		} else if (currentAudioDevice == AudioDevice.BLUETOOTH) {
			mImageViewBluetooth.setSelected(true);
		}
		
		if (availableAudioDevices.contains(AudioDevice.BLUETOOTH)) {
			mImageViewBluetooth.setEnabled(true);
		} else if (!mImageViewBluetooth.isSelected()) {
			mImageViewBluetooth.setEnabled(false);
		}
	}
	
	@Override
	protected void startCall(boolean amICallee) {
		CallOptions callOptions = new CallOptions();
		callOptions.setVideoEnabled(mIsVideoEnabled).setAudioEnabled(mIsAudioEnabled);
		
		if (amICallee) {
			callOptions.setLocalVideoView(mVideoViewSmall).setRemoteVideoView(mVideoViewFullScreen);
		} else {
			callOptions.setLocalVideoView(mVideoViewFullScreen).setRemoteVideoView(mVideoViewSmall);
		}
		
		if (amICallee) {
			Log.i(TAG, "[VideoCallActivity] accept()");
			if (mDirectCall != null) {
				mDirectCall.accept(new AcceptParams().setCallOptions(callOptions));
			}
		} else {
			Log.i(TAG, "[VideoCallActivity] dial()");
			
			DialParams dialParams;
			
			if (isCallingFromChat) {
				dialParams = new DialParams(mCalleeIdToDial).setVideoCall(
						mIsVideoCall).setSendBirdChatOptions(
						new SendBirdChatOptions().setChannelUrl(mChannelUrl)).setCallOptions(
						callOptions);
			} else {
				dialParams =
						new DialParams(mCalleeIdToDial).setVideoCall(mIsVideoCall).setCallOptions(
								callOptions);
			}
			
			
			mDirectCall = SendBirdCall.dial(dialParams, (call, e) -> {
				if (e != null) {
					Log.i(TAG, "[VideoCallActivity] dial() => e: " + e.getMessage());
					if (e.getMessage() != null) {
						ShowToast(mContext, e.getMessage());
					}
					
					finishWithEnding(e.getMessage());
					return;
				}
				
				Log.i(TAG, "[VideoCallActivity] dial() => OK");
				updateCallService();
			});
			setListener(mDirectCall);
		}
	}
	
	@Override
	protected void initViews() {
		super.initViews();
		Log.i(TAG, "[VideoCallActivity] initViews()");
		
		mVideoViewFullScreen = findViewById(R.id.video_view_fullscreen);
		mViewConnectingVideoViewFullScreenFg =
				findViewById(R.id.view_connecting_video_view_fullscreen_fg);
		mRelativeLayoutVideoViewSmall = findViewById(R.id.relative_layout_video_view_small);
		mVideoViewSmall = findViewById(R.id.video_view_small);
		mImageViewCameraSwitch = findViewById(R.id.image_view_camera_switch);
		mImageViewVideoOff = findViewById(R.id.image_view_video_off);
	}
	
	@Override
	protected void setViews() {
		super.setViews();
		
		mVideoViewFullScreen.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
		mVideoViewFullScreen.setZOrderMediaOverlay(false);
		mVideoViewFullScreen.setEnableHardwareScaler(true);
		
		mVideoViewSmall.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
		mVideoViewSmall.setZOrderMediaOverlay(true);
		mVideoViewSmall.setEnableHardwareScaler(true);
		
		if (mDirectCall != null) {
			if (mDirectCall.getMyRole() == DirectCallUserRole.CALLER && mState == STATE.STATE_OUTGOING) {
				mDirectCall.setLocalVideoView(mVideoViewFullScreen);
				mDirectCall.setRemoteVideoView(mVideoViewSmall);
			} else {
				mDirectCall.setLocalVideoView(mVideoViewSmall);
				mDirectCall.setRemoteVideoView(mVideoViewFullScreen);
			}
		}
		
		mImageViewCameraSwitch.setOnClickListener(view -> {
			if (mDirectCall != null) {
				mDirectCall.switchCamera(e -> {
					if (e != null) {
						Log.i(TAG, "[VideoCallActivity] switchCamera(e: " + e.getMessage() + ")");
					}
				});
			}
		});
		
		if (mDirectCall != null && !mDoLocalVideoStart) {
			mIsVideoEnabled = mDirectCall.isLocalVideoEnabled();
		} else {
			mIsVideoEnabled = true;
		}
		mImageViewVideoOff.setSelected(!mIsVideoEnabled);
		mImageViewVideoOff.setOnClickListener(view -> {
			if (mDirectCall != null) {
				if (mIsVideoEnabled) {
					Log.i(TAG, "[VideoCallActivity] stopVideo()");
					mDirectCall.stopVideo();
					mIsVideoEnabled = false;
					mImageViewVideoOff.setSelected(true);
					mImageViewVideoOff.setImageResource(R.drawable.icon_video_turn_off);
				} else {
					Log.i(TAG, "[VideoCallActivity] startVideo()");
					mDirectCall.startVideo();
					mIsVideoEnabled = true;
					mImageViewVideoOff.setSelected(false);
					mImageViewVideoOff.setImageResource(R.drawable.icon_video_call_turn_on);
				}
			}
		});
		mImageViewBluetooth.setEnabled(false);
		mImageViewBluetooth.setOnClickListener(view -> {
			mImageViewBluetooth.setSelected(!mImageViewBluetooth.isSelected());
			if (mDirectCall != null) {
				if (mImageViewBluetooth.isSelected()) {
					mDirectCall.selectAudioDevice(AudioDevice.BLUETOOTH, e -> {
						if (e != null) {
							mImageViewBluetooth.setSelected(false);
						}
					});
				} else {
					mDirectCall.selectAudioDevice(AudioDevice.WIRED_HEADSET, e -> {
						if (e != null) {
							mDirectCall.selectAudioDevice(AudioDevice.SPEAKERPHONE, null);
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
		
		switch (state) {
			case STATE_ACCEPTING: {
				mVideoViewFullScreen.setVisibility(View.GONE);
				mViewConnectingVideoViewFullScreenFg.setVisibility(View.GONE);
				mRelativeLayoutVideoViewSmall.setVisibility(View.GONE);
				mImageViewCameraSwitch.setVisibility(View.GONE);
				mImageViewCameraSwitch.setVisibility(View.GONE);
				mImageViewVideoOff.setVisibility(View.GONE);
				mImageViewAudioOff.setVisibility(View.GONE);
				mImageViewBluetooth.setVisibility(View.GONE);
				break;
			}
			
			case STATE_OUTGOING: {
				mVideoViewFullScreen.setVisibility(View.VISIBLE);
				mViewConnectingVideoViewFullScreenFg.setVisibility(View.VISIBLE);
				mRelativeLayoutVideoViewSmall.setVisibility(View.GONE);
				mImageViewCameraSwitch.setVisibility(View.GONE);
				mImageViewVideoOff.setVisibility(View.GONE);
				mImageViewAudioOff.setVisibility(View.GONE);
				mImageViewBluetooth.setVisibility(View.GONE);
				break;
			}
			
			case STATE_CONNECTED: {
				mVideoViewFullScreen.setVisibility(View.VISIBLE);
				mViewConnectingVideoViewFullScreenFg.setVisibility(View.GONE);
				mRelativeLayoutVideoViewSmall.setVisibility(View.VISIBLE);
				mImageViewCameraSwitch.setVisibility(View.VISIBLE);
				mImageViewVideoOff.setVisibility(View.VISIBLE);
				mImageViewAudioOff.setVisibility(View.VISIBLE);
				mImageViewBluetooth.setVisibility(View.VISIBLE);
				mLinearLayoutInfo.setVisibility(View.GONE);
				
				if (call != null && call.getMyRole() == DirectCallUserRole.CALLER) {
					call.setLocalVideoView(mVideoViewSmall);
					call.setRemoteVideoView(mVideoViewFullScreen);
				}
				break;
			}
			
			case STATE_ENDING:
			case STATE_ENDED: {
				mLinearLayoutInfo.setVisibility(View.VISIBLE);
				
				mVideoViewFullScreen.setVisibility(View.GONE);
				mViewConnectingVideoViewFullScreenFg.setVisibility(View.GONE);
				mRelativeLayoutVideoViewSmall.setVisibility(View.GONE);
				mImageViewCameraSwitch.setVisibility(View.GONE);
				mImageViewVideoOff.setVisibility(View.GONE);
				mImageViewAudioOff.setVisibility(View.GONE);
				mImageViewBluetooth.setVisibility(View.GONE);
				
			}
			break;
		}
		return true;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "[VideoCallActivity] onStart()");
		
		if (mDirectCall != null && mDoLocalVideoStart) {
			mDoLocalVideoStart = false;
			updateCallService();
			mDirectCall.startVideo();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "[VideoCallActivity] onStop()");
		
		if (mDirectCall != null && mDirectCall.isLocalVideoEnabled()) {
			mDirectCall.stopVideo();
			mDoLocalVideoStart = true;
			updateCallService();
		}
	}
	
	protected void setLocalVideoSettings(DirectCall call) {
		mIsVideoEnabled = call.isLocalVideoEnabled();
		Log.i(TAG,
				"[VideoCallActivity] setLocalVideoSettings() => isLocalVideoEnabled(): " + mIsVideoEnabled);
		mImageViewVideoOff.setSelected(!mIsVideoEnabled);
	}
	
}
