package net.beepinc.vip.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.Information.mypost_information;
import net.beepinc.vip.MyApplication;
import net.beepinc.vip.User;
import net.beepinc.vip.UserLocalStore;
import net.beepinc.vip.generalUsage.Uploadings;
import net.beepinc.vip.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tayo on 10/18/2015.
 */
public class MyPostCustomList {

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private Uploadings uploadings;

    private String caption, notes, image, mobile, username, time, up;
    private Context context;
    private String web_URL = AppConfig.web_url+"php_files/upload_voice_database.php";

    private JSONObject object;
    private String outputPath;
    Date date = new Date();
    String currentDate = date.toLocaleString();
    private UserLocalStore userLocalStore;

    public MyPostCustomList(Context context, String cap, String vn, String img, String mob, String usern,String mTime, String outPath, String upload) {
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.context = context;
        this.caption = cap;
        this.notes = vn;
        this.image = img;
        this.mobile = mob;
        this.username = usern;
        this.time = mTime;
        this.outputPath = outPath;
        this.up = upload;
        uploadings = new Uploadings();
        userLocalStore = new UserLocalStore(context);

    }



    public void startTask(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Upload_to_Database();
            }
        }).start();
    }


    public void doUploadFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int response = uploadings.uploadFile(context, outputPath);
                if (response == 200) {
                    UpdateCurrentVoiceNote();
                    Log.e("upload", "database updated");
                } else {

                }
            }
        }).start();
    }

    public void UpdateCurrentVoiceNote() {

        String web_url_uv = AppConfig.web_url+"update_files/updateVoicenote.php?notes="+notes+"&uploaded=full";

        StringRequest stringRequest = new StringRequest(web_url_uv, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int success = 0;
                try {
                    JSONObject object1 = new JSONObject(response);
                    success = object1.getInt("success");
                    if(success == 1){
                        int fk = MyApplication.getWriteableDatabaseForMyPosts().getLastId();
                        MyApplication.getWriteableDatabaseForMyPosts().updateDatabase(fk, "done_icon");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) ;
        requestQueue.add(stringRequest);

    }

    private void Upload_to_Database() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, web_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Done", "Posted to database " + username + " about to start upload " + response.toString());
                doUploadFile();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error in background", error.toString());
            }
        }) {

            @Override
            public String getPostBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("caption", caption);
                params.put("notes", notes);
                params.put("image", image);
                params.put("mobile", mobile);
                params.put("username", username);
                params.put("time",currentDate);
                params.put("uploaded", up);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void Upload_to_Database_repost() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, web_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Done", "Posted to database " + username + " about to start upload " + response.toString());
                Toast.makeText(context,"Voicenote posted", Toast.LENGTH_LONG).show();
                //User user = userLocalStore.getLoggedUser();

                ArrayList<mypost_information> customData = new ArrayList<>();
                int fk = MyApplication.getWriteableDatabaseForMyPosts().getLastId();
                int id = fk+1;
                mypost_information current1 = new mypost_information(id,caption, notes, image, mobile, username, "done_icon", currentDate,"partial");
                customData.add(current1);
                MyApplication.getWriteableDatabaseForMyPosts().insertMyPost(customData, false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error in background", error.toString());
            }
        }) {

            @Override
            public String getPostBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("caption", caption);
                params.put("notes", notes);
                params.put("image", image);
                params.put("mobile", mobile);
                params.put("username", username);
                params.put("time",currentDate);
                params.put("uploaded", up);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }




    public void doUploadFileForRetry(final int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int response = uploadings.uploadFile(context, outputPath);
                if (response == 200) {
                    UpdateCurrentVoiceNoteForRetry(id);
                    Log.e("upload", "database updated");
                } else {

                }
            }
        }).start();
    }

    private void UpdateCurrentVoiceNoteForRetry(final int id) {

        String web_url_uv = AppConfig.web_url+"update_files/updateVoicenote.php?notes="+notes+"&uploaded=full";

        StringRequest stringRequest = new StringRequest(web_url_uv, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int success = 0;
                try {
                    JSONObject object1 = new JSONObject(response);
                    success = object1.getInt("success");
                    if(success == 1){
                        //int fk = MyApplication.getWriteableDatabaseForMyPosts().getLastId();
                        MyApplication.getWriteableDatabaseForMyPosts().updateDatabase(id, "done_icon");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) ;
        requestQueue.add(stringRequest);

    }



}
