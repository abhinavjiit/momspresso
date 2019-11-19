package com.mycity4kids.widget

import android.accounts.NetworkErrorException
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.BadgeListResponse
import com.mycity4kids.retrofitAPIsInterfaces.BadgeAPI
import com.mycity4kids.utils.RoundedTransformation
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class BadgesProfileWidget : LinearLayout {

    private lateinit var badgesShimmerContainer: ShimmerFrameLayout
    private lateinit var badgesContainer: LinearLayout
    private lateinit var badgeImageView1: ImageView
    private lateinit var badgeImageView2: ImageView
    private lateinit var badgeImageView3: ImageView
    private lateinit var arrowImageView: ImageView

    private var badgeList: ArrayList<BadgeListResponse.BadgeListData.BadgeListResult>? = null

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

        badgeList = ArrayList()
//        val handler = Handler()
//        handler.postDelayed(Runnable { getBadges(SharedPrefUtils.getUserDetailModel(context).dynamoId) }, 4000)

    }

    fun getBadges(authorId: String?) {
        val retrofit = BaseApplication.getInstance().retrofit
        val badgeAPI = retrofit.create(BadgeAPI::class.java)
        val badgeListResponseCall = badgeAPI.getBadgeList("1c94cc0e9a7f4238a03d7a398502db7d")
        badgeListResponseCall.enqueue(object : Callback<BadgeListResponse> {
            override fun onFailure(call: Call<BadgeListResponse>, t: Throwable) {
                Crashlytics.logException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }

            override fun onResponse(call: Call<BadgeListResponse>, response: Response<BadgeListResponse>) {
                try {
                    if (response.body() == null) {
                        if (response.raw() != null) {
                            val nee = NetworkErrorException(response.raw().toString())
                            Crashlytics.logException(nee)
                        }
                        return
                    }
                    val responseModel = response.body() as BadgeListResponse
                    if (responseModel.code == 200 && Constants.SUCCESS == responseModel.status) {
                        badgesContainer.visibility = View.VISIBLE
                        badgesShimmerContainer.visibility = View.GONE
                        if (responseModel.data != null && !responseModel.data.isEmpty() && responseModel.data[0] != null) {
                            processResponse(responseModel.data[0].result)
                        } else {
                        }
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
        })

    }

    private fun processResponse(data: ArrayList<BadgeListResponse.BadgeListData.BadgeListResult>) {
        when {
            data.size >= 3 -> {
                badgeImageView1.visibility = View.VISIBLE
                badgeImageView2.visibility = View.VISIBLE
                badgeImageView3.visibility = View.VISIBLE
                Picasso.with(context).load(data[0].badge_image_url)
                        .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(badgeImageView1)
                Picasso.with(context).load(data[1].badge_image_url)
                        .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(badgeImageView2)
                Picasso.with(context).load(data[2].badge_image_url)
                        .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(badgeImageView3)
            }
            data.size == 2 -> {
                badgeImageView1.visibility = View.VISIBLE
                badgeImageView2.visibility = View.VISIBLE
                badgeImageView3.visibility = View.GONE
                Picasso.with(context).load(data[0].badge_image_url)
                        .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(badgeImageView1)
                Picasso.with(context).load(data[1].badge_image_url)
                        .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(badgeImageView2)
            }
            data.size == 1 -> {
                badgeImageView1.visibility = View.VISIBLE
                badgeImageView2.visibility = View.GONE
                badgeImageView3.visibility = View.GONE
                Picasso.with(context).load(data[0].badge_image_url)
                        .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(badgeImageView1)
            }
        }
    }
}