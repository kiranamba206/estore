package com.vetzforpetz.estore.Model;

import java.util.HashMap;

public class Users
{
    private String name, phone, password, image, address, city, email;
    boolean role_delivery;
    private boolean isLoggedIn = false;
    public Users()
    {

    }

    public Users(String name, String phone, String password, String image, String address,
                 String city, String email) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.address = address;
        this.email = email;
        this.city = city;

    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean getRole_delivery() {
        return role_delivery;
    }

    public void setRole_delivery(boolean role_delivery) {
        this.role_delivery = role_delivery;
    }
}
