package net.beepinc.vip.services;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.Information.favorites_information;
import net.beepinc.vip.Information.mypost_information;
import net.beepinc.vip.MyApplication;
import net.beepinc.vip.callback.FavoritesLoadedListener;
import net.beepinc.vip.callback.MyPostsLoadedListener;
import net.beepinc.vip.network.VolleySingleton;
import net.beepinc.vip.task.TaskLoadMyPosts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

/**
 * Created by tayo on 10/3/2015.
 */
public class MyService extends JobService implements MyPostsLoadedListener {
    private JobParameters jobParameters;
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.jobParameters = jobParameters;
        //new TaskLoadFavorites(this).execute();
        new TaskLoadMyPosts(this).execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }


//    public void onFavoritesLoadedListener(ArrayList<favorites_information> list) {
//        jobFinished(jobParameters, false);
//    }

    @Override
    public void onMyPostsLoadedListener(ArrayList<mypost_information> list) {
        jobFinished(jobParameters, false);
    }

    public static class MyTask extends AsyncTask<JobParameters, Void, JobParameters> {

        private static final String URL_FAV = "http://gisanrinadetayo.comuf.com/FetchUserData.php";
        MyService myservice;
        Cursor cursor;
        List<String> c_names = new ArrayList<>();
        List<String> c_phones = new ArrayList<>();
        private JSONObject object = null;
        private VolleySingleton volleySingleton;
        private RequestQueue requestQueue;
        Context context;

        MyTask(MyService myservice) {
            this.myservice = myservice;
            volleySingleton = VolleySingleton.getInstance();
            requestQueue = volleySingleton.getRequestQueue();
            context = MyApplication.getAppContext();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JobParameters doInBackground(JobParameters... params) {
            JSONArray response = sendJsonRequest();
            ArrayList<favorites_information> lists = parseJsonResponse(response);
            MyApplication.getWriteableDatabase().insertFavorites(lists, true);
            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            myservice.jobFinished(jobParameters, false);
        }

        public void performContactTask() {

            cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext()) {
                String contact_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String contact_phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                c_names.add(contact_name);
                c_phones.add(contact_phone);
            }

            cursor.close();
        }

        private JSONArray sendJsonRequest() {
            JSONArray response = null;
            try {

                RequestFuture<JSONArray> requestFuture = RequestFuture.newFuture();

                JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, URL_FAV, object, requestFuture, requestFuture);

                requestQueue.add(arrayRequest);
                response = requestFuture.get(30000, TimeUnit.MILLISECONDS);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return response;


        }

        public ArrayList<favorites_information> parseJsonResponse(JSONArray jsonArray) {
            ArrayList<favorites_information> customData = new ArrayList<>();
            if (jsonArray == null || jsonArray.length() == 0) {
                return null;
            }

            String serverUrl = AppConfig.web_url+"images/";

            performContactTask();
            List<String> customUsername = new ArrayList<>();
            List<String> customMobile = new ArrayList<>();
            List<String> customImage = new ArrayList<>();
            List<String> customCategory = new ArrayList<>();
            List<String> customId = new ArrayList<>();

            String[] uses, mobs, imgs, cats, ids;

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject json = null;


                try {
                    json = jsonArray.getJSONObject(i);

                    customId.add(json.getString("id"));
                    customUsername.add(json.getString("username"));
                    customMobile.add(json.getString("mobile"));
                    customImage.add(json.getString("image"));
                    customCategory.add(json.getString("category"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            uses = new String[jsonArray.length()];
            mobs = new String[jsonArray.length()];
            imgs = new String[jsonArray.length()];
            cats = new String[jsonArray.length()];
            ids = new String[jsonArray.length()];

            for (int j = 0; j < customUsername.size(); j++) {

                uses[j] = customUsername.get(j);
                mobs[j] = customMobile.get(j);
                imgs[j] = customImage.get(j);
                cats[j] = customCategory.get(j);
                ids[j] = customId.get(j);
            }

            for (int i = 0; i < mobs.length; i++) {
                for (int j = 0; j < c_phones.size(); j++) {
                    if (mobs[i].contains(c_phones.get(j))) {

                        String get_id = ids[i];
                        String get_username = uses[i];
                        String get_mobile = mobs[i];
                        String get_image = serverUrl + imgs[i];
                        String get_category = cats[i];

                        favorites_information current = new favorites_information(get_id, get_username, get_mobile, get_category, get_image);
                        customData.add(current);


                    }
                }

            }

            return customData;

        }
    }

}
