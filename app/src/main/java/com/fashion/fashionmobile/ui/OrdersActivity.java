package com.fashion.fashionmobile.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.adapters.CartFlexBoxAdapter;
import com.fashion.fashionmobile.adapters.CartItemDataModel;
import com.fashion.fashionmobile.adapters.OrderDataModel;
import com.fashion.fashionmobile.adapters.OrderFlexBoxAdapter;
import com.fashion.fashionmobile.helpers.ProductFilter;
import com.fashion.fashionmobile.helpers.ServerUrls;
import com.fashion.fashionmobile.helpers.UserLogin;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {

    RecyclerView orderItemsRecycler;
    ArrayList<OrderDataModel> orderItems = new ArrayList<>();
    OrderFlexBoxAdapter ordersAdapter;

    ChipGroup filterOrdersChipGroup;

    public static String[] allOrderFilters = {"Pending", "On The Way", "Delivered", "Returned"};

    RequestQueue queue;

    String filter = "invalid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        if(UserLogin.isLoggedIn) {

            queue = Volley.newRequestQueue(this);

            orderItemsRecycler = findViewById(R.id.orders_recycle);

            filterOrdersChipGroup = findViewById(R.id.filterOrdersChipGroup);

            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setJustifyContent(JustifyContent.CENTER);
            layoutManager.setAlignItems(AlignItems.CENTER);
            layoutManager.setFlexWrap(FlexWrap.WRAP);

            orderItemsRecycler.setLayoutManager(layoutManager);

            ordersAdapter = new OrderFlexBoxAdapter(this, orderItems);
            orderItemsRecycler.setAdapter(ordersAdapter);

            requestOrders();

            ResetFilterChips();

        }

    }


    public void requestOrders(){
        orderItems.clear();
        ordersAdapter.notifyDataSetChanged();
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getAllOrders + "?filter=" + filter, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject row = response.getJSONObject(i);
                        addSingleOrder(row);
                    }

                } catch(Exception e) {
                    Log.d("UIError", e.getMessage() + "");
                    //.makeText(root.getContext(), "UI Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("OrdersError", error.getMessage() + "");
                requestOrders();
            }
        });
        queue.add(request);
    }

    public void addSingleOrder(JSONObject row){
        try {

            int order_id = row.getInt("order_id");
            String fullname = row.getString("fullname");
            String address = row.getString("address");
            String country = row.getString("country");
            String countryCode = row.getString("countryCode");
            String number = row.getString("number");
            String cartItems = row.getString("cart_items").replaceAll("<qm>", "\"")
                    .replaceAll("<ilb>", "<br>")
                    .replaceAll("viewProduct.php", "http://195.62.33.125/viewProduct.php");
            String payment = row.getString("payment");
            String state = row.getString("state");
            String date = row.getString("date");

            OrderDataModel model = new OrderDataModel();
            model.OrderID = order_id;
            model.Fullname = fullname;
            model.Address = address;
            model.Country = country;
            model.PhoneNumber = "+(" + countryCode + ") " + number;
            model.Products = cartItems;
            model.Payment = payment;
            model.State = state;
            model.Date = date;

            orderItems.add(model);
            ordersAdapter.notifyDataSetChanged();

        }catch (Exception ex){
            Log.d("OrdersError", ex.getMessage() + "");
        }

    }

    private Chip lastSelectedTypeChip = null;
    public void ResetFilterChips(){
        if(lastSelectedTypeChip != null){
            filter = "invalid";
            lastSelectedTypeChip = null;
            requestOrders();
        }

        filterOrdersChipGroup.removeAllViews();
        for(String type : allOrderFilters){
            Chip chip = new Chip(this);
            chip.setLayoutParams(new ChipGroup.LayoutParams(ChipGroup.LayoutParams.WRAP_CONTENT, ChipGroup.LayoutParams.WRAP_CONTENT));
            chip.setText(type);
            chip.setTextSize(15);
            ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(this,
                    null,
                    0,
                    R.style.Widget_MaterialComponents_Chip_Choice);
            chip.setEnsureMinTouchTargetSize(false);
            chip.setChipDrawable(chipDrawable);

            chip.setChecked(false);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    lastSelectedTypeChip = chip;
                    filter = type;
                    requestOrders();
                }else {
                    if(lastSelectedTypeChip == chip || lastSelectedTypeChip == null){
                        filter = "invalid";
                        requestOrders();
                    }
                }
            });

            filterOrdersChipGroup.addView(chip);
        }
    }

}