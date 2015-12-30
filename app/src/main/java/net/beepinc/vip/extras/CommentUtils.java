package net.beepinc.vip.extras;

import com.android.volley.RequestQueue;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.Information.comment_information;
import net.beepinc.vip.jsonComment.Parser;
import net.beepinc.vip.jsonComment.Requestor;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by tayo on 10/27/2015.
 */
public class CommentUtils {

    public static String current_post;
    public CommentUtils(String current_post1){
        this.current_post = current_post1;
    }

    public static final String web_url = AppConfig.web_url+"php_fetch/FetchCommentData.php";

    public ArrayList<comment_information> loadRecentComment(RequestQueue requestQueue){

        JSONArray response = Requestor.sendRequestComment(requestQueue, web_url);
        ArrayList<comment_information> lists = Parser.parseJSONResponse(response,current_post);
        return lists;
    }
}
