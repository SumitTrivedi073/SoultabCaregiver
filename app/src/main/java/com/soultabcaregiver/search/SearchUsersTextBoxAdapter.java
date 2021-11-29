package com.soultabcaregiver.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.soultabcaregiver.R;
import com.soultabcaregiver.search.models.UserSearchResultResponse;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

interface SearchItemClickListener {
	
	void onSearchPersonSelected(UserSearchResultResponse.UserSearchResultModel model);
}

public class SearchUsersTextBoxAdapter extends ArrayAdapter<UserSearchResultResponse.UserSearchResultModel> {
	
	private final SearchItemClickListener searchItemClickListener;
	
	private List<UserSearchResultResponse.UserSearchResultModel> usersList = new ArrayList<>();
	
	public SearchUsersTextBoxAdapter(@NonNull Context context,
	                                 SearchItemClickListener searchItemClickListener) {
		super(context, R.layout.item_user_search_suggestion);
		this.searchItemClickListener = searchItemClickListener;
	}
	
	@Override
	public int getCount() {
		return usersList.size();
	}
	
	@Nullable
	@Override
	public UserSearchResultResponse.UserSearchResultModel getItem(int position) {
		return usersList.get(position);
	}
	
	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		@SuppressLint ("ViewHolder") View rowView =
				inflater.inflate(R.layout.item_user_search_suggestion, parent, false);
		TextView userName = rowView.findViewById(R.id.userName);
		
		userName.setText(usersList.get(position).getName());
		
		ImageView userImage = rowView.findViewById(R.id.userPic);
		
		RequestOptions options =
				new RequestOptions().centerCrop().dontAnimate().fitCenter().placeholder(
						R.drawable.user_img).error(R.drawable.user_img);
		
		Glide.with(parent.getContext()).load(usersList.get(position).getProfileImage()).apply(
				options).into(userImage);
		
		rowView.setOnClickListener(
				v -> searchItemClickListener.onSearchPersonSelected(usersList.get(position)));
		
		return rowView;
	}
	
	@NonNull
	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				
				List<UserSearchResultResponse.UserSearchResultModel> suggestions =
						new ArrayList<>();
				if (constraint != null) {
					for (UserSearchResultResponse.UserSearchResultModel model : usersList) {
						if (model.getName().startsWith(constraint.toString())) {
							suggestions.add(model);
						}
					}
					filterResults.values = suggestions;
					filterResults.count = suggestions.size();
				}
				return filterResults;
			}
			
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null && (results.count > 0)) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		};
	}
	
	public void setUsersList(List<UserSearchResultResponse.UserSearchResultModel> newList) {
		this.usersList.clear();
		this.usersList = newList;
		notifyDataSetChanged();
	}
}
