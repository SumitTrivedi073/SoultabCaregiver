package com.soultabcaregiver.reminder_ring_class.model;

import java.util.List;

public class MedicineAlarmModel {
    private String medicineID;
    private List<AlarmSetModel> alarmSetModelList;


    public String getMedicineID() {
        return medicineID;
    }

    public void setMedicineID(String medicineID) {
        this.medicineID = medicineID;
    }

    public List<AlarmSetModel> getAlarmSetModelList() {
        return alarmSetModelList;
    }

    public void setAlarmSetModelList(List<AlarmSetModel> alarmSetModelList) {
        this.alarmSetModelList = alarmSetModelList;
    }
}
