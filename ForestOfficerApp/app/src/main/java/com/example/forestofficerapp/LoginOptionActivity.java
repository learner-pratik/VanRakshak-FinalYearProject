package com.example.forestofficerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class LoginOptionActivity extends Activity {

    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final String BASE_URL = "https://vanrakshak.herokuapp.com";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginoption);

        Button registerButton = findViewById(R.id.registerButton);
        Button loginButton = findViewById(R.id.loginButton);

        System.out.println(SaveSharedPreference.getServiceFlag(this));
        if (!SaveSharedPreference.getServiceFlag(this)) {
            Intent serviceIntent = new Intent(this, ForestService.class);
            Handler handler = new Handler();
            handler.post(() -> startService(serviceIntent));
        }

        String loginEmail = SaveSharedPreference.getEmail(this);
        String loginPassword = SaveSharedPreference.getPassword(this);

        Log.d(LOG_TAG, "Email is "+ loginEmail);
        Log.d(LOG_TAG, "Password is "+ loginPassword);

        if (!loginEmail.isEmpty() && !loginPassword.isEmpty()) {
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
        }

        registerButton.setOnClickListener(v -> {
            if (InternetConnection.checkInternetStatus(this)) {
                Intent registerEmailActivityIntent = new Intent(LoginOptionActivity.this, RegisterEmailActivity.class);
                startActivity(registerEmailActivityIntent);
            } else {
                Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
            }
        });

        loginButton.setOnClickListener(v -> {
            if (InternetConnection.checkInternetStatus(this)) {
                Intent loginActivityIntent = new Intent(LoginOptionActivity.this, LoginActivity.class);
                startActivity(loginActivityIntent);
            } else {
                Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
