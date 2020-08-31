package com.mycity4kids.ui.livestreaming

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import com.google.android.material.tabs.TabLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI
import com.mycity4kids.utils.FullScreenHelper
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import kotlinx.android.synthetic.main.live_steraming_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiveStreamingActivity : BaseActivity() {

    private lateinit var liveStreamPagerAdapter: LiveStreamPagerAdapter
    lateinit var root: DatabaseReference
    lateinit var liveusers: DatabaseReference
    val commentsList = ArrayList<ChatListData>()
    private val fullScreenHelper: FullScreenHelper = FullScreenHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.live_steraming_activity)

        val roomName = intent.getStringExtra("room")
        root = FirebaseDatabase.getInstance().reference.child("live_stream/$roomName/chats")
        liveusers = FirebaseDatabase.getInstance().reference.child("live_stream/$roomName/users")
        tabsLayout.addTab(tabsLayout.newTab().setText(tabsLayout.context.resources.getString(R.string.about_video)))
        tabsLayout.addTab(
            tabsLayout.newTab().setText(tabsLayout.context.resources.getString(R.string.all_questions))
        )

        getVideoId()
    }

    private fun updateChatList(snapshot: DataSnapshot) {
        val liveStreamCommentTabFragment =
            liveStreamPagerAdapter.instantiateItem(viewPager, 1) as LiveStreamCommentTabFragment
        liveStreamCommentTabFragment.updateChatList(snapshot)
    }

    private fun getVideoId() {
        val retrofit = BaseApplication.getInstance().retrofit
        val articleDetailsApi = retrofit.create(ArticleDetailsAPI::class.java)

        val call = articleDetailsApi.videoId
        call.enqueue(object : Callback<LiveStreamResponse> {
            override fun onFailure(call: Call<LiveStreamResponse>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<LiveStreamResponse>,
                response: Response<LiveStreamResponse>
            ) {
                try {
                    val resData = response.body()
                    val url = resData?.data?.video_url
                    val list = url?.split("?v=")
                    youTubePlayerView.addYouTubePlayerListener(object :
                        AbstractYouTubePlayerListener() {
                        override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                            initializeFirebase()
                            initializeViewPager()
                            list?.get(list.size - 1)?.let { youTubePlayer.loadVideo(it, 0f) }
                            //                        youTubePlayer.loadVideo("DgSLkakMDfE", 0f)
                            addFullScreenListenerToPlayer()
                        }
                    })
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4KException", Log.getStackTraceString(e))
                }
            }
        })
    }

    fun initializeFirebase() {
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

        liveusers.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("dwdwdwd", "ccscscscscscsc")
                var counter = 0
                for (i in snapshot.children) {
                    counter++
                }
                userCountTextView.setText("" + counter)
            }
        })
    }

    private fun addFullScreenListenerToPlayer() {
        youTubePlayerView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                tabsLayout.visibility = View.GONE
                viewPager.visibility = View.GONE
                fullScreenHelper.enterFullScreen()
            }

            override fun onYouTubePlayerExitFullScreen() {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                tabsLayout.visibility = View.VISIBLE
                viewPager.visibility = View.VISIBLE
                fullScreenHelper.exitFullScreen()
            }
        })
    }

    fun initializeViewPager() {
        liveStreamPagerAdapter = LiveStreamPagerAdapter(supportFragmentManager)
        viewPager.adapter = liveStreamPagerAdapter
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
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val map = HashMap<String, Any>()
        map[SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId] = ""
        liveusers.updateChildren(map)
    }

    override fun onStop() {
        super.onStop()
        Log.e("remove", "remove")
        liveusers.child(SharedPrefUtils.getUserDetailModel(this).dynamoId).removeValue()
    }

    override fun onBackPressed() {
        if (youTubePlayerView.isFullScreen()) youTubePlayerView.exitFullScreen() else super.onBackPressed()
    }
}
