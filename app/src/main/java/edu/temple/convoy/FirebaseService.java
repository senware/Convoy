package edu.temple.convoy;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseService extends FirebaseMessagingService {

    private LocalBroadcastManager broadcastManager;

    public FirebaseService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("SENWARE", "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("SENWARE", "Message data payload: " + remoteMessage.getData());
            handleNow(remoteMessage.getData().get("payload"));
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("SENWARE", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    public void handleNow(String payload) {
        try {
            JSONObject jsonPayload = new JSONObject(payload);
            if (jsonPayload.getString(MainActivity.ACTION).equals(MainActivity.UPDATE)) {
                String data = jsonPayload.getString(MainActivity.DATA);
                Intent updateIntent = new Intent(MapActivity.INTENT_UPDATE);
                updateIntent.putExtra(MapActivity.EXTRA_DATA, data);
                broadcastManager.sendBroadcast(updateIntent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}