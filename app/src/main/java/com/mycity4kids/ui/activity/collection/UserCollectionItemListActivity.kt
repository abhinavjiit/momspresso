package com.mycity4kids.ui.activity.collection

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.collectionsModels.AddCollectionRequestModel
import com.mycity4kids.models.collectionsModels.UpdateCollectionRequestModel
import com.mycity4kids.models.collectionsModels.UserCollectionsListModel
import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity
import com.mycity4kids.ui.activity.FollowersAndFollowingListActivity
import com.mycity4kids.ui.activity.ParallelFeedActivity
import com.mycity4kids.ui.activity.ShortStoryContainerActivity
import com.mycity4kids.ui.adapter.CollectionItemsListAdapter
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.EndlessScrollListener
import com.mycity4kids.utils.ToastUtils
import com.squareup.picasso.Picasso
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.InputStreamReader
import okhttp3.ResponseBody
import org.json.JSONObject

class UserCollectionItemListActivity : BaseActivity(), View.OnClickListener,
    CollectionItemsListAdapter.RecyclerViewClick {

    private lateinit var collectionId: String
    var userCollectionsListModel = UserCollectionsListModel()
    private lateinit var collectionItemsListAdapter: CollectionItemsListAdapter
    private lateinit var collectionItemRecyclerView: RecyclerView
    private lateinit var setting: ImageView
    private var visibleToAll: SwitchCompat? = null
    private var collectionNameTextView: TextView? = null
    private var collectionImageVIEW: ImageView? = null
    private lateinit var followersCount: TextView
    private lateinit var followFollowingTextView: TextView
    private lateinit var rightArrow: ImageView
    private lateinit var shimmer1: ShimmerFrameLayout
    private lateinit var followersTextView: TextView
    private var share: ImageView? = null
    private var dataList = ArrayList<UserCollectionsModel>()
    private lateinit var itemNotAddedTextView: TextView
    private lateinit var deleteCollectionMainLayout: FrameLayout
    private lateinit var confirmTextView: TextView
    private lateinit var cancel: ImageView
    private lateinit var descriptionTextView: TextView
    private lateinit var collectionDescription: TextView
    private lateinit var toolbar: Toolbar
    private var collectionPos: Int = 0
    private var editType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_collection_item_activity)
        toolbar = findViewById(R.id.toolbar)
        visibleToAll = findViewById<View>(R.id.muteVideoSwitch) as SwitchCompat
        collectionNameTextView = findViewById(R.id.collectionNameTextView)
        collectionImageVIEW = findViewById(R.id.collectionImageVIEW)
        shimmer1 = findViewById(R.id.shimmer1)
        rightArrow = findViewById(R.id.rightArrow)
        followersTextView = findViewById(R.id.followersTextView)
        followFollowingTextView = findViewById(R.id.followFollowingTextView)
        followersCount = findViewById(R.id.followersCount)
        share = findViewById(R.id.share)
        itemNotAddedTextView = findViewById(R.id.itemNotAddedTextView)
        confirmTextView = findViewById(R.id.confirmTextView)
        deleteCollectionMainLayout = findViewById(R.id.deleteCollectionMainLayout)
        collectionNameTextView?.isSelected = true
        setting = findViewById(R.id.setting)
        cancel = findViewById(R.id.cancel)
        collectionDescription = findViewById(R.id.collectionDescription)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        collectionItemRecyclerView = findViewById(R.id.collectionItemRecyclerView)
        val intent = intent
        collectionId = intent.getStringExtra("id")
        collectionPos = intent.getIntExtra("position", 0)
        val thumbStates = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(
                ContextCompat.getColor(this@UserCollectionItemListActivity, R.color.white_color),
                ContextCompat.getColor(
                    this@UserCollectionItemListActivity,
                    R.color.add_video_details_mute_label
                )
            )
        )
        visibleToAll?.thumbTintList = thumbStates
        if (Build.VERSION.SDK_INT >= 24) {
            val trackStates = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                intArrayOf(
                    ContextCompat.getColor(
                        this@UserCollectionItemListActivity,
                        R.color.switch_button_green_collection
                    ),
                    ContextCompat.getColor(this@UserCollectionItemListActivity, R.color.white_color)
                )
            )
            visibleToAll?.trackTintList = trackStates
        }
        visibleToAll?.setOnClickListener {
            if (visibleToAll?.isChecked == true) {
                updateCollection(delete = false, isPublic = true)
            } else {
                updateCollection(delete = false, isPublic = false)
            }
        }
        val linearLayoutManager = LinearLayoutManager(this)
        collectionItemsListAdapter =
            CollectionItemsListAdapter(this@UserCollectionItemListActivity, this)
        collectionItemRecyclerView.layoutManager = linearLayoutManager
        collectionItemRecyclerView.adapter = collectionItemsListAdapter

        getUserCollectionItems(0)
        setting.setOnClickListener {
            chooseImageOptionPopUp(it)
        }
        followFollowingTextView.setOnClickListener {
            followUnfollow()
        }
        collectionItemRecyclerView.addOnScrollListener(object :
            EndlessScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getUserCollectionItems(totalItemsCount)
            }
        })
        collectionNameTextView?.setOnClickListener(this)
        followersCount.setOnClickListener(this)
        followersTextView.setOnClickListener(this)
        rightArrow.setOnClickListener(this)
        share?.setOnClickListener(this)
        confirmTextView.setOnClickListener(this)
        cancel.setOnClickListener(this)
    }

    private fun chooseImageOptionPopUp(view: View) {
        val popup = PopupMenu(this@UserCollectionItemListActivity, view)
        popup.menuInflater.inflate(R.menu.delete_edit_collection_menu, popup.menu)
        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                var id = item?.itemId
                if (id == R.id.delete_collection) {
                    deleteCollectionMainLayout.visibility = View.VISIBLE
                    return true
                } else if (id == R.id.edit_collection) {
                    val intent = Intent(
                        this@UserCollectionItemListActivity,
                        EditCollectionActivity::class.java
                    )
                    intent.putExtra("collectionId", collectionId)
                    startActivityForResult(intent, 1000)
                }
                return false
            }
        })

        val menuHelper =
            MenuPopupHelper(this@UserCollectionItemListActivity, popup.menu as MenuBuilder, view)
        menuHelper.setForceShowIcon(true)
        menuHelper.show()
    }

    fun getUserCollectionItems(start: Int) {
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
                            userCollectionsListModel = response.data!!.result
                            setFirstCallData(start)
                            shimmer1.visibility = View.GONE
                            shimmer1.stopShimmerAnimation()
                            dataList.addAll(userCollectionsListModel.collectionItems)
                            collectionItemsListAdapter.setListData(dataList)
                            collectionItemsListAdapter.notifyDataSetChanged()
                        } else {
                            ToastUtils.showToast(
                                this@UserCollectionItemListActivity,
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

    private fun updateCollection(delete: Boolean, isPublic: Boolean) {
        showProgressDialog(resources.getString(R.string.please_wait))
        val updateCollectionRequestModel = UpdateCollectionRequestModel()
        updateCollectionRequestModel.deleted = delete
        updateCollectionRequestModel.isPublic = isPublic
        val list = ArrayList<String>()
        list.add(collectionId)
        updateCollectionRequestModel.userCollectionId = list
        BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java)
            .editCollection(updateCollectionRequestModel).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<BaseResponseGeneric<AddCollectionRequestModel>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: BaseResponseGeneric<AddCollectionRequestModel>) {
                    if (t.code == 200 && t.status == Constants.SUCCESS) {
                        ToastUtils.showToast(this@UserCollectionItemListActivity, t.data?.msg)
                        if (delete) {
                            val intent = Intent()
                            intent.putExtra(AppConstants.COLLECTION_EDIT_TYPE, "deleteCollection")
                            intent.putExtra("CollectionId", collectionId)
                            intent.putExtra("collectionPos", collectionPos + 1)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    removeProgressDialog()
                    FirebaseCrashlytics.getInstance().recordException(e)
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

    private fun followUnfollow() {
        val addCollectionRequestModel = AddCollectionRequestModel()
        if (AppConstants.FOLLOWING == userCollectionsListModel.isFollowed) {
            addCollectionRequestModel.deleted = true
            addCollectionRequestModel.userId =
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
            addCollectionRequestModel.userCollectionId = collectionId
            Utils.pushProfileEvents(
                this, "CTA_Unfollow_Collection_Detail", "UserCollectionItemListActivity",
                "Unfollow", "-"
            )
        } else {
            addCollectionRequestModel.deleted = false
            addCollectionRequestModel.userId =
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
            addCollectionRequestModel.userCollectionId = collectionId
            Utils.pushProfileEvents(
                this, "CTA_Follow_Collection_Detail", "UserCollectionItemListActivity",
                "Follow", "-"
            )
        }

        BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java)
            .followUnfollowCollection(addCollectionRequestModel = addCollectionRequestModel)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ResponseBody> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: ResponseBody) {
                    try {
                        val strResponse = String(t.bytes())
                        val jsonObject = JSONObject(strResponse)
                        val code = jsonObject.getInt("code")
                        if (code == 200) {
                            val arr = jsonObject.getJSONObject("data").getString("msg")
                            ToastUtils.showToast(this@UserCollectionItemListActivity, arr)
                            if (AppConstants.FOLLOWING == userCollectionsListModel.isFollowed) {
                                userCollectionsListModel.isFollowed = AppConstants.FOLLOW
                                followFollowingTextView.text =
                                    resources.getString(R.string.ad_follow_author)
                                followersCount.text =
                                    ((userCollectionsListModel.totalCollectionFollowers)?.minus(1)).toString()
                                userCollectionsListModel.totalCollectionFollowers =
                                    (userCollectionsListModel.totalCollectionFollowers)?.minus(1)
                            } else {
                                followersCount.text =
                                    ((userCollectionsListModel.totalCollectionFollowers)?.plus(1)).toString()
                                userCollectionsListModel.totalCollectionFollowers =
                                    (userCollectionsListModel.totalCollectionFollowers)?.plus(1)
                                userCollectionsListModel.isFollowed = AppConstants.FOLLOWING
                                followFollowingTextView.text =
                                    resources.getString(R.string.ad_following_author)
                            }
                        } else {
                            val reason = jsonObject.getString("reason")
                            ToastUtils.showToast(this@UserCollectionItemListActivity, reason)
                        }
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4KException", Log.getStackTraceString(e))
                        ToastUtils.showToast(
                            this@UserCollectionItemListActivity,
                            "something went wrong"
                        )
                    }
                }

                override fun onError(e: Throwable) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4KException", Log.getStackTraceString(e))
                    val code = (e as retrofit2.HttpException).code()
                    if (code == 402) {
                        val data = e.response()?.errorBody()!!.byteStream()
                        val jsonParser = JsonParser()
                        val jsonObject = jsonParser.parse(
                            InputStreamReader(data, "UTF-8")
                        ) as JsonObject
                        val reason = jsonObject.get("reason")
                        Toast.makeText(
                            this@UserCollectionItemListActivity,
                            reason.asString,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Log.e("exception in error", e.message.toString())
                }
            })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.followersCount, R.id.rightArrow, R.id.followersTextView -> {
                val intentt = Intent(
                    this@UserCollectionItemListActivity,
                    FollowersAndFollowingListActivity::class.java
                )
                intentt.putExtra(
                    AppConstants.FOLLOW_LIST_TYPE,
                    AppConstants.COLLECTION_FOLLOWING_LIST
                )
                intentt.putExtra("collectionId", collectionId)
                startActivity(intentt)
            }
            R.id.collectionNameTextView -> {
                val intent = Intent()
                intent.putExtra(AppConstants.COLLECTION_EDIT_TYPE, "editCollection")
                intent.putExtra("collectionName", collectionNameTextView?.text.toString())
                intent.putExtra("collectionImage", userCollectionsListModel.imageUrl)
                intent.putExtra("CollectionId", collectionId)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            R.id.share -> {
                userCollectionsListModel.shareUrl?.let {
                    val contentStr = userCollectionsListModel.shareUrl
                    val shareIntent =
                        ShareCompat.IntentBuilder.from(this@UserCollectionItemListActivity)
                            .setType("text/plain")
                            .setText(contentStr)
                            .intent
                    startActivity(shareIntent)
                    Utils.pushProfileEvents(
                        this@UserCollectionItemListActivity, "CTA_Share_Private_Collection_Detail",
                        "UserCollectionItemListActivity", "Share", "-"
                    )
                }
            }
            R.id.confirmTextView -> {
                updateCollection(delete = true, isPublic = true)
            }
            R.id.cancel -> {
                deleteCollectionMainLayout.visibility = View.GONE
            }
        }
    }

    override fun onRecyclerViewclick(position: Int) {
        when {
            dataList[position].itemType == AppConstants.ARTICLE_COLLECTION_TYPE -> {
                val intent = Intent(
                    this@UserCollectionItemListActivity,
                    ArticleDetailsContainerActivity::class.java
                )
                intent.putExtra(Constants.ARTICLE_ID, dataList[position].item_info.id)
                startActivity(intent)
            }
            dataList[position].itemType == AppConstants.VIDEO_COLLECTION_TYPE -> {
                val intent =
                    Intent(this@UserCollectionItemListActivity, ParallelFeedActivity::class.java)
                intent.putExtra(Constants.STREAM_URL, dataList[position].item_info.streamUrl)
                intent.putExtra(Constants.VIDEO_ID, dataList[position].item)
                intent.putExtra(Constants.AUTHOR_ID, dataList[position].item_info.author.id)
                startActivity(intent)
            }
            dataList[position].itemType == AppConstants.SHORT_STORY_COLLECTION_TYPE -> {
                val intent = Intent(
                    this@UserCollectionItemListActivity,
                    ShortStoryContainerActivity::class.java
                )
                intent.putExtra(Constants.ARTICLE_ID, dataList[position].item)
                intent.putExtra(Constants.AUTHOR_ID, dataList[position].item_info.userId)
                intent.putExtra(Constants.BLOG_SLUG, dataList[position].item_info.blogTitleSlug)
                intent.putExtra(Constants.TITLE_SLUG, dataList[position].item_info.titleSlug)
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == 1000 && data != null) {
                    val position = data.getIntExtra("deletedItemPosition", -1)
                    if (position != -1) {
                        dataList.removeAt(position)
                        if (dataList.isEmpty()) {
                            itemNotAddedTextView.visibility = View.VISIBLE
                        }
                        collectionItemsListAdapter.notifyDataSetChanged()
                    }
                    if (data.hasExtra("collectionName")) {
                        editType = "editCollection"
                        val collectionName = data.getStringExtra("collectionName")
                        collectionNameTextView?.text = collectionName
                        userCollectionsListModel.summary =
                            data.getStringExtra("collectionDescription")
                        if (!userCollectionsListModel.summary.isNullOrBlank()) {
                            collectionDescription.visibility = View.VISIBLE
                            descriptionTextView.visibility = View.VISIBLE
                            collectionDescription.text = userCollectionsListModel.summary
                        } else {
                            collectionDescription.visibility = View.GONE
                            descriptionTextView.visibility = View.GONE
                        }
                        try {
                            userCollectionsListModel.imageUrl =
                                data.getStringExtra("collectionImage")
                            Picasso.get().load(data.getStringExtra("collectionImage"))
                                .placeholder(R.drawable.default_article)
                                .error(R.drawable.default_article).into(collectionImageVIEW)
                        } catch (e: Exception) {
                            collectionImageVIEW?.setImageResource(R.drawable.default_article)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4KException", Log.getStackTraceString(e))
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(AppConstants.COLLECTION_EDIT_TYPE, editType)
        intent.putExtra("collectionName", collectionNameTextView?.text.toString())
        intent.putExtra("collectionImage", userCollectionsListModel.imageUrl)
        intent.putExtra("CollectionId", collectionId)
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }

    fun setFirstCallData(start: Int) {
        if (start == 0) {
            collectionNameTextView?.text = userCollectionsListModel.name
            if (userCollectionsListModel.isPublic) {
                visibleToAll?.isChecked = true
                followFollowingTextView
            }

            if (AppUtils.isPrivateProfile(userCollectionsListModel.userId)) {
                followersCount.isClickable = true
                followersTextView.isClickable = true
                rightArrow.visibility = View.GONE
                followFollowingTextView.visibility = View.GONE
                if (userCollectionsListModel.collectionItems.isEmpty()) {
                    itemNotAddedTextView.visibility = View.VISIBLE
                    itemNotAddedTextView.text = getString(R.string.no_collection_item_text)
                } else {
                    itemNotAddedTextView.visibility = View.GONE
                }
                if (userCollectionsListModel.collectionType != 0) {
                    setting.visibility = View.GONE
                    visibleToAll?.visibility = View.GONE
                } else {
                    followersTextView.visibility = View.VISIBLE
                    followersCount.visibility = View.VISIBLE
                    visibleToAll?.visibility = View.VISIBLE
                    setting.visibility = View.VISIBLE
                }
                share?.visibility = View.VISIBLE
                if (!userCollectionsListModel.summary.isNullOrBlank()) {
                    descriptionTextView.visibility = View.VISIBLE
                    collectionDescription.visibility = View.VISIBLE
                    collectionDescription.text = userCollectionsListModel.summary
                }
                Utils.pushGenericEvent(
                    this@UserCollectionItemListActivity, "Show_Private_Collection_Detail",
                    userCollectionsListModel.userId, "UserCollectionItemListActivity"
                )
            } else {
                if (userCollectionsListModel.collectionItems.isEmpty()) {
                    itemNotAddedTextView.visibility = View.VISIBLE
                    itemNotAddedTextView.text = getString(R.string.no_collection_items)
                } else {
                    itemNotAddedTextView.visibility = View.GONE
                }
                followersCount.isClickable = false
                followersTextView.isClickable = false
                rightArrow.visibility = View.GONE
                followFollowingTextView.visibility = View.VISIBLE
                if (AppConstants.FOLLOWING == userCollectionsListModel.isFollowed) {
                    followFollowingTextView.text = resources.getString(R.string.ad_following_author)
                } else {
                    followFollowingTextView.text = resources.getString(R.string.ad_follow_author)
                }
                visibleToAll?.visibility = View.GONE
                setting.visibility = View.GONE
                Utils.pushGenericEvent(
                    this@UserCollectionItemListActivity, "Show_Public_Collection_Detail",
                    userCollectionsListModel.userId, "UserCollectionItemListActivity"
                )
            }
            followersCount.text = userCollectionsListModel.totalCollectionFollowers.toString()
            try {
                Picasso.get().load(userCollectionsListModel.imageUrl)
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                    .into(collectionImageVIEW)
            } catch (e: Exception) {
                collectionImageVIEW?.setImageResource(R.drawable.default_article)
            }
        }
    }
}
