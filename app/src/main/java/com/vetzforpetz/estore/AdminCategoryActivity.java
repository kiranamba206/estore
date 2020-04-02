package com.vetzforpetz.estore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity
{

    private ImageView dogCategory, catCategory, vetServices, customFood, petAccessories;

    private Button LogoutBtn, CheckOrdersBtn, updateProductsBtn;

    private String appUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        //Administartion activity of Admin
        LogoutBtn = findViewById(R.id.admin_logout_btn);
        CheckOrdersBtn = findViewById(R.id.check_orders_btn);
        updateProductsBtn = findViewById(R.id.update_btn);


        appUser = getIntent().getStringExtra("AppUser");

        if(appUser.equals("User")) {
            updateProductsBtn.setVisibility(View.INVISIBLE);
            CheckOrdersBtn.setVisibility(View.INVISIBLE);
            LogoutBtn.setVisibility(View.INVISIBLE);
        }
        else {

            // Admin clicking on Maintain Products Button
            updateProductsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AdminCategoryActivity.this, HomeActivity.class);
                    intent.putExtra("AppUser", "Admin");
                    startActivity(intent);
                }
            });

            // Admin clicking on Check Orders Button
            CheckOrdersBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AdminCategoryActivity.this, AdminNewOrderActivity.class);
                    startActivity(intent);
                }
            });
            // Admin clicking on Logout Button
            LogoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AdminCategoryActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }

        dogCategory = findViewById(R.id.t_dogCategory);
        catCategory = findViewById(R.id.t_catCategory);
        vetServices = findViewById(R.id.t_vetServices);
        customFood = findViewById(R.id.t_custumFood);
        petAccessories = findViewById(R.id.t_accessories);


        dogCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (appUser.equals("User")) {
                    Intent intent = new Intent(AdminCategoryActivity.this, ProductListActivity.class);
                    intent.putExtra("category", "Dog Category");
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                    intent.putExtra("category", "Dog Category");
                    startActivity(intent);
                }
            }
        });


        catCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (appUser.equals("User")) {
                    Intent intent = new Intent(AdminCategoryActivity.this, ProductListActivity.class);
                    intent.putExtra("category", "Cat Category");
                    startActivity(intent);

                } else {

                    Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                    intent.putExtra("category", "Cat Category");
                    startActivity(intent);
                }
            }
        });


        vetServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (appUser.equals("User")) {
                    Intent intent = new Intent(AdminCategoryActivity.this, ProductListActivity.class);
                    intent.putExtra("category", "Vet Services");
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                    intent.putExtra("category", "Vet Services");
                    startActivity(intent);
                }
            }
        });


        customFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (appUser.equals("User")) {
                    Intent intent = new Intent(AdminCategoryActivity.this, ProductListActivity.class);
                    intent.putExtra("category", "Custom Food");
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                    intent.putExtra("category", "Custom Food");
                    startActivity(intent);
                }
            }
        });
        petAccessories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (appUser.equals("User")) {
                    Intent intent = new Intent(AdminCategoryActivity.this, ProductListActivity.class);
                    intent.putExtra("category", "Accessories");
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                    intent.putExtra("category", "Accessories");
                    startActivity(intent);
                }
            }
        });
    }
}