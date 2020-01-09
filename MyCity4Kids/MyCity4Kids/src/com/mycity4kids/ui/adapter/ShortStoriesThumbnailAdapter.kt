package com.mycity4kids.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mycity4kids.models.response.ShortShortiesBackgroundThumbnail
import com.mycity4kids.ui.fragment.ShortStoryBgBaseFragment
import com.mycity4kids.ui.fragment.ShortStoryTextFormatFragment

class ShortStoriesThumbnailAdapter(val fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var thumbnailList: ShortShortiesBackgroundThumbnail? = null

    override fun getCount(): Int {
        return 2
    }

    fun setListData(listData: ShortShortiesBackgroundThumbnail) {
        thumbnailList = listData
    }

    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            val shortStoryBgBaseFragment = ShortStoryBgBaseFragment()
            val bundle = Bundle()
            bundle.putParcelable("thumbnailList", thumbnailList)
            shortStoryBgBaseFragment.arguments = bundle
            return shortStoryBgBaseFragment
        } else {
            return ShortStoryTextFormatFragment()
        }

    }
}