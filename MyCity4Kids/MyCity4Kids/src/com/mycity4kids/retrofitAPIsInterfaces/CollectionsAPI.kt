package com.mycity4kids.retrofitAPIsInterfaces

import com.mycity4kids.models.CollectionsModels.*
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.response.FollowUnfollowUserResponse
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
    fun editCollection(@Body updateCollection: UpdateCollectionRequestModel): Observable<BaseResponseGeneric<AddCollectionRequestModel>>

//testingapi.momspresso.com/v1/collections/
    //http://testingapi.momspresso.com/v1/collectionItem/

    @POST("/v1/collectionItem/")
    fun editCollectionItem(@Body updateCollection: AddCollectionRequestModel): Observable<BaseResponseGeneric<AddCollectionRequestModel>>

    @GET("/v1/collections/featured/{userId}")
    fun getFeaturedOnCollections(@Path("userId") userId: String,
                                 @Query("start") start: Int,
                                 @Query("offset") end: Int): Call<FeaturedOnModel>

    @GET("/v1/collections/featuredItem/{userId}")
    fun getFeatureList(@Path("userId") articleId: String,
                       @Query("start") start: Int,
                       @Query("offset") end: Int): Call<FeaturedOnModel>

    @POST("/v1/followedCollections/")
    fun followCollection(@Body followCollectionRequest: FollowCollectionRequestModel): Call<FollowUnfollowUserResponse>

    //http://testingapi.momspresso.com/v1/collections/images/
    @GET("/v1/collections/images/")
    fun getCollectionImages(): Observable<ResponseBody>

    //http://testingapi.momspresso.com/v1/followedCollections/

    @POST("/v1/followedCollections/")
    fun followUnfollowCollection(@Body addCollectionRequestModel: AddCollectionRequestModel): Observable<ResponseBody>

    //http://testingapi.momspresso.com/v1/followedCollections/d97a8ea81b294b62b74e8fa656c0ae4b?start=0&offset=10

    @GET("/v1/followedCollections/{userId}")
    fun getFollowedCollection(@Path("userId") userId: String,
                              @Query("start") start: Int,
                              @Query("offset") offset: Int): Observable<BaseResponseGeneric<UserCollectionsListModel>>


}