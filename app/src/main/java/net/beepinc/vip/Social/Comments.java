package net.beepinc.vip.Social;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import net.beepinc.vip.User;
import net.beepinc.vip.UserLocalStore;
import net.beepinc.vip.generalUsage.GlobalUse;
import net.beepinc.vip.network.VolleySingleton;
import net.beepinc.vip.taskComment.TaskLoadComments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tayo on 10/25/2015.
 */
public class Comments {

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private String post, image, mobile, username, comment;
    private static final String web_url = "http://www.gisanrinadetayo.comuf.com/php_files/upload_comment.php";
    private Context context;
    private EditText editText;
    private ProgressBar progressBar;
    Date date = new Date();
    private String currentDate = date.toLocaleString();
    private UserLocalStore userLocalStore;

    public Comments(Context context,EditText et,ProgressBar progressBar, String post, String img, String mob, String user, String comment){
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.post = post;
        this.image = img;
        this.mobile = mob;
        this.username = user;
        this.comment = comment;
        this.context = context;
        this.editText = et;
        this.progressBar = progressBar;
        userLocalStore = new UserLocalStore(context);
    }

    public void startTask(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                postComment();
            }
        }).start();
    }

    private void postComment(){

        final User user = userLocalStore.getLoggedUser();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,web_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Comment posted\nPlease swipe to refresh if comment not seen", Toast.LENGTH_LONG).show();
                editText.setText("");
                progressBar.setVisibility(View.GONE);
                updateVoiceNoteNumberofComments();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
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
                params.put("comment",comment);
                params.put("time",currentDate);
                params.put("user_id_fk",user.id+"");

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void updateVoiceNoteNumberofComments(){

        String url = "http://gisanrinadetayo.comuf.com/commentNlikes/fetchCommentNumber.php?notes="+post;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject object = response.getJSONObject(0);
                    int number_of_comments = object.getInt("number_of_comments");
                    int newNumberofcomments = number_of_comments +1;
                    updateVoicenoteCommentsTotal(newNumberofcomments);

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

    private void updateVoicenoteCommentsTotal(int newNumberofcomments) {
        String url = "http://gisanrinadetayo.comuf.com/commentNlikes/updateCommentNumber.php?notes="+post+"&number_of_comments="+newNumberofcomments;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int success = 0;
                try {
                    JSONObject object = new JSONObject(response);
                    success = object.getInt("success");
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
