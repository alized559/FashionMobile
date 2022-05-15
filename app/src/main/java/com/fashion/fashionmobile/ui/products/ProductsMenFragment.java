package com.fashion.fashionmobile.ui.products;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.adapters.CategoryAdapter;
import com.fashion.fashionmobile.databinding.FragmentProductsMenBinding;
import com.fashion.fashionmobile.helpers.CategoryDataModel;
import com.fashion.fashionmobile.ui.products_categories_child.ProductsCategoriesChildFragment;
import com.fashion.fashionmobile.ui.userpanel.UserPanelFragment;
import com.fashion.fashionmobile.ui.userpanel.UserPanelLoginFragment;
import com.fashion.fashionmobile.ui.userpanel.UserPanelManageFragment;

import java.util.ArrayList;

public class ProductsMenFragment extends Fragment {

    private FragmentProductsMenBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductsMenBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ArrayList<CategoryDataModel> categories = new ArrayList<>();
        categories.add(new CategoryDataModel(1, "Shoes", R.drawable.men_shoes, "#74604d"));
        categories.add(new CategoryDataModel(2, "Clothing", R.drawable.men_clothing, "#901b1b"));
        categories.add(new CategoryDataModel(3, "Accessories", R.drawable.men_accessories, "#ce9a60"));
        categories.add(new CategoryDataModel(4, "On Sale", R.drawable.men_sale, "#7b9b98"));

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