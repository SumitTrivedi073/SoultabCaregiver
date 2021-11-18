package com.soultabcaregiver.activity.todotask.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.soultabcaregiver.R;
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
		
		TextView tvUserName, tvComment, tvDays;
		
		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			llOptions = itemView.findViewById(R.id.llOptions);
			tvUserName = itemView.findViewById(R.id.tvUserName);
			tvComment = itemView.findViewById(R.id.tvComment);
			tvDays = itemView.findViewById(R.id.tvDays);
		}
		
		public void bind(int position) {
			TaskActivitiesModel.Response activity = taskActivities.get(position);
			tvUserName.setText(getLastUpdateName(activity));
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
				if (!previousActivityModel.getTitle().equals(activity.getTitle())) {
					return getSpannableString("Has made an update in title\n",
							activity.getTitle());
				} else if (!previousActivityModel.getDescription().equals(
						activity.getDescription())) {
					return getSpannableString("Has made an update in description\n",
							activity.getDescription());
				} else {
					return "Move this card to " + activity.getTaskStatus();
				}
			} else {
				return "Move this card to " + activity.getTaskStatus();
			}
		}
		
		private String getLastUpdateName(TaskActivitiesModel.Response activity) {
			return activity.getUpdatedByName() != null ? activity.getUpdatedByName() :
					activity.getCreatedByName();
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
