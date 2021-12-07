package com.soultabcaregiver.activity.todotask.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.todotask.model.TaskCommentListModel;
import com.soultabcaregiver.utils.TimeAgoUtils;
import com.soultabcaregiver.utils.Utility;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class TaskCommentsAdapter extends RecyclerView.Adapter<TaskCommentsAdapter.ViewHolder> {
	
	private Context context;
	
	private ArrayList<TaskCommentListModel.Response> taskComments;
	
	private OnCommentItemClickListeners listeners;
	
	public TaskCommentsAdapter(ArrayList<TaskCommentListModel.Response> taskComments,
	                           OnCommentItemClickListeners listeners) {
		this.taskComments = taskComments;
		this.listeners = listeners;
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(
				LayoutInflater.from(parent.getContext()).inflate(R.layout.row_todo_task_comments,
						parent, false));
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.bind(position);
	}
	
	@Override
	public int getItemCount() {
		return taskComments.size();
	}
	
	public void updateComments(ArrayList<TaskCommentListModel.Response> comments) {
		this.taskComments = new ArrayList<>();
		this.taskComments.addAll(comments);
		notifyDataSetChanged();
	}
	
	public void addComment(TaskCommentListModel.Response comment) {
		taskComments.add(comment);
		notifyItemInserted(taskComments.size());
	}
	
	public void remove(int position) {
		if (taskComments.size() > 0) {
			taskComments.remove(position);
			notifyItemRemoved(position);
			notifyItemRangeChanged(position, taskComments.size());
		}
	}
	
	public void updateComment(int editCommentPosition, TaskCommentListModel.Response comment) {
		if (taskComments.size() > 0) {
			taskComments.set(editCommentPosition, comment);
			notifyItemChanged(editCommentPosition);
		}
	}
	
	public interface OnCommentItemClickListeners {
		
		void onEditClick(int position, TaskCommentListModel.Response comment);
		void onDeleteClick(int position, TaskCommentListModel.Response comment);
		void onSaveCommentClick(int position, TaskCommentListModel.Response comment);
		
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		
		TextView tvUserName, tvComment, tvEditComment, tvDeleteComment, tvDays, tvSaveComment;
		
		ImageView ivUserImage;
		
		EditText etComment;
		
		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			etComment = itemView.findViewById(R.id.etComment);
			tvSaveComment = itemView.findViewById(R.id.tvSaveComment);
			tvUserName = itemView.findViewById(R.id.tvUserName);
			tvComment = itemView.findViewById(R.id.tvComment);
			tvEditComment = itemView.findViewById(R.id.tvEditComment);
			tvDeleteComment = itemView.findViewById(R.id.tvDeleteComment);
			tvDays = itemView.findViewById(R.id.tvDays);
			ivUserImage = itemView.findViewById(R.id.ivUserImage);
		}
		
		public void bind(int position) {
			TaskCommentListModel.Response comment = taskComments.get(position);
			Glide.with(itemView.getContext()).load(
					APIS.CaregiverImageURL + comment.getProfile_image()).placeholder(
					R.drawable.user_img).into(ivUserImage);
			tvUserName.setText(comment.getCreated_by_name());
			tvComment.setText(comment.getComment());
			tvDays.setText(TimeAgoUtils.covertTimeToText(comment.getCreatedAt()));
			tvEditComment.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (comment.getUserId().equals(
							Utility.getSharedPreferences(itemView.getContext(),
									APIS.caregiver_id))) {
						listeners.onEditClick(position, comment);
					}
				}
			});
			tvDeleteComment.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (comment.getUserId().equals(
							Utility.getSharedPreferences(itemView.getContext(),
									APIS.caregiver_id))) {
						listeners.onDeleteClick(position, comment);
					}
				}
			});
		}
		
	}
	
}
