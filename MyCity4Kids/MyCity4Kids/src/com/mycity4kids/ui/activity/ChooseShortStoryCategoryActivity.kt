package com.mycity4kids.ui.activity

import android.os.Bundle
<<<<<<< HEAD
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
=======
import android.view.View
>>>>>>> working on 100 word story new flow
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
<<<<<<< HEAD
import com.mycity4kids.models.ExploreTopicsModel
import com.mycity4kids.models.Topics
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.adapter.ShortStoryChallengeTopicsAdapter
import com.mycity4kids.ui.adapter.ShortStoryTopicsGridAdapter
import kotlinx.android.synthetic.main.choose_short_story_category_activity.*
import kotlinx.coroutines.*

class ChooseShortStoryCategoryActivity : BaseActivity(), ShortStoryChallengeTopicsAdapter.RecyclerViewClickListener {

    val handler = CoroutineExceptionHandler { _, exception ->
        Log.d("Exception", "$exception handled !")
    }


    override fun onClick(v: View, position: Int) {


    }

    private val shortStoryChallengeAdapter: ShortStoryChallengeTopicsAdapter by lazy { ShortStoryChallengeTopicsAdapter(this) }
    val adapter: ShortStoryTopicsGridAdapter by lazy { ShortStoryTopicsGridAdapter() }
    private var shortShortTopicsData: ArrayList<ExploreTopicsModel>? = null
    private var shortStoryChallengesData: ArrayList<Topics>? = null
=======
import com.mycity4kids.models.Topics
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI
import kotlinx.android.synthetic.main.choose_short_story_category_activity.*
import kotlinx.coroutines.*

class ChooseShortStoryCategoryActivity : BaseActivity() {
>>>>>>> working on 100 word story new flow

    override fun updateUi(response: Response?) {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_short_story_category_activity)

<<<<<<< HEAD
        shortShortTopicsData = ArrayList()
        shortStoryChallengesData = ArrayList()
        val llm = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        shortStoryChallengeHorizontalView.layoutManager = llm
        shortStoryChallengeHorizontalView.adapter = shortStoryChallengeAdapter
        topicsGridView.adapter = adapter
        topicsGridView.isExpanded = true


        CoroutineScope(Dispatchers.Main + handler).launch {
            val shortStoryChallengesResponse = async { getAllShortStoryChallengesAsync() }
            val shortStoryTopicsResponse = async { getAllShortStoryTopicsAsync() }

            if (shortStoryChallengesResponse.await().isSuccessful and shortStoryTopicsResponse.await().isSuccessful) {

                shortStoryShimmer.stopShimmerAnimation()
                shortStoryShimmer.visibility = View.GONE
                //       Log.d("ChallengesResponse11", "shortStoryChallengesResponse")
                processDataForShortStoryChallenges(shortStoryChallengesResponse.await())
                //      Log.d("TopicsResponse22", "shortStoryTopicsResponse")
                processDataForShortStoryTopics(shortStoryTopicsResponse.await())
                //     Log.d("TopicsResponse33", "shortStoryTopicsResponse")


            } else {
                ToastUtils.showToast(this@ChooseShortStoryCategoryActivity, "something went wrong")
            }

        }

        topicsGridView.setOnItemClickListener { parent, view, position, id ->
            when (view.id) {
                R.id.tagImageView -> {
                    val topicId = shortShortTopicsData?.get(position)?.id
                }
            }
        }

    }

    private suspend fun getAllShortStoryChallengesAsync(): retrofit2.Response<ArrayList<Topics>> {
        return BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).getShortStoryChallengesAsync(AppConstants.SHORT_STORY_CHALLENGE_ID, "1", "1")
    }

    private suspend fun getAllShortStoryTopicsAsync(): retrofit2.Response<ArrayList<ExploreTopicsModel>> {
        return BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).getShortStoryTopicsAsync(AppConstants.SHORT_STORY_CATEGORYID, "1")
    }

    private fun processDataForShortStoryChallenges(data: retrofit2.Response<ArrayList<Topics>>) {
        shortStoryChallengesData = data.body()
        shortStoryChallengesData?.let {
            shortStoryChallengeAdapter.setShortStoryChallengesData(it)
            shortStoryChallengeAdapter.notifyDataSetChanged()
        }

    }

    private fun processDataForShortStoryTopics(data: retrofit2.Response<ArrayList<ExploreTopicsModel>>) {
        shortShortTopicsData = data.body()
        shortShortTopicsData?.let {
            adapter.setTopicsData(it)
            adapter.notifyDataSetChanged()
        }
=======
        CoroutineScope(Dispatchers.IO).launch {
            val shortStoryChallengesResponse = getAllShortStoryChallengesAsync().await()
            val shortStoryTopicsResponse = getAllShortStoryTopicsAsync().await()
            if (shortStoryChallengesResponse.isSuccessful && shortStoryTopicsResponse.isSuccessful) {
                MainScope().launch {
                    shortStoryShimmer.stopShimmerAnimation()
                    shortStoryShimmer.visibility = View.GONE
                    processDataForShortStoryChallenges(shortStoryChallengesResponse)
                    processDataForShortStoryTopics(shortStoryTopicsResponse)
                }

            } else {
                MainScope().launch {
                    ToastUtils.showToast(this@ChooseShortStoryCategoryActivity, "something went wrong")
                }
            }
        }
    }

    private fun getAllShortStoryChallengesAsync(): Deferred<retrofit2.Response<Topics>> {
        return BaseApplication.getInstance().campaignRetrofit.create(ShortStoryAPI::class.java).getShortStoryChallengesAsync(AppConstants.SHORT_STORY_CHALLENGE_ID, "1", "1")
    }

    private fun getAllShortStoryTopicsAsync(): Deferred<retrofit2.Response<Topics>> {
        return BaseApplication.getInstance().campaignRetrofit.create(ShortStoryAPI::class.java).getShortStoryTopicsAsync(AppConstants.SHORT_STORY_CATEGORYID, "1")
    }

    private fun processDataForShortStoryChallenges(data: retrofit2.Response<Topics>) {


    }

    private fun processDataForShortStoryTopics(data: retrofit2.Response<Topics>) {
>>>>>>> working on 100 word story new flow

    }

    override fun onStart() {
        super.onStart()
        shortStoryShimmer.startShimmerAnimation()
    }


    override fun onStop() {
        super.onStop()
        shortStoryShimmer.stopShimmerAnimation()
<<<<<<< HEAD
    }

    // suspend fun <T> awaitTask(task: Task<T>): T =
=======

    }
>>>>>>> working on 100 word story new flow

}