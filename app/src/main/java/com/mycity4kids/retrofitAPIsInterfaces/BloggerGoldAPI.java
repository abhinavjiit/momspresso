package com.mycity4kids.retrofitAPIsInterfaces;

import com.mycity4kids.models.response.AllLeaderboardDataResponse;
import com.mycity4kids.models.response.BloggerRankResponse;
import com.mycity4kids.models.response.LeaderboardDataResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BloggerGoldAPI {

    @GET("/gold/blogger/detail/{userId}")
    Call<BloggerRankResponse> getBloggerGoldRank(@Path("userId") String userId,
            @Query("content_type") int content_type);

    @GET("/gold/leaderboard/{userId}")
    Call<LeaderboardDataResponse> getLeaderboardData(@Path("userId") String userId,
            @Query("start") int start,
            @Query("size") int size,
            @Query("content_type") int content_type);

    @GET("/gold/leaderboard/paginated")
    Call<AllLeaderboardDataResponse> getAllLeaderboardData(@Query("page_no") int page_no,
            @Query("content_type") int content_type);

}
