package com.soultabcaregiver.activity.daily_routine.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DailyRoutineModel {

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

    public class Response {

        @SerializedName("daily_routine_data")
        @Expose
        private DailyRoutineData dailyRoutineData;

        public DailyRoutineData getDailyRoutineData() {
            return dailyRoutineData;
        }

        public void setDailyRoutineData(DailyRoutineData dailyRoutineData) {
            this.dailyRoutineData = dailyRoutineData;
        }

        public class DailyRoutineData {

            @SerializedName("morning")
            @Expose
            private List<String> morning = null;
            @SerializedName("noon")
            @Expose
            private List<String> noon = null;
            @SerializedName("evening")
            @Expose
            private List<String> evening = null;
            @SerializedName("dinner")
            @Expose
            private List<String> dinner = null;
            @SerializedName("bedtime")
            @Expose
            private List<String> bedtime = null;

            public List<String> getMorning() {
                return morning;
            }

            public void setMorning(List<String> morning) {
                this.morning = morning;
            }

            public List<String> getNoon() {
                return noon;
            }

            public void setNoon(List<String> noon) {
                this.noon = noon;
            }

            public List<String> getEvening() {
                return evening;
            }

            public void setEvening(List<String> evening) {
                this.evening = evening;
            }

            public List<String> getDinner() {
                return dinner;
            }

            public void setDinner(List<String> dinner) {
                this.dinner = dinner;
            }

            public List<String> getBedtime() {
                return bedtime;
            }

            public void setBedtime(List<String> bedtime) {
                this.bedtime = bedtime;
            }

        }

    }
}
