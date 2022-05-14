package com.fashion.fashionmobile.helpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserLikes {
    private static ArrayList<Integer> likedProducts = new ArrayList<Integer>();

    public static void addLike(int ProductID) {
        if (!likedProducts.contains(ProductID)) {
            likedProducts.add(ProductID);
        }
    }

    public static void removeLike(int ProductID) {
        if (likedProducts.contains(ProductID)) {
            likedProducts.remove((Object)ProductID);
        }
    }

    public static ArrayList<Integer> GetAllLikes(){
        return likedProducts;
    }

    public static boolean DoesLike(int ProductID){
        return likedProducts.contains(ProductID);
    }

    public static void UpdateLikes(Context context) {
        likedProducts.clear();
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest request = new JsonArrayRequest(ServerUrls.getUserLikes(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject row = response.getJSONObject(i);
                        int id = row.getInt("prod_id");
                        if(!likedProducts.contains(id)){
                            likedProducts.add(id);
                        }
                    }
                    Log.d("UpdateLikes", "Received Total Likes " + likedProducts.size());

                } catch (Exception ex) {
                    Log.d("LikesException", ex.getMessage() + "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("LikesError", error.getMessage() + "");
                UpdateLikes(context);
            }
        });
        queue.add(request);

    }

    public static void ResetLikes(){
        likedProducts.clear();
    }
}
