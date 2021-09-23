package com.soultabcaregiver.talk;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.alert.fragment.AlertFragment;
import com.soultabcaregiver.activity.main_screen.MainActivity;
import com.soultabcaregiver.companion.CompanionMainActivity;
import com.soultabcaregiver.sendbird_chat.CallListFragment;
import com.soultabcaregiver.sendbird_chat.ChatFragment;
import com.soultabcaregiver.utils.Utility;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
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
	
	MainActivity mainActivity;
	
	private ViewPager viewPager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_talk, container, false);
		
		mContext = getActivity();
		instance = TalkFragment.this;
		mainActivity = MainActivity.instance;
		tabs = view.findViewById(R.id.tabs);
		viewPager = view.findViewById(R.id.viewpager);
		viewPager.setOffscreenPageLimit(1);
		tabs.setupWithViewPager(viewPager);
		setupViewPager(viewPager);
		
		setBadge();
		
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset,
			                           int positionOffsetPixels) {
				
			}
			
			@Override
			public void onPageSelected(int position) {
				if (position == 1) {
					
					setBadge2();
					if (Utility.getSharedPreferences(mContext, APIS.is_companion).equals("0")) {
						if (mainActivity != null) {
							mainActivity.updatebadge();
							mainActivity.updateBadgeCount();
						}
					} else {
						if (getActivity() instanceof CompanionMainActivity) {
							CompanionMainActivity companionMainActivity =
									(CompanionMainActivity) getActivity();
							companionMainActivity.Alert_countAPI();
						}
					}
					
				}
				
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
			
			}
		});
		
		return view;
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
	
	public static TalkFragment newInstance(String channelUrl) {
		Bundle args = new Bundle();
		TalkFragment fragment = new TalkFragment();
		if (channelUrl != null) {
			args.putString(EXTRA_GROUP_CHANNEL_URL, channelUrl);
		}
		fragment.setArguments(args);
		return fragment;
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
		if (Utility.getSharedPreferences(mContext, APIS.BadgeCount) != null && !String.valueOf(
				Utility.getSharedPreferences(mContext, APIS.BadgeCount)).equals("")) {
			badgeDrawable.setNumber(
					Integer.parseInt(Utility.getSharedPreferences(mContext, APIS.BadgeCount)));
		} else {
			badgeDrawable.setNumber(0);
		}
		badgeDrawable.setBackgroundColor(ContextCompat.getColor(mContext, R.color.orange_color));
		
	}
	
	public void setBadge2() {
		Utility.setSharedPreference(mContext, APIS.BadgeCount, "0");
		BadgeDrawable badgeDrawable = Objects.requireNonNull(tabs.getTabAt(1)).getOrCreateBadge();
		badgeDrawable.setVisible(true);
		badgeDrawable.setNumber(0);
		badgeDrawable.setBackgroundColor(ContextCompat.getColor(mContext, R.color.orange_color));
		
	}
	
	public void navigateToConversationFragment(String url) {
		TalkHolderFragment talkHolderFragment = (TalkHolderFragment) getParentFragment();
		if (talkHolderFragment != null) {
			talkHolderFragment.navigateToConversationFragment(url);
		}
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

