package com.mycity4kids.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.google.gson.GsonBuilder
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.models.ExploreTopicsModel
import com.mycity4kids.models.ExploreTopicsResponse
import com.mycity4kids.models.Topics
import com.mycity4kids.models.TopicsResponse
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI
import com.mycity4kids.ui.adapter.ShortStoryChallengeTopicsAdapter
import com.mycity4kids.ui.adapter.ShortStoryTopicsGridAdapter
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.ArrayAdapterFactory
import java.io.FileNotFoundException
import kotlinx.android.synthetic.main.choose_short_story_category_activity.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChooseShortStoryCategoryActivity : BaseActivity(),
    ShortStoryChallengeTopicsAdapter.RecyclerViewClickListener {

    override fun onClick(v: View, position: Int) {
        val intent = Intent(
            this@ChooseShortStoryCategoryActivity,
            ShortStoryChallengeDetailActivity::class.java
        )
        intent.putExtra("Display_Name", publicShortStoryChallenges?.get(position)?.display_name)
        intent.putExtra("challenge", publicShortStoryChallenges?.get(position)?.id)
        intent.putExtra("topics", publicShortStoryChallenges?.get(position)?.parentName)
        intent.putExtra("parentId", publicShortStoryChallenges?.get(position)?.parentId)
        intent.putExtra("source", source)
        intent.putExtra(
            "StringUrl",
            publicShortStoryChallenges?.get(position)?.extraData?.get(0)?.challenge?.imageUrl
        )
        startActivity(intent)
    }

    private val shortStoryChallengeAdapter: ShortStoryChallengeTopicsAdapter by lazy {
        ShortStoryChallengeTopicsAdapter(
            this
        )
    }
    val adapter: ShortStoryTopicsGridAdapter by lazy { ShortStoryTopicsGridAdapter() }
    private var shortShortTopicsData: ArrayList<ExploreTopicsModel>? = null
    private var shortStoryChallengesData: ArrayList<Topics>? = null
    private var shortStoryChallenges: ArrayList<Topics>? = null
    private var publicShortStoryTopics: ArrayList<ExploreTopicsModel>? = null
    private var publicShortStoryChallenges: ArrayList<Topics>? = null
    private var source: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_short_story_category_activity)
        source = intent.getStringExtra("source")
        challengesTextView.visibility = View.VISIBLE
        shortStoryChallengeHorizontalView.visibility = View.VISIBLE
        val llm = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        shortStoryChallengeHorizontalView.layoutManager = llm
        shortStoryChallengeHorizontalView.adapter = shortStoryChallengeAdapter
        topicsGridView.adapter = adapter
        topicsGridView.isExpanded = true
        fetchShortStoryTopicsAndChallenges()
        toolbarTitle.setOnClickListener {
            finish()
        }
        topicsGridView.setOnItemClickListener { parent, view, position, id ->
            val topicId = publicShortStoryTopics?.get(position)?.id
            val intent =
                Intent(this@ChooseShortStoryCategoryActivity, AddShortStoryActivity::class.java)
            intent.putExtra("categoryId", topicId)
            intent.putExtra("categoryName", publicShortStoryTopics?.get(position)?.display_name)
            startActivity(intent)
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

    private fun fetchShortStoryTopicsAndChallenges() {
        try {
            val fileInputStream =
                BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE)
            val fileContent = AppUtils.convertStreamToString(fileInputStream)
            val gson = GsonBuilder().registerTypeAdapterFactory(ArrayAdapterFactory()).create()
            val res = gson.fromJson(fileContent, ExploreTopicsResponse::class.java)
            val challengesRes = gson.fromJson(fileContent, TopicsResponse::class.java)
            shortShortTopicsData = ArrayList()
            shortStoryChallengesData = ArrayList()
            for (i in 0 until res.data.size) {
                if (AppConstants.SHORT_STORY_CATEGORYID == res.data[i].id) {
                    shortShortTopicsData = (res.data[i].child)
                    shortStoryChallengesData = (challengesRes.data[i].child)
                    shortShortTopicsData?.let {
                        publicShortStoryTopics = ArrayList()
                        it.forEach { checkPublicTopics ->
                            if (checkPublicTopics.publicVisibility == "1") {
                                publicShortStoryTopics?.add(checkPublicTopics)
                            }
                        }
                        publicShortStoryTopics?.let { publicShortStoryTopics ->
                            adapter.setTopicsData(publicShortStoryTopics)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    break
                }
            }
            for (i in 0 until shortStoryChallengesData?.size!!) {
                if (shortStoryChallengesData?.get(i)?.id == AppConstants.SHORT_STORY_CHALLENGE_ID) {
                    shortStoryChallenges = shortStoryChallengesData?.get(i)?.child
                    shortStoryChallenges?.let { challenges ->
                        publicShortStoryChallenges = ArrayList()
                        challenges.forEach { checkPublicChallenges ->
                            if (checkPublicChallenges.publicVisibility == "1") {
                                if (!checkPublicChallenges.extraData.isNullOrEmpty() && checkPublicChallenges.extraData?.get(
                                        0
                                    )?.challenge?.active == "1"
                                )
                                    publicShortStoryChallenges?.add(checkPublicChallenges)
                            }
                        }
                        publicShortStoryChallenges?.reverse()
                        publicShortStoryChallenges?.let { publicAndActiveChallenges ->
                            shortStoryShimmer.stopShimmerAnimation()
                            shortStoryShimmer.visibility = View.GONE
                            shortStoryChallengeAdapter.setShortStoryChallengesData(
                                publicAndActiveChallenges
                            )
                            shortStoryChallengeAdapter.notifyDataSetChanged()
                        }
                    }
                }
                break
            }
        } catch (e: FileNotFoundException) {
            Crashlytics.logException(e)
            Log.d("FileNotFoundException", Log.getStackTraceString(e))
            val retrofit = BaseApplication.getInstance().retrofit
            val topicsAPI = retrofit.create(TopicsCategoryAPI::class.java)
            val caller = topicsAPI.downloadTopicsJSON()
            caller.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Crashlytics.logException(t)
                    Log.d("MC4KException", Log.getStackTraceString(t))
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    try {
                        AppUtils.writeResponseBodyToDisk(
                            BaseApplication.getAppContext(),
                            AppConstants.CATEGORIES_JSON_FILE,
                            response.body()
                        )
                        val fileInputStream = BaseApplication.getAppContext()
                            .openFileInput(AppConstants.CATEGORIES_JSON_FILE)
                        val fileContent = AppUtils.convertStreamToString(fileInputStream)
                        val gson =
                            GsonBuilder().registerTypeAdapterFactory(ArrayAdapterFactory()).create()
                        val res = gson.fromJson(fileContent, ExploreTopicsResponse::class.java)
                        val challengesRes = gson.fromJson(fileContent, TopicsResponse::class.java)
                        shortShortTopicsData = ArrayList()
                        shortStoryChallengesData = ArrayList()
                        for (i in 0 until res.data.size) {
                            if (AppConstants.SHORT_STORY_CATEGORYID == res.data[i].id) {
                                shortShortTopicsData = (res.data[i].child)
                                shortStoryChallengesData = (challengesRes.data[i].child)
                                shortShortTopicsData?.let {
                                    publicShortStoryTopics = ArrayList()
                                    it.forEach { checkPublicTopics ->
                                        if (checkPublicTopics.publicVisibility == "1") {
                                            publicShortStoryTopics?.add(checkPublicTopics)
                                        }
                                    }
                                    publicShortStoryTopics?.let { publicShortStoryTopics ->
                                        adapter.setTopicsData(publicShortStoryTopics)
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                                break
                            }
                        }
                        for (i in 0 until shortStoryChallengesData?.size!!) {
                            if (shortStoryChallengesData?.get(i)?.id == AppConstants.SHORT_STORY_CHALLENGE_ID) {
                                shortStoryChallenges = shortStoryChallengesData?.get(i)?.child
                                shortStoryChallenges?.let { challenges ->
                                    publicShortStoryChallenges = ArrayList()
                                    challenges.forEach { checkPublicChallenges ->

                                        if (checkPublicChallenges.publicVisibility == "1") {
                                            if (!checkPublicChallenges.extraData.isNullOrEmpty() && checkPublicChallenges.extraData?.get(
                                                    0
                                                )?.challenge?.active == "1"
                                            )
                                                publicShortStoryChallenges?.add(
                                                    checkPublicChallenges
                                                )
                                        }
                                    }
                                    publicShortStoryChallenges?.reverse()
                                    publicShortStoryChallenges?.let { publicAndActiveChallenges ->
                                        shortStoryShimmer.stopShimmerAnimation()
                                        shortStoryShimmer.visibility = View.GONE
                                        shortStoryChallengeAdapter.setShortStoryChallengesData(
                                            publicAndActiveChallenges
                                        )
                                        shortStoryChallengeAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                            break
                        }
                    } catch (e: Exception) {
                        Crashlytics.logException(e)
                        Log.d("MC4KException", Log.getStackTraceString(e))
                    }
                }
            })
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4KException", Log.getStackTraceString(e))
        }
    }
}
