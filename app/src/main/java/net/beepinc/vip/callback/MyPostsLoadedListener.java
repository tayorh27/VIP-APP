package net.beepinc.vip.callback;


import net.beepinc.vip.Information.mypost_information;

import java.util.ArrayList;

/**
 * Created by tayo on 10/24/2015.
 */
public interface MyPostsLoadedListener {
    public void onMyPostsLoadedListener(ArrayList<mypost_information> list);
}
