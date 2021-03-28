package com.example.sosapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class PhoneRegisterActivity extends AppCompatActivity {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private TextInputLayout phoneNumberInput;
    private MaterialButton phoneRegisterSubmit;
    private ProgressBar phoneRegisterProgressBar;
    private TextView phoneRegisterProgressMessage;

    private final String URL = "https://rest.moceanapi.com/rest/2/sms";
    private static final String API_KEY = "acd7e24f";
    private static final String API_SECRET = "0daac0bf";
    private static final String API_USER = "pdnaik_9974";
    private String generatedOTP, phoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        String phone = SaveSharedPreference.getPhoneNumber(this);
        if (!phone.isEmpty()) {
            startMainActivity();
        }

        phoneNumberInput = findViewById(R.id.phone_number);
        phoneRegisterSubmit = findViewById(R.id.phone_register_submit);
        phoneRegisterProgressBar = findViewById(R.id.register_phone_progress_bar);
        phoneRegisterProgressMessage = findViewById(R.id.register_phone_progress_message);

        phoneRegisterSubmit.setOnClickListener(v -> {
            phoneNumber = phoneNumberInput.getEditText().getText().toString().trim();
            phoneNumber = "91"+phoneNumber;
            verifyPhoneNumber(phoneNumber);
        });
    }

    private void startMainActivity() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    private void verifyPhoneNumber(String phoneNumber) {

        JSONObject jsonObject = new JSONObject();
        generatedOTP = getOTP();
        String messageContent = "Welcome to Vanrakshak Platform.\n" +
                "Your One Time Password (OTP) for Phone Number Verification" +
                "OTP - "+generatedOTP;
        try {
            jsonObject.put("mocean-api-key", API_KEY);
            jsonObject.put("mocean-api-secret", API_SECRET);
            jsonObject.put("mocean-from", API_USER);
            jsonObject.put("mocean-to", phoneNumber);
            jsonObject.put("mocean-text", messageContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        phoneRegisterProgressBar.setVisibility(View.VISIBLE);
        phoneRegisterProgressMessage.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, jsonObject, response -> {
            Log.d(LOG_TAG, "post request successful");
            Log.d(LOG_TAG, response.toString());

            phoneRegisterProgressBar.setVisibility(View.INVISIBLE);
            phoneRegisterProgressMessage.setText("OTP SENT");

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                phoneRegisterProgressMessage.setVisibility(View.INVISIBLE);
                startOtpActivity();
            }, 2000);
        }, error -> {
            Log.d(LOG_TAG, "post request failed");
            error.printStackTrace();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void startOtpActivity() {
        Intent otpActivityIntent = new Intent(this, RegisterOtpActivity.class);
        otpActivityIntent.putExtra("OTP", generatedOTP);
        otpActivityIntent.putExtra("Phone Number", phoneNumber);
        startActivity(otpActivityIntent);
    }

    private String getOTP() {

        String numbers = "1234567890";
        Random random = new Random();
        char[] otp = new char[6];

        for(int i = 0; i< 6 ; i++) {
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }
        return new String(otp);
    }

}
