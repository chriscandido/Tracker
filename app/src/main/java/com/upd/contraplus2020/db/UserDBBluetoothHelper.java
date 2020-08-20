package com.upd.contraplus2020.db;

import android.content.Context;

public class UserDBBluetoothHelper {

    Context context;
    private String mcaddress;
    private Float distance;
    private Integer createdAd;

    public UserDBBluetoothHelper(String mcaddress, Float distance, Integer createdAd) {
        this.mcaddress = mcaddress;
        this.distance = distance;
        this.createdAd = createdAd;
    }

    public String getMcaddress() {
        return mcaddress;
    }

    public void setMcaddress(String mcaddress) {
        this.mcaddress = mcaddress;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Integer getCreatedAd() {
        return createdAd;
    }

    public void setCreatedAd(Integer createdAd) {
        this.createdAd = createdAd;
    }
}
