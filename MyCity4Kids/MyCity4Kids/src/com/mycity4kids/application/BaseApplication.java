package com.mycity4kids.application;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger.LogLevel;
import com.google.analytics.tracking.android.Tracker;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.database.BaseDbHelper;
import com.mycity4kids.models.businesslist.BusinessDataListing;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.newmodels.parentingmodel.ArticleFilterListModel;

import io.fabric.sdk.android.Fabric;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


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
    private static final LogLevel GA_LOG_VERBOSITY = LogLevel.ERROR;

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


    public static ArrayList<BusinessDataListing> getBusinessREsponse() {
        return businessREsponse;
    }

    public static void setBusinessREsponse(ArrayList<BusinessDataListing> businessREsponse) {
        BaseApplication.businessREsponse = businessREsponse;
    }

    /*

         * Method to handle basic Google Analytics initialization. This call will
         * not block as all Google Analytics work occurs off the main thread.
         */
    @SuppressWarnings("deprecation")
    private void initializeGa() {
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
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

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
        initializeGa();

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
