package com.soultabcaregiver.activity.Calender.CalenderModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AllEventModel extends CommonResponseModel implements Serializable {

    @SerializedName("response")
    @Expose
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public class Response {

        @SerializedName("Activities")
        @Expose
        private Activities activities;

        public Activities getActivities() {
            return activities;
        }

        public void setActivities(Activities activities) {
            this.activities = activities;
        }
    }

    public class Activities {

        @SerializedName("reminders")
        @Expose
        private List<ReminderBean> reminders = null;
        @SerializedName("Appointments")
        @Expose
        private List<AllAppointmentModel> appointments = null;

        public List<ReminderBean> getReminders() {
            return reminders;
        }

        public void setReminders(List<ReminderBean> reminders) {
            this.reminders = reminders;
        }

        public List<AllAppointmentModel> getAppointments() {
            return appointments;
        }

        public void setAppointments(List<AllAppointmentModel> appointments) {
            this.appointments = appointments;
        }
    }
}
