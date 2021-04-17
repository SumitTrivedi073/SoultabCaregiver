package com.soultabcaregiver.reminder_ring_class.model;

import java.io.Serializable;

public class AlarmModel implements Serializable {
    private int alarmId;
    private String mTitle;
    private long mDate;
    private String actualTime;
    private String actualDate;
    private String alarmType;
    private String alarmDescription;
    private String alarmFrom;
    private String itemId;
    private String snooze;
    private String ringing_time;
    private int repeting_time;

    public int getRepeting_time() {
        return repeting_time;
    }

    public void setRepeting_time(int repeting_time) {
        this.repeting_time = repeting_time;
    }

    public String getRinging_time() {
        return ringing_time;
    }

    public void setRinging_time(String ringing_time) {
        this.ringing_time = ringing_time;
    }

    public String getSnooze() {
        return snooze;
    }

    public void setSnooze(String snooze) {
        this.snooze = snooze;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }


    public String getAlarmFrom() {
        return alarmFrom;
    }

    public void setAlarmFrom(String alarmFrom) {
        this.alarmFrom = alarmFrom;
    }


    public String getAlarmDescription() {
        return alarmDescription;
    }

    public void setAlarmDescription(String alarmDescription) {
        this.alarmDescription = alarmDescription;
    }


    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }


    public String getActualDate() {
        return actualDate;
    }

    public void setActualDate(String actualDate) {
        this.actualDate = actualDate;
    }


    public String getActualTime() {
        return actualTime;
    }

    public void setActualTime(String actualTime) {
        this.actualTime = actualTime;
    }


    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public long getmDate() {
        return mDate;
    }

    public void setmDate(long mDate) {
        this.mDate = mDate;
    }
}
