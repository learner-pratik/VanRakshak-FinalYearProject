package com.example.forestofficerapp;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.crossbar.autobahn.websocket.WebSocketConnection;
import io.crossbar.autobahn.websocket.WebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.exceptions.WebSocketException;
import io.crossbar.autobahn.websocket.types.ConnectionResponse;

public class AnimalService extends Service {

    private static final String SOCKET_URL = "ws://vanrakshak.herokuapp.com/animal_socket";
    private final String LOG_TAG = this.getClass().getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
//        AsyncTask.execute(() -> connectWebSocket());
        return START_STICKY;
    }

    private void connectWebSocket() {

        WebSocketConnection connection = new WebSocketConnection();
        try {
            connection.connect(SOCKET_URL, new WebSocketConnectionHandler() {
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
                    try {
                        JSONObject jsonObject = new JSONObject(payload);
                        JSONArray jsonArray = jsonObject.getJSONArray("animals");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            Log.d(LOG_TAG, "Animal Details");
                            JSONObject object = jsonArray.getJSONObject(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }
}
