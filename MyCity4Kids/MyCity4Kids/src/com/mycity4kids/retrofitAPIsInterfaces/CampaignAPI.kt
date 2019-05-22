package com.mycity4kids.retrofitAPIsInterfaces

import com.mycity4kids.models.campaignmodels.*
import com.mycity4kids.models.request.CampaignParticipate
import com.mycity4kids.models.request.CampaignReferral
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.ui.campaign.AddAccountDetailModal
import com.mycity4kids.ui.campaign.BankNameModal
import com.mycity4kids.ui.campaign.DefaultData
import com.mycity4kids.ui.campaign.PaymentModeListModal
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

interface CampaignAPI {

    @GET("/rewards/v1/campaigns/recommendations/{userId}")
    fun getCampaignList(@Path("userId") userId: String? = null,
                        @Query("start") start: Int,
                        @Query("end") end: Int,
                        @Query("v") v: Double)
            : Call<AllCampaignDataResponse>


    @GET("/rewards/v1/campaigns/{campaign-id}")
    fun getCampaignDetail(@Path("campaign-id") campaignId: Int,
                          @Query("v") v: Double)
            : Observable<BaseResponseGeneric<CampaignDetailResult>>

    @GET("/v1/utilities/faqs/rewards/")
    fun getFaqsList()
            : Observable<BaseResponseGeneric<FaqResponse>>

    @GET("rewards/v1/campaigns/submissions/{campaignId}")
    fun getSubmissionDetail(@Path("campaignId") campaignId: Int)
            : Observable<BaseResponseGeneric<GetCampaignSubmissionDetailsResponse>>

    @DELETE("rewards/v1/campaigns/proofs/{proofId}")
    fun deleteProofById(@Path("proofId") proofId: Int)
            : Observable<BaseResponseGeneric<RewardsDetailsResultResonse>>

    @GET("/payments/v1/account/")
    fun getPaymentModes(): Observable<BaseResponseGeneric<PaymentModeListModal>>

    @GET("/payments/v1/banks/")
    fun getAllBankName(): Observable<BaseResponseGeneric<List<BankNameModal>>>

    @PUT("/payments/v1/account/default")
    fun postForDefaultAccount(@Body proofPostModel: ProofPostModel): Observable<BaseResponseGeneric<ProofPostModel>>

    @POST("rewards/v1/campaigns/proofs/")
    fun postProofToServer(@Body proofPostModel: ProofPostModel)
            : Observable<BaseResponseGeneric<RewardsDetailsResultResonse>>

    @PUT("rewards/v1/campaigns/proofs/{proofId}")
    fun updateProofToServer(@Path("proofId") proofId: Int, @Body proofPostModel: ProofPostModel)
            : Observable<BaseResponseGeneric<RewardsDetailsResultResonse>>


    @POST("rewards/v1/campaigns/participate/")
    fun postRegisterCampaign(@Body body: CampaignParticipate)
            : Call<ParticipateCampaignResponse>

    @POST("rewards/v1/campaigns/subscribe/{campaignId}")
    fun postSubscribeCampaign(@Path("campaignId") campaignId: Int)
            : Call<ParticipateCampaignResponse>

    @POST("rewards/v1/campaigns/referrals/")
    fun postReferralCampaign(@Body body: CampaignReferral)
            : Call<ParticipateCampaignResponse>

    @POST("/payments/v1/account/")
    fun addAccountDetail(@Body addAccountDetailModal: AddAccountDetailModal)
            : Observable<BaseResponseGeneric<DefaultData>>

    @GET("/payments/v1/user/pan")
    fun getPanNumber(): Observable<BaseResponseGeneric<ProofPostModel>>

    @PATCH("/payments/v1/user/pan/")
    fun updatePanNumber(@Body proofPostModel: ProofPostModel): Observable<BaseResponseGeneric<ProofPostModel>>

    @POST("/payments/v1/user/pan/")
    fun addPanNumber(@Body proofPostModel: ProofPostModel): Observable<BaseResponseGeneric<ProofPostModel>>
}