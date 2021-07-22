package com.soultabcaregiver.talk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.alert.fragment.AlertFragment;
import com.soultabcaregiver.sendbird_chat.CallListFragment;
import com.soultabcaregiver.sendbird_chat.ChatFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class TalkFragment extends BaseFragment {
	
	private ViewPager viewPager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_talk, container, false);
		TabLayout tabs = view.findViewById(R.id.tabs);
		viewPager = view.findViewById(R.id.viewpager);
		tabs.setupWithViewPager(viewPager);
		setupViewPager(viewPager);
		return view;
	}
	
	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
		adapter.addFragment(new ChatFragment(), getString(R.string.chat));
		adapter.addFragment(new AlertFragment(), getString(R.string.alert));
		adapter.addFragment(new CallListFragment(), getString(R.string.calls));
		viewPager.setAdapter(adapter);
	}
	
	@Override
	public void onViewCreated(@NonNull @NotNull View view,
	                          @Nullable @org.jetbrains.annotations.Nullable
			                          Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
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

