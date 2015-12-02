package net.beepinc.vip.task;

import android.os.AsyncTask;

import net.beepinc.vip.Information.mypost_information;
import net.beepinc.vip.MyApplication;
import net.beepinc.vip.callback.MyPostsLoadedListener;

import java.util.ArrayList;

/**
 * Created by tayo on 10/24/2015.
 */
public class TaskLoadMyPosts extends AsyncTask<Void, Void, ArrayList<mypost_information>>{

    private MyPostsLoadedListener component;

    public TaskLoadMyPosts(MyPostsLoadedListener component){
        this.component = component;
    }


    @Override
    protected ArrayList<mypost_information> doInBackground(Void... params) {
        //ArrayList<mypost_information> lists_my_posts = MyApplication.getWriteableDatabaseForMyPosts().getAllMyPosts();
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<mypost_information> mypost_informations) {
        if(component != null){
            component.onMyPostsLoadedListener(mypost_informations);
        }
    }
}
