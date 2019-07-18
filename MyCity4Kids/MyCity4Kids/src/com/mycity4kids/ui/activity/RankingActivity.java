package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.fragment.ImproveRankPageViewsSocialFragment;
import com.mycity4kids.ui.fragment.RankingHomeFragment;

/**
 * Created by hemant on 28/7/17.
 */
public class RankingActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener {

    private Toolbar mToolbar;
    private TextView toolbarTitle;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(this, "RankingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        String authorId = getIntent().getStringExtra("authorId");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = new Bundle();
        bundle.putString("authorId", authorId);

        RankingHomeFragment rankingHomeFragment = new RankingHomeFragment();
        rankingHomeFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, rankingHomeFragment);
        ft.commit();
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

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onBackStackChanged() {
        final Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (null != topFragment && topFragment instanceof RankingHomeFragment) {
            toolbarTitle.setText(getString(R.string.ranking_toolbar_title));
        } else if (null != topFragment && topFragment instanceof ImproveRankPageViewsSocialFragment) {
//            String fragType = topFragment.getArguments().getString(AppConstants.ANALYTICS_INFO_TYPE);
//            switch (fragType) {
//                case AppConstants.ANALYTICS_INFO_IMPROVE_PAGE_VIEWS:
//                    break;
//                case AppConstants.ANALYTICS_INFO_RANK_CALCULATION:
//                    break;
//                case AppConstants.ANALYTICS_INFO_IMPROVE_RANK:
//                    break;
//                case AppConstants.ANALYTICS_INFO_IMPROVE_SOCIAL_SHARE:
//                    break;
//                case AppConstants.ANALYTICS_INFO_INCREASE_FOLLOWERS:
//                    break;
//            }
            toolbarTitle.setText(getString(R.string.ranking_improve_view_title));
        }
    }
}
