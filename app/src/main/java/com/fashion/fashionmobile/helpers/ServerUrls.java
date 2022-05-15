package com.fashion.fashionmobile.helpers;

import android.provider.ContactsContract;

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
        return WebsiteUrl + "getPostedReviews.php?id=" + prod_id;
    }

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

    public static String getAllProducts = WebsiteUrl + "getProducts.php";
}
