package com.soultabcaregiver.activity.calender.CalenderModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AllAppointmentModel implements Serializable {
    @SerializedName("appointment_id")
    @Expose
    private String id;
    @SerializedName("selected_date")
    @Expose
    private String selectedDate;
    @SerializedName("schedule_time")
    @Expose
    private String scheduleTime;


    @SerializedName("doctor_id")
    @Expose
    private String doctor_id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("doctorName")
    @Expose
    private String doctorName;
    @SerializedName("doctoraddress")
    @Expose
    private String doctoraddress;
    @SerializedName("appointments_reminder")
    @Expose
    private String reminder;

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }


    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctoraddress() {
        return doctoraddress;
    }

    public void setDoctoraddress(String doctoraddress) {
        this.doctoraddress = doctoraddress;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

}
