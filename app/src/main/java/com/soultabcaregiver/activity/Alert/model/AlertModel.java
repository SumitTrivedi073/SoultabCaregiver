package com.soultabcaregiver.activity.Alert.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AlertModel {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status_code")
    @Expose
    private Integer statusCode;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("caregiver_data")
        @Expose
        private List<CaregiverDatum> caregiverData = null;

        public List<CaregiverDatum> getCaregiverData() {
            return caregiverData;
        }

        public void setCaregiverData(List<CaregiverDatum> caregiverData) {
            this.caregiverData = caregiverData;
        }


        public class CaregiverDatum {

            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("icon")
            @Expose
            private String icon;
            @SerializedName("message_data")
            @Expose
            private List<MessageDatum> messageData = null;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public List<MessageDatum> getMessageData() {
                return messageData;
            }

            public void setMessageData(List<MessageDatum> messageData) {
                this.messageData = messageData;
            }


            public class MessageDatum {

                @SerializedName("message")
                @Expose
                private String message;
                @SerializedName("read_flag")
                @Expose
                private String readFlag;
                @SerializedName("date_time")
                @Expose
                private String dateTime;

                public String getMessage() {
                    return message;
                }

                public void setMessage(String message) {
                    this.message = message;
                }

                public String getReadFlag() {
                    return readFlag;
                }

                public void setReadFlag(String readFlag) {
                    this.readFlag = readFlag;
                }

                public String getDateTime() {
                    return dateTime;
                }

                public void setDateTime(String dateTime) {
                    this.dateTime = dateTime;
                }
            }

        }
    }
}