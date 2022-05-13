package com.fashion.fashionmobile.ui.userpanel;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.MainActivity;
import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.databinding.FragmentProductsBinding;
import com.fashion.fashionmobile.databinding.FragmentUserPanelBinding;
import com.fashion.fashionmobile.helpers.ImageCache;
import com.fashion.fashionmobile.helpers.ServerUrls;
import com.fashion.fashionmobile.helpers.UserLogin;
import com.fashion.fashionmobile.ui.products.ProductsViewModel;

public class UserPanelFragment extends Fragment {

    private FragmentUserPanelBinding binding;

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

            MainActivity.profileName.setText(UserLogin.CurrentLoginFullName);
            MainActivity.profileEmail.setText(UserLogin.CurrentLoginEmail);
            MainActivity.profileImage.setImageDrawable(null);
            MainActivity.profileCard.setVisibility(View.INVISIBLE);

            userImageCardView.setVisibility(View.GONE);

            ImageView profileImage = root.findViewById(R.id.user_profile_picture);

            titleText.setText(UserLogin.CurrentLoginFullName);
            usernameText.setText(UserLogin.CurrentLoginUsername);
            emailText.setText(UserLogin.CurrentLoginEmail);

            RequestQueue queue = Volley.newRequestQueue(MainActivity.CurrentContext);

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
                                Toast.makeText(MainActivity.CurrentContext, error.toString(), Toast.LENGTH_SHORT).show();
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

            return root;
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}