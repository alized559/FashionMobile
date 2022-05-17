package com.fashion.fashionmobile.ui.products;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.ViewProductActivity;
import com.fashion.fashionmobile.databinding.FragmentProductsBinding;
import com.fashion.fashionmobile.databinding.FragmentProductsFilterBinding;
import com.fashion.fashionmobile.helpers.ImageCache;
import com.fashion.fashionmobile.helpers.ProductFilter;
import com.fashion.fashionmobile.helpers.ServerUrls;
import com.fashion.fashionmobile.helpers.UserLogin;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ProductsFragment extends Fragment {

    private FragmentProductsBinding binding;

    private RequestQueue queue;

    RelativeLayout loadingLayout;

    FlexboxLayout productsFlexBox;

    View root;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductsBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        ProductFilter.CreateFilter();

        queue = Volley.newRequestQueue(root.getContext());

        Button filterBtn = root.findViewById(R.id.filter_btn);
        filterBtn.setOnClickListener(view -> {
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.setReorderingAllowed(true);
            ft.replace(R.id.nav_host_fragment_content_main, ProductsFilterFragment.class, null);
            ft.addToBackStack(ProductsFilterFragment.class.getName());
            ft.commit();
        });

        loadingLayout = root.findViewById(R.id.loading);

        productsFlexBox = root.findViewById(R.id.products_flex);


        getAllProducts();


        return root;
    }



    @RequiresApi(api = Build.VERSION_CODES.P)
    public void getAllProducts() {
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getAllProducts(ProductFilter.productFilterHasMap), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject row = response.getJSONObject(i);
                        requestSingleProduct(row);
                    }
                } catch(Exception e) {
                    Log.d("RequestsProductError", e.getMessage() + "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getAllProducts();
                Log.d("RequestProductsError", error.getMessage() + "");
            }
        });
        queue.add(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void requestSingleProduct(JSONObject row){
        try {
            int product_id = row.getInt("prod_id");
            if (ImageCache.GetProductImage(product_id) == null) {
                ImageRequest request = new ImageRequest(ServerUrls.getProductImage(product_id), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        ImageCache.SetProductImage(product_id, response);
                        addProduct(row);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                requestSingleProduct(row);
                                Log.d("RequestProductError", error.getMessage() + "");
                            }
                        });
                queue.add(request);
            } else {
                addProduct(row);
            }
        }catch (Exception ex){
            Log.d("RequestProductError", ex.getMessage() + "");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void addProduct(JSONObject row){
        try {

            loadingLayout.setVisibility(View.GONE);

            int prod_id = row.getInt("prod_id");
            String name = row.getString("name");
            double price = row.getDouble("price");
            double discount = row.getDouble("discount");
            String brand = row.getString("brand");

            Bitmap response = ImageCache.GetProductImage(prod_id);

            LinearLayout linear = new LinearLayout(root.getContext());
            LinearLayout.LayoutParams linearlp = new LinearLayout.LayoutParams(ViewGroup.
                    LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearlp.setMargins(0, 0, 0, 60);
            linear.setOrientation(LinearLayout.VERTICAL);
            linear.setLayoutParams(linearlp);

            CardView card = new CardView(root.getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.
                    LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            card.setLayoutParams(lp);
            card.setRadius((int) (10 * root.getContext().getResources().getDisplayMetrics().density + 0.5f));

            card.setOnClickListener(view -> {
                Intent i = new Intent(root.getContext(), ViewProductActivity.class);
                i.putExtra("product_id", prod_id);
                startActivity(i);
            });

            ImageView img = new ImageView(root.getContext());
            img.setImageBitmap(response);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                    (int) (155 * root.getContext().getResources().getDisplayMetrics().density + 0.5f),
                    (int) (190 * root.getContext().getResources().getDisplayMetrics().density + 0.5f));
            img.setLayoutParams(lp1);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageCache.SetProductImage(prod_id, response);

            card.addView(img);

            LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(
                    (int) (155 * root.getContext().getResources().getDisplayMetrics().density + 0.5f),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            textLayout.setMargins(20, 10, 0, 0);
            TextView prod_name = new TextView(root.getContext());
            prod_name.setLayoutParams(textLayout);
            prod_name.setText(name);
            prod_name.setTextSize(16);
            prod_name.setTextColor(Color.BLACK);
            Typeface t = getResources().getFont(R.font.inter_medium);
            prod_name.setTypeface(t);
            prod_name.setLineHeight(50);

            LinearLayout.LayoutParams textLayout1 = new LinearLayout.LayoutParams(
                    (int) (155 * root.getContext().getResources().getDisplayMetrics().density + 0.5f),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            textLayout1.setMargins(20, 0, 0, 0);
            TextView brand_name = new TextView(root.getContext());
            brand_name.setLayoutParams(textLayout1);
            brand_name.setText(brand);
            brand_name.setTextSize(16);
            brand_name.setTextColor(Color.parseColor("#808080"));
            Typeface t1 = getResources().getFont(R.font.inter_medium);
            brand_name.setTypeface(t1);

            LinearLayout linear1 = new LinearLayout(root.getContext());
            LinearLayout.LayoutParams linearlp1 = new LinearLayout.LayoutParams(
                    (int) (155 * root.getContext().getResources().getDisplayMetrics().density + 0.5f),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            if(UserLogin.CurrentCurrency != "US, USD"){
                linear1.setOrientation(LinearLayout.VERTICAL);
            }else {
                linear1.setOrientation(LinearLayout.HORIZONTAL);
            }

            linear1.setLayoutParams(linearlp1);

            if (discount != 0) {
                LinearLayout.LayoutParams textlp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                textlp.setMargins(20, 0, 0, 0);

                TextView prod_price = new TextView(root.getContext());
                prod_price.setLayoutParams(textlp);
                String p = String.valueOf(price);
                if(UserLogin.CurrentCurrency != "US, USD"){
                    prod_price.setText("LBP " + String.format("%,d", (int) Double.parseDouble(p)));
                }else {
                    prod_price.setText("$" + p);
                }
                prod_price.setTextColor(Color.BLACK);
                prod_price.setTextSize(14);
                Typeface typeface = getResources().getFont(R.font.inter_medium);
                prod_price.setTypeface(typeface);
                prod_price.setPaintFlags(prod_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                TextView discount_price = new TextView(root.getContext());
                discount_price.setLayoutParams(textlp);
                double newPrice = price - discount;
                if(UserLogin.CurrentCurrency != "US, USD"){
                    discount_price.setText("LBP " + String.format("%,d", (int) newPrice));
                }else {
                    DecimalFormat df = new DecimalFormat("0.00");
                    discount_price.setText("$" + df.format(newPrice));
                }
                discount_price.setTextColor(Color.parseColor("#ffd814"));
                discount_price.setTextSize(14);
                Typeface t2 = getResources().getFont(R.font.inter_medium);
                discount_price.setTypeface(t2);

                linear1.addView(prod_price);
                linear1.addView(discount_price);
            } else {
                TextView prod_price = new TextView(root.getContext());
                prod_price.setLayoutParams(textLayout1);
                String p = String.valueOf(price);
                if(UserLogin.CurrentCurrency != "US, USD"){
                    prod_price.setText("LBP " + String.format("%,d", (int) Double.parseDouble(p)));
                }else {
                    prod_price.setText("$" + p);
                }
                prod_price.setTextColor(Color.BLACK);
                prod_price.setTextSize(14);
                Typeface t2 = getResources().getFont(R.font.inter_medium);
                prod_price.setTypeface(t2);
                linear1.addView(prod_price);
            }

            linear.addView(card);
            linear.addView(prod_name);
            linear.addView(brand_name);
            linear.addView(linear1);
            productsFlexBox.addView(linear);

        }catch (Exception ex){
            Log.d("RequestProductError", ex.getMessage() + "");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}