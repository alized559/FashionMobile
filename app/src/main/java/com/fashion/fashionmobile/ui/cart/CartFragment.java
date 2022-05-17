package com.fashion.fashionmobile.ui.cart;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.MainActivity;
import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.ViewProductActivity;
import com.fashion.fashionmobile.adapters.CartFlexBoxAdapter;
import com.fashion.fashionmobile.adapters.CartItemDataModel;
import com.fashion.fashionmobile.adapters.ReviewDataModel;
import com.fashion.fashionmobile.adapters.ReviewFlexBoxAdapter;
import com.fashion.fashionmobile.databinding.FragmentCartBinding;
import com.fashion.fashionmobile.helpers.ImageCache;
import com.fashion.fashionmobile.helpers.ServerUrls;
import com.fashion.fashionmobile.helpers.UserLogin;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;

    RecyclerView cartItemsRecycler;
    ArrayList<CartItemDataModel> cartItems = new ArrayList<>();
    CartFlexBoxAdapter cartItemsAdapter;

    RequestQueue queue;

    double TotalCartPrice = 0;
    int TotalCartItems = 0;

    TextView totalPriceText, totalItemsText;

    TextView removeAllText;

    MaterialButton gotoCheckoutButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        removeAllText = root.findViewById(R.id.cart_remove_all_text);
        removeAllText.setVisibility(View.GONE);

        gotoCheckoutButton = root.findViewById(R.id.cart_goto_checkout);
        gotoCheckoutButton.setVisibility(View.GONE);

        if(UserLogin.isLoggedIn){

            queue = Volley.newRequestQueue(root.getContext());

            cartItemsRecycler = root.findViewById(R.id.cart_items_recycle);

            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(root.getContext());
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setJustifyContent(JustifyContent.CENTER);
            layoutManager.setAlignItems(AlignItems.CENTER);
            layoutManager.setFlexWrap(FlexWrap.WRAP);

            cartItemsRecycler.setLayoutManager(layoutManager);

            cartItemsAdapter = new CartFlexBoxAdapter(root.getContext(), cartItems);
            cartItemsRecycler.setAdapter(cartItemsAdapter);

            totalItemsText = root.findViewById(R.id.total_items_text);
            totalPriceText = root.findViewById(R.id.total_price_text);

            removeAllText.setOnClickListener(v -> {
                JsonObjectRequest request = new JsonObjectRequest(ServerUrls.removeAllCartItems(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("CartRemoveAllResult", response.toString());
                        try {
                            if(response.getString("state").equalsIgnoreCase("SUCCESS")){
                                Snackbar.make(v, "All Items Removed Successfully!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                                cartItems.clear();
                                cartItemsAdapter.notifyDataSetChanged();
                                removeAllText.setVisibility(View.GONE);
                                gotoCheckoutButton.setVisibility(View.GONE);

                                TotalCartItems = 0;
                                totalItemsText.setText("0 Items");

                                TotalCartPrice = 0;
                                if(UserLogin.CurrentCurrency != "US, USD"){
                                    totalPriceText.setText("LBP 0");
                                }else {
                                    totalPriceText.setText("$0.00");
                                }


                            }else {
                                Snackbar.make(v, "Failed To Remove All Cart Items!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        } catch (Exception e) {
                            Log.d("UIError", e.getMessage() + "");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("CartRemoveAllError", error.getMessage() + "");
                        Snackbar.make(v, "Failed To Remove All Cart Items!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(
                        (int) TimeUnit.SECONDS.toMillis(1000), //After the set time elapses the request will timeout
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(request);
            });

            gotoCheckoutButton.setOnClickListener(v -> {
                //Go To Checkout here
            });

            requestCartItems();

        }
        return root;
    }

    public void requestCartItems(){
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getCartItems(), new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.d("CartItems", response.toString());
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject row = response.getJSONObject(i);
                        requestSingleItemData(row);
                    }

                } catch(Exception e) {
                    Log.d("UIError", e.getMessage() + "");
                    //.makeText(root.getContext(), "UI Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("CartItemError", error.getMessage() + "");
                requestCartItems();
            }
        });
        queue.add(request);
    }

    public void requestSingleItemData(JSONObject row){

        try {
            int prod_item_id = row.getInt("prod_item_id");
            int product_id = row.getInt("prod_id");
            if (ImageCache.GetProductItemImage(prod_item_id) == null) {
                ImageRequest request = new ImageRequest(ServerUrls.getProductItemImage(product_id, prod_item_id), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        ImageCache.SetProductItemImage(prod_item_id, response);
                        addProductItem(row);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                requestSingleItemData(row);
                                Log.d("ProductItemImageError", error.getMessage() + "");
                            }
                        });
                queue.add(request);
            } else {
                addProductItem(row);
            }
        }catch(Exception e) {
            Log.d("UIError", e.getMessage() + "");
        }
    }

    public void addProductItem(JSONObject row){

        try {
            removeAllText.setVisibility(View.VISIBLE);
            gotoCheckoutButton.setVisibility(View.VISIBLE);

            int cart_item_id = row.getInt("item_id");
            int cart_item_quantity = row.getInt("quantity");
            int product_id = row.getInt("prod_id");
            int product_item_id = row.getInt("prod_item_id");
            String product_name = row.getString("product_name");
            double product_price = row.getDouble("price");
            String product_item_name = row.getString("product_item_name");
            int product_max_quantity = row.getInt("total_quantity");
            String extra = row.getString("data");

            Bitmap response = ImageCache.GetProductItemImage(product_item_id);

            CartItemDataModel model = new CartItemDataModel();
            model.ItemID = cart_item_id;
            model.Amount = cart_item_quantity;
            model.MaxAmount = product_max_quantity;
            model.Title = product_name;
            model.Subtitle = product_item_name;
            model.Extra = extra;

            double totalPrice = product_price * cart_item_quantity;
            model.CurrentPrice = totalPrice;

            DecimalFormat df = new DecimalFormat("0.00");

            if(UserLogin.CurrentCurrency != "US, USD"){
                model.Price = "LBP " + String.format("%,d", (int) totalPrice);
            }else {
                model.Price = "$" + df.format(totalPrice);
            }

            model.Picture = response;

            model.onPlusClicked = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(model.Amount < model.MaxAmount){
                        model.Amount++;
                        JsonObjectRequest request = new JsonObjectRequest(ServerUrls.editCartItem(cart_item_id, model.Amount), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("CartEditResult", response.toString());
                                try {
                                    if(response.getString("state").equalsIgnoreCase("SUCCESS")){
                                        Snackbar.make(v, "Item Added Successfully!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                        TotalCartPrice -= model.CurrentPrice;

                                        model.CurrentPrice = model.Amount * product_price;
                                        TotalCartPrice += model.CurrentPrice;
                                        if(UserLogin.CurrentCurrency != "US, USD"){
                                            totalPriceText.setText("LBP " + String.format("%,d", (int) TotalCartPrice));
                                        }else {
                                            totalPriceText.setText("$" + df.format(TotalCartPrice));
                                        }

                                        if(model.AmountTextView != null){
                                            model.AmountTextView.setText(model.Amount + "");
                                        }
                                        if(model.PriceTextView != null) {
                                            if (UserLogin.CurrentCurrency != "US, USD") {
                                                model.Price = "LBP " + String.format("%,d", (int) model.CurrentPrice);
                                            } else {
                                                model.Price = "$" + df.format(model.CurrentPrice);
                                            }
                                            model.PriceTextView.setText(model.Price);
                                        }

                                    }else {
                                        Snackbar.make(v, "Failed To Add Cart Item!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                } catch (Exception e) {
                                    Log.d("UIError", e.getMessage() + "");
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                model.Amount--;
                                Log.d("CartEditError", error.getMessage() + "");
                                Snackbar.make(v, "Failed To Add Cart Item!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                        request.setRetryPolicy(new DefaultRetryPolicy(
                                (int) TimeUnit.SECONDS.toMillis(1000), //After the set time elapses the request will timeout
                                0,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(request);
                    }
                }
            };

            model.onMinusClicked = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(model.Amount > 1){
                        model.Amount--;
                        JsonObjectRequest request = new JsonObjectRequest(ServerUrls.editCartItem(cart_item_id, model.Amount), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("CartEditResult", response.toString());
                                try {
                                    if(response.getString("state").equalsIgnoreCase("SUCCESS")){
                                        Snackbar.make(v, "Item Removed Successfully!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                        TotalCartPrice -= model.CurrentPrice;

                                        model.CurrentPrice = model.Amount * product_price;
                                        TotalCartPrice += model.CurrentPrice;
                                        if(UserLogin.CurrentCurrency != "US, USD"){
                                            totalPriceText.setText("LBP " + String.format("%,d", (int) TotalCartPrice));
                                        }else {
                                            totalPriceText.setText("$" + df.format(TotalCartPrice));
                                        }

                                        if(model.AmountTextView != null){
                                            model.AmountTextView.setText(model.Amount + "");
                                        }
                                        if(model.PriceTextView != null) {
                                            if (UserLogin.CurrentCurrency != "US, USD") {
                                                model.Price = "LBP " + String.format("%,d", (int) model.CurrentPrice);
                                            } else {
                                                model.Price = "$" + df.format(model.CurrentPrice);
                                            }
                                            model.PriceTextView.setText(model.Price);
                                        }

                                    }else {
                                        Snackbar.make(v, "Failed To Remove Cart Item!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                } catch (Exception e) {
                                    Log.d("UIError", e.getMessage() + "");
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                model.Amount++;
                                Log.d("CartEditError", error.getMessage() + "");
                                Snackbar.make(v, "Failed To Remove Cart Item!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                        request.setRetryPolicy(new DefaultRetryPolicy(
                                (int) TimeUnit.SECONDS.toMillis(1000), //After the set time elapses the request will timeout
                                0,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(request);
                    }
                }
            };

            model.onRemoveClicked = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JsonObjectRequest request = new JsonObjectRequest(ServerUrls.removeCartItem(cart_item_id), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("CartRemoveResult", response.toString());
                            try {
                                if(response.getString("state").equalsIgnoreCase("SUCCESS")){
                                    Snackbar.make(v, "Item Removed Successfully!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                    TotalCartItems--;
                                    totalItemsText.setText(TotalCartItems + " Items");

                                    if(TotalCartItems == 0){
                                        gotoCheckoutButton.setVisibility(View.GONE);
                                        removeAllText.setVisibility(View.GONE);
                                    }

                                    TotalCartPrice -= model.CurrentPrice;
                                    if(UserLogin.CurrentCurrency != "US, USD"){
                                        totalPriceText.setText("LBP " + String.format("%,d", (int) TotalCartPrice));
                                    }else {
                                        totalPriceText.setText("$" + df.format(TotalCartPrice));
                                    }

                                    cartItems.remove(model);
                                    cartItemsAdapter.notifyDataSetChanged();

                                }else {
                                    Snackbar.make(v, "Failed To Remove Cart Item!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            } catch (Exception e) {
                                Log.d("UIError", e.getMessage() + "");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("CartRemoveError", error.getMessage() + "");
                            Snackbar.make(v, "Failed To Remove Cart Item!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                    request.setRetryPolicy(new DefaultRetryPolicy(
                            (int) TimeUnit.SECONDS.toMillis(1000), //After the set time elapses the request will timeout
                            0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(request);
                }
            };

            cartItems.add(model);
            cartItemsAdapter.notifyDataSetChanged();

            TotalCartItems++;
            totalItemsText.setText(TotalCartItems + " Items");

            TotalCartPrice += totalPrice;
            if(UserLogin.CurrentCurrency != "US, USD"){
                totalPriceText.setText("LBP " + String.format("%,d", (int) TotalCartPrice));
            }else {
                totalPriceText.setText("$" + df.format(TotalCartPrice));
            }

        }catch (Exception ex){
            Log.d("UIError", ex.getMessage() + "");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}