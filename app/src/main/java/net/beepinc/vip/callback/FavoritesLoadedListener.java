package net.beepinc.vip.callback;

import net.beepinc.vip.Information.favorites_information;

import java.util.ArrayList;

/**
 * Created by tayo on 10/4/2015.
 */
public interface FavoritesLoadedListener {
    public void onFavoritesLoadedListener(ArrayList<favorites_information> list);
}
