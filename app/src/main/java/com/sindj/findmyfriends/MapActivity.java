package com.sindj.findmyfriends;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    String key = "";
    String userKey = "";
    String nickName = "";
    private userLocation myLocation = new userLocation();
    DatabaseReference groupRef;
    DatabaseReference locationsRef;
    private GoogleMap mMap;
    private ArrayList<Marker> arrMarkers = new ArrayList<>();
    private static final int LOCATION_INTERVAL = 1000;
    LocationManager mLocationManager;
    Location mLastLocation;
    float mDeclination;
    private float[] mRotationMatrix = new float[16];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        String groupName = intent.getStringExtra("name");
        nickName = SharedPref.getString("nickname", null);
        setTitle(groupName);
        key = intent.getStringExtra("key");

        groupRef = FirebaseDatabase.getInstance().getReference("groups/" + key);
        locationsRef = groupRef.child("locations");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        initLocation();

        // initialize your android device sensor capabilities
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sm.getSensorList(Sensor.TYPE_ROTATION_VECTOR).size() != 0) {
            Sensor s = sm.getSensorList(Sensor.TYPE_ROTATION_VECTOR).get(0);
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private class LocationListener implements android.location.LocationListener {

        public LocationListener(String provider) {
            Log.e("", "LocationListener " + provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            GeomagneticField field = new GeomagneticField(
                    (float) location.getLatitude(),
                    (float) location.getLongitude(),
                    (float) location.getAltitude(),
                    System.currentTimeMillis()
            );

            // getDeclination returns degrees
            mDeclination = field.getDeclination();

            doLocationUpdate(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e("", "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("", "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("", "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(
                    mRotationMatrix, event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientation);
            double bearing = Math.toDegrees(orientation[0]) + mDeclination;
            updateCamera((float) bearing);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private void updateCamera(float bearing) {
        CameraPosition oldPos = mMap.getCameraPosition();

        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    private void initLocation() {
        if (SharedPref.getString("userKey", null) != null) {
            userKey = SharedPref.getString("userKey", null);
        } else {
            userKey = locationsRef.push().getKey();
            SharedPref.putString("userKey", userKey);
        }

        locationsRef.child(userKey).child("name").setValue(nickName);
        myLocation.setName(nickName);

        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, 1,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i("", "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Toast.makeText(this, "network provider does not exist", Toast.LENGTH_SHORT).show();
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, 1,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i("", "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Toast.makeText(this, "gps provider does not exist", Toast.LENGTH_SHORT).show();
        }
    }

    // Define a listener that responds to location updates
/*    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.

            DatabaseReference locRef = groupRef.child("locations/" + userKey);
            myLocation.setLatitude(location.getLatitude());
            myLocation.setLongitude(location.getLongitude());
            locRef.setValue(myLocation);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };*/

// Register the listener with the Location Manager to receive location updates
    //    if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED &&ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
    // {
    //     return;
    // }
    //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,locationListener);

    public void copyToClipboard(View view) {
        Toast.makeText(this, "The key has been copied into your clipboard", Toast.LENGTH_SHORT).show();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("FMF group pass", key);
        clipboard.setPrimaryClip(clip);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        //  Marker marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // marker.remove();
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        locationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (Marker marker : arrMarkers) {
                    marker.remove();
                }

                arrMarkers.clear();

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    userLocation userLocation = snap.getValue(userLocation.class);
                    if (!userLocation.getName().equals(nickName)) {
                        LatLng latlngLoc = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latlngLoc).title(userLocation.getName()));
                        arrMarkers.add(marker);
                    }
                    // Toast.makeText(MapActivity.this, userLocation.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void doLocationUpdate(Location l) {
        long minDistance = 1;
        Log.d("", "update received:" + l);
        if (l == null) {
            Log.d("", "Empty location");
            return;
        }
        if (mLastLocation != null) {
            float distance = l.distanceTo(mLastLocation);
            Log.d("", "Distance to last: " + distance);
            if (l.distanceTo(mLastLocation) < minDistance) {
                Log.d("", "Position didn't change");
                return;
            }
            if (l.getAccuracy() >= mLastLocation.getAccuracy()
                    && l.distanceTo(mLastLocation) < l.getAccuracy()) {
                Log.d("",
                        "Accuracy got worse and we are still within the accuracy range.. Not updating");
                return;
            }
        }

        DatabaseReference locRef = groupRef.child("locations/" + userKey);
        myLocation.setLatitude(l.getLatitude());
        myLocation.setLongitude(l.getLongitude());
        locRef.setValue(myLocation);

        mLastLocation = l;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i("", "fail to remove location listners, ignore", ex);
                }
            }
        }
    }
}

