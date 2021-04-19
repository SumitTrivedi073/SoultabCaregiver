package com.soultabcaregiver.activity.Alert.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.Alert.model.CareGiverListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CareGiverListAdapter extends RecyclerView.Adapter<CareGiverListAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    public List<CareGiverListModel.Response> CaregiverListdata;
    public List<CareGiverListModel.Response> CaregiverList_Filtered;
    Context mContext;
    RequestOptions options;


    public CareGiverListAdapter(Context mContext, List<CareGiverListModel.Response> myListData) {
        this.CaregiverListdata = myListData;
        this.CaregiverList_Filtered = myListData;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.caregiver_item, parent, false);
        ViewHolder vh = new ViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //final History myListData = listdata[position];

        CareGiverListModel.Response caregiver_response = CaregiverList_Filtered.get(position);

        holder.caregiver_name_txt.setText(CaregiverList_Filtered.get(position).getName() + " " + CaregiverList_Filtered.get(position).getLastname());

        holder.caregiver_email_txt.setText(CaregiverList_Filtered.get(position).getEmail());

        Glide.with(mContext)
                .load(APIS.CaregiverImageURL + CaregiverList_Filtered.get(position).getProfileImage())
                .apply(options)
                .into(holder.caregiver_image);

        holder.Caregiver_profile_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }


    @Override
    public int getItemCount() {
        //return histories.size();
        return CaregiverList_Filtered.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView caregiver_name_txt, caregiver_email_txt;
        ImageView caregiver_image;
        RelativeLayout delete_btn;
        CardView Caregiver_profile_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            caregiver_name_txt = itemView.findViewById(R.id.caregiver_name_txt);
            caregiver_image = itemView.findViewById(R.id.caregiver_image);
            Caregiver_profile_item = itemView.findViewById(R.id.Caregiver_profile_item);

            options = new RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .fitCenter()
                    .placeholder(R.drawable.place_holder_photo)
                    .error(R.drawable.place_holder_photo);


        }
    }
}
