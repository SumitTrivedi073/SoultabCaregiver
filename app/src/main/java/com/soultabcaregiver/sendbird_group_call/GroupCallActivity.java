package com.soultabcaregiver.sendbird_group_call;

import android.os.Bundle;

import com.soultabcaregiver.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import static com.soultabcaregiver.sendbird_group_call.GroupCallFragment.EXTRA_CHANNEL_URL;
import static com.soultabcaregiver.sendbird_group_call.GroupCallFragment.EXTRA_ROOM_ID;
import static com.soultabcaregiver.sendbird_group_call.GroupCallFragment.EXTRA_USERS_IDS;

public class GroupCallActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_activity);
		addGroupCallFragment();
	}
	
	private void addGroupCallFragment() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.container,
				GroupCallFragment.newInstance(getIntent().getStringExtra(EXTRA_ROOM_ID),
						getIntent().getStringExtra(EXTRA_CHANNEL_URL),
						getIntent().getStringExtra(EXTRA_USERS_IDS)));
		transaction.commit();
	}
	
}
