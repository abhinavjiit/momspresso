package com.mycity4kids.ui.momspressotv

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.retrofitAPIsInterfaces.LiveStreamApi
import com.mycity4kids.ui.livestreaming.LiveAndUpcomingStreamsAdapter
import com.mycity4kids.ui.livestreaming.LiveStreamResult
import com.mycity4kids.ui.livestreaming.RecentLiveStreamResponse
import com.mycity4kids.ui.livestreaming.RecentOrUpcomingLiveStreamsHorizontalAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MomspressoTelevisionLiveAndUpcomingTabFragment : BaseFragment(),
    LiveAndUpcomingStreamsAdapter.RecyclerViewClickListener,
    RecentOrUpcomingLiveStreamsHorizontalAdapter.HorizontalRecyclerViewClickListener {

    private var eventTimeRange: Int = 0
    private lateinit var recyclerView: RecyclerView
    private lateinit var noBlogsTextView: TextView
    private var nextPageNumber = 1
    private var isLastPageReached = false
    private var isRequestRunning = false
    private lateinit var progressBar: ProgressBar
    private var pastVisibleItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var mixFeedList = ArrayList<LiveAndUpcomingData>()
    private val liveAndUpcomingStreamsAdapter: LiveAndUpcomingStreamsAdapter by lazy {
        LiveAndUpcomingStreamsAdapter(
            this, this
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.mtv_live_upcoming_tab_fragment, container, false)
        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recyclerView)
        noBlogsTextView = view.findViewById(R.id.noBlogsTextView)

        val llm = LinearLayoutManager(activity)
        llm.orientation = LinearLayoutManager.VERTICAL
        llm.stackFromEnd = true
        recyclerView.layoutManager = llm
        liveAndUpcomingStreamsAdapter.setListData(mixFeedList)
        recyclerView.adapter = liveAndUpcomingStreamsAdapter
        getRecentLiveStream()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = llm.childCount
                    totalItemCount = llm.itemCount
                    pastVisibleItems = llm.findFirstVisibleItemPosition()
                    if (!isRequestRunning && !isLastPageReached) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            isRequestRunning = true
                            loadUpcomingEvents(eventTimeRange)
                        }
                    }
                }
            }
        })
        return view
    }

    private fun getRecentLiveStream() {
        val retrofit = BaseApplication.getInstance().retrofit
        val liveStreamApi = retrofit.create(
            LiveStreamApi::class.java
        )
        isRequestRunning = true
        val livesCall = liveStreamApi.getRecentLiveStreams(null, null)
        livesCall.enqueue(recentLivesResponseCallback)
    }

    private val recentLivesResponseCallback: Callback<RecentLiveStreamResponse> =
        object : Callback<RecentLiveStreamResponse> {
            override fun onResponse(
                call: Call<RecentLiveStreamResponse>,
                response: Response<RecentLiveStreamResponse>
            ) {
                isRequestRunning = false
                progressBar.visibility = View.GONE
                if (response.body() == null) {
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        if (responseData.data.result.events.isNotEmpty()) {
                            val recentLiveList = LiveAndUpcomingData(
                                contentType = AppConstants.CONTENT_TYPE_RECENT_LIVE_STREAM,
                                recentLivesList = responseData.data.result.events
                            )
                            mixFeedList.add(recentLiveList)
                            liveAndUpcomingStreamsAdapter.notifyDataSetChanged()
                        }
                        eventTimeRange = responseData.data.result.event_timerange
                        loadUpcomingEvents(eventTimeRange)
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4KException", Log.getStackTraceString(e))
                    loadUpcomingEvents(0)
                }
            }

            override fun onFailure(
                call: Call<RecentLiveStreamResponse>,
                t: Throwable
            ) {
                isRequestRunning = false
                progressBar.visibility = View.GONE
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4KException", Log.getStackTraceString(t))
                loadUpcomingEvents(0)
            }
        }

    private fun loadUpcomingEvents(eventTimerange: Int) {
        val retrofit = BaseApplication.getInstance().retrofit
        val liveStreamApi = retrofit.create(
            LiveStreamApi::class.java
        )
        val livesCall =
            liveStreamApi.getUpcomingLiveStreams(
                1,
                "[" + AppConstants.LIVE_STREAM_STATUS_UPCOMING + "]",
                System.currentTimeMillis() + eventTimerange,
                null,
                nextPageNumber
            )
        livesCall.enqueue(upcomingLivesResponseCallback)
    }

    private val upcomingLivesResponseCallback: Callback<RecentLiveStreamResponse> =
        object : Callback<RecentLiveStreamResponse> {
            override fun onResponse(
                call: Call<RecentLiveStreamResponse>,
                response: Response<RecentLiveStreamResponse>
            ) {
                if (response.body() == null) {
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {

                        if (responseData.data.result.events.isNotEmpty()) {
                            if (nextPageNumber == 1) {
                                mixFeedList.add(
                                    LiveAndUpcomingData(
                                        contentType = AppConstants.CONTENT_TYPE_CREATE_SECTION
                                    )
                                )
                            }
                            for (i in responseData.data.result.events.indices) {
                                val recentLiveList = LiveAndUpcomingData(
                                    contentType = AppConstants.CONTENT_TYPE_UPCOMING_LIVE_STREAM,
                                    liveStreamResult = responseData.data.result.events[i]
                                )
                                mixFeedList.add(recentLiveList)
                            }
                            nextPageNumber++
                            liveAndUpcomingStreamsAdapter.notifyDataSetChanged()
                        } else {
                            isLastPageReached = true
                            noBlogsTextView.visibility = View.VISIBLE
                        }
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4KException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(
                call: Call<RecentLiveStreamResponse>,
                t: Throwable
            ) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4KException", Log.getStackTraceString(t))
            }
        }

    override fun onClick(view: View, position: Int) {
        mixFeedList[position].liveStreamResult?.id?.let {
            Utils.shareEventTracking(
                activity,
                "Momspresso TV",
                "Live_Android",
                "TVL_LiveTab_Upcoming_Live"
            )
            (activity as BaseActivity).getLiveStreamInfoFromId(
                it
            )
        }
    }

    class LiveAndUpcomingData(
        var recentLivesList: List<LiveStreamResult>? = null,
        val contentType: String = "",
        val liveStreamResult: LiveStreamResult? = null
    )

    override fun onLiveStreamItemClick(view: View, liveStreamResult: LiveStreamResult?) {
        liveStreamResult?.id?.let {
            if (liveStreamResult.status == AppConstants.LIVE_STREAM_STATUS_ONGOING) {
                Utils.shareEventTracking(
                    activity,
                    "Momspresso TV",
                    "Live_Android",
                    "TVL_LiveTab_Live_TopCarousel_Live"
                )
            } else if (liveStreamResult.status == AppConstants.LIVE_STREAM_STATUS_UPCOMING) {
                Utils.shareEventTracking(
                    activity,
                    "Momspresso TV",
                    "Live_Android",
                    "TVL_LiveTab_Upcoming_TopCarousel_Live"
                )
            } else if (liveStreamResult.status == AppConstants.LIVE_STREAM_STATUS_ENDED) {
                Utils.shareEventTracking(
                    activity,
                    "Momspresso TV",
                    "Live_Android",
                    "TVL_LiveTab_Old_TopCarousel_Live"
                )
            }
            (activity as BaseActivity).getLiveStreamInfoFromId(
                it
            )
        }
    }
}
