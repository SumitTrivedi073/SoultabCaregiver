package com.soultabcaregiver.sendbird_group_call;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sendbird.android.GroupChannel;
import com.sendbird.calls.AudioDevice;
import com.sendbird.calls.EnterParams;
import com.sendbird.calls.Participant;
import com.sendbird.calls.RemoteParticipant;
import com.sendbird.calls.Room;
import com.sendbird.calls.RoomListener;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.SendBirdException;
import com.soultabcaregiver.R;

import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.soultabcaregiver.sendbird_group_call.GroupCallFragment.EXTRA_CHANNEL_URL;
import static com.soultabcaregiver.sendbird_group_call.GroupCallFragment.EXTRA_ROOM_ID;
import static com.soultabcaregiver.sendbird_group_call.GroupCallFragment.EXTRA_USERS_IDS;

public class IncomingGroupCallActivity extends AppCompatActivity {
	
	private static final String TAG = GroupCallViewModel.class.getSimpleName();
	
	public static final String EXTRA_END_CALL = "end_call";
	
	private Room room;
	
	private GroupChannel channel;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		wakeScreen();
		
		setContentView(R.layout.activity_incoming_group_call_screen);
		
		TextView userName = findViewById(R.id.remoteUser);
		
		String roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
		String channelUrl = getIntent().getStringExtra(EXTRA_CHANNEL_URL);
		String userIds = getIntent().getStringExtra(EXTRA_USERS_IDS);
		
		GroupChannel.getChannel(channelUrl, (groupChannel, e) -> {
			channel = groupChannel;
			userName.setText(groupChannel.getName());
		});
		
		this.room = SendBirdCall.getCachedRoomById(roomId);
		
		findViewById(R.id.answerButton).setOnClickListener(v -> {
			EnterParams enterParams = new EnterParams();
			enterParams.setAudioEnabled(true);
			enterParams.setVideoEnabled(true);
			if (room != null) {
				room.enter(enterParams, e1 -> {
					Intent groupCallIntent = new Intent(this, GroupCallActivity.class);
					groupCallIntent.putExtra(EXTRA_ROOM_ID, roomId);
					groupCallIntent.putExtra(EXTRA_CHANNEL_URL, channelUrl);
					startActivity(groupCallIntent);
					finish();
				});
			}
		});
		
		findViewById(R.id.declineButton).setOnClickListener(v -> {
			finish();
		});
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra(EXTRA_END_CALL, false)) {
			finish();
		}
	}
	
	private void wakeScreen() {
		Window window = getWindow();
		window.addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}
	
	class RoomListenerImpl implements RoomListener {
		
		@Override
		public void onAudioDeviceChanged(AudioDevice audioDevice, Set<? extends AudioDevice> set) {
		
		}
		
		@Override
		public void onError(SendBirdException e, Participant participant) {
			Log.e(TAG, e.getMessage());
		}
		
		@Override
		public void onRemoteAudioSettingsChanged(@NonNull RemoteParticipant remoteParticipant) {
		
		}
		
		@Override
		public void onRemoteParticipantEntered(@NonNull RemoteParticipant remoteParticipant) {
		
		}
		
		@Override
		public void onRemoteParticipantExited(@NonNull RemoteParticipant remoteParticipant) {
			if (room.getParticipants().size() == 0) {
				Log.e(TAG, "missed video call");
				finish();
			}
		}
		
		@Override
		public void onRemoteParticipantStreamStarted(@NonNull RemoteParticipant remoteParticipant) {
		
		}
		
		@Override
		public void onRemoteVideoSettingsChanged(@NonNull RemoteParticipant remoteParticipant) {
		
		}
	}
	
}


