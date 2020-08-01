package com.mycity4kids.ui.activity

import android.accounts.NetworkErrorException
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.Topics
import com.mycity4kids.models.response.SuggestedTopicsResponse
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI
import com.mycity4kids.ui.adapter.ChallengeListingRecycleAdapter
import com.mycity4kids.ui.adapter.SuggestedTopicsRecyclerAdapter
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.vlogs.ContentChallengeSelectionHorizontalAdapter
import com.mycity4kids.vlogs.VlogsCategoryWiseChallengesResponse
import com.mycity4kids.widget.SpacesItemDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleChallengesActivity : BaseActivity(),
    ChallengeListingRecycleAdapter.RecyclerViewClickListener,
    ContentChallengeSelectionHorizontalAdapter.RecyclerViewClickListener,
    SuggestedTopicsRecyclerAdapter.RecyclerViewClickListener {
    private lateinit var articleChallengesList: ArrayList<Topics>
    private lateinit var suggestedTopicsList: ArrayList<String>
    private lateinit var articleChallengesRecyclerView: RecyclerView
    private lateinit var suggestedTopicsRecyclerView: RecyclerView
    private lateinit var mToolbar: Toolbar
    private lateinit var articleChallengesRecyclerAdapter: ContentChallengeSelectionHorizontalAdapter
    private lateinit var suggestedTopicsRecyclerAdapter: SuggestedTopicsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.article_challenges_activity)
        articleChallengesRecyclerView =
            findViewById(R.id.articleChallengesRecyclerView)
        suggestedTopicsRecyclerView =
            findViewById(R.id.suggestedTopicsRecyclerView)
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)

        articleChallengesList = ArrayList()
        val llm = LinearLayoutManager(this)
        llm.orientation = RecyclerView.HORIZONTAL
        articleChallengesRecyclerView.layoutManager = llm
        articleChallengesRecyclerAdapter =
            ContentChallengeSelectionHorizontalAdapter(this, articleChallengesList, "")
        articleChallengesRecyclerView.adapter = articleChallengesRecyclerAdapter
        challenges

        suggestedTopicsList = ArrayList()
        val llm1 = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        suggestedTopicsRecyclerView.layoutManager = llm1
        val spacingInPixels =
            resources.getDimensionPixelSize(R.dimen.groups_column_spacing)
        suggestedTopicsRecyclerView.addItemDecoration(SpacesItemDecoration(spacingInPixels))
        suggestedTopicsRecyclerAdapter = SuggestedTopicsRecyclerAdapter(suggestedTopicsList, this)
        suggestedTopicsRecyclerView.adapter = suggestedTopicsRecyclerAdapter
        suggestedTopics
    }

    private val challenges: Unit
        get() {
            val retrofit = BaseApplication.getInstance().retrofit
            val vlogsListingAndDetailsApi =
                retrofit.create(
                    VlogsListingAndDetailsAPI::class.java
                )
            val callRecentVideoArticles =
                vlogsListingAndDetailsApi
                    .vlogsCategoryWiseChallenges
            callRecentVideoArticles.enqueue(vlogChallengeResponseCallBack)
        }

    private val vlogChallengeResponseCallBack: Callback<VlogsCategoryWiseChallengesResponse> =
        object : Callback<VlogsCategoryWiseChallengesResponse> {
            override fun onResponse(
                call: Call<VlogsCategoryWiseChallengesResponse>,
                response: Response<VlogsCategoryWiseChallengesResponse>
            ) {
                if (null == response.body()) {
                    val nee =
                        NetworkErrorException(response.raw().toString())
                    FirebaseCrashlytics.getInstance().recordException(nee)
                    return
                }
                if (response.isSuccessful) {
                    try {
                        val responseData = response.body()
                        if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                            processChallengesData(responseData.data.result)
                        }
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }
                }
            }

            override fun onFailure(
                call: Call<VlogsCategoryWiseChallengesResponse>,
                t: Throwable
            ) {
            }
        }

    private fun processChallengesData(catWiseChallengeList: ArrayList<Topics>) {
        articleChallengesList.addAll(catWiseChallengeList[0].child)
        articleChallengesRecyclerAdapter.notifyDataSetChanged()
    }

    private val suggestedTopics: Unit
        get() {
            val retrofit = BaseApplication.getInstance().retrofit
            val suggestedTopicsAPI = retrofit.create(
                TopicsCategoryAPI::class.java
            )
            val suggestedTopicsCAll =
                suggestedTopicsAPI.getSuggestedTopics("0,1,2,3,4,5,6,7,8,9")
            suggestedTopicsCAll.enqueue(suggestedTopicsResponseCallback)
        }

    private val suggestedTopicsResponseCallback: Callback<SuggestedTopicsResponse> =
        object : Callback<SuggestedTopicsResponse> {
            override fun onResponse(
                call: Call<SuggestedTopicsResponse>,
                response: Response<SuggestedTopicsResponse>
            ) {
                if (response.body() == null) {
                    showToast(getString(R.string.server_went_wrong))
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        processSuggestedTopicsResponse(responseData)
                    } else {
                        showToast(getString(R.string.went_wrong))
                    }
                } catch (e: java.lang.Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4KException", Log.getStackTraceString(e))
                    showToast(getString(R.string.went_wrong))
                }
            }

            override fun onFailure(
                call: Call<SuggestedTopicsResponse>,
                t: Throwable
            ) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4KException", Log.getStackTraceString(t))
            }
        }

    private fun processSuggestedTopicsResponse(responseData: SuggestedTopicsResponse) {
        val topicList = responseData.data[0].result[AppUtils.getLangKey().toString()]
        topicList?.let { suggestedTopicsList.addAll(it) }
        suggestedTopicsRecyclerAdapter.notifyDataSetChanged()
    }

    override fun onClick(
        view: View,
        position: Int,
        activeUrl: String
    ) {
    }

    override fun onChallengeItemClick(view: View, topics: Topics) {
    }

    override fun onSuggestedTopicClick() {
    }
}
