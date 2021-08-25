package com.soultabcaregiver.talk;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.alert.fragment.AlertFragment;
import com.soultabcaregiver.activity.alert.model.AlertCountModel;
import com.soultabcaregiver.sendbird_chat.CallListFragment;
import com.soultabcaregiver.sendbird_chat.ChatFragment;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import static com.soultabcaregiver.sendbird_chat.ConversationFragment.EXTRA_GROUP_CHANNEL_URL;

public class TalkFragment extends BaseFragment {
	
	public static TalkFragment instance;
	
	private final String TAG = getClass().getSimpleName();
	
	TabLayout tabs;
	
	TabLayout itemView;
	
	View badge;
	
	TextView tv_badge;
	
	Context mContext;
	
	private ViewPager viewPager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_talk, container, false);
		
		mContext = getActivity();
		instance = TalkFragment.this;
		tabs = view.findViewById(R.id.tabs);
		viewPager = view.findViewById(R.id.viewpager);
		tabs.setupWithViewPager(viewPager);
		setupViewPager(viewPager);
		
		setBadge();
		
		return view;
	}
	
	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
		adapter.addFragment(new ChatFragment(), getString(R.string.chat));
		adapter.addFragment(new AlertFragment(), getString(R.string.alert));
		adapter.addFragment(new CallListFragment(), getString(R.string.calls));
		viewPager.setAdapter(adapter);
		
		
	}
	
	public void setBadge() {
		BadgeDrawable badgeDrawable = Objects.requireNonNull(tabs.getTabAt(1)).getOrCreateBadge();
		badgeDrawable.setVisible(true);
		badgeDrawable.setNumber(
				Integer.parseInt(Utility.getSharedPreferences(mContext, APIS.BadgeCount)));
		badgeDrawable.setBackgroundColor(ContextCompat.getColor(mContext, R.color.orange_color));
	}
	
	@Override
	public void onViewCreated(@NonNull @NotNull View view,
	                          @Nullable @org.jetbrains.annotations.Nullable
			                          Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (getArguments() != null && getArguments().getString(EXTRA_GROUP_CHANNEL_URL) != null) {
			viewPager.setCurrentItem(0);
			navigateToConversationFragment(getArguments().getString(EXTRA_GROUP_CHANNEL_URL));
		}
	}
	
	public void navigateToConversationFragment(String url) {
		TalkHolderFragment talkHolderFragment = (TalkHolderFragment) getParentFragment();
		if (talkHolderFragment != null) {
			talkHolderFragment.navigateToConversationFragment(url);
		}
	}
	
	public static TalkFragment newInstance(String channelUrl) {
		Bundle args = new Bundle();
		TalkFragment fragment = new TalkFragment();
		if (channelUrl != null) {
			args.putString(EXTRA_GROUP_CHANNEL_URL, channelUrl);
		}
		fragment.setArguments(args);
		return fragment;
	}
	
	public void Alert_countAPI() {
		
		JSONObject mainObject = new JSONObject();
		try {
			mainObject.put("user_id", Utility.getSharedPreferences(mContext, APIS.caregiver_id));
			
			Log.e(TAG, "AlertCount API========>" + mainObject.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			
		}
		
		JsonObjectRequest jsonObjReq =
				new JsonObjectRequest(Request.Method.POST, APIS.BASEURL + APIS.AlertCount,
						mainObject, response -> {
					Log.e(TAG, "AlertCount response=" + response.toString());
					hideProgressDialog();
					
					AlertCountModel alertCountModel =
							new Gson().fromJson(response.toString(), AlertCountModel.class);
					
					if (String.valueOf(alertCountModel.getStatusCode()).equals("200")) {
						
						itemView.removeView(badge);
						if (alertCountModel.getResponse().getUnreadCount() > 9) {
							tv_badge.setText("9+");
							itemView.addView(badge);
							
						} else {
							
							tv_badge.setText(
									String.valueOf(alertCountModel.getResponse().getUnreadCount()));
							itemView.addView(badge);
						}
						
						
					} else if (String.valueOf(alertCountModel.getStatusCode()).equals("403")) {
						logout_app(alertCountModel.getMessage());
					} else {
						Utility.ShowToast(mContext, alertCountModel.getMessage());
					}
					
				}, error -> {
					VolleyLog.d(TAG, "Error: " + error.getMessage());
					hideProgressDialog();
				}) {
					@Override
					public Map<String, String> getHeaders() {
						Map<String, String> params = new HashMap<>();
						params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
						params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
						params.put(APIS.HEADERKEY2,
								Utility.getSharedPreferences(mContext, APIS.EncodeUser_id));
						
						return params;
					}
					
				};
		AppController.getInstance().addToRequestQueue(jsonObjReq);
		jsonObjReq.setShouldCache(false);
		jsonObjReq.setRetryPolicy(
				new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		
		
	}
	
	public void navigateToCreateGroupFragment(boolean isForGroupChat) {
		TalkHolderFragment talkHolderFragment = (TalkHolderFragment) getParentFragment();
		if (talkHolderFragment != null) {
			talkHolderFragment.navigateToCreateGroupFragment(isForGroupChat);
		}
	}
	
	public int getCurrentPageIndex() {
		return viewPager.getCurrentItem();
	}
	
}

class ViewPagerAdapter extends FragmentPagerAdapter {
	
	private final List<Fragment> mFragmentList = new ArrayList<>();
	
	private final List<String> mFragmentTitleList = new ArrayList<>();
	
	public ViewPagerAdapter(FragmentManager manager) {
		super(manager);
	}
	
	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}
	
	@Override
	public int getCount() {
		return mFragmentList.size();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return mFragmentTitleList.get(position);
	}
	
	public void addFragment(Fragment fragment, String title) {
		mFragmentList.add(fragment);
		mFragmentTitleList.add(title);
	}
	
	
}

