package com.github.exsite123.task.vk.gson.users;

public class Item {

    private int id;
    private String first_name;
    private String last_name;
    private City city;
    private String bdate;
    private String mobile_phone;
    private String home_phone;

    public City getCity() {
        return this.city;
    }

    public int getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.first_name;
    }

    public String getLastName() {
        return this.last_name;
    }

    public String getBdate() {
        return this.bdate;
    }

    public String getMobilePhone() {
        return this.mobile_phone;
    }

    public String getHomePhone() {
        return this.home_phone;
    }
}