package com.sindj.findmyfriends;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapActivity extends AppCompatActivity {

    String key = "";
    String userKey = "";
    private userLocation myLocation = new userLocation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        Intent intent = getIntent();
        String groupName = intent.getStringExtra("name");
        setTitle(groupName);
        key = intent.getStringExtra("key");

        final DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups/" + key);
        final DatabaseReference locationsRef = groupRef.child("locations");
        locationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    userLocation userLocation = snap.getValue(userLocation.class);
                    Toast.makeText(MapActivity.this, userLocation.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (SharedPref.getString("userKey", null) != null) {
            userKey = SharedPref.getString("userKey", null);
        } else {
            userKey = groupRef.child("locations").push().getKey();
            SharedPref.putString("userKey", userKey);
        }

        groupRef.child("locations").child(userKey).child("name").setValue(SharedPref.getString("nickname", null));
        myLocation.setName(SharedPref.getString("nickname", null));
        TextView textView = (TextView) findViewById(R.id.groupKey);
        textView.setText("Key: " + key);

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.

                DatabaseReference locRef = groupRef.child("locations/" + userKey);
                myLocation.setLatitude(location.getLatitude());
                myLocation.setLongitude(location.getLongitude());
                locRef.child("location").child("Latitude").setValue(myLocation);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

// Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);

    }

    public void copyToClipboard(View view) {
        Toast.makeText(this, "The key has been copied into your clipboard", Toast.LENGTH_SHORT).show();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("FMF group pass", key);
        clipboard.setPrimaryClip(clip);

    }
}
