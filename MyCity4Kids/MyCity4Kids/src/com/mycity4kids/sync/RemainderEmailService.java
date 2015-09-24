package com.mycity4kids.sync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.kelltontech.utils.ConnectivityUtils;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;

/**
 * Created by manish.soni on 04-08-2015.
 */
public class RemainderEmailService extends IntentService implements UpdateListener {

    int requestType = Constants.REMINDER_TYPE_APPOINTMENT;
    int requestId = 0;

    private final static String TAG = RemainderEmailService.class.getSimpleName();

    public RemainderEmailService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        requestType = intent.getIntExtra(Constants.REMAINDER_TYPE, Constants.REMINDER_TYPE_APPOINTMENT);
        requestId = intent.getIntExtra(Constants.REMAINDER_ID, 0);

        if (ConnectivityUtils.isNetworkEnabled(this)) {
            hitApiRequest(AppConstants.EMAIL_NOTIFICATION_REQUEST);
        }
    }

    private void hitApiRequest(int requestType) {
        ApiHandler handler = new ApiHandler(this, this, requestType);
        handler.execute(getApiUrl(requestType));
    }


    @Override
    public void updateView(String jsonString, int requestType) {
        switch (requestType) {
            case AppConstants.EMAIL_NOTIFICATION_REQUEST:
                try {

                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "Email Remainder response...");
                        Log.e("Email ->", jsonString);
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

                if (requestType == Constants.REMINDER_TYPE_APPOINTMENT) {
                    builder.append(AppConstants.GET_EMAIL_APPOINTMENT_NOTIFICATION_URL);
                } else {
                    builder.append(AppConstants.GET_EMAIL_TASK_NOTIFICATION_URL);
                }
                builder.append(requestId);
                Log.i("Email remainder url", builder.toString());
                break;
        }
        return builder.toString();
    }

}