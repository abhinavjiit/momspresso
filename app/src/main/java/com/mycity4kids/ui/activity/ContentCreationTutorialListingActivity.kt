package com.mycity4kids.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.collectionsModels.UserCollectionsListModel
import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.adapter.CollectionItemsListAdapter
import com.mycity4kids.ui.adapter.TUTORIAL
import com.mycity4kids.utils.EndlessScrollListener
import com.mycity4kids.utils.ToastUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_content_creation_tuorial_listing.*

class ContentCreationTutorialListingActivity : BaseActivity(),
    CollectionItemsListAdapter.RecyclerViewClick {

    private lateinit var collectionItemsListAdapter: CollectionItemsListAdapter
    private var dataList = ArrayList<UserCollectionsModel>()
    var collectionId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_creation_tuorial_listing)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        collectionId = intent.getStringExtra(AppConstants.COLLECTION_ID)

        val linearLayoutManager = LinearLayoutManager(this)
        collectionItemsListAdapter =
            CollectionItemsListAdapter(this, this)
        collectionItemsListAdapter.setListType(TUTORIAL)
        tutorialRecyclerView.layoutManager = linearLayoutManager
        tutorialRecyclerView.adapter = collectionItemsListAdapter
        collectionItemsListAdapter.setListData(dataList)

        getUserCollectionItems(collectionId, 0)

        tutorialRecyclerView.addOnScrollListener(object :
            EndlessScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getUserCollectionItems(collectionId, totalItemsCount)
            }
        })
    }

    private fun getUserCollectionItems(collectionId: String, start: Int) {
        BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java)
            .getUserCollectionItems(collectionId, start, 10).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<BaseResponseGeneric<UserCollectionsListModel>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(response: BaseResponseGeneric<UserCollectionsListModel>) {
                    try {
                        if (response.code == 200 && response.status == Constants.SUCCESS && response.data?.result != null) {
                            dataList.addAll(response.data!!.result.collectionItems)
                            collectionItemsListAdapter.setListData(dataList)
                            collectionItemsListAdapter.notifyDataSetChanged()
                        } else {
                            ToastUtils.showToast(
                                this@ContentCreationTutorialListingActivity,
                                response.data?.msg
                            )
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

    override fun onRecyclerViewclick(position: Int) {
        val intent =
            Intent(this@ContentCreationTutorialListingActivity, ParallelFeedActivity::class.java)
        intent.putExtra(Constants.STREAM_URL, dataList[position].item_info.streamUrl)
        intent.putExtra(Constants.VIDEO_ID, dataList[position].item)
        intent.putExtra(AppConstants.COLLECTION_ID, collectionId)
        intent.putExtra(Constants.AUTHOR_ID, dataList[position].item_info.author.id)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
