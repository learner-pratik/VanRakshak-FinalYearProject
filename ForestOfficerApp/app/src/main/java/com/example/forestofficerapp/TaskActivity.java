package com.example.forestofficerapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class TaskActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private static final String taskURL = "/taskreport/";

    private static final int PERMISSIONS_REQUEST = 1;
    private static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private Bitmap photo;

    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextView taskName, taskType, taskDescription, taskAssignedBy, taskDeadline, progressMessage;
    private TextInputLayout taskReport;
    private MaterialButton taskCameraButton, taskSubmit, taskCancel;
    private ProgressBar progressBar;
    private Task task;
    private int taskIndex;
    private String writtenReport;
    private Double latitude, longitude;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_page);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        topAppBar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        taskName = findViewById(R.id.taskPageTaskName);
        taskType = findViewById(R.id.taskPageTypeName);
        taskDescription = findViewById(R.id.taskPageTaskDescription);
        taskAssignedBy = findViewById(R.id.taskPageTaskAssigner);
        taskDeadline = findViewById(R.id.taskPageDeadlineDate);
        taskReport = findViewById(R.id.taskReportTextField);
        taskCameraButton = findViewById(R.id.taskCamerabutton);
        taskSubmit = findViewById(R.id.taskSubmit);
        taskCancel = findViewById(R.id.taskCancel);
        imageView = findViewById(R.id.taskImageView);
        progressBar = findViewById(R.id.taskPageProgressBar);
        progressMessage = findViewById(R.id.taskPageProgressMessage);

        setSupportActionBar(topAppBar);
        setNavigationViewListener();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        taskIndex = getIntent().getIntExtra("taskIndex", 0);
        task = TaskListActivity.taskList.get(taskIndex);
        taskName.setText(makeCapital(task.getTaskName()));
        taskType.setText(makeCapital(task.getTaskType()));
        taskDescription.setText(makeCapital(task.getTaskDescription()));
        taskAssignedBy.setText(task.getAssignedBy());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-m-yyyy");
        String deadlineDate = dateFormat.format(task.getTaskDeadline());
        taskDeadline.setText(deadlineDate);

        if (hasPermission()) {
            getCurrentLocation();
        } else {
            requestPermission();
        }

        taskCameraButton.setOnClickListener(v -> {
            writtenReport = taskReport.getEditText().getText().toString();
            SaveSharedPreference.setTaskIndex(this, taskIndex);
            SaveSharedPreference.setTaskReport(this, writtenReport);
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        });

        taskSubmit.setOnClickListener(v -> {
            sendTaskReport();
        });

        taskCancel.setOnClickListener(v -> {
            goToTaskList();
        });
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        System.out.println(temp);
        return temp;
    }

    private String makeCapital(String text) {
        char[] charArray = text.toCharArray();
        boolean foundSpace = true;

        for(int i = 0; i < charArray.length; i++) {
            if(Character.isLetter(charArray[i])) {
                if(foundSpace) {
                    charArray[i] = Character.toUpperCase(charArray[i]);
                    foundSpace = false;
                }
            }
            else {
                foundSpace = true;
            }
        }

        String message = String.valueOf(charArray);
        return message;
    }

    private void sendTaskReport() {

        progressBar.setVisibility(View.VISIBLE);
        progressMessage.setVisibility(View.VISIBLE);

        String geoLatitude, geoLongitude;
        if (latitude==null) {
            geoLatitude = "19.725894";
            geoLongitude = "72.321456";
        } else {
            geoLatitude = Double.toString(latitude);
            geoLongitude = Double.toString(longitude);
        }
        String url = LoginOptionActivity.BASE_URL+taskURL;
        String clickedPicture = BitMapToString(photo);
        String reportData = taskReport.getEditText().getText().toString();
        String authToken = "Token "+SaveSharedPreference.getAuthToken(this);

        JSONObject jsonObject = new JSONObject();
        String taskID = task.getTaskID();
        try {
            jsonObject.put("empid", SaveSharedPreference.getEmployeeID(this));
            jsonObject.put("name", SaveSharedPreference.getName(this));
            jsonObject.put("taskID", taskID);
            jsonObject.put("report", reportData);
            jsonObject.put("latitude", geoLatitude);
            jsonObject.put("longitude", geoLongitude);
            jsonObject.put("image", clickedPicture);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject, response -> {

            boolean status = false;
            try {
                status = response.getBoolean("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressBar.setVisibility(View.INVISIBLE);
            if (status) {
                refreshTaskPage();
                int t = SaveSharedPreference.getSubmittedTasks(this);
                SaveSharedPreference.setSubmittedTasks(this, t+1);
            } else {
                progressMessage.setText("FAILED TO SEND REPORT");
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    progressMessage.setVisibility(View.INVISIBLE);
                    goToTaskList();
                }, 1000);
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

    private void refreshTaskPage() {
        progressMessage.setText("REPORT SENT");
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            progressMessage.setVisibility(View.INVISIBLE);
            goToTaskList();
        }, 1000);
    }

    private void goToTaskList() {
        Intent taskListActivityIntent = new Intent(this, TaskListActivity.class);
        startActivity(taskListActivityIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            taskIndex = SaveSharedPreference.getTaskIndex(this);
            taskReport.getEditText().setText(SaveSharedPreference.getTaskReport(this));
        }
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
