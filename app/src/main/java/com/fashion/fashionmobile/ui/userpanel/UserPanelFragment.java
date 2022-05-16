package com.fashion.fashionmobile.ui.userpanel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.MainActivity;
import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.ViewProductActivity;
import com.fashion.fashionmobile.adapters.ReviewFlexBoxAdapter;
import com.fashion.fashionmobile.adapters.ReviewDataModel;
import com.fashion.fashionmobile.databinding.FragmentUserPanelBinding;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserPanelFragment extends Fragment {

    private FragmentUserPanelBinding binding;

    private FlexboxLayout favoritesFlex;
    private FlexboxLayout ordersFlex;

    RecyclerView reviewsRecyclerView;
    ArrayList<ReviewDataModel> reviewsList = new ArrayList<>();
    ReviewFlexBoxAdapter reviewsAdapter;

    private RequestQueue queue;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if(!UserLogin.isLoggedIn){
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.setReorderingAllowed(true);
            ft.replace(R.id.nav_host_fragment_content_main, UserPanelLoginFragment.class, null);
            ft.addToBackStack(UserPanelLoginFragment.class.getName());
            ft.commit();
        }else {
            binding = FragmentUserPanelBinding.inflate(inflater, container, false);
            View root = binding.getRoot();

            TextView titleText = root.findViewById(R.id.user_fullname_text);
            TextView usernameText = root.findViewById(R.id.user_username_text);
            TextView emailText = root.findViewById(R.id.user_email_text);

            CardView userImageCardView = root.findViewById(R.id.user_image_card_view);

            MainActivity.CartFab.setVisibility(View.VISIBLE);

            MainActivity.profileName.setText(UserLogin.CurrentLoginFullName);
            MainActivity.profileEmail.setText(UserLogin.CurrentLoginEmail);
            MainActivity.profileImage.setImageDrawable(null);
            MainActivity.profileCard.setVisibility(View.INVISIBLE);

            userImageCardView.setVisibility(View.GONE);

            ImageView profileImage = root.findViewById(R.id.user_profile_picture);

            titleText.setText(UserLogin.CurrentLoginFullName);
            usernameText.setText(UserLogin.CurrentLoginUsername);
            emailText.setText(UserLogin.CurrentLoginEmail);

            queue = Volley.newRequestQueue(MainActivity.CurrentContext);

            int currentID = UserLogin.CurrentLoginID;
            if(ImageCache.GetProfileImage(currentID) == null){
                ImageRequest request = new ImageRequest(ServerUrls.getUserImage(currentID), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        ImageCache.SetProfileImage(currentID, response);
                        profileImage.setImageBitmap(response);
                        MainActivity.profileImage.setImageBitmap(response);
                        MainActivity.profileCard.setVisibility(View.VISIBLE);
                        userImageCardView.setVisibility(View.VISIBLE);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Toast.makeText(MainActivity.CurrentContext, error.toString(), Toast.LENGTH_SHORT).show();
                                Log.d("ProfileError", error.getMessage() + "");
                            }
                        });

                queue.add(request);
            }else {
                Bitmap userimage = ImageCache.GetProfileImage(currentID);
                profileImage.setImageBitmap(userimage);
                MainActivity.profileImage.setImageBitmap(userimage);
                MainActivity.profileCard.setVisibility(View.VISIBLE);
                userImageCardView.setVisibility(View.VISIBLE);
            }

            Button manageProfile = root.findViewById(R.id.user_manage_profile);

            manageProfile.setOnClickListener(view -> {
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.setReorderingAllowed(true);
                ft.replace(R.id.nav_host_fragment_content_main, UserPanelManageFragment.class, null);
                ft.addToBackStack(UserPanelManageFragment.class.getName());
                ft.commit();
            });

            TextView showMoreFavorites = root.findViewById(R.id.user_favorites_showmore);

            showMoreFavorites.setOnClickListener(view -> {
                if(showMoreFavorites.getText().toString().equalsIgnoreCase("Show More")){
                    getFavoriteProducts(root, -1);
                    showMoreFavorites.setText("Show Less");
                }else {
                    getFavoriteProducts(root, 6);
                    showMoreFavorites.setText("Show More");
                }
            });

            favoritesFlex = root.findViewById(R.id.user_favorites_flex);
            getFavoriteProducts(root, 6);

            TextView showMoreOrders = root.findViewById(R.id.user_orders_showmore);

            showMoreOrders.setOnClickListener(view -> {
                if(showMoreOrders.getText().toString().equalsIgnoreCase("Show More")){
                    getPreviousOrders(root, -1);
                    showMoreOrders.setText("Show Less");
                }else {
                    getPreviousOrders(root, 6);
                    showMoreOrders.setText("Show More");
                }
            });

            ordersFlex = root.findViewById(R.id.user_order_flex);
            getPreviousOrders(root, 6);

            TextView showMoreReviews = root.findViewById(R.id.user_reviews_showmore);

            showMoreReviews.setOnClickListener(view -> {
                if(showMoreReviews.getText().toString().equalsIgnoreCase("Show More")){
                    getPostedReviews(root,-1);
                    showMoreReviews.setText("Show Less");
                }else {
                    getPostedReviews(root, 6);
                    showMoreReviews.setText("Show More");
                }
            });

            reviewsRecyclerView = root.findViewById(R.id.user_reviews_recycle);

            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(root.getContext());
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setJustifyContent(JustifyContent.CENTER);
            layoutManager.setAlignItems(AlignItems.CENTER);
            layoutManager.setFlexWrap(FlexWrap.WRAP);

            reviewsRecyclerView.setLayoutManager(layoutManager);

            reviewsAdapter = new ReviewFlexBoxAdapter(root.getContext(), reviewsList);
            reviewsRecyclerView.setAdapter(reviewsAdapter);

            getPostedReviews(root, 6);

            return root;
        }
        return null;
    }

    public void getFavoriteProducts(View root, int maxCount) {

        favoritesFlex.removeAllViews();
        ArrayList<Integer> allLikes = UserLikes.GetAllLikes();
        for(int i = 0; i < (allLikes.size() > maxCount ? (maxCount != -1 ? maxCount : allLikes.size()) : allLikes.size()); i++){
            int prod_id = allLikes.get(i);
            if(ImageCache.GetProductImage(prod_id) == null) {
                ImageRequest request = new ImageRequest(ServerUrls.getProductImage(prod_id), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {

                        CardView card = new CardView(root.getContext());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.
                                LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(20, 20, 20, 20);
                        card.setLayoutParams(lp);
                        card.setRadius((int) (10 * root.getContext().getResources().getDisplayMetrics().density + 0.5f));

                        ImageView img = new ImageView(root.getContext());
                        img.setImageBitmap(response);
                        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                                (int) (155 * root.getContext().getResources().getDisplayMetrics().density + 0.5f),
                                (int) (170 * root.getContext().getResources().getDisplayMetrics().density + 0.5f));
                        img.setLayoutParams(lp1);
                        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ImageCache.SetProductImage(prod_id, response);

                        card.addView(img);
                        card.setOnClickListener(view -> {
                            Intent i = new Intent(root.getContext(),ViewProductActivity.class);
                            i.putExtra("product_id", prod_id);
                            startActivityForResult(i, 1);
                        });
                        favoritesFlex.addView(card);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                getFavoriteProducts(root, maxCount);
                            }
                        });

                queue.add(request);
            } else {
                CardView card = new CardView(root.getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.
                        LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(20, 20, 20, 20);
                card.setLayoutParams(lp);
                card.setRadius((int) (10 * root.getContext().getResources().getDisplayMetrics().density + 0.5f));

                ImageView img = new ImageView(root.getContext());
                img.setImageBitmap(ImageCache.GetProductImage(prod_id));
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                        (int) (155 * root.getContext().getResources().getDisplayMetrics().density + 0.5f),
                        (int) (170 * root.getContext().getResources().getDisplayMetrics().density + 0.5f));
                img.setLayoutParams(lp1);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);

                card.addView(img);
                card.setOnClickListener(view -> {
                    Intent intent = new Intent(root.getContext(),ViewProductActivity.class);
                    intent.putExtra("product_id", prod_id);
                    startActivityForResult(intent, 1);
                });
                favoritesFlex.addView(card);
            }
        }
    }

    public void getPreviousOrders(View root, int maxCount) {

        ordersFlex.removeAllViews();
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getPreviousOrders(maxCount), new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject row = response.getJSONObject(i);
                        int order_id = row.getInt("order_id");
                        String address = row.getString("address");
                        String country = row.getString("country");
                        String countryCode = row.getString("countryCode");
                        String number = row.getString("number");
                        String payment = row.getString("payment");
                        String state = row.getString("state");
                        String date = row.getString("date");

                        CardView card = new CardView(root.getContext());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.
                                LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(20, 20, 20, 20);
                        card.setLayoutParams(lp);
                        card.setRadius((int) (10 * root.getContext().getResources().getDisplayMetrics().density + 0.5f));

                        LinearLayout layout = new LinearLayout(root.getContext());
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                                (int) (220 * root.getContext().getResources().getDisplayMetrics().density + 0.5f),
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setPadding(15, 15, 15, 15);
                        layout.setLayoutParams(lp2);

                        LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);

                        TextView textView = new TextView(root.getContext());
                        textLayout.setMargins(0, 0, 0, 15);
                        textView.setLayoutParams(textLayout);
                        textView.setText("Order № " + order_id);
                        textView.setTextSize(20);
                        textView.setTextColor(getResources().getColor(R.color.black));
                        Typeface textFont = getResources().getFont(R.font.inter_bold);
                        textView.setTypeface(textFont);
                        layout.addView(textView);

                        textView = new TextView(root.getContext());
                        textView.setLayoutParams(textLayout);
                        textView.setText(country + ", " + address);
                        textView.setTextSize(16);
                        textView.setTextColor(getResources().getColor(R.color.black));
                        textFont = getResources().getFont(R.font.inter_medium);
                        textView.setTypeface(textFont);
                        layout.addView(textView);

                        textView = new TextView(root.getContext());
                        textView.setLayoutParams(textLayout);
                        textView.setText("(+" + countryCode + ") " + number);
                        textView.setTextSize(16);
                        textView.setTextColor(getResources().getColor(R.color.black));
                        textFont = getResources().getFont(R.font.inter_medium);
                        textView.setTypeface(textFont);
                        layout.addView(textView);

                        int stateColor = getResources().getColor(R.color.black);

                        if(state.equalsIgnoreCase("Pending")){
                            stateColor = getResources().getColor(R.color.state_pending);
                        }else if(state.equalsIgnoreCase("Delivered")){
                            stateColor = getResources().getColor(R.color.state_delivered);
                        }else if(state.equalsIgnoreCase("On The Way")){
                            stateColor = getResources().getColor(R.color.state_ontheway);
                        }else if(state.equalsIgnoreCase("Returned")){
                            stateColor = getResources().getColor(R.color.state_returned);
                        }

                        textView = new TextView(root.getContext());
                        textView.setLayoutParams(textLayout);
                        textView.setText(state);
                        textView.setTextSize(16);
                        textView.setTextColor(stateColor);
                        textFont = getResources().getFont(R.font.inter_bold);
                        textView.setTypeface(textFont);
                        layout.addView(textView);

                        textView = new TextView(root.getContext());
                        textView.setLayoutParams(textLayout);
                        textView.setText(date);
                        textView.setTextSize(16);
                        textView.setTextColor(getResources().getColor(R.color.black));
                        textFont = getResources().getFont(R.font.inter_medium);
                        textView.setTypeface(textFont);
                        layout.addView(textView);

                        textView = new TextView(root.getContext());
                        textView.setLayoutParams(textLayout);
                        textView.setText(payment);
                        textView.setTextSize(16);
                        textView.setTextColor(getResources().getColor(R.color.black));
                        textFont = getResources().getFont(R.font.inter_medium);
                        textView.setTypeface(textFont);
                        layout.addView(textView);

                        card.addView(layout);
                        ordersFlex.addView(card);
                    }
                } catch(Exception e) {
                    Log.d("UIError", e.getMessage() + "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("OrdersError", error.getMessage() + "");
                getPreviousOrders(root, maxCount);
            }
        });
        queue.add(request);
    }

    public void getPostedReviews(View root, int maxCount) {

        reviewsList.clear();
        reviewsAdapter.notifyDataSetChanged();
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getPostedReviews(maxCount), new Response.Listener<JSONArray>() {
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
                        String prod_name = row.getString("name");

                        ReviewDataModel model = new ReviewDataModel();
                        model.ReviewID = rev_id;
                        model.UserID = user_id;
                        model.ProductID = product_id;
                        model.Title = UserLogin.CurrentLoginUsername + " • " + prod_name;
                        model.Subtitle = text;
                        model.Ratings = rate;

                        model.CanVisitProduct = true;
                        model.onCardClicked = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(root.getContext(),ViewProductActivity.class);
                                intent.putExtra("product_id", product_id);
                                startActivityForResult(intent, 1);
                            }
                        };

                        if(ImageCache.GetProfileImage(UserLogin.CurrentLoginID) == null){
                            ImageRequest request = new ImageRequest(ServerUrls.getUserImage(UserLogin.CurrentLoginID), new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    ImageCache.SetProfileImage(UserLogin.CurrentLoginID, response);
                                    model.Picture = response;
                                    reviewsList.add(model);
                                    reviewsAdapter.notifyDataSetChanged();
                                }
                            }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888,
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            getPostedReviews(root, maxCount);
                                        }
                                    });

                            queue.add(request);
                        }else {
                            Bitmap userimage = ImageCache.GetProfileImage(UserLogin.CurrentLoginID);
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
                Log.d("ReviewsError", error.getMessage() + "");
                getPostedReviews(root, maxCount);
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