package com.mycity4kids.retrofitAPIsInterfaces

import com.mycity4kids.models.CampaignTypeSelectionData
import com.mycity4kids.models.GetAllPaymentDetails
import com.mycity4kids.models.LanguageSelectionData
import com.mycity4kids.models.campaignmodels.AllCampaignDataResponse
import com.mycity4kids.models.campaignmodels.AllCampaignTotalPayoutResponse
import com.mycity4kids.models.campaignmodels.CampaignDetailResult
import com.mycity4kids.models.campaignmodels.CampaignListTypeResult
import com.mycity4kids.models.campaignmodels.FaqResponse
import com.mycity4kids.models.campaignmodels.GetCampaignSubmissionDetailsResponse
import com.mycity4kids.models.campaignmodels.ParticipateCampaignResponse
import com.mycity4kids.models.campaignmodels.PreProofResponse
import com.mycity4kids.models.campaignmodels.ProofPostModel
import com.mycity4kids.models.campaignmodels.TotalPayoutResponse
import com.mycity4kids.models.request.CampaignParticipate
import com.mycity4kids.models.request.CampaignReferral
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.rewardsmodels.RewardsDetailsResultResonse
import com.mycity4kids.ui.campaign.AddAccountDetailModal
import com.mycity4kids.ui.campaign.BankNameModal
import com.mycity4kids.ui.campaign.BasicResponse
import com.mycity4kids.ui.campaign.DefaultData
import com.mycity4kids.ui.campaign.PaymentModeListModal
import com.mycity4kids.ui.campaign.fragment.CampaignFeedBack
import com.mycity4kids.ui.campaign.fragment.ProofInstructionResult
import com.mycity4kids.ui.mymoneytracker.model.TrackerDataModel
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface CampaignAPI {

    @GET("/rewards/v1/campaigns/generic/")
    fun getDefaultCampaignDetail():
        Observable<BaseResponseGeneric<CampaignDetailResult>>

    @GET("/rewards/v1/campaigns/recommendations/{userId}")
    fun getCampaignList(
        @Path("userId") userId: String? = null,
        @Query("start") start: Int,
        @Query("end") end: Int,
        @Query("v") v: Double
    ):
        Call<AllCampaignDataResponse>

    @GET("/rewards/v1/campaigns/{campaign-id}")
    fun getCampaignDetail(
        @Path("campaign-id") campaignId: Int,
        @Query("v") v: Double
    ):
        Observable<BaseResponseGeneric<CampaignDetailResult>>

    @GET("/rewards/v1/preproofs/{campaign-id}")
    fun getPreProof(@Path("campaign-id") campaignId: Int):
        Call<PreProofResponse>

    @GET("/v1/utilities/faqs/rewards/")
    fun getFaqsList():
        Observable<BaseResponseGeneric<FaqResponse>>

    @GET("rewards/v1/campaigns/submissions/{campaignId}")
    fun getSubmissionDetail(@Path("campaignId") campaignId: Int):
        Observable<BaseResponseGeneric<GetCampaignSubmissionDetailsResponse>>

    @DELETE("rewards/v1/campaigns/proofs/{proofId}")
    fun deleteProofById(@Path("proofId") proofId: Int):
        Observable<BaseResponseGeneric<RewardsDetailsResultResonse>>

    @GET("/payments/v1/account/")
    fun getPaymentModes(): Observable<BaseResponseGeneric<PaymentModeListModal>>

    @GET("/rewards/v1/users/recms/status/{userId}")
    fun getForYouStatus(@Path("userId") userId: String?): Observable<BasicResponse>

    @GET("/payments/v1/banks/")
    fun getAllBankName(): Observable<BaseResponseGeneric<List<BankNameModal>>>

    @PUT("/payments/v1/account/default")
    fun postForDefaultAccount(@Body proofPostModel: ProofPostModel): Observable<BaseResponseGeneric<ProofPostModel>>

    @GET("/payments/v1/account/{id}")
    fun getAllPaymentModeDetails(@Path("id") id: Int): Observable<BaseResponseGeneric<GetAllPaymentDetails>>

    @POST("rewards/v1/campaigns/proofs/")
    fun postProofToServer(@Body proofPostModel: ProofPostModel):
        Observable<BaseResponseGeneric<RewardsDetailsResultResonse>>

    @PUT("rewards/v1/campaigns/proofs/{proofId}")
    fun updateProofToServer(@Path("proofId") proofId: Int, @Body proofPostModel: ProofPostModel):
        Observable<BaseResponseGeneric<RewardsDetailsResultResonse>>

    @POST("rewards/v1/campaigns/participate/")
    fun postRegisterCampaign(@Body body: CampaignParticipate):
        Call<ParticipateCampaignResponse>

    @POST("rewards/v1/campaigns/subscribe/{campaignId}")
    fun postSubscribeCampaign(@Path("campaignId") campaignId: Int):
        Call<ParticipateCampaignResponse>

    @POST("rewards/v1/campaigns/referrals/")
    fun postReferralCampaign(@Body body: CampaignReferral):
        Call<ParticipateCampaignResponse>

    @POST("/payments/v1/account/")
    fun addAccountDetail(@Body addAccountDetailModal: AddAccountDetailModal):
        Observable<BaseResponseGeneric<DefaultData>>

    @GET("/payments/v1/user/pan/")
    fun getPanNumber(): Observable<BaseResponseGeneric<ProofPostModel>>

    @GET("/rewards/v1/campaigns/trackers/{campaignId}")
    fun getTrackerData(@Path("campaignId") campaignId: Int): Observable<BaseResponseGeneric<ArrayList<TrackerDataModel>>>

    @PATCH("/payments/v1/user/pan/")
    fun updatePanNumber(@Body proofPostModel: ProofPostModel): Observable<BaseResponseGeneric<ProofPostModel>>

    @POST("/payments/v1/user/pan/")
    fun addPanNumber(@Body proofPostModel: ProofPostModel): Observable<BaseResponseGeneric<ProofPostModel>>

    @GET("/rewards/v2/users/payments/counts/{userId}")
    fun getTotalPayout(@Path("userId") userId: String? = null):
        Call<TotalPayoutResponse>

    @GET("/rewards/v2/users/payments/{userId}")
    fun getAllCampaignTotalPayout(@Path("userId") userId: String? = null):
        Call<AllCampaignTotalPayoutResponse>

    @GET("rewards/v1/campaigns/proofs/instructions/{campaignId}")
    fun getProofInstruction(@Path("campaignId") campaignId: Int): Observable<BaseResponseGeneric<ProofInstructionResult>>

    @POST("/rewards/v1/campaigns/participations/withdraws/")
    fun unapplyCampaign(@Body body: CampaignParticipate):
        Call<ParticipateCampaignResponse>

    @GET
    fun getAdSlotData(@Url url: String): Call<ResponseBody>

    @GET("rewards/v1/campaigns/feedback/{campaignId}")
    fun getFeedback(@Path("campaignId") campaignId: Int): Observable<BaseResponseGeneric<CampaignFeedBack>>

    @GET("v1/utilities/config/rewadsConfig/")
    fun getCampaignTypeList(): Observable<BaseResponseGeneric<CampaignListTypeResult>>

    //'https://api.momspresso.com/v2/users/916538ec877f455cabd29dacef2f22b4?fn=4'

    @PUT("/v2/users/{userId}")
    fun postCampaignType(
        @Path("userId") userId: String?,
        @Query("fn") fn: Int,
        @Body body: CampaignTypeSelectionData
    ): Call<ResponseBody>
}
