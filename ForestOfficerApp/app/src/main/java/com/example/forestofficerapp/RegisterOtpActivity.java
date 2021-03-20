package com.example.forestofficerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Random;

public class RegisterOtpActivity extends Activity {

    TextInputLayout otpText;
    String sentOtp, registeredEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_otppage);

        otpText = findViewById(R.id.registerOtpEditText);

        MaterialButton otpSubmitButton = findViewById(R.id.registerOtpSubmit);
        MaterialButton cancelButton = findViewById(R.id.registerCancel);

        TextView registeredEmailTextView = findViewById(R.id.registeredEmail);
        registeredEmail = getIntent().getStringExtra("Email");
        registeredEmailTextView.setText(registeredEmail);
        sentOtp = getIntent().getStringExtra("Generated-OTP");

        Intent previousActivity = new Intent(RegisterOtpActivity.this, LoginOptionActivity.class);

        otpSubmitButton.setOnClickListener(v -> {
            if (VerifyOtp()) {
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
        String enteredOtp = otpText.getEditText().getText().toString();
        if (enteredOtp.equals(sentOtp))
            return true;
        else
            return false;
    }


}

