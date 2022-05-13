package com.fashion.fashionmobile.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;

import com.fashion.fashionmobile.MainActivity;
import com.fashion.fashionmobile.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.fashion.fashionmobile.databinding.FragmentHomeBinding;
import com.fashion.fashionmobile.ui.products.ProductsFragment;
import com.fashion.fashionmobile.ui.userpanel.UserPanelFragment;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button shopNow = root.findViewById(R.id.shop_now);
        shopNow.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_home_to_products);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}