package com.fashion.fashionmobile.adapters;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

public class CartItemDataModel {

    public int ItemID = 0;
    public int Amount = 0;
    public int MaxAmount = 0;
    public String Title = "";
    public String Subtitle = "";
    public String Extra = "";
    public String Price = "";
    public Bitmap Picture;
    public View.OnClickListener onRemoveClicked = null;
    public View.OnClickListener onPlusClicked = null;
    public View.OnClickListener onMinusClicked = null;

    public TextView AmountTextView = null, PriceTextView = null;

    public double CurrentPrice = 0;

}
