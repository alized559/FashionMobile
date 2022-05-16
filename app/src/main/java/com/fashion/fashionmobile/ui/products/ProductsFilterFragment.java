package com.fashion.fashionmobile.ui.products;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.databinding.FragmentProductsFilterBinding;
import com.fashion.fashionmobile.helpers.ServerUrls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductsFilterFragment extends Fragment {

    private FragmentProductsFilterBinding binding;
    public static String category;
    public static ArrayList<String> brands, names;
    private RequestQueue queue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductsFilterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView cancelBtn = root.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(view -> {
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.setReorderingAllowed(true);
            ft.replace(R.id.nav_host_fragment_content_main, ProductsListFragment.class, null);
            ft.addToBackStack(ProductsListFragment.class.getName());
            ft.commit();
        });

        queue = Volley.newRequestQueue(root.getContext());

        brands = new ArrayList<>();
        names = new ArrayList<>();

        String[] filterList = {
                "Shoes", "Clothing", "Accessories", "Brand", "Price", "Name"
        };

        ListView list = root.findViewById(R.id.filter_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(root.getContext(), android.R.layout.simple_list_item_1, filterList);
        list.setAdapter(adapter);

        getBrandsAndNames();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                category = filterList[i];

                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.setReorderingAllowed(true);
                ft.replace(R.id.nav_host_fragment_content_main, ProductsFilterChildFragment.class, null);
                ft.addToBackStack(ProductsFilterChildFragment.class.getName());
                ft.commit();
            }
        });

        return root;
    }

    public void getBrandsAndNames() {
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getProductsFilter, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject row = response.getJSONObject(i);
                        String name = row.getString("name");
                        String brand = row.getString("brand");
                        if (!brands.contains(brand)) {
                            brands.add(brand);
                        }
                        if (!names.contains(name)) {
                            names.add(name);
                        }
                    }
                } catch(Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}