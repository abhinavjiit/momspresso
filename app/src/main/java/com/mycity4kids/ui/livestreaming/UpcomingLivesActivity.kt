package com.mycity4kids.ui.livestreaming

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.tabs.TabLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.request.UpdateViewCountRequest
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.DateTimeUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.upcoming_lives_activity.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpcomingLivesActivity : BaseActivity(), View.OnClickListener {

    private var liveStreamResponse: LiveStreamResult? = null
    private lateinit var upcomingLivesPagerAdapter: UpcomingLivesPagerAdapter
    lateinit var root: DatabaseReference
    val commentsList = ArrayList<ChatListData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.upcoming_lives_activity)
        backNavigationImageView.setOnClickListener(this)
        whatsappShareCardView.setOnClickListener(this)
        liveStreamResponse = intent.getParcelableExtra<LiveStreamResult>("item")
        root =
            FirebaseDatabase.getInstance().reference.child("live_stream/${liveStreamResponse?.item_id}/chats")

        remainingTimeTextView.text =
            liveStreamResponse?.live_datetime?.let { DateTimeUtils.timeDiffInMinuteAndSeconds(it) }
        tabsLayout.addTab(tabsLayout.newTab().setText(tabsLayout.context.resources.getString(R.string.about_video)))
        tabsLayout.addTab(tabsLayout.newTab().setText(tabsLayout.context.resources.getString(R.string.all_questions)))

        liveStartTimeTextView.text =
            liveStreamResponse?.live_datetime?.let {
                DateTimeUtils.getDateTimeFromTimestampForLives(
                    it
                )
            }

        Picasso.get().load(liveStreamResponse?.image_url).into(headerImageView)
        initializeViewPager(liveStreamResponse)
        initializeFirebase()
        hitUpdateViewCountApi()
    }

    private fun initializeFirebase() {
        root.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                updateChatList(snapshot)
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                updateChatList(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
        })
    }

    private fun initializeViewPager(liveStreamResponse: LiveStreamResult?) {
        upcomingLivesPagerAdapter =
            UpcomingLivesPagerAdapter(supportFragmentManager, liveStreamResponse)
        viewPager.adapter = upcomingLivesPagerAdapter
        viewPager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                tabsLayout
            )
        )
        tabsLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
                if (tab.position == 0) {
                    Utils.shareEventTracking(
                        this@UpcomingLivesActivity,
                        "Live Detail",
                        "Live_Android",
                        "LD_AboutTab_Live"
                    )
                } else {
                    Utils.shareEventTracking(
                        this@UpcomingLivesActivity,
                        "Live Detail",
                        "Live_Android",
                        "LD_QuesTab_Live"
                    )
                }
            }
        })
    }

    private fun updateChatList(snapshot: DataSnapshot) {
        val liveStreamCommentTabFragment =
            upcomingLivesPagerAdapter.instantiateItem(viewPager, 1) as LiveStreamCommentTabFragment
        liveStreamCommentTabFragment.updateChatList(snapshot)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.backNavigationImageView) {
            onBackPressed()
        } else if (v?.id == R.id.whatsappShareCardView) {
            Utils.shareEventTracking(
                this,
                "Live Detail",
                "Live_Android",
                "LD_Whatsapp_Live"
            )
            AppUtils.shareLinkWithSuccessStatusWhatsapp(
                this,
                AppUtils.getUtmParamsAppendedShareUrl(
                    AppConstants.LIVE_STREAM_SHARE_URL + liveStreamResponse?.slug,
                    "LD_Whatsapp_Share",
                    "Share_Android"
                )
            )
        }
    }

    private fun hitUpdateViewCountApi() {
        val updateViewCountRequest = UpdateViewCountRequest()
        updateViewCountRequest.userId = AppConstants.VIDEO_TEAM_USER_ID
        updateViewCountRequest.tags = ArrayList<MutableMap<String, String>>()
        updateViewCountRequest.cities = ArrayList<MutableMap<String, String>>()
        val retrofit = BaseApplication.getInstance().retrofit
        val articleDetailsApi = retrofit.create(ArticleDetailsAPI::class.java)
        val callUpdateViewCount: Call<ResponseBody> =
            articleDetailsApi.updateViewCount(liveStreamResponse?.item_id, updateViewCountRequest)
        callUpdateViewCount.enqueue(updateViewCountResponseCallback)
    }

    private val updateViewCountResponseCallback: Callback<ResponseBody> =
        object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
            }

            override fun onFailure(
                call: Call<ResponseBody>,
                t: Throwable
            ) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }
}
