package net.beepinc.vip.SocialExtras;

import com.android.volley.RequestQueue;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.Social.FetchComment;
import net.beepinc.vip.jsonComment.Requestor;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by tayo on 10/28/2015.
 */
public class CommentUtils {
    String current_post;
    FetchComment fetchComment;
    public CommentUtils(String get_current_post){
        this.current_post = get_current_post;
        fetchComment = new FetchComment();

    }

    public static final String web_url = AppConfig.web_url+"php_fetch/FetchCommentData.php";

    public ArrayList<String> loadComments(RequestQueue requestQueue){

        JSONArray response = Requestor.sendRequestComment(requestQueue, web_url);
        ArrayList<String> lists = fetchComment.performTotalNumberOfComments(response, current_post);
        return lists;
    }
}
