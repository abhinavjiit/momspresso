package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.ShortStoryPagerAdapter;
import com.mycity4kids.ui.fragment.ViewAllCommentsFragment;
import java.util.ArrayList;

/**
 * Created by hemant on 6/6/17.
 */
public class ShortStoryContainerActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private ShortStoryPagerAdapter shortStoryPagerAdapter;
    private Toolbar toolbar;
    private ImageView playTtsTextView;
    private String authorId;
    private String articleId;
    private ArrayList<ArticleListingResult> articleList;
    private String userDynamoId;
    private RelativeLayout guideOverlay;
    private Toolbar guidetoolbar;
    private int currPos;
    private RelativeLayout root;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.short_story_container);
        root = findViewById(R.id.content_frame);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);
        userDynamoId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        Utils.pushOpenScreenEvent(this, "ShortStoryDetailContainerScreen", userDynamoId + "");
        toolbar = findViewById(R.id.anim_toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        playTtsTextView = findViewById(R.id.playTtsTextView);
        guideOverlay = findViewById(R.id.guideOverlay);
        guidetoolbar = findViewById(R.id.guidetoolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        articleList = bundle.getParcelableArrayList("pagerListData");
        String fromScreen = bundle.getString(Constants.FROM_SCREEN);
        final String author = bundle.getString(Constants.AUTHOR);

        if (bundle.getBoolean("fromNotification")) {
            Utils.pushNotificationClickEvent(this, "shortStoryDetails", userDynamoId, "ShortStoryContainerActivity");
            Utils.pushViewShortStoryEvent(this, "Notification", userDynamoId + "", articleId, "Notification Popup",
                    "-1" + "", author);
        } else {
            String listingType = bundle.getString(Constants.ARTICLE_OPENED_FROM);
            String index = bundle.getString(Constants.ARTICLE_INDEX);
            String screen = bundle.getString(Constants.FROM_SCREEN);
            Utils.pushViewShortStoryEvent(this, screen, userDynamoId + "", articleId, listingType, index + "", author);
        }

        viewPager = (ViewPager) findViewById(R.id.pager);

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

            shortStoryPagerAdapter = new ShortStoryPagerAdapter(getSupportFragmentManager(), articleList.size(),
                    articleList,
                    fromScreen);
            viewPager.setAdapter(shortStoryPagerAdapter);
            viewPager.setCurrentItem(pos);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    currPos = position;
                }

                @Override
                public void onPageSelected(int position) {
                    if (currPos == position) {
                        Utils.pushArticleSwipeEvent(ShortStoryContainerActivity.this, "ShortStoryDetailContainerScreen",
                                userDynamoId + "", articleId, "" + (currPos + 1), "" + position);
                    } else {
                        Utils.pushArticleSwipeEvent(ShortStoryContainerActivity.this, "ShortStoryDetailContainerScreen",
                                userDynamoId + "", articleId, "" + currPos, "" + position);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        playTtsTextView.setOnClickListener(this);
        guideOverlay.setOnClickListener(this);
        guidetoolbar.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
    }

    private void initializeViewPager() {
        shortStoryPagerAdapter = new ShortStoryPagerAdapter(getSupportFragmentManager(), articleList.size(),
                articleList,
                "dw");
        viewPager.setAdapter(shortStoryPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        try {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (fragment instanceof ViewAllCommentsFragment) {
                toolbarTitle.setText("");
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        super.onBackPressed();
    }

    public void setToolbarTitle(String comments) {
        toolbarTitle.setText(comments);
    }
}
