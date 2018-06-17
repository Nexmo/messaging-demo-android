package com.nexmo.conversationdemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class StitchFirebaseService extends FirebaseMessagingService {
    private static final String NOTIFICATION_CHANNEL_ID = "misc";
    public final String TAG = this.getClass().getName();
    public static final String ACTION_BROADCAST_CID = "com.nexmo.sdk.core.gcm.BROADCAST_CID";
    public static final String MESSAGE_KEY_CID = "CONVERSATION_ID";

    public StitchFirebaseService() {}

    @Override
    public void onMessageReceived(RemoteMessage message){
        Map data = message.getData();
        Log.d(TAG, "Data: " + data.toString());

        String cid = null;
        if (data.containsKey(MESSAGE_KEY_CID)) {
            cid = (String) data.get(MESSAGE_KEY_CID);
            Log.d(TAG, "cid" + cid);
        }

        showNotification("New notification", "Message text");
        broadcastPayload(cid);
    }

    /**
     * Manually display a notification indicating the user that a message was received.
     * If a notification with the same id has already been posted by your application and
     * has not yet been canceled, it will be replaced by the updated information.
     *
     * @param notificationTitle The notification title.
     * @param payload           The notification message.
     */
    private void showNotification(final String notificationTitle, final String payload) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "misc")
                .setContentTitle(notificationTitle)
                .setContentText(payload)
                .setSmallIcon(R.drawable.ic_send_black_24dp)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Build and issue the notification. All pending notifications with same id will be canceled.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Miscellaneous", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    private void broadcastPayload(final String cid) {
        Intent intent = new Intent(ACTION_BROADCAST_CID);
        intent.putExtra(MESSAGE_KEY_CID, cid);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}
