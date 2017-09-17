package com.example.user.eventreminder.realm;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by user on 13.09.2017.
 */

public class RealmHelper {
   private Realm realm;

    public RealmHelper(Realm realm) {
        this.realm = realm;
    }

    //show Event date and time
    public ArrayList<String> retrieveEvDateTime(){

        ArrayList<String> evDateTimeList = new ArrayList<>();
        RealmResults<RealmGetterSetter> realmResults = realm.where(RealmGetterSetter.class).findAll();
        for(int i = 0; i < realmResults.size(); i++){
            evDateTimeList.add(realmResults.get(i).getEvDateTime());
        }
        return evDateTimeList;
    }


    //show Description
    public ArrayList<String> retrieveDesc(){
        ArrayList<String> descList = new ArrayList<>();

        RealmResults<RealmGetterSetter> realmResults = realm.where(RealmGetterSetter.class).findAll();
        for(int i = 0; i < realmResults.size(); i++){
            descList.add(realmResults.get(i).getDescription());
        }

        return descList;

    }

    //show current Date and Time
    public ArrayList<String> retrieveDateTime(){

        ArrayList<String> curDateList = new ArrayList<>();
        RealmResults<RealmGetterSetter> realmResults = realm.where(RealmGetterSetter.class).findAll();
        for(int i = 0; i < realmResults.size(); i++){
            curDateList.add(realmResults.get(i).getCurDateTime());
        }

        return curDateList;

    }
}
