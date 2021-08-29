package recharge.com.myrechargegallery;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import android.app.NotificationChannel;
import android.os.Build;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    private static final String TAG = "MyFirebaseMsgService";
    public static String MYValue = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        //Log.d(TAG, "From: " + remoteMessage.getFrom());
        //Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        String title = "", message = "";
        try {
            //String title = remoteMessage.getNotification().getTitle();
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("message");
        } catch (Exception e) {
        }

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        NotificationManager mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SplashActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.logo)  // the status icon
                .setTicker(title)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                //.setContentTitle(getText(R.string.app_name))  // the label of the entry
                .setContentTitle(title)  // the label of the entry
                .setContentText(message)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNM.createNotificationChannel(channel);
        }
        // Send the notification.
        mNM.notify(R.string.app_name, notification);
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }
}