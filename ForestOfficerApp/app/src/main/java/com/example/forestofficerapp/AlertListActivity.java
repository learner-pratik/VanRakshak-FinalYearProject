package com.example.forestofficerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Map;

public class AlertListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;

    private int[] tabIcons = {
            R.drawable.ic_baseline_linked_camera_24,
            R.drawable.ic_baseline_account_box_24
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_list);

        topAppBar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabLayout);

        setSupportActionBar(topAppBar);
        setNavigationViewListener();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        setUpTabIcons();
    }

    private void setUpTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId()==R.id.logoutButton) {
            logoutFromApp();
            SaveSharedPreference.clearPreferences(this);
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }
        return true;
    }

    private void logoutFromApp() {
        String url = LoginOptionActivity.BASE_URL+MainActivity.logoutURL;
        String authToken = "Token "+SaveSharedPreference.getAuthToken(this);
        System.out.println(authToken);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // response
                    Log.d("Logout-response", response);
                    Intent serviceIntent = new Intent(this, ForestService.class);
                    stopService(serviceIntent);
                },
                error -> {
                    // TODO Auto-generated method stub
                    Log.d("ERROR","error => "+error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", authToken);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(postRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.alert_navigation: {
                Intent alertListActivity = new Intent(this, AlertListActivity.class);
                startActivity(alertListActivity);
                break;
            } case R.id.map_navigation: {
                Intent animalListActivity = new Intent(this, AnimalListActivity.class);
                startActivity(animalListActivity);
                break;
            } case R.id.profile_navigation: {
                Intent profileActivity = new Intent(this, MainActivity.class);
                startActivity(profileActivity);
                break;
            } case R.id.report_navigation: {
                Intent reportActivity = new Intent(this, ReportListActivity.class);
                startActivity(reportActivity);
                break;
            } case R.id.task_navigation: {
                Intent taskActivity = new Intent(this, TaskListActivity.class);
                startActivity(taskActivity);
                break;
            }
        }
        drawerLayout.close();
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.personName);
        TextView designation = headerView.findViewById(R.id.personDesignation);
        name.setText(SaveSharedPreference.getName(this));
        designation.setText(SaveSharedPreference.getDesignation(this));
        navigationView.setNavigationItemSelectedListener(this);
    }
}
