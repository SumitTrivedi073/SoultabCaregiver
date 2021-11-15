package com.soultabcaregiver.activity.todotask.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.todotask.model.TaskCaregiversModel;

import java.util.ArrayList;


public class CaregiverListForTaskAdapter extends RecyclerView.Adapter<CaregiverListForTaskAdapter.ViewHolder> {
	
	private Context context;
	
	private boolean isFromAddCaregiver;
	
	private ArrayList<TaskCaregiversModel> caregivers;
	
	private ArrayList<Integer> selectedCaregivers = new ArrayList<>();
	
	public CaregiverListForTaskAdapter(Context context, ArrayList<TaskCaregiversModel> caregivers,
	                                   ArrayList<Integer> selectedCaregivers,
	                                   boolean isFromAddCaregiver) {
		this.context = context;
		this.isFromAddCaregiver = isFromAddCaregiver;
		this.caregivers = caregivers;
		this.selectedCaregivers = selectedCaregivers;
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(
				LayoutInflater.from(parent.getContext()).inflate(R.layout.row_caregivers_names,
						parent, false));
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.bind(position);
	}
	
	@Override
	public int getItemCount() {
		return caregivers.size();
	}
	
	public ArrayList<Integer> getSelectedCaregivers() {
		return selectedCaregivers;
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		
		CheckBox cbNames;
		
		ImageView ivCaregiverImage;
		
		ConstraintLayout clMain;
		
		TextView tvName;
		
		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			cbNames = itemView.findViewById(R.id.cbNames);
			tvName = itemView.findViewById(R.id.tvName);
			ivCaregiverImage = itemView.findViewById(R.id.ivCaregiverImage);
			clMain = itemView.findViewById(R.id.clMain);
			
			if (isFromAddCaregiver) {
				ivCaregiverImage.setVisibility(View.VISIBLE);
			}
		}
		
		public void bind(int position) {
			TaskCaregiversModel caregiversModel = caregivers.get(position);
			tvName.setText(caregiversModel.getName());
			
			Glide.with(context).load(
					APIS.CaregiverImageURL + caregiversModel.getProfileImage()).placeholder(
					R.drawable.user_img).into(ivCaregiverImage);
			
			if (selectedCaregivers.contains(position)) {
				cbNames.setChecked(true);
			} else {
				cbNames.setChecked(false);
			}
			
			clMain.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//					if (cbNames.isChecked()) {
					//						caregiversModel.setIsSelected(0);
					//					} else {
					//						caregiversModel.setIsSelected(1);
					//					}
					//
					if (selectedCaregivers.contains(position)) {
						selectedCaregivers.remove(position);
						cbNames.setChecked(false);
					} else {
						selectedCaregivers.add(position);
						cbNames.setChecked(true);
					}
					
					//					caregivers.set(position, caregiversModel);
				}
			});
			
		}
	}
}