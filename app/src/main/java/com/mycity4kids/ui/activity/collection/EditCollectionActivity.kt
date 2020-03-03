package com.mycity4kids.ui.activity.collection

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.collectionsModels.AddCollectionRequestModel
import com.mycity4kids.models.collectionsModels.UpdateCollectionRequestModel
import com.mycity4kids.models.collectionsModels.UserCollectionsListModel
import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.adapter.AddCollectionAdapter
import com.mycity4kids.ui.fragment.CollectionThumbnailImageChangeDialogFragmnet
import com.mycity4kids.utils.EndlessScrollListener
import com.mycity4kids.utils.StringUtils
import com.mycity4kids.utils.ToastUtils
import com.squareup.picasso.Picasso
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.InputStreamReader
import retrofit2.HttpException

class EditCollectionActivity : BaseActivity(), AddCollectionAdapter.RecyclerViewClickListener, CollectionThumbnailImageChangeDialogFragmnet.SendImage {
    override fun onsendData(imageUrl: String) {
        try {
            Picasso.get().load(imageUrl)
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(collectionImageView)
            userCollectionsListModel.imageUrl = imageUrl
        } catch (e: Exception) {
            collectionImageView.setImageResource(R.drawable.default_article)
        }
    }

    override fun onClick(position: Int) {
        deleteCollectionItem(position)
    }

    private lateinit var collectionItemRecyclerView: RecyclerView
    private lateinit var collectionItemsListAdapter: AddCollectionAdapter
    var userCollectionsListModel = UserCollectionsListModel()
    private lateinit var collectionId: String
    private lateinit var collectionImageView: ImageView
    private lateinit var submit: TextView
    private lateinit var collectionNameChangeEditTextView: EditText
    private lateinit var collectionImageChangeTextView: TextView
    private var dataList = ArrayList<UserCollectionsModel>()
    private lateinit var shimmer1: ShimmerFrameLayout
    private var descriptionEditTextView: EditText? = null
    private lateinit var itemNotAddedTextView: TextView
    private var deletedItemPosition: Int = -1
    private lateinit var back: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_collection_activity)
        collectionItemRecyclerView = findViewById(R.id.collectionItemRecyclerView)
        collectionImageView = findViewById(R.id.collectionImageVIEW)
        collectionNameChangeEditTextView = findViewById(R.id.collectionNameChangeEditTextView)
        collectionImageChangeTextView = findViewById(R.id.collectionImageChangeTextView)
        descriptionEditTextView = findViewById(R.id.descriptionEditTextView)
        submit = findViewById(R.id.submit)
        shimmer1 = findViewById(R.id.shimmer1)
        itemNotAddedTextView = findViewById(R.id.itemNotAddedTextView)
        back = findViewById(R.id.back)
        val bundle = intent
        collectionId = bundle.getStringExtra("collectionId")
        val linearLayoutManager = LinearLayoutManager(this)
        collectionItemsListAdapter = AddCollectionAdapter(this@EditCollectionActivity, this, adapterViewType = true)
        collectionItemRecyclerView.layoutManager = linearLayoutManager
        collectionItemRecyclerView.adapter = collectionItemsListAdapter
        getUserCollectionItems(0)
        submit.setOnClickListener {
            if (isValid())
                editCollection()
        }
        back.setOnClickListener {
            val intent = Intent()
            intent.putExtra("deletedItemPosition", deletedItemPosition)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        collectionImageChangeTextView.setOnClickListener {
            try {
                val collectionThumbnailImageChangeDialogFragmnet = CollectionThumbnailImageChangeDialogFragmnet()
                val fm = supportFragmentManager
                collectionThumbnailImageChangeDialogFragmnet.show(fm, "collectionThumbnail")
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }
        collectionItemRecyclerView.addOnScrollListener(object : EndlessScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getUserCollectionItems(totalItemsCount)
            }
        })
    }

    fun isValid(): Boolean {
        if (StringUtils.isNullOrEmpty(collectionNameChangeEditTextView.text.toString().trim())) {
            ToastUtils.showToast(this@EditCollectionActivity, "field can't be empty")
            return false
        }
        return true
    }

    private fun getUserCollectionItems(start: Int) {
        BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java).getUserCollectionItems(collectionId, start, 10).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<UserCollectionsListModel>> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<UserCollectionsListModel>) {
                try {
                    if (response.code == 200 && response.status == "success" && response.data?.result != null) {
                        userCollectionsListModel = response.data!!.result
                        if (start == 0) {
                            if (userCollectionsListModel.collectionItems.isEmpty()) {
                                itemNotAddedTextView.visibility = View.VISIBLE
                            } else {
                                itemNotAddedTextView.visibility = View.GONE
                            }
                            collectionNameChangeEditTextView.setText(userCollectionsListModel.name)
                            userCollectionsListModel.summary?.let { descriptionEditTextView?.setText(userCollectionsListModel.summary) }
                            try {

                                Picasso.get().load(userCollectionsListModel.imageUrl)
                                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(collectionImageView)
                            } catch (e: Exception) {
                                collectionImageView.setImageResource(R.drawable.default_article)
                            }
                        }
                        shimmer1.stopShimmerAnimation()
                        shimmer1.visibility = View.GONE
                        dataList.addAll(userCollectionsListModel.collectionItems)
                        collectionItemsListAdapter.setItemListData(dataList)
                        collectionItemsListAdapter.notifyDataSetChanged()
                    } else {
                        ToastUtils.showToast(this@EditCollectionActivity, response.data?.msg)
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

    private fun deleteCollectionItem(position: Int) {
        showProgressDialog(resources.getString(R.string.please_wait))
        val addCollectionRequestModel = AddCollectionRequestModel()
        addCollectionRequestModel.itemType = dataList[position].itemType
        addCollectionRequestModel.deleted = true
        addCollectionRequestModel.item = dataList[position].item
        addCollectionRequestModel.itemId = dataList[position].itemId
        addCollectionRequestModel.userCollectionId = dataList[position].userCollectionId
        addCollectionRequestModel.userId = dataList[position].userId

        BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java).editCollectionItem(addCollectionRequestModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<AddCollectionRequestModel>> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponseGeneric<AddCollectionRequestModel>) {
                try {
                    removeProgressDialog()
                    if (t.code == 200 && t.status == Constants.SUCCESS) {
                        dataList.removeAt(position)
                        deletedItemPosition = position
                        if (dataList.isEmpty()) {
                            itemNotAddedTextView.visibility = View.VISIBLE
                        }
                        collectionItemsListAdapter.setItemListData(dataList)
                        collectionItemsListAdapter.notifyDataSetChanged()
                        ToastUtils.showToast(this@EditCollectionActivity, t.data?.msg)
                    } else {
                        ToastUtils.showToast(this@EditCollectionActivity, t.data?.msg)
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4KException", Log.getStackTraceString(e))
                }
            }

            override fun onError(e: Throwable) {
                Crashlytics.logException(e)
                Log.d("MC4KException", Log.getStackTraceString(e))
                try {
                    var data = (e as HttpException).response()?.errorBody()!!.byteStream()
                    var jsonParser = JsonParser()
                    var jsonObject = jsonParser.parse(
                            InputStreamReader(data, "UTF-8")) as JsonObject
                    var reason = jsonObject.get("reason")
                    Toast.makeText(this@EditCollectionActivity, reason.asString, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.e("exception in error", e.message.toString())
                }
            }
        })
    }

    private fun editCollection() {
        val updateCollectionRequestModel = UpdateCollectionRequestModel()
        val list = ArrayList<String>()
        list.add(collectionId)
        updateCollectionRequestModel.userCollectionId = list
        updateCollectionRequestModel.name = collectionNameChangeEditTextView.text.toString()
        updateCollectionRequestModel.imageUrl = userCollectionsListModel.imageUrl
        updateCollectionRequestModel.summary = descriptionEditTextView?.text.toString()

        BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java).editCollection(updateCollectionRequestModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<AddCollectionRequestModel>> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponseGeneric<AddCollectionRequestModel>) {
                try {
                    if (t.status == Constants.SUCCESS && t.code == 200 && t.data?.result != null) {
                        ToastUtils.showToast(this@EditCollectionActivity, t.data?.msg)
                        val bundle = Intent()
                        bundle.putExtra("deletedItemPosition", deletedItemPosition)
                        bundle.putExtra("collectionName", collectionNameChangeEditTextView.text.toString())
                        bundle.putExtra("collectionImage", userCollectionsListModel.imageUrl)
                        bundle.putExtra("collectionDescription", descriptionEditTextView?.text.toString())
                        setResult(Activity.RESULT_OK, bundle)
                        finish()
                    } else {
                        ToastUtils.showToast(this@EditCollectionActivity, t.data?.msg)
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4KException", Log.getStackTraceString(e))
                }
            }

            override fun onError(e: Throwable) {
                Crashlytics.logException(e)
                Log.d("MC4KException", Log.getStackTraceString(e))
                try {
                    var data = (e as HttpException).response()?.errorBody()!!.byteStream()
                    var jsonParser = JsonParser()
                    var jsonObject = jsonParser.parse(
                            InputStreamReader(data, "UTF-8")) as JsonObject
                    val reason = jsonObject.get("reason")
                    Toast.makeText(this@EditCollectionActivity, reason.asString, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.e("exception in error", e.message.toString())
                }
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

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("deletedItemPosition", deletedItemPosition)
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }
}
