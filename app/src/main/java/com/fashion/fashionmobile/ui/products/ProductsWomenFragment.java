package com.fashion.fashionmobile.ui.products;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.adapters.CategoryAdapter;
import com.fashion.fashionmobile.databinding.FragmentProductsWomenBinding;
import com.fashion.fashionmobile.helpers.CategoryDataModel;
import com.fashion.fashionmobile.ui.products_categories_child.ProductsCategoriesChildFragment;

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

        categoriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.setReorderingAllowed(true);
                ft.replace(R.id.nav_host_fragment_content_main, ProductsCategoriesChildFragment.class, null);
                ft.addToBackStack(ProductsCategoriesChildFragment.class.getName());
                ft.commit();
            }
        });

        return root;
    }
}