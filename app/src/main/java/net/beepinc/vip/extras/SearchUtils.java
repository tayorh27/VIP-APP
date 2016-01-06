package net.beepinc.vip.extras;

import com.android.volley.RequestQueue;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.Information.favorites_information;
import net.beepinc.vip.MyApplication;
import net.beepinc.vip.json.Parser;
import net.beepinc.vip.json.Requestor;
import net.beepinc.vip.json.SearchParser;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by tayo on 1/5/2016.
 */
public class SearchUtils {

    private static final String  SEARCH_URL = AppConfig.web_url+"FetchUserDatas.php";

    public static ArrayList<favorites_information> loadSearchList(RequestQueue requestQueue,String username) {

        JSONArray response = Requestor.sendRequestFavorites(requestQueue, SEARCH_URL);
        ArrayList<favorites_information> lists = SearchParser.parseJSONResponse(response,username);
        return lists;
    }
}
