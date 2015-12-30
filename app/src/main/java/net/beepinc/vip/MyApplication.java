package net.beepinc.vip;

import android.app.Application;
import android.content.Context;

import net.beepinc.vip.LocalDBs.FavDatabase;
import net.beepinc.vip.LocalDBs.MyPostDatabase;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * Created by tayo on 9/28/2015.
 */
public class MyApplication extends Application {

    private static MyApplication sInstance;
    private static FavDatabase favDatabase;
    private static MyPostDatabase myPostDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        favDatabase = new FavDatabase(this);
        myPostDatabase = new MyPostDatabase(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                                    .setDefaultFontPath("avenir_light.ttf")
                                    .setFontAttrId(R.attr.fontPath)
                                    .build());
    }

    public static MyApplication getInstance(){
        return sInstance;
    }

    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }

    public synchronized static FavDatabase getWriteableDatabase(){
        if (favDatabase == null){
            favDatabase = new FavDatabase(getAppContext());
        }
        return favDatabase;

    }

    public synchronized static MyPostDatabase getWriteableDatabaseForMyPosts(){
        if (myPostDatabase == null){
            myPostDatabase = new MyPostDatabase(getAppContext());
        }
        return myPostDatabase;

    }
}
