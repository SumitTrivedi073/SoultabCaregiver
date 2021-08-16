package com.soultabcaregiver.sendbird_chat;

import android.content.Context;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBirdException;
import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.sendbird_chat.adapter.GroupMemberListAdapter;
import com.soultabcaregiver.sendbird_chat.model.GroupMemberModel;
import com.soultabcaregiver.sendbird_chat.utils.TextUtils;
import com.soultabcaregiver.utils.Utility;

import java.util.ArrayList;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import static com.soultabcaregiver.sendbird_chat.ConversationFragment.EXTRA_GROUP_CHANNEL_URL;

public class GroupDetailActivity extends BaseActivity {
	
	Context mContext;
	
	TextView titleTextView;
	
	RelativeLayout backButton;
	
	ArrayList<GroupMemberModel> groupMemberList = new ArrayList<>();
	
	private GroupChannel mChannel;
	
	private String mChannelUrl;
	
	private TextView group_member_txt;
	
	private RecyclerView group_user_list;
	
	private SearchView searchView;
	
	private GroupMemberListAdapter groupMemberListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_detail);
		
		mContext = this;
		// Get channel URL from GroupChannelListFragment.
		mChannelUrl = getIntent().getStringExtra(EXTRA_GROUP_CHANNEL_URL);
		
		setupControls();
		refresh();
		
	}
	
	private void setupControls() {
		backButton = findViewById(R.id.back_btn);
		titleTextView = findViewById(R.id.chatTitle);
		group_member_txt = findViewById(R.id.group_member_txt);
		group_user_list = findViewById(R.id.group_user_list);
		searchView = findViewById(R.id.group_member_search);
		
		backButton.setOnClickListener(v -> {
			onBackPressed();
		});
		
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String stext) {
				if (groupMemberListAdapter != null) {
					groupMemberListAdapter.getFilter().filter(stext);
				}
				return false;
			}
		});
		
	}
	
	private void refresh() {
		if (mChannel == null) {
			GroupChannel.getChannel(mChannelUrl, (groupChannel, e) -> {
				if (e != null) {
					// Error!
					e.printStackTrace();
					return;
				}
				
				mChannel = groupChannel;
				
				updateActionBarTitle();
			});
		} else {
			mChannel.refresh(new GroupChannel.GroupChannelRefreshHandler() {
				@Override
				public void onResult(SendBirdException e) {
					if (e != null) {
						// Error!
						e.printStackTrace();
						return;
					}
					updateActionBarTitle();
				}
			});
		}
	}
	
	private void updateActionBarTitle() {
		String title = "";
		
		if (mChannel != null) {
			title = TextUtils.getGroupChannelTitle(mChannel);
			getgroup_members();
		}
		
		// Set action bar title to name of channel
		titleTextView.setText(title);
	}
	
	private void getgroup_members() {
		ArrayList<String> membersIds = new ArrayList<>();
		groupMemberList = new ArrayList<>();
		
		for (Member member : mChannel.getMembers()) {
			if (!member.getUserId().equals(
					Utility.getSharedPreferences(mContext, APIS.caregiver_id))) {
				membersIds.add(member.getUserId());
				GroupMemberModel groupMemberModel = new GroupMemberModel();
				groupMemberModel.setId(String.valueOf(member.getUserId()));
				groupMemberModel.setNickname(member.getNickname());
				groupMemberModel.setImage(member.getProfileUrl());
				groupMemberList.add(groupMemberModel);
			}
		}
		String userIds = android.text.TextUtils.join(",", membersIds);
		
		group_member_txt.setText(groupMemberList.size() + " " + getString(R.string.Participants));
		
		groupMemberListAdapter = new GroupMemberListAdapter(mContext, groupMemberList);
		group_user_list.setHasFixedSize(true);
		group_user_list.setAdapter(groupMemberListAdapter);
		
	}
	
	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
		
	}
}