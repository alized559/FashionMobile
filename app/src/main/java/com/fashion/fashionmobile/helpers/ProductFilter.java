package com.fashion.fashionmobile.helpers;

import java.util.HashMap;

public class ProductFilter {

    public static HashMap<String, String> productFilterHasMap = new HashMap<String, String>();


    public static void ResetFilters(){
        productFilterHasMap.put("gender", "none");
        productFilterHasMap.put("category", "none");
        productFilterHasMap.put("type", "none");
        productFilterHasMap.put("brand", "none");
        productFilterHasMap.put("price", "none");
        productFilterHasMap.put("sale", "none");
        productFilterHasMap.put("name", "none");
    }

    public static void UpdateFilter(String key, String value){
        productFilterHasMap.put(key, value);
    }

    public static void CreateFilter(){
        if(productFilterHasMap.isEmpty()){
            ResetFilters();
        }
    }

    public static String get(String key){
        return productFilterHasMap.get(key);
    }

}
