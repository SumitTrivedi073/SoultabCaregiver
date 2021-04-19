package com.soultabcaregiver.activity.Alert.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.slider.Slider;
import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.Alert.model.AlertModel;
import com.soultabcaregiver.activity.MainScreen.model.ChartModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    public List<AlertModel.Data.CaregiverDatum> AlertListdata;
    public List<AlertModel.Data.CaregiverDatum> AlertList_Filtered;
    Context mContext;
    RequestOptions options;


    public AlertAdapter(Context mContext, List<AlertModel.Data.CaregiverDatum> myListData) {
        this.AlertListdata = myListData;
        this.AlertList_Filtered = myListData;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //final History myListData = listdata[position];

        AlertModel.Data.CaregiverDatum caregiverDatum = AlertList_Filtered.get(position);

        holder.name_txt.setText(caregiverDatum.getName());

        Glide.with(mContext).load(caregiverDatum.getIcon()).apply(options).into(holder.image);

        if (caregiverDatum.getMessageData().size()>0){
            CaregiverMessageAdapter alertAdapter = new CaregiverMessageAdapter(mContext, caregiverDatum.getMessageData());
            holder.message_list.setHasFixedSize(true);
            holder.message_list.setAdapter(alertAdapter);
        }
    }


    @Override
    public int getItemCount() {
        //return histories.size();
        return AlertList_Filtered.size();
    }

  
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name_txt;
        CircleImageView image;
        RecyclerView message_list;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name_txt = itemView.findViewById(R.id.name_txt);
            image = itemView.findViewById(R.id.image);
            message_list = itemView.findViewById(R.id.message_list);

            options = new RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .fitCenter()
                    .placeholder(R.drawable.place_holder_photo)
                    .error(R.drawable.place_holder_photo);


        }
    }

}
