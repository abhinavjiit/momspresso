package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_activity);
        mToolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("mycity4kids");
        Utils.pushOpenScreenEvent(LoadWebViewActivity.this, "Notification WebView", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        String url = getIntent().getStringExtra(Constants.WEB_VIEW_URL);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

    }

    @Override
    protected void updateUi(Response response) {

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent =new Intent(LoadWebViewActivity.this,DashboardActivity.class);
                startActivity(intent);
                finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent =new Intent(LoadWebViewActivity.this,DashboardActivity.class);
        startActivity(intent);
        finish();
       // super.onBackPressed();
    }
}
