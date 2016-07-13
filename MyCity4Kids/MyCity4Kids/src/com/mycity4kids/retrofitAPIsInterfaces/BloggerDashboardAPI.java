package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.newmodels.BloggerDashboardModel;
import com.mycity4kids.newmodels.PublishedArticlesModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by anshul on 5/5/16.
 */
public interface BloggerDashboardAPI {
    @GET("apiblogs/bloggerDashboardData?")
    Call<BloggerDashboardModel> getBloggerData(@Query("userId") String userId);

    @GET("v1/user/bookmark/{from}/{to}")
    Call<ArticleListingResponse> getBookmarkedList(@Path("from") int from,
                                                   @Path("to") int to);

    @GET("apiblogs/publishedArticle?")
    Call<ResponseBody> getPublishedArticleList(@Query("userId") String userId,
                                               @Query("page") String page);
}
