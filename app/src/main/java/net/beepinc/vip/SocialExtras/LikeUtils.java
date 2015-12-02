package net.beepinc.vip.SocialExtras;

import com.android.volley.RequestQueue;

import net.beepinc.vip.Social.FetchLike;
import net.beepinc.vip.jsonComment.Requestor;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by tayo on 10/28/2015.
 */
public class LikeUtils {
    String current_post;
    FetchLike fetchLike;

    public LikeUtils(String get_current_post){
        fetchLike = new FetchLike();
        this.current_post = get_current_post;
    }

    public static final String web_url = "http://www.gisanrinadetayo.comuf.com/php_fetch/FetchLikeData.php";

    public ArrayList<String> loadLikes(RequestQueue requestQueue){

        JSONArray response = Requestor.sendRequestComment(requestQueue, web_url);
        ArrayList<String> lists = fetchLike.performTotalNumberOfLikes(response, current_post);
        return lists;
    }
}
