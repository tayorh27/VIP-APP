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
import net.beepinc.vip.generalUsage.GlobalUse;
import net.beepinc.vip.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
        try {
            like_users = users.split(",");
            ArrayList<String> lu = new ArrayList<>();

            for (int j = 0; j < like_users.length; j++) {
                lu.add(like_users[j]);
            }


            for (int i = 0; i < like_users.length; i++) {
                if (like_users[i].contentEquals(username)) {
                    //like_users[i].replace(username,"");
                    lu.remove(i);
                }
            }

            for (int j = 0; j < lu.size(); j++) {
                newUsers += lu.get(j) + ",";
            }
        }catch (Exception e){
            e.printStackTrace();
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
                    if(number_of_likes >= 0) {
                        int newNumberoflikes = number_of_likes - 1;
                        updateVoicenoteCommentsTotal(newNumberoflikes);
                    }else {
                        textView.setTextColor(context.getResources().getColor(R.color.list_item_title));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GlobalUse.handleVolleyError(error,context);
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
                GlobalUse.handleVolleyError(error, context);
            }
        });

        requestQueue.add(stringRequest);


    }

    private void updateVoicenoteCommentsTotal(final int newNumberoflikes) {

        String url = AppConfig.web_url+"commentNlikes/updateLikeNumber.php?notes="+post+"&number_of_likes="+newNumberoflikes;

        final StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int success = 0;
                try {
                    JSONObject object = new JSONObject(response);
                    success = object.getInt("success");
                    if(success == 1){
                        String getCurrentText = textView.getText().toString();
                        int number = Integer.parseInt(getCurrentText.substring(0, getCurrentText.indexOf(" ")));
                        if(number > 0){
                            textView.setText(newNumberoflikes + "like(s)");
                        }
                        textView.setTextColor(context.getResources().getColor(R.color.list_item_title));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GlobalUse.handleVolleyError(error,context);
            }
        });
        requestQueue.add(stringRequest);
    }
}
