package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.response.BlogPageResponse;
import com.mycity4kids.models.response.DeepLinkingResposnse;
import com.mycity4kids.models.response.DeepLinkingResult;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.ShortStoryDetailResult;
import com.mycity4kids.models.version.RateVersion;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BlogPageAPI;
import com.mycity4kids.retrofitAPIsInterfaces.DeepLinkingAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.fragment.AddArticleVideoFragment;
import com.mycity4kids.ui.fragment.BecomeBloggerFragment;
import com.mycity4kids.ui.fragment.ChangePreferredLanguageDialogFragment;
import com.mycity4kids.ui.fragment.ChooseVideoUploadOptionDialogFragment;
import com.mycity4kids.ui.fragment.ExploreFragment;
import com.mycity4kids.ui.fragment.FragmentBusinesslistEvents;
import com.mycity4kids.ui.fragment.FragmentHomeCategory;
import com.mycity4kids.ui.fragment.FragmentMC4KHomeNew;
import com.mycity4kids.ui.fragment.MyAccountProfileFragment;
import com.mycity4kids.ui.fragment.GroupsFragment;
import com.mycity4kids.ui.fragment.NotificationFragment;
import com.mycity4kids.ui.fragment.RateAppDialogFragment;
import com.mycity4kids.ui.fragment.SendFeedbackFragment;
import com.mycity4kids.ui.fragment.SuggestedTopicsFragment;
import com.mycity4kids.ui.fragment.UploadVideoInfoFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.PermissionUtil;

import java.util.ArrayList;

import life.knowledge4.videotrimmer.utils.FileUtils;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class DashboardActivity extends BaseActivity implements View.OnClickListener, FragmentManager.OnBackStackChangedListener, GroupMembershipStatus.IMembershipStatus {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_GALLERY_PERMISSION = 2;

    private static String[] PERMISSIONS_STORAGE_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    public static final String COMMON_PREF_FILE = "my_city_prefs";

    public boolean filter = false;
    Tracker t;
    private String deepLinkUrl;
    private String mToolbarTitle = "";
    private String fragmentToLoad = "";

    private Toolbar mToolbar;
    private TextView toolbarTitleTextView;
    private ImageView searchAllImageView;
    private BottomNavigationViewEx bottomNavigationView;
    private RelativeLayout toolbarRelativeLayout;
    private RelativeLayout rootLayout;
    private ImageView downArrowImageView;
    private TextView selectOptToolbarTitle;
    private TextView readAllNotificationTextView;
    private Badge badge;
    private View toolbarUnderline;
    private ImageView menuImageView;
    private TextView langTextView;
    private FrameLayout transparentLayerToolbar;
    private FrameLayout transparentLayerNavigation;
    private RelativeLayout groupCoachmark, firstCoachmark, secondCoachmark;
    private TextView selectedlangGuideTextView;
    private MixpanelAPI mMixpanel;


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

        mMixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);

        onNewIntent(getIntent());
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (null != intent.getParcelableExtra("notificationExtras")) {
            if ("upcoming_event_list".equals(((Bundle) intent.getParcelableExtra("notificationExtras")).getString("type")))
                fragmentToLoad = Constants.BUSINESS_EVENTLIST_FRAGMENT;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        downArrowImageView = (ImageView) findViewById(R.id.downArrowImageView);
        menuImageView = (ImageView) findViewById(R.id.menuImageView);
        toolbarUnderline = findViewById(R.id.toolbarUnderline);
        bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.navigation);
        toolbarRelativeLayout = (RelativeLayout) mToolbar.findViewById(R.id.toolbarRelativeLayout);
        toolbarTitleTextView = (TextView) mToolbar.findViewById(R.id.toolbarTitle);
        searchAllImageView = (ImageView) mToolbar.findViewById(R.id.searchAllImageView);
        selectOptToolbarTitle = (TextView) findViewById(R.id.selectOptToolbarTitle);
        readAllNotificationTextView = (TextView) findViewById(R.id.readAllTextView);
        langTextView = (TextView) findViewById(R.id.langTextView);
        selectedlangGuideTextView = (TextView) findViewById(R.id.selectedlangGuideTextView);
        groupCoachmark = (RelativeLayout) findViewById(R.id.groupCoachmark);
        firstCoachmark = (RelativeLayout) findViewById(R.id.firstCoachmark);
        secondCoachmark = (RelativeLayout) findViewById(R.id.secondCoachmark);
        transparentLayerToolbar = (FrameLayout) findViewById(R.id.transparentLayerToolbar);
        transparentLayerNavigation = (FrameLayout) findViewById(R.id.transparentLayerNavigation);

        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.setTextVisibility(false);

        Utils.pushOpenScreenEvent(this, "DashboardScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        downArrowImageView.setOnClickListener(this);
        menuImageView.setOnClickListener(this);
        toolbarTitleTextView.setOnClickListener(this);
        searchAllImageView.setOnClickListener(this);
        readAllNotificationTextView.setOnClickListener(this);
        langTextView.setOnClickListener(this);
        groupCoachmark.setOnClickListener(this);
        firstCoachmark.setOnClickListener(this);
        secondCoachmark.setOnClickListener(this);

        if (AppConstants.LOCALE_ENGLISH.equals(SharedPrefUtils.getAppLocale(this))) {
            langTextView.setText(getString(R.string.language_label_english));
            selectedlangGuideTextView.setText(getString(R.string.language_label_english));
        } else if (AppConstants.LOCALE_HINDI.equals(SharedPrefUtils.getAppLocale(this))) {
            langTextView.setText(getString(R.string.language_label_hindi));
            selectedlangGuideTextView.setText(getString(R.string.language_label_hindi));
        } else if (AppConstants.LOCALE_MARATHI.equals(SharedPrefUtils.getAppLocale(this))) {
            langTextView.setText(getString(R.string.language_label_marathi));
            selectedlangGuideTextView.setText(getString(R.string.language_label_marathi));
        } else if (AppConstants.LOCALE_BENGALI.equals(SharedPrefUtils.getAppLocale(this))) {
            langTextView.setText(getString(R.string.language_label_bengali));
            selectedlangGuideTextView.setText(getString(R.string.language_label_bengali));
        } else if (AppConstants.LOCALE_TAMIL.equals(SharedPrefUtils.getAppLocale(this))) {
            langTextView.setText(getString(R.string.language_label_tamil));
            selectedlangGuideTextView.setText(getString(R.string.language_label_tamil));
        } else if (AppConstants.LOCALE_TELUGU.equals(SharedPrefUtils.getAppLocale(this))) {
            langTextView.setText(getString(R.string.language_label_telegu));
            selectedlangGuideTextView.setText(getString(R.string.language_label_telegu));
        } else {
            langTextView.setText(getString(R.string.language_label_english));
            selectedlangGuideTextView.setText(getString(R.string.language_label_english));
        }
        setSupportActionBar(mToolbar);

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
                                if (topFragment instanceof MyAccountProfileFragment) {
                                    return true;
                                }
                                MyAccountProfileFragment fragment0 = new MyAccountProfileFragment();
                                Bundle mBundle0 = new Bundle();
                                fragment0.setArguments(mBundle0);
                                addFragment(fragment0, mBundle0, true);
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
                                if (topFragment instanceof AddArticleVideoFragment) {
                                    return true;
                                }
                                AddArticleVideoFragment editorPostFragment = new AddArticleVideoFragment();
                                Bundle editorBundle = new Bundle();
                                editorPostFragment.setArguments(editorBundle);
                                addFragment(editorPostFragment, editorBundle, true);
                                break;
                            case R.id.action_location:
                                if (topFragment instanceof GroupsFragment) {
                                    return true;
                                }
                                GroupsFragment groupsFragment = new GroupsFragment();
                                Bundle eBundle = new Bundle();
                                groupsFragment.setArguments(eBundle);
                                addFragment(groupsFragment, eBundle, true);
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
        } else if (Constants.PROFILE_FRAGMENT.equals(fragmentToLoad)) {
            MyAccountProfileFragment fragment0 = new MyAccountProfileFragment();
            Bundle mBundle0 = new Bundle();
            fragment0.setArguments(mBundle0);
            addFragment(fragment0, mBundle0, true);
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
            GroupsFragment fragment1 = new GroupsFragment();
            Bundle mBundle1 = new Bundle();
            fragment1.setArguments(mBundle1);
            addFragment(fragment1, mBundle1, true);
        } else {
            replaceFragment(new FragmentMC4KHomeNew(), null, false);
            String tabType = getIntent().getStringExtra("TabType");
            if ("profile".equals(tabType)) {
                bottomNavigationView.setSelectedItemId(R.id.action_profile);
            } else {

            }
        }

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

                Intent intent1 = new Intent(DashboardActivity.this, ArticleDetailsContainerActivity.class);
                intent1.putExtra("fromNotification", true);
                intent1.putExtra(Constants.ARTICLE_ID, articleId);
                intent1.putExtra(Constants.AUTHOR_ID, authorId);
                intent1.putExtra(Constants.BLOG_SLUG, blogSlug);
                intent1.putExtra(Constants.TITLE_SLUG, titleSlug);
                intent1.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
                intent.putExtra(Constants.FROM_SCREEN, "Notification");
                intent1.putExtra(Constants.ARTICLE_INDEX, "-1");
                intent.putExtra(Constants.AUTHOR, authorId + "~");
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("shortStoryDetails")) {
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
                String articleId = notificationExtras.getString("id");
                String authorId = notificationExtras.getString("userId");
                Intent intent1 = new Intent(DashboardActivity.this, VlogsDetailActivity.class);
                intent1.putExtra("fromNotification", true);
                intent1.putExtra(Constants.VIDEO_ID, articleId);
                intent1.putExtra(Constants.AUTHOR_ID, authorId);
                intent1.putExtra(Constants.ARTICLE_OPENED_FROM, "Notification Popup");
                intent.putExtra(Constants.FROM_SCREEN, "Notification");
                intent1.putExtra(Constants.ARTICLE_INDEX, "-1");
                intent.putExtra(Constants.AUTHOR, authorId + "~");
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("group_membership")
                    || notificationExtras.getString("type").equalsIgnoreCase("group_new_post")
                    || notificationExtras.getString("type").equalsIgnoreCase("group_admin_group_edit")
                    || notificationExtras.getString("type").equalsIgnoreCase("group_admin")) {
                GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
                groupMembershipStatus.checkMembershipStatus(Integer.parseInt(notificationExtras.getString("groupId")), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
            } else if (notificationExtras.getString("type").equalsIgnoreCase("group_new_response")) {
                Intent gpPostIntent = new Intent(this, GroupPostDetailActivity.class);
                gpPostIntent.putExtra("postId", Integer.parseInt(notificationExtras.getString("postId")));
                gpPostIntent.putExtra("groupId", Integer.parseInt(notificationExtras.getString("groupId")));
                gpPostIntent.putExtra("responseId", Integer.parseInt(notificationExtras.getString("responseId")));
                startActivity(gpPostIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("group_new_reply")) {
                Intent gpPostIntent = new Intent(this, GroupPostDetailActivity.class);
                gpPostIntent.putExtra("postId", Integer.parseInt(notificationExtras.getString("postId")));
                gpPostIntent.putExtra("groupId", Integer.parseInt(notificationExtras.getString("groupId")));
                startActivity(gpPostIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("group_admin_membership")) {
                Intent memberIntent = new Intent(this, GroupMembershipActivity.class);
                memberIntent.putExtra("groupId", Integer.parseInt(notificationExtras.getString("groupId")));
                startActivity(memberIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("group_admin_reported")) {
                Intent reportIntent = new Intent(this, GroupsReportedContentActivity.class);
                reportIntent.putExtra("groupId", Integer.parseInt(notificationExtras.getString("groupId")));
                startActivity(reportIntent);
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
            } else if (notificationExtras.getString("type").equalsIgnoreCase("write_blog")) {
                launchEditor();
            } else if (notificationExtras.getString("type").equalsIgnoreCase("profile")) {
                String u_id = notificationExtras.getString("userId");
                if (!SharedPrefUtils.getUserDetailModel(this).getDynamoId().equals(u_id)) {
                    Intent intent1 = new Intent(this, BloggerProfileActivity.class);
                    intent1.putExtra("fromNotification", true);
                    intent1.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, u_id);
                    intent1.putExtra(AppConstants.AUTHOR_NAME, "");
                    intent1.putExtra(Constants.FROM_SCREEN, "Notification");
                    startActivity(intent1);
                } else {
                    fragmentToLoad = Constants.PROFILE_FRAGMENT;
                }
            } else if (notificationExtras.getString("type").equalsIgnoreCase("upcoming_event_list")) {
                fragmentToLoad = Constants.BUSINESS_EVENTLIST_FRAGMENT;
            } else if (notificationExtras.getString("type").equalsIgnoreCase("suggested_topics")) {
                fragmentToLoad = Constants.SUGGESTED_TOPICS_FRAGMENT;
            } else if (notificationExtras.getString("type").equalsIgnoreCase(AppConstants.APP_SETTINGS_DEEPLINK)) {
                Intent intent1 = new Intent(this, AppSettingsActivity.class);
                intent1.putExtra("fromNotification", true);
                intent1.putExtra("load_fragment", Constants.SETTINGS_FRAGMENT);
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("shortStoryListing")) {
                fragmentToLoad = Constants.SHORT_STOY_FRAGMENT;
            } else if (notificationExtras.getString("type").equalsIgnoreCase("group_listing")) {
                fragmentToLoad = Constants.GROUP_LISTING_FRAGMENT;
            }
        } else {
            String tempDeepLinkURL = intent.getStringExtra(AppConstants.DEEP_LINK_URL);
            if (!StringUtils.isNullOrEmpty(tempDeepLinkURL)) {
                if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_EDITOR_URL) || tempDeepLinkURL.contains(AppConstants.DEEPLINK_MOMSPRESSO_EDITOR_URL)) {
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
                                    showToast(getString(R.string.android_version_unsupported));
                                }
                            }
                        });
                    } else {
                        if (Build.VERSION.SDK_INT > 15) {
                            Utils.pushEvent(DashboardActivity.this, GTMEventType.ADD_BLOG_CLICKED_EVENT,
                                    SharedPrefUtils.getUserDetailModel(DashboardActivity.this).getDynamoId() + "", "Mobile Deep Link");
                            launchEditor();
                        } else {
                            showToast(getString(R.string.android_version_unsupported));
                        }
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
                    Intent ssIntent = new Intent(this, AddShortStoryActivity.class);
                    startActivity(intent);
                } else if (tempDeepLinkURL.contains(AppConstants.DEEPLINK_EDIT_SHORT_DRAFT_URL)) {
//                    final String draftId = tempDeepLinkURL.substring(tempDeepLinkURL.lastIndexOf("/") + 1, tempDeepLinkURL.length());
                    Intent ssIntent = new Intent(this, UserPublishedAndDraftsActivity.class);
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
                        String[] groupArray = separated[separated.length - 1].split("-");
                        long groupId = AppUtils.getIdFromHash(groupArray[groupArray.length - 1]);

                        GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
                        groupMembershipStatus.checkMembershipStatus((int) groupId, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
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
        final Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (topFragment instanceof FragmentMC4KHomeNew && SharedPrefUtils.isTopicSelectionChanged(this)) {
            ((FragmentMC4KHomeNew) topFragment).hideFollowTopicHeader();
        }
        refreshMenu();
        if (topFragment instanceof FragmentBusinesslistEvents) {

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
        if (topFragment instanceof FragmentMC4KHomeNew) {

        } else if (topFragment instanceof FragmentBusinesslistEvents) {
            getMenuInflater().inflate(R.menu.menu_event, menu);
        } else if (topFragment instanceof FragmentHomeCategory) {
            getMenuInflater().inflate(R.menu.kidsresource_listing, menu);
        }
        return true;
    }

    public void updateUnreadNotificationCount(String unreadNotifCount) {
        if (StringUtils.isNullOrEmpty(unreadNotifCount) || "0".equals(unreadNotifCount)) {
            addBadgeAt(1, "0");
        } else {
            addBadgeAt(1, unreadNotifCount);
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

            case R.id.filter:
                if (topFragment instanceof FragmentBusinesslistEvents) {
                    ((FragmentBusinesslistEvents) topFragment).toggleFilter();
                }
                break;
            case R.id.save:

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

    @Override
    public void onClick(View v) {
        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        switch (v.getId()) {
            case R.id.langTextView: {
                ChangePreferredLanguageDialogFragment changePreferredLanguageDialogFragment = new ChangePreferredLanguageDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                Bundle _args = new Bundle();
                _args.putString("activity", "dashboard");
                changePreferredLanguageDialogFragment.setArguments(_args);
                changePreferredLanguageDialogFragment.setCancelable(true);
                changePreferredLanguageDialogFragment.show(fm, "Choose video option");
            }
            break;
            case R.id.imgProfile:
                Intent intent4 = new Intent(DashboardActivity.this, BloggerProfileActivity.class);
                startActivity(intent4);
                break;
            case R.id.downArrowImageView:
            case R.id.menuImageView:
            case R.id.toolbarTitle:
                if (topFragment instanceof TopicsListingFragment) {
                    Utils.pushTopMenuClickEvent(this, "TopicArticlesListingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                    onBackPressed();
                } else if (topFragment instanceof TopicsShortStoriesContainerFragment) {
                    Utils.pushTopMenuClickEvent(this, "TopicShortStoriesListingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                    onBackPressed();
                } else {
                    Utils.pushTopMenuClickEvent(this, "HomeScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                    ExploreArticleListingTypeFragment fragment1 = new ExploreArticleListingTypeFragment();
                    Bundle mBundle1 = new Bundle();
                    fragment1.setArguments(mBundle1);
                    addFragment(fragment1, mBundle1, true);
                }

                break;
            case R.id.searchAllImageView:
                if (topFragment instanceof GroupsFragment) {
                    Intent searchIntent = new Intent(this, GroupsSearchActivity.class);
                    startActivity(searchIntent);
                } else {
                    Intent searchIntent = new Intent(this, SearchAllActivity.class);
                    searchIntent.putExtra(Constants.FILTER_NAME, "");
                    searchIntent.putExtra(Constants.TAB_POSITION, 0);
                    startActivity(searchIntent);
                }
                break;
            case R.id.readAllTextView:
                if (topFragment instanceof NotificationFragment) {
                    ((NotificationFragment) topFragment).markAllNotificationAsRead();
                    updateUnreadNotificationCount("0");
                }
                break;
            case R.id.groupCoachmark:
                groupCoachmark.setVisibility(View.GONE);
                if (!SharedPrefUtils.isCoachmarksShownFlag(this, "home")) {
                    firstCoachmark.setVisibility(View.VISIBLE);
                    secondCoachmark.setVisibility(View.GONE);
                }
                SharedPrefUtils.setGroupTourFirstLaunch(this, false);
                SharedPrefUtils.setCoachmarksShownFlag(this, "groups", true);
                break;
            case R.id.firstCoachmark:
                firstCoachmark.setVisibility(View.GONE);
                secondCoachmark.setVisibility(View.VISIBLE);
                break;
            case R.id.secondCoachmark:
                secondCoachmark.setVisibility(View.GONE);
                SharedPrefUtils.setCoachmarksShownFlag(this, "home", true);
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
            Intent intent = new Intent(DashboardActivity.this, BloggerProfileActivity.class);
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
            Intent _authorListIntent = new Intent(DashboardActivity.this, BloggerProfileActivity.class);
            _authorListIntent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, data.getAuthor_id());
            _authorListIntent.putExtra(Constants.DEEPLINK_URL, deepLinkUrl);
            _authorListIntent.putExtra(AppConstants.AUTHOR_NAME, "" + data.getAuthor_name());
            _authorListIntent.putExtra(Constants.FROM_SCREEN, "Deep Linking");
            startActivity(_authorListIntent);
        }
    }

    private void renderBloggerListingScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getBlog_title())) {
            Intent _bloggerListIntent = new Intent(DashboardActivity.this, BloggerProfileActivity.class);
            _bloggerListIntent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, data.getAuthor_id());
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
            Intent intent = new Intent(DashboardActivity.this, VlogsDetailActivity.class);
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
        selectOptToolbarTitle.setVisibility(View.GONE);
        toolbarTitleTextView.setVisibility(View.VISIBLE);
        downArrowImageView.setVisibility(View.INVISIBLE);
        menuImageView.setVisibility(View.GONE);
        readAllNotificationTextView.setVisibility(View.GONE);
        langTextView.setVisibility(View.GONE);
        if (null != topFragment && topFragment instanceof ExploreArticleListingTypeFragment) {
            String fragType = "";
            if (topFragment.getArguments() != null) {
                fragType = topFragment.getArguments().getString("fragType", "");
            }
            mToolbar.setVisibility(View.VISIBLE);
            toolbarUnderline.setVisibility(View.VISIBLE);
            toolbarTitleTextView.setOnClickListener(null);
            toolbarTitleTextView.setVisibility(View.GONE);
            downArrowImageView.setVisibility(View.GONE);
            toolbarRelativeLayout.setVisibility(View.VISIBLE);
            selectOptToolbarTitle.setVisibility(View.VISIBLE);
            menu.findItem(R.id.action_home).setChecked(true);
            if (fragType.equals("search")) {
                selectOptToolbarTitle.setText(getString(R.string.search_topics_toolbar_title));
            } else {
                Utils.pushOpenScreenEvent(this, "TopicScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                selectOptToolbarTitle.setText(getString(R.string.home_screen_select_an_option_title));
                if (!SharedPrefUtils.isCoachmarksShownFlag(this, "topics")) {
                    showToolbarAndNavigationLayer();
                    ((ExploreArticleListingTypeFragment) topFragment).showGuideView();
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else if (null != topFragment && topFragment instanceof BecomeBloggerFragment) {
            mToolbar.setVisibility(View.VISIBLE);
            toolbarUnderline.setVisibility(View.VISIBLE);
            toolbarTitleTextView.setOnClickListener(null);
            toolbarTitleTextView.setText(getString(R.string.home_screen_trending_become_blogger));
            menu.findItem(R.id.action_write).setChecked(true);
            toolbarRelativeLayout.setVisibility(View.VISIBLE);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else if (null != topFragment && topFragment instanceof UploadVideoInfoFragment) {
            mToolbar.setVisibility(View.VISIBLE);
            toolbarUnderline.setVisibility(View.VISIBLE);
            toolbarTitleTextView.setOnClickListener(null);
            toolbarTitleTextView.setText(getString(R.string.home_screen_trending_first_video_upload));
            menu.findItem(R.id.action_write).setChecked(true);
            toolbarRelativeLayout.setVisibility(View.VISIBLE);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            mToolbar.setVisibility(View.VISIBLE);
            toolbarUnderline.setVisibility(View.VISIBLE);
            toolbarTitleTextView.setOnClickListener(null);
            if (null != topFragment && topFragment instanceof MyAccountProfileFragment) {
                Utils.pushOpenScreenEvent(this, "PrivateProfileScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                toolbarTitleTextView.setText(getString(R.string.home_screen_profile_title));
                toolbarTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.myprofile_toolbar_title));
                menu.findItem(R.id.action_profile).setChecked(true);
                toolbarRelativeLayout.setVisibility(View.VISIBLE);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else if (null != topFragment && topFragment instanceof NotificationFragment) {
                Utils.pushOpenScreenEvent(this, "NotificationsScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                toolbarTitleTextView.setText(getString(R.string.home_screen_notification_title));
                toolbarTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.notification_toolbar_title));
                searchAllImageView.setVisibility(View.GONE);
                readAllNotificationTextView.setVisibility(View.VISIBLE);
                menu.findItem(R.id.action_notification).setChecked(true);
                toolbarRelativeLayout.setVisibility(View.VISIBLE);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else if (null != topFragment && topFragment instanceof SuggestedTopicsFragment) {
                Utils.pushOpenScreenEvent(this, "SuggestedTopicScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                toolbarTitleTextView.setText(getString(R.string.home_screen_suggested_topic_title));
                toolbarTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.notification_toolbar_title));
                menu.findItem(R.id.action_write).setChecked(true);
                toolbarRelativeLayout.setVisibility(View.VISIBLE);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else if (null != topFragment && topFragment instanceof FragmentMC4KHomeNew) {
                Utils.pushOpenScreenEvent(this, "HomeScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                if (!SharedPrefUtils.isCoachmarksShownFlag(this, "groups")) {
                    groupCoachmark.setVisibility(View.VISIBLE);
                }
                if (SharedPrefUtils.isTopicSelectionChanged(this)) {
                    ((FragmentMC4KHomeNew) topFragment).hideFollowTopicHeader();
                }
                langTextView.setVisibility(View.VISIBLE);
                toolbarTitleTextView.setOnClickListener(this);
                toolbarTitleTextView.setText(getString(R.string.home_screen_trending_title));
                toolbarTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.home_toolbar_titlecolor));
                menu.findItem(R.id.action_home).setChecked(true);
                toolbarRelativeLayout.setVisibility(View.VISIBLE);
                menuImageView.setVisibility(View.VISIBLE);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else if (null != topFragment && topFragment instanceof AddArticleVideoFragment) {
                menu.findItem(R.id.action_write).setChecked(true);
                mToolbar.setVisibility(View.GONE);
                toolbarUnderline.setVisibility(View.GONE);
            } else if (null != topFragment && topFragment instanceof TopicsListingFragment) {
                Utils.pushOpenScreenEvent(this, "TopicArticlesListingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                if (!SharedPrefUtils.isCoachmarksShownFlag(this, "topics_article")) {
                    ((TopicsListingFragment) topFragment).showGuideView();
                }
                toolbarTitleTextView.setOnClickListener(this);
                toolbarTitleTextView.setText(mToolbarTitle);
                toolbarTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.home_toolbar_titlecolor));
                menu.findItem(R.id.action_home).setChecked(true);
                toolbarRelativeLayout.setVisibility(View.VISIBLE);
                menuImageView.setVisibility(View.VISIBLE);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else if (null != topFragment && topFragment instanceof ExploreFragment) {
                Utils.pushOpenScreenEvent(this, "ExploreScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                toolbarTitleTextView.setText(getString(R.string.home_screen_explore_title));
                toolbarTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.notification_toolbar_title));
                menu.findItem(R.id.action_home).setChecked(true);
                toolbarRelativeLayout.setVisibility(View.VISIBLE);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else if (null != topFragment && topFragment instanceof TopicsShortStoriesContainerFragment) {
                Utils.pushOpenScreenEvent(this, "TopicsShortStoriesContainerFragment", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                toolbarTitleTextView.setOnClickListener(this);
                toolbarTitleTextView.setText(getString(R.string.article_listing_type_short_story_label));
                toolbarTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.home_toolbar_titlecolor));
                menu.findItem(R.id.action_home).setChecked(true);
                toolbarRelativeLayout.setVisibility(View.VISIBLE);
                menuImageView.setVisibility(View.VISIBLE);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else if (null != topFragment && topFragment instanceof GroupsFragment) {
                Utils.pushOpenScreenEvent(this, "GroupsFragment", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                toolbarTitleTextView.setText(getString(R.string.groups_support_groups));
                toolbarTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.groups_light_black_color));
                menu.findItem(R.id.action_location).setChecked(true);
                toolbarRelativeLayout.setVisibility(View.VISIBLE);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else if (null != topFragment && topFragment instanceof FragmentBusinesslistEvents) {
                Utils.pushOpenScreenEvent(this, "EventsListingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                toolbarTitleTextView.setText(getString(R.string.home_screen_upcoming_events_title));
                toolbarTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.notification_toolbar_title));
                menu.findItem(R.id.action_location).setChecked(true);
                toolbarRelativeLayout.setVisibility(View.VISIBLE);
                searchAllImageView.setVisibility(View.GONE);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else if (null != topFragment && topFragment instanceof FragmentHomeCategory) {
                Utils.pushOpenScreenEvent(this, "ResourceListingScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
                toolbarTitleTextView.setText(getString(R.string.home_screen_kids_res_title));
                toolbarTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.notification_toolbar_title));
                menu.findItem(R.id.action_location).setChecked(true);
                toolbarRelativeLayout.setVisibility(View.VISIBLE);
                searchAllImageView.setVisibility(View.GONE);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
            badge.setBadgeText(number);
            if (number.equals("0")) {
                badge.hide(false);
            }
            return badge;
        }
        // add badge
        badge = new QBadgeView(this)
                .setBadgeText(number)
                .setGravityOffset(16, 2, true)
                .bindTarget(bottomNavigationView.getBottomNavigationItemView(position));
        if (number.equals("0")) {
            badge.hide(false);
        }
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
}