package com.mycity4kids.ui.fragment

import android.accounts.NetworkErrorException
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.getbase.floatingactionbutton.FloatingActionButton
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.request.AddEditCommentOrReplyRequest
import com.mycity4kids.models.response.CommentListData
import com.mycity4kids.models.response.CommentListResponse
import com.mycity4kids.models.response.LikeReactionModel
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI
import com.mycity4kids.ui.ContentCommentReplyNotificationActivity
import com.mycity4kids.ui.adapter.ContentCommentReplyNotificationAdapter
import com.mycity4kids.utils.DateTimeUtils
import com.mycity4kids.utils.ToastUtils
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContentCommentReplyNotificationFragment : BaseFragment(),
    View.OnClickListener, ContentCommentReplyNotificationAdapter.RecyclerViewRepliesClickListner,
    CommentOptionsDialogFragment.ICommentOptionAction {

    lateinit var repliesRecyclerView: RecyclerView
    lateinit var openAddReplyDialog: FloatingActionButton
    private var commentId: String? = null
    private var articleId: String? = null
    private lateinit var commentorImageView: ImageView
    private lateinit var commentDataTextView: TextView
    private lateinit var DateTextView: TextView
    private lateinit var likeTextView: TextView
    private lateinit var replyCommentTextView: TextView
    private lateinit var moreOptionImageView: ImageView
    private lateinit var repliesShimmerLayout: ShimmerFrameLayout
    private lateinit var contentCommentReplyNotificationAdapter: ContentCommentReplyNotificationAdapter
    private var type: String? = null
    private var replyId: String? = null
    private lateinit var viewAllTextView: TextView
    private var repliesData: ArrayList<CommentListData>? = null
    private lateinit var commentData: CommentListData
    private var contentType: String? = null
    private var deleteReplyPos: Int = 0
    private lateinit var commentShimmerLayout: ShimmerFrameLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.content_comment_reply_notification_fragment,
            container,
            false
        )
        repliesRecyclerView = view.findViewById(R.id.repliesRecyclerView)
        openAddReplyDialog = view.findViewById(R.id.openAddReplyDialog)
        commentorImageView = view.findViewById(R.id.commentorImageView)
        commentDataTextView = view.findViewById(R.id.commentDataTextView)
        viewAllTextView = view.findViewById(R.id.viewAllTextView)
        DateTextView = view.findViewById(R.id.DateTextView)
        likeTextView = view.findViewById(R.id.likeTextView)
        replyCommentTextView = view.findViewById(R.id.replyCommentTextView)
        moreOptionImageView = view.findViewById(R.id.moreOptionImageView)
        repliesShimmerLayout = view.findViewById(R.id.repliesShimmerLayout)
        commentShimmerLayout = view.findViewById(R.id.commentShimmerLayout)
        commentId = arguments?.getString("commentId")
        articleId = arguments?.getString("articleId")
        replyId = arguments?.getString("replyId")
        type = arguments?.getString("show")
        contentType = arguments?.getString("contentType")
        val linearLayoutManager = LinearLayoutManager(context)
        contentCommentReplyNotificationAdapter = ContentCommentReplyNotificationAdapter(this)
        repliesRecyclerView.layoutManager = linearLayoutManager
        repliesRecyclerView.adapter = contentCommentReplyNotificationAdapter
        if ("comment" == type) {
            getComment()
            getCommentReplies()
        } else if ("replies" == type) {
            getCommentAndReplyData()
        }
        viewAllTextView.setOnClickListener(this)
        likeTextView.setOnClickListener(this)
        replyCommentTextView.setOnClickListener(this)
        moreOptionImageView.setOnClickListener(this)
        return view
    }

    private fun getComment() {
        articleId?.let { articleId ->
            commentId?.let { commentId ->
                val retrofit = BaseApplication.getInstance().retrofit
                val articleDetailApi = retrofit.create(ArticleDetailsAPI::class.java)
                val call = articleDetailApi.getArticleComments(articleId, "replyParent", commentId)
                call.enqueue(object : Callback<CommentListResponse> {
                    override fun onFailure(call: Call<CommentListResponse>, t: Throwable) {
                        FirebaseCrashlytics.getInstance().recordException(t)
                        Log.d("MC4kException", Log.getStackTraceString(t))
                    }

                    override fun onResponse(
                        call: Call<CommentListResponse>,
                        response: Response<CommentListResponse>
                    ) {
                        val responseData = response.body()
                        if (200 == responseData?.code && Constants.SUCCESS == responseData.status) {
                            try {
                                commentShimmerLayout.stopShimmerAnimation()
                                commentShimmerLayout.visibility = View.GONE
                                val commentData = responseData.data?.get(0)
                                setDataInToCommentContainer(commentData)
                            } catch (e: Exception) {
                                FirebaseCrashlytics.getInstance().recordException(e)
                                Log.d("TAG", e.message.toString())
                            }
                        }
                    }
                })
            }
        }
    }

    private fun setDataInToCommentContainer(listData: CommentListData?) {
        listData?.let {
            try {
                commentData = it
                Picasso.get().load(it.userPic.clientApp).error(R.drawable.default_commentor_img).into(
                    commentorImageView
                )
            } catch (e: Exception) {
                commentorImageView.setImageResource(R.drawable.default_commentor_img)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                commentDataTextView.text = (
                    (Html
                        .fromHtml(
                            "<b>" + "<font color=\"#D54058\">" + it.userName + "</font>" +
                                "</b>" +
                                " " +
                                "<font color=\"#4A4A4A\">" + it.message + "</font>", Html.FROM_HTML_MODE_LEGACY
                        ))
                    )
            } else {
                commentDataTextView.text = (
                    (Html
                        .fromHtml(
                            "<b>" + "<font color=\"#D54058\">" + it.userName + "</font>" +
                                "</b>" +
                                " " +
                                "<font color=\"#4A4A4A\">" + it.message + "</font>"
                        ))
                    )
            }

            DateTextView.text =
                DateTimeUtils
                    .getDateFromNanoMilliTimestamp(it.createdTime.toLong())

            if (it.repliesCount > 0) {
                replyCommentTextView.text = "Reply(" + it.repliesCount.toString() + ")"
            } else {
                replyCommentTextView.text = "Reply"
            }

            if (it.liked) {
                val drawable = ContextCompat.getDrawable(likeTextView.context, R.drawable.ic_like)
                likeTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            } else {
                val drawable =
                    ContextCompat.getDrawable(likeTextView.context, R.drawable.ic_like_grey)
                likeTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            }

            if (it.likeCount > 0) {
                likeTextView.text = it.likeCount.toString()
            } else {
                likeTextView.text = ""
            }
        }
    }

    private fun getCommentReplies() {
        articleId?.let { articleId ->
            commentId?.let { commentId ->
                val retrofit = BaseApplication.getInstance().retrofit
                val articleDetailApi = retrofit.create(ArticleDetailsAPI::class.java)
                val repliesCall = articleDetailApi.getArticleCommentRepliesNotification(
                    articleId,
                    "reply",
                    commentId
                )
                repliesCall.enqueue(object : Callback<CommentListResponse> {
                    override fun onFailure(call: Call<CommentListResponse>, t: Throwable) {
                        FirebaseCrashlytics.getInstance().recordException(t)
                        Log.d("MC4kException", Log.getStackTraceString(t))
                    }

                    override fun onResponse(
                        call: Call<CommentListResponse>,
                        response: Response<CommentListResponse>
                    ) {
                        try {
                            val responseData = response.body()
                            if (200 == responseData?.code && Constants.SUCCESS == responseData.status) {
                                val repliesListData = responseData.data as ArrayList
                                repliesShimmerLayout.stopShimmerAnimation()
                                repliesShimmerLayout.visibility = View.GONE
                                repliesData = repliesListData
                                repliesData?.let {
                                    contentCommentReplyNotificationAdapter.setRepliesList(it)
                                    contentCommentReplyNotificationAdapter.notifyDataSetChanged()
                                }
                            }
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)
                            Log.d("MC4kException", Log.getStackTraceString(e))
                        }
                    }
                })
            }
        }
    }

    private fun getCommentAndReplyData() {
        val retrofit = BaseApplication.getInstance().retrofit
        val articleDetailApi = retrofit.create(ArticleDetailsAPI::class.java)
        val call =
            articleDetailApi.getCommentAndReplyData(articleId, "replyParent", commentId, replyId)
        call.enqueue(object : Callback<CommentListResponse> {
            override fun onFailure(call: Call<CommentListResponse>, t: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }

            override fun onResponse(
                call: Call<CommentListResponse>,
                response: Response<CommentListResponse>
            ) {
                try {
                    repliesShimmerLayout.stopShimmerAnimation()
                    repliesShimmerLayout.visibility = View.GONE
                    commentShimmerLayout.stopShimmerAnimation()
                    commentShimmerLayout.visibility = View.GONE
                    val responseData = response.body()
                    if (200 == responseData?.code && Constants.SUCCESS == responseData.status) {
                        val commentData = responseData.data?.get(0)
                        setDataInToCommentContainer(commentData)
                        repliesData = commentData?.replies as ArrayList<CommentListData>
                        repliesData?.let {
                            contentCommentReplyNotificationAdapter.setRepliesList(it)
                            contentCommentReplyNotificationAdapter.notifyDataSetChanged()
                            viewAllTextView.visibility = View.VISIBLE
                        }
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        repliesShimmerLayout.startShimmerAnimation()
        commentShimmerLayout.startShimmerAnimation()
    }

    override fun onStop() {
        super.onStop()
        repliesShimmerLayout.stopShimmerAnimation()
        commentShimmerLayout.stopShimmerAnimation()
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.viewAllTextView) {
            repliesShimmerLayout.visibility = View.VISIBLE
            repliesShimmerLayout.startShimmerAnimation()
            viewAllTextView.visibility = View.GONE
            getCommentReplies()
        } else if (v?.id == R.id.likeTextView) {
            if (commentData.liked) {
                commentData.liked = false
                val drawable =
                    ContextCompat.getDrawable(likeTextView.context, R.drawable.ic_like_grey)
                likeTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                val commentListData = LikeReactionModel()
                commentListData.reaction = "like"
                commentListData.status = "0"
                likeTextView.text = commentData.likeCount.minus(1).toString()
                commentData.likeCount = commentData.likeCount.minus(1)
                val retrofit = BaseApplication.getInstance().retrofit
                val articleDetailsAPI = retrofit.create(
                    ArticleDetailsAPI::class.java
                )
                val call = articleDetailsAPI
                    .likeDislikeComment(commentData.id, commentListData)
                call.enqueue(likeDisLikeCommentCallback)
                commentId?.let {
                    (activity as ContentCommentReplyNotificationActivity).likeDislikeComment(
                        it,
                        false
                    )
                }
            } else {
                commentData.liked = true
                val drawable =
                    ContextCompat.getDrawable(likeTextView.context, R.drawable.ic_like)
                likeTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                val commentListData = LikeReactionModel()
                commentListData.reaction = "like"
                commentListData.status = "1"
                likeTextView.text = commentData.likeCount.plus(1).toString()
                commentData.likeCount = commentData.likeCount.plus(1)
                val retrofit = BaseApplication.getInstance().retrofit
                val articleDetailsAPI = retrofit.create(
                    ArticleDetailsAPI::class.java
                )
                val call = articleDetailsAPI
                    .likeDislikeComment(commentData.id, commentListData)
                call.enqueue(likeDisLikeCommentCallback)
                commentId?.let {
                    (activity as ContentCommentReplyNotificationActivity).likeDislikeComment(
                        it,
                        true
                    )
                }
            }
        } else if (v?.id == R.id.replyCommentTextView) {
            openAddCommentReplyDialog(commentData)
        } else if (v?.id == R.id.moreOptionImageView) {
            val args = Bundle()
            args.putInt("position", 0)
            args.putString("authorId", commentData.userId)
            args.putString("responseType", "COMMENT")
            val commentOptionsDialogFragment =
                CommentOptionsDialogFragment()
            commentOptionsDialogFragment.arguments = args
            commentOptionsDialogFragment.isCancelable = true
            val fm = childFragmentManager
            commentOptionsDialogFragment.show(fm, "Comment Options")
        }
    }

    fun openAddCommentReplyDialog(commentData: CommentListData) {
        val args = Bundle()
        args.putParcelable("parentCommentData", commentData)
        val addArticleCommentReplyDialogFragment =
            AddArticleCommentReplyDialogFragment()
        addArticleCommentReplyDialogFragment.arguments = args
        addArticleCommentReplyDialogFragment.isCancelable = true
        val fm = childFragmentManager
        addArticleCommentReplyDialogFragment.show(fm, "Add Replies")
    }

    override fun onclick(v: View?, position: Int) {
        if (v?.id == R.id.likeTextView) {
            if (repliesData?.get(position)?.liked!!) {
                repliesData?.get(position)?.liked = false
                val commentListData = LikeReactionModel()
                commentListData.reaction = "like"
                commentListData.status = "0"
                repliesData?.get(position)?.likeCount =
                    repliesData?.get(position)?.likeCount?.minus(1)!!
                val retrofit = BaseApplication.getInstance().retrofit
                val articleDetailsAPI = retrofit.create(
                    ArticleDetailsAPI::class.java
                )
                val call = articleDetailsAPI
                    .likeDislikeComment(repliesData?.get(position)?.id, commentListData)
                call.enqueue(likeDisLikeCommentCallback)
            } else {
                repliesData?.get(position)?.liked = true
                val commentListData = LikeReactionModel()
                commentListData.reaction = "like"
                commentListData.status = "1"
                repliesData?.get(position)?.likeCount =
                    repliesData?.get(position)?.likeCount?.plus(1)!!
                val retrofit = BaseApplication.getInstance().retrofit
                val articleDetailsAPI = retrofit.create(
                    ArticleDetailsAPI::class.java
                )
                val call = articleDetailsAPI
                    .likeDislikeComment(repliesData?.get(position)?.id, commentListData)
                call.enqueue(likeDisLikeCommentCallback)
            }
            contentCommentReplyNotificationAdapter.notifyDataSetChanged()
        } else if (v?.id == R.id.moreOptionImageView) {
            val commentOptionsDialogFragment =
                CommentOptionsDialogFragment()
            val fm = childFragmentManager
            val _args = Bundle()
            _args.putInt("position", position)
            _args.putString("responseType", "REPLY")
            _args.putString("authorId", repliesData?.get(position)?.userId)
            commentOptionsDialogFragment.arguments = _args
            commentOptionsDialogFragment.isCancelable = true
            commentOptionsDialogFragment.show(fm, "Comment Options")
        }
    }

    private val likeDisLikeCommentCallback: Callback<ResponseBody> =
        object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if (response == null || null == response.body()) {
                    val nee =
                        NetworkErrorException(response.raw().toString())
                    FirebaseCrashlytics.getInstance().recordException(nee)
                    if (isAdded) {
                        ToastUtils.showToast(
                            activity,
                            resources.getString(R.string.server_went_wrong)
                        )
                    }
                    return
                }
                try {
                    val resData = String(response.body()!!.bytes())
                    val jsonObject = JSONObject(resData)
                    if (jsonObject.getJSONObject("status").toString() == Constants.SUCCESS && jsonObject
                            .getJSONObject("code").toString() == "200") {
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(
                call: Call<ResponseBody?>,
                e: Throwable
            ) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

    fun addReply(content: String?, parentCommentId: String?) {
        showProgressDialog("Adding Reply")
        val addEditCommentOrReplyRequest =
            AddEditCommentOrReplyRequest()
        addEditCommentOrReplyRequest.post_id = articleId
        addEditCommentOrReplyRequest.message = content
        addEditCommentOrReplyRequest.parent_id = parentCommentId
        when (contentType) {
            "2" -> {
                addEditCommentOrReplyRequest.type = "video"
            }
            "0" -> {
                addEditCommentOrReplyRequest.type = "article"
            }
            else -> {
                addEditCommentOrReplyRequest.type = "story"
            }
        }
        val ret = BaseApplication.getInstance().retrofit
        val articleDetailsApi = ret.create(ArticleDetailsAPI::class.java)
        val call: Call<CommentListResponse> =
            articleDetailsApi.addCommentOrReply(addEditCommentOrReplyRequest)
        call.enqueue(addReplyResponseListener)
    }

    private val addReplyResponseListener: Callback<CommentListResponse> =
        object : Callback<CommentListResponse> {
            override fun onResponse(
                call: Call<CommentListResponse?>,
                response: Response<CommentListResponse?>
            ) {
                removeProgressDialog()
                if (response.body() == null) {
                    val nee =
                        NetworkErrorException(response.raw().toString())
                    FirebaseCrashlytics.getInstance().recordException(nee)
                    if (isAdded) {
                        ToastUtils.showToast(
                            activity,
                            "Failed to add comment. Please try again"
                        )
                    }
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {

                        val commentListData = CommentListData()
                        commentListData.id = responseData.data[0].id
                        commentListData.message = responseData.data[0].message
                        commentListData.createdTime = responseData.data[0].createdTime
                        commentListData.postId = responseData.data[0].postId
                        commentListData.parentCommentId = responseData.data[0].parentCommentId
                        commentListData.userPic = responseData.data[0].userPic
                        commentListData.userName = responseData.data[0].userName
                        commentListData.userId = responseData.data[0].userId
                        replyCommentTextView.text =
                            "Reply(" + commentData.repliesCount.plus(1).toString() + ")"
                        commentData.repliesCount = commentData.repliesCount.plus(1)
                        val repliesListData = ArrayList<CommentListData>()
                        if (repliesData.isNullOrEmpty()) {
                            repliesListData.add(commentListData)
                            repliesData = repliesListData
                        } else {
                            repliesData?.add(commentListData)
                        }
                        repliesData?.let {
                            contentCommentReplyNotificationAdapter.setRepliesList(it)
                            contentCommentReplyNotificationAdapter.notifyDataSetChanged()
                        }
                        commentId?.let {
                            (activity as ContentCommentReplyNotificationActivity).addReply(
                                it
                            )
                        }
                    } else {
                        if (isAdded) {
                            ToastUtils.showToast(
                                activity,
                                "Failed to add comment. Please try again"
                            )
                        }
                    }
                } catch (e: Exception) {
                    if (isAdded) {
                        ToastUtils.showToast(
                            activity,
                            "Failed to add comment. Please try again"
                        )
                    }
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(
                call: Call<CommentListResponse?>,
                t: Throwable
            ) {
                removeProgressDialog()
                if (isAdded) {
                    ToastUtils.showToast(
                        activity,
                        "Failed to add comment. Please try again"
                    )
                }
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }

    override fun onResponseDelete(position: Int, responseType: String?) {
        if ("REPLY" == responseType) {
            deleteReplyPos = position
            val retrofit = BaseApplication.getInstance().retrofit
            val articleDetailsAPI = retrofit.create(
                ArticleDetailsAPI::class.java
            )
            val call = articleDetailsAPI
                .deleteCommentOrReply(repliesData?.get(position)?.id)
            call.enqueue(deleteReplyResponseListener)
        } else {

            val retrofit = BaseApplication.getInstance().retrofit
            val articleDetailsAPI = retrofit.create(
                ArticleDetailsAPI::class.java
            )
            val call =
                articleDetailsAPI.deleteCommentOrReply(commentData.id)
            call.enqueue(deleteCommentResponseListener)
        }
    }

    private val deleteCommentResponseListener: Callback<CommentListResponse> =
        object : Callback<CommentListResponse> {
            override fun onResponse(
                call: Call<CommentListResponse?>,
                response: Response<CommentListResponse?>
            ) {
                removeProgressDialog()
                if (response.body() == null) {
                    val nee =
                        NetworkErrorException(response.raw().toString())
                    FirebaseCrashlytics.getInstance().recordException(nee)
                    if (isAdded) {
                        ToastUtils.showToast(
                            activity,
                            "Failed to delete comment. Please try again"
                        )
                    }
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {
                        commentId?.let {
                            (activity as ContentCommentReplyNotificationActivity).deleteComment(
                                it
                            )
                        }
                        (activity as ContentCommentReplyNotificationActivity).onBackPressed()
                    } else {
                        if (isAdded) {
                            ToastUtils.showToast(
                                activity,
                                "Failed to delete comment. Please try again"
                            )
                        }
                    }
                } catch (e: Exception) {
                    if (isAdded) {
                        ToastUtils.showToast(
                            activity,
                            "Failed to delete comment. Please try again"
                        )
                    }
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(
                call: Call<CommentListResponse?>,
                t: Throwable
            ) {
                removeProgressDialog()
                if (isAdded) {
                    ToastUtils.showToast(
                        activity,
                        "Failed to add comment. Please try again"
                    )
                }
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }

    override fun onResponseReport(position: Int, responseType: String?) {
        if ("REPLY" == responseType) {
            val reportContentDialogFragment = ReportContentDialogFragment()
            val fm = childFragmentManager
            val _args = Bundle()
            _args.putString("postId", repliesData?.get(position)?.id)
            _args.putInt("type", AppConstants.REPORT_TYPE_COMMENT)
            reportContentDialogFragment.arguments = _args
            reportContentDialogFragment.isCancelable = true
            reportContentDialogFragment.show(fm, "Report Content")
        } else {
            val reportContentDialogFragment = ReportContentDialogFragment()
            val fm = childFragmentManager
            val _args = Bundle()
            _args.putString("postId", commentData.id)
            _args.putInt("type", AppConstants.REPORT_TYPE_COMMENT)
            reportContentDialogFragment.arguments = _args
            reportContentDialogFragment.isCancelable = true
            reportContentDialogFragment.show(fm, "Report Content")
        }
    }

    override fun onResponseEdit(position: Int, responseType: String?) {
        val addArticleCommentReplyDialogFragment =
            AddArticleCommentReplyDialogFragment()
        val fm = childFragmentManager
        val _args = Bundle()
        if ("COMMENT" == responseType) {
            _args.putString("action", "EDIT_COMMENT")
            _args.putInt("position", 0)
            _args.putParcelable("parentCommentData", commentData)
        } else {
            _args.putString("action", "EDIT_REPLY")
            _args.putInt("position", position)
            _args.putParcelable("parentCommentData", repliesData?.get(position))
        }
        addArticleCommentReplyDialogFragment.arguments = _args
        addArticleCommentReplyDialogFragment.isCancelable = true
        addArticleCommentReplyDialogFragment.show(fm, "Add Comment")
    }

    private val deleteReplyResponseListener: Callback<CommentListResponse> =
        object : Callback<CommentListResponse> {
            override fun onResponse(
                call: Call<CommentListResponse?>,
                response: Response<CommentListResponse?>
            ) {
                removeProgressDialog()
                if (response.body() == null) {
                    val nee =
                        NetworkErrorException(response.raw().toString())
                    FirebaseCrashlytics.getInstance().recordException(nee)
                    if (isAdded) {
                        ToastUtils.showToast(
                            activity,
                            "Failed to add comment. Please try again"
                        )
                    }
                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {
                        repliesData?.removeAt(deleteReplyPos)
                        if (commentData.repliesCount.minus(1) == 0) {
                            replyCommentTextView.text = "Reply"
                        } else {
                            replyCommentTextView.text =
                                "Reply(" + commentData.repliesCount.minus(1).toString() + ")"
                        }
                        commentData.repliesCount = commentData.repliesCount.minus(1)
                        contentCommentReplyNotificationAdapter.notifyDataSetChanged()
                        commentId?.let { it ->
                            (activity as ContentCommentReplyNotificationActivity).deleteReply(
                                it
                            )
                        }
                    } else {
                        if (isAdded) {
                            ToastUtils.showToast(
                                activity,
                                "Failed to add comment. Please try again"
                            )
                        }
                    }
                } catch (e: Exception) {
                    if (isAdded) {
                        ToastUtils.showToast(
                            activity,
                            "Failed to add comment. Please try again"
                        )
                    }
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(
                call: Call<CommentListResponse?>,
                t: Throwable
            ) {
                removeProgressDialog()
                if (isAdded) {
                    ToastUtils.showToast(
                        activity,
                        "Failed to add comment. Please try again"
                    )
                }
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }

    fun editComment(
        content: String,
        responseId: String?,
        position: Int
    ) {
        showProgressDialog("Editing your response")
        val addEditCommentOrReplyRequest =
            AddEditCommentOrReplyRequest()
        addEditCommentOrReplyRequest.post_id = articleId
        addEditCommentOrReplyRequest.message = content
        val retrofit = BaseApplication.getInstance().retrofit
        val articleDetailsApi = retrofit.create(ArticleDetailsAPI::class.java)
        val call: Call<CommentListResponse> =
            articleDetailsApi.editCommentOrReply(responseId, addEditCommentOrReplyRequest)
        call.enqueue(object : Callback<CommentListResponse> {
            override fun onFailure(call: Call<CommentListResponse>, t: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }

            override fun onResponse(
                call: Call<CommentListResponse>,
                response: Response<CommentListResponse>
            ) {
                try {
                    removeProgressDialog()
                    commentData.message = content
                    setDataInToCommentContainer(commentData)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
        })
    }

    fun editReply(content: String, parentCommentId: String, replyId: String, position: Int) {
        showProgressDialog("Editing Reply")
        val addEditCommentOrReplyRequest =
            AddEditCommentOrReplyRequest()
        addEditCommentOrReplyRequest.post_id = articleId
        addEditCommentOrReplyRequest.message = content
        val retrofit = BaseApplication.getInstance().retrofit
        val articleDetailsApi = retrofit.create(ArticleDetailsAPI::class.java)
        val call: Call<CommentListResponse> =
            articleDetailsApi.editCommentOrReply(replyId, addEditCommentOrReplyRequest)
        call.enqueue(object : Callback<CommentListResponse> {
            override fun onFailure(call: Call<CommentListResponse>, t: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }

            override fun onResponse(
                call: Call<CommentListResponse>,
                response: Response<CommentListResponse>
            ) {
                removeProgressDialog()
                try {
                    repliesData?.get(position)?.message = content
                    contentCommentReplyNotificationAdapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
        })
    }
}
