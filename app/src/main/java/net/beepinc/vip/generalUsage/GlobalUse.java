package net.beepinc.vip.generalUsage;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import net.beepinc.vip.Information.mypost_information;
import net.beepinc.vip.MyApplication;
import net.beepinc.vip.R;

import java.util.ArrayList;

/**
 * Created by tayo on 10/16/2015.
 */
public class GlobalUse {

    public static void handleVolleyError(VolleyError error, Context mTextError) {
        //if any error occurs in the network operations, show the TextView that contains the error message

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Toast.makeText(mTextError, R.string.error_timeout, Toast.LENGTH_LONG).show();
        } else if (error instanceof AuthFailureError) {
            Toast.makeText(mTextError, R.string.error_auth, Toast.LENGTH_LONG).show();
            //TODO
        } else if (error instanceof ServerError) {
            Toast.makeText(mTextError, R.string.error_server, Toast.LENGTH_LONG).show();
            //TODO
        } else if (error instanceof NetworkError) {
            Toast.makeText(mTextError, R.string.error_network, Toast.LENGTH_LONG).show();
            //TODO
        } else if (error instanceof ParseError) {
            Toast.makeText(mTextError, R.string.error_parse, Toast.LENGTH_LONG).show();
            //TODO
        }
    }

    public static void setListForMyPost(String caption, String voicenote, String img, String mob, String username, String bitmap, String cTime,String display){

        ArrayList<mypost_information> customData = new ArrayList<>();
        int fk = MyApplication.getWriteableDatabaseForMyPosts().getLastId();
        int id = fk+1;
        mypost_information current = new mypost_information(id,caption, voicenote, img, mob, username, bitmap, cTime,"partial",display);
        customData.add(current);

        MyApplication.getWriteableDatabaseForMyPosts().insertMyPost(customData, false);
    }

    public static void setList(String caption, String voicenote, String img, String mob, String username, String bitmap, String cTime,String display){

        ArrayList<mypost_information> customData = new ArrayList<>();
        int fk = MyApplication.getWritableDatabaseForFavorites().getLastId();
        int id = fk+1;
        mypost_information current = new mypost_information(id,caption, voicenote, img, mob, username, bitmap, cTime,"partial",display);
        customData.add(current);

        MyApplication.getWritableDatabaseForFavorites().insertMyFavorites(customData, false);
    }
}
