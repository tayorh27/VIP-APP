package net.beepinc.vip.extras;

import com.android.volley.RequestQueue;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.Information.recent_post_information;
import net.beepinc.vip.recent_json.Parser;
import net.beepinc.vip.recent_json.Requestor;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by tayo on 10/25/2015.
 */
public class RecentPostUtils {

    public static final String web_url = AppConfig.web_url+"php_fetch/FetchVoicenoteData.php";
    public String current_user;
    public RecentPostUtils(String current_user){
        this.current_user = current_user;
    }

    public ArrayList<recent_post_information> loadRecentPostList(RequestQueue requestQueue){

        JSONArray response = Requestor.sendRequestRecentPosts(requestQueue, web_url);
        ArrayList<recent_post_information> lists = Parser.parseJSONResponse(response, current_user);
        return lists;
    }

}
