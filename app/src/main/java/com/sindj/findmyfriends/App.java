package com.sindj.findmyfriends;

import android.app.Application;
import android.util.Log;

/**
 * Created by Didi-PC on 09/03/2017.
 */

public class App extends Application {

    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
        Log.d("App", "onCreate");
    }

    private void initialize() {
        app = this;
        SharedPref.init(this);
    }
}