package com.mycity4kids.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.tts.ReadArticleService;
import com.mycity4kids.ui.adapter.ArticleDetailsPagerAdapter;
import com.mycity4kids.ui.fragment.ArticleDetailsFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 6/6/17.
 */
public class ArticleDetailsContainerActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private ArticleDetailsPagerAdapter mViewPagerAdapter;
    private Toolbar mToolbar;
    private ImageView backNavigationImageView;
    private ImageView playTtsTextView;
    private boolean isAudioPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_details_container);

        mToolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        backNavigationImageView = (ImageView) findViewById(R.id.backNavigationImageView);
        playTtsTextView = (ImageView) findViewById(R.id.playTtsTextView);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.app_logo);

        Bundle bundle = getIntent().getExtras();
        ArrayList<ArticleListingResult> articleList = bundle.getParcelableArrayList("pagerListData");
        if (articleList == null || articleList.isEmpty()) {
            String articleId = bundle.getString(Constants.ARTICLE_ID);
            String authorId = bundle.getString(Constants.AUTHOR_ID);
            String blogSlug = bundle.getString(Constants.BLOG_SLUG);
            String titleSlug = bundle.getString(Constants.TITLE_SLUG);
            ArticleListingResult articleListingResult = new ArticleListingResult();
            articleListingResult.setId(articleId);
            articleListingResult.setUserId(authorId);
            articleListingResult.setBlogPageSlug(blogSlug);
            articleListingResult.setTitleSlug(titleSlug);
            articleList = new ArrayList<>();
            articleList.add(articleListingResult);
        }
//        ArticleDetailsFragment fragment = new ArticleDetailsFragment();
//        replaceFragment(fragment, bundle, true);
        int pos = Integer.parseInt(bundle.getString(Constants.ARTICLE_INDEX));
        mViewPager = (ViewPager) findViewById(R.id.pager);

        backNavigationImageView.setOnClickListener(this);
        playTtsTextView.setOnClickListener(this);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            playTtsTextView.setVisibility(View.GONE);
        }

        mViewPagerAdapter = new ArticleDetailsPagerAdapter(getSupportFragmentManager(), articleList.size(), articleList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(pos);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Intent readArticleIntent = new Intent(ArticleDetailsContainerActivity.this, ReadArticleService.class);
                stopService(readArticleIntent);
                playTtsTextView.setImageDrawable(ContextCompat.getDrawable(ArticleDetailsContainerActivity.this, R.drawable.ic_play_tts));
                isAudioPlaying = false;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                BaseApplication.setFirstSwipe(false);
            }
        });
    }

    public void hideMainToolbar() {
        mToolbar.animate()
                .translationY(-mToolbar.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        getSupportActionBar().hide();
                    }
                });
        backNavigationImageView.animate()
                .alpha(1)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        backNavigationImageView.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void showMainToolbar() {
        mToolbar.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        getSupportActionBar().show();
                    }
                });
        backNavigationImageView.animate()
                .alpha(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        backNavigationImageView.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                if (null != trackArticleReadTime) {
//                    trackArticleReadTime.updateTimeAtBackendAndGA(shareUrl, articleId, estimatedReadTime);
//                }
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
    protected void onDestroy() {
        Intent readArticleIntent = new Intent(this, ReadArticleService.class);
        stopService(readArticleIntent);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backNavigationImageView:
                finish();
            case R.id.playTtsTextView:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && !isAudioPlaying) {
                    Intent readArticleIntent = new Intent(this, ReadArticleService.class);
                    readArticleIntent.putExtra("content", ((ArticleDetailsFragment) mViewPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem())).getArticleContent());
                    readArticleIntent.putExtra("langCategoryId", "" + ((ArticleDetailsFragment) mViewPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem())).getArticleLanguageCategoryId());
                    startService(readArticleIntent);
                    playTtsTextView.setImageDrawable(ContextCompat.getDrawable(ArticleDetailsContainerActivity.this, R.drawable.ic_stop_tts));
                    isAudioPlaying = true;
                } else {
                    Intent readArticleIntent = new Intent(ArticleDetailsContainerActivity.this, ReadArticleService.class);
                    stopService(readArticleIntent);
                    playTtsTextView.setImageDrawable(ContextCompat.getDrawable(ArticleDetailsContainerActivity.this, R.drawable.ic_play_tts));
                    isAudioPlaying = false;
                }
                break;
        }
    }

    public void hideToolbarPerm() {
        mToolbar.setVisibility(View.GONE);
        backNavigationImageView.setVisibility(View.GONE);
    }
}
