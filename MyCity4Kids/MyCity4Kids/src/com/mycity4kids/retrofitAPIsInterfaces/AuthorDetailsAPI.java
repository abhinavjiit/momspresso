package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.BlogDetailWithArticleModel;
import com.mycity4kids.newmodels.bloggermodel.BlogArticleList.NewArticleListingResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hemant on 9/5/16.
 */
public interface AuthorDetailsAPI {

    @GET("apiparentingstop/getBloggerProfile")
    Call<BlogDetailWithArticleModel> getAuthorDetails(@Query("user_id") String user_id,
                                                      @Query("authorId") String authorId);

    @GET("apiparentingstop/getRecentArticleOfAuthor")
    Call<NewArticleListingResponse> getBloggersRecentArticle(@Query("authorId") String authorId,
                                                             @Query("page") int page);

    @GET("apiparentingstop/getPopularArticleOfAuthor")
    Call<NewArticleListingResponse> getBloggersPopularArticle(@Query("authorId") String authorId,
                                                              @Query("page") int page);

}
