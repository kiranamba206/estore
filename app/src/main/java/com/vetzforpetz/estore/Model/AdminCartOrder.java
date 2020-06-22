package com.vetzforpetz.estore.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminCartOrder {


    //------------ Commenting below to try to use FirebaseUI recycleView Adapter on AdminNewOrderActivity

    /*private HashMap<String, AdminOrders> ordersToBeProcessed;

    public void addOrderToBeProcessed(String orderNumber, AdminOrders order) {
        if (ordersToBeProcessed == null) {
            ordersToBeProcessed = new HashMap<>();
        }
        if (ordersToBeProcessed.containsKey(orderNumber)) {
            ordersToBeProcessed.remove(orderNumber);
        }
        ordersToBeProcessed.put(orderNumber, order);
    }

    public HashMap<String, AdminOrders> getOrdersToBeProcessed() {
        return ordersToBeProcessed;
    }

    public void setOrdersToBeProcessed(HashMap<String, AdminOrders> ordersToBeProcessed) {
        this.ordersToBeProcessed = ordersToBeProcessed;
    }

    @Override
    public String toString() {
        return "AdminCartOrder{" +
                "ordersToBeProcessed=" + ordersToBeProcessed +
                '}';
    }*/


    private List<AdminOrders> ordersToBeProcessed; // will be of the form userId+orderNumber (String), AdminOrders
    //private HashMap<String, String> orderNumberToUserId; //will map userId and OrderNumber

    public AdminCartOrder() {
        ordersToBeProcessed = new ArrayList<>();
    //    orderNumberToUserId = new HashMap<>();
    }

    public void addOrderToBeProcessed( AdminOrders orderDetail) {

        ordersToBeProcessed.add(orderDetail);
    }




    public List< AdminOrders> getOrdersToBeProcessed() {
        return ordersToBeProcessed;
    }

    public void setOrdersToBeProcessed(List<AdminOrders> ordersList) {
        this.ordersToBeProcessed = ordersList;

    }

    @Override
    public String toString() {
        return "AdminCartOrder{" +
                ", ordersList=" + ordersToBeProcessed +
                '}';
    }


}
