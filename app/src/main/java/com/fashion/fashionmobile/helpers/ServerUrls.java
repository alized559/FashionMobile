package com.fashion.fashionmobile.helpers;

public class ServerUrls {
    private static String WebsiteUrl = "http://195.62.33.125/mobile/";

    public static String authenticate = WebsiteUrl + "authenticate.php";

    public static String getUserLikes() {
        return WebsiteUrl + "getLikes.php?userID=" + UserLogin.CurrentLoginID;
    }

    public static String getUserImage(int id) {
        return WebsiteUrl + "getUserImage.php?id=" + id;
    }


    public static String changeProfileImage = WebsiteUrl + "changeProfileImage.php";

    public static String changeProfilePassword = WebsiteUrl + "changeProfilePassword.php";

    public static String changeProfileFullname = WebsiteUrl + "changeProfileFullname.php";

    public static String getPopularProducts = WebsiteUrl + "getPopularProducts.php";

    public static String getProductImage(int id) {
        return WebsiteUrl + "getProductImage.php?id=" + id;
    }

}
