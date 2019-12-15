package com.mycity4kids.ui.fragment

import android.accounts.NetworkErrorException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.profile.MilestonesResponse
import com.mycity4kids.profile.MilestonesResult
import com.mycity4kids.retrofitAPIsInterfaces.MilestonesAPI
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity
import com.mycity4kids.ui.activity.ParallelFeedActivity
import com.mycity4kids.ui.activity.ShortStoryContainerActivity
import com.mycity4kids.ui.adapter.MilestonesListAdapter
import retrofit2.Call
import retrofit2.Callback

/**
 * Created by hemant on 28/7/17.
 */
class MilestonesFragment : BaseFragment(), View.OnClickListener, MilestonesListAdapter.RecyclerViewClickListener {
    private var rootView: View? = null
    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    private var userId: String? = null
    private var milestonesAdapter: MilestonesListAdapter? = null
    private var milestonesList: ArrayList<MilestonesResult>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_milestones, container, false)
        recyclerView = rootView?.findViewById(R.id.recyclerView)
        progressBar = rootView?.findViewById(R.id.progressBar)

        userId = "fdf2b966ae8841cda022d397cbbf85c8" //arguments?.getString(Constants.AUTHOR_ID)
        milestonesList = ArrayList()

        if (userId.isNullOrBlank()) {
            activity?.let {
                ToastUtils.showToast(it, getString(R.string.empty_screen))
            }
        } else {
            getUsersMilestones(userId)
        }
        milestonesAdapter = MilestonesListAdapter(this)
        milestonesAdapter?.setListData(milestonesList)

        val llm = LinearLayoutManager(context)
        llm.orientation = RecyclerView.VERTICAL
        recyclerView?.layoutManager = llm
        recyclerView?.adapter = milestonesAdapter

        return rootView
    }

    private fun getUsersMilestones(userId: String?) {
        val retrofit = BaseApplication.getInstance().retrofit
        val milestonesAPI = retrofit.create(MilestonesAPI::class.java)
        val milestonesListResponseCall = milestonesAPI.getMilestoneList(userId)
        milestonesListResponseCall.enqueue(milestonesListResponseCallback)
    }

    private var milestonesListResponseCallback = object : Callback<MilestonesResponse> {
        override fun onResponse(call: Call<MilestonesResponse>, response: retrofit2.Response<MilestonesResponse>) {
            progressBar?.visibility = View.GONE
            try {
                progressBar?.visibility = View.GONE
                if (null == response.body()) {
                    val nee = NetworkErrorException(response.raw().toString())
                    Crashlytics.logException(nee)
                    return
                }
                val responseData = response.body()
                if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {
                    processMilestoneResponse(responseData.data?.result)
                } else {
                }
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<MilestonesResponse>, e: Throwable) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun processMilestoneResponse(result: List<MilestonesResult>?) {
        result?.let {
            milestonesList?.addAll(result)
            milestonesAdapter?.notifyDataSetChanged()
        }
    }

    override fun onClick(view: View, position: Int) {
        when {
            milestonesList?.get(position)?.item_type == AppConstants.CONTENT_TYPE_ARTICLE -> {
                activity?.let {
                    val intent = Intent(it, ArticleDetailsContainerActivity::class.java)
                    intent.putExtra(Constants.ARTICLE_ID, milestonesList?.get(position)?.content_id)
                    intent.putExtra(Constants.AUTHOR_ID, milestonesList?.get(position)?.user_id)
                    intent.putExtra(Constants.FROM_SCREEN, "MilestonesFragment")
                    intent.putExtra(Constants.AUTHOR, milestonesList?.get(position)?.user_id + "~")
                    startActivity(intent)
                }
            }
            milestonesList?.get(position)?.item_type == AppConstants.CONTENT_TYPE_SHORT_STORY -> {
                activity?.let {
                    val intent = Intent(it, ShortStoryContainerActivity::class.java)
                    intent.putExtra(Constants.ARTICLE_ID, milestonesList?.get(position)?.content_id)
                    intent.putExtra(Constants.AUTHOR_ID, milestonesList?.get(position)?.user_id)
                    intent.putExtra(Constants.FROM_SCREEN, "MilestonesFragment")
                    intent.putExtra(Constants.AUTHOR, milestonesList?.get(position)?.user_id + "~")
                    startActivity(intent)
                }
            }
            milestonesList?.get(position)?.item_type == AppConstants.CONTENT_TYPE_VIDEO -> {
                activity?.let {
                    val intent = Intent(it, ParallelFeedActivity::class.java)
                    intent.putExtra(Constants.VIDEO_ID, milestonesList?.get(position)?.content_id)
                    intent.putExtra(Constants.FROM_SCREEN, "MilestonesFragment")
                    startActivity(intent)
                }
            }
            else -> {

            }
        }
    }

    override fun onClick(v: View) {

    }

    override fun updateUi(response: Response) {

    }
}
