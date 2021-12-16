package com.soultabcaregiver.activity.docter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.soultabcaregiver.R;
import com.soultabcaregiver.utils.CustomProgressDialog;
import com.soultabcaregiver.utils.Utility;

import androidx.appcompat.app.AppCompatActivity;

public class SocialActivity extends AppCompatActivity {
    
    Context mContext;
    
    RelativeLayout back_btn;
    
    WebView wv_webview;
    
    String urlString;
    
    
    
    
    
    private CustomProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        mContext = this;
        back_btn = findViewById(R.id.back_btn);
        wv_webview = findViewById(R.id.social_Web);


        urlString = getIntent().getStringExtra("webUrl");
    
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (Utility.isNetworkConnected(mContext)) {
            showProgressDialog(getResources().getString(R.string.Loading));
            wv_webview.getSettings().setJavaScriptEnabled(true);
            wv_webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            wv_webview.setWebViewClient(new WebViewClient());
            wv_webview.setWebChromeClient(new WebChromeClient());
            wv_webview.getSettings().setDomStorageEnabled(true);
    
            wv_webview.getSettings().setSupportZoom(true);
            wv_webview.getSettings().setBuiltInZoomControls(true);
            wv_webview.getSettings().setDisplayZoomControls(false);
            wv_webview.getSettings().setLoadWithOverviewMode(true);
            wv_webview.getSettings().setUseWideViewPort(true);
    
            wv_webview.getSettings().setJavaScriptEnabled(true);
            wv_webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    
            wv_webview.loadUrl(urlString);
            wv_webview.getSettings().setSupportMultipleWindows(true);
            Log.e("urlString", urlString);
            //FOR WEBPAGE SLOW UI
    
            wv_webview.setWebChromeClient(new WebChromeClient() {
        
                @SuppressLint ("SetJavaScriptEnabled")
                @Override
                public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture,
                                              Message resultMsg) {
            
                    WebView newWebView = new WebView(mContext);
            
                    newWebView.getSettings().setSupportZoom(true);
                    newWebView.getSettings().setBuiltInZoomControls(true);
                    newWebView.getSettings().setDisplayZoomControls(false);
                    newWebView.getSettings().setUseWideViewPort(true);
                    newWebView.getSettings().setJavaScriptEnabled(true);
                    newWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            
                    newWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
                    newWebView.getSettings().setSupportMultipleWindows(true);
                    newWebView.getSettings().setDomStorageEnabled(true);
            
                    view.addView(newWebView);
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(newWebView);
                    resultMsg.sendToTarget();
            
                    newWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            Log.e("url", url);
                            return true;
                        }
                    });
            
                    hideProgressDialog();
            
                    return true;
                }
        
        
            });
    
            wv_webview.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    if (progress < 100) {
                
                    }
                    if (progress == 100) {
                        hideProgressDialog();
                    }
                }
            });
        }else {
            Utility.ShowToast(mContext,getResources().getString(R.string.net_connection));
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        TimerStart();
    }


    private void TimerStart() {
    
        new Handler().postDelayed(new Runnable() {
        
            @Override
            public void run() {
                hideProgressDialog();
                Log.e("Completed", "10 Second");
            
            
            }
        
        }, 10000);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (wv_webview.canGoBack()) {
                        wv_webview.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (progressDialog != null) progressDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) progressDialog.dismiss();
       
    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog != null) progressDialog.dismiss();
        
    }

    public void showProgressDialog(String message) {
        if (progressDialog == null) progressDialog = new CustomProgressDialog(this, message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) progressDialog.dismiss();
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
