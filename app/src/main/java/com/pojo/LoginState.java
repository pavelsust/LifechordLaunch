package com.pojo;

import android.content.Context;
import android.content.SharedPreferences;

import com.com.utils.Constant;

public class LoginState {

    public Context context;
    public SharedPreferences sharedPreferences;

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

}
