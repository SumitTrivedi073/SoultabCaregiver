package com.soultabcaregiver.sendbird_group_call;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sendbird.android.GroupChannel;
import com.soultabcaregiver.R;
import com.soultabcaregiver.utils.Utility;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.soultabcaregiver.sendbird_chat.ConversationFragment.EXTRA_LIST_FOR_GROUP_CALL_MEMBERS;
import static com.soultabcaregiver.sendbird_chat.ConversationFragment.REQUEST_KEY_FOR_GROUP_CALL_MEMBERS;

public class SelectGroupCallMemberFragment extends Fragment {
	
	public static String EXTRA_CHANNEL_URL = "extra_channel_url";
	
	private final ArrayList<String> mSelectedIds = new ArrayList<>();
	
	private final SelectGroupCallMembersAdapter.OnItemCheckedChangeListener
			onItemCheckedChangeListener = (member, checked) -> {
		if (checked) {
			mSelectedIds.add(member.getUserId());
		} else {
			mSelectedIds.remove(member.getUserId());
		}
	};
	
	private RecyclerView recyclerView;
	
	private TextView backButton;
	
	private TextView startCallBtn;
	
	private RelativeLayout progressLayout;
	
	private SelectGroupCallMembersAdapter adapter;
	
	@Nullable
	@Override
	public View onCreateView(@NonNull @NotNull LayoutInflater inflater,
	                         @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_select_group_call, container, false);
		
		recyclerView = view.findViewById(R.id.recycler);
		backButton = view.findViewById(R.id.backButton);
		startCallBtn = view.findViewById(R.id.startCallButton);
		progressLayout = view.findViewById(R.id.progressLayout);
		
		setupClickListeners();
		setupRecyclerView();
		return view;
	}
	
	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (getArguments() != null) {
			String channelUrl = getArguments().getString(EXTRA_CHANNEL_URL);
			GroupChannel.getChannel(channelUrl, (groupChannel, e) -> {
				if (groupChannel != null) {
					adapter.setMembers(groupChannel.getMembers());
					recyclerView.setVisibility(View.VISIBLE);
					progressLayout.setVisibility(View.GONE);
				}
			});
		}
	}
	
	public static SelectGroupCallMemberFragment newInstance(String channelUrl) {
		SelectGroupCallMemberFragment fragment = new SelectGroupCallMemberFragment();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_CHANNEL_URL, channelUrl);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	private void setupRecyclerView() {
		recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
		adapter = new SelectGroupCallMembersAdapter(onItemCheckedChangeListener);
		recyclerView.setAdapter(adapter);
	}
	
	private void setupClickListeners() {
		backButton.setOnClickListener(
				v -> requireActivity().getSupportFragmentManager().popBackStack());
		startCallBtn.setOnClickListener(v -> {
			if (mSelectedIds.isEmpty()) {
				Utility.showSnackBar(getView(),
						getString(R.string.please_select_members_for_call));
				return;
			}
			
			if (mSelectedIds.size() > 5) {
				Utility.showSnackBar(getView(), getString(R.string.maximum_five_members_allowed));
				return;
			}
			
			Bundle bundle = new Bundle();
			bundle.putStringArrayList(EXTRA_LIST_FOR_GROUP_CALL_MEMBERS, mSelectedIds);
			
			requireActivity().getSupportFragmentManager().setFragmentResult(
					REQUEST_KEY_FOR_GROUP_CALL_MEMBERS, bundle);
			requireActivity().getSupportFragmentManager().popBackStack();
			
		});
	}
}
