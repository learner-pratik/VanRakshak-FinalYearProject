package com.example.locationapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import io.crossbar.autobahn.websocket.WebSocketConnection;
import io.crossbar.autobahn.websocket.WebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.exceptions.WebSocketException;
import io.crossbar.autobahn.websocket.types.ConnectionResponse;

public class LocationService extends Service {

    private WebSocketConnection connection;
    private static final String DEMO_URL = "ws://forestwebsocket.herokuapp.com/test";
    private static final String SOCKET_URL = "ws://vanrakshak.herokuapp.com/animal_socket";
    private final String LOG_TAG = this.getClass().getSimpleName();
    private NotificationManager serviceNotificationManager;
    private NotificationChannel locationServiceChannel;
    private static final String SERVICE_CHANNEL_ID = "SERVICE";
    private static FusedLocationProviderClient fusedLocationClient;
    private static LocationRequest locationRequest;
    private static LocationCallback locationCallback;
    private Context ctx = this;

    @Override
    public void onCreate() {
        super.onCreate();
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        createLocationRequest();
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
//            startMyOwnForeground();
//        else
//            startForeground(1, new Notification());
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        float f = (float) 0.01;
        locationRequest.setSmallestDisplacement(f);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {

        String channelName = "Location Background Service";
        locationServiceChannel = new NotificationChannel(SERVICE_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        locationServiceChannel.setLightColor(Color.BLUE);
        locationServiceChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        serviceNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert serviceNotificationManager != null;
        serviceNotificationManager.createNotificationChannel(locationServiceChannel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, SERVICE_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("LocationApp is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        connectWebSocket();
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                if (locationResult == null) {
//                    return;
//                }
//                for (Location location : locationResult.getLocations()) {
//                    String latitude = String.valueOf(location.getLatitude());
//                    String longitude = String.valueOf(location.getLongitude());
//                    connection.sendMessage("location : "+latitude+" "+longitude);
//                }
//            }
//
//            @Override
//            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
//                super.onLocationAvailability(locationAvailability);
//            }
//        };
//        startLocationUpdates();
        return START_STICKY;
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void connectWebSocket() {

        connection = new WebSocketConnection();
        try {
            connection.connect(SOCKET_URL, new WebSocketConnectionHandler() {
                @Override
                public void onConnect(ConnectionResponse response) {
                    Log.d(LOG_TAG, "Connected to server");
                }

                @Override
                public void onOpen() {
                    JSONObject jsonObject = new JSONObject();
                    String latitude, longitude;
                    if (MainActivity.currentLocation==null) {
                        latitude = "19.725894548";
                        longitude = "72.321456237";
                    } else {
                        latitude = String.valueOf(MainActivity.currentLocation.getLatitude());
                        longitude = String.valueOf(MainActivity.currentLocation.getLongitude());
                    }
                    Log.d(LOG_TAG, latitude);
                    Log.d(LOG_TAG, longitude);
                    try {
                        jsonObject.put("animal", SaveSharedPreferences.getAnimal(ctx).toLowerCase());
                        jsonObject.put("id", "id-1");
                        jsonObject.put("latitude", latitude);
                        jsonObject.put("longitude", longitude);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    connection.sendMessage(jsonObject.toString());
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
//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction("RestartService");
//        broadcastIntent.setClass(this, LocationReceiver.class);
//        this.sendBroadcast(broadcastIntent);
    }
}
