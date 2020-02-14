package com.mycity4kids.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 */
public class ConnectivityUtils {

    private static final String LOG_TAG = "ConnectivityUtils";

    /**
     * @param pContext
     * @return
     */
    public static boolean isNetworkEnabled(Context pContext) {
        NetworkInfo activeNetwork = getActiveNetwork(pContext);
        return activeNetwork != null && activeNetwork.isConnected();
    }

    /**
     * @param pContext
     * @return
     */
    public static void logNetworkState(Context pContext) {
        NetworkInfo activeNetwork = getActiveNetwork(pContext);
        if (activeNetwork == null) {
            Log.i(LOG_TAG, "No any active network found.");
            return;
        }
        Log.i(LOG_TAG, "Active Network. Type: " + activeNetwork.getTypeName());
        Log.i(LOG_TAG, "Active Network. isConnected: " + activeNetwork.isConnected());
        Log.i(LOG_TAG, "Active Network. isConnectedOrConnecting: " + activeNetwork.isConnectedOrConnecting());
        Log.i(LOG_TAG, "Active Network. N/W State Reason: " + activeNetwork.getReason());
    }

    /**
     * @param pContext
     * @return
     */
    public static NetworkInfo getActiveNetwork(Context pContext) {
        if (pContext == null) {
            return null;
        }
        ConnectivityManager conMngr = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMngr == null ? null : conMngr.getActiveNetworkInfo();
    }

    /**
     * @param pContext
     * @return
     */
    public static boolean isGpsEnabled(Context pContext) {
        LocationManager locationManager = (LocationManager) pContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null &&
                (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

}