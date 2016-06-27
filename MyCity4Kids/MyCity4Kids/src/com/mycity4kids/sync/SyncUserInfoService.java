package com.mycity4kids.sync;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.PushNotificationModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.DashboardActivity;

import java.util.Random;

/**
 * Created by manish.soni on 04-08-2015.
 */
public class SyncUserInfoService extends IntentService implements UpdateListener {

    private final static String TAG = SyncUserInfoService.class.getSimpleName();
    private PushNotificationModel pushNotificationModel;
    public static final int NOTIFICATION_ID = 11232;

    public SyncUserInfoService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            hitApiRequest(AppConstants.SYNC_USER_INFO_REQUEST);
        }
    }

    private void hitApiRequest(int requestType) {
        ApiHandler handler = new ApiHandler(this, this, requestType);
        handler.execute(getApiUrl(requestType));
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        try {
            Bundle b = intent.getExtras();
            if(b!=null)
            pushNotificationModel = b.getParcelable(Constants.PUSH_MODEL);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateView(String jsonString, int requestType) {
        switch (requestType) {
            case AppConstants.SYNC_USER_INFO_REQUEST:
                try {

                    UserResponse responseData = new Gson().fromJson(jsonString, UserResponse.class);
                    if (responseData.getResponseCode() == 200) {
                        // save in db
//                        saveDatainDB(responseData);
                        new SaveUserInfoinDB().execute(responseData);

                    } else if (responseData.getResponseCode() == 400) {
                        Log.e(TAG, "response failed sync Userinfo");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private String getApiUrl(int requestType) {
        StringBuilder builder = new StringBuilder();
        switch (requestType) {
            case AppConstants.SYNC_USER_INFO_REQUEST:
                builder.append(AppConstants.GET_SYNC_USER_INFO_URL);
                String userId = SharedPrefUtils.getUserDetailModel(this).getId();
                builder.append("user_id:").append(userId);
                Log.i("User sync request url", builder.toString());
                break;
        }
        return builder.toString();
    }

    public void saveDatainDB(UserResponse model) {

        // saving adult data
        TableAdult adultTable = new TableAdult((BaseApplication) getApplicationContext());
        adultTable.deleteAll();
        try {

            adultTable.beginTransaction();
            for (UserModel.AdultsInfo user : model.getResult().getData().getAdult()) {

                adultTable.insertData(user.getUser());
            }
            adultTable.setTransactionSuccessful();
        }
        catch (Exception e)
        {

            Log.e("adult table",e.getMessage());
        }
        finally {
            adultTable.endTransaction();
        }

        // saving child data
        TableKids kidsTable = new TableKids((BaseApplication) getApplicationContext());
        kidsTable.deleteAll();
        try {
            kidsTable.beginTransaction();
            for (KidsInfo kids : model.getResult().getData().getKidsInformation()) {

                kidsTable.insertData(kids);

            }
            kidsTable.setTransactionSuccessful();
        } finally {
            kidsTable.endTransaction();
        }

        // saving family

        TableFamily familyTable = new TableFamily((BaseApplication) getApplicationContext());
        familyTable.deleteAll();
        try {
            familyTable.insertData(model.getResult().getData().getFamily());

        } catch (Exception e) {
            e.getMessage();
        }

        // user table
        UserTable userTable = new UserTable((BaseApplication) getApplicationContext());
        userTable.deleteAll();
        userTable.insertData(model);
        // for profile image
        UserTable table = new UserTable(BaseApplication.getInstance());
        if (table.getRowsCount() > 0) {

            try {
                String profileimg = table.getAllUserData().getProfile().getProfile_image();
                if (!StringUtils.isNullOrEmpty(profileimg)) {
                    SharedPrefUtils.setProfileImgUrl(this, profileimg);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // handling notification

        if (pushNotificationModel != null) {

           Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher);

            int requestID = (int) System.currentTimeMillis();

            String message = pushNotificationModel.getMessage_id();
            String title = pushNotificationModel.getTitle();
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("push", true);
            intent.putExtra(AppConstants.NOTIFICATION_ID, requestID);

            PendingIntent contentIntent = PendingIntent.getActivity(this, getUniqueRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setLargeIcon(icon).setSmallIcon(R.drawable.iconnotify).setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setContentText(message);
            mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
            mBuilder.setAutoCancel(true);
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(requestID, mBuilder.build());
        }

    }
    private int getUniqueRequestCode(){
        Random randomGenerator = new Random();
        return randomGenerator.nextInt(1000);
    }
    public class SaveUserInfoinDB extends AsyncTask<UserResponse, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            showProgressDialog("Please wait..");
        }

        @Override
        protected Void doInBackground(UserResponse... userResponses) {

            saveDatainDB(userResponses[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

        }

    }
}
