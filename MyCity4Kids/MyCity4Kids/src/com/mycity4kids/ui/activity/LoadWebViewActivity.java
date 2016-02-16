package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * Created by manish.soni on 06-08-2015.
 */
public class LoadWebViewActivity extends BaseActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_activity);
        Utils.pushOpenScreenEvent(LoadWebViewActivity.this, "Help", SharedPrefUtils.getUserDetailModel(this).getId() + "");

        String url = getIntent().getStringExtra(Constants.WEB_VIEW_URL);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

    }

    @Override
    protected void updateUi(Response response) {

    }
}
