package com.example.dp863.crimespot;


import android.content.Intent;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;


import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private double mLatitude, mLongitude;
    private String crimeID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getFromDatabase();
    }



        // get Crime data back from Firebase Database
        public void getFromDatabase(){
            mFirebaseInstance = FirebaseDatabase.getInstance();
            mFirebaseDatabase = mFirebaseInstance.getReference("Crimes");

            Map<String, String> entry = new HashMap<String, String>();
            entry.put ("address", "0");
            entry.put ("date", "0");
            entry.put("latitude", "0");
            entry.put("longitude", "0");
            entry.put("status", "0");
            entry.put("time", "0");
            entry.put("type", "0");

            crimeID = mFirebaseDatabase.push().getKey();
            mFirebaseInstance.getReference("Crimes").child(crimeID).setValue(entry);

            mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(getApplicationContext(), "Database Loaded!",
                        Toast.LENGTH_LONG).show();
                Log.e("My App", "There are " + dataSnapshot.getChildrenCount() + "blog posts.");
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Crime crime = postSnapshot.getValue(Crime.class);
                    createMarker(crime);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            Log.e("My App", "Loading the database failed", databaseError.toException());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(new LatLng(35.606855, -77.355916)).title("Marker in ECU"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.606855, -77.355916),12));

        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitude, mLongitude), 14.0f));
    }

    public void createMarker(Crime crime){
        LatLng latLng = new LatLng(Double.parseDouble(crime.getLatitude()), Double.parseDouble(crime.getLongitude()));
        MarkerOptions options = new MarkerOptions();
        int badge =0;
        String type = crime.getType();
        switch(type){
            case "LARCENY":
                badge = R.drawable.small_1;
                break;
            case "PROPERTY":
                badge = R.drawable.small_2;
                break;
            case "DOMESTIC":
                badge = R.drawable.small_3;
                break;

        }
        options
                .title(crime.getType())
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(badge))
                .visible(false)
                .snippet("Location: " + crime.getAddress()+ "\nDate: " + crime.getDate() + "\nTime: " + crime.getTime());
        mMap.addMarker(options);
    }

    /*public void gotoLocation(double lat, double lng, float zoom){
        LatLng latLng = new LatLng(lat,lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, zoom);
        mMap.moveCamera(update);
    }*/

}
