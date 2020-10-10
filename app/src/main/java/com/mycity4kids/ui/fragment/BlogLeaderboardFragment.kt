package com.mycity4kids.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.LeaderboardDataResponse
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.ui.activity.ViewAllLeaderboardActivity
import com.mycity4kids.ui.adapter.BlogLeaderboardRecyclerAdapter


class BlogLeaderboardFragment : BaseFragment(),
    BlogLeaderboardRecyclerAdapter.RecyclerViewClickListener {
    private var recyclerView: RecyclerView? = null
    private var recyclerAdapterBlog: BlogLeaderboardRecyclerAdapter? = null
    private lateinit var viewMoreTextView: TextView
    private lateinit var emptyLeaderboardTextView: TextView
    private var llm: LinearLayoutManager? = null
    private var blogList: ArrayList<LeaderboardDataResponse.LeaderboardData.LeaderBoradRank>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.blog_vlog_leaderboard_fragment, container, false)
        blogList = arguments!!.getParcelableArrayList("blogList")
        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        viewMoreTextView = view.findViewById(R.id.viewMoreTextView)
        emptyLeaderboardTextView = view.findViewById(R.id.emptyLeaderboardTextView)

        if (blogList == null || blogList!!.isEmpty() || blogList?.get(0) == null || blogList?.get(0)!!.ranks == null || blogList?.get(
                0
            )!!.ranks.isEmpty()) {
            viewMoreTextView.visibility = View.GONE
            emptyLeaderboardTextView.visibility = View.VISIBLE
        }

        recyclerAdapterBlog = BlogLeaderboardRecyclerAdapter(this)
        llm = LinearLayoutManager(activity)
        llm!!.orientation = RecyclerView.VERTICAL
        recyclerView!!.layoutManager = llm
        recyclerAdapterBlog!!.setListData(blogList?.get(0))
        recyclerView!!.adapter = recyclerAdapterBlog

        viewMoreTextView.setOnClickListener {
            val intent = Intent(activity, ViewAllLeaderboardActivity::class.java)
            intent.putExtra("tab", "blogs")
            startActivity(intent)
        }
        return view
    }

    override fun onRecyclerViewItemClick(view: View?, position: Int) {
        val userProfileIntent = Intent(
            activity,
            UserProfileActivity::class.java
        )
        userProfileIntent.putExtra(
            Constants.USER_ID,
            blogList?.get(0)?.ranks?.get(position)?.user_id
        )
        startActivity(userProfileIntent)
    }
}
