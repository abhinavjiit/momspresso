package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.azuretts.Synthesizer;
import com.mycity4kids.azuretts.Voice;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.tts.ReadArticleService;
import com.mycity4kids.ui.adapter.ArticleDetailsPagerAdapter;
import com.mycity4kids.ui.fragment.ArticleDetailsFragment;
import com.mycity4kids.widget.CustomViewPager;

import java.util.ArrayList;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 6/6/17.
 */
public class ArticleDetailsContainerActivity extends BaseActivity implements View.OnClickListener, ArticleDetailsFragment.ISwipeRelated {

    public static final String NEW_ARTICLE_DETAIL_FLAG = "new_article_detail_flag";

    private FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    private boolean newArticleDetailFlag;
    private ArticleDetailsAPI articleDetailsAPI;
    private TopicsCategoryAPI topicsAPI;
    private String parentId;
    private CustomViewPager mViewPager;
    private ArticleDetailsPagerAdapter mViewPagerAdapter;
    private Toolbar mToolbar;
    private ImageView backNavigationImageView;
    private ImageView playTtsTextView;
    private boolean isAudioPlaying = false;
    private String authorId;
    private String articleId;
    private ArrayList<ArticleListingResult> articleList;
    private HashSet<String> impressionArticleList;
    private int currPos;
    private String userDynamoId;
    private String preferredLang;
    private long audioStartTime = 0;
    private RelativeLayout guideOverlay, root;
    private Toolbar guidetoolbar;
    private Synthesizer m_syn;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_details_container);
        root = findViewById(R.id.content_frame);
        ((BaseApplication) getApplication()).setActivity(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        preferredLang = SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext());
        Utils.pushOpenScreenEvent(this, "DetailArticleScreen", userDynamoId + "");

        newArticleDetailFlag = mFirebaseRemoteConfig.getBoolean(NEW_ARTICLE_DETAIL_FLAG);
        mToolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        backNavigationImageView = (ImageView) findViewById(R.id.backNavigationImageView);
        playTtsTextView = (ImageView) findViewById(R.id.playTtsTextView);
        guideOverlay = (RelativeLayout) findViewById(R.id.guideOverlay);
        guidetoolbar = (Toolbar) findViewById(R.id.guidetoolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        impressionArticleList = new HashSet<>();

        Bundle bundle = getIntent().getExtras();
        articleList = bundle.getParcelableArrayList("pagerListData");
        String fromScreen = bundle.getString(Constants.FROM_SCREEN);
        final String author = bundle.getString(Constants.AUTHOR);

        if (bundle.getBoolean("fromNotification")) {
            Utils.pushNotificationClickEvent(this, "article_details", userDynamoId, "ArticleDetailsContainerActivity");
            Utils.pushViewArticleEvent(this, "Notification", userDynamoId + "", articleId, "Notification Popup", "-1" + "", author);
        } else {
            String listingType = bundle.getString(Constants.ARTICLE_OPENED_FROM);
            String index = bundle.getString(Constants.ARTICLE_INDEX);
            String screen = bundle.getString(Constants.FROM_SCREEN);
            parentId = bundle.getString("id");
            Utils.pushViewArticleEvent(this, screen, userDynamoId + "", articleId, listingType, index + "", author);
        }

        mViewPager = (CustomViewPager) findViewById(R.id.pager);

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
            hitRelatedArticleAPI();

        } else {
            final int pos = Integer.parseInt(bundle.getString(Constants.ARTICLE_INDEX));

            mViewPagerAdapter = new ArticleDetailsPagerAdapter(getSupportFragmentManager(), articleList.size(), articleList, fromScreen, parentId, newArticleDetailFlag);
            mViewPager.setAdapter(mViewPagerAdapter);
            mViewPager.setCurrentItem(pos);
            mViewPager.setOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    Intent readArticleIntent = new Intent(ArticleDetailsContainerActivity.this, ReadArticleService.class);
                    stopService(readArticleIntent);
                    Log.d("-----AZURE----", "STOPPING");
                    if (m_syn != null) {
                        m_syn.stopSound();
                    }
                    playTtsTextView.setImageDrawable(ContextCompat.getDrawable(ArticleDetailsContainerActivity.this, R.drawable.ic_play_tts));
                    playTtsTextView.setColorFilter(getResources().getColor(R.color.app_red), PorterDuff.Mode.SRC_ATOP);
                    if (isAudioPlaying) {
                        ArticleDetailsFragment articleDetailsFragment = ((ArticleDetailsFragment) mViewPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem()));
                        long duration = (System.currentTimeMillis() - audioStartTime) / 1000;
                        Utils.pushStopArticleAudioEvent(ArticleDetailsContainerActivity.this, "DetailArticleScreen", userDynamoId + "", articleDetailsFragment.getGTMArticleId(), articleDetailsFragment.getGTMAuthor(),
                                articleDetailsFragment.getGTMLanguage(), "" + duration);
                    }
                    isAudioPlaying = false;
                    currPos = position;
                }

                @Override
                public void onPageSelected(int position) {
                    if (currPos == position) {
                        Utils.pushArticleSwipeEvent(ArticleDetailsContainerActivity.this, "DetailArticleScreen", userDynamoId + "", articleId, "" + (currPos + 1), "" + position);
                    } else {
                        Utils.pushArticleSwipeEvent(ArticleDetailsContainerActivity.this, "DetailArticleScreen", userDynamoId + "", articleId, "" + currPos, "" + position);
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BaseApplication) getApplication()).setView(root);
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
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Intent readArticleIntent = new Intent(this, ReadArticleService.class);
        stopService(readArticleIntent);
        try {
            if (isAudioPlaying) {
                ArticleDetailsFragment articleDetailsFragment = ((ArticleDetailsFragment) mViewPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem()));
                long duration = (System.currentTimeMillis() - audioStartTime) / 1000;
                Utils.pushStopArticleAudioEvent(ArticleDetailsContainerActivity.this, "DetailArticleScreen", userDynamoId + "", articleDetailsFragment.getGTMArticleId(), articleDetailsFragment.getGTMAuthor(),
                        articleDetailsFragment.getGTMLanguage(), "" + duration);
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backNavigationImageView:
                finish();
                break;
            case R.id.playTtsTextView:
                ArticleDetailsFragment articleDetailsFragment = ((ArticleDetailsFragment) mViewPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem()));
                if (!isAudioPlaying) {
                    if (AppConstants.TAMIL_CATEGORYID.equals(articleDetailsFragment.getArticleLanguageCategoryId())
                            || AppConstants.TELUGU_CATEGORYID.equals(articleDetailsFragment.getArticleLanguageCategoryId())) {
                        if (m_syn == null) {
                            m_syn = new Synthesizer(getString(R.string.azure_api_key));
                        }
                        m_syn.SetServiceStrategy(Synthesizer.ServiceStrategy.AlwaysService);
                        Voice voice;
                        if (AppConstants.TAMIL_CATEGORYID.equals(articleDetailsFragment.getArticleLanguageCategoryId())) {
                            voice = new Voice("ta-IN", "Microsoft Server Speech Text to Speech Voice (ta-IN, Valluvar)", Voice.Gender.Male, true);
                        } else {
                            voice = new Voice("te-IN", "Microsoft Server Speech Text to Speech Voice (te-IN, Chitra)", Voice.Gender.Female, true);
                        }
                        m_syn.SetVoice(voice, null);
                        String playContent = articleDetailsFragment.getArticleContent();
                        m_syn.SpeakToAudio(playContent);
                        playTtsTextView.setImageDrawable(ContextCompat.getDrawable(ArticleDetailsContainerActivity.this, R.drawable.ic_stop_tts));
                        Utils.pushPlayArticleAudioEvent(this, "DetailArticleScreen", userDynamoId + "", articleDetailsFragment.getGTMArticleId(), articleDetailsFragment.getGTMAuthor(),
                                articleDetailsFragment.getGTMLanguage());
                        audioStartTime = System.currentTimeMillis();
                        isAudioPlaying = true;
                        showToast(getString(R.string.audio_delay_msg));
                        return;
                    }
                } else {
                    if (AppConstants.TAMIL_CATEGORYID.equals(articleDetailsFragment.getArticleLanguageCategoryId())
                            || AppConstants.TELUGU_CATEGORYID.equals(articleDetailsFragment.getArticleLanguageCategoryId())) {
                        if (m_syn != null) {
                            m_syn.stopSound();
                        }
                        playTtsTextView.setImageDrawable(ContextCompat.getDrawable(ArticleDetailsContainerActivity.this, R.drawable.ic_play_tts));
                        isAudioPlaying = false;
                        long duration = (System.currentTimeMillis() - audioStartTime) / 1000;
                        Utils.pushStopArticleAudioEvent(this, "DetailArticleScreen", userDynamoId + "", articleDetailsFragment.getGTMArticleId(), articleDetailsFragment.getGTMAuthor(),
                                articleDetailsFragment.getGTMLanguage(), "" + duration);
                        return;
                    }
                }

                if (!isAudioPlaying) {
                    Intent readArticleIntent = new Intent(this, ReadArticleService.class);
                    String playContent = articleDetailsFragment.getArticleContent();
                    if (StringUtils.isNullOrEmpty(playContent)) {
                        showToast(getString(R.string.ad_tts_toast_unplayable_article));
                        return;
                    }
                    readArticleIntent.putExtra("content", playContent);
                    readArticleIntent.putExtra("langCategoryId", "" + articleDetailsFragment.getArticleLanguageCategoryId());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(readArticleIntent);
                    } else {
                        startService(readArticleIntent);
                    }
                    playTtsTextView.setImageDrawable(ContextCompat.getDrawable(ArticleDetailsContainerActivity.this, R.drawable.ic_stop_tts));
                    Utils.pushPlayArticleAudioEvent(this, "DetailArticleScreen", userDynamoId + "", articleDetailsFragment.getGTMArticleId(), articleDetailsFragment.getGTMAuthor(),
                            articleDetailsFragment.getGTMLanguage());
                    audioStartTime = System.currentTimeMillis();
                    isAudioPlaying = true;
                } else {
                    Intent readArticleIntent = new Intent(ArticleDetailsContainerActivity.this, ReadArticleService.class);
                    stopService(readArticleIntent);
                    playTtsTextView.setImageDrawable(ContextCompat.getDrawable(ArticleDetailsContainerActivity.this, R.drawable.ic_play_tts));
                    isAudioPlaying = false;
                    long duration = (System.currentTimeMillis() - audioStartTime) / 1000;
                    Utils.pushStopArticleAudioEvent(this, "DetailArticleScreen", userDynamoId + "", articleDetailsFragment.getGTMArticleId(), articleDetailsFragment.getGTMAuthor(),
                            articleDetailsFragment.getGTMLanguage(), "" + duration);
                }
                break;
            case R.id.guidetoolbar:
            case R.id.guideOverlay:
                guideOverlay.setVisibility(View.GONE);
                SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "article_details", true);
                break;
        }
    }

    public void hideToolbarPerm() {
        mToolbar.setVisibility(View.GONE);
        backNavigationImageView.setVisibility(View.GONE);
    }

    public void showPlayArticleAudioButton() {
        if (!SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "article_details")) {
            guideOverlay.setVisibility(View.VISIBLE);
        }
        playTtsTextView.setVisibility(View.VISIBLE);
    }

    public void addArticleForImpression(String a_id) {
        impressionArticleList.add(a_id);
    }

    @Override
    public void onRelatedSwipe(ArrayList<ArticleListingResult> articleList) {
//        mViewPagerAdapter = new ArticleDetailsPagerAdapter(getSupportFragmentManager(), articleList.size(), articleList);
//        mViewPager.setAdapter(mViewPagerAdapter);
    }

    private void hitRelatedArticleAPI() {
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
        topicsAPI = retro.create(TopicsCategoryAPI.class);

        Call<ArticleListingResponse> categoryRelatedArticlesCall = articleDetailsAPI.getCategoryRelatedArticles(articleId, 0, 5, SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
        categoryRelatedArticlesCall.enqueue(categoryArticleResponseCallback);
    }

    private Callback<ArticleListingResponse> categoryArticleResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {

            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("Category related Article API failure");
                Crashlytics.logException(nee);
                Call<ArticleListingResponse> callAuthorRecentcall = articleDetailsAPI.getPublishedArticles(authorId, 0, 1, 6);
                callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
                return;
            }

            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
                    if (dataList != null) {
                        for (int i = 0; i < dataList.size(); i++) {
                            if (dataList.get(i).getId().equals(articleId)) {
                                dataList.remove(i);
                                break;
                            }
                        }
                    }
                    if (dataList.size() < 5) {
                        Call<ArticleListingResponse> callAuthorRecentcall = articleDetailsAPI.getPublishedArticles(authorId, 0, 1, 6);
                        callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
                    } else {
                        articleList.addAll(dataList);
                        initializeViewPager();
                    }
                } else {
                    NetworkErrorException nee = new NetworkErrorException("Category related Article Error Response");
                    Crashlytics.logException(nee);
                    Call<ArticleListingResponse> callAuthorRecentcall = articleDetailsAPI.getPublishedArticles(authorId, 0, 1, 6);
                    callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Call<ArticleListingResponse> callAuthorRecentcall = articleDetailsAPI.getPublishedArticles(authorId, 0, 1, 6);
                callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            Call<ArticleListingResponse> callAuthorRecentcall = articleDetailsAPI.getPublishedArticles(authorId, 0, 1, 6);
            callAuthorRecentcall.enqueue(bloggersArticleResponseCallback);
        }
    };

    private Callback<ArticleListingResponse> bloggersArticleResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {

            if (response.body() == null) {
                Call<ArticleListingResponse> filterCall = topicsAPI.getTrendingArticles(1, 6, preferredLang);
                filterCall.enqueue(articleListingResponseCallback);
                return;
            }

            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).getId().equals(articleId)) {
                            dataList.remove(i);
                            break;
                        }
                    }
                    if (dataList.size() < 5) {
                        Call<ArticleListingResponse> filterCall = topicsAPI.getTrendingArticles(1, 6, preferredLang);
                        filterCall.enqueue(articleListingResponseCallback);
                    } else {
                        articleList.addAll(dataList);
                        initializeViewPager();
                    }
                } else {
                    Call<ArticleListingResponse> filterCall = topicsAPI.getTrendingArticles(1, 6, preferredLang);
                    filterCall.enqueue(articleListingResponseCallback);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Call<ArticleListingResponse> filterCall = topicsAPI.getTrendingArticles(1, 6, preferredLang);
                filterCall.enqueue(articleListingResponseCallback);

            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            Call<ArticleListingResponse> filterCall = topicsAPI.getTrendingArticles(1, 6, preferredLang);
            filterCall.enqueue(articleListingResponseCallback);
        }
    };

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            if (response.body() == null) {
                initializeViewPager();
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).getId().equals(articleId)) {
                            dataList.remove(i);
                            break;
                        }
                    }
                    if (dataList == null || dataList.size() == 0) {
                        initializeViewPager();
                    } else {
                        articleList.addAll(dataList);
                        initializeViewPager();
                    }
                } else {
                    initializeViewPager();
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                initializeViewPager();
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            initializeViewPager();
        }
    };

    private void initializeViewPager() {
        mViewPagerAdapter = new ArticleDetailsPagerAdapter(getSupportFragmentManager(), articleList.size(), articleList, "dw", parentId, newArticleDetailFlag);
        mViewPager.setAdapter(mViewPagerAdapter);

        mViewPager.setOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Intent readArticleIntent = new Intent(ArticleDetailsContainerActivity.this, ReadArticleService.class);
                stopService(readArticleIntent);
                Log.d("-----AZURE----", "STOPPING");
                if (m_syn != null) {
                    m_syn.stopSound();
                }
                playTtsTextView.setImageDrawable(ContextCompat.getDrawable(ArticleDetailsContainerActivity.this, R.drawable.ic_play_tts));
                if (isAudioPlaying) {
                    ArticleDetailsFragment articleDetailsFragment = ((ArticleDetailsFragment) mViewPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem()));
                    long duration = (System.currentTimeMillis() - audioStartTime) / 1000;
                    Utils.pushStopArticleAudioEvent(ArticleDetailsContainerActivity.this, "DetailArticleScreen", userDynamoId + "", articleDetailsFragment.getGTMArticleId(), articleDetailsFragment.getGTMAuthor(),
                            articleDetailsFragment.getGTMLanguage(), "" + duration);
                }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void checkAudioPlaying() {
        if (m_syn != null) {
            m_syn.stopSound();
        }
        Intent readArticleIntent = new Intent(ArticleDetailsContainerActivity.this, ReadArticleService.class);
        stopService(readArticleIntent);
        playTtsTextView.setImageDrawable(ContextCompat.getDrawable(ArticleDetailsContainerActivity.this, R.drawable.ic_play_tts));
        isAudioPlaying = false;

    }

    @Override
    protected void onPause() {
        super.onPause();
        checkAudioPlaying();
    }
}