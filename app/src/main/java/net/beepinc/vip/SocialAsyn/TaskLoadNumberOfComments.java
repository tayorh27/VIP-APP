package net.beepinc.vip.SocialAsyn;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.android.volley.RequestQueue;

import net.beepinc.vip.SocialCallbacks.LoadNumberOfCommentsListener;
import net.beepinc.vip.SocialCallbacks.LoadNumberOfLikesListener;
import net.beepinc.vip.SocialExtras.CommentUtils;
import net.beepinc.vip.network.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by tayo on 10/28/2015.
 */
public class TaskLoadNumberOfComments extends AsyncTask<Void,Void,ArrayList<String>> {

    String get_cur_user, get_cur_post;
    CommentUtils commentUtils;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private LoadNumberOfCommentsListener component;

    public TaskLoadNumberOfComments(LoadNumberOfCommentsListener component,String get_current_post){
        this.get_cur_post = get_current_post;
       this.component = component;
//        this.holder = holder;
//        this.position = position;
        //this.get_cur_user = get_current_username;
        commentUtils = new CommentUtils(get_cur_post);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    public ArrayList<String> doInBackground() {
        return null;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        ArrayList<String> getComments = commentUtils.loadComments(requestQueue);
        return getComments;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        if(component != null){
            component.onLoadNumberOfCommentsListener(strings);
        }
    }
}
