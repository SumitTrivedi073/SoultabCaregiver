package com.soultabcaregiver.sendbird_chat;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;

import static com.soultabcaregiver.sendbird_calls.utils.BroadcastUtils.INTENT_EXTRA_CHAT_MESSAGE_BODY;

public class NewMessageActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_new_message);
		if (getSupportActionBar() != null) {
			getSupportActionBar().hide();
		}
		String messageBody = getIntent().getStringExtra(INTENT_EXTRA_CHAT_MESSAGE_BODY);
		
		TextView titleTextView = findViewById(R.id.titleTextView);
		titleTextView.setText(messageBody);
		
		findViewById(R.id.dismissBtn).setOnClickListener(v -> {
			finish();
		});
		
		
	}
}
