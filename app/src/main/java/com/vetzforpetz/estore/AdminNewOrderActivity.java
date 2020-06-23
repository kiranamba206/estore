package com.vetzforpetz.estore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import com.vetzforpetz.estore.Model.AdminCartOrder;
import com.vetzforpetz.estore.Model.AdminOrderExpandableGroup;
import com.vetzforpetz.estore.Model.AdminOrders;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vetzforpetz.estore.Model.Cart;
import com.vetzforpetz.estore.Model.Users;
import com.vetzforpetz.estore.Prevalent.Prevalent;
import com.vetzforpetz.estore.Prevalent.PrevalentOrdersForAdmins;
import com.vetzforpetz.estore.utils.ListViewHeightUtil;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AdminNewOrderActivity extends AppCompatActivity {

    private RecyclerView orderListView;
    private DatabaseReference ordersRef, deliveryAdminsRef;
    Query ordersRefQuery;
    PrevalentOrdersForAdmins prevalentOrdersForAdmins = PrevalentOrdersForAdmins.getInstance();
    Prevalent prevalentUserData = Prevalent.getInstance();
    String TAG = "AdminNewOrderAct";
    //AdminCartOrder ordersToBeProcessed;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);
        prevalentOrdersForAdmins.setOrdersToBeProcessed(new AdminCartOrder());
        deliveryAdminsRef = FirebaseDatabase.getInstance().getReference().child("Admins");
        /*ordersRef = FirebaseDatabase.getInstance().getReference()
                    .child("Cart List")
                    .child("Admin View");

         */

        orderListView = (RecyclerView) findViewById(R.id.orders_list_view);

        orderListView.setLayoutManager(new LinearLayoutManager(this));

            ordersRef = prevalentOrdersForAdmins.getOrdersDataRef();

            if (prevalentUserData.getUserType().equals("Admin")) {
                Log.v(TAG, "Adding startAt letter 'O' for the ordersRefQuery");
                ordersRefQuery = ordersRef.orderByKey().startAt("O");
            } else {
                ordersRefQuery = ordersRef;
            }

        // Fetching the list of delivery Admins
        Query deliveryAdminsRefQuery = deliveryAdminsRef.orderByChild("role_delivery")
                            .equalTo(true);
        deliveryAdminsRefQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    List<Users>deliveryAdminsList = new ArrayList<>();
                    for (DataSnapshot tempAdmin : dataSnapshot.getChildren()) {
                        String key = tempAdmin.getKey();
                        deliveryAdminsList.add(tempAdmin.getValue(Users.class));
                        Log.v(TAG, "all children of dataSnapshot =" + tempAdmin);
                    }
                    prevalentOrdersForAdmins.setDeliveryAdminsList(deliveryAdminsList);
                }
                Log.v(TAG, "Delivery Admins Query successful, list =" + dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
// TODO: Add Toast to say that Delivery Admins were not fetched due to network issues
            }
        });


    }

    List<AdminOrderExpandableGroup> adminOrderExpandableGroupList;
    @Override
    protected void onStart()
    {
        super.onStart();
        Log.v(TAG, "ordersRefQuery =" + ordersRefQuery.toString());

        ordersRefQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    adminOrderExpandableGroupList = new ArrayList<>();
                    for (DataSnapshot tempOrder: dataSnapshot.getChildren()) {
                        Log.v(TAG, "received order data, inside for loop order =" + tempOrder);
                        List<AdminOrders> order = new ArrayList<>();
                        order.add(tempOrder.getValue(AdminOrders.class));
                        int i = order.size()-1;
                        String title = order.get(i).getName()
                                + " mob:" + order.get(i).getPhone()
                                + "\norder#" + order.get(i).getOrderNumber()
                                + "\n" + order.get(i).getFulfillmentMethod()
                                + "- Status:" + order.get(i).getState();
                        prevalentOrdersForAdmins.getOrdersToBeProcessed().addOrderToBeProcessed(order.get(i));
                        adminOrderExpandableGroupList.add(new AdminOrderExpandableGroup(
                                        title,
                                        order
                                         ));

                    }
                    ExpandableOrderListRecyclerAdapter adapter = new ExpandableOrderListRecyclerAdapter(
                            adminOrderExpandableGroupList);
                    orderListView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
// TODO: Show network error message
            }
        });
       /* FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRefQuery, AdminOrders.class)
                        .build();

            FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        Context context = parent.getContext();
                        LayoutInflater inflater = LayoutInflater.from(context);

                        // Inflate the custom layout
                        View contactView = inflater.inflate(R.layout.orders_layout, parent, false);

                        // Return a new holder instance

                        return new AdminOrdersViewHolder(contactView);


                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final AdminOrdersViewHolder holder,
                                                    final int position,
                                                    @NonNull final AdminOrders model)
                    {
                        Log.v("AdmnNewOrdrAct-bind","position =" + position + " order =" + model);
                        //Getting the data from the position
                        final boolean[] isTheViewCompressed = {true};
                        AdminOrders orderRow = model;
                        //Log.v(TAG, "position =" + position + "orderRow = " + orderRow);
                        holder.userId = orderRow.getPhone();
                        holder.orderNumber = orderRow.getOrderNumber();

                        // setting the data from orderRow into the holder object
                        holder.userName.setText("Name: " + orderRow.getName());
                        holder.userPhoneNumber.setText("Mob:" + orderRow.getPhone());
                        holder.userTotalPrice.setText("Order Total: " + orderRow.getTotalAmount());
                        holder.userDateTime.setText("Placed on:\n" + orderRow.getDate());
                        holder.userShippingAddress.setText("Address:" + orderRow.getAddress());
                        holder.userOrderNumber.setText("Order#" + orderRow.getOrderNumber());
                        holder.orderStatus.setText("Status: " + orderRow.getState());
                        if (prevalentUserData.getUserType().equals("Admin")) {
                            //check if the user is admin and then setup data for editing orders from this screen itself
                            //-------Setting up Order Status Drop Down

                            ArrayAdapter<String> OrderStatusSpinnerAdapter =new ArrayAdapter<>(
                                    getApplicationContext(),
                                    android.R.layout.simple_spinner_item,
                                    new ArrayList<>(prevalentOrdersForAdmins.getOrderStatus().keySet()));
                            OrderStatusSpinnerAdapter.setDropDownViewResource
                                    (android.R.layout.simple_spinner_dropdown_item);
                            holder.orderStatusSpinner.setAdapter(OrderStatusSpinnerAdapter);
                        }

                        if (orderRow.getRequestedPickupDate() != null) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                            String requestedPickDateToDisplay = dateFormat.format(orderRow.getRequestedPickupDate())
                                    + "\n" + orderRow.getRequestedPickupTime();
                            holder.requestedOrderDate.setText("Requested Pick Date:" + requestedPickDateToDisplay);
                        }
                        if (orderRow.getApprovedFulfillmentTime() != null
                                && !orderRow.getApprovedFulfillmentTime().isEmpty()) {
                            holder.approvedDateTimeText.setText("Approved Date/Time: "
                                    + orderRow.getApprovedFulfillmentTime());
                        } else {
                            holder.approvedDateTimeText.setText("Change Delivery Date/Time: ");
                        }
                        // ---------- fill the delivery Admin spinner if the user is admin
                        List<String> deliveryAdminNameList = new ArrayList<>();
                        for (int i =0; i< prevalentOrdersForAdmins.getDeliveryAdminsList().size(); i++) {
                            deliveryAdminNameList.add(prevalentOrdersForAdmins.getDeliveryAdminsList().get(i).getName());
                        }
                        if (prevalentUserData.getUserType().equals("Admin")) {
                            ArrayAdapter<String> DeliveryAdminAdapter=new ArrayAdapter<>(
                                    getApplicationContext(),
                                    android.R.layout.simple_spinner_item,
                                    deliveryAdminNameList
                            );
                            DeliveryAdminAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            holder.deliveredBySpinner.setAdapter(DeliveryAdminAdapter);

                        }
                        holder.orderFulfillmentMethod.setText("Order Type: " + orderRow.getFulfillmentMethod());
                        boolean doesOrderHaveCustomMessage = false;

                        Iterator iterator =  orderRow.getLineItems().entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry lineItemDetailMap = (Map.Entry)iterator.next();
                            Cart lineItemDetails = (Cart)lineItemDetailMap.getValue();
                            if(lineItemDetails.getCustomMessage() != null
                                    && !lineItemDetails.getCustomMessage().isEmpty()) {
                                doesOrderHaveCustomMessage = true;
                            }
                        }
                        if( doesOrderHaveCustomMessage) {
                            holder.hasCustomMessage.setText("Order Has Custom Message");
                        }

                        if (prevalentUserData.getUserType().equals("Admin")) {
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.v(TAG, "View =" + v);
                                    if (prevalentUserData.getUserType().equals("Admin") && isTheViewCompressed[0]) {
                                        // Admin user clicked on the order tile for the first time
                                        // expanding the tile to show immediately editable items
                                        // second click will take admin to order detail page

                                        isTheViewCompressed[0] = false;
                                        Log.v(TAG, "Click on detected on the order tile");
                                        holder.deliveredBySpinner.setVisibility(View.VISIBLE);
                                        holder.orderStatusSpinner.setVisibility(View.VISIBLE);
                                        holder.approvedDateTimeText.setVisibility(View.VISIBLE);
                                        holder.approvedDateTimeText.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Log.v(TAG, "Got click on approved Date Time Text");

                                            }
                                        });
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), AdminUserProductsActivity.class);
                                        intent.putExtra("uid", prevalentUserData.getCurrentOnlineUser().getPhone());
                                        intent.putExtra("orderNumber", holder.orderNumber);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }



                    }


                };
                */

       // ordersList.setAdapter(adapter);
        //adapter.startListening();
    }


    private void RemoverOrder(String uID)
    {
        ordersRef.child(uID).removeValue();
    }

}

 class OrderLevelGroupViewHolder extends GroupViewHolder {
    public TextView groupTitle;

    public OrderLevelGroupViewHolder(View itemView) {
        super(itemView);
        groupTitle = itemView.findViewById(R.id.order_group_title);
    }
 }

 class AdminOrdersViewHolder extends ChildViewHolder {
     public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress,
             userOrderNumber, orderStatus, requestedOrderDate, orderFulfillmentMethod,
             hasCustomMessage, deliveredBy, approvedDateTimeText;
     Spinner orderStatusSpinner, deliveredBySpinner;
     ListView lineItemsListView;
     Button saveChangesButton;

     String userId, orderNumber;
     public AdminOrdersViewHolder(@NonNull View itemView) {
         super(itemView);
         userName = itemView.findViewById(R.id.order_user_name);
         userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
         userTotalPrice = itemView.findViewById(R.id.order_total_price);
         userDateTime = itemView.findViewById(R.id.order_date_time);
         userShippingAddress = itemView.findViewById(R.id.order_address_city);
         userOrderNumber = itemView.findViewById(R.id.order_number);
         deliveredBy = itemView.findViewById(R.id.delivered_by_textView);
         orderStatus = itemView.findViewById(R.id.order_status);
         requestedOrderDate = itemView.findViewById(R.id.requested_date_time);
         orderFulfillmentMethod = itemView.findViewById(R.id.order_fulfillment_method);
         hasCustomMessage = itemView.findViewById(R.id.has_custom_message);
         orderStatusSpinner = itemView.findViewById(R.id.order_status_spinner);
         approvedDateTimeText = itemView.findViewById(R.id.approved_date_time);
         deliveredBySpinner = itemView.findViewById(R.id.delivered_by_spinner);
         lineItemsListView = itemView.findViewById(R.id.order_line_item_list);
         saveChangesButton = itemView.findViewById(R.id.save_changes_button);
     }
 }



 class ExpandableOrderListRecyclerAdapter extends
         ExpandableRecyclerViewAdapter<OrderLevelGroupViewHolder,AdminOrdersViewHolder>{
    Context currentContext;
     public ExpandableOrderListRecyclerAdapter(List<? extends ExpandableGroup> groups) {
         super(groups);
     }

     @Override
     public OrderLevelGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
         this.currentContext = parent.getContext();
         LayoutInflater inflater = LayoutInflater.from(currentContext);

         // Inflate the custom layout
         View contactView = inflater.inflate(R.layout.fragment_order_group_item, parent, false);

         // Return a new holder instance

         return new OrderLevelGroupViewHolder(contactView);
     }

     @Override
     public AdminOrdersViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
         this.currentContext = parent.getContext();
         LayoutInflater inflater = LayoutInflater.from(currentContext);

         // Inflate the custom layout
         View contactView = inflater.inflate(R.layout.orders_layout, parent, false);

         // Return a new holder instance

         return new AdminOrdersViewHolder(contactView);
     }

   /*  @Override
     public OrderLineItemsViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
         currentContext = parent.getContext();
         LayoutInflater inflater = LayoutInflater.from(currentContext);

         // Inflate the custom layout
         View contactView = inflater.inflate(R.layout.cart_items_layout, parent, false);

         // Return a new holder instance
         return new OrderLineItemsViewHolder(contactView);
     }*/

     @Override
     public void onBindChildViewHolder(final AdminOrdersViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
         final String TAG = "OrdActOnBndChldVwHldr";
         Log.v(TAG,"position =" + flatPosition + " order =" + group);
         final Prevalent prevalentUserData = Prevalent.getInstance();
         final PrevalentOrdersForAdmins prevalentOrdersForAdmins = PrevalentOrdersForAdmins.getInstance();
        //Getting the data from the position
         final boolean[] isTheViewCompressed = {true};

         final AdminOrders orderRow = (AdminOrders) group.getItems().get(childIndex);
         //Log.v(TAG, "position =" + position + "orderRow = " + orderRow);
         holder.userId = orderRow.getPhone();
         holder.orderNumber = orderRow.getOrderNumber();

         // setting the data from orderRow into the holder object
         holder.userName.setText("Name: " + orderRow.getName());
         holder.userPhoneNumber.setText("Mob:" + orderRow.getPhone());
         holder.userTotalPrice.setText("Order Total: " + orderRow.getTotalAmount());
         holder.userDateTime.setText("Placed on:\n" + orderRow.getDate());
         holder.userShippingAddress.setText("Address:" + orderRow.getAddress());
         holder.userOrderNumber.setText("Order#" + orderRow.getOrderNumber());
         holder.orderStatus.setText("Status: " + orderRow.getState());
         if (prevalentUserData.getUserType().equals("Admin")) {
             //check if the user is admin and then setup data for editing orders from this screen itself
             //-------Setting up Order Status Drop Down

             ArrayAdapter<String> OrderStatusSpinnerAdapter =new ArrayAdapter<>(

                     currentContext,
                     android.R.layout.simple_spinner_item,
                     new ArrayList<>(prevalentOrdersForAdmins.getOrderStatus().keySet()));
             OrderStatusSpinnerAdapter.setDropDownViewResource
                     (android.R.layout.simple_spinner_dropdown_item);
             holder.orderStatusSpinner.setAdapter(OrderStatusSpinnerAdapter);
         }

         if (orderRow.getRequestedPickupDate() != null) {
             SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
             String requestedPickDateToDisplay = dateFormat.format(orderRow.getRequestedPickupDate())
                     + "\n" + orderRow.getRequestedPickupTime();
             holder.requestedOrderDate.setText("Requested Pick Date:" + requestedPickDateToDisplay);
         }
         if (orderRow.getApprovedFulfillmentTime() != null
                 && !orderRow.getApprovedFulfillmentTime().isEmpty()) {
             holder.approvedDateTimeText.setText("Approved Date/Time: "
                     + orderRow.getApprovedFulfillmentTime());
         } else {
             holder.approvedDateTimeText.setText("Change Delivery Date/Time: ");
         }
         // ---------- fill the delivery Admin spinner if the user is admin
         List<String> deliveryAdminNameList = new ArrayList<>();
         for (int i =0; i< prevalentOrdersForAdmins.getDeliveryAdminsList().size(); i++) {
             deliveryAdminNameList.add(prevalentOrdersForAdmins.getDeliveryAdminsList().get(i).getName());
         }
         if (prevalentUserData.getUserType().equals("Admin")) {
             ArrayAdapter<String> DeliveryAdminAdapter=new ArrayAdapter<>(
                     currentContext,
                     android.R.layout.simple_spinner_item,
                     deliveryAdminNameList
             );
             DeliveryAdminAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
             holder.deliveredBySpinner.setAdapter(DeliveryAdminAdapter);

         }
         holder.orderFulfillmentMethod.setText("Order Type: " + orderRow.getFulfillmentMethod());
         boolean doesOrderHaveCustomMessage = false;

         Iterator iterator =  orderRow.getLineItems().entrySet().iterator();
         while (iterator.hasNext()) {
             Map.Entry lineItemDetailMap = (Map.Entry)iterator.next();
             Cart lineItemDetails = (Cart)lineItemDetailMap.getValue();
             if(lineItemDetails.getCustomMessage() != null
                     && !lineItemDetails.getCustomMessage().isEmpty()) {
                 doesOrderHaveCustomMessage = true;
             }
         }
         if( doesOrderHaveCustomMessage) {
             holder.hasCustomMessage.setText("Order Has Custom Message");
         }

         if (prevalentUserData.getUserType().equals("Admin")) {
             holder.itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Log.v(TAG, "View =" + v);
                     if (prevalentUserData.getUserType().equals("Admin") && isTheViewCompressed[0]) {
                         // Admin user clicked on the order tile for the first time
                         // expanding the tile to show immediately editable items
                         // second click will take admin to order   detail page
                         isTheViewCompressed[0] = false;
                         Log.v(TAG, "Click on detected on the order tile");
                         holder.deliveredBySpinner.setVisibility(View.VISIBLE);
                         holder.orderStatusSpinner.setVisibility(View.VISIBLE);
                         holder.approvedDateTimeText.setVisibility(View.VISIBLE);
                         holder.approvedDateTimeText.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 Log.v(TAG, "Got click on approved Date Time Text");
                             }
                         });
                         holder.saveChangesButton.setVisibility(View.VISIBLE);
                         holder.saveChangesButton.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(final View v) {
                                String selectedOrderStatus = holder.orderStatusSpinner.getSelectedItem().toString();

                                DatabaseReference ordersRef;
                                ordersRef = prevalentOrdersForAdmins.getOrdersDataRef();
                                orderRow.setState(selectedOrderStatus);
                                if (orderRow.getFulfillmentMethod().equals("Delivery")) {
                                    String selectedDeliveryAdmin = holder.deliveredBySpinner.getSelectedItem().toString();
                                    orderRow.setDeliveryAdminName(selectedDeliveryAdmin);
                                }
                                final HashMap<String, Object> ordersMap = new HashMap<>();
                                ordersMap.put("O"+orderRow.getPhone()+"-"+orderRow.getOrderNumber(),
                                                orderRow);
                                 ordersRef.updateChildren(ordersMap)
                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 if (task.isSuccessful()) {
                                                     FirebaseDatabase.getInstance().getReference()
                                                             .child("Orders")
                                                             .child(orderRow.getPhone())
                                                             .updateChildren(ordersMap)
                                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                 @Override
                                                                 public void onComplete(@NonNull Task<Void> task) {
                                                                     if (task.isSuccessful()) {
                                                                         Toast.makeText(v.getContext(),
                                                                                 "Order has been Updated successfully.",
                                                                                 Toast.LENGTH_SHORT).show();
                                                                     }
                                                                 }
                                                             });

                                                 }
                                             }
                                         });





                             }
                         });
                     }
                     /*else {
                         Intent intent = new Intent(v.getContext(), AdminUserProductsActivity.class);
                         intent.putExtra("uid", prevalentUserData.getCurrentOnlineUser().getPhone());
                         intent.putExtra("orderNumber", holder.orderNumber);
                         v.getContext().startActivity(intent);
                     }*/
                 }
             });
         }

         //TODO: Get the line Items to show
         ArrayAdapter adapter = new OrderLineItemsAdapter (
                        this.currentContext,
                        R.layout.cart_items_layout,
                        orderRow.getArrayListOfLineItems());
         holder.lineItemsListView.setAdapter(adapter);
         ListViewHeightUtil.setListViewHeightBasedOnChildren(holder.lineItemsListView);

     }

     @Override
     public void onBindGroupViewHolder(final OrderLevelGroupViewHolder holder, int flatPosition, ExpandableGroup group) {
         final String TAG = "OrdActOnBndGrpVwHldr";
         Log.v(TAG,"position =" + flatPosition + " order =" + group);
         holder.groupTitle.setText(group.getTitle());
     }
 }

class OrderLineItemsAdapter extends ArrayAdapter<Cart> {

    private ArrayList<Cart> dataSet;
    Context mContext;
    int layout;
    String TAG = "OrderLineItemsAdapter";
    private static class OrderLineItemsViewHolder {
        public TextView productNameText, productQuantityText, customMessageText, productPriceText;
    }

    public OrderLineItemsAdapter(Context context, int layout, ArrayList<Cart> data) {
        super(context, layout, data);
        Log.v (TAG, "constructor data size received = " + data.size());
        for (int i = 0; i< data.size(); i++) {
            Log.v(TAG, "index = " + i + " data = " + data.get(i));
        }
        this.layout = layout;
        this.dataSet = data;
        this.mContext=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get Data from the position
        Log.v (TAG, "getView, position =" + position);
        Cart cartItem = getItem(position);
        OrderLineItemsViewHolder viewHolder;


        if (convertView == null) {
            viewHolder = new OrderLineItemsViewHolder();
            //since convertView is null, we have to initialize it first
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
            // initializing the view with the layout elements
            viewHolder.productNameText = convertView.findViewById(R.id.cart_item_product_name);
            viewHolder.productQuantityText = convertView.findViewById(R.id.cart_item_product_quantity);
            viewHolder.customMessageText = convertView.findViewById(R.id.cart_item_custom_message);
            viewHolder.productPriceText = convertView.findViewById(R.id.cart_item_product_price);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OrderLineItemsViewHolder) convertView.getTag();
        }

        viewHolder.productNameText.setText(cartItem.getPname());
        viewHolder.productQuantityText.setText(cartItem.getQuantity());
        viewHolder.customMessageText.setText(cartItem.getCustomMessage());
        viewHolder.productPriceText.setText(cartItem.getPrice());

        return convertView;
    }
}

