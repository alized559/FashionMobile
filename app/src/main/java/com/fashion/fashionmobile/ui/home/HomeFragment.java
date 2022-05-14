package com.fashion.fashionmobile.ui.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.fashion.fashionmobile.databinding.FragmentHomeBinding;
import com.fashion.fashionmobile.helpers.ImageCache;
import com.fashion.fashionmobile.helpers.ServerUrls;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FlexboxLayout flex;
    private ViewFlipper flipper;
    private RequestQueue queue;

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
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getPopularProducts, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject row = response.getJSONObject(i);
                        int prod_id = row.getInt("prod_id");
                        if(ImageCache.GetProductImage(prod_id) == null) {
                            ImageRequest request = new ImageRequest(ServerUrls.getProductImage(prod_id), new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    CardView card = new CardView(root.getContext());
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.
                                            LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    lp.setMargins(0, 0, 0, 60);
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
                                    flex.addView(card);
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
                                    LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(0, 0, 0, 60);
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
                            flex.addView(card);
                        }
                    }
                } catch(Exception e) {
                    Toast.makeText(root.getContext(), "error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(root.getContext(), "error", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }

    public void getNewDrops(View root) {
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getNewDrops, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject row = response.getJSONObject(i);
                        int prod_id = row.getInt("prod_id");
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
                    Toast.makeText(root.getContext(), "error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(root.getContext(), "error", Toast.LENGTH_SHORT).show();
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