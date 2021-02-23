package com.example.forestofficerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class RegisterEmailActivity extends Activity {

    EditText emailText;
    MailService mailService = new MailService();
    String generatedOtp;
    boolean emailSent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        emailText = findViewById(R.id.registerEmailEditText);
        Button registerEmailSubmit = findViewById(R.id.registerEmailSubmit);
        Button cancelButton = findViewById(R.id.registerCancel);

        registerEmailSubmit.setOnClickListener(v -> {
            if (checkInternetConnection(RegisterEmailActivity.this)) {
                if (checkRegisteredEmail()) {
                    sendOtp();
                    Intent otpActivityIntent = new Intent(RegisterEmailActivity.this, RegisterOtpActivity.class);
                    otpActivityIntent.putExtra("Email", emailText.getText().toString());
                    otpActivityIntent.putExtra("Generated-OTP", generatedOtp);
                    otpActivityIntent.putExtra("Email-Sent", emailSent);
                    startActivity(otpActivityIntent);
                } else {
                    Toast.makeText(RegisterEmailActivity.this, "Email is not registered", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegisterEmailActivity.this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> {
            Intent previousActivity = new Intent(RegisterEmailActivity.this, LoginOptionActivity.class);
            startActivity(previousActivity);
        });
    }

    private boolean checkInternetConnection(RegisterEmailActivity registerEmailActivity) {
        InternetConnection connection = new InternetConnection(registerEmailActivity);
        connection.execute();
        while (true) {
            if (connection.getStatus().equals(AsyncTask.Status.FINISHED))
                break;
        }
        return connection.getInternetStatus();
    }

    private void sendOtp() {
        generatedOtp = getOTP();
        mailService.execute(generatedOtp);
        while (true) {
            if (mailService.getStatus().equals(AsyncTask.Status.FINISHED))
                break;
        }
        emailSent = mailService.getEmailStatus();
    }

    private boolean checkRegisteredEmail() {

        JSONObject postData = new JSONObject();
        Boolean response = false;
        try {
            postData.put("email", emailText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SendData sendData = new SendData();
        JSONObject receivedData = sendData.sendJsonData(this, postData, "Email");

        try {
            response = receivedData.getBoolean("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
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
