package com.fashion.fashionmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.fashion.fashionmobile.R;

import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter {


    Context context;
    LayoutInflater inflater;
    ArrayList<String> items = new ArrayList<>();

    public SpinnerAdapter(Context context, ArrayList<String> items){
        this.context = context;
        this.items = items;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.product_extra_spinner_layout, null);
        TextView item = (TextView) view.findViewById(R.id.spinner_item_title);
        item.setText(items.get(i));
        return view;
    }
}
