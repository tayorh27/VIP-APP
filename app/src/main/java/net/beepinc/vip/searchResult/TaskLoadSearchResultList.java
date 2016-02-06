package net.beepinc.vip.searchResult;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;

import net.beepinc.vip.Information.recent_post_information;
import net.beepinc.vip.callback.RecentPostsLoadedListener;
import net.beepinc.vip.extras.RecentPostUtils;
import net.beepinc.vip.network.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by tayo on 1/18/2016.
 */
public class TaskLoadSearchResultList extends AsyncTask<Void, Void, ArrayList<recent_post_information>> {

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private RecentPostsLoadedListener component;
    private SearchResultUtils recentPostUtils;
    private String get_current_user;

    public TaskLoadSearchResultList(RecentPostsLoadedListener component, String get_current_user1){
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.component = component;
        this.get_current_user = get_current_user1;
        recentPostUtils = new SearchResultUtils(get_current_user);

    }

    @Override
    protected ArrayList<recent_post_information> doInBackground(Void... params) {
        ArrayList<recent_post_information> listRecent = recentPostUtils.loadSearchResultList(requestQueue);
        return listRecent;
    }

    @Override
    protected void onPostExecute(ArrayList<recent_post_information> recent_post_informations) {
        if(component != null){
            component.onRecentPostsLoadedListener(recent_post_informations);
        }
    }
}
