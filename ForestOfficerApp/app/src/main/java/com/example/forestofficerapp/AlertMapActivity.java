package com.example.forestofficerapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;

public class AlertMapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PermissionsListener {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private MapView mapView;
    private MapboxMap map;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION1 = 99;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private AlertMapActivity.LocationChangeListeningActivityLocationCallback callback =
            new AlertMapActivity.LocationChangeListeningActivityLocationCallback(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_alert_map);

        topAppBar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawerLayout);

        setSupportActionBar(topAppBar);
        setNavigationViewListener();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Initialize the MapView
        mapView = findViewById(R.id.AlertMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {

                map = mapboxMap;

                mapboxMap.setStyle(Style.SATELLITE_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                        initalertLayer(style);
                    }
                });
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponent locationComponent = map.getLocationComponent();
            // Activate with a built LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions
                            .builder(this, loadedMapStyle)
                            .build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            initLocationEngine();
        } else {
            permissionsManager = new PermissionsManager((PermissionsListener) this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);
        LocationEngineRequest locationEngineRequest = new LocationEngineRequest.
                Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
                .build();

        locationEngine.requestLocationUpdates(locationEngineRequest, callback, Looper.getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    private void initalertLayer(@NonNull Style style) {

        Intent intent = getIntent();
        Alert alert;
        if (intent.getStringExtra("type").equals("camera")) {
            alert = CameraAlertFragment.cameraAlertList.get(intent.getIntExtra("alertIndex", 0));
        } else {
            alert = SOSAlertFragment.sosAlertList.get(intent.getIntExtra("alertIndex", 0));
        }
        double lat = alert.getAlertLatitude();
        double lon = alert.getAlertLongitude();

        int d_alert = 0;
        d_alert = getResources().getIdentifier("i_tiger", "drawable", getPackageName());
        style.addImage("icon0", BitmapFactory.decodeResource(this.getResources(), d_alert));
        style.addSource(new GeoJsonSource("sid_alert"));
        style.addLayer(new SymbolLayer("layer-id", "sid_alert").withProperties(
                iconImage("icon0"),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconSize(.02f)
        ));
        GeoJsonSource alertSource = map.getStyle().getSourceAs("sid_alert");
        if (alertSource != null) {
            alertSource.setGeoJson(FeatureCollection.fromFeature(
                    Feature.fromGeometry(Point.fromLngLat(lon,lat))
            ));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        // When the user returns to the activity we want to resume the API calling.
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        // When the user leaves the activity, there is no need in calling the API since the map
        // isn't in view.
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    //
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Plz Enable the permissions...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted){
            map.getStyle(new Style.OnStyleLoaded(){
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        }
        else{
            Toast.makeText(this, "Permissions are not granted...", Toast.LENGTH_SHORT).show();
        }
    }

    private static class LocationChangeListeningActivityLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<AlertMapActivity> activityWeakReference;

        private LocationChangeListeningActivityLocationCallback(AlertMapActivity activity) {
            this.activityWeakReference= new WeakReference<>(activity);
        }
        @Override
        public void onSuccess(LocationEngineResult result) {
            AlertMapActivity activity=activityWeakReference.get();
            if (activity!=null){
                Location location=result.getLastLocation();
                if (location==null){
                    return;
                }
//                Toast.makeText(activity, "Location Co-ordinates: "+location.getLatitude()+
//                                " & "+location.getLongitude()+" & Thread name: "+Thread.currentThread().getName(),
//                                Toast.LENGTH_SHORT).show();
//                new MainActivity().AddLocationUpdatetoFirebase(location);

                //AddLocationUpdatetoFirebase(location);
                if (activity.map!=null&&result.getLastLocation()!=null){
                    activity.map.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            AlertMapActivity activity=activityWeakReference.get();
            if (activity!=null){
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
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
