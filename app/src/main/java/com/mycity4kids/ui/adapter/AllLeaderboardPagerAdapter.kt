package com.mycity4kids.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mycity4kids.models.response.AllLeaderboardDataResponse
import com.mycity4kids.ui.fragment.AllBlogLeaderboardFragment
import com.mycity4kids.ui.fragment.AllVlogLeaderboardFragment
import java.util.ArrayList

class AllLeaderboardPagerAdapter(
    private var allBlogList: ArrayList<AllLeaderboardDataResponse.AllLeaderboardData.AllLeaderboardRankHolder>,
    private var allVlogList: ArrayList<AllLeaderboardDataResponse.AllLeaderboardData.AllLeaderboardRankHolder>,
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private var allBlogLeaderboardFragment: AllBlogLeaderboardFragment? = null

    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            if (allBlogLeaderboardFragment == null) {
                allBlogLeaderboardFragment = AllBlogLeaderboardFragment()
            }
            val bundle = Bundle()
            bundle.putParcelableArrayList("blogList", allBlogList)
            allBlogLeaderboardFragment!!.arguments = bundle
            return allBlogLeaderboardFragment as AllBlogLeaderboardFragment
        } else {
            val allVlogLeaderboardFragment = AllVlogLeaderboardFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList("vlogList", allVlogList)
            allVlogLeaderboardFragment.arguments = bundle
            return allVlogLeaderboardFragment
        }
    }

    override fun getCount(): Int {
        return 2
    }
}
