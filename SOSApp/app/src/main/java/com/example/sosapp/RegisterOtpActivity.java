package com.example.sosapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class RegisterOtpActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView progressTextView;
    private EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five, otp_textbox_six;
    private MaterialButton otpSubmitButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_otp);

        progressBar = findViewById(R.id.register_otp_progress_bar);
        progressTextView = findViewById(R.id.register_otp_progress_message);
        otp_textbox_one = findViewById(R.id.otp_edit_box1);
        otp_textbox_two = findViewById(R.id.otp_edit_box2);
        otp_textbox_three = findViewById(R.id.otp_edit_box3);
        otp_textbox_four = findViewById(R.id.otp_edit_box4);
        otp_textbox_five = findViewById(R.id.otp_edit_box5);
        otp_textbox_six = findViewById(R.id.otp_edit_box6);
        otpSubmitButton = findViewById(R.id.phone_otp_submit);

        EditText[] edit = {otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five, otp_textbox_six};

        otp_textbox_one.addTextChangedListener(new GenericTextWatcher(otp_textbox_one, edit));
        otp_textbox_two.addTextChangedListener(new GenericTextWatcher(otp_textbox_two, edit));
        otp_textbox_three.addTextChangedListener(new GenericTextWatcher(otp_textbox_three, edit));
        otp_textbox_four.addTextChangedListener(new GenericTextWatcher(otp_textbox_four, edit));
        otp_textbox_five.addTextChangedListener(new GenericTextWatcher(otp_textbox_five, edit));
        otp_textbox_six.addTextChangedListener(new GenericTextWatcher(otp_textbox_six, edit));

        otpSubmitButton.setOnClickListener(v -> {
            verifyOtp();
        });
    }

    private void verifyOtp() {
        String enteredOTP = "";
        enteredOTP += otp_textbox_one.getText().toString();
        enteredOTP += otp_textbox_two.getText().toString();
        enteredOTP += otp_textbox_three.getText().toString();
        enteredOTP += otp_textbox_four.getText().toString();
        enteredOTP += otp_textbox_five.getText().toString();
        enteredOTP += otp_textbox_six.getText().toString();

        String generatedOTP = getIntent().getStringExtra("OTP");

        if (enteredOTP.equals(generatedOTP)) startRegistrationPage();
        else {
            Toast.makeText(this, "OTP incorrect", Toast.LENGTH_SHORT).show();
            goToPreviousPage();
        }
    }

    private void goToPreviousPage() {
        Intent previousIntent = new Intent(this, PhoneRegisterActivity.class);
        startActivity(previousIntent);
    }

    private void startRegistrationPage() {
        Intent registerPageIntent = new Intent(this, RegistrationPageActivity.class);
        registerPageIntent.putExtra("Phone Number", getIntent().getStringExtra("Phone Number"));
        startActivity(registerPageIntent);
    }
}
