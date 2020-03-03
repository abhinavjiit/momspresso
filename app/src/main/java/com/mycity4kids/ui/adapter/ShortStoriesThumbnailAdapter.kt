package com.mycity4kids.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mycity4kids.ui.fragment.ShortStoryBgBaseFragment
import com.mycity4kids.ui.fragment.ShortStoryTextFormatFragment

class ShortStoriesThumbnailAdapter(val fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            val shortStoryBgBaseFragment = ShortStoryBgBaseFragment()
            return shortStoryBgBaseFragment
        } else {
            return ShortStoryTextFormatFragment()
        }
    }
}
