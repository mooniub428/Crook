package com.example.stath.crookclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class UserStatus {

    private static final String USER_EMAIL = "email";
    private static final String USER_FIRSTNAME = "firstname";

    private static SharedPreferences getSharedPreferences(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserEmail(Context ctx, String userEmail){
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(USER_EMAIL, userEmail);
        editor.apply();
    }

    public static void setUserFirstname(Context ctx, String userFirstname){
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(USER_FIRSTNAME, userFirstname);
        editor.apply();
    }

    public static String getUserEmail(Context ctx){
        return getSharedPreferences(ctx).getString(USER_EMAIL, "");
    }

    public static String getUserFirstname(Context ctx){
        return getSharedPreferences(ctx).getString(USER_FIRSTNAME, "");
    }

    public static void clearData(Context ctx)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        editor.apply();
    }

}
