package com.fashion.fashionmobile.ui.home;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.R;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.fashion.fashionmobile.databinding.FragmentHomeBinding;
import com.fashion.fashionmobile.helpers.ImageCache;
import com.fashion.fashionmobile.helpers.ServerUrls;
import com.fashion.fashionmobile.helpers.UserLogin;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FlexboxLayout flex;
    private ViewFlipper flipper;
    private RequestQueue queue;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button shopNow = root.findViewById(R.id.shop_now);
        shopNow.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_home_to_products);
        });

        queue = Volley.newRequestQueue(root.getContext());

        flex = root.findViewById(R.id.flexLayout);
        getPopularProducts(root);

        flipper = root.findViewById(R.id.flipper);
        getNewDrops(root);

        return root;
    }

    public void getPopularProducts(View root) {

        flex.removeAllViews();
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getPopularProducts + "?currency=" + UserLogin.CurrentCurrency, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject row = response.getJSONObject(i);
                        int prod_id = row.getInt("prod_id");
                        String name = row.getString("name");
                        double price = row.getDouble("price");
                        double discount = row.getDouble("discount");
                        if(ImageCache.GetProductImage(prod_id) == null) {
                            ImageRequest request = new ImageRequest(ServerUrls.getProductImage(prod_id), new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
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
                                    Typeface t = getResources().getFont(R.font.inter_bold);
                                    prod_name.setTypeface(t);
                                    prod_name.setLineHeight(50);

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
                                        Typeface t1 = getResources().getFont(R.font.inter_bold);
                                        prod_price.setTypeface(t1);
                                        prod_price.setPaintFlags(prod_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                        TextView discount_price = new TextView(root.getContext());
                                        discount_price.setLayoutParams(textlp);
                                        double newPrice = price - discount;
                                        if(UserLogin.CurrentCurrency != "US, USD"){
                                            discount_price.setText("LBP " + String.format("%,d", (int) newPrice));
                                        }else {
                                            discount_price.setText("$" + df.format(newPrice));
                                        }
                                        discount_price.setTextColor(Color.parseColor("#ffd814"));
                                        discount_price.setTextSize(14);
                                        Typeface t2 = getResources().getFont(R.font.inter_bold);
                                        discount_price.setTypeface(t2);

                                        linear1.addView(prod_price);
                                        linear1.addView(discount_price);
                                    } else {
                                        TextView prod_price = new TextView(root.getContext());
                                        prod_price.setLayoutParams(textLayout);
                                        String p = String.valueOf(price);
                                        if(UserLogin.CurrentCurrency != "US, USD"){
                                            prod_price.setText("LBP " + String.format("%,d", (int) Double.parseDouble(p)));
                                        }else {
                                            prod_price.setText("$" + p);
                                        }
                                        prod_price.setTextColor(Color.BLACK);
                                        prod_price.setTextSize(14);
                                        Typeface t2 = getResources().getFont(R.font.inter_bold);
                                        prod_price.setTypeface(t2);
                                        linear1.addView(prod_price);
                                    }

                                    linear.addView(card);
                                    linear.addView(prod_name);
                                    linear.addView(linear1);
                                    flex.addView(linear);
                                }
                            }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888,
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(root.getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            queue.add(request);
                        } else {
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

                            ImageView img = new ImageView(root.getContext());
                            img.setImageBitmap(ImageCache.GetProductImage(prod_id));
                            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                                    (int) (155 * root.getContext().getResources().getDisplayMetrics().density + 0.5f),
                                    (int) (190 * root.getContext().getResources().getDisplayMetrics().density + 0.5f));
                            img.setLayoutParams(lp1);
                            img.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            card.addView(img);

                            LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(
                                    (int) (155 * root.getContext().getResources().getDisplayMetrics().density + 0.5f),
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            textLayout.setMargins(20, 10, 0, 0);
                            TextView prod_name = new TextView(root.getContext());
                            prod_name.setLayoutParams(textLayout);
                            prod_name.setText(name);
                            prod_name.setTextSize(16);
                            Typeface t = getResources().getFont(R.font.inter_bold);
                            prod_name.setTypeface(t);
                            prod_name.setLineHeight(50);

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
                                Typeface t1 = getResources().getFont(R.font.inter_bold);
                                prod_price.setTypeface(t1);
                                prod_price.setPaintFlags(prod_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                TextView discount_price = new TextView(root.getContext());
                                discount_price.setLayoutParams(textlp);
                                double newPrice = price - discount;
                                if(UserLogin.CurrentCurrency != "US, USD"){
                                    discount_price.setText("LBP " + String.format("%,d", (int) newPrice));
                                }else {
                                    discount_price.setText("$" + df.format(newPrice));
                                }
                                discount_price.setTextColor(Color.parseColor("#ffd814"));
                                discount_price.setTextSize(14);
                                Typeface t2 = getResources().getFont(R.font.inter_bold);
                                discount_price.setTypeface(t2);

                                linear1.addView(prod_price);
                                linear1.addView(discount_price);
                            } else {
                                TextView prod_price = new TextView(root.getContext());
                                prod_price.setLayoutParams(textLayout);
                                String p = String.valueOf(price);
                                if(UserLogin.CurrentCurrency != "US, USD"){
                                    prod_price.setText("LBP " + String.format("%,d", (int) Double.parseDouble(p)));
                                }else {
                                    prod_price.setText("$" + p);
                                }
                                prod_price.setTextColor(Color.BLACK);
                                prod_price.setTextSize(14);
                                Typeface t2 = getResources().getFont(R.font.inter_bold);
                                prod_price.setTypeface(t2);
                                linear1.addView(prod_price);
                            }

                            linear.addView(card);
                            linear.addView(prod_name);
                            linear.addView(linear1);
                            flex.addView(linear);
                        }
                    }
                } catch(Exception e) {
                    Log.d("UIError", e.getMessage() + "");
                    //.makeText(root.getContext(), "UI Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ProductsError", error.getMessage() + "");
                //Toast.makeText(root.getContext(), "Server Error Occurred!", Toast.LENGTH_SHORT).show();
                getPopularProducts(root);
            }
        });
        queue.add(request);
    }

    public void getNewDrops(View root) {
        flipper.removeAllViews();
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getNewDrops, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject row = response.getJSONObject(i);
                        int prod_id = row.getInt("prod_id");
//                        String name = row.getString("name");
                        if(ImageCache.GetProductImage(prod_id) == null) {
                            ImageRequest request = new ImageRequest(ServerUrls.getNewDropsImage(prod_id), new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    CardView card = new CardView(root.getContext());
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.
                                            LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    lp.setMargins(0, 0, 0,
                                            (int) (5 * root.getContext().getResources().getDisplayMetrics().density + 0.5f));
                                    card.setLayoutParams(lp);
                                    card.setRadius((int) (10 * root.getContext().getResources().getDisplayMetrics().density + 0.5f));
                                    ImageView img = new ImageView(root.getContext());
                                    img.setImageBitmap(response);
                                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            (int) (200 * root.getContext().getResources().getDisplayMetrics().density + 0.5f));
                                    img.setLayoutParams(lp1);
                                    img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    ImageCache.SetNewDropProductImageCache(prod_id, response);
                                    card.addView(img);
                                    flipper.addView(card);
                                    if (flipper.getChildCount() == 1) {
                                        flipper.stopFlipping();
                                    } else {
                                        flipper.startFlipping();
                                    }

                                }
                            }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888,
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(root.getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            queue.add(request);
                        } else {
                            CardView card = new CardView(root.getContext());
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.
                                    LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(0, 0, 0,
                                    (int) (5 * root.getContext().getResources().getDisplayMetrics().density + 0.5f));
                            card.setLayoutParams(lp);
                            card.setRadius((int) (10 * root.getContext().getResources().getDisplayMetrics().density + 0.5f));
                            ImageView img = new ImageView(root.getContext());
                            img.setImageBitmap(ImageCache.GetNewDropProductImageCache(prod_id));
                            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    (int) (200 * root.getContext().getResources().getDisplayMetrics().density + 0.5f));
                            img.setLayoutParams(lp1);
                            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            card.addView(img);
                            flipper.addView(card);
                            if (flipper.getChildCount() == 1) {
                                flipper.stopFlipping();
                            } else {
                                flipper.startFlipping();
                            }
                        }
                    }
                } catch(Exception e) {
                    Log.d("UIError", e.getMessage() + "");
                    //Toast.makeText(root.getContext(), "UI Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DropsError", error.getMessage() + "");
                //Toast.makeText(root.getContext(), "Server Error Occurred!", Toast.LENGTH_SHORT).show();
                getNewDrops(root);
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