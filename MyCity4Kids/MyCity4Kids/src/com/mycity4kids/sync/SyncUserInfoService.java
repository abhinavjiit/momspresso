package com.mycity4kids.sync;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.models.response.KidsModel;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.newmodels.PushNotificationModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.utils.AppUtils;

import java.io.FileInputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

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

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();

        LoginRegistrationAPI usersAPI = retrofit.create(LoginRegistrationAPI.class);
        Call<UserDetailResponse> call = usersAPI.getUserDetails(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(onLoginResponseReceivedListener);


//        ApiHandler handler = new ApiHandler(this, this, requestType);
//        handler.execute(getApiUrl(requestType));
    }

    Callback<UserDetailResponse> onLoginResponseReceivedListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            Log.d("SUCCESS", "" + response);
//            removeProgressDialog();
            if (response == null || response.body() == null) {
//                showToast(getString(R.string.went_wrong));
                return;
            }

            try {
                UserDetailResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    SharedPrefUtils.setProfileImgUrl(BaseApplication.getAppContext(), responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());
                    UserInfo userInfo = SharedPrefUtils.getUserDetailModel(SyncUserInfoService.this);
                    userInfo.setIsLangSelection(responseData.getData().get(0).getResult().getIsLangSelection());
                    userInfo.setFirst_name(responseData.getData().get(0).getResult().getFirstName());
                    userInfo.setLast_name(responseData.getData().get(0).getResult().getLastName());
                    userInfo.setProfilePicUrl(responseData.getData().get(0).getResult().getProfilePicUrl().getClientApp());
                    userInfo.setSessionId(responseData.getData().get(0).getResult().getSessionId());
                    userInfo.setSubscriptionEmail(responseData.getData().get(0).getResult().getSubscriptionEmail());

                    SharedPrefUtils.setUserDetailModel(BaseApplication.getAppContext(), userInfo);

                    FileInputStream fileInputStream = openFileInput(AppConstants.LANGUAGES_JSON_FILE);
                    String fileContent = AppUtils.convertStreamToString(fileInputStream);
                    LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                            fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                            }.getType()
                    );
                    new SaveUserInfoinDB().execute(responseData);
                    Map<String, String> subscribedContentLanguages = responseData.getData().get(0).getResult().getLangSubscription();
                    String filter = "";
                    for (Map.Entry<String, String> entry : subscribedContentLanguages.entrySet()) {
                        if ("1".equals(entry.getValue())) {
                            for (Map.Entry<String, LanguageConfigModel> langEntry : retMap.entrySet()) {
                                if (entry.getKey().equals("english")) {
                                    if (StringUtils.isNullOrEmpty(filter)) {
                                        filter = "0";
                                        break;
                                    } else {
                                        filter = filter + "," + "0";
                                        break;
                                    }
                                } else {
                                    if (entry.getKey().equals(langEntry.getValue().getName().toLowerCase())) {
                                        if (StringUtils.isNullOrEmpty(filter)) {
                                            filter = langEntry.getKey();
                                        } else {
                                            filter = filter + "," + langEntry.getKey();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    SharedPrefUtils.setLanguageFilters(BaseApplication.getAppContext(), filter);
                } else {
//                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            Log.d("MC4kException", Log.getStackTraceString(t));
            Crashlytics.logException(t);
//            showToast(getString(R.string.went_wrong));
        }
    };

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        try {
            Bundle b = intent.getExtras();
            if (b != null)
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

//                    UserResponse responseData = new Gson().fromJson(jsonString, UserResponse.class);
//                    if (responseData.getResponseCode() == 200) {
//                        // save in db
////                        saveDatainDB(responseData);
//                        new SaveUserInfoinDB().execute(responseData);
//
//                    } else if (responseData.getResponseCode() == 400) {
//                        Log.e(TAG, "response failed sync Userinfo");
//                    }

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
//                Log.i("User sync request url", builder.toString());
                break;
        }
        return builder.toString();
    }

//    public void saveDatainDB(UserDetailResponse model) {
//
//        // saving adult data
//        TableAdult adultTable = new TableAdult((BaseApplication) getApplicationContext());
//        adultTable.deleteAll();
//        try {
//
//            adultTable.beginTransaction();
//            for (UserModel.AdultsInfo user : model.getResult().getData().getAdult()) {
//
//                adultTable.insertData(user.getUser());
//            }
//            adultTable.setTransactionSuccessful();
//        }
//        catch (Exception e)
//        {
//
//            Log.e("adult table",e.getMessage());
//        }
//        finally {
//            adultTable.endTransaction();
//        }
//
//        // saving child data
//        TableKids kidsTable = new TableKids((BaseApplication) getApplicationContext());
//        kidsTable.deleteAll();
//        try {
//            kidsTable.beginTransaction();
//            for (KidsInfo kids : model.getResult().getData().getKidsInformation()) {
//
//                kidsTable.insertData(kids);
//
//            }
//            kidsTable.setTransactionSuccessful();
//        } finally {
//            kidsTable.endTransaction();
//        }
//
//        // saving family
//
//        TableFamily familyTable = new TableFamily((BaseApplication) getApplicationContext());
//        familyTable.deleteAll();
//        try {
//            familyTable.insertData(model.getResult().getData().getFamily());
//
//        } catch (Exception e) {
//            e.getMessage();
//        }
//
//        // user table
//        UserTable userTable = new UserTable((BaseApplication) getApplicationContext());
//        userTable.deleteAll();
//        userTable.insertData(model);
//        // for profile image
//        UserTable table = new UserTable(BaseApplication.getInstance());
//        if (table.getRowsCount() > 0) {
//
//            try {
//                String profileimg = table.getAllUserData().getProfile().getProfile_image();
//                if (!StringUtils.isNullOrEmpty(profileimg)) {
//                    SharedPrefUtils.setProfileImgUrl(this, profileimg);
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        // handling notification
//
//        if (pushNotificationModel != null) {
//
//            Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                    R.drawable.ic_launcher);
//
//            int requestID = (int) System.currentTimeMillis();
//
//            String message = pushNotificationModel.getMessage_id();
//            String title = pushNotificationModel.getTitle();
//            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//
//            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("push", true);
//            intent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
//
//            PendingIntent contentIntent = PendingIntent.getActivity(this, getUniqueRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setLargeIcon(icon).setSmallIcon(R.drawable.iconnotify).setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setContentText(message);
//            mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
//            mBuilder.setAutoCancel(true);
//            mBuilder.setContentIntent(contentIntent);
//            mNotificationManager.notify(requestID, mBuilder.build());
//        }
//
//    }

    private int getUniqueRequestCode() {
        Random randomGenerator = new Random();
        return randomGenerator.nextInt(1000);
    }

    public class SaveUserInfoinDB extends AsyncTask<UserDetailResponse, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            showProgressDialog("Please wait..");
        }

        @Override
        protected Void doInBackground(UserDetailResponse... userResponses) {

            if (null != userResponses[0].getData().get(0).getResult().getKids()) {
                saveKidsInformation(userResponses[0].getData().get(0).getResult().getKids());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

        }

    }

    private void saveKidsInformation(ArrayList<KidsModel> kidsList) {

        if (kidsList.size() == 1 && StringUtils.isNullOrEmpty(kidsList.get(0).getName())) {
            return;
        }
        ArrayList<KidsInfo> kidsInfoArrayList = new ArrayList<>();

        for (KidsModel kid : kidsList) {
            KidsInfo kidsInfo = new KidsInfo();
            kidsInfo.setName(kid.getName());
            kidsInfo.setDate_of_birth(convertTime("" + kid.getBirthDay()));
            kidsInfo.setColor_code(kid.getColorCode());
            kidsInfoArrayList.add(kidsInfo);
        }

        // saving child data
        TableKids kidsTable = new TableKids((BaseApplication) getApplicationContext());
        kidsTable.deleteAll();
        try {
            kidsTable.beginTransaction();
            for (KidsInfo kids : kidsInfoArrayList) {

                kidsTable.insertData(kids);

            }
            kidsTable.setTransactionSuccessful();
        } finally {
            kidsTable.endTransaction();
        }
    }

    public String convertTime(String time) {
        try {
            Date date = new Date(Long.parseLong(time) * 1000);
            Format format = new SimpleDateFormat("dd-MM-yyyy");
            return format.format(date);
        } catch (NumberFormatException nfe) {
            return "";
        }
    }
}
