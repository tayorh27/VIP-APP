package net.beepinc.vip;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tayo on 8/29/2015.
 */
public class UserLoginCheck {
    String[] usernames, passwords;
    String result = "";
    public static final String SERVER_ADDRESS = "http://gisanrinadetayo.comuf.com/";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    Context context;
    ProgressDialog progressDialog;
    JSONParser parser = new JSONParser();

    public UserLoginCheck(Context context){
        this.context = context;
    }

    public void FetchMe(String get_username, String get_password){
        //new LogUserIn(get_username, get_password).execute();
    }



    public boolean Auth(String get_username, String get_password){
        InputStream isr = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(SERVER_ADDRESS+"FetchUserData.php");
            HttpResponse response = null;
            response = client.execute(post);
            HttpEntity entity = response.getEntity();
            isr = entity.getContent();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    isr, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null){
                sb.append(line+"\n");
            }
            isr.close();
            result = sb.toString();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
        }

        try {
            JSONArray array = new JSONArray(result);
            usernames = new String[array.length()];
            passwords = new String[array.length()];

            for (int i = 0; i < array.length(); i++){
                JSONObject object = array.getJSONObject(i);
                usernames[i] = object.getString("username");
                passwords[i] = object.getString("password");
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
        }

        try {
            for (int i = 0; i < usernames.length; i++) {
                if (usernames[i] == get_username && passwords[i] == get_password) {
                    return true;
                }
            }
        }catch (Exception e){
            Toast.makeText(context, e.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
        }

        return false;
    }
}
