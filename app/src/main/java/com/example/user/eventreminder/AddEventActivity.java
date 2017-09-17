package com.example.user.eventreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.eventreminder.alarm.Alarm;
import com.example.user.eventreminder.realm.RealmGetterSetter;
import com.example.user.eventreminder.realm.RealmHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import io.realm.Realm;

public class AddEventActivity extends AppCompatActivity {
    private Realm realm;
    private CustomListAdapter adapter;
    private EditText dayET, monthET, yearET, hourET, minuteET;
    private EditText descriptionET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        init();
        //for preventing wrong date and time
        inputEventNumbers();

    }

    public void init(){
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        dayET = (EditText) findViewById(R.id.dayId);
        monthET = (EditText) findViewById(R.id.monthId);
        yearET = (EditText) findViewById(R.id.yearId);
        hourET = (EditText) findViewById(R.id.hourId);
        minuteET = (EditText) findViewById(R.id.minuteId);
        descriptionET = (EditText) findViewById(R.id.descId);
    }
    //for preventing wrong date and time
    public void inputEventNumbers(){
        //automaticaly move to next EditText
        autoEditTextChange(dayET,monthET, 2);
        autoEditTextChange(monthET, yearET, 2);
        autoEditTextChange(yearET, hourET, 4);
        autoEditTextChange(hourET, minuteET, 2);
        autoEditTextChange(minuteET, descriptionET, 2);

        // for seting date and time number range ex.(month 1 - 12)
        dayET.setFilters(new InputFilter[]{new InputFilterDateTime(0,31)});
        monthET.setFilters(new InputFilter[]{new InputFilterDateTime(0,12)});
        yearET.setFilters(new InputFilter[]{new InputFilterDateTime(1,3000)});
        hourET.setFilters(new InputFilter[]{new InputFilterDateTime(0,23)});
        minuteET.setFilters(new InputFilter[]{new InputFilterDateTime(0,59)});
    }

    //Save Event (button)
    public void saveClick(View view) throws ParseException {
        String dat_str = dayET.getText().toString();
        String month_str = monthET.getText().toString();
        String year_str = yearET.getText().toString();
        String hour_str = hourET.getText().toString();
        String minute_str = minuteET.getText().toString();

        //take event record current date and time
        String dt = new Date().toString();
        final String subDt = dt.substring(0, 16);
        final String subYear = dt.substring(30,34);

        //check whether date and time fields are full
        if(!dat_str.isEmpty() && !month_str.isEmpty() && !year_str.isEmpty() && !hour_str.isEmpty() && !minute_str.isEmpty()){

            //if you input date and time 01/01/2018 01:08 or 1/1/2018 1:8 both will work
            if(dat_str.charAt(0) == '0' ){
                dat_str.replaceFirst("0","");
            }
            if(month_str.charAt(0) == '0'){
                month_str.replaceFirst("0","");
            }
            if(hour_str.charAt(0) == '0'){
                hour_str.replaceFirst("0","");
            }
            if(minute_str.charAt(0) == '0'){
                minute_str.replaceFirst("0","");
            }

            final String evDateTime = dat_str+"/"+month_str+"/"+year_str+"  "+hour_str+":"+minute_str;
            final String evDesc_str = descriptionET.getText().toString();
            final String curDateTime = subDt+" "+subYear;

           //put data into Realm database
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmGetterSetter getterSetter = realm.createObject(RealmGetterSetter.class);
                    getterSetter.setEvDateTime(evDateTime);
                    if(evDesc_str.isEmpty()){
                        getterSetter.setDescription("No_description...");
                    }else {
                        getterSetter.setDescription(evDesc_str);
                    }
                    getterSetter.setCurDateTime(curDateTime);
                }
            });

            adapter = new CustomListAdapter(this, realm);
            MainActivity.listView.setAdapter(adapter);
            finish();

            //Set Alarm
            setAlarm();

        }else {
            Toast.makeText(this, "Fill all fields",Toast.LENGTH_SHORT).show();
        }

    }

    //Set Alarm for created Event
    public void setAlarm() throws ParseException {

        Calendar calendar = Calendar.getInstance();
        ArrayList<String> allListOfDate;
        RealmHelper helper = new RealmHelper(realm);
        allListOfDate = helper.retrieveEvDateTime();
        int index = helper.retrieveEvDateTime().size()-1;

        //check if you don't save past date and time for false notification
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date curTime = new Date();
        String curDT_str = sdf.format(curTime);

        Date curDT = sdf.parse(curDT_str);
        Date eventDT = sdf.parse(allListOfDate.get(index));

        if(eventDT.compareTo(curDT) < 0){
            Toast.makeText(this, "You create past Event",Toast.LENGTH_SHORT).show();
        } else {

            //take just created event date and time line from database
            String firstRing = allListOfDate.get(index);
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

            Intent intent = new Intent(this, Alarm.class);
            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), index, intent,PendingIntent.FLAG_ONE_SHOT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pi);

        }

    }

    // if write 2 character in day EditText automaticaly move to next EditText
    public void autoEditTextChange(final EditText et1, final EditText et2, final int testSize){
        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(et1.getText().toString().length()== testSize)     //size as per your requirement
                {
                    et2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {}

        });

    }

}
