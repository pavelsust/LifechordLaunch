package com.com.utils;

import android.widget.EditText;

public class Utils {


    public static boolean isValidText(EditText e) {
        boolean ret = false;
        if (e.getText().toString().trim().length() > 0) {
            e.setError(null);
            ret = true;
        } else {
            e.setError("Please enter field");
        }
        return ret;
    }

}
