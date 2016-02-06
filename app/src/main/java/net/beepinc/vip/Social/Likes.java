package net.beepinc.vip.Social;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tayo on 10/25/2015.
 */
public class Likes {

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private String post, image, mobile, username, like_status, like_value;
    private static final String web_url = AppConfig.web_url+"php_files/upload_like.php";
    private Context context;
    private TextView textView;

    public Likes(Context context,TextView tv, String post, String img, String mob, String user, String ls, String lv){
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.post = post;
        this.image = img;
        this.mobile = mob;
        this.username = user;
        this.like_status = ls;
        this.like_value = lv;
        this.context = context;
        this.textView = tv;
    }

    public void startTask(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                postLike();
            }
        }).start();
    }

    private void postLike(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, web_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                textView.setTextColor(context.getResources().getColor(R.color.like));
                updateVoiceNoteNumberofLikes();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GlobalUse.handleVolleyError(error, context);
            }
        }){
            @Override
            public String getPostBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("post",post);
                params.put("image",image);
                params.put("mobile",mobile);
                params.put("username",username);
                params.put("like_status",like_status);
                params.put("like_value",like_value);

                return params;
            }
        };

        requestQueue.add(stringRequest);

    }

    private void updateVoiceNoteNumberofLikes(){

        String url = AppConfig.web_url+"commentNlikes/fetchCommentNumber.php?notes="+post;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject object = response.getJSONObject(0);
                    String likeUsers = object.getString("likeUsers");
                    String newLikeUsers = String.valueOf(new StringBuilder().append(likeUsers+username+","));
                    updateVoiceNoteLikeUsers(newLikeUsers);


                    int number_of_likes = object.getInt("number_of_likes");
                    int newNumberoflikes = number_of_likes +1;
                    updateVoicenoteCommentsTotal(newNumberoflikes);

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
                GlobalUse.handleVolleyError(error,context);
            }
        });

        requestQueue.add(stringRequest);


    }

    private void updateVoicenoteCommentsTotal(final int newNumberoflikes) {

        String url = AppConfig.web_url+"commentNlikes/updateLikeNumber.php?notes="+post+"&number_of_likes="+newNumberoflikes;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int success = 0;
                try {
                    JSONObject object = new JSONObject(response);
                    success = object.getInt("success");
                    if(success == 1){
                        String getCurrentText = textView.getText().toString();
                        int number = Integer.parseInt(getCurrentText.substring(0, getCurrentText.indexOf(" ")));
                        textView.setText(newNumberoflikes + "like(s)");
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
