package com.fashion.fashionmobile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.fashion.fashionmobile.helpers.UserLogin;
import com.fashion.fashionmobile.ui.home.HomeFragment;
import com.fashion.fashionmobile.ui.userpanel.UserPanelFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.fashion.fashionmobile.databinding.ActivityMainBinding;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        //navController.

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

        UserLogin.AttemptAutoLogin(this);

        getSupportActionBar().setTitle("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_logout);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                UserLogin.Logout();
                finish();
                startActivity(getIntent());
                return true;
            }
        });*/

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}