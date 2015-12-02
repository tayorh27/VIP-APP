package net.beepinc.vip;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

/**
 * Created by tayo on 9/22/2015.
 */
public class GetApiConnector {

    public JSONArray GetAllUsers(){

        HttpEntity httpEntity = null;
        String url = "http://gisanrinadetayo.comuf.com/FetchUserData.php";

        try {

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = null;
            httpResponse = httpClient.execute(httpGet);

            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = null;

        if(httpEntity != null){
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                jsonArray = new JSONArray(entityResponse);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonArray;
    }
}
