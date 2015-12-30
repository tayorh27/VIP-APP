package net.beepinc.vip.Social;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.R;
import net.beepinc.vip.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tayo on 12/25/2015.
 */
public class unLikeAndDeduct {

    private String post,username;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private String[] like_users;
    private TextView textView;
    private Context context;
    public unLikeAndDeduct(Context context,String post,String username,TextView textView){
        this.context = context;
        this.post = post;
        this.username = username;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.textView = textView;
    }

    public String removeLikeUser(String users){
        String newUsers="";
        like_users = users.split(",");
        for(int i = 0; i < like_users.length; i++){
            if(like_users[i].contentEquals(username)){
                like_users[i].replace(username,"");
            }
        }

        for(int j = 0; j < like_users.length; j++){
            newUsers += like_users[j]+",";
        }
        return newUsers;
    }

    public void startTask(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateVoiceNoteNumberOfLikes();
            }
        }).start();
    }


    private void updateVoiceNoteNumberOfLikes(){

        String url = AppConfig.web_url+"commentNlikes/fetchCommentNumber.php?notes="+post;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject object = response.getJSONObject(0);
                    String likeUsers = object.getString("likeUsers");
                    updateVoiceNoteLikeUsers(removeLikeUser(likeUsers));

                    int number_of_likes = object.getInt("number_of_likes");
                    int newNumberoflikes = number_of_likes -1;
                    updateVoicenoteCommentsTotal(newNumberoflikes);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private void updateVoiceNoteLikeUsers(String newLikeUsers) {

        String url = AppConfig.web_url+"commentNlikes/updateLikeUser.php?notes="+post+"&likeUsers="+newLikeUsers;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("LikeUser", "success");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(stringRequest);


    }

    private void updateVoicenoteCommentsTotal(int newNumberoflikes) {

        String url = AppConfig.web_url+"commentNlikes/updateLikeNumber.php?notes="+post+"&number_of_likes="+newNumberoflikes;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int success = 0;
                try {
                    JSONObject object = new JSONObject(response);
                    success = object.getInt("success");
                    if(success == 1){
                        textView.setTextColor(context.getResources().getColor(R.color.list_item_title));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }
}