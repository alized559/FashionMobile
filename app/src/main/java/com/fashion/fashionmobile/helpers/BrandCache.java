package com.fashion.fashionmobile.helpers;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BrandCache {

    public static ArrayList<String> brands = new ArrayList<>();

    public static void AddBrand(String brand){
        if(!brands.contains(brand)){
            brands.add(brand);
        }
    }

    public static void ClearBrands(){
        brands.clear();
    }

}
