package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.BloggerDashboardAndPublishedArticlesController;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.newmodels.BloggerDashboardModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.BloggerDashboardPagerAdapter;
import com.mycity4kids.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

/**
 * Created by hemant on 16/3/16.
 */
public class BloggerDashboardActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView bloggerNameTextView, rankingTextView, viewCountTextView, followersViewCount;
    private ImageView bloggerImageView;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogger_dashboard);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.pushOpenScreenEvent(BloggerDashboardActivity.this, "Blogger Dashboard", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        rankingTextView = (TextView) findViewById(R.id.rankingTextView);
        viewCountTextView = (TextView) findViewById(R.id.viewCountTextView);
        followersViewCount = (TextView) findViewById(R.id.followersViewCount);

        bloggerNameTextView = (TextView) findViewById(R.id.bloggerNameTextView);
        bloggerNameTextView.setText(SharedPrefUtils.getUserDetailModel(this).getFirst_name());

        bloggerImageView = (ImageView) findViewById(R.id.bloggerImageView);
        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(this))) {
            Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).placeholder(R.drawable.family_xxhdpi)
                    .error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(bloggerImageView);
        }

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Blogger Profile");

        getBloggerDashboardDetails();
    }

    private void getBloggerDashboardDetails() {

        showProgressDialog("please wait ...");
        SharedPrefUtils.getUserDetailModel(this).setId(101856);
        BloggerDashboardAndPublishedArticlesController _controller = new BloggerDashboardAndPublishedArticlesController(this, this);
        _controller.getData(AppConstants.GET_BLOGGER_DASHBOARD_REQUEST, 0);
    }


    @Override
    protected void updateUi(Response response) {
        removeProgressDialog();

        BloggerDashboardModel responseData;

        if (response == null) {
            showToast("Something went wrong from server");
            removeProgressDialog();
//            mLodingView.setVisibility(View.GONE);
            return;
        }

        switch (response.getDataType()) {
            case AppConstants.GET_BLOGGER_DASHBOARD_REQUEST:
                responseData = (BloggerDashboardModel) response.getResponseObject();
                try {
                    if (responseData.getResponseCode() == 200) {
                        removeProgressDialog();

                        if (responseData.getResult().getData().getRank() == 0) {
                            rankingTextView.setText("NA");
                        } else {
                            rankingTextView.setText("" + responseData.getResult().getData().getRank());
                        }
                        viewCountTextView.setText("" + responseData.getResult().getData().getViews());
                        followersViewCount.setText("" + responseData.getResult().getData().getFollowers());

                        tabLayout.addTab(tabLayout.newTab().setText("Bookmarks (" + responseData.getResult().getData().getBookmarkCount() + ")"));
                        tabLayout.addTab(tabLayout.newTab().setText("Published (" + responseData.getResult().getData().getArticleCount() + ")"));

                        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
                        final BloggerDashboardPagerAdapter adapter = new BloggerDashboardPagerAdapter
                                (getSupportFragmentManager(), this, responseData.getResult().getData().getBookmarkCount(),
                                        responseData.getResult().getData().getArticleCount());
                        viewPager.setAdapter(adapter);
                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {
                                viewPager.setCurrentItem(tab.getPosition());
                            }

                            @Override
                            public void onTabUnselected(TabLayout.Tab tab) {

                            }

                            @Override
                            public void onTabReselected(TabLayout.Tab tab) {

                            }
                        });
                    } else if (responseData.getResponseCode() == 400) {
                        String message = responseData.getResult().getMessage();
                        if (!StringUtils.isNullOrEmpty(message)) {
                            showToast(message);
                        } else {
                            showToast(getString(R.string.went_wrong));
                        }
                    }
                } catch (Exception e) {
                    removeProgressDialog();
                    e.printStackTrace();
                    showToast(getString(R.string.went_wrong));
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(BloggerDashboardActivity.this,DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
