package com.vetzforpetz.estore.Prevalent;



import com.vetzforpetz.estore.Model.AdminOrders;
import com.vetzforpetz.estore.Model.Cart;
import com.vetzforpetz.estore.Model.Users;

import java.util.HashMap;

public class Prevalent
{
    private static Prevalent mPrevalent = new Prevalent();
    private static Users currentOnlineUser;
    private String userType;
    private HashMap<String, Cart> orderLineItems;
    private String orderNumber;
    private AdminOrders orderHeader;
    public static final String UserPhoneKey = "UserPhone";
    public static final String UserPasswordKey = "UserPassword";


    public static Prevalent getInstance() {
        if (mPrevalent == null) {
            mPrevalent = new Prevalent();
        }
        return mPrevalent;
    }
    public AdminOrders getOrderHeader() {
        return orderHeader;
    }

    public void setOrderHeader(AdminOrders orderHeader) {
        this.orderHeader = orderHeader;
    }

    public static Users getCurrentOnlineUser() {
        return currentOnlineUser;
    }

    public static void setCurrentOnlineUser(Users currentOnlineUser) {
        Prevalent.currentOnlineUser = currentOnlineUser;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public static String getUserPhoneKey() {
        return UserPhoneKey;
    }

    public static String getUserPasswordKey() {
        return UserPasswordKey;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public HashMap<String, Cart> getOrderLineItems() {
        if (orderLineItems == null) {
            orderLineItems = new HashMap<>();
        }
        return orderLineItems;
    }

    public void setOrderLineItems(HashMap<String, Cart> orderLineItems) {
        this.orderLineItems = orderLineItems;
    }

    public void addOrUpdateOrderLineItem ( Cart lineItem) {
        if (orderLineItems == null) {
            orderLineItems = new HashMap<>();
        } else if (orderLineItems.containsKey(lineItem.getPid())) {
            orderLineItems.remove(lineItem.getPid());
        }
        orderLineItems.put(lineItem.getPid(), lineItem);
    }

    public void removeOrderLineItem (String productId) {
        orderLineItems.remove(productId);
    }

    public void resetCart() {
        orderLineItems.clear();
    }
}