package com.example.forestofficerapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class InternetConnection {

    private static final String LOG_TAG = "InternetConnection";
    private static Context context;
    private static boolean internetStatus = false;

    private static void isInternetPresentOnNetwork() {
        try {
            HttpURLConnection urlc = (HttpURLConnection)
                    (new URL("https://clients3.google.com/generate_204")
                            .openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            internetStatus = (urlc.getResponseCode() == 204 &&
                    urlc.getContentLength() == 0);
            Log.d(LOG_TAG, "The internet status is "+internetStatus);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error checking internet connection", e);
        }
    }

    private static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static boolean checkInternetStatus(Context appContext) {
//        context = appContext;
//        if (isNetworkAvailable()) {
//            isInternetPresentOnNetwork();
//        } else {
//            internetStatus = false;
//        }
//        return internetStatus;
        return true;
    }
}
