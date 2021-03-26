package org.tensorflow.lite.examples.detection;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.location.Location.distanceBetween;

public class ServiceMqtt extends Service {
    private static final String TAG = "ServiceMqtt";

    private final String protocol = "tcp";
    private final String port = "1883";
    private final String url = protocol + "://" + MqttActivity.ipAddress + ":" + port;

    private final String clientId = MqttClient.generateClientId();
    private final MqttAndroidClient client =
            new MqttAndroidClient(this, url, clientId);

    // these are the topics which are subscribed to by camera
    private final String[] topics = {
      "forest/hunter",
      "forest/camera/#",
      "forest/animal/#",
      "forest/animal_sightings/#",
      "forest/forest-officer/#",
      "forest/local",
    };
    private final int[] qos = {1,1,1,1,1,1};

    private HashMap<String, String[]> officer_coordinates = new HashMap<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    try {
                        IMqttToken subToken = client.subscribe(topics, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // The message was published
                                Log.d(TAG, "subscribed");
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards
                                Log.d(TAG, "not subscribed");
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(TAG, "message arrived");
                System.out.println(topic);
                System.out.println(message.toString());

                CallAPI postRequest = new CallAPI();
                String[] values = topic.split("/");
                if (topic.contains("hunter")) {
                    postRequest.execute("hunter", message.toString());
//                    if (checkDistance()) {
//                        postRequest.execute("hunter", message.toString());
//                    }
                } else if (topic.contains("animal")) {
                    postRequest.execute(values[2], message.toString());
                } else if (topic.contains("forest-officer")) {
                    String[] coordinates = message.toString().split(",");
                    officer_coordinates.put(values[2], coordinates);
                } else if (topic.contains("local")) {
                    postRequest.execute("sos", message.toString());
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "delivery complete");
            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        try {
            IMqttToken unsubToken = client.unsubscribe(topics);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                    Log.d(TAG, "client unsubscribed");
                    try {
                        IMqttToken disconToken = client.disconnect();
                        disconToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // we are now successfully disconnected
                                Log.d(TAG, "client disconnected");
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // something went wrong, but probably we are disconnected anyway
                                Log.d(TAG, "client almost disconnected");
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
                    Log.d(TAG, "client not unsubscribed");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    private boolean checkDistance() {
        System.out.println("distance function called");
        for (Map.Entry mapElement : officer_coordinates.entrySet()) {
            String[] coordinates = (String[]) mapElement.getValue();
            double startlatitude = Double.parseDouble(coordinates[0]);
            double startlongitude = Double.parseDouble(coordinates[1]);
            double endlatitiude = CameraActivity.latitude;
            double endlongitude = CameraActivity.longitude;
            float[] results = new float[3];
            distanceBetween(startlatitude, startlongitude, endlatitiude, endlongitude, results);
            if (results[0] <= 1.0) {
                return false;
            }
        }
        return true;
    }

    private class CallAPI extends AsyncTask<String, String, String> {

        public CallAPI(){
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "post request function called");

            String urlString = "https://d763785c541b.ngrok.io/alert"; // URL to call
            JSONObject data = new JSONObject();
            String[] values = params[1].split("/");
            System.out.println(values);
            System.out.println(values[1]);
            try {
                data.put("type", params[0]);
                data.put("value", values[0]);
                data.put("latitude", values[1]);
                data.put("longitude", values[2]);
                data.put("timestamp", values[3]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OutputStream out = null;

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                urlConnection.setDoOutput(true);
                out = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(data.toString());
                writer.flush();
                writer.close();
                out.close();

                Log.d(TAG, "message sent");

                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }

                urlConnection.connect();
            } catch (Exception e) {
                System.out.println(e);
                Log.d(TAG, "data sending failed");
                System.out.println(e.getMessage());
            }
            return null;
        }
    }
}


