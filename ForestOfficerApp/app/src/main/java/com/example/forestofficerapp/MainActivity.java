package com.example.forestofficerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextView name, email, designation, beat, range, division, reportsSent, taskCompleted;
    public static final String logoutURL = "/logout/";
    public static final String animalListURL = "/animal_api/";
    public static Map<String, List<String>> map = new HashMap<>();

    Intent forestServiceIntent;
    private ForestService forestService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forestService = new ForestService();
        forestServiceIntent = new Intent(this, forestService.getClass());
        if (!isMyServiceRunning(forestServiceIntent.getClass())) {
            startService(forestServiceIntent);
        }

        topAppBar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        name = findViewById(R.id.profileOfficerName);
        email = findViewById(R.id.profileOfficerEmail);
        designation = findViewById(R.id.profileDesignationName);
        beat = findViewById(R.id.profileBeatName);
        range = findViewById(R.id.profileRangeName);
        division = findViewById(R.id.profileDivisionName);
        reportsSent = findViewById(R.id.profileReportsSubmitted);
        taskCompleted = findViewById(R.id.profileTasksCompleted);

        setSupportActionBar(topAppBar);
        setNavigationViewListener();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        name.setText(SaveSharedPreference.getName(this));
        email.setText(SaveSharedPreference.getEmail(this));
        designation.setText(SaveSharedPreference.getDesignation(this));
        beat.setText(SaveSharedPreference.getBeat(this));
        range.setText(SaveSharedPreference.getRange(this));
        division.setText(SaveSharedPreference.getDivision(this));
        reportsSent.setText(String.valueOf(SaveSharedPreference.getSubmittedReports(this)));
        taskCompleted.setText(String.valueOf(SaveSharedPreference.getSubmittedTasks(this)));

        if (map.isEmpty()) getAnimalList();
    }

    private void getAnimalList() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("empid", SaveSharedPreference.getEmployeeID(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String authToken = "Token "+SaveSharedPreference.getAuthToken(this);
        String url = LoginOptionActivity.BASE_URL+animalListURL;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject, response -> {
            Log.d(LOG_TAG, response.toString());

            try {
                JSONArray jsonArray = response.getJSONArray("animals");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject object = jsonArray.getJSONObject(i);
                    String animalName = object.getString("animal");
                    JSONArray array = object.getJSONArray("id");
                    List<String> list = new ArrayList<>();

                    for (int j = 0; j < array.length(); j++) {
                        list.add(array.getString(j));
                    }

                    map.put(animalName, list);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.d(LOG_TAG, "post request failed");
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", authToken);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
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
        String url = LoginOptionActivity.BASE_URL+logoutURL;
        String authToken = "Token "+SaveSharedPreference.getAuthToken(this);
        System.out.println(authToken);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // response
                    Log.d(LOG_TAG,"Logout-response : " + response);
                    ForestService.logoutOption = true;
                    Intent serviceIntent = new Intent(this, ForestService.class);
                    stopService(serviceIntent);
                },
                error -> {
                    // TODO Auto-generated method stub
                    Log.d(LOG_TAG,"error => "+error.toString());
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