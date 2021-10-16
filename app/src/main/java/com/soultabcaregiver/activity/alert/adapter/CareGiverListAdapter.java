package com.soultabcaregiver.activity.alert.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.WebService.ApiTokenAuthentication;
import com.soultabcaregiver.activity.alert.model.CareGiverListModel;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.CustomProgressDialog;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CareGiverListAdapter extends RecyclerView.Adapter<CareGiverListAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    public List<CareGiverListModel.Response> CaregiverListdata;
    public List<CareGiverListModel.Response> CaregiverList_Filtered;
    public CustomProgressDialog progressDialog;
    Context mContext;
    RequestOptions options;
    String Message;
    AlertDialog alertDialog;
    RadioButton call_me_checkbox, grocery_checkbox, pickup_medicine_checkbox, book_taxi_checkbox, book_doctor_checkbox;


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


        Glide.with(mContext)
                .load(APIS.CaregiverImageURL + CaregiverList_Filtered.get(position).getProfileImage())
                .apply(options)
                .into(holder.caregiver_image);

        holder.Caregiver_profile_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendAlert(caregiver_response.getId());
            }
        });


    }


    @Override
    public int getItemCount() {
        //return histories.size();
        return CaregiverList_Filtered.size();
    }

    private void SendAlert(String Selected_caregiver_id) {

        Message = "";
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.send_alert_layout,
                null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();

        call_me_checkbox = layout.findViewById(R.id.call_me_checkbox);
        grocery_checkbox = layout.findViewById(R.id.grocery_checkbox);
        pickup_medicine_checkbox = layout.findViewById(R.id.pickup_medicine_checkbox);
        book_taxi_checkbox = layout.findViewById(R.id.book_taxi_checkbox);
        book_doctor_checkbox = layout.findViewById(R.id.book_doctor_checkbox);

        LinearLayout call_me_checkbox_linear = layout.findViewById(R.id.call_me_checkbox_linear);
        LinearLayout grocery_checkbox_linear = layout.findViewById(R.id.grocery_checkbox_linear);
        LinearLayout pickup_medicine_checkbox_linear = layout.findViewById(R.id.pickup_medicine_checkbox_linear);
        LinearLayout book_taxi_checkbox_linear = layout.findViewById(R.id.book_taxi_checkbox_linear);
        LinearLayout book_doctor_checkbox_linear = layout.findViewById(R.id.book_doctor_checkbox_linear);


        TextView call_me_txt = layout.findViewById(R.id.call_me_txt);
        TextView grocery_txt = layout.findViewById(R.id.grocery_txt);
        TextView pickup_medicine_txt = layout.findViewById(R.id.pickup_medicine_txt);
        TextView book_taxi_txt = layout.findViewById(R.id.book_taxi_txt);
        TextView book_doctor_txt = layout.findViewById(R.id.book_doctor_txt);
        TextView send_alert = layout.findViewById(R.id.send_alert);
        TextView cancel_alert = layout.findViewById(R.id.cancel_alert);

        call_me_checkbox.setClickable(false);
        grocery_checkbox.setClickable(false);
        pickup_medicine_checkbox.setClickable(false);
        book_taxi_checkbox.setClickable(false);
        book_doctor_checkbox.setClickable(false);

        call_me_checkbox_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                call_me_checkbox.setChecked(true);
                grocery_checkbox.setChecked(false);
                pickup_medicine_checkbox.setChecked(false);
                book_taxi_checkbox.setChecked(false);
                book_doctor_checkbox.setChecked(false);
            }

        });

        grocery_checkbox_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_me_checkbox.setChecked(false);
                grocery_checkbox.setChecked(true);
                pickup_medicine_checkbox.setChecked(false);
                book_taxi_checkbox.setChecked(false);
                book_doctor_checkbox.setChecked(false);
            }
        });

        pickup_medicine_checkbox_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                call_me_checkbox.setChecked(false);
                grocery_checkbox.setChecked(false);
                pickup_medicine_checkbox.setChecked(true);
                book_taxi_checkbox.setChecked(false);
                book_doctor_checkbox.setChecked(false);

            }
        });

        book_taxi_checkbox_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_me_checkbox.setChecked(false);
                grocery_checkbox.setChecked(false);
                pickup_medicine_checkbox.setChecked(false);
                book_taxi_checkbox.setChecked(true);
                book_doctor_checkbox.setChecked(false);
            }
        });


        book_doctor_checkbox_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_me_checkbox.setChecked(false);
                grocery_checkbox.setChecked(false);
                pickup_medicine_checkbox.setChecked(false);
                book_taxi_checkbox.setChecked(false);
                book_doctor_checkbox.setChecked(true);
            }
        });

        send_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (call_me_checkbox.isChecked()) {
                    Message = mContext.getResources().getString(R.string.Call_me);
                }
                if (grocery_checkbox.isChecked()) {
                    Message = mContext.getResources().getString(R.string.pickup_grocery_txt);
                }
                if (pickup_medicine_checkbox.isChecked()) {
                    Message = mContext.getResources().getString(R.string.pickup_medicine);
                }
    
                if (book_taxi_checkbox.isChecked()) {
                    Message = mContext.getResources().getString(R.string.book_taxi);
                }
                if (book_doctor_checkbox.isChecked()) {
                    Message = mContext.getResources().getString(R.string.book_doctor_appointment);
                }
    
                if (!TextUtils.isEmpty(Message) && !Message.equals("")) {
                    SendAlertMessage(Selected_caregiver_id);
                    alertDialog.dismiss();
                } else {
                    Utility.ShowToast(mContext,
                            mContext.getResources().getString(R.string.select_one_message));
                }
    
    
            }
        });

        cancel_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private void SendAlertMessage(String selected_caregiver_id) {
        final String TAG = getClass().getSimpleName();
        JSONObject mainObject = new JSONObject();

        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String created_at = format.format(today);


        try {
            mainObject.put("created_to", selected_caregiver_id);
            mainObject.put("created_by", Utility.getSharedPreferences(mContext, APIS.caregiver_id));
            mainObject.put("created_at", created_at);
            mainObject.put("message", Message);
            mainObject.put("alert_category", "1");

            Log.e("Yoga_mainObject", String.valueOf(mainObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog(mContext, mContext.getResources().getString(R.string.Loading));
        System.out.println(mainObject.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIS.BASEURL + APIS.QuickAlery, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "response=" + response.toString());
                        hideProgressDialog();

                        try {
                            String code = response.getString("status_code");
                            Log.e(TAG, "response=" + code);

                            if (code.equals("200")) {

                                ShowAlertResponse(mContext.getResources().getString(R.string.Alert_Send));
                            } else if (code.equals("403")) {
                                BaseActivity.getInstance().logout_app(response.getString("message"));
                            } else {
                                ShowAlertResponse(response.getString("message"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                error.printStackTrace();
                hideProgressDialog();
                if (error.networkResponse!=null) {
                    if (String.valueOf(error.networkResponse.statusCode).equals(APIS.APITokenErrorCode)) {
                        ApiTokenAuthentication.refrehToken(mContext, updatedToken -> {
                            if (updatedToken == null) {
                            } else {
                                SendAlertMessage(selected_caregiver_id);
                    
                            }
                        });
                    }else {
                        Utility.ShowToast(
                                mContext,
                                mContext.getResources().getString(R.string.something_went_wrong));
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                params.put(APIS.HEADERKEY2, Utility.getSharedPreferences(mContext,APIS.EncodeUser_id));
                params.put(APIS.APITokenKEY,
                        Utility.getSharedPreferences(mContext, APIS.APITokenValue));
    
                return params;
            }

        };
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void ShowAlertResponse(String message) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.send_successfully_layout,
                null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();

        TextView title_txt = layout.findViewById(R.id.title_txt);
        TextView OK_txt = layout.findViewById(R.id.OK_txt);

        title_txt.setText(mContext.getResources().getString(R.string.Alert_Send));

        OK_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    public void showProgressDialog(Context context, String message) {
        if (progressDialog == null) progressDialog = new CustomProgressDialog(context, message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) progressDialog.dismiss();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView caregiver_name_txt;
        ImageView caregiver_image;
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
