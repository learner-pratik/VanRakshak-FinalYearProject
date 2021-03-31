package com.example.forestofficerapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.mapbox.mapboxsdk.geometry.LatLng;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.crossbar.autobahn.websocket.WebSocketConnection;
import io.crossbar.autobahn.websocket.WebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.exceptions.WebSocketException;
import io.crossbar.autobahn.websocket.types.ConnectionResponse;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;

public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PermissionsListener {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private Map<String, List<String>> animalMap = new HashMap<String, List<String>>();
    private String value1;
    private Handler handler;
    private Runnable runnable;
    public static Map<String, List<String>> animal_info = new HashMap<>();
    public static Map<String, List<String>> animal_location_info = new HashMap<>();

    // apiCallTime is the time interval when we call the API in milliseconds, by default this is set
    // to 2000 and you should only increase the value, reducing the interval will only cause server
    // traffic, the latitude and longitude values aren't updated that frequently.
    private int apiCallTime = 13000;

    // Map variables
    private MapView mapView;
    private MapboxMap map;

    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private LocationChangeListeningActivityLocationCallback callback =
            new LocationChangeListeningActivityLocationCallback(this);

    private WebSocketConnection webSocketConnection;
    private static final String SOCKET_URL = "ws://vanrakshak.herokuapp.com/animallist_socket";
    private static final String DEMO_URL = "ws://forestwebsocket.herokuapp.com/test";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HashMap<String, List<String>> map = (HashMap<String, List<String>>) getIntent().getSerializableExtra("map");
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Log.d("this is last hope", (String) pair.getKey());
            animalMap.put((String) pair.getKey(), convertObjectToList(pair.getValue()));
            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException.
        }

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_map);

        topAppBar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawerLayout);

        setSupportActionBar(topAppBar);
        setNavigationViewListener();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Initialize the MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        startWebSocket();
    }

    private void startWebSocket() {

        webSocketConnection = new WebSocketConnection();
        try {
            webSocketConnection.connect(SOCKET_URL, new WebSocketConnectionHandler(){
                @Override
                public void onConnect(ConnectionResponse response) {
                    super.onConnect(response);
                    Log.d(LOG_TAG, "Connected to server");

                    mapView.getMapAsync(mapboxMap -> {

                        map = mapboxMap;

                        mapboxMap.setStyle(Style.SATELLITE_STREETS, style -> {
                            enableLocationComponent(style);
                            initSpaceStationSymbolLayer(style);
                            callApi();
                            Toast.makeText(MapActivity.this, R.string.space_station_toast, Toast.LENGTH_SHORT).show();
                        });
                    });
                }

                @Override
                public void onClose(int code, String reason) {
                    super.onClose(code, reason);
                    Log.d(LOG_TAG, "Connection closed");
                }

                @Override
                public void onMessage(String payload) {
                    super.onMessage(payload);
                    Log.d(LOG_TAG, payload);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(payload);

                        JSONArray jsonArray = jsonObject.getJSONArray("animals");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            Double latitude = Double.parseDouble(object.getString("latitude"));
                            Double longitude = Double.parseDouble(object.getString("longitude"));
                            String id = object.getString("id");

                            updateMarkerPosition(new LatLng(latitude, longitude), "sid" + id);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
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

    private void callApi() {

// A handler is needed to called the API every x amount of seconds.
        handler = new Handler();
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();

                for (Map.Entry<String, List<String>> entry : animalMap.entrySet()) {
                    String animal = entry.getKey();
                    for (String id : entry.getValue()) {
                        JSONObject object = new JSONObject();
                        try {
                            object.put("animal", animal);
                            object.put("id", id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        jsonArray.put(object);
                    }
                }

                try {
                    jsonObject.put("animals", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                webSocketConnection.sendMessage(jsonObject.toString());
                //tm = 0;
                handler.postDelayed(this, apiCallTime);
            }
        };
        // The first time this runs we don't need a delay so we immediately post.
        handler.post(runnable);
    }

    private void initSpaceStationSymbolLayer(@NonNull Style style) {
        // h<key,value> --> key = animal_type --> value = List[animal id]
        int d=0;
        //int m=0;
        int ic=0;
        for(Map.Entry<String, List<String>> entry : animalMap.entrySet()) {
            String key = entry.getKey();
            Log.d("my debug key is ",key);
            d = getResources().getIdentifier("i_" + key, "drawable", getPackageName());
            style.addImage("icon"+ic,
                    BitmapFactory.decodeResource(
                            this.getResources(), d));
            for (String value : entry.getValue()) {
                Log.d("debug msg",value);
                style.addSource(new GeoJsonSource("sid"+value));
                style.addLayer(new SymbolLayer("layer-id"+value, "sid"+value).withProperties(
                        iconImage("icon"+ic),
                        iconIgnorePlacement(true),
                        iconAllowOverlap(true),
                        iconSize(.02f)
                ));
                //m++;
            }
            ic++;
        }
    }

    private void updateMarkerPosition(LatLng position,String source_id) {
        // This method is where we update the marker position once we have new coordinates. First we
        // check if this is the first time we are executing this handler, the best way to do this is
        // check if marker is null;
        if (map.getStyle() != null) {
            Log.d("debug source id ",source_id);
            GeoJsonSource spaceStationSource = map.getStyle().getSourceAs(source_id);
            if (spaceStationSource != null) {
                spaceStationSource.setGeoJson(FeatureCollection.fromFeature(
                        Feature.fromGeometry(Point.fromLngLat(position.getLongitude(), position.getLatitude()))
                ));
            }
        }
        // Lastly, animate the camera to the new position so the user
        // wont have to search for the marker and then return. uncomment bellow line
        //map.animateCamera(CameraUpdateFactory.newLatLng(position));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        // When the user returns to the activity we want to resume the API calling.
        if (handler != null && runnable != null) {
            handler.post(runnable);
        }
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
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
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

    public static List<String> convertObjectToList(Object obj) {
        List<?> list;
        List<String> r = new ArrayList<String>(1);
        if (obj.getClass().isArray()) {
            list = Arrays.asList((Object[])obj);
            List<String> value = new ArrayList<String>(list.size());
            for (Object object : list) {
                value.add(object != null ? object.toString() : null);
            }
            return(value);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>)obj);
            List<String> value = new ArrayList<String>(list.size());
            for (Object object : list) {
                value.add(object != null ? object.toString() : null);
            }
            return(value);
        }
        r.add(obj.toString());
        return r;
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
            map.getStyle(style -> enableLocationComponent(style));
        }
        else{
            Toast.makeText(this, "Permissions are not granted...", Toast.LENGTH_SHORT).show();
        }
    }

    private static class LocationChangeListeningActivityLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MapActivity> activityWeakReference;

        private LocationChangeListeningActivityLocationCallback(MapActivity activity) {
            this.activityWeakReference= new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            MapActivity activity=activityWeakReference.get();
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
            MapActivity activity=activityWeakReference.get();
            if (activity!=null){
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
