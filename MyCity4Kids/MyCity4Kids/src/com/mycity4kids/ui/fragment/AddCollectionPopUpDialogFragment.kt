package com.mycity4kids.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.kelltontech.utils.StringUtils
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.CollectionsModels.AddCollectionRequestModel
import com.mycity4kids.models.CollectionsModels.UpdateCollectionRequestModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AddCollectionPopUpDialogFragment : DialogFragment() {
    lateinit var confirmTextView: TextView
    lateinit var collectionNameEditTextView: EditText
    lateinit var cancle: ImageView
    lateinit var collectionId: String
    var articleId: String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.add_collection_name_pop_up, container,
                false)

        confirmTextView = rootView.findViewById(R.id.confirmTextView)
        collectionNameEditTextView = rootView.findViewById(R.id.collectionNameEditTextView)
        cancle = rootView.findViewById(R.id.cancle)
        confirmTextView.setOnClickListener {
            if (isValid()) addCollection()
        }
        cancle.setOnClickListener {
            dismiss()
        }

        val bundle = arguments
        articleId = bundle?.getString("articleId")
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
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        }

    }

    fun isValid(): Boolean {
        if (StringUtils.isNullOrEmpty(collectionNameEditTextView.text.toString().trim())) {
            ToastUtils.showToast(context, "field can't be blank")
            return false
        }
        return true
    }

    fun addCollection() {
        var addCollectionRequestModel = AddCollectionRequestModel()
        addCollectionRequestModel.name = collectionNameEditTextView.text.toString().trim()
        addCollectionRequestModel.userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId

        BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).addCollection(addCollectionRequestModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<AddCollectionRequestModel>> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponseGeneric<AddCollectionRequestModel>) {
                if (t.code == 200 && t.status == "success" && t.data?.result != null) {

                    var addCollectionRequestModell: AddCollectionRequestModel = t.data!!.result
                    collectionId = addCollectionRequestModell.userCollectionId

                    if (!StringUtils.isNullOrEmpty(collectionId) && !StringUtils.isNullOrEmpty(articleId)) {
                        addCollectionItem()
                    } else {
                        targetFragment?.onActivityResult(100, 1, activity?.intent)
                        dismiss()
                    }

                } else {
                    ToastUtils.showToast(activity, "nhi hua  add ")

                }

            }

            override fun onError(e: Throwable) {
            }

        })


    }


    fun addCollectionItem() {
        val addCollectionRequestModel1 = UpdateCollectionRequestModel()
        addCollectionRequestModel1.userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        addCollectionRequestModel1.itemType = "0"
        val List = ArrayList<String>()
        List.add(collectionId)
        addCollectionRequestModel1.userCollectionId = List
        addCollectionRequestModel1.item = articleId
        BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java).addCollectionItem(addCollectionRequestModel1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<AddCollectionRequestModel>> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponseGeneric<AddCollectionRequestModel>) {
                if (t != null && t.code == 200 && t.status == "success" && t.data?.result != null) {
                    targetFragment?.onActivityResult(100, 1, activity?.intent)
                    dismiss()
                    ToastUtils.showToast(activity, "item added in collection successfully")


                } else {
                    ToastUtils.showToast(activity, "item  haven't added in collection successfully")

                }


            }

            override fun onError(e: Throwable) {
                ToastUtils.showToast(activity, "item  haven't added in collection successfully , some error at the server ")

            }

        })


    }
}