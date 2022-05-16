package com.fashion.fashionmobile;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.Rating;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.adapters.ReviewDataModel;
import com.fashion.fashionmobile.adapters.ReviewFlexBoxAdapter;
import com.fashion.fashionmobile.adapters.SpinnerAdapter;
import com.fashion.fashionmobile.helpers.ImageCache;
import com.fashion.fashionmobile.helpers.ServerUrls;
import com.fashion.fashionmobile.helpers.UserLikes;
import com.fashion.fashionmobile.helpers.UserLogin;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ViewProductActivity extends AppCompatActivity {

    private RequestQueue queue;

    TextView productTitle;

    int product_id = 0;

    ImageView productImage, productMainImage;

    FlexboxLayout productItemsFlex;

    ImageButton likedImage;

    TextView productItemName, productDiscountText, productPriceText;

    TextView quantityTextView, ratingsTextView, likesTextView;

    MaterialCardView currentSelectedItemCard = null;
    int currentSelectedItemId = 0;
    int currentSelectedItemQuantity = 0;
    String currentSelectedItemExtraName = "";
    String currentSelectedItemExtraOptions = "";

    TableLayout productDetailsTable;

    TextView productDescription;

    CardView reviewAddCardView;

    RecyclerView reviewsRecyclerView;
    ArrayList<ReviewDataModel> reviewsList = new ArrayList<>();
    ReviewFlexBoxAdapter reviewsAdapter;

    EditText reviewInputText;
    RatingBar reviewRatingBar;

    int TotalRaters = 0;
    double totalRatings = 0;

    ArrayList<String> ExtraOptions = new ArrayList<>();
    SpinnerAdapter extraSpinnerAdapter;
    Spinner extraSpinner;
    TextView extraTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        product_id = getIntent().getIntExtra("product_id", 0);

        productImage = findViewById(R.id.product_main_picture);
        productMainImage = findViewById(R.id.product_item_main_picture);
        likedImage = findViewById(R.id.product_like_picture);

        productTitle = findViewById(R.id.product_name_text);

        productItemsFlex = findViewById(R.id.product_items_flex);

        queue = Volley.newRequestQueue(this);

        productItemName = findViewById(R.id.product_item_name_text);
        productDiscountText = findViewById(R.id.product_discount_text);
        productPriceText = findViewById(R.id.product_price_text);

        productDetailsTable = findViewById(R.id.product_details_table);

        productDescription = findViewById(R.id.product_description_text);

        if(UserLikes.DoesLike(product_id)){
            likedImage.setColorFilter(getResources().getColor(R.color.product_like));
        }else {
            likedImage.setColorFilter(getResources().getColor(R.color.product_nolike));
        }


        Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce2);
        likedImage.setOnClickListener(view -> {
            likedImage.startAnimation(bounceAnim);
            likedImage.setEnabled(false);
            StringRequest request;
            if(UserLikes.DoesLike(product_id)){
                request = new StringRequest(1, ServerUrls.updateLikes, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.contains("Error")) {
                            UserLikes.removeLike(product_id);
                            likedImage.setColorFilter(getResources().getColor(R.color.product_nolike));
                            likesTextView.setText((Integer.parseInt(likesTextView.getText().toString()) - 1) + "");
                        }
                        likedImage.setEnabled(true);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        likedImage.setEnabled(true);
                    }
                }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("userID", UserLogin.CurrentLoginID + "");
                        params.put("prod_id", product_id + "");
                        params.put("type", "remove");
                        return params;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(
                        (int) TimeUnit.SECONDS.toMillis(1000), //After the set time elapses the request will timeout
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(request);
            }else {
                request = new StringRequest(1, ServerUrls.updateLikes, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.contains("Error")){
                            UserLikes.addLike(product_id);
                            likedImage.setColorFilter(getResources().getColor(R.color.product_like));
                            likesTextView.setText((Integer.parseInt(likesTextView.getText().toString()) + 1) + "");
                        }
                        likedImage.setEnabled(true);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        likedImage.setEnabled(true);
                    }
                }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("userID", UserLogin.CurrentLoginID + "");
                        params.put("prod_id", product_id + "");
                        params.put("type", "add");
                        return params;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(
                        (int) TimeUnit.SECONDS.toMillis(1000), //After the set time elapses the request will timeout
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(request);
            }
        });

        reviewAddCardView = findViewById(R.id.review_add_cardview);

        reviewInputText = findViewById(R.id.reviewInputText);
        reviewRatingBar = findViewById(R.id.reviewRatingBar);

        reviewRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {

            reviewInputText.setEnabled(false);
            reviewRatingBar.setEnabled(false);

            if(reviewInputText.getText().toString().isEmpty()){
                reviewInputText.setText("Posted A Review");
            }

            StringRequest request = new StringRequest(1, ServerUrls.updateReviews, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(!response.contains("Error")){
                        ReviewDataModel model = new ReviewDataModel();
                        model.ReviewID = Integer.parseInt(response.trim());
                        model.UserID = UserLogin.CurrentLoginID;
                        model.ProductID = product_id;
                        model.Title = UserLogin.CurrentLoginUsername;
                        model.Subtitle = reviewInputText.getText().toString();
                        model.Ratings = rating;
                        model.onDeleteClicked = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reviewInputText.setEnabled(false);
                                reviewRatingBar.setEnabled(false);
                                StringRequest request = new StringRequest(1, ServerUrls.updateReviews, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if(!response.contains("Error")){
                                            if(ratingsTextView != null) {
                                                TotalRaters--;
                                                totalRatings -= model.Ratings;
                                                ratingsTextView.setText((totalRatings / TotalRaters) + "");
                                            }
                                            reviewsList.remove(model);
                                            reviewsAdapter.notifyDataSetChanged();
                                            reviewInputText.setEnabled(true);
                                            reviewRatingBar.setEnabled(true);
                                        }else {
                                            reviewInputText.setEnabled(false);
                                            reviewRatingBar.setEnabled(false);
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        reviewInputText.setEnabled(false);
                                        reviewRatingBar.setEnabled(false);
                                    }
                                }) {
                                    @Nullable
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("type", "remove");
                                        params.put("id", model.ReviewID + "");
                                        params.put("prod_id", model.ProductID + "");
                                        params.put("userID", UserLogin.CurrentLoginID + "");
                                        return params;
                                    }
                                };
                                request.setRetryPolicy(new DefaultRetryPolicy(
                                        (int) TimeUnit.SECONDS.toMillis(1000), //After the set time elapses the request will timeout
                                        0,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                queue.add(request);
                            }
                        };
                        model.CanDelete = true;
                        model.Picture = ImageCache.GetProfileImage(UserLogin.CurrentLoginID);

                        reviewsList.add(model);
                        reviewsAdapter.notifyDataSetChanged();
                        if(ratingsTextView != null) {
                            TotalRaters++;
                            totalRatings += rating;
                            ratingsTextView.setText((totalRatings / TotalRaters) + "");
                        }
                        reviewInputText.setEnabled(false);
                        reviewRatingBar.setEnabled(false);
                    }else {
                        reviewInputText.setEnabled(true);
                        reviewRatingBar.setEnabled(true);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    reviewInputText.setEnabled(true);
                    reviewRatingBar.setEnabled(true);
                    Toast.makeText(ViewProductActivity.this, "Failed to add review, please try again", Toast.LENGTH_LONG).show();
                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("type", "add");
                    params.put("prod_id", product_id + "");
                    params.put("text", reviewInputText.getText().toString());
                    params.put("rate", rating + "");
                    params.put("userID", UserLogin.CurrentLoginID + "");
                    return params;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(1000), //After the set time elapses the request will timeout
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(request);

        });

        reviewInputText.setEnabled(true);
        reviewRatingBar.setEnabled(true);

        extraSpinner = findViewById(R.id.product_item_extra_spinner);
        extraTextView = findViewById(R.id.product_item_extra_text);

        extraSpinnerAdapter = new SpinnerAdapter(this, ExtraOptions);
        extraSpinner.setAdapter(extraSpinnerAdapter);

        extraSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        extraTextView.setVisibility(View.GONE);
        extraSpinner.setVisibility(View.GONE);

        Button addToCartButton = findViewById(R.id.addToCartButton);

        if(!UserLogin.isLoggedIn){
            addToCartButton.setVisibility(View.GONE);
        }

        addToCartButton.setOnClickListener(v -> {
            addToCartButton.setEnabled(false);
            String currentExtra = extraTextView.getText().toString();
            String currentExtraValue = ExtraOptions.get(extraSpinner.getSelectedItemPosition());
            JsonObjectRequest request = new JsonObjectRequest(ServerUrls.addCartItem(product_id, currentSelectedItemId, currentExtra, currentExtraValue), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("CartAddResult", response.toString());
                    try {
                        //JSONObject row = response.getJSONObject(0);
                        if(response.getString("state").equalsIgnoreCase("SUCCESS")){
                            Snackbar.make(v, "Item Added Successfully!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();/*new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra("returnToCart", true);  // put data that you want returned to activity A
                                            setResult(Activity.RESULT_OK, resultIntent);
                                            finish();
                                        }
                                    }).show();*/
                        }else {
                            Snackbar.make(v, "Failed To Add Item To Cart!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }


                    } catch (Exception e) {
                        Log.d("UIError", e.getMessage() + "");
                    }
                    addToCartButton.setEnabled(true);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("CartAddError", error.getMessage() + "");
                    addToCartButton.setEnabled(true);
                    Snackbar.make(v, "Failed To Add Item To Cart!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(1000), //After the set time elapses the request will timeout
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(request);

        });

        requestProductImages();

        requestProductDetails();

        requestProductItems();

        reviewsRecyclerView = findViewById(R.id.user_reviews_recycle);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.CENTER);
        layoutManager.setFlexWrap(FlexWrap.WRAP);

        reviewsRecyclerView.setLayoutManager(layoutManager);

        reviewsAdapter = new ReviewFlexBoxAdapter(this, reviewsList);
        reviewsRecyclerView.setAdapter(reviewsAdapter);

        requestProductReviews();

    }

    public void requestProductImages(){
        if(ImageCache.GetProductImage(product_id) == null){
            ImageRequest request = new ImageRequest(ServerUrls.getProductImage(product_id), new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    ImageCache.SetProductImage(product_id, response);
                    productImage.setImageBitmap(response);
                    productMainImage.setImageBitmap(response);
                }
            }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888,
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestProductImages();
                    Log.d("ProductImageError", error.getMessage() + "");
                }
            });
            queue.add(request);
        }else {
            Bitmap response = ImageCache.GetProductImage(product_id);
            productImage.setImageBitmap(response);
            productMainImage.setImageBitmap(response);
        }
    }

    public void requestProductDetails(){
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getProduct(product_id), new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject row = response.getJSONObject(0);
                    int prod_id = row.getInt("prod_id");
                    String name = row.getString("name");
                    String details = row.getString("details");
                    double price = row.getDouble("price");
                    double discount = row.getDouble("discount");
                    String deliveryTime = row.getString("delivery_time");
                    int likes = row.getInt("likes");
                    String department = row.getString("department");
                    String description = row.getString("description");

                    productTitle.setText(name);

                    DecimalFormat df = new DecimalFormat("0.00");

                    if(discount > 0){
                        if(UserLogin.CurrentCurrency != "US, USD"){
                            productDiscountText.setText("LBP " + String.format("%,d", (int) price));
                        }else {
                            productDiscountText.setText("$" + df.format(price));
                        }
                        productDiscountText.setPaintFlags(productDiscountText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        if(UserLogin.CurrentCurrency != "US, USD"){
                            productPriceText.setText("LBP " + String.format("%,d", (int) (price - discount)));
                        }else {
                            productPriceText.setText("$" + df.format(price - discount));
                        }
                    }else {
                        if(UserLogin.CurrentCurrency != "US, USD"){
                            productPriceText.setText("LBP " + String.format("%,d", (int) price));
                        }else {
                            productPriceText.setText("$" + df.format(price));
                        }
                    }

                    String[] detailLines = details.split("\n");
                    for(String line : detailLines){
                        String[] data = line.split("<>");
                        addDetailsToTable(data[0], data[1]);
                    }
                    String departmentName = "Uni Sex";
                    if(department == "m"){
                        departmentName = "Men";
                    }else if(department == "f"){
                        departmentName = "Women";
                    }
                    addDetailsToTable("Department", departmentName);
                    quantityTextView = addDetailsToTable("Quantity", "");
                    addDetailsToTable("Delivery Time", deliveryTime + " Days");
                    likesTextView = addDetailsToTable("Likes", likes + "");
                    ratingsTextView = addDetailsToTable("Ratings", "");

                    productDescription.setText(description);

                } catch(Exception e) {
                    Log.d("UIError", e.getMessage() + "");
                    //.makeText(root.getContext(), "UI Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ProductError", error.getMessage() + "");
                requestProductDetails();
            }
        });
        queue.add(request);
    }

     public void requestProductItems(){
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getProductItems(product_id), new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject row = response.getJSONObject(i);
                        requestSingleItemData(row, i == 0 ? true : false);
                    }

                } catch(Exception e) {
                    Log.d("UIError", e.getMessage() + "");
                    //.makeText(root.getContext(), "UI Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ProductItemError", error.getMessage() + "");
                requestProductItems();
            }
        });
        queue.add(request);
    }

    public void requestSingleItemData(JSONObject row, boolean isFirstItem){

        try {
            int item_id = row.getInt("item_id");

            if (ImageCache.GetProductItemImage(item_id) == null) {
                ImageRequest request = new ImageRequest(ServerUrls.getProductItemImage(product_id, item_id), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        ImageCache.SetProductItemImage(item_id, response);
                        addProductItem(row, isFirstItem);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                requestSingleItemData(row, isFirstItem);
                                Log.d("ProductItemImageError", error.getMessage() + "");
                            }
                        });
                queue.add(request);
            } else {
                addProductItem(row, isFirstItem);
            }
        }catch(Exception e) {
            Log.d("UIError", e.getMessage() + "");
        }
    }

    public void addProductItem(JSONObject row, boolean isFirstItem){

        try {
            int item_id = row.getInt("item_id");
            String name = row.getString("name");
            int quantity = row.getInt("quantity");
            String extraName = row.getString("extra_name");
            String extraOptions = row.getString("extra_options");

            Bitmap response = ImageCache.GetProductItemImage(item_id);

            if (isFirstItem) {
                productItemName.setText(name);
                quantityTextView.setText(quantity + "");
            }

            MaterialCardView card = new MaterialCardView(ViewProductActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.
                    LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(20, 20, 20, 20);
            card.setLayoutParams(lp);
            card.setRadius((int) (10 * getResources().getDisplayMetrics().density + 0.5f));

            ImageView img = new ImageView(ViewProductActivity.this);
            img.setImageBitmap(response);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                    (int) (105 * getResources().getDisplayMetrics().density + 0.5f),
                    (int) (105 * getResources().getDisplayMetrics().density + 0.5f));
            img.setLayoutParams(lp1);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if (isFirstItem) {
                card.setStrokeWidth(2);
                currentSelectedItemCard = card;
                currentSelectedItemId = item_id;
                currentSelectedItemQuantity = quantity;
                currentSelectedItemExtraName = extraName;
                currentSelectedItemExtraOptions = extraOptions;
                if(extraName.isEmpty()){
                    extraTextView.setText("Extra");
                    ExtraOptions.clear();
                    ExtraOptions.add("none");
                    extraSpinnerAdapter.notifyDataSetChanged();
                    extraTextView.setVisibility(View.GONE);
                    extraSpinner.setSelection(0);
                    extraSpinner.setVisibility(View.GONE);
                }else {
                    extraTextView.setVisibility(View.VISIBLE);
                    extraSpinner.setVisibility(View.VISIBLE);
                    extraTextView.setText(extraName);
                    ExtraOptions.clear();
                    String[] items = extraOptions.split("\n");
                    for(String item : items){
                        ExtraOptions.add(item);
                    }
                    extraSpinnerAdapter.notifyDataSetChanged();
                }
            }
            card.setStrokeColor(getResources().getColor(R.color.black));

            card.addView(img);
            card.setOnClickListener(view -> {
                //Add Item Selection Edition
                currentSelectedItemCard.setStrokeWidth(0);
                card.setStrokeWidth(2);
                currentSelectedItemCard = card;
                currentSelectedItemId = item_id;
                currentSelectedItemQuantity = quantity;
                currentSelectedItemExtraName = extraName;
                currentSelectedItemExtraOptions = extraOptions;

                if(extraName.isEmpty()){
                    extraTextView.setText("Extra");
                    ExtraOptions.clear();
                    ExtraOptions.add("none");
                    extraSpinnerAdapter.notifyDataSetChanged();
                    extraTextView.setVisibility(View.GONE);
                    extraSpinner.setSelection(0);
                    extraSpinner.setVisibility(View.GONE);
                }else {
                    extraTextView.setVisibility(View.VISIBLE);
                    extraSpinner.setVisibility(View.VISIBLE);
                    extraTextView.setText(extraName);
                    ExtraOptions.clear();
                    String[] items = extraOptions.split("\n");
                    for(String item : items){
                        ExtraOptions.add(item);
                    }
                    extraSpinnerAdapter.notifyDataSetChanged();
                }

                productItemName.setText(name);
                quantityTextView.setText(quantity + "");

                productMainImage.setImageBitmap(response);

            });
            productItemsFlex.addView(card);
        }catch (Exception ex){
            Log.d("UIError", ex.getMessage() + "");
        }
    }

    public void requestProductReviews(){
        reviewsList.clear();
        reviewsAdapter.notifyDataSetChanged();
        reviewInputText.setEnabled(true);
        reviewRatingBar.setEnabled(true);
        TotalRaters = 0;
        totalRatings = 0;
        if(ratingsTextView != null){
            ratingsTextView.setText("0");
        }
        Log.d("Reviews", "Loading Reviews For " + product_id);
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getProductReviews(product_id), new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject row = response.getJSONObject(i);
                        int rev_id = row.getInt("rev_id");
                        int user_id = row.getInt("user_id");
                        int product_id = row.getInt("product_id");
                        String text = row.getString("text");
                        double rate = row.getDouble("rate");
                        TotalRaters++;
                        totalRatings += rate;
                        if(ratingsTextView != null) {
                            ratingsTextView.setText((totalRatings / TotalRaters) + "");
                        }

                        ReviewDataModel model = new ReviewDataModel();
                        model.ReviewID = rev_id;
                        model.UserID = user_id;
                        model.ProductID = product_id;
                        model.Title = UserLogin.CurrentLoginUsername;
                        model.Subtitle = text;
                        model.Ratings = rate;
                        model.onDeleteClicked = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reviewInputText.setEnabled(false);
                                reviewRatingBar.setEnabled(false);
                                StringRequest request = new StringRequest(1, ServerUrls.updateReviews, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if(!response.contains("Error")){
                                            if(ratingsTextView != null) {
                                                TotalRaters--;
                                                totalRatings -= model.Ratings;
                                                ratingsTextView.setText((totalRatings / TotalRaters) + "");
                                            }
                                            reviewsList.remove(model);
                                            reviewsAdapter.notifyDataSetChanged();
                                            reviewInputText.setEnabled(true);
                                            reviewRatingBar.setEnabled(true);
                                        }else {
                                            reviewInputText.setEnabled(false);
                                            reviewRatingBar.setEnabled(false);
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        reviewInputText.setEnabled(false);
                                        reviewRatingBar.setEnabled(false);
                                    }
                                }) {
                                    @Nullable
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("type", "remove");
                                        params.put("id", model.ReviewID + "");
                                        params.put("prod_id", model.ProductID + "");
                                        params.put("userID", UserLogin.CurrentLoginID + "");
                                        return params;
                                    }
                                };
                                request.setRetryPolicy(new DefaultRetryPolicy(
                                        (int) TimeUnit.SECONDS.toMillis(1000), //After the set time elapses the request will timeout
                                        0,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                queue.add(request);
                            }
                        };
                        if(UserLogin.CurrentLoginID == user_id){
                            model.CanDelete = true;
                            reviewInputText.setEnabled(false);
                            reviewRatingBar.setEnabled(false);
                        }

                        if(ImageCache.GetProfileImage(user_id) == null){
                            ImageRequest request = new ImageRequest(ServerUrls.getUserImage(user_id), new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    ImageCache.SetProfileImage(user_id, response);
                                    model.Picture = response;
                                    reviewsList.add(model);
                                    reviewsAdapter.notifyDataSetChanged();
                                }
                            }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888,
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    });

                            queue.add(request);
                        }else {
                            Bitmap userimage = ImageCache.GetProfileImage(user_id);
                            model.Picture = userimage;
                            reviewsList.add(model);
                            reviewsAdapter.notifyDataSetChanged();
                        }
                    }
                } catch(Exception e) {
                    Log.d("UIError", e.getMessage() + "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ProductReviewsError", error.getMessage() + "");
            }
        });
        queue.add(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public TextView addDetailsToTable(String title, String text){

        Typeface boldTypeFace = getResources().getFont(R.font.inter_bold);
        Typeface mediumTypeFace = getResources().getFont(R.font.inter_medium);

        TableRow row = new TableRow(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.
                LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(lp);

        TableRow.LayoutParams lp2 = new TableRow.LayoutParams(ViewGroup.
                LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.column = 1;

        lp2.setMargins(0, 10, 0, 0);

        TextView titleTextView = new TextView(this);
        titleTextView.setLayoutParams(lp2);
        titleTextView.setTypeface(boldTypeFace);
        titleTextView.setText(title);
        titleTextView.setTextColor(getResources().getColor(R.color.black));

        TableRow.LayoutParams lp3 = new TableRow.LayoutParams(ViewGroup.
                LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp3.column = 2;

        lp3.setMargins(25, 10, 0, 0);

        TextView textTextView = new TextView(this);
        textTextView.setLayoutParams(lp3);
        textTextView.setTypeface(mediumTypeFace);
        textTextView.setText(text);
        textTextView.setTextColor(getResources().getColor(R.color.black));

        row.addView(titleTextView);
        row.addView(textTextView);

        productDetailsTable.addView(row);

        return textTextView;
    }

}