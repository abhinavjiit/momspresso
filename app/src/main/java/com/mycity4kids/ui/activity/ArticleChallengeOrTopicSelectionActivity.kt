package com.mycity4kids.ui.activity

import android.accounts.NetworkErrorException
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.webkit.WebView
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
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
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.Topics
import com.mycity4kids.models.response.SuggestedTopicsResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI
import com.mycity4kids.ui.adapter.SuggestedTopicsRecyclerAdapter
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.vlogs.ContentChallengeSelectionHorizontalAdapter
import com.mycity4kids.widget.SpacesItemDecoration
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleChallengeOrTopicSelectionActivity : BaseActivity(),
    ContentChallengeSelectionHorizontalAdapter.RecyclerViewClickListener,
    SuggestedTopicsRecyclerAdapter.RecyclerViewClickListener, View.OnClickListener {
    private lateinit var articleChallengesList: ArrayList<Topics>
    private lateinit var suggestedTopicsList: ArrayList<String>
    private lateinit var articleChallengesRecyclerView: RecyclerView
    private lateinit var suggestedTopicsRecyclerView: RecyclerView
    private lateinit var mToolbar: Toolbar
    private lateinit var articleChallengesRecyclerAdapter: ContentChallengeSelectionHorizontalAdapter
    private lateinit var suggestedTopicsRecyclerAdapter: SuggestedTopicsRecyclerAdapter
    private lateinit var bottomLayout: RelativeLayout
    private lateinit var challengesShimmerLayout: ShimmerFrameLayout
    private lateinit var tagImageViewCoachMark: ImageView
    private lateinit var coachMark: RelativeLayout
    private lateinit var secondTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.article_challenges_activity)
        coachMark = findViewById(R.id.coachMark)
        tagImageViewCoachMark = findViewById(R.id.tagImageViewCoachMark)
        secondTextView = findViewById(R.id.secondTextView)
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
            ContentChallengeSelectionHorizontalAdapter(this, "", articleChallengesList, "")
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
            Utils.shareEventTracking(this, "Create section", "Create_Android", "B_SW_CTA")
            val intent = Intent(
                this,
                NewEditor::class.java
            )
            startActivity(intent)
        }
        mToolbar.setOnClickListener {
            onBackPressed()
        }

        if (!SharedPrefUtils.getOriginalContentChallengeClick(this)) {
            showOriginalContentDialog()
        }
        coachMark.setOnClickListener {
            SharedPrefUtils.setCoachmarksShownFlag(
                BaseApplication.getAppContext(),
                "articleChallengeSelectionScreenCoachMark",
                true
            )
            coachMark.visibility = View.GONE
        }
        secondTextView.setOnClickListener(this)
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
                            if (it.size > 0)
                                processChallengesData(it)
                            else coachMark.visibility = View.GONE
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
        for (i in 0 until catWiseChallengeList.size) {
            if ("1" == catWiseChallengeList[i].publicVisibility) {
                articleChallengesList.add(catWiseChallengeList[i])
                if (!SharedPrefUtils.isCoachmarksShownFlag(
                        BaseApplication.getAppContext(),
                        "articleChallengeSelectionScreenCoachMark"
                    )) {
                    coachMark.visibility = View.VISIBLE
                    if (i == 0) {
                        Picasso.get().load(catWiseChallengeList[i].extraData[0].challenge.imageUrl).error(
                            R.drawable.default_article
                        ).into(
                            tagImageViewCoachMark
                        )
                    }
                }
            }
        }
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

    override fun onChallengeItemClick(view: View, topics: Topics, parentCategoryId: String?) {
        if (view.id == R.id.info) {
            Utils.shareEventTracking(this, "Create section", "Create_Android", "B_Show_Challenge_Rules")
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
                imageView.setOnClickListener { dialog.dismiss() }
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
        Utils.shareEventTracking(this, "Create section", "Create_Android", "B_Topic")
        val intent = Intent(this, NewEditor::class.java)
        startActivity(intent)
    }

    private fun showOriginalContentDialog() {
        val dialog = Dialog(this)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_original_content)
        dialog.setCancelable(false)
        dialog.findViewById<View>(R.id.okBtn).setOnClickListener { view: View? ->
            SharedPrefUtils.setOriginalContentChallengeClick(this, true)
            dialog.dismiss()
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.secondTextView -> {
                SharedPrefUtils.setCoachmarksShownFlag(
                    BaseApplication.getAppContext(),
                    "articleChallengeSelectionScreenCoachMark",
                    true
                )
                coachMark.visibility = View.GONE
                SharedPrefUtils.setCoachmarksShownFlag(
                    BaseApplication.getAppContext(),
                    "newEditor_controllerView",
                    true
                )
                SharedPrefUtils.setCoachmarksShownFlag(
                    BaseApplication.getAppContext(),
                    "newEditor",
                    true
                )

            }
        }
    }
}
