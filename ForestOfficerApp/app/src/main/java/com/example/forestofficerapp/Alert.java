package com.example.forestofficerapp;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;

public class Alert {

    private String alertName, alertType;
    private Date alertDate;
    private LocalTime alertTime;
    private Double alertLatitude, alertLongitude;

    public Alert(){}

    public Alert(String alertName, String alertType, Date alertDate, LocalTime alertTime, Double alertLatitude, Double alertLongitude) {
        this.alertName = alertName;
        this.alertType = alertType;
        this.alertDate = alertDate;
        this.alertTime = alertTime;
        this.alertLatitude = alertLatitude;
        this.alertLongitude = alertLongitude;
    }

    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public Date getAlertDate() {
        return alertDate;
    }

    public void setAlertDate(Date alertDate) {
        this.alertDate = alertDate;
    }

    public LocalTime getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(LocalTime alertTime) {
        this.alertTime = alertTime;
    }

    public Double getAlertLatitude() {
        return alertLatitude;
    }

    public void setAlertLatitude(Double alertLatitude) {
        this.alertLatitude = alertLatitude;
    }

    public Double getAlertLongitude() {
        return alertLongitude;
    }

    public void setAlertLongitude(Double alertLongitude) {
        this.alertLongitude = alertLongitude;
    }
}
