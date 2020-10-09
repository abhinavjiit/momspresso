package com.mycity4kids.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.BloggerRankResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.BloggerGoldAPI
import com.mycity4kids.ui.activity.BloggerGoldActivity
import com.mycity4kids.ui.activity.DashboardActivity
import com.mycity4kids.ui.activity.ViewLeaderboardActivity
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.CustomTabsHelper
import com.mycity4kids.utils.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BloggerGoldDashboardFragment : BaseFragment() {

    private lateinit var startCreatingLayout: LinearLayout
    private lateinit var leaderBoardLayout: LinearLayout
    private lateinit var leaderBoardLayout1: LinearLayout
    private lateinit var leaderBoardBtn1: TextView
    private lateinit var startCreatingBtn: TextView
    private lateinit var viewsLayout: LinearLayout
    private lateinit var articleViewLayout: LinearLayout
    private lateinit var videoViewLayout: LinearLayout
    private lateinit var article_total_view: TextView
    private lateinit var article_yesterday_view: TextView
    private lateinit var video_total_view: TextView
    private lateinit var video_yesterday_view: TextView
    private lateinit var leaderboard_btn: TextView
    private lateinit var webviewHack: TextView
    private lateinit var updatedAt: TextView
    private lateinit var earningCalculator: TextView
    private lateinit var divider: View
    private lateinit var date: String
    private var blogAverageView: Int = 0
    private var vlogAverageView: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.blogger_gold_dashboard_fragment, container, false)
        viewsLayout = view.findViewById(R.id.views_layout)
        startCreatingLayout = view.findViewById(R.id.start_creating_layout)
        leaderBoardLayout = view.findViewById(R.id.leaderboard_layout)
        articleViewLayout = view.findViewById(R.id.article_view)
        videoViewLayout = view.findViewById(R.id.video_view)
        webviewHack = view.findViewById(R.id.webview_hack)
        article_total_view = view.findViewById(R.id.article_total_view)
        article_yesterday_view = view.findViewById(R.id.article_yesterday_view)
        video_total_view = view.findViewById(R.id.video_total_view)
        video_yesterday_view = view.findViewById(R.id.video_yesterday_view)
        leaderboard_btn = view.findViewById(R.id.leaderboard_btn)
        earningCalculator = view.findViewById(R.id.earning_calculator)
        updatedAt = view.findViewById(R.id.last_updated)
        leaderBoardLayout1 = view.findViewById(R.id.leaderboard_layout1)
        leaderBoardBtn1 = view.findViewById(R.id.leaderboard_btn1)
        startCreatingBtn = view.findViewById(R.id.start_creating_btn)
        divider = view.findViewById(R.id.view)
        leaderboard_btn.setOnClickListener {
            val intent = Intent(activity, ViewLeaderboardActivity::class.java)
            startActivity(intent)
        }

        articleViewLayout.setOnClickListener {
            val intent = Intent(activity, ViewLeaderboardActivity::class.java)
            startActivity(intent)
        }

        videoViewLayout.setOnClickListener {
            val intent = Intent(activity, ViewLeaderboardActivity::class.java)
            startActivity(intent)
        }

        startCreatingBtn.setOnClickListener {
            val i = Intent(
                activity,
                DashboardActivity::class.java
            )
            i.putExtra(
                AppConstants.HOME_SELECTED_TAB,
                Constants.CREATE_CONTENT_PROMPT
            )
            startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            activity!!.finish()
        }

        showProgressDialog("please wait")
        getBloggerRank
        webviewHack.setOnClickListener {
            launchChromeTabs(
                "https://" + AppUtils.getLanguage(
                    SharedPrefUtils.getAppLocale(
                        BaseApplication.getAppContext()
                    )
                ) + ".momspresso.com/birthdaybonanza/hack_to_get_more_page_views"
            )
        }
        earningCalculator.setOnClickListener {
            activity?.let {
                (it as BloggerGoldActivity).handleDeeplinks(
                    "https://" + AppUtils.getLanguage(
                        SharedPrefUtils.getAppLocale(
                            BaseApplication.getAppContext()
                        )
                    ) + ".momspresso.com/birthdaybonanza/earning_calculator?b=" + blogAverageView + "&v=" + vlogAverageView + "&d=" + date
                )
            }
        }

        return view
    }

    private val getBloggerRank: Unit
        get() {
            val retrofit = BaseApplication.getInstance().retrofit
            val bloggerGoldAPI = retrofit.create(BloggerGoldAPI::class.java)
            val call = bloggerGoldAPI.getBloggerGoldRank(
                SharedPrefUtils.getUserDetailModel(activity).getDynamoId(),
                0
            )
            call.enqueue(bloggerRank)
        }

    private var bloggerRank = object : Callback<BloggerRankResponse> {
        override fun onFailure(call: Call<BloggerRankResponse>, t: Throwable) {
            removeProgressDialog()
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4KException", Log.getStackTraceString(t))
        }

        override fun onResponse(
            call: Call<BloggerRankResponse>,
            response: Response<BloggerRankResponse>
        ) {

            if (response.body() == null) {
                removeProgressDialog()
                return
            }
            try {
                val responseData = response.body()
                if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {
                    processBloggerRankResponse(responseData)
                }
            } catch (e: Exception) {
                removeProgressDialog()

                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4KException", Log.getStackTraceString(e))
            }
        }
    }

    private fun processBloggerRankResponse(response: BloggerRankResponse) {
        if (response.data.result.article.type.equals("article") && response.data.result.article.isContent_created) {
            viewsLayout.visibility = View.VISIBLE
            articleViewLayout.visibility = View.VISIBLE
            earningCalculator.visibility = View.VISIBLE
            article_total_view.text =
                (response.data.result.article.total_views / 1000).toString() + "K"
            article_yesterday_view.text =
                "Yesterday " + (response.data.result.article.yesterday_views / 1000).toString() + "K"
            blogAverageView = response.data.result.article.average_daily_views
            startCreatingLayout.visibility = View.GONE
            leaderBoardLayout.visibility = View.GONE
            leaderBoardLayout1.visibility = View.VISIBLE
            updatedAt.visibility = View.VISIBLE
            divider.visibility = View.VISIBLE
            webviewHack.visibility = View.VISIBLE
        } else {
            articleViewLayout.visibility = View.GONE
        }
        if (response.data.result.video.type.equals("video") && response.data.result.video.isContent_created) {
            viewsLayout.visibility = View.VISIBLE
            videoViewLayout.visibility = View.VISIBLE
            earningCalculator.visibility = View.VISIBLE
            video_total_view.text = (response.data.result.video.total_views / 1000).toString() + "K"
            video_yesterday_view.text =
                "Yesterday " + (response.data.result.video.yesterday_views / 1000).toString() + "K"
            vlogAverageView = response.data.result.video.average_daily_views
            startCreatingLayout.visibility = View.GONE
            leaderBoardLayout.visibility = View.GONE
            leaderBoardLayout1.visibility = View.VISIBLE
            updatedAt.visibility = View.VISIBLE
            divider.visibility = View.VISIBLE
            webviewHack.visibility = View.VISIBLE
        } else {
            videoViewLayout.visibility = View.GONE
        }
        if (!StringUtils.isNullOrEmpty(response.data.result.updated_at)) {
            date = response.data.result.updated_at
            updatedAt.text = "Last updated: " + date
        }
        removeProgressDialog()
    }

    private fun launchChromeTabs(deepLinkUrl: String) {
        try {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            val packageName = CustomTabsHelper.getPackageNameToUse(activity)
            if (packageName != null) {
                customTabsIntent.intent.setPackage(packageName)
            }
            customTabsIntent.launchUrl(activity, Uri.parse(deepLinkUrl))
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }
}
