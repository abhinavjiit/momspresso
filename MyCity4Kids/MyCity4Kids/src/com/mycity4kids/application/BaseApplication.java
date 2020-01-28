package com.mycity4kids.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.multidex.MultiDex;

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
import com.google.android.libraries.places.api.Places;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.MessageEvent;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.LocaleManager;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class holds some application-global instances.
 */
public class BaseApplication extends Application {


    private final String LOG_TAG = "BaseApplication";
    public static final String TAG = BaseApplication.class.getName();

    private RequestQueue mRequestQueue;
    String data = "";
    private static BaseApplication mInstance;
    private static Retrofit retrofit, customTimeoutRetrofit, groupsRetrofit, campaignRewards, azureRetrofit, testRetrofit;
    private static OkHttpClient client, customTimeoutOkHttpClient;

    private static ArrayList<Topics> topicList;
    private static ArrayList<Topics> shortStoryTopicList;
    private static HashMap<Topics, List<Topics>> topicsMap;
    private static HashMap<String, Topics> selectedTopicsMap;
    private String branchData = "";
    private Activity dashboardActivity;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Activity activity;
    private int position;

    /*
     * Google Analytics configuration values.
     */
    private static GoogleAnalytics mGa;
    private static Tracker mTracker;
    public static String base_url;
    public String appVersion;
    private boolean askPermission = true;
    public static boolean isFirstSwipe = true;
    private View view;
    private static Socket mSocket;

    // Placeholder property ID.this was old which create by own account.
    //private static final String GA_PROPERTY_ID = "UA-50870780-1";

    private static final String GA_PROPERTY_ID = "UA-20533582-2";

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

    public boolean isAskPermission() {
        return askPermission;
    }

    public void setAskPermission(boolean askPermission) {
        this.askPermission = askPermission;
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


    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }


    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getDashboardActivity() {
        return dashboardActivity;
    }

    public void setDashboardActivity(Activity dashboardActivity) {
        this.dashboardActivity = dashboardActivity;
    }

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER,// Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

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

    public Retrofit getCampaignRetrofit() {
        if (null == campaignRewards) {
            createRetrofitInstanceForCampaign("https://testingapi.momspresso.com/");
        }
        return campaignRewards;
    }

    public Retrofit createRetrofitInstanceForCampaign(String base_url) {
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
                requestBuilder.addHeader("source", "2");
                requestBuilder.addHeader("appVersion", appVersion);
                requestBuilder.addHeader("latitude", SharedPrefUtils.getUserLocationLatitude(getApplicationContext()));
                requestBuilder.addHeader("longitude", SharedPrefUtils.getUserLocationLongitude(getApplicationContext()));
                requestBuilder.addHeader("userPrint", "" + AppUtils.getDeviceId(getApplicationContext()));
                Request request = requestBuilder.build();
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

        campaignRewards = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(buildGsonConverter())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

        return campaignRewards;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // workaround for http://code.google.com/p/android/issues/detail?id=20915
        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
        }
        Places.initialize(getApplicationContext(), AppConstants.PLACES_API_KEY);

        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());
        Crashlytics.setUserIdentifier("" + SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        Crashlytics.setUserEmail("" + SharedPrefUtils.getUserDetailModel(this).getEmail());

        setInstance(this);
        VolleyLog.setTag("VolleyLogs");

        Fresco.initialize(this);
        createRetrofitInstance(AppConstants.LIVE_URL);

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        comScore.setAppContext(this.getApplicationContext());
        comScore.setCustomerC2("18705325");
        comScore.setPublisherSecret("6116f207ac5e9f9226f6b98e088a22ea");
        AudienceNetworkAds.initialize(this);
        Branch.enableLogging();
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

    public static void startSocket() {
        try {
            if (!TextUtils.isEmpty(SharedPrefUtils.getUserDetailModel(mInstance).getDynamoId())) {
                mSocket = IO.socket("https://socketio.momspresso.com/?user_id=" + SharedPrefUtils.getUserDetailModel(mInstance).getDynamoId()

                        + "&mc4kToken=" + SharedPrefUtils.getUserDetailModel(mInstance).getMc4kToken() + "&lang=" + Locale.getDefault().getLanguage() + "&agent=android");
                mSocket.on(SharedPrefUtils.getUserDetailModel(mInstance).getDynamoId(), onNewMessage);
                if (!mSocket.connected()) {
                    mSocket.connect();
                }
            }
        } catch (URISyntaxException e) {
            Log.e("Exception", e.toString());
        }
    }

    public static Socket getMSocket() {
        return mSocket;
    }

    private static Emitter.Listener onNewMessage = args -> {
        if (EventBus.getDefault() != null) {
            EventBus.getDefault().post(new MessageEvent(args));
        }
    };

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
//                requestBuilder.addHeader("id", "be13a0f26bbf41f0833906ec374f07db");
//                requestBuilder.addHeader("mc4kToken", "ya29.GltVBy0pjSImNx6PgYPeTg9DlTGaR12FlHJYEXi9YN3h39ZHZekRio46xKXB1uHfV_mkDH3V05DRKkFKi2ZzYICMJ5fsxn05eJ_ub7GgCtYAeRdAhgHGZ5Y07AN2");
                requestBuilder.addHeader("id", SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId());
                requestBuilder.addHeader("mc4kToken", SharedPrefUtils.getUserDetailModel(getApplicationContext()).getMc4kToken());
                requestBuilder.addHeader("agent", "android");
                requestBuilder.addHeader("manufacturer", Build.MANUFACTURER);
                requestBuilder.addHeader("model", Build.MODEL);
                requestBuilder.addHeader("source", "2");
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

        retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(buildGsonConverter())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

//        retrofit = new Retrofit.Builder()
//                .baseUrl("http://35.200.209.192:5000/")
//                .addConverterFactory(buildGsonConverter())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .client(client)
//                .build();
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

    public Retrofit createAzureRetrofitInstance(String base_url) {

        Interceptor mainInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();

                requestBuilder.header("Accept-Language", Locale.getDefault().getLanguage());
                requestBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded");
                requestBuilder.addHeader("Ocp-Apim-Subscription-Key", "987918a65c924d0fb5da048e1fbf5dd9");
                Request request = requestBuilder.build();

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

        azureRetrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(buildGsonConverter())
                .client(client)
                .build();
        return azureRetrofit;
    }


    public Retrofit createRetrofitTestInstance(String base_url) {

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

        testRetrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(buildGsonConverter())
                .client(client)
                .build();
        return testRetrofit;
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

    public Retrofit getArticleRetrofit() {
        if (null == retrofit) {
            createRetrofitInstance(SharedPrefUtils.getRewardsBaseURL(this));
        }
        return retrofit;
    }

    public Retrofit getRetrofitTest() {
        if (null == testRetrofit) {
            createRetrofitTestInstance(SharedPrefUtils.getRewardsBaseURL(this));
        }
        return testRetrofit;
    }

    public Retrofit getAzureRetrofit() {
        if (null == azureRetrofit) {
            createAzureRetrofitInstance(AppConstants.AZURE_LIVE_URL);
        }
        return azureRetrofit;
    }

    public Retrofit getGroupsRetrofit() {
        if (null == groupsRetrofit) {
            createGroupRetrofitInstance(AppConstants.GROUPS_TEST_LIVE_URL);
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


    public void setBranchData(String branchData) {
        this.branchData = branchData;
    }

    public String getBranchData() {
        return branchData;
    }


    public void setBranchLink(String data) {
        this.data = data;
    }

    public String getBranchLink() {
        return data;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
