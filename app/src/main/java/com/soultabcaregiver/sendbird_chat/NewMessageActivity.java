package com.soultabcaregiver.sendbird_chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.main_screen.MainActivity;
import com.soultabcaregiver.companion.CompanionMainActivity;
import com.soultabcaregiver.sendbird_chat.utils.ImageUtils;
import com.soultabcaregiver.utils.Utility;

import androidx.core.content.ContextCompat;

import static com.soultabcaregiver.sendbird_chat.ConversationFragment.EXTRA_GROUP_CHANNEL_URL;

public class NewMessageActivity extends BaseActivity {
	
	public static final String EXTRA_SENDER_NAME = "Sender_name";
	
	public static final String EXTRA_CHANNEL_AVATAR = "Channel_avatar";
	
	public static final String EXTRA_IS_GROUP = "Is_Group";
	
	public static final String EXTRA_CHANNEL_URL = "Channel_URL";
	
	public static final String EXTRA_LAST_MSG = "extra_last_message";
	
	String channelUrl;
	
	private ImageView groupPic;
	
	private TextView messageTextView;

	
	@SuppressLint ("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		wakeScreen();
		
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
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setData(intent);
	}
	
	private void wakeScreen() {
		Window window = getWindow();
		window.addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}
	
	private void setData(Intent intent) {
		String name = intent.getStringExtra(EXTRA_SENDER_NAME);
		String avatar = intent.getStringExtra(EXTRA_CHANNEL_AVATAR);
		channelUrl = intent.getStringExtra(EXTRA_CHANNEL_URL);
		String lastMessage = intent.getStringExtra(EXTRA_LAST_MSG);
		boolean isGroup = intent.getBooleanExtra(EXTRA_IS_GROUP, false);
		
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
		
		Spannable spannedText = new SpannableString(messageText + " " + lastMessage);
		
		spannedText.setSpan(
				new ForegroundColorSpan(ContextCompat.getColor(this, R.color.themecolor)),
				spannedText.length() - lastMessage.length(), spannedText.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannedText.setSpan(new StyleSpan(Typeface.BOLD),
				spannedText.length() - lastMessage.length(), spannedText.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		messageTextView.setText(spannedText);
	}
	
	private void setupUI() {
		messageTextView = findViewById(R.id.messageTextView);
		groupPic = findViewById(R.id.groupAvatar);
		findViewById(R.id.closeBtn).setOnClickListener(v -> {
			finish();
		});
		findViewById(R.id.replyBtn).setOnClickListener(v -> {

			if (Utility.getSharedPreferences(this, APIS.is_companion).equals("0")) {
				Intent intent = new Intent(this, MainActivity.class);
				intent.putExtra(EXTRA_GROUP_CHANNEL_URL, channelUrl);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}else {
				Intent intent = new Intent(this, CompanionMainActivity.class);
				intent.putExtra(EXTRA_GROUP_CHANNEL_URL, channelUrl);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});
	}
	
}
