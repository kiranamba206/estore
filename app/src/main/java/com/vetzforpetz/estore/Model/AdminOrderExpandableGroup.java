package com.vetzforpetz.estore.Model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class AdminOrderExpandableGroup extends ExpandableGroup {
    public AdminOrderExpandableGroup(String title, List<AdminOrders> items) {
        super(title, items);
    }
}
