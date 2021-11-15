package com.soultabcaregiver.activity.todotask.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.todotask.model.TaskCountModel;

import java.util.ArrayList;


public class TodoFilterListAdapter extends RecyclerView.Adapter<TodoFilterListAdapter.ViewHolder> {
	
	private ArrayList<TaskCountModel.ToDoFilterModel> todoTagsFilterList = new ArrayList<>();
	
	private final OnFilterItemClickListener listener;
	
	private int selectedPosition = 0;
	
	public TodoFilterListAdapter(ArrayList<TaskCountModel.ToDoFilterModel> todoTagsFilterList,
	                             OnFilterItemClickListener listener) {
		this.listener = listener;
		this.todoTagsFilterList = todoTagsFilterList;
	}
	
	public void updateData(ArrayList<TaskCountModel.ToDoFilterModel> data) {
		todoTagsFilterList = new ArrayList<>();
		todoTagsFilterList.addAll(data);
		notifyDataSetChanged();
	}
	
	public interface OnFilterItemClickListener {
		
		void onFilterItemClick(int position, TaskCountModel.ToDoFilterModel filterModel);
		
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(
				LayoutInflater.from(parent.getContext()).inflate(R.layout.row_todo_filters, parent,
						false));
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder,
	                             @SuppressLint ("RecyclerView") int position) {
		holder.bind(position);
	}
	
	@Override
	public int getItemCount() {
		return todoTagsFilterList.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		
		TextView tvFilterName, tvFilterCount;
		
		ConstraintLayout clMain;
		
		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			tvFilterName = itemView.findViewById(R.id.tvFilterName);
			clMain = itemView.findViewById(R.id.clMain);
			tvFilterCount = itemView.findViewById(R.id.tvFilterCount);
		}
		
		public void bind(int position) {
			TaskCountModel.ToDoFilterModel filters = todoTagsFilterList.get(position);
			tvFilterName.setText(filters.getTagName());
			tvFilterCount.setText(filters.getCount().toString());
			if (selectedPosition == position) {
				GradientDrawable gd = (GradientDrawable) clMain.getBackground().getCurrent();
				gd.setColor(Color.parseColor("#00A5F2"));
				tvFilterName.setTextColor(
						ContextCompat.getColor(itemView.getContext(), R.color.white));
				tvFilterCount.setTextColor(
						ContextCompat.getColor(itemView.getContext(), R.color.white));
			} else {
				GradientDrawable gd = (GradientDrawable) clMain.getBackground().getCurrent();
				gd.setColor(Color.parseColor("#FAFAFA"));
				tvFilterName.setTextColor(
						ContextCompat.getColor(itemView.getContext(), R.color.black));
				tvFilterCount.setTextColor(
						ContextCompat.getColor(itemView.getContext(), R.color.black));
			}
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					selectedPosition = position;
					listener.onFilterItemClick(selectedPosition, filters);
					notifyDataSetChanged();
				}
			});
		}
		
	}
	
}
