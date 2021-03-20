package com.example.forestofficerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterPasswordActivity extends Activity {

    private static final String registerURL = "/new_register";
    private final String LOG_TAG = this.getClass().getSimpleName();

    private TextInputLayout newPasswordEditText, confirmPasswordEditText;
    private String newPassword, confirmPassword, registeredEmail;
    private ProgressBar passwordProgressBar;
    private TextView passwordTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_passwordpage);

        newPasswordEditText = findViewById(R.id.registerPasswordNewEditText);
        confirmPasswordEditText = findViewById(R.id.registerPasswordConfirmEditText);
        passwordProgressBar = findViewById(R.id.passwordProgressBar);
        passwordTextView = findViewById(R.id.credentialsTextMessage);

        registeredEmail = getIntent().getStringExtra("Email");

        MaterialButton passwordSubmitButton = findViewById(R.id.registerPasswordSubmit);
        MaterialButton cancelButton = findViewById(R.id.registerCancel);

        passwordSubmitButton.setOnClickListener(v -> {
            newPassword = newPasswordEditText.getEditText().getText().toString();
            confirmPassword = confirmPasswordEditText.getEditText().getText().toString();

            if (verifyPassword(newPassword)) {
                if (newPassword.equals(confirmPassword)) {
                    sendPassword();
                } else {
                    Toast.makeText(RegisterPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegisterPasswordActivity.this, "Password does not satisfy the specifications", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> {
            goToOptionsPage();
        });
    }

    private void sendPassword() {

        passwordProgressBar.setVisibility(View.VISIBLE);
        passwordTextView.setVisibility(View.VISIBLE);
        passwordTextView.setText("REGISTERING");

        String url = LoginOptionActivity.BASE_URL+registerURL;
        String email = registeredEmail;
        String password = newPasswordEditText.getEditText().getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject, response -> {

            Log.d(LOG_TAG, "post request success");
            Log.d(LOG_TAG, response.toString());

            boolean status = false;
            try {
                status = response.getBoolean("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            passwordProgressBar.setVisibility(View.INVISIBLE);
            if (status) {
                passwordTextView.setText("PASSWORD ADDED");
            } else {
                passwordTextView.setText("PASSWORD REGISTRATION FAILED");
            }

            Handler handler = new Handler();
            boolean finalStatus = status;
            handler.postDelayed(() -> {
                passwordTextView.setVisibility(View.INVISIBLE);
                if (!finalStatus) return;
                startLoginPage();
            }, 2000);

        }, error -> {
            Log.d(LOG_TAG, "post request failure");
            error.printStackTrace();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void goToOptionsPage() {
        Intent optionsActivity = new Intent(this, LoginOptionActivity.class);
        startActivity(optionsActivity);
    }

    private void startLoginPage() {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
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
