package com.soultabcaregiver.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginModel {
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

    public static class Response {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("caregiver_id")
        @Expose
        private String caregiver_id;
        @SerializedName("lastname")
        @Expose
        private String lastname;
        @SerializedName("dob")
        @Expose
        private String dob;
        @SerializedName("email")
        @Expose
        private String email;


        @SerializedName("user_email_address")
        @Expose
        private String user_email_address;

        @SerializedName("Otp_verification")
        @Expose
        private String otpVerification;
        @SerializedName("mobile")
        @Expose
        private String mobile;
        @SerializedName("device_type")
        @Expose
        private String deviceType;
        @SerializedName("user_verification")
        @Expose
        private String userVerification;
        @SerializedName("token_verification")
        @Expose
        private String tokenVerification;
        @SerializedName("profile_image")
        @Expose
        private String profileImage;

        @SerializedName("parent_user")
        @Expose
        private String parent_user;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCaregiver_id() {
            return caregiver_id;
        }

        public void setCaregiver_id(String caregiver_id) {
            this.caregiver_id = caregiver_id;
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUser_email_address() {
            return user_email_address;
        }

        public void setUser_email_address(String user_email_address) {
            this.user_email_address = user_email_address;
        }

        public String getOtpVerification() {
            return otpVerification;
        }

        public void setOtpVerification(String otpVerification) {
            this.otpVerification = otpVerification;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getUserVerification() {
            return userVerification;
        }

        public void setUserVerification(String userVerification) {
            this.userVerification = userVerification;
        }

        public String getTokenVerification() {
            return tokenVerification;
        }

        public void setTokenVerification(String tokenVerification) {
            this.tokenVerification = tokenVerification;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getParent_user() {
            return parent_user;
        }

        public void setParent_user(String parent_user) {
            this.parent_user = parent_user;
        }

    }
}
