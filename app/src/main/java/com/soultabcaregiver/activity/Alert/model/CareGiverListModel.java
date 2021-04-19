package com.soultabcaregiver.activity.Alert.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CareGiverListModel implements Serializable {
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
    private List<Response> response = null;

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

    public List<Response> getResponse() {
        return response;
    }

    public void setResponse(List<Response> response) {
        this.response = response;
    }

    public static class Response implements Serializable {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("cp_users_id")
        @Expose
        private String cpUsersId;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("lastname")
        @Expose
        private String lastname;
        @SerializedName("dob")
        @Expose
        private String dob;
        @SerializedName("username")
        @Expose
        private String username;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("state")
        @Expose
        private String state;
        @SerializedName("zipcode")
        @Expose
        private String zipcode;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("language")
        @Expose
        private String language;
        @SerializedName("company_school")
        @Expose
        private Object companySchool;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("mobile")
        @Expose
        private String mobile;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("profile_image")
        @Expose
        private String profileImage;
        @SerializedName("device_id")
        @Expose
        private String deviceId;
        @SerializedName("device_type")
        @Expose
        private String deviceType;
        @SerializedName("device_token")
        @Expose
        private String deviceToken;
        @SerializedName("user_role")
        @Expose
        private String userRole;
        @SerializedName("apartment")
        @Expose
        private Object apartment;
        @SerializedName("street_name")
        @Expose
        private String streetName;
        @SerializedName("act_link")
        @Expose
        private String actLink;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("conversation_status")
        @Expose
        private String conversationStatus;
        @SerializedName("user_verify_by_app")
        @Expose
        private String userVerifyByApp;
        @SerializedName("device_verify")
        @Expose
        private String deviceVerify;
        @SerializedName("device_verify_by_token")
        @Expose
        private String deviceVerifyByToken;
        @SerializedName("create_user")
        @Expose
        private String createUser;
        @SerializedName("update_user")
        @Expose
        private String updateUser;
        @SerializedName("otp")
        @Expose
        private String otp;
        @SerializedName("otp_expire")
        @Expose
        private String otpExpire;
        @SerializedName("country_code")
        @Expose
        private String countryCode;
        @SerializedName("mobile_with_code")
        @Expose
        private String mobileWithCode;
        @SerializedName("gender")
        @Expose
        private String gender;
        @SerializedName("address")
        @Expose
        private Object address;
        @SerializedName("user_id")
        @Expose
        private Object userId;
        @SerializedName("timezone")
        @Expose
        private String timezone;
        @SerializedName("snt_txt_msg_alrt")
        @Expose
        private String sntTxtMsgAlrt;
        @SerializedName("snt_eml_alrt")
        @Expose
        private String sntEmlAlrt;
        @SerializedName("call_me_alrt")
        @Expose
        private Object callMeAlrt;
        @SerializedName("snt_daily_eml_daily_rutin_alrt")
        @Expose
        private String sntDailyEmlDailyRutinAlrt;
        @SerializedName("snt_eml_no_activity_in_app_alrt")
        @Expose
        private String sntEmlNoActivityInAppAlrt;


        @SerializedName("snt_eml_alrm_is_missed")
        @Expose
        private String snt_eml_alrm_is_missed;



        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCpUsersId() {
            return cpUsersId;
        }

        public void setCpUsersId(String cpUsersId) {
            this.cpUsersId = cpUsersId;
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

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZipcode() {
            return zipcode;
        }

        public void setZipcode(String zipcode) {
            this.zipcode = zipcode;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public Object getCompanySchool() {
            return companySchool;
        }

        public void setCompanySchool(Object companySchool) {
            this.companySchool = companySchool;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(String deviceToken) {
            this.deviceToken = deviceToken;
        }

        public String getUserRole() {
            return userRole;
        }

        public void setUserRole(String userRole) {
            this.userRole = userRole;
        }

        public Object getApartment() {
            return apartment;
        }

        public void setApartment(Object apartment) {
            this.apartment = apartment;
        }

        public String getStreetName() {
            return streetName;
        }

        public void setStreetName(String streetName) {
            this.streetName = streetName;
        }

        public String getActLink() {
            return actLink;
        }

        public void setActLink(String actLink) {
            this.actLink = actLink;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getConversationStatus() {
            return conversationStatus;
        }

        public void setConversationStatus(String conversationStatus) {
            this.conversationStatus = conversationStatus;
        }

        public String getUserVerifyByApp() {
            return userVerifyByApp;
        }

        public void setUserVerifyByApp(String userVerifyByApp) {
            this.userVerifyByApp = userVerifyByApp;
        }

        public String getDeviceVerify() {
            return deviceVerify;
        }

        public void setDeviceVerify(String deviceVerify) {
            this.deviceVerify = deviceVerify;
        }

        public String getDeviceVerifyByToken() {
            return deviceVerifyByToken;
        }

        public void setDeviceVerifyByToken(String deviceVerifyByToken) {
            this.deviceVerifyByToken = deviceVerifyByToken;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public String getUpdateUser() {
            return updateUser;
        }

        public void setUpdateUser(String updateUser) {
            this.updateUser = updateUser;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public String getOtpExpire() {
            return otpExpire;
        }

        public void setOtpExpire(String otpExpire) {
            this.otpExpire = otpExpire;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getMobileWithCode() {
            return mobileWithCode;
        }

        public void setMobileWithCode(String mobileWithCode) {
            this.mobileWithCode = mobileWithCode;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public Object getAddress() {
            return address;
        }

        public void setAddress(Object address) {
            this.address = address;
        }

        public Object getUserId() {
            return userId;
        }

        public void setUserId(Object userId) {
            this.userId = userId;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getSntTxtMsgAlrt() {
            return sntTxtMsgAlrt;
        }

        public void setSntTxtMsgAlrt(String sntTxtMsgAlrt) {
            this.sntTxtMsgAlrt = sntTxtMsgAlrt;
        }

        public String getSntEmlAlrt() {
            return sntEmlAlrt;
        }

        public void setSntEmlAlrt(String sntEmlAlrt) {
            this.sntEmlAlrt = sntEmlAlrt;
        }

        public Object getCallMeAlrt() {
            return callMeAlrt;
        }

        public void setCallMeAlrt(Object callMeAlrt) {
            this.callMeAlrt = callMeAlrt;
        }

        public String getSntDailyEmlDailyRutinAlrt() {
            return sntDailyEmlDailyRutinAlrt;
        }

        public void setSntDailyEmlDailyRutinAlrt(String sntDailyEmlDailyRutinAlrt) {
            this.sntDailyEmlDailyRutinAlrt = sntDailyEmlDailyRutinAlrt;
        }

        public String getSntEmlNoActivityInAppAlrt() {
            return sntEmlNoActivityInAppAlrt;
        }

        public void setSntEmlNoActivityInAppAlrt(String sntEmlNoActivityInAppAlrt) {
            this.sntEmlNoActivityInAppAlrt = sntEmlNoActivityInAppAlrt;
        }

        public String getSnt_eml_alrm_is_missed() {
            return snt_eml_alrm_is_missed;
        }

        public void setSnt_eml_alrm_is_missed(String snt_eml_alrm_is_missed) {
            this.snt_eml_alrm_is_missed = snt_eml_alrm_is_missed;
        }

    }
}
