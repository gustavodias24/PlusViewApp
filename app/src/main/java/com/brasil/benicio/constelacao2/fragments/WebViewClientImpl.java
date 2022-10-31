package com.brasil.benicio.constelacao2.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewClientImpl  extends WebViewClient {

    private Assistir activity = null;

    public WebViewClientImpl(Assistir activity) {
        this.activity = activity;
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        if(url.contains("androidpro.com.br")) return false;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
        return true;
    }

}
