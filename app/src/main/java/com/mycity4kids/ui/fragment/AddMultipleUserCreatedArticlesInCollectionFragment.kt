package com.mycity4kids.ui.fragment

import android.accounts.NetworkErrorException
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
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.MixFeedResponse
import com.mycity4kids.models.response.MixFeedResult
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI
import com.mycity4kids.ui.activity.collection.AddMultipleCollectionItemActivity
import com.mycity4kids.ui.adapter.AddMultipleCollectionAdapter
import retrofit2.Call
import retrofit2.Callback

class AddMultipleUserCreatedArticlesInCollectionFragment : BaseFragment(),
    AddMultipleCollectionAdapter.RecyclerViewClick {

    override fun onclick(position: Int) {
        userContentList?.get(position)?.isCollectionItemSelected =
            !userContentList?.get(position)?.isCollectionItemSelected!!
        userContentAdapter.notifyDataSetChanged()
        multipleUserCreatedSelectedItemList.clear()
        userContentList?.let {
            for (i in 0 until it.size) {
                if (it[i].isCollectionItemSelected) {
                    val dataList = ArrayList<MixFeedResult>()
                    dataList.add(it[i])
                    multipleUserCreatedSelectedItemList.addAll(dataList)
                }
            }
            (activity as AddMultipleCollectionItemActivity).getUserCreatedList(
                multipleUserCreatedSelectedItemList
            )
        }
    }

    private var multipleUserCreatedSelectedItemList = ArrayList<MixFeedResult>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var noBlogsTextView: TextView
    private lateinit var shimmer1: ShimmerFrameLayout
    private var isReuqestRunning = false
    private var isLastPageReached = true
    private lateinit var bottomLoadingView: RelativeLayout
    private var userContentList: ArrayList<MixFeedResult>? = null
    private var start = 0
    private var size = 10
    private var pastVisiblesItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private val userContentAdapter: AddMultipleCollectionAdapter by lazy {
        AddMultipleCollectionAdapter(
            this,
            viewType = "CREATED"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =
            inflater.inflate(R.layout.add_multiple_created_articles_fragment, container, false)
        recyclerView = rootView.findViewById(R.id.recyclerView)
        bottomLoadingView = rootView.findViewById(R.id.bottomLoadingView)
        noBlogsTextView = rootView.findViewById(R.id.noBlogsTextView)
        shimmer1 = rootView.findViewById(R.id.shimmer1)
        rootView.findViewById<View>(R.id.imgLoader).startAnimation(
            AnimationUtils.loadAnimation(
                activity,
                R.anim.rotate_indefinitely
            )
        )
        userContentList = ArrayList()
        getUsersCreatedContent()
        val llm = LinearLayoutManager(activity)
        llm.orientation = RecyclerView.VERTICAL
        recyclerView.layoutManager = llm
        recyclerView.adapter = userContentAdapter
        userContentAdapter.setUserCreatedListData(userContentList)
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
                            getUsersCreatedContent()
                        }
                    }
                }
            }
        })
        return rootView
    }

    private fun getUsersCreatedContent() {
        val retro = BaseApplication.getInstance().retrofit
        val bloggerDashboardAPI = retro.create(BloggerDashboardAPI::class.java)
        val call = bloggerDashboardAPI.getUsersAllContent(
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            start,
            size,
            null
        )
        call.enqueue(object : Callback<MixFeedResponse> {
            override fun onResponse(
                call: Call<MixFeedResponse>,
                response: retrofit2.Response<MixFeedResponse>
            ) {
                shimmer1.stopShimmerAnimation()
                shimmer1.visibility = View.GONE
                try {
                    isReuqestRunning = false
                    bottomLoadingView.visibility = View.GONE
                    if (null == response.body()) {
                        val nee = NetworkErrorException(response.raw().toString())
                        FirebaseCrashlytics.getInstance().recordException(nee)
                        return
                    }
                    val responseData = response.body()
                    if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {
                        if (responseData.data == null && userContentList?.size == 1) {
                            userContentAdapter.setUserCreatedListData(userContentList)
                            userContentAdapter.notifyDataSetChanged()
                            noBlogsTextView.text = getString(R.string.profile_empty_created_content)
                            noBlogsTextView.visibility = View.VISIBLE
                        } else {
                            processUserContentResponse(responseData.data?.result)
                        }
                    } else {
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<MixFeedResponse>, e: Throwable) {
                bottomLoadingView.visibility = View.GONE
                FirebaseCrashlytics.getInstance().recordException(e)
                activity?.let {
                    (it as BaseActivity).apiExceptions(e)
                }
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        })
    }

    private fun processUserContentResponse(responseData: List<MixFeedResult>?) {
        if (responseData.isNullOrEmpty()) {
            isLastPageReached = true
            if (!userContentList.isNullOrEmpty()) {
            } else {
                userContentAdapter.setUserCreatedListData(userContentList)
                userContentAdapter.notifyDataSetChanged()
                noBlogsTextView.text = getString(R.string.profile_empty_created_content)
                noBlogsTextView.visibility = View.VISIBLE
            }
        } else {
            noBlogsTextView.visibility = View.GONE
            start += size
            userContentList?.addAll(responseData)
            userContentAdapter.setUserCreatedListData(userContentList)
            userContentAdapter.notifyDataSetChanged()
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
