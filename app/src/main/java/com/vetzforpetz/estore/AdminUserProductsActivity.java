package com.vetzforpetz.estore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vetzforpetz.estore.Model.AdminOrders;
import com.vetzforpetz.estore.Model.Cart;
import com.vetzforpetz.estore.Prevalent.PrevalentOrdersForAdmins;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AdminUserProductsActivity extends AppCompatActivity {

    private TextView orderTotalTextView, orderUserPhoneTextView, orderAddressTextView,
                orderFulfillmentMethod, orderRequestedPickupDateTime;
    private RecyclerView productsList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartListRef;

    private String userID;
    private String orderNumber;
    private String TAG = "AdminUserProductsActivity";
    AdminOrders activeOrder = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);
        PrevalentOrdersForAdmins prevalentOrdersForAdmins = PrevalentOrdersForAdmins.getInstance();
        userID = getIntent().getStringExtra("uid");
        orderNumber =getIntent().getStringExtra("orderNumber");

        orderRequestedPickupDateTime = findViewById(R.id.order_requested_pickup_date_time);
        orderAddressTextView = findViewById(R.id.order_address);
        orderFulfillmentMethod= findViewById(R.id.order_delivery_type);
        orderUserPhoneTextView = findViewById(R.id.order_phone_number);
        orderTotalTextView = findViewById(R.id.order_total_price);
        productsList = findViewById(R.id.products_list); // This is for the recyclerview
        productsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);

        cartListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List").child("Admin View").child(userID).child(orderNumber);

        Log.v(TAG, "Intent variables userId=" + userID + " ordernumber =" + orderNumber);
        for (AdminOrders tempOrder: prevalentOrdersForAdmins.getOrdersToBeProcessed().getOrdersList()){

        }
        Iterator iterator =prevalentOrdersForAdmins.getOrdersToBeProcessed().getOrdersList().iterator();
        while (iterator.hasNext() &&  activeOrder == null) {
            AdminOrders tempOrder = (AdminOrders)iterator.next();
            if (tempOrder.getOrderNumber().equals(orderNumber) &&
                    tempOrder.getPhone().equals(userID)) {
                activeOrder = tempOrder;

                Log.v(TAG, "Found matching order = " + activeOrder);
            }
            Log.v(TAG, "iterator" + tempOrder);
        }

//        iterator = activeOrder.getLineItems().entrySet().iterator();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (activeOrder == null) {
            //TODO: Display Error Message
        } else {
            //set the order information data
            orderTotalTextView.setText("Total: " +activeOrder.getTotalAmount());
            orderUserPhoneTextView.setText("Mob: " + activeOrder.getPhone());
            orderAddressTextView.setText("Address: "+ activeOrder.getAddress());
            orderFulfillmentMethod.setText("Order Type: " + activeOrder.getFulfillmentMethod());
            if (activeOrder.getRequestedPickupDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                String requestedPickDateToDisplay = dateFormat.format(activeOrder.getRequestedPickupDate())
                         + activeOrder.getRequestedPickupTime();
                orderRequestedPickupDateTime.setText("Requested Pick Date:" + requestedPickDateToDisplay);
            } else {
                orderRequestedPickupDateTime.setText("");
                orderRequestedPickupDateTime.setHeight(0);
            }
            AdminProductsArrayAdapter adminProductsArrayAdapter =
                        new AdminProductsArrayAdapter(activeOrder.getLineItems());
            productsList.setAdapter(adminProductsArrayAdapter);
        }

    }

    public  class AdminProductsArrayAdapter
            extends RecyclerView.Adapter<AdminProductsArrayAdapter.LineItemViewHolder>
    {
        List<Cart> cartList = null;
        String TAG = "AdminProductsArrayAdapter";

        public  class LineItemViewHolder
                extends RecyclerView.ViewHolder
        {
            public TextView productNameText, productQuantityText, customMessageText, productPriceText;

            public LineItemViewHolder(@NonNull View itemView) {
                super(itemView);
                productNameText = itemView.findViewById(R.id.cart_item_product_name);
                productQuantityText = itemView.findViewById(R.id.cart_item_product_quantity);
                customMessageText = itemView.findViewById(R.id.cart_item_custom_message);
                productPriceText = itemView.findViewById(R.id.cart_item_product_price);
            }
        }
        public AdminProductsArrayAdapter(HashMap<String, Cart> cartHashMap) {
            Iterator iterator = cartHashMap.entrySet().iterator();

            while(iterator.hasNext()) {
                if (cartList == null ) {
                    cartList = new ArrayList<>();
                }
                Map.Entry mapElement = (Map.Entry)iterator.next();
                cartList.add((Cart)mapElement.getValue());
            }
            Log.v(TAG, "Constructor, cartList = " + cartList);

        }

        @NonNull
        @Override
        public LineItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View contactView = inflater.inflate(R.layout.cart_items_layout, parent, false);

            // Return a new holder instance
            return new LineItemViewHolder(contactView);
        }

        @Override
        public void onBindViewHolder(@NonNull LineItemViewHolder holder, int position) {
            Cart cartItem = cartList.get(position);
            if(cartItem.getCustomMessage() != null && !cartItem.getCustomMessage().isEmpty()) {
                holder.customMessageText.setText("Custom Message: " + cartItem.getCustomMessage());
            } else {
                holder.customMessageText.setText("");
                holder.customMessageText.setHeight(0);
            }
            holder.productNameText.setText("Product: " + cartItem.getPname());
            holder.productPriceText.setText("Price: " + cartItem.getPrice());
            holder.productQuantityText.setText("Quantity: " + cartItem.getQuantity());
        }

        @Override
        public int getItemCount() {
            return cartList.size();
        }

    }
    /*
    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef, Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model)
            {
                holder.txtProductQuantity.setText("Quantity : " + model.getQuantity());
                holder.txtProductPrice.setText("Price " + model.getPrice() + "Rs");
                holder.txtProductName.setText(model.getPname());
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        productsList.setAdapter(adapter);
        adapter.startListening();
    }
    */
}
