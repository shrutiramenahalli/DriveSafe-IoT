package com.iot.drivesafe;

public class DataModel1 {
    private String name;
    private int age;
    private String email_id;
    private String vehicle_id;
    private String tag_id;
    private int balance;

    public DataModel1() {
        this.name = name;
        this.age = age;
        this.email_id = email_id;
        this.vehicle_id = vehicle_id;
        this.tag_id = tag_id;
        this.balance= balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
