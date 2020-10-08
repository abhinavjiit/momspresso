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
import com.mycity4kids.ui.adapter.AllBlogLeaderboardRecyclerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AllBlogLeaderboardFragment : BaseFragment() {
    private var recyclerView: RecyclerView? = null
    private var recyclerAdapterBlog: AllBlogLeaderboardRecyclerAdapter? = null
    private lateinit var viewMoreTextView: TextView
    private var llm: LinearLayoutManager? = null
    private var blogList: ArrayList<AllLeaderboardDataResponse.AllLeaderboardData.AllLeaderboardRankHolder>? =
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
        blogList = arguments!!.getParcelableArrayList("blogList")
        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        viewMoreTextView = view.findViewById(R.id.viewMoreTextView)

        viewMoreTextView.visibility = View.GONE
        recyclerAdapterBlog =
            AllBlogLeaderboardRecyclerAdapter(activity)
        llm = LinearLayoutManager(activity)
        llm!!.setOrientation(RecyclerView.VERTICAL)
        recyclerView!!.layoutManager = llm
        recyclerAdapterBlog!!.setListData(blogList)
        recyclerView!!.adapter = recyclerAdapterBlog

        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                nextPageNumber = nextPageNumber + 1
                visibleItemCount = llm!!.childCount
                totalItemCount = llm!!.itemCount
                pastVisiblesItems = llm!!.findFirstVisibleItemPosition()

                if (!isReuqestRunning) {
                    //                    if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                    isReuqestRunning = true
                    getALLBlogLeaderboardData(nextPageNumber)
                    //                    }
                }
            }
        })

        return view
    }

    private fun getALLBlogLeaderboardData(page: Int) {
        val retrofit = BaseApplication.getInstance().retrofit
        val bloggerGoldAPI = retrofit.create(BloggerGoldAPI::class.java)
        val call = bloggerGoldAPI.getAllLeaderboardData(
            page,
            0
        )
        call.enqueue(allBlogLeaderboardData)
    }

    private var allBlogLeaderboardData = object : Callback<AllLeaderboardDataResponse> {
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
                    blogList?.addAll(responseData.data.result)
                    recyclerAdapterBlog!!.setListData(blogList)
                    recyclerAdapterBlog!!.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4KException", Log.getStackTraceString(e))
            }
        }
    }

}
