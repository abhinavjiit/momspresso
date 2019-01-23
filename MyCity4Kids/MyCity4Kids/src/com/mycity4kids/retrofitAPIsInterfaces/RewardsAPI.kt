package com.mycity4kids.retrofitAPIsInterfaces

import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import io.reactivex.Observable
import retrofit2.http.*

interface RewardsAPI {
    @PUT("v1/users/{userId}")
    fun sendRewardsapiData(@Path("userId") userId: String, @Body rewardsDetailsResultResonse: RewardsDetailsResultResonse)
            : Observable<BaseResponseGeneric<RewardsDetailsResultResonse>>

    @GET("v1/users/{userId}")
    fun getRewardsapiData(@Path("userId") userId: String)
            : Observable<BaseResponseGeneric<RewardsDetailsResultResonse>>

}