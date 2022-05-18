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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.helpers.ServerUrls;
import com.fashion.fashionmobile.helpers.UserLogin;

import java.util.HashMap;
import java.util.Map;

public class CheckoutInfoActivity extends AppCompatActivity {

    private String fullName, address, countryAddress, countryCode, mobileNumber;
    private TextView name, email, addressText, country, number;
    private Button back, submit;
    private RequestQueue queue;

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
                placeOrder();
            }
        });
    }

    public void placeOrder() {
        StringRequest request = new StringRequest(Request.Method.POST, ServerUrls.placeOrder(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("wrong")) {
                    Toast.makeText(CheckoutInfoActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.getMessage() + " ");
                Toast.makeText(CheckoutInfoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("fullname", fullName);
                params.put("address", address);
                params.put("country", countryAddress);
                params.put("countryCode", countryCode);
                params.put("phoneNumber", mobileNumber);
                params.put("currency", UserLogin.CurrentCurrency);
                return params;
            }
        };
        queue.add(request);
    }
}