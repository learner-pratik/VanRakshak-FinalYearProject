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

    public static void clearPreferences(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        editor.commit();
    }

}
