package com.mycity4kids.ui.activity.collection

import android.os.Bundle
import android.util.Log
import android.view.View
import com.crashlytics.android.Crashlytics
import com.google.android.material.tabs.TabLayout
import com.kelltontech.ui.BaseActivity
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.collectionsModels.AddCollectionRequestModel
import com.mycity4kids.models.collectionsModels.UpdateCollectionRequestModel
import com.mycity4kids.models.response.ArticleListingResult
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.models.response.MixFeedResult
import com.mycity4kids.models.response.UserDetailResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.adapter.MultipleCollectionItemPagerAdapter
import kotlinx.android.synthetic.main.multiple_collection_item_activity.*
import retrofit2.Call
import retrofit2.Callback

class AddMultipleCollectionItemActivity : BaseActivity(), View.OnClickListener {
    private lateinit var multipleCollectionItemPagerAdapter: MultipleCollectionItemPagerAdapter
    private lateinit var collectionId: String
    private var userCreatedSelectedItemList: List<MixFeedResult>? = null
    private var userReadSelectedList: List<ArticleListingResult>? = null
    private var multipleCollectionList: ArrayList<UpdateCollectionRequestModel>? = null
    private var multipleUserCreatedCollectionList: ArrayList<UpdateCollectionRequestModel>? = null
    private var mixedReadAndCreatedSelectedList: HashSet<UpdateCollectionRequestModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.multiple_collection_item_activity)
        collectionId = intent.getStringExtra("collectionId")
        toolbarTitleTextView.text = getString(R.string.please_add_item_multicollection_activity)
        getUserDetail(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId)
        add.setOnClickListener(this)
        skipTextView.setOnClickListener(this)
        back.setOnClickListener(this)
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }
        })
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.add -> {

                multipleUserCreatedCollectionList = ArrayList()
                multipleUserCreatedCollectionList?.clear()
                userCreatedSelectedItemList?.forEach { data ->
                    val updateCollectionRequestModel = UpdateCollectionRequestModel()
                    val list = ArrayList<String>()
                    list.add(collectionId)
                    updateCollectionRequestModel.userCollectionId = list
                    updateCollectionRequestModel.userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
                    updateCollectionRequestModel.item = data.id
                    updateCollectionRequestModel.itemType = data.itemType
                    val dataList = ArrayList<UpdateCollectionRequestModel>()
                    dataList.add(updateCollectionRequestModel)
                    multipleUserCreatedCollectionList!!.addAll(dataList)
                }
                multipleCollectionList = ArrayList()
                multipleCollectionList?.clear()
                userReadSelectedList?.forEach { data ->
                    val updateCollectionRequestModel = UpdateCollectionRequestModel()
                    val list = ArrayList<String>()
                    list.add(collectionId)
                    updateCollectionRequestModel.userCollectionId = list
                    updateCollectionRequestModel.userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
                    updateCollectionRequestModel.item = data.id
                    updateCollectionRequestModel.itemType = AppConstants.ARTICLE_COLLECTION_TYPE
                    val dataList = ArrayList<UpdateCollectionRequestModel>()
                    dataList.add(updateCollectionRequestModel)
                    multipleCollectionList!!.addAll(dataList)
                }
                mixedReadAndCreatedSelectedList = HashSet()
                if (!multipleCollectionList.isNullOrEmpty()) {
                    mixedReadAndCreatedSelectedList!!.addAll(multipleCollectionList!!)
                }
                if (!multipleUserCreatedCollectionList.isNullOrEmpty()) {
                    mixedReadAndCreatedSelectedList!!.addAll(multipleUserCreatedCollectionList!!)
                }
                if (!mixedReadAndCreatedSelectedList.isNullOrEmpty()) {
                    postDataToServer(mixedReadAndCreatedSelectedList!!)
                }
            }
            R.id.back -> {
                finish()
            }
            R.id.skipTextView -> {
                finish()
            }
        }
    }

    private fun postDataToServer(finalList: HashSet<UpdateCollectionRequestModel>) {
        val listData = finalList.toMutableList() as ArrayList<UpdateCollectionRequestModel>
        showProgressDialog(getString(R.string.please_wait))
        val retro = BaseApplication.getInstance().retrofit
        val collectionApi = retro.create(CollectionsAPI::class.java)
        val call = collectionApi.addMultipleCollectionItem(listData)
        call.enqueue(object : Callback<BaseResponseGeneric<AddCollectionRequestModel>> {
            override fun onFailure(call: Call<BaseResponseGeneric<AddCollectionRequestModel>>, t: Throwable) {
                removeProgressDialog()
                Crashlytics.logException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }

            override fun onResponse(call: Call<BaseResponseGeneric<AddCollectionRequestModel>>, response: retrofit2.Response<BaseResponseGeneric<AddCollectionRequestModel>>) {
                removeProgressDialog()
                if (response.body() == null) {
                    return
                }
                try {
                    val responsee = response.body()
                    if (responsee?.code == 200 && responsee.status == "success" && !responsee.data?.result?.listItemId.isNullOrEmpty()) {
                        ToastUtils.showToast(this@AddMultipleCollectionItemActivity, responsee.data?.msg)
                        finish()
                    } else {
                        ToastUtils.showToast(this@AddMultipleCollectionItemActivity, responsee?.data?.msg)
                    }
                } catch (t: Exception) {
                    Crashlytics.logException(t)
                    Log.d("MC4kException", Log.getStackTraceString(t))
                }
            }

        })

    }

    private fun getUserDetail(authorId: String) {
        showProgressDialog(getString(R.string.please_wait))
        val retrofit = BaseApplication.getInstance().retrofit
        val bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI::class.java)
        val call = bloggerDashboardAPI.getBloggerData(authorId)
        call.enqueue(object : Callback<UserDetailResponse> {
            override fun onFailure(call: Call<UserDetailResponse>, e: Throwable) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }

            override fun onResponse(call: Call<UserDetailResponse>, response: retrofit2.Response<UserDetailResponse>) {
                removeProgressDialog()
                if (null == response.body()) {
                    return
                }
                try {
                    val responseData = response.body() as UserDetailResponse
                    if (responseData.code == 200 && Constants.SUCCESS == responseData.status) {
                        multipleCollectionItemPagerAdapter = if (responseData.data[0].result.totalArticles != "0") {
                            tabLayout.apply {
                                addTab(tabLayout.newTab().setText("READ"))
                                addTab(tabLayout.newTab().setText("CREATED"))
                            }
                            MultipleCollectionItemPagerAdapter(supportFragmentManager, isCreatedArticle = true, collectionId = collectionId)
                        } else {
                            tabLayout.visibility = View.GONE
                            MultipleCollectionItemPagerAdapter(supportFragmentManager, isCreatedArticle = false, collectionId = collectionId)
                        }
                        viewPager.adapter = multipleCollectionItemPagerAdapter
                    }
                } catch (e: Exception) {
                    removeProgressDialog()
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }

            }
        })


    }

    fun getUserCreatedList(dataList: List<MixFeedResult>?) {
        userCreatedSelectedItemList = dataList
    }

    fun getUserReadList(dataList: List<ArticleListingResult>?) {
        userReadSelectedList = dataList
    }
}