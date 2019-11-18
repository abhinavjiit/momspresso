package com.mycity4kids.widget

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
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

class BadgesProfileWidget : LinearLayout {

    private lateinit var badgesShimmerContainer: ShimmerFrameLayout
    private lateinit var badgesContainer: LinearLayout
    private lateinit var badgeImageView1: ImageView
    private lateinit var badgeImageView2: ImageView
    private lateinit var badgeImageView3: ImageView
    private lateinit var arrowImageView: ImageView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initializeView()
    }

    private fun initializeView() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.badges_profile_widget, this)
        badgesShimmerContainer = findViewById(R.id.badgesShimmerContainer)
        badgesContainer = findViewById(R.id.badgesContainer)
        badgeImageView1 = findViewById(R.id.badgeImageView_1)
        badgeImageView2 = findViewById(R.id.badgeImageView_2)
        badgeImageView3 = findViewById(R.id.badgeImageView_3)
        arrowImageView = findViewById(R.id.arrowImageView)
        badgesShimmerContainer.startShimmerAnimation()
//        val handler = Handler()
//        handler.postDelayed(Runnable { getBadges(SharedPrefUtils.getUserDetailModel(context).dynamoId) }, 4000)

    }

    fun getBadges(authorId: String) {
        val retrofit = BaseApplication.getInstance().retrofit
        val configAPIs = retrofit.create(ConfigAPIs::class.java)
        val cityCall = configAPIs.getBadges("1c94cc0e9a7f4238a03d7a398502db7d")
        cityCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                badgesContainer.visibility = View.VISIBLE
                badgesShimmerContainer.visibility = View.GONE
                try {
                    val resData = String(response.body()!!.bytes())
//                val gson = GsonBuilder().registerTypeAdapterFactory(ArrayAdapterFactory()).create()
//                val res = gson.fromJson<TopicsResponse>(resData, TopicsResponse::class.java)
                    val jObject = JSONObject(resData)
                    val jArr = jObject.getJSONObject("data").getJSONArray("result")
                    when {
                        jArr.length() >= 3 -> {
                            badgeImageView1.visibility = View.VISIBLE
                            badgeImageView2.visibility = View.VISIBLE
                            badgeImageView3.visibility = View.VISIBLE
                            Picasso.with(context).load(jArr.getJSONObject(0).getString("badge_image_url"))
                                    .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(badgeImageView1)
                            Picasso.with(context).load(jArr.getJSONObject(1).getString("badge_image_url"))
                                    .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(badgeImageView2)
                            Picasso.with(context).load(jArr.getJSONObject(2).getString("badge_image_url"))
                                    .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(badgeImageView3)
                        }
                        jArr.length() == 2 -> {
                            badgeImageView1.visibility = View.VISIBLE
                            badgeImageView2.visibility = View.VISIBLE
                            badgeImageView3.visibility = View.GONE
                            Picasso.with(context).load(jArr.getJSONObject(0).getString("badge_image_url"))
                                    .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(badgeImageView1)
                            Picasso.with(context).load(jArr.getJSONObject(1).getString("badge_image_url"))
                                    .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(badgeImageView2)
                        }
                        jArr.length() == 1 -> {
                            badgeImageView1.visibility = View.VISIBLE
                            badgeImageView2.visibility = View.GONE
                            badgeImageView3.visibility = View.GONE
                            Picasso.with(context).load(jArr.getJSONObject(0).getString("badge_image_url"))
                                    .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(badgeImageView1)
                        }
                        else -> {
                            this@BadgesProfileWidget.visibility = View.GONE
                        }
                    }
                } catch (e: Exception) {
                    this@BadgesProfileWidget.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                this@BadgesProfileWidget.visibility = View.GONE
            }
        })
    }


    fun setBadges() {

    }

}