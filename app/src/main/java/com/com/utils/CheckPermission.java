package com.com.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;


/**
 * Created by android on 5/26/2017.
 */

public class CheckPermission {

    public Context context;

    public static final int PERMISSION_REQUEST_CODE = 200;

    public CheckPermission(Context context) {
        this.context = context;
    }

    public boolean isPermissionGranted() {

        int access_fine_location = ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int access_cross_location = ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int internet = ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.INTERNET);
        return access_fine_location == PackageManager.PERMISSION_GRANTED && access_cross_location == PackageManager.PERMISSION_GRANTED && internet == PackageManager.PERMISSION_GRANTED;

    }

}
