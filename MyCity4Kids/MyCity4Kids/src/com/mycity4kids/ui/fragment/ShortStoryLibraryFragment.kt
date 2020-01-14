package com.mycity4kids.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.R
import com.mycity4kids.models.response.Categories
import com.mycity4kids.models.response.ShortShortiesBackgroundThumbnail
import com.mycity4kids.ui.activity.ShortStoriesCardActivity
import com.mycity4kids.ui.adapter.ShortStoriesLibraryAdapter
import com.mycity4kids.ui.videochallengenewui.ExpandableHeightGridView

class ShortStoryLibraryFragment : BaseFragment() {

    private lateinit var collectionGridView: ExpandableHeightGridView
    private lateinit var shortShortiesAdapter: ShortStoriesLibraryAdapter
    private var arrayList: ShortShortiesBackgroundThumbnail? = null
    private var categoriesList: ArrayList<Categories>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.short_story_thumbnail_list_fragment, container, false)
        collectionGridView = view.findViewById(R.id.collectionGridView)
        val arguments = arguments
//        arrayList = arguments?.getParcelable<ShortShortiesBackgroundThumbnail>("thumbnailList")
        categoriesList = arguments?.getParcelableArrayList("categoriesList")
        context?.run {
            shortShortiesAdapter = ShortStoriesLibraryAdapter(context!!, true)
            collectionGridView.adapter = shortShortiesAdapter
        }
        categoriesList?.let { shortShortiesAdapter.getLibraryColletions(it) }
        shortShortiesAdapter.notifyDataSetChanged()

        collectionGridView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            activity?.supportFragmentManager?.popBackStack()
            (context as ShortStoriesCardActivity).setEnabledDisabled(false)
            (context as ShortStoriesCardActivity).setImageListToNull()
            (context as ShortStoriesCardActivity).getImages("category-f1a5dcea3d884bd8b75e0da8fb1763d3", 1)
        }

        return view
    }

    override fun updateUi(response: Response?) {

    }


}