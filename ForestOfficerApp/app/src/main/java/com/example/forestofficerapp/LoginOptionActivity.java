package com.example.forestofficerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;

public class LoginOptionActivity extends Activity {

    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginoption);

        Button registerButton = findViewById(R.id.registerButton);
        Button loginButton = findViewById(R.id.loginButton);

        String loginEmail = SaveSharedPreference.getEmail(this);
        String loginPassword = SaveSharedPreference.getPassword(this);

        Log.d(LOG_TAG, "Email is "+ loginEmail);
        Log.d(LOG_TAG, "Password is "+ loginPassword);

        if (!loginEmail.isEmpty() && !loginPassword.isEmpty()) {
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
        }

        registerButton.setOnClickListener(v -> {
            Intent registerEmailActivityIntent = new Intent(LoginOptionActivity.this, RegisterEmailActivity.class);
            startActivity(registerEmailActivityIntent);
        });

        loginButton.setOnClickListener(v -> {
            Intent loginActivityIntent = new Intent(LoginOptionActivity.this, LoginActivity.class);
            startActivity(loginActivityIntent);
        });
    }
}
