package net.beepinc.vip.extras;

import com.android.volley.RequestQueue;

import net.beepinc.vip.Information.favorites_information;
import net.beepinc.vip.MyApplication;
import net.beepinc.vip.json.Parser;
import net.beepinc.vip.json.Requestor;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by tayo on 10/4/2015.
 */
public class FavoritesUtils {

    private static final String  FAV_URL = "http://gisanrinadetayo.comuf.com/FetchUserDatas.php";

    public static ArrayList<favorites_information> loadFavoritesList(RequestQueue requestQueue) {

        JSONArray response = Requestor.sendRequestFavorites(requestQueue, FAV_URL);
        ArrayList<favorites_information> lists = Parser.parseJSONResponse(response);
        MyApplication.getWriteableDatabase().insertFavorites(lists, true);
        return lists;
    }
}
