package com.soultabcaregiver.activity.daily_routine.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.daily_routine.model.DailyRoutineOptions;

import java.util.ArrayList;


public class RoutineOptionsAdapter extends RecyclerView.Adapter<RoutineOptionsAdapter.ViewHolder> {
	
	private final ArrayList<DailyRoutineOptions> routineOptions;
	
	private final Context context;
	
	private final LayoutInflater inflater;
	
	private OnRoutineOptionClickListener listener;
	
	public RoutineOptionsAdapter(Activity context_, ArrayList<DailyRoutineOptions> routineOptions,
	                             OnRoutineOptionClickListener listener) {
		this.routineOptions = routineOptions;
		this.context = context_;
		this.listener = listener;
		inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(
				LayoutInflater.from(parent.getContext()).inflate(R.layout.row_daily_routine_options,
						parent, false));
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.bind(position);
	}
	
	@Override
	public int getItemCount() {
		return routineOptions.size();
	}
	
	public interface OnRoutineOptionClickListener {
		
		void onRoutineItemClick(int position, DailyRoutineOptions options);
		
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		
		private ImageView ivImage;
		
		private TextView tvName;
		
		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			tvName = itemView.findViewById(R.id.tvName);
			ivImage = itemView.findViewById(R.id.ivImage);
		}
		
		public void bind(int position) {
			DailyRoutineOptions options = routineOptions.get(position);
			tvName.setText(options.getOptionName());
			ivImage.setImageResource(options.getImage());
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					listener.onRoutineItemClick(position, options);
				}
			});
		}
		
	}
	
}



