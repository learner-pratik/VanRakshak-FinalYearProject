package org.tensorflow.lite.examples.detection;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;

import static org.tensorflow.lite.examples.detection.CameraActivity.animal_locations;
import static org.tensorflow.lite.examples.detection.CameraActivity.animal_types;
import static org.tensorflow.lite.examples.detection.CameraActivity.camera_alert_id;
import static org.tensorflow.lite.examples.detection.CameraActivity.camera_alert_latitude;
import static org.tensorflow.lite.examples.detection.CameraActivity.camera_alert_longitude;
import static org.tensorflow.lite.examples.detection.CameraActivity.camera_alert_timestamp;
import static org.tensorflow.lite.examples.detection.CameraActivity.latitude;
import static org.tensorflow.lite.examples.detection.CameraActivity.longitude;

public class MqttActivity extends Thread {
    public static final String TAG = "MqttActivity";

    public final static String ipAddress = "192.168.49.229";

    private final String protocol = "tcp";
    private final String port = "1883";
    private final String url = protocol + "://" + ipAddress + ":" + port;

    private final String clientId = MqttClient.generateClientId();
    private Context context;

    private final String camera_id = "c-234";

    // hunter detection alerts will be published on this topic
    private final String hunter_topic = "forest/hunter";

    // animal sightings will be published here based on animal
    private final String elephant_topic = "forest/animal/elephant";
    private final String pig_topic = "forest/animal/pig";
    private final String monkey_topic = "forest/animal/monkey";
    private final String civet_topic = "forest/animal/civet";
    private final String deer_topic = "forest/animal/deer";
    private final String camera_topic = "forest/camera-alert";
    private final String map_alert_animals_topic = "forest/map-alert/animals";
    private final String map_alert_locations_topic = "forest/map-alert/locations";

    // message payloads which will be published on various topics
    private String hunter_payload = camera_id+"/"+latitude+"/"+longitude;
    private String animal_payload = camera_id+"/"+latitude+"/"+longitude;
    private String camera_payload = camera_alert_id+"/"+camera_alert_latitude+"/"+camera_alert_longitude+"/"+camera_alert_timestamp;
    private final String mapalert_animals_payload = animal_types;
    private final String mapalert_locations_payload = animal_locations;

    private static MqttAndroidClient client;
    private static IMqttToken token;

    private String payload;

    public MqttActivity (Context context, String payload) {
        this.payload = payload;
        this.context = context;
    }

    public void run () {
        Log.d(TAG, "inside run");
//        Log.d(TAG, token.getClient().toString());
//        Log.d(TAG, payload);
        client = new MqttAndroidClient(context, url, clientId);
        try {
            token = client.connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                // We are connected
                Log.d(TAG, "inside success");
                byte[] encodedPayload = new byte[0];
                MqttMessage message;
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                try {
                    if (payload.equals("Person")) {
                        System.out.println("hunter msg");
                        hunter_payload += "/"+timestamp.toString();
                        encodedPayload = hunter_payload.getBytes("UTF-8");
                        message = new MqttMessage(encodedPayload);
                        client.publish(hunter_topic, message);
//                        client.publish(hunter_topic, encodedPayload, 1, false);
                    } else if (payload.equals("camera")) {
                        encodedPayload = camera_payload.getBytes("UTF-8");
                        message = new MqttMessage(encodedPayload);
                        client.publish(camera_topic, message);
                    } else if (payload.equals("mapalert-animals")) {
                        encodedPayload = mapalert_animals_payload.getBytes("UTF-8");
                        message = new MqttMessage(encodedPayload);
                        client.publish(map_alert_animals_topic, message);
                    } else if (payload.equals("mapalert-locations")) {
                        encodedPayload = mapalert_locations_payload.getBytes("UTF-8");
                        message = new MqttMessage(encodedPayload);
                        client.publish(map_alert_locations_topic, message);
                    } else {
                        animal_payload += "/"+timestamp.toString();
                        encodedPayload = animal_payload.getBytes("UTF-8");
                        message = new MqttMessage(encodedPayload);
                        if (payload.equals("Elephant")) {
                            client.publish(elephant_topic, message);
                        } else if (payload.equals("Monkey")) {
                            client.publish(monkey_topic, message);
                        } else if (payload.equals("Civet")) {
                            client.publish(civet_topic, message);
                        } else if (payload.equals("Pig")) {
                            client.publish(pig_topic, message);
                        } else if (payload.equals("Deer")) {
                            client.publish(deer_topic, message);
                        }
                    }
                    System.out.println("message published");
                } catch (NullPointerException | UnsupportedEncodingException | MqttException e) {
                    System.out.println("error occured");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                System.out.println("Inside failure");
                Log.d(TAG, String.valueOf(exception));
            }
        });

    }

}
