package com.soultabcaregiver.activity.calender.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.WebService.ApiTokenAuthentication;
import com.soultabcaregiver.activity.calender.CalenderModel.ReminderBean;
import com.soultabcaregiver.activity.docter.UpdateDoctorAppointmentActivity;
import com.soultabcaregiver.activity.reminder.AddReminderActivity;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.CustomProgressDialog;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomEventAdapter extends
        RecyclerView.Adapter<CustomEventAdapter.ViewHolder> {
    Activity activity;
    TextView tvNodata;
    String FromDate2;
    AlertDialog alertDialog;
    private final List<ReminderBean> arRemindIn;
    private final Context context;
    private CustomProgressDialog progressDialog;


    public CustomEventAdapter(Context context_, List<ReminderBean> arRemind_, TextView tvNodata, String fromDate2) {
        arRemindIn = arRemind_;
        context = context_;
        activity = (Activity) context_;
        this.tvNodata = tvNodata;
        this.FromDate2 = fromDate2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.calender_event_item, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        // Get the data model based on position
        final ReminderBean reminderBean = arRemindIn.get(position);
        viewHolder.tvTitle.setText(reminderBean.getTitle());
        try {

            if (reminderBean.getDate() != null) {
                String month = Utility.MMM.format(Utility.yyyy_MM_dd.parse(reminderBean.getDate()));
                viewHolder.day_txt.setText(Utility.dd.format(Utility.yyyy_MM_dd.parse(reminderBean.getDate())) + "\n" + month);
                viewHolder.tvDate.setText(Utility.EEE_dd_MMM_yyyy.format(Utility.yyyy_MM_dd.parse(reminderBean.getDate())));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.rlMain.setOnClickListener(v -> {
            if (!reminderBean.isAppointment()) {
                activity.startActivityForResult(new Intent(context, AddReminderActivity.class)
                        .putExtra(APIS.ReminderModel, arRemindIn.get(position))
                        .putExtra("calender", "calender")
                        .putExtra(APIS.Update_reminder, true), 1);

            }else {
                Intent mINTENT = new Intent(context, UpdateDoctorAppointmentActivity.class);//for only view appointed doc details

                mINTENT.putExtra("id", reminderBean.getId());
                mINTENT.putExtra("diff_", "2");
                mINTENT.putExtra("Doctor_id", reminderBean.getDoctor_id());
                mINTENT.putExtra("Doctor_Email", reminderBean.getDoctor_Email());
                mINTENT.putExtra("Doctor_Fax", reminderBean.getDoctor_Fax());
                mINTENT.putExtra("Doctor_Website", reminderBean.getDoctor_Website());

                context.startActivity(mINTENT);
            }
        });

        viewHolder.delete_Reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.getSharedPreferences(context, APIS.calender_hideshow).equals(APIS.Edit)) {

                    if (reminderBean.isAppointment()) {
                        alertmessage(reminderBean.getId(), position, "1");
                    } else {
                        alertmessage(reminderBean.getId(), position, "0");
                    }
                } else {
                    Utility.ShowToast(context, context.getResources().getString(R.string.only_view_permission));
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return arRemindIn.size();
    }

    private void alertmessage(String id, int position, String value) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.common_popup_layout,
                null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();

        TextView title_popup = layout.findViewById(R.id.title_popup);
        TextView message_popup = layout.findViewById(R.id.message_popup);
        TextView no_text_popup = layout.findViewById(R.id.no_text_popup);
        TextView yes_text_popup = layout.findViewById(R.id.yes_text_popup);
        title_popup.setText(context.getResources().getString(R.string.delete_Reminder));
        message_popup.setText(context.getResources().getString(R.string.are_you_sure_you_want_to_delete_reminder));
        no_text_popup.setText(context.getResources().getString(R.string.no_text));
        yes_text_popup.setText(context.getResources().getString(R.string.yes_text));

        yes_text_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (Utility.isNetworkConnected(context)) {
                    DeletRemind(id, position, value);
                } else {

                    Utility.ShowToast(context, context.getResources().getString(R.string.net_connection));
                }
            }
        });

        no_text_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    public void DeletRemind(String sRemindId, int position, String value) {

        final String TAG = "Delete Remind";
        JSONObject mainObject = new JSONObject();
        String URL = null;
        if (value.equals("1")) {
            try {
                mainObject.put("appointment_id", sRemindId);
                mainObject.put("user_id", Utility.getSharedPreferences(context, APIS.user_id));
                mainObject.put("caregiver_id", Utility.getSharedPreferences(context, APIS.caregiver_id));

                URL = APIS.DELETE_DOC_APPOIN_API;
                Log.e(TAG, "appointmentCancel======>" + mainObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (value.equals("0")) {

            try {
                mainObject.put("reminder_id", sRemindId);
                mainObject.put("caregiver_id", Utility.getSharedPreferences(context, APIS.caregiver_id));

                URL = APIS.DELETEREMINDERAPI;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Log.e("URL", URL);

        showProgressDialog(context, context.getResources().getString(R.string.Loading));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + URL, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Delete Remind response=" + response.toString());
                        hideProgressDialog();
                        try {
                            String code = response.getString("status_code");

                            if (code.equals("200")) {

                                ShowAlertResponse();

                                if (arRemindIn.size() > 0) {
                                    arRemindIn.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, arRemindIn.size());

                                } else {
                                    tvNodata.setVisibility(View.VISIBLE);
                                    tvNodata.setText(context.getResources().getString(R.string.no_activity_scheduled) + " " + FromDate2);

                                }

                            } else if (code.equals("403")) {
                                BaseActivity.getInstance().logout_app(response.getString("message"));
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
                if (error.networkResponse!=null) {
                    if (String.valueOf(error.networkResponse.statusCode).equals(APIS.APITokenErrorCode)) {
                        ApiTokenAuthentication.refrehToken(context, updatedToken -> {
                            if (updatedToken == null) {
                            } else {
                                DeletRemind(sRemindId,position,value);
                    
                            }
                        });
                    }else {
                        Utility.ShowToast(
                                context,
                                context.getResources().getString(R.string.something_went_wrong));
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(context,APIS.EncodeUser_id));
                params.put(APIS.APITokenKEY,
                        Utility.getSharedPreferences(context, APIS.APITokenValue));
    
                return params;
            }

        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void ShowAlertResponse() {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.send_successfully_layout,
                null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();


        TextView OK_txt = layout.findViewById(R.id.OK_txt);
        TextView title_txt = layout.findViewById(R.id.title_txt);

        title_txt.setText(context.getResources().getString(R.string.reminder_delete_successfully));

        OK_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (arRemindIn.size() > 0) {

                } else {
                    tvNodata.setVisibility(View.VISIBLE);
                    tvNodata.setText(context.getResources().getString(R.string.no_activity_scheduled) + " " + FromDate2);

                }
            }
        });

    }

    public void showProgressDialog(Context mContext, String message) {
        if (progressDialog == null) progressDialog = new CustomProgressDialog(mContext, message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) progressDialog.dismiss();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView day_txt, tvTitle, tvDate;
        RelativeLayout rlMain;
        RelativeLayout delete_Reminder;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.Event_Title);
            tvDate = itemView.findViewById(R.id.Event_Date);
            day_txt = itemView.findViewById(R.id.day_txt);
            rlMain = itemView.findViewById(R.id.rl_main);
            delete_Reminder = itemView.findViewById(R.id.delete_Reminder);
            delete_Reminder.setVisibility(View.VISIBLE);
        }
    }

}
