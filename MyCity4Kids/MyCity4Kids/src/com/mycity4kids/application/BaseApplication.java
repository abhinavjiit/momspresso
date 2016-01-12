package com.mycity4kids.application;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.DocumentChange;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.AuthenticatorFactory;
import com.couchbase.lite.replicator.Replication;
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
    public static final String TAG = "ChatApp";

    private static final String DATABASE_NAME = "chatdb";

    private Manager manager;
    private Database database;
    private String userNumber="123";
    private String userName="test";

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getUserName()
    {
        return userName;
    }
    public void setUserName(String userName)
    {this.userName=userName;}

    public void initDatabase() {
        try {

            Manager.enableLogging(TAG, com.couchbase.lite.util.Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG, com.couchbase.lite.util.Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG_SYNC_ASYNC_TASK, com.couchbase.lite.util.Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG_SYNC, com.couchbase.lite.util.Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG_QUERY, com.couchbase.lite.util.Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG_VIEW, com.couchbase.lite.util.Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG_DATABASE, com.couchbase.lite.util.Log.VERBOSE);

            manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            com.couchbase.lite.util.Log.e(TAG, "Cannot create Manager object", e);
            return;
        }

        try {
            database = manager.getDatabase(DATABASE_NAME);
            if(database != null) {
                database.addChangeListener(new Database.ChangeListener() {
                    public void changed(Database.ChangeEvent event) {
                        List<DocumentChange> docChangeList = event.getChanges();
                        DocumentChange docChange=docChangeList.get(0);
                        String id= docChange.getDocumentId();
                        Document doc= database.getDocument(id);
                        String msg= (String) doc.getProperty("msg");
                        String title=(String) doc.getProperty("title");
                        notifyMe(title,msg);
                    }
                });
            }
            // database.addChangeListener(new);
        } catch (CouchbaseLiteException e) {
            com.couchbase.lite.util.Log.e(TAG, "Cannot get Database", e);
            return;
        }
    }

    public Database getDatabase() {
        return this.database;
    }

    public Manager getManager() { return this.manager; }
    public URL createSyncURL(boolean isEncrypted){
        URL syncURL = null;
        // String host = "https://10.0.2.2";
        String host="http://52.74.34.206";
        String port = "4984";
        String dbName = "my_test_config";
        try {
            syncURL = new URL(host + ":" + port + "/" + dbName);
        } catch (MalformedURLException me) {
            me.printStackTrace();
        }
        return syncURL;
    }

    public void startReplications(String userName, String Password)  {
        try {
            final Replication pull = this.getDatabase().createPullReplication(this.createSyncURL(false));
            Replication push = this.getDatabase().createPushReplication(this.createSyncURL(false));
            Authenticator authenticator = AuthenticatorFactory.createBasicAuthenticator("bbb", "bbb");
            pull.setAuthenticator(authenticator);
            push.setAuthenticator(authenticator);
            pull.setContinuous(true);
            push.setContinuous(true);
            pull.start();
            push.start();

            pull.addChangeListener(new Replication.ChangeListener() {
                @Override
                public void changed(Replication.ChangeEvent event) {
                    // The replication reporting the notification is either
                    // the push or the pull, but we want to look at the
                    // aggregate of both the push and pull.
                    // First check whether replication is currently active:
                    boolean active = (pull.getStatus() == Replication.ReplicationStatus.REPLICATION_ACTIVE);
                    if (!active) {
                        // progressDialog.dismiss();
                    } else {
                        double total = pull.getCompletedChangesCount();
                        int totalChanges=  pull.getChangesCount();
                        //progressDialog.setMax(total);
                        // progressDialog.setProgress(push.getChangesCount() + pull.getChangesCount());
                        android.util.Log.i("sync-progress", "totalChanges " + totalChanges + " comepletedChanges " + total);
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            android.util.Log.e("sync-error", "cannot sync", e);
        }
    }

    public void notifyMe(String title, String msg){

/*


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.user)
                        .setContentTitle(title)
                        .setContentText(msg);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        manager.notify(123, builder.build());*/
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        setInstance(this);
        initializeGa();
        initDatabase();
        startReplications("test","test");
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
