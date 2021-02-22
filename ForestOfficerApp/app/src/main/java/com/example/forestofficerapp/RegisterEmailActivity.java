package com.example.forestofficerapp;

import android.app.Activity;
import android.content.Intent;
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
            try {
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        cancelButton.setOnClickListener(v -> {
            Intent previousActivity = new Intent(RegisterEmailActivity.this, LoginOptionActivity.class);
            startActivity(previousActivity);
        });
    }

    private void sendOtp() {
        generatedOtp = getOTP();
        mailService.execute(generatedOtp);
        emailSent = mailService.check;
    }

    private boolean checkRegisteredEmail() throws JSONException {

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", emailText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SendData sendData = new SendData();
        JSONObject receivedData = sendData.sendJsonData(this, postData);

        Boolean response = receivedData.getBoolean("email");
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
