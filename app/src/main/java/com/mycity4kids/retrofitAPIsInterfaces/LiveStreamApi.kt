package com.mycity4kids.retrofitAPIsInterfaces

import com.mycity4kids.ui.livestreaming.LiveStreamResponse
import com.mycity4kids.ui.livestreaming.RecentLiveStreamResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LiveStreamApi {
    @GET("/events/library/")
    fun getRecentLiveStreams(
        @Query("per_page") perPage: String?,
        @Query("page") page: String?
    ): Call<RecentLiveStreamResponse>

    @GET("/events/{eventId}")
    fun getLiveStreamDetails(
        @Path("eventId") eventId: Int
    ): Call<LiveStreamResponse>

    @GET("/events/slug/{slug}")
    fun getLiveStreamDetailsFromSlug(
        @Path("slug") slug: String
    ): Call<LiveStreamResponse>

    @GET("/events/")
    fun getUpcomingLiveStreams(
        @Query("event_type") eventType: Int?,
        @Query("status") status: String?,
        @Query("start_date") startDate: Long?,
        @Query("end_date") endDate: Long?,
        @Query("page") page: Int?
    ): Call<RecentLiveStreamResponse>
}
