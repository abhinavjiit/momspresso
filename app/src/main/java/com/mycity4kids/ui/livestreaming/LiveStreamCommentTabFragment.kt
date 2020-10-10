package com.mycity4kids.ui.livestreaming

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.interfaces.CommentPostButtonColorChangeInterface
import com.mycity4kids.models.request.AddEditCommentOrReplyRequest
import com.mycity4kids.models.response.CommentListResponse
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
import com.mycity4kids.tagging.ui.RichEditorView
import com.mycity4kids.utils.ToastUtils
import com.squareup.picasso.Picasso
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Arrays

class LiveStreamCommentTabFragment : BaseFragment(),
    LiveChatRecyclerAdapter.RecyclerViewClickListener, QueryTokenReceiver,
    CommentPostButtonColorChangeInterface {

    private var liveStreamResult: LiveStreamResult? = null
    val commentsList = ArrayList<ChatListData>()
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var addCommentImageView: ImageView
    private lateinit var inputEditText: RichEditorView
    private lateinit var emptyStateTextView: TextView
    private lateinit var userImageView: ImageView

    private val liveChatRecyclerAdapter: LiveChatRecyclerAdapter by lazy {
        LiveChatRecyclerAdapter(this, "")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragView = inflater.inflate(R.layout.live_stream_comments_fragment, container, false)
        chatRecyclerView = fragView.findViewById(R.id.chatRecyclerView)
        addCommentImageView = fragView.findViewById(R.id.addCommentImageView)
        inputEditText = fragView.findViewById(R.id.inputEditText)
        emptyStateTextView = fragView.findViewById(R.id.emptyStateTextView)
        userImageView = fragView.findViewById(R.id.userImageView)

        liveStreamResult = arguments?.getParcelable("item")
        inputEditText.setHint(getString(R.string.all_leave_questions_here))
        val llm = LinearLayoutManager(activity)
        llm.orientation = LinearLayoutManager.VERTICAL
        llm.stackFromEnd = true
        chatRecyclerView.layoutManager = llm
        liveChatRecyclerAdapter.setData(commentsList)
        chatRecyclerView.adapter = liveChatRecyclerAdapter

        Picasso.get().load(SharedPrefUtils.getProfileImgUrl(BaseApplication.getAppContext())).error(
            R.drawable.default_commentor_img
        ).placeholder(
            R.drawable.default_commentor_img
        ).into(userImageView)

        inputEditText.setMaxLines()
        inputEditText.displayTextCounter(false)
        inputEditText.requestFocus()
        inputEditText.setQueryTokenReceiver(this)
        inputEditText.changeButtonColorOnTextChanged(this)

        addCommentImageView.setOnClickListener {
            if (inputEditText.text.toString().isBlank()) {
                ToastUtils.showToast(activity, "Please enter something")
            } else {
                formatMentionDataForApiRequest()
            }
        }

        emptyStateTextView.visibility = View.VISIBLE

        return fragView
    }

    private fun formatMentionDataForApiRequest() {
        val mentionsMap: MutableMap<String, Mentions> =
            java.util.HashMap()
        val commentBody = StringBuilder()
        try {
            val mentionsEditable: MentionsEditable = inputEditText.text
            val marker: MutableList<MentionIndex> = java.util.ArrayList()
            marker.add(MentionIndex(0, null))
            val mentionsList: List<MentionSpan> = inputEditText.mentionSpans
            for (i in mentionsList.indices) {
                val mention = mentionsList[i].mention as Mentions
                marker.add(
                    MentionIndex(
                        mentionsEditable.getSpanStart(mentionsList[i]),
                        mention
                    )
                )
                mentionsMap[mention.userId] = mention
            }
            marker.add(MentionIndex(mentionsEditable.length, null))
            marker.sort()
            val splittedComment = java.util.ArrayList<MentionIndex>()
            for (i in 0 until marker.size - 1) {
                val value = mentionsEditable
                    .subSequence(marker[i].index, marker[i + 1].index)
                splittedComment.add(MentionIndex(value, marker[i].mention))
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
            activity,
            "Live Detail",
            "Live_Android",
            "LD_QuesSubmit_Live"
        )
        val addEditCommentOrReplyRequest =
            AddEditCommentOrReplyRequest()
        addEditCommentOrReplyRequest.post_id = liveStreamResult?.item_id
        addEditCommentOrReplyRequest.message = content
        addEditCommentOrReplyRequest.mentions = mentionsMap
        addEditCommentOrReplyRequest.parent_id = "0"
        addEditCommentOrReplyRequest.is_live = "1"
        when (liveStreamResult?.item_type) {
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

    fun updateChatList(snapshot: DataSnapshot) {
        try {
            val chat = snapshot.getValue(ChatListData::class.java)
            chat?.let { commentsList.add(it) }
        } catch (e: Exception) {
            Log.e("dwada", "" + e.message)
        }
        liveChatRecyclerAdapter.notifyDataSetChanged()
        if (commentsList.size > 0) {
            chatRecyclerView.smoothScrollToPosition(commentsList.size - 1)
        }
        if (commentsList.isEmpty()) {
            emptyStateTextView.visibility = View.VISIBLE
        } else {
            emptyStateTextView.visibility = View.GONE
        }
    }

    override fun onRecyclerItemClick(view: View?, position: Int) {
    }

    class MentionIndex : Comparable<MentionIndex> {
        var index = 0
        var mention: Mentions?
        lateinit var charSequence: CharSequence

        internal constructor(index: Int, mention: Mentions?) {
            this.index = index
            this.mention = mention
        }

        internal constructor(charSequence: CharSequence, mention: Mentions?) {
            this.charSequence = charSequence
            this.mention = mention
        }

        override fun compareTo(other: MentionIndex): Int {
            return index - other.index
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

    override fun onTextChanged(text: String) {
    }
}
