package com.envisage.tracker.utils;

public class HaversineFormula {

    double initialLat, initialLon, finalLat, finalLon;


    public HaversineFormula(double initialLat, double initialLon, double finalLat, double finalLon){
        this.initialLat = initialLat;
        this.initialLon = initialLon;
        this.finalLat = finalLat;
        this.finalLon = finalLon;
    }

    public double getDistance(){
        int radius = 6371;

        double dLat = toRadians(finalLat - initialLat);
        double dLon = toRadians(finalLon - initialLon);

        double a = Math.sin(dLat/2)*Math.sin(dLat/2) + Math.sin(dLon/2)*Math.sin(dLon/2)
                *Math.cos(toRadians(initialLat))*Math.cos(toRadians(finalLat));
        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return (radius*c);
    }

    public double toRadians(double degree){
        return (degree*(Math.PI/180));
    }
}
