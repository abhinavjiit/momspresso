package com.mycity4kids.retrofitAPIsInterfaces

import com.mycity4kids.models.CollectionsModels.AddCollectionRequestModel
import com.mycity4kids.models.CollectionsModels.UpdateCollectionRequestModel
import com.mycity4kids.models.CollectionsModels.UserCollectionsListModel
import com.mycity4kids.models.response.BaseResponseGeneric
import io.reactivex.Observable

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface CollectionsAPI {

    @GET("/v1/collections/user/{userId}")
    fun getUserCollectionList(@Path("userId") userId: String,
                              @Query("start") start: Int,
                              @Query("offset") offset: Int): Observable<BaseResponseGeneric<UserCollectionsListModel>>

    @GET("badges/")
    fun getBadges(@Query("user_id") userId: String): Call<ResponseBody>


    @GET("/v1/collections/user/{userId}")
    fun getUsersCollections(@Path("userId") userId: String,
                            @Query("start") start: Int,
                            @Query("offset") offset: Int): Call<BaseResponseGeneric<UserCollectionsListModel>>

    //http://testingapi.momspresso.com/v1/collections/
    @POST("v1/collections/")
    fun addCollection(@Body addCollectionRequestModel: AddCollectionRequestModel): Observable<BaseResponseGeneric<AddCollectionRequestModel>>

    //http://testingapi.momspresso.com/v1/collectionItem/
    @POST("/v1/collectionItem/")
    fun addCollectionItem(@Body addCollectionRequestModel: UpdateCollectionRequestModel): Observable<BaseResponseGeneric<AddCollectionRequestModel>>


    @GET("/v1/collections/{collectionId}")
    fun getUserCollectionItems(@Path("collectionId") collectionId: String,
                               @Query("start") start: Int,
                               @Query("offset") offset: Int): Observable<BaseResponseGeneric<UserCollectionsListModel>>


    //http://testingapi.momspresso.com/v1/collections/
    @POST("/v1/collections/")
    fun editCollection(@Body updateCollection: UpdateCollectionRequestModel): Observable<BaseResponseGeneric<UpdateCollectionRequestModel>>

//testingapi.momspresso.com/v1/collections/
    //http://testingapi.momspresso.com/v1/collectionItem/

    @POST("/v1/collectionItem/")
    fun editCollectionItem(@Body updateCollection: AddCollectionRequestModel): Observable<BaseResponseGeneric<AddCollectionRequestModel>>

    @GET("/v1/collections/featured/{userId}")
    fun getFeaturedOnCollections(@Path("userId") userId: String,
                                 @Query("start") start: Int,
                                 @Query("offset") end: Int): Call<CollectionFeaturedListModel>

    @GET("/v1/collections/featuredItem/{userId}")
    fun getFeatureList(@Path("userId") articleId: String,
                       @Query("start") start: Int,
                       @Query("offset") end: Int): Call<CollectionFeaturedListModel>

    @POST("/v1/followedCollections/")
    fun followCollection(@Body followCollectionRequest: FollowCollectionRequestModel): Call<FollowUnfollowUserResponse>
}