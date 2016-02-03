package com.mycity4kids.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.database.BaseDbHelper;
import com.mycity4kids.models.businesslist.BusinessDataListing;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.newmodels.parentingmodel.ArticleFilterListModel;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * This class holds some application-global instances.
 */
public class BaseApplication extends Application {
    private final String LOG_TAG = "BaseApplication";

    private ArticleFilterListModel filterList;

    private SQLiteDatabase mWritableDatabase;

    private static BaseApplication mInstance;
    /*
     * Google Analytics configuration values.
     */
    private static GoogleAnalytics mGa;
    private static Tracker mTracker;
    public static String base_url;

    // Placeholder property ID.this was old which create by own account.
    //private static final String GA_PROPERTY_ID = "UA-50870780-1";

    private static final String GA_PROPERTY_ID = "UA-20533582-2";

    // Dispatch period in seconds.
    private static final int GA_DISPATCH_PERIOD = 30;

    // Prevent hits from being sent to reports, i.e. during testing.
    //private static final boolean GA_IS_DRY_RUN = false;

    // GA Logger.
/*    private static final LogLevel GA_LOG_VERBOSITY = LogLevel.ERROR;*/

    // Key used to store a user's tracking preferences in SharedPreferences.
    private static final String TRACKING_PREF_KEY = "trackingPreference";

    private static ArrayList<BusinessDataListing> businessREsponse;

    public static ArrayList<CommonParentingList> getBlogResponse() {
        return blogResponse;
    }

    public static void setBlogResponse(ArrayList<CommonParentingList> blogResponse) {
        BaseApplication.blogResponse = blogResponse;
    }

    private static ArrayList<CommonParentingList> blogResponse;
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER,// Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public static ArrayList<BusinessDataListing> getBusinessREsponse() {
        return businessREsponse;
    }

    public static void setBusinessREsponse(ArrayList<BusinessDataListing> businessREsponse) {
        BaseApplication.businessREsponse = businessREsponse;
    }
    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
 /*   synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }*/

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {


            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ?
                    analytics.newTracker(R.xml.app_tracker)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(GA_PROPERTY_ID)
                    : analytics.newTracker(R.xml.app_tracker);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }
    /*

         * Method to handle basic Google Analytics initialization. This call will
         * not block as all Google Analytics work occurs off the main thread.
         */
    @SuppressWarnings("deprecation")
   /* private void initializeGa() {
        mGa = GoogleAnalytics.getInstance(this);
        mTracker = mGa.getTracker(GA_PROPERTY_ID);

        // Set dispatch period.
        GAServiceManager.getInstance().setLocalDispatchPeriod(GA_DISPATCH_PERIOD);


        // Set dryRun flag.
        // mGa.setDryRun(GA_IS_DRY_RUN);

        // Set Logger verbosity.
        mGa.getLogger().setLogLevel(GA_LOG_VERBOSITY);

        // Set the opt out flag when user updates a tracking preference.
        SharedPreferences userPrefs = PreferenceManager.getDefaultSharedPreferences(this);


        userPrefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(
                    SharedPreferences sharedPreferences, String key) {
                if (key.equals(TRACKING_PREF_KEY)) {
                    GoogleAnalytics.getInstance(getApplicationContext()).setAppOptOut(sharedPreferences.getBoolean(key, false));

                }
            }
        });
    }*/


    /*
     * Returns the Google Analytics tracker.
     */
    public static Tracker getGaTracker() {
        return mTracker;
    }

    /*
     * Returns the Google Analytics instance.
     */
    public static GoogleAnalytics getGaInstance() {
        return mGa;
    }

    public static Context getAppContext() {
        return getInstance();
    }

    public static BaseApplication getInstance() {
        return mInstance;
    }

    protected static void setInstance(BaseApplication mInstance) {
        BaseApplication.mInstance = mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        setInstance(this);
        //initializeGa();
        // startService(new Intent(this,ReplicationService.class))
        // For Google Analytics initialization.

        Log.i(LOG_TAG, "onCreate()");
    }

    /**
     * Get the database instance.
     *
     * @return mWritableDatabase
     */
    public SQLiteDatabase getWritableDbInstance() {
        if (mWritableDatabase == null) {
            BaseDbHelper dbHelper = new BaseDbHelper(this);
            mWritableDatabase = dbHelper.getWritableDatabase();
        }
        return mWritableDatabase;
    }

    @Override
    public void onTerminate() {
        Log.i(LOG_TAG, "onTerminate()");
        if (mWritableDatabase != null) {
            mWritableDatabase.close();
        }
        super.onTerminate();
    }

    public void setFilterList(ArticleFilterListModel list) {
        filterList = list;
    }

    public ArticleFilterListModel getFilterList() {
        return filterList;
    }
}
