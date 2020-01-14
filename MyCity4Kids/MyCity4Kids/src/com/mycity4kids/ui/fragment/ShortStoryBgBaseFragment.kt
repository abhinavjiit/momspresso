package com.mycity4kids.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.models.response.Categories
import com.mycity4kids.models.response.Images
import com.mycity4kids.models.response.ShortShortiesBackgroundThumbnail

class ShortStoryBgBaseFragment : BaseFragment() {
    private var arrayList: ShortShortiesBackgroundThumbnail? = null
    private var pageNumber: Int? = 0
    private var imageList: ArrayList<Images>? = null
    private var categoriesList: ArrayList<Categories>? = null
    private var count: Int? = 0
    override fun updateUi(response: Response?) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val arguments = arguments
        arrayList = arguments?.getParcelable<ShortShortiesBackgroundThumbnail>("thumbnailList")
        pageNumber = arguments?.getInt("pageNumber")
        count = arguments?.getInt("count")
        imageList = arguments?.getParcelableArrayList("imageList")
        categoriesList = arguments?.getParcelableArrayList("categoriesList")
        return inflater.inflate(R.layout.story_base_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shortStoryThumbnailListFragment = ShortStoryThumbnailListFragment()
        val bundle = Bundle()
//        bundle.putParcelable("thumbnailList", arrayList)
        bundle.putParcelableArrayList("imageList", imageList)
        bundle.putParcelableArrayList("categoriesList", categoriesList)
        pageNumber?.let { bundle.putInt("pageNumber", it) }
        count?.let { bundle.putInt("count", it) }
        shortStoryThumbnailListFragment.arguments = bundle

        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, shortStoryThumbnailListFragment)
        transaction?.commit()
    }
}