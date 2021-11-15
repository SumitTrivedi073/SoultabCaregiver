package com.soultabcaregiver.activity.todotask.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.todotask.model.TaskListModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TodoTaskListAdapter extends RecyclerView.Adapter<TodoTaskListAdapter.ViewHolder> {
	
	private OnTodoTaskClickListeners listeners;
	
	private static final String DATE_FORMAT_FOR_DISPLAY = "MMM dd, yyyy", DATE_FORMAT_FOR_API =
			"yyyy-MM-dd";
	
	private ArrayList<TaskListModel.TaskData> taskList;
	
	public TodoTaskListAdapter(Context context, ArrayList<TaskListModel.TaskData> taskList,
	                           OnTodoTaskClickListeners listeners) {
		this.listeners = listeners;
		this.taskList = taskList;
	}
	
	public void updateData(ArrayList<TaskListModel.TaskData> data) {
		taskList = new ArrayList<>();
		taskList.addAll(data);
		notifyDataSetChanged();
	}
	
	public interface OnTodoTaskClickListeners {
		
		void onTodoTaskClick(int position, TaskListModel.TaskData data);
		
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(
				LayoutInflater.from(parent.getContext()).inflate(R.layout.row_todo_tasks, parent,
						false));
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.bind(position);
	}
	
	@Override
	public int getItemCount() {
		return taskList.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		
		CardView cvMain;
		
		RecyclerView rcvCaregiversProfile;
		
		TextView tvStatus, tvTaskName, tvComments, tvDate;
		
		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			tvStatus = itemView.findViewById(R.id.tvStatus);
			cvMain = itemView.findViewById(R.id.cvMain);
			tvTaskName = itemView.findViewById(R.id.tvTaskName);
			tvComments = itemView.findViewById(R.id.tvComments);
			tvDate = itemView.findViewById(R.id.tvDate);
			rcvCaregiversProfile = itemView.findViewById(R.id.rcvCaregiversProfile);
		}
		
		public void bind(int position) {
			TaskListModel.TaskData data = taskList.get(position);
			String taskStatus = data.getTaskStatus();
			String[] caregiversImage = new String[0];
			if (data.getProfile_image().length() > 0) {
				caregiversImage = data.getProfile_image().split(",");
			}
			tvStatus.setText(taskStatus);
			tvTaskName.setText(data.getDescription());
			tvComments.setText(getFormattedCount(data.getCommentCount()));
			tvDate.setText(getFormattedDate(data.getEndDate()));
			if (taskStatus.equalsIgnoreCase("To do")) {
				tvStatus.setTextColor(
						ContextCompat.getColor(itemView.getContext(), R.color.orange_color));
				cvMain.setCardBackgroundColor(
						ContextCompat.getColor(itemView.getContext(), R.color.orange_color));
			} else if (taskStatus.equalsIgnoreCase("Done")) {
				tvStatus.setTextColor(ContextCompat.getColor(itemView.getContext(),
						R.color.green));
				cvMain.setCardBackgroundColor(
						ContextCompat.getColor(itemView.getContext(), R.color.green));
			} else if (taskStatus.equalsIgnoreCase("Inprogress")) {
				tvStatus.setTextColor(
						ContextCompat.getColor(itemView.getContext(), R.color.muzli_color));
				cvMain.setCardBackgroundColor(
						ContextCompat.getColor(itemView.getContext(), R.color.muzli_color));
			}
			rcvCaregiversProfile.setLayoutManager(
					new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL,
							false));
			rcvCaregiversProfile.setAdapter(new CaregiversProfileAdapter(caregiversImage));
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listeners.onTodoTaskClick(position, data);
				}
			});
		}
		
		private String getFormattedDate(String endDate) {
			SimpleDateFormat sourceDateFormat = new SimpleDateFormat(DATE_FORMAT_FOR_API);
			SimpleDateFormat formattedDateFormat = new SimpleDateFormat(DATE_FORMAT_FOR_DISPLAY);
			Date sourceDate = null;
			try {
				sourceDate = sourceDateFormat.parse(endDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return "Due on " + formattedDateFormat.format(sourceDate);
		}
		
		private String getFormattedCount(int commentCount) {
			String count = commentCount < 10 ? ("0" + commentCount) : String.valueOf(commentCount);
			return count + " Comments";
		}
		
	}
	
	private static class CaregiversProfileAdapter extends RecyclerView.Adapter<CaregiversProfileAdapter.ViewHolder> {
		
		String[] caregiversImage;
		
		public CaregiversProfileAdapter(String[] caregiversImage) {
			this.caregiversImage = caregiversImage;
		}
		
		@NonNull
		@Override
		public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
					R.layout.row_todo_tasks_caregivers, parent, false));
		}
		
		@Override
		public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
			holder.bind(position);
		}
		
		@Override
		public int getItemCount() {
			if (caregiversImage.length < 3) {
				return caregiversImage.length;
			}
			return 3;
		}
		
		public class ViewHolder extends RecyclerView.ViewHolder {
			
			ImageView ivProfile;
			
			TextView tvProfileCount;
			
			public ViewHolder(@NonNull View itemView) {
				super(itemView);
				ivProfile = itemView.findViewById(R.id.ivProfile);
				tvProfileCount = itemView.findViewById(R.id.tvProfileCount);
			}
			
			public void bind(int position) {
				String imageName = caregiversImage[0];
				Glide.with(itemView.getContext()).load(
						APIS.CaregiverImageURL + imageName).placeholder(R.drawable.user_img).into(
						ivProfile);
				if (position < 2) {
					ivProfile.setVisibility(View.VISIBLE);
					tvProfileCount.setVisibility(View.GONE);
				} else {
					ivProfile.setVisibility(View.GONE);
					tvProfileCount.setVisibility(View.VISIBLE);
				}
			}
		}
	}
}
