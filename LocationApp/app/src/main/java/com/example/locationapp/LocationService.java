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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.Style;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.crossbar.autobahn.websocket.WebSocketConnection;
import io.crossbar.autobahn.websocket.WebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.exceptions.WebSocketException;
import io.crossbar.autobahn.websocket.types.ConnectionResponse;

public class LocationService extends Service {

    private WebSocketConnection connection;
    private static final String DEMO_URL = "ws://forestwebsocket.herokuapp.com/test";
    private static final String SOCKET_URL = "ws://vanrakshak.herokuapp.com/animal_socket";
    private final String LOG_TAG = this.getClass().getSimpleName();
    private NotificationManager serviceNotificationManager, locationNotificationManager;
    private NotificationChannel locationServiceChannel, locationChannel;
    private static final String SERVICE_CHANNEL_ID = "SERVICE", LOCATION_CHANNEL_ID="LOCATION";
    private Context ctx = this;

    // Variables needed to add the location engine
    private LocationEngine locationEngine;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 10000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    // Variables needed to listen to location updates
    private ServiceLocationCallback callback = new ServiceLocationCallback(this);

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
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
        return START_STICKY;
    }

    private void connectWebSocket() {

        connection = new WebSocketConnection();
        try {
            connection.connect(SOCKET_URL, new WebSocketConnectionHandler() {
                @Override
                public void onConnect(ConnectionResponse response) {
                    Log.d(LOG_TAG, "Connected to server");
                    initLocationEngine();
                }

                @Override
                public void onOpen() {
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

    private void sendNotification(String payload) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, LOCATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_my_location_24)
                .setContentTitle("Location")
                .setContentText(payload);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Location Channel";
            String description = "Channel for getting location updates";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            locationChannel = new NotificationChannel(LOCATION_CHANNEL_ID, name, importance);
            locationChannel.setDescription(description);

            locationNotificationManager = getSystemService(NotificationManager.class);
            locationNotificationManager.createNotificationChannel(locationChannel);
        }
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        System.out.println("location method called");
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("RestartService");
        broadcastIntent.setClass(this, LocationReceiver.class);
        this.sendBroadcast(broadcastIntent);
    }

    private static class ServiceLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<LocationService> activityWeakReference;

        ServiceLocationCallback(LocationService service) {
            this.activityWeakReference = new WeakReference<>(service);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @SuppressLint("StringFormatInvalid")
        @Override
        public void onSuccess(LocationEngineResult result) {
            LocationService service = activityWeakReference.get();

            if (service != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                String myLatitude = String.valueOf(location.getLongitude());
                String myLongitude = String.valueOf(location.getLongitude());
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("animal", "tiger");
                    jsonObject.put("id", "id_2");
                    jsonObject.put("latitude", myLatitude);
                    jsonObject.put("longitude", myLongitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                service.connection.sendMessage(jsonObject.toString());
                service.createNotificationChannel();
                service.sendNotification(myLatitude+" "+myLongitude);

                Log.d("ADebugTag", "Value: " + (result.getLastLocation().getLatitude()));

                // Create a Toast which displays the new location's coordinates
                Toast.makeText(service, String.format(service.getString(R.string.new_location),
                        result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude()),
                        Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can not be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            LocationService service = activityWeakReference.get();
            if (service != null) {
                Toast.makeText(service, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
