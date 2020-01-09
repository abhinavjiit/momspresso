package com.mycity4kids.ui.activity

import android.accounts.NetworkErrorException
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.crashlytics.android.Crashlytics
import com.google.android.material.tabs.TabLayout
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.response.ShortStoryImageData
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import com.mycity4kids.ui.adapter.ShortStoriesThumbnailAdapter
import com.mycity4kids.ui.fragment.ShortStoryLibraryFragment
import com.mycity4kids.utils.OnDragTouchListener
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback


class ShortStoriesCardActivity : BaseActivity() {

    private lateinit var shortLayout: LinearLayout
    private lateinit var tabs: TabLayout
    private lateinit var collectionsViewPager: ViewPager
    private lateinit var adapter: ShortStoriesThumbnailAdapter
    private lateinit var cardBg: ImageView
    private lateinit var title: String
    private lateinit var story: String
    private lateinit var titleTv: TextView
    private lateinit var storyTv: TextView
    private lateinit var back: ImageView
    private lateinit var backButton: ImageButton
    private var fragment: Fragment? = null
    private var isTextFragment: Boolean = false
    private lateinit var parent: RelativeLayout
    private lateinit var divider: View
    private lateinit var ssTopicsText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.short_story_card_activity)
        shortLayout = findViewById(R.id.short_layout)
        tabs = findViewById(R.id.collectionTabLayout)
        cardBg = findViewById(R.id.card_bg)
        titleTv = findViewById(R.id.short_title)
//        parent = findViewById(R.id.rl_layout)
        back = findViewById(R.id.back)
        storyTv = findViewById(R.id.short_text)
        divider = findViewById(R.id.divider)
        collectionsViewPager = findViewById<ViewPager>(R.id.collectionsViewPager)
        title = intent.getStringExtra("title")
        story = intent.getStringExtra("story")
//        ssTopicsText = intent.getStringExtra("ssTopicsText")
        titleTv.text = title
        storyTv.text = story

        tabs.apply {
            addTab(tabs.newTab().setText(resources.getString(R.string.background)))
            addTab(tabs.newTab().setText(resources.getString(R.string.text)))
        }

        setEnabledDisabled(false)

        val root = tabs.getChildAt(0)
        if (root is LinearLayout) {
            (root as LinearLayout).showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            val drawable = GradientDrawable()
            drawable.setColor(resources.getColor(R.color.app_red))
            drawable.setSize(5, 1)
            (root as LinearLayout).dividerPadding = 10
            (root as LinearLayout).dividerDrawable = drawable
        }

        getImages()

        adapter = ShortStoriesThumbnailAdapter(supportFragmentManager)
//        collectionsViewPager.adapter = adapter
        collectionsViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))

        shortLayout.setOnTouchListener(OnDragTouchListener(shortLayout, cardBg))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                collectionsViewPager.currentItem = tab!!.position
                if (tab.position == 0) {
                    isTextFragment = false
                    if (fragment is ShortStoryLibraryFragment) {
                        setEnabledDisabled(true)
                    } else {
                        setEnabledDisabled(false)
                    }
                } else {
                    isTextFragment = true
                    setEnabledDisabled(false)
                }
            }
        })

        back.setOnClickListener {
            if (fragment is ShortStoryLibraryFragment) {
                onBackPressed()
            }
        }

    }


    fun getImages() {
        val retro = BaseApplication.getInstance().retrofit
        val rewardAPI = retro.create(RewardsAPI::class.java)
        val call = rewardAPI.getBackgroundThumbnail("category-7b52fede7bd349e79bd26b24845287d8", 1)
        call.enqueue(getThumbnailList)
    }

    override fun onBackPressed() {
        if (fragment is ShortStoryLibraryFragment && !isTextFragment) {
            setEnabledDisabled(false)
            fragment = null
        } else {
            finish()
        }
        super.onBackPressed()
    }

    private val getThumbnailList = object : Callback<ShortStoryImageData> {
        override fun onResponse(call: Call<ShortStoryImageData>, response: retrofit2.Response<ShortStoryImageData>) {
            if (null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                Crashlytics.logException(nee)
                return
            }
            try {
                val responseData = response.body()!!.data?.result
                /*var libraryList = mutableListOf<ShortStoryLibraryListData>()
                var shortStoryLibraryListData = ShortStoryLibraryListData()
                val responseData = response.body()!!.data?.result
                for (i in 0 until responseData!!.size) {
                    shortStoryLibraryListData.id = responseData.get(i).id
                    shortStoryLibraryListData.name = responseData.get(i).name
                    shortStoryLibraryListData.image_url = "https://static.momspresso.com/assets/badges/meta/Golden-Crown.jpg"
                    libraryList.add(i, shortStoryLibraryListData)
                }

                System.out.println("------" + libraryList)*/
                Picasso.with(BaseApplication.getAppContext()).load(responseData?.images?.results?.get(0)?.image_url).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                        .fit().into(cardBg)
                responseData?.images?.results?.get(0)?.font_colour?.let { setDividerColor(it) }
                responseData?.images?.results?.get(0)?.font_colour?.let { getHexColor(it) }?.let { titleTv.setTextColor(it) }
                responseData?.images?.results?.get(0)?.font_colour?.let { getHexColor(it) }?.let { storyTv.setTextColor(it) }
                response.body()!!.data?.result?.let { adapter.setListData(it) }
                collectionsViewPager.adapter = adapter
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<ShortStoryImageData>, t: Throwable) {
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    fun setDividerColor(color: String) {
        val dividerColor: GradientDrawable
        dividerColor = divider.background as GradientDrawable
        dividerColor.color = getHexColor(color)?.let { ColorStateList.valueOf(it) }
    }

    fun getHexColor(color: String): Int? {
        val colors = color.substring(4, color.length - 1).split(",")
        try {
            val red = colors.get(0).toInt()
            val green = colors.get(1).toInt()
            val blue = colors.get(2).toInt()
            val alpha = colors.get(3).toFloat()
            if (0 <= red && red <= 255 &&
                    0 <= green && green <= 255 &&
                    0 <= blue && blue <= 255 &&
                    0f <= alpha && alpha <= 1f) {
                return Color.argb((alpha * 255).toInt(), red, green, blue);
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
        return 0
    }

    override fun updateUi(response: Response?) {

    }

    fun setBackground(url: String, fontColor: String) {
        Picasso.with(BaseApplication.getAppContext()).load(url).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                .fit().into(cardBg)
        setDividerColor(fontColor)
        getHexColor(fontColor)?.let { titleTv.setTextColor(it) }
        getHexColor(fontColor)?.let { storyTv.setTextColor(it) }
    }

    fun setEnabledDisabled(isEnabled: Boolean) {
        if (isEnabled) {
            back.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_IN);
            back.isClickable = true
        } else {
            back.setColorFilter(ContextCompat.getColor(this, R.color.color_979797), PorterDuff.Mode.SRC_IN);
            back.isClickable = false
        }
    }

    fun currentFragment(fragment: Fragment) {
        this.fragment = fragment
    }

    fun increaseTextSize() {
        var titleTvSize = (titleTv.textSize) / resources.displayMetrics.scaledDensity
        var storyTvSize = (storyTv.textSize) / resources.displayMetrics.scaledDensity
        titleTvSize = titleTvSize + 2
        storyTvSize = storyTvSize + 2
        titleTv.textSize = titleTvSize
        storyTv.textSize = storyTvSize
    }

    fun decreaseTextSize() {
        var titleTvSize = titleTv.textSize / resources.displayMetrics.scaledDensity
        var storyTvSize = storyTv.textSize / resources.displayMetrics.scaledDensity
        titleTvSize = titleTvSize - 2
        storyTvSize = storyTvSize - 2
        titleTv.textSize = titleTvSize
        storyTv.textSize = storyTvSize
    }

    fun textAlign(id: Int) {
        if (id == 0) {
            shortLayout.gravity = Gravity.LEFT
            titleTv.gravity = Gravity.LEFT
            storyTv.gravity = Gravity.LEFT
        } else if (id == 1) {
            shortLayout.gravity = Gravity.CENTER
            titleTv.gravity = Gravity.CENTER
            storyTv.gravity = Gravity.CENTER
        } else {
            shortLayout.gravity = Gravity.RIGHT
            titleTv.gravity = Gravity.RIGHT
            storyTv.gravity = Gravity.RIGHT
        }
    }
}