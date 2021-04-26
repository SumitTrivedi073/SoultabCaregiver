package com.soultabcaregiver.activity.calender.CalenderModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ReminderBean implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("actual_time")
    @Expose
    private String time;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("reminder_before")
    @Expose
    private String reminderBefore;
    @SerializedName("repeat")
    @Expose
    private String repeat;


    @SerializedName("snooze")
    @Expose
    private String snooze;


    @SerializedName("time")
    @Expose
    private ArrayList<String> timearraylist;

    public ArrayList<String> getTimearraylist() {
        return timearraylist;
    }

    public void setTimearraylist(ArrayList<String> timearraylist) {
        this.timearraylist = timearraylist;
    }

    @SerializedName("after_time")
    @Expose
    private String after_time;

    public String getSnooze() {
        return snooze;
    }

    public void setSnooze(String snooze) {
        this.snooze = snooze;
    }

    public String getAfter_time() {
        return after_time;
    }

    public void setAfter_time(String after_time) {
        this.after_time = after_time;
    }

    public String getFor_time() {
        return for_time;
    }

    public void setFor_time(String for_time) {
        this.for_time = for_time;
    }

    @SerializedName("for_time")
    @Expose
    private String for_time;

    private boolean check;
    private boolean isAppointment = false;

    @SerializedName("doctoraddress")
    @Expose
    private String doctoraddress;


    @SerializedName("reminder")
    private String reminder;

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReminderBefore() {
        return reminderBefore;
    }

    public void setReminderBefore(String reminderBefore) {
        this.reminderBefore = reminderBefore;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }


    public boolean isAppointment() {
        return isAppointment;
    }

    public void setAppointment(boolean appointment) {
        isAppointment = appointment;
    }


    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

}
