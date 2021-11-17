package com.soultabcaregiver.activity.todotask.adapter;

import android.content.Context;
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
import com.soultabcaregiver.activity.todotask.model.TaskCaregiversModel;
import com.soultabcaregiver.utils.Utility;

import java.util.ArrayList;


public class AssignedToCaregiverAdapter extends RecyclerView.Adapter<AssignedToCaregiverAdapter.ViewHolder> {
	
	private Context context;
	
	private OnCaregiverItemClickListener listener;
	
	private ArrayList<TaskCaregiversModel> caregiversName = new ArrayList<>();
	
	public AssignedToCaregiverAdapter(Context context, OnCaregiverItemClickListener listener) {
		this.context = context;
		this.listener = listener;
		caregiversName.add(0, new TaskCaregiversModel());
	}
	
	public void update(ArrayList<TaskCaregiversModel> tempCaregiverName) {
		caregiversName.subList(1, caregiversName.size()).clear();
		for (TaskCaregiversModel name : tempCaregiverName) {
			caregiversName.add(name);
		}
		notifyDataSetChanged();
	}
	
	public int getCaregiversCount() {
		return caregiversName.size();
	}
	
	public interface OnCaregiverItemClickListener {
		
		void onAddCaregiverClick();
		void onRemoveCareGiverClick(int position, int caregiversCount);
		
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
				R.layout.row_todo_assigned_caregiver, parent, false));
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.bind(position);
	}
	
	@Override
	public int getItemCount() {
		return caregiversName.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		
		ImageView ivAdd, ivCaregiver, ivCancel;
		
		TextView tvName;
		
		LinearLayout llMain;
		
		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			ivAdd = itemView.findViewById(R.id.ivAdd);
			ivAdd.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add));
			llMain = itemView.findViewById(R.id.llMain);
			ivCaregiver = itemView.findViewById(R.id.ivCaregiver);
			ivCancel = itemView.findViewById(R.id.ivCancel);
			tvName = itemView.findViewById(R.id.tvName);
		}
		
		public void bind(int position) {
			TaskCaregiversModel caregiver = caregiversName.get(position);
			tvName.setText(caregiver.getName());
			if (position == 0) {
				llMain.setVisibility(View.GONE);
				ivAdd.setVisibility(View.VISIBLE);
			} else {
				ivAdd.setVisibility(View.GONE);
				llMain.setVisibility(View.VISIBLE);
				if (caregiver.getId().equals(
						Utility.getSharedPreferences(itemView.getContext(), APIS.caregiver_id))) {
					ivCancel.setVisibility(View.GONE);
				}
			}
			Log.e("TAG", "bind: " + APIS.CaregiverImageURL + caregiver.getProfileImage());
			Glide.with(context).load(
					APIS.CaregiverImageURL + caregiver.getProfileImage()).placeholder(
					R.drawable.user_img).into(ivCaregiver);
			ivAdd.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onAddCaregiverClick();
				}
			});
			ivCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					caregiversName.remove(position);
					notifyItemRemoved(position);
					notifyItemRangeChanged(position, caregiversName.size());
					listener.onRemoveCareGiverClick(position, caregiversName.size());
				}
			});
		}
		
	}
	
}
