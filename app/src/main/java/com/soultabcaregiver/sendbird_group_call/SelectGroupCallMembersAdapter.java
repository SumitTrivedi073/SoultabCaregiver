package com.soultabcaregiver.sendbird_group_call;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sendbird.android.Member;
import com.soultabcaregiver.R;
import com.soultabcaregiver.sendbird_chat.utils.ImageUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

class SelectGroupCallMembersAdapter extends RecyclerView.Adapter<SelectGroupCallMembersAdapter.SelectableUserHolder> {
	
	// For the adapter to track which users have been selected
	private final OnItemCheckedChangeListener mCheckedChangeListener;
	
	public List<Member> participantList = new ArrayList<>();
	
	private List<String> mSelectedMembersId = new ArrayList<>();
	
	public SelectGroupCallMembersAdapter(OnItemCheckedChangeListener checkedChangeListener) {
		mCheckedChangeListener = checkedChangeListener;
	}
	
	@NonNull
	@NotNull
	@Override
	public SelectableUserHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent,
	                                               int viewType) {
		View view =
				LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_selectable_user,
						parent, false);
		return new SelectableUserHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull @NotNull SelectableUserHolder holder, int position) {
		holder.bind(participantList.get(position), isSelected(participantList.get(position)),
				mCheckedChangeListener);
	}
	
	@Override
	public int getItemCount() {
		return participantList.size();
	}
	
	private boolean isSelected(Member member) {
		return mSelectedMembersId.contains(member.getUserId());
	}
	
	public void setMembers(List<Member> members) {
		this.participantList.addAll(members);
		notifyDataSetChanged();
	}
	
	public interface OnItemCheckedChangeListener {
		
		void OnItemChecked(Member user, boolean checked);
	}
	
	public class SelectableUserHolder extends RecyclerView.ViewHolder {
		
		private final TextView nameText;
		
		private final ImageView profileImage;
		
		private final CheckBox checkbox;
		
		public SelectableUserHolder(View itemView) {
			super(itemView);
			
			this.setIsRecyclable(false);
			
			nameText = itemView.findViewById(R.id.text_selectable_user_list_nickname);
			profileImage = itemView.findViewById(R.id.image_selectable_user_list_profile);
			checkbox = itemView.findViewById(R.id.checkbox_selectable_user_list);
		}
		
		private void bind(final Member member, boolean isSelected,
		                  final OnItemCheckedChangeListener listener) {
			nameText.setText(member.getNickname());
			if (member.getProfileUrl().isEmpty()) {
				profileImage.setImageDrawable(
						ContextCompat.getDrawable(itemView.getContext(), R.drawable.icon_avatar));
			} else {
				ImageUtils.displayRoundImageFromUrl(itemView.getContext(), member.getProfileUrl(),
						profileImage);
			}
			
			checkbox.setChecked(isSelected);
			
			itemView.setOnClickListener(v -> {
				listener.OnItemChecked(member, !checkbox.isChecked());
				checkbox.setChecked(!checkbox.isChecked());
				if (checkbox.isChecked()) {
					mSelectedMembersId.add(member.getUserId());
				} else {
					mSelectedMembersId.remove(member.getUserId());
				}
			});
		}
	}
	
}
