package com.example.sosapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class PhoneRegisterActivity extends AppCompatActivity {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private TextInputLayout phoneNumberInput;
    private MaterialButton phoneRegisterSubmit;
    private ProgressBar phoneRegisterProgressBar;
    private TextView phoneRegisterProgressMessage;

    private final String URL = "https://rest.moceanapi.com/rest/2/sms";
    private final String API_KEY = "acd7e24f";
    private final String API_SECRET = "0daac0bf";
    private final String API_USER = "pdnaik_9974";
    private final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS))
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,"Manifest.permission.READ_SMS") ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,"Manifest.permission.READ_SMS")) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{"Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS"},
                        REQUEST_CODE);
                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) +
                    ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS))
                        != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,"Manifest.permission.READ_SMS") ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this,"Manifest.permission.READ_SMS")) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                                new String[]{"Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS"},
                                REQUEST_CODE);

                     // REQUEST_CODE is an
                     // app-defined int constant. The callback method gets the
                     // result of the request.
                  }
                }

                else {
                        // Permission has already been granted
                }
                // REQUEST_CODE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        else {
            // Permission has already been granted
        }

        phoneNumberInput = findViewById(R.id.phone_number);
        phoneRegisterSubmit = findViewById(R.id.phone_register_submit);
        phoneRegisterProgressBar = findViewById(R.id.register_phone_progress_bar);
        phoneRegisterProgressMessage = findViewById(R.id.register_phone_progress_message);

        phoneRegisterSubmit.setOnClickListener(v -> {
            String phoneNumber = phoneNumberInput.getEditText().getText().toString().trim();
            phoneNumber = "917039293193";
            sendSMS(phoneNumber, "Hello from Vanrakshak");
        });
    }

//    private void verifyPhoneNumber(String phoneNumber) {
//
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("mocean-api-key", API_KEY);
//            jsonObject.put("mocean-api-secret", API_SECRET);
//            jsonObject.put("mocean-from", API_USER);
//            jsonObject.put("mocean-to", phoneNumber);
//            jsonObject.put("mocean-text", "OTP: "+getOTP());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        phoneRegisterProgressBar.setVisibility(View.VISIBLE);
//        phoneRegisterProgressMessage.setVisibility(View.VISIBLE);
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
//                URL, jsonObject, response -> {
//            Log.d(LOG_TAG, "post request successful");
//            Log.d(LOG_TAG, response.toString());
//        }, error -> {
//            Log.d(LOG_TAG, "post request failed");
//            error.printStackTrace();
//        });
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(jsonObjectRequest);
//    }
//
//    private String getOTP() {
//
//        String numbers = "1234567890";
//        Random random = new Random();
//        char[] otp = new char[6];
//
//        for(int i = 0; i< 6 ; i++) {
//            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
//        }
//        return new String(otp);
//    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Log.d(LOG_TAG, "Message Sent");
        } catch (Exception ex) {
            Log.d(LOG_TAG, ex.getMessage());
            ex.printStackTrace();
        }
    }
}
