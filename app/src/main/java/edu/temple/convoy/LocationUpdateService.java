package edu.temple.convoy;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationUpdateService extends Service {

    static final int MSG_UPDATE_AVAILABLE = 1;
    static final int MSG_REGISTER_CLIENT = 2;
    static final int MSG_REMOVE_CLIENT = 3;

    private final int NOTIFICATION_ID = 100;
    private final String CHANNEL_ID = "Location Service Channel";
    private final String CHANNEL_NAME = "Location";

    private final int MIN_DISTANCE = 10;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location mLocation;

    private NotificationManager notificationManager;
    private Notification notification;
    private NotificationChannel notificationChannel;
    private Intent intent;
    private PendingIntent pendingIntent;

    protected Messenger client;
    private Messenger messenger;


    public LocationUpdateService() {
    }

    class ReplyHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    client = msg.replyTo;
                    Log.d("SENWARE", "MSG REGISTER CLIENT");
                    break;
                case MSG_REMOVE_CLIENT:
                    client = null;
                    Log.d("SENWARE", "MSG REMOVE CLIENT");
                    stopSelf();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        messenger = new Messenger(new ReplyHandler());
        return messenger.getBinder();
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        makeNotification();
        startForeground(NOTIFICATION_ID, notification);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> mLocation = location);
        startLocationUpdates();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("SENWARE", "Service onDestroy called");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    mLocation = location;
                    try {
                        Log.d("SENWARE", "Client null?: " + (client == null));
                        if (client != null) {
                            Message message = Message.obtain(null, MSG_UPDATE_AVAILABLE, mLocation);
                            client.send(message);
                        } else {
                            break;
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setSmallestDisplacement(MIN_DISTANCE);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void makeNotification() {
        makeChannel();
        intent = new Intent(this, LocationUpdateService.class);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle(getText(R.string.notification_title))
                        .setContentText(getText(R.string.notification_message))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .setTicker(getText(R.string.ticker_text))
                        .build();
    }

    private void makeChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }
}