package com.proggeek;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ConnectionReceiver extends BroadcastReceiver {
    RelativeLayout nonet,net;
    WebView webView;

    public ConnectionReceiver(RelativeLayout nonet, RelativeLayout net,WebView webView) {
        this.nonet = nonet;
        this.net = net;
        this.webView=webView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isCompleted(context)){
            net.setVisibility(View.VISIBLE);
            nonet.setVisibility(View.INVISIBLE);

            Toast.makeText(context, "Internet connected", Toast.LENGTH_SHORT).show();
        }
        else {
            nonet.setVisibility(View.VISIBLE);
            net.setVisibility(View.INVISIBLE);
            Toast.makeText(context, "No Internet connected", Toast.LENGTH_SHORT).show();
        }

    }
    public boolean isCompleted(Context context){
        try{
            ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=cm.getActiveNetworkInfo();
            return(networkInfo!=null&& networkInfo.isConnectedOrConnecting());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }
}
