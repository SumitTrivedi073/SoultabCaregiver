package com.soultabcaregiver.Base;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.soultabcaregiver.Model.DiloagBoxCommon;
import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.login_module.LoginActivity;
import com.soultabcaregiver.activity.main_screen.MainActivity;
import com.soultabcaregiver.companion.CompanionMainActivity;
import com.soultabcaregiver.sendbird_calls.utils.BroadcastUtils;
import com.soultabcaregiver.sendbird_chat.NewMessageActivity;
import com.soultabcaregiver.talk.TalkFragment;
import com.soultabcaregiver.talk.TalkHolderFragment;
import com.soultabcaregiver.utils.CustomProgressDialog;
import com.soultabcaregiver.utils.InternetBrodcastService;
import com.soultabcaregiver.utils.Utility;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

@SuppressWarnings ("ALL")
public abstract class BaseActivity extends AppCompatActivity {
	
	
	public static final String BroadcastStringforAction = "ChectInternet";
	IntentFilter mIntentFilter;
	public static BaseActivity instance;
	
	AlertDialog alertDialog, alertDialog1;
	
	MainActivity mainActivity;
	

	private CustomProgressDialog progressDialog;
	
	private BroadcastReceiver mReceiver, receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainActivity = MainActivity.instance;
		instance = BaseActivity.this;
		
		
		/*Code for quick response of isOnline Start*/
		mIntentFilter =  new IntentFilter();
		mIntentFilter.addAction(BroadcastStringforAction);
		Intent ServiceIntent = new Intent(this,InternetBrodcastService.class);
		startService(ServiceIntent);
		
		if (isOnline(getApplicationContext())){
			ifInternetConnected();
		}else {
			ifInternetNotConnected();
		}
		
	}
	
	
	

	
	public void onRequestPermissionsResult(int requestCode, String[] permissions,
	                                       int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		boolean granted = grantResults.length > 0;
		for (int grantResult : grantResults) {
			granted &= grantResult == PackageManager.PERMISSION_GRANTED;
		}
		if (granted) {
			//Toast.makeText(this, "You may now place a call", Toast.LENGTH_LONG).show();
		} else {
			
			Utility.ShowToast(this,
					"This application needs permission to use your microphone and camera to " +
							"function properly.");
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		initBroadCastReceiver();
		registerReceiver();
		
	}
	
	
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver();
	}
	
	private void unregisterReceiver() {
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}
	
	private void initBroadCastReceiver() {
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String name = intent.getStringExtra(BroadcastUtils.INTENT_EXTRA_CHANNEL_NAME);
				String avatar = intent.getStringExtra(BroadcastUtils.INTENT_EXTRA_CHANNEL_AVATAR);
				String lastMessage =
						intent.getStringExtra(BroadcastUtils.INTENT_EXTRA_LAST_CHANNEL_MESSAGE);
				String channelUrl =
						intent.getStringExtra(BroadcastUtils.INTENT_EXTRA_CHAT_CHANNEL_URL);
				boolean isGroup =
						intent.getBooleanExtra(BroadcastUtils.INTENT_EXTRA_IS_GROUP, false);
				
				if (BaseActivity.this instanceof MainActivity) {
					MainActivity.instance.updateBadgeCount();
					MainActivity mainActivity = (MainActivity) BaseActivity.this;
					Fragment f1 = mainActivity.getSupportFragmentManager().findFragmentById(
							R.id.fragment_container);
					if (f1 instanceof TalkHolderFragment) {
						TalkHolderFragment talkHolderFragment = (TalkHolderFragment) f1;
						Fragment f2 =
								talkHolderFragment.getChildFragmentManager().findFragmentById(
								R.id.container);
						if (f2 instanceof TalkFragment) {
							TalkFragment talkFragment = (TalkFragment) f2;
							if (talkFragment.getCurrentPageIndex() != 0) {
								getPopupIntent(BaseActivity.this, name, avatar, isGroup,
										channelUrl,
										lastMessage);
							}
						}
					} else {
						getPopupIntent(BaseActivity.this, name, avatar, isGroup, channelUrl,
								lastMessage);
					}
				} else if (BaseActivity.this instanceof CompanionMainActivity) {
					CompanionMainActivity mainActivity = (CompanionMainActivity) BaseActivity.this;
					Fragment f1 = mainActivity.getSupportFragmentManager().findFragmentById(
							R.id.fragment_container);
					if (f1 instanceof TalkHolderFragment) {
						TalkHolderFragment talkHolderFragment = (TalkHolderFragment) f1;
						Fragment f2 =
								talkHolderFragment.getChildFragmentManager().findFragmentById(
								R.id.container);
						if (f2 instanceof TalkFragment) {
							TalkFragment talkFragment = (TalkFragment) f2;
							if (talkFragment.getCurrentPageIndex() != 0) {
								getPopupIntent(BaseActivity.this, name, avatar, isGroup,
										channelUrl,
										lastMessage);
							}
						}
					} else {
						getPopupIntent(BaseActivity.this, name, avatar, isGroup, channelUrl,
								lastMessage);
					}
				} else {
					getPopupIntent(BaseActivity.this, name, avatar, isGroup, channelUrl,
							lastMessage);
				}
			}
		};
		
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				
				if (intent.getAction().equals(BroadcastStringforAction)){
					
					if (intent.getStringExtra("online_status").equals("true")){
						ifInternetConnected();
					}else {
						ifInternetNotConnected();
					}
					
				}
				
			}
		};
		
		
	}
	
	private void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BroadcastUtils.INTENT_ACTION_NEW_CHAT_MESSAGE);
		registerReceiver(mReceiver, intentFilter);
	}
	
	public static void getPopupIntent(Context context, String channelName, String channelAvatar,
	                                  boolean isGroup, String channelUrl, String lastMessage) {
		Intent intent = new Intent(context, NewMessageActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		intent.putExtra(NewMessageActivity.EXTRA_SENDER_NAME, channelName);
		intent.putExtra(NewMessageActivity.EXTRA_CHANNEL_AVATAR, channelAvatar);
		intent.putExtra(NewMessageActivity.EXTRA_IS_GROUP, isGroup);
		intent.putExtra(NewMessageActivity.EXTRA_CHANNEL_URL, channelUrl);
		intent.putExtra(NewMessageActivity.EXTRA_LAST_MSG, lastMessage);
		context.startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {
		// if there is a fragment and the back stack of this fragment is not empty,
		// then emulate 'onBackPressed' behaviour, because in default, it is not working
		FragmentManager fm = getSupportFragmentManager();
		for (Fragment frag : fm.getFragments()) {
			if (frag.isVisible()) {
				FragmentManager childFm = frag.getChildFragmentManager();
				if (childFm.getBackStackEntryCount() > 0) {
					childFm.popBackStack();
					return;
				}
			}
		}
		super.onBackPressed();
	}
	
	public static BaseActivity getInstance() {
		return instance;
	}
	
	public DiloagBoxCommon Alertmessage(final Context context, String titleString,
	                                    String descriptionString, String negetiveText,
	                                    String positiveText) {
		DiloagBoxCommon diloagBoxCommon = new DiloagBoxCommon();
		
		LayoutInflater inflater =
				(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.common_popup_layout, null);
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(context,
				R.style.MyDialogTheme);
		
		builder.setView(layout);
		alertDialog = builder.create();
		alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		alertDialog.setCancelable(false);
		alertDialog.getWindow().setGravity(Gravity.CENTER);
		alertDialog.show();
		alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent_black);
		
		TextView title_popup = layout.findViewById(R.id.title_popup);
		TextView message_popup = layout.findViewById(R.id.message_popup);
		TextView no_text_popup = layout.findViewById(R.id.no_text_popup);
		TextView yes_text_popup = layout.findViewById(R.id.yes_text_popup);
		title_popup.setText(titleString);
		message_popup.setText(descriptionString);
		no_text_popup.setText(negetiveText);
		yes_text_popup.setText(positiveText);
		
		no_text_popup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				alertDialog.dismiss();
			}
		});
		
		alertDialog.show();
		diloagBoxCommon.setDialog(alertDialog);
		diloagBoxCommon.setTextViewNew(no_text_popup);
		diloagBoxCommon.setTextView(yes_text_popup);
		
		return diloagBoxCommon;
	}
	
	public void showProgressDialog(String message) {
		if (progressDialog == null)
			progressDialog = new CustomProgressDialog(this, message);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	public void hideProgressDialog() {
		if (progressDialog != null)
			progressDialog.dismiss();
	}
	
	public void HideSoftKeyboard(View view) {
		InputMethodManager imm =
				(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	public void logout_app(String message) {
		LayoutInflater inflater =
				(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.send_successfully_layout, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
		
		builder.setView(layout);
		builder.setCancelable(false);
		alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		alertDialog.show();
		
		TextView OK_txt = layout.findViewById(R.id.OK_txt);
		TextView title_txt = layout.findViewById(R.id.title_txt);
		
		title_txt.setText(message);
		
		OK_txt.setOnClickListener(v -> {
			stopButtonClicked();
			Utility.clearSharedPreference(getApplicationContext());
			alertDialog.dismiss();
		});
		
	}
	
	public void stopButtonClicked() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		finish();
	}
	
	public boolean isOnline (Context context){
		ConnectivityManager
				cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		
		if (activeNetwork!=null && activeNetwork.isConnectedOrConnecting()) {
			return true;
		}else {
			return false;
		}
		
	}
	
	
	
	public void ifInternetConnected(){
		//mainBinding.ConnectionStatus.setText("Connected");
		if (alertDialog1 != null) {
			alertDialog1.dismiss();
			alertDialog1 = null;
		}
	}
	
	public void ifInternetNotConnected(){
		//mainBinding.ConnectionStatus.setText("Please Check Internet");
		if (alertDialog1 == null) {
			LayoutInflater inflater =
					(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.internet_connectivity_popup, null);
			final AlertDialog.Builder builder =
					new AlertDialog.Builder(BaseActivity.this, R.style.FullScreenDialogStyle);
			
			builder.setView(layout);
			builder.setCancelable(false);
			alertDialog1 = builder.create();
			alertDialog1.setCanceledOnTouchOutside(false);
			int width = ViewGroup.LayoutParams.MATCH_PARENT;
			int height = ViewGroup.LayoutParams.MATCH_PARENT;
			alertDialog1.getWindow().setLayout(width, height);
			alertDialog1.getWindow().setBackgroundDrawableResource(android.R.color.white);
			alertDialog1.show();
			
			TextView setting = layout.findViewById(R.id.setting);
			
			setting.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					startActivityForResult(new Intent(Settings.ACTION_SETTINGS)
							, 0);
					
					
				}
			});
			
		}
	}
	
	/*Code for quick response of isOnline End*/
	
	@Override
	protected void onRestart() {
		super.onRestart();
		registerReceiver(receiver,mIntentFilter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver,mIntentFilter);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}
}
