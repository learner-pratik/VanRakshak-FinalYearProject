package com.example.sosapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    static final String firstNameKey = "FIRST-NAME";
    static final String lastNameKey = "LAST-NAME";
    static final String addressKey = "ADDRESS";
    static final String phoneNumberKey = "PHONE-NUMBER";
    static final String reportsKey = "REPORTS";
    static final String alertsKey = "ALERTS";
    static final String reportNameKey = "REPORT-NAME";
    static final String reportTypeKey = "REPORT-TYPE";
    static final String reportDescriptionKey = "REPORT-DESCRIPTION";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setFirstName(Context ctx, String firstName) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(firstNameKey, firstName);
        editor.commit();
    }

    public static void setLastName(Context ctx, String lastName) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(lastNameKey, lastName);
        editor.commit();
    }

    public static void setAddress(Context ctx, String address) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(addressKey, address);
        editor.commit();
    }

    public static void setPhoneNumber(Context ctx, String phoneNumber) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(phoneNumberKey, phoneNumber);
        editor.commit();
    }

    public static void setSubmittedReports(Context ctx, String reports) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(reportsKey, reports);
        editor.commit();
    }

    public static void setSosAlerts(Context ctx, String alerts) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(alertsKey, alerts);
        editor.commit();
    }

    public static void setReportName(Context ctx, String name) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(reportNameKey, name);
        editor.commit();
    }

    public static void setReportType(Context ctx, String type) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(reportTypeKey, type);
        editor.commit();
    }

    public static void setReportDescription(Context ctx, String description) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(reportDescriptionKey, description);
        editor.commit();
    }

    public static String getFirstName(Context ctx) {
        return getSharedPreferences(ctx).getString(firstNameKey, "");
    }

    public static String getLastName(Context ctx) {
        return getSharedPreferences(ctx).getString(lastNameKey, "");
    }

    public static String getAddress(Context ctx) {
        return getSharedPreferences(ctx).getString(addressKey, "");
    }

    public static String getPhoneNumber(Context ctx) {
        return getSharedPreferences(ctx).getString(phoneNumberKey, "");
    }

    public static String getSubmittedReports(Context ctx) {
        return getSharedPreferences(ctx).getString(reportsKey, "");
    }

    public static String getSosAlerts(Context ctx) {
        return getSharedPreferences(ctx).getString(alertsKey, "");
    }

    public static String getReportName(Context ctx) {
        return getSharedPreferences(ctx).getString(reportNameKey, "");
    }

    public static String getReportType(Context ctx) {
        return getSharedPreferences(ctx).getString(reportTypeKey, "");
    }

    public static String getReportDescription(Context ctx) {
        return getSharedPreferences(ctx).getString(reportDescriptionKey, "");
    }

}
