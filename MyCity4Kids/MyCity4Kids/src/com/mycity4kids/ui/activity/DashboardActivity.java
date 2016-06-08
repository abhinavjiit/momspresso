package com.mycity4kids.ui.activity;

import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.comscore.analytics.comScore;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.asynctask.HeavyDbTask;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.ColorCode;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ArticleBlogFollowController;
import com.mycity4kids.controller.CompleteTaskController;
import com.mycity4kids.controller.DeepLinkingController;
import com.mycity4kids.controller.DeleteTaskController;
import com.mycity4kids.controller.TaskListController;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TableTaskList;
import com.mycity4kids.dbtable.TaskCompletedTable;
import com.mycity4kids.dbtable.TaskTableAttendee;
import com.mycity4kids.dbtable.TaskTableFile;
import com.mycity4kids.dbtable.TaskTableNotes;
import com.mycity4kids.dbtable.TaskTableWhoToRemind;
import com.mycity4kids.editor.DraftListViewActivity;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.enums.DialogButtonEvent;
import com.mycity4kids.enums.DialogEnum;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.facebook.FacebookUtils;
import com.mycity4kids.gcm.GCMUtil;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.IGetRateAndUpdateEvent;
import com.mycity4kids.interfaces.OnUIView;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.models.deeplinking.DeepLinkApiModel;
import com.mycity4kids.models.deeplinking.DeepLinkData;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.parentingstop.ArticleBlogFollowRequest;
import com.mycity4kids.models.version.RateVersion;
import com.mycity4kids.newmodels.CompleteTaskRequestModel;
import com.mycity4kids.newmodels.DeleteTaskModel;
import com.mycity4kids.newmodels.ExternalEventModel;
import com.mycity4kids.newmodels.TaskListModel;
import com.mycity4kids.newmodels.TaskListResponse;
import com.mycity4kids.newmodels.TaskResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.reminders.Reminder;
import com.mycity4kids.ui.adapter.UserTaskListAdapter;
import com.mycity4kids.ui.fragment.AddTaskListPopUp;
import com.mycity4kids.ui.fragment.ArticlesFragment;
import com.mycity4kids.ui.fragment.ChangeCityFragment;
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
import com.mycity4kids.ui.fragment.ParentingBlogFragment;
import com.mycity4kids.ui.fragment.RateAppDialogFragment;
import com.mycity4kids.ui.fragment.SendFeedbackFragment;
import com.mycity4kids.ui.fragment.SyncSettingFragment;
import com.mycity4kids.utils.RoundedTransformation;
import com.mycity4kids.widget.CustomListView;
import com.squareup.picasso.Picasso;

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

public class DashboardActivity extends BaseActivity implements View.OnClickListener, IGetRateAndUpdateEvent {

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

    // The onNewIntent() is overridden to get and resolve the data for deep linking
    @Override
    protected void onNewIntent(Intent intent) {
        if (!StringUtils.isNullOrEmpty(intent.getStringExtra(AppConstants.DEEP_LINK_URL)))
            getDeepLinkData(intent.getStringExtra(AppConstants.DEEP_LINK_URL));
        super.onNewIntent(intent);
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
        t.set("&uid", SharedPrefUtils.getUserDetailModel(this).getId() + "");

        // This hit will be sent with the User ID value and be visible in User-ID-enabled views (profiles).
        t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("User Sign In").build());

        // till here
        // Build and send timing.
   /*     t.send(new HitBuilders.TimingBuilder()
                .setCategory(getTimingCategory())
                .setValue(getTimingInterval())
                .setVariable(getTimingName())
                .setLabel(getTimingLabel())
                .build());*/


// Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());
        onNewIntent(getIntent());
        Intent intent = getIntent();
        Bundle notificationExtras=intent.getParcelableExtra("notificationExtras");
        if (notificationExtras!=null)
        {
            if (notificationExtras.getString("type").equalsIgnoreCase("article"))
            {
                String articleId=notificationExtras.getString("articleId");
                Intent intent1=new Intent(DashboardActivity.this, ArticlesAndBlogsDetailsActivity.class);
                intent1.putExtra("article_id",articleId);
                intent1.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent1);
                finish();
            }
            else if (notificationExtras.getString("type").equalsIgnoreCase("event"))
            {

            }
        }
        Bundle extras = intent.getExtras();
        String fragmentToLoad = "";
        if (null != extras)
            fragmentToLoad = extras.getString("load_fragment", "");
        // DatabaseUtil.exportDb();

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

        Utils.pushOpenScreenEvent(DashboardActivity.this, "DashBoard", SharedPrefUtils.getUserDetailModel(this).getId() + "");

//        Reminder.with(this).info(Constants.REMINDER_KIDS_BIRTHDAY, "RANDOM kids birthday")
//                .startTime(1419318120000l).setRepeatBehavior("Yearly", "Forever", "", null)
//                .remindBefore("0").setRecurring("yes").create(11111120);
        TableKids _kidTable = new TableKids(BaseApplication.getInstance());
        Long sTime;
        for (int i = 0; i < _kidTable.getKidsCount(); i++) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date dob;
            try {
                //28800000 -- Morning 8 am addition
                dob = df.parse(_kidTable.getAllKids().get(i).getDate_of_birth());
                Reminder.with(this).info(Constants.REMINDER_KIDS_BIRTHDAY, "" + _kidTable.getAllKids().get(i).getName())
                        .startTime(dob.getTime() + 28800000).setRepeatBehavior("Yearly", "Forever", "", null)
                        .remindBefore("0").setRecurring("yes").create(-i);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
//        addChildbirthTS();

        // onclick events
        findViewById(R.id.rdBtnToday).setOnClickListener(this);
        findViewById(R.id.rdBtnCalender).setOnClickListener(this);
        //  findViewById(R.id.rdBtnTodo).setOnClickListener(this);
        findViewById(R.id.rdBtnUpcoming).setOnClickListener(this);
        findViewById(R.id.feed_back).setOnClickListener(this);

        setSupportActionBar(mToolbar);

//        if (!SharedPrefUtils.getPushTokenUpdateToServer(DashboardActivity.this)) {
      //  GCMUtil.initializeGCM(DashboardActivity.this);
//        }

//        set task list


        taskListAdapter = new UserTaskListAdapter(this, getTaskList(), false);
        allTaskList.setAdapter(taskListAdapter);

        currentdate = Calendar.getInstance();
        tempyear = currentdate.get(Calendar.YEAR);

        year.setText(String.valueOf(tempyear));
        mUsernName.setText(SharedPrefUtils.getUserDetailModel(this).getFirst_name());
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
                        // task complete
                        // frmPush = true;
                        // insert in db then hit api

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


        if (SharedPrefUtils.isCityFetched(this)) {
            findViewById(R.id.rdBtnUpcoming).setVisibility(View.VISIBLE);
            findViewById(R.id.rdBtnKids).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.rdBtnUpcoming).setVisibility(View.GONE);
            findViewById(R.id.rdBtnKids).setVisibility(View.GONE);
        }

        findViewById(R.id.rdBtnKids).setOnClickListener(this);
        findViewById(R.id.rdBtnParentingBlogs).setOnClickListener(this);
        findViewById(R.id.editor).setOnClickListener(this);
        findViewById(R.id.drafts).setOnClickListener(this);
        findViewById(R.id.bloggerDashboard).setOnClickListener(this);
        findViewById(R.id.txvSettings).setOnClickListener(this);
        findViewById(R.id.txvMeetContributors).setOnClickListener(this);
        //  findViewById(R.id.txvHelp).setOnClickListener(this);
        findViewById(R.id.imgProfile).setOnClickListener(this);
        findViewById(R.id.txvUserName).setOnClickListener(this);

        findViewById(R.id.txvfeedback).setOnClickListener(this);
        findViewById(R.id.txvrate).setOnClickListener(this);
        //     findViewById(R.id.txvtelfrnd).setOnClickListener(this);

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
                //  getActionBar().setTitle(mTitle);
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
        //  if (SharedPrefUtils.getAppUpgrade(this)) {
        // RateAndUpdateDialog dialogUpgrade = new RateAndUpdateDialog();
        // dialogUpgrade.newInstance(this, DialogEnum.UPDATE_DIALOG, this);
        // dialogUpgrade.show(getSupportFragmentManager(), "");

//            new rate us app @manish


        RateVersion reteVersionModel = SharedPrefUtils.getRateVersion(this);
        int currentRateVersion = reteVersionModel.getAppRateVersion();
        currentRateVersion++;
        boolean isCompleteRateProcess = reteVersionModel.isAppRateComplete();
        RateVersion rateModel = new RateVersion();
        rateModel.setAppRateComplete(isCompleteRateProcess);
        rateModel.setAppRateVersion(currentRateVersion);
        SharedPrefUtils.setAppRateVersion(this, rateModel);
        if (!SharedPrefUtils.getRateVersion(this).isAppRateComplete() && currentRateVersion >= 3) {
            RateAppDialogFragment rateAppDialogFragment = new RateAppDialogFragment();
            reteVersionModel.setAppRateVersion(-7);
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
                    setTitle(getTodayTime());
                    mDrawerToggle.setDrawerIndicatorEnabled(true);

                } else if (currentFrag instanceof FragmentMC4KHome) {
                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
//
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    changeVisibiltyOfArrow(false);
                    setTitle(getTodayTime());

                } else if (currentFrag instanceof FragmentCalender) {

                    findViewById(R.id.task_popup).setVisibility(View.GONE);


                    changeVisibiltyOfArrow(true);
                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
//                    setTitle("Calender");
                } else if (currentFrag instanceof FragmentCalMonth) {

                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    findViewById(R.id.month_popup).setVisibility(View.GONE);

                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
                    changeVisibiltyOfArrow(true);
//                    setTitle("Calender");
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
                    mDrawerToggle.setHomeAsUpIndicator(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                    changeVisibiltyOfArrow(false);
                } else if (currentFrag instanceof FragmentFamilyProfile) {

                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.setHomeAsUpIndicator(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
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
                    mDrawerToggle.setHomeAsUpIndicator(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                    changeVisibiltyOfArrow(false);
                } else if (currentFrag instanceof FragmentKidProfile) {

                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.setHomeAsUpIndicator(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                    changeVisibiltyOfArrow(false);
                } else if (currentFrag instanceof FragmentTaskHome) {

                    changeVisibiltyOfArrow(true);
                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
//                    if (findViewById(R.id.task_popup).getVisibility() == View.VISIBLE) {
//                        findViewById(R.id.task_popup).setVisibility(View.GONE);
//                    } else {
//                        findViewById(R.id.task_popup).setVisibility(View.VISIBLE);
//                    }
                    setTitle("All Tasks");

                } else if (currentFrag instanceof ParentingBlogFragment) {

                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.setHomeAsUpIndicator(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                    changeVisibiltyOfArrow(false);
                    setTitle("Meet Our Contributors");

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
                    if (SharedPrefUtils.getCurrentCityModel(DashboardActivity.this).getName().isEmpty())
                    {
                        switch (SharedPrefUtils.getCurrentCityModel(DashboardActivity.this).getId()) {
                            case 1:
                                setTitle("Best of " +"Delhi-Ncr");
                                break;
                            case 2:
                                setTitle("Best of " +"Bangalore");
                                break;
                            case 3:
                                setTitle("Best of " +"Mumbai");
                                break;
                            case 4:
                                setTitle("Best of " +"Pune");
                                break;
                            case 5:
                                setTitle("Best of " +"Hyderabad");
                                break;
                            case 6:
                                setTitle("Best of " +"Chennai");
                                break;
                            case 7:
                                setTitle("Best of " +"Kolkata");
                                break;
                            case 8:
                                setTitle("Best of " +"Jaipur");
                                break;
                            case 9:
                                setTitle("Best of " +"Ahmedabad");
                                break;
                            default:
                                setTitle("Best of " +"Delhi-Ncr");
                                break;
                        }

                    } else {
                        setTitle("Best of "+SharedPrefUtils.getCurrentCityModel(DashboardActivity.this).getName());}


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
                    mDrawerToggle.setHomeAsUpIndicator(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                    changeVisibiltyOfArrow(false);
                } else if (currentFrag instanceof ChangeCityFragment) {
                    setTitle("Change City");
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    mDrawerToggle.setDrawerIndicatorEnabled(false);

                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.setHomeAsUpIndicator(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                    changeVisibiltyOfArrow(false);
                } else if (currentFrag instanceof ExternalCalFragment) {
                    setTitle("External Calendars");

                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    mDrawerToggle.setDrawerIndicatorEnabled(false);

                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.setHomeAsUpIndicator(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                    changeVisibiltyOfArrow(false);
                } else if (currentFrag instanceof SyncSettingFragment) {
                    setTitle("Sync Settings");
                    findViewById(R.id.month_popup).setVisibility(View.GONE);
                    findViewById(R.id.task_popup).setVisibility(View.GONE);
                    mDrawerToggle.setDrawerIndicatorEnabled(false);

                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.setHomeAsUpIndicator(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
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
/*
    private String getTimingCategory() {
        return ((EditText) getView().findViewById(R.id.editTimingCategory)).getText().toString().trim();
    }

    private long getTimingInterval() {
        String value =
                ((EditText) getView().findViewById(R.id.editTimingInterval)).getText().toString().trim();
        if (value.length() == 0) {
            return 0;
        }
        return Long.valueOf(value);
    }

    private String getTimingName() {
        return ((EditText) getView().findViewById(R.id.editTimingName)).getText().toString().trim();
    }

    private String getTimingLabel() {
        return ((EditText) getView().findViewById(R.id.editTimingLabel)).getText().toString().trim();
    }
*/


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

//        if (findViewById(R.id.month_popup).getVisibility() == View.VISIBLE)
//            findViewById(R.id.month_popup).setVisibility(View.GONE);
//
//        if (findViewById(R.id.task_popup).getVisibility() == View.VISIBLE)
//            findViewById(R.id.task_popup).setVisibility(View.GONE);

        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (topFragment instanceof FragmentCalender)
            ((FragmentCalender) topFragment).hideFilter();

        if (topFragment instanceof FragmentCalMonth)
            ((FragmentCalMonth) topFragment).hideFilter();
        return ret;
    }

    public void updateImageProfile() {
        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(this))) {
            Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(profileImage);
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

    public void notiftTaskinFragment() {

        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (topFragment instanceof FragmentTaskHome) {

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
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateImageProfile();
        final Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        findViewById(R.id.month_popup).setVisibility(View.GONE);
        findViewById(R.id.task_popup).setVisibility(View.GONE);
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
        } else if (topFragment instanceof ParentingBlogFragment) {
            refreshMenu();
            setTitle("Meet Our Contributors");
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

        // register gcm broadcast
//        LocalBroadcastManager.getInstance(this).registerReceiver(mGcmUpdate,
//                new IntentFilter(Constants.LOCAL_BROADCAST_GCM));


    }

//    private BroadcastReceiver mGcmUpdate = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//
//            if (!SharedPrefUtils.getPushTokenUpdateToServer(DashboardActivity.this)) {
//
//                if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getDeviceToken(DashboardActivity.this))) {
//
//                    // hit api
//                    if (SharedPrefUtils.getUserDetailModel(context).getId() > 0) {
//                        PushTokenController controller = new PushTokenController(DashboardActivity.this, DashboardActivity.this);
//                        controller.getData(AppConstants.PUSH_TOKEN_REQUEST, "");
//
//                    }
//                } else {
//                    GCMUtil.initializeGCM(DashboardActivity.this);
//                }
//
//            }
//
//
//        }
//    };

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
        if (topFragment instanceof FragmentCityForKids) {

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
        } else if (topFragment instanceof ParentingBlogFragment) {
            getMenuInflater().inflate(R.menu.blog_menu, menu);
        } else if (topFragment instanceof NotificationFragment) {
            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof FragmentHomeCategory) {
            getMenuInflater().inflate(R.menu.kidsresource_listing, menu);
        }

        return true;
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
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
                    Intent intent = new Intent(getApplicationContext(), ArticleFilterDialogActivity.class);
                    startActivityForResult(intent, Constants.FILTER_ARTICLE);
                } else if (topFragment instanceof ParentingBlogFragment) {
                    Intent intent = new Intent(getApplicationContext(), BlogFilterActivity.class);
                    startActivityForResult(intent, Constants.FILTER_BLOG);
                }
                break;
            case R.id.search:
                if (topFragment instanceof ArticlesFragment) {
//                    ((FragmentBusinesslistEvents) topFragment).toggleFilter();
                    Intent intent = new Intent(getApplicationContext(), SearchArticlesAndAuthorsActivity.class);
                    intent.putExtra(Constants.FILTER_NAME, "");
                    intent.putExtra(Constants.TAB_POSITION, 0);
                    startActivity(intent);
                } else if (topFragment instanceof ParentingBlogFragment) {
//                    ((FragmentBusinesslistEvents) topFragment).toggleFilter();
                    Intent intent = new Intent(getApplicationContext(), SearchArticlesAndAuthorsActivity.class);
                    intent.putExtra(Constants.FILTER_NAME, "");
                    intent.putExtra(Constants.TAB_POSITION, 1);
                    startActivity(intent);
                }
                break;
            case R.id.three_bar:

                replaceFragment(new FragmentCalender(), null, true);

//                commented by manish
//                if (topFragment instanceof FragmentCalMonth) {
//                    try {
//                        ((FragmentCalMonth) topFragment).resetFilter();
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
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


//    private void rateAppHandling() {
//
//        RateVersion reteVersionModel = SharedPrefUtils.getRateVersion(this);
//        int currentRateVersion = reteVersionModel.getAppRateVersion();
//        currentRateVersion++;
//        boolean isCompleteRateProcess = reteVersionModel.isAppRateComplete();
//        RateVersion rateModel = new RateVersion();
//        rateModel.setAppRateComplete(isCompleteRateProcess);
//        rateModel.setAppRateVersion(currentRateVersion);
//        SharedPrefUtils.setAppRateVersion(this, rateModel);
//        if (!SharedPrefUtils.getRateVersion(this).isAppRateComplete() && currentRateVersion >= 3) {
//            RateAndUpdateDialog dialog = new RateAndUpdateDialog();
//            dialog.newInstance(this, DialogEnum.RAME_ME_DIALOG, this);
//            dialog.show(getSupportFragmentManager(), "");
//        }
//    }


    protected void updateUi(Response response) {

        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        switch (response.getDataType()) {
            case AppConstants.CONFIGURATION_REQUEST:
                removeProgressDialog();
                Object responseObject = response.getResponseObject();
                if (responseObject instanceof ConfigurationApiModel) {
                    ConfigurationApiModel _configurationResponse = (ConfigurationApiModel) responseObject;

                    /**
                     * Save data into tables :-
                     */
                    HeavyDbTask _heavyDbTask = new HeavyDbTask(this,
                            _configurationResponse, new OnUIView() {

                        @Override
                        public void comeBackOnUI() {
                            Log.i("Dashboard", "Configuration Data Updated");
//                            navigateToNextScreen(true);
                        }
                    });
                    _heavyDbTask.execute();

                }
                break;

            case AppConstants.PUSH_TOKEN_REQUEST:
                responseObject = response.getResponseObject();
                if (responseObject instanceof CommonResponse) {
                    CommonResponse commonResponse = (CommonResponse) responseObject;
                    if (commonResponse.getResponseCode() == 200) {

                        SharedPrefUtils.setPushTokenUpdateToServer(this, true);
                        Log.e("push", "token updated");

                    } else {
                        Log.e("push", "token failed");
                    }


                }
                break;


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
//
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

            case AppConstants.ARTICLE_BLOG_FOLLOW_REQUEST:
                CommonResponse followData = (CommonResponse) response.getResponseObject();
                removeProgressDialog();
                if (followData.getResponseCode() == 200) {
                    ToastUtils.showToast(getApplicationContext(), followData.getResult().getMessage(), Toast.LENGTH_SHORT);
                    if (BuildConfig.DEBUG) {
                        Log.e("follow response", followData.getResult().getMessage());
                    }

                    if (topFragment instanceof ParentingBlogFragment) {
                        ((ParentingBlogFragment) topFragment).updateList_followBtn(blogListPosition);
                    }

                } else if (followData.getResponseCode() == 400) {
                    String message = followData.getResult().getMessage();
                    if (BuildConfig.DEBUG) {
                        Log.e("follow response", followData.getResult().getMessage());
                    }
                    if (!StringUtils.isNullOrEmpty(message)) {
                        showToast(message);
                    } else {
                        showToast(getString(R.string.went_wrong));
                    }
                }
                break;

            case AppConstants.DEEP_LINK_RESOLVER_REQUEST:
                removeProgressDialog();
                Object result = response.getResponseObject();
                if (result instanceof DeepLinkApiModel) {
                    DeepLinkApiModel _deepLinkResponse = (DeepLinkApiModel) result;
                    if (_deepLinkResponse != null && _deepLinkResponse.getResult() != null
                            && _deepLinkResponse.getResult().getData() != null) {
                        identifyTargetScreen(_deepLinkResponse.getResult().getData());
                    }
                }
                break;

        }

    }

    @Override
    public void getDialogTypeAndEvent(DialogEnum enumType, DialogButtonEvent type) {
//        switch (enumType) {
//            case RAME_ME_DIALOG:
//                RateVersion rateModel = new RateVersion();
//                switch (type) {
//
//                    case RATE_ME_OR_INSTALL:
//                        rateModel.setAppRateComplete(true);
//                        rateModel.setAppRateVersion(0);
//                        SharedPrefUtils.setAppRateVersion(this, rateModel);
//                        /**
//                         * a try/catch block here because an Exception will be thrown if the Play Store is not installed on the target device.
//                         */
//                        String appPackage = getPackageName();
//                        try {
//                            Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
//                            startActivity(rateIntent);
//                        } catch (Exception e) {
//                            Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
//                            startActivity(rateIntent);
//                        }
//                        break;
//
//                    case LATER:
//                        rateModel.setAppRateComplete(false);
//                        rateModel.setAppRateVersion(0);
//                        SharedPrefUtils.setAppRateVersion(this, rateModel);
//                        break;
//                }
//
//                break;
//            case UPDATE_DIALOG:
//                switch (type) {
//                    case RATE_ME_OR_INSTALL:
//                        String appPackage = getPackageName();
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
//                        startActivity(intent);
//
//                        break;
//
//                    case LATER:
//                        //do nothing.
//                        break;
//                }
//                /**
//                 * if App Upgrade dialog will open
//                 * then after App Upgrade it will open.
//                 * oterwise on onCreate it will handle.
//                 */
//                rateAppHandling();
//
//                break;
//            default:
//                break;
//        }

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
//                    refreshMenu();
                }

                break;


            case R.id.rdBtnToday:
                changeVisibiltyOfArrow(false);
                Utils.pushEvent(DashboardActivity.this, GTMEventType.MC4KToday_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Left Menu Screen");
                //TableAppointmentData data = new TableAppointmentData(BaseApplication.getInstance());
                //TableTaskData taskData = new TableTaskData(BaseApplication.getInstance());
                //int count = data.getRowsCount() + taskData.getRowsCount();
                //if (count > 0) {
                replaceFragment(new FragmentMC4KHome(), null, false);
                setTitle(getTodayTime());

                break;

            case R.id.rdBtnCalender:
                // title show current month
                Utils.pushEvent(DashboardActivity.this, GTMEventType.CALENDAR_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Left Menu Screen");
                if (StringUtils.isNullOrEmpty("" + SharedPrefUtils.getUserDetailModel(this).getFamily_id()) ||
                        SharedPrefUtils.getUserDetailModel(this).getFamily_id() == 0) {
                    showCreateFamilyAlert();
                } else {
                    changeVisibiltyOfArrow(true);
                    Calendar c = Calendar.getInstance();
                    setTitle(form.format(c.getTime()).toString());

                    replaceFragment(new FragmentCalender(), null, true);
                }

                //  startActivity(new Intent(this,ActivityCreateAppointment.class));

                break;/*
            case R.id.rdBtnTodo:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.TODO_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "");
            if (StringUtils.isNullOrEmpty("" + SharedPrefUtils.getUserDetailModel(this).getFamily_id()) ||
                    SharedPrefUtils.getUserDetailModel(this).getFamily_id() == 0) {
                showCreateFamilyAlert();
            } else {
                replaceFragment(new FragmentTaskHome(), null, true);
                setTitle("All Tasks");
                SharedPrefUtils.setTaskListID(DashboardActivity.this, 0);
                taskIconFlag = false;
                refreshMenu();
            }
            break;*/
            case R.id.rdBtnUpcoming:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.UPCOMING_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Left Menu Screen");
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
                Utils.pushEvent(DashboardActivity.this, GTMEventType.RESOURCES_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Left Menu Screen");
                Constants.IS_SEARCH_LISTING = false;
                changeVisibiltyOfArrow(false);
                setTitle("Kids Resources");
                replaceFragment(new FragmentHomeCategory(), null, true);
                break;
            case R.id.rdBtnParentingBlogs:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.BLOGS_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Left Menu Screen");
                changeVisibiltyOfArrow(false);
                setTitle("Articles");
                replaceFragment(new ArticlesFragment(), null, true);
                break;
            case R.id.editor:
                if (Build.VERSION.SDK_INT > 15) {
                    Utils.pushEvent(DashboardActivity.this, GTMEventType.ADD_BLOG_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Left Menu Screen");
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
                } else {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse("http://www.mycity4kids.com/parenting/admin/setupablog"));
                    startActivity(viewIntent);
                }
                break;
            case R.id.drafts:
                Intent intent5 = new Intent(DashboardActivity.this, DraftListViewActivity.class);
              /*  Bundle bundle5 = new Bundle();
                bundle5.putString(EditorPostActivity.TITLE_PARAM, "");
                bundle5.putString(EditorPostActivity.CONTENT_PARAM, "");
                bundle5.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                        getString(R.string.example_post_title_placeholder));
                bundle5.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                        getString(R.string.example_post_content_placeholder));
                bundle5.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
                intent1.putExtras(bundle5);*/
                startActivity(intent5);
                break;
            case R.id.bloggerDashboard:
                Intent intent = new Intent(DashboardActivity.this, BloggerDashboardActivity.class);
                startActivity(intent);
                break;
            case R.id.txvSettings:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.SETTINGS_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Left Menu Screen");
                changeVisibiltyOfArrow(false);
                setTitle("Settings");
                replaceFragment(new FragmentSetting(), null, true);
                break;
            case R.id.txvMeetContributors:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.MEETCONTRIBUTORS_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Left Menu Screen");
                changeVisibiltyOfArrow(false);
                replaceFragment(new ParentingBlogFragment(), null, true);
                break;

           /* case R.id.txvHelp:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.HELP_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId()+"", "");
                Intent intent = new Intent(DashboardActivity.this, LoadWebViewActivity.class);
                intent.putExtra(Constants.WEB_VIEW_URL, "http://www.mycity4kids.com/mobile#faq");
                startActivity(intent);
//                changeVisibiltyOfArrow(false);

                break;*/

            case R.id.txvfeedback:
                Intent intentEmail = new Intent(Intent.ACTION_SEND);
                String[] recipients = {"feedback@mycity4kids.com"};
                intentEmail.putExtra(Intent.EXTRA_EMAIL, recipients);
                intentEmail.putExtra(Intent.EXTRA_SUBJECT, "mycity4kids mobile app");
                //intentEmail.putExtra(Intent.EXTRA_TEXT,"I just downloaded the amazing mycity4kids mobile app. Check it out @: http://www.mycity4kids.com/mobile ");
                //intentEmail.putExtra(Intent.EXTRA_CC,"ghi");
                intentEmail.setType("text/html");
                startActivity(Intent.createChooser(intentEmail, "Send mail").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;

            case R.id.txvrate:
                String appPackage = getPackageName();
                try {
                    Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
                    startActivity(rateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } catch (Exception e) {
                    Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
                    startActivity(rateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
                break;

            /*case R.id.txvtelfrnd:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.TELLFRIEND_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId()+"", "");
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "mycity4kids android app");
                String shareMessage = "I just downloaded the amazing mycity4kids mobile app. Check it out http://www.mycity4kids.com/mobile";
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "mycity4kids").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;*/

            case R.id.feed_back:
                Utils.pushEvent(DashboardActivity.this, GTMEventType.FEEDBACK_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Left Menu Screen");
                changeVisibiltyOfArrow(false);
                setTitle("Send Feedback");
                replaceFragment(new SendFeedbackFragment(), null, true);

                break;

            case R.id.txvUserName:

                startActivity(new Intent(this, ActivitySignUp.class));
                break;

            case R.id.imgProfile:
                Intent intent4 = new Intent(DashboardActivity.this, BloggerDashboardActivity.class);
                startActivity(intent4);
                //  replaceFragment(new FragmentFamilyDetail(), null, true);
                break;

            case R.id.back_month:
//                setTitle();
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
//                        ((FragmentCalender) topFragment).updateListbyDay(0, Integer.parseInt(String.valueOf(year.getText())));
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
                        ((FragmentFamilyDetail) topFragment).onActivityResultDelegate(requestCode, resultCode, data);
                    }
                    break;
                case Constants.TAKE_PICTURE:
                    if (topFragment instanceof FragmentFamilyProfile) {
                        topFragment.onActivityResult(requestCode, resultCode, data);
                    } else if (topFragment instanceof FragmentFamilyDetail) {
                        ((FragmentFamilyDetail) topFragment).onActivityResultDelegate(requestCode, resultCode, data);
                    }
                    break;

                case Constants.CROP_IMAGE:
                    if (topFragment instanceof FragmentFamilyProfile) {
                        topFragment.onActivityResult(requestCode, resultCode, data);
                    } else if (topFragment instanceof FragmentFamilyDetail) {
                        ((FragmentFamilyDetail) topFragment).onActivityResultDelegate(requestCode, resultCode, data);
                    }
                    break;
                case Constants.CREATE_TASK:
                    if (topFragment instanceof FragmentTaskHome) {
                        ((FragmentTaskHome) topFragment).refreshTaskList();
                        notiftTaskList();
                    }
                    break;

                case Constants.FILTER_ARTICLE:

                    if (data.getBooleanExtra(Constants.IS_MEET_CONTRIBUTORS_SELECTED, false)) {

                        replaceFragment(new ParentingBlogFragment(), null, true);
                    }
                case Constants.FILTER_BLOG:
                    if (topFragment instanceof ParentingBlogFragment) {
                        ((ParentingBlogFragment) topFragment).sortParentingBlogListing(data.getStringExtra(Constants.FILTER_BLOG_SORT_TYPE));
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
                case Constants.BLOG_FOLLOW_STATUS:
                    if (topFragment instanceof ParentingBlogFragment) {
                        ((ParentingBlogFragment) topFragment).updateList_followBtn(data.getIntExtra(Constants.BLOG_LIST_POSITION, 0));
                    }
                    break;

                default:
                    if (topFragment instanceof FragmentCalender) {
//                        ((FragmentCalender) topFragment).refreshView();
                    } else if (topFragment instanceof FragmentMC4KHome) {
                        ((FragmentMC4KHome) topFragment).refreshList();
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

    public void followAPICall_List(String id, int position) {

        blogListPosition = position;

        ArticleBlogFollowRequest _followRequest = new ArticleBlogFollowRequest();
        _followRequest.setSessionId("" + SharedPrefUtils.getUserDetailModel(getApplicationContext()).getSessionId());
        _followRequest.setUserId("" + SharedPrefUtils.getUserDetailModel(getApplicationContext()).getId());
        _followRequest.setAuthorId("" + id);
        ArticleBlogFollowController _followController = new ArticleBlogFollowController(this, this);
        showProgressDialog(getString(R.string.please_wait));
        _followController.getData(AppConstants.ARTICLE_BLOG_FOLLOW_REQUEST, _followRequest);

    }

    private void getDeepLinkData(String deepLinkURL) {
        DeepLinkingController _deepLinkingController = new DeepLinkingController(this, this);
        _deepLinkingController.getData(AppConstants.DEEP_LINK_RESOLVER_REQUEST, deepLinkURL);
        showProgressDialog("");
    }

    private void identifyTargetScreen(DeepLinkData data) {
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
        }
    }

    private void renderBusinessListingScreen(DeepLinkData data) {
        if (!StringUtils.isNullOrEmpty(data.getCategory_id())) {
            Intent _businessListIntent = new Intent(DashboardActivity.this, BusinessListActivityKidsResources.class);
            _businessListIntent.putExtra(Constants.EXTRA_CATEGORY_ID, Integer.parseInt(data.getCategory_id()));
            _businessListIntent.putExtra(Constants.CITY_ID_DEEPLINK, data.getCity_id() + "");
            _businessListIntent.putExtra(Constants.IS_FROM_DEEPLINK, true);
            _businessListIntent.putExtra(Constants.DEEPLINK_URL, data.getUrl());
            startActivity(_businessListIntent);
        }
    }

    private void renderBusinessDetailScreen(DeepLinkData data) {
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
    private void renderEventListingScreen(DeepLinkData data) {
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

    private void renderEventDetailScreen(DeepLinkData data) {
        if (!StringUtils.isNullOrEmpty(data.getCategory_id()) && !StringUtils.isNullOrEmpty(data.getDetail_id())) {
            Intent _eventDetailIntent = new Intent(DashboardActivity.this, BusinessDetailsActivity.class);
            _eventDetailIntent.putExtra(Constants.CATEGORY_ID, Integer.parseInt(data.getCategory_id()));
            _eventDetailIntent.putExtra(Constants.BUSINESS_OR_EVENT_ID, data.getDetail_id() + "");
            _eventDetailIntent.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
            _eventDetailIntent.putExtra(Constants.DEEPLINK_URL, data.getUrl());
            startActivity(_eventDetailIntent);
        }
    }

    private void renderAuthorListingScreen(DeepLinkData data) {
        if (!StringUtils.isNullOrEmpty(data.getAuthor_name())) {
            Intent _authorListIntent = new Intent(DashboardActivity.this, BlogDetailActivity.class);
            _authorListIntent.putExtra(Constants.IS_COMMING_FROM_LISTING, false);
            _authorListIntent.putExtra(Constants.ARTICLE_NAME, Uri.encode(data.getAuthor_name()) + "");
            _authorListIntent.putExtra(Constants.AUTHOR_ID, data.getAuthor_id());
            _authorListIntent.putExtra(Constants.FILTER_TYPE, "authors");
            _authorListIntent.putExtra(Constants.DEEPLINK_URL, data.getUrl());
            startActivity(_authorListIntent);
        }
    }

    private void renderBloggerListingScreen(DeepLinkData data) {
        if (!StringUtils.isNullOrEmpty(data.getBlog_title())) {
            Intent _bloggerListIntent = new Intent(DashboardActivity.this, BlogDetailActivity.class);
            _bloggerListIntent.putExtra(Constants.IS_COMMING_FROM_LISTING, false);
            _bloggerListIntent.putExtra(Constants.ARTICLE_NAME, Uri.encode(data.getBlog_title()) + "");
            _bloggerListIntent.putExtra(Constants.FILTER_TYPE, "blogs");
            _bloggerListIntent.putExtra(Constants.AUTHOR_ID, data.getAuthor_id());
            _bloggerListIntent.putExtra(Constants.DEEPLINK_URL, data.getUrl());
            startActivity(_bloggerListIntent);
        }
    }

    private void renderArticleDetailScreen(DeepLinkData data) {
        if (!StringUtils.isNullOrEmpty(data.getArticle_id())) {
            Intent _articleDetailIntent = new Intent(DashboardActivity.this, ArticlesAndBlogsDetailsActivity.class);
            _articleDetailIntent.putExtra(Constants.ARTICLE_ID, data.getArticle_id());
            _articleDetailIntent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
            _articleDetailIntent.putExtra(Constants.DEEPLINK_URL, data.getUrl());
            startActivity(_articleDetailIntent);
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
}
