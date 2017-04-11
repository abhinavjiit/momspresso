package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.Settings;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.joanzapata.iconify.widget.IconButton;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.ColorCode;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.CompleteTaskController;
import com.mycity4kids.controller.DeleteTaskController;
import com.mycity4kids.controller.TaskListController;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TableTaskList;
import com.mycity4kids.dbtable.TaskCompletedTable;
import com.mycity4kids.dbtable.TaskTableAttendee;
import com.mycity4kids.dbtable.TaskTableFile;
import com.mycity4kids.dbtable.TaskTableNotes;
import com.mycity4kids.dbtable.TaskTableWhoToRemind;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.facebook.FacebookUtils;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.response.DeepLinkingResposnse;
import com.mycity4kids.models.response.DeepLinkingResult;
import com.mycity4kids.models.version.RateVersion;
import com.mycity4kids.newmodels.CompleteTaskRequestModel;
import com.mycity4kids.newmodels.DeleteTaskModel;
import com.mycity4kids.newmodels.ExternalEventModel;
import com.mycity4kids.newmodels.TaskListModel;
import com.mycity4kids.newmodels.TaskListResponse;
import com.mycity4kids.newmodels.TaskResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.reminders.Reminder;
import com.mycity4kids.retrofitAPIsInterfaces.DeepLinkingAPI;
import com.mycity4kids.ui.adapter.UserTaskListAdapter;
import com.mycity4kids.ui.fragment.AddTaskListPopUp;
import com.mycity4kids.ui.fragment.ArticlesFragment;
import com.mycity4kids.ui.fragment.ChangeCityFragment;
import com.mycity4kids.ui.fragment.ChooseVideoUploadOptionDialogFragment;
import com.mycity4kids.ui.fragment.ExternalCalFragment;
import com.mycity4kids.ui.fragment.FragmentAdultProfile;
import com.mycity4kids.ui.fragment.FragmentBusinesslistEvents;
import com.mycity4kids.ui.fragment.FragmentCalMonth;
import com.mycity4kids.ui.fragment.FragmentCalender;
import com.mycity4kids.ui.fragment.FragmentCityForKids;
import com.mycity4kids.ui.fragment.FragmentEditorsPick;
import com.mycity4kids.ui.fragment.FragmentFamilyDetail;
import com.mycity4kids.ui.fragment.FragmentFamilyProfile;
import com.mycity4kids.ui.fragment.FragmentHomeCategory;
import com.mycity4kids.ui.fragment.FragmentKidProfile;
import com.mycity4kids.ui.fragment.FragmentMC4KHome;
import com.mycity4kids.ui.fragment.FragmentSetting;
import com.mycity4kids.ui.fragment.FragmentTaskHome;
import com.mycity4kids.ui.fragment.NotificationFragment;
import com.mycity4kids.ui.fragment.RateAppDialogFragment;
import com.mycity4kids.ui.fragment.SendFeedbackFragment;
import com.mycity4kids.ui.fragment.SyncSettingFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.RoundedTransformation;
import com.mycity4kids.widget.CustomListView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import life.knowledge4.videotrimmer.utils.FileUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class DashboardActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_GALLERY_PERMISSION = 2;

    private static String[] PERMISSIONS_STORAGE_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private View mDrawerView;
    private CharSequence mTitle;
    private boolean isPopupOpen;
    TextView year;
    Calendar currentdate;
    int tempyear;
    SimpleDateFormat form;
    private TextView mUsernName;
    private ImageView downArrow;
    public boolean filter = false;
    public String selected_colorcode = "";
    private ImageView profileImage;
    private UserTaskListAdapter taskListAdapter;
    CustomListView allTaskList;
    int taskListID = 0;
    Boolean taskIconFlag = false;
    private TextView toolBarTitleView;
    private boolean editTaskList;
    private String editTaskListname = "";
    private TextView txvAllTaskPopup;
    ArrayList<Integer> taskIdlist;
    ArrayList<Integer> deletedTasksList;
    private String oldListName = "";
    private boolean frmPush = false;
    private int taskId = 0;
    private TableTaskData taskData;
    private int blogListPosition;
    Tracker t;
    private String deepLinkUrl;
    String fragmentToLoad = "";
    private int hot_number = 2;
    private TextView ui_hot = null;
    private TextView itemMessagesBadgeTextView;
    private FrameLayout badgeLayout;

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
                intent1.putExtra(Constants.ARTICLE_ID, articleId);
                intent1.putExtra(Constants.AUTHOR_ID, authorId);
                intent1.putExtra(Constants.BLOG_SLUG, blogSlug);
                intent1.putExtra(Constants.TITLE_SLUG, titleSlug);
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("video_details")) {
                String articleId = notificationExtras.getString("id");
                String authorId = notificationExtras.getString("userId");
                Intent intent1 = new Intent(DashboardActivity.this, VlogsDetailActivity.class);
                intent1.putExtra(Constants.VIDEO_ID, articleId);
                intent1.putExtra(Constants.AUTHOR_ID, authorId);
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("event_details")) {
                String eventId = notificationExtras.getString("id");
                Intent resultIntent = new Intent(getApplicationContext(), BusinessDetailsActivity.class);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resultIntent.putExtra(Constants.CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getApplication()));
                resultIntent.putExtra(Constants.BUSINESS_OR_EVENT_ID, eventId + "");
                resultIntent.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                resultIntent.putExtra(Constants.DISTANCE, "0");
                startActivity(resultIntent);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("webView")) {
                String url = notificationExtras.getString("url");
                Intent intent1 = new Intent(this, LoadWebViewActivity.class);
                intent1.putExtra(Constants.WEB_VIEW_URL, url);
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("profile")) {
                String u_id = notificationExtras.getString("userId");
                Intent intent1 = new Intent(this, BloggerDashboardActivity.class);
                intent1.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, u_id);
                startActivity(intent1);
            } else if (notificationExtras.getString("type").equalsIgnoreCase("upcoming_event_list")) {
                fragmentToLoad = Constants.BUSINESS_EVENTLIST_FRAGMENT;
//                intent.getExtras().putString(Constants.LOAD_FRAGMENT, Constants.BUSINESS_EVENTLIST_FRAGMENT);
            } else if (notificationExtras.getString("type").equalsIgnoreCase(AppConstants.APP_SETTINGS_DEEPLINK)) {
                Intent intent1 = new Intent(this, SettingsActivity.class);
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

        taskData = new TableTaskData(BaseApplication.getInstance());

        form = new SimpleDateFormat("MMM yyyy", Locale.US);

        mUsernName = (TextView) findViewById(R.id.txvUserName);
        mDrawerView = (View) findViewById(R.id.left_drawerview);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        year = (TextView) findViewById(R.id.year);
        downArrow = (ImageView) findViewById(R.id.downarrow);
        profileImage = (ImageView) findViewById(R.id.imgProfile);
        allTaskList = (CustomListView) findViewById(R.id.show_tasklist);

        txvAllTaskPopup = (TextView) findViewById(R.id.all_tasklist);

        Utils.pushOpenScreenEvent(DashboardActivity.this, "DashBoard", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        // onclick events
        findViewById(R.id.rdBtnToday).setOnClickListener(this);
        findViewById(R.id.rdBtnUpcoming).setOnClickListener(this);
        findViewById(R.id.feed_back).setOnClickListener(this);
        findViewById(R.id.addVideosTextView).setOnClickListener(this);
        findViewById(R.id.myVideosTextView).setOnClickListener(this);

        setSupportActionBar(mToolbar);

        taskListAdapter = new UserTaskListAdapter(this, getTaskList(), false);
        allTaskList.setAdapter(taskListAdapter);

        currentdate = Calendar.getInstance();
        tempyear = currentdate.get(Calendar.YEAR);

        year.setText(String.valueOf(tempyear));
        mUsernName.setText(SharedPrefUtils.getUserDetailModel(this).getFirst_name() + " " + SharedPrefUtils.getUserDetailModel(this).getLast_name());
        // setting profile image

        updateImageProfile();


        if (Constants.BUSINESS_EVENTLIST_FRAGMENT.equals(fragmentToLoad)) {
            setTitle("Upcoming Events");
            FragmentBusinesslistEvents fragment = new FragmentBusinesslistEvents();
            Bundle mBundle = new Bundle();
            mBundle.putInt(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
            mBundle.putInt(Constants.EXTRA_CATEGORY_ID, SharedPrefUtils.getEventIdForCity(DashboardActivity.this));
            mBundle.putString(Constants.CATEGOTY_NAME, "Events & workshop");
            fragment.setArguments(mBundle);
            replaceFragment(fragment, mBundle, true);
        } else if (Constants.TODOLIST_FRAGMENT.equals(fragmentToLoad)) {
            if (StringUtils.isNullOrEmpty("" + SharedPrefUtils.getUserDetailModel(this).getFamily_id()) ||
                    SharedPrefUtils.getUserDetailModel(this).getFamily_id() == 0) {
                showCreateFamilyAlert();
            } else {
                setTitle("Weekly To-Do");
                replaceFragment(new FragmentTaskHome(), null, true);
            }
        } else if (Constants.CALENDARLIST_FRAGMENT.equals(fragmentToLoad)) {
            if (StringUtils.isNullOrEmpty("" + SharedPrefUtils.getUserDetailModel(this).getFamily_id()) ||
                    SharedPrefUtils.getUserDetailModel(this).getFamily_id() == 0) {
                showCreateFamilyAlert();
            } else {
                setTitle("Weekly Calender");
                replaceFragment(new FragmentCalender(), null, true);
            }
        } else if (Constants.SETTINGS_FRAGMENT.equals(fragmentToLoad)) {
            changeVisibiltyOfArrow(false);
            setTitle("Settings");
            Bundle bundle = new Bundle();
            bundle.putString("bio", getIntent().getStringExtra("bio"));
            bundle.putString("firstName", getIntent().getStringExtra("firstName"));
            bundle.putString("lastName", getIntent().getStringExtra("lastName"));
            replaceFragment(new FragmentSetting(), bundle, true);
        } else {
            replaceFragment(new FragmentMC4KHome(), null, false);
        }

        if (extras != null) {
            boolean push = extras.getBoolean("push");
            taskId = extras.getInt(AppConstants.EXTRA_TASK_ID);
            String isrecurring = extras.getString(AppConstants.IS_RECURRING);
            NotificationManager nMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel(extras.getInt(AppConstants.NOTIFICATION_ID, 0));
            nMgr.cancel(extras.getInt(AppConstants.NOTIFICATION_ID, 2));
            if (push)
                replaceFragment(new FragmentFamilyDetail(), null, true);

            else if (taskId > 0) {
                // delete taskid
                // check recurring
                if (!StringUtils.isNullOrEmpty(isrecurring)) {
                    if (isrecurring.equalsIgnoreCase("yes")) {

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String formattedDate = df.format(c.getTime());

                        TaskCompletedTable completedTable = new TaskCompletedTable(BaseApplication.getInstance());
                        completedTable.AddTasks(formattedDate, taskId);

                        hitApiRequest(AppConstants.TASKS_COMPLETE_REQUEST);

                    } else {
                        // delete
                        frmPush = true;
                        DeleteTaskModel taskModel = new DeleteTaskModel();

                        ArrayList<DeleteTaskModel.Tasks> tasksArrayList = new ArrayList<>();

                        DeleteTaskModel.Tasks model = new DeleteTaskModel().new Tasks();
                        model.setId(taskId);
                        tasksArrayList.add(model);
                        taskModel.setTasks(tasksArrayList);

                        showProgressDialog(getString(R.string.please_wait));

                        DeleteTaskController _controller = new DeleteTaskController(this, this);
                        _controller.getData(AppConstants.DELETE_TASK_REQUEST, taskModel);

                    }

                }

            }
        }

        //  title all tasks popup
        txvAllTaskPopup.setText("All Tasks (" + taskData.getRowsCount() + ")");


        if (SharedPrefUtils.isCityFetched(this) && SharedPrefUtils.getCurrentCityModel(this).getId() != AppConstants.OTHERS_CITY_ID) {
            findViewById(R.id.rdBtnUpcoming).setVisibility(View.VISIBLE);
            findViewById(R.id.rdBtnKids).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.rdBtnUpcoming).setVisibility(View.GONE);
            findViewById(R.id.rdBtnKids).setVisibility(View.GONE);
        }

        findViewById(R.id.rdBtnKids).setOnClickListener(this);
        findViewById(R.id.rdBtnParentingBlogs).setOnClickListener(this);
        findViewById(R.id.rdBtnMomspressoVideo).setOnClickListener(this);
        findViewById(R.id.rdBtnHindi).setOnClickListener(this);
        findViewById(R.id.rdBtnBangla).setOnClickListener(this);
        findViewById(R.id.rdBtnMarathi).setOnClickListener(this);
        findViewById(R.id.editor).setOnClickListener(this);
        findViewById(R.id.imgProfile).setOnClickListener(this);
        findViewById(R.id.txvUserName).setOnClickListener(this);
        findViewById(R.id.back_month).setOnClickListener(this);
        findViewById(R.id.next_month).setOnClickListener(this);
        findViewById(R.id.all_tasklist).setOnClickListener(this);
        findViewById(R.id.jan).setOnClickListener(this);
        findViewById(R.id.feb).setOnClickListener(this);
        findViewById(R.id.mar).setOnClickListener(this);
        findViewById(R.id.apr).setOnClickListener(this);
        findViewById(R.id.may).setOnClickListener(this);
        findViewById(R.id.june).setOnClickListener(this);
        findViewById(R.id.july).setOnClickListener(this);
        findViewById(R.id.aug).setOnClickListener(this);
        findViewById(R.id.sept).setOnClickListener(this);
        findViewById(R.id.oct).setOnClickListener(this);
        findViewById(R.id.nov).setOnClickListener(this);
        findViewById(R.id.dec).setOnClickListener(this);
        findViewById(R.id.add_tasklist).setOnClickListener(this);
        findViewById(R.id.downarrow).setOnClickListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.black_color));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.blank, R.string.blank) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
                syncState();
            }

            public void onDrawerOpened(View drawerView) {
                // getActionBar().setTitle(mTitle);
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

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


        // manage fragment change

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

                if (currentFrag instanceof FragmentCityForKids) {
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    changeVisibiltyOfArrow(false);
                    setTitle("");
                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                } else if (currentFrag instanceof FragmentMC4KHome) {
                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    changeVisibiltyOfArrow(false);
                    setTitle("");
                } else if (currentFrag instanceof FragmentCalender) {
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    changeVisibiltyOfArrow(true);
                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
                } else if (currentFrag instanceof FragmentCalMonth) {
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
                    changeVisibiltyOfArrow(true);
                } else if (currentFrag instanceof FragmentSetting) {
                    changeVisibiltyOfArrow(false);
                    setTitle("Settings");
                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                } else if (currentFrag instanceof FragmentBusinesslistEvents) {
                    changeVisibiltyOfArrow(false);
                    setTitle("Upcoming Events");
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                } else if (currentFrag instanceof FragmentHomeCategory) {
                    changeVisibiltyOfArrow(false);
                    setTitle("Kids Resources");
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                } else if (currentFrag instanceof FragmentFamilyDetail) {
                    setTitle("Family Details");
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
                    changeVisibiltyOfArrow(false);
                } else if (currentFrag instanceof FragmentFamilyProfile) {
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    changeVisibiltyOfArrow(false);
                } else if (currentFrag instanceof FragmentAdultProfile) {
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
                    changeVisibiltyOfArrow(false);
                } else if (currentFrag instanceof FragmentKidProfile) {
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
                    changeVisibiltyOfArrow(false);
                } else if (currentFrag instanceof FragmentTaskHome) {
                    changeVisibiltyOfArrow(true);
                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    setTitle("All Tasks");
                } else if (currentFrag instanceof ArticlesFragment) {
                    changeVisibiltyOfArrow(false);
                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    setTitle("Blogs");
                } else if (currentFrag instanceof FragmentEditorsPick) {
                    changeVisibiltyOfArrow(false);
                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    if (SharedPrefUtils.getCurrentCityModel(DashboardActivity.this).getName().isEmpty()) {
                        switch (SharedPrefUtils.getCurrentCityModel(DashboardActivity.this).getId()) {
                            case 1:
                                setTitle("Best of " + "Delhi-NCR");
                                break;
                            case 2:
                                setTitle("Best of " + "Bangalore");
                                break;
                            case 3:
                                setTitle("Best of " + "Mumbai");
                                break;
                            case 4:
                                setTitle("Best of " + "Pune");
                                break;
                            case 5:
                                setTitle("Best of " + "Hyderabad");
                                break;
                            case 6:
                                setTitle("Best of " + "Chennai");
                                break;
                            case 7:
                                setTitle("Best of " + "Kolkata");
                                break;
                            case 8:
                                setTitle("Best of " + "Jaipur");
                                break;
                            case 9:
                                setTitle("Best of " + "Ahmedabad");
                                break;
                            default:
                                setTitle("Best of " + "Delhi-NCR");
                                break;
                        }

                    } else {
                        if (SharedPrefUtils.getCurrentCityModel(DashboardActivity.this).getName().equals("Delhi-Ncr")) {
                            SharedPrefUtils.getCurrentCityModel(DashboardActivity.this).setName("Delhi-NCR");
                        }
                        setTitle("Best of " + SharedPrefUtils.getCurrentCityModel(DashboardActivity.this).getName());
                    }
                } else if (currentFrag instanceof SendFeedbackFragment) {
                    changeVisibiltyOfArrow(false);
                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    setTitle("Send Feedback");
                } else if (currentFrag instanceof NotificationFragment) {
                    setTitle("Notifications");
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
                    changeVisibiltyOfArrow(false);
                } else if (currentFrag instanceof ChangeCityFragment) {
                    setTitle("Change City");
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
                    changeVisibiltyOfArrow(false);
                } else if (currentFrag instanceof ExternalCalFragment) {
                    setTitle("External Calendars");
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
                    changeVisibiltyOfArrow(false);
                } else if (currentFrag instanceof SyncSettingFragment) {
                    setTitle("Sync Settings");
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
                    changeVisibiltyOfArrow(false);
                }
                invalidateOptionsMenu();
                mDrawerToggle.syncState();

            }
        });

        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case -1:
                        getSupportFragmentManager().popBackStack();
                        break;

                    default:
                        break;
                }
            }
        });

        refreshMonthPopup();

        // Toolbar title listener

        Field titleField = null;
        try {
            titleField = Toolbar.class.getDeclaredField("mTitleTextView");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        titleField.setAccessible(true);
        try {
            toolBarTitleView = (TextView) titleField.get(mToolbar);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        toolBarTitleView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                if (topFragment instanceof FragmentCalender) {
                    if (findViewById(R.id.month_popup).getVisibility() == View.VISIBLE) {
                        isPopupOpen = false;
                        findViewById(R.id.month_popup).setVisibility(View.GONE);

                    } else {
                        isPopupOpen = true;
                        findViewById(R.id.month_popup).setVisibility(View.VISIBLE);

                    }

                } else if (topFragment instanceof FragmentCalMonth) {

                    if (findViewById(R.id.month_popup).getVisibility() == View.VISIBLE) {
                        isPopupOpen = false;
                        findViewById(R.id.month_popup).setVisibility(View.GONE);
                    } else {
                        isPopupOpen = true;
                        findViewById(R.id.month_popup).setVisibility(View.VISIBLE);
                    }

                    refreshMenu();

                } else if (topFragment instanceof FragmentTaskHome) {

                    findViewById(R.id.month_popup).setVisibility(View.GONE);

                    if (findViewById(R.id.task_popup).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.task_popup).setVisibility(View.GONE);
                    } else {
                        findViewById(R.id.task_popup).setVisibility(View.VISIBLE);
                    }
                }

            }
        });

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

        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (topFragment instanceof FragmentCalender)
            ((FragmentCalender) topFragment).hideFilter();

        if (topFragment instanceof FragmentCalMonth)
            ((FragmentCalMonth) topFragment).hideFilter();
        return ret;
    }

    public void updateImageProfile() {
        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(this))) {
            Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)
                    .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(profileImage);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPrefUtils.setTaskListID(DashboardActivity.this, 0);
        System.out.println("dashboard destroy");
    }

    public void changeVisibiltyOfArrow(boolean result) {
        if (result)
            downArrow.setVisibility(View.VISIBLE);
        else
            downArrow.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (showUploadVideoTutorial()) return;
        mUsernName.setText(SharedPrefUtils.getUserDetailModel(this).getFirst_name() + " " + SharedPrefUtils.getUserDetailModel(this).getLast_name());
        updateImageProfile();
        final Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (findViewById(R.id.month_popup) != null) {
            findViewById(R.id.month_popup).setVisibility(View.GONE);
        }
        if (findViewById(R.id.task_popup) != null) {
            findViewById(R.id.task_popup).setVisibility(View.GONE);
        }
        refreshMenu();
        if (topFragment instanceof FragmentCalender) {
            ((FragmentCalender) topFragment).refreshView();
        } else if (topFragment instanceof FragmentCalMonth) {
            try {
                ((FragmentCalMonth) topFragment).refreshCalender_afterAdd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (topFragment instanceof FragmentMC4KHome) {
            try {
                ((FragmentMC4KHome) topFragment).refreshList();
                if (SharedPrefUtils.isCityFetched(this) && SharedPrefUtils.getCurrentCityModel(this).getId() != AppConstants.OTHERS_CITY_ID) {
                    findViewById(R.id.rdBtnUpcoming).setVisibility(View.VISIBLE);
                    findViewById(R.id.rdBtnKids).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.rdBtnUpcoming).setVisibility(View.GONE);
                    findViewById(R.id.rdBtnKids).setVisibility(View.GONE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (topFragment instanceof FragmentBusinesslistEvents) {

            try {
                ((FragmentBusinesslistEvents) topFragment).refreshList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (topFragment instanceof FragmentTaskHome) {

            // set title header

            TableTaskData taskData = new TableTaskData(BaseApplication.getInstance());
            txvAllTaskPopup.setText("All Tasks (" + taskData.getRowsCount() + ")");

            // get share prefrence list
            int listid = SharedPrefUtils.getTaskListID(this);
            if (listid == 0) {
                taskIconFlag = false;
                setTitle("All Tasks");
                ((FragmentTaskHome) topFragment).NotifyTaskByListId(false, 0);
            } else {
                // get name from db according to list

                taskIconFlag = true;
                setTitle(new TableTaskList(BaseApplication.getInstance()).getListName(listid));
                ((FragmentTaskHome) topFragment).NotifyTaskByListId(true, listid);
            }

            notiftTaskList();
            refreshMenu();
        } else if (topFragment instanceof SendFeedbackFragment) {
            refreshMenu();
            setTitle("Send Feedback");
        } else if (topFragment instanceof ArticlesFragment) {
            Log.d("Articles Frag ", "REFRESH TIME");
            ((ArticlesFragment) topFragment).refreshBlogList();
        }

        allTaskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment topFragment1 = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                int listid = ((TaskListModel) taskListAdapter.getItem(i)).getId();
                oldListName = ((TaskListModel) taskListAdapter.getItem(i)).getList_name();


                if (topFragment1 instanceof FragmentTaskHome) {
                    taskListID = ((TaskListModel) taskListAdapter.getItem(i)).getId();
                    taskIconFlag = true;

                    SharedPrefUtils.setTaskListID(DashboardActivity.this, listid);

                    ((FragmentTaskHome) topFragment1).NotifyTaskByListId(true, listid);
                    setTitle(((TaskListModel) taskListAdapter.getItem(i)).getList_name());
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    refreshMenu();

                }
            }
        });

        TableAppointmentData data = new TableAppointmentData(BaseApplication.getInstance());
        TableTaskData taskData = new TableTaskData(BaseApplication.getInstance());
        int count = data.getRowsCount() + taskData.getRowsCount();
        if (count > 0) {
            SharedPrefUtils.setHomeCheckFlag(this, true);
        } else {
            SharedPrefUtils.setHomeCheckFlag(this, false);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mGcmUpdate);
    }

    public void refreshMenu() {
        //findViewById(R.id.month_popup).setVisibility(View.GONE);
        invalidateOptionsMenu();
        mDrawerToggle.syncState();
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
                    Log.d("NNNNNN------", "PPPPPPPPPP");
                    Intent wintent = new Intent(getApplicationContext(), NotificationCenterListActivity.class);
                    startActivity(wintent);
                }
            });
        } else if (topFragment instanceof FragmentCityForKids) {

        } else if (topFragment instanceof FragmentCalender) {
            if (filter) {
                if (StringUtils.isNullOrEmpty(selected_colorcode)) {
                    getMenuInflater().inflate(R.menu.menu, menu);
                } else {
                    menu.clear();
                    MenuInflater inflater = getMenuInflater();
                    inflater.inflate(R.menu.menu, menu);
                    MenuItem item = menu.findItem(R.id.filter);

                    String key = new ColorCode().getKey(selected_colorcode);
                    Drawable drawable = getResources().getDrawable(getResources()
                            .getIdentifier("filter_" + key + "xxhdpi", "drawable", getPackageName()));
                    item.setIcon(drawable);
                }
            } else {
                getMenuInflater().inflate(R.menu.menu, menu);
            }
        } else if (topFragment instanceof FragmentCalMonth) {
            if (filter) {
                if (StringUtils.isNullOrEmpty(selected_colorcode)) {
                    getMenuInflater().inflate(R.menu.menu_change, menu);
                } else {
                    menu.clear();
                    MenuInflater inflater = getMenuInflater();
                    inflater.inflate(R.menu.menu_change, menu);
                    MenuItem item = menu.findItem(R.id.filter);
                    String key = new ColorCode().getKey(selected_colorcode);
                    Drawable drawable = getResources().getDrawable(getResources()
                            .getIdentifier("filter_" + key + "xxhdpi", "drawable", getPackageName()));
                    item.setIcon(drawable);

                }
            } else {
                getMenuInflater().inflate(R.menu.menu_change, menu);
            }
            //getMenuInflater().inflate(R.menu.menu_change, menu);
        } else if (topFragment instanceof FragmentFamilyDetail) {

            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof FragmentFamilyProfile) {

            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof FragmentAdultProfile) {

            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof FragmentKidProfile) {

            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof ChangeCityFragment) {
            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof FragmentTaskHome) {

            if (taskIconFlag) {
                getMenuInflater().inflate(R.menu.task_home_delete, menu);
            } else {
                getMenuInflater().inflate(R.menu.task_home, menu);
            }
        } else if (topFragment instanceof FragmentBusinesslistEvents) {
            getMenuInflater().inflate(R.menu.menu_event, menu);
        } else if (topFragment instanceof ArticlesFragment) {
            getMenuInflater().inflate(R.menu.menu_articles, menu);
        } else if (topFragment instanceof NotificationFragment) {
            getMenuInflater().inflate(R.menu.forgot_password, menu);
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

    private void onHotlistSelected() {
        Log.d("vfvfvfvvfvfvfv", "dwadawd");
    }

    // call the updating code on the main thread,
// so we can call this asynchronously
    public void updateHotCount(final int new_hot_number) {
        hot_number = new_hot_number;
        if (ui_hot == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (new_hot_number == 0)
                    ui_hot.setVisibility(View.INVISIBLE);
                else {
                    ui_hot.setVisibility(View.VISIBLE);
                    ui_hot.setText(Integer.toString(new_hot_number));
                }
            }
        });
    }


    static abstract class MyMenuItemStuffListener implements View.OnClickListener, View.OnLongClickListener {
        private String hint;
        private View view;

        MyMenuItemStuffListener(View view, String hint) {
            this.view = view;
            this.hint = hint;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        abstract public void onClick(View v);

        @Override
        public boolean onLongClick(View v) {
            final int[] screenPos = new int[2];
            final Rect displayFrame = new Rect();
            view.getLocationOnScreen(screenPos);
            view.getWindowVisibleDisplayFrame(displayFrame);
            final Context context = view.getContext();
            final int width = view.getWidth();
            final int height = view.getHeight();
            final int midy = screenPos[1] + height / 2;
            final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            Toast cheatSheet = Toast.makeText(context, hint, Toast.LENGTH_SHORT);
            if (midy < displayFrame.height()) {
                cheatSheet.setGravity(Gravity.TOP | Gravity.RIGHT,
                        screenWidth - screenPos[0] - width / 2, height);
            } else {
                cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
            }
            cheatSheet.show();
            return true;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (getSupportActionBar() != null) {
            if (StringUtils.isEmpty(mTitle)) {
                getSupportActionBar().setIcon(R.drawable.myicon);
            } else {
                getSupportActionBar().setIcon(null);
            }
            getSupportActionBar().setTitle(mTitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        switch (item.getItemId()) {

            case R.id.today:
//                findViewById(R.id.month_popup).setVisibility(View.GONE);
                if (topFragment instanceof FragmentCalender) {
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    ((FragmentCalender) topFragment).showTodayIcon(this);
                } else if (topFragment instanceof FragmentCalMonth) {
                    ((FragmentCalMonth) topFragment).setTodayCalenderView();
                    refreshMenu();
//                    findViewById(R.id.month_popup).setVisibility(View.VISIBLE);
                }

                break;
            case R.id.calender:
                findViewById(R.id.month_popup).setVisibility(View.GONE);
                refreshMenu();
                if (topFragment instanceof FragmentCalender) {
                    ((FragmentCalender) topFragment).showCalender(this);
                }
                break;
            case R.id.filter:
                findViewById(R.id.month_popup).setVisibility(View.GONE);
                if (topFragment instanceof FragmentCalender) {
                    ((FragmentCalender) topFragment).showFilter();
                } else if (topFragment instanceof FragmentCalMonth) {
                    ((FragmentCalMonth) topFragment).showFilter();
                } else if (topFragment instanceof FragmentBusinesslistEvents) {
                    ((FragmentBusinesslistEvents) topFragment).toggleFilter();
                } else if (topFragment instanceof ArticlesFragment) {
//                    ((FragmentBusinesslistEvents) topFragment).toggleFilter();
                    Intent intent = new Intent(getApplicationContext(), TopicsFilterActivity.class);
                    startActivity(intent);
//                    startActivityForResult(intent, Constants.FILTER_ARTICLE);
                }
                break;
            case R.id.write:
//                CompleteProfileDialogFragment completeProfileDialogFragment = new CompleteProfileDialogFragment();
//                FragmentManager fm = getSupportFragmentManager();
//                completeProfileDialogFragment.setCancelable(false);
//                completeProfileDialogFragment.show(fm, "Complete blogger profile");

                if (Build.VERSION.SDK_INT > 15) {
                    Utils.pushEvent(DashboardActivity.this, GTMEventType.ADD_BLOG_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Left Menu Screen");
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
            case R.id.three_bar:
                replaceFragment(new FragmentCalender(), null, true);
                refreshMenu();
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
                } else if (topFragment instanceof NotificationFragment) {
                    ((NotificationFragment) topFragment).saveNotificationSetting();
                } else if (topFragment instanceof ChangeCityFragment)
                    ((ChangeCityFragment) topFragment).changeCity();
                break;
            case android.R.id.home:
                finish();
                break;

            case R.id.delete:
                if (topFragment instanceof FragmentTaskHome) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setMessage(getResources().getString(R.string.delete_list)).setNegativeButton(R.string.new_yes
                            , new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    TaskListModel taskListModel = new TaskListModel();
                                    taskListModel.setId(taskListID);
                                    if (ConnectivityUtils.isNetworkEnabled(DashboardActivity.this)) {
                                        showProgressDialog(getString(R.string.please_wait));

                                        TaskListController _controller = new TaskListController(DashboardActivity.this, DashboardActivity.this);
                                        _controller.getData(AppConstants.DELETE_LIST_REQUEST, taskListModel);
                                    } else {
                                        showToast(getString(R.string.error_network));
                                    }
                                }
                            }).setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            dialog.cancel();
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert);

                    AlertDialog alert11 = dialog.create();
                    alert11.show();

                    alert11.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.home_light_blue));
                    alert11.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.canceltxt_color));
                }
                break;
            case R.id.editTaskListId:
                if (topFragment instanceof FragmentTaskHome) {
                    AddTaskListPopUp addTaskListPopUp = new AddTaskListPopUp();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("from", "dashboard");
                    bundle1.putBoolean("editList", true);
                    addTaskListPopUp.setArguments(bundle1);
                    addTaskListPopUp.show(getFragmentManager(), "addTaskList");
                }
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

        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        switch (response.getDataType()) {
            case AppConstants.TASKS_COMPLETE_REQUEST:
                try {
                    CommonResponse responseData = (CommonResponse) response.getResponseObject();
                    if (responseData.getResponseCode() == 200) {
                        // mark sync as 1
                        // remove reminder
                        for (int taskid : taskIdlist) {
                            TaskCompletedTable table = new TaskCompletedTable(BaseApplication.getInstance());
                            ArrayList<String> dbDateList = table.getDatesById(taskid);
                            for (String date : dbDateList) {
                                table.updateSyncFlag(date, taskid);
                            }
                        }
                        // update on today screen too
                        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                        if (visibleFragment instanceof FragmentMC4KHome) {

                            try {
                                ((FragmentMC4KHome) visibleFragment).refreshList();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Log.e("", "response failed task complete");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case AppConstants.DELETE_TASK_REQUEST:
                try {
                    TaskResponse responseData = (TaskResponse) response.getResponseObject();
                    if (responseData.getResponseCode() == 200) {
                        // delete from db
                        TableTaskData tableTask = new TableTaskData(BaseApplication.getInstance());
                        // check if form push
                        if (frmPush) {
                            frmPush = false;
                            if (taskId > 0) {
                                removeProgressDialog();
                                tableTask.deleteTask(taskId);
                                // get from attendee table
                                TaskTableAttendee attendeeTable = new TaskTableAttendee(BaseApplication.getInstance());
                                attendeeTable.deleteTask(taskId);
                                // get from whotoRemond table
                                TaskTableWhoToRemind whotoRemindTable = new TaskTableWhoToRemind(BaseApplication.getInstance());
                                whotoRemindTable.deleteTask(taskId);
                                // get from FILES
                                TaskTableFile fileTable = new TaskTableFile(BaseApplication.getInstance());
                                fileTable.deleteTask(taskId);
                                // note
                                TaskTableNotes notesTable = new TaskTableNotes(BaseApplication.getInstance());
                                notesTable.deleteTask(taskId);
                                showToast(responseData.getResult().getMessage());
                                Reminder.with(DashboardActivity.this).cancel(taskId);
                            }
                        } else {
                            for (int taskId : deletedTasksList) {
                                // delete  in db
                                tableTask.deleteTask(taskId);
                                // get from attendee table
                                TaskTableAttendee attendeeTable = new TaskTableAttendee(BaseApplication.getInstance());
                                attendeeTable.deleteTask(taskId);
                                // get from whotoRemond table
                                TaskTableWhoToRemind whotoRemindTable = new TaskTableWhoToRemind(BaseApplication.getInstance());
                                whotoRemindTable.deleteTask(taskId);
                                // get from FILES
                                TaskTableFile fileTable = new TaskTableFile(BaseApplication.getInstance());
                                fileTable.deleteTask(taskId);
                                // note
                                TaskTableNotes notesTable = new TaskTableNotes(BaseApplication.getInstance());
                                notesTable.deleteTask(taskId);
                                Reminder.with(DashboardActivity.this).cancel(taskId);
                            }
                        }

                        // update on today screen too
                        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                        if (visibleFragment instanceof FragmentMC4KHome) {
                            try {
                                ((FragmentMC4KHome) visibleFragment).refreshList();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (responseData.getResponseCode() == 400) {
                        Log.e("", "response failed getAppointment");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case AppConstants.CREATE_TASKLIST_REQUEST:
                try {
                    TaskListResponse responseData = (TaskListResponse) response.getResponseObject();
                    if (responseData.getResponseCode() == 200) {
                        // save in db
                        saveListData(responseData.getResult().getData());
                        showToast(responseData.getResult().getMessage());

                    } else if (responseData.getResponseCode() == 400) {
                        showToast(responseData.getResult().getMessage());
                    }
                    removeProgressDialog();

                } catch (Exception e) {
                    removeProgressDialog();
                }
                break;

            case AppConstants.DELETE_LIST_REQUEST:

                try {
                    TaskResponse responseData = (TaskResponse) response.getResponseObject();
                    String asb = "";
                    if (responseData.getResponseCode() == 200) {
                        // save in db
                        showToast(responseData.getResult().getMessage());
                        if (topFragment instanceof FragmentTaskHome) {
                            TableTaskList tableTaskList = new TableTaskList(BaseApplication.getInstance());
                            tableTaskList.deleteList(taskListID);

                            TableTaskData taskData = new TableTaskData(BaseApplication.getInstance());
                            taskData.deleteTaskByListId(taskListID);

                            ((FragmentTaskHome) topFragment).NotifyTaskByListId(false, 0);
                            setTitle("All Tasks");
                            notiftTaskList();
                            refreshMenu();

                            findViewById(R.id.task_popup).setVisibility(View.GONE);
                            taskIconFlag = false;
                        }
                    } else if (responseData.getResponseCode() == 400) {
                        showToast(responseData.getResult().getMessage());
                    }
                    removeProgressDialog();

                } catch (Exception e) {
                    removeProgressDialog();
                }

                break;

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
        mDrawerLayout.closeDrawer(mDrawerView);
        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        switch (v.getId()) {

            case R.id.downarrow:
                if (topFragment instanceof FragmentCalender) {
                    if (findViewById(R.id.month_popup).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.month_popup).setVisibility(View.GONE);
                    } else {
                        isPopupOpen = true;
                        findViewById(R.id.month_popup).setVisibility(View.VISIBLE);
                    }
                } else if (topFragment instanceof FragmentCalMonth) {
                    if (findViewById(R.id.month_popup).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.month_popup).setVisibility(View.GONE);
                    } else {
                        isPopupOpen = true;
                        findViewById(R.id.month_popup).setVisibility(View.VISIBLE);
                    }
                    refreshMenu();
                } else if (topFragment instanceof FragmentTaskHome) {
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    if (findViewById(R.id.task_popup).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.task_popup).setVisibility(View.GONE);
                    } else {
                        findViewById(R.id.task_popup).setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.rdBtnToday:
                changeVisibiltyOfArrow(false);
                Utils.pushEvent(DashboardActivity.this, GTMEventType.MC4KToday_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Left Menu Screen");
                replaceFragment(new FragmentMC4KHome(), null, false);
                setTitle("");
                break;
            case R.id.rdBtnUpcoming:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.UPCOMING_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Left Menu Screen");
                Constants.IS_SEARCH_LISTING = false;
                changeVisibiltyOfArrow(false);
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
                changeVisibiltyOfArrow(false);
                setTitle("Kids Resources");
                replaceFragment(new FragmentHomeCategory(), null, true);
                break;
            case R.id.rdBtnHindi:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.RESOURCES_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Left Menu Screen");
                Intent hindiIntent = new Intent(this, FilteredTopicsArticleListingActivity.class);
                Topics hindiTopic = AppUtils.getSpecificLanguageTopic(this, AppConstants.HINDI_CATEGORYID);
                if (hindiTopic == null) {
                    hindiIntent.putExtra("selectedTopics", AppConstants.HINDI_CATEGORYID);
                    hindiIntent.putExtra("displayName", getString(R.string.home_sections_title_hindi));
                } else {
                    hindiIntent.putExtra("selectedTopics", AppConstants.HINDI_CATEGORYID);
                    hindiIntent.putExtra("displayName", hindiTopic.getDisplay_name());
                }
                startActivity(hindiIntent);
                break;
            case R.id.rdBtnBangla:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.RESOURCES_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Left Menu Screen");
                Intent banglaIntent = new Intent(this, FilteredTopicsArticleListingActivity.class);
                Topics banglaTopic = AppUtils.getSpecificLanguageTopic(this, AppConstants.BANGLA_CATEGORYID);
                if (banglaTopic == null) {
                    banglaIntent.putExtra("selectedTopics", AppConstants.BANGLA_CATEGORYID);
                    banglaIntent.putExtra("displayName", getString(R.string.home_sections_title_bangla));
                } else {
                    banglaIntent.putExtra("selectedTopics", AppConstants.BANGLA_CATEGORYID);
                    banglaIntent.putExtra("displayName", banglaTopic.getDisplay_name());
                }
                startActivity(banglaIntent);
                break;
            case R.id.rdBtnMarathi:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.RESOURCES_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Left Menu Screen");
                Intent marathiIntent = new Intent(this, FilteredTopicsArticleListingActivity.class);
                Topics marathiTopic = AppUtils.getSpecificLanguageTopic(this, AppConstants.MARATHI_CATEGORYID);
                if (marathiTopic == null) {
                    marathiIntent.putExtra("selectedTopics", AppConstants.MARATHI_CATEGORYID);
                    marathiIntent.putExtra("displayName", getString(R.string.home_sections_title_marathi));
                } else {
                    marathiIntent.putExtra("selectedTopics", AppConstants.MARATHI_CATEGORYID);
                    marathiIntent.putExtra("displayName", marathiTopic.getDisplay_name());
                }
                startActivity(marathiIntent);
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
                    Utils.pushEvent(DashboardActivity.this, GTMEventType.ADD_BLOG_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Left Menu Screen");
                    launchEditor();
                } else {
                    showToast("This version of android is no more supported.");
                }
                break;
            case R.id.feed_back:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.FEEDBACK_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "", "Left Menu Screen");
                changeVisibiltyOfArrow(false);
                setTitle("Send Feedback");
                replaceFragment(new SendFeedbackFragment(), null, true);
                break;
            case R.id.addVideosTextView:
                launchAddVideoOptions();
                break;
            case R.id.myVideosTextView:
                Intent funnyIntent = new Intent(DashboardActivity.this, MyFunnyVideosListingActivity.class);
                startActivity(funnyIntent);
                break;
            case R.id.txvUserName:
            case R.id.imgProfile:
                Intent intent4 = new Intent(DashboardActivity.this, BloggerDashboardActivity.class);
                startActivity(intent4);
                break;
            case R.id.back_month:
                year.setText(String.valueOf(Integer.parseInt(String.valueOf(year.getText())) - 1));
                refreshMonthPopup();
                break;
            case R.id.next_month:
                year.setText(String.valueOf(Integer.parseInt(String.valueOf(year.getText())) + 1));
                refreshMonthPopup();
                break;
            case R.id.jan:
                if (topFragment instanceof FragmentCalender) {
                    try {
                        ((FragmentCalender) topFragment).updateListbyDay(0, Integer.parseInt(String.valueOf(year.getText())));
                        setTitleFormat(1, Integer.parseInt(String.valueOf(year.getText())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (topFragment instanceof FragmentCalMonth) {
                    ((FragmentCalMonth) topFragment).setMonthByPopUp(0, Integer.parseInt(String.valueOf(year.getText())));
                    setTitleFormat(1, Integer.parseInt(String.valueOf(year.getText())));

                }
                findViewById(R.id.month_popup).setVisibility(View.GONE);
                break;
            case R.id.feb:
                if (topFragment instanceof FragmentCalender) {
                    try {
                        ((FragmentCalender) topFragment).updateListbyDay(1, Integer.parseInt(String.valueOf(year.getText())));
                        setTitleFormat(2, Integer.parseInt(String.valueOf(year.getText())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (topFragment instanceof FragmentCalMonth) {

                    ((FragmentCalMonth) topFragment).setMonthByPopUp(1, Integer.parseInt(String.valueOf(year.getText())));
                    setTitleFormat(2, Integer.parseInt(String.valueOf(year.getText())));
                }
                findViewById(R.id.month_popup).setVisibility(View.GONE);
                break;
            case R.id.mar:
                if (topFragment instanceof FragmentCalender) {
                    try {
                        ((FragmentCalender) topFragment).updateListbyDay(2, Integer.parseInt(String.valueOf(year.getText())));
                        setTitleFormat(3, Integer.parseInt(String.valueOf(year.getText())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (topFragment instanceof FragmentCalMonth) {
                    ((FragmentCalMonth) topFragment).setMonthByPopUp(2, Integer.parseInt(String.valueOf(year.getText())));
                    setTitleFormat(3, Integer.parseInt(String.valueOf(year.getText())));
                }
                findViewById(R.id.month_popup).setVisibility(View.GONE);
                break;
            case R.id.apr:
                if (topFragment instanceof FragmentCalender) {
                    try {
                        ((FragmentCalender) topFragment).updateListbyDay(3, Integer.parseInt(String.valueOf(year.getText())));
                        setTitleFormat(4, Integer.parseInt(String.valueOf(year.getText())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (topFragment instanceof FragmentCalMonth) {
                    ((FragmentCalMonth) topFragment).setMonthByPopUp(3, Integer.parseInt(String.valueOf(year.getText())));
                    setTitleFormat(4, Integer.parseInt(String.valueOf(year.getText())));
                }
                findViewById(R.id.month_popup).setVisibility(View.GONE);
                break;
            case R.id.may:
                if (topFragment instanceof FragmentCalender) {
                    try {
                        ((FragmentCalender) topFragment).updateListbyDay(4, Integer.parseInt(String.valueOf(year.getText())));
                        setTitleFormat(5, Integer.parseInt(String.valueOf(year.getText())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (topFragment instanceof FragmentCalMonth) {

                    ((FragmentCalMonth) topFragment).setMonthByPopUp(4, Integer.parseInt(String.valueOf(year.getText())));
                    setTitleFormat(5, Integer.parseInt(String.valueOf(year.getText())));
                }
                findViewById(R.id.month_popup).setVisibility(View.GONE);
                break;
            case R.id.june:
                if (topFragment instanceof FragmentCalender) {
                    try {
                        ((FragmentCalender) topFragment).updateListbyDay(5, Integer.parseInt(String.valueOf(year.getText())));
                        setTitleFormat(6, Integer.parseInt(String.valueOf(year.getText())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (topFragment instanceof FragmentCalMonth) {
                    ((FragmentCalMonth) topFragment).setMonthByPopUp(5, Integer.parseInt(String.valueOf(year.getText())));
                    setTitleFormat(6, Integer.parseInt(String.valueOf(year.getText())));
                }
                findViewById(R.id.month_popup).setVisibility(View.GONE);
                break;
            case R.id.july:
                if (topFragment instanceof FragmentCalender) {
                    try {
                        ((FragmentCalender) topFragment).updateListbyDay(6, Integer.parseInt(String.valueOf(year.getText())));
                        setTitleFormat(7, Integer.parseInt(String.valueOf(year.getText())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (topFragment instanceof FragmentCalMonth) {
                    ((FragmentCalMonth) topFragment).setMonthByPopUp(6, Integer.parseInt(String.valueOf(year.getText())));
                    setTitleFormat(7, Integer.parseInt(String.valueOf(year.getText())));
                }
                findViewById(R.id.month_popup).setVisibility(View.GONE);
                break;
            case R.id.aug:
                if (topFragment instanceof FragmentCalender) {
                    try {
                        ((FragmentCalender) topFragment).updateListbyDay(7, Integer.parseInt(String.valueOf(year.getText())));
                        setTitleFormat(8, Integer.parseInt(String.valueOf(year.getText())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (topFragment instanceof FragmentCalMonth) {
                    ((FragmentCalMonth) topFragment).setMonthByPopUp(7, Integer.parseInt(String.valueOf(year.getText())));
                    setTitleFormat(8, Integer.parseInt(String.valueOf(year.getText())));
                }
                findViewById(R.id.month_popup).setVisibility(View.GONE);
                break;
            case R.id.sept:
                if (topFragment instanceof FragmentCalender) {
                    try {
                        ((FragmentCalender) topFragment).updateListbyDay(8, Integer.parseInt(String.valueOf(year.getText())));
                        setTitleFormat(9, Integer.parseInt(String.valueOf(year.getText())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (topFragment instanceof FragmentCalMonth) {
                    ((FragmentCalMonth) topFragment).setMonthByPopUp(8, Integer.parseInt(String.valueOf(year.getText())));
                    setTitleFormat(9, Integer.parseInt(String.valueOf(year.getText())));
                }
                findViewById(R.id.month_popup).setVisibility(View.GONE);
                break;
            case R.id.oct:
                if (topFragment instanceof FragmentCalender) {
                    try {
                        ((FragmentCalender) topFragment).updateListbyDay(9, Integer.parseInt(String.valueOf(year.getText())));
                        setTitleFormat(10, Integer.parseInt(String.valueOf(year.getText())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (topFragment instanceof FragmentCalMonth) {

                    ((FragmentCalMonth) topFragment).setMonthByPopUp(9, Integer.parseInt(String.valueOf(year.getText())));
                    setTitleFormat(10, Integer.parseInt(String.valueOf(year.getText())));
                }
                findViewById(R.id.month_popup).setVisibility(View.GONE);
                break;
            case R.id.nov:
                if (topFragment instanceof FragmentCalender) {
                    try {
                        ((FragmentCalender) topFragment).updateListbyDay(10, Integer.parseInt(String.valueOf(year.getText())));
                        setTitleFormat(11, Integer.parseInt(String.valueOf(year.getText())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (topFragment instanceof FragmentCalMonth) {
                    ((FragmentCalMonth) topFragment).setMonthByPopUp(10, Integer.parseInt(String.valueOf(year.getText())));
                    setTitleFormat(11, Integer.parseInt(String.valueOf(year.getText())));
                }
                findViewById(R.id.month_popup).setVisibility(View.GONE);
                break;
            case R.id.dec:
                if (topFragment instanceof FragmentCalender) {
                    try {
                        ((FragmentCalender) topFragment).updateListbyDay(11, Integer.parseInt(String.valueOf(year.getText())));
                        setTitleFormat(12, Integer.parseInt(String.valueOf(year.getText())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (topFragment instanceof FragmentCalMonth) {
                    ((FragmentCalMonth) topFragment).setMonthByPopUp(11, Integer.parseInt(String.valueOf(year.getText())));
                    setTitleFormat(12, Integer.parseInt(String.valueOf(year.getText())));
                }
                findViewById(R.id.month_popup).setVisibility(View.GONE);
                break;
            case R.id.add_tasklist:
                AddTaskListPopUp addTaskListPopUp = new AddTaskListPopUp();
                Bundle bundle1 = new Bundle();
                bundle1.putString("from", "dashboard");
                addTaskListPopUp.setArguments(bundle1);
                addTaskListPopUp.show(getFragmentManager(), "addTaskList");
                break;
            case R.id.all_tasklist:
                if (topFragment instanceof FragmentTaskHome) {
                    SharedPrefUtils.setTaskListID(DashboardActivity.this, 0);
                    ((FragmentTaskHome) topFragment).NotifyTaskByListId(false, 0);
                    setTitle("All Tasks");
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    taskIconFlag = false;
                    refreshMenu();
                }
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
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateDate(int day, int month, int year) {
        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (topFragment instanceof FragmentCalMonth) {
            ((FragmentCalMonth) topFragment).setCurrentDate(day, month, year);
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
                case Constants.CREATE_TASK:
                    if (topFragment instanceof FragmentTaskHome) {
                        ((FragmentTaskHome) topFragment).refreshTaskList();
                        notiftTaskList();
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
//                        mStatusText.setText("Account unspecified.");
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
                    if (topFragment instanceof FragmentCalender) {
//                        ((FragmentCalender) topFragment).refreshView();
                    } else if (topFragment instanceof FragmentMC4KHome) {
                        ((FragmentMC4KHome) topFragment).refreshList();
                        if (SharedPrefUtils.isCityFetched(this) && SharedPrefUtils.getCurrentCityModel(this).getId() != AppConstants.OTHERS_CITY_ID) {
                            findViewById(R.id.rdBtnUpcoming).setVisibility(View.VISIBLE);
                            findViewById(R.id.rdBtnKids).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.rdBtnUpcoming).setVisibility(View.GONE);
                            findViewById(R.id.rdBtnKids).setVisibility(View.GONE);
                        }
                    } else if (topFragment instanceof FragmentCalMonth) {
                        ((FragmentCalMonth) topFragment).refreshCalender_afterAdd();
                    } else if (topFragment instanceof FragmentMC4KHome) {
                        ((FragmentMC4KHome) topFragment).notifyTaskList();
                    } else if (topFragment instanceof FragmentTaskHome) {
                        ((FragmentTaskHome) topFragment).refreshTaskList();
                        notiftTaskList();
                    }

                    break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (resultCode == 2) {
//
//            if (topFragment instanceof FragmentTaskHome) {
//
//                ((FragmentTaskHome) topFragment).refreshTaskList();
//            }
//        }

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

    public void setTitleFormat(int month, int year) {

        Calendar calendar = Calendar.getInstance();
        String headerAtPos = year + " " + month;

        DateFormat format = new SimpleDateFormat("yyyy MM", Locale.US);
        DateFormat format1 = new SimpleDateFormat("MMM yyyy", Locale.US);
        Date date = null;
        try {
            date = format.parse(headerAtPos);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(date);
        calendar.setTime(date);

        String temp = format1.format(calendar.getTime());

        setTitle(format1.format(calendar.getTime()));
    }


    public void refreshMonthPopup() {

        SimpleDateFormat form = new SimpleDateFormat("MMM yyyy", Locale.US);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));

        String currentDate = (form.format(calendar.getTime())).toUpperCase();

        if (currentDate.toUpperCase().equals(((TextView) findViewById(R.id.jan)).getText().toString().toUpperCase() + " " + ((TextView) findViewById(R.id.year)).getText().toString())) {
            ((TextView) findViewById(R.id.jan)).setTextColor(Color.parseColor("#0000FF"));
        } else if (currentDate.toUpperCase().equals(((TextView) findViewById(R.id.feb)).getText().toString().toUpperCase() + " " + ((TextView) findViewById(R.id.year)).getText().toString())) {
            ((TextView) findViewById(R.id.feb)).setTextColor(Color.parseColor("#0000FF"));
        } else if (currentDate.toUpperCase().equals(((TextView) findViewById(R.id.mar)).getText().toString().toUpperCase() + " " + ((TextView) findViewById(R.id.year)).getText().toString())) {
            ((TextView) findViewById(R.id.mar)).setTextColor(Color.parseColor("#0000FF"));
        } else if (currentDate.toUpperCase().equals(((TextView) findViewById(R.id.apr)).getText().toString().toUpperCase() + " " + ((TextView) findViewById(R.id.year)).getText().toString())) {
            ((TextView) findViewById(R.id.apr)).setTextColor(Color.parseColor("#0000FF"));
        } else if (currentDate.toUpperCase().equals(((TextView) findViewById(R.id.may)).getText().toString().toUpperCase() + " " + ((TextView) findViewById(R.id.year)).getText().toString())) {
            ((TextView) findViewById(R.id.may)).setTextColor(Color.parseColor("#0000FF"));
        } else if (currentDate.toUpperCase().equals(((TextView) findViewById(R.id.june)).getText().toString().toUpperCase() + " " + ((TextView) findViewById(R.id.year)).getText().toString())) {
            ((TextView) findViewById(R.id.june)).setTextColor(Color.parseColor("#0000FF"));
        } else if (currentDate.toUpperCase().equals(((TextView) findViewById(R.id.july)).getText().toString().toUpperCase() + " " + ((TextView) findViewById(R.id.year)).getText().toString())) {
            ((TextView) findViewById(R.id.july)).setTextColor(Color.parseColor("#0000FF"));
        } else if (currentDate.toUpperCase().equals(((TextView) findViewById(R.id.aug)).getText().toString().toUpperCase() + " " + ((TextView) findViewById(R.id.year)).getText().toString())) {
            ((TextView) findViewById(R.id.aug)).setTextColor(Color.parseColor("#0000FF"));
        } else if (currentDate.toUpperCase().equals(((TextView) findViewById(R.id.sept)).getText().toString().toUpperCase() + " " + ((TextView) findViewById(R.id.year)).getText().toString())) {
            ((TextView) findViewById(R.id.sept)).setTextColor(Color.parseColor("#0000FF"));
        } else if (currentDate.toUpperCase().equals(((TextView) findViewById(R.id.oct)).getText().toString().toUpperCase() + " " + ((TextView) findViewById(R.id.year)).getText().toString())) {
            ((TextView) findViewById(R.id.oct)).setTextColor(Color.parseColor("#0000FF"));
        } else if (currentDate.toUpperCase().equals(((TextView) findViewById(R.id.nov)).getText().toString().toUpperCase() + " " + ((TextView) findViewById(R.id.year)).getText().toString())) {
            ((TextView) findViewById(R.id.nov)).setTextColor(Color.parseColor("#0000FF"));
        } else if (currentDate.toUpperCase().equals(((TextView) findViewById(R.id.dec)).getText().toString().toUpperCase() + " " + ((TextView) findViewById(R.id.year)).getText().toString())) {
            ((TextView) findViewById(R.id.dec)).setTextColor(Color.parseColor("#0000FF"));
        } else {
            ((TextView) findViewById(R.id.jan)).setTextColor(Color.parseColor("#ec3b55"));
            ((TextView) findViewById(R.id.feb)).setTextColor(Color.parseColor("#ec3b55"));
            ((TextView) findViewById(R.id.mar)).setTextColor(Color.parseColor("#ec3b55"));
            ((TextView) findViewById(R.id.apr)).setTextColor(Color.parseColor("#ec3b55"));
            ((TextView) findViewById(R.id.may)).setTextColor(Color.parseColor("#ec3b55"));
            ((TextView) findViewById(R.id.june)).setTextColor(Color.parseColor("#ec3b55"));
            ((TextView) findViewById(R.id.july)).setTextColor(Color.parseColor("#ec3b55"));
            ((TextView) findViewById(R.id.aug)).setTextColor(Color.parseColor("#ec3b55"));
            ((TextView) findViewById(R.id.sept)).setTextColor(Color.parseColor("#ec3b55"));
            ((TextView) findViewById(R.id.oct)).setTextColor(Color.parseColor("#ec3b55"));
            ((TextView) findViewById(R.id.nov)).setTextColor(Color.parseColor("#ec3b55"));
            ((TextView) findViewById(R.id.dec)).setTextColor(Color.parseColor("#ec3b55"));
        }
    }

    public ArrayList<TaskListModel> getTaskList() {

        TableTaskList tableTaskList = new TableTaskList(BaseApplication.getInstance());

        ArrayList<TaskListModel> userTaskLists = (ArrayList<TaskListModel>) tableTaskList.getAllList(SharedPrefUtils.getUserDetailModel(this).getId());

        return userTaskLists;
    }

    public void notiftTaskList() {
        ArrayList<TaskListModel> sortTaskFolderList = getTaskList();
        Collections.sort(sortTaskFolderList, new TaskFolderListComparator());
        taskListAdapter.notifyList(sortTaskFolderList, false);
        TableTaskData taskData = new TableTaskData(BaseApplication.getInstance());
        txvAllTaskPopup.setText("All Tasks (" + taskData.getRowsCount() + ")");

    }


    public class TaskFolderListComparator implements Comparator<TaskListModel> {
        @Override
        public int compare(TaskListModel taskListModel, TaskListModel t1) {
            if (taskListModel.getSize() > t1.getSize() || taskListModel.getSize() < t1.getSize()) {
                return t1.getSize() - taskListModel.getSize();
            } else {
                return taskListModel.getList_name().compareToIgnoreCase(t1.getList_name());
            }
        }
    }

    public void addTaskList(String name, boolean edit) {

        TaskListModel taskListModel = new TaskListModel();

        if (name != "") {

            if (edit) {
                editTaskList = true;
                editTaskListname = name;
                taskListModel.setList_name(name);
                taskListModel.setId(SharedPrefUtils.getTaskListID(this));

            } else {
                taskListModel.setList_name(name);
                taskListModel.setId(0);
            }

            if (ConnectivityUtils.isNetworkEnabled(this)) {
                showProgressDialog(getString(R.string.please_wait));

                TaskListController _controller = new TaskListController(this, this);
                _controller.getData(AppConstants.CREATE_TASKLIST_REQUEST, taskListModel);
            } else {
                showToast(getString(R.string.error_network));
            }
        }

    }

    public void saveListData(ArrayList<TaskListResponse.AllList> model) {

        TableTaskList taskTable = new TableTaskList((BaseApplication) getApplicationContext());
        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (editTaskList) {
            editTaskList = false;
            // update list name
            taskTable.updateList(editTaskListname, SharedPrefUtils.getTaskListID(this));

            setTitle(editTaskListname);

            if (topFragment instanceof FragmentTaskHome) {
                ((FragmentTaskHome) topFragment).refreshList(oldListName, editTaskListname, SharedPrefUtils.getTaskListID(this));
            }

            //taskTable.deleteList(SharedPrefUtils.getTaskListID(this));
            //taskTable.insertData((model.get(model.size() - 1).getTaskList()));

        } else {

            taskTable.insertData((model.get(model.size() - 1).getTaskList()));
        }

        notiftTaskList();

    }

    private void hitApiRequest(int requestType) {

        switch (requestType) {
            case AppConstants.TASKS_COMPLETE_REQUEST:

                //get from db
                TaskCompletedTable table = new TaskCompletedTable(BaseApplication.getInstance());
                taskIdlist = table.getIdList();

                if (!taskIdlist.isEmpty()) {

                    CompleteTaskRequestModel mainModel = new CompleteTaskRequestModel();

                    ArrayList<CompleteTaskRequestModel.Todo> todoCompleteList = new ArrayList<>();

                    for (int taskid : taskIdlist) {

                        // get dates
                        ArrayList<String> dbDateList = table.getDatesById(taskid);

                        CompleteTaskRequestModel.Todo todo = new CompleteTaskRequestModel().new Todo();

                        CompleteTaskRequestModel.CompletedTask modelCompletedTask = new CompleteTaskRequestModel().new CompletedTask();

                        ArrayList<CompleteTaskRequestModel.Dates> modelDateList = new ArrayList<>();


                        for (String date : dbDateList) {
                            CompleteTaskRequestModel.Dates dateModel = new CompleteTaskRequestModel().new Dates();
                            dateModel.setDate(date);

                            modelDateList.add(dateModel);
                        }
                        modelCompletedTask.setTask_id(taskid);
                        modelCompletedTask.setExcluded_date(modelDateList);
                        todo.setTask(modelCompletedTask);

                        todoCompleteList.add(todo);


                    }

                    mainModel.setTodolist(todoCompleteList);

                    String data = new Gson().toJson(mainModel);

                    CompleteTaskController _controller = new CompleteTaskController(this, this);
                    _controller.getData(AppConstants.TASKS_COMPLETE_REQUEST, mainModel);


                }


                break;
            case AppConstants.DELETE_TASK_REQUEST:

                // get from db

                TableTaskData taskData = new TableTaskData(BaseApplication.getInstance());
                deletedTasksList = taskData.getInActiveTasksData();

                if (deletedTasksList != null && !deletedTasksList.isEmpty()) {
                    DeleteTaskModel taskModel = new DeleteTaskModel();
                    ArrayList<DeleteTaskModel.Tasks> tasksArrayList = new ArrayList<>();


                    for (int val : deletedTasksList) {
                        DeleteTaskModel.Tasks model = new DeleteTaskModel().new Tasks();
                        model.setId(val);
                        tasksArrayList.add(model);
                    }

                    taskModel.setTasks(tasksArrayList);

                    String data = new Gson().toJson(taskModel);

                    DeleteTaskController _controller = new DeleteTaskController(this, this);
                    _controller.getData(AppConstants.DELETE_TASK_REQUEST, taskModel);

                }
                break;
        }


    }


    public void UploadCompleteTasks() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                if (ConnectivityUtils.isNetworkEnabled(DashboardActivity.this)) {
                    hitApiRequest(AppConstants.TASKS_COMPLETE_REQUEST);
                    hitApiRequest(AppConstants.DELETE_TASK_REQUEST);
                }
            }
        });
        t.start();

    }

    public void clearResultsText() {

        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (topFragment instanceof ExternalCalFragment) {
            ((ExternalCalFragment) topFragment).clearResultsText();
        }
    }

    public void updateResultsText(ArrayList<ExternalEventModel> dataFromApi) {

        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (topFragment instanceof ExternalCalFragment) {
            ((ExternalCalFragment) topFragment).updateResultsText(dataFromApi);
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
            changeVisibiltyOfArrow(false);
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
            startActivity(_authorListIntent);
        }
    }

    private void renderBloggerListingScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getBlog_title())) {
            Intent _bloggerListIntent = new Intent(DashboardActivity.this, BloggerDashboardActivity.class);
            _bloggerListIntent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, data.getAuthor_id());
            _bloggerListIntent.putExtra(Constants.DEEPLINK_URL, deepLinkUrl);
            startActivity(_bloggerListIntent);
        }
    }

    private void renderArticleDetailScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getArticle_id())) {
            Intent intent = new Intent(DashboardActivity.this, ArticlesAndBlogsDetailsActivity.class);
            intent.putExtra(Constants.AUTHOR_ID, data.getAuthor_id());
            intent.putExtra(Constants.ARTICLE_ID, data.getArticle_id());
            intent.putExtra(Constants.DEEPLINK_URL, deepLinkUrl);
            startActivity(intent);
        }
    }

    private void renderVlogDetailScreen(DeepLinkingResult data) {
        if (!StringUtils.isNullOrEmpty(data.getId())) {
            Intent intent = new Intent(DashboardActivity.this, VlogsDetailActivity.class);
//            intent.putExtra(Constants.AUTHOR_ID, data.getAuthor_id());
            intent.putExtra(Constants.VIDEO_ID, data.getId());
            intent.putExtra(Constants.DEEPLINK_URL, deepLinkUrl);
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

    private void showCreateFamilyAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setMessage(getResources().getString(R.string.create_family)).setNegativeButton(getResources().getString(R.string.yes)
                , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent createFamilyIntent = new Intent(DashboardActivity.this, CreateFamilyActivity.class);
                        startActivity(createFamilyIntent);
                        dialog.cancel();
                    }
                }).setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
                dialog.cancel();

            }
        }).setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alert11 = dialog.create();
        alert11.show();

        alert11.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.home_light_blue));
        alert11.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.canceltxt_color));

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
            Snackbar.make(mDrawerLayout, R.string.permission_storage_rationale,
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
            Snackbar.make(mDrawerLayout, R.string.permission_camera_rationale,
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
                Snackbar.make(mDrawerLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(videoCapture, AppConstants.REQUEST_VIDEO_TRIMMER);
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(mDrawerLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == REQUEST_GALLERY_PERMISSION) {
            Log.i("Permissions", "Received response for storage permissions request.");

            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(mDrawerLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent();
                intent.setType("video/mp4");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), AppConstants.REQUEST_VIDEO_TRIMMER);
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(mDrawerLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
