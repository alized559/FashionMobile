package com.fashion.fashionmobile.helpers;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class CategoryDataModel {

    private int id = 1;
    private String name;
    private int image = 0;
    private String color;

    public CategoryDataModel(int id, String name, int image, String color){
        this.id = id;
        this.name = name;
        this.image = image;
        this.color = color;
    }

    public int getImage(){
        return image;
    }

    public int getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
