package com.mycity4kids.retrofitAPIsInterfaces

import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import io.reactivex.Observable
import retrofit2.http.*

interface RewardsAPI {
    @PUT("/v1/users/{userId}")
    fun sendRewardsapiData(@Path("userId") userId: String, @Body rewardsDetailsResultResonse: RewardsDetailsResultResonse)
            : Observable<BaseResponseGeneric<RewardsDetailsResultResonse>>

    //v1/users/a66ac4980fb54dec85dccb3b894d793a?fn=1
    @GET("/rewards/v1/users/{userId}")
    fun getRewardsapiData(@Path("userId") userId: String, @Query("fn") pageValue : Int)
            : Observable<BaseResponseGeneric<RewardsDetailsResultResonse>>

}