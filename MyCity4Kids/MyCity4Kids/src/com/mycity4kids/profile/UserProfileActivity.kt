package com.mycity4kids.profile

import android.Manifest
import android.accounts.NetworkErrorException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.kelltontech.utils.ConnectivityUtils
import com.kelltontech.utils.StringUtils
import com.mycity4kids.R
import com.mycity4kids.animation.MyCityAnimationsUtil
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.collectionsModels.FeaturedOnModel
import com.mycity4kids.models.request.ArticleDetailRequest
import com.mycity4kids.models.request.FollowUnfollowUserRequest
import com.mycity4kids.models.response.*
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI
import com.mycity4kids.ui.activity.*
import com.mycity4kids.ui.activity.collection.UserCollectionItemListActivity
import com.mycity4kids.ui.fragment.AddCollectionAndCollectionItemDialogFragment
import com.mycity4kids.ui.fragment.AddCollectionPopUpDialogFragment
import com.mycity4kids.ui.fragment.UserBioDialogFragment
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.PermissionUtil
import com.mycity4kids.utils.RoundedTransformation
import com.mycity4kids.widget.BadgesProfileWidget
import com.mycity4kids.widget.ResizableTextView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class UserProfileActivity : BaseActivity(),
        UserContentAdapter.RecyclerViewClickListener, View.OnClickListener, UsersFeaturedContentAdapter.RecyclerViewClickListener,
        AddCollectionPopUpDialogFragment.AddCollectionInterface, UsersBookmarksAdapter.RecyclerViewClickListener,
        ResizableTextView.SeeMore {

    val REQUEST_GALLERY_PERMISSION = 1
    val PERMISSIONS_INIT = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val sharableProfileImageName = "profile"
    var shareCardType: String? = null
    var shareMedium: String? = null
    var shareContentPosition: Int? = null
    lateinit var isRewardAdded: String


    private lateinit var rootLayout: CoordinatorLayout
    private lateinit var toolbar: Toolbar
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var profileShimmerLayout: ShimmerFrameLayout
    private lateinit var headerContainer: RelativeLayout
    private lateinit var appSettingsImageView: ImageView
    private lateinit var profileImageView: ImageView
    private lateinit var crownImageView: ImageView
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
    private lateinit var authorBioTextView: ResizableTextView
    private lateinit var cityTextView: TextView
    private lateinit var contentLangTextView: TextView
    private lateinit var contentLangContainer: LinearLayout
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
    private lateinit var profileShareCardWidget: ProfileShareCardWidget

    private var pastVisiblesItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var start = 0
    private var size = 10
    private var isRequestRunning = true
    private var isLastPageReached = false
    private var authorId: String? = null
    private var isFollowing: Boolean = false
    private var isFollowUnFollowRequestRunning: Boolean = false
    private val multipleRankList = java.util.ArrayList<LanguageRanksModel>()
    private var userContentList: ArrayList<MixFeedResult>? = null
    private var userBookmarkList: ArrayList<MixFeedResult>? = null
    private var userFeaturedOnList: ArrayList<MixFeedResult>? = null
    private var deeplinkBadgeId: String? = null
    private var profileDetail: String? = null

    private val userContentAdapter: UserContentAdapter by lazy { UserContentAdapter(this, AppUtils.isPrivateProfile(authorId)) }
    private val usersFeaturedContentAdapter: UsersFeaturedContentAdapter by lazy { UsersFeaturedContentAdapter(this) }
    private val usersBookmarksAdapter: UsersBookmarksAdapter by lazy { UsersBookmarksAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile_activity)

        rootLayout = findViewById(R.id.rootLayout)
        toolbar = findViewById(R.id.toolbar)
        appBarLayout = findViewById(R.id.appBarLayout);
        recyclerView = findViewById(R.id.recyclerView)
        profileShimmerLayout = findViewById(R.id.profileShimmerLayout)
        profileImageView = findViewById(R.id.profileImageView)
        appSettingsImageView = findViewById(R.id.appSettingsImageView)
        crownImageView = findViewById(R.id.crownImageView)
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
        contentLangContainer = findViewById(R.id.contentLangContainer)
        creatorTab = findViewById(R.id.creatorTab)
        featuredTab = findViewById(R.id.featuredTab)
        bookmarksTab = findViewById(R.id.bookmarksTab)
        divider2 = findViewById(R.id.divider2)
        sharePrivateTextView = findViewById(R.id.sharePrivateTextView)
        analyticsTextView = findViewById(R.id.analyticsTextView)
        followAuthorTextView = findViewById(R.id.followAuthorTextView)
        sharePublicTextView = findViewById(R.id.sharePublicTextView)
        bottomLoadingView = findViewById(R.id.bottomLoadingView)
        profileShareCardWidget = findViewById(R.id.profileShareCardWidget)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        contentLangTextView.isSelected = true
        cityTextView.isSelected = true

        authorId = intent.getStringExtra(Constants.USER_ID)
        deeplinkBadgeId = intent.getStringExtra("badgeId")
        profileDetail = intent.getStringExtra("detail")

        if (!deeplinkBadgeId.isNullOrBlank()) {
            showBadgeDialog(deeplinkBadgeId)
        }

//        profileImageView.setOnClickListener {
//            if (SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId == authorId) {
//                val pIntent = Intent(this, PrivateProfileActivity::class.java)
//                startActivity(pIntent)
//            } else {
//                val intentnn = Intent(this, PublicProfileActivity::class.java)
//                intentnn.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, authorId)
//                startActivity(intentnn)
//            }
//        }

        if (AppUtils.isPrivateProfile(authorId)) {
            authorId = SharedPrefUtils.getUserDetailModel(this).dynamoId
            followerContainer.setOnClickListener(this)
            followingContainer.setOnClickListener(this)
            sharePrivateTextView.setOnClickListener(this)
            analyticsTextView.setOnClickListener(this)
            followAuthorTextView.visibility = View.GONE
            sharePublicTextView.visibility = View.GONE
            sharePrivateTextView.visibility = View.VISIBLE
            analyticsTextView.visibility = View.VISIBLE
            appSettingsImageView.visibility = View.VISIBLE
            myCollectionsWidget.getCollections(authorId, true)
            Utils.pushGenericEvent(this, "Show_Private_Profile", authorId, "UserProfileActivity")
        } else {
            bookmarksTab.visibility = View.GONE
            divider2.visibility = View.GONE
            followAuthorTextView.visibility = View.VISIBLE
            sharePublicTextView.visibility = View.VISIBLE
            sharePrivateTextView.visibility = View.GONE
            analyticsTextView.visibility = View.GONE
            appSettingsImageView.visibility = View.GONE
            followAuthorTextView.setOnClickListener(this)
            sharePublicTextView.setOnClickListener(this)
            checkFollowingStatusAPI()
            myCollectionsWidget.getCollections(authorId, false)
            Utils.pushGenericEvent(this, "Show_Public_Profile", authorId, "UserProfileActivity")
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
        appSettingsImageView.setOnClickListener(this)

        creatorTab.isSelected = true
        featuredTab.isSelected = false
        bookmarksTab.isSelected = false
        if (AppUtils.isPrivateProfile(authorId)) {
            userContentList?.add(MixFeedResult(contentType = AppConstants.CONTENT_TYPE_CREATE_SECTION))
        }

        getUserDetail(authorId)
        badgesContainer.getBadges(authorId)
        getUsersCreatedContent()

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
                                getUsersCreatedContent()
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
                    profileShareCardWidget.visibility = View.INVISIBLE
                    val responseData = response.body() as UserDetailResponse
                    if (responseData.code == 200 && Constants.SUCCESS == responseData.status) {
                        isRewardAdded = responseData.data.get(0).result.rewardsAdded
                        processCityInfo(responseData)
                        processContentLanguages(responseData)
                        processAuthorRankAndCrown(responseData)
                        processAuthorPostCount(responseData)
                        processAuthorsFollowingAndFollowership(responseData)
                        processAuthorPersonalDetails(responseData)
                        profileShareCardWidget.populateUserDetails(authorId!!, responseData.data[0].result)
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
            Utils.pushProfileEvents(this, "CTA_Unfollow_Profile", "UserProfileActivity",
                    "Unfollow", "-");
        } else {
            isFollowing = true
            followAuthorTextView.setText(R.string.ad_following_author)
            val followUnfollowUserResponseCall = followAPI.followUser(request)
            followUnfollowUserResponseCall.enqueue(followUserResponseCallback)
            Utils.pushProfileEvents(this, "CTA_Follow_Profile", "UserProfileActivity",
                    "Follow", "-");
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
            Picasso.with(this@UserProfileActivity).load(responseData.data[0].result.profilePicUrl.clientApp)
                    .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(profileImageView)
        }
        if (responseData.data[0].result.userBio == null || responseData.data[0].result.userBio.isEmpty()) {
            authorBioTextView.visibility = View.GONE
        } else {
            authorBioTextView.text = responseData.data[0].result.userBio
            authorBioTextView.visibility = View.VISIBLE
            authorBioTextView.setUserBio(responseData.data[0].result.userBio, this)
        }
    }

    private fun processContentLanguages(responseData: UserDetailResponse) {
        if (responseData.data[0].result.createrLangs.isEmpty()) {
            contentLangContainer.visibility = View.GONE
        } else {
            contentLangContainer.visibility = View.VISIBLE
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
        val followingCount = Integer.parseInt(responseData.data[0].result.followingCount)
        followerCountTextView.text = AppUtils.withSuffix(followerCount.toLong())
        followingCountTextView.text = AppUtils.withSuffix(followingCount.toLong())
    }

    private fun processAuthorPostCount(responseData: UserDetailResponse) {
        postsCountTextView.text = responseData.data[0].result.totalArticles
    }

    private fun processAuthorRankAndCrown(responseData: UserDetailResponse) {
        var crown: Crown? = null
        try {
            val jsonObject = Gson().toJsonTree(responseData.data.get(0).result.crownData).asJsonObject
            crown = Gson().fromJson<Crown>(jsonObject, Crown::class.java)
            Picasso.with(this@UserProfileActivity).load(crown.image_url).error(
                    R.drawable.family_xxhdpi).fit().into(crownImageView)
            if (!profileDetail.isNullOrBlank() && profileDetail == "rank") {
                showCrownDialog(crown)
            }
        } catch (e: Exception) {
            crownImageView.visibility = View.GONE
        }

        if (responseData.data[0].result.ranks == null || responseData.data[0].result.ranks.size == 0) {
            rankCountTextView.text = "--"
            rankLanguageTextView.text = getString(R.string.myprofile_rank_label)
            rankContainer.setOnClickListener(null)
        } else if (responseData.data[0].result.ranks.size < 2) {
            rankContainer.setOnClickListener(this)
            rankContainer.tag = crown
            rankCountTextView.text = "" + responseData.data[0].result.ranks[0].rank
            if (AppConstants.LANG_KEY_ENGLISH == responseData.data[0].result.ranks[0].langKey) {
                rankLanguageTextView.text = getString(R.string.blogger_profile_rank_in) + " ENGLISH"
            } else {
                rankLanguageTextView.text = (getString(R.string.blogger_profile_rank_in)
                        + " " + responseData.data[0].result.ranks[0].langValue.toUpperCase())
            }
        } else {
            rankContainer.setOnClickListener(this)
            rankContainer.tag = crown
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
            MyCityAnimationsUtil.animate(this@UserProfileActivity, rankContainer, multipleRankList, 0, true)
        }
    }

    private fun getUsersCreatedContent() {
        val retro = BaseApplication.getInstance().retrofit
        val bloggerDashboardAPI = retro.create(BloggerDashboardAPI::class.java)
        val call = bloggerDashboardAPI.getUsersAllContent(authorId, start, size, null)
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
                    if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {
                        if (responseData.data == null && userContentList?.size == 1) {
                            userContentAdapter.setListData(userContentList)
                            userContentAdapter.notifyDataSetChanged()
                        } else {
                            processUserContentResponse(responseData.data.result)
                        }
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
            } else {
                userContentAdapter.setListData(userContentList)
                userContentAdapter.notifyDataSetChanged()
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
            if (!userBookmarkList.isNullOrEmpty()) {
            } else {
                usersBookmarksAdapter.setListData(userBookmarkList)
                usersBookmarksAdapter.notifyDataSetChanged()
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
                    try {
                        if (null == response.body()) {
                            val nee = NetworkErrorException(response.raw().toString())
                            Crashlytics.logException(nee)
                            return
                        }
                        isRequestRunning = false
                        bottomLoadingView.visibility = View.GONE
                        val responseData = response.body() as FeaturedOnModel
                        if (responseData.code == 200 && Constants.SUCCESS == responseData.status) {
                            processFeaturedContentResponse(responseData.data.result.item_list)
                        } else {
                        }
                    } catch (e: Exception) {
                        Crashlytics.logException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }
                }

                override fun onFailure(call: Call<FeaturedOnModel>, t: Throwable) {
                    bottomLoadingView.visibility = View.GONE
                    Crashlytics.logException(t)
                    Log.d("MC4kException", Log.getStackTraceString(t))
                }
            })
        }
    }

    private fun processFeaturedContentResponse(featuredItemList: List<MixFeedResult>) {
        if (featuredItemList.isNullOrEmpty()) {
            isLastPageReached = true
            if (!userFeaturedOnList.isNullOrEmpty()) {
            } else {
                usersFeaturedContentAdapter.setListData(userFeaturedOnList)
                usersFeaturedContentAdapter.notifyDataSetChanged()
            }
        } else {
            start += size
            userFeaturedOnList?.addAll(featuredItemList)
            usersFeaturedContentAdapter.setListData(userFeaturedOnList)
            usersFeaturedContentAdapter.notifyDataSetChanged()
        }
    }

    private fun showBadgeDialog(badgeId: String?) {
        val badgesDialogFragment = BadgesDialogFragment()
        val bundle = Bundle()
        bundle.putString(Constants.USER_ID, authorId)
        bundle.putString("id", badgeId)
        badgesDialogFragment.arguments = bundle
        val fm = supportFragmentManager
        fm?.let {
            badgesDialogFragment.show(fm!!, "BadgeDetailDialog")
        }
    }

    private fun showCrownDialog(crown: Any?) {
        if (crown is Crown) {
            val crownDialogFragment = CrownDialogFragment()
            val bundle = Bundle()
            bundle.putString(Constants.USER_ID, authorId)
            bundle.putParcelable("crown", crown)
            crownDialogFragment.arguments = bundle
            val fm = supportFragmentManager
            fm?.let {
                crownDialogFragment.show(fm!!, "CrownDetailDialog")
            }
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
                isLastPageReached = false
                recyclerView.adapter = userContentAdapter
                if (AppUtils.isPrivateProfile(authorId)) {
                    userContentList?.add(MixFeedResult(contentType = AppConstants.CONTENT_TYPE_CREATE_SECTION))
                }
                getUsersCreatedContent()
            }
            view?.id == R.id.featuredTab -> {
                creatorTab.isSelected = false
                featuredTab.isSelected = true
                bookmarksTab.isSelected = false
                recyclerView.adapter = usersFeaturedContentAdapter
                userFeaturedOnList?.clear()
                start = 0
                isLastPageReached = false
                getFeaturedContent()
                if (AppUtils.isPrivateProfile(authorId)) {
                    Utils.pushProfileEvents(this, "CTA_Private_Featured_Collections", "UserProfileActivity",
                            "Featured Collections", "-")
                } else {
                    Utils.pushProfileEvents(this, "CTA_Public_Featured_Collections", "UserProfileActivity",
                            "Featured Collections", "-")
                }
            }
            view?.id == R.id.bookmarksTab -> {
                creatorTab.isSelected = false
                featuredTab.isSelected = false
                bookmarksTab.isSelected = true
                userBookmarkList?.clear()
                start = 0
                isLastPageReached = false
                recyclerView.adapter = usersBookmarksAdapter
                getUsersBookmark()
                Utils.pushProfileEvents(this, "CTA_Bookmarks", "UserProfileActivity",
                        "Bookmarks", "-")
            }
            view?.id == R.id.badgeContainer -> {
                val intent = Intent(this, BadgeActivity::class.java)
                intent.putExtra(Constants.USER_ID, authorId)
                startActivity(intent)
            }
            view?.id == R.id.followAuthorTextView -> {
                if (!isFollowUnFollowRequestRunning) {
                    isFollowUnFollowRequestRunning = true
                    hitFollowUnfollowAPI()
                }
            }
            view?.id == R.id.sharePrivateTextView || view?.id == R.id.sharePublicTextView -> {
                shareCardType = "profile"
                if (createSharableImageWhileCheckingPermissions()) {
                    return
                }
                shareGenericImage()
            }
            view?.id == R.id.analyticsTextView -> {
                if (AppConstants.DEBUGGING_USER_ID.contains("" + authorId)) {
                    rankContainer.setOnLongClickListener {
                        val intent = Intent(this, IdTokenLoginActivity::class.java)
                        startActivity(intent)
                        false
                    }
                    val intent = Intent(this, RankingActivity::class.java)
                    startActivity(intent)
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
                if (AppConstants.DEBUGGING_USER_ID.contains("" + authorId)) {
                    val intent = Intent(this, IdTokenLoginActivity::class.java)
                    startActivity(intent)
                }
                showCrownDialog(view.tag)
            }
            view?.id == R.id.postsCountContainer -> {
                appBarLayout.setExpanded(false)
            }
            view?.id == R.id.appSettingsImageView -> {
                val intent = Intent(this, ProfileSetting::class.java)
                intent.putExtra("isRewardAdded", isRewardAdded)
                startActivity(intent)
                Utils.pushProfileEvents(this, "CTA_Settings", "UserProfileActivity",
                        "Settings", "-")
            }
        }
    }

    private fun shareGenericImage() {
        try {
            val uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/profile.jpg")
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/jpeg"
            }
            startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.ad_bottom_bar_generic_share)))
            if (AppUtils.isPrivateProfile(authorId)) {
                Utils.pushProfileEvents(this, "CTA_Share_Private_Profile", "UserProfileActivity",
                        "Share", "-")
            } else {
                Utils.pushProfileEvents(this, "CTA_Share_Public_Profile", "UserProfileActivity",
                        "Share", "-")
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    override fun onClick(view: View, position: Int) {
        when {
            view.id == R.id.articleItemView || view.id == R.id.videoItemView || view.id == R.id.rootView -> {
                launchContentDetail(userContentList?.get(position))
            }
            view.id == R.id.draftContainer -> {
                val intent = Intent(this, UserDraftsContentActivity::class.java)
                startActivity(intent)
                Utils.pushProfileEvents(this, "CTA_Drafts_Folder", "UserProfileActivity",
                        "Drafts folder", "-")
            }
            view.id == R.id.articleContainer -> {
                val intent = Intent(this, UserPublishedContentActivity::class.java)
                intent.putExtra("isPrivateProfile", true)
                intent.putExtra(Constants.AUTHOR_ID, authorId)
                intent.putExtra("contentType", AppConstants.CONTENT_TYPE_ARTICLE)
                startActivity(intent)
                Utils.pushProfileEvents(this, "CTA_Blogs_Folder", "UserProfileActivity",
                        "Blogs folder", "-")
            }
            view.id == R.id.storyContainer -> {
                val intent = Intent(this, UserPublishedContentActivity::class.java)
                intent.putExtra("isPrivateProfile", true)
                intent.putExtra(Constants.AUTHOR_ID, authorId)
                intent.putExtra("contentType", AppConstants.CONTENT_TYPE_SHORT_STORY)
                startActivity(intent)
                Utils.pushProfileEvents(this, "CTA_100WS_Folder", "UserProfileActivity",
                        "100WS folder", "-")
            }
            view.id == R.id.vlogContainer -> {
                val intent = Intent(this, UserPublishedContentActivity::class.java)
                intent.putExtra("isPrivateProfile", true)
                intent.putExtra(Constants.AUTHOR_ID, authorId)
                intent.putExtra("contentType", AppConstants.CONTENT_TYPE_VIDEO)
                startActivity(intent)
                Utils.pushProfileEvents(this, "CTA_Vlogs_Folder", "UserProfileActivity",
                        "Vlogs folder", "-")
            }
            view.id == R.id.shareArticleImageView -> {
                shareContent(userContentList?.get(position))
            }
            view.id == R.id.facebookShareImageView -> {
                AppUtils.shareStoryWithFB(this, userContentList?.get(position)?.userType, userContentList?.get(position)?.blogTitleSlug,
                        userContentList?.get(position)?.titleSlug, "Profile",
                        SharedPrefUtils.getUserDetailModel(this).dynamoId + "",
                        userContentList?.get(position)?.id, authorId, userContentList?.get(position)?.userName)
            }
            view.id == R.id.whatsappShareImageView -> {
                shareContentPosition = position
                shareCardType = "story"
                shareMedium = AppConstants.MEDIUM_WHATSAPP
                if (createSharableImageWhileCheckingPermissions()) return
                AppUtils.shareStoryWithWhatsApp(this, userContentList?.get(position)?.userType,
                        userContentList?.get(position)?.blogTitleSlug, userContentList?.get(position)?.titleSlug,
                        "Profile", SharedPrefUtils.getUserDetailModel(this).dynamoId,
                        userContentList?.get(position)?.id, authorId, userContentList?.get(position)?.userName)
            }
            view.id == R.id.instagramShareImageView -> {
                shareContentPosition = position
                shareCardType = "story"
                shareMedium = AppConstants.MEDIUM_INSTAGRAM
                if (createSharableImageWhileCheckingPermissions()) return
                AppUtils.shareStoryWithInstagram(this, "Profile", SharedPrefUtils.getUserDetailModel(this).dynamoId,
                        userContentList?.get(position)?.id, authorId, userContentList?.get(position)?.userName)
            }
            view.id == R.id.genericShareImageView -> {
                try {
                    val addCollectionAndCollectionitemDialogFragment = AddCollectionAndCollectionItemDialogFragment()
                    val bundle = Bundle()
                    bundle.putString("articleId", userContentList?.get(position)?.id)
                    bundle.putString("type", AppConstants.ARTICLE_COLLECTION_TYPE)
                    addCollectionAndCollectionitemDialogFragment.arguments = bundle
                    val fm = supportFragmentManager
                    addCollectionAndCollectionitemDialogFragment.show(fm!!, "collectionAdd")
                    Utils.pushProfileEvents(this, "CTA_100WS_Add_To_Collection",
                            "UserProfileActivity", "Add to Collection", "-")
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
            view.id == R.id.bookmarkArticleImageView -> {
                bookmarkItem(position)
            }
        }
    }

    private fun bookmarkItem(position: Int) {
        val retro = BaseApplication.getInstance().retrofit
        val articleDetailsAPI = retro.create(ArticleDetailsAPI::class.java)
        val articleDetailRequest = ArticleDetailRequest()
        articleDetailRequest.articleId = userContentList?.get(position)?.id

        if ("1" == userContentList?.get(position)?.isMomspresso) {
            val call = articleDetailsAPI.addVideoWatchLater(articleDetailRequest)
            call.enqueue(object : Callback<AddBookmarkResponse> {
                override fun onResponse(call: Call<AddBookmarkResponse>, response: retrofit2.Response<AddBookmarkResponse>) {
                    if (null == response.body()) {
                        showToast(getString(R.string.server_went_wrong))
                        return
                    }
                    val responseData = response.body()
                    if (responseData?.code == 200) {
                        userContentList?.get(position)?.isbookmark = 1
                        userContentAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<AddBookmarkResponse>, e: Throwable) {
                    showToast(getString(R.string.server_went_wrong))
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }

            })
            Utils.pushWatchLaterArticleEvent(this, "Profile",
                    SharedPrefUtils.getUserDetailModel(this).dynamoId + "",
                    userContentList?.get(position)?.id, authorId + "~" + userContentList?.get(position)?.userName)
        } else {
            val call = articleDetailsAPI.addBookmark(articleDetailRequest)
            call.enqueue(object : Callback<AddBookmarkResponse> {
                override fun onResponse(call: Call<AddBookmarkResponse>, response: retrofit2.Response<AddBookmarkResponse>) {
                    if (null == response.body()) {
                        showToast(getString(R.string.server_went_wrong))
                        return
                    }
                    val responseData = response.body()
                    if (responseData?.code == 200) {
                        userContentList?.get(position)?.isbookmark = 1
                        userContentAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<AddBookmarkResponse>, e: Throwable) {
                    showToast(getString(R.string.server_went_wrong))
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }

            })
            Utils.pushBookmarkArticleEvent(this, "Profile",
                    SharedPrefUtils.getUserDetailModel(this).dynamoId + "",
                    userContentList?.get(position)?.id, authorId + "~" + userContentList?.get(position)?.userName)
        }
    }

    override fun onFeaturedItemClick(view: View, position: Int) {
        when {
            view.id == R.id.featuredItemRootView -> {
                launchContentDetail(userFeaturedOnList?.get(position))
            }
            view.id == R.id.collectionItem1TextView -> {
                val intent = Intent(this, UserCollectionItemListActivity::class.java)
                intent.putExtra("id", userFeaturedOnList?.get(position)?.collectionList?.get(0)?.userCollectionId)
                startActivity(intent)
            }
            view.id == R.id.collectionItem2TextView -> {
                val intent = Intent(this, UserCollectionItemListActivity::class.java)
                intent.putExtra("id", userFeaturedOnList?.get(position)?.collectionList?.get(0)?.userCollectionId)
                startActivity(intent)
            }
            view.id == R.id.collectionItem3TextView -> {
                val intent = Intent(this, UserCollectionItemListActivity::class.java)
                intent.putExtra("id", userFeaturedOnList?.get(position)?.collectionList?.get(0)?.userCollectionId)
                startActivity(intent)
            }
            view.id == R.id.collectionItem4TextView -> {
                val intent = Intent(this, UserCollectionItemListActivity::class.java)
                intent.putExtra("id", userFeaturedOnList?.get(position)?.collectionList?.get(0)?.userCollectionId)
                startActivity(intent)
            }
            view.id == R.id.moreItemsTextView -> {
                val featureIntent = Intent(this, FeaturedOnActivity::class.java)
                featureIntent.putExtra(AppConstants.CONTENT_ID, userFeaturedOnList?.get(position)?.id)
                startActivity(featureIntent)
            }
        }
    }

    private fun launchContentDetail(item: MixFeedResult?) {
        when {
            item?.itemType == AppConstants.CONTENT_TYPE_ARTICLE -> {
                val intent = Intent(this, ArticleDetailsContainerActivity::class.java)
                intent.putExtra(Constants.ARTICLE_ID, item.id)
                intent.putExtra(Constants.AUTHOR_ID, item.userId)
                intent.putExtra(Constants.BLOG_SLUG, item.blogTitleSlug)
                intent.putExtra(Constants.TITLE_SLUG, item.titleSlug)
                intent.putExtra(Constants.FROM_SCREEN, "Profile")
                intent.putExtra(Constants.AUTHOR, item.userId + "~" + item.userName)
                startActivity(intent)
            }
            item?.itemType == AppConstants.CONTENT_TYPE_SHORT_STORY -> {
                val intent = Intent(this, ShortStoryContainerActivity::class.java)
                intent.putExtra(Constants.ARTICLE_ID, item.id)
                intent.putExtra(Constants.AUTHOR_ID, item.userId)
                intent.putExtra(Constants.BLOG_SLUG, item.blogTitleSlug)
                intent.putExtra(Constants.TITLE_SLUG, item.titleSlug)
                intent.putExtra(Constants.FROM_SCREEN, "Profile")
                intent.putExtra(Constants.AUTHOR, item.userId + "~" + item.userName)
                startActivity(intent)
            }
            item?.itemType == AppConstants.CONTENT_TYPE_VIDEO -> {
                val intent = Intent(this, ParallelFeedActivity::class.java)
                intent.putExtra(Constants.VIDEO_ID, item.id)
                intent.putExtra(Constants.FROM_SCREEN, "Profile")
                startActivity(intent)
            }
        }
    }

    override fun onBookmarkItemInteraction(view: View, position: Int) {
        when {
            view.id == R.id.rootView -> {
                launchContentDetail(userBookmarkList?.get(position))
            }
            view.id == R.id.removeBookmarkTextView -> {

            }
            view.id == R.id.shareImageView -> {
                shareContent(userBookmarkList?.get(position))
            }
        }
    }

    private fun shareContent(data: MixFeedResult?) {
        when {
            data?.itemType == AppConstants.CONTENT_TYPE_ARTICLE -> {
                val shareIntent = AppUtils.getArticleShareIntent(data.userType, data.blogTitleSlug, data.titleSlug,
                        getString(R.string.check_out_blog), data.title, data.userName)
                startActivity(Intent.createChooser(shareIntent, "Momspresso"))
                Utils.pushShareArticleEvent(this, "Profile",
                        SharedPrefUtils.getUserDetailModel(this).dynamoId + "", data.id,
                        data.userId + "~" + data.userName, "-")
            }
            data?.itemType == AppConstants.CONTENT_TYPE_SHORT_STORY -> {
            }
            data?.itemType == AppConstants.CONTENT_TYPE_VIDEO -> {

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

    private fun createSharableImageWhileCheckingPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions()
                return true
            } else {
                try {
                    if (shareCardType == "profile") {
                        AppUtils.getBitmapFromView(profileShareCardWidget, sharableProfileImageName)
                    } else {
                        shareContentPosition?.let {
                            AppUtils.drawMultilineTextToBitmap(R.color.short_story_card_bg_1,
                                    userContentList?.get(it)?.title,
                                    userContentList?.get(it)?.body, userContentList?.get(it)?.userName)
                        }
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                    return true
                }
            }
        } else {
            try {
                if (shareCardType == "profile") {
                    AppUtils.getBitmapFromView(profileShareCardWidget, sharableProfileImageName)
                } else {
                    shareContentPosition?.let {
                        AppUtils.drawMultilineTextToBitmap(R.color.short_story_card_bg_1,
                                userContentList?.get(it)?.title,
                                userContentList?.get(it)?.body, userContentList?.get(it)?.userName)
                    }
                }
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
                return true
            }
        }
        return false
    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(rootLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok) { requestUngrantedPermissions() }.show()
        } else {
            requestUngrantedPermissions()
        }
    }

    private fun requestUngrantedPermissions() {
        val permissionList = java.util.ArrayList<String>()
        for (s in PERMISSIONS_INIT) {
            if (ActivityCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(s)
            }
        }
        val requiredPermission = permissionList.toTypedArray()
        ActivityCompat.requestPermissions(this, requiredPermission, REQUEST_GALLERY_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_GALLERY_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show()
                try {
                    if (shareCardType == "profile") {
                        AppUtils.getBitmapFromView(profileShareCardWidget, sharableProfileImageName)
                        shareGenericImage()
                    } else {
                        if (AppConstants.MEDIUM_WHATSAPP == shareMedium) {
                            shareContentPosition?.let {
                                AppUtils.shareStoryWithWhatsApp(this, userContentList?.get(it)?.userType,
                                        userContentList?.get(it)?.blogTitleSlug, userContentList?.get(it)?.titleSlug, "Profile",
                                        SharedPrefUtils.getUserDetailModel(this).dynamoId, userContentList?.get(it)?.id,
                                        authorId, userContentList?.get(it)?.userName)
                            }
                        } else if (AppConstants.MEDIUM_INSTAGRAM == shareMedium) {
                            shareContentPosition?.let {
                                AppUtils.shareStoryWithInstagram(this, "Profile",
                                        SharedPrefUtils.getUserDetailModel(this).dynamoId, userContentList?.get(it)?.id,
                                        authorId, userContentList?.get(it)?.userName)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            } else {
                Snackbar.make(rootLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onCollectionAddSuccess() {
        Handler().postDelayed(Runnable {
            myCollectionsWidget.refresh(authorId, AppUtils.isPrivateProfile(authorId))
        }, 1000)
    }

    override fun onSeeMoreClick(userBio: String) {
        val userBioDialogFragment = UserBioDialogFragment()
        val fm = supportFragmentManager
        val _args = Bundle()
        _args.putString("userBio", userBio)
        userBioDialogFragment.arguments = _args
        userBioDialogFragment.isCancelable = true
        userBioDialogFragment.show(fm, "Choose video option")
    }

    override fun updateUi(response: Response?) {

    }
}
