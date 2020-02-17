package com.mycity4kids.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * Created by Hemant Parmar on 25-08-2015.
 */
public class NewsLetterWebviewActivity extends BaseActivity {
    private String url;
    private WebView webview;
    private Toolbar toolbar;
    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsletter_webview);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(NewsLetterWebviewActivity.this, "WebViewScreen", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webview = (WebView) findViewById(R.id.webview1);

        webview.getSettings().setJavaScriptEnabled(true);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString(Constants.URL);

            if (!StringUtils.isNullOrEmpty(url)) {
                // load in webview
                webview.loadUrl(url);
                webview.getSettings().setAppCacheEnabled(true);
                webview.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }

                    public void onPageFinished(WebView view, String url) {
                    }

                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    }
                });
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
