package com.sindj.findmyfriends;

/**
 * Created by nirel on 05/09/2017.
 */

public class userLocation {

    private String name;
    private double Latitude;
    private double Longitude;

    public userLocation() {

    }

    public userLocation(String name, double latitude, double longitude) {
        this.name = name;
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    @Override
    public String toString() {
        return (name + ": [" + this.getLatitude() + "," + this.getLongitude()+ ']');
    }
}
