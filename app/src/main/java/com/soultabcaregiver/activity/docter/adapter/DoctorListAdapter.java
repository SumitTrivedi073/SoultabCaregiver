package com.soultabcaregiver.activity.docter.adapter;

import static com.soultabcaregiver.utils.Utility.mContext;

import android.app.Activity;
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

import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.docter.DocorDetailsActivity;
import com.soultabcaregiver.activity.docter.DoctorModel.DoctorListModel;
import com.soultabcaregiver.utils.Utility;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.ViewHolder> implements Filterable {

    Context context;

    String docCatNm;

    TextView tvNodata;

    private final List<DoctorListModel.Response.DoctorDatum> arSearch;

    private List<DoctorListModel.Response.DoctorDatum> arDoclist;

    private AppointedDocSelectionListener docSelectionListener;

    private AppointedDocSelectionListener docFavListener;

    public DoctorListAdapter(Activity mainActivity,
                             List<DoctorListModel.Response.DoctorDatum> listdata,
                             TextView tvNodata) {
        arDoclist = listdata;
        context = mainActivity;
        this.arSearch = new ArrayList<>();
        this.arSearch.addAll(listdata);
        this.tvNodata = tvNodata;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.doctor_list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DoctorListModel.Response.DoctorDatum DocListBean = arDoclist.get(position);
        holder.doctor_name.setText(DocListBean.getName());

        if (DocListBean.getFavorite().equalsIgnoreCase("1")) {
            holder.iv_fav.setImageResource(R.drawable.heart);
        } else {
            holder.iv_fav.setImageResource(R.drawable.heart_black);
        }


        holder.doctor_list_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.getSharedPreferences(context, APIS.doctor_hide_show).equals(APIS.Edit)) {

                    Intent mINTENT = new Intent(view.getContext(), DocorDetailsActivity.class);
                    mINTENT.putExtra(APIS.DocListItem, DocListBean);
                    view.getContext().startActivity(mINTENT);
                } else {
                    Utility.ShowToast(context, mContext.getResources().getString(R.string.only_view_permission));

                }

            }
        });

        holder.rl_cust_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.getSharedPreferences(context, APIS.doctor_hide_show).equals(APIS.Edit)) {

                    if (docSelectionListener != null) {
                        docSelectionListener.DocFavListener(DocListBean, holder.getLayoutPosition());
                    }
                } else {
                    Utility.ShowToast(context, context.getResources().getString(R.string.only_view_permission));

                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return arDoclist.size();
    }


    public void DocSelection(AppointedDocSelectionListener actDocList) {
        try {
            docSelectionListener = actDocList;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public void DocFavSelection(AppointedDocSelectionListener actDocList) {
        try {
            docFavListener = actDocList;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView doctor_name;
        public RelativeLayout doctor_list_relative, rl_cust_fav;
        public ImageView iv_fav;

        public ViewHolder(View itemView) {
            super(itemView);
            this.doctor_name = itemView.findViewById(R.id.doctor_name);
            doctor_list_relative = itemView.findViewById(R.id.doctor_list_relative);
            rl_cust_fav = itemView.findViewById(R.id.rl_cust_fav);
            iv_fav = itemView.findViewById(R.id.iv_fav);


        }
    }

    public interface AppointedDocSelectionListener {
        void DocSelectionListener(List<DoctorListModel.Response.DoctorDatum> DocBeanList, boolean isSearch);

        void DocFavListener(DoctorListModel.Response.DoctorDatum DocBeanList, int isSearch);

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    arDoclist = arSearch;
                } else {
                    List<DoctorListModel.Response.DoctorDatum> filteredList = new ArrayList<>();
                    for (DoctorListModel.Response.DoctorDatum row : arSearch) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    arDoclist = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = arDoclist;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                arDoclist = (ArrayList<DoctorListModel.Response.DoctorDatum>) filterResults.values;
                if (arDoclist.size() > 0) {
                    tvNodata.setVisibility(View.GONE);
                    if (docSelectionListener != null) {
                        docSelectionListener.DocSelectionListener(arDoclist, true);
                    }
                } else {
                    tvNodata.setVisibility(View.VISIBLE);
                }
                notifyDataSetChanged();
            }
        };
    }


}