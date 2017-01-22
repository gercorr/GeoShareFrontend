package com.logicalpanda.geoshare.pojos;

import com.google.android.gms.maps.model.LatLng;
import com.logicalpanda.geoshare.other.Globals;

public class Note {

    public Note()
    {}

    public Note(LatLng latLng, String text)
    {
        setLatitude(latLng.latitude);
        setLongitude(latLng.longitude);
        setText(text);
        setUser(Globals.getCurrentUser());
    }

    private int id;

    private String text;

    private double latitude;

    private double longitude;

    private User user;

    public int getId( ) {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText( ) {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
