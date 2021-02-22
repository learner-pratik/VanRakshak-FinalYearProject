package com.example.forestofficerapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class InternetConnection extends AsyncTask<Void, Void, Void> {

    private Context context;
    private WeakReference<Context> contextRef;
    private final String LOG_TAG = InternetConnection.this.getClass().getSimpleName();
    private boolean internetStatus;

    public InternetConnection(Context context) {
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                internetStatus = (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error checking internet connection", e);
            }
        } else {
            Log.d(LOG_TAG, "No network available!");
            internetStatus = false;
        }

        return null;
    }

    private boolean isNetworkAvailable() {
        context = contextRef.get();
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public boolean getInternetStatus() {
        return internetStatus;
    }
}
