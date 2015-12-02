package net.beepinc.vip;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by tayo on 8/29/2015.
 */
public class InternetChecking {

    Context context;
    public InternetChecking(Context context){
        this.context = context;
    }

    public boolean isConnectedToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
            if (infos != null){
                for (int i = 0; i < infos.length; i++){
                    if(infos[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
