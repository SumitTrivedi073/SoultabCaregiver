package com.soultabcaregiver.activity.docter.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soultabcaregiver.Model.DiloagBoxCommon;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.docter.UpdateDoctorAppointmentActivity;
import com.soultabcaregiver.activity.docter.DoctorModel.DoctorAppointmentList;
import com.soultabcaregiver.activity.docter.fragment.DoctorAppointmentFragment;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.CustomProgressDialog;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by poonam on 1/16/2019.
 */

public class DoctorAppointedListAdptr extends
        RecyclerView.Adapter<DoctorAppointedListAdptr.ViewHolder> implements Filterable {
    Context context;
    int diff;
    TextView tvNodata;
    private List<DoctorAppointmentList.Response.AppointmentDatum> arAppointedDoc, arSearch;
    private CustomProgressDialog progressDialog;
    DoctorAppointmentFragment doctorAppointmentFragment;


    public DoctorAppointedListAdptr(Context mContext, List<DoctorAppointmentList.Response.AppointmentDatum> arRemind_, int diff_, TextView tvNodata) {
        arAppointedDoc = arRemind_;
        context = mContext;
        this.diff = diff_;
        this.tvNodata = tvNodata;
        doctorAppointmentFragment = DoctorAppointmentFragment.instance;
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

        viewHolder.doctor_name.setText(AppointedDocBean.getDoctorName());



        viewHolder.update_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mINTENT = new Intent(context, UpdateDoctorAppointmentActivity.class);//for update appointed doc
                mINTENT.putExtra(APIS.DocListItem, AppointedDocBean);
                context.startActivity(mINTENT);
            }
        });

        viewHolder.delete_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertmessage(AppointedDocBean.getAppointmentId());
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
                    tvNodata.setVisibility(View.GONE);

                }else {
                    tvNodata.setVisibility(View.VISIBLE);

                }
                notifyDataSetChanged();
            }
        };
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView doctor_name;
        RelativeLayout update_appointment,delete_appointment;

        public ViewHolder(View itemView) {
            super(itemView);
            doctor_name = itemView.findViewById(R.id.doctor_name);
            delete_appointment = itemView.findViewById(R.id.delete_appointment);
            update_appointment = itemView.findViewById(R.id.update_appointment);
        }
    }

    private void alertmessage(String appointmentId) {

        final DiloagBoxCommon diloagBoxCommon = Utility.Alertmessage(context, context.getResources().getString(R.string.delete_Appointment)
                , context.getResources().getString(R.string.are_you_sure_you_want_to_delete_appointment)
                , context.getResources().getString(R.string.no_text)
                , context.getResources().getString(R.string.yes_text));
        diloagBoxCommon.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diloagBoxCommon.getDialog().dismiss();
                if (Utility.isNetworkConnected(context)) {
                    DeletAppointment(appointmentId);
                } else {

                    Utility.ShowToast(context, context.getResources().getString(R.string.net_connection));
                }
            }
        });
    }

    public void DeletAppointment(String appointmentId) {

        final String TAG = "Delete AppointedDoc";
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("appointment_id", appointmentId);
            mainObject.put("user_id", Utility.getSharedPreferences(context,APIS.user_id));
            mainObject.put("caregiver_id", Utility.getSharedPreferences(context,APIS.caregiver_id));

            Log.e(TAG, "appointmentdelete======>" + mainObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showProgressDialog(context,context.getResources().getString(R.string.Loading));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.DELETE_DOC_APPOIN_API, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Delete AppointedDoc response=" + response.toString());
                        hideProgressDialog();
                        try {
                            String code = response.getString("status_code");
                            if (code.equals("200")) {

                                Utility.ShowToast(context, response.getJSONObject("response")
                                        .getString("appointment_data"));

                                    doctorAppointmentFragment.GetAppointedDocRecond();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                return params;
            }

        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void showProgressDialog(Context context,String message){
        if(progressDialog == null) progressDialog = new CustomProgressDialog(context, message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgressDialog(){
        if(progressDialog != null) progressDialog.dismiss();
    }



}
