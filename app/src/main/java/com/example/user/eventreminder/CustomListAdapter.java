package com.example.user.eventreminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.eventreminder.realm.RealmHelper;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by user on 13.09.2017.
 */

public class CustomListAdapter extends BaseAdapter{
    private Context context;
    private Realm realm;
    private RealmHelper helper;
    private ArrayList<String> evDateTimeList;
    private ArrayList<String> descList ;
    private ArrayList<String> curDateTimeList;

    public CustomListAdapter(Context context, Realm realm) {
        this.context = context;
        this.realm = realm;
    }

    @Override
    public int getCount() {
        helper = new RealmHelper(realm);
        return helper.retrieveDesc().size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.custom_layout, null);

        TextView evDateTime = (TextView)convertView.findViewById(R.id.evDateTimeId);
        TextView evDesc = (TextView)convertView.findViewById(R.id.evDescId);
        TextView curDate = (TextView)convertView.findViewById(R.id.curDateTimeId);

        evDateTimeList = helper.retrieveEvDateTime();
        descList = helper.retrieveDesc();
        curDateTimeList = helper.retrieveDateTime();

        String[] evDateTimeArray = evDateTimeList.toArray(new String[evDateTimeList.size()]);
        evDateTime.setText(evDateTimeArray[position]);

        String[] evDescArray = descList.toArray(new String[descList.size()]);
        evDesc.setText(evDescArray[position]);

        String[] curDateArray = curDateTimeList.toArray(new String[curDateTimeList.size()]);
        curDate.setText(curDateArray[position]);

        return convertView;
    }
}
