package com.example.user.eventreminder.realm;

import io.realm.RealmObject;

/**
 * Created by user on 13.09.2017.
 */

public class RealmGetterSetter extends RealmObject {

    private String evDateTime;
    private String description;
    private String curDateTime;

    public String getEvDateTime() {
        return evDateTime;
    }

    public void setEvDateTime(String evDateTime) {
        this.evDateTime = evDateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurDateTime() {
        return curDateTime;
    }

    public void setCurDateTime(String curDateTime) {
        this.curDateTime = curDateTime;
    }
}
