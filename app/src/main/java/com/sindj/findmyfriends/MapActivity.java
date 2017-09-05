package com.sindj.findmyfriends;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends AppCompatActivity {

    String key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        setTitle(name);
        key = intent.getStringExtra("key");

        TextView textView = (TextView) findViewById(R.id.groupKey);
        textView.setText("Key: " + key);
    }

    public void copyToClipboard(View view) {
        Toast.makeText(this, "The key has been copied into your clipboard", Toast.LENGTH_SHORT).show();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("FMF group pass", key);
        clipboard.setPrimaryClip(clip);

    }
}
