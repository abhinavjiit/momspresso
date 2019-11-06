package com.mycity4kids.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.ShortStoryPagerAdapter;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by hemant on 6/6/17.
 */
public class ShortStoryContainerActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private ShortStoryPagerAdapter mViewPagerAdapter;
    private Toolbar mToolbar;
    private ImageView backNavigationImageView;
    private ImageView playTtsTextView;

    private String authorId;
    private String articleId;
    private ArrayList<ArticleListingResult> articleList;
    private String userDynamoId;
    private RelativeLayout guideOverlay;
    private Toolbar guidetoolbar;
    private int currPos;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.short_story_container);
        root = findViewById(R.id.content_frame);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);
        userDynamoId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        Utils.pushOpenScreenEvent(this, "ShortStoryDetailContainerScreen", userDynamoId + "");
        mToolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        backNavigationImageView = (ImageView) findViewById(R.id.backNavigationImageView);
        playTtsTextView = (ImageView) findViewById(R.id.playTtsTextView);
        guideOverlay = (RelativeLayout) findViewById(R.id.guideOverlay);
        guidetoolbar = (Toolbar) findViewById(R.id.guidetoolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        articleList = bundle.getParcelableArrayList("pagerListData");
        String fromScreen = bundle.getString(Constants.FROM_SCREEN);
        final String author = bundle.getString(Constants.AUTHOR);

        if (bundle.getBoolean("fromNotification")) {
            Utils.pushEventNotificationClick(this, GTMEventType.NOTIFICATION_CLICK_EVENT, userDynamoId, "Notification Popup", "shortStoryDetails");
            Utils.pushViewShortStoryEvent(this, "Notification", userDynamoId + "", articleId, "Notification Popup", "-1" + "", author);
        } else {
            String listingType = bundle.getString(Constants.ARTICLE_OPENED_FROM);
            String index = bundle.getString(Constants.ARTICLE_INDEX);
            String screen = bundle.getString(Constants.FROM_SCREEN);
            Utils.pushViewShortStoryEvent(this, screen, userDynamoId + "", articleId, listingType, index + "", author);
        }

        mViewPager = (ViewPager) findViewById(R.id.pager);

        if (articleList == null || articleList.isEmpty()) {
            articleId = bundle.getString(Constants.ARTICLE_ID);
            authorId = bundle.getString(Constants.AUTHOR_ID);
            String blogSlug = bundle.getString(Constants.BLOG_SLUG);
            String titleSlug = bundle.getString(Constants.TITLE_SLUG);
            ArticleListingResult articleListingResult = new ArticleListingResult();
            articleListingResult.setId(articleId);
            articleListingResult.setUserId(authorId);
            articleListingResult.setBlogPageSlug(blogSlug);
            articleListingResult.setTitleSlug(titleSlug);
            articleList = new ArrayList<>();
            articleList.add(articleListingResult);
            initializeViewPager();
        } else {
            final int pos = Integer.parseInt(bundle.getString(Constants.ARTICLE_INDEX));

            mViewPagerAdapter = new ShortStoryPagerAdapter(getSupportFragmentManager(), articleList.size(), articleList, fromScreen);
            mViewPager.setAdapter(mViewPagerAdapter);
            mViewPager.setCurrentItem(pos);
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    currPos = position;
                }

                @Override
                public void onPageSelected(int position) {
                    if (currPos == position) {
                        Utils.pushArticleSwipeEvent(ShortStoryContainerActivity.this, "ShortStoryDetailContainerScreen", userDynamoId + "", articleId, "" + (currPos + 1), "" + position);
                    } else {
                        Utils.pushArticleSwipeEvent(ShortStoryContainerActivity.this, "ShortStoryDetailContainerScreen", userDynamoId + "", articleId, "" + currPos, "" + position);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        backNavigationImageView.setOnClickListener(this);
        playTtsTextView.setOnClickListener(this);
        guideOverlay.setOnClickListener(this);
        guidetoolbar.setOnClickListener(this);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            playTtsTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backNavigationImageView:
                finish();
                break;
        }
    }

    private void initializeViewPager() {
        mViewPagerAdapter = new ShortStoryPagerAdapter(getSupportFragmentManager(), articleList.size(), articleList, "dw");
        mViewPager.setAdapter(mViewPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
