package com.soultabcaregiver.activity.main_screen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.slider.Slider;
import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.main_screen.model.ChartModel;

import java.util.List;


public class BarChartAdapter extends RecyclerView.Adapter<BarChartAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    public List<ChartModel.Data.BarChart> BarChartListdata;
    public List<ChartModel.Data.BarChart> BarChartList_Filtered;
    Context mContext;
    RequestOptions options;
    

    public BarChartAdapter(Context mContext, List<ChartModel.Data.BarChart> myListData) {
        this.BarChartListdata = myListData;
        this.BarChartList_Filtered = myListData;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bar_chart_item, parent, false);
        ViewHolder vh = new ViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //final History myListData = listdata[position];

        ChartModel.Data.BarChart barChart = BarChartList_Filtered.get(position);

        holder.bar_title.setText(barChart.getName());

        if (Integer.parseInt(barChart.getValue())<=200){

            String Value = String.valueOf(barChart.getValue());

            holder.Continues_Slider.setValue(Float.parseFloat(Value));

        }else {
            String Value = "200";

            holder.Continues_Slider.setValue(Float.parseFloat(Value));

        }
    }


    @Override
    public int getItemCount() {
        //return histories.size();
        return BarChartList_Filtered.size();
    }

  
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView bar_title;
        Slider Continues_Slider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bar_title = itemView.findViewById(R.id.bar_title);
            Continues_Slider = itemView.findViewById(R.id.Continues_Slider);


        }
    }

}
