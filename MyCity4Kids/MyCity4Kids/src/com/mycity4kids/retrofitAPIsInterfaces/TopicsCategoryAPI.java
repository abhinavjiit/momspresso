package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.editor.ArticleDraftRequest;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.models.response.ArticleDraftResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.models.response.TopicsFollowingStatusData;
import com.mycity4kids.models.response.TopicsFollowingStatusResponse;
import com.mycity4kids.newmodels.FollowUnfollowCategoriesRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by hemant on 3/5/16.
 */
public interface TopicsCategoryAPI {

    @GET("apiparentingstop/getCategoryList")
    Call<TopicsResponse> getTopicsCategory(@Query("user_id") String user_id);

    @GET("apiparentingstop/searchV1")
    Call<CommonParentingResponse> filterCategories(@Query("q") String searchParam,
                                                   @Query("type") String type,
                                                   @Query("page") int pageNum);

    @GET("/v1/utilities/config/")
    Call<ResponseBody> downloadCategoriesJSON();

    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);

    @GET("/v1/articles/topics/{categoryId}")
    Call<ArticleListingResponse> getArticlesForCategory(@Path("categoryId") String categoryId,
                                                        @Query("sort") int sort,
                                                        @Query("start") int start,
                                                        @Query("end") int end);

    @GET("/v1/articles/cities/{cityId}")
    Call<ArticleListingResponse> getBestArticlesForCity(@Path("cityId") String cityId,
                                                        @Query("sort") int sort,
                                                        @Query("start") int start,
                                                        @Query("end") int end);

    @PUT("/v1/users/{userId}/topics")
    Call<FollowUnfollowCategoriesResponse> followCategories(@Path("userId") String userId,
                                                            @Body FollowUnfollowCategoriesRequest body);

    @GET("/v1/users/{userId}/topics")
    Call<FollowUnfollowCategoriesResponse> getFollowedCategories(@Path("userId") String userId);

    @GET
    Call<ResponseBody> downloadTopicsListForFollowUnfollow(@Url String fileUrl);

    @GET("/v1/users/{userId}/topics/{topicId}")
    Call<TopicsFollowingStatusResponse> checkTopicsFollowingStatus(@Path("userId") String userId,
                                                                   @Path("topicId") String topicId);

    @GET("/v1/recommend/{userId}/{start}/{end}")
    Call<ArticleListingResponse> getForYouArticles(@Path("userId") String userId,
                                                          @Path("start") String start,
                                                          @Path("end") String end);
}