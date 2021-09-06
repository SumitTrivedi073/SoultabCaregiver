package com.soultabcaregiver.sendbird_group_call;

import android.os.Bundle;

import com.soultabcaregiver.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import static com.soultabcaregiver.sendbird_group_call.SendBirdGroupCallService.EXTRA_CHANNEL_URL;
import static com.soultabcaregiver.sendbird_group_call.SendBirdGroupCallService.EXTRA_GROUPS_USERS_IDS;
import static com.soultabcaregiver.sendbird_group_call.SendBirdGroupCallService.EXTRA_ROOM_ID;

public class GroupCallActivity extends AppCompatActivity {
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_activity);

		getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		addGroupCallFragment();
	}
	
	@Override
	public void onBackPressed() {
	
	}
	
	private void addGroupCallFragment() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.container,
				GroupCallFragment.newInstance(getIntent().getStringExtra(EXTRA_ROOM_ID),
						getIntent().getStringExtra(EXTRA_CHANNEL_URL),
						getIntent().getStringExtra(EXTRA_GROUPS_USERS_IDS)));
		transaction.commit();
	}
}
