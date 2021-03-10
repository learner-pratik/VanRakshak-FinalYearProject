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

    private static final String BASE_URL = "http:localhost:3000";
    private static final String loginURL = "/login";
    private static final String logoutURL = "/logout";
    private static final String registerURL = "/register";
    private static final String emailURL = "/check";
    private static final String taskURL = "/taskreport";
    private static final String reportURL = "/report";
    JSONObject receivedResponse = null;

    public Object sendJsonData(Context context, JSONObject object, String requestType) {

        String requestURL;

        switch (requestType) {
            case "Login" : {
                requestURL = BASE_URL + loginURL;
                break;
            }
            case "Logout" : {
                requestURL = BASE_URL + logoutURL;
                break;
            }
            case "Register" : {
                requestURL = BASE_URL + registerURL;
                break;
            }
            case "Email" : {
                requestURL = BASE_URL + emailURL;
                break;
            }
            case "Task" : {
                requestURL = BASE_URL + taskURL;
                break;
            }
            case "Report" : {
                requestURL = BASE_URL + reportURL;
                break;
            }
            default: {
                requestURL = BASE_URL;
                break;
            }
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                requestURL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                receivedResponse = response;
                System.out.println("post request success");
                System.out.println(response);
            }
        }, error -> {
            System.out.println("post request failure");
            error.printStackTrace();
        });

        requestQueue.add(jsonObjectRequest);

        return receivedResponse;
    }
}
