package com.soultabcaregiver.sendbird_chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.soultabcaregiver.R;
import com.soultabcaregiver.sendbird_chat.model.GroupMemberModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMemberListAdapter extends RecyclerView.Adapter<GroupMemberListAdapter.ViewHolder> implements Filterable {
    
    private final List<GroupMemberModel> arSearch;
    
    private final Context mcontext;
    
    private List<GroupMemberModel> groupmemberlist;
    
    public GroupMemberListAdapter(Context context_, List<GroupMemberModel> groupmemberlist_) {
        this.groupmemberlist = groupmemberlist_;
        this.arSearch = groupmemberlist_;
        this.mcontext = context_;
        notifyDataSetChanged();
        
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.group_member_list, parent, false);
        return new ViewHolder(contactView);
    }
    
    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        
        holder.group_member_name.setText(groupmemberlist.get(position).getNickname());
        Glide.with(mcontext).load(groupmemberlist.get(position).getImage()).
                placeholder(R.drawable.user_img).into(holder.group_member_image);
        
    }
    
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
    
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
    
    @Override
    public int getItemCount() {
        return groupmemberlist.size();
    }
    
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    groupmemberlist = arSearch;
                } else {
                    List<GroupMemberModel> filteredList = new ArrayList<>();
                    for (GroupMemberModel row : arSearch) {
                        
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getNickname().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    
                    groupmemberlist = filteredList;
                }
                
                FilterResults filterResults = new FilterResults();
                filterResults.values = groupmemberlist;
                return filterResults;
            }
            
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                groupmemberlist = (ArrayList<GroupMemberModel>) filterResults.values;
                
                notifyDataSetChanged();
            }
        };
    }
    
    public List<GroupMemberModel> getMedialist() {
        return groupmemberlist;
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        
        CircleImageView group_member_image;
        
        TextView group_member_name;
        
        public ViewHolder(View rowView) {
            super(rowView);
            group_member_image = rowView.findViewById(R.id.group_member_image);
            group_member_name = rowView.findViewById(R.id.group_member_name);
        }
    }
    
    
}



