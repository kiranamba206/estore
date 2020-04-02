package com.vetzforpetz.estore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vetzforpetz.estore.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, addressEditText, cityEditText;
    private Button confirmOrderBtn;
    private String orderNumber = "";

    private String totalAmount = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);


        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price =  $ " + totalAmount, Toast.LENGTH_SHORT).show();


        confirmOrderBtn = findViewById(R.id.confirm_final_order_btn);
        nameEditText = findViewById(R.id.shippment_name);
        phoneEditText = findViewById(R.id.shippment_phone_number);
        addressEditText = findViewById(R.id.shippment_address);
        cityEditText = findViewById(R.id.shippment_city);


        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }
        });
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

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("name", nameEditText.getText().toString());
        ordersMap.put("phone", phoneEditText.getText().toString());
        ordersMap.put("address", addressEditText.getText().toString());
        ordersMap.put("city", cityEditText.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("ordernumber", orderNumber);
        ordersMap.put("state", "Not shipped");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Once order is placed, remove the items from cart
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Your final order has been placed successfully.", Toast.LENGTH_SHORT).show();
                                        sendEmail();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.putExtra("AppUser", "User");
                                        startActivity(intent);
                                        finish();

                                    }
                                }
                            });
                }
            }
        });
    }


    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {"kiranamba206@gmail.com"};
        String[] CC = {"kavithakiran1406@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your order for VetzForPetz");
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        emailIntent.putExtra(Intent.EXTRA_TEXT, orderNumber);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail"));
            finish();
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ConfirmFinalOrderActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}