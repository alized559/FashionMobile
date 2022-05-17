package com.fashion.fashionmobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
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
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONObject;

public class SummaryActivity extends AppCompatActivity {

    private RequestQueue queue;
    private FlexboxLayout flex1, flex2;
    private TextView summaryDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        queue = Volley.newRequestQueue(this);
        flex1 = findViewById(R.id.flexLayout);
        summaryDetails = findViewById(R.id.summary_details);

        getSummary();
    }

    public void getSummary() {
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getSummary, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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
                        int totalQuantity = row.getInt("total_quantity");
                        String extra = row.getString("data");

                        TextView name = new TextView(SummaryActivity.this);
                        name.setText(prodName);
                        name.setTextColor(Color.BLACK);
                        Typeface typeface = getResources().getFont(R.font.inter_bold);
                        name.setTypeface(typeface);
                        name.setTextSize(18);

                        TextView price = new TextView(SummaryActivity.this);
                        price.setText(String.valueOf(prodPrice));
                        price.setTextColor(Color.BLACK);
                        Typeface typeface1 = getResources().getFont(R.font.inter_bold);
                        price.setTypeface(typeface1);
                        price.setTextSize(18);

                        flex1.addView(name);
                        flex1.addView(price);

                        summaryDetails.setText("Qty: " + quantity + ", " + extra + ", (" + prodItemName + ")");
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