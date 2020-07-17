package com.mycity4kids.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.AdapterView.OnItemClickListener
import android.widget.RelativeLayout
import android.widget.TextView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.collectionsModels.UserCollectionsListModel
import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.activity.collection.UserCollectionItemListActivity
import com.mycity4kids.ui.adapter.CollectionsAdapter
import com.mycity4kids.ui.videochallengenewui.ExpandableHeightGridView
import com.mycity4kids.utils.ToastUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class UserCreatedCollectionsFragment : BaseFragment() {
    private lateinit var userCreatedFollowedCollectionAdapter: CollectionsAdapter
    private lateinit var collectionGridView: ExpandableHeightGridView
    var userId: String? = null
    var start: Int = 0
    private var mLodingView: RelativeLayout? = null
    private lateinit var shimmer1: ShimmerFrameLayout
    private lateinit var notCreatedTextView: TextView
    private var pageNumber = 0
    private var isLastPageReached = false
    private var isReuqestRunning = false
    private var dataList = ArrayList<UserCollectionsModel>()
    private var bottomLoadingView: RelativeLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_created_collections_fragment, container, false)
        bottomLoadingView = view.findViewById(R.id.bottomLoadingView)
        collectionGridView = view.findViewById(R.id.collectionGridView)
        mLodingView = view.findViewById(R.id.relativeLoadingView)
        shimmer1 = view.findViewById(R.id.shimmer1)
        notCreatedTextView = view.findViewById(R.id.notCreatedTextView)
        view.findViewById<View>(R.id.imgLoader).startAnimation(
            AnimationUtils.loadAnimation(
                activity,
                R.anim.rotate_indefinitely
            )
        )
        val bundle = arguments
        userId = bundle?.getString("userId")
        getUserCreatedCollections()
        context?.run {
            userCreatedFollowedCollectionAdapter = CollectionsAdapter(this)
            collectionGridView.adapter = userCreatedFollowedCollectionAdapter
        }
        collectionGridView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            val intent = Intent(activity, UserCollectionItemListActivity::class.java)
            intent.putExtra("id", dataList[position].userCollectionId)
            startActivityForResult(intent, 1000)
        }
        collectionGridView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
            }

            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {

                    val loadMore = firstVisibleItem + visibleItemCount >= totalItemCount
                    if (visibleItemCount != 0 && loadMore && firstVisibleItem >= 0 && !isReuqestRunning && !isLastPageReached) {
                        bottomLoadingView?.visibility = View.VISIBLE
                        getUserCreatedCollections()
                        isReuqestRunning = true
                    }
            }
        })
        return view
    }

    private fun getUserCreatedCollections() {
        userId?.let {
            BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java).getUserCollectionList(
                it,
                pageNumber,
                10,
                null
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                object : Observer<BaseResponseGeneric<UserCollectionsListModel>> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(response: BaseResponseGeneric<UserCollectionsListModel>) {
                        isReuqestRunning = false
                        try {
                            if (response.code == 200 && response.status == Constants.SUCCESS && response.data?.result != null) {
                                shimmer1.stopShimmerAnimation()
                                shimmer1.visibility = View.GONE
                                bottomLoadingView?.visibility = View.GONE
                                processResponse(response.data?.result!!)
                            } else {
                                ToastUtils.showToast(activity, response.data?.msg)
                            }
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)
                            Log.d("MC4KException", Log.getStackTraceString(e))
                        }
                    }

                    override fun onError(e: Throwable) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4KException", Log.getStackTraceString(e))
                    }
                })
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

    private fun processResponse(data: UserCollectionsListModel) {
        val newDatalist = data.collectionsList
        if (newDatalist.size == 0) {
            isLastPageReached = true
            if (dataList.isNotEmpty()) {
            } else {
                dataList = newDatalist
                userCreatedFollowedCollectionAdapter.getUserColletions(dataList)
                userCreatedFollowedCollectionAdapter.notifyDataSetChanged()
                notCreatedTextView.visibility = View.VISIBLE
            }
        } else {
            notCreatedTextView.visibility = View.GONE
            if (pageNumber == 0) {
                dataList = newDatalist
            } else {
                dataList.addAll(newDatalist)
            }
            userCreatedFollowedCollectionAdapter.getUserColletions(dataList)
            pageNumber += 10
            userCreatedFollowedCollectionAdapter.notifyDataSetChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1000) {
                val collectionId = data?.getStringExtra("CollectionId")
                if (dataList.isNotEmpty() && data != null) {
                    for (i in 0 until dataList.size) {
                        if (dataList[i].userCollectionId == collectionId) {
                            if (data.hasExtra(AppConstants.COLLECTION_EDIT_TYPE)) {
                                val comingFor =
                                    data.getStringExtra(AppConstants.COLLECTION_EDIT_TYPE)
                                if (!comingFor.isNullOrBlank()) {
                                    if ("editCollection" == comingFor) {
                                        dataList[i].imageUrl =
                                            data.getStringExtra("collectionImage")
                                        dataList[i].name = data.getStringExtra("collectionName")
                                    } else if ("deleteCollection" == comingFor) {
                                        dataList.removeAt(i)
                                    }
                                    userCreatedFollowedCollectionAdapter.notifyDataSetChanged()
                                }
                            }
                            break
                        }
                    }
                }
            }
        }
    }
}
