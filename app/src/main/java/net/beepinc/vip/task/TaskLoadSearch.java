package net.beepinc.vip.task;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;

import net.beepinc.vip.Information.favorites_information;
import net.beepinc.vip.callback.SearchLoadedListener;
import net.beepinc.vip.extras.SearchUtils;
import net.beepinc.vip.network.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by tayo on 1/5/2016.
 */
public class TaskLoadSearch extends AsyncTask<Void, Void, ArrayList<favorites_information>> {

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private SearchLoadedListener component;
    private String username;

    public TaskLoadSearch(SearchLoadedListener component, String username){
        this.component = component;
        this.username = username;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    @Override
    protected ArrayList<favorites_information> doInBackground(Void... params) {
        ArrayList<favorites_information> listFav = SearchUtils.loadSearchList(requestQueue,username);
        return listFav;
    }

    @Override
    protected void onPostExecute(ArrayList<favorites_information> favorites_informations) {
        if(component != null){
            component.onSearchLoadedListener(favorites_informations);
        }
    }
}
