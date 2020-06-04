package com.vetzforpetz.estore.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.vetzforpetz.estore.AdminCategoryActivity;
import com.vetzforpetz.estore.CartActivity;
import com.vetzforpetz.estore.MainActivity;
import com.vetzforpetz.estore.Prevalent.Prevalent;
import com.vetzforpetz.estore.R;
import com.vetzforpetz.estore.SearchProductsActivity;
import com.vetzforpetz.estore.SettingsActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class NavigationMenuUtil implements NavigationView.OnNavigationItemSelectedListener {

    private Context currentContext;
    private Prevalent mPrevalent;
    private NavigationView navigationView;
    private Activity currentActivity;

    public NavigationMenuUtil(Context context, View viewById) {
        this.currentContext = context;
        this.currentActivity = (Activity)context;

        this.mPrevalent = Prevalent.getInstance();
        if (mPrevalent.getUserType().equals("Admin")) {
            HideOptionsNotNeededForAdmin();
        }

        this.navigationView = (NavigationView) viewById;
        this.navigationView.setNavigationItemSelectedListener(this);
        View headerView = this.navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

        if (!mPrevalent.getUserType().equals("Admin")) {
            userNameTextView.setText(mPrevalent.getCurrentOnlineUser().getName());
            Picasso.get().load(mPrevalent.getCurrentOnlineUser().getImage())
                    .placeholder(R.drawable.profile).into(profileImageView);
        }
    }

    private void HideOptionsNotNeededForAdmin() {
        this.navigationView.findViewById(R.id.nav_cart).setVisibility(View.INVISIBLE);
        this.navigationView.findViewById(R.id.nav_search).setVisibility(View.INVISIBLE);
        this.navigationView.findViewById(R.id.nav_settings).setVisibility(View.INVISIBLE);
        //this.navigationView.findViewById(R.id.nav_order_history).setVisibility(View.INVISIBLE);
        this.navigationView.findViewById(R.id.nav_settings).setVisibility(View.INVISIBLE);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart)
        {
            if (!mPrevalent.getUserType().equals("Admin"))
            {
                Intent intent = new Intent(currentContext, CartActivity.class);
                currentContext.startActivity(intent);
            }

        }
        else if (id == R.id.nav_search)
        {
            // Search option
            if (!mPrevalent.getUserType().equals("Admin"))
            {
                Intent intent = new Intent(currentContext, SearchProductsActivity.class);
                currentContext.startActivity(intent);
            }

        }
        else if (id == R.id.nav_categories)
        {
            if (mPrevalent.getUserType().equals("Admin")) {
                Intent intent = new Intent(currentContext, AdminCategoryActivity.class);
                //intent.putExtra("AppUser","Admin" );
                currentContext.startActivity(intent);
            }
            else {
                Intent intent = new Intent(currentContext, AdminCategoryActivity.class);
                //intent.putExtra("AppUser","User" );
                currentContext.startActivity(intent);
            }
        }
        else if (id == R.id.nav_settings)
        {
            if (!mPrevalent.getUserType().equals("Admin")) {
                //Open settings page
                Intent intent = new Intent(currentContext, SettingsActivity.class);
                currentContext.startActivity(intent);
            }
        }
        /*
        else if (id == R.id.nav_order_history)
        {//TODO have to create activity for the Order history page for users
            if (!mPrevalent.getUserType().equals("Admin")) {
                //Open settings page
                Intent intent = new Intent(currentContext, SettingsActivity.class);
                currentContext.startActivity(intent);
            }
        }

         */
        else if (id == R.id.nav_logout)
        {

            Paper.book().destroy();

            Intent intent = new Intent(currentContext, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            currentContext.startActivity(intent);
            //currentActivity.finish();

        }
        else if (id == R.id.nav_share) {
            shareApp();
        }
        else if (id == R.id.nav_send) {
            shareApp();
        }

        DrawerLayout drawer = currentActivity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareApp() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Vetz For Petz App");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hi,\n Please try the Vetz for Petz app to order your favourite vet products and accessories.\n https://play.google.com/store/apps/details?id=com.vetzforpetz.estore");
        currentContext.startActivity(Intent.createChooser(sharingIntent,"Share via"));
    }

}
