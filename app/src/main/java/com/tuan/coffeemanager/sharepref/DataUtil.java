package com.tuan.coffeemanager.sharepref;

import android.content.Context;
import android.content.SharedPreferences;

import com.tuan.coffeemanager.contact.ContactBaseApp;

public class DataUtil {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static SharedPreferences initPref(Context context) {
        sharedPreferences = context.getSharedPreferences(ContactBaseApp.SHARED_PREF, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    private static SharedPreferences.Editor initEdit() {
        editor = sharedPreferences.edit();
        return editor;
    }

    public static void newInstance(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = initPref(context);
        }
        if (editor == null) {
            editor = initEdit();
        }
    }

    public static void setIdUser(String id) {
        editor.putString(ContactBaseApp.ID_USER, id);
        editor.apply();
    }

    public static String getIdUser() {
        return sharedPreferences.getString(ContactBaseApp.ID_USER, "");
    }
}
