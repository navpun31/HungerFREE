package com.spectators.hungerfree;

import android.net.Uri;

import java.sql.Time;
import java.util.Date;

public class Donation {
    public String email;
    public String quantity;
    public String food;
    public String imageUri;
    public String date;
    public String time;

    //location
    public double lat;
    public double lng;
    public String locName;
    public String locAddress;

    public Donation(){
    }
}
