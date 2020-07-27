package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.collectionsModels.TutorialCollectionsListModel;
import com.mycity4kids.models.request.AddCommentRequest;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.request.UpdateVlogTitleRequest;
import com.mycity4kids.models.request.UploadVideoRequest;
import com.mycity4kids.models.request.VlogsEventRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.AddCommentResponse;
import com.mycity4kids.models.response.ArticleRecommendationStatusResponse;
import com.mycity4kids.models.response.BaseResponseGeneric;
import com.mycity4kids.models.response.MomVlogListingResponse;
import com.mycity4kids.models.response.MomVlogersDetailResponse;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.models.response.VlogsDetailResponse;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.vlogs.VlogsCategoryWiseChallengesResponse;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hemant on 3/5/16.
 */
public interface VlogsListingAndDetailsAPI {


    @GET("/v2/categories")
    Call<Topics> getVlogCategoriesAndChallenges(@Query("id") String id);

    @GET("/v2/categories")
    Call<Topics> getVlogChallengeDetails(@Query("id") String id);

    @GET("/v2/categories/video/challenges")
    Call<VlogsCategoryWiseChallengesResponse> getVlogsCategoryWiseChallenges();

    @GET("v2/categories/")
    Call<Topics> getArticleChallenges(@Query("id") String id);

    @GET("/v2/categories/category/challenges/")
    Call<VlogsCategoryWiseChallengesResponse> getSingleChallenge(@Query("id") ArrayList<String> id);

    @GET("/v2/videos/{videoId}")
    Call<VlogsDetailResponse> getVlogDetail(@Path("videoId") String videoId);

    @PUT("v2/videos/{videoId}/views/")
    Call<ResponseBody> updateViewCount(@Path("videoId") String videoId);

    @PATCH("/v2/videos/{videoId}")
    Call<VlogsDetailResponse> updateVideoTitle(@Path("videoId") String videoId,
            @Body UpdateVlogTitleRequest body);

    @GET("v2/videos")
    Call<VlogsListingResponse> getPublishedVlogs(@Query("user_id") String userId,
            @Query("start") int start,
            @Query("end") int end,
            @Query("sort") int sort);

    @GET("v2/videos")
    Call<VlogsListingResponse> getPublishedVlogsForPublicProfile(@Query("user_id") String userId,
            @Query("start") int start,
            @Query("end") int end,
            @Query("sort") int sort,
            @Query("publication_status") int publicationStatus);

    @GET("/user_history/v1/{userId}")
    Call<VlogsListingResponse> getAuthorsSeenVideos(@Path("userId") String userId,
            @Query("size") int size,
            @Query("chunks") int chunks,
            @Query("filter") String filter);

    @GET("v2/videos/")
    Call<VlogsListingResponse> getVlogsList(@Query("start") int start,
            @Query("end") int end,
            @Query("sort") int sort,
            @Query("type") int type,
            @Query("category_id") String categoryId);

    @GET("v2/videos/related/{videoId}")
    Call<VlogsListingResponse> getRelatedVlogs(@Path("videoId") String videoId,
            @Query("start") int start,
            @Query("end") int end);

    @GET("/v1/collections/{collectionId}")
    Call<BaseResponseGeneric<TutorialCollectionsListModel>> getTutorialCollectionItems(
            @Path("collectionId") String collectionId,
            @Query("start") int start,
            @Query("offset") int offset
    );


    @GET("v2/videos/")
    Call<VlogsListingResponse> getVlogsListForWinner(@Query("start") int start,
            @Query("end") int end,
            @Query("sort") int sort,
            @Query("type") int type,
            @Query("category_id") String categoryId,
            @Query("$order_by") String orderBy);

    @GET("v2/videos/")
    Call<VlogsListingResponse> getWinnerVlogsAllLanguages(@Query("start") int start,
            @Query("end") int end,
            @Query("sort") int sort,
            @Query("type") int type,
            @Query("category_id") String categoryId,
            @Query("winner") String winner);

    @POST("v2/videos/")
    Call<ResponseBody> publishHomeVideo(@Body UploadVideoRequest uploadVideoRequest);

    @GET("/v1/categories/videochallenges/")
    Call<TopicsResponse> getVlogChallenges();

    @GET("v1/users/vlog/bookmarkVideo/")
    Call<AddBookmarkResponse> checkFollowingBookmarkStatus(@Body ArticleDetailRequest articleDetailRequest);

    @PUT("v1/comments/{commentId}")
    Call<AddCommentResponse> editComment(@Path("commentId") String commentId,
            @Body AddCommentRequest body);

    @POST("v1/users/vlog/bookmarkVideo/")
    Call<AddBookmarkResponse> addBookmark(@Body ArticleDetailRequest body);

    @HTTP(method = "DELETE", path = "v1/users/vlog/deleteBookmarkVideo/", hasBody = true)
    Call<AddBookmarkResponse> deleteBookmark(@Body ArticleDetailRequest body);

    @GET("v1/users/likes/{articleId}")
    Call<ArticleRecommendationStatusResponse> getArticleRecommendedStatus(@Path("articleId") String articleId);

    @PUT("v1/users/likes/")
    Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle(
            @Body RecommendUnrecommendArticleRequest body);

    @GET("v1/recommendations/video_ad")
    Call<Topics> getRecommendedVideoAd();

    @POST("http://eventsapi.momspresso.com/datapipeline/")
    Call<ResponseBody> addVlogsCreateIntentEvent(@Body VlogsEventRequest vlogsEventRequest);

    //35.200.142.199/personalised/vlogs/7ebbffc86dba4c8a82750278024d1332?start=0&end=10
    @GET("/personalised/vlogs/{userId}")
    Call<MomVlogListingResponse> getVlogsData(@Path("userId") String userId,
            @Query("start") int start,
            @Query("end") int end);

    //35.200.142.199/personalised/vloggers/7ebbffc86dba4c8a82750278024d1332?start=0&end=10&is_top=1
    @GET("/personalised/vloggers/{userId}")
    Call<MomVlogersDetailResponse> getVlogersData(@Path("userId") String userId,
            @Query("start") int start,
            @Query("end") int end,
            @Query("is_top") int isTop);

    @GET("/personalised/vloggers/{userId}")
    Call<MomVlogersDetailResponse> getGoldVlogersData(@Path("userId") String userId,
            @Query("start") int start,
            @Query("end") int end,
            @Query("is_gold") int isGold);
}
