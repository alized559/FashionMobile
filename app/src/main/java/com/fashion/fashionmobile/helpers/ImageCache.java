package com.fashion.fashionmobile.helpers;

import android.graphics.Bitmap;

import java.util.HashMap;

public class ImageCache {

    static HashMap<Integer, Bitmap> userProfileCache = new HashMap<>();

    static HashMap<Integer, Bitmap> productImageCache = new HashMap<>();

    static HashMap<Integer, Bitmap> productItemImageCache = new HashMap<>();

    static HashMap<Integer, Bitmap> newDropProductImageCache = new HashMap<>();

    public static void SetProfileImage(int id, Bitmap image){
        userProfileCache.put(id, image);
    }

    public static Bitmap GetProfileImage(int id) {
        if (userProfileCache.containsKey(id)){
            return userProfileCache.get(id);
        }
        return null;
    }

    public static void SetProductImage(int id, Bitmap image){
        productImageCache.put(id, image);
    }

    public static Bitmap GetProductImage(int id) {
        if (productImageCache.containsKey(id)){
            return productImageCache.get(id);
        }
        return null;
    }

    public static void SetProductItemImage(int id, Bitmap image){
        productItemImageCache.put(id, image);
    }

    public static Bitmap GetProductItemImage(int id) {
        if (productItemImageCache.containsKey(id)){
            return productItemImageCache.get(id);
        }
        return null;
    }

    public static void SetNewDropProductImageCache(int id, Bitmap image){
        newDropProductImageCache.put(id, image);
    }

    public static Bitmap GetNewDropProductImageCache(int id) {
        if (newDropProductImageCache.containsKey(id)){
            return newDropProductImageCache.get(id);
        }
        return null;
    }

}
