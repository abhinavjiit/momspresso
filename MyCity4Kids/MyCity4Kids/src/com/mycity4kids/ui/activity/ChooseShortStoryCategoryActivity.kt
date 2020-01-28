package com.mycity4kids.ui.activity

import android.os.Bundle
import android.view.View
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.models.Topics
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI
import kotlinx.android.synthetic.main.choose_short_story_category_activity.*
import kotlinx.coroutines.*

class ChooseShortStoryCategoryActivity : BaseActivity() {

    override fun updateUi(response: Response?) {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_short_story_category_activity)

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

    }

    override fun onStart() {
        super.onStart()
        shortStoryShimmer.startShimmerAnimation()
    }


    override fun onStop() {
        super.onStop()
        shortStoryShimmer.stopShimmerAnimation()

    }

}