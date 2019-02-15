package com.mycity4kids.retrofitAPIsInterfaces

import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.response.UserDetailData
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import io.reactivex.Observable
import retrofit2.http.*

interface RewardsAPI {
    @PUT("/rewards/v1/users/{userId}")
    fun sendRewardsapiData(@Path("userId") userId: String, @Body rewardsDetailsResultResonse: RewardsDetailsResultResonse,
                           @Query("fn") pageValue : Int)
            : Observable<BaseResponseGeneric<UserDetailData>>

    @GET("/rewards/v1/usersa/{userId}")
    fun getRewardsapiData(@Path("userId") userId: String, @Query("fn") pageValue : Int)
            : Observable<BaseResponseGeneric<RewardsDetailsResultResonse>>

}