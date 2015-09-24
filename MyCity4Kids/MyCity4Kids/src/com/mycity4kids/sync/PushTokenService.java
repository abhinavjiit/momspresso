package com.mycity4kids.sync;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.newmodels.PushNotificationModel;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * Created by kapil.vij on 17-07-2015.
 */
public class PushTokenService extends IntentService implements UpdateListener {

    private final static String TAG = PushTokenService.class.getSimpleName();
    private boolean taskCallFromAppointment = true;
    private boolean isAppointment = true;
    private int id;
    private PushNotificationModel pushNotificationModel;
    public static final int APPOINTMENT_NOTIFICATION_ID = 11231;
    public static final int TASK_NOTIFICATION_ID = 11230;
    private Uri bitmapUri;

    public PushTokenService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ConnectivityUtils.isNetworkEnabled(this)) {

            if (!SharedPrefUtils.getPushTokenUpdateToServer(this)) {

                if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getDeviceToken(this))) {
                    // hit api
                    if (SharedPrefUtils.getUserDetailModel(this).getId() > 0) {
                        hitApiRequest(AppConstants.PUSH_TOKEN_REQUEST);

                    }
                }

            }
        }

    }

    private void hitApiRequest(int requestType) {
        ApiHandler handler = new ApiHandler(this, this, requestType);
        handler.execute(getApiUrl(requestType));
    }


    @Override
    public void updateView(String jsonString, int requestType) {
        switch (requestType) {
            case AppConstants.PUSH_TOKEN_REQUEST:
                CommonResponse responseData = new Gson().fromJson(jsonString, CommonResponse.class);
                if (responseData.getResponseCode() == 200) {
                    SharedPrefUtils.setPushTokenUpdateToServer(this, true);
                    Log.e("push", "token updated");

                } else {
                    Log.e("push", "token failed");
                }

                break;

        }
    }

    private String getApiUrl(int requestType) {
        StringBuilder builder = new StringBuilder();
        switch (requestType) {
            case AppConstants.PUSH_TOKEN_REQUEST:
                builder.append(AppConstants.PUSH_TOKEN_URL);

                builder.append("user_id=").append(SharedPrefUtils.getUserDetailModel(this).getId());
                builder.append("&push_token=").append(SharedPrefUtils.getDeviceToken(this));

                Log.i("get appointment ", builder.toString());
                return builder.toString().replace(" ", "%20");

        }
        return builder.toString();
    }
}
