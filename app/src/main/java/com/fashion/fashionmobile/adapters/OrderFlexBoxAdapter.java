package com.fashion.fashionmobile.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.helpers.ServerUrls;
import com.fashion.fashionmobile.helpers.UserLogin;
import com.fashion.fashionmobile.ui.OrdersActivity;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class OrderFlexBoxAdapter extends RecyclerView.Adapter<OrderFlexBoxAdapter.ViewHolder>{

    Context context;
    ArrayList<OrderDataModel> arrayList = new ArrayList<OrderDataModel>();

    public OrderFlexBoxAdapter(Context context, ArrayList<OrderDataModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public OrderFlexBoxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderFlexBoxAdapter.ViewHolder holder, int position) {

        holder.setViewModel(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, fullname, country, address, phone, date, state, products, payment;
        CardView orderCardView;

        int currentOrderID = 0;

        public ViewHolder(View itemView) {
            super(itemView);
            orderCardView = itemView.findViewById(R.id.order_card_view);
            title = itemView.findViewById(R.id.order_title);
            fullname = itemView.findViewById(R.id.order_fullname_text);
            country = itemView.findViewById(R.id.order_country_text);
            address = itemView.findViewById(R.id.order_address_text);
            phone = itemView.findViewById(R.id.order_phone_text);
            date = itemView.findViewById(R.id.order_date_text);
            state = itemView.findViewById(R.id.order_state_text);
            products = itemView.findViewById(R.id.order_products_text);
            payment = itemView.findViewById(R.id.order_price_text);

            orderCardView.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setTitle("Update State");
                builder.setItems(OrdersActivity.allOrderFilters, (dialog, which) -> {
                    String stateText = OrdersActivity.allOrderFilters[which];

                    RequestQueue queue = Volley.newRequestQueue(itemView.getContext());
                    JsonObjectRequest request = new JsonObjectRequest(ServerUrls.editOrderState(currentOrderID, stateText), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getString("state").equalsIgnoreCase("SUCCESS")){

                                    Resources res = itemView.getContext().getResources();
                                    int stateColor = res.getColor(R.color.black);

                                    if(stateText.equalsIgnoreCase("Pending")){
                                        stateColor = res.getColor(R.color.state_pending);
                                    }else if(stateText.equalsIgnoreCase("Delivered")){
                                        stateColor = res.getColor(R.color.state_delivered);
                                    }else if(stateText.equalsIgnoreCase("On The Way")){
                                        stateColor = res.getColor(R.color.state_ontheway);
                                    }else if(stateText.equalsIgnoreCase("Returned")){
                                        stateColor = res.getColor(R.color.state_returned);
                                    }

                                    state.setText(stateText);
                                    state.setTextColor(stateColor);

                                    Snackbar.make(v, "Edited Order State Successfully", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                }else {
                                    Snackbar.make(v, "Failed To Edit State!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            } catch (Exception e) {
                                Log.d("UIError", e.getMessage() + "");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("OrderStateEditError", error.getMessage() + "");
                            Snackbar.make(v, "Failed To Edit State!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                    request.setRetryPolicy(new DefaultRetryPolicy(
                            (int) TimeUnit.SECONDS.toMillis(1000), //After the set time elapses the request will timeout
                            0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(request);

                });
                builder.create().show();
            });

        }

        public void setViewModel(OrderDataModel model){
            currentOrderID = model.OrderID;
            title.setText("Order № " + model.OrderID);
            fullname.setText(model.Fullname);
            country.setText(model.Country);
            address.setText(model.Address);
            phone.setText(model.PhoneNumber);
            date.setText(model.Date);

            Resources res = itemView.getContext().getResources();
            int stateColor = res.getColor(R.color.black);

            String stateText = model.State;

            if(stateText.equalsIgnoreCase("Pending")){
                stateColor = res.getColor(R.color.state_pending);
            }else if(stateText.equalsIgnoreCase("Delivered")){
                stateColor = res.getColor(R.color.state_delivered);
            }else if(stateText.equalsIgnoreCase("On The Way")){
                stateColor = res.getColor(R.color.state_ontheway);
            }else if(stateText.equalsIgnoreCase("Returned")){
                stateColor = res.getColor(R.color.state_returned);
            }

            state.setText(stateText);
            state.setTextColor(stateColor);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                products.setText(Html.fromHtml(model.Products, Html.FROM_HTML_MODE_COMPACT));
            } else {
                products.setText(Html.fromHtml(model.Products));
            }
            products.setClickable(true);
            products.setMovementMethod(LinkMovementMethod.getInstance());

            payment.setText(model.Payment);
        }
    }
}
