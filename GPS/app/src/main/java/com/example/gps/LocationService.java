package com.example.gps;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import io.crossbar.autobahn.websocket.WebSocketConnection;
import io.crossbar.autobahn.websocket.WebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.exceptions.WebSocketException;
import io.crossbar.autobahn.websocket.types.ConnectionResponse;

public class LocationService extends Service {

    public static WebSocketConnection connection;
    private final String LOG_TAG = this.getClass().getSimpleName();
    private static final String DEMO_URL = "ws://forestwebsocket.herokuapp.com/test";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startWebSocket();
        return START_STICKY;
    }

    private void startWebSocket() {

        connection = new WebSocketConnection();
        try {
            connection.connect(DEMO_URL, new WebSocketConnectionHandler() {
                @Override
                public void onConnect(ConnectionResponse response) {
                    Log.d(LOG_TAG, "Connected to server");
                }

                @Override
                public void onOpen() {
//                    connection.sendMessage(SaveSharedPreference.getAuthToken(ctx));
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(LOG_TAG, "Connection closed");
                }

                @Override
                public void onMessage(String payload) {
                    Log.d(LOG_TAG, "Message from Server: "+payload);
                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connection.sendClose();
    }
}
