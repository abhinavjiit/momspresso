package com.mycity4kids.ui.livestreaming

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.models.request.AddEditCommentOrReplyRequest
import com.mycity4kids.models.response.CommentListResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI
import com.mycity4kids.tagging.Mentions
import com.mycity4kids.utils.FullScreenHelper
import com.mycity4kids.utils.ToastUtils
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import kotlinx.android.synthetic.main.live_steraming_activity.userCountTextView
import kotlinx.android.synthetic.main.live_steraming_activity.youTubePlayerView
import kotlinx.android.synthetic.main.new_live_steraming_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewLiveStreamingActivity : BaseActivity(), LiveChatRecyclerAdapter.RecyclerViewClickListener {

    private lateinit var liveStreamResponse: LiveStreamResponse
    private lateinit var liveStreamPagerAdapter: LiveStreamPagerAdapter
    private var root: DatabaseReference? = null
    private var liveusers: DatabaseReference? = null
    private val fullScreenHelper: FullScreenHelper = FullScreenHelper(this)
    val commentsList = ArrayList<ChatListData>()

    private val liveChatRecyclerAdapter: LiveChatRecyclerAdapter by lazy {
        LiveChatRecyclerAdapter(this, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_live_steraming_activity)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        llm.stackFromEnd = true
        chatRecyclerView.layoutManager = llm
        liveChatRecyclerAdapter.setData(commentsList)
        chatRecyclerView.adapter = liveChatRecyclerAdapter
        getVideoId()

        backNavigationImageView.setOnClickListener {
            onBackPressed()
        }
    }

    fun addComment(content: String, mentionsMap: Map<String, Mentions>?) {
        val addEditCommentOrReplyRequest =
            AddEditCommentOrReplyRequest()
        addEditCommentOrReplyRequest.post_id = liveStreamResponse.data.item_id
        addEditCommentOrReplyRequest.message = content
        addEditCommentOrReplyRequest.mentions = mentionsMap
        addEditCommentOrReplyRequest.parent_id = "0"
        addEditCommentOrReplyRequest.is_live = "1"
        when (liveStreamResponse.data.item_type) {
            2 -> {
                addEditCommentOrReplyRequest.type = "video"
            }
            0 -> {
                addEditCommentOrReplyRequest.type = "article"
            }
            else -> {
                addEditCommentOrReplyRequest.type = "story"
            }
        }
        inputEditText.text.clear()
        val retrofit = BaseApplication.getInstance().retrofit
        val articleDetailsApi = retrofit.create(ArticleDetailsAPI::class.java)
        val call: Call<CommentListResponse> =
            articleDetailsApi.addCommentOrReply(addEditCommentOrReplyRequest)

        call.enqueue(object : Callback<CommentListResponse> {
            override fun onFailure(call: Call<CommentListResponse>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<CommentListResponse>,
                response: Response<CommentListResponse>
            ) {
            }
        })
    }

    private fun updateChatList(snapshot: DataSnapshot) {
        try {
            val chat = snapshot.getValue(ChatListData::class.java)
            chat?.let { commentsList.add(it) }
        } catch (e: Exception) {
            Log.e("dwada", "" + e.message)
        }
        liveChatRecyclerAdapter.notifyDataSetChanged()
        chatRecyclerView.smoothScrollToPosition(commentsList.size - 1)
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
                liveStreamResponse = response.body()!!
                val url = liveStreamResponse.data.video_url

                titleTextView.text = liveStreamResponse.data.name
                descriptionTextView.text = liveStreamResponse.data.description
                root =
                    FirebaseDatabase.getInstance().reference.child("live_stream/${liveStreamResponse.data.item_id}/chats")
                liveusers =
                    FirebaseDatabase.getInstance().reference.child("live_stream/${liveStreamResponse.data.item_id}/users")

                val list = url.split("?v=")
                youTubePlayerView.addYouTubePlayerListener(object :
                    AbstractYouTubePlayerListener() {
                    override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                        initializeFirebase()
                        val map = HashMap<String, Any>()
                        map[SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId] =
                            ""
                        liveusers?.updateChildren(map)

                        list[list.size - 1].let { youTubePlayer.loadVideo(it, 0f) }
                        addFullScreenListenerToPlayer()

                        addCommentImageView.setOnClickListener {
                            if (inputEditText.text.toString().isBlank()) {
                                ToastUtils.showToast(
                                    this@NewLiveStreamingActivity,
                                    "Please enter something"
                                )
                            } else {
                                //                formatMentionDataForApiRequest()
                                addComment(inputEditText.text.toString(), null)
                            }
                        }
                    }
                })
            }
        })
    }

    fun initializeFirebase() {
        root?.addChildEventListener(object : ChildEventListener {
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

        liveusers?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("dwdwdwd", "ccscscscscscsc")
                var counter = 0
                for (i in snapshot.children) {
                    counter++
                }
                userCountTextView.text = "" + counter
            }
        })
    }

    private fun addFullScreenListenerToPlayer() {
        youTubePlayerView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                chatRecyclerView.visibility = View.GONE
                commentContainer.visibility = View.GONE
                fullScreenHelper.enterFullScreen()
            }

            override fun onYouTubePlayerExitFullScreen() {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                chatRecyclerView.visibility = View.VISIBLE
                commentContainer.visibility = View.VISIBLE
                fullScreenHelper.exitFullScreen()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (liveusers != null) {
            val map = HashMap<String, Any>()
            map[SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId] = ""
            liveusers?.updateChildren(map)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.e("remove", "remove")
        liveusers?.child(SharedPrefUtils.getUserDetailModel(this).dynamoId)?.removeValue()
    }

    override fun onBackPressed() {
        if (youTubePlayerView.isFullScreen()) youTubePlayerView.exitFullScreen() else super.onBackPressed()
    }

    override fun onRecyclerItemClick(view: View?, position: Int) {
    }
}
