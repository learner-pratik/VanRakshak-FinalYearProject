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
        EmailAsyncTask emailAsyncTask = new EmailAsyncTask(this);
        emailAsyncTask.execute(emailText.getEditText().getText().toString());
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

    private static class EmailAsyncTask extends AsyncTask<String, Void, Integer> {

        private WeakReference<RegisterEmailActivity> activityWeakReference;
        private String submittedMail;

        public EmailAsyncTask(RegisterEmailActivity emailActivity) {
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
            System.out.println("preexecute methood of emailasynctask called");

            emailActivity.emailProgressBar.setVisibility(View.VISIBLE);
            emailActivity.otpTextView.setVisibility(View.VISIBLE);
            emailActivity.otpTextView.setText("VERIFYING EMAIL");

        }

        @Override
        protected Integer doInBackground(String... strings) {

            submittedMail = strings[0];
            JSONObject postData = new JSONObject();
            int response = 0;
            try {
                postData.put("email", strings[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SendData sendData = new SendData();
            JSONObject receivedData = (JSONObject) sendData.sendJsonData(activityWeakReference.get(), postData, "Email");

            try {
                response = receivedData.getInt("email");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            return 1;
        }

        @Override
        protected void onPostExecute(Integer emailStatus) {
            super.onPostExecute(emailStatus);

            RegisterEmailActivity emailActivity = activityWeakReference.get();
            if (emailActivity == null || emailActivity.isFinishing()) {
                return;
            }

            emailActivity.emailProgressBar.setVisibility(View.INVISIBLE);
            switch (emailStatus) {
                case 1:
                    emailActivity.otpTextView.setText("EMAIL INCORRECT");
                    Toast.makeText(emailActivity, "Please enter the registered email", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    if (!emailActivity.checkWhetherRedirectedFromForgetPasswordOption()) {
                        emailActivity.otpTextView.setText("PASSWORD ALREADY CREATED FOR THE EMAIL");
                        Toast.makeText(emailActivity, "Redirecting you to the login page", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 3:
                    emailActivity.otpTextView.setText("EMAIL VERIFIED");
                    break;
                default:
                    break;
            }

            Handler handler = new Handler();
            handler.postDelayed(() -> {

                if (emailStatus == 2) {
                    emailActivity.goToLoginPage();
                }
                else if (emailStatus == 3 || emailActivity.checkWhetherRedirectedFromForgetPasswordOption()) {
                    String generatedOtp = emailActivity.getOTP();
                    OtpAsyncTask otpAsyncTask = new OtpAsyncTask(emailActivity);
                    otpAsyncTask.execute(submittedMail, generatedOtp);
                }
            }, 1000);

        }
    }

    private static class OtpAsyncTask extends AsyncTask<String, Void, Boolean> {

//        private boolean check = false;
        private static final String username = "pratiknaik4799@gmail.com";
        private static final String password = "uzumakinaruto@4799";
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
            boolean check = false;
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
