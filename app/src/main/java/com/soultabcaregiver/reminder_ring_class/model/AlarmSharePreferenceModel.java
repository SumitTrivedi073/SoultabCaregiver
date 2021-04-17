package com.soultabcaregiver.reminder_ring_class.model;

import java.util.List;

public class AlarmSharePreferenceModel {

    public List<MedicineAlarmModel> getMedicineModelList() {
        return medicineModelList;
    }

    public void setMedicineModelList(List<MedicineAlarmModel> medicineModelList) {
        this.medicineModelList = medicineModelList;
    }

    private List<MedicineAlarmModel> medicineModelList;



}
