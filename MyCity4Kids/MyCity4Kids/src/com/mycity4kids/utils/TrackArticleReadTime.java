package com.mycity4kids.utils;

import android.content.Context;
import android.util.Log;

import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * Created by hemant on 25/11/16.
 */
public class TrackArticleReadTime {
    private long startTime = 0;
    private long timeSpent = 0;
    private int isActivityStoppped = 0;
    private Context mContext;

    public TrackArticleReadTime(Context mContext) {
        this.mContext = mContext;
    }

    public void startTimer() {
        timeSpent = 0;
        isActivityStoppped = 0;
        startTime = System.currentTimeMillis();
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

    public void updateTimeAtBackendAndGA(String articleURL, String articleId, String estimatedTime) {
        timeSpent = timeSpent + System.currentTimeMillis() - startTime;
        Log.d("updateTimeBackendAndGA", "" + timeSpent / 1000f);
        Utils.pushArticleDetailsTimeSpent(mContext, GTMEventType.ARTICLE_TIME_SPENT_EVENT, SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(),
                "Blog Detail", articleURL, "" + timeSpent, estimatedTime);
//        Retrofit retro = BaseApplication.getInstance().getRetrofit();
//        ArticleDetailsAPI bookmarFollowingStatusAPI = retro.create(ArticleDetailsAPI.class);

//        Call<ArticleDetailResponse> callBookmark = bookmarFollowingStatusAPI.checkFollowingBookmarkStatus(articleId, authorId);
//        callBookmark.enqueue(isBookmarkedFollowedResponseCallback);

    }
}
