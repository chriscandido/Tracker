package com.envisage.tracker.db;

import android.location.Location;

public class UserDBLocationHelper {

    private Location location;

    public UserDBLocationHelper(Location location){
        this.location = location;
    }

    public Location getLocation(){
        return location;
    }

    public double getLatitude(){
        return location.getLatitude();
    }

    public double getLongitude(){
        return location.getLongitude();
    }

    public long getTime(){
        return location.getTime();
    }

}
