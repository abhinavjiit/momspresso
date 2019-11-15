package com.mycity4kids.ui.activity.collection

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.CollectionsModels.AddCollectionRequestModel
import com.mycity4kids.models.CollectionsModels.UpdateCollectionRequestModel
import com.mycity4kids.models.CollectionsModels.UserCollectionsListModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.adapter.CollectionItemsListAdapter
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class UserCollectionItemListActivity : BaseActivity() {
    private lateinit var id: TextView
    private lateinit var idd: String
    var userCollectionsListModel = UserCollectionsListModel()
    private lateinit var collectionItemsListAdapter: CollectionItemsListAdapter
    private lateinit var collectionItemRecyclerView: RecyclerView
    private lateinit var setting: ImageView
    private var itemDelete: String = "item1"

    override fun updateUi(response: Response?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_collection_item_activity)
        id = findViewById(R.id.id)
        setting = findViewById(R.id.setting)

        collectionItemRecyclerView = findViewById(R.id.collectionItemRecyclerView)
        val intent = intent
        idd = intent.getStringExtra("id")
        getUserCollectionItems()

        val linearLayoutManager = LinearLayoutManager(this)
        collectionItemsListAdapter = CollectionItemsListAdapter(this@UserCollectionItemListActivity)
        collectionItemRecyclerView.layoutManager = linearLayoutManager
        collectionItemRecyclerView.adapter = collectionItemsListAdapter

        setting.setOnClickListener {


            chooseImageOptionPopUp(it)
        }

    }

    fun chooseImageOptionPopUp(view: View) {
        val popup = PopupMenu(this@UserCollectionItemListActivity, view)
        popup.menuInflater.inflate(R.menu.edit_vlog_details_menu, popup.menu)
        popup.menu.findItem(R.id.disable_comment).isVisible = true
        itemDelete = "colletion"
        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                var id = item?.itemId
                if (id == R.id.edit_vlog) {
                    if (itemDelete.equals("item")) {

                        val addCollectionRequestModel = AddCollectionRequestModel()
                        addCollectionRequestModel.itemType = "0"
                        addCollectionRequestModel.deleted = true
                        addCollectionRequestModel.item = userCollectionsListModel.collectionItems[0].item
                        addCollectionRequestModel.itemId = userCollectionsListModel.collectionItems[0].itemId
                        addCollectionRequestModel.userCollectionId = userCollectionsListModel.collectionItems[0].userCollectionId
                        addCollectionRequestModel.userId = userCollectionsListModel.collectionItems[0].userId


                        BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).editCollectionItem(addCollectionRequestModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<AddCollectionRequestModel>> {
                            override fun onComplete() {

                            }

                            override fun onSubscribe(d: Disposable) {
                            }

                            override fun onNext(t: BaseResponseGeneric<AddCollectionRequestModel>) {

                            }

                            override fun onError(e: Throwable) {
                            }
                        })
                    } else {

                        val updateCollectionRequestModel = UpdateCollectionRequestModel()
                        updateCollectionRequestModel.deleted = true
                        val list = ArrayList<String>()
                        list.add(idd)
                        updateCollectionRequestModel.userCollectionId = list
                        BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).editCollection(updateCollectionRequestModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<UpdateCollectionRequestModel>> {
                            override fun onComplete() {


                            }

                            override fun onSubscribe(d: Disposable) {
                            }

                            override fun onNext(t: BaseResponseGeneric<UpdateCollectionRequestModel>) {


                                if (t != null && t.code == 200 && t.status == "success") {

                                }
                            }

                            override fun onError(e: Throwable) {
                            }
                        })

                    }

                    //collection delete

                    return true
                } else if (id == R.id.disable_comment) {
                    val updateCollectionRequestModel = UpdateCollectionRequestModel()
                    val list = ArrayList<String>()
                    list.add(idd)
                    updateCollectionRequestModel.userCollectionId = list
                    updateCollectionRequestModel.name = "abhinav chchcccccccc"
                    updateCollectionRequestModel.imageUrl = "http://tineye.com/images/widgets/mona.jpg"


                    //collection edit  window
                    BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).editCollection(updateCollectionRequestModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<UpdateCollectionRequestModel>> {
                        override fun onComplete() {
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(t: BaseResponseGeneric<UpdateCollectionRequestModel>) {


                            if (t != null && t.status == "success" && t.code == 200 && t.data?.result != null) {

                                ToastUtils.showToast(this@UserCollectionItemListActivity, "updated")

                            }
                        }

                        override fun onError(e: Throwable) {

                        }
                    })


                    return true

                }
                return false
            }
        })

        val menuHelper = MenuPopupHelper(this@UserCollectionItemListActivity, popup.menu as MenuBuilder, view)
        menuHelper.setForceShowIcon(true)
        menuHelper.show()

    }


    fun getUserCollectionItems() {
        BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).getUserCollectionItems(idd, 0, 20).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<UserCollectionsListModel>> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<UserCollectionsListModel>) {

                if (response.code == 200 && response.status == "success" && response.data?.result != null) {
                    userCollectionsListModel = response.data!!.result
                    collectionItemsListAdapter.setListData(userCollectionsListModel)
                    collectionItemsListAdapter.notifyDataSetChanged()

                } else {
                    ToastUtils.showToast(this@UserCollectionItemListActivity, "nhi hua ")

                }

            }

            override fun onError(e: Throwable) {
            }

        })


    }
}