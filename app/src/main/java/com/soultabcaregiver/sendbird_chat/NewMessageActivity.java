package com.soultabcaregiver.sendbird_chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;
import com.soultabcaregiver.sendbird_chat.utils.ImageUtils;

import androidx.core.content.ContextCompat;

public class NewMessageActivity extends BaseActivity {
	
	public static final String SENDER_NAME = "Sender_name";
	
	public static final String CHANNEL_AVATAR = "Channel_avatar";
	
	public static final String IS_GROUP = "Is_Group";
	
	public static final String CHANNEL_URL = "Channel_URL";
	
	private ImageView groupPic;
	
	private TextView messageTextView;
	
	@SuppressLint ("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_new_message);
		
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		
		if (getSupportActionBar() != null) {
			getSupportActionBar().hide();
		}
		
		setupUI();
		setData(getIntent());
		
		
	}
	
	private void setupUI() {
		messageTextView = findViewById(R.id.messageTextView);
		groupPic = findViewById(R.id.groupAvatar);
		findViewById(R.id.closeBtn).setOnClickListener(v -> {
			finish();
		});
	}
	
	private void setData(Intent intent) {
		String name = intent.getStringExtra(SENDER_NAME);
		String avatar = intent.getStringExtra(CHANNEL_AVATAR);
		String channelUrl = intent.getStringExtra(CHANNEL_URL);
		boolean isGroup = intent.getBooleanExtra(IS_GROUP, false);
		
		if (avatar.isEmpty()) {
			if (isGroup) {
				groupPic.setImageDrawable(
						ContextCompat.getDrawable(this, R.drawable.icon_avatar_group));
			} else {
				groupPic.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_avatar));
			}
		} else {
			ImageUtils.displayRoundImageFromUrl(this, avatar, groupPic);
		}
		
		StringBuilder messageText = new StringBuilder();
		if (isGroup) {
			messageText.append(getString(R.string.new_message_group_text)).append(" ").append(name);
		} else {
			messageText.append(getString(R.string.new_message_user_text)).append(" ").append(name);
		}
		
		messageTextView.setText(messageText);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setData(intent);
	}
}
