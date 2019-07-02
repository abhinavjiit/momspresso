package com.mycity4kids.ui.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsletter_webview);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(NewsLetterWebviewActivity.this, "WebViewScreen", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webview = (WebView) findViewById(R.id.webview);

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
                        // TODO Auto-generated method stub
                        super.onPageStarted(view, url, favicon);
                        //progressBar.setVisibility(View.VISIBLE);
                    }

                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }

                    public void onPageFinished(WebView view, String url) {
                        //progressBar.setVisibility(View.GONE);
                    }

                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        //progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }

    }

    @Override
    protected void updateUi(Response response) {

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
