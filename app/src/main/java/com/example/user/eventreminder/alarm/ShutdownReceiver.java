package com.example.user.eventreminder.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 16.09.2017.
 */

public class ShutdownReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date shautDownDT = new Date();
        String shutDownDT_str = sdf.format(shautDownDT);

        // Save shut down date and time into sharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("myData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("shutDownKey", shutDownDT_str);
        editor.commit();

    }
}
