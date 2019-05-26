package com.mycity4kids.retrofitAPIsInterfaces

import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.response.SetupBlogData
import com.mycity4kids.models.response.UserDetailData
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.models.rewardsmodels.RewardsPersonalResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

interface RewardsAPI {
    @PUT("/rewards/v1/users/{userId}")
    fun sendRewardsapiData(@Path("userId") userId: String, @Body rewardsDetailsResultResonse: RewardsDetailsResultResonse,
                           @Query("fn") pageValue : Int)
            : Observable<BaseResponseGeneric<SetupBlogData>>

    @PUT("/rewards/v1/users/{userId}")
    fun sendRewardsapiDataTest(@Path("userId") userId: String, @Body rewardsDetailsResultResonse: RewardsDetailsResultResonse,
                           @Query("fn") pageValue : Int)
            : Call<RewardsPersonalResponse>

    @PUT("/rewards/v1/users/{userId}")
    fun sendRewardsapiDataForAny(@Path("userId") userId: String, @Body rewardsDetailsResultResonse: RewardsDetailsResultResonse,
                           @Query("fn") pageValue : Int)
            : Observable<RewardsPersonalResponse>



    @GET("/rewards/v1/users/{userId}")
    fun getRewardsapiData(@Path("userId") userId: String, @Query("fn") pageValue : Int)
            : Observable<BaseResponseGeneric<RewardsDetailsResultResonse>>

}