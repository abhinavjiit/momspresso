package com.mycity4kids.profile

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.collectionsModels.UserCollectionsListModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.activity.collection.CollectionsActivity
import com.mycity4kids.ui.activity.collection.UserCollectionItemListActivity
import com.mycity4kids.ui.fragment.AddCollectionPopUpDialogFragment
import com.squareup.picasso.Picasso
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MyCollectionsWidget : RelativeLayout, View.OnClickListener {
    private lateinit var collectionsShimmerContainer: ShimmerFrameLayout
    private lateinit var addCollectionContainer: RelativeLayout
    private lateinit var collectionsContainer: RelativeLayout
    private lateinit var collectionsHSV: HorizontalScrollView
    private lateinit var collectionsHSVContainer: LinearLayout
    private lateinit var viewAllTextView: TextView

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
        viewAllTextView = findViewById(R.id.viewAllTextView)
        collectionsShimmerContainer.startShimmerAnimation()

        viewAllTextView.setOnClickListener(this)
        addCollectionContainer.setOnClickListener(this)
    }

    fun getCollections(authorId: String?, isPrivateProfile: Boolean) {
        if (isPrivateProfile) {
            addCollectionContainer.visibility = View.VISIBLE
        } else {
            addCollectionContainer.visibility = View.GONE
        }
        authorId?.let {
            BaseApplication.getInstance().retrofit.create(CollectionsAPI::class.java)
                    .getUserCollectionList(authorId, 0, 7)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<BaseResponseGeneric<UserCollectionsListModel>> {
                        override fun onComplete() {
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(response: BaseResponseGeneric<UserCollectionsListModel>) {
                            try {
                                if (response.code == 200 && response.status == "success" && response.data?.result != null) {
                                    userCollectionsListModel = response.data!!.result
                                    if (userCollectionsListModel.collectionsList.size > 0) {
                                        collectionsContainer.visibility = View.VISIBLE
                                        collectionsShimmerContainer.visibility = View.GONE
                                    } else {
                                        collectionsShimmerContainer.visibility = View.GONE
                                        if (isPrivateProfile) {
                                            collectionsContainer.visibility = View.VISIBLE
                                        } else {
                                            collectionsContainer.visibility = View.GONE
                                        }
                                    }
                                    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                                    for (i in 0 until userCollectionsListModel.collectionsList.size) {
                                        val itemView = inflater.inflate(R.layout.profile_collections_item, null)
                                        try {
                                            Picasso.with(BaseApplication.getAppContext()).load(userCollectionsListModel.collectionsList[i].imageUrl)
                                                    .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                                    .fit().into(itemView.findViewById<ImageView>(R.id.collectionImageView))
                                        } catch (e: Exception) {
                                            itemView.findViewById<ImageView>(R.id.collectionImageView).setImageDrawable(
                                                    ContextCompat.getDrawable(context, R.drawable.default_article))
                                        }

                                        itemView.findViewById<TextView>(R.id.collectionTitleTextView).text = userCollectionsListModel.collectionsList[i].name
                                        itemView.findViewById<ImageView>(R.id.collectionImageView).clipToOutline = true
                                        itemView.setOnClickListener(OnClickListener {
                                            val intent = Intent(it.context, UserCollectionItemListActivity::class.java)
                                            intent.putExtra("id", userCollectionsListModel.collectionsList[i].userCollectionId)
                                            it.context.startActivity(intent)
                                        })
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
    }

    override fun onClick(v: View?) {
        try {
            when {
                v?.id == R.id.addCollectionContainer -> {
                    if (context is M_PrivateProfileActivity) {
                        val addCollectionPopUpDialogFragment = AddCollectionPopUpDialogFragment()
                        val fm = (context as M_PrivateProfileActivity).supportFragmentManager
                        addCollectionPopUpDialogFragment.show(fm, "collectionAddPopUp")
                    }
                }
                v?.id == R.id.viewAllTextView -> {
                    val intent = Intent(context, CollectionsActivity::class.java)
                    context.startActivity(intent)
                }
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    fun refresh(authorId: String?, privateProfile: Boolean) {
        for (i in 1 until collectionsHSVContainer.childCount) {
            collectionsHSVContainer.removeViewAt(1)
        }
        getCollections(authorId, privateProfile)
    }

}