package com.soultabcaregiver.activity.docter.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.docter.UpdateDoctorAppointmentActivity;
import com.soultabcaregiver.activity.docter.DoctorModel.DoctorAppointmentList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 1/16/2019.
 */

public class DoctorAppointedListAdptr extends
        RecyclerView.Adapter<DoctorAppointedListAdptr.ViewHolder> implements Filterable {
    Context context;
    int diff;
    RelativeLayout rl_select_remove;
    TextView tvNodata;
    private List<DoctorAppointmentList.Response.AppointmentDatum> arAppointedDoc, arSearch;
    private AppointedDocSelectionListener appointedDocSelectionListener;

    public DoctorAppointedListAdptr(Context mContext, List<DoctorAppointmentList.Response.AppointmentDatum> arRemind_, int diff_, RelativeLayout rl_select_remove, TextView tvNodata) {
        arAppointedDoc = arRemind_;
        context = mContext;
        this.diff = diff_;
        this.arSearch = new ArrayList<>();
        this.arSearch.addAll(arAppointedDoc);
        this.rl_select_remove = rl_select_remove;
        this.tvNodata = tvNodata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.doctor_appint_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get the data model based on position

        final DoctorAppointmentList.Response.AppointmentDatum AppointedDocBean = arAppointedDoc.get(position);

        viewHolder.tvTitle.setText(AppointedDocBean.getDoctorName());


        if (AppointedDocBean.isCheck()) {
            viewHolder.ivSelect.setImageResource(R.drawable.checked);
        } else {
            viewHolder.ivSelect.setImageResource(R.drawable.uncheck);
        }

        viewHolder.ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppointedDocBean.isCheck()) {
                    AppointedDocBean.setCheck(false);
                } else {
                    AppointedDocBean.setCheck(true);
                }
                if (appointedDocSelectionListener != null) {
                    appointedDocSelectionListener.AppointedDocSelection(arAppointedDoc, false);
                }
            }
        });
        viewHolder.rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mINTENT = new Intent(context, UpdateDoctorAppointmentActivity.class);//for update appointed doc
                mINTENT.putExtra(APIS.DocListItem, AppointedDocBean);
                context.startActivity(mINTENT);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arAppointedDoc.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    arAppointedDoc = arSearch;
                } else {
                    List<DoctorAppointmentList.Response.AppointmentDatum> filteredList = new ArrayList<>();
                    for (DoctorAppointmentList.Response.AppointmentDatum row : arSearch) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getDoctorName().toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }

                    arAppointedDoc = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = arAppointedDoc;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                arAppointedDoc = (ArrayList<DoctorAppointmentList.Response.AppointmentDatum>) filterResults.values;
                if (arAppointedDoc.size()>0) {
                    rl_select_remove.setVisibility(View.VISIBLE);
                    tvNodata.setVisibility(View.GONE);

                    if (appointedDocSelectionListener != null) {
                        appointedDocSelectionListener.AppointedDocSelection(arAppointedDoc, true);
                    }
                }else {
                    rl_select_remove.setVisibility(View.GONE);
                    tvNodata.setVisibility(View.VISIBLE);

                }
                notifyDataSetChanged();
            }
        };
    }




    public void setAppointedDocSelection(AppointedDocSelectionListener listener) {
        try {
            appointedDocSelectionListener = listener;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }


    public interface AppointedDocSelectionListener {
        void AppointedDocSelection(List<DoctorAppointmentList.Response.AppointmentDatum> appointedDocBeanList, boolean isSearch);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivSelect;
        TextView tvTitle;
        RelativeLayout rlMain;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            rlMain = itemView.findViewById(R.id.rl_main);
            ivSelect = itemView.findViewById(R.id.iv_select);
        }
    }
}
