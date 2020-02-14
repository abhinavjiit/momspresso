package com.mycity4kids.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;

import androidx.appcompat.widget.Toolbar;

public class ProfileWebViewActivity extends BaseActivity {
    private WebView webView;
    private String webUrl,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_webview_activity);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        webView = (WebView) findViewById(R.id.profile_webview);

        webUrl = getIntent().getStringExtra(Constants.WEB_VIEW_URL);
        title = getIntent().getStringExtra("title");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebViewClient(new MyWebView());
        webView.loadUrl(webUrl);

    }

    private class MyWebView extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            showProgressDialog(getString(R.string.please_wait));
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.equals(webUrl)) {
                view.loadUrl(url);
            } else {
                finish();
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.requestFocus();
            if (title.equals("About")) {
                webView.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('header_bar navMenu')[0].style.display='none'; })()");
                webView.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('title_withbg abt')[0].style.margin='-50px 0px 0px 0px'; })()");
                webView.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('abt_img')[0].style.margin='50px 0px 0px 0px'; })()");
            }
            removeProgressDialog();
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
