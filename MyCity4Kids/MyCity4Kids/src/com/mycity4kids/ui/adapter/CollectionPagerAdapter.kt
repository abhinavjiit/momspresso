package com.mycity4kids.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mycity4kids.ui.fragment.UserCreatedCollectionsFragment
import com.mycity4kids.ui.fragment.UserFollowedCollectionsFragment

class CollectionPagerAdapter(val fragmentManager: FragmentManager, var isPrivate: Boolean) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return if (isPrivate)
            2
        else
            1

    }

    override fun getItem(position: Int): Fragment {

        return if (isPrivate) {
            if (position == 0)
                UserCreatedCollectionsFragment()
            else
                UserFollowedCollectionsFragment()

        } else
            UserCreatedCollectionsFragment()
    }


}