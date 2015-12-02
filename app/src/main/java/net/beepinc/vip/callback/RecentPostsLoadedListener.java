package net.beepinc.vip.callback;

import net.beepinc.vip.Information.recent_post_information;

import java.util.ArrayList;

/**
 * Created by tayo on 10/25/2015.
 */
public interface RecentPostsLoadedListener {
    public void onRecentPostsLoadedListener(ArrayList<recent_post_information> lists);
}
