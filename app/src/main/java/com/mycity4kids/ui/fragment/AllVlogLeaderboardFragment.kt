package com.mycity4kids.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.AllLeaderboardDataResponse
import com.mycity4kids.retrofitAPIsInterfaces.BloggerGoldAPI
import com.mycity4kids.ui.adapter.AllVlogLeaderboardRecyclerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllVlogLeaderboardFragment : BaseFragment() {
    private lateinit var viewMoreTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private var recyclerAdapterBlog: AllVlogLeaderboardRecyclerAdapter? = null
    private var llm: LinearLayoutManager? = null
    private var allVlogList: ArrayList<AllLeaderboardDataResponse.AllLeaderboardData.AllLeaderboardRankHolder>? =
        null

    private var nextPageNumber = 0
    private var isReuqestRunning = false
    private var isLastPageReached = true
    private var pastVisiblesItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.blog_vlog_leaderboard_fragment, container, false)


        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        viewMoreTextView = view.findViewById<TextView>(R.id.viewMoreTextView)
        viewMoreTextView.visibility = View.GONE
        recyclerAdapterBlog =
            AllVlogLeaderboardRecyclerAdapter(activity)
        llm = LinearLayoutManager(activity)
        llm!!.setOrientation(RecyclerView.VERTICAL)
        recyclerView.layoutManager = llm
        if (allVlogList == null) {
            allVlogList = arguments!!.getParcelableArrayList("vlogList")
            recyclerAdapterBlog!!.setListData(allVlogList)
        }
        recyclerView.adapter = recyclerAdapterBlog

        recyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                if (dy > 0) {
                    nextPageNumber = nextPageNumber + 1
                    visibleItemCount = llm!!.childCount
                    totalItemCount = llm!!.itemCount
                    pastVisiblesItems = llm!!.findFirstVisibleItemPosition()

                    if (!isReuqestRunning) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            if (recyclerView.adapter is AllVlogLeaderboardRecyclerAdapter) {
                                isReuqestRunning = true
                                getAllVlogLeaderboardData(nextPageNumber)
                            }
                        }
                    }
                }
            }
        })

        return view
    }

    private fun getAllVlogLeaderboardData(page: Int) {
        val retrofit = BaseApplication.getInstance().retrofit
        val bloggerGoldAPI = retrofit.create(BloggerGoldAPI::class.java)
        val call = bloggerGoldAPI.getAllLeaderboardData(
            page,
            2
        )
        call.enqueue(allVlogLeaderboardData)
    }

    private var allVlogLeaderboardData = object : Callback<AllLeaderboardDataResponse> {
        override fun onFailure(call: Call<AllLeaderboardDataResponse>, t: Throwable) {
            removeProgressDialog()
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4KException", Log.getStackTraceString(t))
        }

        override fun onResponse(
            call: Call<AllLeaderboardDataResponse>,
            response: Response<AllLeaderboardDataResponse>
        ) {
            if (response.body() == null) {
                return
            }
            try {
                val responseData = response.body()
                if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {
                    allVlogList?.addAll(responseData.data.result)
                    recyclerAdapterBlog!!.setListData(allVlogList)
                    recyclerAdapterBlog!!.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4KException", Log.getStackTraceString(e))
            }
        }
    }
}
