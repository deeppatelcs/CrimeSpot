package com.example.dp863.crimespot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private boolean mPermissionDenied = false;
    private LocationManager locationManager;
    private LocationListener listener;



    private GoogleMap mMap;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String crimeID;
    private double mLatitude, mLongitude;
    Date fromDate, toDate;

    List<Crime> currentCrimeList = new ArrayList<Crime>();
    ImageButton mImageButton;
    protected boolean[] isChecked = {
            true,
            true,
            true,
            true,
            true,
            true,
            true
    };


    Map<String, Boolean> map = new HashMap<>();
    Set<String> keys = map.keySet();

    // Declare a variable for the cluster manager.
    private ClusterManager<MyItem> mClusterManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        onMyLocationButtonClick();

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        try {
            fromDate = df.parse("01-01-2016");
            toDate = df.parse("01-01-2020");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (getIntent().getExtras() != null) {
          if (getIntent().getExtras().getBooleanArray("yourBool") != null) {
              isChecked = getIntent().getExtras().getBooleanArray("yourBool");
              for (int i = 0; i < isChecked.length; i++) {
                  Log.i("My app", isChecked[i] ? "MapsActivity true" : "MapsActivity false");
              }
          }
          if(getIntent().getExtras().getString("fromDate") != null && getIntent().getExtras().getString("toDate") != null) {

              try {
                  fromDate = df.parse(getIntent().getExtras().getString("fromDate"));
                  toDate = df.parse(getIntent().getExtras().getString("toDate"));
              } catch (ParseException e) {
                  e.printStackTrace();
              }


          }

        }


        map.put("ASSAULT", isChecked[0]);
        map.put("LARCENY", isChecked[1]);
        map.put("ROBBERY", isChecked[2]);
        map.put("TRESPASSING", isChecked[3]);
        map.put("PROPERTY", isChecked[4]);
        map.put("WEAPONS", isChecked[5]);
        map.put("MISCELLANEOUS", isChecked[6]);



        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            System.out.println(key + " : " + value);
        }
        getFromDatabase();
    }


    // get Crime data back from Firebase Database
    public void getFromDatabase() {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("Crimes");

        Map<String, String> entry = new HashMap<String, String>();
        entry.put("address", "0");
        entry.put("date", "0");
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
                Toast.makeText(getApplicationContext(), "Database Connected!", Toast.LENGTH_LONG).show();
                Log.e("My App", "There are " + dataSnapshot.getChildrenCount() + "blog posts.");
                ArrayList<LatLng> list = new ArrayList<LatLng>();
                LatLng latLng;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Crime crime = postSnapshot.getValue(Crime.class);
                    currentCrimeList.add(crime);
                }
                //Log.e("My App", "There are " + list.size() + " number of points.");
                //createMarker(currentCrimeList);
                //addHeatMap(currentCrimeList);
                //addItems(currentCrimeList);
                //onMyLocationButtonClick();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("My App", "Loading the database failed", databaseError.toException());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //setting button
        mImageButton = (ImageButton) findViewById(R.id.imageButton);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < isChecked.length; i++) {
                    Log.i("My App", isChecked[i] ? "MapsActivity true" : "MapsActivity false");
                }
                Toast.makeText(MapsActivity.this, "MapsActivity Settings", Toast.LENGTH_SHORT).show();
                Intent mIntent = new Intent(getApplicationContext(), SortActivity.class);
                mIntent.putExtra("yourBool", isChecked);
                startActivity(mIntent);

            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Add menu handling code
        switch (id) {
            case R.id.mapTypeNormal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                addItems(currentCrimeList);
                break;
            case R.id.mapTypeSatellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeHybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.layoutTypeMarkers:
                createMarker(currentCrimeList);
                break;
            case R.id.layoutTypeCluster:
                // for cluster
                mClusterManager = new ClusterManager<MyItem>(this, mMap);
                mMap.setOnCameraIdleListener(mClusterManager);

                addItems(currentCrimeList);
                break;
            case R.id.layoutTypeHeatMap:
                addHeatMap(currentCrimeList);
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    public void addHeatMap(List<Crime> currentCrimeList) {
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.606855, -77.355916), 13));
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        for (Crime crime : currentCrimeList) {
            LatLng latLng = new LatLng(Double.parseDouble(crime.getLatitude()), Double.parseDouble(crime.getLongitude()));
            list.add(latLng);
        }
        // Create the gradient.
        int[] colors = {
                Color.rgb(0, 225, 0), //green
                Color.rgb(225, 0, 0) //red

        };

        float[] startPoints = {
                0.02f, 0.05f
        };
        Gradient gradient = new Gradient(colors, startPoints);

        // Create a heat map title provider, passing it the latlngs of crimes
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                .data(list)
                .gradient(gradient)
                .build();
        provider.setRadius(100);

        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider).fadeIn(false));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();




    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Current location...", Toast.LENGTH_SHORT).show();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("My App", "onLocationChanged: Latitude and longitude" + location.getLatitude() + " : " + location.getLongitude());
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                LatLng currentLoc = new LatLng(mLatitude, mLongitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 13));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private void addItems(List<Crime> currentCrimeList) {
        mMap.clear();
        mClusterManager.clearItems();
        mMap.addMarker(new MarkerOptions().position(new LatLng(35.606855, -77.355916)).title("Marker in ECU"));
        mClusterManager.setAnimation(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.606855, -77.355916), 13));
        Log.e("My App", "There are " + currentCrimeList.size() + " size posts.");
        for (Crime crime : currentCrimeList) {
            LatLng latLng = new LatLng(Double.parseDouble(crime.getLatitude()), Double.parseDouble(crime.getLongitude()));
            MyItem clusItem = new MyItem(latLng);
            mClusterManager.addItem(clusItem);
        }
    }


    public void createMarker(List<Crime> currentCrimeList) {
        mMap.clear();
        Log.e("My App", "From Date" + fromDate);
        Log.e("My App", "to Date" + toDate);
        boolean markeradded;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.606855, -77.355916), 15));

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date crimeDate = null;

        for (Crime crime : currentCrimeList) {

            try {
                crimeDate = df.parse(crime.getDate());

            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (crimeDate != null) {
                if (crimeDate.after(fromDate) && crimeDate.before(toDate)) {
                    Log.e("My App", "Crime Date: " + crimeDate);
                    markeradded = false;

                    LatLng latLng = new LatLng(Double.parseDouble(crime.getLatitude()), Double.parseDouble(crime.getLongitude()));

                    MarkerOptions options = new MarkerOptions();
                    String type = crime.getType();
                    try {
                        // Assault

                        if (type.contains("ASSAULT") || type.contains("AWDW")) {
                            markeradded = true;
                            if (map.get("ASSAULT")) {

                                options
                                        .title(crime.getType())
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                                        .snippet("Location: " + crime.getAddress() + "\nDate: " + crime.getDate() + "\nTime: " + crime.getTime());
                                mMap.addMarker(options);
                            } else {
                                options
                                        .title(crime.getType())
                                        .position(latLng)
                                        .visible(false)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                mMap.addMarker(options);
                            }
                        }

                        // Larceny
                        if (type.contains("LARCENY")) {
                            markeradded = true;
                            if (map.get("LARCENY")) {

                                options
                                        .title(crime.getType())
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                                        .snippet("Location: " + crime.getAddress() + "\nDate: " + crime.getDate() + "\nTime: " + crime.getTime());
                                ;
                                mMap.addMarker(options);
                            } else {
                                options
                                        .title(crime.getType())
                                        .position(latLng)
                                        .visible(false)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                                mMap.addMarker(options);
                            }
                        }

                        // Robbery
                        if (type.contains("ROBBERY") || type.contains("BURGLARY")) {
                            markeradded = true;
                            if (map.get("ROBBERY")) {

                                options
                                        .title(crime.getType())
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                        .snippet("Location: " + crime.getAddress() + "\nDate: " + crime.getDate() + "\nTime: " + crime.getTime());
                                mMap.addMarker(options);
                            } else {
                                options
                                        .title(crime.getType())
                                        .position(latLng)
                                        .visible(false)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                mMap.addMarker(options);
                            }
                        }

                        // Trespassing
                        if (type.contains("TRESPASSING") || type.contains("B & E")) {
                            markeradded = true;
                            if (map.get("TRESPASSING")) {

                                options
                                        .title(crime.getType())
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                        .snippet("Location: " + crime.getAddress() + "\nDate: " + crime.getDate() + "\nTime: " + crime.getTime());
                                mMap.addMarker(options);
                            } else {
                                options
                                        .title(crime.getType())
                                        .position(latLng)
                                        .visible(false)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                mMap.addMarker(options);
                            }
                        }

                        // Property
                        if (type.contains("PROPERTY")) {
                            markeradded = true;
                            if (map.get("PROPERTY")) {

                                options
                                        .title(crime.getType())
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                        .snippet("Location: " + crime.getAddress() + "\nDate: " + crime.getDate() + "\nTime: " + crime.getTime());
                                mMap.addMarker(options);
                            } else {
                                options
                                        .title(crime.getType())
                                        .position(latLng)
                                        .visible(false)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                mMap.addMarker(options);
                            }

                        }

                        // Weapons
                        if (type.contains("WEAPONS") || type.contains("SHOOTING")) {
                            markeradded = true;
                            if (map.get("WEAPONS")) {

                                options
                                        .title(crime.getType())
                                        .position(latLng)
                                        .visible(false)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                                        .snippet("Location: " + crime.getAddress() + "\nDate: " + crime.getDate() + "\nTime: " + crime.getTime());
                                mMap.addMarker(options);
                            } else {
                                options
                                        .title(crime.getType())
                                        .position(latLng)
                                        .visible(false)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                                mMap.addMarker(options);
                            }
                        }

                        // MISCELLANEOUS
                        if (markeradded == false) {
                            if (map.get("MISCELLANEOUS")) {

                                options
                                        .title(crime.getType())
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                        .snippet("Location: " + crime.getAddress() + "\nDate: " + crime.getDate() + "\nTime: " + crime.getTime());
                                mMap.addMarker(options);
                            }
                        }


                    } catch (NullPointerException e) {
                        e.printStackTrace();

                    }

                }
            }
        }

    }

}
