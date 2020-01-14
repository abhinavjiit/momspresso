package com.mycity4kids.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.FragmentTransaction
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.models.response.Categories
import com.mycity4kids.models.response.Images
import com.mycity4kids.models.response.ShortShortiesBackgroundThumbnail
import com.mycity4kids.ui.activity.ShortStoriesCardActivity
import com.mycity4kids.ui.adapter.ShortStoriesAdapter
import com.mycity4kids.ui.videochallengenewui.ExpandableHeightGridView


class ShortStoryThumbnailListFragment : BaseFragment() {


    private lateinit var collectionGridView: ExpandableHeightGridView
    private lateinit var shortShortiesAdapter: ShortStoriesAdapter
    private var arrayList: ShortShortiesBackgroundThumbnail? = null
    private var pageNumber: Int = 0
    private var count: Int = 0
    private var isLastPageReached = false
    private var isReuqestRunning = false
    //    private var imageList: ArrayList<Images>? = null
    private var categoriesList: ArrayList<Categories>? = null
    private var dataList: ArrayList<Images>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.short_story_thumbnail_list_fragment, container, false)
        collectionGridView = view.findViewById(R.id.collectionGridView)
        val arguments = arguments
//        arrayList = arguments?.getParcelable<ShortShortiesBackgroundThumbnail>("thumbnailList")
        dataList = arguments?.getParcelableArrayList("imageList")
        categoriesList = arguments?.getParcelableArrayList("categoriesList")
        pageNumber = arguments?.getInt("pageNumber")!!
        count = arguments?.getInt("count")!!
        context?.run {
            shortShortiesAdapter = ShortStoriesAdapter(context!!, false)
            collectionGridView.adapter = shortShortiesAdapter
        }
//        processResponse(dataList)

        dataList?.let { shortShortiesAdapter.getUserColletions(it) }
        shortShortiesAdapter.notifyDataSetChanged()
        collectionGridView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            if (position == 0) {
                (context as ShortStoriesCardActivity).setEnabledDisabled(true)
                val shortStoryLibraryFragment = ShortStoryLibraryFragment()
                val bundle = Bundle()
                bundle.putParcelableArrayList("categoriesList", categoriesList)
//                bundle.putParcelable("thumbnailList", arrayList)
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
                dataList?.get(position - 1)?.image_url?.let { dataList?.get(position - 1)?.font_colour?.let { it1 -> dataList?.get(position - 1)?.id?.let { it2 -> (context as ShortStoriesCardActivity).setBackground(it, it1, it2) } } }
//                arrayList?.images!!.results?.get(position - 1)?.image_url?.let { arrayList?.images!!.results?.get(position - 1)?.font_colour?.let { it1 -> (context as ShortStoriesCardActivity).setBackground(it, it1) } }
//                arrayList?.images!!.results?.get(position - 1)?.image_url?.let { (context as ShortStoriesCardActivity).setBackground(it) }
            }
        }

        /*collectionGridView.addOnScrollListener(object : EndlessScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                fetchCampaignList(totalItemsCount, true)
            }
        })*/



        collectionGridView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {}

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                val loadMore = dataList?.size!! < count
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
//                    bottomLoadingView?.visibility = View.VISIBLE
                    (context as ShortStoriesCardActivity).getImages("category-f1a5dcea3d884bd8b75e0da8fb1763d3", pageNumber)
                    isReuqestRunning = true
                }
            }
        })

        return view
    }

    /*private fun processResponse(result: ShortShortiesBackgroundThumbnail?) {
        val newDatalist = result?.images?.results
        isReuqestRunning = false
        if (newDatalist?.size == 0) {
            isLastPageReached = true
            if (dataList.isNotEmpty()) {
            } else {
                dataList = newDatalist
                shortShortiesAdapter.getUserColletions(dataList)
                shortShortiesAdapter.notifyDataSetChanged()
            }
        } else {
            if (pageNumber == 0) {
                dataList = newDatalist!!
            } else {
                dataList.addAll(newDatalist!!)
            }

            shortShortiesAdapter.getUserColletions(dataList)
            pageNumber++
            shortShortiesAdapter.notifyDataSetChanged()
        }
    }
*/

    override fun updateUi(response: Response?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}