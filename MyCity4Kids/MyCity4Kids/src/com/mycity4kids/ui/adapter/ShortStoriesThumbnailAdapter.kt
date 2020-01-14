package com.mycity4kids.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mycity4kids.models.response.Categories
import com.mycity4kids.models.response.Images
import com.mycity4kids.models.response.ShortShortiesBackgroundThumbnail
import com.mycity4kids.ui.fragment.ShortStoryBgBaseFragment
import com.mycity4kids.ui.fragment.ShortStoryTextFormatFragment

class ShortStoriesThumbnailAdapter(val fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var thumbnailList: ShortShortiesBackgroundThumbnail? = null
    private var imageList: ArrayList<Images>? = null
    private var categoriesList: ArrayList<Categories>? = null
    private var pageNumber: Int = 0
    private var count: Int = 0

    override fun getCount(): Int {
        return 2
    }

    fun setListData(imagesList: ArrayList<Images>, categoryList: ArrayList<Categories>, page: Int, listCount: Int) {
//        thumbnailList = listData
        imageList = imagesList
        categoriesList = categoryList
        pageNumber = page
        count = listCount
    }

    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            val shortStoryBgBaseFragment = ShortStoryBgBaseFragment()
            val bundle = Bundle()
            bundle.putInt("pageNumber", pageNumber)
            bundle.putInt("count", count)
            bundle.putParcelableArrayList("imageList", imageList)
            bundle.putParcelableArrayList("categoriesList", categoriesList)
//            bundle.putParcelable("thumbnailList", thumbnailList)
            shortStoryBgBaseFragment.arguments = bundle
            return shortStoryBgBaseFragment
        } else {
            return ShortStoryTextFormatFragment()
        }

    }
}