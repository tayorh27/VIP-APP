package net.beepinc.vip.task;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;

import net.beepinc.vip.Information.favorites_information;
import net.beepinc.vip.callback.FavoritesLoadedListener;
import net.beepinc.vip.extras.FavoritesUtils;
import net.beepinc.vip.network.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by tayo on 10/4/2015.
 */
public class TaskLoadFavorites extends AsyncTask<Void, Void, ArrayList<favorites_information>> {
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private FavoritesLoadedListener component;

    public TaskLoadFavorites(FavoritesLoadedListener component){
        this.component = component;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    @Override
    protected ArrayList<favorites_information> doInBackground(Void... params) {
        ArrayList<favorites_information> listFav = FavoritesUtils.loadFavoritesList(requestQueue);
        return listFav;
    }

    @Override
    protected void onPostExecute(ArrayList<favorites_information> favorites_informations) {
        if(component != null){
            component.onFavoritesLoadedListener(favorites_informations);
        }
    }
}
