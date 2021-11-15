package com.soultabcaregiver.activity.todotask.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.todotask.model.TaskAttachmentsModel;

import java.util.ArrayList;


public class AttachmentsAdapter extends RecyclerView.Adapter<AttachmentsAdapter.ViewHolder> {
	
	private Context context;
	
	private OnAttachmentItemClickListeners listeners;
	
	private ArrayList<TaskAttachmentsModel> attachments = new ArrayList<>();
	
	public AttachmentsAdapter(Context context, OnAttachmentItemClickListeners listeners) {
		this.context = context;
		this.listeners = listeners;
		attachments.add(0, new TaskAttachmentsModel());
		
	}
	
	public AttachmentsAdapter(Context context) {
		this.context = context;
	}
	
	public int getAttachmentCount() {
		return attachments.size();
	}
	
	public void add(TaskAttachmentsModel attachment) {
		attachments.add(attachment);
		notifyDataSetChanged();
	}
	
	public ArrayList<TaskAttachmentsModel> getAttachments() {
		ArrayList<TaskAttachmentsModel> temp = new ArrayList<>();
		temp.addAll(attachments);
		temp.remove(0);
		return temp;
	}
	
	public void update(ArrayList<TaskAttachmentsModel> attachmentsList) {
		attachments.subList(1, attachments.size()).clear();
		for (TaskAttachmentsModel model : attachmentsList) {
			attachments.add(model);
		}
		notifyDataSetChanged();
	}
	
	
	public interface OnAttachmentItemClickListeners {
		
		void onAddAttachmentClick();
		void removeAttachment(int size);
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
				R.layout.row_todo_attachments_adapter, parent, false));
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		
		holder.bind(position);
	}
	
	@Override
	public int getItemCount() {
		return attachments.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		
		ImageView ivAdd, ivCaregiver, ivCancel;
		
		TextView tvName;
		
		LinearLayout llMain;
		
		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			
			ivAdd = itemView.findViewById(R.id.ivAdd);
			ivAdd.setImageDrawable(
					ContextCompat.getDrawable(context, R.drawable.ic_add_attachment));
			
			llMain = itemView.findViewById(R.id.llMain);
			ivCaregiver = itemView.findViewById(R.id.ivCaregiver);
			ivCancel = itemView.findViewById(R.id.ivCancel);
			tvName = itemView.findViewById(R.id.tvName);
		}
		
		public void bind(int position) {
			
			TaskAttachmentsModel attachment = attachments.get(position);
			tvName.setText(attachment.getFileName());
			
			ivAdd.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listeners.onAddAttachmentClick();
				}
			});
			
			if (position == 0) {
				llMain.setVisibility(View.GONE);
				ivAdd.setVisibility(View.VISIBLE);
			} else {
				llMain.setVisibility(View.VISIBLE);
				ivAdd.setVisibility(View.GONE);
			}
			
			ivCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					attachments.remove(position);
					notifyItemRemoved(position);
					notifyItemRangeChanged(position, attachments.size());
					listeners.removeAttachment(attachments.size());
				}
			});
			
		}
	}
}
