package com.mycity4kids.ui.activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.ui.fragment.NotificationFragment;

public class NotificationActivity extends BaseActivity {
    private Toolbar mToolbar;
    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.back_arroow);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black_color), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        final Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        NotificationFragment fragment = new NotificationFragment();
        Bundle mBundle = new Bundle();
        fragment.setArguments(mBundle);
        addFragment(fragment, mBundle, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
