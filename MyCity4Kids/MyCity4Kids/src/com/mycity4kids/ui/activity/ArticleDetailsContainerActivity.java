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
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.tts.ReadArticleService;
import com.mycity4kids.ui.adapter.ArticleDetailsPagerAdapter;
import com.mycity4kids.ui.fragment.ArticleDetailsFragment;

import java.util.ArrayList;

/**
 * Created by hemant on 6/6/17.
 */
public class ArticleDetailsContainerActivity extends BaseActivity implements View.OnClickListener, ArticleDetailsFragment.ISwipeRelated {

    //    String sample = "Mrs. Sudha Murthy needs no introduction. She launched her latest book The Serpent’s Revenge Unusual Tales from the Mahabharata today in Mumbai amidst a very excited and enthusiastic bunch of children making sure to write every child’s name on the book while signing it. She was in conversation with RJ Anita from Radio One. Mrs Murthy started the conversation by emphasizing the importance of reading amongst young children. She says If you read more, you will learn more, you will discover new aspects, you will develop a viewpoint and you will tell unusual or your own interpretation of the tale. She has been an avid reader from a very young age and she reads about 100-150 pages every single day. She reads a variety of books from Shashi Tharoor, accomplished UK authors to lighter reads by Twinkle Khanna. Over the years she has become so addicted to reading that she feels she may end up reading the newspaper 3 times if she doesn’t have any books to read!. She is particularly fond of the Indian scriptures and mythology. However they are quite complex for young children. Most of the good books are thick, there is a lot of narration and large part of it is about praising the Gods which small children find difficult to follow. They lose interest beyond a point.  Her own children, when small, could not go beyond the first 10 pages. So it is her wish to convey these stories in simple language & with age appropriate narration so as to catch the attention of the young readers. And she tries to write unusual stories. In today’s time of television and YouTube, most children know of Krishna as a naughty boy who is always up to some mischief. One incidence she shares is when she visited her granddaughters in London. The kid’s version of the story was - Krishna is a very naughty boy. Once he saw some aunties in the swimming pool. So he took out the dresses of all the aunties from the locker and hid them. When the aunties were done with their swim, they came out and took a shower. When they went to their locker to take out their dress all the dresses were missing? The aunties knew who was up to mischief, so they all came to Krishna’s house and complained to his mother. When Krishna was summoned, he justified by saying that the aunties complain all the time about him opening their refrigerator and eating their cheese. That is why he hid all their clothes, so as to teach them a lesson.";
    private ViewPager mViewPager;
    private ArticleDetailsPagerAdapter mViewPagerAdapter;
    private Toolbar mToolbar;
    private ImageView backNavigationImageView;
    private ImageView playTtsTextView;
    private ImageView coachmarksImageView;
    private boolean isAudioPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_details_container);

        mToolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        backNavigationImageView = (ImageView) findViewById(R.id.backNavigationImageView);
        playTtsTextView = (ImageView) findViewById(R.id.playTtsTextView);
        coachmarksImageView = (ImageView) findViewById(R.id.coachmarksImageView);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.app_logo);

        Bundle bundle = getIntent().getExtras();
        ArrayList<ArticleListingResult> articleList = bundle.getParcelableArrayList("pagerListData");
        String fromScreen = bundle.getString(Constants.FROM_SCREEN);

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
        coachmarksImageView.setOnClickListener(this);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            playTtsTextView.setVisibility(View.GONE);
        }

        mViewPagerAdapter = new ArticleDetailsPagerAdapter(getSupportFragmentManager(), articleList.size(), articleList, fromScreen);
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

        if (!SharedPrefUtils.isCoachmarksShownFlag(this, "article_details")) {
            coachmarksImageView.setVisibility(View.VISIBLE);
        }


        Intent readArticleIntent = new Intent(this, ReadArticleService.class);
        startService(readArticleIntent);
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
                break;
            case R.id.playTtsTextView:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && !isAudioPlaying) {
                    Intent readArticleIntent = new Intent(this, ReadArticleService.class);
                    String playContent = ((ArticleDetailsFragment) mViewPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem())).getArticleContent();
                    if (StringUtils.isNullOrEmpty(playContent)) {
                        showToast(getString(R.string.ad_tts_toast_unplayable_article));
                        return;
                    }
                    readArticleIntent.putExtra("content", playContent);
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
            case R.id.coachmarksImageView:
                coachmarksImageView.setVisibility(View.GONE);
                SharedPrefUtils.setCoachmarksShownFlag(this, "article_details", true);
                break;
        }
    }

    public void hideToolbarPerm() {
        mToolbar.setVisibility(View.GONE);
        backNavigationImageView.setVisibility(View.GONE);
    }

    public void showPlayArticleAudioButton() {
        playTtsTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRelatedSwipe(ArrayList<ArticleListingResult> articleList) {
//        mViewPagerAdapter = new ArticleDetailsPagerAdapter(getSupportFragmentManager(), articleList.size(), articleList);
//        mViewPager.setAdapter(mViewPagerAdapter);
    }
}
