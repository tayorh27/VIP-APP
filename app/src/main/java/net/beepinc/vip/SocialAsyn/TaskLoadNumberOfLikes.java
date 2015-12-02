package net.beepinc.vip.SocialAsyn;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.android.volley.RequestQueue;

import net.beepinc.vip.Social.FetchLike;
import net.beepinc.vip.SocialCallbacks.LoadNumberOfLikesListener;
import net.beepinc.vip.SocialExtras.LikeUtils;
import net.beepinc.vip.network.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by tayo on 10/28/2015.
 */
public class TaskLoadNumberOfLikes extends AsyncTask<Void,Void,ArrayList<String>>{

    String get_cur_user, get_cur_post;
    LikeUtils likeUtils;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private LoadNumberOfLikesListener component;

    public TaskLoadNumberOfLikes(LoadNumberOfLikesListener component, String get_current_post){
        this.get_cur_post = get_current_post;
        this.component = component;
//        this.holder = holder;
//        this.position = position;
        //this.get_cur_user = get_current_username;
        likeUtils = new LikeUtils(get_cur_post);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

    }

    public ArrayList<String> doInBackground() {
        return null;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        ArrayList<String> getLists = likeUtils.loadLikes(requestQueue);
        return getLists;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        if(component != null){
            component.onLoadNumberOfLikesListener(strings);
        }
    }
}
