package com.chatPlatform.ServiceReplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.AuthenticatorFactory;
import com.couchbase.lite.replicator.Replication;
import com.mycity4kids.application.BaseApplication;

/**
 * Created by anshul on 1/13/16.
 */
public class ReplicationService extends Service {
    BaseApplication app;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            app=(BaseApplication)getApplicationContext();
            SharedPreferences _sharedPref = getSharedPreferences("my_city_prefs", Context.MODE_PRIVATE);
            int UserId=_sharedPref.getInt("userid", 0);
           String username="user-"+UserId;
            final Replication pull = app.getDatabase().createPullReplication(app.createSyncURL(false));
            Replication push = app.getDatabase().createPushReplication(app.createSyncURL(false));
            Authenticator authenticator = AuthenticatorFactory.createBasicAuthenticator(username, "password");
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
        return super.onStartCommand(intent, flags, startId);

    }
}
