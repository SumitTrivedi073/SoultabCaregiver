package com.soultabcaregiver.sendbird_chat;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.main_screen.MainActivity;
import com.soultabcaregiver.sendbird_chat.utils.ImageUtils;

import java.util.List;

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
	
	MainActivity mainActivity;
	
	boolean AppInBackground;
	
	@SuppressLint ("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_new_message);
		
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		
		mainActivity = MainActivity.instance;
		AppInBackground = isAppIsInBackground(this);
		if (getSupportActionBar() != null) {
			getSupportActionBar().hide();
		}
		
		setupUI();
		setData(getIntent());
		
		
	}
	
	private boolean isAppIsInBackground(Context context) {
		boolean isInBackground = true;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
			List<ActivityManager.RunningAppProcessInfo> runningProcesses =
					am.getRunningAppProcesses();
			for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
				if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					for (String activeProcess : processInfo.pkgList) {
						if (activeProcess.equals(context.getPackageName())) {
							isInBackground = false;
						}
					}
				}
			}
		} else {
			List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
			ComponentName componentInfo = taskInfo.get(0).topActivity;
			if (componentInfo.getPackageName().equals(context.getPackageName())) {
				isInBackground = false;
			}
		}
		return isInBackground;
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
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setData(intent);
	}
	
	private void setupUI() {
		messageTextView = findViewById(R.id.messageTextView);
		groupPic = findViewById(R.id.groupAvatar);
		findViewById(R.id.closeBtn).setOnClickListener(v -> {
			finish();
		});
		findViewById(R.id.replyBtn).setOnClickListener(v -> {
			/*if (AppInBackground){
				if (mainActivity != null) {
					
					mainActivity.updatenavigation();
				}
			}*/
			
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra(EXTRA_GROUP_CHANNEL_URL, channelUrl);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		});
	}
	
}
