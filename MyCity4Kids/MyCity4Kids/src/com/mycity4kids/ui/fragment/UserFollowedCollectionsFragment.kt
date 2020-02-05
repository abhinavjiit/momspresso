package com.mycity4kids.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.RelativeLayout
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
import com.kelltontech.ui.BaseFragment
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.collectionsModels.UserCollectionsListModel
import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.activity.collection.UserCollectionItemListActivity
import com.mycity4kids.ui.adapter.CollectionsAdapter
import com.mycity4kids.ui.videochallengenewui.ExpandableHeightGridView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class UserFollowedCollectionsFragment : BaseFragment() {

    private lateinit var userCreatedFollowedCollectionAdapter: CollectionsAdapter
    private lateinit var collectionGridView: ExpandableHeightGridView
    private lateinit var userCollectionsListModel: UserCollectionsListModel
    private var list = ArrayList<String>()
    private lateinit var shimmer1: ShimmerFrameLayout
    private lateinit var notCreatedTextView: TextView
    private var pageNumber = 0
    private var isLastPageReached = false
    private var isReuqestRunning = false
    private var bottomLoadingView: RelativeLayout? = null
    private var dataList = ArrayList<UserCollectionsModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.user_followed_collections_fragment, container, false)
        bottomLoadingView = view.findViewById(R.id.bottomLoadingView)
        collectionGridView = view.findViewById(R.id.collectionGridView)
        shimmer1 = view.findViewById(R.id.shimmer1)
        notCreatedTextView = view.findViewById(R.id.notCreatedTextView)
        context?.run {
            userCreatedFollowedCollectionAdapter = CollectionsAdapter(context!!)
            collectionGridView.adapter = userCreatedFollowedCollectionAdapter
        }
        view.findViewById<View>(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotate_indefinitely))
        getFollowedCollections()
        collectionGridView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val intent = Intent(activity, UserCollectionItemListActivity::class.java)
                intent.putExtra("id", dataList[position].userCollectionId)
                startActivity(intent)
            }
        })

        collectionGridView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {}

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                val loadMore = firstVisibleItem + visibleItemCount >= totalItemCount
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
                    bottomLoadingView?.visibility = View.VISIBLE
                    getFollowedCollections()
                    isReuqestRunning = true
                }
            }
        })
        return view
    }

    private fun getFollowedCollections() {
        BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java).getFollowedCollection(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, pageNumber, 10).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<UserCollectionsListModel>> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<UserCollectionsListModel>) {
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
                    Crashlytics.logException(e)
                    Log.d("MC4KException", Log.getStackTraceString(e))
                }
            }

            override fun onError(e: Throwable) {
                Crashlytics.logException(e)
                Log.d("MC4KException", Log.getStackTraceString(e))
            }
        })
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
}
