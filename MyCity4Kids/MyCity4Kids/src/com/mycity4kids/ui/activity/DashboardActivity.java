package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.joanzapata.iconify.widget.IconButton;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.facebook.FacebookUtils;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.response.DeepLinkingResposnse;
import com.mycity4kids.models.response.DeepLinkingResult;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.version.RateVersion;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.DeepLinkingAPI;
import com.mycity4kids.ui.fragment.ArticlesFragment;
import com.mycity4kids.ui.fragment.ChangeCityFragment;
import com.mycity4kids.ui.fragment.ChooseVideoUploadOptionDialogFragment;
import com.mycity4kids.ui.fragment.EditorPostFragment;
import com.mycity4kids.ui.fragment.ExternalCalFragment;
import com.mycity4kids.ui.fragment.FragmentAdultProfile;
import com.mycity4kids.ui.fragment.FragmentBusinesslistEvents;
import com.mycity4kids.ui.fragment.FragmentFamilyDetail;
import com.mycity4kids.ui.fragment.FragmentFamilyProfile;
import com.mycity4kids.ui.fragment.FragmentHomeCategory;
import com.mycity4kids.ui.fragment.FragmentKidProfile;
import com.mycity4kids.ui.fragment.FragmentMC4KHome;
import com.mycity4kids.ui.fragment.FragmentMC4KHomeNew;
import com.mycity4kids.ui.fragment.FragmentSetting;
import com.mycity4kids.ui.fragment.NotificationFragment;
import com.mycity4kids.ui.fragment.ProfileSelectFragment;
import com.mycity4kids.ui.fragment.RateAppDialogFragment;
import com.mycity4kids.ui.fragment.SendFeedbackFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.PermissionUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import life.knowledge4.videotrimmer.utils.FileUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class DashboardActivity extends BaseActivity implements View.OnClickListener, FragmentManager.OnBackStackChangedListener {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_GALLERY_PERMISSION = 2;

    private static String[] PERMISSIONS_STORAGE_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private Toolbar mToolbar;
    private CharSequence mTitle;
    //    private TextView mUsernName;
    public boolean filter = false;
    //    private ImageView profileImage;
    Tracker t;
    private String deepLinkUrl;
    String fragmentToLoad = "";
    private TextView itemMessagesBadgeTextView;
    private TextView toolbarTitleTextView;
    private FrameLayout badgeLayout;
    //    private FlowLayout languageContainer;
    private BottomNavigationViewEx bottomNavigationView;
    private RelativeLayout toolbarRelativeLayout;
    private RelativeLayout rootLayout;
    private String mToolbarTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        t = ((BaseApplication) getApplication()).getTracker(
                BaseApplication.TrackerName.APP_TRACKER);
        // Enable Display Features.
        t.enableAdvertisingIdCollection(true);
        // Set screen name.
        t.setScreenName("DashBoard");
        // You only need to set User ID on a tracker once. By setting it on the tracker, the ID will be
        // sent with all subsequent hits.
        // new code
        t.set("&uid", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        // This hit will be sent with the User ID value and be visible in User-ID-enabled views (profiles).
        t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("User Sign In").build());
        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        onNewIntent(getIntent());
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (null != intent.getParcelableExtra("notificationExtras")) {
            if ("upcoming_event_list".equals(((Bundle) intent.getParcelableExtra("notificationExtras")).getString("type")))
                fragmentToLoad = Constants.BUSINESS_EVENTLIST_FRAGMENT;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
//        mUsernName = (TextView) findViewById(R.id.txvUserName);
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        profileImage = (ImageView) findViewById(R.id.imgProfile);
//        languageContainer = (FlowLayout) findViewById(R.id.languageContainer);
        bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.navigation);
        toolbarRelativeLayout = (RelativeLayout) mToolbar.findViewById(R.id.toolbarRelativeLayout);
        toolbarTitleTextView = (TextView) mToolbar.findViewById(R.id.toolbarTitle);

        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.setTextVisibility(false);


        Utils.pushOpenScreenEvent(DashboardActivity.this, "DashBoard", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        // onclick events
//        findViewById(R.id.rdBtnToday).setOnClickListener(this);
//        findViewById(R.id.rdBtnUpcoming).setOnClickListener(this);
//        findViewById(R.id.feed_back).setOnClickListener(this);
//        findViewById(R.id.addVideosTextView).setOnClickListener(this);
//        findViewById(R.id.myVideosTextView).setOnClickListener(this);
        toolbarRelativeLayout.setOnClickListener(this);

        setSupportActionBar(mToolbar);

//        mUsernName.setText(SharedPrefUtils.getUserDetailModel(this).getFirst_name() + " " + SharedPrefUtils.getUserDetailModel(this).getLast_name());
        // setting profile image

        updateImageProfile();
        final BottomNavigationItemView[] items = bottomNavigationView.getBottomNavigationItemViews();
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setIconSize(30, 30);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        final Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {

                            case R.id.action_profile:
//                                if (topFragment instanceof ProfileSelectFragment) {
//                                    return true;
//                                }
//                                ProfileSelectFragment profileSelectFragment = new ProfileSelectFragment();
//                                Bundle profileBundle = new Bundle();
//                                profileSelectFragment.setArguments(profileBundle);
//                                addFragment(profileSelectFragment, profileBundle, true);
                                Intent intent1 = new Intent(DashboardActivity.this, BloggerDashboardActivity.class);
                                startActivity(intent1);
                                break;
                            case R.id.action_notification:
                                if (topFragment instanceof NotificationFragment) {
                                    return true;
                                }
                                NotificationFragment fragment = new NotificationFragment();
                                Bundle mBundle = new Bundle();
                                fragment.setArguments(mBundle);
                                addFragment(fragment, mBundle, true);
                                break;
                            case R.id.action_home:
                                if (topFragment instanceof FragmentMC4KHomeNew) {
                                    return true;
                                }
                                FragmentMC4KHomeNew fragment1 = new FragmentMC4KHomeNew();
                                Bundle mBundle1 = new Bundle();
                                fragment1.setArguments(mBundle1);
                                addFragment(fragment1, mBundle1, true);
                                break;
                            case R.id.action_write:
                                if (topFragment instanceof EditorPostFragment) {
                                    return true;
                                }
                                EditorPostFragment editorPostFragment = new EditorPostFragment();
                                Bundle editorBundle = new Bundle();
                                editorPostFragment.setArguments(editorBundle);
                                addFragment(editorPostFragment, editorBundle, true);
                                break;
                            case R.id.action_location:
                                Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
                                intent.putExtra("load_fragment", Constants.SETTINGS_FRAGMENT);
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                });

        if (Constants.BUSINESS_EVENTLIST_FRAGMENT.equals(fragmentToLoad)) {
            setTitle("Upcoming Events");
            FragmentBusinesslistEvents fragment = new FragmentBusinesslistEvents();
            Bundle mBundle = new Bundle();
            mBundle.putInt(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
            mBundle.putInt(Constants.EXTRA_CATEGORY_ID, SharedPrefUtils.getEventIdForCity(DashboardActivity.this));
            mBundle.putString(Constants.CATEGOTY_NAME, "Events & workshop");
            fragment.setArguments(mBundle);
            replaceFragment(fragment, mBundle, true);
        } else if (Constants.SETTINGS_FRAGMENT.equals(fragmentToLoad)) {
            setTitle("Settings");
            Bundle bundle = new Bundle();
            bundle.putString("bio", getIntent().getStringExtra("bio"));
            bundle.putString("firstName", getIntent().getStringExtra("firstName"));
            bundle.putString("lastName", getIntent().getStringExtra("lastName"));
            replaceFragment(new FragmentSetting(), bundle, true);
        } else {
            replaceFragment(new FragmentMC4KHomeNew(), null, false);
        }

//        if (SharedPrefUtils.isCityFetched(this) && SharedPrefUtils.getCurrentCityModel(this).getId() != AppConstants.OTHERS_CITY_ID) {
//            findViewById(R.id.rdBtnUpcoming).setVisibility(View.VISIBLE);
//            findViewById(R.id.rdBtnKids).setVisibility(View.VISIBLE);
//        } else {
//            findViewById(R.id.rdBtnUpcoming).setVisibility(View.GONE);
//            findViewById(R.id.rdBtnKids).setVisibility(View.GONE);
//        }
//
//        findViewById(R.id.rdBtnKids).setOnClickListener(this);
//        findViewById(R.id.rdBtnParentingBlogs).setOnClickListener(this);
//        findViewById(R.id.rdBtnMomspressoVideo).setOnClickListener(this);
//        findViewById(R.id.editor).setOnClickListener(this);
//        findViewById(R.id.imgProfile).setOnClickListener(this);
//        findViewById(R.id.txvUserName).setOnClickListener(this);

        /**
         * this dialog will open App Upgrade
         */

        RateVersion reteVersionModel = SharedPrefUtils.getRateVersion(this);
        int currentRateVersion = reteVersionModel.getAppRateVersion();
        currentRateVersion++;
        boolean isCompleteRateProcess = reteVersionModel.isAppRateComplete();
        RateVersion rateModel = new RateVersion();
        rateModel.setAppRateComplete(isCompleteRateProcess);
        rateModel.setAppRateVersion(currentRateVersion);
        SharedPrefUtils.setAppRateVersion(this, rateModel);
        if (!SharedPrefUtils.getRateVersion(this).isAppRateComplete() && currentRateVersion >= 10) {
            RateAppDialogFragment rateAppDialogFragment = new RateAppDialogFragment();
            reteVersionModel.setAppRateVersion(-20);
            rateAppDialogFragment.show(getFragmentManager(), rateAppDialogFragment.getClass().getSimpleName());
        }


//        populateLanguagesInMenu();
        // manage fragment change

//        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.content_frame);
//
//                if (currentFrag instanceof FragmentMC4KHome) {
//                    mDrawerToggle.setDrawerIndicatorEnabled(true);
//                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
//                    setTitle("");
//                } else if (currentFrag instanceof FragmentSetting) {
//                    setTitle("Settings");
//                    mDrawerToggle.setDrawerIndicatorEnabled(true);
//                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
//                } else if (currentFrag instanceof FragmentBusinesslistEvents) {
//                    setTitle("Upcoming Events");
//                } else if (currentFrag instanceof FragmentHomeCategory) {
//                    setTitle("Kids Resources");
//                } else if (currentFrag instanceof FragmentFamilyDetail) {
//                    setTitle("Family Details");
//                    mDrawerToggle.setDrawerIndicatorEnabled(false);
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                    getSupportActionBar().setHomeButtonEnabled(true);
//                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
//                } else if (currentFrag instanceof FragmentAdultProfile) {
//                    mDrawerToggle.setDrawerIndicatorEnabled(false);
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                    getSupportActionBar().setHomeButtonEnabled(true);
//                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
//                } else if (currentFrag instanceof FragmentKidProfile) {
//                    mDrawerToggle.setDrawerIndicatorEnabled(false);
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                    getSupportActionBar().setHomeButtonEnabled(true);
//                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
//                } else if (currentFrag instanceof FragmentEditorsPick) {
//                    mDrawerToggle.setDrawerIndicatorEnabled(true);
//                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
//                    if (SharedPrefUtils.getCurrentCityModel(DashboardActivity.this).getName().isEmpty()) {
//                        switch (SharedPrefUtils.getCurrentCityModel(DashboardActivity.this).getId()) {
//                            case 1:
//                                setTitle("Best of " + "Delhi-NCR");
//                                break;
//                            case 2:
//                                setTitle("Best of " + "Bangalore");
//                                break;
//                            case 3:
//                                setTitle("Best of " + "Mumbai");
//                                break;
//                            case 4:
//                                setTitle("Best of " + "Pune");
//                                break;
//                            case 5:
//                                setTitle("Best of " + "Hyderabad");
//                                break;
//                            case 6:
//                                setTitle("Best of " + "Chennai");
//                                break;
//                            case 7:
//                                setTitle("Best of " + "Kolkata");
//                                break;
//                            case 8:
//                                setTitle("Best of " + "Jaipur");
//                                break;
//                            case 9:
//                                setTitle("Best of " + "Ahmedabad");
//                                break;
//                            default:
//                                setTitle("Best of " + "Delhi-NCR");
//                                break;
//                        }
//
//                    } else {
//                        if (SharedPrefUtils.getCurrentCityModel(DashboardActivity.this).getName().equals("Delhi-Ncr")) {
//                            SharedPrefUtils.getCurrentCityModel(DashboardActivity.this).setName("Delhi-NCR");
//                        }
//                        setTitle("Best of " + SharedPrefUtils.getCurrentCityModel(DashboardActivity.this).getName());
//                    }
//                } else if (currentFrag instanceof SendFeedbackFragment) {
//                    mDrawerToggle.setDrawerIndicatorEnabled(true);
//                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
//                    setTitle("Send Feedback");
//                } else if (currentFrag instanceof ChangeCityFragment) {
//                    setTitle("Change City");
//                    mDrawerToggle.setDrawerIndicatorEnabled(false);
//                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
//                } else if (currentFrag instanceof SyncSettingFragment) {
//                    setTitle("Sync Settings");
//                    mDrawerToggle.setDrawerIndicatorEnabled(false);
//                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
//                }
//                invalidateOptionsMenu();
//                mDrawerToggle.syncState();
//
//            }
//        });
//
//        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()) {
//                    case -1:
//                        getSupportFragmentManager().popBackStack();
//                        break;
//
//                    default:
//                        break;
//                }
//            }
//        });

    }

    // The onNewIntent() is overridden to get and resolve the data for deep linking
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle notificationExtras = intent.getParcelableExtra("notificationExtras");
        if (notificationExtras != null) {
            if (notificationExtras.getString("type").equalsIgnoreCase("article_details")) {
                String articleId = notificationExtras.getString("id");
                String authorId = notificationExtras.getString("userId");
                String blogSlug = notificationExtras.getString("blogSlug");
                String titleSlug = notificationExtras.getString("titleSlug");
                Intent intent1 = new Intent(DashboardActivity.this, ArticlesAndBlogsDetailsActivity.class);
                intent1.putExtra("fromNotification", true);
                intent1.putExtra(Constants.ARTICLE_ID, articleId);
                intent1.putExtra(Constants.AUTHOR_ID, authorId);
                intent1.putExtra(Constants.BLOG_SLUG, blogSlug);
                intent1.putExtra(Constants.TITLE_SLUG, titleSlug);
                intent1.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
                intent.putExtra(Constants.FROM_SCREEN, "Notification");
                intent1.putExtra(Constants.ARTICLE_INDEX, "-1");
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("video_details")) {
                String articleId = notificationExtras.getString("id");
                String authorId = notificationExtras.getString("userId");
                Intent intent1 = new Intent(DashboardActivity.this, VlogsDetailActivity.class);
                intent1.putExtra("fromNotification", true);
                intent1.putExtra(Constants.VIDEO_ID, articleId);
                intent1.putExtra(Constants.AUTHOR_ID, authorId);
                intent1.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
                intent.putExtra(Constants.FROM_SCREEN, "Notification");
                intent1.putExtra(Constants.ARTICLE_INDEX, "-1");
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("event_details")) {
                String eventId = notificationExtras.getString("id");
                Intent resultIntent = new Intent(getApplicationContext(), BusinessDetailsActivity.class);
                resultIntent.putExtra("fromNotification", true);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resultIntent.putExtra(Constants.CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getApplication()));
                resultIntent.putExtra(Constants.BUSINESS_OR_EVENT_ID, eventId + "");
                resultIntent.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                resultIntent.putExtra(Constants.DISTANCE, "0");
                startActivity(resultIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("webView")) {
                String url = notificationExtras.getString("url");
                Intent intent1 = new Intent(this, LoadWebViewActivity.class);
                intent1.putExtra("fromNotification", true);
                intent1.putExtra(Constants.WEB_VIEW_URL, url);
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("profile")) {
                String u_id = notificationExtras.getString("userId");
                Intent intent1 = new Intent(this, BloggerDashboardActivity.class);
                intent1.putExtra("fromNotification", true);
                intent1.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, u_id);
                intent1.putExtra(AppConstants.AUTHOR_NAME, "");
                intent1.putExtra(Constants.FROM_SCREEN, "Notification");
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("upcoming_event_list")) {

                fragmentToLoad = Constants.BUSINESS_EVENTLIST_FRAGMENT;
//                intent.getExtras().putString(Constants.LOAD_FRAGMENT, Constants.BUSINESS_EVENTLIST_FRAGMENT);
            } else if (notificationExtras.getString("type").equalsIgnoreCase(AppConstants.APP_SETTINGS_DEEPLINK)) {
                Intent intent1 = new Intent(this, SettingsActivity.class);
                intent1.putExtra("fromNotification", true);
                intent1.putExtra("load_fragment", Constants.SETTINGS_FRAGMENT);
                startActivity(intent1);
            }
        } else {
            String tempDeepLinkURL = intent.getStringExtra(AppConstants.DEEP_LINK_URL);
            if (!StringUtils.isNullOrEmpty(tempDeepLinkURL)) {
                if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_EDITOR_URL)) {
                    final String bloggerId = tempDeepLinkURL.substring(tempDeepLinkURL.lastIndexOf("/") + 1, tempDeepLinkURL.length());
                    if (!StringUtils.isNullOrEmpty(bloggerId) && !bloggerId.equals(SharedPrefUtils.getUserDetailModel(this).getDynamoId())) {
                        showAlertDialog("Message", "Logged in as " + SharedPrefUtils.getUserDetailModel(this).getFirst_name() + " " + SharedPrefUtils.getUserDetailModel(this).getLast_name(), new OnButtonClicked() {
                            @Override
                            public void onButtonCLick(int buttonId) {
                                if (Build.VERSION.SDK_INT > 15) {
                                    Utils.pushEvent(DashboardActivity.this, GTMEventType.ADD_BLOG_CLICKED_EVENT,
                                            SharedPrefUtils.getUserDetailModel(DashboardActivity.this).getDynamoId() + "", "Mobile Deep Link");
                                    launchEditor();
                                } else {
                                    showToast("This version of android is no more supported.");
                                }
                            }
                        });
                    } else {
                        if (Build.VERSION.SDK_INT > 15) {
                            Utils.pushEvent(DashboardActivity.this, GTMEventType.ADD_BLOG_CLICKED_EVENT,
                                    SharedPrefUtils.getUserDetailModel(DashboardActivity.this).getDynamoId() + "", "Mobile Deep Link");
                            launchEditor();
                        } else {
                            showToast("This version of android is no more supported.");
                        }
                    }
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_ADD_FUNNY_VIDEO_URL)) {
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
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_PROFILE_URL)) {
                    final String bloggerId = tempDeepLinkURL.substring(tempDeepLinkURL.lastIndexOf("/") + 1, tempDeepLinkURL.length());
                    if (!StringUtils.isNullOrEmpty(bloggerId) && !bloggerId.equals(SharedPrefUtils.getUserDetailModel(this).getDynamoId())) {
                        showAlertDialog("Message", "Logged in as " + SharedPrefUtils.getUserDetailModel(this).getFirst_name() + " " + SharedPrefUtils.getUserDetailModel(this).getLast_name(), new OnButtonClicked() {
                            @Override
                            public void onButtonCLick(int buttonId) {
                                Intent profileIntent = new Intent(DashboardActivity.this, BloggerDashboardActivity.class);
                                profileIntent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, SharedPrefUtils.getUserDetailModel(DashboardActivity.this).getDynamoId());
                                startActivity(profileIntent);
                            }
                        });
                    } else {
                        Intent profileIntent = new Intent(DashboardActivity.this, BloggerDashboardActivity.class);
                        profileIntent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, bloggerId);
                        profileIntent.putExtra(AppConstants.AUTHOR_NAME, "");
                        profileIntent.putExtra(Constants.FROM_SCREEN, "Deep Linking");
                        startActivity(profileIntent);
                    }
                } else {
                    getDeepLinkData(tempDeepLinkURL);
                }
            } else if (Constants.BUSINESS_EVENTLIST_FRAGMENT.equals(intent.getStringExtra(Constants.LOAD_FRAGMENT))) {
                fragmentToLoad = Constants.BUSINESS_EVENTLIST_FRAGMENT;
                setTitle("Upcoming Events");
                FragmentBusinesslistEvents fragment = new FragmentBusinesslistEvents();
                Bundle mBundle = new Bundle();
                mBundle.putInt(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                mBundle.putInt(Constants.EXTRA_CATEGORY_ID, SharedPrefUtils.getEventIdForCity(DashboardActivity.this));
                mBundle.putString(Constants.CATEGOTY_NAME, "Events & workshop");
                fragment.setArguments(mBundle);
                replaceFragment(fragment, mBundle, true);
            }
            deepLinkUrl = intent.getStringExtra(AppConstants.DEEP_LINK_URL);
        }
    }

    private void populateLanguagesInMenu() {
        try {
            FileInputStream fileInputStream = openFileInput(AppConstants.LANGUAGES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
//            ConfigResult res = new Gson().fromJson(fileContent, ConfigResult.class);
            LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                    fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                    }.getType()
            );
            Log.d("Map", "" + retMap.toString());
            for (final Map.Entry<String, LanguageConfigModel> entry : retMap.entrySet()) {
                final TextView view = (TextView) getLayoutInflater().inflate(R.layout.language_navigation_menu_item, null);
                view.setText(entry.getValue().getDisplay_name());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent languageIntent = new Intent(DashboardActivity.this, FilteredTopicsArticleListingActivity.class);
                        languageIntent.putExtra("selectedTopics", entry.getValue().getId());
                        languageIntent.putExtra("displayName", entry.getValue().getDisplay_name());
                        languageIntent.putExtra("categoryName", entry.getValue().getName());
                        languageIntent.putExtra("isLanguage", true);
                        languageIntent.putExtra(Constants.FROM_SCREEN, "Navigation Menu");
                        startActivity(languageIntent);
                    }
                });
//                languageContainer.addView(view);
            }
        } catch (FileNotFoundException ffe) {
            Crashlytics.logException(ffe);
            Log.d("MC4kException", Log.getStackTraceString(ffe));
        }
    }

    private boolean showUploadVideoTutorial() {
        String version = AppUtils.getAppVersion(this);
        if (version.equals(AppConstants.UPLOAD_VIDEO_RELEASE_VERSION) && SharedPrefUtils.isUploadVideoFirstLaunch(this)) {
            SharedPrefUtils.setUploadVideoFirstLaunch(this, false);
            Intent videoFlIntent = new Intent(this, UploadVideoFLActivity.class);
            startActivity(videoFlIntent);
            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);
        return ret;
    }

    public void updateImageProfile() {
        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(this))) {
//            Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)
//                    .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(profileImage);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (showUploadVideoTutorial()) return;
//        mUsernName.setText(SharedPrefUtils.getUserDetailModel(this).getFirst_name() + " " + SharedPrefUtils.getUserDetailModel(this).getLast_name());
        updateImageProfile();
        final Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        refreshMenu();
        if (topFragment instanceof FragmentMC4KHome) {
            try {
                ((FragmentMC4KHome) topFragment).refreshList();
//                if (SharedPrefUtils.isCityFetched(this) && SharedPrefUtils.getCurrentCityModel(this).getId() != AppConstants.OTHERS_CITY_ID) {
//                    findViewById(R.id.rdBtnUpcoming).setVisibility(View.VISIBLE);
//                    findViewById(R.id.rdBtnKids).setVisibility(View.VISIBLE);
//                } else {
//                    findViewById(R.id.rdBtnUpcoming).setVisibility(View.GONE);
//                    findViewById(R.id.rdBtnKids).setVisibility(View.GONE);
//                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (topFragment instanceof FragmentBusinesslistEvents) {

            try {
                ((FragmentBusinesslistEvents) topFragment).refreshList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (topFragment instanceof SendFeedbackFragment) {
            refreshMenu();
            setTitle("Send Feedback");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void refreshMenu() {
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it
        final Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (topFragment instanceof FragmentMC4KHome) {
            getMenuInflater().inflate(R.menu.menu_home, menu);
            MenuItem itemMessages = menu.findItem(R.id.notification_center);

            badgeLayout = (FrameLayout) MenuItemCompat.getActionView(itemMessages);
            String notifCount = "";
            if (null != itemMessagesBadgeTextView && itemMessagesBadgeTextView.getText() != null) {
                notifCount = itemMessagesBadgeTextView.getText().toString();
            }
            itemMessagesBadgeTextView = (TextView) badgeLayout.findViewById(R.id.badge_textView);
            itemMessagesBadgeTextView.setText(notifCount);

            if (StringUtils.isNullOrEmpty(itemMessagesBadgeTextView.getText().toString())) {
                itemMessagesBadgeTextView.setVisibility(View.GONE); // initially hidden
            }

            IconButton iconButtonMessages = (IconButton) badgeLayout.findViewById(R.id.badge_icon_button);
//            iconButtonMessages.setText("{fa-envelope}");
            iconButtonMessages.setTextColor(ContextCompat.getColor(this, R.color.grey));

            iconButtonMessages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent wintent = new Intent(getApplicationContext(), NotificationCenterListActivity.class);
                    startActivity(wintent);
                }
            });
            itemMessagesBadgeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent wintent = new Intent(getApplicationContext(), NotificationCenterListActivity.class);
                    startActivity(wintent);
                }
            });
        } else if (topFragment instanceof FragmentFamilyDetail) {

            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof FragmentMC4KHomeNew) {
//            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof FragmentFamilyProfile) {

            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof FragmentAdultProfile) {

            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof FragmentKidProfile) {

            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof ChangeCityFragment) {
            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof FragmentBusinesslistEvents) {
            getMenuInflater().inflate(R.menu.menu_event, menu);
        } else if (topFragment instanceof FragmentHomeCategory) {
            getMenuInflater().inflate(R.menu.kidsresource_listing, menu);
        }
        return true;
    }

    public void updateUnreadNotificationCount(String unreadNotifCount) {
        if (StringUtils.isNullOrEmpty(unreadNotifCount) || "0".equals(unreadNotifCount)) {
            itemMessagesBadgeTextView.setVisibility(View.GONE);
            itemMessagesBadgeTextView.setText("");
        } else {
            itemMessagesBadgeTextView.setVisibility(View.VISIBLE);
            itemMessagesBadgeTextView.setText(unreadNotifCount);
        }

    }

    @Override
    public void setTitle(CharSequence title) {
//        mTitle = title;
//        if (getSupportActionBar() != null) {
//            if (StringUtils.isEmpty(mTitle)) {
//                getSupportActionBar().setIcon(R.drawable.myicon);
//            } else {
//                getSupportActionBar().setIcon(null);
//            }
//            getSupportActionBar().setTitle(mTitle);
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        switch (item.getItemId()) {

            case R.id.filter:
                if (topFragment instanceof FragmentBusinesslistEvents) {
                    ((FragmentBusinesslistEvents) topFragment).toggleFilter();
                } else if (topFragment instanceof ArticlesFragment) {
                    Intent intent = new Intent(getApplicationContext(), TopicsFilterActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.write:
                if (Build.VERSION.SDK_INT > 15) {
                    Utils.pushEvent(DashboardActivity.this, GTMEventType.ADD_BLOG_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Header");
                    launchEditor();
                } else {
                    showToast("This version of android is no more supported.");
                }
                break;
            case R.id.search:
                if (topFragment instanceof ArticlesFragment || topFragment instanceof FragmentMC4KHome) {
                    Intent intent = new Intent(getApplicationContext(), SearchArticlesAndAuthorsActivity.class);
                    intent.putExtra(Constants.FILTER_NAME, "");
                    intent.putExtra(Constants.TAB_POSITION, 0);
                    startActivity(intent);
                }
                break;
            case R.id.notification_center:
                Intent wintent = new Intent(DashboardActivity.this, NotificationCenterListActivity.class);
                startActivity(wintent);
                break;
            case R.id.save:
                if (topFragment instanceof FragmentAdultProfile) {
                    ((FragmentAdultProfile) topFragment).callService();
                } else if (topFragment instanceof FragmentKidProfile) {
                    ((FragmentKidProfile) topFragment).callService();
                } else if (topFragment instanceof FragmentFamilyProfile) {
                    ((FragmentFamilyProfile) topFragment).callService();
                } else if (topFragment instanceof FragmentFamilyDetail) {
                    ((FragmentFamilyDetail) topFragment).onHeaderButtonTapped();
                } else if (topFragment instanceof ChangeCityFragment)
                    ((ChangeCityFragment) topFragment).changeCity();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.kidsresource_bookmark:
                if (topFragment instanceof FragmentHomeCategory) {
                    Log.d("KIDS RESOURCE ", "bookmark kids resource");
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

    protected void updateUi(Response response) {

        switch (response.getDataType()) {
            case AppConstants.DEEP_LINK_RESOLVER_REQUEST:
                break;

        }

    }

    public String getTodayTime() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM, EEEE");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    @Override
    public void onClick(View v) {
        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        switch (v.getId()) {
            case R.id.rdBtnToday:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.MC4KToday_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Left Menu Screen");
                replaceFragment(new FragmentMC4KHome(), null, false);
                setTitle("");
                break;
            case R.id.rdBtnUpcoming:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.UPCOMING_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Left Menu Screen");
                Constants.IS_SEARCH_LISTING = false;
                setTitle("Upcoming Events");
                FragmentBusinesslistEvents fragment = new FragmentBusinesslistEvents();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                bundle.putInt(Constants.EXTRA_CATEGORY_ID, SharedPrefUtils.getEventIdForCity(DashboardActivity.this));
                bundle.putString(Constants.CATEGOTY_NAME, "Events & workshop");
                fragment.setArguments(bundle);
                replaceFragment(fragment, bundle, true);
                break;
            case R.id.rdBtnKids:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.RESOURCES_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Left Menu Screen");
                Constants.IS_SEARCH_LISTING = false;
                setTitle("Kids Resources");
                replaceFragment(new FragmentHomeCategory(), null, true);
                break;
            case R.id.rdBtnParentingBlogs:
                Intent intent = new Intent(getApplicationContext(), TopicsFilterActivity.class);
                startActivity(intent);
                break;
            case R.id.rdBtnMomspressoVideo:
                Intent videoArticlesIntent = new Intent(this, AllVideoSectionActivity.class);
                startActivity(videoArticlesIntent);
                break;
            case R.id.editor:
                if (Build.VERSION.SDK_INT > 15) {
                    Utils.pushEvent(DashboardActivity.this, GTMEventType.ADD_BLOG_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Navigation Menu");
                    launchEditor();
                } else {
                    showToast("This version of android is no more supported.");
                }
                break;
            case R.id.feed_back:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.FEEDBACK_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Left Menu Screen");
                setTitle("Send Feedback");
                replaceFragment(new SendFeedbackFragment(), null, true);
                break;
            case R.id.addVideosTextView:
                launchAddVideoOptions();
                break;
            case R.id.myVideosTextView:
                Intent funnyIntent = new Intent(DashboardActivity.this, MyFunnyVideosListingActivity.class);
                funnyIntent.putExtra(Constants.FROM_SCREEN, "Navigation Menu");
                startActivity(funnyIntent);
                break;
            case R.id.txvUserName:
            case R.id.imgProfile:
                Intent intent4 = new Intent(DashboardActivity.this, BloggerDashboardActivity.class);
                startActivity(intent4);
                break;
            case R.id.toolbarRelativeLayout:
                ExploreArticleListingTypeFragment fragment1 = new ExploreArticleListingTypeFragment();
                Bundle mBundle1 = new Bundle();
                fragment1.setArguments(mBundle1);
                addFragment(fragment1, mBundle1, true);
                break;
            default:
                break;
        }
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
            Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (topFragment instanceof ExploreArticleListingTypeFragment) {
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

    public CharSequence getTitleText() {
        return getSupportActionBar().getTitle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        try {
            Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

            if (topFragment instanceof ExternalCalFragment) {
                FacebookUtils.onActivityResult(this, requestCode, resultCode, data);
            }

            switch (requestCode) {
                case Constants.OPEN_GALLERY:
                    if (topFragment instanceof FragmentFamilyProfile) {
                        topFragment.onActivityResult(requestCode, resultCode, data);
                    } else if (topFragment instanceof FragmentFamilyDetail) {
//                        ((FragmentFamilyDetail) topFragment).onActivityResultDelegate(requestCode, resultCode, data);
                    }
                    break;
                case Constants.TAKE_PICTURE:
                    if (topFragment instanceof FragmentFamilyProfile) {
                        topFragment.onActivityResult(requestCode, resultCode, data);
                    } else if (topFragment instanceof FragmentFamilyDetail) {
//                        ((FragmentFamilyDetail) topFragment).onActivityResultDelegate(requestCode, resultCode, data);
                    }
                    break;

                case Constants.CROP_IMAGE:
                    if (topFragment instanceof FragmentFamilyProfile) {
                        topFragment.onActivityResult(requestCode, resultCode, data);
                    } else if (topFragment instanceof FragmentFamilyDetail) {
//                        ((FragmentFamilyDetail) topFragment).onActivityResultDelegate(requestCode, resultCode, data);
                    }
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
                        if (topFragment instanceof ExternalCalFragment) {
                            ((ExternalCalFragment) topFragment).setAccountName(accountName);
                        }
                    } else if (resultCode == RESULT_CANCELED) {
                        showToast("Account unspecified.");
                    }
                    break;
                case AppConstants.REQUEST_AUTHORIZATION:
                    if (resultCode != RESULT_OK) {
                        if (topFragment instanceof ExternalCalFragment) {
                            ((ExternalCalFragment) topFragment).chooseAccount();
                        }
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
                    if (topFragment instanceof FragmentMC4KHome) {
                        ((FragmentMC4KHome) topFragment).refreshList();
                        if (SharedPrefUtils.isCityFetched(this) && SharedPrefUtils.getCurrentCityModel(this).getId() != AppConstants.OTHERS_CITY_ID) {
                            findViewById(R.id.rdBtnUpcoming).setVisibility(View.VISIBLE);
                            findViewById(R.id.rdBtnKids).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.rdBtnUpcoming).setVisibility(View.GONE);
                            findViewById(R.id.rdBtnKids).setVisibility(View.GONE);
                        }
                    } else if (topFragment instanceof FragmentMC4KHome) {
                        ((FragmentMC4KHome) topFragment).notifyTaskList();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(this, VideoTrimmerActivity.class);
        String filepath = FileUtils.getPath(this, uri);
        if (null != filepath && filepath.endsWith(".mp4")) {
            intent.putExtra("EXTRA_VIDEO_PATH", FileUtils.getPath(this, uri));
            startActivity(intent);
        } else {
            showToast("please choose a .mp4 format file");
        }
    }

    public void showGooglePlayServicesAvailabilityErrorDialog(int connectionStatusCode) {

        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (topFragment instanceof ExternalCalFragment) {
            ((ExternalCalFragment) topFragment).showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
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
        /*DeepLinkingController _deepLinkingController = new DeepLinkingController(this, this);
        _deepLinkingController.getData(AppConstants.DEEP_LINK_RESOLVER_REQUEST, deepLinkURL);
        showProgressDialog("");*/
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
                    DeepLinkingResposnse responseData = (DeepLinkingResposnse) response.body();
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
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });
    }

    private void identifyTargetScreen(DeepLinkingResult data) {
        switch (data.getType()) {
            case AppConstants.DEEP_LINK_BUSINESS_LISTING:
                renderBusinessListingScreen(data);
                break;
            case AppConstants.DEEP_LINK_BUSINESS_DETAIL:
                renderBusinessDetailScreen(data);
                break;
            case AppConstants.DEEP_LINK_EVENT_LISTING:
                renderEventListingScreen(data);
                break;
            case AppConstants.DEEP_LINK_EVENT_DETAIL:
                renderEventDetailScreen(data);
                break;
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
        }
    }

    private void renderArticleListingScreen(DeepLinkingResult data) {
        replaceFragment(new ArticlesFragment(), null, true);
    }

    private void renderAuthorDetailScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getAuthor_id())) {
            Intent intent = new Intent(DashboardActivity.this, BloggerDashboardActivity.class);
            intent.putExtra(AppConstants.PUBLIC_PROFILE_FLAG, true);
            intent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, data.getAuthor_id());
            intent.putExtra(AppConstants.AUTHOR_NAME, "" + data.getAuthor_name());
            intent.putExtra(Constants.FROM_SCREEN, "Deep Linking");
            startActivity(intent);
        }
    }

    private void renderBusinessListingScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getCategory_id())) {
            Intent _businessListIntent = new Intent(DashboardActivity.this, BusinessListActivityKidsResources.class);
            _businessListIntent.putExtra(Constants.EXTRA_CATEGORY_ID, Integer.parseInt(data.getCategory_id()));
            _businessListIntent.putExtra(Constants.CITY_ID_DEEPLINK, data.getCity_id() + "");
            _businessListIntent.putExtra(Constants.IS_FROM_DEEPLINK, true);
            _businessListIntent.putExtra(Constants.DEEPLINK_URL, data.getUrl());
            startActivity(_businessListIntent);
        }
    }

    private void renderBusinessDetailScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getCategory_id()) && !StringUtils.isNullOrEmpty(data.getDetail_id())) {
            Intent _eventDetailIntent = new Intent(DashboardActivity.this, BusinessDetailsActivity.class);
            _eventDetailIntent.putExtra(Constants.CATEGORY_ID, Integer.parseInt(data.getCategory_id()));
            _eventDetailIntent.putExtra(Constants.BUSINESS_OR_EVENT_ID, data.getDetail_id() + "");
            _eventDetailIntent.putExtra(Constants.PAGE_TYPE, Constants.BUSINESS_PAGE_TYPE);
            _eventDetailIntent.putExtra(Constants.DEEPLINK_URL, data.getUrl());
            startActivity(_eventDetailIntent);
        }
    }

    // Pending for Indexing
    private void renderEventListingScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getCategory_id())) {
            Constants.IS_SEARCH_LISTING = false;
            setTitle("Upcoming Events");
            FragmentBusinesslistEvents fragment = new FragmentBusinesslistEvents();
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
            bundle.putInt(Constants.EXTRA_CATEGORY_ID, Integer.parseInt(data.getCategory_id()));
            bundle.putString(Constants.CATEGOTY_NAME, "Events & workshop");
            bundle.putString(Constants.DEEPLINK_URL, data.getUrl());
            fragment.setArguments(bundle);
            replaceFragment(fragment, bundle, true);
        }
    }

    private void renderEventDetailScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getCategory_id()) && !StringUtils.isNullOrEmpty(data.getDetail_id())) {
            Intent _eventDetailIntent = new Intent(DashboardActivity.this, BusinessDetailsActivity.class);
            _eventDetailIntent.putExtra(Constants.CATEGORY_ID, Integer.parseInt(data.getCategory_id()));
            _eventDetailIntent.putExtra(Constants.BUSINESS_OR_EVENT_ID, data.getDetail_id() + "");
            _eventDetailIntent.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
            _eventDetailIntent.putExtra(Constants.DEEPLINK_URL, data.getUrl());
            startActivity(_eventDetailIntent);
        }
    }

    private void renderAuthorListingScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getAuthor_name())) {
            Intent _authorListIntent = new Intent(DashboardActivity.this, BloggerDashboardActivity.class);
            _authorListIntent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, data.getAuthor_id());
            _authorListIntent.putExtra(Constants.DEEPLINK_URL, deepLinkUrl);
            _authorListIntent.putExtra(AppConstants.AUTHOR_NAME, "" + data.getAuthor_name());
            _authorListIntent.putExtra(Constants.FROM_SCREEN, "Deep Linking");
            startActivity(_authorListIntent);
        }
    }

    private void renderBloggerListingScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getBlog_title())) {
            Intent _bloggerListIntent = new Intent(DashboardActivity.this, BloggerDashboardActivity.class);
            _bloggerListIntent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, data.getAuthor_id());
            _bloggerListIntent.putExtra(Constants.DEEPLINK_URL, deepLinkUrl);
            _bloggerListIntent.putExtra(AppConstants.AUTHOR_NAME, "" + data.getAuthor_name());
            _bloggerListIntent.putExtra(Constants.FROM_SCREEN, "Deep Linking");
            startActivity(_bloggerListIntent);
        }
    }

    private void renderArticleDetailScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getArticle_id())) {
            Intent intent = new Intent(DashboardActivity.this, ArticlesAndBlogsDetailsActivity.class);
            intent.putExtra(Constants.AUTHOR_ID, data.getAuthor_id());
            intent.putExtra(Constants.ARTICLE_ID, data.getArticle_id());
            intent.putExtra(Constants.DEEPLINK_URL, deepLinkUrl);
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Deep Linking");
            intent.putExtra(Constants.ARTICLE_INDEX, "-1");
            intent.putExtra(Constants.FROM_SCREEN, "Deep Linking");
            startActivity(intent);
        }
    }

    private void renderVlogDetailScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getId())) {
            Intent intent = new Intent(DashboardActivity.this, VlogsDetailActivity.class);
//            intent.putExtra(Constants.AUTHOR_ID, data.getAuthor_id());
            intent.putExtra(Constants.VIDEO_ID, data.getId());
            intent.putExtra(Constants.DEEPLINK_URL, deepLinkUrl);
            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Deep Linking");
            intent.putExtra(Constants.ARTICLE_INDEX, "-1");
            intent.putExtra(Constants.FROM_SCREEN, "Deep Linking");
            startActivity(intent);
        }
    }

    private void renderAppSettingsScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getId())) {
            Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
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
//        if ("Explore".equals(fragmentId)) {
//            toolbarRelativeLayout.setOnClickListener(null);
//            getSupportActionBar().setIcon(null);
//            toolbarRelativeLayout.setVisibility(View.VISIBLE);
//            setSupportActionBar(mToolbar);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        } else {

//        }

    }

    @Override
    public void onBackStackChanged() {
        final Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        System.out.println("====================================================changeeeeeeeeeeeeeeeeeeeeeeeeee" + topFragment);
        Menu menu = bottomNavigationView.getMenu();
        if (null != topFragment && topFragment instanceof ExploreArticleListingTypeFragment) {
            toolbarRelativeLayout.setOnClickListener(null);
            menu.findItem(R.id.action_home).setChecked(true);
            toolbarTitleTextView.setText(getString(R.string.home_screen_select_an_option_title));
            toolbarRelativeLayout.setVisibility(View.VISIBLE);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            if (null != topFragment && topFragment instanceof ProfileSelectFragment) {
                toolbarRelativeLayout.setOnClickListener(null);
                toolbarTitleTextView.setText(getString(R.string.home_screen_profile_title));
                menu.findItem(R.id.action_profile).setChecked(true);
            } else if (null != topFragment && topFragment instanceof NotificationFragment) {
                toolbarRelativeLayout.setOnClickListener(null);
                toolbarTitleTextView.setText(getString(R.string.home_screen_notification_title));
                menu.findItem(R.id.action_notification).setChecked(true);
            } else if (null != topFragment && topFragment instanceof FragmentMC4KHomeNew) {
                toolbarRelativeLayout.setOnClickListener(this);
                toolbarTitleTextView.setText(getString(R.string.home_screen_trending_title));
                menu.findItem(R.id.action_home).setChecked(true);
            } else if (null != topFragment && topFragment instanceof EditorPostFragment) {
                toolbarRelativeLayout.setOnClickListener(null);
                toolbarTitleTextView.setText(getString(R.string.home_screen_editor_title));
                menu.findItem(R.id.action_write).setChecked(true);
            } else if (null != topFragment && topFragment instanceof TopicsListingFragment) {
                toolbarRelativeLayout.setOnClickListener(null);
                toolbarTitleTextView.setText(mToolbarTitle);
                menu.findItem(R.id.action_home).setChecked(true);
            }

            toolbarRelativeLayout.setVisibility(View.VISIBLE);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }


}