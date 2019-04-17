package com.mycity4kids.retrofitAPIsInterfaces

import com.mycity4kids.models.campaignmodels.AllCampaignDataResponse
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.response.SetupBlogData
import com.mycity4kids.models.response.UserDetailData
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import io.reactivex.Observable
import retrofit2.http.*

interface CampaignAPI {

    @GET("/rewards/v1/campaigns/admin/")
    fun getCampaignList(@Query("start") start: Int,
                        @Query("end") end: Int,
                        @Query("approval_status") approval_status: Int)
            : Observable<BaseResponseGeneric<AllCampaignDataResponse>>

}