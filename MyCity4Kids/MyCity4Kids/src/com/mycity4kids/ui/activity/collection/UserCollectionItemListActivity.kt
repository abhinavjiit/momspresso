package com.mycity4kids.ui.activity.collection

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
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
import com.mycity4kids.utils.AppUtils
import com.squareup.picasso.Picasso
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class UserCollectionItemListActivity : BaseActivity() {
    private lateinit var id: TextView
    private lateinit var collectionId: String
    var userCollectionsListModel = UserCollectionsListModel()
    private lateinit var collectionItemsListAdapter: CollectionItemsListAdapter
    private lateinit var collectionItemRecyclerView: RecyclerView
    private lateinit var setting: ImageView
    private var itemDelete: String = "item1"
    private var muteSwitch: SwitchCompat? = null
    private var collectionNameTextView: TextView? = null
    private var collectionImageVIEW: ImageView? = null
    private lateinit var followersCount: TextView
    private lateinit var followFollowingTextView: TextView
    private lateinit var rightArrow: ImageView
    private lateinit var shimmer1: ShimmerFrameLayout


    override fun updateUi(response: Response?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_collection_item_activity)
        // id = findViewById(R.id.id)
        muteSwitch = findViewById<View>(R.id.muteVideoSwitch) as SwitchCompat
        collectionNameTextView = findViewById(R.id.collectionNameTextView)
        collectionImageVIEW = findViewById(R.id.collectionImageVIEW)
        shimmer1 = findViewById(R.id.shimmer1)
        rightArrow = findViewById(R.id.rightArrow)
        followFollowingTextView = findViewById(R.id.followFollowingTextView)
        followersCount = findViewById(R.id.followersCount)
        collectionNameTextView?.isSelected = true
        setting = findViewById(R.id.setting)
        collectionItemRecyclerView = findViewById(R.id.collectionItemRecyclerView)
        val intent = intent
        collectionId = intent.getStringExtra("id")
        val thumbStates = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                intArrayOf(

                        resources.getColor(R.color.white), resources.getColor(R.color.add_video_details_mute_label))
        )
        muteSwitch?.setThumbTintList(thumbStates)
        if (Build.VERSION.SDK_INT >= 24) {
            val trackStates = ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                    intArrayOf(

                            getColor(R.color.switch_button_green_collection), getColor(R.color.add_video_details_mute_label_50_percent_opacity))
            )
            muteSwitch?.setTrackTintList(trackStates)

        }
        muteSwitch?.setOnClickListener {

            if (muteSwitch?.isChecked == true) {

                updateCollection(delete = false, isPublic = true)
            } else {

                updateCollection(delete = false, isPublic = false)
            }
        }


        val linearLayoutManager = LinearLayoutManager(this)
        collectionItemsListAdapter = CollectionItemsListAdapter(this@UserCollectionItemListActivity)
        collectionItemRecyclerView.layoutManager = linearLayoutManager
        collectionItemRecyclerView.adapter = collectionItemsListAdapter

        /*  collectionItemRecyclerView.setOnScrollListener(object : EndlessScrollListener(linearLayoutManager) {
              override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                  getUserCollectionItems()
              }
          })*/
        getUserCollectionItems()

        setting.setOnClickListener {
            chooseImageOptionPopUp(it)
        }

    }

    fun chooseImageOptionPopUp(view: View) {
        val popup = PopupMenu(this@UserCollectionItemListActivity, view)
        popup.menuInflater.inflate(R.menu.delete_edit_collection_menu, popup.menu)
        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                var id = item?.itemId
                if (id == R.id.delete_collection) {
                    /*     if (itemDelete.equals("item")) {

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
                         } else {*/
                    updateCollection(delete = true, isPublic = true)
                    return true
                }

                //collection delete


                else if (id == R.id.edit_collection) {

                    val intent = Intent(this@UserCollectionItemListActivity, EditCollectionActivity::class.java)
                    intent.putExtra("collectionId", collectionId)
                    startActivityForResult(intent, 1000)


////////////    OPEN NEW ACTIVITY


                    /*     val updateCollectionRequestModel = UpdateCollectionRequestModel()
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


                         return true*/

                }
                return false
            }
        })

        val menuHelper = MenuPopupHelper(this@UserCollectionItemListActivity, popup.menu as MenuBuilder, view)
        menuHelper.setForceShowIcon(true)
        menuHelper.show()

    }


    fun getUserCollectionItems() {
        /* var start: Int = 0
         if (itemCount == 0) {
             start = 0
         }
         if (itemCount != 0) {
             start = +1
         }*/

        BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).getUserCollectionItems(collectionId, 0, 10).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<UserCollectionsListModel>> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<UserCollectionsListModel>) {

                if (response.code == 200 && response.status == "success" && response.data?.result != null) {
                    shimmer1.visibility = View.GONE
                    shimmer1.stopShimmerAnimation()
                    userCollectionsListModel = response.data!!.result
                    collectionNameTextView?.text = userCollectionsListModel.name
                    if (userCollectionsListModel.isPublic) {
                        muteSwitch?.isChecked = true
                        followFollowingTextView
                    }

                    if (AppUtils.isPrivateProfile(userCollectionsListModel.userId)) {
                        followersCount.isEnabled = true
                        followFollowingTextView.visibility = View.GONE
                        muteSwitch?.visibility = View.VISIBLE
                        setting.visibility = View.VISIBLE


                    } else {
                        followersCount.isEnabled = false
                        followFollowingTextView.visibility = View.VISIBLE
                        muteSwitch?.visibility = View.GONE
                        setting.visibility = View.GONE


                    }

                    followersCount.text = userCollectionsListModel.totalCollectionFollowers.toString()
                    try {
                        Picasso.with(this@UserCollectionItemListActivity).load(userCollectionsListModel.imageUrl)
                                .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(collectionImageVIEW)
                    } catch (e: Exception) {
                        collectionImageVIEW?.setImageResource(R.drawable.default_article)
                    }
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

    fun updateCollection(delete: Boolean, isPublic: Boolean) {
        showProgressDialog(resources.getString(R.string.please_wait))
        val updateCollectionRequestModel = UpdateCollectionRequestModel()
        updateCollectionRequestModel.deleted = delete
        updateCollectionRequestModel.public = isPublic
        val list = ArrayList<String>()
        list.add(collectionId)
        updateCollectionRequestModel.userCollectionId = list
        BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).editCollection(updateCollectionRequestModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<AddCollectionRequestModel>> {
            override fun onComplete() {

                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponseGeneric<AddCollectionRequestModel>) {


                if (t != null && t.code == 200 && t.status == "success") {

                    ToastUtils.showToast(this@UserCollectionItemListActivity, "done")

                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
                ToastUtils.showToast(this@UserCollectionItemListActivity, e.message)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1000 && data != null) {

                val collectionName = data.getStringExtra("collectionName")
                collectionNameTextView = findViewById(R.id.collectionNameTextView)
                collectionNameTextView?.text = collectionName
                try {
                    Picasso.with(this@UserCollectionItemListActivity).load(data.getStringExtra("collectionImage"))
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(collectionImageVIEW)
                } catch (e: Exception) {
                    collectionImageVIEW?.setImageResource(R.drawable.default_article)
                }
            }
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