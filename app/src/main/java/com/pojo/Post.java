package com.pojo;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Post {

    public String name;
    public String designation;
    public String launchDate;
    public String isAlreadySelect;
    public String userID;

    public Post() {
    }

    public Post(String name, String designation, String launchDate, String isAlreadySelect, String userID) {
        this.name = name;
        this.designation = designation;
        this.launchDate = launchDate;
        this.isAlreadySelect = isAlreadySelect;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Post(String launchDate, String isAlreadySelect) {
        this.launchDate = launchDate;
        this.isAlreadySelect = isAlreadySelect;
    }

    public String getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(String launchDate) {
        this.launchDate = launchDate;
    }

    public String getIsAlreadySelect() {
        return isAlreadySelect;
    }

    public void setIsAlreadySelect(String isAlreadySelect) {
        this.isAlreadySelect = isAlreadySelect;
    }

    @NonNull
    @Override
    public String toString() {
        return "\nname: " + name + "\nemail: " + designation + "\ndate: " + launchDate + "\nisAlreadySet: " + isAlreadySelect;
    }
}

