package com.envisage.tracker.utils;

import android.location.Location;

public class LocationHolder {

    private Location location;

    public LocationHolder(Location location){
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
