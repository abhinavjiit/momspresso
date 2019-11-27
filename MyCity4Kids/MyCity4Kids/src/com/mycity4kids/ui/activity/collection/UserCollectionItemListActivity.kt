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
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.collectionsModels.AddCollectionRequestModel
import com.mycity4kids.models.collectionsModels.UpdateCollectionRequestModel
import com.mycity4kids.models.collectionsModels.UserCollectionsListModel
import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity
import com.mycity4kids.ui.activity.FollowersAndFollowingListActivity
import com.mycity4kids.ui.adapter.CollectionItemsListAdapter
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.EndlessScrollListener
import com.squareup.picasso.Picasso
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject

class UserCollectionItemListActivity : BaseActivity(), View.OnClickListener, CollectionItemsListAdapter.RecyclerViewClick {


    private lateinit var collectionId: String
    var userCollectionsListModel = UserCollectionsListModel()
    private lateinit var collectionItemsListAdapter: CollectionItemsListAdapter
    private lateinit var collectionItemRecyclerView: RecyclerView
    private lateinit var setting: ImageView
    private var muteSwitch: SwitchCompat? = null
    private var collectionNameTextView: TextView? = null
    private var collectionImageVIEW: ImageView? = null
    private lateinit var followersCount: TextView
    private lateinit var followFollowingTextView: TextView
    private lateinit var rightArrow: ImageView
    private lateinit var shimmer1: ShimmerFrameLayout
    private lateinit var followersTextView: TextView
    private lateinit var back: ImageView
    private var share: ImageView? = null
    private var dataList = ArrayList<UserCollectionsModel>()
    private lateinit var itemNotAddedTextView: TextView


    override fun updateUi(response: Response?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_collection_item_activity)
        muteSwitch = findViewById<View>(R.id.muteVideoSwitch) as SwitchCompat
        collectionNameTextView = findViewById(R.id.collectionNameTextView)
        collectionImageVIEW = findViewById(R.id.collectionImageVIEW)
        shimmer1 = findViewById(R.id.shimmer1)
        rightArrow = findViewById(R.id.rightArrow)
        followersTextView = findViewById(R.id.followersTextView)
        followFollowingTextView = findViewById(R.id.followFollowingTextView)
        followersCount = findViewById(R.id.followersCount)
        share = findViewById(R.id.share)
        back = findViewById(R.id.back)
        itemNotAddedTextView = findViewById(R.id.itemNotAddedTextView)
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
        muteSwitch?.thumbTintList = thumbStates
        if (Build.VERSION.SDK_INT >= 24) {
            val trackStates = ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                    intArrayOf(

                            getColor(R.color.switch_button_green_collection), getColor(R.color.add_video_details_mute_label_50_percent_opacity))
            )
            muteSwitch?.trackTintList = trackStates

        }
        muteSwitch?.setOnClickListener {

            if (muteSwitch?.isChecked == true) {

                updateCollection(delete = false, isPublic = true)
            } else {
                updateCollection(delete = false, isPublic = false)
            }
        }
        val linearLayoutManager = LinearLayoutManager(this)
        collectionItemsListAdapter = CollectionItemsListAdapter(this@UserCollectionItemListActivity, this)
        collectionItemRecyclerView.layoutManager = linearLayoutManager
        collectionItemRecyclerView.adapter = collectionItemsListAdapter

        getUserCollectionItems(0)
        setting.setOnClickListener {
            chooseImageOptionPopUp(it)
        }

        followFollowingTextView.setOnClickListener {
            followUnfollow()
        }

        back.setOnClickListener(this)
        followersCount.setOnClickListener(this)
        followersTextView.setOnClickListener(this)
        rightArrow.setOnClickListener(this)
        share?.setOnClickListener(this)

        collectionItemRecyclerView.addOnScrollListener(object : EndlessScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getUserCollectionItems(totalItemsCount)
            }
        })
    }


    private fun chooseImageOptionPopUp(view: View) {
        val popup = PopupMenu(this@UserCollectionItemListActivity, view)
        popup.menuInflater.inflate(R.menu.delete_edit_collection_menu, popup.menu)
        if (userCollectionsListModel.collectionType == 0)
            popup.menu.findItem(R.id.edit_collection).isVisible = true
        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                var id = item?.itemId
                if (id == R.id.delete_collection) {
                    updateCollection(delete = true, isPublic = true)
                    return true
                } else if (id == R.id.edit_collection) {
                    val intent = Intent(this@UserCollectionItemListActivity, EditCollectionActivity::class.java)
                    intent.putExtra("collectionId", collectionId)
                    startActivityForResult(intent, 1000)
                }
                return false
            }
        })

        val menuHelper = MenuPopupHelper(this@UserCollectionItemListActivity, popup.menu as MenuBuilder, view)
        menuHelper.setForceShowIcon(true)
        menuHelper.show()
    }


    fun getUserCollectionItems(start: Int) {
        BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java).getUserCollectionItems(collectionId, start, 10).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<UserCollectionsListModel>> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<UserCollectionsListModel>) {
                try {
                    if (response.code == 200 && response.status == Constants.SUCCESS && response.data?.result != null) {
                        userCollectionsListModel = response.data!!.result
                        if (start == 0) {
                            if (userCollectionsListModel.collectionItems.isEmpty()) {
                                itemNotAddedTextView.visibility = View.VISIBLE
                            } else {
                                itemNotAddedTextView.visibility = View.GONE

                            }
                            collectionNameTextView?.text = userCollectionsListModel.name
                            if (userCollectionsListModel.isPublic) {
                                muteSwitch?.isChecked = true
                                followFollowingTextView
                            }

                            if (AppUtils.isPrivateProfile(userCollectionsListModel.userId)) {
                                followersCount.isClickable = true
                                followersTextView.isClickable = true
                                rightArrow.visibility = View.GONE
                                followFollowingTextView.visibility = View.GONE
                                muteSwitch?.visibility = View.VISIBLE
                                setting.visibility = View.VISIBLE
                                share?.visibility = View.VISIBLE


                            } else {
                                share?.visibility = View.GONE
                                followersCount.isClickable = false
                                followersTextView.isClickable = false
                                rightArrow.visibility = View.GONE
                                followFollowingTextView.visibility = View.VISIBLE
                                if (AppConstants.FOLLOWING == userCollectionsListModel.isFollowed) {
                                    followFollowingTextView.text = resources.getString(R.string.ad_following_author)
                                } else {
                                    followFollowingTextView.text = resources.getString(R.string.ad_follow_author)
                                }
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
                        }
                        shimmer1.visibility = View.GONE
                        shimmer1.stopShimmerAnimation()


                        dataList.addAll(0, userCollectionsListModel.collectionItems)
                        collectionItemsListAdapter.setListData(dataList)
                        collectionItemsListAdapter.notifyDataSetChanged()

                    } else {
                        ToastUtils.showToast(this@UserCollectionItemListActivity, "nhi hua ")

                    }

                } catch (e: Exception) {

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
        updateCollectionRequestModel.isPublic = isPublic
        val list = ArrayList<String>()
        list.add(collectionId)
        updateCollectionRequestModel.userCollectionId = list
        BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java).editCollection(updateCollectionRequestModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<AddCollectionRequestModel>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponseGeneric<AddCollectionRequestModel>) {
                if (t.code == 200 && t.status == Constants.SUCCESS) {
                    ToastUtils.showToast(this@UserCollectionItemListActivity, t.data?.msg)
                    if (delete)
                        finish()
                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
                ToastUtils.showToast(this@UserCollectionItemListActivity, e.message)
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


    private fun followUnfollow() {
        val addCollectionRequestModel = AddCollectionRequestModel()
        if (AppConstants.FOLLOWING == userCollectionsListModel.isFollowed) {
            addCollectionRequestModel.deleted = true
            addCollectionRequestModel.userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
            addCollectionRequestModel.userCollectionId = collectionId
        } else {
            addCollectionRequestModel.deleted = false
            addCollectionRequestModel.userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
            addCollectionRequestModel.userCollectionId = collectionId
        }

        BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java).followUnfollowCollection(addCollectionRequestModel = addCollectionRequestModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<ResponseBody> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: ResponseBody) {

                try {
                    val strResponse = String(t.bytes())
                    val jsonObject = JSONObject(strResponse)

                    val arr = jsonObject.getJSONObject("data").getString("msg")
                    ToastUtils.showToast(this@UserCollectionItemListActivity, arr)
                    if (AppConstants.FOLLOWING == userCollectionsListModel.isFollowed) {
                        userCollectionsListModel.isFollowed = AppConstants.FOLLOW
                        followFollowingTextView.text = resources.getString(R.string.ad_follow_author)
                    } else {
                        userCollectionsListModel.isFollowed = AppConstants.FOLLOWING
                        followFollowingTextView.text = resources.getString(R.string.ad_following_author)
                    }
                } catch (e: Exception) {
                }
            }

            override fun onError(e: Throwable) {
                ToastUtils.showToast(this@UserCollectionItemListActivity, "unsuccessful")
            }
        })


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.followersCount, R.id.rightArrow, R.id.followersTextView -> {
                val intentt = Intent(this@UserCollectionItemListActivity, FollowersAndFollowingListActivity::class.java)
                intentt.putExtra(AppConstants.FOLLOW_LIST_TYPE, AppConstants.COLLECTION_FOLLOWING_LIST)
                intentt.putExtra("collectionId", collectionId)
                startActivity(intentt)
            }
            R.id.back -> {
                finish()
            }
            R.id.share -> {
                userCollectionsListModel.shareUrl?.let {
                    val contentStr = userCollectionsListModel.shareUrl
                    val shareIntent = ShareCompat.IntentBuilder.from(this@UserCollectionItemListActivity)
                            .setType("text/plain")
                            .setText(contentStr)
                            .intent
                    startActivity(shareIntent)
                }
            }
        }
    }


    override fun onRecyclerViewclick(position: Int) {
        val intent = Intent(this@UserCollectionItemListActivity, ArticleDetailsContainerActivity::class.java)
        intent.putExtra(Constants.ARTICLE_ID, dataList[position].item_info.id)
        startActivity(intent)
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

}

