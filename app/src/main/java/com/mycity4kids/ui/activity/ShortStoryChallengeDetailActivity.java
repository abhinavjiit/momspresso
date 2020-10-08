package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycity4kids.R;
import com.mycity4kids.ShortStoryDetailPagerAdapter;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.ExploreTopicsModel;
import com.mycity4kids.models.ExploreTopicsResponse;
import com.mycity4kids.models.Topics;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.widget.MomspressoButtonWidget;
import com.squareup.picasso.Picasso;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ShortStoryChallengeDetailActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<ExploreTopicsModel> ssTopicsList;
    private String selectedId;
    private RelativeLayout chooseLayout;
    private String ssTopicsText;
    private RelativeLayout root;
    private String shortStoryCategoryId;
    private TextView startWriting;
    private TextView toolbarTitle;
    private RelativeLayout guideOverlay;
    private RadioGroup chooseoptionradioButton;
    private Toolbar toolbar;
    private Topics challengeCategory;
    private String challengeSlug;

    private ViewPager viewPager;
    private TabLayout tabs;
    private ImageView ChallengeNameImage;
    private ShortStoryDetailPagerAdapter shortStoryDetailPagerAdapter;
    private MomspressoButtonWidget submit_story_text;
    private View overlayView_choose_story_challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challnege_detail_listing);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setActivity(this);

        viewPager = findViewById(R.id.viewPager);
        tabs = findViewById(R.id.tabs);
        ChallengeNameImage = findViewById(R.id.ChallengeNameImage);
        submit_story_text = findViewById(R.id.submit_story_text);
        toolbar = findViewById(R.id.toolbar);
        startWriting = findViewById(R.id.start_writing);
        chooseLayout = findViewById(R.id.choose_layout);
        toolbarTitle = findViewById(R.id.toolbarTitleTextView);
        guideOverlay = findViewById(R.id.guideOverlay);
        chooseoptionradioButton = findViewById(R.id.reportReasonRadioGroup);
        overlayView_choose_story_challenge = findViewById(R.id.overlayView_choose_story_challenge);

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
        toolbarTitle.setText(getString(R.string.article_listing_type_short_story_label));

        ssTopicsList = new ArrayList<>();
        guideOverlay.setOnClickListener(this);
        startWriting.setOnClickListener(this);
        submit_story_text.setOnClickListener(this);
        overlayView_choose_story_challenge.setOnClickListener(this);

        Intent intent = getIntent();
        selectedId = intent.getStringExtra("challenge");
        challengeSlug = intent.getStringExtra("challengeSlug");
        if (selectedId != null) {
            getChallengeDetails(selectedId);
        } else if (challengeSlug != null) {
            getChallengeDetailsFromSlug(challengeSlug);
        } else {
            return;
        }

        try {
            FileInputStream fileInputStream = BaseApplication.getAppContext()
                    .openFileInput(AppConstants.CATEGORIES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
            ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
            createTopicsData(res);
            addShortStoryCategories(chooseoptionradioButton);
        } catch (FileNotFoundException e) {
            Retrofit retr = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsApi = retr.create(TopicsCategoryAPI.class);
            Call<ResponseBody> caller = topicsApi.downloadTopicsJSON();
            caller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE,
                            response.body());
                    try {
                        FileInputStream fileInputStream = openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                        ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
                        createTopicsData(res);
                        addShortStoryCategories(chooseoptionradioButton);
                    } catch (FileNotFoundException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("FileNotFoundException", Log.getStackTraceString(e));
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4KException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4KException", Log.getStackTraceString(t));
                }
            });
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4KException", Log.getStackTraceString(e));
        }

        chooseoptionradioButton.setOnCheckedChangeListener((radioGroup, i) -> {
            ssTopicsText = ssTopicsList.get(i).getDisplay_name();
            shortStoryCategoryId = ssTopicsList.get(i).getId();
        });
    }

    private void getChallengeDetailsFromSlug(String challengeSlug) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI categoryDetailApi = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<Topics> call = categoryDetailApi.getCategoryDetailsFromSlug(challengeSlug);
        call.enqueue(categoryDetailsResponseCallback);
    }

    private void getChallengeDetails(String challengeId) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI categoryDetailApi = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<Topics> call = categoryDetailApi.getCategoryDetails(challengeId);
        call.enqueue(categoryDetailsResponseCallback);
    }

    private Callback<Topics> categoryDetailsResponseCallback = new Callback<Topics>() {
        @Override
        public void onResponse(Call<Topics> call, Response<Topics> response) {
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            if (response.isSuccessful()) {
                try {
                    Topics responseData = response.body();
                    challengeCategory = responseData;
                    selectedId = responseData.getId();
                    Picasso.get().load(responseData.getExtraData().get(0).getChallenge().getImageUrl())
                            .error(R.drawable.default_article).into(ChallengeNameImage);

                    shortStoryDetailPagerAdapter = new ShortStoryDetailPagerAdapter(getSupportFragmentManager(),
                            selectedId);
                    tabs.addTab(tabs.newTab().setText("Story"));
                    tabs.addTab(tabs.newTab().setText("Winner"));
                    viewPager.setCurrentItem(0);
                    viewPager.setAdapter(shortStoryDetailPagerAdapter);
                    viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
                    tabs.addOnTabSelectedListener(new OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(Tab tab) {
                            viewPager.setCurrentItem(tab.getPosition());
                        }

                        @Override
                        public void onTabUnselected(Tab tab) {

                        }

                        @Override
                        public void onTabReselected(Tab tab) {
                            viewPager.setCurrentItem(tab.getPosition());

                        }
                    });
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }
        }

        @Override
        public void onFailure(Call<Topics> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void addShortStoryCategories(RadioGroup chooseoptionradioButton) {
        RadioGroup.LayoutParams rprms = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < ssTopicsList.size(); i++) {
            AppCompatRadioButton rbn = new AppCompatRadioButton(this);
            rbn.setId(i);
            rbn.setTextColor(getResources().getColor(R.color.short_story_light_black_color));
            rbn.setText(ssTopicsList.get(i).getDisplay_name());
            chooseoptionradioButton.addView(rbn, rprms);
            rbn.setPaddingRelative(10, 0, 0, 0);
            if (Build.VERSION.SDK_INT >= 21) {
                ColorStateList colorStateList = new ColorStateList(
                        new int[][] {
                                new int[] {-android.R.attr.state_enabled},
                                new int[] {android.R.attr.state_enabled}
                        },
                        new int[] {
                                getResources().getColor(R.color.app_red),
                                getResources().getColor(R.color.app_red)
                        });
                rbn.setButtonTintList(colorStateList);
            }
        }
    }

    private void createTopicsData(ExploreTopicsResponse responseData) {
        try {
            for (int i = 0; i < responseData.getData().size(); i++) {
                if (AppConstants.SHORT_STORY_CATEGORYID.equals(responseData.getData().get(i).getId())) {
                    for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
                        //DO NOT REMOVE below commented check -- showInMenu 0 from backend
                        // --might be used to show/hide in future
                        if ("1".equals(responseData.getData().get(i).getChild().get(j).getPublicVisibility())) {
                            ssTopicsList.add(responseData.getData().get(i).getChild().get(j));
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.writeArticleCell:
            case R.id.submit_story_text:
                Utils.shareEventTracking(this, "Story Challenge", "Story_Challenges_Android", "H_SCD_CTA1_Challenge");
                chooseLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.start_writing:
                Utils.shareEventTracking(this, "Story Challenge", "Story_Challenges_Android", "H_SCD_CTA2_Challenge");
                if (ssTopicsText != null
                        && challengeCategory != null
                        && !StringUtils.isNullOrEmpty(challengeCategory.getDisplay_name())
                        && challengeCategory.getExtraData() != null
                        && !challengeCategory.getExtraData().isEmpty()
                        && challengeCategory.getExtraData().get(0).getChallenge() != null
                        && !StringUtils
                        .isNullOrEmpty(challengeCategory.getExtraData().get(0).getChallenge().getImageUrl())) {
                    Intent intentt = new Intent(this, AddShortStoryActivity.class);
                    intentt.putExtra("selectedrequest", "challenge");
                    intentt.putExtra("challengeId", selectedId);
                    intentt.putExtra("challengeName", challengeCategory.getDisplay_name());
                    intentt.putExtra("Url", challengeCategory.getExtraData().get(0).getChallenge().getImageUrl());
                    intentt.putExtra("selectedCategory", ssTopicsText);
                    intentt.putExtra("shortStoryCategoryId", shortStoryCategoryId);
                    startActivity(intentt);
                } else {
                    Toast.makeText(this, R.string.select_atleast_one_topic, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.overlayView_choose_story_challenge:
                chooseLayout.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BaseApplication) getApplication()).setView(root);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        chooseLayout.setVisibility(View.INVISIBLE);
    }
}
