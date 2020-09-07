package com.mycity4kids.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.MessageEvent;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.preference.PreferenceLocaleStore;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.yariksoffice.lingver.Lingver;
import io.branch.referral.Branch;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class holds some application-global instances.
 */
public class BaseApplication extends Application {

    public static final String TAG = BaseApplication.class.getName();

    String data = "";
    private static BaseApplication applicationInstance;
    private static Retrofit retrofit;
    private static Retrofit vlogRetrofit;
    private static Retrofit customTimeoutRetrofit;
    private static Retrofit groupsRetrofit;
    private static Retrofit azureRetrofit;
    private static OkHttpClient client;
    private static OkHttpClient customTimeoutOkHttpClient;

    private static ArrayList<Topics> topicList;
    private static ArrayList<Topics> shortStoryTopicList;
    private static HashMap<Topics, List<Topics>> topicsMap;
    private static HashMap<String, Topics> selectedTopicsMap;
    private String branchData = "";
    private Activity activity;
    /*
     * Google Analytics configuration values.
     */
    public String appVersion;
    public static boolean isFirstSwipe = true;
    private View view;
    private static Socket mSocket;

    private static final String GA_PROPERTY_ID = "UA-20533582-2";

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

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER,// Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> trackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!trackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER)
                    ? analytics.newTracker(R.xml.app_tracker) : (trackerId == TrackerName.GLOBAL_TRACKER)
                    ? analytics.newTracker(GA_PROPERTY_ID) : analytics.newTracker(R.xml.app_tracker);
            trackers.put(trackerId, t);
        }
        return trackers.get(trackerId);
    }

    public static Context getAppContext() {
        return getInstance();
    }

    public static BaseApplication getInstance() {
        return applicationInstance;
    }

    protected static void setInstance(BaseApplication instance) {
        BaseApplication.applicationInstance = instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // workaround for http://code.google.com/p/android/issues/detail?id=20915
        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        Places.initialize(getApplicationContext(), AppConstants.PLACES_API_KEY);
        FirebaseCrashlytics.getInstance().setUserId("" + SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        FirebaseCrashlytics.getInstance()
                .setCustomKey("email", "" + SharedPrefUtils.getUserDetailModel(this).getEmail());
        setInstance(this);
        AndroidThreeTen.init(this);
        createRetrofitInstance(AppConstants.LIVE_URL);

        Branch.enableLogging();
        Branch.getAutoInstance(this);

        PreferenceLocaleStore store = new PreferenceLocaleStore(this, new Locale(AppConstants.LOCALE_ENGLISH));
        Lingver.init(this, store);

        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        appVersion = packageInfo.versionName;

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    public static void startSocket() {
        try {
            if (!TextUtils.isEmpty(SharedPrefUtils.getUserDetailModel(applicationInstance).getDynamoId())) {
                mSocket = IO.socket("https://socketio.momspresso.com/?user_id=" + SharedPrefUtils
                        .getUserDetailModel(applicationInstance).getDynamoId()

                        + "&mc4kToken=" + SharedPrefUtils.getUserDetailModel(applicationInstance).getMc4kToken()
                        + "&lang="
                        + Locale.getDefault().getLanguage() + "&agent=android");
                mSocket.on(SharedPrefUtils.getUserDetailModel(applicationInstance).getDynamoId(), onNewMessage);
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

    public Retrofit createMomVlogRetrofitInstance(String baseUrl) {
        Interceptor mainInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();
                Request.Builder requestBuilder = original.newBuilder();
                requestBuilder.header("Accept-Language", Locale.getDefault().getLanguage());
                requestBuilder
                        .addHeader("id", SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId());
                requestBuilder.addHeader("mc4kToken",
                        SharedPrefUtils.getUserDetailModel(getApplicationContext()).getMc4kToken());
                requestBuilder.addHeader("agent", "android");
                requestBuilder.addHeader("manufacturer", Build.MANUFACTURER);
                requestBuilder.addHeader("model", Build.MODEL);
                requestBuilder.addHeader("source", "2");
                requestBuilder.addHeader("appVersion", appVersion);
                requestBuilder.addHeader("latitude", SharedPrefUtils.getUserLocationLatitude(getApplicationContext()));
                requestBuilder
                        .addHeader("longitude", SharedPrefUtils.getUserLocationLongitude(getApplicationContext()));
                requestBuilder.addHeader("userPrint", "" + AppUtils.getDeviceId(getApplicationContext()));
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
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
                    .addInterceptor(logging)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
        }

        vlogRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(buildGsonConverter())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        return vlogRetrofit;
    }


    public Retrofit createRetrofitInstance(String baseUrl) {
        Interceptor mainInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();
                Request.Builder requestBuilder = original.newBuilder();
                requestBuilder.header("Accept-Language", Locale.getDefault().getLanguage());
                requestBuilder
                        .addHeader("id", SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId());
                requestBuilder.addHeader("mc4kToken",
                        SharedPrefUtils.getUserDetailModel(getApplicationContext()).getMc4kToken());
                requestBuilder.addHeader("agent", "android");
                requestBuilder.addHeader("manufacturer", Build.MANUFACTURER);
                requestBuilder.addHeader("model", Build.MODEL);
                requestBuilder.addHeader("source", "2");
                requestBuilder.addHeader("appVersion", appVersion);
                requestBuilder.addHeader("adId", SharedPrefUtils.getAdvertisementId(getApplicationContext()));
                requestBuilder.addHeader("latitude", SharedPrefUtils.getUserLocationLatitude(getApplicationContext()));
                requestBuilder
                        .addHeader("longitude", SharedPrefUtils.getUserLocationLongitude(getApplicationContext()));
                requestBuilder.addHeader("userPrint", "" + AppUtils.getDeviceId(getApplicationContext()));
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
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
                .baseUrl(baseUrl)
                .addConverterFactory(buildGsonConverter())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }

    public Retrofit createGroupRetrofitInstance(String baseUrl) {
        Interceptor mainInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();
                Request.Builder requestBuilder = original.newBuilder();
                requestBuilder.header("Accept-Language", Locale.getDefault().getLanguage());
                requestBuilder
                        .addHeader("id", SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId());
                requestBuilder.addHeader("mc4kToken",
                        SharedPrefUtils.getUserDetailModel(getApplicationContext()).getMc4kToken());
                requestBuilder.addHeader("agent", "android");
                requestBuilder.addHeader("manufacturer", Build.MANUFACTURER);
                requestBuilder.addHeader("model", Build.MODEL);
                requestBuilder.addHeader("appVersion", appVersion);
                requestBuilder.addHeader("latitude", SharedPrefUtils.getUserLocationLatitude(getApplicationContext()));
                requestBuilder
                        .addHeader("longitude", SharedPrefUtils.getUserLocationLongitude(getApplicationContext()));
                requestBuilder.addHeader("userPrint", "" + AppUtils.getDeviceId(getApplicationContext()));
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
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
                    .addInterceptor(logging)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
        }

        groupsRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(buildGsonConverter())
                .client(client)
                .build();
        return groupsRetrofit;
    }

    public Retrofit createAzureRetrofitInstance(String baseUrl) {
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
                .baseUrl(baseUrl)
                .addConverterFactory(buildGsonConverter())
                .client(client)
                .build();
        return azureRetrofit;
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
            createRetrofitInstance(SharedPrefUtils.getBaseUrl(this));
        }
        return retrofit;
    }

    public Retrofit getStagingRetrofit() {
        if (null == vlogRetrofit) {
            createMomVlogRetrofitInstance("https://stagingapi.momspresso.com/");
        }
        return vlogRetrofit;
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

    public Retrofit getConfigurableTimeoutRetrofit(int timeout) {
        if (null == customTimeoutRetrofit) {
            createCustomTimeoutRetrofitInstance(SharedPrefUtils.getBaseUrl(this), timeout);
        }
        return customTimeoutRetrofit;
    }

    public Retrofit createCustomTimeoutRetrofitInstance(String baseUrl, int timeout) {
        Interceptor mainInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();
                Request.Builder requestBuilder = original.newBuilder();

                requestBuilder.header("Accept-Language", Locale.getDefault().getLanguage());
                requestBuilder
                        .addHeader("id", SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId());
                requestBuilder.addHeader("mc4kToken",
                        SharedPrefUtils.getUserDetailModel(getApplicationContext()).getMc4kToken());
                requestBuilder.addHeader("agent", "android");
                requestBuilder.addHeader("manufacturer", Build.MANUFACTURER);
                requestBuilder.addHeader("model", Build.MODEL);
                requestBuilder.addHeader("appVersion", appVersion);
                requestBuilder.addHeader("latitude", SharedPrefUtils.getUserLocationLatitude(getApplicationContext()));
                requestBuilder
                        .addHeader("longitude", SharedPrefUtils.getUserLocationLongitude(getApplicationContext()));
                requestBuilder.addHeader("userPrint", "" + AppUtils.getDeviceId(getApplicationContext()));
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }

        };

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
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
                .baseUrl(baseUrl)
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
}
