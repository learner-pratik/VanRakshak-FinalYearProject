package com.example.forestofficerapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ForestService extends Service {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private WebSocketClient webSocketClient;
    private JSONObject responseObject;
    private NotificationManager taskNotificationManager, alertNotificationManager;
    private NotificationChannel taskChannel, alertChannel;
    private static final String TASK_CHANNEL_ID = "TASK", ALERT_CHANNEL_ID = "ALERT";
    private static final int taskNotificationId = 1, alertNotificationId = 2;
    private String token;
    private Task task;
    private Alert alert;
    private Context ctx = this;

    @Override
    public void onCreate() {
        super.onCreate();
        SaveSharedPreference.setServiceFlag(this, true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void connectWebSocket() {
        URI uri;

        try {
//            uri = new URI("ws://vanrakshak.herokuapp.com/web_socket");
            uri = new URI("ws://forestwebsocket.herokuapp.com/test");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("WebSocket", "opened");
//                webSocketClient.send(token);
            }

            @Override
            public void onMessage(String message) {

                Log.d(LOG_TAG, "Message from Server: "+message);
                try {
                    responseObject = new JSONObject(message);
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
                    addTaskToList();
                    sendTaskNotification();
                } else {
                    addAlertsToList();
                    sendAlertNotification();
                }

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

    private void addAlertsToList() {

        alert = new Alert();
        String type = "", name = "";
        try {
            type = responseObject.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (type.equals("hunter")) name = "Hunter Spotting";
        else if (type.equals("sos")) name = "Settlement Emergency";
        else name = "Camera Broken";
        try {
            alert.setAlertName(name);
            alert.setAlertType(responseObject.getString("type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addTaskToList() {

        task = new Task();
        try {
            task.setTaskID(responseObject.getString("id"));
            task.setTaskType(responseObject.getString("task_type"));
            task.setTaskName(responseObject.getString("task_name"));
            task.setTaskDescription(responseObject.getString("description"));
            task.setAssignedBy(responseObject.getString("assigning_officer"));
            Date date = new SimpleDateFormat("dd/mm/yyyy").parse(responseObject.getString("deadline"));
            task.setTaskDeadline(date);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TaskListActivity.taskList.add(task);
    }

    private void sendTaskNotification() {

        createTaskNotificationChannel();

        Intent taskListIntent = new Intent(this, TaskListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, (int) System.currentTimeMillis(), taskListIntent, 0
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, TASK_CHANNEL_ID)
                .setChannelId(TASK_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_content_task_24px)
                .setContentTitle(task.getTaskType())
                .setContentInfo(task.getTaskName())
                .setContentText("Assigned By - "+task.getAssignedBy())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(taskNotificationId, builder.build());
    }

    private void createTaskNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.task_channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            taskChannel = new NotificationChannel(TASK_CHANNEL_ID, name, importance);
            taskChannel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            taskNotificationManager = getSystemService(NotificationManager.class);
            taskNotificationManager.createNotificationChannel(taskChannel);
        }
    }

    private void sendAlertNotification() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                connectWebSocket();
            }
        });

        return android.app.Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SaveSharedPreference.setServiceFlag(this, false);
    }


}
