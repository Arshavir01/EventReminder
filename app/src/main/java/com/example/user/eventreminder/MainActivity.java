package com.example.user.eventreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.eventreminder.alarm.Alarm;
import com.example.user.eventreminder.realm.RealmGetterSetter;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private Realm realm;
    static ListView listView;
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        adapter = new CustomListAdapter(this, realm);
        listView.setAdapter(adapter);

        //Press long for deleting Event
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                alterDialog(position);
                return true;
            }
        });

    }

    public void init(){
        listView = (ListView)findViewById(R.id.listId);
        Realm.init(this);
        realm = Realm.getDefaultInstance();

    }

    public void addEventClick(View view) {
         Intent intent = new Intent(this, AddEventActivity.class);
         startActivity(intent);

    }

    //Set Alter Dialog for deleting an Event
    public void alterDialog(final int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this Event ?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this,"Event Deleted", Toast.LENGTH_SHORT).show();
                final RealmResults<RealmGetterSetter> results = realm.where(RealmGetterSetter.class).findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        results.deleteFromRealm(pos);
                        adapter = new CustomListAdapter(MainActivity.this, realm);
                        listView.setAdapter(adapter);
                        cancelAlarm(pos);
                    }
                });

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert =  builder.create();
        alert.show();

    }//end Alert Dialog

    //Cancel deleted event
    public void cancelAlarm(int alarmId){
        Intent intent = new Intent(this, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),alarmId,intent,PendingIntent.FLAG_ONE_SHOT);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(pi);

    }

}
