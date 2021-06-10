package com.soultabcaregiver.activity.docter.DoctorModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DoctorListModel implements Serializable {
    @SerializedName("status_code")
    @Expose
    private Integer statusCode;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("pages")
    @Expose
    private Integer pages;
    @SerializedName("per_page_records")
    @Expose
    private Integer perPageRecords;
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

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Integer getPerPageRecords() {
        return perPageRecords;
    }

    public void setPerPageRecords(Integer perPageRecords) {
        this.perPageRecords = perPageRecords;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static class Response implements Serializable {

        @SerializedName("doctor_data")
        @Expose
        private List<DoctorDatum> doctorData = null;

        public List<DoctorDatum> getDoctorData() {
            return doctorData;
        }

        public void setDoctorData(List<DoctorDatum> doctorData) {
            this.doctorData = doctorData;
        }

        public static class DoctorDatum implements Serializable {

            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("fees")
            @Expose
            private Object fees;
            @SerializedName("address")
            @Expose
            private String address;
            @SerializedName("contact")
            @Expose
            private String contact;
            @SerializedName("image")
            @Expose
            private String image;
            @SerializedName("doctor_image")
            @Expose
            private String doctorImage;
            @SerializedName("doctor_email")
            @Expose
            private String doctorEmail;
            @SerializedName("website")
            @Expose
            private String portal;
            @SerializedName("fax_num")
            @Expose
            private String faxNum;



            @SerializedName("favorite")
            @Expose
            private String favorite;

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

            public Object getFees() {
                return fees;
            }

            public void setFees(Object fees) {
                this.fees = fees;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getContact() {
                return contact;
            }

            public void setContact(String contact) {
                this.contact = contact;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getDoctorImage() {
                return doctorImage;
            }

            public void setDoctorImage(String doctorImage) {
                this.doctorImage = doctorImage;
            }

            public String getDoctorEmail() {
                return doctorEmail;
            }

            public void setDoctorEmail(String doctorEmail) {
                this.doctorEmail = doctorEmail;
            }

            public String getPortal() {
                return portal;
            }

            public void setPortal(String portal) {
                this.portal = portal;
            }

            public String getFaxNum() {
                return faxNum;
            }

            public void setFaxNum(String faxNum) {
                this.faxNum = faxNum;
            }

            public String getFavorite() {
                return favorite;
            }

            public void setFavorite(String favorite) {
                this.favorite = favorite;
            }
        }
    }
}
