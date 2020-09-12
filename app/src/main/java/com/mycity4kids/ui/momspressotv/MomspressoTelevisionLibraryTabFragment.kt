package com.mycity4kids.ui.momspressotv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.request.ArticleDetailRequest
import com.mycity4kids.models.request.DeleteBookmarkRequest
import com.mycity4kids.models.response.AddBookmarkResponse
import com.mycity4kids.models.response.ArticleListingResponse
import com.mycity4kids.models.response.ArticleListingResult
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity
import com.mycity4kids.utils.ToastUtils
import java.util.ArrayList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MomspressoTelevisionLibraryTabFragment : BaseFragment(),
    MomspressoLibraryAdapter.RecyclerViewClickListener {
    private lateinit var loadingView: RelativeLayout
    private lateinit var recyclerView: RecyclerView
    private var nextPageNumber = 1
    private val limit = 15
    private var isRequestRunning = false
    private var isLastPageReached = false
    private var pastVisiblesItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var libraryList = ArrayList<ArticleListingResult>()

    private val libraryAdapter: MomspressoLibraryAdapter by lazy {
        MomspressoLibraryAdapter(
            this, libraryList
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.mtv_library_tab_fragment, container, false)
        loadingView = view.findViewById(R.id.relativeLoadingView)
        recyclerView = view.findViewById(R.id.recyclerView)

        view.findViewById<View>(R.id.imgLoader).startAnimation(
            AnimationUtils.loadAnimation(
                activity,
                R.anim.rotate_indefinitely
            )
        )

        val llm = LinearLayoutManager(activity)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        recyclerView.adapter = libraryAdapter

        hitFilteredTopicsArticleListingApi()

        recyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                if (dy > 0) {
                    visibleItemCount = llm.getChildCount()
                    totalItemCount = llm.getItemCount()
                    pastVisiblesItems = llm.findFirstVisibleItemPosition()
                    if (!isRequestRunning && !isLastPageReached) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            isRequestRunning = true
                            loadingView.visibility = View.VISIBLE
                            hitFilteredTopicsArticleListingApi()
                        }
                    }
                }
            }
        })

        return view
    }

    private fun hitFilteredTopicsArticleListingApi() {
        val retrofit = BaseApplication.getInstance().retrofit
        val topicsApi = retrofit.create(
            TopicsCategoryAPI::class.java
        )
        val from: Int = (nextPageNumber - 1) * limit + 1
        val filterCall: Call<ArticleListingResponse>
        filterCall = topicsApi.getArticlesForCategory(
            AppConstants.MOMSPRESSO_CATEGORYID,
            0,
            from,
            from + limit - 1,
            SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext())
        )
        filterCall.enqueue(articleListingResponseCallback)
    }

    private val articleListingResponseCallback: Callback<ArticleListingResponse> =
        object : Callback<ArticleListingResponse> {
            override fun onResponse(
                call: Call<ArticleListingResponse?>,
                response: Response<ArticleListingResponse?>
            ) {
                isRequestRunning = false
                if (loadingView.visibility == View.VISIBLE) {
                    loadingView.visibility = View.GONE
                }
                if (response.body() == null) {
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        processArticleListingResponse(responseData)
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4KException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(
                call: Call<ArticleListingResponse?>,
                t: Throwable
            ) {
                if (loadingView.visibility == View.VISIBLE) {
                    loadingView.visibility = View.GONE
                }
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4KException", Log.getStackTraceString(t))
            }
        }

    private fun processArticleListingResponse(responseData: ArticleListingResponse) {
        val dataList =
            responseData.data[0].result
        if (dataList.size == 0) {
            isLastPageReached = false
            if (libraryList.isNotEmpty()) {
                // No more next results for search from pagination
                isLastPageReached = true
            } else {
                // No results
                libraryAdapter.notifyDataSetChanged()
            }
        } else {
            libraryList.addAll(dataList)
            nextPageNumber += 1
            libraryAdapter.notifyDataSetChanged()
        }
    }

    override fun onRecyclerItemClick(view: View?, position: Int) {
        when (view?.id) {
            R.id.articleItemView, R.id.liveStreamItemView -> {
                if (libraryList[position].eventId != null && libraryList[position].eventId.isNotBlank()) {
                    Utils.shareEventTracking(
                        activity,
                        "Momspresso TV",
                        "Live_Android",
                        "TVL_LibraryTab_Card_Live"
                    )
                }
                val intent = Intent(
                    activity,
                    ArticleDetailsContainerActivity::class.java
                )
                intent.putExtra(Constants.ARTICLE_ID, libraryList[position].id)
                intent.putExtra(Constants.AUTHOR_ID, libraryList[position].userId)
                intent.putExtra(Constants.BLOG_SLUG, libraryList[position].blogPageSlug)
                intent.putExtra(Constants.TITLE_SLUG, libraryList[position].titleSlug)
                intent.putExtra(Constants.FROM_SCREEN, "MomspressoTelevisionLibraryTabFragment")
                intent.putExtra(
                    Constants.AUTHOR,
                    libraryList[position].userId + "~" + libraryList[position].userName
                )
                startActivity(intent)
            }
            R.id.bookmarkArticleImageView -> {
                if (libraryList[position].is_bookmark == "0") {
                    bookmarkItem(position)
                } else {
                    deleteBookmark(position)
                }
            }
        }
    }

    private fun bookmarkItem(position: Int) {
        val retro = BaseApplication.getInstance().retrofit
        val articleDetailsAPI = retro.create(ArticleDetailsAPI::class.java)
        val articleDetailRequest = ArticleDetailRequest()
        articleDetailRequest.articleId = libraryList.get(position).id
        val call = articleDetailsAPI.addBookmark(articleDetailRequest)
        call.enqueue(object : Callback<AddBookmarkResponse> {
            override fun onResponse(
                call: Call<AddBookmarkResponse>,
                response: Response<AddBookmarkResponse>
            ) {
                if (null == response.body()) {
                    activity?.let {
                        ToastUtils.showToast(it, getString(R.string.server_went_wrong))
                    }
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData?.code == 200) {
                        libraryList[position].is_bookmark = "1"
                        libraryList[position].bookmarkId = responseData.data.result.bookmarkId
                        libraryAdapter.notifyDataSetChanged()
                        activity?.let {
                            Utils.pushBookmarkArticleEvent(
                                it,
                                "ArticleListingFragment",
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId + "",
                                libraryList[position].id,
                                libraryList[position].userId + "~" + libraryList[position].userName
                            )
                        }
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<AddBookmarkResponse>, e: Throwable) {
                activity?.let {
                    ToastUtils.showToast(it, getString(R.string.server_went_wrong))
                }
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        })
    }

    private fun deleteBookmark(position: Int) {
        val retro = BaseApplication.getInstance().retrofit
        val deleteBookmarkRequest = DeleteBookmarkRequest()
        deleteBookmarkRequest.id = libraryList.get(position).bookmarkId
        val articleDetailsAPI = retro.create(
            ArticleDetailsAPI::class.java
        )
        val call =
            articleDetailsAPI.deleteBookmark(deleteBookmarkRequest)
        call.enqueue(object : Callback<AddBookmarkResponse> {
            override fun onFailure(call: Call<AddBookmarkResponse>, t: Throwable) {
                activity?.let {
                    ToastUtils.showToast(it, getString(R.string.server_went_wrong))
                }
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }

            override fun onResponse(
                call: Call<AddBookmarkResponse>,
                response: Response<AddBookmarkResponse>
            ) {
                if (null == response.body()) {
                    activity?.let {
                        ToastUtils.showToast(it, getString(R.string.server_went_wrong))
                    }
                    return
                }
                val responseData = response.body()
                if (responseData?.code == 200) {
                    libraryList[position].is_bookmark = "0"
                    libraryList[position].bookmarkId = ""
                    libraryAdapter.notifyDataSetChanged()
                    activity?.let {
                        Utils.pushUnbookmarkArticleEvent(
                            it,
                            "ArticleListingFragment",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId + "",
                            libraryList[position].id,
                            libraryList[position].userId + "~" + libraryList[position].userName
                        )
                    }
                }
            }
        })
    }
}
