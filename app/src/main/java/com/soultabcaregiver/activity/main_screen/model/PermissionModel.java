package com.soultabcaregiver.activity.main_screen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PermissionModel {
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
    private String response;
    @SerializedName("permission")
    @Expose
    private Permission permission;

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

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public class Permission {

        @SerializedName("social_dashboard")
        @Expose
        private String socialDashboard;
        @SerializedName("transportation_dashboard")
        @Expose
        private String transportationDashboard;
        @SerializedName("email_dashboard")
        @Expose
        private String emailDashboard;
        @SerializedName("yoga")
        @Expose
        private String yoga;
        @SerializedName("yoga_news")
        @Expose
        private String yogaNews;
        @SerializedName("spirituality")
        @Expose
        private String spirituality;
        @SerializedName("spirituality_news")
        @Expose
        private String spiritualityNews;
        @SerializedName("show_activities")
        @Expose
        private String showActivities;
        @SerializedName("dailyroutine")
        @Expose
        private String dailyroutine;
        @SerializedName("dashboard_new")
        @Expose
        private String dashboardNew;
        @SerializedName("talk")
        @Expose
        private String talk;
        @SerializedName("notes")
        @Expose
        private String notes;
        @SerializedName("appointment_list")
        @Expose
        private String appointmentList;
        @SerializedName("vital_signs")
        @Expose
        private String vitalSigns;
        @SerializedName("testreport_list")
        @Expose
        private String testreportList;
        @SerializedName("medicine_list")
        @Expose
        private String medicineList;
        @SerializedName("pharmacy_dashboard")
        @Expose
        private String pharmacyDashboard;
        @SerializedName("product_category")
        @Expose
        private String productCategory;
        @SerializedName("weather")
        @Expose
        private String weather;
        @SerializedName("music")
        @Expose
        private String music;
        @SerializedName("dashboard_game")
        @Expose
        private String dashboardGame;
        @SerializedName("user_photos")
        @Expose
        private String userPhotos;
        @SerializedName("camera")
        @Expose
        private String camera;
        @SerializedName("movie")
        @Expose
        private String movie;
        @SerializedName("internet")
        @Expose
        private String internet;
        @SerializedName("news")
        @Expose
        private String news;

        public String getSocialDashboard() {
            return socialDashboard;
        }

        public void setSocialDashboard(String socialDashboard) {
            this.socialDashboard = socialDashboard;
        }

        public String getTransportationDashboard() {
            return transportationDashboard;
        }

        public void setTransportationDashboard(String transportationDashboard) {
            this.transportationDashboard = transportationDashboard;
        }

        public String getEmailDashboard() {
            return emailDashboard;
        }

        public void setEmailDashboard(String emailDashboard) {
            this.emailDashboard = emailDashboard;
        }

        public String getYoga() {
            return yoga;
        }

        public void setYoga(String yoga) {
            this.yoga = yoga;
        }

        public String getYogaNews() {
            return yogaNews;
        }

        public void setYogaNews(String yogaNews) {
            this.yogaNews = yogaNews;
        }

        public String getSpirituality() {
            return spirituality;
        }

        public void setSpirituality(String spirituality) {
            this.spirituality = spirituality;
        }

        public String getSpiritualityNews() {
            return spiritualityNews;
        }

        public void setSpiritualityNews(String spiritualityNews) {
            this.spiritualityNews = spiritualityNews;
        }

        public String getShowActivities() {
            return showActivities;
        }

        public void setShowActivities(String showActivities) {
            this.showActivities = showActivities;
        }

        public String getDailyroutine() {
            return dailyroutine;
        }

        public void setDailyroutine(String dailyroutine) {
            this.dailyroutine = dailyroutine;
        }

        public String getDashboardNew() {
            return dashboardNew;
        }

        public void setDashboardNew(String dashboardNew) {
            this.dashboardNew = dashboardNew;
        }

        public String getTalk() {
            return talk;
        }

        public void setTalk(String talk) {
            this.talk = talk;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getAppointmentList() {
            return appointmentList;
        }

        public void setAppointmentList(String appointmentList) {
            this.appointmentList = appointmentList;
        }

        public String getVitalSigns() {
            return vitalSigns;
        }

        public void setVitalSigns(String vitalSigns) {
            this.vitalSigns = vitalSigns;
        }

        public String getTestreportList() {
            return testreportList;
        }

        public void setTestreportList(String testreportList) {
            this.testreportList = testreportList;
        }

        public String getMedicineList() {
            return medicineList;
        }

        public void setMedicineList(String medicineList) {
            this.medicineList = medicineList;
        }

        public String getPharmacyDashboard() {
            return pharmacyDashboard;
        }

        public void setPharmacyDashboard(String pharmacyDashboard) {
            this.pharmacyDashboard = pharmacyDashboard;
        }

        public String getProductCategory() {
            return productCategory;
        }

        public void setProductCategory(String productCategory) {
            this.productCategory = productCategory;
        }

        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public String getMusic() {
            return music;
        }

        public void setMusic(String music) {
            this.music = music;
        }

        public String getDashboardGame() {
            return dashboardGame;
        }

        public void setDashboardGame(String dashboardGame) {
            this.dashboardGame = dashboardGame;
        }

        public String getUserPhotos() {
            return userPhotos;
        }

        public void setUserPhotos(String userPhotos) {
            this.userPhotos = userPhotos;
        }

        public String getCamera() {
            return camera;
        }

        public void setCamera(String camera) {
            this.camera = camera;
        }

        public String getMovie() {
            return movie;
        }

        public void setMovie(String movie) {
            this.movie = movie;
        }

        public String getInternet() {
            return internet;
        }

        public void setInternet(String internet) {
            this.internet = internet;
        }

        public String getNews() {
            return news;
        }

        public void setNews(String news) {
            this.news = news;
        }

    }
}
