package com.com.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class LoginState {
    public Context context;
    public SharedPreferences sharedPreferences;

    public static String DATE_FORMAT = "yyyy-MM-dd hh:mm a zzz";

    public LoginState(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constant.LOGIN_STATE, Context.MODE_PRIVATE);
    }

    public void saveDataIntoSharePreferance(String key, String value) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void saveDataIntoSharePreferance(String key, boolean value) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public String getDataFromSharedPreferance(String key) {
        String value = sharedPreferences.getString(key, "");
        return value;
    }

    public boolean getBooleanDataFromSharedPreferance(String key) {
        boolean value = sharedPreferences.getBoolean(key, false);
        return value;
    }
    public void clearSharedPreferance() {
        this.context.getSharedPreferences(Constant.LOGIN_STATE, Context.MODE_PRIVATE).edit().clear().commit();
    }


    public String finalDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
        String currentDate = sdf.format(new Date());

        SimpleDateFormat zoDateFormat = new SimpleDateFormat("zzz");
        String currentZone = zoDateFormat.format(new Date());
        return currentDate + "10:45 AM " + "GMT+6:00";
    }

    public String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Date date = new Date();
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+6:00"));
        return sdf.format(date);
    }

    public String convertToGMTDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Date date = new Date();
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+6:00"));
        return sdf.format(date);
    }

    public boolean compareTwoDate() {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Date currentDateTime = null;
        Date setDateTime = null;
        try {
            currentDateTime = sdf.parse(getCurrentDate());
            setDateTime = sdf.parse(convertToGMTDate(finalDate()));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (currentDateTime.before(setDateTime)) {
            return true;
        } else {
            return false;
        }
    }
}
