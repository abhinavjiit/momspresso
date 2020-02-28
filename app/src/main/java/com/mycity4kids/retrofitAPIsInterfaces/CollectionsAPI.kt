package com.mycity4kids.retrofitAPIsInterfaces

import com.mycity4kids.models.ExploreTopicsModel
import com.mycity4kids.models.Topics
import com.mycity4kids.models.collectionsModels.*
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.response.FollowUnfollowUserResponse
import com.mycity4kids.models.response.MixFeedResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface CollectionsAPI {

    @GET("/v1/collections/user/{userId}")
    fun getUserCollectionList(
        @Path("userId") userId: String,
        @Query("start") start: Int,
        @Query("offset") offset: Int,
        @Query("collectionType") collectionType: String? = null
    ): Observable<BaseResponseGeneric<UserCollectionsListModel>>

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

    // api.momspresso.com/v1/collectionItem/addItems/
    @POST("/v1/collectionItem/addItems/")
    fun addMultipleCollectionItem(@Body multipleCollectionItems: ArrayList<UpdateCollectionRequestModel>): Call<BaseResponseGeneric<AddCollectionRequestModel>>

    @GET("/v2/categories")
    suspend fun getShortStoryTopicsAsync(
        @Query("id") id: String,
        @Query("public") isPublic: String
    ): Response<ArrayList<ExploreTopicsModel>>

    @GET("/v2/categories")
    suspend fun getShortStoryChallengesAsync(
        @Query("id") id: String,
        @Query("isActive") isActive: String,
        @Query("public") isPublic: String
    ): Response<ArrayList<Topics>>
}
