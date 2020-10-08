package com.mycity4kids.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.models.response.LeaderboardDataResponse
import com.mycity4kids.ui.activity.ViewAllLeaderboardActivity
import com.mycity4kids.ui.activity.ViewLeaderboardActivity
import com.mycity4kids.ui.adapter.BlogLeaderboardRecyclerAdapter


class BlogLeaderboardFragment : BaseFragment() {
    private var recyclerView: RecyclerView? = null
    private var recyclerAdapterBlog: BlogLeaderboardRecyclerAdapter? = null
    private lateinit var viewMoreTextView: TextView
    private var llm: LinearLayoutManager? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.blog_vlog_leaderboard_fragment, container, false)
        val blogList: ArrayList<LeaderboardDataResponse.LeaderboardData.LeaderBoradRank>? = arguments!!.getParcelableArrayList("blogList")
        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        viewMoreTextView = view.findViewById(R.id.viewMoreTextView)

        if (blogList == null){
            viewMoreTextView.visibility = View.GONE
        }

        recyclerAdapterBlog =
            BlogLeaderboardRecyclerAdapter(activity)
        llm = LinearLayoutManager(activity)
        llm!!.setOrientation(RecyclerView.VERTICAL)
        recyclerView!!.layoutManager = llm
        recyclerAdapterBlog!!.setListData(blogList?.get(0))
        recyclerView!!.adapter = recyclerAdapterBlog

        viewMoreTextView.setOnClickListener {
            val intent = Intent(activity, ViewAllLeaderboardActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
