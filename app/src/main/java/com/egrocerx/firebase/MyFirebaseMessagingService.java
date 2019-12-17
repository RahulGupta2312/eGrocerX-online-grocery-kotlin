package com.egrocerx.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.egrocerx.R;
import com.egrocerx.ui.splash.MainActivity;
import com.egrocerx.util.AppPreference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String TAG = "FCMNotification";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getData().toString());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            showNotification(this, remoteMessage.getData());
        }

        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        AppPreference.getInstance().saveFcmToken(s);
    }

    private void sendNotification(Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("afdsd")
                        .setContentText("SCsac")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(123,
                notificationBuilder.build());
    }

    public void showNotification(Context context, Map<String, String> data) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "eCommerceX Notification";
        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(data.get("title"))
                .setContentText(data.get("body"))
                .setStyle(new NotificationCompat.BigTextStyle());

        Intent intent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }


    private class SendNotification extends AsyncTask<String, Void, Bitmap> {

        Context context;
        Map<String, String> data;

        public SendNotification(Context context, Map<String, String> data) {
            super();
            this.context = context;
            this.data = data;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;


            try {

                URL url = new URL(data.get("image"));
//                Log.e("fcm", url.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);
            try {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                int notificationId = 1;
                String channelId = "channel-01";
                String channelName = "eCommerceX Notification";
                int importance = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    importance = NotificationManager.IMPORTANCE_HIGH;
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel mChannel = new NotificationChannel(
                            channelId, channelName, importance);
                    notificationManager.createNotificationChannel(mChannel);
                }

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_home)
                        .setContentTitle(data.get("title"))
                        .setContentText(data.get("body"))
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setStyle(new NotificationCompat.BigTextStyle());

                if (result != null) {
                    mBuilder.setLargeIcon(result);
                }

                Intent intent = new Intent(context, MainActivity.class);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntent(intent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
                mBuilder.setContentIntent(resultPendingIntent);

                notificationManager.notify(notificationId, mBuilder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

