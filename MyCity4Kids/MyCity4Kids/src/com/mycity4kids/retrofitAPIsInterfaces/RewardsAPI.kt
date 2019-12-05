package com.mycity4kids.retrofitAPIsInterfaces

import com.mycity4kids.models.campaignmodels.ReferralCodeResult
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.response.SetupBlogData
import com.mycity4kids.models.response.UserDetailResponse
import com.mycity4kids.models.response.UserDetailResult
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

    @GET("/rewards/v1/users/referrals/{userId}")
    fun getReferralCode(@Path("userId") userId: String)
            : Observable<BaseResponseGeneric<ReferralCodeResult>>

    @GET("/rewards/v1/users/referrals/validations/{referralCode}")
    fun validateReferralCode(@Path("referralCode") referralCode: String)
            : Observable<BaseResponseGeneric<ReferralCodeResult>>

    @GET("v1/users/{userId}")
    fun getUserDetails(@Path("userId") userId: String, @Query("email") required: String)
            : Observable<BaseResponseGeneric<UserDetailResult>>

    @PUT("/v2/users/{userId}")
    fun sendProfileDataForAny(@Path("userId") userId: String, @Body userDetailResult: UserDetailResult,
                                 @Query("fn") pageValue : Int)
            : Observable<RewardsPersonalResponse>

}