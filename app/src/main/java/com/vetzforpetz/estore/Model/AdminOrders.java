package com.vetzforpetz.estore.Model;

import java.util.Date;
import java.util.HashMap;

public class AdminOrders {
    private String name, phone, address, city, state, date, time, totalAmount, orderNumber;
    private HashMap<String, Cart> lineItems;
    private String fulfillmentMethod;
    private Date requestedPickupDate;
    private String requestedPickupTime;

    private boolean isOrderApproved;
    private String approvedFulfillmentTime;
    private String deliveryAdminName;
    private String deliveryAdminPhone;

    public AdminOrders() {
        fulfillmentMethod="Pickup";
    }

    public AdminOrders(String name, String phone, String address, String city, String state,
                       String date, String time, String totalAmount, String orderNumber) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.date = date;
        this.time = time;
        this.totalAmount = totalAmount;
        this.orderNumber = orderNumber;
        fulfillmentMethod="Pickup";
    }


    public AdminOrders(String name, String phone, String address, String city, String state,
                       String date, String time, String totalAmount, String orderNumber,
                       HashMap<String, Cart> lineItems) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.date = date;
        this.time = time;
        this.totalAmount = totalAmount;
        this.orderNumber = orderNumber;
        this.lineItems = lineItems;
        fulfillmentMethod="Pickup";
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderNumber() {return orderNumber;}

    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public HashMap<String, Cart> getLineItems() {
        return lineItems;
    }

    public void setLineItems(HashMap<String, Cart> lineItems) {
        this.lineItems = lineItems;
    }

    public String getFulfillmentMethod() {
        return fulfillmentMethod;
    }

    public void setFulfillmentMethod(String fulfillmentMethod) {
        this.fulfillmentMethod = fulfillmentMethod;
    }

    public boolean isOrderApproved() {
        return isOrderApproved;
    }

    public void setOrderApproved(boolean orderApproved) {
        isOrderApproved = orderApproved;
    }

    public String getApprovedFulfillmentTime() {
        return approvedFulfillmentTime;
    }

    public void setApprovedFulfillmentTime(String approvedFulfillmentTime) {
        this.approvedFulfillmentTime = approvedFulfillmentTime;
    }

    public String getDeliveryAdminName() {
        return deliveryAdminName;
    }

    public void setDeliveryAdminName(String deliveryAdminName) {
        this.deliveryAdminName = deliveryAdminName;
    }

    public String getDeliveryAdminPhone() {
        return deliveryAdminPhone;
    }

    public void setDeliveryAdminPhone(String deliveryAdminPhone) {
        this.deliveryAdminPhone = deliveryAdminPhone;
    }

    public Date getRequestedPickupDate() {
        return requestedPickupDate;
    }

    public void setRequestedPickupDate(Date requestedPickupDate) {
        this.requestedPickupDate = requestedPickupDate;
    }

    public String getRequestedPickupTime() {
        return requestedPickupTime;
    }

    public void setRequestedPickupTime(String requestedPickupTime) {
        this.requestedPickupTime = requestedPickupTime;
    }

    @Override
    public String toString() {
        return "AdminOrders{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", totalAmount='" + totalAmount + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", lineItems=" + lineItems +
                ", fulfillmentMethod='" + fulfillmentMethod + '\'' +
                ", requestedPickupDate=" + requestedPickupDate +
                ", requestedPickupTime='" + requestedPickupTime + '\'' +
                ", isOrderApproved=" + isOrderApproved +
                ", approvedFulfillmentTime=" + approvedFulfillmentTime +
                ", deliveryAdminName='" + deliveryAdminName + '\'' +
                ", deliveryAdminPhone='" + deliveryAdminPhone + '\'' +
                '}';
    }
}
