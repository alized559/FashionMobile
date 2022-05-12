package com.fashion.fashionmobile.helpers;

public class ServerUrls {
    private static String WebsiteUrl = "http://195.62.33.125/mobile/";

    public static String authenticate = WebsiteUrl + "authenticate.php";

    public static String getUserLikes() {
        return WebsiteUrl + "getLikes.php?userID=" + UserLogin.CurrentLoginID;
    }
}
