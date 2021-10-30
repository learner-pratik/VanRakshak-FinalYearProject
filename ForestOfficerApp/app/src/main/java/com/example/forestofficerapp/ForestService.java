package com.example.forestofficerapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.crossbar.autobahn.websocket.WebSocketConnection;
import io.crossbar.autobahn.websocket.WebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.exceptions.WebSocketException;
import io.crossbar.autobahn.websocket.types.ConnectionResponse;

public class ForestService extends Service {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private static final String SOCKET_URL = "ws://vanrakshak.herokuapp.com/web_socket";
    private static final String DEMO_URL = "ws://forestwebsocket.herokuapp.com/test";
    private JSONObject responseObject;
    private NotificationManager taskNotificationManager, alertNotificationManager, serviceNotificationManager;
    private NotificationChannel taskChannel, alertChannel, forestServiceChannel;
    private static final String TASK_CHANNEL_ID = "TASK", ALERT_CHANNEL_ID = "ALERT", SERVICE_CHANNEL_ID = "SERVICE";
    private static final int taskNotificationId = 3, alertNotificationId = 4;
    private Task task;
    private Alert alert;
    private Context ctx = this;
    public static boolean logoutOption = false;
    private WebSocketConnection webSocketConnection;

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

        String channelName = "Forest Background Service";
        forestServiceChannel = new NotificationChannel(SERVICE_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        forestServiceChannel.setLightColor(Color.BLUE);
        forestServiceChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        serviceNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert serviceNotificationManager != null;
        serviceNotificationManager.createNotificationChannel(forestServiceChannel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, SERVICE_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("ForestOfficerApp is running in background")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        AsyncTask.execute(() -> connectWebSocket());
        return android.app.Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void connectWebSocket() {

        webSocketConnection = new WebSocketConnection();
        try {
            webSocketConnection.connect(SOCKET_URL, new WebSocketConnectionHandler() {
                @Override
                public void onConnect(ConnectionResponse response) {
                    Log.d(LOG_TAG, "Connected to server");
                }

                @Override
                public void onOpen() {
                    webSocketConnection.sendMessage(SaveSharedPreference.getAuthToken(ctx));
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(LOG_TAG, "Connection closed");
                }

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onMessage(String payload) {
                    Log.d(LOG_TAG, "Message from Server: "+payload);
                    try {
                        responseObject = new JSONObject(payload);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String type = "";
                    try {
                        type = responseObject.getString("type");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (type.equals("task")) {
                        sendTaskNotification();
                    } else {
                        addAlertsToList();
                        sendAlertNotification();
                    }
                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addAlertsToList() {

        alert = new Alert();
        String type = "", name = "";

        try {
            type = responseObject.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (type.equals("hunter")) name = "Hunter Spotting";
        else if (type.equals("sos")) {
            String sosType = "";
            try {
                sosType = responseObject.getString("sos_type");
                char[] charArray = sosType.toCharArray();
                boolean foundSpace = true;
                for(int i = 0; i < charArray.length; i++) {
                    if(Character.isLetter(charArray[i])) {
                        if(foundSpace) {
                            charArray[i] = Character.toUpperCase(charArray[i]);
                            foundSpace = false;
                        }
                    }
                    else foundSpace = true;
                }
                sosType = String.valueOf(charArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            name = sosType;
        }
        else name = "Camera Broken";

        try {

            alert.setAlertName(name);
            alert.setAlertType(responseObject.getString("type"));
            alert.setAlertDate(new Date());
            alert.setAlertTime(LocalTime.now());
            alert.setAlertLatitude(Double.valueOf(responseObject.getString("latitude")));
            alert.setAlertLongitude(Double.valueOf(responseObject.getString("longitude")));

            if (type.equals("sos")) {
                alert.setUserObject(
                        responseObject.getString("name"),
                        responseObject.getString("phone_number"),
                        responseObject.getString("address")
                );
                alert.setSosType(responseObject.getString("sos_type"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (type.equals("sos")) SOSAlertFragment.sosAlertList.add(alert);
        else CameraAlertFragment.cameraAlertList.add(alert);
    }

    private void sendTaskNotification() {

        createTaskNotificationChannel();

        task = new Task();
        try {
            task.setTaskID(responseObject.getString("id"));
            task.setTaskType(responseObject.getString("task_type"));
            task.setTaskName(responseObject.getString("task_name"));
            task.setTaskDescription(responseObject.getString("description"));
            task.setAssignedBy(responseObject.getString("assigning_officer"));
            Date date = new SimpleDateFormat("yyyy-mm-dd").parse(responseObject.getString("deadline"));
            task.setTaskDeadline(date);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        Intent taskListIntent = new Intent(this, TaskListActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(taskListIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent taskPendingIntent =
                stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, TASK_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_content_task_24px)
                .setContentTitle(task.getTaskType())
                .setContentInfo(task.getTaskName())
                .setContentText("Assigned By - "+task.getAssignedBy())
                .setContentIntent(taskPendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(taskNotificationId, builder.build());
    }

    private void createTaskNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.task_channel_name);
            String description = getString(R.string.task_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            taskChannel = new NotificationChannel(TASK_CHANNEL_ID, name, importance);
            taskChannel.setDescription(description);

            taskNotificationManager = getSystemService(NotificationManager.class);
            taskNotificationManager.createNotificationChannel(taskChannel);
        }
    }

    private void sendAlertNotification() {

        createAlertNotificationChannel();

        Intent alertListIntent = new Intent(this, AlertListActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(alertListIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent alertPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ALERT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add_alert_24px)
                .setContentTitle(alert.getAlertType())
                .setContentText(alert.getAlertName())
                .setContentIntent(alertPendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(alertNotificationId, builder.build());
    }

    private void createAlertNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.alert_channel_name);
            String description = getString(R.string.alert_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            alertChannel = new NotificationChannel(ALERT_CHANNEL_ID, name, importance);
            alertChannel.setDescription(description);

            alertNotificationManager = getSystemService(NotificationManager.class);
            alertNotificationManager.createNotificationChannel(alertChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!logoutOption) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("RestartService");
            broadcastIntent.setClass(this, ForestReceiver.class);
            this.sendBroadcast(broadcastIntent);
        } else {
            webSocketConnection.sendClose();
            System.out.println(webSocketConnection.isConnected());
        }
    }

}
