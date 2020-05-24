package com.vetzforpetz.estore.Prevalent;



import com.vetzforpetz.estore.Model.Users;

public class Prevalent
{
    private static final Prevalent mPrevalent = new Prevalent();

    private static Users currentOnlineUser;

    public static final String UserPhoneKey = "UserPhone";
    public static final String UserPasswordKey = "UserPassword";

    public static Prevalent getInstance() {
        return mPrevalent;
    }

    public static Users getCurrentOnlineUser() {
        return currentOnlineUser;
    }

    public static void setCurrentOnlineUser(Users currentOnlineUser) {
        Prevalent.currentOnlineUser = currentOnlineUser;
    }

    public static String getUserPhoneKey() {
        return UserPhoneKey;
    }

    public static String getUserPasswordKey() {
        return UserPasswordKey;
    }
}