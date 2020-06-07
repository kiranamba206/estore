package com.vetzforpetz.estore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vetzforpetz.estore.Model.Users;
import com.vetzforpetz.estore.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;
import com.vetzforpetz.estore.Prevalent.PrevalentOrdersForAdmins;

import io.paperdb.Paper;

import static android.widget.Toast.*;

public class LoginActivity extends AppCompatActivity
{
    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private TextView AdminLink, NotAdminLink;

    private String parentDbName = "Users";
    private CheckBox chkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = findViewById(R.id.login_btn);
        InputPassword = findViewById(R.id.login_password_input);
        InputPhoneNumber = findViewById(R.id.login_phone_number_input);
        AdminLink = findViewById(R.id.admin_panel_link);
        NotAdminLink = findViewById(R.id.not_admin_panel_link);
        loadingBar = new ProgressDialog(this);

        chkBoxRememberMe = findViewById(R.id.remember_me_chkb);

        // Stores the username and password to Android memory
        // Details - https://github.com/pilgr/Paper
        Paper.init(this);


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginButton.setText(R.string.LOGIN_ADMIN_TITLE);
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginButton.setText(R.string.LOGIN_TITLE);
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });
    }

    private void LoginUser()
    {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(phone))
        {
            makeText(this, R.string.ENTER_PHONE_NUMBER, LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            makeText(this, R.string.ENTER_PASSWORD, LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle(getString(R.string.LOGIN_ACCOUNT_TITLE));
            loadingBar.setMessage(getString(R.string.VALIDATE_CREDENTIALS));
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phone, password);
        }
    }

    private void AllowAccessToAccount(final String phone, final String password)
    {

        final Prevalent mPrevalent = Prevalent.getInstance();
        final PrevalentOrdersForAdmins prevalentOrders = PrevalentOrdersForAdmins.getInstance();
        if(chkBoxRememberMe.isChecked())
        {
            Paper.book().write(mPrevalent.getUserPhoneKey(), phone);
            Paper.book().write(mPrevalent.getUserPasswordKey(), password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            if (parentDbName.equals("Admins"))
                            {
                                makeText(LoginActivity.this, R.string.WELCOME_ADMIN, LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                mPrevalent.setCurrentOnlineUser(usersData);
                                mPrevalent.setUserType("Admin");
                                prevalentOrders.setOrdersDataRef(FirebaseDatabase.getInstance().getReference()
                                        .child("Cart List")
                                        .child("Admin View"));

                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                //intent.putExtra("AppUser","Admin" );
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users"))
                            {
                                makeText(LoginActivity.this, R.string.LOGIN_SUCCESS, LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                mPrevalent.setUserType("User");
                                prevalentOrders.setOrdersDataRef(FirebaseDatabase.getInstance().getReference()
                                        .child("Orders")
                                        .child(phone));

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                //intent.putExtra("AppUser","User" );
                                mPrevalent.setCurrentOnlineUser(usersData);

                                startActivity(intent);
                            }
                        }
                        else
                        {
                            loadingBar.dismiss();
                            makeText(LoginActivity.this, R.string.INCORRECT_PASSWORD, LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    makeText(LoginActivity.this, "Account with this " + phone + " number do not exists.", LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}