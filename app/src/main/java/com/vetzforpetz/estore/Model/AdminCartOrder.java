package com.vetzforpetz.estore.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminCartOrder {
    private List<AdminOrders> ordersList; // will be of the form userId+orderNumber (String), AdminOrders
    private HashMap<String, String> orderNumberToUserId; //will map userId and OrderNumber

    public AdminCartOrder() {
        ordersList = new ArrayList<>();
        orderNumberToUserId = new HashMap<>();
    }

    public void addItemToOrderList( AdminOrders orderDetail) {

        ordersList.add(orderDetail);
    }




    public List< AdminOrders> getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<AdminOrders> ordersList) {
        this.ordersList = ordersList;

    }

    @Override
    public String toString() {
        return "AdminCartOrder{" +
                ", ordersList=" + ordersList +
                '}';
    }
}
