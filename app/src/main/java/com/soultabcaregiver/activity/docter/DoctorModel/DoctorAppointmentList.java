package com.soultabcaregiver.activity.docter.DoctorModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DoctorAppointmentList implements Serializable {

    @SerializedName("status_code")
    @Expose
    private Integer statusCode;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("response")
    @Expose
    private Response response;

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

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static class Response implements Serializable {

        @SerializedName("appointment_data")
        @Expose
        private List<AppointmentDatum> appointmentData = null;

        public List<AppointmentDatum> getAppointmentData() {
            return appointmentData;
        }

        public void setAppointmentData(List<AppointmentDatum> appointmentData) {
            this.appointmentData = appointmentData;
        }

        public static class AppointmentDatum implements Serializable {

            @SerializedName("appointment_id")
            @Expose
            private String appointmentId;
            @SerializedName("doctor_id")
            @Expose
            private String doctor_id;


            @SerializedName("doctor_name")
            @Expose
            private String doctorName;
            @SerializedName("doctor_address")
            @Expose
            private String doctorAddress;
            @SerializedName("doctor_mobile")
            @Expose
            private String doctorMobile;
            @SerializedName("Email")
            @Expose
            private String email;
            @SerializedName("Fax")
            @Expose
            private String fax;
            @SerializedName("website")
            @Expose
            private String website;
            @SerializedName("Speciality")
            @Expose
            private String speciality;
            @SerializedName("appointments_reminder")
            @Expose
            private String appointmentsReminder;
            @SerializedName("date")
            @Expose
            private String date;
            @SerializedName("time")
            @Expose
            private String time;

            private boolean isSelected;


            public String getAppointmentId() {
                return appointmentId;
            }

            public void setAppointmentId(String appointmentId) {
                this.appointmentId = appointmentId;
            }

            public String getDoctor_id() {
                return doctor_id;
            }

            public void setDoctor_id(String doctor_id) {
                this.doctor_id = doctor_id;
            }

            public String getDoctorName() {
                return doctorName;
            }

            public void setDoctorName(String doctorName) {
                this.doctorName = doctorName;
            }

            public String getDoctorAddress() {
                return doctorAddress;
            }

            public void setDoctorAddress(String doctorAddress) {
                this.doctorAddress = doctorAddress;
            }

            public String getDoctorMobile() {
                return doctorMobile;
            }

            public void setDoctorMobile(String doctorMobile) {
                this.doctorMobile = doctorMobile;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getFax() {
                return fax;
            }

            public void setFax(String fax) {
                this.fax = fax;
            }

            public String getWebsite() {
                return website;
            }

            public void setWebsite(String website) {
                this.website = website;
            }

            public String getSpeciality() {
                return speciality;
            }

            public void setSpeciality(String speciality) {
                this.speciality = speciality;
            }

            public String getAppointmentsReminder() {
                return appointmentsReminder;
            }

            public void setAppointmentsReminder(String appointmentsReminder) {
                this.appointmentsReminder = appointmentsReminder;
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

            public boolean isCheck() {
                return isSelected;
            }

            public void setCheck(boolean check) {
                this.isSelected = check;
            }


        }
    }
}
