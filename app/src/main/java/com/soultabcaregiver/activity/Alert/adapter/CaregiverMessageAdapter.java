package com.soultabcaregiver.activity.Alert.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.Alert.model.AlertModel;
import com.soultabcaregiver.utils.Utility;

import java.text.ParseException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CaregiverMessageAdapter extends RecyclerView.Adapter<CaregiverMessageAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    public List<AlertModel.Data.CaregiverDatum.MessageDatum> AlertMessageListdata;
    public List<AlertModel.Data.CaregiverDatum.MessageDatum> AlertMessageList_Filtered;
    Context mContext;
    RequestOptions options;


    public CaregiverMessageAdapter(Context mContext, List<AlertModel.Data.CaregiverDatum.MessageDatum> myListData) {
        this.AlertMessageListdata = myListData;
        this.AlertMessageList_Filtered = myListData;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //final History myListData = listdata[position];

        AlertModel.Data.CaregiverDatum.MessageDatum messageDatum = AlertMessageList_Filtered.get(position);

        holder.message_title.setText(messageDatum.getMessage());
        try {
            holder.message_date.setText(Utility.EEE_dd_MMM_yyyy_hh_mm_aa.format(Utility.yyyy_mm_dd_hh_mm_ss.parse(messageDatum.getDateTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (position==AlertMessageListdata.size()-1){
             holder.view_line.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        //return histories.size();
        return AlertMessageList_Filtered.size();
    }

  
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView message_title,message_date;
        View view_line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            message_title = itemView.findViewById(R.id.message_title);
            message_date = itemView.findViewById(R.id.message_date);
            view_line = itemView.findViewById(R.id.view_line);
        }
    }

}
