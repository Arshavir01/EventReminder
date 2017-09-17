package com.example.user.eventreminder.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.user.eventreminder.realm.RealmHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import io.realm.Realm;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 15.09.2017.
 */

public class BootReciever extends BroadcastReceiver {
    private Realm realm;
    @Override
    public void onReceive(Context context, Intent intent) {
        Realm.init(context);
        realm = Realm.getDefaultInstance();

        try {
            //after rebooting device all Events automatically set to the Alarm
            resetAlarm(context);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //sending notification for All missed Event
         missedNotification(context);
    }

    //reset all events after rebooting device
    public void resetAlarm(Context context) throws ParseException {

        Calendar calendar = Calendar.getInstance();
        ArrayList<String> allListOfDate;
        RealmHelper helper = new RealmHelper(realm);
        allListOfDate = helper.retrieveEvDateTime();

        //boot time
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date bootTime = new Date();
        String bootDT_str = sdf.format(bootTime);

        //take data from database line to line
        for(int i = 0; i < helper.retrieveEvDateTime().size();i++){

            Date bootDT = sdf.parse(bootDT_str);
            Date eventTime = sdf.parse(allListOfDate.get(i));

            //checking for false notification (if you have only past events in database, system can send you one false notification)
            if(bootDT.compareTo(eventTime) <= 0){

                String firstRing = allListOfDate.get(i);
                StringTokenizer tokenizer = new StringTokenizer(firstRing);

                String   day = tokenizer.nextToken("/");
                String  month = tokenizer.nextToken("/");
                String  year = tokenizer.nextToken("/ ");

                String hour = tokenizer.nextToken(" :");
                String minute = tokenizer.nextToken(" :");

                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
                calendar.set(Calendar.MONTH, Integer.parseInt(month)-1);
                calendar.set(Calendar.YEAR, Integer.parseInt(year));

                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
                calendar.set(Calendar.MINUTE, Integer.parseInt(minute));

                calendar.set(Calendar.SECOND, 2);

                Intent intent = new Intent(context, Alarm.class);
                final int sysSec = (int)((System.currentTimeMillis()/1000)%3600);
                PendingIntent pi = PendingIntent.getBroadcast(context,sysSec,intent,PendingIntent.FLAG_ONE_SHOT);

                AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pi);
            }

        }

    }

    //send missed notifications when device turn off and on
    public void missedNotification(Context context){
        ArrayList<String> allListOfDate;
        RealmHelper helper = new RealmHelper(realm);
        allListOfDate = helper.retrieveEvDateTime();
        int counter = 0; //missed Event counter

        try {
            //device booting date time
            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date bootTime = new Date();
            String bootDT_str = sdf.format(bootTime);
            Date bootDT = sdf.parse(bootDT_str);

            //take shutDown date and time from SharedPreferences
            SharedPreferences preferences = context.getSharedPreferences("myData", MODE_PRIVATE);
            String shutDownDT_str = preferences.getString("shutDownKey", "");
            Date shatDownDT = sdf.parse(shutDownDT_str);

            //check for every event between booting time and shut down time
            for(int i = 0; i < helper.retrieveEvDateTime().size();i++ ){
                //every event date and time from database
                Date eventDT = sdf.parse(allListOfDate.get(i));
                if(!shutDownDT_str.isEmpty()){
                    if( shatDownDT.compareTo(eventDT) <= 0 && eventDT.compareTo(bootDT) <= 0) {
                        //for every missed event
                        counter++;
                    }

                }
            }

            if(counter != 0){
                Alarm alarm = new Alarm();
                alarm.notification(context, "You have "+String.valueOf(counter)+" missed event", "Missed Event");
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
