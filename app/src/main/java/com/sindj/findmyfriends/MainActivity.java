package com.sindj.findmyfriends;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void JoinGroup(View view) {
    }

    public void createGroup(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText edittext = new EditText(this);
        alert.setMessage("Choose group name");
        alert.setTitle("Create group");

        alert.setView(edittext);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String groupName = edittext.getText().toString();
                if (groupName.length() > 3) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference groupsRef = database.getReference("groups");
                    DatabaseReference groupRef = groupsRef.push();
                    groupRef.child("name").setValue(groupName);
                    Toast.makeText(MainActivity.this, "Group password:" + groupRef.getKey(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Group name must to be more then 3 chars", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }
}
