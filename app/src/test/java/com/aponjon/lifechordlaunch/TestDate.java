package com.aponjon.lifechordlaunch;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

public class TestDate {


    public static void main(String[] args) {


        String dd = "2019-07-18 4:59";
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM");

        try {
            date = sdf.parse(dd);
        } catch (Exception e) {

        }

        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        System.out.println("" + sdf.format(date));

        LocalDateTime localDateTime = LocalDateTime.parse("" + sdf.format(date), sdf);
        localDateTime.plusMinutes(30);

        System.out.println("Dhaka: " + localDateTime.toString());
    }


}
