package app.splitbit.Services;

import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.ServerValue;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;

import app.splitbit.R;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("", "Message data payload: " + remoteMessage.getData());



            //--
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(MyFirebaseMessagingService.this,"splitbit")
                            .setContentTitle(remoteMessage.getData().get("title").toString())
                            .setContentText(remoteMessage.getData().get("body").toString())
                            .setSmallIcon(R.drawable.ic_app_notification)
                            .setAutoCancel(true)
                            .setVibrate(new long[]{250,250,250,250})
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("body").toString()));

            //-- Create Notification
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Date now = new Date();
            notificationManager.notify(Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now)), notificationBuilder.build());



        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("", "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
