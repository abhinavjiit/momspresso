package com.mycity4kids

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mycity4kids.ui.fragment.TopicsShortStoriesTabFragment

class ShortStoryDetailPagerAdapter(fm: FragmentManager, val challengeId: String) :
    FragmentStatePagerAdapter(
        fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            val shortStoryChallengeListingFragment = TopicsShortStoriesTabFragment()
            val bundle = Bundle()
            bundle.putString("shortStoryChallengeId", challengeId)
            bundle.putString("position", position.toString())
            shortStoryChallengeListingFragment.arguments = bundle
            return shortStoryChallengeListingFragment
        } else {
            val shortStoryChallengeListingFragment = TopicsShortStoriesTabFragment()
            val bundle = Bundle()
            bundle.putString("shortStoryChallengeId", challengeId)
            bundle.putString("position", position.toString())
            shortStoryChallengeListingFragment.arguments = bundle
            return shortStoryChallengeListingFragment
        }
    }

    override fun getCount(): Int {
        return 2
    }
}
