package com.fashion.fashionmobile.helpers;

import android.provider.ContactsContract;
import android.util.Log;

import java.util.HashMap;

public class ServerUrls {
    private static String WebsiteUrl = "http://195.62.33.125/mobile/";

    public static String authenticate = WebsiteUrl + "authenticate.php";

    public static String getUserLikes() {
        return WebsiteUrl + "getLikes.php?id=" + UserLogin.CurrentLoginID;
    }

    public static String getPreviousOrders(int limit) {
        return WebsiteUrl + "getPreviousOrders.php?id=" + UserLogin.CurrentLoginID + "&limit=" + limit;
    }

    public static String getPostedReviews(int limit) {
        return WebsiteUrl + "getPostedReviews.php?id=" + UserLogin.CurrentLoginID + "&limit=" + limit;
    }

    public static String getUserImage(int id) {
        return WebsiteUrl + "getUserImage.php?id=" + id;
    }

    public static String getProduct(int prod_id) {
        return WebsiteUrl + "getProduct.php?id=" + prod_id + "&currency=" + UserLogin.CurrentCurrency;
    }

    public static String getProductItems(int prod_id){
        return WebsiteUrl + "getProductItems.php?id=" + prod_id;
    }

    public static String getProductItemImage(int prod_id, int item_id){
        return WebsiteUrl + "getProductItemImage.php?id=" + prod_id + "&item=" + item_id;
    }

    public static String getProductReviews(int prod_id) {
        return WebsiteUrl + "getProductReviews.php?id=" + prod_id;
    }

    public static String addCartItem(int prod_id, int item, String extraName, String extraValue){
        return WebsiteUrl + "manageCart.php?userID=" + UserLogin.CurrentLoginID + "&id=" + prod_id + "&item=" + item + "&type=add&dataName=" + extraName + "&dataValue=" + extraValue;
    }

    public static String getCartItems() {
        return WebsiteUrl + "getCart.php?userID=" + UserLogin.CurrentLoginID + "&currency=" + UserLogin.CurrentCurrency;
    }

    public static String removeCartItem(int item){
        return WebsiteUrl + "manageCart.php?id=" + item + "&type=remove&userID=" + UserLogin.CurrentLoginID;
    }

    public static String editCartItem(int item, int amount){
        return WebsiteUrl + "manageCart.php?id=" + item + "&type=edit&userID=" + UserLogin.CurrentLoginID + "&amount=" + amount;
    }

    public static String removeAllCartItems(){
        return WebsiteUrl + "manageCart.php?userID=" + UserLogin.CurrentLoginID + "&type=removeall&id=0";
    }

    public static String updateReviews = WebsiteUrl + "updateReviews.php";

    public static String updateLikes = WebsiteUrl + "updateLikes.php";

    public static String changeProfileImage = WebsiteUrl + "changeProfileImage.php";

    public static String changeProfilePassword = WebsiteUrl + "changeProfilePassword.php";

    public static String changeProfileFullname = WebsiteUrl + "changeProfileFullname.php";

    public static String getPopularProducts = WebsiteUrl + "getPopularProducts.php";

    public static String getNewDrops = WebsiteUrl + "getNewDrops.php";

    public static String getProductImage(int id) {
        return WebsiteUrl + "getProductImage.php?id=" + id;
    }

    public static String getNewDropsImage(int id) {
        return WebsiteUrl + "getNewDropsImage.php?id=" + id;
    }

    public static String getAllProducts(HashMap<String, String> filter) {
        String current = WebsiteUrl + "getProducts.php?currency=" + UserLogin.CurrentCurrency;
        for(String key : filter.keySet()){
            String value = filter.get(key);
            current += "&" + key + "=" + value;
        }
        return current;
    }

    public static String getAllProducts() {
        return WebsiteUrl + "getProducts.php?currency=" + UserLogin.CurrentCurrency;
    }

<<<<<<< HEAD
    public static String getProductsFilter = WebsiteUrl + "getProductsFilter.php";

    public static String getSummary = WebsiteUrl + "getSummary.php?userID=" + UserLogin.CurrentLoginID;
=======
    public static String getAllBrands = WebsiteUrl + "getAllBrands.php";
>>>>>>> b7acbaa86fdc5e6668ad7a5e3abd3585e546138a
}
