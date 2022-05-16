package com.fashion.fashionmobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.helpers.CategoryDataModel;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<CategoryDataModel> {

    private int resourceLayout;
    private ArrayList<CategoryDataModel> dataSet;
    private Context mContext;

    public CategoryAdapter(Context context, int resource, ArrayList<CategoryDataModel> data) {
        super(context, R.layout.category_list, data);
        this.resourceLayout = resource;
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        CategoryDataModel p = getItem(position);

        if (p != null) {
            ImageView categoryImage = v.findViewById(R.id.categoryImage);
            TextView categoryName = v.findViewById(R.id.categoryText);
            CardView card = v.findViewById(R.id.CategoryCard);

            if (categoryImage != null) {
                categoryImage.setImageResource(p.getImage());
            }
            if (categoryName != null) {
                categoryName.setText(p.getName());
            }
            if (card != null) {
                card.setCardBackgroundColor(Color.parseColor(p.getColor()));
            }
        }

        return v;
    }
}
