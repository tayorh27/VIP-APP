package net.beepinc.vip.Async;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by tayo on 10/17/2015.
 */
public class UploadVoice extends AsyncTask<Void, Void, Void> {

    Context context;
    AlertDialog alertDialog;
    private String caption, notes, image, mobile, username;
    private String web_URL = "http://gisanrinadetayo.comuf.com/php_files/upload_voice_database.php";
    private int CONNECTION_TIMEOUT = 1000 * 15;
    public boolean isSent = false;

    public UploadVoice(Context context, String cap, String vn, String img, String mob, String usern) {
        this.context = context;
        this.caption = cap;
        this.notes = vn;
        this.image = img;
        this.mobile = mob;
        this.username = usern;
        alertDialog = new AlertDialog.Builder(context).create();
    }

    public UploadVoice() {

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... params) {

        ArrayList<NameValuePair> dataSend = new ArrayList<>();
        dataSend.add(new BasicNameValuePair("caption", caption));
        dataSend.add(new BasicNameValuePair("notes", notes));
        dataSend.add(new BasicNameValuePair("image", image));
        dataSend.add(new BasicNameValuePair("mobile", mobile));
        dataSend.add(new BasicNameValuePair("username", username));

        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(web_URL);


        try {
            post.setEntity(new UrlEncodedFormEntity(dataSend));
            client.execute(post);
            isSent = true;
            Log.e("uploading", "Success to database");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //alertDialog.dismiss();
        Log.e("Backgound Return = ", String.valueOf(isSent));
    }
}
