package com.sindj.findmyfriends;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Didi-PC on 09/03/2017.
 */

public class SharedPref {

    private static SharedPreferences sharedPref;

    public static void init(Context context) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void putString(String key, String value) {
        sharedPref.edit().putString(key, value).apply();
    }

    public static String getString(String key, String defValue) {
        return sharedPref.getString(key, defValue);
    }

    public static void putInteger(String key, Integer value) {
        sharedPref.edit().putInt(key, value).apply();
    }

    public static Integer getInteger(String key, Integer defValue) {
        return sharedPref.getInt(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        sharedPref.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return sharedPref.getBoolean(key, defValue);
    }

    public static void putLong(String key, long value) {
        sharedPref.edit().putLong(key, value).apply();
    }

    public static long getLong(String key, long defValue) {
        return sharedPref.getLong(key, defValue);
    }
}
