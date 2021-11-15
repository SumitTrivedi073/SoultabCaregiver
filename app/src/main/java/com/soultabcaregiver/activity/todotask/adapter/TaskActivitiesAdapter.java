package com.soultabcaregiver.activity.todotask.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.todotask.model.TaskActivitiesModel;
import com.soultabcaregiver.utils.TimeAgoUtils;

import java.util.ArrayList;


public class TaskActivitiesAdapter extends RecyclerView.Adapter<TaskActivitiesAdapter.ViewHolder> {
	
	private Context context;
	
	private ArrayList<TaskActivitiesModel.Response> taskActivities;
	
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
			
			tvUserName.setText(activity.getCreatedByName());
			tvComment.setText(activity.getTitle());
			tvDays.setText(TimeAgoUtils.covertTimeToText(activity.getCreatedAt()));
			
			llOptions.setVisibility(View.GONE);
		}
	}
}
