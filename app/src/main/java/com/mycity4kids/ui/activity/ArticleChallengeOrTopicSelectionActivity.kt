package com.mycity4kids.ui.activity

import android.accounts.NetworkErrorException
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.editor.NewEditor
import com.mycity4kids.models.Topics
import com.mycity4kids.models.response.SuggestedTopicsResponse
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI
import com.mycity4kids.ui.adapter.SuggestedTopicsRecyclerAdapter
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.vlogs.ContentChallengeSelectionHorizontalAdapter
import com.mycity4kids.widget.SpacesItemDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleChallengeOrTopicSelectionActivity : BaseActivity(),
    ContentChallengeSelectionHorizontalAdapter.RecyclerViewClickListener,
    SuggestedTopicsRecyclerAdapter.RecyclerViewClickListener {
    private lateinit var articleChallengesList: ArrayList<Topics>
    private lateinit var suggestedTopicsList: ArrayList<String>
    private lateinit var articleChallengesRecyclerView: RecyclerView
    private lateinit var suggestedTopicsRecyclerView: RecyclerView
    private lateinit var mToolbar: Toolbar
    private lateinit var articleChallengesRecyclerAdapter: ContentChallengeSelectionHorizontalAdapter
    private lateinit var suggestedTopicsRecyclerAdapter: SuggestedTopicsRecyclerAdapter
    private lateinit var bottomLayout: RelativeLayout
    private lateinit var challengesShimmerLayout: ShimmerFrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.article_challenges_activity)
        articleChallengesRecyclerView =
            findViewById(R.id.articleChallengesRecyclerView)
        suggestedTopicsRecyclerView =
            findViewById(R.id.suggestedTopicsRecyclerView)
        mToolbar = findViewById(R.id.toolbar)
        bottomLayout = findViewById(R.id.bottomLayout)
        challengesShimmerLayout = findViewById(R.id.challengesShimmerLayout)
        setSupportActionBar(mToolbar)
        articleChallengesList = ArrayList()

        challengesShimmerLayout.startShimmerAnimation()
        challengesShimmerLayout.visibility = View.VISIBLE

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
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.space_15)
        suggestedTopicsRecyclerView.addItemDecoration(SpacesItemDecoration(spacingInPixels))
        suggestedTopicsRecyclerAdapter = SuggestedTopicsRecyclerAdapter(suggestedTopicsList, this)
        suggestedTopicsRecyclerView.adapter = suggestedTopicsRecyclerAdapter
        suggestedTopics

        bottomLayout.setOnClickListener {
            val intent = Intent(
                this,
                NewEditor::class.java
            )
            startActivity(intent)
        }
        mToolbar.setOnClickListener {
            onBackPressed()
        }
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
                    .getCategoryDetails(AppConstants.ARTICLE_CHALLENGE_CATEGORY_ID)
            callRecentVideoArticles.enqueue(blogsChallengeResponseCallBack)
        }

    private val blogsChallengeResponseCallBack: Callback<Topics> =
        object : Callback<Topics> {
            override fun onResponse(
                call: Call<Topics>,
                response: Response<Topics>
            ) {
                if (null == response.body()) {
                    val nee =
                        NetworkErrorException(response.raw().toString())
                    FirebaseCrashlytics.getInstance().recordException(nee)
                    return
                }
                if (response.isSuccessful) {
                    try {
                        challengesShimmerLayout.stopShimmerAnimation()
                        challengesShimmerLayout.visibility = View.GONE
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
                t: Throwable
            ) {
            }
        }

    private fun processChallengesData(catWiseChallengeList: ArrayList<Topics>) {
        articleChallengesList.addAll(catWiseChallengeList)
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

    override fun onChallengeItemClick(view: View, topics: Topics) {
        if (view.id == R.id.info) {
            topics.extraData[0].challenge.rules?.let {
                val dialog = Dialog(this)
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
        } else {
            val intent = Intent(this, ArticleChallengeDetailActivity::class.java)
            intent.putExtra("articleChallengeId", topics.id)
            intent.putExtra("challengeName", topics.display_name)
            startActivity(intent)
        }
    }

    override fun onSuggestedTopicClick() {
        val intent = Intent(this, NewEditor::class.java)
        startActivity(intent)
    }
}
