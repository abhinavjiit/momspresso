package com.mycity4kids.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mycity4kids.ui.fragment.SelectBlogTopicsFragment
import com.mycity4kids.ui.fragment.SelectStoryTopicsFragment
import com.mycity4kids.ui.fragment.SelectVlogTopicsFragment

class SelectContentTopicsPagerAdapter(
    fm: FragmentManager,
    private val selectedContent: ArrayList<String>?
) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
      return  selectedContent?.size!!
    }

    override fun getItem(position: Int): Fragment {
        return when {
            selectedContent?.get(position) == "blog" -> {
                SelectBlogTopicsFragment()
            }
            selectedContent?.get(position) == "vlog" -> {
                SelectVlogTopicsFragment()
            }
            else -> {
                SelectStoryTopicsFragment()
            }
        }
    }
}