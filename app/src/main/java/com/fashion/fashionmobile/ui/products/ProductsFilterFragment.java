package com.fashion.fashionmobile.ui.products;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.databinding.FragmentProductsFilterBinding;
import com.fashion.fashionmobile.helpers.BrandCache;
import com.fashion.fashionmobile.helpers.ProductFilter;
import com.fashion.fashionmobile.helpers.ServerUrls;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ProductsFilterFragment extends Fragment {

    private FragmentProductsFilterBinding binding;
    private RequestQueue queue;

    MultiStateToggleButton departmentToggle, categoryToggle;
    ChipGroup typeChipGroup, brandChipGroup;

    TextView typeTextView;

    HashMap<String, ArrayList<String>> typeAccordingToCat = new HashMap<String, ArrayList<String>>(){
        {
            put("shoes", new ArrayList<String>(){
                {
                    add("Basketball");
                    add("Training");
                    add("Football");
                    add("Walking");
                    add("Slides");
                }
            });
            put("clothing", new ArrayList<String>(){
                {
                    add("Pants");
                    add("Hoodies");
                    add("Jackets");
                    add("Socks");
                    add("Polos");
                    add("Shorts");
                    add("Suits");
                    add("T-Shirts");
                }
            });
            put("accessories", new ArrayList<String>(){
                {
                    add("Bags");
                    add("Hats");
                    add("Gloves");
                    add("Sunglasses");
                    add("Necklaces");
                }
            });
        }

    };

    View root;

    Chip lastSelectedSaleChip = null, lastSelectedPriceChip = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductsFilterBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        EditText nameFilter = root.findViewById(R.id.search_name_input);

        String oldNameFilter = ProductFilter.get("name");
        if(!oldNameFilter.isEmpty() && !oldNameFilter.equalsIgnoreCase("none")){
            nameFilter.setText(oldNameFilter);
        }

        ImageView cancelBtn = root.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(view -> {
            String filterByName = nameFilter.getText().toString();
            if(filterByName.isEmpty()){
                ProductFilter.UpdateFilter("name", "none");
            }else {
                ProductFilter.UpdateFilter("name", filterByName);
            }

            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.setReorderingAllowed(true);
            ft.replace(R.id.nav_host_fragment_content_main, ProductsFragment.class, null);
            ft.addToBackStack(ProductsFragment.class.getName());
            ft.commit();
        });

        departmentToggle = root.findViewById(R.id.department_toggle);
        categoryToggle = root.findViewById(R.id.category_toggle);
        typeChipGroup = root.findViewById(R.id.typeChipGroup);

        brandChipGroup = root.findViewById(R.id.brandChipGroup);

        typeTextView = root.findViewById(R.id.typeTextView);

        departmentToggle.setOnValueChangedListener(value -> {
            if(value == 0){
                ProductFilter.UpdateFilter("gender", "m");
            }else if(value == 1){
                ProductFilter.UpdateFilter("gender", "f");
            }else {
                ProductFilter.UpdateFilter("gender", "none");
            }
        });

        categoryToggle.setOnValueChangedListener(value -> {
            if(value == 0){
                ProductFilter.UpdateFilter("category", "shoes");
                ResetTypeChips("shoes");
            }else if(value == 1){
                ProductFilter.UpdateFilter("category", "clothing");
                ResetTypeChips("clothing");
            }else if(value == 2){
                ProductFilter.UpdateFilter("category", "accessories");
                ResetTypeChips("accessories");
            }else {
                ProductFilter.UpdateFilter("category", "none");
                ResetTypeChips("none");
            }
        });


        String currentGender = ProductFilter.get("gender");
        departmentToggle.setValue(currentGender == "m" ? 0 : currentGender == "f" ? 1 : 2);

        String currentCategory = ProductFilter.get("category");
        categoryToggle.setValue(currentCategory == "shoes" ? 0 : currentCategory == "clothing" ? 1 : currentCategory == "accessories" ? 2 : 3);

        queue = Volley.newRequestQueue(root.getContext());

        getBrandsAndNames();

        ResetTypeChips(currentCategory);

        Chip saleAscendingChip = root.findViewById(R.id.sale_ascending_chip);
        Chip saleDescendingChip = root.findViewById(R.id.sale_descending_chip);

        saleAscendingChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                ProductFilter.UpdateFilter("sale", "ASC");
                lastSelectedSaleChip = saleAscendingChip;
            }else {
                if(lastSelectedSaleChip == saleAscendingChip){
                    ProductFilter.UpdateFilter("sale", "none");
                }
            }
        });

        saleDescendingChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                ProductFilter.UpdateFilter("sale", "DESC");
                lastSelectedSaleChip = saleDescendingChip;
            }else {
                if(lastSelectedSaleChip == saleDescendingChip){
                    ProductFilter.UpdateFilter("sale", "none");
                }
            }
        });

        saleAscendingChip.setChecked(ProductFilter.get("sale").equalsIgnoreCase("ASC"));
        saleDescendingChip.setChecked(ProductFilter.get("sale").equalsIgnoreCase("DESC"));

        Chip priceAscendingChip = root.findViewById(R.id.price_ascending_chip);
        Chip priceDescendingChip = root.findViewById(R.id.price_descending_chip);

        priceAscendingChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                ProductFilter.UpdateFilter("price", "ASC");
                lastSelectedPriceChip = priceAscendingChip;
            }else {
                if(lastSelectedPriceChip == priceAscendingChip){
                    ProductFilter.UpdateFilter("price", "none");
                }
            }
        });

        priceDescendingChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                ProductFilter.UpdateFilter("price", "DESC");
                lastSelectedPriceChip = priceDescendingChip;
            }else {
                if(lastSelectedPriceChip == priceDescendingChip){
                    ProductFilter.UpdateFilter("price", "none");
                }
            }
        });

        priceAscendingChip.setChecked(ProductFilter.get("price").equalsIgnoreCase("ASC"));
        priceDescendingChip.setChecked(ProductFilter.get("price").equalsIgnoreCase("DESC"));

        return root;
    }

    private Chip lastSelectedTypeChip = null;
    public void ResetTypeChips(String category){
        if(lastSelectedTypeChip != null){
            ProductFilter.UpdateFilter("type", "none");
            lastSelectedTypeChip = null;
        }

        typeChipGroup.removeAllViews();
        if(!category.equalsIgnoreCase("none")){
            typeTextView.setVisibility(View.VISIBLE);
            typeChipGroup.setVisibility(View.VISIBLE);
            for(String type : typeAccordingToCat.get(category)){
                Chip chip = new Chip(root.getContext());
                chip.setLayoutParams(new ChipGroup.LayoutParams(ChipGroup.LayoutParams.WRAP_CONTENT, ChipGroup.LayoutParams.WRAP_CONTENT));
                chip.setText(type);
                chip.setTextSize(15);
                ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(root.getContext(),
                        null,
                        0,
                        R.style.Widget_MaterialComponents_Chip_Choice);
                chip.setChipDrawable(chipDrawable);

                chip.setChecked(ProductFilter.get("type").equalsIgnoreCase(type));

                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if(isChecked){
                        lastSelectedTypeChip = chip;
                        ProductFilter.UpdateFilter("type", type);
                    }else {
                        if(lastSelectedTypeChip == chip || lastSelectedTypeChip == null){
                            ProductFilter.UpdateFilter("type", "none");
                        }
                    }
                });

                typeChipGroup.addView(chip);
            }
        }else {
            typeTextView.setVisibility(View.GONE);
            typeChipGroup.setVisibility(View.GONE);
            ProductFilter.UpdateFilter("type", "none");
        }
    }

    public void getBrandsAndNames() {
        brandChipGroup.removeAllViews();
        if(BrandCache.brands.isEmpty()) {
            JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getAllBrands, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject row = response.getJSONObject(i);
                            String name = row.getString("name");
                            BrandCache.AddBrand(name);
                            addSingleBrand(name);
                        }
                    } catch (Exception e) {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    getBrandsAndNames();
                }
            });
            queue.add(request);
        }else {
            for (int i = 0; i < BrandCache.brands.size(); i++) {
                String name = BrandCache.brands.get(i);
                addSingleBrand(name);
            }
        }
    }

    private Chip lastSelectedBrandChip = null;
    public void addSingleBrand(String name){
        Chip chip = new Chip(root.getContext());
        chip.setLayoutParams(new ChipGroup.LayoutParams(ChipGroup.LayoutParams.WRAP_CONTENT, ChipGroup.LayoutParams.WRAP_CONTENT));
        chip.setText(name);
        chip.setTextSize(15);
        ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(root.getContext(),
                null,
                0,
                R.style.Widget_MaterialComponents_Chip_Choice);
        chip.setChipDrawable(chipDrawable);

        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ProductFilter.UpdateFilter("brand", name);
                lastSelectedBrandChip = chip;
            } else {
                if (lastSelectedBrandChip == chip) {
                    ProductFilter.UpdateFilter("brand", "none");
                }
            }
        });

        chip.setChecked(ProductFilter.get("brand").equalsIgnoreCase(name));

        brandChipGroup.addView(chip);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}