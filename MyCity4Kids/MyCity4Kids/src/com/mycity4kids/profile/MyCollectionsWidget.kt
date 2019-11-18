package com.mycity4kids.profile

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.CollectionsModels.UserCollectionsListModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.utils.RoundedTransformation
import com.squareup.picasso.Picasso
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MyCollectionsWidget : RelativeLayout {

    private lateinit var collectionsShimmerContainer: ShimmerFrameLayout
    private lateinit var addCollectionContainer: RelativeLayout
    private lateinit var collectionsContainer: RelativeLayout
    private lateinit var collectionsHSV: HorizontalScrollView
    private lateinit var collectionsHSVContainer: LinearLayout

    private lateinit var userCollectionsListModel: UserCollectionsListModel

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initializeView()
    }

    private fun initializeView() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.my_collections_widget, this)
        collectionsShimmerContainer = findViewById(R.id.collectionsShimmerContainer)
        addCollectionContainer = findViewById(R.id.addCollectionContainer)
        collectionsContainer = findViewById(R.id.collectionsContainer)
        collectionsHSV = findViewById(R.id.collectionsHSV)
        collectionsHSVContainer = findViewById(R.id.collectionsHSVContainer)
        collectionsShimmerContainer.startShimmerAnimation()

        val handler = Handler()
        handler.postDelayed(Runnable { getCollections(SharedPrefUtils.getUserDetailModel(context).dynamoId) }, 4000)

    }

    private fun getCollections(authorId: String) {

        BaseApplication.getInstance().campaignRetrofit.create(CollectionsAPI::class.java)
                .getUserCollectionList(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), 0, 20)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<BaseResponseGeneric<UserCollectionsListModel>> {
                    override fun onComplete() {
//                        removeProgressDialog()
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(response: BaseResponseGeneric<UserCollectionsListModel>) {
                        try {
                            if (response.code == 200 && response.status == "success" && response.data?.result != null) {
                                userCollectionsListModel = response.data!!.result
                                if (userCollectionsListModel.collections_list.size > 0) {
                                    collectionsContainer.visibility = View.VISIBLE
                                    collectionsShimmerContainer.visibility = View.GONE
                                } else {
                                    collectionsContainer.visibility = View.GONE
                                    collectionsShimmerContainer.visibility = View.GONE
                                }
                                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                                for (i in 0 until userCollectionsListModel.collections_list.size) {
                                    val itemView = inflater.inflate(R.layout.explore_topics_grid_item, null)
                                    Picasso.with(context).load(userCollectionsListModel.collections_list[i].imageUrl)
                                            .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation())
                                            .into(itemView.findViewById<ImageView>(R.id.tagImageView))
                                    itemView.findViewById<TextView>(R.id.topicsNameTextView).text = userCollectionsListModel.collections_list[i].name
                                    collectionsHSVContainer.addView(itemView)
                                }
                            } else {
                                this@MyCollectionsWidget.visibility = View.GONE
                            }
                        } catch (e: Exception) {
                            this@MyCollectionsWidget.visibility = View.GONE
                            Crashlytics.logException(e)
                            Log.d("MC4kException", Log.getStackTraceString(e))
                        }
                    }

                    override fun onError(e: Throwable) {
                        this@MyCollectionsWidget.visibility = View.GONE
                        Crashlytics.logException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }

                })
    }


    fun setBadges() {

    }

}