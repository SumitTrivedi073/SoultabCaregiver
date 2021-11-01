package com.soultabcaregiver.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.soultabcaregiver.R;
import com.soultabcaregiver.search.models.UserSearchResultResponse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SearchUsersAdapter extends RecyclerView.Adapter<SearchUsersAdapter.SearchUsersViewHolder> {
	
	private final SearchUserClickHandler userClickHandler;
	
	private List<UserSearchResultResponse.UserSearchResultModel> usersList = new ArrayList<>();
	
	public SearchUsersAdapter(SearchUserClickHandler clickHandler) {
		this.userClickHandler = clickHandler;
	}
	
	@NonNull
	@Override
	public SearchUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_search,
				parent,
				false);
		return new SearchUsersViewHolder(v);
	}
	
	@Override
	public void onBindViewHolder(@NonNull SearchUsersViewHolder holder, int position) {
		holder.bind(usersList.get(position), position);
	}
	
	@Override
	public int getItemCount() {
		return usersList.size();
	}
	
	public void setUsersList(List<UserSearchResultResponse.UserSearchResultModel> list) {
		usersList = list;
		notifyDataSetChanged();
	}
	
	public void updateItemStatus(int position, UsersConnectedStatus status) {
		usersList.get(position).setConnected(status.toString());
		notifyItemChanged(position);
	}
	
	public void removeUserFromList(int userPosition) {
		usersList.remove(userPosition);
		notifyItemRemoved(userPosition);
		notifyItemRangeChanged(userPosition,usersList.size());
		notifyDataSetChanged();
	}
	
	
	public enum UsersConnectedStatus {
		Pending {
			@NotNull
			@Override
			public String toString() {
				return "pending";
			}
		}, Decline {
			@NotNull
			@Override
			public String toString() {
				return "decline";
			}
		}, Connected {
			@NotNull
			@Override
			public String toString() {
				return "connected";
			}
		}, Requested {
			@NotNull
			@Override
			public String toString() {
				return "requested";
			}
		}, NotConnected {
			@NotNull
			@Override
			public String toString() {
				return "notconnected";
			}
		},Empty {
			@NotNull
			@Override
			public String toString() {
				return "";
			}
		}
	}
	
	public interface SearchUserClickHandler {
		
		void acceptClickListener(UserSearchResultResponse.UserSearchResultModel userModel,
		                         int position);
		
		void rejectClickListener(UserSearchResultResponse.UserSearchResultModel userModel,
		                         int position);
		
		void removeClickListener(UserSearchResultResponse.UserSearchResultModel userModel,
		                         int position);
		
		void inviteMember(UserSearchResultResponse.UserSearchResultModel userModel, int position);
		
		void viewUserDetails(UserSearchResultResponse.UserSearchResultModel userModel,
		                     int position);
		
	}
	
	class SearchUsersViewHolder extends RecyclerView.ViewHolder {
		
		private final ImageView userPicImageView;
		
		private final TextView userName, alreadyMemberText, acceptBtn, rejectBtn, removeButton,
				viewDetailBtn, inviteMember, connectionRequestedBtn;
		
		private final LinearLayoutCompat requestedLayout, connectedLayout, notConnectedLayout,
				pendingLayout;
		
		public SearchUsersViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			userPicImageView = itemView.findViewById(R.id.userPic);
			
			userName = itemView.findViewById(R.id.userName);
			alreadyMemberText = itemView.findViewById(R.id.alreadyMember);
			
			acceptBtn = itemView.findViewById(R.id.acceptBtn);
			rejectBtn = itemView.findViewById(R.id.rejectBtn);
			removeButton = itemView.findViewById(R.id.removeButton);
			viewDetailBtn = itemView.findViewById(R.id.viewDetailBtn);
			inviteMember = itemView.findViewById(R.id.inviteMember);
			connectionRequestedBtn = itemView.findViewById(R.id.connectionRequestedBtn);
			
			requestedLayout = itemView.findViewById(R.id.requestedLayout);
			connectedLayout = itemView.findViewById(R.id.connectedLayout);
			notConnectedLayout = itemView.findViewById(R.id.notConnectedLayout);
			pendingLayout = itemView.findViewById(R.id.pendingLayout);
		}
		
		public void bind(UserSearchResultResponse.UserSearchResultModel model, int position) {
			
			userName.setText(model.getName());
			
			RequestOptions options =
					new RequestOptions().centerCrop().dontAnimate().fitCenter().placeholder(
							R.drawable.user_img).error(R.drawable.user_img);
			
			Glide.with(itemView.getContext()).load(model.getProfileImage()).apply(options).into(
					userPicImageView);
			
			requestedLayout.setVisibility(View.GONE);
			connectedLayout.setVisibility(View.GONE);
			notConnectedLayout.setVisibility(View.GONE);
			pendingLayout.setVisibility(View.GONE);
			alreadyMemberText.setVisibility(View.GONE);
			
			if (model.getConnected() == null || model.getConnected().isEmpty() || model.getConnected().toLowerCase().equals(
					UsersConnectedStatus.Decline.toString())) {
				notConnectedLayout.setVisibility(View.VISIBLE);
			} else if (model.getConnected().toLowerCase().equals(
					UsersConnectedStatus.Pending.toString())) {
				pendingLayout.setVisibility(View.VISIBLE);
			} else if (model.getConnected().toLowerCase().equals(
					UsersConnectedStatus.Requested.toString())) {
				requestedLayout.setVisibility(View.VISIBLE);
			} else if (model.getConnected().toLowerCase().equals(
					UsersConnectedStatus.Connected.toString())) {
				alreadyMemberText.setVisibility(View.VISIBLE);
				connectedLayout.setVisibility(View.VISIBLE);
			}
			
			userName.setOnClickListener(v -> userClickHandler.viewUserDetails(model, position));
			userPicImageView.setOnClickListener(
					v -> userClickHandler.viewUserDetails(model, position));
			
			acceptBtn.setOnClickListener(
					v -> userClickHandler.acceptClickListener(model, position));
			rejectBtn.setOnClickListener(
					v -> userClickHandler.rejectClickListener(model, position));
			removeButton.setOnClickListener(
					v -> userClickHandler.removeClickListener(model, position));
			inviteMember.setOnClickListener(v -> userClickHandler.inviteMember(model, position));
			viewDetailBtn.setOnClickListener(
					v -> userClickHandler.viewUserDetails(model, position));
			
		}
		
	}
	
}
