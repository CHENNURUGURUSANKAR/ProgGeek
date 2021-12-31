package com.proggeek;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    Dialog dialog;
    AdView mAdView;
    static InterstitialAd mInterstitialAd;
    int count = 1;
    int webcount = 1;
    RelativeLayout nonet, net;
    TextView refresh;
    BroadcastReceiver broadcastReceiver;

    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webmain);
        nonet = findViewById(R.id.no_net_ll);
        net = findViewById(R.id.net_ll);

        //broadcast receiver
        broadcastReceiver = new ConnectionReceiver(nonet,net,webView);
        registerBroadcastNetwork();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        /*try {
            checkInterNetPermissions();
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setUiColors();
    }

    /*  private void checkInterNetPermissions() {
          ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
          NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
          NetworkInfo mobil = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
          if (wifi.isConnected() || mobil.isConnected()) {
              nonet.setVisibility(View.INVISIBLE);
              net.setVisibility(View.VISIBLE);
              webView.reload();
          } else {
              net.setVisibility(View.INVISIBLE);
              nonet.setVisibility(View.VISIBLE);

          }


      }*/
    protected void registerBroadcastNetwork() {
        webView.setClickable(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.getCacheMode();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSettings.getSafeBrowsingEnabled();
        }
        webView.canZoomIn();
        webView.canZoomOut();
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://shivaakira12.github.io/proggeek.com//");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

    }

    protected void unregisterBroadCat() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadCat();
    }

    private void loadInterads() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-7141210209529810/3197368858", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                        if (interstitialAd != null) {
                            interstitialAd.show(MainActivity.this);
                        }
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    @SuppressLint("NewApi")
    private void setUiColors() {
        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.statusbar));
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {

            if (count >= 3) {
                loadInterads();
                count = 1;
                Log.d("count", String.valueOf(count));
            } else {
                Log.d("count", String.valueOf(count));
                count = count + 1;
                webView.goBack();
            }

        } else {
            loadInterads();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Are you sure to exit");
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.super.onBackPressed();
                }
            }).show();
        }
    }
}