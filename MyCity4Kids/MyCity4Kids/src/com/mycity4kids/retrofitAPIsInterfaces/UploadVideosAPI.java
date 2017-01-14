package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.UploadVideoRequest;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.BloggerAnalyticsResponse;
import com.mycity4kids.models.response.ReviewResponse;
import com.mycity4kids.models.response.UpdateVideoDetailsResponse;
import com.mycity4kids.models.response.UserCommentsResponse;
import com.mycity4kids.models.response.UserDetailResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by anshul on 5/5/16.
 */
public interface UploadVideosAPI {

    @POST("v1/videos/")
    Call<UpdateVideoDetailsResponse> updateUploadedVideoURL(@Body UploadVideoRequest body);

}
