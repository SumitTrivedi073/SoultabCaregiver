package com.soultabcaregiver.Base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.soultabcaregiver.Model.DiloagBoxCommon;
import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.main_screen.MainActivity;
import com.soultabcaregiver.sendbird_calls.SendBirdAuthentication;
import com.soultabcaregiver.utils.CustomProgressDialog;
import com.soultabcaregiver.utils.Utility;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {
	
	public CustomProgressDialog progressDialog;
	
	public AlertDialog alertDialog;
	
	MainActivity mainActivity;
	
	public BaseFragment() {
		// Required empty public constructor
	}
	
	public void onRequestPermissionsResult(int requestCode, String[] permissions,
	                                       int[] grantResults) {
		boolean granted = grantResults.length > 0;
		for (int grantResult : grantResults) {
			granted &= grantResult == PackageManager.PERMISSION_GRANTED;
		}
		if (granted) {
			//Toast.makeText(this, "You may now place a call", Toast.LENGTH_LONG).show();
		} else {
			
			Utility.ShowToast(getActivity(),
					"This application needs permission to use your microphone and camera to " +
							"function properly.");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		TextView textView = new TextView(getActivity());
		textView.setText(R.string.hello_blank_fragment);
		
		mainActivity = MainActivity.instance;
		return textView;
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
	
	public void showProgressDialog(Context context, String message) {
		if (progressDialog == null)
			progressDialog = new CustomProgressDialog(context, message);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	public void hideProgressDialog() {
		if (progressDialog != null)
			progressDialog.dismiss();
	}
	
	public void logout_app(String message) {
		LayoutInflater inflater =
				(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.send_successfully_layout, null);
		final AlertDialog.Builder builder =
				new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
		
		builder.setView(layout);
		builder.setCancelable(false);
		alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		alertDialog.show();
		
		TextView OK_txt = layout.findViewById(R.id.OK_txt);
		TextView title_txt = layout.findViewById(R.id.title_txt);
		
		title_txt.setText(message);
		
		OK_txt.setOnClickListener(v -> SendBirdAuthentication.logout(isSuccess -> {
			BaseActivity baseActivity = (BaseActivity) getActivity();
			if (baseActivity != null) {
				baseActivity.stopButtonClicked();
				Utility.clearSharedPreference(getActivity());
				alertDialog.dismiss();
			}
		}));
		
	}
}
