package com.sindj.findmyfriends;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String name = intent.getStringExtra("nickname");

        TextView textView = (TextView) findViewById(R.id.welcome);
        textView.setText("Hey " + name);
    }

    public void JoinGroup(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        edittext.setHint("Paste group key");
        alert.setMessage("Join existing group");
        alert.setTitle("Join group");
        alert.setView(edittext);

        alert.setPositiveButton("JOIN", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String groupKey = edittext.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference groupKeyRef = database.getReference("groups-keys").child(groupKey);
                groupKeyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Connected
                            //Toast.makeText(MainActivity.this, "Group exists", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, MapActivity.class);
                            intent.putExtra("key", groupKey);
                            intent.putExtra("name", dataSnapshot.getValue(String.class));
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Group doesn't exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(MainActivity.this, "Creating group aborted", Toast.LENGTH_LONG).show();
            }
        });

        alert.show();
    }

    public void createGroup(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        edittext.setHint("Enter group name");
        alert.setMessage("Choose group name");
        alert.setTitle("Create group");
        alert.setView(edittext);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String groupName = edittext.getText().toString();
                if (groupName.length() > 3) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference groupsRef = database.getReference("groups");
                    DatabaseReference groupsKeysRef = database.getReference("groups-keys");
                    final DatabaseReference groupRef = groupsRef.push();
                    groupsKeysRef.child(groupRef.getKey()).setValue(groupName);
                    groupRef.child("name").setValue(groupName, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Toast.makeText(MainActivity.this, groupName + " password:" + groupRef.getKey(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "The key has been copied into your clipboard", Toast.LENGTH_LONG).show();

                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("FMF group pass", groupRef.getKey());
                            clipboard.setPrimaryClip(clip);

                            Intent intent = new Intent(MainActivity.this, MapActivity.class);
                            intent.putExtra("key", groupRef.getKey());
                            intent.putExtra("name", groupName);
                            startActivity(intent);
                        }
                    });


                } else {
                    Toast.makeText(MainActivity.this, "Group name must to be more then 3 chars", Toast.LENGTH_LONG).show();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(MainActivity.this, "Creating group aborted", Toast.LENGTH_LONG).show();
            }
        });

        alert.show();
    }

    public void logout(View view) {
        SharedPref.putString("nickname", null);
        Intent intent = new Intent(MainActivity.this, NameActivity.class);
        startActivity(intent);
        finish();
    }
}
