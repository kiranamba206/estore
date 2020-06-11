package com.vetzforpetz.estore.Prevalent;

import com.google.firebase.database.DatabaseReference;
import com.vetzforpetz.estore.Model.AdminCartOrder;
import com.vetzforpetz.estore.Model.Users;

import java.util.HashMap;
import java.util.List;

public class PrevalentOrdersForAdmins {
    //making this class as singleton
    private static PrevalentOrdersForAdmins prevalentOrdersForAdmins = new PrevalentOrdersForAdmins();

    AdminCartOrder ordersToBeProcessed;
    DatabaseReference ordersDataRef;
    HashMap<String, Integer> orderStatusCodes;
    List<Users> deliveryAdminsList;

    private PrevalentOrdersForAdmins() {
        orderStatusCodes = new HashMap<>();
        orderStatusCodes.put("Not Shipped", 1);
        orderStatusCodes.put("Approved with changed date/time", 2);
        orderStatusCodes.put("Processing", 3);
        orderStatusCodes.put("Ready for Pickup", 4);
        orderStatusCodes.put("Out for Delivery", 5);
        orderStatusCodes.put("Complete", 6);
    }

    public static PrevalentOrdersForAdmins getInstance() {

        if (prevalentOrdersForAdmins == null) {
            prevalentOrdersForAdmins = new PrevalentOrdersForAdmins();
        }
        return prevalentOrdersForAdmins;
    }

    public HashMap<String, Integer> getOrderStatus(){
        return orderStatusCodes;
    }

    public Integer getOrderStatusCode(String orderStatus) {
        if (orderStatusCodes.containsKey(orderStatus)) {
            return orderStatusCodes.get(orderStatus);
        } else {
            return null;
        }
    }

    public AdminCartOrder getOrdersToBeProcessed() {
        return ordersToBeProcessed;
    }

    public void setOrdersToBeProcessed(AdminCartOrder ordersToBeProcessed) {
        this.ordersToBeProcessed = ordersToBeProcessed;
    }

    public DatabaseReference getOrdersDataRef() {
        return ordersDataRef;
    }

    public void setOrdersDataRef(DatabaseReference ordersDataRef) {
        this.ordersDataRef = ordersDataRef;
    }

    public List<Users> getDeliveryAdminsList() {
        return deliveryAdminsList;
    }

    public void setDeliveryAdminsList(List<Users> deliveryAdminsList) {
        this.deliveryAdminsList = deliveryAdminsList;
    }
}
