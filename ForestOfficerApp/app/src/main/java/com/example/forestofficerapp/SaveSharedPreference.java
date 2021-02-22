package com.example.forestofficerapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    static final String emailKey = "EMAIL";
    static final String passwordKey = "PASSWORD";

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

    public static String getEmail(Context ctx) {
        return getSharedPreferences(ctx).getString(emailKey, "");
    }

    public static String getPassword(Context ctx) {
        return getSharedPreferences(ctx).getString(passwordKey, "");
    }

}
