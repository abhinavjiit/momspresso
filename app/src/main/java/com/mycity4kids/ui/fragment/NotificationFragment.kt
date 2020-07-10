package com.mycity4kids.ui.fragment

import android.accounts.NetworkErrorException
import android.content.Intent
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
import com.mycity4kids.BuildConfig
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.editor.NewEditor
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.request.FollowUnfollowUserRequest
import com.mycity4kids.models.request.NotificationReadRequest
import com.mycity4kids.models.response.GroupsMembershipResponse
import com.mycity4kids.models.response.NotificationCenterListResponse
import com.mycity4kids.models.response.NotificationCenterResult
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI
import com.mycity4kids.retrofitAPIsInterfaces.NotificationsAPI
import com.mycity4kids.ui.ContentCommentReplyNotificationActivity
import com.mycity4kids.ui.GroupMembershipStatus
import com.mycity4kids.ui.GroupMembershipStatus.IMembershipStatus
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity
import com.mycity4kids.ui.activity.ArticleListingActivity
import com.mycity4kids.ui.activity.BadgeActivity
import com.mycity4kids.ui.activity.CategoryVideosListingActivity
import com.mycity4kids.ui.activity.DashboardActivity
import com.mycity4kids.ui.activity.GroupDetailsActivity
import com.mycity4kids.ui.activity.GroupPostDetailActivity
import com.mycity4kids.ui.activity.GroupsSummaryActivity
import com.mycity4kids.ui.activity.LoadWebViewActivity
import com.mycity4kids.ui.activity.ParallelFeedActivity
import com.mycity4kids.ui.activity.ShortStoriesListingContainerActivity
import com.mycity4kids.ui.activity.ShortStoryChallengeDetailActivity
import com.mycity4kids.ui.activity.ShortStoryContainerActivity
import com.mycity4kids.ui.activity.ShortStoryModerationOrShareActivity
import com.mycity4kids.ui.activity.SuggestedTopicsActivity
import com.mycity4kids.ui.activity.TopicsListingActivity
import com.mycity4kids.ui.activity.ViewGroupPostCommentsRepliesActivity
import com.mycity4kids.ui.activity.collection.UserCollectionItemListActivity
import com.mycity4kids.ui.adapter.NotificationCenterRecyclerAdapter
import com.mycity4kids.ui.campaign.activity.CampaignContainerActivity
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity
import com.mycity4kids.utils.StringUtils
import com.mycity4kids.utils.ToastUtils
import java.util.ArrayList
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by hemant.parmar on 30-12-2017.
 */
class NotificationFragment : BaseFragment(), IMembershipStatus,
    NotificationCenterRecyclerAdapter.RecyclerViewClickListener {
    private var notificationCenterRecyclerAdapter: NotificationCenterRecyclerAdapter? = null
    private var notificationCenterResultArrayList: ArrayList<NotificationCenterResult>? =
        null
    private var paginationValue = ""
    private var isLastPageReached = false
    private var isReuqestRunning = false
    private lateinit var progressBar: ProgressBar
    private lateinit var noBlogsTextView: TextView
    private lateinit var notificationRecyclerView: RecyclerView
    private var pastVisiblesItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var recentTimestamp: Long = 0L
    private var isEarlierAdded: Boolean = false
    private var isRecentAdded: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.aa_notification, container, false)
        progressBar =
            view.findViewById(R.id.progressBar)
        noBlogsTextView =
            view.findViewById(R.id.noBlogsTextView)
        notificationCenterResultArrayList = ArrayList()
        notificationRecyclerView = view.findViewById(R.id.notificationRecyclerView)
        val llm =
            LinearLayoutManager(activity)
        llm.orientation = RecyclerView.VERTICAL
        notificationRecyclerView.layoutManager = llm
        notificationCenterRecyclerAdapter = NotificationCenterRecyclerAdapter(
            this,
            notificationCenterResultArrayList!!
        )

        recentTimestamp =
            SharedPrefUtils.getNotificationCenterVisitTimestamp(BaseApplication.getAppContext())
        notificationRecyclerView.adapter = notificationCenterRecyclerAdapter
        notificationFromAPI
        notificationRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                if (dy > 0) {
                    visibleItemCount = llm.childCount
                    totalItemCount = llm.itemCount
                    pastVisiblesItems = llm.findFirstVisibleItemPosition()
                    if (!isReuqestRunning && !isLastPageReached) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            isReuqestRunning = true
                            notificationFromAPI
                        }
                    }
                }
            }
        })
        return view
    }

    private val notificationFromAPI: Unit
        private get() {
            val retrofit = BaseApplication.getInstance().retrofit
            val notificationsAPI = retrofit.create(
                NotificationsAPI::class.java
            )
            val call =
                notificationsAPI.getNotificationCenterList(
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    10,
                    paginationValue
                )
            progressBar.visibility = View.VISIBLE
            call.enqueue(notificationCenterResponseCallback)
        }

    private val notificationCenterResponseCallback: Callback<NotificationCenterListResponse> =
        object : Callback<NotificationCenterListResponse> {
            override fun onResponse(
                call: Call<NotificationCenterListResponse>,
                response: Response<NotificationCenterListResponse?>
            ) {
                progressBar.visibility = View.GONE
                isReuqestRunning = false
                if (null == response.body()) {
                    val nee =
                        NetworkErrorException(response.raw().toString())
                    FirebaseCrashlytics.getInstance().recordException(nee)
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        SharedPrefUtils.setNotificationCenterVisitTimestamp(
                            BaseApplication.getAppContext(),
                            System.currentTimeMillis() / 1000
                        )
                        processResponse(responseData)
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(
                call: Call<NotificationCenterListResponse>,
                t: Throwable
            ) {
                progressBar.visibility = View.GONE
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }

    private fun processResponse(responseData: NotificationCenterListResponse?) {
        try {
            if (responseData!!.data == null) {
                isLastPageReached = true
                noBlogsTextView.visibility = View.VISIBLE
                return
            }
            val dataList =
                responseData.data.result
            if (dataList.size == 0) {
                isLastPageReached = true
                if (null == notificationCenterResultArrayList || notificationCenterResultArrayList!!.isEmpty()) {
                    notificationCenterResultArrayList!!.clear()
                    notificationCenterResultArrayList!!.addAll(dataList)
                    notificationCenterRecyclerAdapter!!.notifyDataSetChanged()
                    noBlogsTextView.visibility = View.VISIBLE
                    noBlogsTextView.text = getString(R.string.notification_no_notification)
                }
            } else {
                noBlogsTextView.visibility = View.GONE
                if ("" == paginationValue) {
                    notificationCenterResultArrayList!!.clear()
                    if (dataList[0].createdTime.toLong() > recentTimestamp) {
                        notificationCenterResultArrayList!!.add(
                            NotificationCenterResult(
                                AppConstants.NOTIFICATION_CENTER_RECENT_HEADER
                            )
                        )
                        isRecentAdded = true
                    }
                    for (i in 0 until dataList.size) {
                        if (isRecentAdded && !isEarlierAdded && dataList[i].createdTime.toLong() <= recentTimestamp) {
                            notificationCenterResultArrayList!!.add(
                                NotificationCenterResult(
                                    AppConstants.NOTIFICATION_CENTER_EARLIER_HEADER
                                )
                            )
                            isEarlierAdded = true
                        }
                        notificationCenterResultArrayList!!.add(dataList[i])
                    }
                    if (notificationCenterResultArrayList != null && notificationCenterResultArrayList!!.isNotEmpty()) {
                        SharedPrefUtils.setLastNotificationIdForUnreadFlag(
                            BaseApplication.getAppContext(),
                            notificationCenterResultArrayList!![1].id
                        )
                    }
                } else {
                    for (i in 0 until dataList.size) {
                        if (isRecentAdded && !isEarlierAdded && dataList[i].createdTime.toLong() <= recentTimestamp) {
                            notificationCenterResultArrayList!!.add(NotificationCenterResult("earlier"))
                            isEarlierAdded = true
                        }
                        notificationCenterResultArrayList!!.add(dataList[i])
                    }
                }
                if (null == responseData.data.pagination) {
                    isLastPageReached = true
                    paginationValue = ""
                } else {
                    paginationValue =
                        responseData.data.pagination.id + "_" + responseData.data
                            .pagination.createdTime
                }
                notificationCenterRecyclerAdapter!!.notifyDataSetChanged()
            }
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
            Log.d("MC4kException", Log.getStackTraceString(ex))
        }
    }

    override fun onNotificationItemClick(
        view: View,
        position: Int
    ) {
        if (view.id == R.id.rootView) {
            notificationCenterResultArrayList!![position].isRead =
                AppConstants.NOTIFICATION_STATUS_READ
            hitNotificationReadApi(notificationCenterResultArrayList!![position].id)
            notificationCenterRecyclerAdapter!!.notifyDataSetChanged()
            val notifType =
                notificationCenterResultArrayList!![position].notifType
            if (StringUtils.isNullOrEmpty(notifType)) {
                return
            }
            when (notifType) {
                AppConstants.NOTIFICATION_CENTER_WEB_VIEW -> {
                    val intent1 =
                        Intent(activity, LoadWebViewActivity::class.java)
                    intent1.putExtra(
                        Constants.WEB_VIEW_URL,
                        notificationCenterResultArrayList!![position].url
                    )
                    startActivity(intent1)
                    pushEvent("NOTIFICATION_CENTER_WEB_VIEW")
                }
                AppConstants.NOTIFICATION_CENTER_ARTICLE_DETAIL -> {
                    val authorId: String
                    authorId = if (StringUtils.isNullOrEmpty(
                            notificationCenterResultArrayList!![position].authorId
                        ) || ("0"
                            == notificationCenterResultArrayList!![position].authorId)) {
                        notificationCenterResultArrayList!![position].userId
                    } else {
                        notificationCenterResultArrayList!![position].authorId
                    }
                    val intent = Intent(
                        activity,
                        ArticleDetailsContainerActivity::class.java
                    )
                    intent.putExtra(
                        Constants.ARTICLE_ID,
                        notificationCenterResultArrayList!![position].articleId
                    )
                    intent.putExtra(Constants.AUTHOR_ID, authorId)
                    intent.putExtra(
                        Constants.BLOG_SLUG,
                        notificationCenterResultArrayList!![position].blogTitleSlug
                    )
                    intent.putExtra(
                        Constants.TITLE_SLUG,
                        notificationCenterResultArrayList!![position].titleSlug
                    )
                    intent.putExtra(
                        Constants.ARTICLE_OPENED_FROM,
                        "NotificationsScreen"
                    )
                    intent.putExtra(
                        Constants.FROM_SCREEN,
                        "NotificationsScreen"
                    )
                    intent.putExtra(
                        Constants.ARTICLE_INDEX,
                        "" + position
                    )
                    intent.putExtra(Constants.AUTHOR, "$authorId~")
                    startActivity(intent)
                    pushEvent("NOTIFICATION_CENTER_ARTICLE_DETAIL")
                }
                AppConstants.NOTIFICATION_CENTER_PROFILE -> {
                    val authorId: String =
                        if (notificationCenterResultArrayList!![position].notifiedBy.isNullOrEmpty()) {
                            notificationCenterResultArrayList!![position].userId
                        } else {
                            notificationCenterResultArrayList!![position].notifiedBy[0].userId
                        }

                    val profileIntent =
                        Intent(activity, UserProfileActivity::class.java)
                    profileIntent.putExtra(Constants.USER_ID, authorId)
                    profileIntent
                        .putExtra(
                            AppConstants.BADGE_ID,
                            notificationCenterResultArrayList!![position].badgeId
                        )
                    profileIntent.putExtra(
                        AppConstants.MILESTONE_ID,
                        notificationCenterResultArrayList!![position].milestoneId
                    )
                    startActivity(profileIntent)
                    pushEvent("NOTIFICATION_CENTER_PROFILE")
                }
                AppConstants.NOTIFICATION_CENTER_VIDEO_DETAIL -> {
                    val intent =
                        Intent(activity, ParallelFeedActivity::class.java)
                    if (StringUtils.isNullOrEmpty(
                            notificationCenterResultArrayList!![position].articleId
                        ) ||
                        notificationCenterResultArrayList!!.get(position).articleId == "0") {
                        intent.putExtra(
                            Constants.VIDEO_ID,
                            notificationCenterResultArrayList!![position].videoId
                        )
                    } else {
                        intent.putExtra(
                            Constants.VIDEO_ID,
                            notificationCenterResultArrayList!![position].articleId
                        )
                    }
                    intent.putExtra(
                        Constants.AUTHOR_ID,
                        notificationCenterResultArrayList!![position].authorId
                    )
                    intent.putExtra(Constants.FROM_SCREEN, "Home Screen")
                    intent.putExtra(
                        Constants.ARTICLE_OPENED_FROM,
                        "Funny Videos"
                    )
                    startActivity(intent)
                    pushEvent("NOTIFICATION_CENTER_VIDEO_DETAIL")
                }
                AppConstants.NOTIFICATION_CENTER_LAUNCH_EDITOR -> {
                    launchEditor()
                    pushEvent("NOTIFICATION_CENTER_LAUNCH_EDITOR")
                }
                AppConstants.NOTIFICATION_CENTER_SUGGESTED_TOPICS -> {
                    val intent =
                        Intent(activity, SuggestedTopicsActivity::class.java)
                    startActivity(intent)
                    pushEvent("NOTIFICATION_CENTER_SUGGESTED_TOPICS")
                }
                AppConstants.NOTIFICATION_CENTER_TODAYS_BEST -> {
                    val intent =
                        Intent(activity, ArticleListingActivity::class.java)
                    intent.putExtra(
                        Constants.SORT_TYPE,
                        Constants.KEY_TODAYS_BEST
                    )
                    startActivity(intent)
                    pushEvent("NOTIFICATION_CENTER_TODAYS_BEST")
                }
                AppConstants.NOTIFICATION_CENTER_SHORT_STORY_LIST -> {
                    val resultIntent = Intent(
                        activity,
                        ShortStoriesListingContainerActivity::class.java
                    )
                    resultIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    resultIntent.putExtra("fromNotification", true)
                    resultIntent.putExtra("parentTopicId", AppConstants.SHORT_STORY_CATEGORYID)
                    resultIntent.putExtra(
                        "selectedTabCategoryId",
                        "" + notificationCenterResultArrayList!![position].categoryId
                    )
                    startActivity(resultIntent)
                    pushEvent("NOTIFICATION_CENTER_SHORT_STORY_LIST")
                }
                AppConstants.NOTIFICATION_CENTER_SHORT_STORY_DETAILS -> {
                    val authorId: String
                    authorId = if (StringUtils.isNullOrEmpty(
                            notificationCenterResultArrayList!![position].authorId
                        ) ||
                        "0" == notificationCenterResultArrayList!![position].authorId) {
                        notificationCenterResultArrayList!![position].userId
                    } else {
                        notificationCenterResultArrayList!![position].authorId
                    }
                    val intent = Intent(
                        activity,
                        ShortStoryContainerActivity::class.java
                    )
                    intent.putExtra(
                        Constants.ARTICLE_ID,
                        notificationCenterResultArrayList!![position].articleId
                    )
                    intent.putExtra(Constants.AUTHOR_ID, authorId)
                    intent.putExtra(
                        Constants.BLOG_SLUG,
                        notificationCenterResultArrayList!![position].blogTitleSlug
                    )
                    intent.putExtra(
                        Constants.TITLE_SLUG,
                        notificationCenterResultArrayList!![position].titleSlug
                    )
                    intent.putExtra(
                        Constants.ARTICLE_OPENED_FROM,
                        "NotificationsScreen"
                    )
                    intent.putExtra(
                        Constants.FROM_SCREEN,
                        "NotificationsScreen"
                    )
                    intent.putExtra(
                        Constants.ARTICLE_INDEX,
                        "" + position
                    )
                    intent.putExtra(Constants.AUTHOR, "$authorId~")
                    startActivity(intent)
                    pushEvent("NOTIFICATION_CENTER_SHORT_STORY_DETAILS")
                }
                AppConstants.NOTIFICATION_CENTER_GROUP_MEMBERSHIP -> {
                    val groupMembershipStatus = GroupMembershipStatus(this)
                    groupMembershipStatus
                        .checkMembershipStatus(
                            notificationCenterResultArrayList!![position].groupId,
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
                        )
                    pushEvent("NOTIFICATION_CENTER_GROUP_MEMBERSHIP")
                }
                AppConstants.NOTIFICATION_CENTER_GROUP_NEW_POST -> {
                    val groupMembershipStatus = GroupMembershipStatus(this)
                    groupMembershipStatus
                        .checkMembershipStatus(
                            notificationCenterResultArrayList!![position].groupId,
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
                        )
                    pushEvent("NOTIFICATION_CENTER_GROUP_NEW_POST")
                }
                AppConstants.NOTIFICATION_CENTER_GROUP_NEW_RESPONSE -> {
                    val intent =
                        Intent(activity, GroupPostDetailActivity::class.java)
                    intent.putExtra(
                        "postId",
                        notificationCenterResultArrayList!![position].postId
                    )
                    intent.putExtra(
                        "groupId",
                        notificationCenterResultArrayList!![position].groupId
                    )
                    startActivity(intent)
                    pushEvent("NOTIFICATION_CENTER_GROUP_NEW_RESPONSE")
                }
                AppConstants.NOTIFICATION_CENTER_GROUP_NEW_REPLY -> {
                    val intent = Intent(
                        activity,
                        ViewGroupPostCommentsRepliesActivity::class.java
                    )
                    intent.putExtra(
                        "postId",
                        notificationCenterResultArrayList!![position].postId
                    )
                    intent.putExtra(
                        "groupId",
                        notificationCenterResultArrayList!![position].groupId
                    )
                    intent.putExtra(
                        "responseId",
                        notificationCenterResultArrayList!![position].responseId
                    )
                    intent.putExtra("action", "commentReply")
                    startActivity(intent)
                    pushEvent("NOTIFICATION_CENTER_GROUP_NEW_REPLY")
                }
                AppConstants.NOTIFICATION_CENTER_GROUP_ADMIN_MEMBERSHIP -> {
                    val groupMembershipStatus = GroupMembershipStatus(this)
                    groupMembershipStatus
                        .checkMembershipStatus(
                            notificationCenterResultArrayList!![position].groupId,
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
                        )
                    pushEvent("NOTIFICATION_CENTER_GROUP_ADMIN_MEMBERSHIP")
                }
                AppConstants.NOTIFICATION_CENTER_GROUP_ADMIN_REPORTED -> {
                    val groupMembershipStatus = GroupMembershipStatus(this)
                    groupMembershipStatus
                        .checkMembershipStatus(
                            notificationCenterResultArrayList!![position].groupId,
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
                        )
                    pushEvent("NOTIFICATION_CENTER_GROUP_ADMIN_REPORTED")
                }
                AppConstants.NOTIFICATION_CENTER_GROUP_ADMIN_EDIT_GROUP -> {
                    val groupMembershipStatus = GroupMembershipStatus(this)
                    groupMembershipStatus
                        .checkMembershipStatus(
                            notificationCenterResultArrayList!![position].groupId,
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
                        )
                    pushEvent("NOTIFICATION_CENTER_GROUP_ADMIN_EDIT_GROUP")
                }
                AppConstants.NOTIFICATION_CENTER_GROUP_ADMIN -> {
                    val groupMembershipStatus = GroupMembershipStatus(this)
                    groupMembershipStatus
                        .checkMembershipStatus(
                            notificationCenterResultArrayList!![position].groupId,
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
                        )
                    pushEvent("NOTIFICATION_CENTER_GROUP_ADMIN")
                }
                AppConstants.NOTIFICATION_CENTER_GROUP_LISTING -> {
                    val fragment0 = GroupsViewFragment()
                    val bundle = Bundle()
                    fragment0.arguments = bundle
                    if (isAdded) {
                        (activity as DashboardActivity?)!!.addFragment(fragment0, bundle)
                    }
                    pushEvent("NOTIFICATION_CENTER_GROUP_LISTING")
                }
                AppConstants.NOTIFICATION_CENTER_CREATE_SECTION -> {
                    pushEvent("NOTIFICATION_CENTER_CREATE_SECTION")
                }
                AppConstants.NOTIFICATION_CENTER_MY_MONEY_PERSONAL_INFO -> {
                    val intent =
                        Intent(activity, RewardsContainerActivity::class.java)
                    startActivity(intent)
                    pushEvent("NOTIFICATION_CENTER_MY_MONEY_PERSONAL_INFO")
                }
                AppConstants.NOTIFICATION_CENTER_TOPICS_ARTICLE_LISTING -> {
                    val intent =
                        Intent(activity, TopicsListingActivity::class.java)
                    intent.putExtra(
                        "parentTopicId",
                        "" + notificationCenterResultArrayList!![position].categoryId
                    )
                    startActivity(intent)
                    pushEvent("NOTIFICATION_CENTER_TOPICS_ARTICLE_LISTING")
                }
                AppConstants.NOTIFICATION_CENTER_CAMPAIGN_LISTING -> {
                    val campaignIntent =
                        Intent(activity, CampaignContainerActivity::class.java)
                    campaignIntent.putExtra("campaign_listing", "campaign_listing")
                    startActivity(campaignIntent)
                    pushEvent("NOTIFICATION_CENTER_CAMPAIGN_LISTING")
                }
                AppConstants.NOTIFICATION_CENTER_CAMPAIGN_DETAIL -> {
                    val campaignIntent =
                        Intent(activity, CampaignContainerActivity::class.java)
                    campaignIntent
                        .putExtra(
                            "campaign_id",
                            notificationCenterResultArrayList!![position].campaignId.toString() + ""
                        )
                    campaignIntent.putExtra("campaign_detail", "campaign_detail")
                    campaignIntent.putExtra("fromNotification", true)
                    startActivity(campaignIntent)
                    pushEvent("NOTIFICATION_CENTER_CAMPAIGN_DETAIL")
                }
                AppConstants.NOTIFICATION_CENTER_CAMPAIGN_SUBMIT_PROOF -> {
                    val campaignIntent =
                        Intent(activity, CampaignContainerActivity::class.java)
                    campaignIntent
                        .putExtra(
                            "campaign_Id",
                            notificationCenterResultArrayList!![position].campaignId.toString() + ""
                        )
                    campaignIntent.putExtra("campaign_submit_proof", "campaign_submit_proof")
                    startActivity(campaignIntent)
                    pushEvent("NOTIFICATION_CENTER_CAMPAIGN_SUBMIT_PROOF")
                }
                AppConstants.NOTIFICATION_CENTER_CAMPAIGN_PANCARD -> {
                    val campaignIntent =
                        Intent(activity, RewardsContainerActivity::class.java)
                    campaignIntent.putExtra("isComingFromRewards", true)
                    campaignIntent.putExtra("pageLimit", 5)
                    campaignIntent.putExtra("pageNumber", 5)
                    campaignIntent.putExtra("panCardFormNotification", "mymoney_pancard")
                    campaignIntent.putExtra("mymoney_pancard", "mymoney_pancard")
                    startActivity(campaignIntent)
                    pushEvent("NOTIFICATION_CENTER_CAMPAIGN_PANCARD")
                }
                AppConstants.NOTIFICATION_CENTER_CAMPAIGN_BANKDETAIL -> {
                    val campaignIntent =
                        Intent(activity, RewardsContainerActivity::class.java)
                    campaignIntent.putExtra("isComingfromCampaign", true)
                    campaignIntent.putExtra("pageLimit", 4)
                    campaignIntent.putExtra("pageNumber", 4)
                    campaignIntent
                        .putExtra(
                            "campaign_Id",
                            notificationCenterResultArrayList!![position].campaignId.toString() + ""
                        )
                    campaignIntent.putExtra("mymoney_bankdetails", "mymoney_bankdetails")
                    startActivity(campaignIntent)
                    pushEvent("NOTIFICATION_CENTER_CAMPAIGN_BANKDETAIL")
                }
                AppConstants.NOTIFICATION_CENTER_VIDEO_CHALLENGE_DETAIL -> {
                    val videoChallengeIntent =
                        Intent(activity, NewVideoChallengeActivity::class.java)
                    videoChallengeIntent
                        .putExtra(
                            Constants.CHALLENGE_ID,
                            "" + notificationCenterResultArrayList!![position].challengeId
                        )
                    videoChallengeIntent.putExtra("comingFrom", "notification")
                    startActivity(videoChallengeIntent)
                    pushEvent("NOTIFICATION_CENTER_VIDEO_CHALLENGE_DETAIL")
                }
                AppConstants.NOTIFICATION_CENTER_COLLECTION_DETAIL -> {
                    val intent = Intent(
                        activity,
                        UserCollectionItemListActivity::class.java
                    )
                    intent.putExtra(
                        "id",
                        "" + notificationCenterResultArrayList!![position].collectionId
                    )
                    intent.putExtra("comingFrom", "notification")
                    startActivity(intent)
                    pushEvent("NOTIFICATION_CENTER_COLLECTION_DETAIL")
                }
                AppConstants.NOTIFICATION_CENTER_BADGE_LISTING -> {
                    val intent =
                        Intent(activity, BadgeActivity::class.java)
                    startActivity(intent)
                    pushEvent("NOTIFICATION_CENTER_BADGE_LISTING")
                }
                AppConstants.NOTIFICATION_CENTER_STORY_PUBLISH_SUCCESS -> {
                    val intent = Intent(
                        activity,
                        ShortStoryModerationOrShareActivity::class.java
                    )
                    intent.putExtra("shareUrl", "")
                    intent.putExtra(
                        Constants.ARTICLE_ID,
                        notificationCenterResultArrayList!![position].articleId
                    )
                    startActivity(intent)
                    pushEvent("NOTIFICATION_CENTER_STORY_PUBLISH_SUCCESS")
                }
                AppConstants.NOTIFICATION_CENTER_STORY_CHALLENGE_DETAIL -> {
                    if (!StringUtils.isNullOrEmpty(
                            notificationCenterResultArrayList!![position].challengeId
                        )) {
                        val intent = Intent(
                            activity,
                            ShortStoryChallengeDetailActivity::class.java
                        )
                        intent.putExtra(
                            "challenge",
                            notificationCenterResultArrayList!![position].challengeId
                        )
                        startActivity(intent)
                        pushEvent("NOTIFICATION_CENTER_STORY_CHALLENGE_DETAIL")
                    }
                }
                AppConstants.NOTIFICATION_CENTER_INVITE_FRIENDS -> {
                    val profileIntent =
                        Intent(activity, UserProfileActivity::class.java)
                    profileIntent.putExtra(AppConstants.SHOW_INVITE_DIALOG_FLAG, true)
                    profileIntent.putExtra("source", "notification")
                    startActivity(profileIntent)
                    pushEvent("NOTIFICATION_CENTER_INVITE_FRIENDS")
                }
                AppConstants.NOTIFICATION_CENTER_VIDEO_LISTING -> {
                    val vlogsIntent = Intent(
                        activity,
                        CategoryVideosListingActivity::class.java
                    )
                    vlogsIntent
                        .putExtra(
                            "categoryId",
                            "" + notificationCenterResultArrayList!![position].categoryId
                        )
                    startActivity(vlogsIntent)
                    pushEvent("NOTIFICATION_CENTER_VIDEO_LISTING")
                }
                AppConstants.NOTIFICATION_CENTER_CONTENT_COMMENTS -> {
                    val contentType =
                        notificationCenterResultArrayList!![position].contentType
                    var contentId: String? = null
                    if (AppConstants.CONTENT_TYPE_ARTICLE == contentType) {
                        contentId = notificationCenterResultArrayList!![position].articleId
                    } else if (AppConstants.CONTENT_TYPE_SHORT_STORY == contentType) {
                        contentId = notificationCenterResultArrayList!![position].articleId
                    } else if (AppConstants.CONTENT_TYPE_VIDEO == contentType) {
                        contentId = notificationCenterResultArrayList!![position].videoId
                    }
                    if (StringUtils.isNullOrEmpty(contentId)) {
                        return
                    }
                    val commentId =
                        notificationCenterResultArrayList!![position].commentId
                    val replyId =
                        notificationCenterResultArrayList!![position].replyId
                    val commentReplyNotificationIntent =
                        Intent(
                            activity,
                            ContentCommentReplyNotificationActivity::class.java
                        )
                    commentReplyNotificationIntent.putExtra("articleId", contentId)
                    commentReplyNotificationIntent.putExtra("commentId", commentId)
                    commentReplyNotificationIntent.putExtra("type", "comment")
                    commentReplyNotificationIntent.putExtra("contentType", contentType)
                    commentReplyNotificationIntent.putExtra("replyId", replyId)
                    startActivity(commentReplyNotificationIntent)
                    pushEvent("NOTIFICATION_CENTER_CONTENT_COMMENTS")
                }
                AppConstants.NOTIFICATION_CENTER_CONTENT_REPLY -> {
                    val contentType =
                        notificationCenterResultArrayList!![position].contentType
                    var contentId: String? = null
                    if (AppConstants.CONTENT_TYPE_ARTICLE == contentType) {
                        contentId = notificationCenterResultArrayList!![position].articleId
                    } else if (AppConstants.CONTENT_TYPE_SHORT_STORY == contentType) {
                        contentId = notificationCenterResultArrayList!![position].articleId
                    } else if (AppConstants.CONTENT_TYPE_VIDEO == contentType) {
                        contentId = notificationCenterResultArrayList!![position].videoId
                    }
                    if (StringUtils.isNullOrEmpty(contentId)) {
                        return
                    }
                    val commentId =
                        notificationCenterResultArrayList!![position].commentId
                    val replyId =
                        notificationCenterResultArrayList!![position].replyId
                    val commentReplyNotificationIntent =
                        Intent(
                            activity,
                            ContentCommentReplyNotificationActivity::class.java
                        )
                    commentReplyNotificationIntent.putExtra("articleId", contentId)
                    commentReplyNotificationIntent.putExtra("commentId", commentId)
                    commentReplyNotificationIntent.putExtra("type", "reply")
                    commentReplyNotificationIntent.putExtra("contentType", contentType)
                    commentReplyNotificationIntent.putExtra("replyId", replyId)
                    startActivity(commentReplyNotificationIntent)
                    pushEvent("NOTIFICATION_CENTER_CONTENT_REPLY")
                }
                AppConstants.NOTIFICATION_CENTER_CONTENT_LIKE -> {
                    val contentType =
                        notificationCenterResultArrayList!![position].contentType
                    if (AppConstants.CONTENT_TYPE_ARTICLE == contentType) {
                        val intent = Intent(
                            activity,
                            ArticleDetailsContainerActivity::class.java
                        )
                        intent.putExtra(
                            Constants.ARTICLE_ID,
                            notificationCenterResultArrayList!![position].contentId
                        )
                        startActivity(intent)
                    } else if (AppConstants.CONTENT_TYPE_SHORT_STORY == contentType) {
                        val intent = Intent(
                            activity,
                            ShortStoryContainerActivity::class.java
                        )
                        intent.putExtra(
                            Constants.ARTICLE_ID,
                            notificationCenterResultArrayList!![position].contentId
                        )
                        startActivity(intent)
                    } else if (AppConstants.CONTENT_TYPE_VIDEO == contentType) {
                        val intent =
                            Intent(activity, ParallelFeedActivity::class.java)
                        intent.putExtra(
                            Constants.VIDEO_ID,
                            notificationCenterResultArrayList!![position].contentId
                        )
                        startActivity(intent)
                    }
                    pushEvent("NOTIFICATION_CENTER_CONTENT_LIKE")
                }
                AppConstants.NOTIFICATION_CENTER_ANNOUNCEMENT -> {
                    activity?.let {
                        (it as BaseActivity).handleDeeplinks(notificationCenterResultArrayList!![position].url)
                    }
                    pushEvent("NOTIFICATION_CENTER_ANNOUNCEMENT")
                }
                else -> {
                }
            }
        } else if (view.id == R.id.actionButtonWidget) {
            if (view.tag == null) {
                return
            }
            when (view.tag.toString()) {
                AppConstants.NOTIFICATION_ACTION_FOLLOW_AUTHOR -> {
                    try {
                        notificationCenterResultArrayList?.get(position)?.isFollowing = true
                        notificationCenterRecyclerAdapter!!.notifyDataSetChanged()
                        followAuthorAPI(notificationCenterResultArrayList!![position])
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }
                }
                //                AppConstants.NOTIFICATION_ACTION_SHARE_BADGE -> {
                //                    activity?.let {
                //                        if (AppUtils.shareGenericLinkWithSuccessStatus(
                //                                it,
                //                                notificationCenterResultArrayList?.get(position)?.sharingUrl
                //                            )
                //                        ) {
                //                            Utils.pushProfileEvents(
                //                                it, "CTA_Generic_Share_Private_Badge_Detail",
                //                                "BadgesDialogFragment", "Generic Share", badgeData?.badge_name
                //                            )
                //                        }
                //                    }
                //
                //                }
                //                AppConstants.NOTIFICATION_ACTION_SHARE_MILESTONE -> {
                //                    activity?.let {
                //                        if (AppUtils.shareGenericLinkWithSuccessStatus(
                //                                it,
                //                                notificationCenterResultArrayList?.get(position)?.sharingUrl
                //                            )
                //                        ) {
                //                            Utils.pushProfileEvents(
                //                                it, "CTA_Generic_Share_Private_Badge_Detail",
                //                                "BadgesDialogFragment", "Generic Share", badgeData?.badge_name
                //                            )
                //                        }
                //                    }
                //                }
                //                AppConstants.NOTIFICATION_ACTION_STORY_CHALLENGE -> {
                //                }
                //                AppConstants.NOTIFICATION_ACTION_VLOG_CHALLENGE -> {
                //                }
                else -> {
                }
            }
        }
    }

    private fun followAuthorAPI(notificationCenterResult: NotificationCenterResult) {
        if (notificationCenterResult.serviceType == "followers_mapping") {
            Utils.shareEventTracking(
                activity,
                "Notification Centre",
                "Follow_Android",
                "Notif_FollowBack_Follow"
            )
        } else {
            Utils.shareEventTracking(
                activity,
                "Notification Centre",
                "Follow_Android",
                "Notif_Invite_Follow"
            )
        }
        val retrofit = BaseApplication.getInstance().retrofit
        val followApi = retrofit.create(FollowAPI::class.java)
        val request = FollowUnfollowUserRequest()
        request.followee_id = notificationCenterResult.notifiedBy[0].userId
        val followUnfollowUserResponseCall =
            followApi.followUserInShortStoryListingV2(request)
        followUnfollowUserResponseCall.enqueue(followUnfollowUserResponseCallback)
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

    private fun pushEvent(type: String) {
        Utils.pushNotificationCenterItemClickEvent(
            activity, type,
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            "NotificationCenterRecyclerAdapter"
        )
    }

    private fun hitNotificationReadApi(notificationCenterId: String) {
        val notificationReadRequest = NotificationReadRequest()
        notificationReadRequest.notifId = notificationCenterId
        val retrofit = BaseApplication.getInstance().retrofit
        val notificationsApi = retrofit.create(
            NotificationsAPI::class.java
        )
        val call =
            notificationsApi.markNotificationAsRead(notificationReadRequest)
        call.enqueue(markNotificationReadResponseCallback)
    }

    private val markNotificationReadResponseCallback: Callback<NotificationCenterListResponse> =
        object : Callback<NotificationCenterListResponse> {
            override fun onResponse(
                call: Call<NotificationCenterListResponse>,
                response: Response<NotificationCenterListResponse>
            ) {
            }

            override fun onFailure(
                call: Call<NotificationCenterListResponse>,
                t: Throwable
            ) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }

    private fun launchEditor() {
        if (isAdded) {
            val bundle5 = Bundle()
            bundle5.putString("TITLE_PARAM", "")
            bundle5.putString("CONTENT_PARAM", "")
            bundle5.putString(
                "TITLE_PLACEHOLDER_PARAM",
                getString(R.string.example_post_title_placeholder)
            )
            bundle5.putString(
                "CONTENT_PLACEHOLDER_PARAM",
                getString(R.string.example_post_content_placeholder)
            )
            bundle5.putInt("EDITOR_PARAM", NewEditor.USE_NEW_EDITOR)
            bundle5.putString("from", "dashboard")
            val intent1 =
                Intent(activity, NewEditor::class.java)
            intent1.putExtras(bundle5)
            startActivity(intent1)
        }
    }

    override fun onMembershipStatusFetchSuccess(
        body: GroupsMembershipResponse,
        groupId: Int
    ) {
        var userType: String? = null
        if (body.data.result != null && !body.data.result.isEmpty()) {
            if (body.data.result[0].isAdmin == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_ADMIN
            } else if (body.data.result[0].isModerator == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_MODERATOR
            }
        }
        if (AppConstants.GROUP_MEMBER_TYPE_MODERATOR != userType && AppConstants.GROUP_MEMBER_TYPE_ADMIN != userType) {
            if ("male".equals(
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).gender,
                    ignoreCase = true
                ) ||
                "m".equals(
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).gender,
                    ignoreCase = true
                )) {
                if (isAdded) {
                    ToastUtils.showToast(
                        activity,
                        getString(R.string.women_only)
                    )
                }
                if (!BuildConfig.DEBUG && !AppConstants.DEBUGGING_USER_ID
                        .contains(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId)) {
                    return
                }
            }
        }
        if (body.data.result == null || body.data.result.isEmpty()) {
            val intent =
                Intent(activity, GroupsSummaryActivity::class.java)
            intent.putExtra("groupId", groupId)
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType)
            startActivity(intent)
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED == body.data.result[0].status) {
            if (isAdded) {
                ToastUtils.showToast(
                    activity,
                    getString(R.string.groups_user_blocked_msg)
                )
            }
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER == body.data.result[0].status) {
            val intent =
                Intent(activity, GroupDetailsActivity::class.java)
            intent.putExtra("groupId", groupId)
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType)
            startActivity(intent)
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION
            == body.data.result[0].status) {
            val intent =
                Intent(activity, GroupsSummaryActivity::class.java)
            intent.putExtra("groupId", groupId)
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType)
            intent.putExtra("pendingMembershipFlag", true)
            startActivity(intent)
        } else {
            val intent =
                Intent(activity, GroupsSummaryActivity::class.java)
            intent.putExtra("groupId", groupId)
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType)
            startActivity(intent)
        }
    }

    override fun onMembershipStatusFetchFail() {}
}
