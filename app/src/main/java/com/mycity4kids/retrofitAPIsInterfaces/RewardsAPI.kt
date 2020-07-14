package com.mycity4kids.retrofitAPIsInterfaces

import com.mycity4kids.models.campaignmodels.ReferralCodeResult
import com.mycity4kids.models.campaignmodels.UserHandleAvailabilityResponse
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.response.SetupBlogData
import com.mycity4kids.models.response.ShortStoryImageData
import com.mycity4kids.models.response.UserDetailResult
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.models.rewardsmodels.RewardsPersonalResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RewardsAPI {
    @PUT("/rewards/v1/users/{userId}")
    fun sendRewardsapiData(
        @Path("userId") userId: String,
        @Body rewardsDetailsResultResonse: RewardsDetailsResultResonse,
        @Query("fn") pageValue: Int
    ):
        Observable<BaseResponseGeneric<SetupBlogData>>

    @PUT("/rewards/v1/users/{userId}")
    fun sendRewardsapiDataTest(
        @Path("userId") userId: String,
        @Body rewardsDetailsResultResonse: RewardsDetailsResultResonse,
        @Query("fn") pageValue: Int
    ):
        Call<RewardsPersonalResponse>

    @PUT("/rewards/v1/users/{userId}")
    fun sendRewardsapiDataForAny(
        @Path("userId") userId: String,
        @Body rewardsDetailsResultResonse: RewardsDetailsResultResonse,
        @Query("fn") pageValue: Int
    ):
        Observable<RewardsPersonalResponse>

    @GET("/rewards/v1/users/{userId}")
    fun getRewardsapiData(@Path("userId") userId: String, @Query("fn") pageValue: Int):
        Observable<BaseResponseGeneric<RewardsDetailsResultResonse>>

    @GET("/rewards/v1/users/referrals/{userId}")
    fun getReferralCode(@Path("userId") userId: String):
        Observable<BaseResponseGeneric<ReferralCodeResult>>

    @GET("/rewards/v1/users/referrals/validations/{referralCode}")
    fun validateReferralCode(@Path("referralCode") referralCode: String):
        Observable<BaseResponseGeneric<ReferralCodeResult>>

    @GET("v1/users/{userId}")
    fun getUserDetails(@Path("userId") userId: String, @Query("email") required: String):
        Observable<BaseResponseGeneric<UserDetailResult>>

    @GET("v1/users/handle/")
    fun checkUserHandleAvailability(@Query("userHandle") userHandle: String):
        Observable<BaseResponseGeneric<UserHandleAvailabilityResponse>>

    @PUT("/v2/users/{userId}")
    fun sendProfileDataForAny(
        @Path("userId") userId: String,
        @Body userDetailResult: UserDetailResult,
        @Query("fn") pageValue: Int
    ):
        Observable<RewardsPersonalResponse>

    @GET("/article-category-images/category-images/{categoryId}/")
    fun getBackgroundThumbnail(
        @Path("categoryId") categoryId: String,
        @Query("page") pageValue: Int
    ):
        Call<ShortStoryImageData>

    // coroutine
    @GET("/rewards/v1/users/{userId}")
    suspend fun getInstagramHandle(@Path("userId") userId: String, @Query("fn") pageValue: Int):
        BaseResponseGeneric<RewardsDetailsResultResonse>

    @PUT("/rewards/v1/users/{userId}")
    suspend fun sendInstageamHandle(
        @Path("userId") userId: String,
        @Body rewardsDetailsResultResonse: RewardsDetailsResultResonse,
        @Query("fn") pageValue: Int
    ):
        RewardsPersonalResponse
}
