package com.mycity4kids.ui.fragment

import android.Manifest
import android.accounts.NetworkErrorException
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.analytics.HitBuilders.ScreenViewBuilder
import com.google.android.gms.analytics.Tracker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.Topics
import com.mycity4kids.models.request.ArticleDetailRequest
import com.mycity4kids.models.request.DeleteBookmarkRequest
import com.mycity4kids.models.request.FollowUnfollowUserRequest
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest
import com.mycity4kids.models.response.AddBookmarkResponse
import com.mycity4kids.models.response.ContributorListResponse
import com.mycity4kids.models.response.ContributorListResult
import com.mycity4kids.models.response.MixFeedResponse
import com.mycity4kids.models.response.MixFeedResult
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse
import com.mycity4kids.models.response.SuggestedCreators
import com.mycity4kids.models.response.SuggestedCreatorsResponse
import com.mycity4kids.models.response.SuggestedTopics
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.profile.UserContentAdapter
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI
import com.mycity4kids.retrofitAPIsInterfaces.ContributorListAPI
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI
import com.mycity4kids.retrofitAPIsInterfaces.LiveStreamApi
import com.mycity4kids.retrofitAPIsInterfaces.RecommendationAPI
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI
import com.mycity4kids.retrofitAPIsInterfaces.TorcaiAdsAPI
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI
import com.mycity4kids.ui.activity.ArticleChallengeDetailActivity
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity
import com.mycity4kids.ui.activity.FindFbFriendsActivity
import com.mycity4kids.ui.activity.ParallelFeedActivity
import com.mycity4kids.ui.activity.ShortStoryChallengeDetailActivity
import com.mycity4kids.ui.activity.ShortStoryContainerActivity
import com.mycity4kids.ui.activity.ViewAllCommentsActivity
import com.mycity4kids.ui.adapter.BlogChallengeAdapter
import com.mycity4kids.ui.adapter.ShortStoryChallengesRecyclerAdapter
import com.mycity4kids.ui.adapter.SuggestedCreatorsRecyclerAdapter
import com.mycity4kids.ui.adapter.TopCreatorsRecyclerAdapter
import com.mycity4kids.ui.livestreaming.LiveStreamResult
import com.mycity4kids.ui.livestreaming.RecentLiveStreamResponse
import com.mycity4kids.ui.livestreaming.RecentOrUpcomingLiveStreamsHorizontalAdapter
import com.mycity4kids.ui.momspressotv.MomspressoTelevisionActivity
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.ConnectivityUtils
import com.mycity4kids.utils.DateTimeUtils
import com.mycity4kids.utils.PermissionUtil
import com.mycity4kids.utils.SharingUtils
import com.mycity4kids.utils.StringUtils
import com.mycity4kids.utils.ToastUtils
import com.mycity4kids.vlogs.ContentChallengeSelectionHorizontalAdapter
import com.mycity4kids.vlogs.VideoChallengeSelectionVerticalAdapter
import com.mycity4kids.vlogs.VlogsCategoryWiseChallengesResponse
import com.mycity4kids.widget.MomspressoButtonWidget
import com.mycity4kids.widget.StoryShareCardWidget
import okhttp3.ResponseBody
import org.apache.commons.lang3.text.WordUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ArticleListingFragment : BaseFragment(), View.OnClickListener,
    OnRefreshListener, UserContentAdapter.RecyclerViewClickListener,
    ContentChallengeSelectionHorizontalAdapter.RecyclerViewClickListener,
    ShortStoryChallengesRecyclerAdapter.RecyclerViewClickListener,
    BlogChallengeAdapter.BlogsChallengesClickListener,
    RecentOrUpcomingLiveStreamsHorizontalAdapter.HorizontalRecyclerViewClickListener,
    SuggestedCreatorsRecyclerAdapter.SuggestedCreatorsClickListener,
    TopCreatorsRecyclerAdapter.TopCreatorsClickListener {
    private var mixfeedList: ArrayList<MixFeedResult>? = null
    private var sortType: String? = null
    private var nextPageNumber = 1
    private var isLastPageReached = false
    private var isRequestRunning = false
    private lateinit var progressBar: ProgressBar
    private var chunks: String = "0"
    private var pastVisibleItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private lateinit var loadingView: RelativeLayout
    private lateinit var noBlogsTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private var fromPullToRefresh = false
    private var tabPosition = 0
    private lateinit var mixpanel: MixpanelAPI
    private var tracker: Tracker? = null
    private var userDynamoId: String? = null
    private var position = 0
    private var likeStatus: String? = null
    private var currentShortStoryPosition = 0
    private var isRecommendRequestRunning = false
    private var rootLayout: RelativeLayout? = null
    private lateinit var storyShareCardWidget: StoryShareCardWidget
    private lateinit var shareStoryImageView: ImageView
    private lateinit var sharedStoryItem: MixFeedResult
    private lateinit var shareMedium: String
    private lateinit var mixfeedAdapter: UserContentAdapter
    private lateinit var articleFilterTextView: MomspressoButtonWidget
    private lateinit var storyFilterTextView: MomspressoButtonWidget
    private lateinit var vlogsFilterTextView: MomspressoButtonWidget
    private lateinit var filterContentContainer: LinearLayout
    private lateinit var articleChallengesList: ArrayList<Topics>
    private var categoryWiseChallengeList = ArrayList<Topics>()
    private var followingFeedExtrasFlag = ""

    private val blogChallengeAdapter: BlogChallengeAdapter by lazy {
        BlogChallengeAdapter(articleChallengesList, this)
    }
    private val shortStoryChallengeAdapter: ShortStoryChallengesRecyclerAdapter by lazy {
        ShortStoryChallengesRecyclerAdapter(this)
    }
    private val videoChallengeSelectionVerticalAdapter: VideoChallengeSelectionVerticalAdapter by lazy {
        VideoChallengeSelectionVerticalAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =
            inflater.inflate(R.layout.new_article_layout, container, false)
        recyclerView = rootView.findViewById(R.id.recyclerView)
        shimmerFrameLayout = rootView.findViewById(R.id.shimmer1)
        loadingView =
            rootView.findViewById(R.id.relativeLoadingView)
        noBlogsTextView =
            rootView.findViewById(R.id.noBlogsTextView)
        progressBar =
            rootView.findViewById(R.id.progressBar)
        pullToRefresh = rootView.findViewById(R.id.pullToRefresh)
        rootLayout =
            rootView.findViewById(R.id.rootLayout)

        filterContentContainer = rootView.findViewById(R.id.filterContentContainer)
        articleFilterTextView = rootView.findViewById(R.id.articleFilterTextView)
        storyFilterTextView = rootView.findViewById(R.id.storyFilterTextView)
        vlogsFilterTextView = rootView.findViewById(R.id.vlogsFilterTextView)

        articleFilterTextView.setOnClickListener(this)
        storyFilterTextView.setOnClickListener(this)
        vlogsFilterTextView.setOnClickListener(this)

        articleFilterTextView.setText(
            WordUtils.capitalizeFully(articleFilterTextView.context.getString(R.string.groups_sections_blogs))
        )
        storyFilterTextView.setText(
            WordUtils.capitalizeFully(storyFilterTextView.context.getString(R.string.myprofile_section_short_story_label))
        )
        vlogsFilterTextView.setText(
            WordUtils.capitalizeFully(vlogsFilterTextView.context.getString(R.string.myprofile_section_videos_label))
        )

        articleFilterTextView.isSelected = true
        storyFilterTextView.isSelected = false
        vlogsFilterTextView.isSelected = false

        mixpanel =
            MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN)
        userDynamoId =
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId

        activity?.let {
            rootView.findViewById<View>(R.id.imgLoader)
                .startAnimation(
                    AnimationUtils.loadAnimation(
                        it,
                        R.anim.rotate_indefinitely
                    )
                )
        }

        if (arguments != null) {
            sortType = arguments!!.getString(Constants.SORT_TYPE)
            tabPosition = arguments!!.getInt(Constants.TAB_POSITION)
        }
        val llm =
            LinearLayoutManager(activity)
        llm.orientation = RecyclerView.VERTICAL
        recyclerView.layoutManager = llm
        hitArticleListingApi(sortType)
        if (Constants.KEY_CHALLENGE == (sortType)) {
            articleChallengesList = ArrayList()
            recyclerView.adapter = blogChallengeAdapter
            blogChallengeAdapter.notifyDataSetChanged()
            loadingView.visibility = View.GONE
        } else {
            mixfeedAdapter = UserContentAdapter(this, this, this, this, false)
            mixfeedList = ArrayList()
            mixfeedList = ArrayList()
            nextPageNumber = 1
            mixfeedAdapter.setListData(mixfeedList)
            recyclerView.adapter = mixfeedAdapter
        }

        pullToRefresh.setOnRefreshListener {
            if (Constants.KEY_CHALLENGE == (sortType)) {
                pullToRefresh.isRefreshing = false
            } else {
                mixfeedList!!.clear()
                mixfeedAdapter.notifyDataSetChanged()
                shimmerFrameLayout.visibility = View.VISIBLE
                shimmerFrameLayout.startShimmerAnimation()
                sortType = arguments!!.getString(Constants.SORT_TYPE)
                nextPageNumber = 1
                fromPullToRefresh = true
                hitArticleListingApi(sortType)
                pullToRefresh.isRefreshing = false
            }
        }
        recyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                if (dy > 0) {
                    if (Constants.KEY_CHALLENGE != (sortType)) {
                        visibleItemCount = llm.childCount
                        totalItemCount = llm.itemCount
                        pastVisibleItems = llm.findFirstVisibleItemPosition()
                        if (!isRequestRunning && !isLastPageReached) {
                            if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                                isRequestRunning = true
                                loadingView.visibility = View.VISIBLE
                                hitArticleListingApi(sortType)
                            }
                        }
                    }
                }
            }
        })
        tracker = BaseApplication.getInstance().getTracker(BaseApplication.TrackerName.APP_TRACKER)
        return rootView
    }

    private fun hitArticleListingApi(sortKey: String?) {
        activity?.let {
            if (!ConnectivityUtils.isNetworkEnabled(it)) {
                removeProgressDialog()
                ToastUtils.showToast(
                    it,
                    getString(R.string.error_network)
                )
                return
            }
        }

        if (Constants.KEY_FOLLOWING == sortKey) {
            filterContentContainer.visibility = View.GONE
            val retrofit = BaseApplication.getInstance().retrofit
            val recommendationApi = retrofit.create(
                RecommendationAPI::class.java
            )
            if (fromPullToRefresh) {
                fromPullToRefresh = false
                chunks = "0"
            }
            val from = (nextPageNumber - 1) * LIMIT
            val call = recommendationApi.getFollowingFeed(
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                LIMIT,
                chunks
            )
            progressBar.visibility = View.VISIBLE
            call.enqueue(followingFeedResponseCallback)
        } else if (Constants.KEY_TODAYS_BEST == sortKey) {
            if (nextPageNumber == 1) {
                filterContentContainer.visibility = View.VISIBLE
                val retrofit = BaseApplication.getInstance().retrofit
                val liveStreamApi = retrofit.create(
                    LiveStreamApi::class.java
                )
                val livesCall = liveStreamApi.getRecentLiveStreams(null, null)
                livesCall.enqueue(recentLivesResponseCallback)
            } else {
                loadTodaysBest()
            }
        } else if (Constants.KEY_TRENDING == sortKey) {
            filterContentContainer.visibility = View.VISIBLE
            val retrofit = BaseApplication.getInstance().retrofit
            val topicsApi = retrofit.create(
                TopicsCategoryAPI::class.java
            )
            val from = (nextPageNumber - 1) * LIMIT
            val filterCall = topicsApi.getTrendingFeed(
                from, LIMIT,
                SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()),
                getContentFilters()
            )
            filterCall.enqueue(articleListingResponseCallback)
        } else if (Constants.KEY_CHALLENGE == sortKey) {
            if (getContentFilters() == "0") {
                showProgressDialog("please wait")
                val retrofit = BaseApplication.getInstance().retrofit
                val vlogsListingAndDetailsApi =
                    retrofit.create(
                        VlogsListingAndDetailsAPI::class.java
                    )
                val callRecentVideoArticles =
                    vlogsListingAndDetailsApi
                        .getCategoryDetails(AppConstants.ARTICLE_CHALLENGE_CATEGORY_ID)
                callRecentVideoArticles.enqueue(blogsChallengeResponseCallBack)
            } else if (getContentFilters() == "1") {
                showProgressDialog("please wait")
                val retrofit = BaseApplication.getInstance().retrofit
                val topicsCategoryApi = retrofit.create(
                    TopicsCategoryAPI::class.java
                )
                val call = topicsCategoryApi
                    .momVlogTopics(AppConstants.SHORT_STORY_CHALLENGE_ID)
                call.enqueue(shortStroyChallengeCallBack)
            } else {
                showProgressDialog("please wait")
                if (!ConnectivityUtils.isNetworkEnabled(activity)) {
                    removeProgressDialog()
                    return
                }
                val retrofit = BaseApplication.getInstance().retrofit
                val vlogsListingAndDetailsApi =
                    retrofit.create(
                        VlogsListingAndDetailsAPI::class.java
                    )
                val callRecentVideoArticles =
                    vlogsListingAndDetailsApi.vlogsCategoryWiseChallenges
                callRecentVideoArticles.enqueue(vlogChallengeResponseCallBack)
            }
        } else {
            filterContentContainer.visibility = View.VISIBLE
            val retrofit = BaseApplication.getInstance().retrofit
            val topicsApi = retrofit.create(
                TopicsCategoryAPI::class.java
            )
            val from = (nextPageNumber - 1) * LIMIT + 1
            val filterCall = topicsApi.getRecentFeed(
                from, from + LIMIT - 1,
                SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()),
                getContentFilters()
            )
            filterCall.enqueue(articleListingResponseCallback)
        }
    }

    private fun getContentFilters(): String {
        var filter: String = ""
        if (articleFilterTextView.isSelected) {
            if (filter.isEmpty()) {
                filter = "0"
            } else {
                filter = "$filter,0"
            }
        }
        if (storyFilterTextView.isSelected) {
            if (filter.isEmpty()) {
                filter = "1"
            } else {
                filter = "$filter,1"
            }
        }
        if (vlogsFilterTextView.isSelected) {
            if (filter.isEmpty()) {
                filter = "2"
            } else {
                filter = "$filter,2"
            }
        }
        return filter
    }

    private val shortStroyChallengeCallBack = object : Callback<Topics> {
        override fun onFailure(call: Call<Topics>, t: Throwable) {
            removeProgressDialog()
        }

        override fun onResponse(call: Call<Topics>, response: Response<Topics>) {
            removeProgressDialog()
            if (null == response.body()) {
                return
            }
            try {
                val res = response.body()
                res?.let {
                    shortStoryChallengeAdapter.setListData(it)
                    shortStoryChallengeAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
            }
        }
    }

    private val vlogChallengeResponseCallBack: Callback<VlogsCategoryWiseChallengesResponse> =
        object : Callback<VlogsCategoryWiseChallengesResponse> {
            override fun onResponse(
                call: Call<VlogsCategoryWiseChallengesResponse?>,
                response: Response<VlogsCategoryWiseChallengesResponse?>
            ) {
                removeProgressDialog()
                if (null == response.body()) {
                    val nee =
                        NetworkErrorException(response.raw().toString())
                    FirebaseCrashlytics.getInstance().recordException(nee)
                    return
                }
                if (response.isSuccessful) {
                    try {
                        val responseData = response.body()
                        if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {
                            processVlogChallengesData(responseData.data.result)
                        }
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }
                }
            }

            override fun onFailure(
                call: Call<VlogsCategoryWiseChallengesResponse?>,
                e: Throwable
            ) {
                removeProgressDialog()
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

    private val blogsChallengeResponseCallBack: Callback<Topics> =
        object : Callback<Topics> {
            override fun onResponse(
                call: Call<Topics>,
                response: Response<Topics>
            ) {
                removeProgressDialog()
                if (null == response.body()) {
                    val nee =
                        NetworkErrorException(response.raw().toString())
                    FirebaseCrashlytics.getInstance().recordException(nee)
                    return
                }
                if (response.isSuccessful) {
                    try {
                        val responseData = response.body()
                        responseData?.child?.let {
                            processChallengesData(it)
                        }
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }
                }
            }

            override fun onFailure(
                call: Call<Topics>,
                e: Throwable
            ) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
                removeProgressDialog()
            }
        }

    private fun processVlogChallengesData(catWiseChallengeList: ArrayList<Topics>) {
        categoryWiseChallengeList.clear()
        for (i in catWiseChallengeList.indices) {
            val originalChallengeList = ArrayList<Topics>()
            originalChallengeList.addAll(catWiseChallengeList[i].child)
            categoryWiseChallengeList.add(catWiseChallengeList[i])
            categoryWiseChallengeList[i].child = ArrayList()
            for (j in originalChallengeList.indices) {
                if ("1" == originalChallengeList[j].publicVisibility) {
                    categoryWiseChallengeList[i].child.add(originalChallengeList[j])
                }
            }
        }
        videoChallengeSelectionVerticalAdapter.setListData(categoryWiseChallengeList)
        videoChallengeSelectionVerticalAdapter.setSource("vlogsListing")
        videoChallengeSelectionVerticalAdapter.notifyDataSetChanged()
    }

    private fun processChallengesData(catWiseChallengeList: ArrayList<Topics>) {
        for (i in 0 until catWiseChallengeList.size) {
            if ("1" == catWiseChallengeList[i].publicVisibility) {
                articleChallengesList.add(catWiseChallengeList[i])
            }
        }
        blogChallengeAdapter.notifyDataSetChanged()
    }

    private val followingFeedResponseCallback: Callback<MixFeedResponse?> =
        object : Callback<MixFeedResponse?> {
            override fun onResponse(
                call: Call<MixFeedResponse?>,
                response: Response<MixFeedResponse?>
            ) {
                if (!isAdded) {
                    return
                }
                progressBar.visibility = View.GONE
                loadingView.visibility = View.GONE
                isRequestRunning = false
                if (null == response.body()) {
                    val nee =
                        NetworkErrorException(response.raw().toString())
                    FirebaseCrashlytics.getInstance().recordException(nee)
                    activity?.let {
                        ToastUtils.showToast(
                            it,
                            getString(R.string.server_went_wrong)
                        )
                    }
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS.equals(
                            responseData.status,
                            ignoreCase = true
                        )) {
                        processFollowingFeedData(responseData)
                        shimmerFrameLayout.stopShimmerAnimation()
                        shimmerFrameLayout.visibility = View.GONE
                    } else {
                        activity?.let {
                            ToastUtils.showToast(
                                it,
                                responseData.reason
                            )
                        }
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                    activity?.let {
                        ToastUtils.showToast(
                            it,
                            getString(R.string.went_wrong)
                        )
                    }
                }
            }

            override fun onFailure(
                call: Call<MixFeedResponse?>,
                t: Throwable
            ) {
                progressBar.visibility = View.GONE
                loadingView.visibility = View.GONE
                isRequestRunning = false
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
                activity?.let {
                    ToastUtils.showToast(
                        it,
                        getString(R.string.went_wrong)
                    )
                }
            }
        }

    private fun processFollowingFeedData(responseData: MixFeedResponse?) {
        try {
            if (responseData!!.data!!.result.isNullOrEmpty() && mixfeedList.isNullOrEmpty()) {
                loadEmptyStateForFollowingFeed()
                return
            }
            val dataList =
                responseData.data!!.result as ArrayList<MixFeedResult>?
            if (dataList.isNullOrEmpty()) {
                isLastPageReached = true
                if (mixfeedList.isNullOrEmpty()) {
                    mixfeedAdapter.setListData(mixfeedList)
                    mixfeedAdapter.notifyDataSetChanged()
                }
            } else {
                noBlogsTextView.visibility = View.GONE
                if (responseData.data.chunks.isNullOrBlank()) {
                    isLastPageReached = true
                } else {
                    AppUtils.updateFollowingStatusMixFeed(dataList)
                    mixfeedList!!.addAll(dataList)
                    if (chunks == "0") {
                        loadFollowingFeedExtras()
                    }
                    chunks = responseData.data.chunks!!
                }
                mixfeedAdapter.setListData(mixfeedList)
                mixfeedAdapter.notifyDataSetChanged()
            }
        } catch (ex: Exception) {
            loadingView.visibility = View.GONE
            FirebaseCrashlytics.getInstance().recordException(ex)
            Log.d("MC4kException", Log.getStackTraceString(ex))
        }
    }

    private fun loadEmptyStateForFollowingFeed() {
        Utils.shareEventTracking(
            activity,
            "My Feed",
            "Onboarding_Android",
            "MyFeed_ES"
        )
        val item = MixFeedResult(
            contentType = AppConstants.CONTENT_TYPE_FOLLOW_TOPICS_AND_CREATORS
        )
        mixfeedList?.add(item)
        mixfeedAdapter.notifyDataSetChanged()
        loadCreatorsBasedOnLangwiseRanking(1)
    }

    private fun loadFollowingFeedExtras() {
        if (!SharedPrefUtils.getFollowingTopicsJson(BaseApplication.getAppContext()).isNullOrEmpty() &&
            !SharedPrefUtils.getFollowingJson(BaseApplication.getAppContext()).isNullOrEmpty()) {
            Log.e("ndkwandkwdkn", "----- TopicsAndFriends -----")
            followingFeedExtrasFlag = "TopicsAndFriends"
            loadCreatorsBasedOnFollowedTopics(3)
        } else if (!SharedPrefUtils.getFollowingTopicsJson(BaseApplication.getAppContext()).isNullOrEmpty()) {
            Log.e("ndkwandkwdkn", "----- Topics -----")
            followingFeedExtrasFlag = "Topics"
            loadCreatorsBasedOnFollowedTopics(3)
        } else if (!SharedPrefUtils.getFollowingJson(BaseApplication.getAppContext()).isNullOrEmpty()) {
            Log.e("ndkwandkwdkn", "----- Friends -----")
            followingFeedExtrasFlag = "Friends"
            loadCreatorsBasedFriendsFollowing(3)
        } else {
            Log.e("ndkwandkwdkn", "----- NONE -----")
        }
    }

    private fun loadCreatorsBasedOnFollowedTopics(pos: Int) {
        Log.e("ndkwandkwdkn", "----- loadCreatorsBasedOnFollowedTopics -----")
        val retro = BaseApplication.getInstance().retrofit
        val followAPI: FollowAPI = retro.create(FollowAPI::class.java)
        val call = followAPI.getSuggestedCreators(
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            1,
            0,
            10
        )
        call.enqueue(object : Callback<SuggestedCreatorsResponse> {
            override fun onResponse(
                call: Call<SuggestedCreatorsResponse>,
                response: Response<SuggestedCreatorsResponse>
            ) {
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        val item = MixFeedResult(
                            contentType = AppConstants.CONTENT_TYPE_SUGGESTED_CREATORS_BASED_ON_TOPICS,
                            suggestedCreatorList = responseData.data.result.suggestion
                        )
                        if (mixfeedList?.size!! > pos) {
                            if (!responseData.data.result.suggestion.isNullOrEmpty()) {
                                mixfeedList?.add(
                                    pos,
                                    item
                                )
                                loadCreatorsBasedOnUserActivity(7)
                            } else {
                                loadCreatorsBasedOnUserActivity(6)
                            }
                        } else {
                            if (!responseData.data.result.suggestion.isNullOrEmpty()) {
                                mixfeedList?.add(
                                    item
                                )
                            }
                            loadCreatorsBasedOnUserActivity(0)
                        }
                        mixfeedAdapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d(
                        "MC4kException",
                        Log.getStackTraceString(e)
                    )
                }
            }

            override fun onFailure(call: Call<SuggestedCreatorsResponse>, t: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d(
                    "MC4kException",
                    Log.getStackTraceString(t)
                )
            }
        })
    }

    private fun loadCreatorsBasedOnUserActivity(pos: Int) {
        Log.e("ndkwandkwdkn", "----- loadCreatorsBasedOnUserActivity -----")
        val retro = BaseApplication.getInstance().retrofit
        val followAPI: FollowAPI = retro.create(FollowAPI::class.java)
        val call = followAPI.getSuggestedCreators(
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            2,
            0,
            10
        )
        call.enqueue(object : Callback<SuggestedCreatorsResponse> {
            override fun onResponse(
                call: Call<SuggestedCreatorsResponse>,
                response: Response<SuggestedCreatorsResponse>
            ) {
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        val item = MixFeedResult(
                            contentType = AppConstants.CONTENT_TYPE_SUGGESTED_CREATORS_BASED_ON_ACTIVITY,
                            suggestedCreatorList = responseData.data.result.suggestion
                        )
                        if (mixfeedList?.size!! > pos) {
                            if (!responseData.data.result.suggestion.isNullOrEmpty()) {
                                mixfeedList?.add(
                                    pos,
                                    item
                                )
                                if (followingFeedExtrasFlag == "TopicsAndFriends") {
                                    loadCreatorsBasedFriendsFollowing(13)
                                } else if (followingFeedExtrasFlag == "Topics") {
                                    loadCreatorsBasedOnLangwiseRanking(13)
                                }
                            } else {
                                if (followingFeedExtrasFlag == "TopicsAndFriends") {
                                    loadCreatorsBasedFriendsFollowing(12)
                                } else if (followingFeedExtrasFlag == "Topics") {
                                    loadCreatorsBasedOnLangwiseRanking(12)
                                }
                            }
                        } else {
                            if (!responseData.data.result.suggestion.isNullOrEmpty()) {
                                mixfeedList?.add(
                                    item
                                )
                            }
                            if (followingFeedExtrasFlag == "TopicsAndFriends") {
                                loadCreatorsBasedFriendsFollowing(0)
                            } else if (followingFeedExtrasFlag == "Topics") {
                                loadCreatorsBasedOnLangwiseRanking(0)
                            }
                        }
                        mixfeedAdapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d(
                        "MC4kException",
                        Log.getStackTraceString(e)
                    )
                }
            }

            override fun onFailure(call: Call<SuggestedCreatorsResponse>, t: Throwable) {

            }
        })
    }

    private fun loadCreatorsBasedFriendsFollowing(pos: Int) {
        Log.e("ndkwandkwdkn", "----- loadCreatorsBasedFriendsFollowing -----")
        val retro = BaseApplication.getInstance().retrofit
        val followAPI: FollowAPI = retro.create(FollowAPI::class.java)
        val call = followAPI.getSuggestedCreators(
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            3,
            0,
            10,
            2
        )
        call.enqueue(object : Callback<SuggestedCreatorsResponse> {
            override fun onResponse(
                call: Call<SuggestedCreatorsResponse>,
                response: Response<SuggestedCreatorsResponse>
            ) {
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        val item = MixFeedResult(
                            contentType = AppConstants.CONTENT_TYPE_SUGGESTED_CREATORS_FOLLOWED_BY_FRIENDS,
                            suggestedCreatorList = responseData.data.result.suggestion
                        )
                        if (followingFeedExtrasFlag == "TopicsAndFriends") {
                            if (mixfeedList?.size!! > pos) {
                                if (!responseData.data.result.suggestion.isNullOrEmpty()) {
                                    mixfeedList?.add(
                                        pos,
                                        item
                                    )
                                }
                            } else {
                                if (!responseData.data.result.suggestion.isNullOrEmpty()) {
                                    mixfeedList?.add(
                                        item
                                    )
                                }
                            }
                        } else if (followingFeedExtrasFlag == "Friends") {
                            if (mixfeedList?.size!! > pos) {
                                if (!responseData.data.result.suggestion.isNullOrEmpty()) {
                                    mixfeedList?.add(
                                        pos,
                                        item
                                    )
                                    loadSuggestedTopics(7)
                                } else {
                                    loadSuggestedTopics(6)
                                }
                            } else {
                                if (!responseData.data.result.suggestion.isNullOrEmpty()) {
                                    mixfeedList?.add(
                                        item
                                    )
                                }
                                loadSuggestedTopics(0)
                            }
                        }

                        mixfeedAdapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d(
                        "MC4kException",
                        Log.getStackTraceString(e)
                    )
                }
            }

            override fun onFailure(call: Call<SuggestedCreatorsResponse>, t: Throwable) {

            }
        })
    }

    private fun loadSuggestedTopics(pos: Int) {
        Log.e("ndkwandkwdkn", "----- loadSuggestedTopics -----")
        val retro = BaseApplication.getInstance().retrofit
        val followAPI: FollowAPI = retro.create(FollowAPI::class.java)
        val call = followAPI.getSuggestedTopics(
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            0,
            10,
            1
        )
        call.enqueue(object : Callback<SuggestedTopics> {
            override fun onResponse(
                call: Call<SuggestedTopics>,
                response: Response<SuggestedTopics>
            ) {
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        val item = MixFeedResult(
                            contentType = AppConstants.CONTENT_TYPE_SUGGESTED_TOPICS,
                            suggestedTopicsList = responseData.data.result.suggestion
                        )
                        if (mixfeedList?.size!! > pos) {
                            if (!responseData.data.result.suggestion.isNullOrEmpty()) {
                                mixfeedList?.add(
                                    pos,
                                    item
                                )
                                loadCreatorsBasedOnLangwiseRanking(13)
                            } else {
                                loadCreatorsBasedOnLangwiseRanking(12)
                            }
                        } else {
                            if (!responseData.data.result.suggestion.isNullOrEmpty()) {
                                mixfeedList?.add(
                                    item
                                )
                            }
                            loadCreatorsBasedOnLangwiseRanking(0)
                        }
                        mixfeedAdapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d(
                        "MC4kException",
                        Log.getStackTraceString(e)
                    )
                }
            }

            override fun onFailure(call: Call<SuggestedTopics>, t: Throwable) {

            }
        })
    }

    private fun loadCreatorsBasedOnLangwiseRanking(pos: Int) {
        val retrofit = BaseApplication.getInstance().retrofit
        val contributorListApi =
            retrofit.create(
                ContributorListAPI::class.java
            )
        val call = contributorListApi
            .getContributorList(
                10,
                2,
                AppConstants.USER_TYPE_BLOGGER,
                "" + AppUtils.getLangKey(),
                ""
            )
        call.enqueue(object : Callback<ContributorListResponse> {
            override fun onResponse(
                call: Call<ContributorListResponse>,
                response: Response<ContributorListResponse>
            ) {
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        val bloggersList = response.body()?.data?.result
                        AppUtils.updateFollowingStatusContributorList(bloggersList)

                        val item = MixFeedResult(
                            contentType = AppConstants.CONTENT_TYPE_SUGGESTED_CREATORS_TOP_RANKERS,
                            topCreatorList = responseData.data.result
                        )
                        if (mixfeedList?.size!! > pos) {
                            if (!responseData.data.result.isNullOrEmpty()) {
                                mixfeedList?.add(
                                    pos,
                                    item
                                )
                            }
                        } else {
                            if (!responseData.data.result.isNullOrEmpty()) {
                                mixfeedList?.add(
                                    item
                                )
                            }
                        }
                        mixfeedAdapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d(
                        "MC4kException",
                        Log.getStackTraceString(e)
                    )
                }
            }

            override fun onFailure(call: Call<ContributorListResponse>, t: Throwable) {

            }
        })
    }

    private val recentLivesResponseCallback: Callback<RecentLiveStreamResponse> =
        object : Callback<RecentLiveStreamResponse> {
            override fun onResponse(
                call: Call<RecentLiveStreamResponse>,
                response: Response<RecentLiveStreamResponse>
            ) {
                isRequestRunning = false
                progressBar.visibility = View.GONE
                loadingView.visibility = View.GONE
                if (response.body() == null) {
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        if (responseData.data.result.events.isNotEmpty()) {
                            val recentLiveList = MixFeedResult(
                                contentType = AppConstants.CONTENT_TYPE_RECENT_LIVE_STREAM,
                                recentLiveStreamsList = responseData.data.result.events
                            )
                            mixfeedList?.add(recentLiveList)
                        }
                        loadTodaysBest()
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4KException", Log.getStackTraceString(e))
                    loadTodaysBest()
                }
            }

            override fun onFailure(
                call: Call<RecentLiveStreamResponse>,
                t: Throwable
            ) {
                loadingView.visibility = View.GONE
                isRequestRunning = false
                progressBar.visibility = View.GONE
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4KException", Log.getStackTraceString(t))
                loadTodaysBest()
            }
        }

    private fun loadTodaysBest() {
        val from = (nextPageNumber - 1) * LIMIT + 1
        val retrofit = BaseApplication.getInstance().retrofit
        val topicsApi = retrofit.create(
            TopicsCategoryAPI::class.java
        )
        val filterCall = topicsApi.getTodaysBestMixedFeed(
            DateTimeUtils.getKidsDOBNanoMilliTimestamp("" + System.currentTimeMillis()),
            from, from + LIMIT - 1,
            SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()),
            getContentFilters()
        )
        filterCall.enqueue(articleListingResponseCallback)
    }

    private val articleListingResponseCallback: Callback<MixFeedResponse?> =
        object : Callback<MixFeedResponse?> {
            override fun onResponse(
                call: Call<MixFeedResponse?>,
                response: Response<MixFeedResponse?>
            ) {
                isRequestRunning = false
                progressBar.visibility = View.GONE
                loadingView.visibility = View.GONE
                if (response.body() == null) {
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        processArticleListingResponse(responseData)
                        shimmerFrameLayout.stopShimmerAnimation()
                        shimmerFrameLayout.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4KException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(
                call: Call<MixFeedResponse?>,
                t: Throwable
            ) {
                loadingView.visibility = View.GONE
                isRequestRunning = false
                progressBar.visibility = View.GONE
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4KException", Log.getStackTraceString(t))
            }
        }

    private fun processArticleListingResponse(responseData: MixFeedResponse?) {
        val dataList =
            responseData!!.data!!.result as ArrayList<MixFeedResult>?
        if (dataList!!.size == 0) {
            isLastPageReached = false
            if (null != mixfeedList && mixfeedList!!.isNotEmpty()) {
                // No more next results for search from pagination
                isLastPageReached = true
            } else { // No results for search
                noBlogsTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                noBlogsTextView.text = getString(R.string.no_articles_found)
                mixfeedList = dataList
                mixfeedAdapter.setListData(mixfeedList)
                mixfeedAdapter.notifyDataSetChanged()
            }
        } else {
            noBlogsTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            AppUtils.updateFollowingStatusMixFeed(dataList)
            mixfeedList!!.addAll(dataList)
            mixfeedAdapter.setListData(mixfeedList)
            nextPageNumber += 1
            mixfeedAdapter.notifyDataSetChanged()
            if (nextPageNumber == 2) {
                loadTorcaiAds()
                if (Constants.KEY_TODAYS_BEST == sortType) {
                    loadUpcomingLiveStreams()
                }
            }
        }
    }

    private fun loadTorcaiAds() {
        val retro = BaseApplication.getInstance().retrofit
        val torcaiAdsApi: TorcaiAdsAPI = retro.create(
            TorcaiAdsAPI::class.java
        )
        val topAdsCall = torcaiAdsApi.getTorcaiAd(
            AppUtils.getAdSlotId("CAT", ""),
            "www.momspresso.com",
            SharedPrefUtils.getPublicIpAddress(BaseApplication.getAppContext()),
            "1",
            "Momspresso",
            AppUtils.getAppVersion(BaseApplication.getAppContext()),
            "https://play.google.com/store/apps/details?id=com.mycity4kids&hl=en_IN", "mobile",
            SharedPrefUtils.getAdvertisementId(BaseApplication.getAppContext()),
            "" + System.getProperty("http.agent")
        )
        topAdsCall.enqueue(torcaiAdResponseCallback)
    }

    private val torcaiAdResponseCallback: Callback<ResponseBody> =
        object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                var resData: String? = null
                try {
                    if (response.body() != null) {
                        resData = String(response.body()!!.bytes())
                        val jsonObject = JSONObject(resData)
                        val jsonArray = jsonObject.getJSONArray("response")
                        val html =
                            jsonArray.getJSONObject(0).getJSONObject("response").getString("adm")
                                .replace("\"//".toRegex(), "\"https://")
                        Log.e("HTML CONTENT", "html == $html")
                        if (mixfeedList?.get(0)?.contentType == AppConstants.CONTENT_TYPE_RECENT_LIVE_STREAM) {
                            mixfeedList?.add(
                                4,
                                MixFeedResult(
                                    contentType = AppConstants.CONTENT_TYPE_TORCAI_ADS,
                                    torcaiAdsData = html
                                )
                            )
                        } else {
                            mixfeedList?.add(
                                3,
                                MixFeedResult(
                                    contentType = AppConstants.CONTENT_TYPE_TORCAI_ADS,
                                    torcaiAdsData = html
                                )
                            )
                        }
                        mixfeedAdapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d(
                        "FileNotFoundException",
                        Log.getStackTraceString(e)
                    )
                }
            }

            override fun onFailure(
                call: Call<ResponseBody?>,
                t: Throwable
            ) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("FileNotFoundException", Log.getStackTraceString(t))
            }
        }

    private fun loadUpcomingLiveStreams() {
        val retrofit = BaseApplication.getInstance().retrofit
        val liveStreamApi = retrofit.create(
            LiveStreamApi::class.java
        )
        val livesCall =
            liveStreamApi.getUpcomingLiveStreams(
                1,
                "[" + AppConstants.LIVE_STREAM_STATUS_UPCOMING + "]",
                System.currentTimeMillis(),
                null,
                1
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
                            val recentLiveList = MixFeedResult(
                                contentType = AppConstants.CONTENT_TYPE_UPCOMING_LIVE_STREAM,
                                recentLiveStreamsList = responseData.data.result.events
                            )
                            when {
                                mixfeedList?.get(3)?.contentType == AppConstants.CONTENT_TYPE_TORCAI_ADS -> {
                                    mixfeedList?.add(8, recentLiveList)
                                }
                                mixfeedList?.get(4)?.contentType == AppConstants.CONTENT_TYPE_TORCAI_ADS -> {
                                    mixfeedList?.add(9, recentLiveList)
                                }
                                else -> {
                                    mixfeedList?.add(7, recentLiveList)
                                }
                            }
                            mixfeedAdapter.notifyDataSetChanged()
                        } else {
                            val item = MixFeedResult(
                                contentType = AppConstants.CONTENT_TYPE_FOLLOW_TOPICS_AND_CREATORS
                            )
                            if (SharedPrefUtils.getFollowingTopicsJson(BaseApplication.getAppContext()).isNullOrEmpty() || SharedPrefUtils.getFollowingJson(
                                    BaseApplication.getAppContext()
                                ).isNullOrEmpty()) {
                                when {
                                    mixfeedList?.get(3)?.contentType == AppConstants.CONTENT_TYPE_TORCAI_ADS -> {
                                        mixfeedList?.add(8, item)
                                    }
                                    mixfeedList?.get(4)?.contentType == AppConstants.CONTENT_TYPE_TORCAI_ADS -> {
                                        mixfeedList?.add(9, item)
                                    }
                                    else -> {
                                        mixfeedList?.add(7, item)
                                    }
                                }
                            }
                            mixfeedAdapter.notifyDataSetChanged()
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

    override fun onRefresh() {
        activity?.let {
            if (!ConnectivityUtils.isNetworkEnabled(it)) {
                removeProgressDialog()
                ToastUtils.showToast(
                    it,
                    getString(R.string.error_network)
                )
                return
            }
        }
        isLastPageReached = false
        chunks = "0"
        nextPageNumber = 1
        hitArticleListingApi(sortType)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
            }
            else -> {
            }
        }
        return true
    }

    private fun filterTags(tagObjectList: ArrayList<Map<String, String>>?) {
        val tagList = ArrayList<String>()
        for (i in tagObjectList!!.indices) {
            for ((key) in tagObjectList[i]) {
                if (key.startsWith("category-")) {
                    tagList.add(key)
                }
            }
        }
        val hashtags = AppUtils.getHasTagFromCategoryList(tagList)
        AppUtils.copyToClipboard(hashtags)
        if (isAdded) {
            ToastUtils.showToast(
                activity,
                activity!!.getString(R.string.all_insta_share_clipboard_msg)
            )
        }
    }

    private fun recommendUnrecommendArticleApi(
        status: String,
        articleId: String,
        authorId: String,
        author: String
    ) {
        Utils.pushLikeStoryEvent(
            activity, "ArticleListingFragment", userDynamoId + "", articleId,
            "$authorId~$author"
        )
        isRecommendRequestRunning = true
        val recommendUnrecommendArticleRequest =
            RecommendUnrecommendArticleRequest()
        recommendUnrecommendArticleRequest.articleId = articleId
        recommendUnrecommendArticleRequest.status = likeStatus
        val retro = BaseApplication.getInstance().retrofit
        val articleDetailsApi = retro.create(
            ArticleDetailsAPI::class.java
        )
        val recommendUnrecommendArticle =
            articleDetailsApi
                .recommendUnrecommendArticle(recommendUnrecommendArticleRequest)
        recommendUnrecommendArticle.enqueue(recommendUnrecommendArticleResponseCallback)
    }

    private val recommendUnrecommendArticleResponseCallback: Callback<RecommendUnrecommendArticleResponse?> =
        object : Callback<RecommendUnrecommendArticleResponse?> {
            override fun onResponse(
                call: Call<RecommendUnrecommendArticleResponse?>,
                response: Response<RecommendUnrecommendArticleResponse?>
            ) {
                isRecommendRequestRunning = false
                if (null == response.body()) {
                    if (!isAdded) {
                        return
                    }
                    ToastUtils.showToast(
                        activity,
                        getString(R.string.server_went_wrong)
                    )
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        if (likeStatus == "1") {
                            if (!responseData.data.isEmpty()) {
                                mixfeedList!![currentShortStoryPosition].likesCount =
                                    (mixfeedList!![currentShortStoryPosition].likesCount +
                                        1)
                            }
                            mixfeedList!![currentShortStoryPosition].isLiked = true
                        } else {
                            if (!responseData.data.isEmpty()) {
                                mixfeedList!![currentShortStoryPosition].likesCount =
                                    (mixfeedList!![currentShortStoryPosition].likesCount -
                                        1)
                            }
                            mixfeedList!![currentShortStoryPosition].isLiked = false
                        }
                        mixfeedAdapter.notifyDataSetChanged()
                        if (isAdded) {
                            ToastUtils.showToast(
                                activity,
                                responseData.reason
                            )
                        }
                    } else {
                        if (isAdded) {
                            ToastUtils.showToast(
                                activity,
                                getString(R.string.server_went_wrong)
                            )
                        }
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                    if (isAdded) {
                        ToastUtils.showToast(
                            activity,
                            getString(R.string.went_wrong)
                        )
                    }
                }
            }

            override fun onFailure(
                call: Call<RecommendUnrecommendArticleResponse?>,
                t: Throwable
            ) {
                isRecommendRequestRunning = false
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.articleFilterTextView -> {
                articleFilterTextView.isSelected = true
                storyFilterTextView.isSelected = false
                vlogsFilterTextView.isSelected = false
                filterCurrentFeed()
            }
            R.id.storyFilterTextView -> {
                articleFilterTextView.isSelected = false
                storyFilterTextView.isSelected = true
                vlogsFilterTextView.isSelected = false
                filterCurrentFeed()
            }
            R.id.vlogsFilterTextView -> {
                articleFilterTextView.isSelected = false
                storyFilterTextView.isSelected = false
                vlogsFilterTextView.isSelected = true
                filterCurrentFeed()
            }
            else -> {
            }
        }
    }

    private fun filterCurrentFeed() {
        if (Constants.KEY_CHALLENGE == sortType) {
            when (getContentFilters()) {
                "0" -> {
                    Utils.shareEventTracking(
                        activity,
                        "Home screen",
                        "Blog_Challenges_Android",
                        "H_Show_BCListing_Challenge"
                    )
                    articleChallengesList.clear()
                    recyclerView.adapter = blogChallengeAdapter
                }
                "1" -> {
                    Utils.shareEventTracking(
                        activity,
                        "Home screen",
                        "Story_Challenges_Android",
                        "H_Show_SCListing_Challenge"
                    )
                    recyclerView.adapter = shortStoryChallengeAdapter
                }
                "2" -> {
                    Utils.shareEventTracking(
                        activity,
                        "Home screen",
                        "Vlog_Challenges_Android",
                        "H_Show_VCListing_Challenge"
                    )
                    recyclerView.adapter = videoChallengeSelectionVerticalAdapter
                }
                else
                -> {
                }
            }
        } else {
            mixfeedList!!.clear()
            mixfeedAdapter.notifyDataSetChanged()
            shimmerFrameLayout.visibility = View.VISIBLE
            shimmerFrameLayout.startShimmerAnimation()
            nextPageNumber = 1
        }
        when (sortType) {
            Constants.KEY_CHALLENGE -> {
                logFilterEvent("ChallengeScreen")
            }
            Constants.KEY_RECENT -> {
                logFilterEvent("RecentScreen")
            }
            Constants.KEY_TODAYS_BEST -> {
                logFilterEvent("TodaysBestScreen")
            }
        }
        hitArticleListingApi(sortType)
    }

    private fun logFilterEvent(screenName: String) {
        when {
            getContentFilters() == "0" -> {
                if (screenName == "TodaysBestScreen") {
                    Utils.shareEventTracking(
                        activity,
                        "Home screen",
                        "Home_Android",
                        "TB_Blog_TN_Home"
                    )
                }
            }
            getContentFilters() == "1" -> {
                if (screenName == "TodaysBestScreen") {
                    Utils.shareEventTracking(
                        activity,
                        "Home screen",
                        "Home_Android",
                        "TB_100WS_TN_Home"
                    )
                }
            }
            getContentFilters() == "2" -> {
                if (screenName == "TodaysBestScreen") {
                    Utils.shareEventTracking(
                        activity,
                        "Home screen",
                        "Home_Android",
                        "TB_Vlog_TN_Home"
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shimmerFrameLayout.startShimmerAnimation()
        try {
            if (!StringUtils.isNullOrEmpty(sortType)) {
                when {
                    Constants.KEY_RECENT.equals(
                        sortType,
                        ignoreCase = true
                    ) -> {
                        tracker!!.setScreenName("RecentScreen")
                        Utils.pushOpenScreenEvent(
                            activity, "RecentScreen",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId + ""
                        )
                    }
                    Constants.KEY_TODAYS_BEST.equals(
                        sortType,
                        ignoreCase = true
                    ) -> {
                        tracker!!.setScreenName("TodaysBestScreen")
                        Utils.pushOpenScreenEvent(
                            activity, "TodaysBestScreen",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId + ""
                        )
                    }
                    Constants.KEY_TRENDING.equals(
                        sortType,
                        ignoreCase = true
                    ) -> {
                        tracker!!.setScreenName("TrendingScreen")
                        Utils.pushOpenScreenEvent(
                            activity, "TrendingScreen",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId + ""
                        )
                    }
                    Constants.KEY_FOLLOWING.equals(
                        sortType,
                        ignoreCase = true
                    ) -> {
                        tracker!!.setScreenName("FollowingContentScreen")
                        Utils.pushOpenScreenEvent(
                            activity, "FollowingContentScreen",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId + ""
                        )
                    }
                    Constants.KEY_CHALLENGE.equals(
                        sortType,
                        ignoreCase = true
                    ) -> {
                    }
                }
                tracker!!.send(ScreenViewBuilder().build())
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    override fun onPause() {
        super.onPause()
        shimmerFrameLayout.stopShimmerAnimation()
    }

    @SuppressLint("RestrictedApi")
    private fun chooseMenuOptionsItem(view: View, position: Int) {
        val popupMenu = PopupMenu(
            activity!!,
            view
        )
        popupMenu.menuInflater.inflate(
            R.menu.choose_short_story_menu,
            popupMenu.menu
        )
        for (i in 0 until popupMenu.menu.size()) {
            val drawable =
                popupMenu.menu.getItem(i).icon
            if (drawable != null) {
                drawable.mutate()
                drawable.setColorFilter(
                    resources.getColor(R.color.app_red),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        }
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.addCollection) {
                try {
                    val addCollectionAndCollectionitemDialogFragment =
                        AddCollectionAndCollectionItemDialogFragment()
                    val bundle = Bundle()
                    bundle.putString("articleId", mixfeedList!![position].id)
                    bundle.putString("type", AppConstants.SHORT_STORY_COLLECTION_TYPE)
                    addCollectionAndCollectionitemDialogFragment.arguments = bundle
                    val fm = fragmentManager
                    addCollectionAndCollectionitemDialogFragment.setTargetFragment(this, 0)
                    addCollectionAndCollectionitemDialogFragment.show(fm!!, "collectionAdd")
                    Utils.pushProfileEvents(
                        activity, "CTA_100WS_Add_To_Collection",
                        "ArticleListingFragment", "Add to Collection", "-"
                    )
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
                return@setOnMenuItemClickListener true
            } else if (item.itemId == R.id.bookmarkShortStory) {
                return@setOnMenuItemClickListener true
            } else if (item.itemId == R.id.copyLink) {
                AppUtils.copyToClipboard(
                    AppUtils.getShortStoryShareUrl(
                        mixfeedList!![position].userType,
                        mixfeedList!![position].blogTitleSlug,
                        mixfeedList!![position].titleSlug
                    )
                )
                if (isAdded) {
                    Toast.makeText(
                        activity,
                        activity!!.getString(R.string.ss_story_link_copied),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@setOnMenuItemClickListener true
            } else if (item.itemId == R.id.reportContentShortStory) {
                val reportContentDialogFragment =
                    ReportContentDialogFragment()
                val args = Bundle()
                args.putString("postId", mixfeedList!![position].id)
                args.putInt("type", AppConstants.REPORT_TYPE_STORY)
                reportContentDialogFragment.arguments = args
                reportContentDialogFragment.isCancelable = true
                val fm = childFragmentManager
                reportContentDialogFragment.show(fm, "Report Content")
                return@setOnMenuItemClickListener true
            }
            false
        }
        val menuPopupHelper =
            MenuPopupHelper(
                view.context, (popupMenu.menu as MenuBuilder),
                view
            )
        menuPopupHelper.setForceShowIcon(true)
        menuPopupHelper.show()
    }

    private fun followApiCall(authorId: String, position: Int) {
        this.position = position
        val retrofit = BaseApplication.getInstance().retrofit
        val followApi = retrofit.create(FollowAPI::class.java)
        val request = FollowUnfollowUserRequest()
        request.followee_id = authorId
        if ("1" == mixfeedList!![position].isfollowing) {
            Utils.pushGenericEvent(
                activity, "CTA_Unfollow_100WS_Detail", userDynamoId,
                "ArticleListingFragment"
            )
            mixfeedList!![position].isfollowing = "0"
            mixfeedAdapter.notifyDataSetChanged()
            val followUnfollowUserResponseCall =
                followApi.unfollowUserInShortStoryListingV2(request)
            followUnfollowUserResponseCall.enqueue(followUnfollowUserResponseCallback)
        } else {
            trackEventsWithScreenName("Follow_Android", "MixedFeedStory_Follow")
            mixfeedList!![position].isfollowing = "1"
            mixfeedAdapter.notifyDataSetChanged()
            val followUnfollowUserResponseCall =
                followApi.followUserInShortStoryListingV2(request)
            followUnfollowUserResponseCall.enqueue(followUnfollowUserResponseCallback)
        }
    }

    private fun trackEventsWithScreenName(objective: String, event: String) {
        val screenName: String = when (sortType) {
            Constants.KEY_TRENDING -> {
                "Trending"
            }
            Constants.KEY_RECENT -> {
                "Recent"
            }
            Constants.KEY_TODAYS_BEST -> {
                "TodaysBest"
            }
            Constants.KEY_FOLLOWING -> {
                "FollowingFeed"
            }
            else -> "NA"
        }
        Utils.shareEventTracking(activity, screenName, objective, event)
    }

    private val followUnfollowUserResponseCallback: Callback<ResponseBody?> =
        object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                activity?.let {
                    (it as BaseActivity).syncFollowingList()
                }
            }

            override fun onFailure(
                call: Call<ResponseBody?>,
                t: Throwable
            ) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) ||
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )) {
            Snackbar.make(
                rootLayout!!, R.string.permission_storage_rationale,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.ok) { requestUngrantedPermissions() }.show()
        } else {
            requestUngrantedPermissions()
        }
    }

    private fun requestUngrantedPermissions() {
        val permissionList =
            ArrayList<String>()
        for (s in PERMISSIONS_INIT) {
            if (ActivityCompat.checkSelfPermission(activity!!, s)
                != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(s)
            }
        }
        val requiredPermission =
            permissionList.toTypedArray()
        requestPermissions(
            requiredPermission,
            REQUEST_INIT_PERMISSION
        )
    }

    private fun checkPermissionAndCreateShareableImage() {
        if (Build.VERSION.SDK_INT >= 23 && isAdded) {
            if (ActivityCompat
                    .checkSelfPermission(
                        activity!!,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat
                    .checkSelfPermission(
                        activity!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions()
            } else {
                try {
                    createBitmapForSharingStory()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
        } else {
            try {
                createBitmapForSharingStory()
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_INIT_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(
                    rootLayout!!, R.string.permision_available_init,
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                createBitmapForSharingStory()
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.")
                Snackbar.make(
                    rootLayout!!, R.string.permissions_not_granted,
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun getSharableViewForPosition(position: Int, medium: String) {
        storyShareCardWidget =
            recyclerView.layoutManager!!.findViewByPosition(position)?.findViewById(R.id.storyShareCardWidget)!!
        shareStoryImageView =
            storyShareCardWidget.findViewById(R.id.storyImageView)
        shareMedium = medium
        sharedStoryItem = mixfeedList!![position]
        checkPermissionAndCreateShareableImage()
    }

    private fun createBitmapForSharingStory() {
        if (isAdded) {
            val bitmap1 =
                (shareStoryImageView.drawable as BitmapDrawable).bitmap
            shareStoryImageView.setImageBitmap(
                SharingUtils.getRoundCornerBitmap(
                    bitmap1,
                    AppUtils.dpTopx(4.0f)
                )
            )
            // Bh**d**a facebook caches shareIntent. Need different name for all files
            val tempName = "" + System.currentTimeMillis()
            AppUtils.getBitmapFromView(
                storyShareCardWidget,
                AppConstants.STORY_SHARE_IMAGE_NAME + tempName
            )
            shareStory(tempName)
        }
    }

    private fun shareStory(tempName: String) {
        val uri = Uri.parse(
            "file://" + BaseApplication.getAppContext().getExternalFilesDir(null) +
                File.separator + AppConstants.STORY_SHARE_IMAGE_NAME + tempName + ".jpg"
        )
        val screenName: String = when (sortType) {
            Constants.KEY_TRENDING -> {
                "Trending"
            }
            Constants.KEY_RECENT -> {
                "Recent"
            }
            Constants.KEY_TODAYS_BEST -> {
                "TodaysBest"
            }
            Constants.KEY_FOLLOWING -> {
                "FollowingFeed"
            }
            else -> "NA"
        }
        if (isAdded) {
            when (shareMedium) {
                AppConstants.MEDIUM_FACEBOOK -> {
                    SharingUtils.shareViaFacebook(activity, uri)
                    Utils.shareEventTracking(
                        activity,
                        screenName,
                        "Share_Android",
                        "MFS_Facebook_Share"
                    )
                }
                AppConstants.MEDIUM_WHATSAPP -> {
                    if (AppUtils.shareImageWithWhatsApp(
                            activity,
                            uri,
                            getString(
                                R.string.ss_follow_author,
                                sharedStoryItem.userName,
                                AppUtils.getUtmParamsAppendedShareUrl(
                                    AppConstants.USER_PROFILE_SHARE_BASE_URL + sharedStoryItem.userId,
                                    "MFS_Whatsapp_Share",
                                    "Share_Android"
                                )
                            )
                        )) {
                        Utils.shareEventTracking(
                            activity,
                            screenName,
                            "Share_Android",
                            "MFS_Whatsapp_Share"
                        )
                    }
                }
                AppConstants.MEDIUM_INSTAGRAM -> {
                    if (AppUtils.shareImageWithInstagram(activity, uri)) {
                        Utils.shareEventTracking(
                            activity,
                            screenName,
                            "Share_Android",
                            "MFS_Instagram_Share"
                        )
                    }
                }
                AppConstants.MEDIUM_GENERIC -> {
                    if (AppUtils.shareGenericImageAndOrLink(
                            activity, uri, getString(
                            R.string.ss_follow_author,
                            sharedStoryItem.userName,
                            AppUtils.getUtmParamsAppendedShareUrl(
                                AppConstants.USER_PROFILE_SHARE_BASE_URL + sharedStoryItem.userId,
                                "MFS_Generic_Share",
                                "Share_Android"
                            )
                        )
                        )) {
                        Utils.shareEventTracking(
                            activity,
                            screenName,
                            "Share_Android",
                            "MFS_Generic_Share"
                        )
                    }
                }
                else -> {
                }
            }
        }
    }

    override fun onClick(view: View, position: Int) {
        when (view.id) {
            R.id.followCtaImageView -> {
                activity?.let {
                    (activity as BaseActivity).syncFollowingList()
                }
            }
            R.id.liveStreamItemView -> {
                mixfeedList?.get(position)?.recentLiveStreamsList?.get(
                    0
                )?.id?.let {
                    Utils.shareEventTracking(
                        activity,
                        "Home screen",
                        "Live_Android",
                        "TB_LC_Live"
                    )
                    (activity as BaseActivity).getLiveStreamInfoFromId(
                        it
                    )
                }
            }
            R.id.viewAllLivesTextView -> {
                Utils.shareEventTracking(
                    activity,
                    "Home screen",
                    "Live_Android",
                    "TB_UC_ViewAll_Live"
                )
                val intent = Intent(
                    activity,
                    MomspressoTelevisionActivity::class.java
                )
                startActivity(intent)
            }
            R.id.articleItemView, R.id.videoItemView, R.id.rootView -> {
                launchContentDetail(mixfeedList?.get(position))
            }
            R.id.icSsComment -> {
                val intent = Intent(activity, ViewAllCommentsActivity::class.java)
                intent.putExtra(Constants.ARTICLE_ID, mixfeedList?.get(position)?.id)
                intent.putExtra(Constants.AUTHOR_ID, mixfeedList?.get(position)?.userId)
                intent.putExtra(Constants.BLOG_SLUG, mixfeedList?.get(position)?.blogTitleSlug)
                intent.putExtra(Constants.TITLE_SLUG, mixfeedList?.get(position)?.titleSlug)
                val tagList = ArrayList<String>()
                for (i in mixfeedList?.get(position)?.tags?.indices!!) {
                    for ((key, value) in mixfeedList?.get(position)?.tags?.get(i)!!) {
                        if (key.startsWith("category-")) {
                            tagList.add(key)
                        }
                    }
                }
                intent.putExtra("tags", tagList)
                startActivity(intent)
            }
            R.id.shareArticleImageView -> {
                // shareContent(mixfeedList?.get(position))
            }
            R.id.facebookShareImageView -> {
                getSharableViewForPosition(position, AppConstants.MEDIUM_FACEBOOK)
            }
            R.id.whatsappShareImageView -> {
                getSharableViewForPosition(position, AppConstants.MEDIUM_WHATSAPP)
            }
            R.id.instagramShareImageView -> {
                try {
                    filterTags(mixfeedList?.get(position)?.tags!!)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
                getSharableViewForPosition(position, AppConstants.MEDIUM_INSTAGRAM)
            }
            R.id.genericShareImageView -> {
                getSharableViewForPosition(position, AppConstants.MEDIUM_GENERIC)
            }
            R.id.storyImageView1 -> {
                val intent = Intent(activity, ShortStoryContainerActivity::class.java)
                intent.putExtra(Constants.ARTICLE_ID, mixfeedList?.get(position)?.id)
                intent.putExtra(Constants.AUTHOR_ID, mixfeedList?.get(position)?.userId)
                intent.putExtra(Constants.BLOG_SLUG, mixfeedList?.get(position)?.blogTitleSlug)
                intent.putExtra(Constants.TITLE_SLUG, mixfeedList?.get(position)?.titleSlug)
                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "" + "ArticleListingActivity")
                intent.putExtra(Constants.FROM_SCREEN, "ArticleListingActivity")
                intent.putExtra(Constants.ARTICLE_INDEX, "" + position)
                intent.putExtra(
                    Constants.AUTHOR,
                    mixfeedList?.get(position)?.userId + "~" + mixfeedList?.get(position)?.userName
                )
                startActivity(intent)
            }
            R.id.bookmarkArticleImageView -> {
                if (mixfeedList?.get(position)?.isbookmark == 0) {
                    bookmarkItem(position)
                } else {
                    deleteBookmark(position)
                }
            }
            R.id.authorNameTextView -> {
                val intent = Intent(activity, UserProfileActivity::class.java)
                intent.putExtra(
                    Constants.USER_ID,
                    mixfeedList?.get(position)?.userId
                )
                startActivity(intent)
            }
            R.id.followAuthorTextView -> {
                followApiCall(
                    mixfeedList!![position].userId, position
                )
            }
            R.id.menuItem -> {
                chooseMenuOptionsItem(view, position)
            }
            R.id.storyRecommendationContainer -> {
                if (!isRecommendRequestRunning) {
                    mixfeedList?.get(position)?.isLiked?.let {
                        if (it) {
                            likeStatus = "0"
                            currentShortStoryPosition = position
                            recommendUnrecommendArticleApi(
                                "0",
                                mixfeedList?.get(position)?.id!!,
                                mixfeedList?.get(position)?.userId!!,
                                mixfeedList?.get(position)?.userName!!
                            )
                        } else {
                            trackEventsWithScreenName("Like_Android", "MixedFeedStory_Like")
                            likeStatus = "1"
                            currentShortStoryPosition = position
                            recommendUnrecommendArticleApi(
                                "1",
                                mixfeedList?.get(position)?.id!!,
                                mixfeedList?.get(position)?.userId!!,
                                mixfeedList?.get(position)?.userName!!
                            )
                        }
                    }
                }
            }
            R.id.menuItemImageView -> {
            }
            R.id.followTopicsWidget -> {
                when (sortType) {
                    Constants.KEY_FOLLOWING -> {
                        Utils.shareEventTracking(
                            activity,
                            "My Feed",
                            "Onboarding_Android",
                            "MyFeed_ES_Follow_Topics"
                        )
                    }
//                    Constants.KEY_TODAYS_BEST -> {
//                        Utils.shareEventTracking(
//                            activity,
//                            "My Feed",
//                            "Onboarding_Android",
//                            "RO_Bloggers_Friends_Back"
//                        )
//                    }
                }
                val chooseContentTopicsBottomSheetDialogFragment =
                    ChooseContentTopicsBottomSheetDialogFragment()
                chooseContentTopicsBottomSheetDialogFragment.show(
                    activity?.supportFragmentManager!!,
                    "choose_topic"
                )
            }
            R.id.followCreatorsWidget -> {
                activity?.let {
                    when (sortType) {
                        Constants.KEY_FOLLOWING -> {
                            Utils.shareEventTracking(
                                it,
                                "My Feed",
                                "Onboarding_Android",
                                "MyFeed_ES_Find_Friends"
                            )
                        }
//                        Constants.KEY_TODAYS_BEST -> {
//                            Utils.shareEventTracking(
//                                it,
//                                "My Feed",
//                                "Onboarding_Android",
//                                "RO_Bloggers_Friends_Back"
//                            )
//                        }
                    }
                    val intent = Intent(it, FindFbFriendsActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onFollowSuccess() {
        activity?.let {
            (it as BaseActivity).syncFollowingList()
        }
    }

    override fun onTorcaiAdClick(request: WebResourceRequest) {
        if (AppUtils.isMomspressoDomain(request.url.toString())) {
            if (activity != null) {
                (activity as BaseActivity).handleDeeplinks(request.url.toString())
            }
        } else {
            if (activity != null) {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(activity, request.url)
            }
        }
    }

    private fun launchContentDetail(item: MixFeedResult?) {
        when {
            item?.itemType == AppConstants.CONTENT_TYPE_ARTICLE ||
                item?.contentType == AppConstants.CONTENT_TYPE_ARTICLE -> {
                val intent = Intent(activity, ArticleDetailsContainerActivity::class.java)
                intent.putExtra(Constants.ARTICLE_ID, item.id)
                intent.putExtra(Constants.AUTHOR_ID, item.userId)
                intent.putExtra(Constants.BLOG_SLUG, item.blogTitleSlug)
                intent.putExtra(Constants.TITLE_SLUG, item.titleSlug)
                intent.putExtra(Constants.FROM_SCREEN, "Profile")
                intent.putExtra(Constants.AUTHOR, item.userId + "~" + item.userName)
                startActivity(intent)
            }
            item?.itemType == AppConstants.CONTENT_TYPE_SHORT_STORY ||
                item?.contentType == AppConstants.CONTENT_TYPE_SHORT_STORY -> {
                val intent = Intent(activity, ShortStoryContainerActivity::class.java)
                intent.putExtra(Constants.ARTICLE_ID, item.id)
                intent.putExtra(Constants.AUTHOR_ID, item.userId)
                intent.putExtra(Constants.BLOG_SLUG, item.blogTitleSlug)
                intent.putExtra(Constants.TITLE_SLUG, item.titleSlug)
                intent.putExtra(Constants.FROM_SCREEN, "Profile")
                intent.putExtra(Constants.AUTHOR, item.userId + "~" + item.userName)
                startActivity(intent)
            }
            item?.itemType == AppConstants.CONTENT_TYPE_VIDEO ||
                item?.contentType == AppConstants.CONTENT_TYPE_VIDEO -> {
                val intent = Intent(activity, ParallelFeedActivity::class.java)
                intent.putExtra(Constants.VIDEO_ID, item.id)
                intent.putExtra(Constants.FROM_SCREEN, "Profile")
                startActivity(intent)
            }
        }
    }

    private fun bookmarkItem(position: Int) {
        val retro = BaseApplication.getInstance().retrofit
        val articleDetailsAPI = retro.create(ArticleDetailsAPI::class.java)
        val articleDetailRequest = ArticleDetailRequest()
        articleDetailRequest.articleId = mixfeedList?.get(position)?.id

        if ("1" == mixfeedList?.get(position)?.isMomspresso) {
            val call = articleDetailsAPI.addVideoWatchLater(articleDetailRequest)
            call.enqueue(object : Callback<AddBookmarkResponse> {
                override fun onResponse(
                    call: Call<AddBookmarkResponse>,
                    response: Response<AddBookmarkResponse>
                ) {
                    if (null == response.body()) {
                        activity?.let {
                            ToastUtils.showToast(it, getString(R.string.server_went_wrong))
                        }
                        return
                    }
                    val responseData = response.body()
                    if (responseData?.code == 200) {
                        mixfeedList?.get(position)?.isbookmark = 1
                        mixfeedList?.get(position)?.bookmarkId = responseData.data.result.bookmarkId
                        mixfeedAdapter.notifyDataSetChanged()
                        activity?.let {
                            Utils.pushWatchLaterArticleEvent(
                                it,
                                "ArticleListingFragment",
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId + "",
                                mixfeedList?.get(position)?.id,
                                mixfeedList?.get(position)?.author?.id + "~" + mixfeedList?.get(
                                    position
                                )?.userName
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<AddBookmarkResponse>, e: Throwable) {
                    activity?.let {
                        ToastUtils.showToast(it, getString(R.string.server_went_wrong))
                    }
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            })
        } else {
            val call = articleDetailsAPI.addBookmark(articleDetailRequest)
            call.enqueue(object : Callback<AddBookmarkResponse> {
                override fun onResponse(
                    call: Call<AddBookmarkResponse>,
                    response: Response<AddBookmarkResponse>
                ) {
                    if (null == response.body()) {
                        activity?.let {
                            ToastUtils.showToast(it, getString(R.string.server_went_wrong))
                        }
                        return
                    }
                    val responseData = response.body()
                    if (responseData?.code == 200) {
                        mixfeedList?.get(position)?.isbookmark = 1
                        mixfeedList?.get(position)?.bookmarkId = responseData.data.result.bookmarkId
                        mixfeedAdapter.notifyDataSetChanged()
                        activity?.let {
                            Utils.pushBookmarkArticleEvent(
                                it,
                                "ArticleListingFragment",
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId + "",
                                mixfeedList?.get(position)?.id,
                                mixfeedList?.get(position)?.author?.id + "~" + mixfeedList?.get(
                                    position
                                )?.userName
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<AddBookmarkResponse>, e: Throwable) {
                    activity?.let {
                        ToastUtils.showToast(it, getString(R.string.server_went_wrong))
                    }
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            })
        }
    }

    private fun deleteBookmark(position: Int) {
        val retro = BaseApplication.getInstance().retrofit
        val deleteBookmarkRequest = DeleteBookmarkRequest()
        deleteBookmarkRequest.id = mixfeedList?.get(position)?.bookmarkId
        val articleDetailsAPI = retro.create(
            ArticleDetailsAPI::class.java
        )
        if ("1" == mixfeedList?.get(position)?.isMomspresso) {
            val call =
                articleDetailsAPI.deleteVideoWatchLater(deleteBookmarkRequest)
            call.enqueue(object : Callback<AddBookmarkResponse> {
                override fun onFailure(call: Call<AddBookmarkResponse>, t: Throwable) {
                    activity?.let {
                        ToastUtils.showToast(it, getString(R.string.server_went_wrong))
                    }
                    FirebaseCrashlytics.getInstance().recordException(t)
                    Log.d("MC4kException", Log.getStackTraceString(t))
                }

                override fun onResponse(
                    call: Call<AddBookmarkResponse>,
                    response: Response<AddBookmarkResponse>
                ) {
                    if (null == response.body()) {
                        activity?.let {
                            ToastUtils.showToast(it, getString(R.string.server_went_wrong))
                        }
                        return
                    }
                    val responseData = response.body()
                    if (responseData?.code == 200) {
                        mixfeedList?.get(position)?.isbookmark = 0
                        mixfeedList?.get(position)?.bookmarkId = ""
                        mixfeedAdapter.notifyDataSetChanged()
                        activity?.let {
                            Utils.pushUnbookmarkArticleEvent(
                                it,
                                "ArticleListingFragment",
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId + "",
                                mixfeedList?.get(position)?.id,
                                mixfeedList?.get(position)?.author?.id + "~" + mixfeedList?.get(
                                    position
                                )?.userName
                            )
                        }
                    }
                }
            })
        } else {
            val call =
                articleDetailsAPI.deleteBookmark(deleteBookmarkRequest)
            call.enqueue(object : Callback<AddBookmarkResponse> {
                override fun onFailure(call: Call<AddBookmarkResponse>, t: Throwable) {
                    activity?.let {
                        ToastUtils.showToast(it, getString(R.string.server_went_wrong))
                    }
                    FirebaseCrashlytics.getInstance().recordException(t)
                    Log.d("MC4kException", Log.getStackTraceString(t))
                }

                override fun onResponse(
                    call: Call<AddBookmarkResponse>,
                    response: Response<AddBookmarkResponse>
                ) {
                    if (null == response.body()) {
                        activity?.let {
                            ToastUtils.showToast(it, getString(R.string.server_went_wrong))
                        }
                        return
                    }
                    val responseData = response.body()
                    if (responseData?.code == 200) {
                        mixfeedList?.get(position)?.isbookmark = 0
                        mixfeedList?.get(position)?.bookmarkId = ""
                        mixfeedAdapter.notifyDataSetChanged()
                        activity?.let {
                            Utils.pushUnbookmarkArticleEvent(
                                it,
                                "ArticleListingFragment",
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId + "",
                                mixfeedList?.get(position)?.id,
                                mixfeedList?.get(position)?.author?.id + "~" + mixfeedList?.get(
                                    position
                                )?.userName
                            )
                        }
                    }
                }
            })
        }
    }

    companion object {
        private const val LIMIT = 15
        private const val FORYOU_LIMIT = 10
        private const val REQUEST_INIT_PERMISSION = 2
        private val PERMISSIONS_INIT = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onChallengeItemClick(view: View, topics: Topics, parentCategoryId: String?) {
        when (view.id) {
            R.id.tagImageView -> {
                parentCategoryId?.let {
                    when (it) {
                        "category-eed5fd2777a24bd48ba9a7e1e4dd4b47" -> {
                            Utils.shareEventTracking(
                                activity,
                                "Home screen",
                                "Vlog_Challenges_Android",
                                "H_VCL_MP_Challenge"
                            )
                        }
                        "category-958b29175e174f578c2d92a925451d4f" -> {
                            Utils.shareEventTracking(
                                activity,
                                "Home screen",
                                "Vlog_Challenges_Android",
                                "H_VCL_CT_Challenge"
                            )
                        }
                        "category-2ce9257cbf4c4794acacacb173feda13" -> {
                            Utils.shareEventTracking(
                                activity,
                                "Home screen",
                                "Vlog_Challenges_Android",
                                "H_VCL_Aww_Challenge"
                            )
                        }
                        else -> {
                            Utils.shareEventTracking(
                                activity,
                                "Home screen",
                                "Vlog_Challenges_Android",
                                "H_VCL_Live_Challenge"
                            )
                        }
                    }
                }
                val intent = Intent(activity, NewVideoChallengeActivity::class.java)
                intent.putExtra("challenge", topics.id)
                intent.putExtra("comingFrom", "vlog_listing")
                startActivity(intent)
            }
            R.id.info -> {
                topics.extraData[0].challenge.rules?.let {
                    val dialog = Dialog(context!!)
                    dialog.setContentView(R.layout.challenge_rules_dialog)
                    dialog.setTitle("Title...")
                    val imageView =
                        dialog.findViewById<View>(R.id.closeEditorImageView) as ImageView
                    val webView =
                        dialog.findViewById<View>(R.id.videoChallengeRulesWebView) as WebView
                    webView.loadDataWithBaseURL(
                        "",
                        it,
                        "text/html",
                        "UTF-8",
                        ""
                    )
                    imageView.setOnClickListener { view2: View? -> dialog.dismiss() }
                    dialog.show()
                }
            }
        }
    }

    override fun onClick(
        view: View?,
        position: Int,
        challengeId: String?,
        Display_Name: String?,
        articledatamodelsnew: Topics?,
        activeImageUrl: String?
    ) {
        when (view?.id) {
            R.id.mainView, R.id.getStartedTextView -> {
                if (position == 0) {
                    Utils.shareEventTracking(
                        activity,
                        "Home screen",
                        "Story_Challenges_Android",
                        "H_SCL_ThisWeek_Challenge"
                    )
                } else {
                    Utils.shareEventTracking(
                        activity,
                        "Home screen",
                        "Story_Challenges_Android",
                        "H_SCL_PreviousWeeks_Challenge"
                    )
                }
                val intent = Intent(activity, ShortStoryChallengeDetailActivity::class.java)
                intent.putExtra("challenge", challengeId)
                startActivity(intent)
            }
        }
    }

    override fun onBlogChallengeItemClick(position: Int, v: View?, topics: Topics) {
        when (v?.id) {
            R.id.tagImageView -> {
                if (position == 0) {
                    Utils.shareEventTracking(
                        activity,
                        "Home screen",
                        "Blog_Challenges_Android",
                        "H_BCL_ThisWeek_Challenge"
                    )
                } else {
                    Utils.shareEventTracking(
                        activity,
                        "Home screen",
                        "Blog_Challenges_Android",
                        "H_BCL_PreviousWeeks_Challenge"
                    )
                }

                val intent = Intent(activity, ArticleChallengeDetailActivity::class.java)
                intent.putExtra("articleChallengeId", topics.id)
                intent.putExtra("challengeName", topics.display_name)
                startActivity(intent)
            }
            R.id.info -> {
                Utils.shareEventTracking(
                    activity,
                    "Home screen",
                    "Blog_Challenges_Android",
                    "H_BCL_InfoIcon_Challenge"
                )
                topics.extraData[0].challenge.rules?.let {
                    val dialog = Dialog(context!!)
                    dialog.setContentView(R.layout.challenge_rules_dialog)
                    dialog.setTitle("Title...")
                    val imageView =
                        dialog.findViewById<View>(R.id.closeEditorImageView) as ImageView
                    val webView =
                        dialog.findViewById<View>(R.id.videoChallengeRulesWebView) as WebView
                    webView.loadDataWithBaseURL(
                        "",
                        it,
                        "text/html",
                        "UTF-8",
                        ""
                    )
                    imageView.setOnClickListener { dialog.dismiss() }
                    dialog.show()
                }
            }
        }
    }

    override fun onLiveStreamItemClick(view: View, liveStreamResult: LiveStreamResult?) {
        liveStreamResult?.id?.let {
            Utils.shareEventTracking(
                activity,
                "Home screen",
                "Live_Android",
                "TB_UC_Card_Live"
            )
            (activity as BaseActivity).getLiveStreamInfoFromId(
                it
            )
        }
    }

    override fun onSuggestedCreatorClick(
        mixFeedPosition: Int,
        position: Int,
        view: View,
        suggestedCreator: SuggestedCreators?
    ) {
        when (view.id) {
            R.id.authorImageView -> {
                val intent = Intent(activity, UserProfileActivity::class.java)
                intent.putExtra(
                    Constants.USER_ID,
                    suggestedCreator?.id
                )
                startActivity(intent)
            }
            R.id.authorFollowTextView -> {
                suggestedCreator?.id?.let { followSuggestedCreator(it, mixFeedPosition, position) }
            }
        }
    }

    override fun onTopCreatorClick(
        mixFeedPosition: Int,
        position: Int,
        view: View,
        topCreator: ContributorListResult?
    ) {
        when (view.id) {
            R.id.authorImageView -> {
                val intent = Intent(activity, UserProfileActivity::class.java)
                intent.putExtra(
                    Constants.USER_ID,
                    topCreator?.id
                )
                startActivity(intent)
            }
            R.id.authorFollowTextView -> {
                topCreator?.id?.let { followTopCreator(it, mixFeedPosition, position) }
            }
        }
    }

    private fun followSuggestedCreator(authorId: String, mixfeedPosition: Int, position: Int) {
        val retrofit = BaseApplication.getInstance().retrofit
        val followApi = retrofit.create(FollowAPI::class.java)
        val request = FollowUnfollowUserRequest()
        request.followee_id = authorId
        if ("1" == mixfeedList!![mixfeedPosition].suggestedCreatorList?.get(position)?.isfollowing) {
            mixfeedList!![mixfeedPosition].suggestedCreatorList?.get(position)?.isfollowing = "0"
            mixfeedAdapter.notifyDataSetChanged()
            val followUnfollowUserResponseCall =
                followApi.unfollowUserInShortStoryListingV2(request)
            followUnfollowUserResponseCall.enqueue(followUnfollowUserResponseCallback)
        } else {
            if (mixfeedList!![mixfeedPosition].contentType == AppConstants.CONTENT_TYPE_SUGGESTED_CREATORS_BASED_ON_TOPICS) {
                Utils.shareEventTracking(
                    activity,
                    "My Feed",
                    "Read_Android",
                    "MyFeed_Topic_Bloggers_Follow_CTA"
                )
            } else if (mixfeedList!![mixfeedPosition].contentType == AppConstants.CONTENT_TYPE_SUGGESTED_CREATORS_BASED_ON_ACTIVITY) {
                Utils.shareEventTracking(
                    activity,
                    "My Feed",
                    "Read_Android",
                    "MyFeed_Bloggers_Read_Follow_CTA"
                )
            } else if (mixfeedList!![mixfeedPosition].contentType == AppConstants.CONTENT_TYPE_SUGGESTED_CREATORS_FOLLOWED_BY_FRIENDS) {
                Utils.shareEventTracking(
                    activity,
                    "My Feed",
                    "Read_Android",
                    "MyFeed_Friend_Bloggers_Follow_CTA"
                )
            } else{
                Log.e("jbubjbj","popopopopop")
            }
            mixfeedList!![mixfeedPosition].suggestedCreatorList?.get(position)?.isfollowing = "1"
            mixfeedAdapter.notifyDataSetChanged()
            val followUnfollowUserResponseCall =
                followApi.followUserInShortStoryListingV2(request)
            followUnfollowUserResponseCall.enqueue(followUnfollowUserResponseCallback)
        }
    }

    private fun followTopCreator(authorId: String, mixfeedPosition: Int, position: Int) {
        val retrofit = BaseApplication.getInstance().retrofit
        val followApi = retrofit.create(FollowAPI::class.java)
        val request = FollowUnfollowUserRequest()
        request.followee_id = authorId
        if (1 == mixfeedList!![mixfeedPosition].topCreatorList?.get(position)?.isFollowed) {
            mixfeedList!![mixfeedPosition].topCreatorList?.get(position)?.isFollowed = 0
            mixfeedAdapter.notifyDataSetChanged()
            val followUnfollowUserResponseCall =
                followApi.unfollowUserInShortStoryListingV2(request)
            followUnfollowUserResponseCall.enqueue(followUnfollowUserResponseCallback)
        } else {
            Utils.shareEventTracking(
                activity,
                "My Feed",
                "Read_Android",
                "MyFeed_Top_Bloggers_Follow_CTA"
            )
            mixfeedList!![mixfeedPosition].topCreatorList?.get(position)?.isFollowed = 1
            mixfeedAdapter.notifyDataSetChanged()
            val followUnfollowUserResponseCall =
                followApi.followUserInShortStoryListingV2(request)
            followUnfollowUserResponseCall.enqueue(followUnfollowUserResponseCallback)
        }
    }
}
