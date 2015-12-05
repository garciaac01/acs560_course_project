package com.ipfw.myezshopper;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

/**
 * Created by mcpar on 12/4/2015.
 */
public class NetworkHelper {

    Context mContext;
    ConnectivityManager connectivityManager;

    public NetworkHelper(Context c){
        mContext = c;
        connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isConnected() {

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean connected;
        if (networkInfo != null && networkInfo.isConnected()) {
            connected = true;
        } else {
            connected = false;
        }
        // Return false by default
        return connected;
    }


}
