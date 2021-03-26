package org.tensorflow.lite.examples.detection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class CameraMqttService extends Service {
    private static final String TAG = "CameraMqttService";

    private final String protocol = "tcp";
    private final String port = "1883";
    private final String url = protocol + "://" + MqttActivity.ipAddress + ":" + port;

    private final String clientId = MqttClient.generateClientId();
    private Context context;

    private final String camera_id = "c-234";
    // camera working status will be published on this topic
    private final String trap_camera_topic = "forest/camera/"+camera_id;
    private final String trap_camera_payload = "";

    private static MqttAndroidClient client;
    private static IMqttToken token;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.d(TAG, "camera service started");

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 20 seconds
                Log.d(TAG, "inside handler run");
                client = new MqttAndroidClient(getApplicationContext(), url, clientId);
                try {
                    token = client.connect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d(TAG, "inside success");
                        byte[] encodedPayload = new byte[0];
                        MqttMessage message;

                        try {
                            encodedPayload = trap_camera_payload.getBytes("UTF-8");
                            message = new MqttMessage(encodedPayload);
                            client.publish(trap_camera_topic, message);
                            Log.d(TAG, "inside success");
                        } catch (NullPointerException | UnsupportedEncodingException | MqttException e) {
                            System.out.println("error occured");
                            e.printStackTrace();
                        }
                        try {
                            client.disconnect();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Log.d(TAG, "inside failure");
                        Log.d(TAG, String.valueOf(exception));
                    }
                });

                handler.postDelayed(this, 10000);
            }
        }, 10000);  //the time is in miliseconds

        return START_STICKY;
    }
}
