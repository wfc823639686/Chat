package com.brik.chat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by wangfengchen on 15/11/4.
 */
public class NetReceiver extends BroadcastReceiver {

    private ConnectivityManager mConnectivityManager;

    private NetworkInfo netInfo;

    public static String networkState;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = mConnectivityManager.getActiveNetworkInfo();
            if(netInfo != null && netInfo.isAvailable()) {

                //网络连接
                String name = netInfo.getTypeName();

                if(netInfo.getType()==ConnectivityManager.TYPE_WIFI){
                    //WiFi网络
                    setNetworkState("WIFI");
                }else if(netInfo.getType()==ConnectivityManager.TYPE_ETHERNET){
                    //有线网络
                    setNetworkState("ETHERNET");
                }else if(netInfo.getType()==ConnectivityManager.TYPE_MOBILE){
                    //3g网络
                    setNetworkState("MOBILE");
                }
            } else {
                //网络断开
                setNetworkState("NONE");
            }
        }

    }


    public static String getNetworkState() {
        return networkState;
    }

    public static void setNetworkState(String networkState) {
        NetReceiver.networkState = networkState;
    }
}

