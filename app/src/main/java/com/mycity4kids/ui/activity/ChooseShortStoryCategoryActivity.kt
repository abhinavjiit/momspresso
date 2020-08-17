package com.mycity4kids.ui.activity

import android.accounts.NetworkErrorException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.models.Topics
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI
import com.mycity4kids.ui.adapter.ShortStoryChallengeTopicsAdapter
import com.mycity4kids.ui.adapter.ShortStoryTopicsGridAdapter
import kotlinx.android.synthetic.main.choose_short_story_category_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChooseShortStoryCategoryActivity : BaseActivity(),
    ShortStoryChallengeTopicsAdapter.RecyclerViewClickListener {
    private val shortStoryChallengeAdapter: ShortStoryChallengeTopicsAdapter by lazy {
        ShortStoryChallengeTopicsAdapter(
            this
        )
    }
    val adapter: ShortStoryTopicsGridAdapter by lazy { ShortStoryTopicsGridAdapter() }
    private lateinit var shortShortTopicsData: ArrayList<Topics>
    private lateinit var shortStoryChallengesData: ArrayList<Topics>
    private var source: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_short_story_category_activity)
        source = intent.getStringExtra("source")

        shortShortTopicsData = ArrayList()
        shortStoryChallengesData = ArrayList()

        challengesTextView.visibility = View.VISIBLE
        shortStoryChallengeHorizontalView.visibility = View.VISIBLE
        val llm = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        shortStoryChallengeHorizontalView.layoutManager = llm
        shortStoryChallengeHorizontalView.adapter = shortStoryChallengeAdapter

        topicsGridView.adapter = adapter
        topicsGridView.isExpanded = true

        toolbarTitle.setOnClickListener {
            finish()
        }
        topicsGridView.setOnItemClickListener { parent, view, position, id ->
            val topicId = shortShortTopicsData.get(position).id
            val intent =
                Intent(this@ChooseShortStoryCategoryActivity, AddShortStoryActivity::class.java)
            intent.putExtra("categoryId", topicId)
            intent.putExtra("categoryName", shortShortTopicsData.get(position).display_name)
            startActivity(intent)
        }

        getCategoriesData()
        getChallengeData()
    }

    private fun getCategoriesData() {
        val retrofit = BaseApplication.getInstance().retrofit
        val categoriesApi = retrofit.create(VlogsListingAndDetailsAPI::class.java)
        val categoriesCall = categoriesApi.getCategoryDetails(AppConstants.SHORT_STORY_CATEGORYID)
        categoriesCall.enqueue(ssCategoriesResponseCallBack)
    }

    private fun getChallengeData() {
        val retrofit = BaseApplication.getInstance().retrofit
        val challengesApi = retrofit.create(VlogsListingAndDetailsAPI::class.java)
        val challengesCall = challengesApi.getCategoryDetails(AppConstants.SHORT_STORY_CHALLENGE_ID)
        challengesCall.enqueue(ssChallengeListResponseCallBack)
    }

    private var ssCategoriesResponseCallBack = object : Callback<Topics> {
        override fun onResponse(call: Call<Topics>, response: Response<Topics>) {
            shortStoryShimmer.stopShimmerAnimation()
            shortStoryShimmer.visibility = View.GONE
            if (null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                FirebaseCrashlytics.getInstance().recordException(nee)
                return
            }
            if (response.isSuccessful) {
                try {
                    val responseData = response.body()
                    responseData?.let {
                        processTopicsData(it)
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d(
                        "MC4kException", Log.getStackTraceString(e)
                    )
                }
            }
        }

        override fun onFailure(call: Call<Topics>, t: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    private fun processTopicsData(responseData: Topics) {
        responseData.child?.let {
            for (i in 0 until it.size) {
                if (it[i]?.id != AppConstants.SHORT_STORY_CHALLENGE_ID && it[i]?.publicVisibility == "1") {
                    shortShortTopicsData.add(it[i])
                }
            }
            adapter.setTopicsData(shortShortTopicsData)
            adapter.notifyDataSetChanged()
        }
    }

    private var ssChallengeListResponseCallBack = object : Callback<Topics> {
        override fun onResponse(call: Call<Topics>, response: Response<Topics>) {
            shortStoryShimmer.stopShimmerAnimation()
            shortStoryShimmer.visibility = View.GONE
            if (null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                FirebaseCrashlytics.getInstance().recordException(nee)
                return
            }
            if (response.isSuccessful) {
                try {
                    val responseData = response.body()
                    responseData?.let {
                        processChallengesData(it)
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d(
                        "MC4kException", Log.getStackTraceString(e)
                    )
                }
            }
        }

        override fun onFailure(call: Call<Topics>, t: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    private fun processChallengesData(challengeData: Topics) {
        challengeData.child?.let {
            for (i in 0 until it.size) {
                if (it[i]?.publicVisibility == "1") {
                    shortStoryChallengesData.add(it[i])
                }
            }
            shortStoryChallengesData.reverse()
            shortStoryChallengeAdapter.setShortStoryChallengesData(shortStoryChallengesData)
            shortStoryChallengeAdapter.notifyDataSetChanged()
        }
    }

    override fun onStart() {
        super.onStart()
        shortStoryShimmer.startShimmerAnimation()
    }

    override fun onStop() {
        super.onStop()
        shortStoryShimmer.stopShimmerAnimation()
    }

    override fun onClick(v: View, position: Int) {
        val intent = Intent(this, ShortStoryChallengeDetailActivity::class.java)
        intent.putExtra("challenge", shortStoryChallengesData.get(position).id)
        startActivity(intent)
    }
}
