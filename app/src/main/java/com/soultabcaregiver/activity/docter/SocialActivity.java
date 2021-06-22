package com.soultabcaregiver.activity.docter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.soultabcaregiver.R;
import com.soultabcaregiver.utils.CustomProgressDialog;
import com.soultabcaregiver.utils.Utility;

public class SocialActivity extends AppCompatActivity {

    Context mContext;
    RelativeLayout back_btn;
    WebView wv_webview;
    String urlString;
    Handler handler;
    Runnable myRunnable;
    private CustomProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        mContext = this;
        back_btn = findViewById(R.id.back_btn);
        wv_webview = findViewById(R.id.social_Web);


        urlString = getIntent().getStringExtra("webUrl");
        Log.e("webUrl2", urlString);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(myRunnable);
                onBackPressed();
            }
        });

        if (Utility.isNetworkConnected(mContext)) {
            showProgressDialog(getResources().getString(R.string.Loading));
            wv_webview.getSettings().setJavaScriptEnabled(true);
            wv_webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            wv_webview.setWebViewClient(new WebViewClient());
            wv_webview.setWebChromeClient(new WebChromeClient());
            wv_webview.getSettings().setDisplayZoomControls(true);
            wv_webview.getSettings().setDomStorageEnabled(true);
            wv_webview.loadUrl(urlString);
            wv_webview.setWebViewClient(new MyWebViewClient());

            TimerStart();
        }else {
            Utility.ShowToast(mContext,getResources().getString(R.string.net_connection));
        }
    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            if (Utility.isNetworkConnected(mContext)) {

                showProgressDialog(getResources().getString(R.string.Loading));
                view.loadUrl(url);
            }else {
                Utility.ShowToast(mContext,getResources().getString(R.string.net_connection));
            }
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

            hideProgressDialog();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void TimerStart() {

        handler =  new Handler();
        myRunnable = new Runnable() {
            public void run() {
                if (progressDialog != null) progressDialog.dismiss();
                Log.e("Completed","20 Second");

            }
        };

        handler.postDelayed(myRunnable, 20000);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (wv_webview.canGoBack()) {
                        wv_webview.goBack();
                    } else {
                        handler.removeCallbacks(myRunnable);
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
        handler.removeCallbacks(myRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) progressDialog.dismiss();
        handler.removeCallbacks(myRunnable);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog != null) progressDialog.dismiss();
        handler.removeCallbacks(myRunnable);
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
