package com.example.sosapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SOSActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String URL = "https://vanrakshak.herokuapp.com/alert_api/";
    private static final int PERMISSIONS_REQUEST = 1;
    private static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final String LOG_TAG = this.getClass().getSimpleName();
    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Spinner spinner;
    private ExtendedFloatingActionButton sosButton;
    private FusedLocationProviderClient fusedLocationClient;
    private Double latitude, longitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        topAppBar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        spinner = findViewById(R.id.spinner);
        sosButton = findViewById(R.id.sos_button);

        setSupportActionBar(topAppBar);
        setNavigationViewListener();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Animal Intrusion");
        categories.add("Tree Fall");
        categories.add("Medical Emergency");
        categories.add("Unidentified object");
        categories.add("Other");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setPrompt("Select Alert type");

        if (hasPermission()) {
            getCurrentLocation();
        } else {
            requestPermission();
        }

        sosButton.setOnClickListener(v -> {
            String category = String.valueOf(spinner.getSelectedItem());
            sendSosAlert(category);
        });
    }

    private void sendSosAlert(String category) {

        JSONObject jsonObject = new JSONObject();

        String geoLatitude, geoLongitude;
        if (latitude==null) {
            geoLatitude = "19.725894";
            geoLongitude = "72.321456";
        } else {
            geoLatitude = Double.toString(latitude);
            geoLongitude = Double.toString(longitude);
        }
        String userName = SaveSharedPreference.getFirstName(this)+" "+SaveSharedPreference.getLastName(this);
        String userPhone = "+"+SaveSharedPreference.getPhoneNumber(this);
        String userAddress = SaveSharedPreference.getAddress(this);

        try {
            jsonObject.put("type", "sos");
            jsonObject.put("camera_id", "");
            jsonObject.put("latitude", geoLatitude);
            jsonObject.put("longitude", geoLongitude);

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            jsonObject.put("timestamp", timestamp.toString());

            jsonObject.put("sos_type", category.toLowerCase());
            jsonObject.put("name", userName);
            jsonObject.put("phone_number", userPhone);
            jsonObject.put("address", userAddress);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, jsonObject, response -> {
            Boolean status = false;
            try {
                status = response.getBoolean("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (status) {
                Log.d(LOG_TAG, "Alert post request sent");
                int r = Integer.parseInt(SaveSharedPreference.getSosAlerts(this));
                r++;
                SaveSharedPreference.setSosAlerts(this, Integer.toString(r));
                startMainActivity();
            }
        }, error -> {
            error.printStackTrace();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void startMainActivity() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode, final String[] permissions, final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST) {
            if (allPermissionsGranted(grantResults)) {
                getCurrentLocation();
            } else {
                requestPermission();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.d(LOG_TAG, String.valueOf(latitude));
                Log.d(LOG_TAG, String.valueOf(longitude));
            }
        });
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(PERMISSION_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION_LOCATION)) {
                Toast.makeText(
                        this,
                        "Location permission is required for this demo",
                        Toast.LENGTH_LONG)
                        .show();
            }
            requestPermissions(new String[] {PERMISSION_LOCATION}, PERMISSIONS_REQUEST);
        }
    }

    private static boolean allPermissionsGranted(final int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
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
        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.personName);
        String name = SaveSharedPreference.getFirstName(this)+" "+SaveSharedPreference.getLastName(this);
        userName.setText(name);
        navigationView.setNavigationItemSelectedListener(this);
    }

}
