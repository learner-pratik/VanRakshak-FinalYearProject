package com.example.forestofficerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimalListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Spinner animalSpinner, animalIdSpinner;
    private MaterialButton submitButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_list);

        topAppBar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        animalSpinner = findViewById(R.id.animalSpinner);
        animalIdSpinner = findViewById(R.id.animalIdSpinner);
        submitButton = findViewById(R.id.animalListNextButton);

        setSupportActionBar(topAppBar);
        setNavigationViewListener();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Spinner Drop down elements
        List<String> animalTypes = new ArrayList<String>();
        animalTypes.add("All");
        for (Map.Entry<String, List<String>> entry : MainActivity.map.entrySet()) {
            String animal = entry.getKey();
            animal = animal.substring(0, 1).toUpperCase()+animal.substring(1, animal.length());
            animalTypes.add(animal);
        }

        ArrayAdapter<String> animalAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, animalTypes);
        animalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animalSpinner.setAdapter(animalAdapter);
        animalSpinner.setSelection(0);
        animalSpinner.setOnItemSelectedListener(this);

        //spinner 2
        String[] vi = {"All"};
        ArrayAdapter<String> tempAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vi);
        tempAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animalIdSpinner.setAdapter(tempAdapter);
        animalIdSpinner.setSelection(0);
        animalIdSpinner.setOnItemSelectedListener(this);
        animalIdSpinner.setEnabled(false);
        animalIdSpinner.setClickable(false);

        submitButton.setOnClickListener(v -> {
            Intent mapIntent = new Intent(this, MapActivity.class);
            HashMap<String, List<String>> copy;

            if(animalSpinner.getSelectedItemPosition()==0){
                copy = new HashMap<>(MainActivity.map);
                mapIntent.putExtra("map", copy);
            }
            else{
                String animal = animalSpinner.getSelectedItem().toString().toLowerCase();
                if(animalIdSpinner.getSelectedItemPosition()==0){
                    copy = new HashMap<>();
                    copy.put(animal, MainActivity.map.get(animal));
                }
                else{
                    copy = new HashMap<>();
                    List<String> list = new ArrayList<>();
                    list.add(animalIdSpinner.getSelectedItem().toString());
                    copy.put(animal, list);
                }
            }

            mapIntent.putExtra("map",copy);
            startActivity(mapIntent);
        });
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {

            case R.id.animalSpinner : {
                if (parent.getSelectedItemPosition()!=0) {
                    String animalName = parent.getSelectedItem().toString().toLowerCase();
                    List<String> animalId = MainActivity.map.get(animalName);
                    animalId.add(0, "All");

                    ArrayAdapter<String> animalIDAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, animalId);
                    animalIDAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    animalIdSpinner.setAdapter(animalIDAdapter);
                    animalIdSpinner.setSelection(0);
                    animalIdSpinner.setClickable(true);
                    animalIdSpinner.setEnabled(true);
                    animalIdSpinner.setOnItemSelectedListener(this);
                } else {
                    animalIdSpinner.setSelection(0);
                    animalIdSpinner.setClickable(false);
                    animalIdSpinner.setEnabled(false);
                }
                break;
            }
            case R.id.animalIdSpinner : {
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
