package com.fashion.fashionmobile.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.MainActivity;
import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.ui.userpanel.UserPanelFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserLogin {
    static SharedPreferences prefs = null;

    public static boolean isLoggedIn = false;
    public static String CurrentLoginUsername = "Anonymous";
    public static String CurrentLoginEmail = "No Email Found";
    public static String CurrentLoginFullName = "No Full Name Found";
    public static String CurrentLoginType = "user";
    public static String CurrentCurrency = "US, USD";
    public static int CurrentLoginID = -1;

    public static void AttemptAutoLogin(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences("user_settings", context.MODE_PRIVATE);
        }
        String currency = prefs.getString("currency", null);
        if(currency != null && !currency.isEmpty()){
            CurrentCurrency = currency;
        }
        if (!isLoggedIn) {

            String username = prefs.getString("username", null);
            String password = prefs.getString("password", null);

            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                //Login Failed
                MainActivity.profileImage.setImageDrawable(null);
                MainActivity.profileCard.setVisibility(View.INVISIBLE);
                MainActivity.profileName.setText("");
                MainActivity.profileEmail.setText("");
            } else {
                SendLoginRequest(username, password, context);
            }
        }
    }

    public static void SendLoginRequest(String username, String password, Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest (1, ServerUrls.authenticate, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("state");
                    if(status.equalsIgnoreCase("success")){
                        JSONObject jsonData = json.getJSONObject("0");
                        int id = jsonData.getInt("user_id");
                        String username = jsonData.getString("username");
                        String fullname = jsonData.getString("fullname");
                        String email = jsonData.getString("email");
                        String type = jsonData.getString("type");

                        UserLogin.UpdateLoginState(true, id, username, email, fullname, type);
                        UserLogin.UpdateAccount(username, password);
                        UserLikes.UpdateLikes(context);

                        Toast.makeText(context, "Logged In Successfully!", Toast.LENGTH_SHORT).show();

                        MainActivity.profileName.setText(UserLogin.CurrentLoginFullName);
                        MainActivity.profileEmail.setText(UserLogin.CurrentLoginEmail);
                        MainActivity.profileCard.setVisibility(View.INVISIBLE);

                        if(type.equalsIgnoreCase("admin") || type.equalsIgnoreCase("driver")) {
                            MainActivity.UserOrdersImage.setVisibility(View.VISIBLE);
                        }
                        MainActivity.UserLogoutImage.setVisibility(View.VISIBLE);

                        int currentID = UserLogin.CurrentLoginID;
                        if(ImageCache.GetProfileImage(currentID) == null){
                            ImageRequest request = new ImageRequest(ServerUrls.getUserImage(currentID), new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    ImageCache.SetProfileImage(currentID, response);
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
                            MainActivity.profileImage.setImageBitmap(userimage);
                            MainActivity.profileCard.setVisibility(View.VISIBLE);
                        }


                    }else {
                        UserLogin.UpdateLoginState(false, -1, "Anonymous", "No Email Found", "No Full Name Found", "user");
                        UserLikes.ResetLikes();
                        MainActivity.profileImage.setImageDrawable(null);
                        MainActivity.profileCard.setVisibility(View.INVISIBLE);
                        MainActivity.profileName.setText("");
                        MainActivity.profileEmail.setText("");
                    }

                }catch(Exception e){
                    Log.e("LoginException", e.getMessage() + "");
                    UserLogin.UpdateLoginState(false, -1, "Anonymous", "No Email Found", "No Full Name Found", "user");
                    UserLikes.ResetLikes();
                    MainActivity.profileImage.setImageDrawable(null);
                    MainActivity.profileCard.setVisibility(View.INVISIBLE);
                    MainActivity.profileName.setText("");
                    MainActivity.profileEmail.setText("");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LoginError", error.getMessage() + "");
                SendLoginRequest(username, password, context);
                UserLogin.UpdateLoginState(false, -1, "Anonymous", "No Email Found", "No Full Name Found", "user");
                UserLikes.ResetLikes();
                MainActivity.profileImage.setImageDrawable(null);
                MainActivity.profileImage.setVisibility(View.INVISIBLE);
                MainActivity.profileName.setText("");
                MainActivity.profileEmail.setText("");
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("login", "true");
                return params;
            }
        };
        queue.add(request);
    }

    public static void UpdateLoginState(boolean loggedIn, int loginID, String username, String email, String fullname, String type){
        isLoggedIn = loggedIn;
        CurrentLoginUsername = username;
        CurrentLoginID = loginID;
        CurrentLoginFullName = fullname;
        CurrentLoginEmail = email;
        CurrentLoginType = type;
    }

    public static void UpdateAccount(String username, String password) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.putString("password", password);

        editor.commit();//Apply changes
    }

    public static void UpdateCurrency(String currency) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("currency", currency);
        CurrentCurrency = currency;
        editor.commit();//Apply changes
    }

    public static void Logout() {
        UserLikes.ResetLikes();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", null);
        editor.putString("password", null);
        UpdateLoginState(false, -1, "Anonymous", "No Email Found", "No Full Name Found", "user");
        editor.commit();//Apply changes
        MainActivity.profileImage.setImageDrawable(null);
        MainActivity.profileCard.setVisibility(View.INVISIBLE);
        MainActivity.profileName.setText("");
        MainActivity.profileEmail.setText("");
        MainActivity.UserOrdersImage.setVisibility(View.GONE);
        MainActivity.UserLogoutImage.setVisibility(View.GONE);
    }
}
