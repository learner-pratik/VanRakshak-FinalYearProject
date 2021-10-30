package org.tensorflow.lite.examples.detection;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

public class CameraService extends Service {

    private static final String URL = "https://vanrakshak.herokuapp.com/alert_api/";
    private final String LOG_TAG = this.getClass().getSimpleName();
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCameraStatus();
                handler.postDelayed(this, 6000);
            }
        }, 6000);

        return START_STICKY;
    }

    private void sendCameraStatus() {

        JSONObject jsonObject = new JSONObject();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            jsonObject.put("type", "working");
            jsonObject.put("camera_id", CameraActivity.CAMERA_ID);
            jsonObject.put("latitude", String.valueOf(CameraActivity.latitude));
            jsonObject.put("longitude", String.valueOf(CameraActivity.longitude));
            jsonObject.put("timestamp", String.valueOf(timestamp));
            jsonObject.put("sos_type", "");
            jsonObject.put("name", "");
            jsonObject.put("phone_number", "");
            jsonObject.put("address", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, jsonObject, null, error -> {
            Log.d(LOG_TAG, "post alert request failed");
            error.printStackTrace();
        });

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}
