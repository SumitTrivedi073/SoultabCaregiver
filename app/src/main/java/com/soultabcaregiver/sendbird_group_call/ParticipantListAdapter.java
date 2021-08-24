package com.soultabcaregiver.sendbird_group_call;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.sendbird.calls.LocalParticipant;
import com.sendbird.calls.Participant;
import com.sendbird.calls.ParticipantState;
import com.sendbird.calls.RemoteParticipant;
import com.sendbird.calls.SendBirdVideoView;
import com.soultabcaregiver.R;
import com.soultabcaregiver.utils.Utility;

import org.jetbrains.annotations.NotNull;
import org.webrtc.RendererCommon;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ParticipantListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	
	private static final String TAG = "ParticipantListAdapter";
	
	private List<Participant> participants;
	
	private int parentWidth;
	
	private int parentHeight;
	
	public ParticipantListAdapter(@NotNull List<Participant> participants) {
		super();
		this.participants = participants;
	}
	
	@NotNull
	@Override
	public ParticipantListAdapter.ParticipantViewHolder onCreateViewHolder(ViewGroup parent,
	                                                                       int viewType) {
		this.parentWidth = parent.getWidth();
		this.parentHeight = parent.getHeight();
		View view = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.item_group_video_call_participant, parent, false);
		return new ParticipantListAdapter.ParticipantViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
		this.setViewHolderLayout((ParticipantViewHolder) holder, position);
		((ParticipantViewHolder) holder).bind(this.participants.get(position));
	}
	
	@Override
	public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position,
	                             @NonNull List<Object> payload) {
		this.setViewHolderLayout((ParticipantViewHolder) holder, position);
		if (!payload.isEmpty()) {
			((ParticipantViewHolder) holder).refreshAudio(
					((Participant) payload.get(0)).isAudioEnabled());
		} else {
			((ParticipantViewHolder) holder).bind(this.participants.get(position));
		}
	}
	
	@Override
	public long getItemId(int position) {
		return this.participants.get(position).getParticipantId().hashCode();
	}
	
	public int getItemCount() {
		return this.participants.size();
	}
	
	public void updateParticipants(List<RemoteParticipant> newParticipants) {
		for (Participant newParticipant : newParticipants) {
			boolean contains = false;
			for (Participant participant : this.participants) {
				if (participant.getParticipantId().equals(newParticipant.getParticipantId())) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				this.participants.add(newParticipant);
				notifyItemInserted(this.participants.size() - 1);
			}
		}
	}
	
	private void setViewHolderLayout(ParticipantListAdapter.ParticipantViewHolder holder,
	                                 int position) {
		int spacing = Utility.dpToPixel(holder.itemView.getContext(), 4);
		
		// calculate layout position type
		ViewHolderPositionType type;
		if (getItemCount() != 2) {
			if (position % 2 == 0) {
				if (getItemCount() % 2 == 1 && getItemCount() - 1 == position) {
					type = ViewHolderPositionType.CENTER;
				} else {
					type = ViewHolderPositionType.LEFT;
				}
			} else {
				type = ViewHolderPositionType.RIGHT;
			}
		} else {
			if (position == 0) {
				type = ViewHolderPositionType.TOP;
			} else {
				type = ViewHolderPositionType.BOTTOM;
			}
		}
		
		// set margins
		int leftMargin = 0;
		if (type == ViewHolderPositionType.RIGHT) {
			leftMargin = spacing / 2;
		}
		
		int rightMargin = 0;
		if (type == ViewHolderPositionType.LEFT) {
			rightMargin = spacing / 2;
		}
		
		int topMargin;
		if (position / 2 >= 1) {
			topMargin = spacing;
		} else {
			topMargin = 0;
		}
		
		// calculate width and height
		int width;
		if (getItemCount() <= 2) {
			width = parentWidth;
		} else {
			width = parentWidth / 2;
		}
		
		width -= leftMargin;
		width -= rightMargin;
		
		int numberOfRows = (getItemCount() + 1) / 2;
		int height;
		if (getItemCount() <= 2) {
			height = (parentHeight / 2) - topMargin;
		} else {
			height = (parentHeight / numberOfRows) - topMargin;
		}
		
		// calculate video view's ratio
		int parentRatio = width > height ? width / height : height / width;
		
		double expectedRatio = 3.0 / 4;
		int currentRow = (position / 2) + 1;
		
		int gravity;
		
		if (type == ViewHolderPositionType.RIGHT) {
			gravity = Gravity.START;
		} else if (type == ViewHolderPositionType.LEFT) {
			gravity = Gravity.END;
		} else if (type == ViewHolderPositionType.TOP) {
			gravity = Gravity.CENTER;
		} else if (type == ViewHolderPositionType.BOTTOM) {
			gravity = Gravity.CENTER;
		} else {
			gravity = Gravity.CENTER_HORIZONTAL;
		}
		
		if ((currentRow * 2) < (numberOfRows + 1)) {
			gravity = gravity | Gravity.BOTTOM;
		} else if ((currentRow * 2) > (numberOfRows + 1)) {
			gravity = gravity | Gravity.TOP;
		} else {
			gravity = gravity | Gravity.CENTER_HORIZONTAL;
		}
		
		FlexboxLayoutManager.LayoutParams flexParams =
				new FlexboxLayoutManager.LayoutParams(width, height);
		flexParams.setMargins(leftMargin, topMargin, rightMargin, 0);
		holder.itemView.setLayoutParams(flexParams);
		
		LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
				parentRatio > expectedRatio ? (int) (height * expectedRatio) : width,
				parentRatio > expectedRatio ? height : (int) (width / expectedRatio));
		
		if (getItemCount() <= 2) {
			linearParams.width = width;
		}
		
		Log.e(TAG, "width " + width);
		Log.e(TAG, "height " + height);
		Log.e(TAG, "parentWidth " + parentWidth);
		Log.e(TAG, "parentHeight " + parentHeight);
		Log.e(TAG, "parentRatio " + parentRatio);
		Log.e(TAG, "expectedRatio " + expectedRatio);
		
		holder.participantRelativeLayoutVideoView.setLayoutParams(linearParams);
		
		holder.participantLinearLayout.setGravity(gravity);
		
	}
	
	@NonNull
	public final List<Participant> getParticipants() {
		return this.participants;
	}
	
	public final void removeParticipant(Participant participant) {
		int index = -1;
		for (int i = 0; i < this.participants.size(); i++) {
			if (participants.get(i).getParticipantId().equals(participant.getParticipantId())) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			this.participants.remove(index);
			notifyDataSetChanged();
		}
	}
	
	public final void updateParticipantAudio(Participant participant) {
		for (int i = 0; i < this.participants.size(); i++) {
			if (this.participants.get(i).getParticipantId().equals(
					participant.getParticipantId())) {
				notifyItemChanged(i, participant);
				break;
			}
		}
	}
	
	public final void updateParticipant(Participant participant) {
		for (int i = 0; i < this.participants.size(); i++) {
			if (this.participants.get(i).getParticipantId().equals(
					participant.getParticipantId())) {
				this.participants.remove(i);
				this.participants.add(i, participant);
				notifyItemChanged(i);
				break;
			}
		}
	}
	
	public final void addParticipant(Participant participant) {
		this.participants.add(participant);
		notifyDataSetChanged();
	}
	
	public enum ViewHolderPositionType {
		LEFT, RIGHT, CENTER, TOP, BOTTOM;
	}
	
	public static final class ParticipantViewHolder extends RecyclerView.ViewHolder {
		
		RelativeLayout participantRelativeLayoutVideoView;
		
		SendBirdVideoView participantVideoView;
		
		LinearLayout participantLinearLayout;
		
		ImageView participantImageViewProfile, participantImageViewAudioOnOff;
		
		TextView participantUserId;
		
		public ParticipantViewHolder(View itemView) {
			super(itemView);
			participantRelativeLayoutVideoView =
					itemView.findViewById(R.id.participant_relative_layout_video_view);
			participantLinearLayout = itemView.findViewById(R.id.participant_linear_layout);
			participantVideoView = itemView.findViewById(R.id.participant_sendbird_video_view);
			participantImageViewProfile =
					itemView.findViewById(R.id.participant_image_view_profile);
			participantUserId = itemView.findViewById(R.id.participant_text_view_user_id);
			participantImageViewAudioOnOff =
					itemView.findViewById(R.id.participant_image_view_audio_muted);
		}
		
		public void refreshAudio(boolean isAudioEnabled) {
			if (isAudioEnabled) {
				participantImageViewAudioOnOff.setVisibility(View.GONE);
			} else {
				participantImageViewAudioOnOff.setVisibility(View.VISIBLE);
			}
		}
		
		@SuppressLint ("CheckResult")
		public final void bind(@NotNull Participant participant) {
			
			participantUserId.setText(participant.getUser().getNickname());
			
			if (participant.isAudioEnabled()) {
				participantImageViewAudioOnOff.setVisibility(View.GONE);
			} else {
				participantImageViewAudioOnOff.setVisibility(View.VISIBLE);
			}
			
			if (participant.isVideoEnabled()) {
				participantVideoView.setVisibility(View.VISIBLE);
				participantImageViewProfile.setVisibility(View.GONE);
			} else {
				participantVideoView.setVisibility(View.GONE);
				participantImageViewProfile.setVisibility(View.VISIBLE);
				int radius = Utility.dpToPixel(itemView.getContext(), 40);
				if (participant.getUser().getProfileUrl() != null) {
					Glide.with(itemView.getContext()).load(
							participant.getUser().getProfileUrl()).apply(
							new RequestOptions().transform(new RoundedCorners(radius)).error(
									R.drawable.icon_avatar));
				}
			}
			
			boolean isStreaming =
					participant instanceof LocalParticipant || participant.getState() == ParticipantState.CONNECTED;
			
			if (isStreaming && participant.getVideoView() != participantVideoView) {
				participantVideoView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
				participant.setVideoView(participantVideoView);
			}
			
		}
	}
}