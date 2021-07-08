package com.soultabcaregiver.sendbird_chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBird;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.sendbird_chat.utils.ConnectionManager;
import com.soultabcaregiver.sendbird_chat.utils.SpaceItemDecoration;
import com.soultabcaregiver.sinch_calling.BaseFragment;
import com.soultabcaregiver.utils.Utility;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.soultabcaregiver.sendbird_chat.ConversationActivity.EXTRA_GROUP_CHANNEL_URL;

public class ChatListFragment extends BaseFragment {
	
	private static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHANNEL_LIST";
	
	private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_LIST";
	
	private RecyclerView mRecyclerView;
	
	private FloatingActionButton newChatFabBtn;
	
	private LinearLayoutManager mLayoutManager;
	
	private GroupChannelListQuery mChannelListQuery;
	
	private ChatListAdapter mChannelListAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
		mRecyclerView = view.findViewById(R.id.recycler);
		newChatFabBtn = view.findViewById(R.id.newChatBtn);
		
		newChatFabBtn.setOnClickListener(v -> {
			addCreateGroupFragment();
			//startActivity(new Intent(getContext(), CreateGroupFragment.class));
		});
		
		return view;
	}
	
	private void addCreateGroupFragment() {
		ChatFragment chatFragment = ((ChatFragment) getParentFragment());
		if (chatFragment != null) {
			chatFragment.navigateToCreateGroupFragment();
		}
	}
	
	@Override
	public void onViewCreated(@NonNull @NotNull View view,
	                          @Nullable @org.jetbrains.annotations.Nullable
			                          Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mChannelListAdapter = new ChatListAdapter(getActivity());
		mChannelListAdapter.load();
		setUpRecyclerView();
		setUpChannelListAdapter();
		getMyChatChannels();
	}
	
	// Sets up recycler view
	private void setUpRecyclerView() {
		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.setAdapter(mChannelListAdapter);
		mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		mRecyclerView.addItemDecoration(new SpaceItemDecoration(4, true, true));
		// If user scrolls to bottom of the list, loads more channels.
		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				if (mLayoutManager.findLastVisibleItemPosition() == mChannelListAdapter.getItemCount() - 1) {
					loadNextChannelList();
				}
			}
		});
	}
	
	// Sets up channel list adapter
	private void setUpChannelListAdapter() {
		mChannelListAdapter.setOnItemClickListener(this :: enterGroupChannel);
		
		mChannelListAdapter.setOnItemLongClickListener(channel -> {
			//showChannelOptionsDialog(channel);
		});
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
			//			ArrayList<GroupChannel> channels = new ArrayList<>();
			//			channels.addAll(list);
			//			channels.addAll(list);
			//			channels.addAll(list);
			mChannelListAdapter.setChannels(list);
		});
	}
	
	/**
	 * Loads the next channels from the current query instance.
	 */
	private void loadNextChannelList() {
		mChannelListQuery.next((list, e) -> {
			if (e != null) {
				// Error!
				e.printStackTrace();
				return;
			}
			
			for (GroupChannel channel : list) {
				mChannelListAdapter.addLast(channel);
			}
		});
	}
	
	private void enterGroupChannel(GroupChannel channel) {
		Intent intent = new Intent(getContext(), ConversationActivity.class);
		intent.putExtra(EXTRA_GROUP_CHANNEL_URL, channel.getUrl());
		startActivity(intent);
	}
	
	@Override
	public void onResume() {
		Log.d("LIFECYCLE", "GroupChannelListFragment onResume()");
		
		ConnectionManager.addConnectionManagementHandler(
				Utility.getSharedPreferences(getContext(), APIS.caregiver_id),
				CONNECTION_HANDLER_ID, reconnect -> refresh());
		
		SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
			@Override
			public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
			}
			
			@Override
			public void onChannelChanged(BaseChannel channel) {
				mChannelListAdapter.updateOrInsert(channel);
			}
			
			@Override
			public void onTypingStatusUpdated(GroupChannel channel) {
				mChannelListAdapter.updateOrInsert(channel);
				mChannelListAdapter.notifyDataSetChanged();
			}
		});
		
		super.onResume();
	}
	
	private void refresh() {
		getMyChatChannels();
	}
	
	@Override
	public void onPause() {
		mChannelListAdapter.save();
		ConnectionManager.removeConnectionManagementHandler(CONNECTION_HANDLER_ID);
		SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
		super.onPause();
	}
	
	
}
