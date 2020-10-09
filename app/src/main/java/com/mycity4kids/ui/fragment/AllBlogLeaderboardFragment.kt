package com.mycity4kids.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.AllLeaderboardDataResponse
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.retrofitAPIsInterfaces.BloggerGoldAPI
import com.mycity4kids.ui.adapter.AllBlogLeaderboardRecyclerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AllBlogLeaderboardFragment : BaseFragment(),
    AllBlogLeaderboardRecyclerAdapter.RecyclerViewClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingView: RelativeLayout
    private lateinit var emptyLeaderboardTextView: TextView
    private lateinit var recyclerAdapterBlog: AllBlogLeaderboardRecyclerAdapter
    private lateinit var blogList: ArrayList<AllLeaderboardDataResponse.AllLeaderboardData.AllLeaderboardRankHolder>
    private var nextPageNumber = 0
    private var isRequestRunning = true
    private var pastVisiblesItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.all_blog_vlog_leaderboard_fragment, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        loadingView = view.findViewById(R.id.relativeLoadingView)
        emptyLeaderboardTextView = view.findViewById(R.id.emptyLeaderboardTextView)
        recyclerAdapterBlog = AllBlogLeaderboardRecyclerAdapter(this)
        val llm = LinearLayoutManager(activity)
        llm.orientation = RecyclerView.VERTICAL
        recyclerView.layoutManager = llm
        blogList = ArrayList()
        recyclerAdapterBlog.setListData(blogList)
        recyclerView.adapter = recyclerAdapterBlog

        getALLBlogLeaderboardData(nextPageNumber)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = llm.childCount
                    totalItemCount = llm.itemCount
                    pastVisiblesItems = llm.findFirstVisibleItemPosition()
                    if (!isRequestRunning) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            isRequestRunning = true
                            loadingView.visibility = View.VISIBLE
                            getALLBlogLeaderboardData(nextPageNumber)
                        }
                    }
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
            isRequestRunning = false
            loadingView.visibility = View.GONE
            emptyLeaderboardTextView.visibility = View.VISIBLE
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4KException", Log.getStackTraceString(t))
        }

        override fun onResponse(
            call: Call<AllLeaderboardDataResponse>,
            response: Response<AllLeaderboardDataResponse>
        ) {
            loadingView.visibility = View.GONE
            isRequestRunning = false
            if (response.body() == null) {
                return
            }
            try {
                val responseData = response.body()
                if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {
                    if (nextPageNumber == 0 && (responseData.data.result == null || responseData.data.result.isEmpty())) {
                        emptyLeaderboardTextView.visibility = View.VISIBLE
                        return
                    }
                    nextPageNumber++
                    blogList.addAll(responseData.data.result)
                    recyclerAdapterBlog.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                isRequestRunning = false
                emptyLeaderboardTextView.visibility = View.VISIBLE
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4KException", Log.getStackTraceString(e))
            }
        }
    }

    override fun onRecyclerViewItemClick(view: View?, position: Int) {
        val userProfileIntent = Intent(
            activity,
            UserProfileActivity::class.java
        )
        userProfileIntent.putExtra(
            Constants.USER_ID,
            blogList.get(position).user_id
        )
        startActivity(userProfileIntent)
    }

}
