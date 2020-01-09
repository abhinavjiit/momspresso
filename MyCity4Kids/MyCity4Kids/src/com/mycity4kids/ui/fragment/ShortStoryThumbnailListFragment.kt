package com.mycity4kids.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.FragmentTransaction
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.models.response.Images
import com.mycity4kids.models.response.ShortShortiesBackgroundThumbnail
import com.mycity4kids.ui.activity.ShortStoriesCardActivity
import com.mycity4kids.ui.adapter.ShortStoriesAdapter
import com.mycity4kids.ui.videochallengenewui.ExpandableHeightGridView


class ShortStoryThumbnailListFragment : BaseFragment() {


    private lateinit var collectionGridView: ExpandableHeightGridView
    private lateinit var shortShortiesAdapter: ShortStoriesAdapter
    private var arrayList: ShortShortiesBackgroundThumbnail? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.short_story_thumbnail_list_fragment, container, false)
        collectionGridView = view.findViewById(R.id.collectionGridView)
        val arguments = arguments
        arrayList = arguments?.getParcelable<ShortShortiesBackgroundThumbnail>("thumbnailList")
        context?.run {
            shortShortiesAdapter = ShortStoriesAdapter(context!!, false)
            collectionGridView.adapter = shortShortiesAdapter
        }
        shortShortiesAdapter.getUserColletions(arrayList?.images?.results as ArrayList<Images>)
        shortShortiesAdapter.notifyDataSetChanged()

        collectionGridView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            if (position == 0) {
                (context as ShortStoriesCardActivity).setEnabledDisabled(true)
                val shortStoryLibraryFragment = ShortStoryLibraryFragment()
                val bundle = Bundle()
                bundle.putParcelable("thumbnailList", arrayList)
                shortStoryLibraryFragment.arguments = bundle
                (context as ShortStoriesCardActivity).currentFragment(shortStoryLibraryFragment)

                val trans = fragmentManager?.beginTransaction()
                trans?.replace(R.id.container, shortStoryLibraryFragment)
                trans?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                trans?.addToBackStack(null)
                trans?.commit()
            } else {
                (context as ShortStoriesCardActivity).setEnabledDisabled(false)
                (context as ShortStoriesCardActivity).currentFragment(this)
                arrayList?.images!!.results?.get(position - 1)?.image_url?.let { arrayList?.images!!.results?.get(position - 1)?.font_colour?.let { it1 -> (context as ShortStoriesCardActivity).setBackground(it, it1) } }
//                arrayList?.images!!.results?.get(position - 1)?.image_url?.let { (context as ShortStoriesCardActivity).setBackground(it) }
            }
        }

        return view
    }


    override fun updateUi(response: Response?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}