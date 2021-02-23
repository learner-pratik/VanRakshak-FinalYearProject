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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    EditText emailText, passwordText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button submitButton = (Button) findViewById(R.id.loginSubmit);
        Button cancelButton = (Button)findViewById(R.id.loginCancel);

        emailText = (EditText) findViewById(R.id.emailEditText);
        passwordText = (EditText) findViewById(R.id.passwordEditText);

        submitButton.setOnClickListener(v -> {
            if (!checkInternetConnection(LoginActivity.this)) {
                Toast.makeText(LoginActivity.this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
            }
            checkLoginCredentials();
        });

        cancelButton.setOnClickListener(v -> {
            Intent previousActivity = new Intent(LoginActivity.this, LoginOptionActivity.class);
            startActivity(previousActivity);
        });
    }

    private boolean checkInternetConnection(LoginActivity loginActivity) {
        InternetConnection connection = new InternetConnection(loginActivity);
        connection.execute();
        while (true) {
            if (connection.getStatus().equals(AsyncTask.Status.FINISHED))
                break;
        }
        return connection.getInternetStatus();
    }

    private void checkLoginCredentials() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        Boolean check = validateDataFromServer(email, password);

        if (check) {
            SaveSharedPreference.setEmail(getApplicationContext(), email);
            SaveSharedPreference.setPassword(getApplicationContext(), password);
            Intent mainPage = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainPage);
        } else {
            Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean validateDataFromServer(String email, String password) {

        JSONObject jsonObject = new JSONObject();
        Boolean answer = false;
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SendData sendData = new SendData();
        JSONObject receivedData = sendData.sendJsonData(this, jsonObject, "Login");

        try {
            answer = (receivedData.getBoolean("email") && receivedData.getBoolean("password"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return answer;
    }
}
