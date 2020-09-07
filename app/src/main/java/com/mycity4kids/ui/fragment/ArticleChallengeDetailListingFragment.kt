package com.mycity4kids.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.request.ArticleDetailRequest
import com.mycity4kids.models.response.AddBookmarkResponse
import com.mycity4kids.models.response.ArticleListingResponse
import com.mycity4kids.models.response.ArticleListingResult
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity
import com.mycity4kids.ui.adapter.ArticleChallengeListingAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ArticleChallengeDetailListingFragment : BaseFragment(),
    ArticleChallengeListingAdapter.RecyclerViewItemClickListener {
    private lateinit var recyclerViewArticleChallengeListing: RecyclerView

    private var articleChallengeId: String? = null
    private lateinit var adapter: ArticleChallengeListingAdapter
    private var nextPageNumber = 1
    private var limit = 15
    private var isRequestingRunning = false
    private var isLastPageReached = false
    private var articleListingResults: ArrayList<ArticleListingResult>? = null
    private var pastVisiblesItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private lateinit var loadingView: RelativeLayout
    private lateinit var shimmer1: ShimmerFrameLayout
    private lateinit var noArticleTextView: TextView
    private var tabPosition: String? = null
    private var start = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.article_challenge_listing_fragment, container, false)
        recyclerViewArticleChallengeListing =
            view.findViewById(R.id.recyclerViewArticleChallengeListing)
        loadingView = view.findViewById(R.id.relativeLoadingView)
        shimmer1 = view.findViewById(R.id.shimmer1)
        noArticleTextView = view.findViewById(R.id.noArticleTextView)
        articleChallengeId = arguments?.getString("articleChallengeId")
        tabPosition = arguments?.getString("position")
        articleListingResults = ArrayList()
        val llm = LinearLayoutManager(activity)
        recyclerViewArticleChallengeListing.layoutManager = llm
        adapter = if ("1" == tabPosition)
            ArticleChallengeListingAdapter(this)
        else
            ArticleChallengeListingAdapter(this, "winnerTab")
        recyclerViewArticleChallengeListing.adapter = adapter
        adapter.setListData(articleListingResults!!)
        adapter.notifyDataSetChanged()
        if ("1" == tabPosition)
            getArticleChallengeListing
        else
            getArticleChallengeWinnersListing

        recyclerViewArticleChallengeListing.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = llm.childCount
                    totalItemCount = llm.itemCount
                    pastVisiblesItems = llm.findFirstVisibleItemPosition()
                    if (!isRequestingRunning && !isLastPageReached) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            isRequestingRunning = true
                            loadingView.visibility = View.VISIBLE
                            if ("1" == tabPosition)
                                getArticleChallengeListing
                            else
                                getArticleChallengeWinnersListing
                        }
                    }
                }
            }
        })
        return view
    }

    private val getArticleChallengeListing: Unit
        get() {
            val from = (nextPageNumber - 1) * limit + 1
            val retrofit = BaseApplication.getInstance().retrofit
            val topicsCategoryApi = retrofit.create(TopicsCategoryAPI::class.java)
            val call = topicsCategoryApi.getArticlesForCategory(
                articleChallengeId,
                0,
                from,
                from + limit - 1,
                SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext())
            )
            call.enqueue(articleListing)
        }

    private val getArticleChallengeWinnersListing: Unit
        get() {
            CoroutineScope(Dispatchers.IO).launch {
                val ret = BaseApplication.getInstance().retrofit
                val topicsCategoryAPI = ret.create(CollectionsAPI::class.java)
                val responseData =
                    async {
                        topicsCategoryAPI.getWinnerArticleChallenge(
                            start,
                            10,
                            articleChallengeId,
                            "0"
                        )
                    }
                try {
                    val response = responseData.await()
                    launch(Dispatchers.Main)
                    {
                        isRequestingRunning = false
                        if (loadingView.visibility == View.VISIBLE) {
                            loadingView.visibility = View.GONE
                        }
                        if (response?.code == 200 && response.status == Constants.SUCCESS) {
                            processWinnerArticleListingResponse(response)
                            shimmer1.stopShimmerAnimation()
                            shimmer1.visibility = View.GONE
                        }
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4KException", Log.getStackTraceString(e))
                } catch (t: Throwable) {
                    FirebaseCrashlytics.getInstance().recordException(t)
                    Log.d("MC4KException", Log.getStackTraceString(t))
                }

            }
        }

    private var articleListing = object : Callback<ArticleListingResponse> {
        override fun onFailure(call: Call<ArticleListingResponse>, t: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4KException", Log.getStackTraceString(t))
        }

        override fun onResponse(
            call: Call<ArticleListingResponse>,
            response: Response<ArticleListingResponse>
        ) {
            isRequestingRunning = false
            if (loadingView.visibility == View.VISIBLE) {
                loadingView.visibility = View.GONE
            }
            if (response.body() == null) {
                return
            }
            try {
                val responseData = response.body()
                if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {
                    processArticleListingResponse(responseData)
                    shimmer1.stopShimmerAnimation()
                    shimmer1.visibility = View.GONE
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4KException", Log.getStackTraceString(e))
            }
        }
    }

    private fun processWinnerArticleListingResponse(response: ArticleListingResponse) {
        val dataList: ArrayList<ArticleListingResult> =
            response.data[0].result

        if (dataList.size == 0) {
            isLastPageReached = false
            if (null != articleListingResults && articleListingResults!!.isNotEmpty()) {
                isLastPageReached = true
            } else {
                noArticleTextView.visibility = View.VISIBLE
                articleListingResults = dataList
                adapter.notifyDataSetChanged()
            }
        } else {
            noArticleTextView.visibility = View.GONE
            if (start == 0) {
                articleListingResults = dataList
            } else {
                articleListingResults?.addAll(dataList)
            }
            start += 10
            adapter.setListData(articleListingResults!!)
            adapter.notifyDataSetChanged()
        }
    }

    private fun processArticleListingResponse(response: ArticleListingResponse) {
        val dataList: ArrayList<ArticleListingResult> =
            response.data[0].result

        if (dataList.size == 0) {
            isLastPageReached = false
            if (null != articleListingResults && articleListingResults!!.isNotEmpty()) {
                isLastPageReached = true
            } else {
                noArticleTextView.visibility = View.VISIBLE
                articleListingResults = dataList
                adapter.notifyDataSetChanged()
            }
        } else {
            noArticleTextView.visibility = View.GONE
            if (nextPageNumber == 1) {
                articleListingResults = dataList
            } else {
                articleListingResults?.addAll(dataList)
            }
            nextPageNumber += 1
            adapter.setListData(articleListingResults!!)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onClick(view: View, position: Int) {
        when (view.id) {
            R.id.articleItemView -> {
                val intent = Intent(activity, ArticleDetailsContainerActivity::class.java)
                intent.putExtra(
                    Constants.ARTICLE_ID,
                    articleListingResults?.get(position)?.id
                )
                startActivity(intent)
            }
            R.id.bookmarkArticleImageView -> {
                val ret = BaseApplication.getInstance().retrofit
                val articleDetailsApi = ret.create(ArticleDetailsAPI::class.java)
                if (articleListingResults?.get(position)?.is_bookmark == "0") {
                    val articleDetailRequest = ArticleDetailRequest()
                    articleDetailRequest.articleId =
                        articleListingResults?.get(position)?.id
                    articleListingResults?.get(position)?.is_bookmark = "1"
                    adapter.setListData(articleListingResults!!)
                    adapter.notifyDataSetChanged()
                    val call: Call<AddBookmarkResponse> = articleDetailsApi
                        .addBookmark(articleDetailRequest)
                    call.enqueue(addBookmarkResponseCallback)
                } else {
                    articleListingResults?.get(position)?.is_bookmark = "0"
                    adapter.setListData(articleListingResults!!)
                    adapter.notifyDataSetChanged()
                    /* val deleteBookmarkRequest = DeleteBookmarkRequest()
                     deleteBookmarkRequest.id = bookmarkId
                     val call: Call<AddBookmarkResponse> = articleDetailsApi
                         .deleteBookmark(deleteBookmarkRequest)
                     call.enqueue(addBookmarkResponseCallback)*/
                }
            }
        }
    }

    private val addBookmarkResponseCallback: Callback<AddBookmarkResponse> =
        object : Callback<AddBookmarkResponse> {
            override fun onResponse(
                call: Call<AddBookmarkResponse?>,
                response: Response<AddBookmarkResponse?>
            ) {
                if (null == response.body()) {
                    if (isAdded) {
                        (activity as ArticleDetailsContainerActivity?)
                            ?.showToast(getString(R.string.server_went_wrong))
                    }
                    return
                }
                val responseData = response.body()
                //  updateBookmarkStatus(ADD_BOOKMARK, responseData)
            }

            override fun onFailure(
                call: Call<AddBookmarkResponse?>,
                t: Throwable
            ) {
            }
        }

    /* private fun updateBookmarkStatus(
         status: Int,
         responseData: AddBookmarkResponse
     ) {
         try {
             if (responseData.code == 200 && Constants.SUCCESS == responseData.status) {
                 if (status == ADD_BOOKMARK) {
                     todaysBestListData.get(todaysBestBookmarkIdIndex)
                         .bookmarkId = responseData.data.result.bookmarkId
                 }
             }
         } catch (e: java.lang.Exception) {
             FirebaseCrashlytics.getInstance().recordException(e)
             Log.d("MC4kException", Log.getStackTraceString(e))
         }
     }*/
}
