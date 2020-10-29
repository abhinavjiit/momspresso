package com.mycity4kids.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mycity4kids.ui.fragment.SelectOrAddBlogTopicsFragment
import com.mycity4kids.ui.fragment.SelectOrAddStoryTopicsFragment
import com.mycity4kids.ui.fragment.SelectOrAddVlogTopicsFragment

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
                SelectOrAddBlogTopicsFragment()
            }
            selectedContent?.get(position) == "vlog" -> {
                SelectOrAddVlogTopicsFragment()
            }
            else -> {
                SelectOrAddStoryTopicsFragment()
            }
        }
    }
}