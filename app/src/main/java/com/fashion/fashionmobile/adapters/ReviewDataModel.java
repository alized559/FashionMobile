package com.fashion.fashionmobile.adapters;

import android.graphics.Bitmap;
import android.view.View;

public class ReviewDataModel {

    public int ReviewID = -1;
    public int ProductID = -1;
    public String Title = "";
    public String Subtitle = "";
    public double Ratings = 0;
    public int UserID = -1;
    public Bitmap Picture;
    public boolean CanDelete = false;
    public boolean CanVisitProduct = false;
    public View.OnClickListener onDeleteClicked = null;
    public View.OnClickListener onCardClicked = null;



}
