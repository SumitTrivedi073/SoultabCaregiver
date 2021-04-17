package com.soultabcaregiver.activity.docter.DoctorModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentRequestModel {
    @SerializedName("status_code")
    @Expose
    private Integer statusCode;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    private String message;

    @SerializedName("ok")
    private String ok;

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }


    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
