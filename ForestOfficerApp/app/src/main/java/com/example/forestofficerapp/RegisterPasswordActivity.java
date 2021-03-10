package com.example.forestofficerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterPasswordActivity extends Activity {

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
        LoginCredentialsAsyncTask credentialsAsyncTask = new LoginCredentialsAsyncTask(this);
        credentialsAsyncTask.execute();
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

    private static class LoginCredentialsAsyncTask extends AsyncTask<String, Void, Boolean> {

        private WeakReference<RegisterPasswordActivity> passwordActivityWeakReference;

        public LoginCredentialsAsyncTask(RegisterPasswordActivity passwordActivity) {
            super();
            passwordActivityWeakReference = new WeakReference<>(passwordActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            RegisterPasswordActivity passwordActivity = passwordActivityWeakReference.get();
            if (passwordActivity == null || passwordActivity.isFinishing())
                return;

            passwordActivity.passwordProgressBar.setVisibility(View.VISIBLE);
            passwordActivity.passwordTextView.setVisibility(View.VISIBLE);
            passwordActivity.passwordTextView.setText("REGISTERING");
        }

        @Override
        protected Boolean doInBackground(String... strings) {

//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("Email", strings[0]);
//                jsonObject.put("Password", strings[1]);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            SendData sendData = new SendData();
//            JSONObject response = sendData.sendJsonData(RegisterPasswordActivity.this, jsonObject, "Register");

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            RegisterPasswordActivity passwordActivity = passwordActivityWeakReference.get();
            if (passwordActivity == null || passwordActivity.isFinishing())
                return;

            if (aBoolean) {
                passwordActivity.passwordProgressBar.setVisibility(View.INVISIBLE);
                passwordActivity.passwordTextView.setText("PASSWORD ADDED");
            }

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                passwordActivity.passwordTextView.setVisibility(View.INVISIBLE);

                if (!aBoolean) return;

                passwordActivity.startLoginPage();
            }, 1000);
        }
    }
}
