package com.mycity4kids.ui.activity.collection

import android.os.Bundle
import android.view.View
import android.widget.TextView
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
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.adapter.AddCollectionAdapter
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AddCollectionActivity : BaseActivity(), AddCollectionAdapter.RecyclerViewClickListener {
    override fun onClick(position: Int) {
        addCollectionItem(position)
    }


    lateinit var addCollectionRecyclerView: RecyclerView
    lateinit var addCollectionAdapter: AddCollectionAdapter
    lateinit var userCollectionsListModel: UserCollectionsListModel
    lateinit var articleId: String
    lateinit var shimmer1: ShimmerFrameLayout
    lateinit var addNewTextView: TextView


    override fun updateUi(response: Response?) {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_collection_activity)
        addCollectionRecyclerView = findViewById(R.id.addCollectionRecyclerView)
        addNewTextView = findViewById(R.id.addNewTextView)
        shimmer1 = findViewById(R.id.shimmer1)
        val linearLayoutManager = LinearLayoutManager(this)
        addCollectionAdapter = AddCollectionAdapter(this@AddCollectionActivity, this, adapterViewType = false)
        addCollectionRecyclerView.layoutManager = linearLayoutManager
        addCollectionRecyclerView.adapter = addCollectionAdapter
        val intent = intent
        if (intent != null)
            articleId = intent.getStringExtra("articleId")
        getUserCreatedCollections()
        addNewTextView.setOnClickListener {

        }
    }

    fun getUserCreatedCollections() {
        showProgressDialog("please wait")
        BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).getUserCollectionList(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId, 0, 20).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<UserCollectionsListModel>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<UserCollectionsListModel>) {
                if (response.code == 200 && response.status == "success" && response.data?.result != null) {
                    shimmer1.stopShimmerAnimation()
                    shimmer1.visibility = View.GONE
                    userCollectionsListModel = response.data!!.result
                    addCollectionAdapter.setListData(userCollectionsListModel)
                    addCollectionAdapter.notifyDataSetChanged()
                } else {
                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
            }

        })
    }

    fun addCollectionItem(position: Int) {
        showProgressDialog("please wait")
        val addCollectionRequestModel1 = UpdateCollectionRequestModel()
        addCollectionRequestModel1.userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        addCollectionRequestModel1.itemType = "2"
        val List = ArrayList<String>()
        List.add(userCollectionsListModel.collections_list[position].userCollectionId)
        addCollectionRequestModel1.userCollectionId = List
        addCollectionRequestModel1.item = articleId
        BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).addCollectionItem(addCollectionRequestModel1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<AddCollectionRequestModel>> {
            override fun onComplete() {
                removeProgressDialog()

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponseGeneric<AddCollectionRequestModel>) {
                if (t.code == 200 && t.status == "success" && t.data?.result != null) {


                    ToastUtils.showToast(this@AddCollectionActivity, "item added in collection successfully")


                } else {
                    ToastUtils.showToast(this@AddCollectionActivity, "item  haven't added in collection successfully")

                }


            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
                ToastUtils.showToast(this@AddCollectionActivity, "item  haven't added in collection successfully , some error at the server ")

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
}