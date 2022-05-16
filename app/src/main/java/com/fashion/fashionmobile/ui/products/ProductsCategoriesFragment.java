package com.fashion.fashionmobile.ui.products;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.adapters.CategoryAdapter;
import com.fashion.fashionmobile.databinding.FragmentProductsCategoriesBinding;
import com.fashion.fashionmobile.helpers.CategoryDataModel;
import com.google.android.flexbox.FlexboxLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProductsCategoriesFragment extends Fragment {

    private FragmentProductsCategoriesBinding binding;
    public static String state;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductsCategoriesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ArrayList<CategoryDataModel> categories = new ArrayList<>();
        categories.add(new CategoryDataModel(1, "Women", R.drawable.women, "#ee6e8b"));
        categories.add(new CategoryDataModel(2, "Men", R.drawable.men_clothing, "#901b1b"));
        categories.add(new CategoryDataModel(3, "On Sale", R.drawable.men_sale, "#7b9b98"));

        ListView list = root.findViewById(R.id.categories_list);
        CategoryAdapter adapter = new CategoryAdapter(root.getContext(), R.layout.category_list, categories);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    state = "women";
                } else if (i == 1) {
                    state = "men";
                } else if (i == 2) {
                    state = "sale";
                }

                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.setReorderingAllowed(true);
                ft.replace(R.id.nav_host_fragment_content_main, ProductsListFragment.class, null);
                ft.addToBackStack(ProductsListFragment.class.getName());
                ft.commit();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}