package edu.temple.convoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.server.response.FastJsonResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nambimobile.widgets.efab.ExpandableFab;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, JoinCreateFragment.JoinCreateInterface, EndFragment.EndConvoyInterface {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    static final String EXTRA_CONVOY_ID = "edu.temple.convoy.CONVOY_ID";
    static final String EXTRA_OWNER = "edu.temple.convoy.OWNER";

    String username, sessionKey;
    SupportMapFragment mapFragment;
    private FusedLocationProviderClient locationProviderClient;
    boolean locationPermissionGranted;
    FragmentManager manager;
    JoinCreateFragment joinCreateFragment;
    EndFragment endConvoyFragment;
    ActionBar actionBar;

    String convoyID;
    RequestQueue reQueue;
    String status;
    boolean convoyOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        manager = getSupportFragmentManager();

        mapFragment = (SupportMapFragment) manager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        actionBar = getSupportActionBar();


        endConvoyFragment = EndFragment.newInstance();

        if (savedInstanceState == null) {
            convoyOwner = false;
            Intent intent = getIntent();
            username = intent.getStringExtra(MainActivity.EXTRA_USERNAME);
            sessionKey = intent.getStringExtra(MainActivity.EXTRA_SESSION_KEY);
            joinCreateFragment = JoinCreateFragment.newInstance();
            manager
                    .beginTransaction()
                    .add(R.id.mapContainer, joinCreateFragment, "joinCreateContainer")
                    .commit();

        } else {
            Log.d("SENWARE", "Loaded from saved instance state");
            convoyOwner = savedInstanceState.getBoolean(EXTRA_OWNER);
            username = savedInstanceState.getString(MainActivity.EXTRA_USERNAME);
            sessionKey = savedInstanceState.getString(MainActivity.EXTRA_SESSION_KEY);
            convoyID = savedInstanceState.getString(EXTRA_CONVOY_ID);
            if (convoyID == null) {
                convoyOwner = false;
                if(!(manager.findFragmentById(R.id.mapContainer) instanceof JoinCreateFragment)) {
                    Log.d("SENWARE", "Restoring Join/Create Fragment");
                    joinCreateFragment = JoinCreateFragment.newInstance();
                    manager
                            .beginTransaction()
                            .add(R.id.mapContainer, joinCreateFragment, "joinCreateContainer")
                            .commit();
                }
            } else {
                actionBar.setTitle(getString(R.string.convoy_id_tag) + convoyID);
                if (convoyOwner) {
                    if (!(manager.findFragmentById(R.id.mapContainer) instanceof EndFragment))
                    manager
                            .beginTransaction()
                            .add(R.id.mapContainer, endConvoyFragment)
                            .commit();
                }
            }
        }

        FloatingActionButton logOffButton = findViewById(R.id.logOffButton);
        logOffButton.setOnClickListener(v -> logout());

        TextView mapDebugTextView = findViewById(R.id.mapDebugTextView);
        String debugText = "Username: " + username + "\nSession Key: " + sessionKey;
        Log.d("SENWARE", "Session key: " + sessionKey);
        Log.d("SENWARE", "Convoy Owner? " + convoyOwner);
        mapDebugTextView.setText(debugText);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        getLocationPermission();
        if (locationPermissionGranted) {
            locationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                map.addMarker(new MarkerOptions()
                        .position(pos)
                        .flat(true));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f));
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
        Log.d("SENWARE", "Saved instance state");
        outState.putString(MainActivity.EXTRA_USERNAME, username);
        Log.d("SENWARE", "Saved Username: " + username);
        outState.putString(MainActivity.EXTRA_SESSION_KEY, sessionKey);
        Log.d("SENWARE", "Saved Session Key: " + sessionKey);
        outState.putString(EXTRA_CONVOY_ID, convoyID);
        Log.d("SENWARE", "Saved Convoy ID: " + convoyID);
        outState.putBoolean(EXTRA_OWNER, convoyOwner);
        Log.d("SENWARE", "Saved Convoy Owner?: " + convoyOwner);
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

    @Override
    public void joinConvoy() {
        Log.d("SENWARE", "joinConvoy Called");
        final EditText entry = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(R.string.join_convoy_message)
                .setTitle(R.string.join_convoy)
                .setView(entry);
        builder.setPositiveButton(R.string.join, (dialog, which) -> {
            final String convoyIDToSend = entry.getText().toString();
            Log.d("SENWARE", convoyIDToSend);
            // TODO: Contact API to join convoy
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            // do nothing
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void createConvoy() {
        final TextView convoyIDView = new TextView(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        StringRequest request = new StringRequest(Request.Method.POST, MainActivity.CONVOY_URL,
                response -> {
                    try {
                        JSONObject JSONResponse = new JSONObject(response);
                        status = JSONResponse.getString(MainActivity.STATUS);
                        if (status.equals(MainActivity.SUCCESS)) {
                            convoyID = JSONResponse.getString(MainActivity.CONVOY_ID);
                            final String concatConvoyID = getString(R.string.convoy_id_tag) + convoyID;
                            convoyIDView.setText(concatConvoyID);
                            builder
                                    .setMessage(R.string.start_convoy_message)
                                    .setTitle(R.string.start_convoy)
                                    .setView(convoyIDView);
                            builder.setPositiveButton(R.string.start, (dialog, which) -> {
                                actionBar.setTitle(concatConvoyID);
                                manager
                                        .beginTransaction()
                                        .replace(R.id.mapContainer, endConvoyFragment, "endConvoyFragment")
                                        .commit();
                            });
                            convoyOwner = true;
                            Log.d("SENWARE", "Convoy created, id: " + convoyID);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                                convoyIDView.setText(JSONResponse.getString(MainActivity.MESSAGE));
                                builder
                                        .setMessage(R.string.start_convoy_failed)
                                        .setTitle(R.string.start_convoy)
                                        .setView(convoyIDView);
                                builder.setPositiveButton(R.string.start, (dialog, which) -> {
                                    // do nothing
                            });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    convoyIDView.setText(R.string.network_error);
                    builder
                            .setMessage(R.string.start_convoy_failed)
                            .setTitle(R.string.start_convoy)
                            .setView(convoyIDView);
                    builder.setPositiveButton(R.string.start, (dialog, which) -> {
                        // do nothing
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(MainActivity.ACTION, MainActivity.CREATE);
                params.put(MainActivity.USERNAME, username);
                params.put(MainActivity.SESSION_KEY, sessionKey);
                return params;
            }
        };
        reQueue = Volley.newRequestQueue(this);
        reQueue.add(request);
    }

    @Override
    public void endConvoy() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(R.string.end_message)
                .setTitle(R.string.end_convoy);
        builder.setPositiveButton(R.string.end, (dialog, which) -> {
            killConvoy();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.cancel();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void killConvoy() {
        StringRequest request = new StringRequest(Request.Method.POST, MainActivity.CONVOY_URL,
                response -> {
                    try {
                        JSONObject JSONResponse = new JSONObject(response);
                        status = JSONResponse.getString(MainActivity.STATUS);
                        if (status.equals(MainActivity.SUCCESS)) {
                            convoyID = null;
                            convoyOwner = false;
                            actionBar.setTitle(R.string.convoy);
                            joinCreateFragment = JoinCreateFragment.newInstance();
                            manager
                                    .beginTransaction()
                                    .replace(R.id.mapContainer, joinCreateFragment, "joinCreateFragment")
                                    .commit();
                        } else {
                            // TODO idk maybe add another another dialog..???
                            Log.d("SENWARE", "@killConvoy" + JSONResponse.getString(MainActivity.MESSAGE));
                            Log.d("SENWARE", "@killConvoy, session key: " + sessionKey);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // TODO idk maybe add another dialog?
                    Log.d("SENWARE", "Network Error @killConvoy");
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(MainActivity.ACTION, MainActivity.END);
                params.put(MainActivity.USERNAME, username);
                params.put(MainActivity.SESSION_KEY, sessionKey);
                params.put(MainActivity.CONVOY_ID, convoyID);
                return params;
            }
        };
        reQueue = Volley.newRequestQueue(this);
        reQueue.add(request);
    }

    public void logout() {
        if(convoyOwner) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setMessage(R.string.logout_message)
                    .setTitle(R.string.end_convoy);
            builder.setPositiveButton(R.string.end, (dialog, which) -> {
                killConvoy();
            });
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
                dialog.cancel();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            logoff();
        }
    }

    private void logoff() {
        StringRequest request = new StringRequest(Request.Method.POST, MainActivity.ACCOUNT_URL,
                response -> {
                    try {
                        JSONObject JSONResponse = new JSONObject(response);
                        status = JSONResponse.getString(MainActivity.STATUS);
                        if (status.equals(MainActivity.SUCCESS)){
                            Log.d("SENWARE", "Logout Success");
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d("SENWARE", "Logout Error");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.d("SENWARE", "Logout Network Error");
                }) {
            // send parameters here
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(MainActivity.ACTION, MainActivity.LOGOUT);
                params.put(MainActivity.USERNAME, username);
                params.put(MainActivity.SESSION_KEY, sessionKey);
                return params;
            }
        };
        reQueue = Volley.newRequestQueue(this);
        reQueue.add(request);
    }
}

