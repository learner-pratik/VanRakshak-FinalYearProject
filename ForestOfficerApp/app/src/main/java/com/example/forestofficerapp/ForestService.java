package com.example.forestofficerapp;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class ForestService extends Service {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private WebSocketClient webSocketClient;
    public static Boolean isForestServiceRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        isForestServiceRunning = true;
        connectWebSocket();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void connectWebSocket() {
        URI uri;

        try {
            uri = new URI("ws://forestwebsocket.herokuapp.com/test");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("WebSocket", "opened");
                webSocketClient.send("Hello from "+ Build.MANUFACTURER+" "+Build.MODEL);
            }

            @Override
            public void onMessage(String message) {

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("WebSocket", "Closed " + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.i("WebSocket", "Error "+ex.getMessage());
            }
        };
        webSocketClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isForestServiceRunning = false;
    }
}
