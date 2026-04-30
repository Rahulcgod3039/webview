package io.codetic.webview;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ShowWebView extends Activity {

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This will not show title bar 
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_web_view);
         
        // Get webview
        webView = (WebView) findViewById(R.id.webView1);
        
        if(haveNetworkConnection()){
            // Yahan tumhari Vercel link aayegi
            startWebView("https://indian-tiers.vercel.app/");
        } else {
            webView.loadUrl("file:///android_asset/error.html");
        }
    }
     
    private void startWebView(String url) {
         
        webView.setWebViewClient(new WebViewClient() {     
            ProgressDialog progressDialog;
          
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {             
                if(url.startsWith("tel:")){
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
            
            // Show loader on url load
            @Override
            public void onLoadResource (WebView view, String url) {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(ShowWebView.this);
                    progressDialog.setMessage("Loading IndianTiers...");
                    progressDialog.show();
                }
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                } catch(Exception exception){
                    exception.printStackTrace();
                }
            }
        });
          
        // Javascript enabled on webview 
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true); // Modern websites ke liye zaroori hai
         
        // Load url in webview
        webView.loadUrl(url);
    }
     
    // Open previous opened link from history on webview when back button pressed
    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
