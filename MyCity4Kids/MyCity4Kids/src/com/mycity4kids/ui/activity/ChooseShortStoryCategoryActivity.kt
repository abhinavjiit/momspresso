package com.mycity4kids.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.google.gson.GsonBuilder
import com.kelltontech.ui.BaseActivity
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
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
import kotlinx.android.synthetic.main.choose_short_story_category_activity.*
import kotlinx.coroutines.CoroutineExceptionHandler
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileNotFoundException

class ChooseShortStoryCategoryActivity : BaseActivity(), ShortStoryChallengeTopicsAdapter.RecyclerViewClickListener {

    val handler = CoroutineExceptionHandler { _, exception ->
        Log.d("Exception", "$exception handled !")
    }


    override fun onClick(v: View, position: Int) {
        val intent = Intent(this@ChooseShortStoryCategoryActivity, ShortStoryChallengeDetailActivity::class.java)
        intent.putExtra("Display_Name", publicShortStoryChallenges?.get(position)?.display_name)
        intent.putExtra("challenge", publicShortStoryChallenges?.get(position)?.id)
        intent.putExtra("topics", publicShortStoryChallenges?.get(position)?.parentName)
        intent.putExtra("parentId", publicShortStoryChallenges?.get(position)?.parentId)
        intent.putExtra("StringUrl", publicShortStoryChallenges?.get(position)?.extraData?.get(0)?.challenge?.imageUrl)
        startActivity(intent)
    }

    private val shortStoryChallengeAdapter: ShortStoryChallengeTopicsAdapter by lazy { ShortStoryChallengeTopicsAdapter(this) }
    val adapter: ShortStoryTopicsGridAdapter by lazy { ShortStoryTopicsGridAdapter() }
    private var shortShortTopicsData: ArrayList<ExploreTopicsModel>? = null
    private var shortStoryChallengesData: ArrayList<Topics>? = null
    private var shortStoryChallenges: ArrayList<Topics>? = null
    private var publicShortStoryTopics: ArrayList<ExploreTopicsModel>? = null
    private var publicShortStoryChallenges: ArrayList<Topics>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_short_story_category_activity)
        shortShortTopicsData = ArrayList()
        shortStoryChallengesData = ArrayList()
        val source = intent.getStringExtra("source")
        if ("dashboard" == source) {
            challengesTextView.visibility = View.VISIBLE
            shortStoryChallengeHorizontalView.visibility = View.VISIBLE
        }
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
            val intent = Intent(this@ChooseShortStoryCategoryActivity, AddShortStoryActivity::class.java)
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
            val fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE)
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
            shortStoryChallengesData?.forEach {
                if (it.id == AppConstants.SHORT_STORY_CHALLENGE_ID) {
                    shortStoryChallenges = it.child
                    shortStoryChallenges?.let { challenges ->
                        publicShortStoryChallenges = ArrayList()
                        challenges.forEach { checkPublicChallenges ->
                            if (checkPublicChallenges.publicVisibility == "1" && checkPublicChallenges.extraData[0].challenge.active == "1") {
                                publicShortStoryChallenges?.add(checkPublicChallenges)
                            }
                        }
                        publicShortStoryChallenges?.reverse()
                        publicShortStoryChallenges?.let { publicAndActiveChallenges ->
                            shortStoryShimmer.stopShimmerAnimation()
                            shortStoryShimmer.visibility = View.GONE
                            shortStoryChallengeAdapter.setShortStoryChallengesData(publicAndActiveChallenges)
                            shortStoryChallengeAdapter.notifyDataSetChanged()
                        }
                    }
                }
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

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val writtenToDisk = AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE, response.body())
                    Log.d("TopicsFilterActivity", "file download was a success? $writtenToDisk")

                    val fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE)
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
                    shortStoryChallengesData?.forEach {
                        if (it.id == AppConstants.SHORT_STORY_CHALLENGE_ID) {
                            shortStoryChallenges = it.child
                            shortStoryChallenges?.let { challenges ->
                                publicShortStoryChallenges = ArrayList()
                                challenges.forEach { checkPublicChallenges ->
                                    if (checkPublicChallenges.publicVisibility == "1" && checkPublicChallenges.extraData[0].challenge.active == "1") {
                                        publicShortStoryChallenges?.add(checkPublicChallenges)
                                    }
                                }
                                publicShortStoryChallenges?.reverse()
                                publicShortStoryChallenges?.let { publicAndActiveChallenges ->
                                    shortStoryShimmer.stopShimmerAnimation()
                                    shortStoryShimmer.visibility = View.GONE
                                    shortStoryChallengeAdapter.setShortStoryChallengesData(publicAndActiveChallenges)
                                    shortStoryChallengeAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
            })
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("Exception", Log.getStackTraceString(e))
        }
    }
}

