package com.example.forestofficerapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class SendData {

    public static final String BASE_URL = "https://encd7j4cwr4gx6f.m.pipedream.net";
    JSONObject receivedResponse = null;

    public JSONObject sendJsonData(Context context, JSONObject object) {

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                BASE_URL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                receivedResponse = response;
                System.out.println("post request success");
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("post request failure");
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);

        return receivedResponse;
    }
}
