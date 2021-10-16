package com.soultabcaregiver.WebService;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soultabcaregiver.utils.AppController;
import com.soultabcaregiver.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public class ApiTokenAuthentication {

    static String UpdatedToken;
    private final String TAG = getClass().getSimpleName();

    public static void refrehToken(Context context, AuthenticateHandler handler) {


       
        String userId = Utility.getSharedPreferences(context,APIS.caregiver_id);

        if (!TextUtils.isEmpty(userId)) {

            JSONObject jsonObject = new JSONObject();
            try {
                
                jsonObject.put("refresh_token", Utility.getSharedPreferences(context,
                        APIS.RefressTokenValue));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    APIS.BASEURL + APIS.refreshToken, jsonObject, response -> {
                Log.e("API response", response.toString());
                try {
                    String code = response.getString("status_code");
                    Log.e("response=", code);
                    if (code.equals("200")) {

                        JSONObject mainObject = response.getJSONObject("response");

                        UpdatedToken = mainObject.optString("jwt_token");
                        Log.e("UpdatedToken", UpdatedToken);
                        Utility.setSharedPreference(context, APIS.APITokenValue, UpdatedToken);
                        if (handler != null) {
                            handler.onResult(UpdatedToken);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (handler != null) {
                        handler.onResult(null);
                    }
                }

            }, Throwable::printStackTrace) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(APIS.HEADERKEY, APIS.HEADERVALUE);
                    params.put(APIS.HEADERKEY1, APIS.HEADERVALUE1);
                    return params;
                }

            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq);
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    10000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } else {
            if (handler != null) {
                handler.onResult(null);
            }
        }
    }


    public interface AuthenticateHandler {

        void onResult(@Nullable String userId);
    }
}
