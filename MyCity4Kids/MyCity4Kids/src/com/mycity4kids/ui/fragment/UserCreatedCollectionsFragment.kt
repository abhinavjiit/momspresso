package com.mycity4kids.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.RelativeLayout
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.CollectionsModels.UserCollectionsListModel
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
import java.lang.Exception

class UserCreatedCollectionsFragment : BaseFragment() {
    var userCollectionsListModel = UserCollectionsListModel()
    private lateinit var userCreatedFollowedCollectionAdapter: CollectionsAdapter
    private lateinit var collectionGridView: ExpandableHeightGridView
    private var list = ArrayList<String>()
    private lateinit var collectionId: String
    var userId: String? = null
    var start: Int = 0
    var isLoading: Boolean = false
    var hasMoreItems: Boolean = false
    private var mLodingView: RelativeLayout? = null
    override fun updateUi(response: Response?) {
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.user_created_collections_fragment, container, false)
        collectionGridView = view.findViewById(R.id.collectionGridView)
        mLodingView = view.findViewById(R.id.relativeLoadingView)

        val bundle = arguments
        userId = bundle?.getString("userId")

        getUserCreatedCollections(start)
        context?.run {
            userCreatedFollowedCollectionAdapter = CollectionsAdapter(context!!)
            collectionGridView.adapter = userCreatedFollowedCollectionAdapter
        }
        collectionGridView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val intent = Intent(activity, UserCollectionItemListActivity::class.java)
                intent.putExtra("id", userCollectionsListModel.collections_list[position].userCollectionId)
                startActivity(intent)

            }
        })

        /*  collectionGridView.setOnScrollListener(object : AbsListView.OnScrollListener {
              override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                  if (totalItemCount > 0) {
                      var lastvisible = firstVisibleItem + visibleItemCount
                      if (!isLoading && !hasMoreItems && (lastvisible == totalItemCount)) {
                          isLoading = true
                          mLodingView?.setVisibility(View.VISIBLE)
                          getUserCreatedCollections(lastvisible + 10)

                      }
                  }
              }

              override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
              }

          }


          )*/

        return view

    }



    /* fun addCollectionItem() {
         val addCollectionRequestModel1 = UpdateCollectionRequestModel()
         addCollectionRequestModel1.userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
         addCollectionRequestModel1.itemType = "0"
         val List = ArrayList<String>()
         List.add("5dcc1a88f07eac587471ef4d")
         addCollectionRequestModel1.userCollectionId = List
         addCollectionRequestModel1.item = "article-833ea0a3648e4a7fb6f2a6d2ba1a51f71"
         BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).addCollectionItem(addCollectionRequestModel1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<AddCollectionRequestModel>> {
             override fun onComplete() {

             }

             override fun onSubscribe(d: Disposable) {
             }

             override fun onNext(t: BaseResponseGeneric<AddCollectionRequestModel>) {
                 if (t != null && t.code == 200 && t.status == "success" && t.data?.result != null) {

                     var addCollectionRequestModel = AddCollectionRequestModel()
                     addCollectionRequestModel = t.data!!.result


                 } else {
                     ToastUtils.showToast(activity, "nhi hua  add ")

                 }


             }

             override fun onError(e: Throwable) {
             }

         })


     }*/

    private fun getUserCreatedCollections(start: Int) {
        userId?.let {
            BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).getUserCollectionList(it, 0, 10).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<UserCollectionsListModel>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(response: BaseResponseGeneric<UserCollectionsListModel>) {
                    /* isLoading = false
                     if (mLodingView?.visibility == View.VISIBLE) {
                         mLodingView?.visibility == View.GONE
                     }*/
                    try {
                        if (response.code == 200 && response.status == "success" && response.data?.result != null) {
                            userCollectionsListModel = response.data?.result!!
                            userCreatedFollowedCollectionAdapter.getUserColletions(userCollectionsListModel)
                            userCreatedFollowedCollectionAdapter.notifyDataSetChanged()

                        } else {
                            ToastUtils.showToast(activity, "nhi hua ")
                        }
                    } catch (e: Exception) {
                        Crashlytics.logException(e)
                        Log.d("MC4KException", Log.getStackTraceString(e))
                    }
                }

                override fun onError(e: Throwable) {
                }

            })

        }
    }

    fun processCollectionListingResponse(userCollectionsListModel: UserCollectionsListModel) {
        /* if (userCollectionsListModel.collections_list.size == 0) {
             hasMoreItems = false
             if (null != userCollectionsListModel.collections_list && userCollectionsListModel.collectionItems.isNotEmpty()) {
                 hasMoreItems = true
             }


         }*/


        /*userCreatedFollowedCollectionAdapter.getUserColletions(userCollectionsListModel)
        userCreatedFollowedCollectionAdapter.notifyDataSetChanged()*/

    }

}