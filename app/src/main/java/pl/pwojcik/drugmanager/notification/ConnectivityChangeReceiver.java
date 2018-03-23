package pl.pwojcik.drugmanager.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by pawel on 23.03.18.
 */

public class ConnectivityChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (isInitialStickyBroadcast()) {
        } else {
            if(isConnectedToInternet(context)){
                Toast.makeText(context,"Hurra! Sieć działa!",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(context,"Nie ma sieci",Toast.LENGTH_LONG).show();
            }
        }
    }
    private boolean isConnectedToInternet(Context context) {
        try {
            if (context != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
                if(connectivityManager!=null) {

                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    return networkInfo != null && networkInfo.isConnected();
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
