package com.vetzforpetz.estore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.vetzforpetz.estore.Model.Cart;
import com.vetzforpetz.estore.Model.Products;
import com.vetzforpetz.estore.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private String TAG = "ProductDetailsActivity";
    private Button addToCartButton;
    private ImageView productImage;
    private ElegantNumberButton productQuantityButton;
    private TextView productPrice, productDescription, productName;
    private EditText  customMessage;
    private String productID = "", state = "Normal";
    private String preLoadedQuantity = "";
    private ProgressDialog loadingBar;
    private boolean updateAction = false;
    Prevalent mPrevalent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        mPrevalent = Prevalent.getInstance();
        productID = getIntent().getStringExtra("pid");
        preLoadedQuantity = getIntent().getStringExtra("qty");
        updateAction  = getIntent().getBooleanExtra("updateAction", false);

        String customMessageTemp = getIntent().getStringExtra("custom_message");
        Log.i(TAG,"onCreate preLoading qty =" + preLoadedQuantity);

        addToCartButton = findViewById(R.id.pd_add_to_cart_button);
        productQuantityButton = findViewById(R.id.quantity_button);
        productImage = findViewById(R.id.product_image_details);
        productName = findViewById(R.id.product_name_details);
        productDescription = findViewById(R.id.product_description_details);
        productPrice = findViewById(R.id.product_price_details);
        customMessage = (EditText)findViewById(R.id.product_custom_message);
        customMessage.setVisibility(View.INVISIBLE);
        if (customMessageTemp != null && !customMessageTemp.isEmpty()) {
            Log.i(TAG," preloading customMessageTemp =" + customMessageTemp);
            customMessage.setText(customMessageTemp);
        }
        loadingBar = new ProgressDialog(this);

        getProductDetails(productID);
        if (updateAction) {
            // we are updating the cart, so updating the text of the button to say Update
            addToCartButton.setText("UPDATE CART");
        } else {
            addToCartButton.setText("ADD TO CART");
        }

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (state.equals("Order Placed") || state.equals("Order Shipped"))
                {
                    Toast.makeText(ProductDetailsActivity.this, "you can purchase more products, once your order is shipped or confirmed.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    loadingBar.setTitle("Adding to Cart");
                    loadingBar.setMessage("Please wait, adding to cart.");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    addingToCartList();
                }
            }
        });
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        //TODO: Update Check Order State to read from updated Orders
        //CheckOrderState();
    }

    private void addingToCartList()
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", productQuantityButton.getNumber());
        cartMap.put("discount", "");
        String mCustomMessage = customMessage.getText().toString();
        if (!customMessage.getText().toString().isEmpty()) {
            cartMap.put("customMessage", mCustomMessage);
        }

        Cart mCart =  new Cart(productID, productName.getText().toString(),
                productPrice.getText().toString(), productQuantityButton.getNumber(),
                "", mCustomMessage );
        Log.i(TAG, "calling addOrUpdateLineItem " + mCart.toString());
        mPrevalent.addOrUpdateOrderLineItem(mCart);

        cartListRef.child(mPrevalent.getCurrentOnlineUser().getPhone())
                .child("Products").child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(ProductDetailsActivity.this, "Added to Cart List.", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                            //intent.putExtra("AppUser","User" );
                            startActivity(intent);
                        }

                        if (task.isSuccessful())
                        {
                            cartListRef.child("Admin View").child(mPrevalent.getCurrentOnlineUser().getPhone())
                                    .child("Products").child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(ProductDetailsActivity.this, "Added to Cart List.", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();

                                                Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                                //intent.putExtra("AppUser","User" );
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void getProductDetails(String productID)
    {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    Products products = dataSnapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productPrice.setText(products.getPrice());
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                    if (preLoadedQuantity != null && !preLoadedQuantity.isEmpty()) {
                        productQuantityButton.setNumber(preLoadedQuantity);
                    }
                    if (products.getCategory().equals("Custom Food")) {
                        customMessage.setVisibility(View.VISIBLE);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void CheckOrderState()
    {
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(mPrevalent.getCurrentOnlineUser().getPhone());

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String shippingState = dataSnapshot.child("state").getValue().toString();

                    if (shippingState.equals("shipped"))
                    {
                        state = "Order Shipped";
                    }
                    else if(shippingState.equals("not shipped"))
                    {
                        state = "Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
