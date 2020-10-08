package com.mycity4kids.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mycity4kids.models.response.LeaderboardDataResponse
import com.mycity4kids.ui.fragment.BlogLeaderboardFragment
import com.mycity4kids.ui.fragment.VlogLeaderboardFragment
import java.util.ArrayList

class LeaderboardPagerAdapter(
    private var responseList: ArrayList<LeaderboardDataResponse.LeaderboardData.LeaderBoradRank>,
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private var blogLeaderboardFragment: BlogLeaderboardFragment? = null

    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            if (blogLeaderboardFragment == null) {
                blogLeaderboardFragment = BlogLeaderboardFragment()
            }
            val bundle = Bundle()
            bundle.putParcelableArrayList("blogList", responseList)
//            bundle.putString("rules", rules)
            blogLeaderboardFragment!!.arguments = bundle
            return blogLeaderboardFragment as BlogLeaderboardFragment
        } else {
            val vlogLeaderboardFragment = VlogLeaderboardFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList("vlogList", responseList)
//            bundle.putString("articleChallengeId", challengeId)
//            bundle.putString("position", position.toString())
            vlogLeaderboardFragment.arguments = bundle
            return vlogLeaderboardFragment
        }
    }

    override fun getCount(): Int {
        return 2
    }
}
