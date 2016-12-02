package com.mycity4kids.utils;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.ArticleReadTimeRequest;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 25/11/16.
 */
public class TrackArticleReadTime {
    private long startTime = 0;
    private long timeSpent = 0;
    private long initStartTime = 0;
    private int isActivityStoppped = 0;
    private Context mContext;

    public TrackArticleReadTime(Context mContext) {
        this.mContext = mContext;
    }

    public void startTimer() {
        timeSpent = 0;
        isActivityStoppped = 0;
        startTime = System.currentTimeMillis();
        initStartTime = System.currentTimeMillis();
        Log.d("initStartTime", "" + initStartTime);
    }

    public void resumeTimer() {
        if (timeSpent != 0) {
            startTime = System.currentTimeMillis();
        }
    }

    public void pauseTimer() {
        timeSpent = timeSpent + System.currentTimeMillis() - startTime;
        Log.d("TIME_SPENT", "" + timeSpent / 1000f);
    }

    public void resetTimer() {
        startTime = 0;
        timeSpent = 0;
        isActivityStoppped = 1;
    }

    public int getActivityTimerStatus() {
        return isActivityStoppped;
    }

    public void updateTimeAtBackendAndGA(String articleURL, String articleId, long estimatedTime) {
        timeSpent = timeSpent + System.currentTimeMillis() - startTime;
        Log.d("updateTimeBackendAndGA", " timeSpent = " + timeSpent + " initStartTime=" + initStartTime + " estimatedtime=" + estimatedTime);
        Utils.pushArticleDetailsTimeSpent(mContext, GTMEventType.ARTICLE_TIME_SPENT_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(),
                "Blog Detail", articleURL, "" + timeSpent, "" + estimatedTime);

        long timeSpentEstimatedTimePercentage = (timeSpent * 100) / (estimatedTime * 1000);

        Log.d("updateTimeBackendAndGA", "timeSpentEstimatedTimePercentage = " + timeSpentEstimatedTimePercentage);
        if (timeSpentEstimatedTimePercentage > AppConstants.MIN_PERCENT_FOR_TIMESPENT) {
            Log.d("updateTimeBackendAndGA", "updateTimeBackendAndGA = " + timeSpentEstimatedTimePercentage);
            ArticleReadTimeRequest articleReadTimeRequest = new ArticleReadTimeRequest();
            articleReadTimeRequest.setArticleId(articleId);
            articleReadTimeRequest.setStartTime("" + initStartTime);
            articleReadTimeRequest.setTimeSpent("" + timeSpent);
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            ArticleDetailsAPI articleTimeSpentAPI = retro.create(ArticleDetailsAPI.class);

            Call<ResponseBody> callBookmark = articleTimeSpentAPI.updateArticleTimeSpent(articleReadTimeRequest);
            callBookmark.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("SUCESS RESPONSE", "dadawdawdad");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Crashlytics.logException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            });
        }

    }
}
