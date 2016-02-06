package net.beepinc.vip.searchResult;

import com.android.volley.RequestQueue;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.Information.recent_post_information;
import net.beepinc.vip.recent_json.Requestor;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by tayo on 1/18/2016.
 */
public class SearchResultUtils {

    public static String current_user;

    public SearchResultUtils(String current_user){
        this.current_user = current_user;
    }

    public static final String web_url = AppConfig.web_url+"php_fetch/FetchUserVoicenotes.php?username="+current_user;



    public ArrayList<recent_post_information> loadSearchResultList(RequestQueue requestQueue){

        JSONArray response = Requestor.sendRequestRecentPosts(requestQueue, web_url);
        ArrayList<recent_post_information> lists = SearchResultParser.parseJSONResponse(response);
        return lists;
    }
}
