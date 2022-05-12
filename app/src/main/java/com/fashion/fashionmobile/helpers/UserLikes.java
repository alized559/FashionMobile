package com.fashion.fashionmobile.helpers;

import android.content.Context;

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

    public static void addLike(int RecipeID) {
        if (!likedProducts.contains(RecipeID)) {
            likedProducts.add(RecipeID);
        }
    }

    public static void removeLike(int RecipeID) {
        if (likedProducts.contains(RecipeID)) {
            likedProducts.remove((Object)RecipeID);
        }
    }

    public static boolean DoesLike(int RecipeID){
        return likedProducts.contains(RecipeID);
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
                        int id = row.getInt("recipeID");
                        if(!likedProducts.contains(id)){
                            likedProducts.add(id);
                        }
                    }

                } catch (Exception ex) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);

    }

    public static void ResetLikes(){
        likedProducts.clear();
    }
}
