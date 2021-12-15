package com.soultabcaregiver.activity.todotask.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.todotask.model.TodoNotificationModel;
import com.soultabcaregiver.utils.TimeAgoUtils;

import java.util.ArrayList;


public class TodoTaskNotificationsAdapter extends RecyclerView.Adapter<TodoTaskNotificationsAdapter.ViewHolder> {
	
	ArrayList<TodoNotificationModel.TaskNotification> taskNotifications = new ArrayList<>();
	
	public TodoTaskNotificationsAdapter(
			ArrayList<TodoNotificationModel.TaskNotification> taskNotifications) {
		this.taskNotifications = taskNotifications;
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(
				LayoutInflater.from(parent.getContext()).inflate(R.layout.row_todo_notification,
						parent, false));
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.bind(position);
	}
	
	@Override
	public int getItemCount() {
		return taskNotifications.size();
	}
	
	public void updateNotificationData(
			ArrayList<TodoNotificationModel.TaskNotification> notificationData) {
		taskNotifications = new ArrayList<>();
		taskNotifications.addAll(notificationData);
		notifyDataSetChanged();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		
		View viewUnread;
		
		TextView tvUserName, tvComment, tvDays;
		
		ImageView ivUserImage;
		
		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			viewUnread = itemView.findViewById(R.id.viewUnread);
			tvUserName = itemView.findViewById(R.id.tvUserName);
			tvComment = itemView.findViewById(R.id.tvComment);
			tvDays = itemView.findViewById(R.id.tvDays);
			ivUserImage = itemView.findViewById(R.id.ivUserImage);
		}
		
		public void bind(int position) {
			TodoNotificationModel.TaskNotification taskNotification =
					taskNotifications.get(position);
			tvUserName.setText(taskNotification.getUser_name());
			Glide.with(itemView.getContext()).load(taskNotification.getProfile_image()).placeholder(
					R.drawable.user_img).skipMemoryCache(false).diskCacheStrategy(
					DiskCacheStrategy.RESOURCE).into(ivUserImage);
			tvComment.setText(taskNotification.getMsg());
			tvDays.setText(TimeAgoUtils.covertTimeToText(taskNotification.getCreatedAt()));
			viewUnread.setVisibility(View.GONE);
			//			if (position == 0) {
			//				viewUnread.setVisibility(View.VISIBLE);
			//			} else {
			//			viewUnread.setVisibility(View.GONE);
			//			}
		}
		
	}
	
}
