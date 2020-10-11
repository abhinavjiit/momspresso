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
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.response.LeaderboardDataResponse
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.ui.activity.ViewAllLeaderboardActivity
import com.mycity4kids.ui.adapter.VlogLeaderboardRecyclerAdapter

class VlogLeaderboardFragment : BaseFragment(),
    VlogLeaderboardRecyclerAdapter.RecyclerViewClickListener {
    private var recyclerView: RecyclerView? = null
    private var recyclerAdapterBlog: VlogLeaderboardRecyclerAdapter? = null
    private var vlogList: ArrayList<LeaderboardDataResponse.LeaderboardData.LeaderBoradRank>? = null
    private lateinit var viewMoreTextView: TextView
    private lateinit var emptyLeaderboardTextView: TextView
    private var llm: LinearLayoutManager? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.blog_vlog_leaderboard_fragment, container, false)
        vlogList = arguments!!.getParcelableArrayList("vlogList")

        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        viewMoreTextView = view.findViewById(R.id.viewMoreTextView)
        emptyLeaderboardTextView = view.findViewById(R.id.emptyLeaderboardTextView)

        if (vlogList == null || vlogList!!.isEmpty() || vlogList?.get(0) == null || vlogList?.get(0)!!.ranks == null || vlogList?.get(
                0
            )!!.ranks.isEmpty()) {
            viewMoreTextView.visibility = View.GONE
            emptyLeaderboardTextView.visibility = View.VISIBLE
        }
        recyclerAdapterBlog = VlogLeaderboardRecyclerAdapter(this)
        llm = LinearLayoutManager(activity)
        llm!!.setOrientation(RecyclerView.VERTICAL)
        recyclerView!!.layoutManager = llm
        recyclerAdapterBlog!!.setListData(vlogList?.get(1))
        recyclerView!!.adapter = recyclerAdapterBlog
        viewMoreTextView.setOnClickListener {
            Utils.shareEventTracking(
                activity,
                "BB Program Page",
                "BirthdayBonanza_Android",
                "LB_ViewMore_BB"
            )
            val intent = Intent(activity, ViewAllLeaderboardActivity::class.java)
            intent.putExtra("tab", "vlogs")
            startActivity(intent)
        }

        return view
    }

    override fun onRecyclerViewItemClick(view: View?, position: Int) {
        Utils.shareEventTracking(
            activity,
            "BB Program Page",
            "BirthdayBonanza_Android",
            "LB_ProfileClick_BB"
        )
        val userProfileIntent = Intent(
            activity,
            UserProfileActivity::class.java
        )
        userProfileIntent.putExtra(
            Constants.USER_ID,
            vlogList?.get(1)?.ranks?.get(position)?.user_id
        )
        startActivity(userProfileIntent)
    }
}
