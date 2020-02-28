package com.mycity4kids.ui.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.crashlytics.android.Crashlytics
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mycity4kids.utils.StringUtils
import com.mycity4kids.utils.ToastUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.collectionsModels.AddCollectionRequestModel
import com.mycity4kids.models.collectionsModels.UpdateCollectionRequestModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.activity.collection.AddMultipleCollectionItemActivity
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.InputStreamReader

class AddCollectionPopUpDialogFragment : DialogFragment() {
    private lateinit var confirmTextView: TextView
    private lateinit var collectionNameEditTextView: EditText
    lateinit var cancel: ImageView
    lateinit var collectionId: String
    var articleId: String? = null
    var type: String? = null
    lateinit var addCollectionInterface: AddCollectionInterface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.add_collection_name_pop_up, container,
                false)
        confirmTextView = rootView.findViewById(R.id.confirmTextView)
        collectionNameEditTextView = rootView.findViewById(R.id.collectionNameEditTextView)
        cancel = rootView.findViewById(R.id.cancel)
        confirmTextView.setOnClickListener {
            if (isValid()) addCollection()
        }
        cancel.setOnClickListener {
            dismiss()
        }
        val bundle = arguments
        articleId = bundle?.getString("articleId")
        type = bundle?.getString("type")

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is UserProfileActivity) {
            try {
                addCollectionInterface = context
            } catch (e: ClassCastException) {
                Crashlytics.logException(e)
                Log.d("MC4KException", Log.getStackTraceString(e))
            }
        }
    }

    fun isValid(): Boolean {
        if (StringUtils.isNullOrEmpty(collectionNameEditTextView.text.toString().trim())) {
            ToastUtils.showToast(context, "field can't be blank")
            return false
        }
        return true
    }

    private fun addCollection() {
        var addCollectionRequestModel = AddCollectionRequestModel()
        addCollectionRequestModel.name = collectionNameEditTextView.text.toString().trim()
        addCollectionRequestModel.userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId

        BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java).addCollection(addCollectionRequestModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<AddCollectionRequestModel>> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponseGeneric<AddCollectionRequestModel>) {
                try {
                    if (t.code == 200 && t.status == Constants.SUCCESS && t.data?.result != null) {
                        var addCollectionRequestModell: AddCollectionRequestModel = t.data!!.result
                        collectionId = addCollectionRequestModell.userCollectionId
                        Utils.pushGenericEvent(activity, "CTA_Collection_Creation",
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                                "AddCollectionPopUpDialogFragment")
                        if (!StringUtils.isNullOrEmpty(collectionId) && !StringUtils.isNullOrEmpty(articleId)) {
                            addCollectionItem()
                        } else {
                            addMultipleCollectionItemsInCollection(collectionId)
                            targetFragment?.onActivityResult(100, 1, activity?.intent)
                            addCollectionInterface?.let {
                                it.onCollectionAddSuccess()
                            }
                            dismiss()
                        }
                    } else {
                        ToastUtils.showToast(activity, t.data?.msg)
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
                    //    Log.d("CODE", code.toString())
                    var data = (e as HttpException).response()?.errorBody()!!.byteStream()
                    var jsonParser = JsonParser()
                    var jsonObject = jsonParser.parse(
                            InputStreamReader(data, "UTF-8")) as JsonObject
                    var reason = jsonObject.get("reason")
                    Toast.makeText(activity, reason.asString, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.e("exception in error", e.message.toString())
                }
            }
        })
    }

    fun addCollectionItem() {
        val addCollectionRequestModel1 = UpdateCollectionRequestModel()
        addCollectionRequestModel1.userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        addCollectionRequestModel1.itemType = type
        val List = ArrayList<String>()
        List.add(collectionId)
        addCollectionRequestModel1.userCollectionId = List
        addCollectionRequestModel1.item = articleId
        BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java).addCollectionItem(addCollectionRequestModel1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<AddCollectionRequestModel>> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponseGeneric<AddCollectionRequestModel>) {
                try {
                    if (t.code == 200 && t.status == Constants.SUCCESS && t.data?.result != null) {
                        targetFragment?.onActivityResult(100, 1, activity?.intent)
                        dismiss()
                        ToastUtils.showToast(activity, t.data?.msg)
                    } else {
                        ToastUtils.showToast(activity, t.data?.msg)
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
                    val data = (e as HttpException).response()?.errorBody()!!.byteStream()
                    val jsonParser = JsonParser()
                    val jsonObject = jsonParser.parse(
                            InputStreamReader(data, "UTF-8")) as JsonObject
                    val reason = jsonObject.get("reason")
                    Toast.makeText(activity, reason.asString, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.e("exception in error", e.message.toString())
                }
            }
        })
    }

    private fun addMultipleCollectionItemsInCollection(collectionId: String) {
        val intent = Intent(activity, AddMultipleCollectionItemActivity::class.java)
        intent.putExtra("collectionId", collectionId)
        startActivity(intent)
    }

    interface AddCollectionInterface {
        fun onCollectionAddSuccess()
    }
}
