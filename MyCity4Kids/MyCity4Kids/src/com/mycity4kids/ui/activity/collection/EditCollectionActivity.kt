package com.mycity4kids.ui.activity.collection

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.kelltontech.utils.StringUtils
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.CollectionsModels.AddCollectionRequestModel
import com.mycity4kids.models.CollectionsModels.UpdateCollectionRequestModel
import com.mycity4kids.models.CollectionsModels.UserCollectionsListModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.adapter.AddCollectionAdapter
import com.mycity4kids.ui.fragment.CollectionThumbnailImageChangeDialogFragmnet
import com.squareup.picasso.Picasso
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class EditCollectionActivity : BaseActivity(), AddCollectionAdapter.RecyclerViewClickListener, CollectionThumbnailImageChangeDialogFragmnet.SendImage {
    override fun onsendData(imageUrl: String) {
        try {
            Picasso.with(this@EditCollectionActivity).load(imageUrl)
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(collectionImageVIEW)
            userCollectionsListModel.imageUrl = imageUrl

        } catch (e: Exception) {
            collectionImageVIEW.setImageResource(R.drawable.default_article)
        }
    }

    override fun onClick(position: Int) {
        deleteCollectionItem(position)

    }

    private lateinit var collectionItemRecyclerView: RecyclerView
    private lateinit var collectionItemsListAdapter: AddCollectionAdapter
    var userCollectionsListModel = UserCollectionsListModel()
    private lateinit var collectionId: String
    private lateinit var collectionImageVIEW: ImageView
    private lateinit var submit: TextView
    private lateinit var collectionNameChangeEditTextView: EditText
    private lateinit var collectionImageChangeTextView: TextView
    override fun updateUi(response: Response?) {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_collection_activity)
        collectionItemRecyclerView = findViewById(R.id.collectionItemRecyclerView)
        collectionImageVIEW = findViewById(R.id.collectionImageVIEW)
        collectionNameChangeEditTextView = findViewById(R.id.collectionNameChangeEditTextView)
        collectionImageChangeTextView = findViewById(R.id.collectionImageChangeTextView)
        submit = findViewById(R.id.submit)

        val bundle = intent
        collectionId = bundle.getStringExtra("collectionId")
        val linearLayoutManager = LinearLayoutManager(this)
        collectionItemsListAdapter = AddCollectionAdapter(this@EditCollectionActivity, this, adapterViewType = true)
        collectionItemRecyclerView.layoutManager = linearLayoutManager
        collectionItemRecyclerView.adapter = collectionItemsListAdapter
        getUserCollectionItems()
        submit.setOnClickListener {
            if (isValid())
                editCollection()


        }
        collectionImageChangeTextView.setOnClickListener {
            try {
                val collectionThumbnailImageChangeDialogFragmnet = CollectionThumbnailImageChangeDialogFragmnet()
                val fm = supportFragmentManager
                collectionThumbnailImageChangeDialogFragmnet.show(fm, "collectionThumbnail")

                /* val fm = fragmentManager
                 collectionThumbnailImageChangeDialogFragmnet.setTargetFragment(this, 0)
                 collectionThumbnailImageChangeDialogFragmnet.show(fm!!, "collectionAdd"*/
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }


        }

    }

    fun isValid(): Boolean {
        if (StringUtils.isNullOrEmpty(collectionNameChangeEditTextView.text.toString().trim())) {
            ToastUtils.showToast(this@EditCollectionActivity, "field can't be empty")
            return false
        }
        return true
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
                try {
                    if (response.code == 200 && response.status == "success" && response.data?.result != null) {
                        userCollectionsListModel = response.data!!.result
                        collectionNameChangeEditTextView.setText(userCollectionsListModel.name)
                        try {

                            Picasso.with(this@EditCollectionActivity).load(userCollectionsListModel.imageUrl)
                                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(collectionImageVIEW)
                        } catch (e: Exception) {
                            collectionImageVIEW.setImageResource(R.drawable.default_article)
                        }
                        collectionItemsListAdapter.setItemListData(userCollectionsListModel)
                        collectionItemsListAdapter.notifyDataSetChanged()

                    } else {
                        ToastUtils.showToast(this@EditCollectionActivity, "nhi hua ")
                    }
                } catch (e: Exception) {

                }
            }

            override fun onError(e: Throwable) {
            }

        })


    }


    private fun deleteCollectionItem(position: Int) {

        val addCollectionRequestModel = AddCollectionRequestModel()
        addCollectionRequestModel.itemType = userCollectionsListModel.collectionItems[position].itemType
        addCollectionRequestModel.deleted = true
        addCollectionRequestModel.item = userCollectionsListModel.collectionItems[position].item
        addCollectionRequestModel.itemId = userCollectionsListModel.collectionItems[position].itemId
        addCollectionRequestModel.userCollectionId = userCollectionsListModel.collectionItems[position].userCollectionId
        addCollectionRequestModel.userId = userCollectionsListModel.collectionItems[position].userId


        BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).editCollectionItem(addCollectionRequestModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<AddCollectionRequestModel>> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponseGeneric<AddCollectionRequestModel>) {

                userCollectionsListModel.collectionItems.removeAt(position)
                collectionItemsListAdapter.setItemListData(userCollectionsListModel)
                collectionItemsListAdapter.notifyDataSetChanged()

                ToastUtils.showToast(this@EditCollectionActivity, " successfully removed ")


            }

            override fun onError(e: Throwable) {
                ToastUtils.showToast(this@EditCollectionActivity, "successfully removed nhi hua ")

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

        BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).editCollection(updateCollectionRequestModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<AddCollectionRequestModel>> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponseGeneric<AddCollectionRequestModel>) {


                if (t != null && t.status == "success" && t.code == 200 && t.data?.result != null) {

                    ToastUtils.showToast(this@EditCollectionActivity, "updated")
                    val bundle = Intent()
                    bundle.putExtra("collectionName", collectionNameChangeEditTextView.text.toString())
                    bundle.putExtra("collectionImage", userCollectionsListModel.imageUrl)
                    setResult(Activity.RESULT_OK, bundle)


                }
            }

            override fun onError(e: Throwable) {
                ToastUtils.showToast(this@EditCollectionActivity, " not updated")

            }
        })

    }


}