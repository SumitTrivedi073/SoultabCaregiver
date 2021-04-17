package com.soultabcaregiver.reminder_ring_class.model;

import java.io.Serializable;
import java.util.List;

public class PersonalAlarmModel implements Serializable {
    private List<AlarmSetModel> alarmSetModelList;

    public List<AlarmSetModel> getAlarmSetModelList() {
        return alarmSetModelList;
    }

    public void setAlarmSetModelList(List<AlarmSetModel> alarmSetModelList) {
        this.alarmSetModelList = alarmSetModelList;
    }

}
