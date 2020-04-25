package com.mycity4kids.ui.fragment

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.request.FollowUnfollowUserRequest
import com.mycity4kids.models.response.FollowUnfollowUserResponse
import com.mycity4kids.models.response.MomVlogListingResponse
import com.mycity4kids.models.response.MomVlogersDetailResponse
import com.mycity4kids.models.response.UserDetailResult
import com.mycity4kids.models.response.VlogsListingAndDetailResult
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI
import com.mycity4kids.ui.adapter.MomVLogFollowFollowingAdapter
import com.mycity4kids.ui.adapter.MomVlogFollowingAndVideosAdapter
import com.mycity4kids.utils.EndlessScrollListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowingVideoTabFragment : BaseFragment(),
    MomVLogFollowFollowingAdapter.FollowFollowingRecyclerViewClickListner {

    private lateinit var momVlogFollowingAndVideosAdapter: MomVlogFollowingAndVideosAdapter
    private lateinit var momVLogFollowFollowingAdapter: MomVLogFollowFollowingAdapter
    private lateinit var recyclerView: RecyclerView
    private var listData: ArrayList<VlogsListingAndDetailResult>? = null
    private var vlogersListData: ArrayList<UserDetailResult>? = null
    private var start = 0
    private var vloggers_start = 1
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var linearLayoutManager: GridLayoutManager
    private lateinit var headerTextView: TextView
    private lateinit var suggestingFollowTextView: TextView
    private lateinit var recyclerView_videos: RecyclerView
    private var headerTextChangeAfterFiveFollowingTextViewClick = 0
    private lateinit var shimmer_funny_videos_article: ShimmerFrameLayout
    private lateinit var headerContainerLayout: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.mom_vlog_follow_following_tab_fragment, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        headerTextView = view.findViewById(R.id.headerTextView)
        suggestingFollowTextView = view.findViewById(R.id.suggestingFollowTextView)
        recyclerView_videos = view.findViewById(R.id.recyclerView_videos)
        shimmer_funny_videos_article = view.findViewById(R.id.shimmer_funny_videos_article)
        headerContainerLayout = view.findViewById(R.id.headerContainerLayout)
        context?.let {
            momVlogFollowingAndVideosAdapter = MomVlogFollowingAndVideosAdapter(it)
            momVLogFollowFollowingAdapter = MomVLogFollowFollowingAdapter(this, it)
        }
        listData = ArrayList()
        vlogersListData = ArrayList()
        vloggers_start = 1
        getVlogs(0)
        gridLayoutManager = GridLayoutManager(activity, 2)
        recyclerView_videos.layoutManager = gridLayoutManager
        momVlogFollowingAndVideosAdapter.setListData(listData)

        linearLayoutManager = GridLayoutManager(activity, 3)
        recyclerView.layoutManager = linearLayoutManager

        if (SharedPrefUtils.getFollowClickCountInMomVlog(context)) {
            headerTextView.text =
                getString(R.string.watch_other_videos_text_in_mom_vlog_following_feed)
        } else {
            headerTextView.text =
                getString(R.string.follow_creators_feed_header_text_in_mom_vlog_feed)
        }
        recyclerView_videos.addOnScrollListener(object : EndlessScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                start = totalItemsCount
                getVlogs(start)
            }
        })
        /*   recyclerView.addOnScrollListener(object : EndlessScrollListener(linearLayoutManager) {
               override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                   getVlogers(totalItemsCount)
               }
           })*/
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (momVlogFollowingAndVideosAdapter.getItemViewType(position) == 1)
                    2
                else
                    1
            }
        }
        recyclerView_videos.addItemDecoration(object :
            RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view) // item position
                if (momVlogFollowingAndVideosAdapter.getItemViewType(position) == 1) {
                    outRect.left = 0
                    outRect.right = 0
                    outRect.top = 0
                    outRect.bottom = 0
                } else {
                    when ((view.layoutParams as GridLayoutManager.LayoutParams).spanIndex) {
                        0 -> {
                            outRect.right = 4
                        }
                        1 -> {
                            outRect.left = 4
                        }
                        else -> {
                            outRect.left = 0
                            outRect.right = 0
                            outRect.top = 0
                            outRect.bottom = 0
                        }
                    }
                }
            }
        })
        return view
    }

    override fun onStart() {
        super.onStart()
        shimmer_funny_videos_article.visibility = View.VISIBLE
        shimmer_funny_videos_article.startShimmerAnimation()
    }

    override fun onStop() {
        super.onStop()
        shimmer_funny_videos_article.visibility = View.GONE
        shimmer_funny_videos_article.stopShimmerAnimation()
    }

    private fun getVlogs(start: Int) {
        this.start = start
        val retrofit = BaseApplication.getInstance().retrofit
        val vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI::class.java)
        val call = vlogsListingAndDetailsAPI.getVlogsData(
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            start,
            start + 10
        )
        call.enqueue(object : Callback<MomVlogListingResponse> {
            override fun onFailure(call: Call<MomVlogListingResponse>, t: Throwable) {
                Crashlytics.logException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }

            override fun onResponse(
                call: Call<MomVlogListingResponse>,
                response: Response<MomVlogListingResponse>
            ) {
                try {
                    shimmer_funny_videos_article.stopShimmerAnimation()
                    shimmer_funny_videos_article.visibility = View.GONE
                    val responseData = response.body()?.data?.result
                    processData(responseData as ArrayList<VlogsListingAndDetailResult>?)
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
        })
    }

    fun processData(res: ArrayList<VlogsListingAndDetailResult>?) {
        if (res.isNullOrEmpty()) {
            if (listData.isNullOrEmpty()) {
                shimmer_funny_videos_article.startShimmerAnimation()
                shimmer_funny_videos_article.visibility = View.VISIBLE
                headerContainerLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                headerTextView.visibility = View.VISIBLE
                suggestingFollowTextView.visibility = View.VISIBLE
                getVlogers(vloggers_start)
                recyclerView.adapter = momVLogFollowFollowingAdapter
            } else {
                listData?.let {
                    momVlogFollowingAndVideosAdapter.setListData(it)
                    momVlogFollowingAndVideosAdapter.notifyDataSetChanged()
                }
            }
        } else {

            listData?.addAll(res)
            if (start == 0) {
                listData?.add(VlogsListingAndDetailResult(1))
                recyclerView_videos.visibility = View.VISIBLE
                recyclerView_videos.adapter = momVlogFollowingAndVideosAdapter
            } else {
                if (res.size == 10) {
                    listData?.add(VlogsListingAndDetailResult(1))
                }
            }
            listData?.let {
                momVlogFollowingAndVideosAdapter.setListData(it)
                momVlogFollowingAndVideosAdapter.notifyDataSetChanged()
            }
        }
    }

    fun getVlogers(start: Int) {
        this.vloggers_start = start
        val retrofit = BaseApplication.getInstance().retrofit
        val vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI::class.java)
        val call = vlogsListingAndDetailsAPI.getVlogersData(
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            1,
            15,
            1
        )
        call.enqueue(object : Callback<MomVlogersDetailResponse> {
            override fun onFailure(call: Call<MomVlogersDetailResponse>, e: Throwable) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }

            override fun onResponse(
                call: Call<MomVlogersDetailResponse>,
                response: Response<MomVlogersDetailResponse>
            ) {
                try {
                    shimmer_funny_videos_article.stopShimmerAnimation()
                    shimmer_funny_videos_article.visibility = View.GONE
                    val responseVlogersData = response.body()?.data?.result
                    processVlogersData(responseVlogersData as ArrayList<UserDetailResult>?)
                } catch (e: Exception) {

                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
        })
    }

    private fun processVlogersData(responseVlogersData: ArrayList<UserDetailResult>?) {
        if (responseVlogersData.isNullOrEmpty()) {
            if (vlogersListData.isNullOrEmpty()) {
            }
        } else {
            vlogersListData?.addAll(responseVlogersData)
        }

        vlogersListData?.let {
            momVLogFollowFollowingAdapter.setVlogersData(it)
            momVLogFollowFollowingAdapter.notifyDataSetChanged()
        }
    }

    override fun recyclerViewClick(position: Int, view: View) {
        if (view.id == R.id.followTextView) {
            headerTextChangeAfterFiveFollowingTextViewClick += 1
            if (vlogersListData?.get(position)?.following == true) {
                unFollowApiCall(vlogersListData?.get(position)?.dynamoId!!, position)
            } else {
                followApiCall(vlogersListData?.get(position)?.dynamoId!!, position)
            }
            if (headerTextChangeAfterFiveFollowingTextViewClick == 5) {
                headerTextView.text =
                    getString(R.string.watch_other_videos_text_in_mom_vlog_following_feed)
                SharedPrefUtils.setFollowClickCountInMomVlog(context, true)
            }
        } else if (view.id == R.id.authorImageView) {
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra(Constants.USER_ID, vlogersListData?.get(position)?.dynamoId)
            startActivity(intent)
        }
    }

    private fun unFollowApiCall(
        authorId: String,
        position: Int
    ) {
        vlogersListData?.get(position)?.following = false
        momVLogFollowFollowingAdapter.notifyDataSetChanged()
        val retrofit = BaseApplication.getInstance().retrofit
        val followApi = retrofit.create(FollowAPI::class.java)
        val request = FollowUnfollowUserRequest()
        request.followee_id = authorId
        val followUnfollowUserResponseCall = followApi.unfollowUserV2(request)
        followUnfollowUserResponseCall.enqueue(object :
            Callback<FollowUnfollowUserResponse> {
            override fun onFailure(call: Call<FollowUnfollowUserResponse>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<FollowUnfollowUserResponse>,
                response: Response<FollowUnfollowUserResponse>
            ) {
            }
        })
    }

    private fun followApiCall(
        authorId: String,
        position: Int
    ) {
        vlogersListData?.get(position)?.following = true
        momVLogFollowFollowingAdapter.notifyDataSetChanged()
        val retrofit = BaseApplication.getInstance().retrofit
        val followApi = retrofit.create(FollowAPI::class.java)
        val request = FollowUnfollowUserRequest()
        request.followee_id = authorId
        val followUnfollowUserResponseCall = followApi.followUserV2(request)
        followUnfollowUserResponseCall.enqueue(object :
            Callback<FollowUnfollowUserResponse> {
            override fun onFailure(call: Call<FollowUnfollowUserResponse>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<FollowUnfollowUserResponse>,
                response: Response<FollowUnfollowUserResponse>
            ) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        shimmer_funny_videos_article.stopShimmerAnimation()
        shimmer_funny_videos_article.visibility = View.GONE
    }
}