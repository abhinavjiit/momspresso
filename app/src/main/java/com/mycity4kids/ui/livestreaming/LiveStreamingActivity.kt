package com.mycity4kids.ui.livestreaming

import android.content.pm.ActivityInfo
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.interfaces.CommentPostButtonColorChangeInterface
import com.mycity4kids.models.request.AddEditCommentOrReplyRequest
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest
import com.mycity4kids.models.request.UpdateViewCountRequest
import com.mycity4kids.models.response.CommentListResponse
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI
import com.mycity4kids.retrofitAPIsInterfaces.SearchArticlesAuthorsAPI
import com.mycity4kids.tagging.Mentions
import com.mycity4kids.tagging.MentionsResponse
import com.mycity4kids.tagging.mentions.MentionSpan
import com.mycity4kids.tagging.mentions.MentionsEditable
import com.mycity4kids.tagging.suggestions.SuggestionsResult
import com.mycity4kids.tagging.tokenization.QueryToken
import com.mycity4kids.tagging.tokenization.interfaces.QueryTokenReceiver
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.FullScreenHelper
import com.mycity4kids.utils.ToastUtils
import com.mycity4kids.widget.ResizableTextView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.squareup.picasso.Picasso
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Arrays
import kotlinx.android.synthetic.main.live_streaming_activity.*
import okhttp3.ResponseBody
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiveStreamingActivity : BaseActivity(), LiveChatRecyclerAdapter.RecyclerViewClickListener,
    View.OnClickListener, QueryTokenReceiver, ResizableTextView.SeeMore,
    CommentPostButtonColorChangeInterface {

    private var liveStreamResponse: LiveStreamResult? = null
    private var root: DatabaseReference? = null
    private var liveusers: DatabaseReference? = null
    private var liveLikes: DatabaseReference? = null
    private val fullScreenHelper: FullScreenHelper = FullScreenHelper(this)
    val commentsList = ArrayList<ChatListData>()
    private var recommendStatus = 0

    private val liveChatRecyclerAdapter: LiveChatRecyclerAdapter by lazy {
        LiveChatRecyclerAdapter(this, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.live_streaming_activity)
        val llm = LinearLayoutManager(this)

        inputEditText.setMaxLines()
        inputEditText.displayTextCounter(false)
        inputEditText.requestFocus()
        inputEditText.setQueryTokenReceiver(this)
        inputEditText.changeButtonColorOnTextChanged(this)
        inputEditText.setHint(getString(R.string.all_leave_questions_here))

        Picasso.get().load(SharedPrefUtils.getProfileImgUrl(this)).error(R.drawable.default_commentor_img).placeholder(
            R.drawable.default_commentor_img
        ).into(userImageView)

        llm.orientation = LinearLayoutManager.VERTICAL
        llm.stackFromEnd = true
        chatRecyclerView.layoutManager = llm
        liveChatRecyclerAdapter.setData(commentsList)
        chatRecyclerView.adapter = liveChatRecyclerAdapter
        liveStreamResponse = intent.getParcelableExtra("item")
        initializeYoutubePlayer()
        likeImageView.setOnClickListener(this)
        backNavigationImageView.setOnClickListener(this)
        whatsappShareImageView.setOnClickListener(this)
        hitUpdateViewCountApi()
    }

    private fun initializeYoutubePlayer() {
        val url = liveStreamResponse?.video_url

        titleTextView.text = liveStreamResponse?.name
        liveStreamResponse?.description?.let {
            descriptionTextView.text = it
            descriptionTextView.setUserBio(it, this)
        }
        root =
            FirebaseDatabase.getInstance().reference.child("live_stream/${liveStreamResponse?.item_id}/chats")
        liveusers =
            FirebaseDatabase.getInstance().reference.child("live_stream/${liveStreamResponse?.item_id}/users")
        liveLikes =
            FirebaseDatabase.getInstance().reference.child("live_stream/${liveStreamResponse?.item_id}/likes")
        val list: List<String>
        list = if (url?.contains("?v=")!!) {
            url.split("?v=")
        } else {
            url.split("/")
        }

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
                            this@LiveStreamingActivity,
                            "Please enter something"
                        )
                    } else {
                        formatMentionDataForApiRequest()
                    }
                }
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
                var counter = 0
                for (i in snapshot.children) {
                    counter++
                }
                userCountTextView.text = getString(R.string.live_watching_count, counter)
            }
        })

        liveLikes?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    likeCountTextView.text =
                        AppUtils.withSuffix(snapshot.child("count").value as Long)
                } catch (e: Exception) {
                    likeCountTextView.text = "" + snapshot.child("count").value
                }
            }
        })
    }

    private fun formatMentionDataForApiRequest() {
        val mentionsMap: MutableMap<String, Mentions> =
            java.util.HashMap()
        val commentBody = StringBuilder()
        try {
            val mentionsEditable: MentionsEditable = inputEditText.text
            val marker: MutableList<LiveStreamCommentTabFragment.MentionIndex> =
                java.util.ArrayList()
            marker.add(LiveStreamCommentTabFragment.MentionIndex(0, null))
            val mentionsList: List<MentionSpan> = inputEditText.mentionSpans
            for (i in mentionsList.indices) {
                val mention = mentionsList[i].mention as Mentions
                marker.add(
                    LiveStreamCommentTabFragment.MentionIndex(
                        mentionsEditable.getSpanStart(mentionsList[i]),
                        mention
                    )
                )
                mentionsMap[mention.userId] = mention
            }
            marker.add(LiveStreamCommentTabFragment.MentionIndex(mentionsEditable.length, null))
            marker.sort()
            val splittedComment = java.util.ArrayList<LiveStreamCommentTabFragment.MentionIndex>()
            for (i in 0 until marker.size - 1) {
                val value = mentionsEditable
                    .subSequence(marker[i].index, marker[i + 1].index)
                splittedComment.add(
                    LiveStreamCommentTabFragment.MentionIndex(
                        value,
                        marker[i].mention
                    )
                )
            }
            for (i in splittedComment.indices) {
                if (splittedComment[i].mention != null) {
                    commentBody.append(
                        StringUtils
                            .replaceFirst(
                                splittedComment[i].charSequence.toString(),
                                splittedComment[i].mention?.name,
                                "[~userId:" + splittedComment[i].mention?.userId + "]"
                            )
                    )
                } else {
                    commentBody.append(splittedComment[i].charSequence)
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
        addComment(commentBody.toString(), mentionsMap)
    }

    fun addComment(content: String, mentionsMap: Map<String, Mentions>?) {
        Utils.shareEventTracking(
            this,
            "Live Detail",
            "Live_Android",
            "LD_QuesSubmit_Live"
        )
        val addEditCommentOrReplyRequest =
            AddEditCommentOrReplyRequest()
        addEditCommentOrReplyRequest.post_id = liveStreamResponse?.item_id
        addEditCommentOrReplyRequest.message = content
        addEditCommentOrReplyRequest.mentions = mentionsMap
        addEditCommentOrReplyRequest.parent_id = "0"
        addEditCommentOrReplyRequest.is_live = "1"
        when (liveStreamResponse?.item_type) {
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
        commentCountTextView.text = "" + commentsList.size + " Comments"
        liveChatRecyclerAdapter.notifyDataSetChanged()
        chatRecyclerView.smoothScrollToPosition(commentsList.size - 1)
    }

    private fun addFullScreenListenerToPlayer() {
        youTubePlayerView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                Utils.shareEventTracking(
                    this@LiveStreamingActivity,
                    "Live Detail",
                    "Live_Android",
                    "LD_PlayerExpand_Live"
                )
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
        liveusers?.child(SharedPrefUtils.getUserDetailModel(this).dynamoId)?.removeValue()
    }

    private fun likeUnlikeArticle(status: String) {
        val recommendUnrecommendArticleRequest =
            RecommendUnrecommendArticleRequest()
        recommendUnrecommendArticleRequest.articleId = liveStreamResponse?.item_id
        recommendUnrecommendArticleRequest.status = status
        recommendUnrecommendArticleRequest.is_live = "1"
        val retro = BaseApplication.getInstance().retrofit
        val articleDetailsApi = retro.create(ArticleDetailsAPI::class.java)
        val recommendUnrecommendArticle: Call<RecommendUnrecommendArticleResponse> =
            articleDetailsApi
                .recommendUnrecommendArticle(recommendUnrecommendArticleRequest)
        recommendUnrecommendArticle.enqueue(recommendUnrecommendArticleResponseCallback)
    }

    private val recommendUnrecommendArticleResponseCallback: Callback<RecommendUnrecommendArticleResponse> =
        object : Callback<RecommendUnrecommendArticleResponse> {
            override fun onResponse(
                call: Call<RecommendUnrecommendArticleResponse?>,
                response: Response<RecommendUnrecommendArticleResponse?>
            ) {
                if (null == response.body()) {
                    showToast(getString(R.string.server_went_wrong))
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                        if (recommendStatus == 0) {
                            recommendStatus = 1
                            val drawable =
                                ContextCompat.getDrawable(likeImageView.context, R.drawable.ic_like)
                            likeImageView.setImageDrawable(drawable)
                        } else {
                            val drawable = ContextCompat.getDrawable(
                                likeImageView.context,
                                R.drawable.ic_like_grey
                            )
                            drawable!!.setColorFilter(
                                ContextCompat.getColor(
                                    likeImageView.context,
                                    R.color.app_red
                                ), PorterDuff.Mode.SRC_IN
                            )
                            likeImageView.setImageDrawable(drawable)
                            recommendStatus = 0
                        }
                        showToast(responseData.reason)
                    } else {
                        if (responseData.code == 401) {
                            showToast(responseData.reason)
                        } else {
                            showToast(getString(R.string.went_wrong))
                        }
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(
                call: Call<RecommendUnrecommendArticleResponse?>,
                t: Throwable
            ) {
                handleExceptions(t)
            }
        }

    private fun handleExceptions(t: Throwable) {
        if (t is UnknownHostException) {
            showToast(getString(R.string.error_network))
        } else if (t is SocketTimeoutException) {
            showToast(getString(R.string.connection_timeout))
        }
        FirebaseCrashlytics.getInstance().recordException(t)
        Log.d("MC4kException", Log.getStackTraceString(t))
    }

    override fun onBackPressed() {
        if (youTubePlayerView.isFullScreen()) youTubePlayerView.exitFullScreen() else super.onBackPressed()
    }

    override fun onRecyclerItemClick(view: View?, position: Int) {
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.likeImageView) {
            if (recommendStatus == 0) {
                Utils.shareEventTracking(
                    this,
                    "Live Detail",
                    "Live_Android",
                    "LD_Like_Live"
                )
                likeUnlikeArticle("1")
            } else {
                likeUnlikeArticle("0")
            }
        } else if (v?.id == R.id.backNavigationImageView) {
            onBackPressed()
        } else if (v?.id == R.id.whatsappShareImageView) {
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

    override fun onQueryReceived(queryToken: QueryToken): MutableList<String> {
        val receiver: QueryTokenReceiver = inputEditText
        val retro = BaseApplication.getInstance().retrofit
        val searchArticlesAuthorsApi =
            retro.create(
                SearchArticlesAuthorsAPI::class.java
            )
        val call =
            searchArticlesAuthorsApi.searchUserHandles(queryToken.keywords)
        call.enqueue(object : Callback<MentionsResponse?> {
            override fun onResponse(
                call: Call<MentionsResponse?>,
                response: Response<MentionsResponse?>
            ) {
                try {
                    if (response.isSuccessful) {
                        val responseModel = response.body()
                        val suggestions: List<Mentions> =
                            java.util.ArrayList(responseModel!!.data.result)
                        val result = SuggestionsResult(queryToken, suggestions)
                        inputEditText.onReceiveSuggestionsResult(result, "dddd")
                    }
                } catch (e: java.lang.Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(
                call: Call<MentionsResponse?>,
                t: Throwable
            ) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        })

        return Arrays.asList("dddd")
    }

    override fun onSeeMoreClick(userBio: String) {
        Utils.shareEventTracking(
            this,
            "Live Detail",
            "Live_Android",
            "LD_ReadMore_Live"
        )
        val bottomSheet = LiveStreamBottomSheetDialog()
        val bundle = Bundle()
        bundle.putString("content", userBio)
        bottomSheet.arguments = bundle
        bottomSheet.show(supportFragmentManager, "")
    }

    override fun onTextChanged(text: String) {
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
