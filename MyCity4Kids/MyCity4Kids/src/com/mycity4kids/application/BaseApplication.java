package com.mycity4kids.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.comscore.analytics.comScore;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.database.BaseDbHelper;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.businesslist.BusinessDataListing;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.newmodels.parentingmodel.ArticleFilterListModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.LocaleManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
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
    private RequestQueue mRequestQueue;
    private static BaseApplication mInstance;
    private static Retrofit retrofit, customTimeoutRetrofit, groupsRetrofit;
    private static OkHttpClient client, customTimeoutOkHttpClient;

    private static ArrayList<Topics> topicList;
    private static ArrayList<Topics> shortStoryTopicList;
    private static HashMap<Topics, List<Topics>> topicsMap;
    private static HashMap<String, Topics> selectedTopicsMap;

    /*
     * Google Analytics configuration values.
     */
    private static GoogleAnalytics mGa;
    private static Tracker mTracker;
    public static String base_url;
    public String appVersion;

    public static boolean isFirstSwipe = true;

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

    public static ArrayList<ArticleListingResult> getBestCityResponse() {
        return bestCityResponse;
    }

    public static void setBestCityResponse(ArrayList<ArticleListingResult> bestCityResponse) {
        BaseApplication.bestCityResponse = bestCityResponse;
    }

    private static ArrayList<ArticleListingResult> bestCityResponse;

    public static ArrayList<Topics> getTopicList() {
        return topicList;
    }

    public static void setTopicList(ArrayList<Topics> topicLists) {
        topicList = topicLists;
    }

    public static ArrayList<Topics> getShortStoryTopicList() {
        return shortStoryTopicList;
    }

    public static void setShortStoryTopicList(ArrayList<Topics> shortStoryTopicList) {
        BaseApplication.shortStoryTopicList = shortStoryTopicList;
    }

    public static HashMap<Topics, List<Topics>> getTopicsMap() {
        return topicsMap;
    }

    public static void setTopicsMap(HashMap<Topics, List<Topics>> topicsMaps) {
        topicsMap = topicsMaps;
    }

    public static HashMap<String, Topics> getSelectedTopicsMap() {
        return selectedTopicsMap;
    }

    public static void setSelectedTopicsMap(HashMap<String, Topics> selectedTopicsMap) {
        BaseApplication.selectedTopicsMap = selectedTopicsMap;
    }

    private static boolean hasLanguagePreferrenceChanged = false;

    public static boolean isHasLanguagePreferrenceChanged() {
        return hasLanguagePreferrenceChanged;
    }

    public static void setHasLanguagePreferrenceChanged(boolean hasLanguagePreferrenceChanged) {
        BaseApplication.hasLanguagePreferrenceChanged = hasLanguagePreferrenceChanged;
    }

    public static boolean isFirstSwipe() {
        return isFirstSwipe;
    }

    public static void setFirstSwipe(boolean firstSwipe) {
        isFirstSwipe = firstSwipe;
    }

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

        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

//        Fabric.with(this, new Crashlytics.Builder().build());
        Crashlytics.setUserIdentifier("" + SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        Crashlytics.setUserEmail("" + SharedPrefUtils.getUserDetailModel(this).getEmail());

        setInstance(this);
        VolleyLog.setTag("VolleyLogs");

        Fresco.initialize(this);
        createRetrofitInstance(AppConstants.LIVE_URL);

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
// Initialize comScore Application Tag library
        comScore.setAppContext(this.getApplicationContext());
        // Include any of the comScore Application Tag library initialization settings here.
        comScore.setCustomerC2("18705325");
        comScore.setPublisherSecret("6116f207ac5e9f9226f6b98e088a22ea");
        //initializeGa();
        // startService(new Intent(this,ReplicationService.class))
        // For Google Analytics initialization.
//        userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
        AudienceNetworkAds.initialize(this);
// Branch logging for debugging
        Branch.enableLogging();
        // Branch object initialization
        Branch.getAutoInstance(this);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        appVersion = pInfo.versionName;

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
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

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public void add(com.android.volley.Request req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancel() {
        mRequestQueue.cancelAll(TAG);
    }

    public ArticleFilterListModel getFilterList() {
        return filterList;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
        MultiDex.install(this);
        Log.d(TAG, "attachBaseContext");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
        LocaleManager.setNewLocale(this, newConfig.locale.getLanguage());
        Log.d(TAG, "onConfigurationChanged: " + newConfig.locale.getLanguage());
    }

    public Retrofit createRetrofitInstance(String base_url) {

        Interceptor mainInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();
                Request.Builder requestBuilder = original.newBuilder();

                requestBuilder.header("Accept-Language", Locale.getDefault().getLanguage());
                requestBuilder.addHeader("id", SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId());
                requestBuilder.addHeader("mc4kToken", SharedPrefUtils.getUserDetailModel(getApplicationContext()).getMc4kToken());
                requestBuilder.addHeader("agent", "android");
                requestBuilder.addHeader("manufacturer", Build.MANUFACTURER);
                requestBuilder.addHeader("model", Build.MODEL);
                requestBuilder.addHeader("appVersion", appVersion);
                requestBuilder.addHeader("latitude", SharedPrefUtils.getUserLocationLatitude(getApplicationContext()));
                requestBuilder.addHeader("longitude", SharedPrefUtils.getUserLocationLongitude(getApplicationContext()));
                requestBuilder.addHeader("userPrint", "" + AppUtils.getDeviceId(getApplicationContext()));
                Request request = requestBuilder.build();

//                Response response = chain.proceed(request);
//                Log.w("Retrofit@Response", response.body().string());
                return chain.proceed(request);
            }

        };

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        if (BuildConfig.DEBUG) {
            client = new OkHttpClient
                    .Builder()
                    .addInterceptor(mainInterceptor)
                    .addInterceptor(logging)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
        } else {
            client = new OkHttpClient
                    .Builder()
                    .addInterceptor(mainInterceptor)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
        }

//        retrofit = new Retrofit.Builder()
//                .baseUrl(base_url)
//                .addConverterFactory(buildGsonConverter())
//                .client(client)
//                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://35.200.209.192:5000/rewards/")
                //.addConverterFactory(buildGsonConverter())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }

    public Retrofit createGroupRetrofitInstance(String base_url) {

        Interceptor mainInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();
                Request.Builder requestBuilder = original.newBuilder();

                requestBuilder.header("Accept-Language", Locale.getDefault().getLanguage());
                requestBuilder.addHeader("id", SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId());
                requestBuilder.addHeader("mc4kToken", SharedPrefUtils.getUserDetailModel(getApplicationContext()).getMc4kToken());
                requestBuilder.addHeader("agent", "android");
                requestBuilder.addHeader("manufacturer", Build.MANUFACTURER);
                requestBuilder.addHeader("model", Build.MODEL);
                requestBuilder.addHeader("appVersion", appVersion);
                requestBuilder.addHeader("latitude", SharedPrefUtils.getUserLocationLatitude(getApplicationContext()));
                requestBuilder.addHeader("longitude", SharedPrefUtils.getUserLocationLongitude(getApplicationContext()));
                requestBuilder.addHeader("userPrint", "" + AppUtils.getDeviceId(getApplicationContext()));
                Request request = requestBuilder.build();

//                Response response = chain.proceed(request);
//                Log.w("Retrofit@Response", response.body().string());
                return chain.proceed(request);
            }

        };

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        if (BuildConfig.DEBUG) {
            client = new OkHttpClient
                    .Builder()
                    .addInterceptor(mainInterceptor)
                    .addInterceptor(logging)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
        } else {
            client = new OkHttpClient
                    .Builder()
                    .addInterceptor(mainInterceptor)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
        }

        groupsRetrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(buildGsonConverter())
                .client(client)
                .build();
        return groupsRetrofit;
    }

    private static GsonConverterFactory buildGsonConverter() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Adding custom deserializers
        gsonBuilder.registerTypeAdapterFactory(new ArrayAdapterFactory());
        Gson myGson = gsonBuilder.create();

        return GsonConverterFactory.create(myGson);
    }

    public void destroyRetrofitInstance() {
        retrofit = null;
    }

    public Retrofit getRetrofit() {
        if (null == retrofit) {
            createRetrofitInstance(SharedPrefUtils.getBaseURL(this));
        }
        return retrofit;
    }

    public Retrofit getGroupsRetrofit() {
        if (null == groupsRetrofit) {
            createGroupRetrofitInstance(AppConstants.GROUPS_LIVE_URL);
        }
        return groupsRetrofit;
    }

    public void toggleGroupBaseURL() {

        if (HttpUrl.parse(AppConstants.GROUPS_TEST_LIVE_URL).equals(groupsRetrofit.baseUrl())) {
            groupsRetrofit = null;
            createGroupRetrofitInstance(AppConstants.GROUPS_TEST_STAGING_URL);
            Toast.makeText(this, "switch to staging", Toast.LENGTH_SHORT).show();
        } else {
            groupsRetrofit = null;
            createGroupRetrofitInstance(AppConstants.GROUPS_TEST_LIVE_URL);
            Toast.makeText(this, "switch to live", Toast.LENGTH_SHORT).show();
        }

    }

    public static void changeApiBaseUrl() {

        if (AppConstants.LIVE_URL.equals(retrofit.baseUrl().toString())) {
            SharedPrefUtils.setBaseURL(getAppContext(), AppConstants.DEV_URL);
            retrofit = new Retrofit.Builder()
                    .baseUrl(AppConstants.STAGING_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        } else {
            SharedPrefUtils.setBaseURL(getAppContext(), AppConstants.LIVE_URL);
            retrofit = new Retrofit.Builder()
                    .baseUrl(AppConstants.LIVE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
    }

    public Retrofit getConfigurableTimeoutRetrofit(int timeout) {
        if (null == customTimeoutRetrofit) {
            createCustomTimeoutRetrofitInstance(SharedPrefUtils.getBaseURL(this), timeout);
        }
        return customTimeoutRetrofit;
    }

    public Retrofit createCustomTimeoutRetrofitInstance(String base_url, int timeout) {

        Interceptor mainInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();
                Request.Builder requestBuilder = original.newBuilder();

                requestBuilder.header("Accept-Language", Locale.getDefault().getLanguage());
                requestBuilder.addHeader("id", SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId());
                requestBuilder.addHeader("mc4kToken", SharedPrefUtils.getUserDetailModel(getApplicationContext()).getMc4kToken());
                requestBuilder.addHeader("agent", "android");
                requestBuilder.addHeader("manufacturer", Build.MANUFACTURER);
                requestBuilder.addHeader("model", Build.MODEL);
                requestBuilder.addHeader("appVersion", appVersion);
                requestBuilder.addHeader("latitude", SharedPrefUtils.getUserLocationLatitude(getApplicationContext()));
                requestBuilder.addHeader("longitude", SharedPrefUtils.getUserLocationLongitude(getApplicationContext()));
                requestBuilder.addHeader("userPrint", "" + AppUtils.getDeviceId(getApplicationContext()));
                Request request = requestBuilder.build();

//                Response response = chain.proceed(request);
//                Log.w("Retrofit@Response", response.body().string());
                return chain.proceed(request);
            }

        };

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        if (BuildConfig.DEBUG) {
            customTimeoutOkHttpClient = new OkHttpClient
                    .Builder()
                    .addInterceptor(mainInterceptor)
                    .addInterceptor(logging)
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .build();
        } else {
            customTimeoutOkHttpClient = new OkHttpClient
                    .Builder()
                    .addInterceptor(mainInterceptor)
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .build();
        }

        customTimeoutRetrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(buildGsonConverter())
                .client(customTimeoutOkHttpClient)
                .build();
        return customTimeoutRetrofit;
    }


//    private static final String DOWNLOAD_ACTION_FILE = "actions";
//    private static final String DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions";
//    private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";
//    private static final int MAX_SIMULTANEOUS_DOWNLOADS = 2;
//
//    protected String userAgent;
//
//    private File downloadDirectory;
//    private Cache downloadCache;
//    private DownloadManager downloadManager;
//    private DownloadTracker downloadTracker;
//
//    /**
//     * Returns a {@link DataSource.Factory}.
//     */
//    public DataSource.Factory buildDataSourceFactory() {
//        DefaultDataSourceFactory upstreamFactory =
//                new DefaultDataSourceFactory(this, buildHttpDataSourceFactory());
//        return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache());
//    }
//
//    /**
//     * Returns a {@link HttpDataSource.Factory}.
//     */
//    public HttpDataSource.Factory buildHttpDataSourceFactory() {
//        return new DefaultHttpDataSourceFactory(userAgent);
//    }
//
//    /**
//     * Returns whether extension renderers should be used.
//     */
//    public boolean useExtensionRenderers() {
//        return "withExtensions".equals(BuildConfig.FLAVOR);
//    }
//
//    public DownloadManager getDownloadManager() {
//        initDownloadManager();
//        return downloadManager;
//    }
//
//    public DownloadTracker getDownloadTracker() {
//        initDownloadManager();
//        return downloadTracker;
//    }
//
//    private synchronized void initDownloadManager() {
//        if (downloadManager == null) {
//            DownloaderConstructorHelper downloaderConstructorHelper =
//                    new DownloaderConstructorHelper(getDownloadCache(), buildHttpDataSourceFactory());
//            downloadManager =
//                    new DownloadManager(
//                            downloaderConstructorHelper,
//                            MAX_SIMULTANEOUS_DOWNLOADS,
//                            DownloadManager.DEFAULT_MIN_RETRY_COUNT,
//                            new File(getDownloadDirectory(), DOWNLOAD_ACTION_FILE));
//            downloadTracker =
//                    new DownloadTracker(
//              /* context= */ this,
//                            buildDataSourceFactory(),
//                            new File(getDownloadDirectory(), DOWNLOAD_TRACKER_ACTION_FILE));
//            downloadManager.addListener(downloadTracker);
//        }
//    }
//
//    private Cache getDownloadCache() {
//        if (downloadCache == null) {
//            File downloadContentDirectory = new File(getDownloadDirectory(), DOWNLOAD_CONTENT_DIRECTORY);
//            downloadCache = new SimpleCache(downloadContentDirectory, new NoOpCacheEvictor());
//        }
//        return downloadCache;
//    }
//
//    private File getDownloadDirectory() {
//        if (downloadDirectory == null) {
//            downloadDirectory = getExternalFilesDir(null);
//            if (downloadDirectory == null) {
//                downloadDirectory = getFilesDir();
//            }
//        }
//        return downloadDirectory;
//    }
//
//    private static CacheDataSourceFactory buildReadOnlyCacheDataSource(
//            DefaultDataSourceFactory upstreamFactory, Cache cache) {
//        return new CacheDataSourceFactory(
//                cache,
//                upstreamFactory,
//                new FileDataSourceFactory(),
//        /* cacheWriteDataSinkFactory= */ null,
//                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
//        /* eventListener= */ null);
//    }
}
