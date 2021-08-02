package com.soultabcaregiver.sendbird_group_call;

import com.sendbird.calls.AudioDevice;
import com.sendbird.calls.LocalParticipant;
import com.sendbird.calls.Participant;
import com.sendbird.calls.RemoteParticipant;
import com.sendbird.calls.Room;
import com.sendbird.calls.RoomListener;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.SendBirdException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

class GroupCallViewModel extends ViewModel {
	
	private static final String TAG = GroupCallViewModel.class.getSimpleName();
	
	private final String mRoomId;
	
	private final Room room;
	
	private final MutableLiveData<List<Participant>> _participants = new MutableLiveData<>();
	final LiveData<List<Participant>> participants = _participants;
	
	private final MutableLiveData<Participant> _localParticipant = new MutableLiveData<>();
	final LiveData<Participant> localParticipant = _localParticipant;
	
	private final MutableLiveData<AudioDevice> _currentAudioDevice = new MutableLiveData<>();
	final LiveData<AudioDevice> currentAudioDevice = _currentAudioDevice;
	
	private final MutableLiveData<Boolean> _callState = new MutableLiveData<>();
	final LiveData<Boolean> callState = _callState;
	
	public GroupCallViewModel(String roomId) {
		this.mRoomId = roomId;
		room = SendBirdCall.getCachedRoomById(mRoomId);
		if (room != null) {
			_participants.setValue(getSortedParticipant());
			room.addListener(TAG, new RoomListenerImpl());
			_currentAudioDevice.setValue(room.getCurrentAudioDevice());
			_callState.setValue(true);
		}
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
	
	public void endCall() {
		if (room != null) {
			try {
				room.exit();
			} catch (SendBirdException e) {
				e.printStackTrace();
			}
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
	
	public void switchCamera() {
		if (room != null && room.getLocalParticipant() != null) {
			room.getLocalParticipant().switchCamera(e -> {
			
			});
		}
	}
	
	public void muteMicrophone() {
		if (room != null && room.getLocalParticipant() != null) {
			room.getLocalParticipant().muteMicrophone();
			_localParticipant.setValue(room.getLocalParticipant());
			_participants.postValue(getSortedParticipant());
		}
	}
	
	public void unmuteMicrophone() {
		if (room != null && room.getLocalParticipant() != null) {
			room.getLocalParticipant().unmuteMicrophone();
			_localParticipant.setValue(room.getLocalParticipant());
			_participants.postValue(getSortedParticipant());
		}
	}
	
	public void stopLocalVideo() {
		if (room != null && room.getLocalParticipant() != null) {
			room.getLocalParticipant().stopVideo();
			_localParticipant.setValue(room.getLocalParticipant());
			_participants.postValue(getSortedParticipant());
		}
	}
	
	public void startLocalVideo() {
		if (room != null && room.getLocalParticipant() != null) {
			room.getLocalParticipant().startVideo();
			_localParticipant.setValue(room.getLocalParticipant());
			_participants.postValue(getSortedParticipant());
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
			_participants.setValue(_participants.getValue());
		}
		
		@Override
		public void onRemoteParticipantEntered(@NonNull RemoteParticipant remoteParticipant) {
			if (room != null) {
				_participants.setValue(getSortedParticipant());
			}
		}
		
		@Override
		public void onRemoteParticipantExited(@NonNull RemoteParticipant remoteParticipant) {
			if (room != null) {
				_participants.setValue(getSortedParticipant());
				if (getSortedParticipant().size() == 1) {
					endCall();
					_callState.setValue(false);
				}
			}
		}
		
		@Override
		public void onRemoteParticipantStreamStarted(@NonNull RemoteParticipant remoteParticipant) {
			_participants.postValue(_participants.getValue());
		}
		
		@Override
		public void onRemoteVideoSettingsChanged(@NonNull RemoteParticipant remoteParticipant) {
			_participants.postValue(_participants.getValue());
		}
	}
	
}
