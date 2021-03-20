package com.example.sosapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class SOSActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        topAppBar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawerLayout);

        setSupportActionBar(topAppBar);
        setNavigationViewListener();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
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
        drawerLayout.closeDrawer(0);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }
}
