package com.fashion.fashionmobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.helpers.ServerUrls;
import com.fashion.fashionmobile.helpers.UserLogin;
import com.fashion.fashionmobile.ui.cart.CartFragment;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class SummaryActivity extends AppCompatActivity {

    private RequestQueue queue;
    private LinearLayout linearLayout;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static TextView subTotal;
    private double subtotal = 0;
    private Button backBtn, nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        queue = Volley.newRequestQueue(this);
        linearLayout = findViewById(R.id.linearLayout);
        subTotal = findViewById(R.id.subtotal);
        backBtn = findViewById(R.id.back_btn);
        nextBtn = findViewById(R.id.next_btn);

        getSummary();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                ((Activity) SummaryActivity.this).overridePendingTransition(0, 0);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SummaryActivity.this, CheckoutActivity.class);
                startActivity(i);
                ((Activity) SummaryActivity.this).overridePendingTransition(0, 0);
            }
        });
    }

    public void getSummary() {
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getSummary(), new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject row = response.getJSONObject(i);
                        int quantity = row.getInt("quantity");
                        String prodName = row.getString("product_name");
                        double prodPrice = row.getDouble("price");
                        double discount = row.getDouble("discount");
                        String prodItemName = row.getString("product_item_name");
                        String extra = row.getString("data");

                        FlexboxLayout flex = new FlexboxLayout(SummaryActivity.this);
                        FlexboxLayout.LayoutParams flexLayout = new FlexboxLayout.LayoutParams(
                                FlexboxLayout.LayoutParams.MATCH_PARENT,
                                FlexboxLayout.LayoutParams.WRAP_CONTENT
                        );
                        flex.setJustifyContent(JustifyContent.SPACE_BETWEEN);
                        flex.setFlexDirection(FlexDirection.ROW);

                        TextView name = new TextView(SummaryActivity.this);
                        name.setText(prodName);
                        name.setTextColor(Color.BLACK);
                        Typeface typeface = getResources().getFont(R.font.inter_bold);
                        name.setTypeface(typeface);
                        name.setTextSize(18);

                        TextView price = new TextView(SummaryActivity.this);
                        double basePrice = prodPrice - discount;
                        double totalPrice = basePrice * quantity;
                        if(UserLogin.CurrentCurrency != "US, USD"){
                            price.setText("LBP " + String.format("%,d", (int) totalPrice));
                        } else {
                            price.setText("$" + df.format(totalPrice));
                        }
                        price.setTextColor(Color.BLACK);
                        Typeface typeface1 = getResources().getFont(R.font.inter_bold);
                        price.setTypeface(typeface1);
                        price.setTextSize(18);

                        flex.addView(name);
                        flex.addView(price);
                        linearLayout.addView(flex);

                        TextView details = new TextView(SummaryActivity.this);
                        details.setText("Qty: " + quantity + ", " + extra + ", (" + prodItemName + ")");
                        details.setTextSize(16);
                        Typeface typeface3 = getResources().getFont(R.font.inter_bold);
                        details.setTypeface(typeface3);

                        linearLayout.addView(details);

                        subtotal += totalPrice;
                        if(UserLogin.CurrentCurrency != "US, USD"){
                            subTotal.setText("LBP " + String.format("%,d", (int) subtotal));
                        } else {
                            subTotal.setText("$" + df.format(subtotal));
                        }
                    }
                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }
}