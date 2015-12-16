package net.beepinc.vip.Social;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.MyAdapters.recentposts_adapters;
import net.beepinc.vip.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tayo on 10/25/2015.
 */
public class FetchLike {

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    private JSONObject object = null;
    private String web_url = AppConfig.web_url+"php_fetch/FetchLikeData.php";
    //private recentposts_adapters.RecentAdapter


    public FetchLike() {
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    public int performFetchVolley(){
        final int[] volley_like = {0};
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, web_url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int getL = 0;//performTotalNumberOfLikes(response);
                if(getL > 0) {
                    volley_like[0] = getL;
                }else {
                    volley_like[0] = -1;
                }
                //Toast.makeText(context, "getL = "+getL, Toast.LENGTH_LONG).show();
                Log.e("Number of Likes got", String.valueOf(volley_like[0]));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

       requestQueue.add(jsonArrayRequest);
        return volley_like[0];
    }

    public ArrayList<String> performTotalNumberOfLikes(JSONArray jsonArray, String get_current_post) {
        int total_like = 0;
        String[] values;
        List<String> customPost = new ArrayList<>();
        List<String> customValue = new ArrayList<>();

        ArrayList<String> getTotal = new ArrayList<>();

        customPost.clear();
        customValue.clear();

        if (jsonArray != null && jsonArray.length() > 0) {
            try {

                values = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject object = jsonArray.getJSONObject(i);
                    String getPost = object.getString("post");
                    String get_value = object.getString("like_value");

                    customPost.add(getPost);
                    customValue.add(get_value);
                }

                for (int j = 0; j < customPost.size(); j++){
                    if(customPost.get(j).contentEquals(get_current_post)){
                        getTotal.add(customValue.get(j));
                    }
                }

//                for (int k = 0; k < values.length; k++){
//                    total_like += Integer.parseInt(values[k]);
//                    Log.e("Number of Likes", String.valueOf(total_like));
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return getTotal;
    }

    public String performFetchVolleyLiked(){

        final String[] getLiked = {""};
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, web_url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //getLiked[0] = getLikeStatus(response);
                //Log.e("Liked Value after volley = ", getLiked[0]+get_username);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonArrayRequest);
        return getLiked[0];
    }

    public String getLikeStatus(JSONArray jsonArray, String get_current_post, String get_username){

        String getValue = "";
        List<String> customPost = new ArrayList<>();
        List<String> customValue = new ArrayList<>();

        customPost.clear();
        customValue.clear();


        if (jsonArray != null && jsonArray.length() > 0) {
            try {

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject object = jsonArray.getJSONObject(i);
                    String getPost = object.getString("post");
                    String get_value = object.getString("like_value");

                    customPost.add(getPost);
                    customValue.add(get_value);
                }

                for(int j = 0; j < customPost.size(); j++){
                    if(customPost.get(j).contentEquals(get_current_post) && customValue.get(j).contentEquals("liked" + get_username)){
                        getValue = "liked";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        Log.e("Liked Value = ", getValue+get_username);
        return getValue;

    }
}
