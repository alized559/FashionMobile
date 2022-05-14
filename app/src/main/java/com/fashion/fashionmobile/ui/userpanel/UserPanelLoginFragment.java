package com.fashion.fashionmobile.ui.userpanel;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.MainActivity;
import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.databinding.FragmentUserPanelBinding;
import com.fashion.fashionmobile.databinding.FragmentUserPanelLoginBinding;
import com.fashion.fashionmobile.helpers.ServerUrls;
import com.fashion.fashionmobile.helpers.UserLikes;
import com.fashion.fashionmobile.helpers.UserLogin;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class UserPanelLoginFragment extends Fragment {

    private FragmentUserPanelLoginBinding binding;

    boolean isLoggingIn = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUserPanelLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView titleText = root.findViewById(R.id.main_login_tv);

        TextView repeatPasswordTextView = root.findViewById(R.id.repeatPasswordTextView);

        EditText usernameText = root.findViewById(R.id.usernameInputText);

        TextView emailTextView = root.findViewById(R.id.textView2);
        EditText emailText = root.findViewById(R.id.emailInputText);

        TextView fullnameTextView = root.findViewById(R.id.textView3);
        EditText fullnameText = root.findViewById(R.id.fullnameInputText);

        EditText passwordText = root.findViewById(R.id.passwordInputText);

        EditText repeatPasswordText = root.findViewById(R.id.repeatPasswordInputText);

        Button loginButton = root.findViewById(R.id.loginButton);

        TextView errorMessage = root.findViewById(R.id.errorMessageTextView);
        TextView switchAccount = root.findViewById(R.id.switchAccountText);

        ScrollView mainScrollView = root.findViewById(R.id.main_user_login_scrollview);

        errorMessage.setVisibility(View.INVISIBLE);
        repeatPasswordText.setVisibility(View.GONE);
        repeatPasswordTextView.setVisibility(View.GONE);
        emailTextView.setVisibility(View.GONE);
        emailText.setVisibility(View.GONE);
        fullnameTextView.setVisibility(View.GONE);
        fullnameText.setVisibility(View.GONE);

        switchAccount.setOnClickListener(view -> {
            if (isLoggingIn) {
                titleText.setText("User Registration");
                isLoggingIn = false;
                repeatPasswordText.setVisibility(View.VISIBLE);
                repeatPasswordTextView.setVisibility(View.VISIBLE);
                emailTextView.setVisibility(View.VISIBLE);
                emailText.setVisibility(View.VISIBLE);
                fullnameTextView.setVisibility(View.VISIBLE);
                fullnameText.setVisibility(View.VISIBLE);
                switchAccount.setText("Already Have An Account? Login.");
                loginButton.setText("Register");
                loginButton.setEnabled(true);
            } else {
                titleText.setText("User Login");
                isLoggingIn = true;
                repeatPasswordText.setVisibility(View.GONE);
                repeatPasswordTextView.setVisibility(View.GONE);
                emailTextView.setVisibility(View.GONE);
                emailText.setVisibility(View.GONE);
                fullnameTextView.setVisibility(View.GONE);
                fullnameText.setVisibility(View.GONE);
                switchAccount.setText("Don't Have An Account? Register.");
                loginButton.setText("Login");
                loginButton.setEnabled(true);
            }
            errorMessage.clearAnimation();
            errorMessage.setVisibility(View.INVISIBLE);

        });

        Animation bounceAnim = AnimationUtils.loadAnimation(root.getContext(), R.anim.bounce2);

        loginButton.setOnClickListener(view2 -> {
            String username = usernameText.getText().toString();
            String password = passwordText.getText().toString();
            if (isLoggingIn) {
                if (username.length() < 4 || password.length() < 6) {
                    Toast.makeText(root.getContext(), "Input Fields Invalid Length (MIN 6)", Toast.LENGTH_LONG).show();
                } else {
                    errorMessage.clearAnimation();
                    errorMessage.setVisibility(View.INVISIBLE);
                    loginButton.setText("Please Wait");
                    loginButton.setEnabled(false);
                    RequestQueue queue = Volley.newRequestQueue(root.getContext());
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
                                    UserLikes.UpdateLikes(root.getContext());

                                    MainActivity.CartFab.setVisibility(View.VISIBLE);
                                    MainActivity.UserLikesImage.setVisibility(View.VISIBLE);
                                    MainActivity.UserLogoutImage.setVisibility(View.VISIBLE);

                                    //Move To Panel

                                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                                    ft.setReorderingAllowed(true);
                                    ft.replace(R.id.nav_host_fragment_content_main, UserPanelFragment.class, null);
                                    ft.addToBackStack(UserPanelFragment.class.getName());
                                    ft.commit();

                                }else {
                                    UserLogin.UpdateLoginState(false, -1, "Anonymous", "No Email Found", "No Full Name Found", "user");
                                    UserLikes.ResetLikes();
                                    loginButton.setText("Login");
                                    loginButton.setEnabled(true);
                                    errorMessage.setText(status);
                                    errorMessage.setVisibility(View.VISIBLE);
                                    errorMessage.startAnimation(bounceAnim);
                                }

                            }catch(Exception e){
                                Log.e("LoginException", e.getMessage() + "");
                                UserLogin.UpdateLoginState(false, -1, "Anonymous", "No Email Found", "No Full Name Found", "user");
                                UserLikes.ResetLikes();
                                loginButton.setText("Login");
                                loginButton.setEnabled(true);
                                errorMessage.setText("Fatal Exception Occurred!");
                                errorMessage.setVisibility(View.VISIBLE);
                                errorMessage.startAnimation(bounceAnim);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("LoginError", error.getMessage() + "");
                            UserLogin.UpdateLoginState(false, -1, "Anonymous", "No Email Found", "No Full Name Found", "user");
                            UserLikes.ResetLikes();
                            loginButton.setText("Login");
                            loginButton.setEnabled(true);
                            errorMessage.setText("Fatal Error Occurred!");
                            errorMessage.setVisibility(View.VISIBLE);
                            errorMessage.startAnimation(bounceAnim);
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
            } else {
                String email = emailText.getText().toString();
                String fullname = fullnameText.getText().toString();
                String repeatPassword = repeatPasswordText.getText().toString();
                if(username.length() < 4 || password.length() < 6 || email.length() < 6 || fullname.length() < 6 || repeatPassword.length() < 6){
                    Toast.makeText(root.getContext(), "Input Fields Invalid Length (MIN 6)", Toast.LENGTH_LONG).show();
                }else {
                    if(!password.equals(repeatPassword)){
                        Toast.makeText(root.getContext(), "Passwords Must Match!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    errorMessage.clearAnimation();
                    errorMessage.setVisibility(View.INVISIBLE);
                    loginButton.setText("Please Wait");
                    loginButton.setEnabled(false);
                    RequestQueue queue = Volley.newRequestQueue(root.getContext());
                    StringRequest request = new StringRequest (1, ServerUrls.authenticate, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonData = new JSONObject(response);
                                String status = jsonData.getString("state");
                                if(status.equalsIgnoreCase("success")){
                                    int id = jsonData.getInt("user_id");
                                    String username = jsonData.getString("username");
                                    String fullname = jsonData.getString("fullname");
                                    String email = jsonData.getString("email");
                                    String type = jsonData.getString("type");

                                    UserLogin.UpdateLoginState(true, id, username, email, fullname, type);
                                    UserLogin.UpdateAccount(username, password);
                                    UserLikes.UpdateLikes(root.getContext());

                                    MainActivity.CartFab.setVisibility(View.VISIBLE);
                                    MainActivity.UserLikesImage.setVisibility(View.VISIBLE);
                                    MainActivity.UserLogoutImage.setVisibility(View.VISIBLE);

                                    //Move To Panel

                                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                                    ft.setReorderingAllowed(true);
                                    ft.replace(R.id.nav_host_fragment_content_main, UserPanelFragment.class, null);
                                    ft.addToBackStack(UserPanelFragment.class.getName());
                                    ft.commit();

                                }else {
                                    UserLogin.UpdateLoginState(false, -1, "Anonymous", "No Email Found", "No Full Name Found", "user");
                                    UserLikes.ResetLikes();
                                    loginButton.setText("Register");
                                    loginButton.setEnabled(true);
                                    errorMessage.setText(status);
                                    errorMessage.setVisibility(View.VISIBLE);
                                    errorMessage.startAnimation(bounceAnim);
                                    mainScrollView.fullScroll(ScrollView.FOCUS_UP);
                                }

                            }catch(Exception e){
                                Log.e("RegisterException", e.getMessage() + "");
                                UserLogin.UpdateLoginState(false, -1, "Anonymous", "No Email Found", "No Full Name Found", "user");
                                UserLikes.ResetLikes();
                                loginButton.setText("Register");
                                loginButton.setEnabled(true);
                                errorMessage.setText("Fatal Exception Occurred!");
                                errorMessage.setVisibility(View.VISIBLE);
                                errorMessage.startAnimation(bounceAnim);
                                mainScrollView.fullScroll(ScrollView.FOCUS_UP);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("RegisterError", error.getMessage() + "");
                            UserLogin.UpdateLoginState(false, -1, "Anonymous", "No Email Found", "No Full Name Found", "user");
                            UserLikes.ResetLikes();
                            loginButton.setText("Register");
                            loginButton.setEnabled(true);
                            errorMessage.setText("Fatal Error Occurred!");
                            errorMessage.setVisibility(View.VISIBLE);
                            errorMessage.startAnimation(bounceAnim);
                            mainScrollView.fullScroll(ScrollView.FOCUS_UP);
                        }
                    }) {
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("username", username);
                            params.put("email", email);
                            params.put("fullname", fullname);
                            params.put("password", password);
                            params.put("login", "false");
                            return params;
                        }
                    };
                    queue.add(request);
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}