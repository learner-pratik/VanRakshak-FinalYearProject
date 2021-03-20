package com.example.forestofficerapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ForestReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Boolean status = InternetConnection.checkInternetStatus(context);
        if (!status) {
            Toast.makeText(context, "NO INTERNET CONNECTION", Toast.LENGTH_LONG).show();
        }
    }
}
