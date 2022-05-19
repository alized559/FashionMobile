package com.fashion.fashionmobile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.helpers.ServerUrls;
import com.fashion.fashionmobile.helpers.UserLogin;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CheckoutInfoActivity extends AppCompatActivity {

    private String fullName, address, countryAddress, countryCode, mobileNumber;
    private TextView name, email, addressText, country, number;
    private Button back, submit;
    private RequestQueue queue;
    private int orderID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_info);

        queue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        fullName = intent.getStringExtra("fullname");
        address = intent.getStringExtra("address1");
        countryAddress = intent.getStringExtra("address2");
        countryCode = intent.getStringExtra("countryCode");
        mobileNumber = intent.getStringExtra("mobileNumber");

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        addressText = findViewById(R.id.address1Text);
        country = findViewById(R.id.address2Text);
        number = findViewById(R.id.number);
        back = findViewById(R.id.back_btn);
        submit = findViewById(R.id.next_btn);

        name.setText(fullName);
        email.setText(UserLogin.CurrentLoginEmail);
        addressText.setText(address);
        country.setText(countryAddress);
        number.setText("(+" + countryCode + ") " + mobileNumber);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                ((Activity) CheckoutInfoActivity.this).overridePendingTransition(0, 0);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeOrder(view);

            }
        });
    }

    public void placeOrder(View view) {
        StringRequest request = new StringRequest(Request.Method.POST, ServerUrls.placeOrder(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("PlaceOrderError", response);
                if (!response.contains("Error")) {
                    orderID = Integer.parseInt(response.trim());
                    Intent i = new Intent(CheckoutInfoActivity.this, ViewPlacedOrderActivity.class);
                    i.putExtra("orderID", orderID);
                    startActivity(i);
                }else {
                    Log.d("PlaceOrderError", response + " ");
                    Snackbar.make(view, "Failed Placing Order!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("PlaceOrderError", error.getMessage() + "");
                Snackbar.make(view, "Failed Placing Order!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("userID", UserLogin.CurrentLoginID + "");
                params.put("fullname", fullName);
                params.put("address", address);
                params.put("country", countryAddress);
                params.put("countryCode", countryCode);
                params.put("phoneNumber", mobileNumber);
                params.put("currency", UserLogin.CurrentCurrency);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(1000), //After the set time elapses the request will timeout
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
}