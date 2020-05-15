package com.mycity4kids.ui.fragment

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.collectionsModels.AddCollectionRequestModel
import com.mycity4kids.models.collectionsModels.UpdateCollectionRequestModel
import com.mycity4kids.models.response.ArticleListingResponse
import com.mycity4kids.models.response.ArticleListingResult
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.adapter.AddMultipleCollectionAdapter
import com.mycity4kids.utils.ConnectivityUtils
import com.mycity4kids.utils.ToastUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMultipleCollectionItemDialogFragment : DialogFragment(),
    AddMultipleCollectionAdapter.RecyclerViewClick, View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back -> {
                dismiss()
            }
            R.id.add -> {
                multipleCollectionList.clear()
                collectionId?.let {
                    for (i in 0 until articleDataModelsNew.size) {
                        if (articleDataModelsNew[i].isCollectionItemSelected) {
                            val updateCollectionRequestModel = UpdateCollectionRequestModel()
                            val list = ArrayList<String>()
                            list.add(it)
                            updateCollectionRequestModel.userCollectionId = list
                            updateCollectionRequestModel.userId =
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())
                                    .dynamoId
                            updateCollectionRequestModel.item = articleDataModelsNew[i].id
                            updateCollectionRequestModel.itemType =
                                AppConstants.ARTICLE_COLLECTION_TYPE
                            val dataList = ArrayList<UpdateCollectionRequestModel>()
                            dataList.add(updateCollectionRequestModel)
                            multipleCollectionList.addAll(dataList)
                        }
                    }
                    postDataToServer()
                }
            }
            R.id.skipTextView -> {
                dismiss()
            }
        }
    }

    override fun onclick(position: Int) {
        articleDataModelsNew[position].isCollectionItemSelected =
            !articleDataModelsNew[position].isCollectionItemSelected
        addMultipleCollectionAdapter.notifyDataSetChanged()
    }

    lateinit var shimmer1: ShimmerFrameLayout
    private lateinit var addMultipleCollectionAdapter: AddMultipleCollectionAdapter
    lateinit var recyclerView: RecyclerView
    private var chunk = 0
    private var multipleCollectionList = ArrayList<UpdateCollectionRequestModel>()
    private var nextPageNumber = 0
    private var isReuqestRunning = false
    private var isLastPageReached = true
    private var pastVisiblesItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private lateinit var articleDataModelsNew: ArrayList<ArticleListingResult>
    private var noBlogsTextView: TextView? = null
    private var bottomLoadingView: RelativeLayout? = null
    private lateinit var back: ImageView
    lateinit var add: TextView
    lateinit var skip: TextView
    var collectionId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(
            R.layout.add_multiple_collection_item_activity, container,
            false
        )
        recyclerView = rootView.findViewById(R.id.recyclerView)
        bottomLoadingView = rootView.findViewById(R.id.bottomLoadingView)
        noBlogsTextView = rootView.findViewById(R.id.noBlogsTextView)
        shimmer1 = rootView.findViewById(R.id.shimmer1)
        back = rootView.findViewById(R.id.back)
        skip = rootView.findViewById(R.id.skipTextView)
        add = rootView.findViewById(R.id.add)
        rootView.findViewById<View>(R.id.imgLoader)
            .startAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotate_indefinitely))
        collectionId = arguments?.getString("collectionId")
        getReadArticles()
        val llm = LinearLayoutManager(activity)
        llm.orientation = RecyclerView.VERTICAL
        recyclerView.layoutManager = llm
        articleDataModelsNew = ArrayList()
        //   addMultipleCollectionAdapter = AddMultipleCollectionAdapter( this)
        recyclerView.adapter = addMultipleCollectionAdapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = llm.childCount
                    totalItemCount = llm.itemCount
                    pastVisiblesItems = llm.findFirstVisibleItemPosition()

                    if (!isReuqestRunning) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            isReuqestRunning = true
                            bottomLoadingView?.visibility = View.VISIBLE
                            getReadArticles()
                        }
                    }
                }
            }
        })
        back.setOnClickListener(this)
        skip.setOnClickListener(this)
        add.setOnClickListener(this)
        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            dialog.window!!.setWindowAnimations(R.style.CollectionDialogAnimation)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.transparent)))
        }
        shimmer1.startShimmerAnimation()
    }

    private fun postDataToServer() {
        if (isAdded) {
            if (!ConnectivityUtils.isNetworkEnabled(activity)) {
                (activity as UserProfileActivity).showToast(getString(R.string.connectivity_unavailable))
                return
            }
        }

        val retro = BaseApplication.getInstance().retrofit
        val collectionApi = retro.create(CollectionsAPI::class.java)
        val call = collectionApi.addMultipleCollectionItem(multipleCollectionList)
        call.enqueue(object : Callback<BaseResponseGeneric<AddCollectionRequestModel>> {
            override fun onFailure(
                call: Call<BaseResponseGeneric<AddCollectionRequestModel>>,
                t: Throwable
            ) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }

            override fun onResponse(
                call: Call<BaseResponseGeneric<AddCollectionRequestModel>>,
                response: Response<BaseResponseGeneric<AddCollectionRequestModel>>
            ) {
                if (response.body() == null) {
                    return
                }
                try {
                    val responsee = response.body()
                    if (responsee?.code == 200 && responsee.status == "success" && !responsee.data?.result?.listItemId.isNullOrEmpty()) {
                        ToastUtils.showToast(activity, responsee.data?.msg)
                        dismiss()
                    } else {
                        ToastUtils.showToast(activity, responsee?.data?.msg)
                    }
                } catch (t: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(t)
                    Log.d("MC4kException", Log.getStackTraceString(t))
                }
            }
        })
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
        val call = userpublishedArticlesAPI.getAuthorsReadArticles(
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            10,
            chunk,
            "articles"
        )
        call.enqueue(object : Callback<ArticleListingResponse> {
            override fun onFailure(call: Call<ArticleListingResponse>, t: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }

            override fun onResponse(
                call: Call<ArticleListingResponse>,
                response: Response<ArticleListingResponse>
            ) {
                shimmer1.stopShimmerAnimation()
                shimmer1.visibility = View.GONE
                bottomLoadingView?.visibility = View.GONE
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
                    FirebaseCrashlytics.getInstance().recordException(e)
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
                // No more next results for search from pagination
            } else {
                // No results
                articleDataModelsNew.addAll(dataList)
                //     addMultipleCollectionAdapter.setListData(articleDataModelsNew)
                addMultipleCollectionAdapter.notifyDataSetChanged()
                noBlogsTextView?.visibility = View.VISIBLE
            }
        } else {
            if (nextPageNumber == 1) {
                articleDataModelsNew.addAll(dataList)
                //   addMultipleCollectionAdapter.setListData(articleDataModelsNew)
                addMultipleCollectionAdapter.notifyDataSetChanged()
            } else {
                articleDataModelsNew.addAll(dataList)
            }
            //   addMultipleCollectionAdapter.setListData(articleDataModelsNew)
            nextPageNumber = nextPageNumber + 1
            addMultipleCollectionAdapter.notifyDataSetChanged()
        }
    }

    override fun onStop() {
        super.onStop()
        shimmer1.stopShimmerAnimation()
        shimmer1.visibility = View.GONE
    }
}
