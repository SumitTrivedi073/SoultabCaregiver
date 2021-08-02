package com.soultabcaregiver.sendbird_group_call;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.sendbird.calls.AudioDevice;
import com.sendbird.calls.Room;
import com.sendbird.calls.SendBirdCall;
import com.soultabcaregiver.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

public class GroupCallFragment extends Fragment {
	
	public static final String EXTRA_ROOM_ID = "extra_room_id";
	public static final String EXTRA_CHANNEL_URL = "extra_channel_url";
	public static final String EXTRA_USERS_IDS = "extra_users_ids";
	
	private GroupCallViewModel viewModel;
	
	private Room room;
	
	private String roomId;
	private String userIds;
	private String channelUrl;
	
	private RecyclerView recyclerView;
	private ImageView groupCallImageViewVideoOnOff;
	private ImageView groupCallImageViewAudioOnOff;
	private ImageView groupCallLinearLayoutParticipants;
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_group_call, container, false);
		
		if (getArguments() != null) {
			roomId = getArguments().getString(EXTRA_ROOM_ID, "");
			Log.e("roomId", roomId);
			channelUrl = getArguments().getString(EXTRA_CHANNEL_URL, "");
			userIds = getArguments().getString(EXTRA_USERS_IDS, "");
			
		}
		viewModel = new GroupCallViewModel(roomId);
		
		initViews(view);
		initRecyclerView();
		observeViewModel();
		
		//
		setAudioEnabledImage(room.getLocalParticipant().isAudioEnabled());
		setVideoEnabledImage(room.getLocalParticipant().isVideoEnabled());
		
		return view;
	}
	
	@Override
	public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	// region Private methods
	
	private void initViews(View view) {
		
		room = SendBirdCall.getCachedRoomById(roomId);
		
		recyclerView = view.findViewById(R.id.group_call_recycler_view_participants);
		ImageView groupCallImageViewSpeaker =
				view.findViewById(R.id.group_call_image_view_speaker);
		ImageView groupCallImageViewCameraFlip =
				view.findViewById(R.id.group_call_image_view_camera_flip);
		groupCallImageViewVideoOnOff = view.findViewById(R.id.group_call_image_view_video_on_off);
		groupCallImageViewAudioOnOff = view.findViewById(R.id.group_call_image_view_audio_on_off);
		ImageView groupCallImageViewExit = view.findViewById(R.id.group_call_image_view_exit);
		
		groupCallImageViewSpeaker.setOnClickListener(v -> showSelectingAudioDeviceDialog());
		
		groupCallImageViewCameraFlip.setOnClickListener(v -> viewModel.switchCamera());
		
		groupCallImageViewVideoOnOff.setOnClickListener(v -> {
			boolean isVideoEnabled = room.getLocalParticipant().isVideoEnabled();
			if (isVideoEnabled) {
				viewModel.stopLocalVideo();
			} else {
				viewModel.startLocalVideo();
			}
		});
		
		groupCallImageViewAudioOnOff.setOnClickListener(v -> {
			boolean isAudioEnabled = room.getLocalParticipant().isAudioEnabled();
			if (isAudioEnabled) {
				viewModel.muteMicrophone();
			} else {
				viewModel.unmuteMicrophone();
			}
		});
		
		groupCallImageViewExit.setOnClickListener(v -> {
			viewModel.endCall();
			requireActivity().finish();
		});
		
	}
	
	private void initRecyclerView() {
		if (viewModel.participants.getValue() != null) {
			ParticipantListAdapter adapter =
					new ParticipantListAdapter(viewModel.participants.getValue());
			FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
			layoutManager.setJustifyContent(JustifyContent.CENTER);
			recyclerView.setLayoutManager(layoutManager);
			recyclerView.setAdapter(adapter);
			viewModel.participants.observe(getViewLifecycleOwner(), adapter :: setParticipants);
		}
	}
	
	private void observeViewModel() {
		viewModel.localParticipant.observe(getViewLifecycleOwner(), localParticipant -> {
			if (localParticipant != null) {
				setAudioEnabledImage(localParticipant.isAudioEnabled());
				setVideoEnabledImage(localParticipant.isVideoEnabled());
			}
		});
		viewModel.callState.observe(getViewLifecycleOwner(), (Observer<Boolean>) aBoolean -> {
			if (!aBoolean) {
				viewModel.endCall();
				requireActivity().finish();
			}
		});
	}
	
	private void setAudioEnabledImage(boolean isEnabled) {
		Drawable drawable;
		if (isEnabled) {
			drawable = ContextCompat.getDrawable(requireContext(), R.drawable.icon_audio_on);
		} else {
			drawable = ContextCompat.getDrawable(requireContext(), R.drawable.icon_audio_off);
		}
		groupCallImageViewAudioOnOff.setImageDrawable(drawable);
	}
	
	private void setVideoEnabledImage(boolean isEnabled) {
		Drawable drawable;
		if (isEnabled) {
			drawable = ContextCompat.getDrawable(requireContext(), R.drawable.icon_video_on);
		} else {
			drawable = ContextCompat.getDrawable(requireContext(), R.drawable.icon_video_off);
		}
		groupCallImageViewVideoOnOff.setImageDrawable(drawable);
	}
	
	private void showSelectingAudioDeviceDialog() {
		List<AudioDevice> audioDevices = new ArrayList<>(viewModel.getAvailableAudioDevices());
		AudioDevice currentAudioDevice = viewModel.currentAudioDevice.getValue();
		int currentAudioDeviceIndex = 0;
		for (int i = 0; i < audioDevices.size(); i++) {
			if (audioDevices.get(i) == currentAudioDevice) {
				currentAudioDeviceIndex = i;
				break;
			}
		}
		
		String[] audioDevicesNames = new String[audioDevices.size()];
		for (int i = 0; i < audioDevices.size(); i++) {
			audioDevicesNames[i] = GroupUtils.audioDeviceToReadableString(audioDevices.get(i));
		}
		
		AtomicInteger selectedIndex = new AtomicInteger();
		int finalCurrentAudioDeviceIndex = currentAudioDeviceIndex;
		new AlertDialog.Builder(requireContext()).setTitle(getString(
				R.string.group_call_select_audio_device_dialog_title)).setSingleChoiceItems(
				audioDevicesNames, currentAudioDeviceIndex,
				(dialog, which) -> selectedIndex.set(which)).setPositiveButton(R.string.mdtp_ok,
				(dialog, which) -> {
					if (finalCurrentAudioDeviceIndex != selectedIndex.get()) {
						viewModel.selectAudioDevice(audioDevices.get(selectedIndex.get()));
					}
				}).setNegativeButton(R.string.cancel_text, (dialog, which) -> {
			
		}).create().show();
		
	}
	
	//endregion
	
	public static GroupCallFragment newInstance(String roomId, String channelUrl, String userIds) {
		GroupCallFragment groupCallFragment = new GroupCallFragment();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_ROOM_ID, roomId);
		bundle.putString(EXTRA_CHANNEL_URL, channelUrl);
		bundle.putString(EXTRA_USERS_IDS, userIds);
		groupCallFragment.setArguments(bundle);
		return groupCallFragment;
	}
	
	
}