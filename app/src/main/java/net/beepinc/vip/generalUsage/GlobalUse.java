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

import net.beepinc.vip.R;

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
}
