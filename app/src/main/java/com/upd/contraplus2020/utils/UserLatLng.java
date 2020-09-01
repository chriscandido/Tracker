package com.upd.contraplus2020.utils;

public class UserLatLng {

    public double latitude;
    public double longitude;
    public int createdAt;

    public UserLatLng(double latitude, double longitude, int createdAt) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }
}
