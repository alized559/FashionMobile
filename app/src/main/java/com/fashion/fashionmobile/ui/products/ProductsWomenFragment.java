package com.fashion.fashionmobile.ui.products;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.adapters.CategoryAdapter;
import com.fashion.fashionmobile.databinding.FragmentProductsWomenBinding;
import com.fashion.fashionmobile.helpers.CategoryDataModel;

import java.util.ArrayList;

public class ProductsWomenFragment extends Fragment {

    private FragmentProductsWomenBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductsWomenBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ArrayList<CategoryDataModel> categories = new ArrayList<>();
        categories.add(new CategoryDataModel(1, "Shoes", R.drawable.shoes_women, "#00738f"));
        categories.add(new CategoryDataModel(2, "Clothing", R.drawable.women_clothing, "#ea90cc"));
        categories.add(new CategoryDataModel(3, "Accessories", R.drawable.women_accessories, "#ee6e8b"));
        categories.add(new CategoryDataModel(4, "On Sale", R.drawable.women_sale, "#e5c2a5"));

        ListView categoriesList = root.findViewById(R.id.categories_list);
        CategoryAdapter categoryAdapter = new CategoryAdapter(root.getContext(), R.layout.category_list, categories);
        categoriesList.setAdapter(categoryAdapter);

        return root;
    }
}