package com.mycity4kids.ui.fragment

import android.accounts.NetworkErrorException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.BuildConfig
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.profile.MilestonesDialogFragment
import com.mycity4kids.profile.MilestonesResponse
import com.mycity4kids.profile.MilestonesResult
import com.mycity4kids.retrofitAPIsInterfaces.MilestonesAPI
import com.mycity4kids.ui.adapter.MilestonesListAdapter
import com.mycity4kids.utils.ToastUtils
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
    private var page: Int = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_milestones, container, false)
        recyclerView = rootView?.findViewById(R.id.recyclerView)
        progressBar = rootView?.findViewById(R.id.progressBar)

        if (BuildConfig.DEBUG) {
            userId = "3021794b38254a48bb5b6ffd3a0e62e2"
        } else {
            userId = arguments?.getString(Constants.AUTHOR_ID)
        }
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
                    FirebaseCrashlytics.getInstance().recordException(nee)
                    return
                }
                val responseData = response.body()
                if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {
                    processMilestoneResponse(responseData.data?.result)
                    if (BuildConfig.DEBUG && page == 0) {
                        page++
                        getUsersMilestones("0a80bbe4e193424e8fd555adc14a2155")
                    }
                } else {
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<MilestonesResponse>, e: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    private fun processMilestoneResponse(result: List<MilestonesResult>?) {
        result?.let {
            for (element in result) {
                if (element.item_type == AppConstants.CONTENT_TYPE_ARTICLE ||
                        element.item_type == AppConstants.CONTENT_TYPE_SHORT_STORY ||
                        element.item_type == AppConstants.CONTENT_TYPE_VIDEO ||
                        element.item_type == AppConstants.CONTENT_TYPE_MYMONEY)
                    milestonesList?.add(element)
            }
            milestonesAdapter?.notifyDataSetChanged()
        }
    }

    override fun onClick(view: View, position: Int) {
        val milestonesDialogFragment = MilestonesDialogFragment()
        val bundle = Bundle()
        if (BuildConfig.DEBUG) {
            if (position == 0) {
                bundle.putString(Constants.USER_ID, userId)
            } else {
                bundle.putString(Constants.USER_ID, "0a80bbe4e193424e8fd555adc14a2155")
            }
        } else {
            bundle.putString(Constants.USER_ID, userId)
        }
        bundle.putString("id", milestonesList?.get(position)?.id)
        milestonesDialogFragment.arguments = bundle
        fragmentManager?.let {
            milestonesDialogFragment.show(it, "MilestonesDialogFragment")
        }
    }

    override fun onClick(v: View) {
    }
}
