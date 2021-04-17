package com.soultabcaregiver.activity.MainScreen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChartModel {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("ok")
    @Expose
    private Integer ok;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getOk() {
        return ok;
    }

    public void setOk(Integer ok) {
        this.ok = ok;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("lineChart")
        @Expose
        private List<LineChart> lineChart = null;
        @SerializedName("x_label")
        @Expose
        private List<String> xLabel = null;
        @SerializedName("barChart")
        @Expose
        private List<BarChart> barChart = null;

        @SerializedName("device_Data")
        @Expose
        private DeviceData deviceData;
        @SerializedName("compliance")
        @Expose
        private Compliance compliance;

        public DeviceData getDeviceData() {
            return deviceData;
        }

        public void setDeviceData(DeviceData deviceData) {
            this.deviceData = deviceData;
        }

        public List<LineChart> getLineChart() {
            return lineChart;
        }

        public void setLineChart(List<LineChart> lineChart) {
            this.lineChart = lineChart;
        }

        public List<String> getxLabel() {
            return xLabel;
        }

        public void setxLabel(List<String> xLabel) {
            this.xLabel = xLabel;
        }

        public List<BarChart> getBarChart() {
            return barChart;
        }

        public void setBarChart(List<BarChart> barChart) {
            this.barChart = barChart;
        }


        public Compliance getCompliance() {
            return compliance;
        }

        public void setCompliance(Compliance compliance) {
            this.compliance = compliance;
        }

        public class BarChart {

            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("value")
            @Expose
            private Integer value;
            @SerializedName("color_set")
            @Expose
            private String colorSet;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Integer getValue() {
                return value;
            }

            public void setValue(Integer value) {
                this.value = value;
            }

            public String getColorSet() {
                return colorSet;
            }

            public void setColorSet(String colorSet) {
                this.colorSet = colorSet;
            }

        }

        public class LineChart {

            @SerializedName("color_set")
            @Expose
            private String colorSet;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("yaxis")
            @Expose
            private List<String> yaxis = null;


            public String getColorSet() {
                return colorSet;
            }

            public void setColorSet(String colorSet) {
                this.colorSet = colorSet;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<String> getYaxis() {
                return yaxis;
            }

            public void setYaxis(List<String> yaxis) {
                this.yaxis = yaxis;
            }

        }

        public class Compliance {

            @SerializedName("daily")
            @Expose
            private Daily daily = null;
            @SerializedName("weekly")
            @Expose
            private Weekly weekly;
            @SerializedName("monthly")
            @Expose
            private Monthly monthly;

            public Daily getDaily() {
                return daily;
            }

            public void setDaily(Daily daily) {
                this.daily = daily;
            }

            public Weekly getWeekly() {
                return weekly;
            }

            public void setWeekly(Weekly weekly) {
                this.weekly = weekly;
            }

            public Monthly getMonthly() {
                return monthly;
            }

            public void setMonthly(Monthly monthly) {
                this.monthly = monthly;
            }

            public class Daily {

                @SerializedName("count")
                @Expose
                private String count;
                @SerializedName("type")
                @Expose
                private String type;

                public String getCount() {
                    return count;
                }

                public void setCount(String count) {
                    this.count = count;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

            }

            public class Weekly {

                @SerializedName("count")
                @Expose
                private String count;
                @SerializedName("type")
                @Expose
                private String type;

                public String getCount() {
                    return count;
                }

                public void setCount(String count) {
                    this.count = count;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

            }

            public class Monthly {

                @SerializedName("count")
                @Expose
                private String count;
                @SerializedName("type")
                @Expose
                private String type;

                public String getCount() {
                    return count;
                }

                public void setCount(String count) {
                    this.count = count;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

            }
        }

        public class DeviceData {

            @SerializedName("Status")
            @Expose
            private Integer status;
            @SerializedName("device_last_online")
            @Expose
            private String deviceLastOnline;
            @SerializedName("primary_Username")
            @Expose
            private String primaryUsername;

            public Integer getStatus() {
                return status;
            }

            public void setStatus(Integer status) {
                this.status = status;
            }

            public String getDeviceLastOnline() {
                return deviceLastOnline;
            }

            public void setDeviceLastOnline(String deviceLastOnline) {
                this.deviceLastOnline = deviceLastOnline;
            }

            public String getPrimaryUsername() {
                return primaryUsername;
            }

            public void setPrimaryUsername(String primaryUsername) {
                this.primaryUsername = primaryUsername;
            }

        }
    }
}
