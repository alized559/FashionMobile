package com.fashion.fashionmobile.ui.userpanel;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.MainActivity;
import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.databinding.FragmentUserPanelBinding;
import com.fashion.fashionmobile.databinding.FragmentUserPanelManageBinding;
import com.fashion.fashionmobile.helpers.UserLikes;
import com.fashion.fashionmobile.helpers.UserLogin;
import com.fashion.fashionmobile.helpers.ImageCache;
import com.fashion.fashionmobile.helpers.ServerUrls;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserPanelManageFragment extends Fragment {

    private FragmentUserPanelManageBinding binding;

    Button changeImage = null;

    private RequestQueue queue = null;

    ImageView profileImage = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUserPanelManageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        queue = Volley.newRequestQueue(root.getContext());

        MainActivity.CartFab.setVisibility(View.GONE);

        EditText fullnameInput = root.findViewById(R.id.fullnameInputText);
        Button changeFullname = root.findViewById(R.id.userChangeFullname);

        changeFullname.setOnClickListener(view -> {
            String fullname = fullnameInput.getText().toString();

            if(fullname.length() < 6){
                Toast.makeText(root.getContext(), "Input Fields Invalid Length (MIN 6)", Toast.LENGTH_LONG).show();
            }else {
                changeFullname.setEnabled(false);

                StringRequest request = new StringRequest(1, ServerUrls.changeProfileFullname, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.contains("success")){
                            changeFullname.setEnabled(true);
                            fullnameInput.setText("");
                            Toast.makeText(MainActivity.CurrentContext, response, Toast.LENGTH_SHORT).show();
                            UserLogin.CurrentLoginFullName = fullname;
                            MainActivity.profileName.setText(fullname);
                        }else {
                            changeFullname.setEnabled(true);
                            Toast.makeText(MainActivity.CurrentContext, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        changeFullname.setEnabled(true);
                        Log.e("FullnameError", error.getMessage() + " ");
                        Toast.makeText(MainActivity.CurrentContext, "Error, Couldn't Change Full Name!", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("id", UserLogin.CurrentLoginID + "");
                        params.put("fullname", fullname);
                        return params;
                    }
                };
                queue.add(request);

            }

        });

        EditText currentPasswordInput = root.findViewById(R.id.currentPasswordInputText);
        EditText newPasswordInput = root.findViewById(R.id.newPasswordInputText);
        EditText repeatNewPasswordInput = root.findViewById(R.id.repeatNewPasswordInputText);
        Button changePassword = root.findViewById(R.id.userChangePassword);

        changePassword.setOnClickListener(view -> {
            String password = currentPasswordInput.getText().toString();
            String newPassword = newPasswordInput.getText().toString();
            String repeatPassword = repeatNewPasswordInput.getText().toString();

            if(newPassword.length() < 6 || password.length() < 6 || repeatPassword.length() < 6){
                Toast.makeText(root.getContext(), "Input Fields Invalid Length (MIN 6)", Toast.LENGTH_LONG).show();
            }else {
                if (!newPassword.equals(repeatPassword)) {
                    Toast.makeText(root.getContext(), "Passwords Must Match!", Toast.LENGTH_LONG).show();
                    return;
                }
                changePassword.setEnabled(false);

                StringRequest request = new StringRequest(1, ServerUrls.changeProfilePassword, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.contains("success")){
                            changePassword.setEnabled(true);
                            currentPasswordInput.setText("");
                            newPasswordInput.setText("");
                            repeatNewPasswordInput.setText("");
                            Toast.makeText(MainActivity.CurrentContext, response, Toast.LENGTH_SHORT).show();
                            UserLogin.UpdateAccount(UserLogin.CurrentLoginUsername, newPassword);
                        }else {
                            changePassword.setEnabled(true);
                            Toast.makeText(MainActivity.CurrentContext, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        changePassword.setEnabled(true);
                        Log.e("PasswordError", error.getMessage() + " ");
                        Toast.makeText(MainActivity.CurrentContext, "Error, Couldn't Change Password!", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("id", UserLogin.CurrentLoginID + "");
                        params.put("oldPassword", password);
                        params.put("newPassword", newPassword);
                        return params;
                    }
                };
                queue.add(request);

            }

        });

        profileImage = root.findViewById(R.id.userOldProfilePicture);
        changeImage = root.findViewById(R.id.userChangeProfileImage);

        changeImage.setOnClickListener(view -> {
            mGetContent.launch("image/*");
        });

        Button cancelManage = root.findViewById(R.id.userCancelManage);

        cancelManage.setOnClickListener(view -> {
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.setReorderingAllowed(true);
            ft.replace(R.id.nav_host_fragment_content_main, UserPanelFragment.class, null);
            ft.addToBackStack(UserPanelFragment.class.getName());
            ft.commit();
        });

        int currentID = UserLogin.CurrentLoginID;
        if(ImageCache.GetProfileImage(currentID) == null){
            ImageRequest request = new ImageRequest(ServerUrls.getUserImage(currentID), new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    ImageCache.SetProfileImage(currentID, response);
                    profileImage.setImageBitmap(response);
                    MainActivity.profileImage.setImageBitmap(response);
                    MainActivity.profileCard.setVisibility(View.VISIBLE);
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
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
        new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                try {
                    Bitmap image = MediaStore.Images.Media.getBitmap(MainActivity.CurrentContext.getContentResolver(), uri);//Retrieve the image from files

                    changeImage.setEnabled(false);

                    StringRequest request = new StringRequest(1, ServerUrls.changeProfileImage, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            changeImage.setEnabled(true);
                            ImageCache.SetProfileImage(UserLogin.CurrentLoginID, image);
                            profileImage.setImageBitmap(image);
                            MainActivity.profileImage.setImageBitmap(image);
                            MainActivity.profileCard.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.CurrentContext, response, Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            changeImage.setEnabled(true);
                            Log.e("ImageError", error.getMessage() + " ");
                            Toast.makeText(MainActivity.CurrentContext, "Error, Couldn't Change Profile Image!", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.JPEG,50,bos);
                            byte[] bb = bos.toByteArray();
                            String imager = Base64.encodeToString(bb, Base64.DEFAULT);
                            Map<String, String> params = new HashMap<>();
                            params.put("id", UserLogin.CurrentLoginID + "");
                            params.put("file", imager);
                            return params;
                        }
                    };
                    queue.add(request);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

}