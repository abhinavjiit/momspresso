package com.mycity4kids.ui.momspressotv

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MomspressoTelevisionPagerAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            val fragment: Fragment = MomspressoTelevisionLiveAndUpcomingTabFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        } else {
            val fragment: Fragment = MomspressoTelevisionLibraryTabFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getCount(): Int {
        return 2
    }
}
