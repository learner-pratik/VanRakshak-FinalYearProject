package com.example.forestofficerapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

public class ReportActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private static final String reportURL = "/report/";

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private Bitmap photo;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private String reportType;
    private String writtenName, writtenDescription;
    private FusedLocationProviderClient fusedLocationClient;
    private TextInputLayout reportDescription, reportName;
    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ProgressBar progressBar;
    private TextView progressMessage;

    private int locationRequestCode = 1000;
    private double wayLatitude = 0.0, wayLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        TextView textView = findViewById(R.id.txt_bundle);
        imageView = findViewById(R.id.picture_taken);
        Bundle bundle = getIntent().getExtras();
        reportType = bundle.get("data").toString();
        textView.setText(reportType);

        reportDescription = findViewById(R.id.report_description);
        reportName = findViewById(R.id.report_name);
        topAppBar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        progressBar = findViewById(R.id.reportProgressBar);
        progressMessage = findViewById(R.id.reportProgressMessage);

        MaterialButton cameraButton = findViewById(R.id.take_picture);
        MaterialButton submitButton = findViewById(R.id.submit_report);
        MaterialButton cancelButton = findViewById(R.id.cancel_report);

        setSupportActionBar(topAppBar);
        setNavigationViewListener();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        cameraButton.setOnClickListener(v -> {
//                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
//                {
//                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
//                }
//                else
//                {
//                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                }
            writtenName = reportName.getEditText().getText().toString();
            writtenDescription = reportDescription.getEditText().getText().toString();
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        });

        submitButton.setOnClickListener(v -> {
            sendReport();
        });

        cancelButton.setOnClickListener(v -> {
            startMainActivity();
        });
    }

    private void startMainActivity() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        System.out.println(temp);
        return temp;
    }

    private void sendReport() {

        String geoLatitude = String.valueOf(MainActivity.currentLocation.getLatitude());
        String geoLongitude = String.valueOf(MainActivity.currentLocation.getLongitude());
        String name = reportName.getEditText().getText().toString();
        String description = reportDescription.getEditText().getText().toString();
        String url = LoginOptionActivity.BASE_URL+reportURL;
        String clickedPicture = BitMapToString(photo);

        progressBar.setVisibility(View.VISIBLE);
        progressMessage.setVisibility(View.VISIBLE);

        JSONObject postData = new JSONObject();
        try {
            postData.put("empid", SaveSharedPreference.getEmployeeID(this));
            postData.put("name", name);
            postData.put("type", reportType);
            postData.put("description", description);
            postData.put("latitude", geoLatitude);
            postData.put("longitude", geoLongitude);
            postData.put("image", clickedPicture);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, postData, response -> {

            boolean status = false;
            try {
               status  = response.getBoolean("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressBar.setVisibility(View.INVISIBLE);
            if (status) {
                progressMessage.setText("REPORT SENT");
            } else {
                progressMessage.setText("UNABLE TO SEND REPORT");
            }

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                progressMessage.setVisibility(View.INVISIBLE);
                startMainActivity();
            }, 2000);

        }, error -> {
            Log.d(LOG_TAG, "post request failed");
            error.printStackTrace();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId()==R.id.logoutButton) {
            SaveSharedPreference.clearPreferences(this);
            logoutFromApp();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }
        return true;
    }

    private void logoutFromApp() {
        String url = LoginOptionActivity.BASE_URL+MainActivity.logoutURL;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, null, null, error -> {
            Log.d(LOG_TAG, "post request failed");
            error.printStackTrace();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            reportName.getEditText().setText(writtenName);
            reportDescription.getEditText().setText(writtenDescription);
        }
    }
}
