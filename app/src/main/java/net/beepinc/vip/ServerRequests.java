package net.beepinc.vip;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by tayo on 8/23/2015.
 */
public class ServerRequests {

    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://gisanrinadetayo.comuf.com/";

    public ServerRequests(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Creating user account");
    }

    public void storeUserDataInBackground(User user, GetUserCallback callback) {
        progressDialog.show();
        new StoreUserDataAsyncTask(user, callback).execute();
    }

    public void fetchUserDataInBackground(User user, GetUserCallback callback) {
        progressDialog.show();
        new FetchUserDataAsyncTask(user, callback).execute();
    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {

        User user;
        GetUserCallback userCallback;

        public StoreUserDataAsyncTask(User user, GetUserCallback callback) {
            this.user = user;
            this.userCallback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> doTosend = new ArrayList<>();
            doTosend.add(new BasicNameValuePair("username", user.uname));
            doTosend.add(new BasicNameValuePair("mobile", user.mob));
            doTosend.add(new BasicNameValuePair("password", user.pass));
            doTosend.add(new BasicNameValuePair("category", user.cate));
            doTosend.add(new BasicNameValuePair("image", user.image));

            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);

            HttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "Register.php");

            try {
                post.setEntity(new UrlEncodedFormEntity(doTosend));
                httpClient.execute(post);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            userCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }


    public class FetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {

        User user;
        GetUserCallback userCallback;

        public FetchUserDataAsyncTask(User user, GetUserCallback callback) {
            this.user = user;
            this.userCallback = callback;
        }

        @Override
        protected User doInBackground(Void... params) {
            ArrayList<NameValuePair> doTosend = new ArrayList<>();
            doTosend.add(new BasicNameValuePair("username", user.uname));
            doTosend.add(new BasicNameValuePair("password", user.pass));

            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS+"FetchUserData.php");

            User user1 = null;

            try {
                post.setEntity(new UrlEncodedFormEntity(doTosend));
                HttpResponse response = client.execute(post);
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject object = new JSONObject(result);

                if (object.length() == 0){
                    user1 = null;
                }else {
                    String username = object.getString("username");
                    String mob = object.getString("mobile");
                    String password = object.getString("password");
                    String cate = object.getString("category");
                    String image = object.getString("image");

                    user1 = new User(0,username,mob,password, cate, image);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return user1;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            progressDialog.dismiss();
            userCallback.done(returnedUser);
            super.onPostExecute(returnedUser);
        }
    }
}


