package com.mycity4kids.retrofitAPIsInterfaces

import com.mycity4kids.models.collectionsModels.AddCollectionRequestModel
import com.mycity4kids.models.collectionsModels.FeaturedOnModel
import com.mycity4kids.models.collectionsModels.FollowCollectionRequestModel
import com.mycity4kids.models.collectionsModels.TutorialCollectionsListModel
import com.mycity4kids.models.collectionsModels.UpdateCollectionRequestModel
import com.mycity4kids.models.collectionsModels.UserCollectionsListModel
import com.mycity4kids.models.response.ArticleListingResponse
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.response.FollowUnfollowUserResponse
import com.mycity4kids.models.response.MixFeedResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CollectionsAPI {

    @GET("/v1/collections/user/{userId}")
    fun getUserCollectionList(
        @Path("userId") userId: String,
        @Query("start") start: Int,
        @Query("offset") offset: Int,
        @Query("collectionType") collectionType: String? = null
    ): Observable<BaseResponseGeneric<UserCollectionsListModel>>

    @GET("/v1/collections/user/{userId}")
    fun getUserCreatedCollections(
        @Path("userId") userId: String,
        @Query("start") start: Int,
        @Query("offset") offset: Int,
        @Query("collectionType") collectionType: String? = null
    ): Call<BaseResponseGeneric<UserCollectionsListModel>>

    @POST("v1/collections/")
    fun addCollection(@Body addCollectionRequestModel: AddCollectionRequestModel): Observable<BaseResponseGeneric<AddCollectionRequestModel>>

    @POST("/v1/collectionItem/")
    fun addCollectionItem(@Body addCollectionRequestModel: UpdateCollectionRequestModel): Observable<BaseResponseGeneric<AddCollectionRequestModel>>

    @GET("/v1/collections/{collectionId}")
    fun getUserCollectionItems(
        @Path("collectionId") collectionId: String,
        @Query("start") start: Int,
        @Query("offset") offset: Int
    ): Observable<BaseResponseGeneric<UserCollectionsListModel>>

    @GET("/v1/collections/{collectionId}")
    fun getUserCollectionItem(
        @Path("collectionId") collectionId: String,
        @Query("start") start: Int,
        @Query("offset") offset: Int
    ): Call<BaseResponseGeneric<UserCollectionsListModel>>

    @GET("/v1/collections/{collectionId}")
    fun getTutorialCollectionItems(
        @Path("collectionId") collectionId: String,
        @Query("start") start: Int,
        @Query("offset") offset: Int
    ): Observable<BaseResponseGeneric<TutorialCollectionsListModel>>

    @POST("/v1/collections/")
    fun editCollection(@Body updateCollection: UpdateCollectionRequestModel): Observable<BaseResponseGeneric<AddCollectionRequestModel>>

    @POST("/v1/collectionItem/")
    fun editCollectionItem(@Body updateCollection: AddCollectionRequestModel): Observable<BaseResponseGeneric<AddCollectionRequestModel>>

    @GET("/v1/collections/featured/{userId}")
    fun getFeaturedOnCollections(
        @Path("userId") userId: String,
        @Query("start") start: Int,
        @Query("offset") end: Int
    ): Call<FeaturedOnModel>

    @GET("/v1/collections/featuredItem/{contentId}/{contentType}")
    fun getFeatureList(
        @Path("contentId") contentId: String,
        @Path("contentType") contentType: String,
        @Query("start") start: Int,
        @Query("offset") end: Int
    ): Call<MixFeedResponse>

    @POST("/v1/followedCollections/")
    fun followCollection(@Body followCollectionRequest: FollowCollectionRequestModel): Call<FollowUnfollowUserResponse>

    @GET("/v1/collections/images/")
    fun getCollectionImages(): Observable<ResponseBody>

    @POST("/v1/followedCollections/")
    fun followUnfollowCollection(@Body addCollectionRequestModel: AddCollectionRequestModel): Observable<ResponseBody>

    @GET("/v1/followedCollections/{userId}")
    fun getFollowedCollection(
        @Path("userId") userId: String,
        @Query("start") start: Int,
        @Query("offset") offset: Int
    ): Observable<BaseResponseGeneric<UserCollectionsListModel>>

    @POST("/v1/collectionItem/addItems/")
    fun addMultipleCollectionItem(@Body multipleCollectionItems: ArrayList<UpdateCollectionRequestModel>): Call<BaseResponseGeneric<AddCollectionRequestModel>>

    @GET("/winner/content/")
    suspend fun getWinnerArticleChallenge(
        @Query("start") start: Int,
        @Query("size") size: Int,
        @Query("category_id") category_id: String?,
        @Query("content_type") content_type: String?
    ): ArticleListingResponse?
}
