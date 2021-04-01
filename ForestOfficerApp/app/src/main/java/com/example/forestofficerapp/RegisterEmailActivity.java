package com.example.forestofficerapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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

import androidx.annotation.ColorInt;
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
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

  public class RegisterEmailActivity extends Activity {

    private final String LOG_TAG = RegisterEmailActivity.this.getClass().getSimpleName();
    private static final String emailURL = "/check/";
    private TextInputLayout emailText;
    private ProgressBar emailProgressBar;
    private TextView otpTextView;
    private boolean redirectedForForgotPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_emailpage);

        emailText = findViewById(R.id.registerEmailEditText);
        emailProgressBar = findViewById(R.id.emailProgressBar);
        otpTextView = findViewById(R.id.otpTextMessage);

        MaterialButton registerEmailSubmit = findViewById(R.id.registerEmailSubmit);
        MaterialButton cancelButton = findViewById(R.id.registerCancel);

        redirectedForForgotPassword = getIntent().getBooleanExtra("forgot_password", false);

        registerEmailSubmit.setOnClickListener(v -> {
            if (checkInternetConnection(RegisterEmailActivity.this)) {
                verifyMailAndSendOTP();
            } else {
                Toast.makeText(RegisterEmailActivity.this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> goToOptionsPage());
    }

    private void verifyMailAndSendOTP() {
//        EmailAsyncTask emailAsyncTask = new EmailAsyncTask(this);
//        emailAsyncTask.execute(emailText.getEditText().getText().toString());

        emailProgressBar.setVisibility(View.VISIBLE);
        otpTextView.setVisibility(View.VISIBLE);
        otpTextView.setText("VERIFYING EMAIL");

        String url = LoginOptionActivity.BASE_URL+emailURL;
        String email = emailText.getEditText().getText().toString();

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, postData, response -> {
            Log.d(LOG_TAG, "post request success");
            Log.d(LOG_TAG, response.toString());

            int emailStatus = 0;
            try {
                emailStatus = response.getInt("email");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            emailProgressBar.setVisibility(View.INVISIBLE);
            switch (emailStatus) {
                case 1: {
                    otpTextView.setText("EMAIL INCORRECT");
                    Toast.makeText(this, "Please enter the registered email", Toast.LENGTH_LONG).show();
                    break;
                }
                case 2: {
                    if (!checkWhetherRedirectedFromForgetPasswordOption()) {
                        otpTextView.setText("PASSWORD ALREADY CREATED FOR THE EMAIL");
                        Toast.makeText(this, "Redirecting you to the login page", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case 3: {
                    otpTextView.setText("EMAIL VERIFIED");
                    break;
                }
                default:
                    break;
            }

            Handler handler = new Handler();
            int finalEmailStatus = emailStatus;
            handler.postDelayed(() -> {

                if (finalEmailStatus == 2) {
                    goToLoginPage();
                }
                else if (finalEmailStatus == 3 || checkWhetherRedirectedFromForgetPasswordOption()) {
                    String generatedOtp = getOTP();
                    OtpAsyncTask otpAsyncTask = new OtpAsyncTask(this);
                    otpAsyncTask.execute(email, generatedOtp);
                }
            }, 3000);

        }, error -> {
            System.out.println("post request failure");
            error.printStackTrace();
            goToOptionsPage();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

    private boolean checkInternetConnection(RegisterEmailActivity registerEmailActivity) {
//        InternetConnection connection = new InternetConnection(registerEmailActivity);
//        connection.execute();
//        try {
//            Void voidObject = connection.get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Log.d(LOG_TAG, "The internet status is"+connection.getInternetStatus());
//        return connection.getInternetStatus();
        return true;
    }

    private String getOTP() {

        String numbers = "1234567890";
        Random random = new Random();
        char[] otp = new char[6];

        for(int i = 0; i< 6 ; i++) {
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }
        return new String(otp);
    }

    private void startOtpActivity(String registeredEmail, String generatedOtp) {
        Intent otpActivityIntent = new Intent(this, RegisterOtpActivity.class);
        otpActivityIntent.putExtra("Email", registeredEmail);
        otpActivityIntent.putExtra("Generated-OTP", generatedOtp);
        otpActivityIntent.putExtra("Email-Sent", true);
        startActivity(otpActivityIntent);
    }

    private void goToOptionsPage() {
        Intent optionsActivity = new Intent(this, LoginOptionActivity.class);
        startActivity(optionsActivity);
    }

    private void goToLoginPage() {
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(loginActivityIntent);
    }

    private boolean checkWhetherRedirectedFromForgetPasswordOption() {
        return redirectedForForgotPassword;
    }

    private static class OtpAsyncTask extends AsyncTask<String, Void, Boolean> {

        private static final String username = "vanrakshakheroku@gmail.com";
        private static final String password = "uzumakiNaruto@9974";
        private String registeredEmail, generatedOtp;

        private WeakReference<RegisterEmailActivity> activityWeakReference;

        public OtpAsyncTask(RegisterEmailActivity emailActivity) {
            super();
            activityWeakReference = new WeakReference<>(emailActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            RegisterEmailActivity emailActivity = activityWeakReference.get();
            if (emailActivity == null || emailActivity.isFinishing()) {
                return;
            }

            emailActivity.emailProgressBar.setVisibility(View.VISIBLE);
            emailActivity.otpTextView.setVisibility(View.VISIBLE);
            emailActivity.otpTextView.setText("Sending OTP");
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean check = false;
            registeredEmail = strings[0];
            generatedOtp = strings[1];

            String textMessage = "Please enter this OTP in the Android application\n"+"OTP - "+strings[1];

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });
            try {
//                Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("pratiknaik4799@gmail.com"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(strings[0]));
                message.setSubject("OTP for Vanrakshak");
                message.setText(textMessage);

                MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
                mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
                mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
                mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
                mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
                mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");

                Transport.send(message);

                System.out.println("Done");

                check = true;

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }

            return check;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);

            System.out.println("on post execute method of otpasynctask called");
            RegisterEmailActivity emailActivity = activityWeakReference.get();
            if (emailActivity == null || emailActivity.isFinishing()) {
                return;
            }

            if (b) {
                emailActivity.otpTextView.setText("OTP sent to registered email");
                emailActivity.emailProgressBar.setVisibility(View.INVISIBLE);
            } else {
                emailActivity.otpTextView.setText("OTP sending failed");
                Toast.makeText(emailActivity, "Sorry for the inconvenience", Toast.LENGTH_LONG).show();
            }

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                emailActivity.otpTextView.setVisibility(View.INVISIBLE);

                if (!b) emailActivity.goToOptionsPage();
                else emailActivity.startOtpActivity(registeredEmail, generatedOtp);
            }, 1000);
        }

    }
}
