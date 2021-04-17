package com.soultabcaregiver.activity.reminder.model;

public class BeforeTimeModel {
    private String timeName;
    private boolean isSelection;
    private int timeValue;

    public String getTimeName() {
        return timeName;
    }

    public void setTimeName(String timeName) {
        this.timeName = timeName;
    }

    public boolean isSelection() {
        return isSelection;
    }

    public void setSelection(boolean selection) {
        isSelection = selection;
    }

    public int getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(int timeValue) {
        this.timeValue = timeValue;
    }


}
