package com.example.forestofficerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterPasswordActivity extends Activity {

    EditText newPasswordEditText, confirmPasswordEditText;
    String newPassword, confirmPassword, registeredEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newPasswordEditText = findViewById(R.id.registerPasswordNewEditText);
        confirmPasswordEditText = findViewById(R.id.registerPasswordConfirmEditText);

        registeredEmail = getIntent().getStringExtra("Email");

        Button passwordSubmitButton = findViewById(R.id.registerPasswordSubmit);

        passwordSubmitButton.setOnClickListener(v -> {
            newPassword = newPasswordEditText.getText().toString();
            confirmPassword = confirmPasswordEditText.getText().toString();

            if (verifyPassword(newPassword)) {
                if (newPassword.equals(confirmPassword)) {
                    sendPassword();
                    Intent loginIntent = new Intent(RegisterPasswordActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    Toast.makeText(RegisterPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegisterPasswordActivity.this, "Password does not satisfy the specifications", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendPassword() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Email", registeredEmail);
            jsonObject.put("Password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendData sendData = new SendData();
        JSONObject response = sendData.sendJsonData(RegisterPasswordActivity.this, jsonObject, "Register");

    }

    private boolean verifyPassword(String password) {

        String pattern = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(password);

        if (m.matches()) {
            if (password.length()>=8 && password.length()<=12)
                return true;
        }
        return false;
    }
}
