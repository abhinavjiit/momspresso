package com.mycity4kids.profile

import android.accounts.NetworkErrorException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.AppBarLayout
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.kelltontech.utils.ConnectivityUtils
import com.kelltontech.utils.StringUtils
import com.mycity4kids.R
import com.mycity4kids.animation.MyCityAnimationsUtil
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.collectionsModels.FeaturedOnModel
import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import com.mycity4kids.models.request.ArticleDetailRequest
import com.mycity4kids.models.request.FollowUnfollowUserRequest
import com.mycity4kids.models.response.*
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI
import com.mycity4kids.ui.activity.*
import com.mycity4kids.ui.fragment.AddCollectionPopUpDialogFragment
import com.mycity4kids.ui.fragment.UserBioDialogFragment
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.RoundedTransformation
import com.mycity4kids.widget.BadgesProfileWidget
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class M_PrivateProfileActivity : BaseActivity(),
        UserContentAdapter.RecyclerViewClickListener, View.OnClickListener, UsersFeaturedContentAdapter.RecyclerViewClickListener,
        AddCollectionPopUpDialogFragment.AddCollectionInterface, UsersBookmarksAdapter.RecyclerViewClickListener {

    private lateinit var toolbar: Toolbar
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var profileShimmerLayout: ShimmerFrameLayout
    private lateinit var headerContainer: RelativeLayout
    private lateinit var profileImageView: ImageView
    private lateinit var followerContainer: LinearLayout
    private lateinit var followingContainer: LinearLayout
    private lateinit var rankContainer: LinearLayout
    private lateinit var postsCountContainer: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var followingCountTextView: TextView
    private lateinit var followerCountTextView: TextView
    private lateinit var rankCountTextView: TextView
    private lateinit var postsCountTextView: TextView
    private lateinit var rankLanguageTextView: TextView
    private lateinit var authorNameTextView: TextView
    private lateinit var authorBioTextView: TextView
    private lateinit var cityTextView: TextView
    private lateinit var contentLangTextView: TextView
    private lateinit var creatorTab: ImageView
    private lateinit var featuredTab: ImageView
    private lateinit var bookmarksTab: ImageView
    private lateinit var divider2: View
    private lateinit var sharePrivateTextView: TextView
    private lateinit var sharePublicTextView: TextView
    private lateinit var analyticsTextView: TextView
    private lateinit var followAuthorTextView: TextView
    private lateinit var bottomLoadingView: RelativeLayout

    private lateinit var badgesContainer: BadgesProfileWidget
    private lateinit var myCollectionsWidget: MyCollectionsWidget

    private var pastVisiblesItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var start = 0
    private var size = 10
    private var isRequestRunning = true
    private var isLastPageReached = true
    private var authorId: String? = null
    private var isFollowing: Boolean = false
    private var isFollowUnFollowRequestRunning: Boolean = false
    private val multipleRankList = java.util.ArrayList<LanguageRanksModel>()
    private var userContentList: ArrayList<MixFeedResult>? = null
    private var userBookmarkList: ArrayList<MixFeedResult>? = null
    private var userFeaturedOnList: ArrayList<FeaturedItem>? = null

    private val userContentAdapter: UserContentAdapter by lazy { UserContentAdapter(this, AppUtils.isPrivateProfile(authorId)) }
    private val usersFeaturedContentAdapter: UsersFeaturedContentAdapter by lazy { UsersFeaturedContentAdapter(this) }
    private val usersBookmarksAdapter: UsersBookmarksAdapter by lazy { UsersBookmarksAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m_private_profile_activity)

        authorId = intent.getStringExtra(Constants.USER_ID)

        toolbar = findViewById(R.id.toolbar)
        appBarLayout = findViewById(R.id.appBarLayout);
        recyclerView = findViewById(R.id.recyclerView)
        profileShimmerLayout = findViewById(R.id.profileShimmerLayout)
        profileImageView = findViewById(R.id.profileImageView)
        followerContainer = findViewById(R.id.followerContainer)
        followingContainer = findViewById(R.id.followingContainer)
        rankContainer = findViewById(R.id.rankContainer)
        postsCountContainer = findViewById(R.id.postsCountContainer)
        headerContainer = findViewById(R.id.headerContainer)
        followingCountTextView = findViewById(R.id.followingCountTextView)
        followerCountTextView = findViewById(R.id.followerCountTextView)
        rankCountTextView = findViewById(R.id.rankCountTextView)
        postsCountTextView = findViewById(R.id.postsCountTextView)
        rankLanguageTextView = findViewById(R.id.rankLanguageTextView)
        authorNameTextView = findViewById(R.id.authorNameTextView)
        cityTextView = findViewById(R.id.cityTextView)
        authorBioTextView = findViewById(R.id.authorBioTextView)
        badgesContainer = findViewById(R.id.badgeContainer)
        myCollectionsWidget = findViewById(R.id.myCollectionsWidget)
        contentLangTextView = findViewById(R.id.contentLangTextView)
        creatorTab = findViewById(R.id.creatorTab)
        featuredTab = findViewById(R.id.featuredTab)
        bookmarksTab = findViewById(R.id.bookmarksTab)
        divider2 = findViewById(R.id.divider2)
        sharePrivateTextView = findViewById(R.id.sharePrivateTextView)
        analyticsTextView = findViewById(R.id.analyticsTextView)
        followAuthorTextView = findViewById(R.id.followAuthorTextView)
        sharePublicTextView = findViewById(R.id.sharePublicTextView)
        bottomLoadingView = findViewById(R.id.bottomLoadingView)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

//        appBarLayout.addOnOffsetChangedListener(object:AppBarLayout.OnOffsetChangedListener{
//            override fun onOffsetChanged(p0: AppBarLayout?, verticalOffset: Int) {
//                headerContainer.alpha = 1.0f - Math.abs(verticalOffset / appBarLayout.totalScrollRange)
//            }
//        })

        if (AppUtils.isPrivateProfile(authorId)) {
            authorId = SharedPrefUtils.getUserDetailModel(this).dynamoId
            followerContainer.setOnClickListener(this)
            followingContainer.setOnClickListener(this)
            rankContainer.setOnClickListener(this)
            sharePrivateTextView.setOnClickListener(this)
            analyticsTextView.setOnClickListener(this)
            followAuthorTextView.visibility = View.GONE
            sharePublicTextView.visibility = View.GONE
            sharePrivateTextView.visibility = View.VISIBLE
            analyticsTextView.visibility = View.VISIBLE
            myCollectionsWidget.getCollections(authorId, true)
        } else {
            bookmarksTab.visibility = View.GONE
            divider2.visibility = View.GONE
            myCollectionsWidget.visibility = View.GONE
            followAuthorTextView.visibility = View.VISIBLE
            sharePublicTextView.visibility = View.VISIBLE
            sharePrivateTextView.visibility = View.GONE
            analyticsTextView.visibility = View.GONE
            followAuthorTextView.setOnClickListener(this)
            sharePublicTextView.setOnClickListener(this)
            checkFollowingStatusAPI()
            myCollectionsWidget.getCollections(authorId, false)
        }

        val llm = LinearLayoutManager(this)
        llm.orientation = RecyclerView.VERTICAL

        recyclerView.layoutManager = llm
        recyclerView.adapter = userContentAdapter

        userContentList = ArrayList()
        userBookmarkList = ArrayList()
        userFeaturedOnList = ArrayList()

        profileShimmerLayout.startShimmerAnimation()

        creatorTab.setOnClickListener(this)
        featuredTab.setOnClickListener(this)
        bookmarksTab.setOnClickListener(this)
        badgesContainer.setOnClickListener(this)
        postsCountContainer.setOnClickListener(this)

        creatorTab.isSelected = true
        featuredTab.isSelected = false
        bookmarksTab.isSelected = false
        if (AppUtils.isPrivateProfile(authorId)) {
            userContentList?.add(MixFeedResult(contentType = AppConstants.CONTENT_TYPE_CREATE_SECTION))
        }

        getUserDetail(authorId)
        badgesContainer.getBadges(authorId)
        getUsersCreatedContent(authorId)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = llm.childCount
                    totalItemCount = llm.itemCount
                    pastVisiblesItems = llm.findFirstVisibleItemPosition()
                    if (!isRequestRunning && !isLastPageReached) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            isRequestRunning = true
                            bottomLoadingView.visibility = View.VISIBLE
                            if (creatorTab.isSelected) {
                                getUsersCreatedContent(authorId)
                            } else if (bookmarksTab.isSelected) {
                                getUsersBookmark()
                            } else {
                                getFeaturedContent()
                            }
                        }
                    }
                }
            }
        })
    }

    private fun getUserDetail(authorId: String?) {
        val retrofit = BaseApplication.getInstance().retrofit
        val bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI::class.java)
        val call = bloggerDashboardAPI.getBloggerData(authorId)
        call.enqueue(object : Callback<UserDetailResponse> {
            override fun onResponse(call: Call<UserDetailResponse>, response: retrofit2.Response<UserDetailResponse>) {
                if (null == response.body()) {
                    return
                }
                try {
                    profileShimmerLayout.visibility = View.GONE
                    headerContainer.visibility = View.VISIBLE
                    val responseData = response.body() as UserDetailResponse
                    if (responseData.code == 200 && Constants.SUCCESS == responseData.status) {
                        processCityInfo(responseData)
                        processContentLanguages(responseData)
                        processAuthorRank(responseData)
                        processAuthorsFollowingAndFollowership(responseData)
                        processAuthorPersonalDetails(responseData)
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<UserDetailResponse>, e: Throwable) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        })
    }

    private fun checkFollowingStatusAPI() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog()
            showToast(getString(R.string.error_network))
            return
        }
        val retro = BaseApplication.getInstance().retrofit
        val articleDetailsAPI = retro.create(ArticleDetailsAPI::class.java)
        val articleDetailRequest = ArticleDetailRequest()
        articleDetailRequest.articleId = ""
        val callBookmark = articleDetailsAPI.checkFollowingBookmarkStatus("0", authorId)
        callBookmark.enqueue(object : Callback<ArticleDetailResponse> {
            override fun onResponse(call: Call<ArticleDetailResponse>, response: retrofit2.Response<ArticleDetailResponse>) {
                if (null == response.body()) {
                    showToast(getString(R.string.server_went_wrong))
                    return
                }
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                    isFollowing = if ("0" == responseData.data.result.isFollowed) {
                        followAuthorTextView.setText(R.string.ad_follow_author)
                        false
                    } else {
                        followAuthorTextView.setText(R.string.ad_following_author)
                        true
                    }
                } else {
                    showToast(getString(R.string.server_went_wrong))
                }
            }

            override fun onFailure(call: Call<ArticleDetailResponse>, t: Throwable) {
                when (t) {
                    is UnknownHostException -> showToast(getString(R.string.error_network))
                    is SocketTimeoutException -> showToast(getString(R.string.connection_timeout))
                    else -> showToast(getString(R.string.server_went_wrong))
                }
                Crashlytics.logException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        })
    }

    private fun hitFollowUnfollowAPI() {
        val retrofit = BaseApplication.getInstance().retrofit
        val followAPI = retrofit.create(FollowAPI::class.java)
        val request = FollowUnfollowUserRequest()
        request.followerId = authorId
        if (isFollowing) {
            isFollowing = false
            followAuthorTextView.setText(R.string.ad_follow_author)
            val followUnfollowUserResponseCall = followAPI.unfollowUser(request)
            followUnfollowUserResponseCall.enqueue(unfollowUserResponseCallback)
        } else {
            isFollowing = true
            followAuthorTextView.setText(R.string.ad_following_author)
            val followUnfollowUserResponseCall = followAPI.followUser(request)
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback)
        }
    }

    private var followUserResponseCallback: Callback<FollowUnfollowUserResponse> = object : Callback<FollowUnfollowUserResponse> {
        override fun onResponse(call: Call<FollowUnfollowUserResponse>, response: retrofit2.Response<FollowUnfollowUserResponse>) {
            isFollowUnFollowRequestRunning = false
            if (response.body() == null) {
                showToast(getString(R.string.went_wrong))
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {

                } else {
                    followAuthorTextView.setText(R.string.ad_follow_author)
                    isFollowing = false
                }
            } catch (e: Exception) {
                showToast(getString(R.string.server_went_wrong))
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<FollowUnfollowUserResponse>, t: Throwable) {
            isFollowUnFollowRequestRunning = false
            showToast(getString(R.string.server_went_wrong))
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    private var unfollowUserResponseCallback: Callback<FollowUnfollowUserResponse> = object : Callback<FollowUnfollowUserResponse> {
        override fun onResponse(call: Call<FollowUnfollowUserResponse>, response: retrofit2.Response<FollowUnfollowUserResponse>) {
            isFollowUnFollowRequestRunning = false
            if (response.body() == null) {
                showToast(getString(R.string.went_wrong))
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                } else {
                    followAuthorTextView.setText(R.string.ad_following_author)
                    isFollowing = true
                }
            } catch (e: Exception) {
                showToast(getString(R.string.server_went_wrong))
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<FollowUnfollowUserResponse>, t: Throwable) {
            isFollowUnFollowRequestRunning = false
            showToast(getString(R.string.server_went_wrong))
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    private fun processAuthorPersonalDetails(responseData: UserDetailResponse) {
        authorNameTextView.text = responseData.data[0].result.firstName + " " + responseData.data[0].result.lastName
        if (!StringUtils.isNullOrEmpty(responseData.data[0].result.profilePicUrl.clientApp)) {
            Picasso.with(this@M_PrivateProfileActivity).load(responseData.data[0].result.profilePicUrl.clientApp)
                    .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(profileImageView)
        }
        if (responseData.data[0].result.userBio == null || responseData.data[0].result.userBio.isEmpty()) {
            authorBioTextView.visibility = View.GONE
        } else {
            authorBioTextView.text = responseData.data[0].result.userBio
            authorBioTextView.visibility = View.VISIBLE
            makeTextViewResizable(authorBioTextView, 2, "See More", true, responseData.data[0].result.userBio)
        }
    }

    private fun processContentLanguages(responseData: UserDetailResponse) {
        if (responseData.data[0].result.createrLangs.isEmpty()) {
            contentLangTextView.visibility = View.GONE
        } else {
            contentLangTextView.visibility = View.VISIBLE
            var contentLang: String = responseData.data[0].result.createrLangs[0]
            for (i in 1 until responseData.data[0].result.createrLangs.size) {
                contentLang = contentLang + " \u2022 " + responseData.data[0].result.createrLangs[i]
            }
            contentLangTextView.text = contentLang
        }
    }

    private fun processCityInfo(responseData: UserDetailResponse) {
        if (StringUtils.isNullOrEmpty(responseData.data[0].result.cityName)) {
            cityTextView.visibility = View.GONE
        } else {
            cityTextView.visibility = View.VISIBLE
            cityTextView.text = responseData.data[0].result.cityName
        }
    }

    private fun processAuthorsFollowingAndFollowership(responseData: UserDetailResponse) {
        val followerCount = Integer.parseInt(responseData.data[0].result.followersCount)
        if (followerCount > 999) {
            val singleFollowerCount = followerCount.toFloat() / 1000
            followerCountTextView.text = "" + singleFollowerCount + "k"
        } else {
            followerCountTextView.text = "" + followerCount
        }

        val followingCount = Integer.parseInt(responseData.data[0].result.followingCount)
        if (followingCount > 999) {
            val singleFollowingCount = followingCount.toFloat() / 1000
            followingCountTextView.text = "" + singleFollowingCount + "k"
        } else {
            followingCountTextView.text = "" + followingCount
        }
    }

    private fun processAuthorRank(responseData: UserDetailResponse) {
        if (responseData.data[0].result.ranks == null || responseData.data[0].result.ranks.size == 0) {
            rankCountTextView.text = "--"
            rankLanguageTextView.text = getString(R.string.myprofile_rank_label)
        } else if (responseData.data[0].result.ranks.size < 2) {
            rankCountTextView.text = "" + responseData.data[0].result.ranks[0].rank
            if (AppConstants.LANG_KEY_ENGLISH == responseData.data[0].result.ranks[0].langKey) {
                rankLanguageTextView.text = getString(R.string.blogger_profile_rank_in) + " ENGLISH"
            } else {
                rankLanguageTextView.text = (getString(R.string.blogger_profile_rank_in)
                        + " " + responseData.data[0].result.ranks[0].langValue.toUpperCase())
            }
        } else {
            for (i in 0 until responseData.data[0].result.ranks.size) {
                if (AppConstants.LANG_KEY_ENGLISH == responseData.data[0].result.ranks[i].langKey) {
                    multipleRankList.add(responseData.data[0].result.ranks[i])
                    break
                }
            }
            responseData.data[0].result.ranks.sort()
            for (i in 0 until responseData.data[0].result.ranks.size) {
                if (AppConstants.LANG_KEY_ENGLISH != responseData.data[0].result.ranks[i].langKey) {
                    multipleRankList.add(responseData.data[0].result.ranks[i])
                }
            }
            MyCityAnimationsUtil.animate(this@M_PrivateProfileActivity, rankContainer, multipleRankList, 0, true)
        }
    }

    private fun getUsersCreatedContent(authorId: String?) {
        val retro = BaseApplication.getInstance().retrofit
        val bloggerDashboardAPI = retro.create(BloggerDashboardAPI::class.java)

//        val call = if (creatorTab.isSelected) {
        val call = bloggerDashboardAPI.getUsersAllContent(authorId, start, size, null)
//        } else {
//            bloggerDashboardAPI.getUsersAllBookmark(start, size, 1)
//        }

        call.enqueue(object : Callback<MixFeedResponse> {
            override fun onResponse(call: Call<MixFeedResponse>, response: retrofit2.Response<MixFeedResponse>) {
                try {
                    isRequestRunning = false
                    bottomLoadingView.visibility = View.GONE
                    if (null == response.body()) {
                        val nee = NetworkErrorException(response.raw().toString())
                        Crashlytics.logException(nee)
                        return
                    }
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        processUserContentResponse(responseData.data.result)
                    } else {
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<MixFeedResponse>, e: Throwable) {
                bottomLoadingView.visibility = View.GONE
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        })
    }

    private fun processUserContentResponse(responseData: List<MixFeedResult>?) {
        if (responseData.isNullOrEmpty()) {
            isLastPageReached = true
            if (!userContentList.isNullOrEmpty()) {
                //No more next results for search from pagination
            } else {
                // No results
                userContentAdapter.setListData(userContentList)
                userContentAdapter.notifyDataSetChanged()
//                noBlogsTextView.setText(getString(R.string.short_s_no_published))
//                noBlogsTextView.setVisibility(View.VISIBLE)
            }
        } else {
            start += size
            userContentList?.addAll(responseData)
            userContentAdapter.setListData(userContentList)
            userContentAdapter.notifyDataSetChanged()
        }
    }

    private fun getUsersBookmark() {
        val retro = BaseApplication.getInstance().retrofit
        val bloggerDashboardAPI = retro.create(BloggerDashboardAPI::class.java)
        val call = bloggerDashboardAPI.getUsersAllBookmark(start, size, 1)
        call.enqueue(object : Callback<MixFeedResponse> {
            override fun onResponse(call: Call<MixFeedResponse>, response: retrofit2.Response<MixFeedResponse>) {
                try {
                    isRequestRunning = false
                    bottomLoadingView.visibility = View.GONE
                    if (null == response.body()) {
                        val nee = NetworkErrorException(response.raw().toString())
                        Crashlytics.logException(nee)
                        return
                    }
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        processUserBookmarks(responseData.data.result)
                    } else {
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<MixFeedResponse>, e: Throwable) {
                bottomLoadingView.visibility = View.GONE
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        })
    }

    private fun processUserBookmarks(result: List<MixFeedResult>?) {
        if (result.isNullOrEmpty()) {
            isLastPageReached = true
            if (!userContentList.isNullOrEmpty()) {
                //No more next results for search from pagination
            } else {
                // No results
                usersBookmarksAdapter.setListData(userBookmarkList)
                usersBookmarksAdapter.notifyDataSetChanged()
//                noBlogsTextView.setText(getString(R.string.short_s_no_published))
//                noBlogsTextView.setVisibility(View.VISIBLE)
            }
        } else {
            start += size
            userBookmarkList?.addAll(result)
            usersBookmarksAdapter.setListData(userBookmarkList)
            usersBookmarksAdapter.notifyDataSetChanged()
        }
    }

    private fun getFeaturedContent() {
        val retrofit = BaseApplication.getInstance().retrofit
        val featureListAPI = retrofit.create(CollectionsAPI::class.java)
        authorId?.let {
            val call = featureListAPI.getFeaturedOnCollections(it, start, size)
            call.enqueue(object : Callback<FeaturedOnModel> {
                override fun onResponse(call: Call<FeaturedOnModel>, response: retrofit2.Response<FeaturedOnModel>) {
                    if (null == response.body()) {
                        val nee = NetworkErrorException(response.raw().toString())
                        Crashlytics.logException(nee)
                        return
                    }
                    try {
                        val responseData = response.body() as FeaturedOnModel
                        if (responseData.code == 200 && Constants.SUCCESS == responseData.status) {
                            processFeaturedContentResponse(responseData.data.result.item_list)
                            usersFeaturedContentAdapter.setListData(userFeaturedOnList)
                            usersFeaturedContentAdapter.notifyDataSetChanged()
                        } else {
                        }
                    } catch (e: Exception) {
                        Crashlytics.logException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }
                }

                override fun onFailure(call: Call<FeaturedOnModel>, t: Throwable) {
                    Crashlytics.logException(t)
                    Log.d("MC4kException", Log.getStackTraceString(t))
                }
            })
        }

    }

    private fun processFeaturedContentResponse(featuredItemList: List<FeaturedItem>) {
        if (featuredItemList.isNullOrEmpty()) {
            isLastPageReached = true
            if (!userFeaturedOnList.isNullOrEmpty()) {
                //No more next results for search from pagination
            } else {
                // No results
                usersFeaturedContentAdapter.setListData(userFeaturedOnList)
                usersFeaturedContentAdapter.notifyDataSetChanged()
//                noBlogsTextView.setText(getString(R.string.short_s_no_published))
//                noBlogsTextView.setVisibility(View.VISIBLE)
            }
        } else {
            start += size
            userFeaturedOnList?.addAll(featuredItemList)
            usersFeaturedContentAdapter.setListData(userFeaturedOnList)
            usersFeaturedContentAdapter.notifyDataSetChanged()
        }
    }

    override fun onClick(view: View?) {
        when {
            view?.id == R.id.creatorTab -> {
                creatorTab.isSelected = true
                featuredTab.isSelected = false
                bookmarksTab.isSelected = false
                userContentList?.clear()
                start = 0
                recyclerView.adapter = userContentAdapter
                if (AppUtils.isPrivateProfile(authorId)) {
                    userContentList?.add(MixFeedResult(contentType = AppConstants.CONTENT_TYPE_CREATE_SECTION))
                }
                getUsersCreatedContent(authorId)
            }
            view?.id == R.id.featuredTab -> {
                creatorTab.isSelected = false
                featuredTab.isSelected = true
                bookmarksTab.isSelected = false
                recyclerView.adapter = usersFeaturedContentAdapter
                userFeaturedOnList?.clear()
                start = 0
                getFeaturedContent()
            }
            view?.id == R.id.bookmarksTab -> {
                creatorTab.isSelected = false
                featuredTab.isSelected = false
                bookmarksTab.isSelected = true
                userBookmarkList?.clear()
                start = 0
                recyclerView.adapter = usersBookmarksAdapter
                getUsersBookmark()
            }
            view?.id == R.id.badgeContainer -> {
                val intent = Intent(this, BadgeActivity::class.java)
                startActivity(intent)
            }
            view?.id == R.id.followAuthorTextView -> {
                if (!isFollowUnFollowRequestRunning) {
                    isFollowUnFollowRequestRunning = true
                    hitFollowUnfollowAPI()
                }
            }
            view?.id == R.id.sharePrivateTextView -> {

            }
            view?.id == R.id.sharePublicTextView -> {

            }
            view?.id == R.id.analyticsTextView -> {
                if (AppConstants.DEBUGGING_USER_ID.contains("" + authorId)) {
                    rankContainer.setOnLongClickListener {
                        BaseApplication.getInstance().toggleGroupBaseURL()
                        false
                    }
                    val intent = Intent(this, IdTokenLoginActivity::class.java)
                    startActivity(intent)
                    return
                } else {
                    val intent = Intent(this, RankingActivity::class.java)
                    startActivity(intent)
                }
            }
            view?.id == R.id.followerContainer -> {
                val intent = Intent(this, FollowersAndFollowingListActivity::class.java)
                intent.putExtra(AppConstants.FOLLOW_LIST_TYPE, AppConstants.FOLLOWER_LIST)
                intent.putExtra(AppConstants.USER_ID_FOR_FOLLOWING_FOLLOWERS, authorId)
                startActivity(intent)
            }
            view?.id == R.id.followingContainer -> {
                val intent = Intent(this, FollowersAndFollowingListActivity::class.java)
                intent.putExtra(AppConstants.FOLLOW_LIST_TYPE, AppConstants.FOLLOWING_LIST)
                intent.putExtra(AppConstants.USER_ID_FOR_FOLLOWING_FOLLOWERS, authorId)
                startActivity(intent)
            }
            view?.id == R.id.rankContainer -> {

            }
            view?.id == R.id.postsCountContainer -> {
                appBarLayout.setExpanded(false)
            }
        }
    }

    override fun onClick(view: View, position: Int) {
        when {
            view.id == R.id.articleItemView -> {
                val intent = Intent(this, ArticleDetailsContainerActivity::class.java)
                intent.putExtra(Constants.ARTICLE_ID, userContentList?.get(position)?.id)
                intent.putExtra(Constants.AUTHOR_ID, userContentList?.get(position)?.userId)
                intent.putExtra(Constants.BLOG_SLUG, userContentList?.get(position)?.blogTitleSlug)
                intent.putExtra(Constants.TITLE_SLUG, userContentList?.get(position)?.titleSlug)
                intent.putExtra(Constants.FROM_SCREEN, "Profile")
                intent.putExtra(Constants.AUTHOR, userContentList?.get(position)?.userId + "~" + userContentList?.get(position)?.userName)
//                userContentList?.let {
//                    val filteredResult = AppUtils.getFilteredContentList(it., AppConstants.CONTENT_TYPE_ARTICLE)
//                    intent.putParcelableArrayListExtra("pagerListData", filteredResult)
//                    intent.putExtra(Constants.ARTICLE_INDEX, "" + AppUtils.getFilteredPosition(posSubList, articleDataModelsSubList, AppConstants.CONTENT_TYPE_ARTICLE))
//                }
                startActivity(intent)
            }
            view.id == R.id.rootView -> {
                val intent = Intent(this, ShortStoryContainerActivity::class.java)
                intent.putExtra(Constants.ARTICLE_ID, userContentList?.get(position)?.id)
                intent.putExtra(Constants.AUTHOR_ID, userContentList?.get(position)?.userId)
                intent.putExtra(Constants.BLOG_SLUG, userContentList?.get(position)?.blogTitleSlug)
                intent.putExtra(Constants.TITLE_SLUG, userContentList?.get(position)?.titleSlug)
                intent.putExtra(Constants.FROM_SCREEN, "HomeScreen")
                intent.putExtra(Constants.AUTHOR, userContentList?.get(position)?.userId + "~" + userContentList?.get(position)?.userName)
//                val filteredResult = AppUtils.getFilteredContentList(articleDataModelsSubList, AppConstants.CONTENT_TYPE_SHORT_STORY)
//                intent.putParcelableArrayListExtra("pagerListData", filteredResult)
//                intent.putExtra(Constants.ARTICLE_INDEX, "" + AppUtils.getFilteredPosition(posSubList, articleDataModelsSubList, AppConstants.CONTENT_TYPE_SHORT_STORY))
                startActivity(intent)
            }
            view.id == R.id.videoItemView -> {
                val intent = Intent(this, ParallelFeedActivity::class.java)
                intent.putExtra(Constants.VIDEO_ID, userContentList?.get(position)?.id)
                intent.putExtra(Constants.FROM_SCREEN, "Search Screen")
                startActivity(intent)
            }
            view.id == R.id.draftContainer -> {
                val intent = Intent(this, UserDraftsContentActivity::class.java)
                startActivity(intent)
            }
            view.id == R.id.articleContainer -> {
                val articleIntent = Intent(this, UserPublishedContentActivity::class.java)
                articleIntent.putExtra("isPrivateProfile", true)
                articleIntent.putExtra("contentType", AppConstants.CONTENT_TYPE_ARTICLE)
                startActivity(articleIntent)
            }
            view.id == R.id.storyContainer -> {
                val articleIntent = Intent(this, UserPublishedContentActivity::class.java)
                articleIntent.putExtra("isPrivateProfile", true)
                articleIntent.putExtra("contentType", AppConstants.CONTENT_TYPE_SHORT_STORY)
                startActivity(articleIntent)
            }
            view.id == R.id.vlogContainer -> {
                val articleIntent = Intent(this, UserPublishedContentActivity::class.java)
                articleIntent.putExtra("isPrivateProfile", true)
                articleIntent.putExtra("contentType", AppConstants.CONTENT_TYPE_VIDEO)
                startActivity(articleIntent)
            }
        }
    }

    override fun onFeaturedItemClick(view: View, position: Int) {
        when {
            view.id == R.id.featuredItemRootView -> {

            }
            view.id == R.id.moreItemsTextView -> {

            }
        }
    }

    override fun onBookmarkItemInteraction(view: View, position: Int) {


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

    override fun onCollectionAddSuccess() {
        Handler().postDelayed(Runnable {
            myCollectionsWidget.refresh(AppUtils.isPrivateProfile(authorId))
        }, 1000)
    }


    fun makeTextViewResizable(tv: TextView, maxLine: Int, expandText: String, viewMore: Boolean, userBio: String) {
        if (tv.tag == null) {
            tv.tag = tv.text
        }
        val vto = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val obs = tv.viewTreeObserver
                obs.removeOnGlobalLayoutListener(this)
                if (maxLine == 0) {
                    val lineEndIndex = tv.layout.getLineEnd(0)
                    val text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1).toString() + " " + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.text.toString()), tv, maxLine, expandText,
                                    viewMore, userBio), TextView.BufferType.SPANNABLE)
                } else if (maxLine > 0 && tv.lineCount > maxLine) {
                    val lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                    if (lineEndIndex - expandText.length + 1 > 10) {
                        val text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1).toString() + " " + expandText
                        tv.text = text
                        tv.movementMethod = LinkMovementMethod.getInstance()
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.text.toString()), tv, maxLine, expandText,
                                        viewMore, userBio), TextView.BufferType.SPANNABLE)
                    } else {
                        val text = tv.text.subSequence(0, lineEndIndex).toString() + " " + expandText
                        tv.text = text
                        tv.movementMethod = LinkMovementMethod.getInstance()
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.text.toString()), tv, maxLine, expandText,
                                        viewMore, userBio), TextView.BufferType.SPANNABLE)
                    }
                } else {
                }
            }
        })
    }

    private fun addClickablePartTextViewResizable(strSpanned: Spanned, tv: TextView,
                                                  maxLine: Int, spanableText: String, viewMore: Boolean, userBio: String): SpannableStringBuilder {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        if (str.contains(spanableText)) {
            ssb.setSpan(object : MySpannable(false) {
                override fun onClick(widget: View) {
                    val userBioDialogFragment = UserBioDialogFragment()
                    val fm = supportFragmentManager
                    val _args = Bundle()
                    _args.putString("userBio", userBio)
                    userBioDialogFragment.arguments = _args
                    userBioDialogFragment.isCancelable = true
                    userBioDialogFragment.show(fm, "Choose video option")
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
        }
        return ssb
    }

    open inner class MySpannable(isUnderline: Boolean) : ClickableSpan() {
        private var isUnderline = true

        init {
            this.isUnderline = isUnderline
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = isUnderline
            ds.color = Color.parseColor("#1b76d3")
        }

        override fun onClick(widget: View) {
        }
    }

    override fun updateUi(response: Response?) {

    }
}