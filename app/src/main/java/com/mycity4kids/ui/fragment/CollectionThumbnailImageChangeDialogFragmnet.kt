package com.mycity4kids.ui.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.facebook.shimmer.ShimmerFrameLayout
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.collectionsModels.UserCollectionsListModel
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.adapter.CollectionImageThumbnailAdapter
import com.mycity4kids.ui.videochallengenewui.ExpandableHeightGridView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject

class CollectionThumbnailImageChangeDialogFragmnet : DialogFragment() {

    var userCollectionsListModel = UserCollectionsListModel()
    private lateinit var collectionImageThumbnailAdapter: CollectionImageThumbnailAdapter
    private lateinit var collectionGridView: ExpandableHeightGridView
    lateinit var shimmer1: ShimmerFrameLayout
    lateinit var toolbar: Toolbar
    lateinit var toolbarTitle: TextView
    lateinit var sendImage: SendImage
    val dataList = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.collection_thumbnail_image_dialog_fragment, container,
                false)

        collectionGridView = view.findViewById(R.id.collectionGridView)
        shimmer1 = view.findViewById(R.id.shimmer1)
        toolbar = view.findViewById(R.id.toolbar)
        toolbarTitle = view.findViewById(R.id.toolbarTitle)

        context?.run {
            collectionImageThumbnailAdapter = CollectionImageThumbnailAdapter(context!!)
            collectionGridView.adapter = collectionImageThumbnailAdapter
        }
        getImagesForCollection()
        toolbarTitle.setOnClickListener {
            dismiss()
        }

        collectionGridView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                context?.let { sendImage.onsendData(dataList[position]) }
                dismiss()
            }
        })

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sendImage = context as SendImage
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

    private fun getImagesForCollection() {
        BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java).getCollectionImages().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<ResponseBody> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: ResponseBody) {
                try {
                    shimmer1.stopShimmerAnimation()
                    shimmer1.visibility = View.GONE
                    val strResponse = String(t.bytes())
                    val jsonObject = JSONObject(strResponse)

                    val arr = jsonObject.getJSONObject("data").getJSONArray("result")

                    for (i in 0..arr!!.length() - 1) {
                        dataList.add(arr[i].toString())
                    }
                    collectionImageThumbnailAdapter.setImages(dataList)
                    collectionImageThumbnailAdapter.notifyDataSetChanged()
                } catch (e: Exception) {
                }
            }

            override fun onError(e: Throwable) {
            }
        })
    }

    override fun onStop() {
        super.onStop()
        shimmer1.stopShimmerAnimation()
    }

    interface SendImage {
        fun onsendData(imageUrl: String)
    }
}
