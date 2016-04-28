package com.mycity4kids.application;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.kelltontech.utils.ConnectivityUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.database.BaseDbHelper;
import com.mycity4kids.models.businesslist.BusinessDataListing;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.newmodels.parentingmodel.ArticleFilterListModel;
import com.mycity4kids.preference.SharedPrefUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * This class holds some application-global instances.
 */
public class BaseApplication extends Application {
    private final String LOG_TAG = "BaseApplication";
    public static final String TAG = BaseApplication.class.getName();
    private ArticleFilterListModel filterList;

    private SQLiteDatabase mWritableDatabase;

    private static BaseApplication mInstance;
    private Retrofit retrofit;
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
        // workaround for http://code.google.com/p/android/issues/detail?id=20915
        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
        }
        Fabric.with(this, new Crashlytics());
        Crashlytics.setUserIdentifier("" + SharedPrefUtils.getUserDetailModel(this).getId());
        Crashlytics.setUserEmail("" + SharedPrefUtils.getUserDetailModel(this).getEmail());
        setInstance(this);
        createRetrofitInstance();
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

    public Retrofit createRetrofitInstance() {
        OkHttpClient client = new OkHttpClient
                .Builder()
                .cache(new Cache(getCacheDir(), 10 * 1024 * 1024)) // 10 MB
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        if (ConnectivityUtils.isNetworkEnabled(getApplicationContext())) {
                            request = request.newBuilder().header("Cache-Control", "public, max-age=" + 60).build();
                        } else {
                            request = request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build();
                        }
                        return chain.proceed(request);
                    }
                })
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }

    public Retrofit getRetrofit() {
        if (null == retrofit) {
            createRetrofitInstance();
        }
        return retrofit;
    }
}
