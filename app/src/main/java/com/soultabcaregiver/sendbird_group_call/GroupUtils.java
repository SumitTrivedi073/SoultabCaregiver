package com.soultabcaregiver.sendbird_group_call;

import com.sendbird.calls.AudioDevice;

public class GroupUtils {
	
	public static String audioDeviceToReadableString(AudioDevice audioDevice) {
		switch (audioDevice) {
			case SPEAKERPHONE:
				return "Speaker";
			case EARPIECE:
				return "Phone";
			case BLUETOOTH:
				return "Bluetooth";
			case WIRED_HEADSET:
				return "Headphones";
		}
		return null;
	}
}
