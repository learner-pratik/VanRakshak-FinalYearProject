package com.example.forestofficerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Random;

public class RegisterOtpActivity extends Activity {

    EditText otpText;
    boolean emailSent;
    String sentOtp, registeredEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        otpText = findViewById(R.id.registerOtpEditText);

        Button otpSubmitButton = findViewById(R.id.registerOtpSubmit);
        Button cancelButton = findViewById(R.id.registerCancel);

        TextView registeredEmailTextView = findViewById(R.id.registeredEmail);
        registeredEmail = getIntent().getStringExtra("Email");
        registeredEmailTextView.setText(registeredEmail);
        emailSent = getIntent().getBooleanExtra("Email-Sent", false);
        sentOtp = getIntent().getStringExtra("Generated-OTP");

        Intent previousActivity = new Intent(RegisterOtpActivity.this, LoginOptionActivity.class);

        otpSubmitButton.setOnClickListener(v -> {
            if (!emailSent) {
                Toast.makeText(RegisterOtpActivity.this, "Unable to send OTP, check internet connection", Toast.LENGTH_SHORT).show();
            }
            else if (VerifyOtp()) {
                Intent passwordActivityIntent = new Intent(RegisterOtpActivity.this, RegisterPasswordActivity.class);
                passwordActivityIntent.putExtra("Email", registeredEmail);
                startActivity(passwordActivityIntent);
            } else if (!VerifyOtp()) {
                Toast.makeText(RegisterOtpActivity.this, "Wrong OTP. Registration cancelled", Toast.LENGTH_SHORT).show();
                startActivity(previousActivity);
            }
        });

        cancelButton.setOnClickListener(v -> {
            startActivity(previousActivity);
        });
    }

    private boolean VerifyOtp() {
        String enteredOtp = otpText.getText().toString();
        if (enteredOtp.equals(sentOtp))
            return true;
        else
            return false;
    }


}

