package com.fashion.fashionmobile.ui.products;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.adapters.ReviewFlexBoxAdapter;
import com.fashion.fashionmobile.databinding.FragmentProductsFilterBinding;
import com.fashion.fashionmobile.databinding.FragmentProductsFilterChildBinding;
import com.fashion.fashionmobile.helpers.ServerUrls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductsFilterChildFragment extends Fragment {

    private FragmentProductsFilterChildBinding binding;
    private String state = ProductsCategoriesFragment.state;
    private String category = ProductsFilterFragment.category;
    private ArrayList<String> childs;
    private RequestQueue queue;
    private ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductsFilterChildBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView backBtn = root.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(view -> {
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.setReorderingAllowed(true);
            ft.replace(R.id.nav_host_fragment_content_main, ProductsFilterFragment.class, null);
            ft.addToBackStack(ProductsFilterFragment.class.getName());
            ft.commit();
        });

        queue = Volley.newRequestQueue(root.getContext());

        childs = new ArrayList<>();

        if (category.equals("Brand")) {
            childs = ProductsFilterFragment.brands;
        } else if (category.equals("Name")) {
            childs = ProductsFilterFragment.names;
        } else {
            getFilterChilds();
        }

        list = root.findViewById(R.id.filter_child_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(root.getContext(), android.R.layout.simple_list_item_multiple_choice, childs);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = ((TextView) view).getText().toString();
                getSelectedChild(selectedItem);
            }
        });

        Button submit = root.findViewById(R.id.submit_btn);
        submit.setOnClickListener(view -> {
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.setReorderingAllowed(true);
            ft.replace(R.id.nav_host_fragment_content_main, ProductsListFragment.class, null);
            ft.addToBackStack(ProductsListFragment.class.getName());
            ft.commit();
        });

        return root;
    }

    public void getFilterChilds() {
        childs.clear();
        if (category.equals("Shoes")) {
            childs.add("basketball");
            childs.add("training");
            childs.add("football");
            childs.add("walking");
            childs.add("slides");
        } else if (category.equals("Clothing")) {
            childs.add("pants");
            childs.add("hoodies");
            childs.add("jackets");
            childs.add("socks");
            childs.add("polos");
            childs.add("shorts");
            childs.add("suits");
            childs.add("t-shirts");
        } else if (category.equals("Accessories") && state.equals("women")) {
            childs.add("bags");
            childs.add("hats");
            childs.add("gloves");
            childs.add("sunglasses");
            childs.add("necklaces");
        } else if (category.equals("Accessories") && state.equals("men")) {
            childs.add("bags");
            childs.add("hats");
            childs.add("gloves");
            childs.add("sunglasses");
        } else if (category.equals("Price")) {
            childs.add("Ascending");
            childs.add("Descending");
        }
    }

    public void getSelectedChild(String selectedItem) {
        if (category.equals("Shoes")) {
            if (ProductsListFragment.selectedShoes.contains(selectedItem)) {
                ProductsListFragment.selectedShoes.remove(selectedItem);
            } else {
                ProductsListFragment.selectedShoes.add(selectedItem);
            }
        } else if (category.equals("Clothing")) {
            if (ProductsListFragment.selectedClothing.contains(selectedItem)) {
                ProductsListFragment.selectedClothing.remove(selectedItem);
            } else {
                ProductsListFragment.selectedClothing.add(selectedItem);
            }
        } else if (category.equals("Accessories")) {
            if (ProductsListFragment.selectedAccessories.contains(selectedItem)) {
                ProductsListFragment.selectedAccessories.remove(selectedItem);
            } else {
                ProductsListFragment.selectedAccessories.add(selectedItem);
            }
        } else if (category.equals("Brand")) {
            if (ProductsListFragment.selectedBrand.contains(selectedItem)) {
                ProductsListFragment.selectedBrand.remove(selectedItem);
            } else {
                ProductsListFragment.selectedBrand.add(selectedItem);
            }
        } else if (category.equals("Name")) {
            if (ProductsListFragment.selectedName.contains(selectedItem)) {
                ProductsListFragment.selectedName.remove(selectedItem);
            } else {
                ProductsListFragment.selectedName.add(selectedItem);
            }
        } else if (category.equals("Price")) {
            if (ProductsListFragment.selectedPrice.contains(selectedItem)) {
                ProductsListFragment.selectedPrice.remove(selectedItem);
            } else {
                ProductsListFragment.selectedPrice.add(selectedItem);
            }
        }
    }
}