package com.soultabcaregiver.sendbird_calls;


import com.sendbird.calls.AudioDevice;

import java.util.Set;

public class VoiceCallActivity extends CallActivity {
	
	
	@Override
	protected int getLayoutResourceId() {
		return 0;
	}
	
	@Override
	protected void setAudioDevice(AudioDevice currentAudioDevice,
	                              Set<AudioDevice> availableAudioDevices) {
		
	}
	
	@Override
	protected void startCall(boolean amICallee) {
	
	}
}
