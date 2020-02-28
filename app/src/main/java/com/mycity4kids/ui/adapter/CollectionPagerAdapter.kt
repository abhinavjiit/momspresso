package com.mycity4kids.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mycity4kids.ui.fragment.UserCreatedCollectionsFragment
import com.mycity4kids.ui.fragment.UserFollowedCollectionsFragment

class CollectionPagerAdapter(val fragmentManager: FragmentManager, var isPrivate: Boolean, var userId: String) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return if (isPrivate)
            2
        else
            1
    }

    override fun getItem(position: Int): Fragment {

        if (isPrivate) {
            if (position == 0) {
                val userCreatedCollectionsFragment = UserCreatedCollectionsFragment()
                val bundle = Bundle()
                bundle.putString("userId", userId)
                userCreatedCollectionsFragment.arguments = bundle
                return userCreatedCollectionsFragment
            } else {
                return UserFollowedCollectionsFragment()
            }
        } else {
            val userCreatedCollectionsFragment = UserCreatedCollectionsFragment()
            val bundle = Bundle()
            bundle.putString("userId", userId)
            userCreatedCollectionsFragment.arguments = bundle
            return userCreatedCollectionsFragment
        }
    }
}