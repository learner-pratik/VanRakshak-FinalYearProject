package com.example.sosapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

public class RegistrationPageActivity extends AppCompatActivity {

    private MaterialTextView phoneNumberTextView;
    private TextInputLayout firstName, lastName, address;
    private MaterialButton registerPageSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_details);

        phoneNumberTextView = findViewById(R.id.phone_number_text_view);
        firstName = findViewById(R.id.register_first_name);
        lastName = findViewById(R.id.register_last_name);
        address = findViewById(R.id.register_address);
        registerPageSubmit = findViewById(R.id.register_page_submit);

        String phoneNumber = getIntent().getStringExtra("Phone Number");
        phoneNumberTextView.setText(phoneNumber);

        registerPageSubmit.setOnClickListener(v -> {
            SaveSharedPreference.setFirstName(this, firstName.getEditText().getText().toString());
            SaveSharedPreference.setLastName(this, lastName.getEditText().getText().toString());
            SaveSharedPreference.setAddress(this, address.getEditText().getText().toString());
            SaveSharedPreference.setPhoneNumber(this, phoneNumber);
            SaveSharedPreference.setSubmittedReports(this, "0");
            SaveSharedPreference.setSosAlerts(this, "0");
            startMainActivity();
        });
    }

    private void startMainActivity() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }
}
