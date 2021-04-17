package com.soultabcaregiver.activity.docter.DoctorModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DoctorCategoryModel implements Serializable {
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

        @SerializedName("category_data")
        @Expose
        private List<CategoryDatum> categoryData = null;

        public List<CategoryDatum> getCategoryData() {
            return categoryData;
        }

        public void setCategoryData(List<CategoryDatum> categoryData) {
            this.categoryData = categoryData;
        }

        public static class CategoryDatum implements Serializable {

            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("icon")
            @Expose
            private String icon;

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

        }
    }
}
