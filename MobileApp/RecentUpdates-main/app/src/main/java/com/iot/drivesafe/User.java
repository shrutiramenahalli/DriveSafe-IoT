package com.iot.drivesafe;

import android.app.Application;

public class User extends Application {
    public static String email;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
