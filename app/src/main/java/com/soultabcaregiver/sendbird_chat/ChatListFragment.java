package com.soultabcaregiver.sendbird_chat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBird;
import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.main_screen.MainActivity;
import com.soultabcaregiver.companion.CompanionMainActivity;
import com.soultabcaregiver.companion.OnDemandVisitStatus;
import com.soultabcaregiver.companion.UserListForCompanionModel;
import com.soultabcaregiver.companion.UserListForCompanionResponse;
import com.soultabcaregiver.sendbird_chat.utils.ConnectionManager;
import com.soultabcaregiver.sendbird_chat.utils.SpaceItemDecoration;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListFragment extends BaseFragment {
	
	private static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHANNEL_LIST";
	
	private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_LIST";
	
	public static final String REQUEST_KEY_FOR_CREATE_GROUP = "request_key_for_create_group";
	
	public static final String EXTRA_GROUP_CHANNEL_URL = "extra_group_channel_url";
	
	Context mContext;
	
	private RecyclerView mRecyclerView;
	
	private FloatingActionButton newChatFabBtn, newGroupChatBtn;
	
	private LinearLayoutManager mLayoutManager;
	
	private GroupChannelListQuery mChannelListQuery;
	
	private ChatListAdapter mChannelListAdapter;
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getActivity();
		
		getActivity().getSupportFragmentManager().setFragmentResultListener(
				REQUEST_KEY_FOR_CREATE_GROUP, this, (requestKey, result) -> {
					if (requestKey.equals(REQUEST_KEY_FOR_CREATE_GROUP)) {
						String groupChannel = result.getString(EXTRA_GROUP_CHANNEL_URL);
						enterGroupChannel(groupChannel);
					}
				});
		
	}
	
	@Override
	public void onViewCreated(@NonNull @NotNull View view,
	                          @Nullable @org.jetbrains.annotations.Nullable
			                          Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mChannelListAdapter = new ChatListAdapter(getActivity());
		if (Utility.getSharedPreferences(mContext, APIS.is_companion).equals("0")) {
			mChannelListAdapter.load(false);
		} else {
			mChannelListAdapter.load(true);
		}
		setUpRecyclerView();
		setUpChannelListAdapter();
		
		if (Utility.getSharedPreferences(mContext, APIS.is_companion).equals("1")) {
			getUsersForCompanion();
		} else {
			getMyChatChannels();
		}
		
	}
	
	@Override
	public void onResume() {
		Log.d("LIFECYCLE", "GroupChannelListFragment onResume()");
		if (Utility.getSharedPreferences(mContext, APIS.is_companion).equals("0")) {
			MainActivity.getInstance().Alert_countAPI();
		} else {
			if (getActivity() instanceof CompanionMainActivity) {
				CompanionMainActivity companionMainActivity =
						(CompanionMainActivity) getActivity();
				companionMainActivity.Alert_countAPI();
			}
		}
		ConnectionManager.addConnectionManagementHandler(
				Utility.getSharedPreferences(getContext(), APIS.caregiver_id),
				CONNECTION_HANDLER_ID, reconnect -> refresh());
		
		SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
			@Override
			public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
				Log.e("SendBird", baseMessage.getMessage());
				mChannelListAdapter.updateOrInsert(baseChannel);
			}
			
			@Override
			public void onChannelChanged(BaseChannel channel) {
				mChannelListAdapter.updateOrInsert(channel);
				if (Utility.getSharedPreferences(mContext, APIS.is_companion).equals("0")) {
					MainActivity.instance.Alert_countAPI();
				} else {
					if (getActivity() instanceof CompanionMainActivity) {
						CompanionMainActivity companionMainActivity =
								(CompanionMainActivity) getActivity();
						companionMainActivity.Alert_countAPI();
					}
				}
			}
			
			@Override
			public void onTypingStatusUpdated(GroupChannel channel) {
				mChannelListAdapter.updateOrInsert(channel);
				mChannelListAdapter.notifyDataSetChanged();
			}
		});
		
		super.onResume();
		
	}
	
	@Override
	public void onPause() {
		mChannelListAdapter.save();
		ConnectionManager.removeConnectionManagementHandler(CONNECTION_HANDLER_ID);
		SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
		super.onPause();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
		mRecyclerView = view.findViewById(R.id.recycler);
		newChatFabBtn = view.findViewById(R.id.newChatBtn);
		newGroupChatBtn = view.findViewById(R.id.newGroupButton);
		
		newChatFabBtn.setOnClickListener(v -> addCreateGroupFragment(false));
		
		newGroupChatBtn.setOnClickListener(v -> addCreateGroupFragment(true));
		
		if (Utility.getSharedPreferences(mContext, APIS.is_companion).equals("1")) {
			newChatFabBtn.setVisibility(View.GONE);
			newGroupChatBtn.setVisibility(View.GONE);
		}
		
		return view;
	}
	
	public void getSendBirdData(final int index, List<UserListForCompanionModel> list) {
		ArrayList<String> ids = new ArrayList<>();
		ids.add(Utility.getSharedPreferences(mContext, APIS.caregiver_id));
		ids.add(list.get(index).getUserId());
		if (list.get(index).getUserSendbirdUser().equals("1")) {
			ChatHelper.createGroupChannel(ids, true, groupChannel -> {
				Log.e("channel", "" + groupChannel.getUrl());
				mChannelListAdapter.addGroupChannel(groupChannel);
				if (list.size() > (index + 1)) {
					getSendBirdData((index + 1), list);
				} else {
					hideProgressDialog();
				}
			});
		}
	}
	
	private void getUsersForCompanion() {
		JSONObject mainObject = new JSONObject();
		try {
			mainObject.put("companion_id",
					Utility.getSharedPreferences(mContext, APIS.caregiver_id));
			mainObject.put("status",
					OnDemandVisitStatus.IN_PROGRESS.toString() + "," + OnDemandVisitStatus.COMPLETED.toString() + "," + OnDemandVisitStatus.PENDING.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		showProgressDialog(mContext, getResources().getString(R.string.Loading));
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
				APIS.BASEURL + APIS.getUsersListForCompanion, mainObject, response -> {
			Log.e("ChatListFragment", response.toString());
			try {
				UserListForCompanionResponse userListForCompanionResponse =
						new Gson().fromJson(response.toString(),
								UserListForCompanionResponse.class);
				if (userListForCompanionResponse.getStatusCode() == 200) {
					getMyChatChannels();
					getSendBirdData(0, userListForCompanionResponse.getResponse());
				} else {
					getMyChatChannels();
					hideProgressDialog();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, error -> hideProgressDialog()) {
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
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjReq);
	}
	
	private void addCreateGroupFragment(boolean isForGroupChat) {
		ChatFragment chatFragment = ((ChatFragment) getParentFragment());
		if (chatFragment != null) {
			chatFragment.navigateToCreateGroupFragment(isForGroupChat);
		}
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
	
	//userIds only for Companion Application
	private void getMyChatChannels() {
		mChannelListQuery = GroupChannel.createMyGroupChannelListQuery();
		mChannelListQuery.setIncludeEmpty(
				Utility.getSharedPreferences(mContext, APIS.is_companion).equals("0"));
		mChannelListQuery.setMemberStateFilter(GroupChannelListQuery.MemberStateFilter.ALL);
		mChannelListQuery.next((list, e) -> {
			if (e != null) {
				// Error!
				e.printStackTrace();
				return;
			}
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
	
	private void enterGroupChannel(String channelUrl) {
		ChatFragment chatFragment = ((ChatFragment) getParentFragment());
		if (chatFragment != null) {
			chatFragment.navigateToConversationFragment(channelUrl);
		}
	}
	
	private void refresh() {
		//getMyChatChannels();
	}
	
	
}
