package com.example.locationapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreferences {

    static final String animalKey = "ANIMAL";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setAnimal(Context ctx, String animal) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(animalKey, animal);
        editor.commit();
    }

    public static String getAnimal(Context ctx) {
        return getSharedPreferences(ctx).getString(animalKey, "");
    }
}
