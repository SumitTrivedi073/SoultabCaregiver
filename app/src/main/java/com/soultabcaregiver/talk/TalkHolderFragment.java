package com.soultabcaregiver.talk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.R;
import com.soultabcaregiver.sendbird_chat.ConversationFragment;
import com.soultabcaregiver.sendbird_chat.CreateGroupFragment;
import com.soultabcaregiver.sendbird_group_call.SelectGroupCallMemberFragment;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import static com.soultabcaregiver.sendbird_chat.ConversationFragment.EXTRA_GROUP_CHANNEL_URL;

public class TalkHolderFragment extends BaseFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_chat, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull @NotNull View view,
	                          @Nullable @org.jetbrains.annotations.Nullable
			                          Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		String channelUrl = null;
		if (getArguments() != null && getArguments().getString(EXTRA_GROUP_CHANNEL_URL) != null) {
			channelUrl = getArguments().getString(EXTRA_GROUP_CHANNEL_URL);
		}
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.add(R.id.container, TalkFragment.newInstance(channelUrl));
		transaction.commit();
	}
	
	public static TalkHolderFragment newInstance(String channelUrl) {
		Bundle args = new Bundle();
		TalkHolderFragment fragment = new TalkHolderFragment();
		if (channelUrl != null) {
			args.putString(EXTRA_GROUP_CHANNEL_URL, channelUrl);
		}
		fragment.setArguments(args);
		return fragment;
	}
	
	public void navigateToConversationFragment(String url) {
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.add(R.id.container, ConversationFragment.newInstance(url, false));
		transaction.addToBackStack(ConversationFragment.class.getName());
		transaction.commit();
	}
	
	public void navigateToSelectGroupMembersFragment(String url) {
		FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
		transaction.add(R.id.container, SelectGroupCallMemberFragment.newInstance(url));
		transaction.addToBackStack(SelectGroupCallMemberFragment.class.getName());
		transaction.commit();
	}
	
	public void navigateToCreateGroupFragment(boolean isForGroupChat) {
		FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
		transaction.add(R.id.container, CreateGroupFragment.newInstance(isForGroupChat));
		transaction.addToBackStack(CreateGroupFragment.class.getName());
		transaction.commit();
	}
	
}
