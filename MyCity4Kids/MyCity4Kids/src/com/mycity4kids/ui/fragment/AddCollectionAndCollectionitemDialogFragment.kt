package com.mycity4kids.ui.fragment

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
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

class AddCollectionAndCollectionitemDialogFragment : DialogFragment(), AddCollectionAdapter.RecyclerViewClickListener {
    override fun onClick(position: Int) {
        addCollectionItem(position)

    }

    lateinit var addCollectionAdapter: AddCollectionAdapter
    lateinit var userCollectionsListModel: UserCollectionsListModel
    lateinit var addCollectionRecyclerView: RecyclerView
    var articleId: String? = null
    lateinit var addNewTextView: TextView
    lateinit var shimmer1: ShimmerFrameLayout
    var type: String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.add_collection_activity, container,
                false)
        addCollectionRecyclerView = rootView.findViewById(R.id.addCollectionRecyclerView)
        addNewTextView = rootView.findViewById(R.id.addNewTextView)
        shimmer1 = rootView.findViewById(R.id.shimmer1)
        val linearLayoutManager = LinearLayoutManager(context)
        addCollectionAdapter = AddCollectionAdapter(context!!, this, adapterViewType = false)
        addCollectionRecyclerView.layoutManager = linearLayoutManager
        addCollectionRecyclerView.adapter = addCollectionAdapter
        val bundle = arguments

        articleId = bundle?.getString("articleId")
        type = bundle?.getString("type")
        getUserCreatedCollections()
        addNewTextView.setOnClickListener {

            try {
                val addCollectionPopUpDialogFragment = AddCollectionPopUpDialogFragment()
                addCollectionPopUpDialogFragment.arguments = bundle
                val fm = fragmentManager
                addCollectionPopUpDialogFragment.setTargetFragment(this, 100)
                addCollectionPopUpDialogFragment.show(fm!!, "collectionAddPopUp")
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }

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
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.window!!.setWindowAnimations(R.style.CollectionDialogAnimation)
            //            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_bg_rounded_corners));
            dialog.window!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.campaign_4A4A4A)))

        }

        shimmer1.startShimmerAnimation()
    }

    fun getUserCreatedCollections() {

        BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).getUserCollectionList(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), 0, 20).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<UserCollectionsListModel>> {
            override fun onComplete() {
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
            }

        })


    }

    fun addCollectionItem(position: Int) {
        val addCollectionRequestModel1 = UpdateCollectionRequestModel()
        addCollectionRequestModel1.userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        addCollectionRequestModel1.itemType = type
        val List = ArrayList<String>()
        List.add(userCollectionsListModel.collections_list[position].userCollectionId)
        addCollectionRequestModel1.userCollectionId = List
        addCollectionRequestModel1.item = articleId
        BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).addCollectionItem(addCollectionRequestModel1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<AddCollectionRequestModel>> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponseGeneric<AddCollectionRequestModel>) {
                if (t != null && t.code == 200 && t.status == "success" && t.data?.result != null) {

                    dismiss()
                    ToastUtils.showToast(activity, "item added in collection successfully")


                } else {
                    ToastUtils.showToast(activity, "item  haven't added in collection successfully")

                }


            }

            override fun onError(e: Throwable) {

                ToastUtils.showToast(activity, e.message.toString())

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