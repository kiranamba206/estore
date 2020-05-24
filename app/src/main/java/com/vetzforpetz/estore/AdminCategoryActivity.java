package com.vetzforpetz.estore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.vetzforpetz.estore.Prevalent.Prevalent;

public class AdminCategoryActivity extends AppCompatActivity
{

    private LinearLayout  dogCategory, catCategory, vetServices, customFood, petAccessories;

    private Button LogoutBtn, CheckOrdersBtn, updateProductsBtn;

    private String appUser = "";
    Prevalent mPrevalent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);
        mPrevalent= Prevalent.getInstance();

        //Administartion activity of Admin
        LogoutBtn = findViewById(R.id.admin_logout_btn);
        CheckOrdersBtn = findViewById(R.id.check_orders_btn);
        updateProductsBtn = findViewById(R.id.update_btn);


        appUser = mPrevalent.getUserType();//getIntent().getStringExtra("AppUser");

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
                    //intent.putExtra("AppUser", "Admin");
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

        dogCategory = findViewById(R.id.dog_products_category_layout);
        catCategory = findViewById(R.id.cat_products_category_layout);
        vetServices = findViewById(R.id.vet_services_category_layout);
        customFood = findViewById(R.id.customized_food_category_layout);
        petAccessories = findViewById(R.id.accessories_category_layout);


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