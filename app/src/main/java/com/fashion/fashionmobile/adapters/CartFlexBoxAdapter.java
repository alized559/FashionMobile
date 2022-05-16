package com.fashion.fashionmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fashion.fashionmobile.R;

import java.util.ArrayList;

public class CartFlexBoxAdapter extends RecyclerView.Adapter<CartFlexBoxAdapter.ViewHolder>{

    Context context;
    ArrayList<CartItemDataModel> arrayList = new ArrayList<CartItemDataModel>();

    public CartFlexBoxAdapter(Context context, ArrayList<CartItemDataModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public CartFlexBoxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartFlexBoxAdapter.ViewHolder holder, int position) {

        holder.setViewModel(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView picture, remove;
        TextView title, subtitle, extra, amount, price;
        CardView plus, minus;

        public ViewHolder(View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.cart_item_image);
            title = itemView.findViewById(R.id.cart_item_title);
            subtitle = itemView.findViewById(R.id.cart_item_subtitle);
            extra = itemView.findViewById(R.id.cart_item_extra);
            amount = itemView.findViewById(R.id.cart_item_count);
            price = itemView.findViewById(R.id.cart_item_price);
            remove = itemView.findViewById(R.id.cart_item_remove);
            plus = itemView.findViewById(R.id.cart_item_plus);
            minus = itemView.findViewById(R.id.cart_item_minus);
        }

        public void setViewModel(CartItemDataModel model){
            picture.setImageBitmap(model.Picture);
            title.setText(model.Title);
            subtitle.setText(model.Subtitle);
            extra.setText(model.Extra);
            amount.setText(model.Amount + "");
            price.setText(model.Price);

            model.PriceTextView = price;
            model.AmountTextView = amount;

            plus.setOnClickListener(model.onPlusClicked);
            minus.setOnClickListener(model.onMinusClicked);
            remove.setOnClickListener(model.onRemoveClicked);
        }
    }
}
