package com.example.dp863.crimespot;

/**
 * Created by dp863 on 2/22/2017.
 */

public class Crime {
    String date;
    String time;
    String address;
    String type;
    String status;
    String latitude;
    String longitude;


    // A no-agrs constructor is required for deserialization
    public Crime(){

    }

    public Crime( String date, String time, String address, String type, String status, String latitude, String longitude){
        this.date = date;
        this.time = time;
        this.address = address;
        this.status = status;
        this.type = type;
        this.latitude =latitude;
        this.longitude = longitude;

    }

    // accessor methods

    public String getDate() { return date;}
    public String getTime() { return time;}
    public String getAddress() { return address;}
    public String getStatus() { return status;}
    public String getType() { return type;}
    public String getLatitude(){
        return latitude;
    }
    public String getLongitude(){ return longitude; }

}




