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
import com.sendbird.android.GroupChannel;
import com.sendbird.android.UserMessageParams;
import com.sendbird.calls.AudioDevice;
import com.sendbird.calls.LocalParticipant;
import com.sendbird.calls.Participant;
import com.sendbird.calls.RemoteParticipant;
import com.sendbird.calls.Room;
import com.sendbird.calls.RoomListener;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.SendBirdException;
import com.soultabcaregiver.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class GroupCallFragment extends Fragment {
	
	public static final String EXTRA_ROOM_ID = "extra_room_id";
	
	public static final String EXTRA_CHANNEL_URL = "extra_channel_url";
	
	public static final String EXTRA_USERS_IDS = "extra_users_ids";
	
	private Room room;
	
	private String roomId;
	
	private String userIds;
	
	private String channelUrl;
	
	private ParticipantListAdapter adapter;
	
	private RecyclerView recyclerView;
	
	private ImageView groupCallImageViewVideoOnOff;
	
	private ImageView groupCallImageViewAudioOnOff;
	
	private ImageView groupCallLinearLayoutParticipants;
	
	private Timer callEndTimer;
	
	private Timer participantTimer;
	
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
		
		initViews(view);
		
		return view;
	}
	
	@Override
	public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		callEndTimer = new Timer();
		callEndTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (adapter.getParticipants().size() == 1) {
					if (getActivity() != null) {
						requireActivity().runOnUiThread(() -> endCall());
					}
				}
			}
		}, 60000);
		
		participantTimer = new Timer();
		participantTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (room != null) {
					if (getActivity() != null) {
						requireActivity().runOnUiThread(
								() -> adapter.updateParticipants(room.getRemoteParticipants()));
					}
				}
			}
		}, 0, 2000);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		startLocalVideo();
		unmuteMicrophone();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		stopLocalVideo();
		muteMicrophone();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		callEndTimer.cancel();
		participantTimer.cancel();
	}
	
	public static GroupCallFragment newInstance(String roomId, String channelUrl, String userIds) {
		GroupCallFragment groupCallFragment = new GroupCallFragment();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_ROOM_ID, roomId);
		bundle.putString(EXTRA_CHANNEL_URL, channelUrl);
		bundle.putString(EXTRA_USERS_IDS, userIds);
		groupCallFragment.setArguments(bundle);
		return groupCallFragment;
	}
	
	public void endCall() {
		if (room != null) {
			try {
				room.exit();
				if (getActivity() != null) {
					getActivity().finish();
				}
			} catch (SendBirdException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void initViews(View view) {
		
		SendBirdCall.fetchRoomById(roomId, (newRoom, e) -> {
			room = newRoom;
			if (room != null) {
				room.addListener("TAG", new RoomListenerImpl());
				initRecyclerView();
				//
				setAudioEnabledImage(room.getLocalParticipant().isAudioEnabled());
				setVideoEnabledImage(room.getLocalParticipant().isVideoEnabled());
			}
		});
		
		recyclerView = view.findViewById(R.id.group_call_recycler_view_participants);
		ImageView groupCallImageViewSpeaker =
				view.findViewById(R.id.group_call_image_view_speaker);
		ImageView groupCallImageViewCameraFlip =
				view.findViewById(R.id.group_call_image_view_camera_flip);
		groupCallImageViewVideoOnOff = view.findViewById(R.id.group_call_image_view_video_on_off);
		groupCallImageViewAudioOnOff = view.findViewById(R.id.group_call_image_view_audio_on_off);
		ImageView groupCallImageViewExit = view.findViewById(R.id.group_call_image_view_exit);
		
		groupCallImageViewSpeaker.setOnClickListener(v -> showSelectingAudioDeviceDialog());
		
		groupCallImageViewCameraFlip.setOnClickListener(v -> switchCamera());
		
		groupCallImageViewVideoOnOff.setOnClickListener(v -> {
			boolean isVideoEnabled = room.getLocalParticipant().isVideoEnabled();
			if (isVideoEnabled) {
				stopLocalVideo();
			} else {
				startLocalVideo();
			}
		});
		
		groupCallImageViewAudioOnOff.setOnClickListener(v -> {
			boolean isAudioEnabled = room.getLocalParticipant().isAudioEnabled();
			if (isAudioEnabled) {
				muteMicrophone();
			} else {
				unmuteMicrophone();
			}
		});
		
		groupCallImageViewExit.setOnClickListener(v -> {
			if (adapter.getParticipants().size() == 1) {
				UserMessageParams params = new UserMessageParams();
				params.setCustomType(GroupCallType.END_GROUP_VIDEO.name());
				GroupChannel.getChannel(channelUrl, (groupChannel, e) -> {
					GroupCallMessage callMessage =
							new GroupCallMessage(userIds, channelUrl, room.getRoomId());
					Log.e("callMessage", callMessage.toString());
					params.setMessage(callMessage.toString());
					groupChannel.sendUserMessage(params, (userMessage, e1) -> {
						endCall();
					});
				});
			} else {
				endCall();
			}
		});
		
	}
	
	private List<Participant> getSortedParticipant() {
		if (room == null) {
			return new ArrayList<>();
		}
		
		List<Participant> sortedParticipants = room.getParticipants();
		
		Collections.sort(sortedParticipants, (p1, p2) -> {
			if (p1 instanceof LocalParticipant) {
				return -1;
			} else {
				return (int) (p1.getEnteredAt() - p2.getEnteredAt());
			}
		});
		
		return sortedParticipants;
	}
	
	private void initRecyclerView() {
		if (room.getParticipants().size() > 0) {
			adapter = new ParticipantListAdapter(getSortedParticipant());
			FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
			layoutManager.setJustifyContent(JustifyContent.CENTER);
			recyclerView.setLayoutManager(layoutManager);
			adapter.setHasStableIds(true);
			recyclerView.setAdapter(adapter);
		}
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
		List<AudioDevice> audioDevices = new ArrayList<AudioDevice>(getAvailableAudioDevices());
		AudioDevice currentAudioDevice = room.getCurrentAudioDevice();
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
						selectAudioDevice(audioDevices.get(selectedIndex.get()));
					}
				}).setNegativeButton(R.string.cancel_text, (dialog, which) -> {
			
		}).create().show();
		
	}
	
	public void switchCamera() {
		if (room != null && room.getLocalParticipant() != null) {
			room.getLocalParticipant().switchCamera(e -> {
			
			});
		}
	}
	
	public void muteMicrophone() {
		if (room != null && room.getLocalParticipant() != null) {
			room.getLocalParticipant().muteMicrophone();
			setAudioEnabledImage(false);
		}
	}
	
	public void unmuteMicrophone() {
		if (room != null && room.getLocalParticipant() != null) {
			room.getLocalParticipant().unmuteMicrophone();
			setAudioEnabledImage(true);
		}
	}
	
	public void stopLocalVideo() {
		if (room != null && room.getLocalParticipant() != null) {
			room.getLocalParticipant().stopVideo();
			setVideoEnabledImage(false);
		}
	}
	
	public void startLocalVideo() {
		if (room != null && room.getLocalParticipant() != null) {
			room.getLocalParticipant().startVideo();
			setVideoEnabledImage(true);
		}
	}
	
	public Set<AudioDevice> getAvailableAudioDevices() {
		if (room != null) {
			return room.getAvailableAudioDevices();
		} else {
			return new HashSet<>();
		}
	}
	
	public void selectAudioDevice(AudioDevice audioDevice) {
		if (room != null) {
			room.selectAudioDevice(audioDevice, e -> {
			
			});
		}
	}
	
	class RoomListenerImpl implements RoomListener {
		
		@Override
		public void onAudioDeviceChanged(AudioDevice audioDevice, Set<? extends AudioDevice> set) {
		
		}
		
		@Override
		public void onError(SendBirdException e, Participant participant) {
		
		}
		
		@Override
		public void onRemoteAudioSettingsChanged(@NonNull RemoteParticipant remoteParticipant) {
			if (room != null) {
				adapter.updateParticipantAudio(remoteParticipant);
			}
		}
		
		@Override
		public void onRemoteParticipantEntered(@NonNull RemoteParticipant remoteParticipant) {
			if (room != null) {
				adapter.addParticipant(remoteParticipant);
			}
		}
		
		@Override
		public void onRemoteParticipantExited(@NonNull RemoteParticipant remoteParticipant) {
			if (room != null) {
				adapter.removeParticipant(remoteParticipant);
				if (adapter.getParticipants().size() == 1) {
					endCall();
				}
			}
		}
		
		@Override
		public void onRemoteParticipantStreamStarted(@NonNull RemoteParticipant remoteParticipant) {
			if (room != null) {
				adapter.updateParticipant(remoteParticipant);
			}
		}
		
		@Override
		public void onRemoteVideoSettingsChanged(@NonNull RemoteParticipant remoteParticipant) {
			if (room != null) {
				adapter.updateParticipant(remoteParticipant);
			}
		}
	}
	
}