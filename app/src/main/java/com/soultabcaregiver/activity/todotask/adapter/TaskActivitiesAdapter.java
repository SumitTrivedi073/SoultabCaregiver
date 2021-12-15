package com.soultabcaregiver.activity.todotask.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.todotask.model.TaskActivitiesModel;
import com.soultabcaregiver.utils.TimeAgoUtils;

import java.util.ArrayList;


public class TaskActivitiesAdapter extends RecyclerView.Adapter<TaskActivitiesAdapter.ViewHolder> {
	
	private Context context;
	
	private ArrayList<TaskActivitiesModel.Response> taskActivities;
	
	private TaskActivitiesModel.Response previousActivityModel = null;
	
	public TaskActivitiesAdapter(ArrayList<TaskActivitiesModel.Response> taskActivities) {
		this.taskActivities = taskActivities;
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(
				LayoutInflater.from(parent.getContext()).inflate(R.layout.row_todo_task_comments,
						parent, false));
	}
	
	public void updateActivities(ArrayList<TaskActivitiesModel.Response> list) {
		this.taskActivities = new ArrayList<>();
		this.taskActivities.addAll(list);
		notifyDataSetChanged();
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.bind(position);
	}
	
	@Override
	public int getItemCount() {
		return taskActivities.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		
		LinearLayout llOptions;
		
		ImageView ivUserImage;
		
		TextView tvUserName, tvComment, tvDays;
		
		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			llOptions = itemView.findViewById(R.id.llOptions);
			tvUserName = itemView.findViewById(R.id.tvUserName);
			tvComment = itemView.findViewById(R.id.tvComment);
			tvDays = itemView.findViewById(R.id.tvDays);
			ivUserImage = itemView.findViewById(R.id.ivUserImage);
		}
		
		public void bind(int position) {
			TaskActivitiesModel.Response activity = taskActivities.get(position);
			Glide.with(itemView.getContext()).load(
					APIS.CaregiverImageURL + activity.getProfile_image()).placeholder(
					R.drawable.user_img).into(ivUserImage);
			tvUserName.setText(getLastUpdateName(activity));
			Log.e("TAG", "bind: activity" + position + "=====>" + getCommentForActivity(
					previousActivityModel, activity));
			tvComment.setText(getCommentForActivity(previousActivityModel, activity));
			tvDays.setText(TimeAgoUtils.covertTimeToText(
					activity.getUpdatedAt() != null ? activity.getUpdatedAt() :
							activity.getCreatedAt()));
			previousActivityModel = activity;
			llOptions.setVisibility(View.GONE);
		}
		
		private CharSequence getCommentForActivity(
				TaskActivitiesModel.Response previousActivityModel,
				TaskActivitiesModel.Response activity) {
			if (previousActivityModel != null) {
				CharSequence sequence = "";
				if (!previousActivityModel.getTitle().equals(activity.getTitle())) {
					sequence = TextUtils.concat(sequence, sequence.length() > 0 ? "\n" : "",
							getSpannableString("Has made an update in title\n",
									activity.getTitle()));
					//					if (!previousActivityModel.getDescription().equals
					//					(activity.getDescription())) {
					//						sequence = TextUtils.concat(sequence,
					//								getSpannableString("\nHas made an update in "
					//								+ "description\n",
					//										activity.getDescription()));
					//					}
					//					if (!previousActivityModel.getTaskStatus().equals(activity
					//					.getTaskStatus())) {
					//						sequence = TextUtils.concat(sequence,
					//								getSpannableString("\nMove this " + "card to ",
					//										activity.getTaskStatus()));
					//					}
					//					return sequence;
				}
				if (!previousActivityModel.getDescription().equals(activity.getDescription())) {
					sequence = TextUtils.concat(sequence, sequence.length() > 0 ? "\n" : "",
							getSpannableString("Has made an update in description\n",
									activity.getDescription()));
				}
				if (!previousActivityModel.getTaskStatus().equals(activity.getTaskStatus())) {
					sequence = TextUtils.concat(sequence, sequence.length() > 0 ? "\n" : "",
							getSpannableString("Move this card to ", activity.getTaskStatus()));
				}
				if (previousActivityModel.getTitle().equals(
						activity.getTitle()) && previousActivityModel.getDescription().equals(
						activity.getDescription()) && previousActivityModel.getTaskStatus().equals(
						activity.getTaskStatus())) {
					hide();
					return "";
				}
				return sequence;
			} else {
				return "Create new task";
			}
		}
		
		private void hide() {
			itemView.getLayoutParams().height = 0;
		}
		
		private String getLastUpdateName(TaskActivitiesModel.Response activity) {
			return activity.getCreatedByName();
		}
		
		public CharSequence getSpannableString(String startString, String endString) {
			SpannableString ss1 = new SpannableString(startString);
			SpannableString ss2 = new SpannableString(endString);
			ss2.setSpan(new ForegroundColorSpan(
							ContextCompat.getColor(itemView.getContext(), R.color.themecolor)), 0,
					endString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			return TextUtils.concat(ss1, ss2);
		}
		
	}
	
}
