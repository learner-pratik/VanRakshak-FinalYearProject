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
        
        InternetConnection connection = new InternetConnection(this);
        return true;
    }

    private void checkLoginCredentials() {
        String email = emailText.getEditText().getText().toString();
        String password = passwordText.getEditText().getText().toString();
        Log.d(LOG_TAG, "Entered email is "+email);
        Log.d(LOG_TAG, "Entered password is "+password);

        UserCredentialsAsyncTask credentialsAsyncTask = new UserCredentialsAsyncTask(this);
        credentialsAsyncTask.execute(email, password);
    }

    private static class UserCredentialsAsyncTask extends AsyncTask<String, Boolean, Integer> {

        private WeakReference<LoginActivity> loginActivityWeakReference;
        private String registeredEmail, registeredPassword;
        private JSONObject userProfile;
        private String profileName, profileEmail, profileDesignation, profileBeat, profileRange, profileDivision;

        public UserCredentialsAsyncTask(LoginActivity loginActivity) {
            super();
            loginActivityWeakReference = new WeakReference<>(loginActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            LoginActivity loginActivity = loginActivityWeakReference.get();
            if (loginActivity == null || loginActivity.isFinishing())
                return;

            loginActivity.loginProgressBar.setVisibility(View.VISIBLE);
            loginActivity.loginTextView.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... strings) {

            registeredEmail = strings[0];
            registeredPassword = strings[1];

//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("email", registeredEmail);
//                jsonObject.put("password", registeredPassword);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            SendData sendData = new SendData();
//            JSONObject receivedData = (JSONObject) sendData.sendJsonData(loginActivityWeakReference.get(), jsonObject, "Login");
//
//            Boolean verifiedEmail = false, verifiedPassword = false, passwordRegistered = false;
//            try {
//                verifiedEmail = receivedData.getBoolean("email");
//                verifiedPassword = receivedData.getBoolean("password");
//                passwordRegistered = receivedData.getBoolean("password_registration");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            if (!verifiedEmail) {
//                return 1;
//            } else if (!verifiedPassword && passwordRegistered) {
//                return 2;
//            } else if (!passwordRegistered) {
//                return 3;
//            } else if (verifiedEmail && verifiedPassword) {
//                try {
//                    userProfile = receivedData.getJSONObject("user");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                return 4;
//            }
//            return 0;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return 4;
        }

        @Override
        protected void onPostExecute(Integer option) {
            super.onPostExecute(option);

            LoginActivity loginActivity = loginActivityWeakReference.get();
            if (loginActivity == null || loginActivity.isFinishing())
                return;

            loginActivity.loginProgressBar.setVisibility(View.INVISIBLE);
            switch (option) {
                case 1: {
                    loginActivity.loginTextView.setText("EMAIL NOT REGISTERED");
                    break;
                }
                case 2: {
                    loginActivity.loginTextView.setText("PASSWORD INCORRECT");
                    break;
                }
                case 3: {
                    loginActivity.loginTextView.setText("PASSWORD NOT REGISTERED");
                    break;
                }
                case 4: {
                    loginActivity.loginTextView.setText("LOGGING YOU");

//                    try {
//                        profileName = userProfile.getString("name");
//                        profileEmail = userProfile.getString("email");
//                        profileDesignation = userProfile.getString("designation");
//                        profileBeat = userProfile.getString("beat");
//                        profileRange = userProfile.getString("range");
//                        profileDivision = userProfile.getString("division");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                    SaveSharedPreference.setEmail(loginActivity, registeredEmail);
                    SaveSharedPreference.setPassword(loginActivity, registeredPassword);
//                    SaveSharedPreference.setName(loginActivity, profileName);
//                    SaveSharedPreference.setDesignation(loginActivity, profileDesignation);
//                    SaveSharedPreference.setBeat(loginActivity, profileBeat);
//                    SaveSharedPreference.setRange(loginActivity, profileRange);
//                    SaveSharedPreference.setDivision(loginActivity, profileDivision);
                    break;
                }
                default:
                    break;
            }
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                loginActivity.loginTextView.setVisibility(View.INVISIBLE);
                if (option.equals(4)) loginActivity.startMainActivity();
            }, 1000);
        }
    }
}
