package net.beepinc.vip.json;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by tayo on 10/4/2015.
 */
public class Requestor {

    public static JSONArray sendRequestFavorites(RequestQueue requestQueue, String favUrl) {

        JSONObject object = null;
        JSONArray response = null;
        try {

            RequestFuture<JSONArray> requestFuture = RequestFuture.newFuture();


            JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, favUrl, object, requestFuture, requestFuture);

            requestQueue.add(arrayRequest);
            response = requestFuture.get(30000, TimeUnit.MILLISECONDS);

            //Log.e("Response from server = ", response.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return response;
    }
}
