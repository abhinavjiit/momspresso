package com.mycity4kids.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.collectionsModels.AddCollectionRequestModel
import com.mycity4kids.models.collectionsModels.UpdateCollectionRequestModel
import com.mycity4kids.models.collectionsModels.UserCollectionsListModel
import com.mycity4kids.models.collectionsModels.UserCollectionsModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.adapter.AddCollectionAdapter
import com.mycity4kids.utils.EndlessScrollListener
import com.mycity4kids.utils.ToastUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.InputStreamReader
import retrofit2.HttpException

class AddCollectionAndCollectionItemDialogFragment : DialogFragment(),
    AddCollectionAdapter.RecyclerViewClickListener {
    override fun onClick(position: Int) {
        addCollectionItem(position)
    }

    lateinit var addCollectionAdapter: AddCollectionAdapter
    lateinit var userCollectionsListModel: UserCollectionsListModel
    private lateinit var addCollectionRecyclerView: RecyclerView
    var articleId: String? = null
    private lateinit var addNewTextView: TextView
    lateinit var shimmer1: ShimmerFrameLayout
    private var dataList = ArrayList<UserCollectionsModel>()
    private lateinit var cancel: ImageView
    private lateinit var noItemAddedTextView: TextView
    var type: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(
            R.layout.add_collection_activity, container,
            false
        )
        addCollectionRecyclerView = rootView.findViewById(R.id.addCollectionRecyclerView)
        addNewTextView = rootView.findViewById(R.id.addNewTextView)
        shimmer1 = rootView.findViewById(R.id.shimmer1)
        cancel = rootView.findViewById(R.id.cancel)
        noItemAddedTextView = rootView.findViewById(R.id.noItemAddedTextView)
        val linearLayoutManager = LinearLayoutManager(context)
        addCollectionAdapter = AddCollectionAdapter(context!!, this, adapterViewType = false)
        addCollectionRecyclerView.layoutManager = linearLayoutManager as RecyclerView.LayoutManager?
        addCollectionRecyclerView.adapter = addCollectionAdapter
        val bundle = arguments

        articleId = bundle?.getString("articleId")
        type = bundle?.getString("type")
        getUserCreatedCollections(0)
        addNewTextView.setOnClickListener {
            try {
                val addCollectionPopUpDialogFragment = AddCollectionPopUpDialogFragment()
                addCollectionPopUpDialogFragment.arguments = bundle
                val fm = fragmentManager
                addCollectionPopUpDialogFragment.setTargetFragment(this, 100)
                addCollectionPopUpDialogFragment.show(fm!!, "collectionAddPopUp")
                Utils.pushProfileEvents(
                    activity, "CTA_Add_Collection_From_Content",
                    "AddCollectionAndCollectionItemDialogFragment", "New collection", "-"
                )
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }
        addCollectionRecyclerView.addOnScrollListener(object :
            EndlessScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getUserCreatedCollections(totalItemsCount)
            }
        })

        cancel.setOnClickListener {
            dismiss()
        }
        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            dialog.window!!.setWindowAnimations(R.style.CollectionDialogAnimation)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.transparent)))
        }
        shimmer1.startShimmerAnimation()
    }

    private fun getUserCreatedCollections(start: Int) {
        BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java).getUserCollectionList(
            userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            start = start,
            offset = 20,
            collectionType = "0"
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object :
            Observer<BaseResponseGeneric<UserCollectionsListModel>> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<UserCollectionsListModel>) {
                try {
                    if (response.code == 200 && response.status == Constants.SUCCESS && response.data?.result != null) {

                        shimmer1.stopShimmerAnimation()
                        shimmer1.visibility = View.GONE
                        userCollectionsListModel = response.data!!.result
                        if (start == 0) {
                            if (userCollectionsListModel.collectionsList.isEmpty())
                                noItemAddedTextView.visibility = View.VISIBLE
                            else
                                noItemAddedTextView.visibility = View.GONE
                        }
                        dataList.addAll(userCollectionsListModel.collectionsList)
                        addCollectionAdapter.setListData(dataList)
                        addCollectionAdapter.notifyDataSetChanged()
                    } else {
                        Log.d("Error", response.toString())
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

    private fun addCollectionItem(position: Int) {
        val addCollectionRequestModel1 = UpdateCollectionRequestModel()
        addCollectionRequestModel1.userId =
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        addCollectionRequestModel1.itemType = type
        val list = ArrayList<String>()
        list.add(dataList[position].userCollectionId)
        addCollectionRequestModel1.userCollectionId = list
        addCollectionRequestModel1.item = articleId
        BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java).addCollectionItem(
            addCollectionRequestModel1
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object :
            Observer<BaseResponseGeneric<AddCollectionRequestModel>> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponseGeneric<AddCollectionRequestModel>) {
                try {
                    if (t.code == 200 && t.status == Constants.SUCCESS && t.data?.result != null && t.data?.result?.listItemId != null) {
                        ToastUtils.showToast(activity, t.data?.msg)
                        dismiss()
                    } else {
                        ToastUtils.showToast(activity, t.data?.msg)
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4KException", Log.getStackTraceString(e))
                }
            }

            override fun onError(e: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4KException", Log.getStackTraceString(e))
                try {
                    var data = (e as HttpException).response()?.errorBody()!!.byteStream()
                    var jsonParser = JsonParser()
                    var jsonObject = jsonParser.parse(
                        InputStreamReader(data, "UTF-8")
                    ) as JsonObject
                    var reason = jsonObject.get("reason")
                    Toast.makeText(activity, reason.asString, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.e("exception in error", e.message.toString())
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        shimmer1.stopShimmerAnimation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            if (requestCode == 100) {
                dismiss()
            }
        }
    }
}
