package net.beepinc.vip;

import android.content.Context;
import android.content.SharedPreferences;

import net.beepinc.vip.LocalDBs.StoreFavorites;

import java.util.Arrays;

/**
 * Created by tayo on 8/16/2015.
 */
public class UserLocalStore {

    public static final String SP_NAME = "userDetails";

    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context){
        userLocalDatabase = context.getSharedPreferences(SP_NAME,0);
    }

    public void storeUserData(User user){
        SharedPreferences.Editor editor = userLocalDatabase.edit();
        editor.putInt("id",user.id);
        editor.putString("username", user.uname);
        editor.putString("mobile", user.mob);
        editor.putString("image", user.image);
        editor.putString("category", user.cate);
        editor.putString("password", user.pass);
        editor.putString("sq", user.sq);
        editor.putString("sa", user.sa);
        editor.apply();
    }

    public void storeFav(StoreFavorites storeFavorites){
        SharedPreferences.Editor editor = userLocalDatabase.edit();
        editor.putString("usernames", storeFavorites.usernames);
        editor.putString("mobiles", storeFavorites.mobiles);
        editor.putString("categories", storeFavorites.categories);
        editor.putString("images", storeFavorites.images);
        editor.putString("ids", storeFavorites.ids);
        editor.apply();
    }

    public StoreFavorites getFav(){
        String ids = userLocalDatabase.getString("ids","");
        String unames = userLocalDatabase.getString("usernames","");
        String mobs = userLocalDatabase.getString("mobiles","");
        String cats = userLocalDatabase.getString("categories","");
        String imgs = userLocalDatabase.getString("images","");

        StoreFavorites storef = new StoreFavorites(unames,mobs,cats,imgs,ids);
        return storef;
    }

    public User getLoggedUser(){
        int id = userLocalDatabase.getInt("id",0);
        String uname = userLocalDatabase.getString("username","");
        String mob = userLocalDatabase.getString("mobile","");
        String img = userLocalDatabase.getString("image","");
        String cat = userLocalDatabase.getString("category","");
        String pwd = userLocalDatabase.getString("password","");
        String sq = userLocalDatabase.getString("sq","");
        String sa = userLocalDatabase.getString("sa","");


        User storedUser = new User(id,uname,mob,img,cat,pwd,sq,sa);
        return storedUser;
    }

    public void setUserLoggedIn(boolean logged){
        SharedPreferences.Editor editor = userLocalDatabase.edit();
        editor.putBoolean("logged", logged);
        editor.apply();
    }

    public boolean getUserLoggedIn(){
        if (userLocalDatabase.getBoolean("logged",false) == true){
            return true;
        }else{
            return false;
        }
    }

    public void clearUserDatabase(){
        SharedPreferences.Editor editor = userLocalDatabase.edit();
        editor.clear();
        editor.apply();
    }
}
