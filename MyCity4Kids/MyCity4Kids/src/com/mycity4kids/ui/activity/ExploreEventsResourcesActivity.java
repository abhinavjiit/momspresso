package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.fragment.ExploreFragment;
import com.mycity4kids.ui.fragment.FragmentBusinesslistEvents;
import com.mycity4kids.ui.fragment.FragmentHomeCategory;

/**
 * Created by hemant on 7/8/17.
 */
public class ExploreEventsResourcesActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener {

    Toolbar toolbar;
    TextView toolbarTitleTextView;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore_events_resources_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitleTextView = (TextView) findViewById(R.id.toolbarTitleTextView);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

        ExploreFragment fragment1 = new ExploreFragment();
        Bundle mBundle1 = new Bundle();
        fragment1.setArguments(mBundle1);
        addFragment(fragment1, mBundle1, true);
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it
        final Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (topFragment instanceof FragmentBusinesslistEvents) {
            getMenuInflater().inflate(R.menu.menu_event, menu);
        } else if (topFragment instanceof FragmentHomeCategory) {
            getMenuInflater().inflate(R.menu.kidsresource_listing, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        switch (item.getItemId()) {
            case R.id.filter:
                if (topFragment instanceof FragmentBusinesslistEvents) {
                    ((FragmentBusinesslistEvents) topFragment).toggleFilter();
                }
                break;
            case R.id.save:
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.kidsresource_bookmark:
                if (topFragment instanceof FragmentHomeCategory) {
                    Intent intent = new Intent(this, BusinessListActivityKidsResources.class);
                    intent.putExtra(Constants.SHOW_BOOKMARK_RESOURCES, 1);
                    startActivity(intent);
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        final Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (null != topFragment && topFragment instanceof ExploreFragment) {
            Utils.pushOpenScreenEvent(this, "ExploreScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
            toolbarTitleTextView.setText(getString(R.string.home_screen_explore_title));
        } else if (null != topFragment && topFragment instanceof FragmentBusinesslistEvents) {
            Utils.pushOpenScreenEvent(this, "EventsListingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
            toolbarTitleTextView.setText(getString(R.string.home_screen_upcoming_events_title));
        } else if (null != topFragment && topFragment instanceof FragmentHomeCategory) {
            Utils.pushOpenScreenEvent(this, "ResourceListingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
            toolbarTitleTextView.setText(getString(R.string.home_screen_kids_res_title));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        try {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
