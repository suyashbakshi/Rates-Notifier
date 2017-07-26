package net.ddns.suyashbakshi.ratenotifier;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by suyas on 7/25/2017.
 */

public class Utility {

    public static boolean isOnline(Context context){
        ConnectivityManager manager = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = manager.getActiveNetworkInfo();

        return info!=null && info.isConnectedOrConnecting();
    }
}
