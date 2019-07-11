package com.pojo;

public class MyUser {
    public String name, email, designation;

    public MyUser() {
    }

    public MyUser(String name, String email, String designation) {
        this.name = name;
        this.email = email;
        this.designation = designation;
    }

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
}
