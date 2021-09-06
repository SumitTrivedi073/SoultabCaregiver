package com.soultabcaregiver.sendbird_group_call;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sendbird.android.GroupChannel;
import com.sendbird.calls.EnterParams;
import com.sendbird.calls.Room;
import com.sendbird.calls.SendBirdCall;
import com.soultabcaregiver.R;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static com.soultabcaregiver.sendbird_group_call.SendBirdGroupCallService.EXTRA_CHANNEL_URL;
import static com.soultabcaregiver.sendbird_group_call.SendBirdGroupCallService.EXTRA_GROUPS_USERS_IDS;
import static com.soultabcaregiver.sendbird_group_call.SendBirdGroupCallService.EXTRA_GROUP_NAME;
import static com.soultabcaregiver.sendbird_group_call.SendBirdGroupCallService.EXTRA_ROOM_ID;

public class IncomingGroupCallActivity extends AppCompatActivity {
	
	private static final String TAG = IncomingGroupCallActivity.class.getSimpleName();
	
	public static final String EXTRA_END_CALL = "end_call";
	
	private final long[] mVibratePattern = {0, 500, 500};
	
	AudioManager am;
	
	MediaPlayer mediaPlayer;
	
	private Room room;
	
	private Vibrator mVibrator;
	
	private boolean mVibrate = true;
	
	@RequiresApi (api = Build.VERSION_CODES.M)
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getIntent().getBooleanExtra(EXTRA_END_CALL, false)) {
			finish();
		}
		
		wakeScreen();
		
		setContentView(R.layout.activity_incoming_group_call_screen);
		
		TextView userName = findViewById(R.id.remoteUser);
		
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		if (mVibrate) {
			mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		}
		
		mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ringing);
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
		
		if (mVibrate) {
			mVibrator.vibrate(mVibratePattern, 0);
		}
		
		String roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
		String channelUrl = getIntent().getStringExtra(EXTRA_CHANNEL_URL);
		String groupName = getIntent().getStringExtra(EXTRA_GROUP_NAME);
		String userIds = getIntent().getStringExtra(EXTRA_GROUPS_USERS_IDS);
		
		GroupChannel.getChannel(channelUrl, (groupChannel, e) -> {
			userName.setText(groupChannel.getName());
		});
		
		SendBirdCall.fetchRoomById(roomId, (newRoom, e) -> {
			room = newRoom;
		});
		
		findViewById(R.id.answerButton).setOnClickListener(v -> {
			EnterParams enterParams = new EnterParams();
			enterParams.setAudioEnabled(true);
			enterParams.setVideoEnabled(true);
			if (room != null) {
				
				SendBirdGroupCallService.startService(IncomingGroupCallActivity.this, groupName,
						room.getRoomId(), channelUrl, userIds, false);
				
				room.enter(enterParams, e1 -> {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.stop();
						if (mVibrate)
							mVibrator.cancel();
					}
					Intent groupCallIntent = new Intent(this, GroupCallActivity.class);
					groupCallIntent.putExtra(EXTRA_ROOM_ID, roomId);
					groupCallIntent.putExtra(EXTRA_CHANNEL_URL, channelUrl);
					groupCallIntent.putExtra(EXTRA_GROUPS_USERS_IDS, userIds);
					startActivity(groupCallIntent);
					finish();
				});
			}
		});
		
		findViewById(R.id.declineButton).setOnClickListener(v -> {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				if (mVibrate)
					mVibrator.cancel();
			}
			finish();
		});
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra(EXTRA_END_CALL, false)) {
			if (room != null) {
				if (room.getParticipants().size() <= 1) {
					finish();
				}
			} else {
				finish();
			}
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			if (mVibrate)
				mVibrator.cancel();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			if (mVibrate)
				mVibrator.cancel();
		}
	}
	
	private void wakeScreen() {
		Window window = getWindow();
		window.addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}
	
}


