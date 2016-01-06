package net.beepinc.vip.SocialExtras;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.Information.favorites_information;
import net.beepinc.vip.MyAdapters.favorites_adapters;
import net.beepinc.vip.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tayo on 1/6/2016.
 */
public class FetchLikeUsers {


    private String post;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    favorites_adapters adapters;
    TextView tv;
    ProgressBar progressBar;

    public FetchLikeUsers(String post,favorites_adapters adapters,TextView tv,ProgressBar progressBar){
        this.post = post;
        this.adapters = adapters;
        this.tv = tv;
        this.progressBar = progressBar;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    public void updateVoiceNoteNumberOfLikes(){

        String url = AppConfig.web_url+"commentNlikes/fetchCommentNumber.php?notes="+post;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject object = response.getJSONObject(0);
                    String likeUsers = object.getString("likeUsers");
                    ArrayList<favorites_information> customData = getEachUser(likeUsers);
                    adapters.setList(customData);

                    progressBar.setVisibility(View.GONE);
                    tv.setVisibility(View.GONE);
                    if(customData.isEmpty()){
                        tv.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    tv.setVisibility(View.VISIBLE);
                    tv.setText(error.getMessage());
                    progressBar.setVisibility(View.GONE);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private ArrayList<favorites_information> getEachUser(String allUsers){

        ArrayList<favorites_information> custom = new ArrayList<>();
        ArrayList<String> lu = new ArrayList<>();
        String[] gu = allUsers.split(",");
        for(int i = 0; i < gu.length; i++){
            lu.add(gu[i]);
        }

        for(int j = 0; j < lu.size(); j++){
            for(int k = 1; k < lu.size(); k++){
                if(lu.get(j).contentEquals(lu.get(k))){
                    lu.remove(k);
                }
            }
        }

        for(int l = 0; l < lu.size(); l++){
            favorites_information current = new favorites_information("0",lu.get(l),"","","");
            custom.add(current);
        }

     return custom;
    }
}
