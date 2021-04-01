package com.example.forestofficerapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;

public class SaveSharedPreference {

    static final String emailKey = "EMAIL";
    static final String passwordKey = "PASSWORD";
    static final String nameKey = "NAME";
    static final String designationKey = "DESIGNATION";
    static final String beatKey = "BEAT";
    static final String rangeKey = "RANGE";
    static final String divisionKey = "PASSWORD";
    static final String employeeIdKey = "EMPLOYEE_ID";
    static final String authTokenKey = "TOKEN";
    static final String forestServiceKey = "FOREST-SERVICE";
    static final String messageKey = "MESSAGE";
    static final String reportNameKey = "REPORT-NAME";
    static final String reportDescriptionKey = "REPORT-DESCRIPTION";
    static final String taskReportKey = "TASK-REPORT";
    static final String reportsSubmittedKey = "REPORT-SUBMITTED";
    static final String tasksSubmittedKey = "TASK-SUBMITTED";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setEmail(Context ctx, String email) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(emailKey, email);
        editor.commit();
    }

    public static void setPassword(Context ctx, String passwd) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(passwordKey, passwd);
        editor.commit();
    }

    public static void setName(Context ctx, String name) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(nameKey, name);
        editor.commit();
    }

    public static void setDesignation(Context ctx, String designation) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(designationKey, designation);
        editor.commit();
    }

    public static void setBeat(Context ctx, String beat) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(beatKey, beat);
        editor.commit();
    }

    public static void setRange(Context ctx, String range) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(rangeKey, range);
        editor.commit();
    }

    public static void setDivision(Context ctx, String division) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(divisionKey, division);
        editor.commit();
    }

    public static void setEmployeeID(Context ctx, String division) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(employeeIdKey, division);
        editor.commit();
    }

    public static void setAuthToken(Context ctx, String token) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(authTokenKey, token);
        editor.commit();
    }

    public static void setReportName(Context ctx, String name) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(reportNameKey, name);
        editor.commit();
    }

    public static void setReportDescription(Context ctx, String description) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(reportDescriptionKey, description);
        editor.commit();
    }

    public static void setTaskReport(Context ctx, String report) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(taskReportKey, report);
        editor.commit();
    }

    public static void setSubmittedReports(Context ctx, int num) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(reportsSubmittedKey, num);
        editor.commit();
    }

    public static void setSubmittedTasks(Context ctx, int num) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(tasksSubmittedKey, num);
        editor.commit();
    }

    public static String getEmail(Context ctx) {
        return getSharedPreferences(ctx).getString(emailKey, "");
    }

    public static String getPassword(Context ctx) {
        return getSharedPreferences(ctx).getString(passwordKey, "");
    }

    public static String getName(Context ctx) {
        return getSharedPreferences(ctx).getString(nameKey, "");
    }

    public static String getDesignation(Context ctx) {
        return getSharedPreferences(ctx).getString(designationKey, "");
    }

    public static String getBeat(Context ctx) {
        return getSharedPreferences(ctx).getString(beatKey, "");
    }

    public static String getRange(Context ctx) {
        return getSharedPreferences(ctx).getString(rangeKey, "");
    }

    public static String getDivision(Context ctx) {
        return getSharedPreferences(ctx).getString(divisionKey, "");
    }

    public static String getEmployeeID(Context ctx) {
        return getSharedPreferences(ctx).getString(employeeIdKey, "");
    }

    public static String getAuthToken(Context ctx) {
        return getSharedPreferences(ctx).getString(authTokenKey, "");
    }

    public static String getReportName(Context ctx) {
        return getSharedPreferences(ctx).getString(reportNameKey, "");
    }

    public static String getReportDescription(Context ctx) {
        return getSharedPreferences(ctx).getString(reportDescriptionKey, "");
    }

    public static String getTaskReport(Context ctx) {
        return getSharedPreferences(ctx).getString(taskReportKey, "");
    }

    public static int getSubmittedReports(Context ctx) {
        return getSharedPreferences(ctx).getInt(reportsSubmittedKey, 0);
    }

    public static int getSubmittedTasks(Context ctx) {
        return getSharedPreferences(ctx).getInt(tasksSubmittedKey, 0);
    }

    public static void clearPreferences(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        editor.commit();
    }

}
