package com.mycity4kids.ui.fragment

import android.accounts.NetworkErrorException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.FragmentTransaction
import com.crashlytics.android.Crashlytics
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.response.Categories
import com.mycity4kids.models.response.Images
import com.mycity4kids.models.response.ShortShortiesBackgroundThumbnail
import com.mycity4kids.models.response.ShortStoryImageData
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import com.mycity4kids.ui.activity.ShortStoriesCardActivity
import com.mycity4kids.ui.adapter.ShortStoriesAdapter
import com.mycity4kids.ui.videochallengenewui.ExpandableHeightGridView
import retrofit2.Call
import retrofit2.Callback


class ShortStoryThumbnailListFragment : BaseFragment() {


    private lateinit var collectionGridView: ExpandableHeightGridView
    private lateinit var shortShortiesAdapter: ShortStoriesAdapter
    private var arrayList: ShortShortiesBackgroundThumbnail? = null
    private var pageNumber: Int = 0
    private var count: Int = 0
    private var isLastPageReached = false
    private var isReuqestRunning = false
    private var categoriesList: ArrayList<Categories>? = null
    private var dataList = ArrayList<Images>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.short_story_thumbnail_list_fragment, container, false)
        collectionGridView = view.findViewById(R.id.collectionGridView)
        dataList.clear()
        getImages((context as ShortStoriesCardActivity).getCategoryId(), 1)
        context?.run {
            shortShortiesAdapter = ShortStoriesAdapter(context!!, false)
            collectionGridView.adapter = shortShortiesAdapter
        }
        collectionGridView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            if (position == 0) {
                (context as ShortStoriesCardActivity).setEnabledDisabled(true)
                val shortStoryLibraryFragment = ShortStoryLibraryFragment()
                val bundle = Bundle()
                bundle.putParcelableArrayList("categoriesList", categoriesList)
                shortStoryLibraryFragment.arguments = bundle
                (context as ShortStoriesCardActivity).currentFragment(shortStoryLibraryFragment)

                val trans = fragmentManager?.beginTransaction()
                trans?.replace(R.id.container, shortStoryLibraryFragment)
                trans?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                trans?.addToBackStack("ShortStoryThumbnailListFragment")
                trans?.commit()
            } else {
                (context as ShortStoriesCardActivity).setEnabledDisabled(false)
                (context as ShortStoriesCardActivity).currentFragment(this)
                dataList?.get(position - 1)?.image_url?.let { dataList?.get(position - 1)?.font_colour?.let { it1 -> dataList?.get(position - 1)?.id?.let { it2 -> (context as ShortStoriesCardActivity).setBackground(it, it1, it2) } } }
            }
        }

        collectionGridView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {}

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                val loadMore = firstVisibleItem + visibleItemCount >= totalItemCount
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
                    getImages((context as ShortStoriesCardActivity).getCategoryId(), pageNumber)
                    isReuqestRunning = true
                }
            }
        })

        return view
    }

    fun getImages(categoryId: String, pageValue: Int) {
        pageNumber = pageValue
        val retro = BaseApplication.getInstance().retrofit
        val rewardAPI = retro.create(RewardsAPI::class.java)
        val call = rewardAPI.getBackgroundThumbnail(categoryId, pageValue)
        call.enqueue(getThumbnailList)
    }


    private val getThumbnailList = object : Callback<ShortStoryImageData> {
        override fun onResponse(call: Call<ShortStoryImageData>, response: retrofit2.Response<ShortStoryImageData>) {
            if (null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                Crashlytics.logException(nee)
                return
            }
            try {
                val responseData = response.body()!!.data?.result

                categoriesList = responseData?.categories
                count = responseData?.images?.count!!
                if (pageNumber == 1)
                    responseData?.images?.results?.get(0)?.image_url?.let { responseData?.images?.results?.get(0)?.font_colour?.let { it1 -> responseData?.images?.results?.get(0)?.id?.let { it2 -> (context as ShortStoriesCardActivity).setBackground(it, it1, it2) } } }
                processResponse(responseData)
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<ShortStoryImageData>, t: Throwable) {
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    private fun processResponse(responseData: ShortShortiesBackgroundThumbnail?) {
        count = responseData?.images?.count!!
        val newDatalist = responseData?.images?.results
        isReuqestRunning = false
        if (newDatalist?.size == 0) {
            isLastPageReached = true
            if (dataList.isNotEmpty()) {
            } else {
                dataList = newDatalist
                dataList.let { shortShortiesAdapter.getUserColletions(it) }
                shortShortiesAdapter.notifyDataSetChanged()
            }
        } else {
            if (pageNumber == 1) {
                dataList = newDatalist!!
            } else {
                dataList.addAll(newDatalist!!)
            }
            pageNumber++
            dataList.let { shortShortiesAdapter.getUserColletions(it) }
            shortShortiesAdapter.notifyDataSetChanged()
        }
    }

    override fun updateUi(response: Response?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}