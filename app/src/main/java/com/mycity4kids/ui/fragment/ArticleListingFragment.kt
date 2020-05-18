package com.mycity4kids.ui.fragment

import android.Manifest
import android.accounts.NetworkErrorException
import android.annotation.SuppressLint
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
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.campaignmodels.AllCampaignDataResponse
import com.mycity4kids.models.campaignmodels.CampaignDataListResult
import com.mycity4kids.models.request.ArticleDetailRequest
import com.mycity4kids.models.request.DeleteBookmarkRequest
import com.mycity4kids.models.request.FollowUnfollowUserRequest
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest
import com.mycity4kids.models.response.AddBookmarkResponse
import com.mycity4kids.models.response.MixFeedResponse
import com.mycity4kids.models.response.MixFeedResult
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.profile.UserContentAdapter
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI
import com.mycity4kids.retrofitAPIsInterfaces.RecommendationAPI
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity
import com.mycity4kids.ui.activity.ExploreArticleListingTypeActivity
import com.mycity4kids.ui.activity.ParallelFeedActivity
import com.mycity4kids.ui.activity.ShortStoryContainerActivity
import com.mycity4kids.ui.adapter.MainArticleRecyclerViewAdapter
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.ConnectivityUtils
import com.mycity4kids.utils.DateTimeUtils
import com.mycity4kids.utils.PermissionUtil
import com.mycity4kids.utils.SharingUtils
import com.mycity4kids.utils.StringUtils
import com.mycity4kids.utils.ToastUtils
import com.mycity4kids.widget.MomspressoButtonWidget
import com.mycity4kids.widget.StoryShareCardWidget
import java.io.File
import java.util.ArrayList
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleListingFragment : BaseFragment(), View.OnClickListener,
    OnRefreshListener, MainArticleRecyclerViewAdapter.RecyclerViewClickListener,
    UserContentAdapter.RecyclerViewClickListener {
    private var mixfeedList: ArrayList<MixFeedResult>? = null
    private var campaignListDataModels: ArrayList<CampaignDataListResult>? = null
    private var sortType: String? = null
    private var nextPageNumber = 0
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
    private lateinit var addTopicsLayout: LinearLayout
    private lateinit var headerArticleCardLayout: FrameLayout
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
    private lateinit var filterContentContainer: ConstraintLayout

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
        addTopicsLayout =
            rootView.findViewById(R.id.addTopicsLayout)
        headerArticleCardLayout =
            rootView.findViewById(R.id.headerArticleView)
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

        articleFilterTextView.isSelected = true
        storyFilterTextView.isSelected = false
        vlogsFilterTextView.isSelected = false

        mixpanel =
            MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN)
        userDynamoId =
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        addTopicsLayout.setOnClickListener(this)

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
        mixfeedAdapter = UserContentAdapter(this, false)
        mixfeedList = ArrayList()
        campaignListDataModels = ArrayList()
        mixfeedList = ArrayList()
        nextPageNumber = 1
        hitArticleListingApi(sortType)
        val llm =
            LinearLayoutManager(activity)
        llm.orientation = RecyclerView.VERTICAL
        recyclerView.layoutManager = llm
        mixfeedAdapter.setListData(mixfeedList)
        recyclerView.adapter = mixfeedAdapter
        pullToRefresh.setOnRefreshListener {
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
        recyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                if (dy > 0) {
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
            filterContentContainer.visibility = View.VISIBLE
            val retrofit = BaseApplication.getInstance().retrofit
            val topicsApi = retrofit.create(
                TopicsCategoryAPI::class.java
            )
            val campaignApi = retrofit.create(CampaignAPI::class.java)
            val from = (nextPageNumber - 1) * LIMIT + 1
            val filterCall = topicsApi.getTodaysBestMixedFeed(
                DateTimeUtils.getKidsDOBNanoMilliTimestamp("" + System.currentTimeMillis()),
                from, from + LIMIT - 1,
                SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()),
                getContentFilters()
            )
            filterCall.enqueue(articleListingResponseCallback)
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
            if (responseData!!.data!!.result == null && (mixfeedList == null ||
                    mixfeedList!!.isEmpty())) {
                addTopicsLayout.visibility = View.VISIBLE
                headerArticleCardLayout.visibility = View.GONE
                return
            }
            val dataList =
                responseData.data!!.result as ArrayList<MixFeedResult>?
            if (dataList.isNullOrEmpty()) {
                isLastPageReached = true
                if (mixfeedList.isNullOrEmpty()) { // No results
                    mixfeedList!!.add(MixFeedResult(contentType = AppConstants.CONTENT_TYPE_SUGGESTED_BLOGGERS))
                    mixfeedAdapter.setListData(mixfeedList)
                    mixfeedAdapter.notifyDataSetChanged()
                }
            } else {
                noBlogsTextView.visibility = View.GONE
                if (responseData.data.chunks.isNullOrBlank()) {
                    isLastPageReached = true
                } else {
                    mixfeedList!!.addAll(dataList)
                    chunks = responseData.data.chunks!!
                }
                if (!mixfeedList.isNullOrEmpty() && mixfeedList!!.size <= LIMIT) {
                    if (mixfeedList?.size!! < 3) {
                        mixfeedList?.add(
                            MixFeedResult(contentType = AppConstants.CONTENT_TYPE_SUGGESTED_BLOGGERS)
                        )
                    } else {
                        mixfeedList?.add(
                            3,
                            MixFeedResult(contentType = AppConstants.CONTENT_TYPE_SUGGESTED_BLOGGERS)
                        )
                    }
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

    private val getCampaignList: Callback<AllCampaignDataResponse?> =
        object : Callback<AllCampaignDataResponse?> {
            override fun onResponse(
                call: Call<AllCampaignDataResponse?>,
                response: Response<AllCampaignDataResponse?>
            ) {
                if (response.body() == null) {
                    return
                }
                try {
                    val allCampaignDataResponse = response.body()
                    if (allCampaignDataResponse!!.code == 200 && (Constants.SUCCESS
                            == allCampaignDataResponse.status)) {
                        processCampaignListingResponse(allCampaignDataResponse)
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4KException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(
                call: Call<AllCampaignDataResponse?>,
                e: Throwable
            ) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4KException", Log.getStackTraceString(e))
            }
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
            if (null != mixfeedList && mixfeedList!!.isNotEmpty()) { // No more next results for search from pagination
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
            if (nextPageNumber == 1) {
                mixfeedList = dataList
            } else {
                mixfeedList!!.addAll(dataList)
            }
            mixfeedAdapter.setListData(mixfeedList)
            nextPageNumber += 1
            mixfeedAdapter.notifyDataSetChanged()
        }
    }

    private fun processCampaignListingResponse(responseData: AllCampaignDataResponse?) {
        val dataList = responseData!!.data!!.result
        campaignListDataModels!!.addAll(dataList!!)
        mixfeedAdapter.setCampaignOrAdSlotData("campaign", campaignListDataModels!!, "")
        mixfeedAdapter.notifyDataSetChanged()
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

    override fun onRecyclerItemClick(view: View, position: Int) {
        try {
            when (view.id) {
                R.id.videoContainerFL1 -> launchVideoDetailsActivity(position, 0)
                R.id.cardView1 -> {
                    try {
                        Utils.campaignEvent(
                            activity,
                            "HomeScreen",
                            "HomeScreenCarousel",
                            "CTA_Campaign_Carousel",
                            "" + campaignListDataModels!![0].name,
                            "android",
                            SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                            System.currentTimeMillis().toString(),
                            "CTA_Campaign_Carousel"
                        )
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4KException", Log.getStackTraceString(e))
                    }
                    val campaignIntent =
                        Intent(activity, CampaignContainerActivity::class.java)
                    campaignIntent.putExtra(
                        "campaign_id",
                        campaignListDataModels!![0].id.toString() + ""
                    )
                    campaignIntent.putExtra("campaign_detail", "campaign_detail")
                    startActivity(campaignIntent)
                }
                R.id.headerArticleView, R.id.fbAdArticleView, R.id.storyHeaderView, R.id.storyImageView1 -> {
                    val page = position / LIMIT
                    val posSubList = position % LIMIT
                    val startIndex = page * LIMIT
                    val endIndex = startIndex + LIMIT
                    val articleDataModelsSubList =
                        ArrayList(
                            mixfeedList!!.subList(startIndex, endIndex)
                        )
                    if ("1" == mixfeedList!![position].contentType) {
                        val intent = Intent(
                            activity,
                            ShortStoryContainerActivity::class.java
                        )
                        if (Constants.KEY_RECENT.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "RecentScreen"
                            )
                        } else if (Constants.KEY_TODAYS_BEST.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "TodaysBestScreen"
                            )
                        } else if (Constants.KEY_TRENDING.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "AllTrending"
                            )
                        } else if (Constants.KEY_FOLLOWING.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "FollowingContentScreen"
                            )
                        }
                        intent.putExtra(
                            Constants.ARTICLE_ID,
                            articleDataModelsSubList[posSubList].id
                        )
                        intent.putExtra(
                            Constants.AUTHOR_ID,
                            articleDataModelsSubList[posSubList].userId
                        )
                        intent.putExtra(
                            Constants.BLOG_SLUG,
                            articleDataModelsSubList[posSubList].blogTitleSlug
                        )
                        intent.putExtra(
                            Constants.TITLE_SLUG,
                            articleDataModelsSubList[posSubList].titleSlug
                        )
                        intent.putExtra(
                            Constants.FROM_SCREEN,
                            "HomeScreen"
                        )
                        intent.putExtra(
                            Constants.AUTHOR,
                            articleDataModelsSubList[posSubList].userId + "~" + articleDataModelsSubList[posSubList].userName
                        )
                        val filteredResult = AppUtils
                            .getFilteredContentList1(
                                articleDataModelsSubList,
                                AppConstants.CONTENT_TYPE_SHORT_STORY
                            )
                        //                        intent.putParcelableArrayListExtra("pagerListData", filteredResult);
                        intent.putExtra(
                            Constants.ARTICLE_INDEX, "" + AppUtils
                            .getFilteredPosition1(
                                posSubList, articleDataModelsSubList,
                                AppConstants.CONTENT_TYPE_SHORT_STORY
                            )
                        )
                        startActivity(intent)
                    } else {
                        val intent = Intent(
                            activity,
                            ArticleDetailsContainerActivity::class.java
                        )
                        if (Constants.KEY_RECENT.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "RecentScreen"
                            )
                        } else if (Constants.KEY_TODAYS_BEST.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "TodaysBestScreen"
                            )
                        } else if (Constants.KEY_TRENDING.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "AllTrending"
                            )
                        } else if (Constants.KEY_FOLLOWING.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "FollowingContentScreen"
                            )
                        }
                        intent.putExtra(
                            Constants.ARTICLE_ID,
                            articleDataModelsSubList[posSubList].id
                        )
                        intent.putExtra(
                            Constants.AUTHOR_ID,
                            articleDataModelsSubList[posSubList].userId
                        )
                        intent.putExtra(
                            Constants.BLOG_SLUG,
                            articleDataModelsSubList[posSubList].blogTitleSlug
                        )
                        intent.putExtra(
                            Constants.TITLE_SLUG,
                            articleDataModelsSubList[posSubList].titleSlug
                        )
                        intent.putExtra(
                            Constants.FROM_SCREEN,
                            "HomeScreen"
                        )
                        intent.putExtra(
                            Constants.AUTHOR,
                            articleDataModelsSubList[posSubList].userId + "~" + articleDataModelsSubList[posSubList].userName
                        )
                        val filteredResult = AppUtils
                            .getFilteredContentList1(
                                articleDataModelsSubList,
                                AppConstants.CONTENT_TYPE_ARTICLE
                            )
                        //                        intent.putParcelableArrayListExtra("pagerListData", filteredResult);
                        intent.putExtra(
                            Constants.ARTICLE_INDEX, "" + AppUtils
                            .getFilteredPosition1(
                                posSubList, articleDataModelsSubList,
                                AppConstants.CONTENT_TYPE_ARTICLE
                            )
                        )
                        startActivity(intent)
                    }
                }
                R.id.menuItem -> {
                    chooseMenuOptionsItem(view, position)
                }
                R.id.followAuthorTextView -> {
                    followApiCall(
                        mixfeedList!![position].userId, position
                    )
                }
                R.id.whatsappShareImageView -> getSharableViewForPosition(
                    position,
                    AppConstants.MEDIUM_WHATSAPP
                )
                R.id.facebookShareImageView -> getSharableViewForPosition(
                    position,
                    AppConstants.MEDIUM_FACEBOOK
                )
                R.id.instagramShareImageView -> {
                    try {
                        filterTags(mixfeedList!![position].tags)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }
                    getSharableViewForPosition(position, AppConstants.MEDIUM_INSTAGRAM)
                }
                R.id.genericShareImageView -> {
                    if (isAdded) {
                        getSharableViewForPosition(position, AppConstants.MEDIUM_GENERIC)
                    }
                }
                R.id.storyRecommendationContainer -> if (!isRecommendRequestRunning) {
                    if (!mixfeedList!![position].isLiked) {
                        likeStatus = "1"
                        currentShortStoryPosition = position
                        recommendUnrecommendArticleApi(
                            "1", mixfeedList!![position].id,
                            mixfeedList!![position].userId,
                            mixfeedList!![position].userName
                        )
                    }
                }
                else -> {
                    val limit = LIMIT
                    val page = position / limit
                    val posSubList = position % limit
                    val startIndex = page * limit
                    val endIndex = startIndex + limit
                    val articleDataModelsSubList =
                        ArrayList(
                            mixfeedList!!.subList(startIndex, endIndex)
                        )
                    if ("1" == mixfeedList!![position].contentType) {
                        val intent = Intent(
                            activity,
                            ShortStoryContainerActivity::class.java
                        )
                        if (Constants.KEY_RECENT.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "RecentScreen"
                            )
                        } else if (Constants.KEY_TODAYS_BEST.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "TodaysBestScreen"
                            )
                        } else if (Constants.KEY_TRENDING.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "AllTrending"
                            )
                        } else if (Constants.KEY_FOLLOWING.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "FollowingContentScreen"
                            )
                        }
                        intent.putExtra(
                            Constants.ARTICLE_ID,
                            articleDataModelsSubList[posSubList].id
                        )
                        intent.putExtra(
                            Constants.AUTHOR_ID,
                            articleDataModelsSubList[posSubList].userId
                        )
                        intent.putExtra(
                            Constants.BLOG_SLUG,
                            articleDataModelsSubList[posSubList].blogTitleSlug
                        )
                        intent.putExtra(
                            Constants.TITLE_SLUG,
                            articleDataModelsSubList[posSubList].titleSlug
                        )
                        intent.putExtra(
                            Constants.FROM_SCREEN,
                            "HomeScreen"
                        )
                        intent.putExtra(
                            Constants.AUTHOR,
                            articleDataModelsSubList[posSubList].userId + "~" + articleDataModelsSubList[posSubList].userName
                        )
                        val filteredResult = AppUtils
                            .getFilteredContentList1(
                                articleDataModelsSubList,
                                AppConstants.CONTENT_TYPE_SHORT_STORY
                            )
                        intent.putExtra(
                            Constants.ARTICLE_INDEX, "" + AppUtils
                            .getFilteredPosition1(
                                posSubList, articleDataModelsSubList,
                                AppConstants.CONTENT_TYPE_SHORT_STORY
                            )
                        )
                        startActivity(intent)
                    } else {
                        val intent = Intent(
                            activity,
                            ArticleDetailsContainerActivity::class.java
                        )
                        if (Constants.KEY_FOR_YOU.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "ForYouScreen"
                            )
                        } else if (Constants.KEY_RECENT.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "RecentScreen"
                            )
                        } else if (Constants.KEY_TODAYS_BEST.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "TodaysBestScreen"
                            )
                        } else if (Constants.KEY_TRENDING.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "AllTrending"
                            )
                        } else if (Constants.KEY_FOLLOWING.equals(
                                sortType,
                                ignoreCase = true
                            )) {
                            intent.putExtra(
                                Constants.ARTICLE_OPENED_FROM,
                                "FollowingContentScreen"
                            )
                        }
                        intent.putExtra(
                            Constants.ARTICLE_ID,
                            articleDataModelsSubList[posSubList].id
                        )
                        intent.putExtra(
                            Constants.AUTHOR_ID,
                            articleDataModelsSubList[posSubList].userId
                        )
                        intent.putExtra(
                            Constants.BLOG_SLUG,
                            articleDataModelsSubList[posSubList].blogTitleSlug
                        )
                        intent.putExtra(
                            Constants.TITLE_SLUG,
                            articleDataModelsSubList[posSubList].titleSlug
                        )
                        intent.putExtra(
                            Constants.FROM_SCREEN,
                            "HomeScreen"
                        )
                        intent.putExtra(
                            Constants.AUTHOR,
                            articleDataModelsSubList[posSubList].userId + "~" + articleDataModelsSubList[posSubList].userName
                        )
                        val filteredResult = AppUtils
                            .getFilteredContentList1(
                                articleDataModelsSubList,
                                AppConstants.CONTENT_TYPE_ARTICLE
                            )
                        intent.putExtra(
                            Constants.ARTICLE_INDEX, "" + AppUtils
                            .getFilteredPosition1(
                                posSubList, articleDataModelsSubList,
                                AppConstants.CONTENT_TYPE_ARTICLE
                            )
                        )
                        startActivity(intent)
                    }
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
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
            R.id.addTopicsLayout -> {
                val intent1 = Intent(
                    activity,
                    ExploreArticleListingTypeActivity::class.java
                )
                intent1.putExtra("fragType", "search")
                intent1.putExtra("source", "foryou")
                startActivity(intent1)
            }
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
        mixfeedList!!.clear()
        mixfeedAdapter.notifyDataSetChanged()
        shimmerFrameLayout.visibility = View.VISIBLE
        shimmerFrameLayout.startShimmerAnimation()
        nextPageNumber = 1
        hitArticleListingApi(sortType)
    }

    override fun onResume() {
        super.onResume()
        shimmerFrameLayout.startShimmerAnimation()
        try {
            if (!StringUtils.isNullOrEmpty(sortType)) {
                if (Constants.KEY_RECENT.equals(
                        sortType,
                        ignoreCase = true
                    )) {
                    tracker!!.setScreenName("RecentScreen")
                    Utils.pushOpenScreenEvent(
                        activity, "RecentScreen",
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId + ""
                    )
                } else if (Constants.KEY_TODAYS_BEST.equals(
                        sortType,
                        ignoreCase = true
                    )) {
                    tracker!!.setScreenName("TodaysBestScreen")
                    Utils.pushOpenScreenEvent(
                        activity, "TodaysBestScreen",
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId + ""
                    )
                } else if (Constants.KEY_TRENDING.equals(
                        sortType,
                        ignoreCase = true
                    )) {
                    tracker!!.setScreenName("AllTrendingScreen")
                    Utils.pushOpenScreenEvent(
                        activity, "AllTrending",
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId + ""
                    )
                } else if (Constants.KEY_FOLLOWING.equals(
                        sortType,
                        ignoreCase = true
                    )) {
                    tracker!!.setScreenName("FollowingContentScreen")
                    Utils.pushOpenScreenEvent(
                        activity, "FollowingContentScreen",
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId + ""
                    )
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

    private fun launchVideoDetailsActivity(
        position: Int,
        videoIndex: Int
    ) { //        MixPanelUtils.pushMomVlogClickEvent(mixpanel, videoIndex, "TrendingAll");
        //        if (articleDataModelsNew.get(position).getCarouselVideoList() != null && !articleDataModelsNew.get(position)
        //                .getCarouselVideoList().isEmpty()) {
        //            if (isAdded()) {
        //                Utils.momVlogEvent(getActivity(), "Home Screen", "Vlog_card_home_feed",
        //                        "", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
        //                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
        //                        String.valueOf(System.currentTimeMillis()), "Show_Video_Listing", "", "");
        //            }
        //            VlogsListingAndDetailResult result = articleDataModelsNew.get(position).getCarouselVideoList()
        //                    .get(videoIndex);
        //            Intent intent = new Intent(getActivity(), ParallelFeedActivity.class);
        //            intent.putExtra(Constants.VIDEO_ID, result.getId());
        //            intent.putExtra(Constants.STREAM_URL, result.getUrl());
        //            intent.putExtra(Constants.AUTHOR_ID, result.getAuthor().getId());
        //            intent.putExtra(Constants.FROM_SCREEN, "Home Screen");
        //            intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Funny Videos");
        //            intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
        //            intent.putExtra(Constants.AUTHOR,
        //                    result.getAuthor().getId() + "~" + result.getAuthor().getFirstName() + " " + result.getAuthor()
        //                            .getLastName());
        //            startActivity(intent);
        //        }
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
            Utils.pushGenericEvent(
                activity, "CTA_Follow_100WS_Detail", userDynamoId,
                "ArticleListingFragment"
            )
            mixfeedList!![position].isfollowing = "1"
            mixfeedAdapter.notifyDataSetChanged()
            val followUnfollowUserResponseCall =
                followApi.followUserInShortStoryListingV2(request)
            followUnfollowUserResponseCall.enqueue(followUnfollowUserResponseCallback)
        }
    }

    private val followUnfollowUserResponseCallback: Callback<ResponseBody?> =
        object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
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
        if (isAdded) {
            when (shareMedium) {
                AppConstants.MEDIUM_FACEBOOK -> {
                    SharingUtils.shareViaFacebook(activity, uri)
                    Utils.pushShareStoryEvent(
                        activity, "ArticleListingFragment",
                        userDynamoId + "", sharedStoryItem.id,
                        sharedStoryItem.userId + "~" + sharedStoryItem.userName, "Facebook"
                    )
                }
                AppConstants.MEDIUM_WHATSAPP -> {
                    if (AppUtils.shareImageWithWhatsApp(
                            activity, uri, getString(
                            R.string.ss_follow_author,
                            sharedStoryItem!!.userName,
                            AppConstants.USER_PROFILE_SHARE_BASE_URL + sharedStoryItem!!.userId
                        )
                        )) {
                        Utils.pushShareStoryEvent(
                            activity, "ArticleListingFragment",
                            userDynamoId + "", sharedStoryItem.id,
                            sharedStoryItem.userId + "~" + sharedStoryItem.userName, "Whatsapp"
                        )
                    }
                }
                AppConstants.MEDIUM_INSTAGRAM -> {
                    if (AppUtils.shareImageWithInstagram(activity, uri)) {
                        Utils.pushShareStoryEvent(
                            activity, "ArticleListingFragment",
                            userDynamoId + "", sharedStoryItem.id,
                            sharedStoryItem.userId + "~" + sharedStoryItem.userName, "Instagram"
                        )
                    }
                }
                AppConstants.MEDIUM_GENERIC -> {
                    if (AppUtils.shareGenericImageAndOrLink(
                            activity, uri, getString(
                            R.string.ss_follow_author,
                            sharedStoryItem!!.userName,
                            AppConstants.USER_PROFILE_SHARE_BASE_URL + sharedStoryItem!!.userId
                        )
                        )) {
                        Utils.pushShareStoryEvent(
                            activity, "ArticleListingFragment",
                            userDynamoId + "", sharedStoryItem.id,
                            sharedStoryItem.userId + "~" + sharedStoryItem.userName, "Generic"
                        )
                    }
                }
                else -> {
                }
            }
        }
    }

    override fun onClick(view: View, position: Int) {
        when {
            view.id == R.id.articleItemView || view.id == R.id.videoItemView || view.id == R.id.rootView -> {
                launchContentDetail(mixfeedList?.get(position))
            }
            view.id == R.id.shareArticleImageView -> {
                //                shareContent(mixfeedList?.get(position))
            }
            view.id == R.id.facebookShareImageView -> {
                getSharableViewForPosition(position, AppConstants.MEDIUM_FACEBOOK)
            }
            view.id == R.id.whatsappShareImageView -> {
                getSharableViewForPosition(position, AppConstants.MEDIUM_WHATSAPP)
            }
            view.id == R.id.instagramShareImageView -> {
                try {
                    filterTags(mixfeedList?.get(position)?.tags!!)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
                getSharableViewForPosition(position, AppConstants.MEDIUM_INSTAGRAM)
            }
            view.id == R.id.genericShareImageView -> {
                getSharableViewForPosition(position, AppConstants.MEDIUM_GENERIC)
            }
            view.id == R.id.storyImageView1 -> {
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
            view.id == R.id.bookmarkArticleImageView -> {
                if (mixfeedList?.get(position)?.isbookmark == 0) {
                    bookmarkItem(position)
                } else {
                    deleteBookmark(position)
                }
            }
            view.id == R.id.authorNameTextView -> {
                val intent = Intent(activity, UserProfileActivity::class.java)
                intent.putExtra(
                    Constants.USER_ID,
                    mixfeedList?.get(position)?.userId
                )
                startActivity(intent)
            }
            view.id == R.id.followAuthorTextView -> {
                followApiCall(
                    mixfeedList!![position].userId, position
                )
            }
            view.id == R.id.menuItem -> {
                chooseMenuOptionsItem(view, position)
            }
            view.id == R.id.storyRecommendationContainer -> {
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
            view.id == R.id.menuItemImageView -> {
                //                showArticleMenuOptions(view, position)
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
}
