package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.BranchModel;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.request.VlogsEventRequest;
import com.mycity4kids.models.response.AllDraftsResponse;
import com.mycity4kids.models.response.BlogPageResponse;
import com.mycity4kids.models.response.DeepLinkingResposnse;
import com.mycity4kids.models.response.DeepLinkingResult;
import com.mycity4kids.models.response.DraftListResult;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.ShortStoryDetailResult;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.version.RateVersion;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.MyMoneyRegistrationDialogFragment;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDraftAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BlogPageAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.DeepLinkingAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.activity.collection.CollectionsActivity;
import com.mycity4kids.ui.activity.collection.UserCollectionItemListActivity;
import com.mycity4kids.ui.adapter.UserAllDraftsRecyclerAdapter;
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity;
import com.mycity4kids.ui.fragment.BecomeBloggerFragment;
import com.mycity4kids.ui.fragment.ChangePreferredLanguageDialogFragment;
import com.mycity4kids.ui.fragment.ChooseVideoUploadOptionDialogFragment;
import com.mycity4kids.ui.fragment.FragmentMC4KHomeNew;
import com.mycity4kids.ui.fragment.GroupsViewFragment;
import com.mycity4kids.ui.fragment.RateAppDialogFragment;
import com.mycity4kids.ui.fragment.SuggestedTopicsFragment;
import com.mycity4kids.ui.fragment.UploadVideoInfoFragment;
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity;
import com.mycity4kids.ui.rewards.activity.RewardsShareReferralCodeActivity;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.MixPanelUtils;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.videotrimmer.utils.FileUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class DashboardActivity extends BaseActivity implements View.OnClickListener, FragmentManager.OnBackStackChangedListener,
        GroupMembershipStatus.IMembershipStatus, UserAllDraftsRecyclerAdapter.DraftRecyclerViewClickListener {

    private FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    private int num_of_challeneges;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_GALLERY_PERMISSION = 2;
    private static String[] PERMISSIONS_STORAGE_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private String selectedrequest = "default";
    private String shortstory = "ShortStory";
    private String challenge = "ChallengeTake";
    private String FromDeepLink = "FromDeepLink";
    public static final String COMMON_PREF_FILE = "my_city_prefs";
    String Display_Name;
    private String challengeId;
    private String ImageUrl;
    private String shortStoryChallengesList;
    private String deepLinkDisplayName;
    private String deepLinkImageUrl;
    private ArrayList<Topics> shortStoriesTopicList;
    int groupId;
    Map<String, String> image;
    public boolean filter = false;
    Tracker t;
    private String deepLinkUrl;
    private String mToolbarTitle = "";
    private String fragmentToLoad = "";
    private Animation slideDownAnim;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar, toolbar0, toolbar1, toolbar2;
    private TextView toolbarTitleTextView;
    private ImageView searchAllImageView, notificationImg;
    private BottomNavigationViewEx bottomNavigationView;
    private RelativeLayout toolbarRelativeLayout;
    private RelativeLayout rootLayout;
    private ImageView downArrowImageView;
    private TextView selectOptToolbarTitle;
    private Badge badge;
    private View toolbarUnderline, langView;
    private TextView langTextView;
    private FrameLayout transparentLayerToolbar;
    private FrameLayout transparentLayerNavigation;
    private RelativeLayout groupCoachmark, firstCoachmark, secondCoachmark;
    private TextView selectedlangGuideTextView;
    private MixpanelAPI mMixpanel;
    private RelativeLayout bookmarkInfoView;
    private TextView viewBookmarkedArticleTextView;
    private ImageView profileImageView;
    private Animation slideAnim, fadeAnim;
    private LinearLayout actionItemContainer, articleContainer, videoContainer, storyContainer;
    private View overlayView;
    private RelativeLayout createContentContainer;
    private TextView usernameTextView, coachUsernameTextView, videosTextView, shortStoryTextView,
            momspressoTextView, groupsTextView, bookmarksTextView, settingTextView, referral;
    private LinearLayout drawerTopContainer, drawerContainer, rewardsTextView;
    private RelativeLayout drawerSettingsContainer;
    private TextView homeTextView;
    private RelativeLayout homeCoachmark, exploreCoachmark, createCoachmark, vlogsCoachmark, drawerProfileCoachmark,
            drawerSettingsCoachmark, menuCoachmark, languageLayout, drawerMyMoneyCoachmark, drawerMyMoneyContainer;
    private RecyclerView draftsRecyclerView;
    private ShimmerFrameLayout draftsShimmerLayout;
    private TextView createLabelTextView, continueWritingLabelTV;
    private ImageView createTextImageVIew, langImageRightArrow;
    private ArrayList<AllDraftsResponse.AllDraftsData.AllDraftsResult> allDraftsList = new ArrayList<>();
    private UserAllDraftsRecyclerAdapter userAllDraftsRecyclerAdapter;

    private TextView selectedLangTextView;
    private TopicsResponse res;
    private int num_of_categorys;
    private String branchVideoChallengeId, currentVersion, onlineVersionCode;
    private FrameLayout root;
    private Boolean rateNowDialog = false;
    private String UPDATE_APP_POPUP_KEY = "latest_app_version";
    private int frequecy;
    private String UPDATE_APP_FREQUENCY_KEY = "app_update_frequency";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BaseApplication.startSocket();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(720).build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        if (SharedPrefUtils.getFirebaseRemoteConfigUpdateFlag(BaseApplication.getAppContext())) {
            showProgressDialog(getString(R.string.please_wait));
            mFirebaseRemoteConfig.fetch(0).addOnCompleteListener(this, task -> {
                removeProgressDialog();
                mFirebaseRemoteConfig.activate();
                SharedPrefUtils.setFirebaseRemoteConfigUpdateFlag(BaseApplication.getAppContext(), false);
            });
        } else {
            mFirebaseRemoteConfig.fetchAndActivate()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.d("FirebaseRemoteConfig", "Config params updated: " + updated);
                        } else {
                        }
                    });
        }
        root = findViewById(R.id.dash_root);
        ((BaseApplication) getApplication()).setActivity(this);

        t = ((BaseApplication) getApplication()).getTracker(
                BaseApplication.TrackerName.APP_TRACKER);
        t.enableAdvertisingIdCollection(true);
        t.setScreenName("DashBoard");
        t.set("&uid", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("User Sign In").build());
        t.send(new HitBuilders.ScreenViewBuilder().build());

        mMixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        Utils.pushGenericEvent(this, "Dashboard_event", SharedPrefUtils.getUserDetailModel(this).getDynamoId(), "DashboardActivity");
        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        onlineVersionCode = mFirebaseRemoteConfig.getString(UPDATE_APP_POPUP_KEY);
        try {
            frequecy = Integer.parseInt(mFirebaseRemoteConfig.getString(UPDATE_APP_FREQUENCY_KEY));
        } catch (NumberFormatException e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        Intent intent = getIntent();
        appUpdatePopUp();
        onNewIntent(intent);

        if (null != intent.getParcelableExtra("notificationExtras")) {
            if ("upcoming_event_list".equals(((Bundle) intent.getParcelableExtra("notificationExtras")).getString("type")))
                fragmentToLoad = Constants.BUSINESS_EVENTLIST_FRAGMENT;
        }
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        langTextView = (TextView) findViewById(R.id.langTextView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar1 = (Toolbar) findViewById(R.id.toolbar1);
        toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        downArrowImageView = (ImageView) findViewById(R.id.downArrowImageView);
        toolbarUnderline = findViewById(R.id.toolbarUnderline);
        bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.navigation);
        toolbarRelativeLayout = (RelativeLayout) mToolbar.findViewById(R.id.toolbarRelativeLayout);
        toolbarTitleTextView = (TextView) mToolbar.findViewById(R.id.toolbarTitle);
        searchAllImageView = (ImageView) mToolbar.findViewById(R.id.searchAllImageView);
        notificationImg = (ImageView) mToolbar.findViewById(R.id.notification);
        selectOptToolbarTitle = (TextView) findViewById(R.id.selectOptToolbarTitle);
        langTextView = (TextView) findViewById(R.id.langTextView);
        selectedLangTextView = (TextView) findViewById(R.id.selectedLangtext);
        langImageRightArrow = (ImageView) findViewById(R.id.langImageRightArrow);
        selectedlangGuideTextView = (TextView) findViewById(R.id.selectedlangGuideTextView);
        groupCoachmark = (RelativeLayout) findViewById(R.id.groupCoachmark);
        firstCoachmark = (RelativeLayout) findViewById(R.id.firstCoachmark);
        secondCoachmark = (RelativeLayout) findViewById(R.id.secondCoachmark);
        transparentLayerToolbar = (FrameLayout) findViewById(R.id.transparentLayerToolbar);
        transparentLayerNavigation = (FrameLayout) findViewById(R.id.transparentLayerNavigation);
        bookmarkInfoView = (RelativeLayout) findViewById(R.id.bookmarkInfoView);
        viewBookmarkedArticleTextView = (TextView) findViewById(R.id.viewBookmarkedArticleTextView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        profileImageView = (ImageView) findViewById(R.id.profileImageView);
        actionItemContainer = (LinearLayout) findViewById(R.id.actionItemContainer);
        createContentContainer = (RelativeLayout) findViewById(R.id.createContentContainer);
        overlayView = findViewById(R.id.overlayView);
        articleContainer = (LinearLayout) findViewById(R.id.articleContainer);
        videoContainer = (LinearLayout) findViewById(R.id.videoContainer);
        storyContainer = (LinearLayout) findViewById(R.id.storyContainer);
        videosTextView = (TextView) findViewById(R.id.videosTextView);
        shortStoryTextView = (TextView) findViewById(R.id.shortStoryTextView);
        momspressoTextView = (TextView) findViewById(R.id.momspressoTextView);
        groupsTextView = (TextView) findViewById(R.id.groupsTextView);
        rewardsTextView = (LinearLayout) findViewById(R.id.rewardsTextView);
        bookmarksTextView = (TextView) findViewById(R.id.bookmarksTextView);
        settingTextView = (TextView) findViewById(R.id.settingTextView);
        usernameTextView = (TextView) findViewById(R.id.usernameTextView);
        coachUsernameTextView = (TextView) findViewById(R.id.coachUsernameTextView);
        homeTextView = (TextView) findViewById(R.id.homeTextView);
        drawerTopContainer = (LinearLayout) findViewById(R.id.topContainer);
        drawerContainer = (LinearLayout) findViewById(R.id.drawerProfileContainer);
        drawerMyMoneyContainer = (RelativeLayout) findViewById(R.id.drawerMyMoneyContainer);
        drawerSettingsContainer = (RelativeLayout) findViewById(R.id.drawerSettingsContainer);
        homeCoachmark = (RelativeLayout) findViewById(R.id.homeCoachmark);
        exploreCoachmark = (RelativeLayout) findViewById(R.id.exploreCoachmark);
        createCoachmark = (RelativeLayout) findViewById(R.id.createCoachmark);
        vlogsCoachmark = (RelativeLayout) findViewById(R.id.vlogsCoachmark);
        menuCoachmark = (RelativeLayout) findViewById(R.id.menuCoachmark);
        drawerProfileCoachmark = (RelativeLayout) findViewById(R.id.drawerProfileCoachmark);
        drawerSettingsCoachmark = (RelativeLayout) findViewById(R.id.drawerSettingsCoachmark);
        drawerMyMoneyCoachmark = (RelativeLayout) findViewById(R.id.drawerMyMoneyCoachmark);
        draftsRecyclerView = (RecyclerView) findViewById(R.id.draftsRecyclerView);
        draftsShimmerLayout = (ShimmerFrameLayout) findViewById(R.id.draftsShimmerLayout);
        createLabelTextView = (TextView) findViewById(R.id.createLabelTextView);
        continueWritingLabelTV = (TextView) findViewById(R.id.continueWritingLabelTV);
        createTextImageVIew = (ImageView) findViewById(R.id.createTextImageVIew);
        languageLayout = (RelativeLayout) findViewById(R.id.languageLayout);
        referral = findViewById(R.id.referral);
        langView = (View) findViewById(R.id.langView);
        homeCoachmark.setOnClickListener(this);
        langView.setOnClickListener(this);
        languageLayout.setOnClickListener(this);
        exploreCoachmark.setOnClickListener(this);
        createCoachmark.setOnClickListener(this);
        vlogsCoachmark.setOnClickListener(this);
        menuCoachmark.setOnClickListener(this);
        drawerProfileCoachmark.setOnClickListener(this);
        drawerSettingsCoachmark.setOnClickListener(this);
        drawerMyMoneyCoachmark.setOnClickListener(this);

        referral.setOnClickListener(this);
        settingTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_app_settings), null, null, null);
        videosTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_mom_vlogs), null, null, null);
        homeTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.drawer_home_red), null, null, null);
        selectedLangTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_language), null, null, null);
        referral.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.share_dra), null, null, null);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        draftsRecyclerView.setLayoutManager(llm);
        userAllDraftsRecyclerAdapter = new UserAllDraftsRecyclerAdapter(this, this);
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.setTextVisibility(true);
        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.hamburger_menu);
        upArrow.setColorFilter(getResources().getColor(R.color.app_red), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().show();

        Utils.pushOpenScreenEvent(this, "DashboardScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        downArrowImageView.setOnClickListener(this);
        searchAllImageView.setOnClickListener(this);
        notificationImg.setOnClickListener(this);
        toolbarTitleTextView.setOnClickListener(this);
        searchAllImageView.setOnClickListener(this);
        overlayView.setOnClickListener(this);
        articleContainer.setOnClickListener(this);
        storyContainer.setOnClickListener(this);
        videoContainer.setOnClickListener(this);
        shortStoryTextView.setOnClickListener(this);
        momspressoTextView.setOnClickListener(this);
        groupsTextView.setOnClickListener(this);
        rewardsTextView.setOnClickListener(this);
        bookmarksTextView.setOnClickListener(this);
        videosTextView.setOnClickListener(this);
        settingTextView.setOnClickListener(this);
        homeTextView.setOnClickListener(this);
        langTextView.setOnClickListener(this);
        groupCoachmark.setOnClickListener(this);
        firstCoachmark.setOnClickListener(this);
        secondCoachmark.setOnClickListener(this);
        viewBookmarkedArticleTextView.setOnClickListener(this);
        profileImageView.setOnClickListener(this);
        drawerTopContainer.setOnClickListener(this);
        slideAnim = AnimationUtils.loadAnimation(this, R.anim.appear_from_bottom);
        fadeAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        slideDownAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_down_from_top);
        slideDownAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bookmarkInfoView.setVisibility(View.GONE);
                    }
                }, 2000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset < 1) {
                    drawerProfileCoachmark.setVisibility(View.GONE);
                    drawerMyMoneyCoachmark.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (!SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "Drawer")) {
                    drawerContainer.getLayoutParams().width = drawerView.getWidth();
                    drawerMyMoneyContainer.getLayoutParams().width = drawerView.getWidth();
                    drawerSettingsContainer.getLayoutParams().width = drawerView.getWidth();
                    drawerContainer.requestLayout();
                    drawerMyMoneyContainer.requestLayout();
                    drawerSettingsContainer.requestLayout();
                    drawerProfileCoachmark.setVisibility(View.VISIBLE);
                    if (AppConstants.LOCALE_ENGLISH.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                        langTextView.setText(getString(R.string.language_label_english));
                        selectedlangGuideTextView.setText(getString(R.string.language_label_english));
                    } else if (AppConstants.LOCALE_HINDI.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                        langTextView.setText(getString(R.string.language_label_hindi));
                        selectedlangGuideTextView.setText(getString(R.string.language_label_hindi));
                    } else if (AppConstants.LOCALE_MARATHI.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                        langTextView.setText(getString(R.string.language_label_marathi));
                        selectedlangGuideTextView.setText(getString(R.string.language_label_marathi));
                    } else if (AppConstants.LOCALE_BENGALI.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                        langTextView.setText(getString(R.string.language_label_bengali));
                        selectedlangGuideTextView.setText(getString(R.string.language_label_bengali));
                    } else if (AppConstants.LOCALE_TAMIL.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                        langTextView.setText(getString(R.string.language_label_tamil));
                        selectedlangGuideTextView.setText(getString(R.string.language_label_tamil));
                    } else if (AppConstants.LOCALE_TELUGU.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                        langTextView.setText(getString(R.string.language_label_telegu));
                        selectedlangGuideTextView.setText(getString(R.string.language_label_telegu));
                    } else if (AppConstants.LOCALE_KANNADA.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                        selectedLangTextView.setText(getString(R.string.language_label_kannada));
                        langTextView.setText(getString(R.string.language_label_kannada));
                        selectedlangGuideTextView.setText(getString(R.string.language_label_kannada));
                    } else if (AppConstants.LOCALE_MALAYALAM.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                        langTextView.setText(getString(R.string.language_label_malayalam));
                        selectedlangGuideTextView.setText(getString(R.string.language_label_malayalam));
                    } else if (AppConstants.LOCALE_GUJARATI.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                        langTextView.setText(getString(R.string.language_label_gujarati));
                        selectedlangGuideTextView.setText(getString(R.string.language_label_gujarati));
                    } else if (AppConstants.LOCALE_PUNJABI.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                        langTextView.setText(getString(R.string.language_label_punjabi));
                        selectedlangGuideTextView.setText(getString(R.string.language_label_punjabi));
                    } else {
                        langTextView.setText(getString(R.string.language_label_english));
                        selectedlangGuideTextView.setText(getString(R.string.language_label_english));
                    }
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (AppConstants.LOCALE_ENGLISH.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                    langTextView.setText(getString(R.string.language_label_english));
                    selectedlangGuideTextView.setText(getString(R.string.language_label_english));
                } else if (AppConstants.LOCALE_HINDI.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                    langTextView.setText(getString(R.string.language_label_hindi));
                    selectedlangGuideTextView.setText(getString(R.string.language_label_hindi));
                } else if (AppConstants.LOCALE_MARATHI.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                    langTextView.setText(getString(R.string.language_label_marathi));
                    selectedlangGuideTextView.setText(getString(R.string.language_label_marathi));
                } else if (AppConstants.LOCALE_BENGALI.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                    langTextView.setText(getString(R.string.language_label_bengali));
                    selectedlangGuideTextView.setText(getString(R.string.language_label_bengali));
                } else if (AppConstants.LOCALE_TAMIL.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                    langTextView.setText(getString(R.string.language_label_tamil));
                    selectedlangGuideTextView.setText(getString(R.string.language_label_tamil));
                } else if (AppConstants.LOCALE_TELUGU.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                    langTextView.setText(getString(R.string.language_label_telegu));
                    selectedlangGuideTextView.setText(getString(R.string.language_label_telegu));
                } else if (AppConstants.LOCALE_KANNADA.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                    selectedLangTextView.setText(getString(R.string.language_label_kannada));
                    langTextView.setText(getString(R.string.language_label_kannada));
                    selectedlangGuideTextView.setText(getString(R.string.language_label_kannada));
                } else if (AppConstants.LOCALE_MALAYALAM.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                    langTextView.setText(getString(R.string.language_label_malayalam));
                    selectedlangGuideTextView.setText(getString(R.string.language_label_malayalam));
                } else if (AppConstants.LOCALE_GUJARATI.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                    langTextView.setText(getString(R.string.language_label_gujarati));
                    selectedlangGuideTextView.setText(getString(R.string.language_label_gujarati));
                } else if (AppConstants.LOCALE_PUNJABI.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                    langTextView.setText(getString(R.string.language_label_punjabi));
                    selectedlangGuideTextView.setText(getString(R.string.language_label_punjabi));
                } else {
                    langTextView.setText(getString(R.string.language_label_english));
                    selectedlangGuideTextView.setText(getString(R.string.language_label_english));
                }
            }
        });
        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(BaseApplication.getAppContext()))) {
            Picasso.get().load(SharedPrefUtils.getProfileImgUrl(BaseApplication.getAppContext())).placeholder(R.drawable.family_xxhdpi)
                    .error(R.drawable.family_xxhdpi).into(profileImageView);
        }
        usernameTextView.setText(SharedPrefUtils.getUserDetailModel(this).getFirst_name() + " " + SharedPrefUtils.getUserDetailModel(this).getLast_name());
        coachUsernameTextView.setText(SharedPrefUtils.getUserDetailModel(this).getFirst_name() + " " + SharedPrefUtils.getUserDetailModel(this).getLast_name());

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger_menu);

        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setIconSize(24, 24);
        bottomNavigationView.setTextSize(12.0f);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    final Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.action_profile:
                            hideCreateContentView();
                            if (topFragment instanceof ExploreArticleListingTypeFragment) {
                                return true;
                            }
                            ExploreArticleListingTypeFragment fragment0 = new ExploreArticleListingTypeFragment();
                            Bundle mBundle0 = new Bundle();
                            fragment0.setArguments(mBundle0);
                            addFragment(fragment0, mBundle0, true);
                            break;
                        case R.id.action_momVlog:
                            MixPanelUtils.pushMomVlogsDrawerClickEvent(mMixpanel);
                            Utils.momVlogEvent(DashboardActivity.this, "Home Screen", "Bottom_nav_videos",
                                    "", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                                    String.valueOf(System.currentTimeMillis()), "Show_Video_Listing", "", "");
                            Intent cityIntent = new Intent(DashboardActivity.this, CategoryVideosListingActivity.class);
                            cityIntent.putExtra("parentTopicId", AppConstants.HOME_VIDEOS_CATEGORYID);
                            startActivity(cityIntent);
                            break;
                        case R.id.action_home:
                            hideCreateContentView();
                            if (topFragment instanceof FragmentMC4KHomeNew) {
                                return true;
                            }
                            FragmentMC4KHomeNew fragment1 = new FragmentMC4KHomeNew();
                            Bundle mBundle1 = new Bundle();
                            fragment1.setArguments(mBundle1);
                            addFragment(fragment1, mBundle1, true);
                            break;
                        case R.id.action_write:
                            userAllDraftsRecyclerAdapter.notifyDataSetChanged();
                            if (createContentContainer.getVisibility() == View.VISIBLE) {

                            } else {
                                allDraftsList.clear();
                                loadAllDrafts();
                                createContentContainer.setVisibility(View.VISIBLE);
                                actionItemContainer.setVisibility(View.VISIBLE);
                                overlayView.setVisibility(View.VISIBLE);
                                actionItemContainer.startAnimation(slideAnim);
                                overlayView.startAnimation(fadeAnim);
                            }
                            break;
                        case R.id.action_location:
                            hideCreateContentView();
                            if (topFragment instanceof GroupsViewFragment) {
                                return true;
                            }
                            Utils.groupsEvent(DashboardActivity.this, "Home Screen", "Group_bottom_nav", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Group_listing", "", "");
                            GroupsViewFragment groupsFragment = new GroupsViewFragment();
                            Bundle eBundle = new Bundle();
                            groupsFragment.setArguments(eBundle);
                            addFragment(groupsFragment, eBundle, true);
                            break;
                    }
                    return true;
                });
        if (Constants.PROFILE_FRAGMENT.equals(fragmentToLoad)) {
            Intent pIntent = new Intent(this, UserProfileActivity.class);
            Bundle notificationExtras = getIntent().getParcelableExtra("notificationExtras");
            pIntent.putExtra(Constants.USER_ID, SharedPrefUtils.getUserDetailModel(this).getDynamoId());
            pIntent.putExtra(AppConstants.BADGE_ID, notificationExtras.getString(AppConstants.BADGE_ID));
            pIntent.putExtra(AppConstants.MILESTONE_ID, notificationExtras.getString(AppConstants.MILESTONE_ID));
            startActivity(pIntent);
        } else if (Constants.SUGGESTED_TOPICS_FRAGMENT.equals(fragmentToLoad)) {
            SuggestedTopicsFragment fragment0 = new SuggestedTopicsFragment();
            Bundle mBundle0 = new Bundle();
            fragment0.setArguments(mBundle0);
            addFragment(fragment0, mBundle0, true);
        } else if (Constants.SHORT_STOY_FRAGMENT.equals(fragmentToLoad)) {
            TopicsShortStoriesContainerFragment fragment1 = new TopicsShortStoriesContainerFragment();
            Bundle mBundle1 = new Bundle();
            mBundle1.putString("parentTopicId", AppConstants.SHORT_STORY_CATEGORYID);
            fragment1.setArguments(mBundle1);
            addFragment(fragment1, mBundle1, true);
        } else if (Constants.GROUP_LISTING_FRAGMENT.equals(fragmentToLoad)) {
            GroupsViewFragment fragment1 = new GroupsViewFragment();
            Bundle mBundle1 = new Bundle();
            fragment1.setArguments(mBundle1);
            addFragment(fragment1, mBundle1, true);
        } else if (Constants.CREATE_CONTENT_PROMPT.equals(fragmentToLoad)) {
            replaceFragment(new FragmentMC4KHomeNew(), null, false);
            bottomNavigationView.setSelectedItemId(R.id.action_write);
        } else {
            replaceFragment(new FragmentMC4KHomeNew(), null, false);
            String tabType = getIntent().getStringExtra("TabType");
            if ("profile".equals(tabType)) {
                bottomNavigationView.setSelectedItemId(R.id.action_profile);
            } else if ("group".equals(tabType)) {
                bottomNavigationView.setSelectedItemId(R.id.action_location);
            }
        }

        RateVersion reteVersionModel = SharedPrefUtils.getRateVersion(BaseApplication.getAppContext());
        int currentRateVersion = reteVersionModel.getAppRateVersion();
        currentRateVersion++;
        boolean isCompleteRateProcess = reteVersionModel.isAppRateComplete();
        RateVersion rateModel = new RateVersion();
        rateModel.setAppRateComplete(isCompleteRateProcess);
        rateModel.setAppRateVersion(currentRateVersion);
        SharedPrefUtils.setAppRateVersion(BaseApplication.getAppContext(), rateModel);
        if (!SharedPrefUtils.getRateVersion(BaseApplication.getAppContext()).isAppRateComplete() && currentRateVersion >= 10 && rateNowDialog) {
            RateAppDialogFragment rateAppDialogFragment = new RateAppDialogFragment();
            reteVersionModel.setAppRateVersion(-20);
            rateAppDialogFragment.show(getFragmentManager(), rateAppDialogFragment.getClass().getSimpleName());
        }
        getUsersData();
        String isRewardsAdded = SharedPrefUtils.getIsRewardsAdded(BaseApplication.getAppContext());
        if (getIntent().getBooleanExtra("isFromShare", false) && !isRewardsAdded.isEmpty()
                && isRewardsAdded.equals("0")) {
            showMyMoneyRegistrationPrompt(getIntent());
        }
    }

    private void showMyMoneyRegistrationPrompt(Intent _intent) {
        if (!_intent.hasExtra("branchLink") &&
                !_intent.hasExtra(AppConstants.BRANCH_DEEPLINK_URL) &&
                "1".equals(SharedPrefUtils.getUserDetailModel(this).getIsNewUser())) {
            MyMoneyRegistrationDialogFragment mmRegistrationDialogFragment = new MyMoneyRegistrationDialogFragment();
            FragmentManager fm = getSupportFragmentManager();
            Bundle _args = new Bundle();
            mmRegistrationDialogFragment.setArguments(_args);
            mmRegistrationDialogFragment.setCancelable(true);
            mmRegistrationDialogFragment.show(fm, "MyMoney");
        }
    }

    private void loadAllDrafts() {
        draftsShimmerLayout.setVisibility(View.VISIBLE);
        draftsShimmerLayout.startShimmerAnimation();
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ArticleDraftAPI draftAPI = retrofit.create(ArticleDraftAPI.class);
        Call<ResponseBody> call = draftAPI.getAllDrafts("0");
        //  Call<ResponseBody> call = draftAPI.getAllDrafts("0");
        call.enqueue(draftsResponseCallback);
    }

    private void getUsersData() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        String userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        if (!userId.isEmpty()) {
            Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(userId);
            call.enqueue(userDetailsResponseListener);
        }
    }

    private Callback<UserDetailResponse> userDetailsResponseListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                return;
            }
            try {
                UserDetailResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (responseData.getData() != null && responseData.getData().get(0) != null && responseData.getData().get(0).getResult() != null) {
                        SharedPrefUtils.setIsRewardsAdded(BaseApplication.getAppContext(), responseData.getData().get(0).getResult().getRewardsAdded());
                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            Crashlytics.logException(t);
            apiExceptions(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    private Callback<ResponseBody> draftsResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            draftsShimmerLayout.stopShimmerAnimation();
            draftsShimmerLayout.setVisibility(View.GONE);
            if (response.body() == null) {
                if (response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                createLabelTextView.setVisibility(View.VISIBLE);
                createTextImageVIew.setVisibility(View.VISIBLE);
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jObject = new JSONObject(resData);
                int code = jObject.getInt("code");
                String status = jObject.getString("status");
                if (code == 200 && Constants.SUCCESS.equals(status)) {
                    AllDraftsResponse draftListResponse = new AllDraftsResponse();
                    AllDraftsResponse.AllDraftsData draftListData = new AllDraftsResponse.AllDraftsData();
                    JSONArray dataObj = jObject.optJSONArray("data");
                    if (null != dataObj) {
                        //                     Empty Draft List Handling
                        ArrayList<AllDraftsResponse.AllDraftsData.AllDraftsResult> emptyDraftList = new ArrayList<>();
                        draftListData.setResult(emptyDraftList);
                        draftListResponse.setData(draftListData);
                        allDraftsList.addAll(draftListResponse.getData().getResult());
                        processDraftsResponse();
                        return;
                    }

                    JSONArray resultJsonObject = jObject.getJSONObject("data").optJSONArray("result");
                    ArrayList<AllDraftsResponse.AllDraftsData.AllDraftsResult> draftList = new ArrayList<>();
                    ArrayList<Map<String, String>> retMap;
                    for (int i = 0; i < resultJsonObject.length(); i++) {
                        AllDraftsResponse.AllDraftsData.AllDraftsResult draftitem = new AllDraftsResponse.AllDraftsData.AllDraftsResult();
                        draftitem.setId(resultJsonObject.getJSONObject(i).getString("id"));
                        draftitem.setArticleType(resultJsonObject.getJSONObject(i).getString("articleType"));
                        draftitem.setCreatedTime(resultJsonObject.getJSONObject(i).getString("createdTime"));
                        draftitem.setUpdatedTime(resultJsonObject.getJSONObject(i).getLong("updatedTime"));
                        draftitem.setBody(resultJsonObject.getJSONObject(i).getString("body"));
                        draftitem.setTitle(resultJsonObject.getJSONObject(i).getString("title"));
                        if (resultJsonObject.getJSONObject(i).has("contentType")) {
                            draftitem.setContentType(resultJsonObject.getJSONObject(i).getString("contentType"));

                        } else {
                            draftitem.setContentType("0");
                        }
                        if (resultJsonObject.getJSONObject(i).has("tags")) {
                            JSONArray tagsArray = resultJsonObject.getJSONObject(i).optJSONArray("tags");
                            if (null != tagsArray) {
                                retMap = new Gson().fromJson(tagsArray.toString(), new TypeToken<ArrayList<HashMap<String, String>>>() {
                                }.getType());
                                draftitem.setTags(retMap);
                            } else {
                                JSONArray jsArray = resultJsonObject.getJSONObject(i).getJSONObject("tags").optJSONArray("tagsArr");
                                retMap = new Gson().fromJson(jsArray.toString(), new TypeToken<ArrayList<HashMap<String, String>>>() {
                                }.getType());
                                draftitem.setTags(retMap);
                            }
                        } else {
                            //no tags key in the json
                            retMap = new ArrayList<Map<String, String>>();
                            draftitem.setTags(retMap);
                        }
                        draftList.add(draftitem);
                    }
                    draftListData.setResult(draftList);
                    draftListResponse.setData(draftListData);
                    allDraftsList.addAll(draftListResponse.getData().getResult());
                    processDraftsResponse();
                } else {
                    showToast(jObject.getString("reason"));
                }
            } catch (JSONException jsonexception) {
                Crashlytics.logException(jsonexception);
                Log.d("JSONException", Log.getStackTraceString(jsonexception));
                showToast("Something went wrong while parsing response from server");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            apiExceptions(t);
            draftsShimmerLayout.setVisibility(View.GONE);
            createLabelTextView.setVisibility(View.VISIBLE);
            createTextImageVIew.setVisibility(View.VISIBLE);
        }
    };

    private void processDraftsResponse() {
        if (allDraftsList.size() == 0) {
            createLabelTextView.setVisibility(View.VISIBLE);
            createTextImageVIew.setVisibility(View.VISIBLE);
        } else {
            createLabelTextView.setVisibility(View.INVISIBLE);
            createTextImageVIew.setVisibility(View.INVISIBLE);
            userAllDraftsRecyclerAdapter.setListData(allDraftsList);
            draftsRecyclerView.setAdapter(userAllDraftsRecyclerAdapter);
        }
    }

    //     The onNewIntent() is overridden to get and resolve the data for deep linking
    @Override
    protected void onNewIntent(Intent _intent) {
        super.onNewIntent(_intent);
        Bundle notificationExtras = _intent.getParcelableExtra("notificationExtras");
        if (notificationExtras != null) {
            try {
                for (String key : notificationExtras.keySet()) {
                    Log.e("notificationExtras", key + " : " + (notificationExtras.get(key) != null ? notificationExtras.get(key) : "NULL"));
                }
            } catch (Exception e) {
            }
            if (notificationExtras.getString("type").equals("remote_config_silent_update")) {
                showProgressDialog(getString(R.string.please_wait));
                mFirebaseRemoteConfig.fetch(0).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        removeProgressDialog();
                        mFirebaseRemoteConfig.activate();
                        SharedPrefUtils.setFirebaseRemoteConfigUpdateFlag(BaseApplication.getAppContext(), false);
                    }
                });
            } else if (notificationExtras.getString("type").equalsIgnoreCase("article_details")) {
                pushEvent("article_details");
                String articleId = notificationExtras.getString("id");
                String authorId = notificationExtras.getString("userId");
                String blogSlug = notificationExtras.getString("blogSlug");
                String titleSlug = notificationExtras.getString("titleSlug");
                Intent intent1 = new Intent(DashboardActivity.this, ArticleDetailsContainerActivity.class);
                intent1.putExtra(Constants.ARTICLE_ID, articleId);
                intent1.putExtra(Constants.AUTHOR_ID, authorId);
                intent1.putExtra(Constants.BLOG_SLUG, blogSlug);
                intent1.putExtra(Constants.TITLE_SLUG, titleSlug);
                intent1.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
                intent1.putExtra(Constants.FROM_SCREEN, "Notification");
                intent1.putExtra(Constants.ARTICLE_INDEX, "-1");
                intent1.putExtra(Constants.AUTHOR, authorId + "~");
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("collection_detail")) {
                pushEvent("collection_detail");
                Intent intent = new Intent(DashboardActivity.this, UserCollectionItemListActivity.class);
                intent.putExtra("id", notificationExtras.getString(AppConstants.COLLECTION_ID));
                startActivity(intent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("create_content_prompt")) {
                pushEvent("create_content_prompt");
                fragmentToLoad = Constants.CREATE_CONTENT_PROMPT;
            } else if (notificationExtras.getString("type").equalsIgnoreCase("momsights_screen")) {
                pushEvent("momsights_screen");
                Intent intent1 = new Intent(DashboardActivity.this, RewardsContainerActivity.class);
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("campaign_listing")) {
                pushEvent("campaign_listing");
                Intent campaignIntent = new Intent(this, CampaignContainerActivity.class);
                campaignIntent.putExtra("campaign_listing", "campaign_listing");
                startActivity(campaignIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("choose_video_category")) {
                pushEvent("choose_video_category");
                Intent createVideoIntent = new Intent(this, ChooseVideoCategoryActivity.class);
                createVideoIntent.putExtra("comingFrom", "notification");
                startActivity(createVideoIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("video_challenge_details")) {
                pushEvent("video_challenge_details");
                Intent videoChallengeIntent = new Intent(this, NewVideoChallengeActivity.class);
                videoChallengeIntent.putExtra(Constants.CHALLENGE_ID, "" + notificationExtras.getString("challengeId"));
                videoChallengeIntent.putExtra("comingFrom", "notification");
                startActivity(videoChallengeIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("campaign_detail")) {
                pushEvent("campaign_detail");
                Intent campaignIntent = new Intent(this, CampaignContainerActivity.class);
                campaignIntent.putExtra("campaign_id", notificationExtras.getString("campaign_id"));
                campaignIntent.putExtra("campaign_detail", "campaign_detail");
                campaignIntent.putExtra("fromNotification", true);
                startActivity(campaignIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("campaign_submit_proof")) {
                pushEvent("campaign_submit_proof");
                Intent campaignIntent = new Intent(this, CampaignContainerActivity.class);
                campaignIntent.putExtra("campaign_Id", notificationExtras.getString("campaign_id"));
                campaignIntent.putExtra("campaign_submit_proof", "campaign_submit_proof");
                startActivity(campaignIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("mymoney_bankdetails")) {
                pushEvent("mymoney_bankdetails");
                Intent campaignIntent = new Intent(this, RewardsContainerActivity.class);
                campaignIntent.putExtra("isComingfromCampaign", true);
                campaignIntent.putExtra("pageLimit", 4);
                campaignIntent.putExtra("pageNumber", 4);
                campaignIntent.putExtra("campaign_Id", notificationExtras.getString("campaign_id"));
                campaignIntent.putExtra("mymoney_bankdetails", "mymoney_bankdetails");
                startActivity(campaignIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("mymoney_pancard")) {
                pushEvent("mymoney_pancard");
                Intent campaignIntent = new Intent(this, RewardsContainerActivity.class);
                campaignIntent.putExtra("isComingFromRewards", true);
                campaignIntent.putExtra("pageLimit", 5);
                campaignIntent.putExtra("pageNumber", 5);
                campaignIntent.putExtra("panCardFormNotification", "mymoney_pancard");
                campaignIntent.putExtra("mymoney_pancard", "mymoney_pancard");
                startActivity(campaignIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("shortStoryDetails")) {
                pushEvent("shortStoryDetails");
                Intent ssIntent = new Intent(DashboardActivity.this, ShortStoryContainerActivity.class);
                ssIntent.putExtra(Constants.AUTHOR_ID, notificationExtras.getString("userId"));
                ssIntent.putExtra(Constants.ARTICLE_ID, notificationExtras.getString("id"));
                ssIntent.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
                ssIntent.putExtra(Constants.BLOG_SLUG, notificationExtras.getString("blogSlug"));
                ssIntent.putExtra(Constants.TITLE_SLUG, notificationExtras.getString("titleSlug"));
                ssIntent.putExtra(Constants.FROM_SCREEN, "Notification");
                ssIntent.putExtra(Constants.ARTICLE_INDEX, "-1");
                ssIntent.putExtra(Constants.AUTHOR, notificationExtras.getString("userId") + "~");
                startActivity(ssIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("video_details")) {
                pushEvent("video_details");
                String articleId = notificationExtras.getString("id");
                String authorId = notificationExtras.getString("userId");
                Intent intent1 = new Intent(DashboardActivity.this, ParallelFeedActivity.class);
                intent1.putExtra("fromNotification", true);
                intent1.putExtra(Constants.VIDEO_ID, articleId);
                intent1.putExtra(Constants.AUTHOR_ID, authorId);
                intent1.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
                intent1.putExtra(Constants.FROM_SCREEN, "Notification");
                intent1.putExtra(Constants.ARTICLE_INDEX, "-1");
                intent1.putExtra(Constants.AUTHOR, authorId + "~");
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("group_membership")
                    || notificationExtras.getString("type").equalsIgnoreCase("group_new_post")
                    || notificationExtras.getString("type").equalsIgnoreCase("group_admin_group_edit")
                    || notificationExtras.getString("type").equalsIgnoreCase("group_admin")) {
                pushEvent(notificationExtras.getString(notificationExtras.getString("type")));
                GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
                groupMembershipStatus.checkMembershipStatus(Integer.parseInt(notificationExtras.getString("groupId")), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
            } else if (notificationExtras.getString("type").equalsIgnoreCase("group_new_response")) {
                pushEvent("group_new_response");
                Intent gpPostIntent = new Intent(this, GroupPostDetailActivity.class);
                gpPostIntent.putExtra("postId", Integer.parseInt(notificationExtras.getString("postId")));
                gpPostIntent.putExtra("groupId", Integer.parseInt(notificationExtras.getString("groupId")));
                gpPostIntent.putExtra("responseId", Integer.parseInt(notificationExtras.getString("responseId")));
                startActivity(gpPostIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("group_new_reply")) {
                pushEvent("group_new_reply");
                Intent gpPostIntent = new Intent(this, ViewGroupPostCommentsRepliesActivity.class);
                gpPostIntent.putExtra("postId", Integer.parseInt(notificationExtras.getString("postId")));
                gpPostIntent.putExtra("groupId", Integer.parseInt(notificationExtras.getString("groupId")));
                gpPostIntent.putExtra("responseId", Integer.parseInt(notificationExtras.getString("responseId")));
                startActivity(gpPostIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("group_admin_membership")) {
                pushEvent("group_admin_membership");
                Intent memberIntent = new Intent(this, GroupMembershipActivity.class);
                memberIntent.putExtra("groupId", Integer.parseInt(notificationExtras.getString("groupId")));
                startActivity(memberIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("group_admin_reported")) {
                pushEvent("group_admin_reported");
                Intent reportIntent = new Intent(this, GroupsReportedContentActivity.class);
                reportIntent.putExtra("groupId", Integer.parseInt(notificationExtras.getString("groupId")));
                startActivity(reportIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("webView")) {
                pushEvent("webView");
                String url = notificationExtras.getString("url");
                Intent intent1 = new Intent(this, LoadWebViewActivity.class);
                intent1.putExtra("fromNotification", true);
                intent1.putExtra(Constants.WEB_VIEW_URL, url);
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("write_blog")) {
                pushEvent("write_blog");
                launchEditor();
            } else if (notificationExtras.getString("type").equalsIgnoreCase("profile")) {
                pushEvent("profile");
                String u_id = notificationExtras.getString("userId");
                if (!SharedPrefUtils.getUserDetailModel(this).getDynamoId().equals(u_id)) {
                    Intent intent1 = new Intent(this, UserProfileActivity.class);
                    intent1.putExtra("fromNotification", true);
                    intent1.putExtra(Constants.USER_ID, u_id);
                    intent1.putExtra(AppConstants.BADGE_ID, notificationExtras.getString(AppConstants.BADGE_ID));
                    intent1.putExtra(AppConstants.MILESTONE_ID, notificationExtras.getString(AppConstants.MILESTONE_ID));
                    intent1.putExtra(Constants.FROM_SCREEN, "Notification");
                    startActivity(intent1);
                } else {
                    fragmentToLoad = Constants.PROFILE_FRAGMENT;
                }
            } else if (notificationExtras.getString("type").equalsIgnoreCase("badge_list")) {
                pushEvent("badge_list");
                Intent badgeIntent = new Intent(this, BadgeActivity.class);
                startActivity(badgeIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("upcoming_event_list")) {
                pushEvent("upcoming_event_list");
                fragmentToLoad = Constants.BUSINESS_EVENTLIST_FRAGMENT;
            } else if (notificationExtras.getString("type").equalsIgnoreCase("suggested_topics")) {
                pushEvent("suggested_topics");
                fragmentToLoad = Constants.SUGGESTED_TOPICS_FRAGMENT;
            } else if (notificationExtras.getString("type").equalsIgnoreCase(AppConstants.APP_SETTINGS_DEEPLINK)) {
                pushEvent(AppConstants.APP_SETTINGS_DEEPLINK);
                Intent intent1 = new Intent(this, AppSettingsActivity.class);
                intent1.putExtra("fromNotification", true);
                intent1.putExtra("load_fragment", Constants.SETTINGS_FRAGMENT);
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("my_money_earnings")) {
                pushEvent("my_money_earnings");
                Intent intent1 = new Intent(this, MyTotalEarningActivity.class);
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("my_money_profile")) {
                pushEvent("my_money_profile");
                Intent intent1 = new Intent(this, EditProfileNewActivity.class);
                intent1.putExtra("isComingfromCampaign", true);
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("category_listing")) {
                pushEvent("category_listing");
                Intent intent1 = new Intent(this, TopicsListingActivity.class);
                intent1.putExtra("parentTopicId", notificationExtras.getString("categoryId"));
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("shortStoryListing")) {
                pushEvent("shortStoryListing");
                Intent intent1 = new Intent(this, ShortStoriesListingContainerActivity.class);
                intent1.putExtra("parentTopicId", AppConstants.SHORT_STORY_CATEGORYID);
                intent1.putExtra("selectedTabCategoryId", notificationExtras.getString("categoryId"));
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("shortStoryListingInChallengeListing")) {
                pushEvent("shortStoryListingInChallenge");
                findValues(notificationExtras.getString("categoryId"));
                Intent intent1 = new Intent(this, ShortStoryChallengeDetailActivity.class);
                intent1.putExtra("Display_Name", deepLinkDisplayName);
                intent1.putExtra("challenge", shortStoryChallengesList);
                intent1.putExtra("position", 0);
                intent1.putExtra("topics", shortStoriesTopicList.get(0).getDisplay_name());
                intent1.putExtra("parentId", shortStoriesTopicList.get(0).getId());
                intent1.putExtra("StringUrl", deepLinkImageUrl);
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("group_listing")) {
                pushEvent("group_listing");
                fragmentToLoad = Constants.GROUP_LISTING_FRAGMENT;
            } else if (notificationExtras.getString("type").equalsIgnoreCase("shortStoryPublishSuccess")) {
                pushEvent("shortStoryPublishSuccess");
                Intent intent1 = new Intent(this, ShortStoryModerationOrShareActivity.class);
                intent1.putExtra("shareUrl", "");
                intent1.putExtra(Constants.ARTICLE_ID, notificationExtras.getString("id"));
                startActivity(intent1);
            }
        } else if (_intent.hasExtra("branchLink") ||
                _intent.hasExtra(AppConstants.BRANCH_DEEPLINK_URL)) {
            String branchdata = BaseApplication.getInstance().getBranchData();
            JsonParser parser = new JsonParser();
            JsonElement mJson = parser.parse(branchdata);
            Gson gson = new Gson();
            BranchModel branchModel = gson.fromJson(mJson, BranchModel.class);

            Log.i("MixFeedData", branchdata + ":");
            if (!StringUtils.isNullOrEmpty(branchdata)) {
                if (!StringUtils.isNullOrEmpty(branchModel.getType()) && branchModel.getType().equals(AppConstants.BRANCH__CAMPAIGN_LISTING)) {
                    Intent intent1 = new Intent(DashboardActivity.this, CampaignContainerActivity.class);
                    startActivity(intent1);
                } else if (!StringUtils.isNullOrEmpty(branchModel.getType()) && branchModel.getType().equals(AppConstants.BRANCH_CAMPAIGN_DETAIL)) {
                    String campaignID = branchModel.getId();
                    Intent campaignIntent = new Intent(DashboardActivity.this, CampaignContainerActivity.class);
                    campaignIntent.putExtra("campaignID", Integer.parseInt(campaignID));
                    startActivity(campaignIntent);
                } else if (!StringUtils.isNullOrEmpty(branchModel.getType()) && branchModel.getType().equals(AppConstants.BRANCH_MOMVLOGS)) {
                    String challengeId = branchModel.getId();
                    Intent challengeIntent = new Intent(DashboardActivity.this, NewVideoChallengeActivity.class);
                    challengeIntent.putExtra("challenge", challengeId);
                    challengeIntent.putExtra("mappedId", branchModel.getMapped_category());
                    challengeIntent.putExtra("comingFrom", "branch_deep_link");
                    startActivity(challengeIntent);
                } else if (!StringUtils.isNullOrEmpty(branchModel.getType()) && branchModel.getType().equals(AppConstants.BRANCH_PERSONALINFO)) {
                    Intent intent1 = new Intent(DashboardActivity.this, RewardsContainerActivity.class);
                    intent1.putExtra("showProfileInfo", true);
                    startActivity(intent1);
                } else {
                }
            }
        } else {
            String tempDeepLinkURL = _intent.getStringExtra(AppConstants.DEEP_LINK_URL);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                jsonObject.put("url", "" + tempDeepLinkURL);
                mMixpanel.track("DeepLinking", jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!StringUtils.isNullOrEmpty(tempDeepLinkURL)) {
                if (matchRegex(tempDeepLinkURL)) {
                    //////// need to optimize this code
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_EDITOR_URL) || tempDeepLinkURL.contains(AppConstants.DEEPLINK_MOMSPRESSO_EDITOR_URL)) {
                    final String bloggerId = tempDeepLinkURL.substring(tempDeepLinkURL.lastIndexOf("/") + 1, tempDeepLinkURL.length());
                    if (!StringUtils.isNullOrEmpty(bloggerId) && !bloggerId.equals(SharedPrefUtils.getUserDetailModel(this).getDynamoId())) {
                        showAlertDialog("Message", "Logged in as " + SharedPrefUtils.getUserDetailModel(this).getFirst_name() + " " + SharedPrefUtils.getUserDetailModel(this).getLast_name(), new OnButtonClicked() {
                            @Override
                            public void onButtonCLick(int buttonId) {
                                Utils.pushEvent(DashboardActivity.this, GTMEventType.ADD_BLOG_CLICKED_EVENT,
                                        SharedPrefUtils.getUserDetailModel(DashboardActivity.this).getDynamoId() + "", "Mobile Deep Link");
                                launchEditor();
                            }
                        });
                    } else {
                        Utils.pushEvent(DashboardActivity.this, GTMEventType.ADD_BLOG_CLICKED_EVENT,
                                SharedPrefUtils.getUserDetailModel(DashboardActivity.this).getDynamoId() + "", "Mobile Deep Link");
                        launchEditor();
                    }
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_ADD_FUNNY_VIDEO_URL) || tempDeepLinkURL.contains(AppConstants.DEEPLINK_MOMSPRESSO_ADD_FUNNY_VIDEO_URL)) {
                    final String bloggerId = tempDeepLinkURL.substring(tempDeepLinkURL.lastIndexOf("/") + 1, tempDeepLinkURL.length());
                    if (!StringUtils.isNullOrEmpty(bloggerId) && !bloggerId.equals(SharedPrefUtils.getUserDetailModel(this).getDynamoId())) {
                        showAlertDialog("Message", "Logged in as " + SharedPrefUtils.getUserDetailModel(this).getFirst_name() + " " + SharedPrefUtils.getUserDetailModel(this).getLast_name(), new OnButtonClicked() {
                            @Override
                            public void onButtonCLick(int buttonId) {
                                launchAddVideoOptions();
                            }
                        });
                    } else {
                        launchAddVideoOptions();
                    }
                } else if (tempDeepLinkURL.endsWith(AppConstants.DEEPLINK_SELF_PROFILE_URL_1) || tempDeepLinkURL.endsWith(AppConstants.DEEPLINK_SELF_PROFILE_URL_2)) {
                    fragmentToLoad = Constants.PROFILE_FRAGMENT;
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_PROFILE_URL) || tempDeepLinkURL.contains(AppConstants.DEEPLINK_MOMSPRESSO_PROFILE_URL)) {
                    final String bloggerId = tempDeepLinkURL.substring(tempDeepLinkURL.lastIndexOf("/") + 1, tempDeepLinkURL.length());
                    if (!StringUtils.isNullOrEmpty(bloggerId) && !bloggerId.equals(SharedPrefUtils.getUserDetailModel(this).getDynamoId())) {
                        showAlertDialog("Message", "Logged in as " + SharedPrefUtils.getUserDetailModel(this).getFirst_name() + " " + SharedPrefUtils.getUserDetailModel(this).getLast_name(), new OnButtonClicked() {
                            @Override
                            public void onButtonCLick(int buttonId) {
                                fragmentToLoad = Constants.PROFILE_FRAGMENT;
                            }
                        });
                    } else {
                        fragmentToLoad = Constants.PROFILE_FRAGMENT;
                    }
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_ADD_SHORT_STORY_URL)) {
                    //String temp = tempDeepLinkURL.concat("category-8220f60fd2f6432ba4417861a50f0587");
                    final String deepLinkChallengeId = tempDeepLinkURL.substring(tempDeepLinkURL.lastIndexOf("/") + 1, tempDeepLinkURL.length());
                    if (StringUtils.isNullOrEmpty(deepLinkChallengeId)) {
                        Intent ssIntent = new Intent(this, AddShortStoryActivity.class);
                        startActivity(ssIntent);
                    } else {
                        findValues(deepLinkChallengeId);
                        if (StringUtils.isNullOrEmpty(shortStoryChallengesList) && StringUtils.isNullOrEmpty(deepLinkDisplayName) && StringUtils.isNullOrEmpty(deepLinkImageUrl) && shortStoriesTopicList != null && !shortStoriesTopicList.isEmpty()) {
                            Intent deepLinkIntent = new Intent(this, ShortStoryChallengeDetailActivity.class);
                            deepLinkIntent.putExtra("selectedrequest", FromDeepLink);
                            deepLinkIntent.putExtra("Display_Name", deepLinkDisplayName);
                            deepLinkIntent.putExtra("challenge", shortStoryChallengesList);
                            deepLinkIntent.putExtra("position", 0);
                            deepLinkIntent.putExtra("topics", shortStoriesTopicList.get(0).getDisplay_name());
                            deepLinkIntent.putExtra("parentId", shortStoriesTopicList.get(0).getId());
                            deepLinkIntent.putExtra("StringUrl", deepLinkImageUrl);
                            startActivity(deepLinkIntent);
                        } else {
                            findValues(deepLinkChallengeId);
                            Intent deepLinkIntent = new Intent(this, ShortStoryChallengeDetailActivity.class);
                            deepLinkIntent.putExtra("selectedrequest", FromDeepLink);
                            deepLinkIntent.putExtra("Display_Name", deepLinkDisplayName);
                            deepLinkIntent.putExtra("challenge", shortStoryChallengesList);
                            deepLinkIntent.putExtra("position", 0);
                            deepLinkIntent.putExtra("topics", shortStoriesTopicList.get(0).getDisplay_name());
                            deepLinkIntent.putExtra("parentId", shortStoriesTopicList.get(0).getId());
                            deepLinkIntent.putExtra("StringUrl", deepLinkImageUrl);
                            startActivity(deepLinkIntent);
                            ToastUtils.showToast(this, "server problem, please try again later");
                        }
                    }
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_EDIT_SHORT_DRAFT_URL)) {
                    final String draftId = tempDeepLinkURL.substring(tempDeepLinkURL.lastIndexOf("/") + 1, tempDeepLinkURL.length());
                    Intent ssIntent = new Intent(this, UserDraftsContentActivity.class);
                    ssIntent.putExtra("isPrivateProfile", true);
                    ssIntent.putExtra("contentType", "shortStory");
                    ssIntent.putExtra(Constants.AUTHOR_ID, SharedPrefUtils.getUserDetailModel(this).getDynamoId());
                    startActivity(ssIntent);
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_EDIT_SHORT_STORY_URL)) {
                    final String storyId = tempDeepLinkURL.substring(tempDeepLinkURL.lastIndexOf("/") + 1, tempDeepLinkURL.length());
                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    ShortStoryAPI shortStoryAPI = retrofit.create(ShortStoryAPI.class);
                    Call<ShortStoryDetailResult> call = shortStoryAPI.getShortStoryDetails(storyId, "articleId");
                    call.enqueue(ssDetailResponseCallback);
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_SUGGESTED_TOPIC_URL) || tempDeepLinkURL.contains(AppConstants.DEEPLINK_MOMSPRESSO_SUGGESTED_TOPIC_URL)) {
                    fragmentToLoad = Constants.SUGGESTED_TOPICS_FRAGMENT;
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_UPCOMING_EVENTS)) {
                    fragmentToLoad = Constants.BUSINESS_EVENTLIST_FRAGMENT;
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_SETUP_BLOG)) {
                    SharedPreferences pref = getSharedPreferences(COMMON_PREF_FILE, MODE_PRIVATE);
                    boolean blogSetup = pref.getBoolean("blogSetup", false);
                    if (!blogSetup) {
                        checkIsBlogSetup();
                    } else {
                        launchEditor();
                    }
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_MOMSPRESSO_REWARD_PAGE)) {
                    Intent rewardForm = new Intent(this, RewardsContainerActivity.class);
                    final String referralCode = tempDeepLinkURL.substring(tempDeepLinkURL.lastIndexOf("=") + 1, tempDeepLinkURL.length());
                    rewardForm.putExtra("pageLimit", 1);
                    rewardForm.putExtra("pageNumber", 1);
                    rewardForm.putExtra("referral", referralCode);
                    startActivity(rewardForm);
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_MOMSPRESSO_REWARD_MYMONEY)) {
                    Intent campaignIntent = new Intent(this, CampaignContainerActivity.class);
                    startActivity(campaignIntent);
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_MOMSPRESSO_CAMPAIGN)) {
                    if (tempDeepLinkURL.contains("?")) {
                        final String campaignID = tempDeepLinkURL.substring(tempDeepLinkURL.lastIndexOf("/") + 1, tempDeepLinkURL.indexOf("?"));
                        if (!StringUtils.isNullOrEmpty(campaignID)) {
                            Intent campaignIntent = new Intent(this, CampaignContainerActivity.class);
                            campaignIntent.putExtra("campaignID", Integer.parseInt(campaignID));
                            startActivity(campaignIntent);
                        }
                    } else {
                        final String campaignID = tempDeepLinkURL.substring(tempDeepLinkURL.lastIndexOf("/") + 1);
                        if (!StringUtils.isNullOrEmpty(campaignID)) {
                            Intent campaignIntent = new Intent(this, CampaignContainerActivity.class);
                            campaignIntent.putExtra("campaignID", Integer.parseInt(campaignID));
                            startActivity(campaignIntent);
                        }
                    }
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_GROUPS)) {
                    String[] separated = tempDeepLinkURL.split("/");
                    if (separated[separated.length - 1].startsWith("comment-")) {
                        String[] commArray = separated[separated.length - 1].split("-");
                        long commentId = AppUtils.getIdFromHash(commArray[1]);
                        String[] postArray = separated[separated.length - 2].split("-");
                        long postId = AppUtils.getIdFromHash(postArray[1]);
                        String[] groupArray = separated[separated.length - 3].split("-");
                        long groupId = AppUtils.getIdFromHash(groupArray[groupArray.length - 1]);
                        Intent gpPostIntent = new Intent(this, GroupPostDetailActivity.class);
                        gpPostIntent.putExtra("postId", (int) postId);
                        gpPostIntent.putExtra("groupId", (int) groupId);
                        gpPostIntent.putExtra("responseId", (int) commentId);
                        startActivity(gpPostIntent);
                    } else if (separated[separated.length - 1].startsWith("post-")) {
                        String[] postArray = separated[separated.length - 1].split("-");
                        long postId = AppUtils.getIdFromHash(postArray[1]);
                        String[] groupArray = separated[separated.length - 2].split("-");
                        long groupId = AppUtils.getIdFromHash(groupArray[groupArray.length - 1]);
                        Intent gpPostIntent = new Intent(this, GroupPostDetailActivity.class);
                        gpPostIntent.putExtra("postId", (int) postId);
                        gpPostIntent.putExtra("groupId", (int) groupId);
                        startActivity(gpPostIntent);
                    } else if (separated[separated.length - 1].equals("join")) {
                        String[] groupArray = separated[separated.length - 2].split("-");
                        long groupId = AppUtils.getIdFromHash(groupArray[groupArray.length - 1]);
                        GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
                        groupMembershipStatus.checkMembershipStatus((int) groupId, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    } else {
                        String[] groupArray;
                        long groupId;
                        if (separated[separated.length - 1].contains("?")) {
                            groupArray = separated[separated.length - 1].split("[?]");
                            groupArray = groupArray[0].split("-");
                            groupId = AppUtils.getIdFromHash(groupArray[groupArray.length - 1]);
                        } else {
                            groupArray = separated[separated.length - 1].split("-");
                            groupId = AppUtils.getIdFromHash(groupArray[groupArray.length - 1]);
                        }
                        if (groupId == -1) {
                            return;
                        }
                        GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
                        groupMembershipStatus.checkMembershipStatus((int) groupId, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    }
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_MOMSPRESSO_REFERRAL)) {
                    Intent intent1 = new Intent(this, RewardsContainerActivity.class);
                    intent1.putExtra("pageNumber", 1);
                    startActivity(intent1);
                } else {
                    getDeepLinkData(tempDeepLinkURL);
                }
            }
            deepLinkUrl = _intent.getStringExtra(AppConstants.DEEP_LINK_URL);
        }
    }

    private void pushEvent(String type) {
        Utils.pushNotificationClickEvent(this, type,
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), "DashboardActivity");
    }

    private void findValues(String deepLinkChallengeId) {
        try {
            if (shortStoriesTopicList != null && shortStoriesTopicList.size() != 0) {
                num_of_categorys = shortStoriesTopicList.get(0).getChild().size();
                for (int j = 0; j < num_of_categorys; j++) {
                    if (shortStoriesTopicList.get(0).getChild().get(j).getId().equals(AppConstants.SHORT_STORY_CHALLENGE_ID)) {
                        num_of_challeneges = shortStoriesTopicList.get(0).getChild().get(j).getChild().size();
                        for (int k = 0; k < num_of_challeneges; k++) {
                            if (deepLinkChallengeId.equals(shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getId())) {
                                if (shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getExtraData() != null && shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getExtraData().size() != 0) {
                                    shortStoryChallengesList = shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getId();
                                    deepLinkDisplayName = shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getDisplay_name();
                                    deepLinkImageUrl = shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getExtraData().get(0).getChallenge().getImageUrl();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (shortStoriesTopicList == null || shortStoriesTopicList.size() == 0) {
                FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                res = gson.fromJson(fileContent, TopicsResponse.class);
                shortStoriesTopicList = new ArrayList<Topics>();
                for (int i = 0; i < res.getData().size(); i++) {
                    if (AppConstants.SHORT_STORY_CATEGORYID.equals(res.getData().get(i).getId())) {
                        shortStoriesTopicList.add(res.getData().get(i));
                    }
                }
                num_of_categorys = shortStoriesTopicList.get(0).getChild().size();
                for (int j = 0; j < num_of_categorys; j++) {
                    if (shortStoriesTopicList.get(0).getChild().get(j).getId().equals(AppConstants.SHORT_STORY_CHALLENGE_ID)) {
                        num_of_challeneges = shortStoriesTopicList.get(0).getChild().get(j).getChild().size();
                        for (int k = 0; k < num_of_challeneges; k++) {
                            if (deepLinkChallengeId.equals(shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getId())) {
                                if (shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getExtraData() != null && shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getExtraData().size() != 0) {
                                    shortStoryChallengesList = shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getId();
                                    deepLinkDisplayName = shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getDisplay_name();
                                    deepLinkImageUrl = shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getExtraData().get(0).getChallenge().getImageUrl();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
            Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();
            caller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE, response.body());
                    try {
                        FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                        res = gson.fromJson(fileContent, TopicsResponse.class);
                        shortStoriesTopicList = new ArrayList<Topics>();
                        for (int i = 0; i < res.getData().size(); i++) {
                            if (AppConstants.SHORT_STORY_CATEGORYID.equals(res.getData().get(i).getId())) {
                                shortStoriesTopicList.add(res.getData().get(i));
                            }
                        }
                        num_of_categorys = shortStoriesTopicList.get(0).getChild().size();
                        for (int j = 0; j < num_of_categorys; j++) {
                            if (shortStoriesTopicList.get(0).getChild().get(j).getId().equals(AppConstants.SHORT_STORY_CHALLENGE_ID)) {
                                num_of_challeneges = shortStoriesTopicList.get(0).getChild().get(j).getChild().size();
                                for (int k = 0; k < num_of_challeneges; k++) {
                                    if (deepLinkChallengeId.equals(shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getId())) {
                                        if (shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getExtraData() != null && shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getExtraData().size() != 0) {
                                            shortStoryChallengesList = shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getId();
                                            deepLinkDisplayName = shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getDisplay_name();
                                            deepLinkImageUrl = shortStoriesTopicList.get(0).getChild().get(j).getChild().get(k).getExtraData().get(0).getChallenge().getImageUrl();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (FileNotFoundException e) {
                        Crashlytics.logException(e);
                        Log.d("FileNotFoundException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Crashlytics.logException(t);
                    apiExceptions(t);
                    Log.d("MC4KException", Log.getStackTraceString(t));
                }
            });
        }
    }

    Callback<ShortStoryDetailResult> ssDetailResponseCallback = new Callback<ShortStoryDetailResult>() {
        @Override
        public void onResponse(Call<ShortStoryDetailResult> call, retrofit2.Response<ShortStoryDetailResult> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ShortStoryDetailResult responseData = response.body();
                Intent intent = new Intent(DashboardActivity.this, AddShortStoryActivity.class);
                intent.putExtra("from", "publishedList");
                intent.putExtra("title", responseData.getTitle());
                intent.putExtra("body", responseData.getBody());
                intent.putExtra("articleId", responseData.getId());
                intent.putExtra("tag", new Gson().toJson(responseData.getTags()));
                intent.putExtra("cities", new Gson().toJson(responseData.getCities()));
                startActivity(intent);
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<ShortStoryDetailResult> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            apiExceptions(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void checkIsBlogSetup() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BlogPageAPI getBlogPageAPI = retrofit.create(BlogPageAPI.class);

        Call<BlogPageResponse> call = getBlogPageAPI.getUserBlogPage(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(blogPageSetUpResponseListener);
    }

    private Callback<BlogPageResponse> blogPageSetUpResponseListener = new Callback<BlogPageResponse>() {
        @Override
        public void onResponse(Call<BlogPageResponse> call, retrofit2.Response<BlogPageResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            BlogPageResponse responseModel = response.body();
            if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                if (responseModel.getData().getResult().getIsSetup() == 1) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(COMMON_PREF_FILE, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("blogSetup", true);
                    editor.commit();
                    launchEditor();
                } else if (responseModel.getData().getResult().getIsSetup() == 0) {
                    Intent intent = new Intent(DashboardActivity.this, BlogSetupActivity.class);
                    startActivity(intent);
                }
            }
        }

        @Override
        public void onFailure(Call<BlogPageResponse> call, Throwable t) {
            removeProgressDialog();
            apiExceptions(t);
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);
        return ret;
    }

    @Override
    protected void onDestroy() {
        mMixpanel.flush();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BaseApplication) getApplication()).setView(root);
        final Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (topFragment instanceof FragmentMC4KHomeNew && SharedPrefUtils.isTopicSelectionChanged(BaseApplication.getAppContext())) {
            ((FragmentMC4KHomeNew) topFragment).hideFollowTopicHeader();
        }
        refreshMenu();
        final Fragment topFragmentt = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        Menu menu = bottomNavigationView.getMenu();
        if (topFragmentt instanceof ExploreArticleListingTypeFragment) {
            menu.findItem(R.id.action_profile).setChecked(true);

        } else if (topFragmentt instanceof FragmentMC4KHomeNew) {
            menu.findItem(R.id.action_home).setChecked(true);

        } else if (topFragmentt instanceof GroupsViewFragment) {
            menu.findItem(R.id.action_location).setChecked(true);
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        createContentContainer.setVisibility(View.INVISIBLE);
        actionItemContainer.setVisibility(View.INVISIBLE);
        overlayView.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void refreshMenu() {
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void updateUnreadNotificationCount(String unreadNotifCount) {
        if (StringUtils.isNullOrEmpty(unreadNotifCount) || "0".equals(unreadNotifCount)) {
            addBadgeAt(3, "0");
        } else {
            addBadgeAt(3, unreadNotifCount);
        }
    }

    public void showHideNotificationCenterMark(boolean flag) {
        if (flag) {
            addBadgeAt(3, "1");
        } else {
            addBadgeAt(3, "0");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        switch (item.getItemId()) {
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            case R.id.save:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchEditor() {
        Intent intent1 = new Intent(DashboardActivity.this, EditorPostActivity.class);
        Bundle bundle5 = new Bundle();
        bundle5.putString(EditorPostActivity.TITLE_PARAM, "");
        bundle5.putString(EditorPostActivity.CONTENT_PARAM, "");
        bundle5.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                getString(R.string.example_post_title_placeholder));
        bundle5.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                getString(R.string.example_post_content_placeholder));
        bundle5.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
        bundle5.putString("from", "dashboard");
        intent1.putExtras(bundle5);
        startActivity(intent1);
    }

    @Override
    public void onClick(View v) {
        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        switch (v.getId()) {
            case R.id.drawerProfileCoachmark: {
                drawerProfileCoachmark.setVisibility(View.GONE);
                drawerMyMoneyCoachmark.setVisibility(View.GONE);
                SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "Drawer", true);
            }
            break;
            case R.id.homeCoachmark: {
                homeCoachmark.setVisibility(View.GONE);
                createCoachmark.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.createCoachmark: {
                createCoachmark.setVisibility(View.GONE);
                vlogsCoachmark.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.vlogsCoachmark: {
                vlogsCoachmark.setVisibility(View.GONE);
                groupCoachmark.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.groupCoachmark: {
                groupCoachmark.setVisibility(View.GONE);
                menuCoachmark.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.menuCoachmark: {
                menuCoachmark.setVisibility(View.GONE);
                SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "HomeScreen", true);
                showMyMoneyRegistrationPrompt(getIntent());
            }
            break;
            case R.id.viewBookmarkedArticleTextView: {
                mDrawerLayout.closeDrawers();
                Intent cityIntent = new Intent(this, UsersBookmarkListActivity.class);
                startActivity(cityIntent);
            }
            break;
            case R.id.homeTextView:
                mDrawerLayout.closeDrawers();
                hideCreateContentView();
                if (topFragment instanceof FragmentMC4KHomeNew) {
                    return;
                }
                FragmentMC4KHomeNew fragment1 = new FragmentMC4KHomeNew();
                Bundle mBundle1 = new Bundle();
                fragment1.setArguments(mBundle1);
                addFragment(fragment1, mBundle1, true);
                break;
            case R.id.articleContainer:
                hideCreateContentView();
                if ("0".equals(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getUserType()) && !SharedPrefUtils.getBecomeBloggerFlag(BaseApplication.getAppContext())) {
                    BecomeBloggerFragment becomeBloggerFragment = new BecomeBloggerFragment();
                    Bundle searchBundle = new Bundle();
                    becomeBloggerFragment.setArguments(searchBundle);
                    addFragment(becomeBloggerFragment, searchBundle, true);
                } else {
                    Intent intent = new Intent(this, SuggestedTopicsActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.storyContainer:
                hideCreateContentView();
                Intent chooseShortStory = new Intent(this, ChooseShortStoryCategoryActivity.class);
                chooseShortStory.putExtra("source", "dashboard");
                startActivity(chooseShortStory);
                break;
            case R.id.videoContainer: {
                hideCreateContentView();
                mDrawerLayout.closeDrawers();
                Utils.momVlogEvent(DashboardActivity.this, "Home Screen", "Create_video", "", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_video_creation_categories", "", "");
                MixPanelUtils.pushMomVlogsDrawerClickEvent(mMixpanel);
                Intent cityIntent = new Intent(this, ChooseVideoCategoryActivity.class);
                cityIntent.putExtra("comingFrom", "createDashboardIcon");
                startActivity(cityIntent);
                fireEventForVideoCreationIntent();
            }
            break;
            case R.id.overlayView:
                hideCreateContentView();
                break;
            case R.id.topContainer:
            case R.id.profileImageView:
                mDrawerLayout.closeDrawers();
                Utils.campaignEvent(this, "profile", "sidebar", "Update", "", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "CTA_Update_Rewards");
                Intent pIntent = new Intent(this, UserProfileActivity.class);
                startActivity(pIntent);
                break;
            case R.id.langTextView:
            case R.id.langView: {
                mDrawerLayout.closeDrawers();
                ChangePreferredLanguageDialogFragment changePreferredLanguageDialogFragment = new ChangePreferredLanguageDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                Bundle _args = new Bundle();
                _args.putString("activity", "dashboard");
                changePreferredLanguageDialogFragment.setArguments(_args);
                changePreferredLanguageDialogFragment.setCancelable(true);
                changePreferredLanguageDialogFragment.show(fm, "Choose video option");
            }
            break;
            case R.id.downArrowImageView:
            case R.id.toolbarTitle:
                break;
            case R.id.searchAllImageView:
                if (topFragment instanceof GroupsViewFragment) {
                    Intent searchIntent = new Intent(this, GroupsSearchActivity.class);
                    startActivity(searchIntent);
                } else {
                    Intent searchIntent = new Intent(this, SearchAllActivity.class);
                    searchIntent.putExtra(Constants.FILTER_NAME, "");
                    searchIntent.putExtra(Constants.TAB_POSITION, 0);
                    startActivity(searchIntent);
                }
                break;
            case R.id.notification:
                hideCreateContentView();
                Intent notificationIntent = new Intent(this, NotificationActivity.class);
                startActivity(notificationIntent);
                break;

            case R.id.firstCoachmark:
                firstCoachmark.setVisibility(View.GONE);
                secondCoachmark.setVisibility(View.VISIBLE);
                break;
            case R.id.secondCoachmark:
                secondCoachmark.setVisibility(View.GONE);
                SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "home", true);
                break;
            case R.id.videosTextView: {
                mDrawerLayout.closeDrawers();
                MixPanelUtils.pushMomVlogsDrawerClickEvent(mMixpanel);
                Utils.momVlogEvent(DashboardActivity.this, "Home Screen", "Sidebar_vlogs", "", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_Video_Listing", "", "");
                Intent cityIntent = new Intent(this, CategoryVideosListingActivity.class);
                cityIntent.putExtra("parentTopicId", AppConstants.HOME_VIDEOS_CATEGORYID);
                startActivity(cityIntent);
            }
            break;
            case R.id.momspressoTextView: {
                mDrawerLayout.closeDrawers();
                Intent intent = new Intent(this, FilteredTopicsArticleListingActivity.class);
                intent.putExtra("selectedTopics", AppConstants.MOMSPRESSO_CATEGORYID);
                intent.putExtra("displayName", getString(R.string.all_videos_tabbar_momspresso_label));
                startActivity(intent);
            }
            break;
            case R.id.shortStoryTextView: {
                mDrawerLayout.closeDrawers();
                Intent intent = new Intent(this, ShortStoriesListingContainerActivity.class);
                intent.putExtra("parentTopicId", AppConstants.SHORT_STORY_CATEGORYID);
                startActivityForResult(intent, 1234);
            }
            break;
            case R.id.groupsTextView: {
                mDrawerLayout.closeDrawers();
                Utils.groupsEvent(DashboardActivity.this, "Home Screen", "Sidebar_groups", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Group_listing", "", "");
                GroupsViewFragment groupsFragment = new GroupsViewFragment();
                Bundle eBundle = new Bundle();
                groupsFragment.setArguments(eBundle);
                addFragment(groupsFragment, eBundle, true);
            }
            break;
            case R.id.rewardsTextView: {
                Utils.campaignEvent(this, "Campaign Listing", "Sidebar", "Rewards", "", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_Campaign_Listing");
                mDrawerLayout.closeDrawers();
                Intent cityIntent = new Intent(this, CampaignContainerActivity.class);
                startActivity(cityIntent);
            }
            break;
            case R.id.bookmarksTextView: {
                Intent cityIntent = new Intent(this, UsersBookmarkListActivity.class);
                startActivity(cityIntent);
            }
            break;
            case R.id.referral:
                Utils.pushGenericEvent(this, "CTA_MyMoney_Sidebar_Refer",
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), "Home Screen");
                Intent intent = new Intent(this, RewardsShareReferralCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.settingTextView: {
                mDrawerLayout.closeDrawers();
                Intent cityIntent = new Intent(this, AppSettingsActivity.class);
                startActivity(cityIntent);
            }
            break;
            default:
                break;
        }
    }

    private void fireEventForVideoCreationIntent() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        VlogsEventRequest vlogsEventRequest = new VlogsEventRequest();
        vlogsEventRequest.setCreatedTime(System.currentTimeMillis());
        vlogsEventRequest.setKey(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
        vlogsEventRequest.setTopic("create_video_fab");
        vlogsEventRequest.setPayload(vlogsEventRequest.getPayload());
        Call<ResponseBody> call = vlogsListingAndDetailsAPI.addVlogsCreateIntentEvent(vlogsEventRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                apiExceptions(t);
            }
        });
    }

    private void hideCreateContentView() {
        createContentContainer.setVisibility(View.INVISIBLE);
        overlayView.setVisibility(View.INVISIBLE);
        actionItemContainer.setVisibility(View.INVISIBLE);
    }

    public void launchAddVideoOptions() {
        ChooseVideoUploadOptionDialogFragment chooseVideoUploadOptionDialogFragment = new ChooseVideoUploadOptionDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        Bundle _args = new Bundle();
        _args.putString("activity", "dashboard");
        chooseVideoUploadOptionDialogFragment.setArguments(_args);
        chooseVideoUploadOptionDialogFragment.setCancelable(true);
        chooseVideoUploadOptionDialogFragment.show(fm, "Choose video option");
    }

    @Override
    public void onBackPressed() {
        try {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return;
            }
            if (createContentContainer.getVisibility() == View.VISIBLE) {
                hideCreateContentView();
                return;
            }
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        try {
            switch (requestCode) {
                case Constants.OPEN_GALLERY:
                    break;
                case Constants.TAKE_PICTURE:
                    break;

                case Constants.CROP_IMAGE:
                    break;
                case AppConstants.REQUEST_GOOGLE_PLAY_SERVICES:
                    if (resultCode != RESULT_OK) {
                        isGooglePlayServicesAvailable();
                    }
                    break;
                case AppConstants.REQUEST_ACCOUNT_PICKER:
                    if (resultCode == RESULT_OK && data != null &&
                            data.getExtras() != null) {
                        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    } else if (resultCode == RESULT_CANCELED) {
                        showToast("Account unspecified.");
                    }
                    break;
                case AppConstants.REQUEST_AUTHORIZATION:
                    if (resultCode != RESULT_OK) {
                    }
                    break;
                case AppConstants.REQUEST_VIDEO_TRIMMER:
                    final Uri selectedUri = data.getData();
                    if (selectedUri != null) {
                        startTrimActivity(selectedUri);
                    } else {
                        Toast.makeText(this, R.string.toast_cannot_retrieve_selected_video, Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(this, VideoTrimmerActivity.class);
        String filepath = FileUtils.getPath(this, uri);
        if (null != filepath && (filepath.endsWith(".mp4") || filepath.endsWith(".MP4"))) {
            intent.putExtra("EXTRA_VIDEO_PATH", FileUtils.getPath(this, uri));
            startActivity(intent);
        } else {
            showToast(getString(R.string.choose_mp4_file));
        }
    }

    public void showGooglePlayServicesAvailabilityErrorDialog(int connectionStatusCode) {

    }

    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    private void getDeepLinkData(final String deepLinkURL) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        showProgressDialog("");
        DeepLinkingAPI deepLinkingAPI = retrofit.create(DeepLinkingAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        Call<DeepLinkingResposnse> call = deepLinkingAPI.getUrlDetails(deepLinkURL);
        call.enqueue(new Callback<DeepLinkingResposnse>() {
            @Override
            public void onResponse(Call<DeepLinkingResposnse> call, retrofit2.Response<DeepLinkingResposnse> response) {
                removeProgressDialog();
                try {
                    DeepLinkingResposnse responseData = response.body();
                    if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                        identifyTargetScreen(responseData.getData().getResult());
                    } else {
                        showToast(getString(R.string.toast_response_error));
                    }
                } catch (Exception e) {
                    showToast(getString(R.string.toast_response_error));
                }
            }

            @Override
            public void onFailure(Call<DeepLinkingResposnse> call, Throwable t) {
                removeProgressDialog();
                showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(t);
                apiExceptions(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });
    }

    private void identifyTargetScreen(DeepLinkingResult data) {
        switch (data.getType()) {
            case AppConstants.DEEP_LINK_AUTHOR_LISTING:
                renderAuthorListingScreen(data);
                break;
            case AppConstants.DEEP_LINK_BLOGGER_LISTING:
                renderBloggerListingScreen(data);
                break;
            case AppConstants.DEEP_LINK_ARTICLE_DETAIL:
                renderArticleDetailScreen(data);
                break;
            case AppConstants.DEEP_LINK_AUTHOR_DETAIL:
                renderAuthorDetailScreen(data);
                break;
            case AppConstants.DEEP_LINK_ARTICLE_LISTING:
                renderArticleListingScreen(data);
                break;
            case AppConstants.DEEP_LINK_TOPIC_LISTING:
                renderArticleListingScreen(data);
                break;
            case AppConstants.DEEP_LINK_VLOG_DETAIL:
                renderVlogDetailScreen(data);
                break;
            case AppConstants.APP_SETTINGS_DEEPLINK:
                renderAppSettingsScreen(data);
                break;
            case AppConstants.DEEP_LINK_STORY_DETAILS:
                navigateToShortStory(data);
                break;
        }
    }

    private void navigateToShortStory(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getArticle_id())) {
            Intent intent = new Intent(DashboardActivity.this, ShortStoryContainerActivity.class);
            intent.putExtra(Constants.AUTHOR_ID, data.getAuthor_id());
            intent.putExtra(Constants.ARTICLE_ID, data.getArticle_id());
            intent.putExtra(Constants.DEEPLINK_URL, deepLinkUrl);
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "DeepLinking");
            intent.putExtra(Constants.ARTICLE_INDEX, "-1");
            intent.putExtra(Constants.FROM_SCREEN, "DeepLinking");
            intent.putExtra(Constants.AUTHOR, data.getAuthor_id() + "~" + data.getAuthor_name());
            startActivity(intent);
        }
    }

    private void renderArticleListingScreen(DeepLinkingResult data) {
    }

    private void renderAuthorDetailScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getAuthor_id())) {
            Intent intent = new Intent(DashboardActivity.this, UserProfileActivity.class);
            intent.putExtra(AppConstants.PUBLIC_PROFILE_FLAG, true);
            intent.putExtra(Constants.USER_ID, data.getAuthor_id());
            intent.putExtra(AppConstants.AUTHOR_NAME, "" + data.getAuthor_name());
            intent.putExtra(Constants.FROM_SCREEN, "Deep Linking");
            startActivity(intent);
        }
    }

    private void renderAuthorListingScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getAuthor_name())) {
            Intent _authorListIntent = new Intent(DashboardActivity.this, UserProfileActivity.class);
            _authorListIntent.putExtra(Constants.USER_ID, data.getAuthor_id());
            _authorListIntent.putExtra(Constants.DEEPLINK_URL, deepLinkUrl);
            _authorListIntent.putExtra(AppConstants.AUTHOR_NAME, "" + data.getAuthor_name());
            _authorListIntent.putExtra(Constants.FROM_SCREEN, "Deep Linking");
            startActivity(_authorListIntent);
        }
    }

    private void renderBloggerListingScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getBlog_title())) {
            Intent _bloggerListIntent = new Intent(DashboardActivity.this, UserProfileActivity.class);
            _bloggerListIntent.putExtra(Constants.USER_ID, data.getAuthor_id());
            _bloggerListIntent.putExtra(Constants.DEEPLINK_URL, deepLinkUrl);
            _bloggerListIntent.putExtra(AppConstants.AUTHOR_NAME, "" + data.getAuthor_name());
            _bloggerListIntent.putExtra(Constants.FROM_SCREEN, "Deep Linking");
            startActivity(_bloggerListIntent);
        }
    }

    private void renderArticleDetailScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getArticle_id())) {
            Intent intent = new Intent(DashboardActivity.this, ArticleDetailsContainerActivity.class);
            intent.putExtra(Constants.AUTHOR_ID, data.getAuthor_id());
            intent.putExtra(Constants.ARTICLE_ID, data.getArticle_id());
            intent.putExtra(Constants.DEEPLINK_URL, deepLinkUrl);
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "DeepLinking");
            intent.putExtra(Constants.ARTICLE_INDEX, "-1");
            intent.putExtra(Constants.FROM_SCREEN, "DeepLinking");
            intent.putExtra(Constants.AUTHOR, data.getAuthor_id() + "~" + data.getAuthor_name());
            startActivity(intent);
        }
    }

    private void renderVlogDetailScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getId())) {
            Intent intent = new Intent(DashboardActivity.this, ParallelFeedActivity.class);
//            intent.putExtra(Constants.AUTHOR_ID, data.getAuthor_id());
            intent.putExtra(Constants.VIDEO_ID, data.getId());
            intent.putExtra(Constants.DEEPLINK_URL, deepLinkUrl);
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Deep Linking");
            intent.putExtra(Constants.ARTICLE_INDEX, "-1");
            intent.putExtra(Constants.FROM_SCREEN, "Deep Linking");
            intent.putExtra(Constants.AUTHOR, data.getAuthor_id() + "~" + data.getAuthor_name());
            startActivity(intent);
        }
    }

    private void renderAppSettingsScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getId())) {
            Intent intent = new Intent(DashboardActivity.this, AppSettingsActivity.class);
            intent.putExtra("load_fragment", Constants.SETTINGS_FRAGMENT);
            startActivity(intent);
        }
    }

    public void requestPermissions(final String imageFrom) {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i("Permissions",
                    "Displaying storage permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(rootLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions(imageFrom);
                        }
                    })
                    .show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(rootLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions(imageFrom);
                        }
                    })
                    .show();
        } else {
            requestUngrantedPermissions(imageFrom);
        }
    }

    private void requestUngrantedPermissions(String imageFrom) {
        ArrayList<String> permissionList = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS_STORAGE_CAMERA.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, PERMISSIONS_STORAGE_CAMERA[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(PERMISSIONS_STORAGE_CAMERA[i]);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        if ("gallery".equals(imageFrom)) {
            ActivityCompat.requestPermissions(this, requiredPermission, REQUEST_GALLERY_PERMISSION);
        } else if ("camera".equals(imageFrom)) {
            ActivityCompat.requestPermissions(this, requiredPermission, REQUEST_CAMERA_PERMISSION);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            Log.i("Permissions", "Received response for camera permissions request.");
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(videoCapture, AppConstants.REQUEST_VIDEO_TRIMMER);
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(rootLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == REQUEST_GALLERY_PERMISSION) {
            Log.i("Permissions", "Received response for storage permissions request.");
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent();
                intent.setType("video/mp4");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), AppConstants.REQUEST_VIDEO_TRIMMER);
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(rootLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void setDynamicToolbarTitle(String name) {
        toolbarTitleTextView.setText(mToolbarTitle);
        mToolbarTitle = name;
    }

    @Override
    public void onBackStackChanged() {
        final Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        Menu menu = bottomNavigationView.getMenu();
        transparentLayerToolbar.setVisibility(View.GONE);
        transparentLayerNavigation.setVisibility(View.GONE);
        searchAllImageView.setVisibility(View.VISIBLE);
        notificationImg.setVisibility(View.VISIBLE);
        selectOptToolbarTitle.setVisibility(View.GONE);
        toolbarTitleTextView.setVisibility(View.VISIBLE);
        downArrowImageView.setVisibility(View.INVISIBLE);
        if (topFragment instanceof BecomeBloggerFragment) {
            toolbarUnderline.setVisibility(View.VISIBLE);
            toolbarTitleTextView.setText(getString(R.string.home_screen_trending_become_blogger));
            menu.findItem(R.id.action_write).setChecked(true);
            toolbarRelativeLayout.setVisibility(View.VISIBLE);
        } else if (topFragment instanceof UploadVideoInfoFragment) {
            toolbarUnderline.setVisibility(View.VISIBLE);
            toolbarTitleTextView.setText(getString(R.string.home_screen_trending_first_video_upload));
            menu.findItem(R.id.action_write).setChecked(true);
            toolbarRelativeLayout.setVisibility(View.VISIBLE);
        } else {
            toolbarUnderline.setVisibility(View.VISIBLE);
            if (topFragment instanceof ExploreArticleListingTypeFragment) {
                Utils.pushOpenScreenEvent(this, "TopicScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                toolbarTitleTextView.setText(getString(R.string.home_screen_select_an_option_title));
                toolbarTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.myprofile_toolbar_title));
                menu.findItem(R.id.action_profile).setChecked(true);
                toolbarRelativeLayout.setVisibility(View.VISIBLE);
                selectOptToolbarTitle.setText(getString(R.string.home_screen_select_an_option_title));
            } else if (topFragment instanceof SuggestedTopicsFragment) {
                Utils.pushOpenScreenEvent(this, "SuggestedTopicScreen",
                        SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                toolbarTitleTextView.setText(getString(R.string.home_screen_suggested_topic_title));
                toolbarTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.notification_toolbar_title));
                menu.findItem(R.id.action_write).setChecked(true);
                toolbarRelativeLayout.setVisibility(View.VISIBLE);
            } else if (topFragment instanceof FragmentMC4KHomeNew) {
                Utils.pushOpenScreenEvent(this, "HomeScreen",
                        SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                if (!SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "HomeScreen") &&
                        !BuildConfig.DEBUG) {
                    homeCoachmark.setVisibility(View.VISIBLE);
                }
                if (SharedPrefUtils.isTopicSelectionChanged(this)) {
                    ((FragmentMC4KHomeNew) topFragment).hideFollowTopicHeader();
                }
                langTextView.setVisibility(View.VISIBLE);
                toolbarTitleTextView.setText(getString(R.string.navigation_bar_home));
                toolbarTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.home_toolbar_titlecolor));
                menu.findItem(R.id.action_home).setChecked(true);
                toolbarRelativeLayout.setVisibility(View.VISIBLE);
            } else if (topFragment instanceof TopicsListingFragment) {
                Utils.pushOpenScreenEvent(this, "TopicArticlesListingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                if (!SharedPrefUtils.isCoachmarksShownFlag(BaseApplication.getAppContext(), "topics_article")) {
                    ((TopicsListingFragment) topFragment).showGuideView();
                }
                toolbarTitleTextView.setOnClickListener(this);
                toolbarTitleTextView.setText(mToolbarTitle);
                toolbarTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.home_toolbar_titlecolor));
                menu.findItem(R.id.action_home).setChecked(true);
                toolbarRelativeLayout.setVisibility(View.VISIBLE);
            } else if (topFragment instanceof TopicsShortStoriesContainerFragment) {
                Utils.pushOpenScreenEvent(this, "TopicsShortStoriesContainerFragment", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                toolbarTitleTextView.setOnClickListener(this);
                toolbarTitleTextView.setText(getString(R.string.article_listing_type_short_story_label));
                toolbarTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.home_toolbar_titlecolor));
                menu.findItem(R.id.action_home).setChecked(true);
                toolbarRelativeLayout.setVisibility(View.VISIBLE);
            } else if (topFragment instanceof GroupsViewFragment) {
                Utils.pushOpenScreenEvent(this, "GroupsViewFragment", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                toolbarTitleTextView.setText(getString(R.string.groups_support_groups));
                toolbarTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.groups_light_black_color));
                menu.findItem(R.id.action_location).setChecked(true);
                toolbarRelativeLayout.setVisibility(View.VISIBLE);
            }
        }

    }

    public void showToolbarAndNavigationLayer() {
        transparentLayerToolbar.setVisibility(View.VISIBLE);
        transparentLayerNavigation.setVisibility(View.VISIBLE);
    }

    public void hideToolbarAndNavigationLayer() {
        transparentLayerToolbar.setVisibility(View.GONE);
        transparentLayerNavigation.setVisibility(View.GONE);
    }

    private Badge addBadgeAt(int position, String number) {
        if (badge != null) {
            badge.setBadgeText("");
            if (number.equals("0")) {
                badge.hide(false);
            }
            return badge;
        }
        // add badge
        badge = new QBadgeView(this)
                .setBadgeText("")
                .setBadgeBackgroundColor(getResources().getColor(R.color.app_red))
                .setBadgePadding(5, true)
                .setBadgeGravity(Gravity.TOP | Gravity.END)
                .setGravityOffset(25, 12, true)
                .bindTarget(notificationImg);
        if (number.equals("0")) {
            badge.hide(false);
        }

        new QBadgeView(this)
                .setBadgeText(" " + getString(R.string.new_label) + " ")
                .setBadgeTextSize(7, true)
                .setBadgePadding(3, true)
                .setBadgeGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL)
                .setGravityOffset(0, 0, true)
                .bindTarget(bottomNavigationView.getBottomNavigationItemView(1));

        return badge;
    }


    @Override
    public void onMembershipStatusFetchSuccess(GroupsMembershipResponse body, int groupId) {
        String userType = null;
        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {

        } else {
            if (body.getData().getResult().get(0).getIsAdmin() == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_ADMIN;
            } else if (body.getData().getResult().get(0).getIsModerator() == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_MODERATOR;
            }
        }

        if (!AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(userType) && !AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(userType)) {
            if ("male".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender()) ||
                    "m".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender())) {
                Toast.makeText(this, getString(R.string.women_only), Toast.LENGTH_SHORT).show();
                if (BuildConfig.DEBUG || AppConstants.DEBUGGING_USER_ID.contains(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId())) {

                } else {
                    return;
                }
            } else {

            }
        }

        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {
            Intent intent = new Intent(this, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED.equals(body.getData().getResult().get(0).getStatus())) {
            Toast.makeText(this, getString(R.string.groups_user_blocked_msg), Toast.LENGTH_SHORT).show();
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(this, GroupDetailsActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(this, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("pendingMembershipFlag", true);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            startActivity(intent);
        }
    }

    @Override
    public void onMembershipStatusFetchFail() {

    }

    public void showBookmarkConfirmationTooltip() {
        bookmarkInfoView.setVisibility(View.VISIBLE);
        bookmarkInfoView.startAnimation(slideDownAnim);
    }

    public void hideViews() {
//        getSupportActionBar().hide();
    }

    public void showViews() {
//        getSupportActionBar().show();
    }

    @Override
    public void onDraftItemClick(View view, int position) {
        if (AppConstants.CONTENT_TYPE_SHORT_STORY.equals(allDraftsList.get(position).getContentType())) {
            if (Build.VERSION.SDK_INT > 15) {
                DraftListResult draftListResult = new DraftListResult();
                draftListResult.setArticleType(allDraftsList.get(position).getArticleType());
                draftListResult.setId(allDraftsList.get(position).getId());
                draftListResult.setBody(allDraftsList.get(position).getBody());
                draftListResult.setTitle(allDraftsList.get(position).getTitle());
                draftListResult.setCreatedTime(allDraftsList.get(position).getCreatedTime());
                draftListResult.setUpdatedTime((allDraftsList.get(position).getUpdatedTime()));
                draftListResult.setTags(allDraftsList.get(position).getTags());
                Intent intent = new Intent(this, AddShortStoryActivity.class);
                intent.putExtra("draftItem", draftListResult);
                intent.putExtra("from", "draftList");
                startActivity(intent);
            } else {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("http://www.momspresso.com/parenting/admin/setupablog"));
                startActivity(viewIntent);
            }
        } else {
            if (Build.VERSION.SDK_INT > 15) {
                DraftListResult draftListResult = new DraftListResult();
                draftListResult.setArticleType(allDraftsList.get(position).getArticleType());
                draftListResult.setId(allDraftsList.get(position).getId());
                draftListResult.setBody(allDraftsList.get(position).getBody());
                draftListResult.setTitle(allDraftsList.get(position).getTitle());
                draftListResult.setCreatedTime(allDraftsList.get(position).getCreatedTime());
                draftListResult.setUpdatedTime((allDraftsList.get(position).getUpdatedTime()));
                Intent intent = new Intent(this, EditorPostActivity.class);
                intent.putExtra("draftItem", draftListResult);
                intent.putExtra("from", "draftList");
                startActivity(intent);
            } else {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("http://www.momspresso.com/parenting/admin/setupablog"));
                startActivity(viewIntent);
            }
        }
    }

    public void appUpdatePopUp() {
        if (!StringUtils.isNullOrEmpty(onlineVersionCode) && !StringUtils.isNullOrEmpty(currentVersion)) {
            String[] v1 = currentVersion.split("\\.");
            String[] v2 = onlineVersionCode.split("\\.");
            Log.d("current::::", currentVersion);
            Log.d("online", onlineVersionCode);
            if (v1.length != v2.length)
                return;
            for (int pos = 0; pos < v1.length; pos++) {
                if (Integer.parseInt(v1[pos]) > Integer.parseInt(v2[pos])) {
                    rateNowDialog = true;
                    break;
                } else if (Integer.parseInt(v1[pos]) < Integer.parseInt(v2[pos])) {
                    if (SharedPrefUtils.getFrequencyForShowingUpdateApp(BaseApplication.getAppContext()) != frequecy) {
                        Dialog dialog = new Dialog(this);
                        dialog.setContentView(R.layout.update_app_pop_up_layout);
                        dialog.setCancelable(true);
                        TextView updateNow = dialog.findViewById(R.id.updateNowTextView);
                        updateNow.setOnClickListener(view -> {
                            String appPackage = DashboardActivity.this.getPackageName();
                            try {
                                Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
                                startActivity(rateIntent);
                            } catch (Exception e) {
                                Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
                                startActivity(rateIntent);
                            }
                            dialog.dismiss();
                        });
                        dialog.show();
                        SharedPrefUtils.setFrequencyForShowingAppUpdate(BaseApplication.getAppContext(), frequecy);
                        break;
                    } else {
                        rateNowDialog = true;
                    }
                } else {
                    rateNowDialog = true;
                }
            }
        }
    }

    private Boolean matchRegex(String tempDeepLinkURL) {
        try {
            String urlWithNoParams = tempDeepLinkURL.split("\\?")[0];
            if (urlWithNoParams.endsWith("/")) {
                urlWithNoParams = urlWithNoParams.substring(0, urlWithNoParams.length() - 1);
            }
            Pattern pattern = Pattern.compile(AppConstants.COLLECTION_LIST_REGEX);
            Matcher matcher = pattern.matcher(urlWithNoParams);
            if (matcher.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(this, CollectionsActivity.class);
                intent.putExtra("userId", separated[separated.length - 2]);
                startActivity(intent);
                return true;
            }

            Pattern pattern1 = Pattern.compile(AppConstants.COLLECTION_DETAIL_REGEX);
            Matcher matcher1 = pattern1.matcher(urlWithNoParams);
            if (matcher1.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(this, UserCollectionItemListActivity.class);
                intent.putExtra("id", separated[separated.length - 1]);
                startActivity(intent);
                return true;
            }

            Pattern pattern2 = Pattern.compile(AppConstants.BADGES_LISTING_REGEX);
            Matcher matcher2 = pattern2.matcher(urlWithNoParams);
            if (matcher2.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(this, BadgeActivity.class);
                intent.putExtra(Constants.USER_ID, separated[separated.length - 2]);
                startActivity(intent);
                return true;
            }

            Pattern pattern3 = Pattern.compile(AppConstants.BADGES_DETAIL_REGEX);
            Matcher matcher3 = pattern3.matcher(urlWithNoParams);
            if (matcher3.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(AppConstants.BADGE_ID, separated[separated.length - 1]);
                intent.putExtra(Constants.USER_ID, separated[separated.length - 3]);
                startActivity(intent);
                return true;
            }

            Pattern pattern4 = Pattern.compile(AppConstants.MILESTONE_DETAIL_REGEX);
            Matcher matcher4 = pattern4.matcher(urlWithNoParams);
            if (matcher4.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(AppConstants.MILESTONE_ID, separated[separated.length - 1]);
                intent.putExtra(Constants.USER_ID, separated[separated.length - 3]);
                startActivity(intent);
                return true;
            }

            Pattern pattern5 = Pattern.compile(AppConstants.USER_PROFILE_REGEX);
            Matcher matcher5 = pattern5.matcher(urlWithNoParams);
            if (matcher5.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(Constants.USER_ID, separated[separated.length - 1]);
                startActivity(intent);
                return true;
            }

            Pattern pattern6 = Pattern.compile(AppConstants.USER_ANALYTICS_REGEX);
            Matcher matcher6 = pattern6.matcher(urlWithNoParams);
            if (matcher6.matches()) {
                String[] separated = urlWithNoParams.split("/");
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("detail", "rank");
                intent.putExtra(Constants.USER_ID, separated[separated.length - 2]);
                startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        return false;
    }
}
