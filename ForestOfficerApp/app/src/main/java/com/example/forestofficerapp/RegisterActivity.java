package com.example.forestofficerapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class RegisterActivity extends Activity {

    TextView passwordDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        passwordDescription = (TextView) findViewById(R.id.passwordDescription);
        passwordDescription.setVisibility(View.INVISIBLE);
    }
}
