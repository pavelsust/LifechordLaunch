package com.pojo;

import androidx.annotation.NonNull;

public class MyResult {

    public String launchDate;
    public String isAlreadySelect;

    public String name, email, designation;


    public MyResult(String launchDate, String isAlreadySelect, String name, String email, String designation) {
        this.launchDate = launchDate;
        this.isAlreadySelect = isAlreadySelect;
        this.name = name;
        this.email = email;
        this.designation = designation;
    }


    public MyResult(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
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
        return "\nname:  "+name +"\nemail: "+email+"\nlaunch:  "+launchDate+ "\ndesignation: "+ designation+"\nis_first_launch: "+ isAlreadySelect;
    }
}
