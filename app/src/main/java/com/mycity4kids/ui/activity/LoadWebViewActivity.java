package com.mycity4kids.ui.activity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * Created by manish.soni on 06-08-2015.
 */
public class LoadWebViewActivity extends BaseActivity {

    private WebView webView;
    Toolbar mToolbar;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Momspresso");
        Utils.pushOpenScreenEvent(LoadWebViewActivity.this, "Notification WebView", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        String url = getIntent().getStringExtra(Constants.WEB_VIEW_URL);

        if (getIntent().getExtras().getBoolean("fromNotification")) {
            Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "Notification Popup", "webView");
        }
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }
}
