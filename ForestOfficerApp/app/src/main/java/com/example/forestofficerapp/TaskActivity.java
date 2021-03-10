package com.example.forestofficerapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;

public class TaskActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextView taskName, taskType, taskDescription, taskAssignedBy, taskDeadline, progressMessage;
    private TextInputLayout taskReport;
    private MaterialButton taskCameraButton, taskSubmit, taskCancel;
    private ProgressBar progressBar;
    private Task task;
    private int taskIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_page);

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
        taskName.setText(task.getTaskName());
        taskType.setText(task.getTaskType());
        taskDescription.setText(task.getTaskDescription());
        taskAssignedBy.setText(task.getAssignedBy());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-m-yyyy");
        String deadlineDate = dateFormat.format(task.getTaskDeadline());
        taskDeadline.setText(deadlineDate);

        taskCameraButton.setOnClickListener(v -> {
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

    private void goToTaskList() {
        Intent taskListActivityIntent = new Intent(this, TaskListActivity.class);
        startActivity(taskListActivityIntent);
    }

    private void sendTaskReport() {
        String reportData = taskReport.getEditText().getText().toString();

        TaskAsyncTask taskAsyncTask = new TaskAsyncTask(this);
        taskAsyncTask.execute(reportData);
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
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId()==R.id.logoutButton) {
            SaveSharedPreference.clearPreferences(this);
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }
        return true;
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

    private class TaskAsyncTask extends AsyncTask<String, Void, Boolean> {

        private WeakReference<TaskActivity> taskActivityWeakReference;

        public TaskAsyncTask(TaskActivity taskActivity) {
            super();
            taskActivityWeakReference = new WeakReference<>(taskActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TaskActivity taskActivity = taskActivityWeakReference.get();
            if (taskActivity == null || taskActivity.isFinishing())
                return;

            taskActivity.progressBar.setVisibility(View.VISIBLE);
            taskActivity.progressMessage.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... strings) {

//            JSONObject jsonObject = new JSONObject();
//            String taskID = String.valueOf(taskActivityWeakReference.get().task.getTaskID());
//            try {
//                jsonObject.put("email", SaveSharedPreference.getEmail(taskActivityWeakReference.get()));
//                jsonObject.put("taskID", taskID);
//                jsonObject.put("report", strings[0]);
//                jsonObject.put("latitude", strings[1]);
//                jsonObject.put("longitude", strings[2]);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            SendData sendData = new SendData();
//            JSONObject receivedData = (JSONObject) sendData.sendJsonData(
//                    taskActivityWeakReference.get(), jsonObject, "Task");
//            try {
//                return receivedData.getBoolean("status");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            TaskActivity taskActivity = taskActivityWeakReference.get();
            if (taskActivity == null || taskActivity.isFinishing())
                return;

            taskActivity.progressBar.setVisibility(View.INVISIBLE);
            if (aBoolean) {
                taskActivity.refreshTaskPage();
            } else {
                taskActivity.progressMessage.setText("FAILED TO SEND REPORT");
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    taskActivity.progressMessage.setVisibility(View.INVISIBLE);
                    taskActivity.goToTaskList();
                }, 1000);
            }
        }
    }

    private void refreshTaskPage() {
        progressMessage.setText("REPORT SENT");
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            progressMessage.setVisibility(View.INVISIBLE);
            goToTaskList();
        }, 1000);
    }

}
