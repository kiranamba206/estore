package com.vetzforpetz.estore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vetzforpetz.estore.Model.AdminCartOrder;
import com.vetzforpetz.estore.Model.AdminOrders;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vetzforpetz.estore.Model.Cart;
import com.vetzforpetz.estore.Model.Users;
import com.vetzforpetz.estore.Prevalent.Prevalent;
import com.vetzforpetz.estore.Prevalent.PrevalentOrdersForAdmins;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AdminNewOrderActivity extends AppCompatActivity {

    private RecyclerView orderListView;
    private DatabaseReference ordersRef, deliveryAdminsRef;
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

        ordersRef = prevalentOrdersForAdmins.getOrdersDataRef();
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
                        Log.v(TAG, "all choldren of dataSnapshot =" + tempAdmin);
                    }
                    prevalentOrdersForAdmins.setDeliveryAdminsList(deliveryAdminsList);
                }
                Log.v(TAG, "Delivery Admins Query successful, list =" + dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        orderListView = (RecyclerView) findViewById(R.id.orders_list_view);

        orderListView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (prevalentUserData.getUserType() == "Admin") {
                    for (DataSnapshot userListLevel : dataSnapshot.getChildren()) {
                        Log.v(TAG, "userListLevel " + userListLevel.toString());
                        //String userId = userListLevel.getKey();
                        for (DataSnapshot orderListLevel : userListLevel.getChildren()) {
                            if (orderListLevel.getKey().equals("Products")) {
                                //these are older format orders
                            } else {
                                Log.v(TAG, "orderListLevel" + orderListLevel);
                                prevalentOrdersForAdmins.getOrdersToBeProcessed()
                                        .addItemToOrderList(orderListLevel.getValue(AdminOrders.class));
                            }
                        }
                    }
                } else {
                    // this is for normal user, where the database reference has already come
                    // with phone number level reference
                    Log.v(TAG, "userListLevel " + dataSnapshot.toString());
                    //String userId = userListLevel.getKey();
                    for (DataSnapshot orderListLevel : dataSnapshot.getChildren()) {
                        if (orderListLevel.getKey().equals("Products")) {
                            //these are older format orders
                        } else {
                            Log.v(TAG, "orderListLevel" + orderListLevel);
                            prevalentOrdersForAdmins.getOrdersToBeProcessed()
                                    .addItemToOrderList(orderListLevel.getValue(AdminOrders.class));
                        }
                    }

                }
                Log.v(TAG, "ordersToBeProcessed = " + prevalentOrdersForAdmins.getOrdersToBeProcessed());

                AdminOrdersArrayAdapter ordersArrayAdapter = new AdminOrdersArrayAdapter(
                                //getApplicationContext(),
                                //        R.layout.orders_layout,
                                        prevalentOrdersForAdmins.getOrdersToBeProcessed().getOrdersList());


                orderListView.setAdapter(ordersArrayAdapter);

                //View ordersLayout = inflater.in
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v(TAG, "ValueEventListener cancelled. Error =" + databaseError);
            }
        };
        ordersRef.addListenerForSingleValueEvent(eventListener);
    }

    /*
    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<AdminCartOrder> options =
                new FirebaseRecyclerOptions.Builder<AdminCartOrder>()
                        .setQuery(ordersRef, AdminCartOrder.class)
                        .build();

            FirebaseRecyclerAdapter<AdminCartOrder, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminCartOrder, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder,
                                                    final int position,
                                                    @NonNull final AdminCartOrder model)
                    {
                        Log.v("AdmnNewOrdrAct","position =" + position + " order =" + model);
                       /*
                        holder.userOrderNumber.setText("Order# : " + model.getOrderNumber());
                        holder.userName.setText("Name : " + model.getName());
                        holder.userPhoneNumber.setText("Phone : " + model.getPhone());
                        holder.userTotalPrice.setText("Total Amount :  Rs. " + model.getTotalAmount());
                        holder.userDateTime.setText("Order on : " + model.getDate() + "  " + model.getTime());
                        holder.userShippingAddress.setText("Shipping Address : " + model.getAddress() + ", " + model.getCity());
                        holder.orderStatus.setText("Status: " + model.getState());
                        holder.requestedOrderDate.setText("Requested on " + model.getRequestedPickupDate() + " " + model.getRequestedPickupTime());
                        holder.orderFulfillmentMethod.setText("Order Type: " + model.getFulfillmentMethod());

                        holder.ShowOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                String uID = getRef(position).getKey();

                                Intent intent = new Intent(AdminNewOrderActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("uid", uID);
                                startActivity(intent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                CharSequence[] options = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrderActivity.this);
                                builder.setTitle("Is this order shipped?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        if (i == 0)
                                        {
                                            String uID = getRef(position).getKey();

                                            RemoverOrder(uID);
                                        }
                                        else
                                        {
                                            finish();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                        return new AdminOrdersViewHolder(view);
                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();
    }
    */

        public class AdminOrdersArrayAdapter
                    extends RecyclerView.Adapter<AdminOrdersArrayAdapter.OrderViewHolder>
    {
        private String TAG = "AdminOrdersViewHolder";
        List<AdminOrders> orderListDataSet;


        public  class OrderViewHolder
                        extends RecyclerView.ViewHolder {
            public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress,
                    userOrderNumber, orderStatus, requestedOrderDate, orderFulfillmentMethod,
                    hasCustomMessage, deliveredBy, approvedDateTimeText;
            Spinner orderStatusSpinner, deliveredBySpinner;

            private String userId, orderNumber;

            public OrderViewHolder(@NonNull View itemView) {
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
            }



        }

        public AdminOrdersArrayAdapter(List<AdminOrders> ordersList) {
            orderListDataSet = ordersList;
        }

        //single order row is obtained in the OnCreateViewHolder. Attaching this to the orders_layout XML to inflate it.
        // Also associated it with the OrderViewHolder to connect the inflater, xml and ViewHolder
        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View contactView = inflater.inflate(R.layout.orders_layout, parent, false);

            // Return a new holder instance
            OrderViewHolder viewHolder = new OrderViewHolder(contactView);
            return viewHolder;


        }
        // Populating data in a row
        @Override
        public void onBindViewHolder(@NonNull final OrderViewHolder holder, int position) {
            //Getting the data from the position
            final boolean[] isTheViewCompressed = {true};
            AdminOrders orderRow = orderListDataSet.get(position);
            Log.v(TAG, "position =" + position + "orderRow = " + orderRow);
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
                                    Log.v(TAG, "Got clikc on approved Date Time Text");

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




        @Override
        public int getItemCount() {
            return orderListDataSet.size();
        }
    }

    private void RemoverOrder(String uID)
    {
        ordersRef.child(uID).removeValue();
    }

}

