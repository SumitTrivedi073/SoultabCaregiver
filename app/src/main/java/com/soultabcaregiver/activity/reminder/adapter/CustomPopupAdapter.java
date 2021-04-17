package com.soultabcaregiver.activity.reminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.reminder.model.BeforeTimeModel;

import java.util.List;

public class CustomPopupAdapter extends RecyclerView.Adapter<CustomPopupAdapter.ViewHolder> {
    private List<BeforeTimeModel> repeatArrayIn;
    private Context context;
    private boolean isRepeat;

    public CustomPopupAdapter(Context context_, List<BeforeTimeModel> repeatArray_, boolean isRepeat_) {
        repeatArrayIn = repeatArray_;
        context = context_;
        isRepeat = isRepeat_;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.custome_repeat_view, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        if (position == 0 && !isRepeat) {
            viewHolder.tb_remind_noreminder.setVisibility(View.VISIBLE);
            viewHolder.ivSelect.setVisibility(View.GONE);
        } else {
            viewHolder.tb_remind_noreminder.setVisibility(View.GONE);
            viewHolder.ivSelect.setVisibility(View.VISIBLE);
        }
        viewHolder.tvTitle.setText(repeatArrayIn.get(position).getTimeName());
        if (!isRepeat && position == 0) {
            viewHolder.tb_remind_noreminder.setChecked(repeatArrayIn.get(position).isSelection());
        } else if (repeatArrayIn.get(position).isSelection()) {
            viewHolder.ivSelect.setImageResource(R.drawable.ic_check_icon);
        } else {
            viewHolder.ivSelect.setImageResource(R.drawable.ic_reminder_unselect);
        }
        viewHolder.rlMain.setOnClickListener(v -> {


            if (beforeTimeListener != null) {
                beforeTimeListener.BeforeTimeClick(repeatArrayIn.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return repeatArrayIn.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        LinearLayout rlMain;
        public ImageView ivSelect;
        public Switch tb_remind_noreminder;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title_repeat);
            ivSelect = itemView.findViewById(R.id.img_before_event);
            rlMain = itemView.findViewById(R.id.rl_main_repeat_reminder);
            tb_remind_noreminder = itemView.findViewById(R.id.tb_remind_noreminder);

        }
    }


    public interface BeforeTimeListener {
        void BeforeTimeClick(final BeforeTimeModel beforeTimeModel);
    }

    private BeforeTimeListener beforeTimeListener;

    public void setBeforeTimeListener(BeforeTimeListener listener) {
        try {
            beforeTimeListener = listener;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
