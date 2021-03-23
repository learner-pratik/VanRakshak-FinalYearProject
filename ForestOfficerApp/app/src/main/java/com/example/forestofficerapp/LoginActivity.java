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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class LoginActivity extends Activity {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private static final String loginURL = "/login/";

    private TextInputLayout emailText, passwordText;
    private ProgressBar loginProgressBar;
    private TextView loginTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MaterialButton submitButton = findViewById(R.id.loginSubmit);
        MaterialButton cancelButton = findViewById(R.id.loginCancel);
        MaterialButton forgotPasswordButton = findViewById(R.id.forgotPasswordButton);

        emailText = findViewById(R.id.emailEditText);
        passwordText = findViewById(R.id.passwordEditText);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        loginTextView = findViewById(R.id.loginTextMessage);

        submitButton.setOnClickListener(v -> {
            if (!checkInternetConnection(LoginActivity.this)) {
                Toast.makeText(LoginActivity.this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
            } else {
                checkLoginCredentials();
            }
        });

        cancelButton.setOnClickListener(v -> {
            goToOptionsPage();
        });

        forgotPasswordButton.setOnClickListener(v -> {
            goToRegistrationPage();
        });
    }

    private void goToOptionsPage() {
        Intent optionsActivity = new Intent(this, LoginOptionActivity.class);
        startActivity(optionsActivity);
    }

    private void startMainActivity() {
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
    }

    private void goToRegistrationPage() {
        Intent registerActivity = new Intent(this, RegisterEmailActivity.class);
        registerActivity.putExtra("forgot_password", true);
        startActivity(registerActivity);
    }

    private boolean checkInternetConnection(LoginActivity loginActivity) {
        
//        InternetConnection connection = new InternetConnection(this);
        return true;
    }

    private void checkLoginCredentials() {

        loginProgressBar.setVisibility(View.VISIBLE);
        loginTextView.setVisibility(View.VISIBLE);

        String url = LoginOptionActivity.BASE_URL+loginURL;
        String email = emailText.getEditText().getText().toString();
        String password = passwordText.getEditText().getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject, response -> {

            boolean verifiedEmail = false, verifiedPassword = false, passwordRegistered = false;
            try {
                verifiedEmail = response.getBoolean("email");
                verifiedPassword = response.getBoolean("password");
                passwordRegistered = response.getBoolean("password_registration");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int option = 0;
            JSONObject userProfile = new JSONObject();
            if (!verifiedEmail) {
                option = 1;
            } else if (!verifiedPassword && passwordRegistered) {
                option = 2;
            } else if (!passwordRegistered) {
                option = 3;
            } else if (verifiedEmail && verifiedPassword) {
                try {
                    userProfile = response.getJSONObject("user");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                option = 4;
            }

            String profileName = "", profileDesignation = "", profileBeat = "", profileRange = "", profileDivision = "", profileEmployeeID="";
            loginProgressBar.setVisibility(View.INVISIBLE);
            switch (option) {
                case 1: {
                    loginTextView.setText("EMAIL NOT REGISTERED");
                    break;
                }
                case 2: {
                    loginTextView.setText("PASSWORD INCORRECT");
                    break;
                }
                case 3: {
                    loginTextView.setText("PASSWORD NOT REGISTERED");
                    break;
                }
                case 4: {
                    loginTextView.setText("LOGGING YOU");

                    try {
                        profileName = userProfile.getString("name");
                        profileDesignation = userProfile.getString("designation");
                        profileBeat = userProfile.getString("beat");
                        profileRange = userProfile.getString("range");
                        profileDivision = userProfile.getString("division");
                        profileEmployeeID = userProfile.getString("empid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    SaveSharedPreference.setEmail(this, email);
                    SaveSharedPreference.setPassword(this, password);
                    SaveSharedPreference.setName(this, profileName);
                    SaveSharedPreference.setDesignation(this, profileDesignation);
                    SaveSharedPreference.setBeat(this, profileBeat);
                    SaveSharedPreference.setRange(this, profileRange);
                    SaveSharedPreference.setDivision(this, profileDivision);
                    SaveSharedPreference.setEmployeeID(this, profileEmployeeID);
                    break;
                }
                default:
                    break;
            }
            Handler handler = new Handler();
            int finalOption = option;
            handler.postDelayed(() -> {
                loginTextView.setVisibility(View.INVISIBLE);
                if (finalOption == 4) startMainActivity();
            }, 2000);

        }, error -> {
            Log.d(LOG_TAG, "post request failed");
            error.printStackTrace();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}
