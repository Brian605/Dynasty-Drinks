package com.returno.dynasty.services;

import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.returno.dynasty.R;

import java.util.Random;

public class MessageService extends FirebaseMessagingService {
    public MessageService() {
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title=remoteMessage.getNotification().getTitle();
        String body=remoteMessage.getNotification().getBody();

        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),getResources().getString(R.string.app_name));
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setSmallIcon(R.drawable.ic_baseline_local_drink_24);

        NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager!=null){
            manager.notify(new Random().nextInt(),builder.build());
        }
    }
}