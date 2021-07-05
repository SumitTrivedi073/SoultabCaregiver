package com.soultabcaregiver.sendbird_chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.soultabcaregiver.R;
import com.soultabcaregiver.sendbird_chat.utils.TextUtils;
import com.soultabcaregiver.sinch_calling.BaseFragment;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ChatListFragment extends BaseFragment {
	
	private GroupChannelListQuery mChannelListQuery;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
		return view;
	}
	
	@Override
	public void onViewCreated(@NonNull @NotNull View view,
	                          @Nullable @org.jetbrains.annotations.Nullable
			                          Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getMyChatChannels();
	}
	
	private void getMyChatChannels() {
		mChannelListQuery = GroupChannel.createMyGroupChannelListQuery();
		mChannelListQuery.setIncludeEmpty(true);
		mChannelListQuery.setMemberStateFilter(GroupChannelListQuery.MemberStateFilter.ALL);
		
		mChannelListQuery.next((list, e) -> {
			if (e != null) {
				// Error!
				e.printStackTrace();
				return;
			}
			for (int i = 0; i < list.size(); i++) {
				Log.e("ChannelName", TextUtils.getGroupChannelTitle(list.get(i)));
			}
			Log.e("channelList", list.toString());
		});
	}
	
}
