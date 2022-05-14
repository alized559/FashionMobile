package com.fashion.fashionmobile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.fashion.fashionmobile.helpers.UserLogin;
import com.fashion.fashionmobile.ui.home.HomeFragment;
import com.fashion.fashionmobile.ui.userpanel.UserPanelFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.fashion.fashionmobile.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    public static Context CurrentContext = null;

    public static ImageView profileImage = null;
    public static TextView profileName = null, profileEmail = null;
    public static CardView profileCard = null;

    public static NavController navController = null;

    public static FloatingActionButton CartFab = null;
    public static ImageView UserLikesImage = null, UserLogoutImage = null;

    public static ArrayList<String> CurrencyList = new ArrayList<String>(){
        {
            add("US, USD");
            add("LB, LBP");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserLogin.AttemptAutoLogin(this);

        CurrentContext = this;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        CartFab = binding.appBarMain.cartFab;
        binding.appBarMain.cartFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This is going to be the cart", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_products, R.id.nav_cart, R.id.nav_userpanel)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View header = navigationView.getHeaderView(0);
        profileImage = header.findViewById(R.id.user_profile_picture_navbar);
        profileName = header.findViewById(R.id.user_username_text_navbar);
        profileEmail = header.findViewById(R.id.user_email_text_navbar);
        profileCard = header.findViewById(R.id.user_profile_picture_navbar_card);

        MainActivity.profileCard.setVisibility(View.INVISIBLE);

        UserLikesImage = binding.appBarMain.mainToolbarLikes;
        UserLogoutImage = binding.appBarMain.mainToolbarLogout;

        CartFab.setVisibility(View.GONE);
        UserLikesImage.setVisibility(View.GONE);
        UserLogoutImage.setVisibility(View.GONE);

        UserLikesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This is going to be the user likes", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        UserLogoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserLogin.Logout();
                navController.navigateUp();
            }
        });

        getSupportActionBar().setTitle("");

        MenuItem spinnerItem = navigationView.getMenu().findItem(R.id.nav_currency);
        Spinner spinner = (Spinner) spinnerItem.getActionView();

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        CurrencyList); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setSelection(CurrencyList.indexOf(UserLogin.CurrentCurrency));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                UserLogin.UpdateCurrency(CurrencyList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}