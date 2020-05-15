package com.mycity4kids.ui.fragment

import android.accounts.NetworkErrorException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView.OnItemClickListener
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentTransaction
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
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
    private var mLodingView: RelativeLayout? = null
    private lateinit var shimmer1: ShimmerFrameLayout
    private var bottomLoadingView: RelativeLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.short_story_thumbnail_list_fragment, container, false)
        collectionGridView = view.findViewById(R.id.collectionGridView)
        bottomLoadingView = view.findViewById(R.id.bottomLoadingView)
        mLodingView = view.findViewById(R.id.relativeLoadingView)
        shimmer1 = view.findViewById(R.id.shimmer1)
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
                dataList?.get(position - 1)?.image_url?.let {
                    dataList?.get(position - 1)?.font_colour?.let { it1 ->
                        dataList?.get(
                            position - 1
                        )?.id?.let { it2 ->
                            (context as ShortStoriesCardActivity).setBackground(
                                it,
                                it1,
                                it2
                            )
                        }
                    }
                }
            }
        }

        collectionGridView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {}

            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                val loadMore = firstVisibleItem + visibleItemCount >= totalItemCount
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
                    bottomLoadingView?.visibility = View.VISIBLE
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

    override fun onStart() {
        super.onStart()
        shimmer1.startShimmerAnimation()
    }

    override fun onStop() {
        super.onStop()
        shimmer1.stopShimmerAnimation()
    }

    private val getThumbnailList = object : Callback<ShortStoryImageData> {
        override fun onResponse(
            call: Call<ShortStoryImageData>,
            response: retrofit2.Response<ShortStoryImageData>
        ) {
            //            removeProgressDialog()
            if (null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                FirebaseCrashlytics.getInstance().recordException(nee)
                return
            }
            try {
                shimmer1.stopShimmerAnimation()
                shimmer1.visibility = View.GONE
                bottomLoadingView?.visibility = View.GONE
                val responseData = response.body()!!.data?.result
                categoriesList = responseData?.categories
                count = responseData?.images?.count!!
                if (pageNumber == 1)
                    responseData?.images?.results?.get(0)?.image_url?.let {
                        responseData?.images?.results?.get(
                            0
                        )?.font_colour?.let { it1 ->
                            responseData?.images?.results?.get(0)?.id?.let { it2 ->
                                (context as ShortStoriesCardActivity).setBackground(
                                    it,
                                    it1,
                                    it2
                                )
                            }
                        }
                    }
                processResponse(responseData)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<ShortStoryImageData>, t: Throwable) {
            removeProgressDialog()
            FirebaseCrashlytics.getInstance().recordException(t)
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
}
