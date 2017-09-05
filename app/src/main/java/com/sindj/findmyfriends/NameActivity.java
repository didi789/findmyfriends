package com.sindj.findmyfriends;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        if (SharedPref.getString("nickname", null) != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("nickname", SharedPref.getString("nickname", null));
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 80: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                sendNickName(null);
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void sendNickName(View view) {
        if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 80);
        else {
            EditText editText = (EditText) findViewById(R.id.nickname);
            String nickname = editText.getText().toString();

            if (nickname.length() <= 1) {
                Toast.makeText(this, "Nickname must be more than 1 letter", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("nickname", nickname);
                SharedPref.putString("nickname", nickname);
                startActivity(intent);
                finish();
            }
        }
    }
}
