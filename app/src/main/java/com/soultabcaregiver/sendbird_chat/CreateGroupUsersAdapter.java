package com.soultabcaregiver.sendbird_chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.alert.model.CareGiverListModel;
import com.soultabcaregiver.sendbird_chat.utils.ImageUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class CreateGroupUsersAdapter extends RecyclerView.Adapter<CreateGroupUsersAdapter.SelectableUserHolder> {
	
	private static final List<String> mSelectedUserIds = new ArrayList<>();
	
	public List<CareGiverListModel.Response> caregiverList = new ArrayList<>();
	
	public List<CareGiverListModel.Response> filteredList = new ArrayList<>();
	
	// For the adapter to track which users have been selected
	private OnItemCheckedChangeListener mCheckedChangeListener;
	
	public CreateGroupUsersAdapter(OnItemCheckedChangeListener checkedChangeListener) {
		mCheckedChangeListener = checkedChangeListener;
	}
	
	@Override
	public SelectableUserHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
		View view =
				LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_selectable_user,
						parent, false);
		return new SelectableUserHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NotNull SelectableUserHolder holder, int position) {
		holder.bind(filteredList.get(position), isSelected(filteredList.get(position)),
				mCheckedChangeListener);
	}
	
	private boolean isSelected(CareGiverListModel.Response user) {
		return mSelectedUserIds.contains(user.getId());
	}
	
	@Override
	public int getItemCount() {
		return filteredList.size();
	}
	
	public void setCaregiverList(List<CareGiverListModel.Response> list) {
		caregiverList = list;
		filteredList.addAll(caregiverList);
		notifyDataSetChanged();
	}
	
	public void updateFilter(String filterText) {
		filteredList.clear();
		if (filterText.isEmpty()) {
			filteredList.addAll(caregiverList);
		} else {
			for (CareGiverListModel.Response response : caregiverList) {
				if (response.getName().toLowerCase().contains(filterText.toLowerCase())) {
					filteredList.add(response);
				}
			}
		}
		notifyDataSetChanged();
	}
	
	public interface OnItemCheckedChangeListener {
		
		void OnItemChecked(CareGiverListModel.Response user, boolean checked);
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
		
		private void bind(final CareGiverListModel.Response user, boolean isSelected,
		                  final OnItemCheckedChangeListener listener) {
			nameText.setText(user.getName());
			if (user.getProfileImage().isEmpty()) {
				profileImage.setImageDrawable(
						ContextCompat.getDrawable(itemView.getContext(), R.drawable.icon_avatar));
			} else {
				ImageUtils.displayRoundImageFromUrl(itemView.getContext(),
						APIS.CaregiverImageURL + user.getProfileImage(), profileImage);
			}
			
			checkbox.setChecked(isSelected);
			
			itemView.setOnClickListener(v -> checkbox.setChecked(!checkbox.isChecked()));
			
			checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
				listener.OnItemChecked(user, isChecked);
				if (isChecked) {
					mSelectedUserIds.add(user.getId());
				} else {
					mSelectedUserIds.remove(user.getUserId());
				}
			});
		}
	}
	
}
