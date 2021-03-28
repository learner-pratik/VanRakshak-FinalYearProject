package com.example.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private MaterialTextView userName, userPhoneNumber, userAddress, userReports, userAlerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topAppBar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        userName = findViewById(R.id.user_name);
        userPhoneNumber = findViewById(R.id.user_phone_number);
        userAddress = findViewById(R.id.user_address);
        userReports = findViewById(R.id.user_reports);
        userAlerts = findViewById(R.id.user_sos_alerts);

        setSupportActionBar(topAppBar);
        setNavigationViewListener();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        String phoneNumber = SaveSharedPreference.getPhoneNumber(this);
        String finalPhoneNumber = "+"+phoneNumber.substring(0, 2)+
                "-"+phoneNumber.substring(2, 5)+"-"+phoneNumber.substring(5, 8)+"-"+phoneNumber.substring(8);
        String name = SaveSharedPreference.getFirstName(this)+" "+SaveSharedPreference.getLastName(this);

        userName.setText(name);
        userPhoneNumber.setText(finalPhoneNumber);
        userAddress.setText(SaveSharedPreference.getAddress(this));
        userReports.setText(SaveSharedPreference.getSubmittedReports(this));
        userAlerts.setText(SaveSharedPreference.getSosAlerts(this));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        drawerToggle.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_navigation: {
                Intent profileActivity = new Intent(this, MainActivity.class);
                startActivity(profileActivity);
                break;
            } case R.id.report_navigation: {
                Intent reportActivity = new Intent(this, ReportActivity.class);
                startActivity(reportActivity);
                break;
            } case R.id.sos_navigation: {
                Intent taskActivity = new Intent(this, SOSActivity.class);
                startActivity(taskActivity);
                break;
            }
        }
        drawerLayout.closeDrawer(Gravity.LEFT);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.personName);
        String name = SaveSharedPreference.getFirstName(this)+" "+SaveSharedPreference.getLastName(this);
        userName.setText(name);
        navigationView.setNavigationItemSelectedListener(this);
    }
}