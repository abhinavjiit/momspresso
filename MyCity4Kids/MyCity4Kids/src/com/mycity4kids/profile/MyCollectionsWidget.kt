package com.mycity4kids.profile

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.facebook.shimmer.ShimmerFrameLayout
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs
import com.mycity4kids.utils.RoundedTransformation
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCollectionsWidget : RelativeLayout {

    private lateinit var collectionsShimmerContainer: ShimmerFrameLayout
    private lateinit var addCollectionContainer: RelativeLayout
    private lateinit var collectionsContainer: RelativeLayout
    private lateinit var collectionsHSV: HorizontalScrollView
    private lateinit var collectionsHSVContainer: LinearLayout

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
        val retrofit = BaseApplication.getInstance().retrofit
        val configAPIs = retrofit.create(ConfigAPIs::class.java)
        val cityCall = configAPIs.getCollections("be13a0f26bbf41f0833906ec374f07db", 0, 20)
        cityCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                collectionsContainer.visibility = View.VISIBLE
//                collectionsShimmerContainer.visibility = View.GONE
                try {
                    val resData = String(response.body()!!.bytes())
                    val jObject = JSONObject(resData)
                    val jArr = jObject.getJSONObject("data").getJSONObject("result").getJSONArray("collections_list")
                    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    if (jArr.length() > 0) {
                        collectionsContainer.visibility = View.VISIBLE
                        collectionsShimmerContainer.visibility = View.GONE
                    } else {
                        collectionsContainer.visibility = View.GONE
                        collectionsShimmerContainer.visibility = View.GONE
                    }
                    for (i in 0 until jArr.length()) {
                        val itemView = inflater.inflate(R.layout.explore_topics_grid_item, null)
                        Picasso.with(context).load(jArr.getJSONObject(i).getString("imageUrl"))
                                .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(itemView.findViewById<ImageView>(R.id.tagImageView))
                        itemView.findViewById<TextView>(R.id.topicsNameTextView).text = jArr.getJSONObject(i).getString("name")
                        collectionsHSVContainer.addView(itemView)
                    }
                } catch (e: Exception) {
                    this@MyCollectionsWidget.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                this@MyCollectionsWidget.visibility = View.GONE
            }
        })
    }


    fun setBadges() {

    }

}