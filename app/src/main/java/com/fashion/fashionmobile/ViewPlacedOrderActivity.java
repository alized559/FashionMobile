package com.fashion.fashionmobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.helpers.ServerUrls;
import com.fashion.fashionmobile.ui.userpanel.UserPanelFragment;

import org.json.JSONArray;
import org.json.JSONObject;

public class ViewPlacedOrderActivity extends AppCompatActivity {

    private TextView userId, fullName, deliverTo, phoneNumber, orderDate, deliveryState,
            assignedDriver, totalItemsText, totalPriceText, orderNum, itemsSummary;
    private Button backToPanel;
    private RequestQueue queue;
    private int orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_placed_order);

        Intent i = getIntent();
        orderID = i.getIntExtra("orderID", 0);

        queue = Volley.newRequestQueue(this);

        userId = findViewById(R.id.user_id);
        fullName = findViewById(R.id.fullname);
        deliverTo = findViewById(R.id.deliver_to);
        phoneNumber = findViewById(R.id.phone_number);
        orderDate = findViewById(R.id.order_date);
        deliveryState = findViewById(R.id.delivery_state);
        assignedDriver = findViewById(R.id.assigned_driver);
        totalItemsText = findViewById(R.id.total_items_text);
        backToPanel = findViewById(R.id.back_to_panel);
        orderNum = findViewById(R.id.order_num);
        itemsSummary = findViewById(R.id.items_summary);
        totalPriceText = findViewById(R.id.total_price_text);

        getOrderSummary();

        backToPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void getOrderSummary() {
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getOrderSummary(orderID), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject row = response.getJSONObject(0);
                    int user_id = row.getInt("user_id");
                    String fullname = row.getString("fullname");
                    String deliver_to = row.getString("address");
                    int phone_number = row.getInt("number");
                    int countryCode = row.getInt("countryCode");
                    String date = row.getString("date");
                    String state = row.getString("state");
                    String cartItems = row.getString("cart_items").replaceAll("<qm>", "\"")
                            .replaceAll("<ilb>", "<br>")
                            .replaceAll("viewProduct.php", "http://195.62.33.125/viewProduct.php");
                    String payment = row.getString("payment");

                    orderNum.setText("Order №" + orderID + " Summary");
                    userId.setText("User ID: " + String.valueOf(user_id));
                    fullName.setText("Fullname: " + fullname);
                    deliverTo.setText("Deliver To: " + deliver_to);
                    phoneNumber.setText("Phone Number: (+" + String.valueOf(countryCode + ") " + phone_number));
                    orderDate.setText("Order Date: " + date);
                    String s = "<font color='#FFD814'>" + state + "</font>";
                    deliveryState.setText(Html.fromHtml("Delivery State: " + s));
                    assignedDriver.setText("Assigned Driver: Not Yet Assigned");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        itemsSummary.setText(Html.fromHtml(cartItems, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        itemsSummary.setText(Html.fromHtml(cartItems));
                    }
                    itemsSummary.setClickable(true);
                    itemsSummary.setMovementMethod(LinkMovementMethod.getInstance());
                    totalItemsText.setText(String.valueOf(cartItems.split("<br>").length) + " Items");
                    totalPriceText.setText(payment);

                } catch (Exception e) {
                    Log.d("OrderError", e.getMessage() + "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewPlacedOrderActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
}