package com.example.sosapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSIONS_REQUEST = 1;
    private static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final int CAMERA_REQUEST = 1888;
    private static final String URL = "https://vanrakshak.herokuapp.com/localreport_api/";
    private final String LOG_TAG = this.getClass().getSimpleName();
    private ImageView imageView;
    private Bitmap photo;

    private FusedLocationProviderClient fusedLocationClient;
    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextInputLayout reportType, reportName, reportDescription;
    private ImageView clickedPicture;
    private ProgressBar reportProgressBar;
    private TextView reportMessage;
    private MaterialButton takePictureButton, reportSubmitButton, cancelButton;
    private String writtenName, writtenDescription, writtenType;
    private Double latitude, longitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        topAppBar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        reportType = findViewById(R.id.report_type);
        reportName = findViewById(R.id.report_name);
        reportDescription = findViewById(R.id.report_description);
        clickedPicture = findViewById(R.id.picture_taken);
        reportProgressBar = findViewById(R.id.report_progress_bar);
        reportMessage = findViewById(R.id.report_progress_message);
        takePictureButton = findViewById(R.id.take_picture);
        imageView = findViewById(R.id.picture_taken);
        reportSubmitButton = findViewById(R.id.submit_report);
        cancelButton = findViewById(R.id.cancel_report);

        setSupportActionBar(topAppBar);
        setNavigationViewListener();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        System.out.println("onCreate method called1");

        if (hasPermission()) {
            getCurrentLocation();
        } else {
            requestPermission();
        }

        takePictureButton.setOnClickListener(v -> {
            writtenName = reportName.getEditText().getText().toString();
            writtenType = reportType.getEditText().getText().toString();
            writtenDescription = reportDescription.getEditText().getText().toString();
            SaveSharedPreference.setReportName(this, writtenName);
            SaveSharedPreference.setReportType(this, writtenType);
            SaveSharedPreference.setReportDescription(this, writtenDescription);
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        });

        System.out.println("onCreate method called2");

        reportSubmitButton.setOnClickListener(v -> {
            sendReport();
        });

        cancelButton.setOnClickListener(v -> {
            startMainActivity();
        });
    }

    private void sendReport() {

        String geoLatitude, geoLongitude;
        if (latitude==null) {
            geoLatitude = "19.725894";
            geoLongitude = "72.321456";
        } else {
            geoLatitude = Double.toString(latitude);
            geoLongitude = Double.toString(longitude);
        }
        String userName = SaveSharedPreference.getFirstName(this)+" "+SaveSharedPreference.getLastName(this);
        String userPhoneNumber = SaveSharedPreference.getPhoneNumber(this);
        String address = SaveSharedPreference.getAddress(this);
        String name = reportName.getEditText().getText().toString();
        String description = reportDescription.getEditText().getText().toString();
        String clickedPicture = BitMapToString(photo);

        reportProgressBar.setVisibility(View.VISIBLE);
        reportMessage.setVisibility(View.VISIBLE);

        JSONObject postData = new JSONObject();
        try {
            postData.put("user_name", userName);
            postData.put("phone_number", userPhoneNumber);
            postData.put("address", address);
            postData.put("report_name", name);
            postData.put("type", reportType);
            postData.put("description", description);
            postData.put("latitude", geoLatitude);
            postData.put("longitude", geoLongitude);
            postData.put("image", clickedPicture);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, postData, response -> {

            boolean status = false;
            try {
                status  = response.getBoolean("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            reportProgressBar.setVisibility(View.INVISIBLE);
            if (status) {
                reportMessage.setText("REPORT SENT");
                int r = Integer.parseInt(SaveSharedPreference.getSubmittedReports(this));
                r++;
                SaveSharedPreference.setSubmittedReports(this, Integer.toString(r));
            } else {
                reportMessage.setText("UNABLE TO SEND REPORT");
            }

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                reportMessage.setVisibility(View.INVISIBLE);
//                startMainActivity();
            }, 2000);

        }, error -> {
            Log.d(LOG_TAG, "post request failed");
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
        System.out.println("get current location method called");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d(LOG_TAG, String.valueOf(latitude));
                        Log.d(LOG_TAG, String.valueOf(longitude));
                    } else {
                        System.out.println("location is null");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            reportName.getEditText().setText(SaveSharedPreference.getReportName(this));
            reportType.getEditText().setText(SaveSharedPreference.getReportType(this));
            reportDescription.getEditText().setText(SaveSharedPreference.getReportDescription(this));
        }
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
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
