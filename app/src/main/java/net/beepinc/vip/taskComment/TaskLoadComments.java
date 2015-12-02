package net.beepinc.vip.taskComment;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.android.volley.Response;

import net.beepinc.vip.Information.comment_information;
import net.beepinc.vip.callback.CommentsLoadedLisener;
import net.beepinc.vip.extras.CommentUtils;
import net.beepinc.vip.network.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by tayo on 10/27/2015.
 */
public class TaskLoadComments extends AsyncTask<Void, Void, ArrayList<comment_information>>{

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private CommentsLoadedLisener component;
    private String get_current_post;
    private CommentUtils commentUtils;

    public TaskLoadComments(CommentsLoadedLisener component, String get_current_post1){
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.component = component;
        this.get_current_post = get_current_post1;
        commentUtils = new CommentUtils(get_current_post);
    }

    @Override
    protected ArrayList<comment_information> doInBackground(Void... params) {
        ArrayList<comment_information> listComments = commentUtils.loadRecentComment(requestQueue);
        return listComments;
    }

    @Override
    protected void onPostExecute(ArrayList<comment_information> comment_informations) {
        if (component != null){
            component.onCommentsLoadedLisener(comment_informations);
        }
    }
}
