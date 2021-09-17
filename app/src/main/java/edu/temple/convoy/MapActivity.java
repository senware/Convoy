package edu.temple.convoy;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "SEN";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    String username, sessionKey;
    SupportMapFragment mapFragment;
    private GoogleMap map;
    private FusedLocationProviderClient locationProviderClient;
    boolean locationPermissionGranted;

    @SuppressLint("MissingPermission")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            username = intent.getStringExtra(MainActivity.EXTRA_USERNAME);
            sessionKey = intent.getStringExtra(MainActivity.EXTRA_SESSION_KEY);
        } else {
            username = savedInstanceState.getString(MainActivity.EXTRA_USERNAME);
            sessionKey = savedInstanceState.getString(MainActivity.EXTRA_SESSION_KEY);
        }

        TextView mapDebugTextView = findViewById(R.id.mapDebugTextView);
        String debugText = "Username: " + username + "\nSession Key: " + sessionKey;
        mapDebugTextView.setText(debugText);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        getLocationPermission();
        if (locationPermissionGranted) {
            locationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                    map.addMarker(new MarkerOptions()
                            .position(pos)
                            .flat(true));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f));
                }
            });
        }
    }


    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("USERINFO", "Saved instance state");
        outState.putString(MainActivity.EXTRA_USERNAME, username);
        Log.d("USERINFO", "USERNAME KEY: " + MainActivity.EXTRA_USERNAME);
        Log.d("USERINFO", "Username: " + username);
        outState.putString(MainActivity.EXTRA_SESSION_KEY, sessionKey);
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }
}

