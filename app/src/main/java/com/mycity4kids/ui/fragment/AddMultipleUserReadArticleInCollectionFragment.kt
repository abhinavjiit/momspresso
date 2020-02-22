package com.mycity4kids.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.ArticleListingResponse
import com.mycity4kids.models.response.ArticleListingResult
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI
import com.mycity4kids.ui.activity.collection.AddMultipleCollectionItemActivity
import com.mycity4kids.ui.adapter.AddMultipleCollectionAdapter
import com.mycity4kids.utils.ConnectivityUtils
import retrofit2.Call
import retrofit2.Callback

class AddMultipleUserReadArticleInCollectionFragment : BaseFragment(), AddMultipleCollectionAdapter.RecyclerViewClick, View.OnClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomLoadingView: RelativeLayout
    private lateinit var noBlogsTextView: TextView
    private lateinit var shimmer1: ShimmerFrameLayout
    private lateinit var addMultipleCollectionAdapter: AddMultipleCollectionAdapter
    private var chunk = 0
    private var nextPageNumber = 0
    private var isReuqestRunning = false
    private var isLastPageReached = true
    private lateinit var articleDataModelsNew: ArrayList<ArticleListingResult>
    private var multipleCollectionList = ArrayList<ArticleListingResult>()
    private var pastVisiblesItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.add_mutilple_read_articles_fragment, container, false)
        recyclerView = rootView.findViewById(R.id.recyclerView)
        bottomLoadingView = rootView.findViewById(R.id.bottomLoadingView)
        noBlogsTextView = rootView.findViewById(R.id.noBlogsTextView)
        shimmer1 = rootView.findViewById(R.id.shimmer1)
        rootView.findViewById<View>(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotate_indefinitely))
        articleDataModelsNew = ArrayList()
        getReadArticles()
        val llm = LinearLayoutManager(activity)
        llm.orientation = RecyclerView.VERTICAL
        recyclerView.layoutManager = llm
        addMultipleCollectionAdapter = AddMultipleCollectionAdapter(this, viewType = "READ")
        recyclerView.adapter = addMultipleCollectionAdapter
        addMultipleCollectionAdapter.setUserReadListData(articleDataModelsNew)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = llm.childCount
                    totalItemCount = llm.itemCount
                    pastVisiblesItems = llm.findFirstVisibleItemPosition()

                    if (!isReuqestRunning) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            isReuqestRunning = true
                            bottomLoadingView.visibility = View.VISIBLE
                            getReadArticles()
                        }
                    }
                }
            }
        })
        return rootView
    }

    override fun onClick(v: View?) {

    }

    override fun onclick(position: Int) {
        articleDataModelsNew[position].isCollectionItemSelected = !articleDataModelsNew[position].isCollectionItemSelected
        addMultipleCollectionAdapter.notifyDataSetChanged()
        multipleCollectionList.clear()
        for (i in 0 until articleDataModelsNew.size) {
            if (articleDataModelsNew[i].isCollectionItemSelected) {
                val dataList = ArrayList<ArticleListingResult>()
                dataList.add(articleDataModelsNew[i])
                multipleCollectionList.addAll(dataList)
            }
        }
        (activity as AddMultipleCollectionItemActivity).getUserReadList(multipleCollectionList)
    }

    private fun getReadArticles() {
        if (isAdded) {
            if (!ConnectivityUtils.isNetworkEnabled(activity)) {
                (activity as UserProfileActivity).showToast(getString(R.string.connectivity_unavailable))
                return
            }
        }

        val retro = BaseApplication.getInstance().retrofit
        val userpublishedArticlesAPI = retro.create(BloggerDashboardAPI::class.java)
        val call = userpublishedArticlesAPI.getAuthorsReadArticles(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, 10, chunk, "articles")
        call.enqueue(object : Callback<ArticleListingResponse> {
            override fun onFailure(call: Call<ArticleListingResponse>, t: Throwable) {
                Crashlytics.logException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }

            override fun onResponse(call: Call<ArticleListingResponse>, response: retrofit2.Response<ArticleListingResponse>) {
                shimmer1.stopShimmerAnimation()
                shimmer1.visibility = View.GONE
                bottomLoadingView.visibility = View.GONE
                if (response.body() == null) {
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {

                        chunk = Integer.parseInt(responseData.data[0].chunks)
                        processPublisedArticlesResponse(responseData)

                    } else {

                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
        })
    }

    private fun processPublisedArticlesResponse(responseData: ArticleListingResponse) {
        val dataList = responseData.data[0].result
        if (dataList.size == 0) {
            isLastPageReached = false
            if (!articleDataModelsNew.isNullOrEmpty()) {
                //No more next results for search from pagination
            } else {
                // No results
                articleDataModelsNew.addAll(dataList)
                addMultipleCollectionAdapter.setUserReadListData(articleDataModelsNew)
                addMultipleCollectionAdapter.notifyDataSetChanged()
                noBlogsTextView.visibility = View.VISIBLE
            }
        } else {
            if (nextPageNumber == 1) {
                articleDataModelsNew.addAll(dataList)
                addMultipleCollectionAdapter.setUserReadListData(articleDataModelsNew)
                addMultipleCollectionAdapter.notifyDataSetChanged()
            } else {
                articleDataModelsNew.addAll(dataList)
            }
            addMultipleCollectionAdapter.setUserReadListData(articleDataModelsNew)
            nextPageNumber += 1
            addMultipleCollectionAdapter.notifyDataSetChanged()
        }
    }

    override fun onStart() {
        super.onStart()
        shimmer1.startShimmerAnimation()
    }

    override fun onStop() {
        super.onStop()
        shimmer1.stopShimmerAnimation()
    }
}
