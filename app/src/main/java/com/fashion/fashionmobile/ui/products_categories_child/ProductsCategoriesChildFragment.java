package com.fashion.fashionmobile.ui.products_categories_child;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.databinding.FragmentProductsBinding;
import com.fashion.fashionmobile.databinding.FragmentProductsCategoriesChildBinding;

public class ProductsCategoriesChildFragment extends Fragment {

    private FragmentProductsCategoriesChildBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductsCategoriesChildBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        return root;
    }
}