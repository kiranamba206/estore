package com.vetzforpetz.estore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vetzforpetz.estore.Model.AdminOrders;
import com.vetzforpetz.estore.Model.Users;
import com.vetzforpetz.estore.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vetzforpetz.estore.ui.datePicker.DatePickerFragment;
import com.vetzforpetz.estore.Prevalent.PrevalentOrdersForAdmins;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    final String TAG = "ConfrmFinlOrderActvty";
    private EditText nameEditText, phoneEditText, addressEditText, cityEditText;
    private Button confirmOrderBtn, button_pickupDate;
    private RadioGroup orderFulfillmentMethodRadioGroup;
    private RadioButton orderPickupRadioButton, orderDeliveryRadioButton;
    private TextView editTextDate_pickupDate;
    private Spinner spinner_timeSelection;
    private String orderNumber = "";


    private String totalAmount = "";

    private Prevalent mPrevalent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);
        mPrevalent = Prevalent.getInstance();
        Users currentUser = mPrevalent.getCurrentOnlineUser();

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price =  $ " + totalAmount, Toast.LENGTH_SHORT).show();

        orderFulfillmentMethodRadioGroup = findViewById(R.id.radioGroup_pickOrDelivery);
        orderPickupRadioButton = findViewById(R.id.radioButton_pickup);
        orderDeliveryRadioButton = findViewById(R.id.radioButton_delivery);
        spinner_timeSelection = findViewById(R.id.spinner_pickupTime);
        editTextDate_pickupDate = findViewById(R.id.editTextDate_pickupDate);
        confirmOrderBtn = findViewById(R.id.confirm_final_order_btn);
        nameEditText = findViewById(R.id.shippment_name);
        phoneEditText = findViewById(R.id.shippment_phone_number);
        addressEditText = findViewById(R.id.shippment_address);
        cityEditText = findViewById(R.id.shippment_city);
        button_pickupDate = findViewById(R.id.button_pickupDate);

        //Preloading values from the user's settings
        nameEditText.setText(currentUser.getName());
        phoneEditText.setText(currentUser.getPhone());
        addressEditText.setText(currentUser.getAddress());
        // city is currently unavailable in the user's settings as of May 23rd 2020

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }
        });
        if (mPrevalent.getOrderHeader() == null) {
            mPrevalent.setOrderHeader(new AdminOrders());
        }
        if(mPrevalent.getOrderHeader().getFulfillmentMethod() == null) {
            mPrevalent.getOrderHeader().setFulfillmentMethod("Pickup");
        } else if (mPrevalent.getOrderHeader().getFulfillmentMethod().equals("Delivery")) {

            orderPickupRadioButton.toggle();
            orderDeliveryRadioButton.toggle();
        }

        Date today = Calendar.getInstance().getTime();//getting date
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");//formating according to my need
        String date = formatter.format(today);
        editTextDate_pickupDate.setText(date);
        mPrevalent.getOrderHeader().setRequestedPickupDate(today);

        //------------ Setting the Drop Down UI for Pickup/Delivery Times --------
        ArrayAdapter<CharSequence> timeSelectionSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.pickup_delivery_times_array, android.R.layout.simple_spinner_item);

        timeSelectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner

        spinner_timeSelection.setAdapter(timeSelectionSpinnerAdapter);

        spinner_timeSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPrevalent.getOrderHeader().setRequestedPickupTime(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mPrevalent.getOrderHeader().setApprovedFulfillmentTime("Any Convenient Time");
            }
        });
        //----------- Finished Setting up the Pickup/Delivery Times DropDown


        orderFulfillmentMethodRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (!orderPickupRadioButton.isChecked()) {
                    Log.v(TAG, "inside setOnCheckedChangeListener checkedId is delivery");
                    // Disable the pickup time and date
                    mPrevalent.getOrderHeader().setFulfillmentMethod("Delivery");
                } else {
                    Log.v(TAG, "inside setOnCheckedChangeListener checkedId is pickup");
                    mPrevalent.getOrderHeader().setFulfillmentMethod("Pickup");
                }
            }
        });

        button_pickupDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPrevalent.getOrderHeader().getFulfillmentMethod().equals("Pickup")) {
                    showDatePickerDialog(v);
                }
            }
        });
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.showDatePickerFragmentForOrderPage(getSupportFragmentManager(),
                "datePicker", editTextDate_pickupDate, mPrevalent.getOrderHeader().getRequestedPickupDate()
                        );
        //newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void updatePickDate() {
        editTextDate_pickupDate.setText(mPrevalent.getOrderHeader().getRequestedPickupDate().toString());
    }

    private void Check() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())) {
            Toast.makeText(this, "Please enter your full name.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phoneEditText.getText().toString())) {
            Toast.makeText(this, "Please enter your phone number.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(addressEditText.getText().toString())) {
            Toast.makeText(this, "Please enter your address.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cityEditText.getText().toString())
                && (cityEditText.getText().equals("Mysore") || cityEditText.getText().equals("Mysuru"))) {
            Toast.makeText(this, "Please enter your city name.", Toast.LENGTH_SHORT).show();
        } else {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {
        final String saveCurrentDate, saveCurrentTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        //generate order number using datetime
        SimpleDateFormat dataTime = new SimpleDateFormat("yyMMddHHmmssZ");
        orderNumber = dataTime.format(calForDate.getTimeInMillis());



/*
    Order Status
    01 - Not Shipped
    02 - Approved with changed date/time
    03 - Processing
    04 - Ready for Pickup
    05 - Out for Delivery
    06 - Complete

 */
        AdminOrders mAdminOrder = new AdminOrders(
                nameEditText.getText().toString(),
                phoneEditText.getText().toString(),
                addressEditText.getText().toString(),
                cityEditText.getText().toString(),
                "Not Shipped",
                saveCurrentDate,
                saveCurrentTime,
                totalAmount,
                orderNumber,
                mPrevalent.getOrderLineItems()
                );
        mAdminOrder.setUserId(Prevalent.getCurrentOnlineUser().getPhone());
        PrevalentOrdersForAdmins prevalentOrdersForAdmins = PrevalentOrdersForAdmins.getInstance();
        mAdminOrder.setOrderStatusCode(prevalentOrdersForAdmins.getOrderStatusCode("Not Shipped"));
        mAdminOrder.setRequestedPickupTime(mPrevalent.getOrderHeader().getRequestedPickupTime());

        if (orderPickupRadioButton.isChecked()) {
            //order is pickup
            mAdminOrder.setFulfillmentMethod("Pickup");
            mAdminOrder.setRequestedPickupDate(mPrevalent.getOrderHeader().getRequestedPickupDate());

        } else {
            //order is delivery
            mAdminOrder.setFulfillmentMethod("Delivery");
        }

        final HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("O"+mPrevalent.getCurrentOnlineUser().getPhone() + "-" + orderNumber, mAdminOrder);


        Log.v(TAG, "Order object created : " + ordersMap);


        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(mPrevalent.getCurrentOnlineUser().getPhone());


        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Once order is placed, remove the items from cart
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User View")
                            .child(mPrevalent.getCurrentOnlineUser().getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Your final order has been placed successfully.", Toast.LENGTH_SHORT).show();
                                        sendEmail();
                                        sendSMS();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        //intent.putExtra("AppUser", "User");
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                    //Once order is placed, add copy of order into Admin View
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("Admin View")
                            //.child(mPrevalent.getCurrentOnlineUser().getPhone())
                            .updateChildren(ordersMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Your final order has been placed successfully.", Toast.LENGTH_SHORT).show();
                                        sendEmail();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        //intent.putExtra("AppUser", "User");
                                        startActivity(intent);
                                        finish();

                                    }
                                }
                            });
                    mPrevalent.resetCart();
                }
            }
        });
    }


    protected void sendEmail() {
        /*Log.i("Send email", "");
        String[] TO = {"kiranamba206@gmail.com"};
        String[] CC = {"kiranamba206@gmail.com"};
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        //emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(android.content.Intent.EXTRA_CC, CC);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Your order for VetzForPetz");
        emailIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, orderNumber);

        try {
            startActivity(android.content.Intent.createChooser(emailIntent, "Send mail"));
            finish();
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ConfirmFinalOrderActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }*/

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"VetzForPetz - Order");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Order placed");
        startActivity(Intent.createChooser(emailIntent,"Order Statement"));
    }

    protected void sendSMS(){
        try{
            SmsManager smgr = SmsManager.getDefault();
            smgr.sendTextMessage(phoneEditText.getText().toString(),null,"Order Placed",null,null);
            Toast.makeText(ConfirmFinalOrderActivity.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(ConfirmFinalOrderActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
        }
    }


}