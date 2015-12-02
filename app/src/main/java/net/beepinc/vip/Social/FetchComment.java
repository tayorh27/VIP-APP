package net.beepinc.vip.Social;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import net.beepinc.vip.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tayo on 10/25/2015.
 */
public class FetchComment {

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private JSONObject object = null;
    private String web_url = "http://www.gisanrinadetayo.comuf.com/php_fetch/FetchCommentData.php";



    public FetchComment() {
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    public int performFetchVolley(){
        final int[] volley_comment = {0};
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, web_url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int getC =0;//= performTotalNumberOfComments(response);
                if(getC > 0){
                    volley_comment[0] = getC;
                }else {
                    volley_comment[0] = -1;
                }
                //Toast.makeText(context, "getC = " + getC, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonArrayRequest);

        return volley_comment[0];
    }

    public ArrayList<String> performTotalNumberOfComments(JSONArray jsonArray, String get_current_post) {
        int total_comment = 0;
        String[] values;
        List<String> customPost = new ArrayList<>();
        ArrayList<String> getTotal = new ArrayList<>();

        if (jsonArray != null && jsonArray.length() > 0) {
            try {
                values = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject object = jsonArray.getJSONObject(i);
                    String getPost = object.getString("post");
                    customPost.add(getPost);
                }

                for(int k = 0; k < customPost.size(); k++){
                    if(customPost.get(k).contentEquals(get_current_post)){
                        getTotal.add(customPost.get(k));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return getTotal;
    }

    public void getAllComments(JSONArray jsonArray){
        String image,username, comment, time;

    }
}
