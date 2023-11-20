package com.example.cpen321mappost;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessageHandler extends FirebaseMessagingService {
    private final ProfileManager profileManager = new ProfileManager();
    private final String TAG = "Message Handler";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // Handle FCM messages here.
        // If the application is in the foreground, handle both data and notification payload.
        // If the application is in the background, the system automatically handles the notification payload.

        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

    }

    @Override
    public void onNewToken(@NonNull String token) {
        // Send the token to your server for sending notifications.

        Log.d(TAG, "NEW TOKEN IS RETRIEVED: " + token);

        User user = User.getInstance();

        profileManager.getUserData(user, null, new User.UserCallback() {
            @Override
            public String onSuccess(User user) {

                user.setToken(token);

                profileManager.putUserData(user, null, new User.UserCallback() {
                    @Override
                    public String onSuccess(User user) {

                        Log.d(TAG, "TOKEN IS UPDATED");

                        return null;

                    }

                    @Override
                    public void onFailure(Exception e) {

                        Log.d(TAG, "FAILED UPDATE NEW TOKEN");

                    }
                });

                return null;

            }

            @Override
            public void onFailure(Exception e) {

                Log.d(TAG, "Falied to get user data: " + e.toString());

            }
        });
    }

    private void sendNotification(String messageTitle, String messageBody) {

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo, notification channel is needed.
        NotificationChannel channel = new NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(0, notificationBuilder.build());

    }

}
