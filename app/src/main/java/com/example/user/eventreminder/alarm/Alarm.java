package com.example.user.eventreminder.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import com.example.user.eventreminder.MainActivity;
import com.example.user.eventreminder.R;

/**
 * Created by user on 14.09.2017.
 */

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Vibrator v = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        v.vibrate(1000);
        notification(context, "You have an event today","Event Reminder");
    }

    public void notification(Context context, String textEv, String titleEv) {
        //for notification id
       final int sysSec = (int)((System.currentTimeMillis()/1000)%3600);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(titleEv)
                .setContentText(textEv)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setSmallIcon(R.drawable.notification);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, sysSec, intent, 0);;

        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(sysSec, builder.build());
    }
}
