package com.mycity4kids.ui

import android.accounts.NetworkErrorException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.request.AddEditCommentOrReplyRequest
import com.mycity4kids.models.response.CommentListData
import com.mycity4kids.models.response.CommentListResponse
import com.mycity4kids.models.response.LikeReactionModel
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity
import com.mycity4kids.ui.activity.ParallelFeedActivity
import com.mycity4kids.ui.activity.ShortStoryContainerActivity
import com.mycity4kids.ui.adapter.ArticleCommentsRecyclerAdapter
import com.mycity4kids.ui.fragment.AddArticleCommentReplyDialogFragment
import com.mycity4kids.ui.fragment.ContentCommentReplyNotificationFragment
import com.mycity4kids.ui.fragment.CommentOptionsDialogFragment
import com.mycity4kids.ui.fragment.ReportContentDialogFragment
import com.mycity4kids.utils.EndlessScrollListener
import com.mycity4kids.utils.ToastUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ContentCommentReplyNotificationActivity : BaseActivity(),
    ArticleCommentsRecyclerAdapter.RecyclerViewClickListener,
    CommentOptionsDialogFragment.ICommentOptionAction, View.OnClickListener {

    private lateinit var commentToolbarTextView: TextView
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var articleCommentsRecyclerAdapter: ArticleCommentsRecyclerAdapter
    private var articleId: String? = null
    private var replyId: String? = null
    private var commentId: String? = null
    lateinit var commentsShimmerLayout: ShimmerFrameLayout
    private var commentList: ArrayList<CommentListData>? = null
    private var paginationId: String? = null
    private var contentType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_comment_reply_notification_activity)
        commentsShimmerLayout = findViewById(R.id.CommentsShimmerLayout)
        commentToolbarTextView = findViewById(R.id.commentToolbarTextView)
        commentRecyclerView = findViewById(R.id.commentRecyclerView)
        contentType = intent?.getStringExtra("contentType")
        articleId = intent?.getStringExtra("articleId")
        replyId = intent?.getStringExtra("replyId")
        commentId = intent?.getStringExtra("commentId")
        val show = intent?.getStringExtra("type")
        commentList = ArrayList()
        getComments(null)
        if ("comment" == show) {
            showCommentFragment(commentId)
        } else if ("reply" == show) {
            showReplyFragment()
        }
        articleCommentsRecyclerAdapter = ArticleCommentsRecyclerAdapter(this, this)
        val linearLayoutManager = LinearLayoutManager(this)
        commentRecyclerView.layoutManager = linearLayoutManager
        commentRecyclerView.adapter = articleCommentsRecyclerAdapter
        articleCommentsRecyclerAdapter.setData(commentList)
        articleCommentsRecyclerAdapter.notifyDataSetChanged()
        commentRecyclerView.addOnScrollListener(object :
            EndlessScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getComments("comment")
            }

        })
        commentToolbarTextView.setOnClickListener(this)
    }

    private fun getComments(type: String?) {

        val retrofit = BaseApplication.getInstance().retrofit
        val articleDetailApi = retrofit.create(ArticleDetailsAPI::class.java)
        val call = articleDetailApi.getArticleComments(articleId, type, paginationId)
        call.enqueue(object : Callback<CommentListResponse> {
            override fun onFailure(call: Call<CommentListResponse>, t: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }

            override fun onResponse(
                call: Call<CommentListResponse>,
                response: Response<CommentListResponse>
            ) {
                commentsShimmerLayout.stopShimmerAnimation()
                commentsShimmerLayout.visibility = View.GONE
                if (response.body() == null) {
                    val nee =
                        NetworkErrorException("New comments API failure")
                    FirebaseCrashlytics.getInstance().recordException(nee)
                    return
                }

                try {
                    val responseData = response.body()
                    responseData?.data?.let {
                        showComments(it as ArrayList<CommentListData>)
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }
        })
    }

    private fun showComments(commentsListData: ArrayList<CommentListData>) {
        if (!commentsListData.isNullOrEmpty()) {
            if (paginationId == null) {
                commentList = commentsListData
            } else {
                commentList?.addAll(commentsListData)
            }

            paginationId = commentsListData.get(commentsListData.size - 1).id
            commentList?.let {
                articleCommentsRecyclerAdapter.setData(it)
                articleCommentsRecyclerAdapter.notifyDataSetChanged()
            }
        }
    }


    private fun showCommentFragment(commentId: String?) {
        commentId?.let {
            val bundle = Bundle()
            bundle.putString("commentId", it)
            bundle.putString("articleId", articleId)
            bundle.putString("show", "comment")
            bundle.putString("contentType", contentType)
            val articleShortStoryMomVlogCommentAndReplyNotificationFragment =
                ContentCommentReplyNotificationFragment()
            articleShortStoryMomVlogCommentAndReplyNotificationFragment.arguments = bundle
            (this@ContentCommentReplyNotificationActivity).addFragment(
                articleShortStoryMomVlogCommentAndReplyNotificationFragment,
                bundle
            )
        }
    }

    private fun showReplyFragment() {
        val bundle = Bundle()
        bundle.putString("commentId", commentId)
        bundle.putString("articleId", articleId)
        bundle.putString("replyId", replyId)
        bundle.putString("show", "replies")
        bundle.putString("contentType", contentType)
        val articleShortStoryMomVlogCommentAndReplyNotificationFragment =
            ContentCommentReplyNotificationFragment()
        articleShortStoryMomVlogCommentAndReplyNotificationFragment.arguments = bundle
        (this@ContentCommentReplyNotificationActivity).addFragment(
            articleShortStoryMomVlogCommentAndReplyNotificationFragment,
            bundle
        )
    }

    override fun onRecyclerItemClick(view: View?, position: Int) {
        when (view?.id) {
            R.id.likeTextView -> {
                if (commentList?.get(position)?.liked!!) {
                    commentList?.get(position)?.liked = false
                    val commentListData = LikeReactionModel()
                    commentListData.reaction = "like"
                    commentListData.status = "0"
                    commentList?.get(position)?.likeCount =
                        commentList?.get(position)?.likeCount?.minus(1)!!
                    val retrofit = BaseApplication.getInstance().retrofit
                    val articleDetailsAPI = retrofit.create(
                        ArticleDetailsAPI::class.java
                    )
                    val call = articleDetailsAPI
                        .likeDislikeComment(commentList?.get(position)?.id, commentListData)
                    call.enqueue(likeDisLikeCommentCallback)
                } else {
                    commentList?.get(position)?.liked = true
                    val commentListData = LikeReactionModel()
                    commentListData.reaction = "like"
                    commentListData.status = "1"
                    commentList?.get(position)?.likeCount =
                        commentList?.get(position)?.likeCount?.plus(1)!!
                    val retrofit = BaseApplication.getInstance().retrofit
                    val articleDetailsAPI = retrofit.create(
                        ArticleDetailsAPI::class.java
                    )
                    val call = articleDetailsAPI
                        .likeDislikeComment(commentList?.get(position)?.id, commentListData)
                    call.enqueue(likeDisLikeCommentCallback)
                }
                articleCommentsRecyclerAdapter.notifyDataSetChanged()
            }

            R.id.moreOptionImageView -> {
                val args = Bundle()
                args.putInt("position", position)
                args.putString("authorId", commentList?.get(position)?.userId)
                args.putString("responseType", "COMMENT")
                val commentOptionsDialogFragment =
                    CommentOptionsDialogFragment()
                commentOptionsDialogFragment.arguments = args
                commentOptionsDialogFragment.isCancelable = true
                val fm: FragmentManager = supportFragmentManager
                commentOptionsDialogFragment.show(fm, "Comment Options")
            }
            R.id.replyCommentTextView -> {
                if (commentList?.get(position)?.repliesCount == 0) {
                    openAddCommentReplyDialog(commentList?.get(position))
                } else {
                    showCommentFragment(commentList?.get(position)?.id)
                }
            }
        }
    }

    fun openAddCommentReplyDialog(commentData: CommentListData?) {
        val args = Bundle()
        args.putParcelable("parentCommentData", commentData)
        val addArticleCommentReplyDialogFragment =
            AddArticleCommentReplyDialogFragment()
        addArticleCommentReplyDialogFragment.arguments = args
        addArticleCommentReplyDialogFragment.isCancelable = true
        val fm: FragmentManager = supportFragmentManager
        addArticleCommentReplyDialogFragment.show(fm, "Add Replies")
    }

    private val likeDisLikeCommentCallback: Callback<ResponseBody> =
        object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
            }

            override fun onFailure(
                call: Call<ResponseBody?>,
                e: Throwable
            ) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }


    override fun onStart() {
        super.onStart()
        commentsShimmerLayout.startShimmerAnimation()
    }

    override fun onStop() {
        super.onStop()
        commentsShimmerLayout.stopShimmerAnimation()
    }


    override fun onBackPressed() {
        val fragmentCount = supportFragmentManager.backStackEntryCount
        if (fragmentCount == 0) {
            super.onBackPressed()
            when (contentType) {
                "0" -> {
                    val intent = Intent(this, ArticleDetailsContainerActivity::class.java)
                    intent.putExtra(Constants.ARTICLE_ID, articleId)
                    startActivity(intent)
                }
                "1" -> {
                    val intent = Intent(this, ShortStoryContainerActivity::class.java)
                    intent.putExtra(Constants.ARTICLE_ID, articleId)
                    startActivity(intent)
                }

                "2" -> {
                    val intent = Intent(this, ParallelFeedActivity::class.java)
                    intent.putExtra(Constants.ARTICLE_ID, articleId)
                    startActivity(intent)
                }
                else -> {

                }
            }
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }

    }

    fun deleteComment(commentId: String) {
        commentList?.let {
            for (i in 0 until it.size) {
                if (it[i].id == commentId) {
                    it.removeAt(i)
                    articleCommentsRecyclerAdapter.setData(it)
                    articleCommentsRecyclerAdapter.notifyDataSetChanged()
                    break
                }
            }
        }
    }

    fun deleteReply(commentId: String) {
        commentList?.let {
            for (i in 0 until it.size) {
                if (it[i].id == commentId) {
                    it[i].repliesCount = it[i].repliesCount.minus(1)
                    articleCommentsRecyclerAdapter.setData(it)
                    articleCommentsRecyclerAdapter.notifyDataSetChanged()
                    break
                }
            }
        }
    }

    fun addReply(commentId: String) {
        commentList?.let {
            for (i in 0 until it.size) {
                if (it[i].id == commentId) {
                    it[i].repliesCount = it[i].repliesCount.plus(1)
                    articleCommentsRecyclerAdapter.setData(it)
                    articleCommentsRecyclerAdapter.notifyDataSetChanged()
                    break
                }
            }
        }
    }

    fun likeDislikeComment(commentId: String, liked: Boolean) {
        commentList?.let {
            for (i in 0 until it.size) {
                if (it[i].id == commentId) {
                    it[i].liked = liked
                    articleCommentsRecyclerAdapter.setData(it)
                    articleCommentsRecyclerAdapter.notifyDataSetChanged()
                    break
                }
            }
        }
    }

    override fun onResponseDelete(position: Int, responseType: String?) {
        val retrofit = BaseApplication.getInstance().retrofit
        val articleDetailsAPI = retrofit.create(
            ArticleDetailsAPI::class.java
        )
        val call =
            articleDetailsAPI.deleteCommentOrReply(commentList?.get(position)?.id)
        call.enqueue(deleteCommentResponseListener)

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

                    ToastUtils.showToast(
                        this@ContentCommentReplyNotificationActivity,
                        "Failed to delete comment. Please try again"
                    )

                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {
                        commentId?.let {
                            deleteComment(it)
                            /* (activity as ArticleShortStoryMomVlogCommentNotificationActivity).deleteComment(
                                 it
                             )*/
                        }
                    } else {
                        ToastUtils.showToast(
                            this@ContentCommentReplyNotificationActivity,
                            "Failed to delete comment. Please try again"
                        )

                    }
                } catch (e: Exception) {
                    ToastUtils.showToast(
                        this@ContentCommentReplyNotificationActivity,
                        "Failed to delete comment. Please try again"
                    )

                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(
                call: Call<CommentListResponse?>,
                t: Throwable
            ) {
                removeProgressDialog()
                ToastUtils.showToast(
                    this@ContentCommentReplyNotificationActivity,
                    "Failed to add comment. Please try again"
                )
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }

    override fun onResponseReport(position: Int, responseType: String?) {
        val reportContentDialogFragment = ReportContentDialogFragment()
        val fm = supportFragmentManager
        val _args = Bundle()
        _args.putString("postId", commentList?.get(position)?.id)
        _args.putInt("type", AppConstants.REPORT_TYPE_COMMENT)
        reportContentDialogFragment.arguments = _args
        reportContentDialogFragment.isCancelable = true
        reportContentDialogFragment.show(fm, "Report Content")
    }

    override fun onResponseEdit(position: Int, responseType: String?) {
        val addArticleCommentReplyDialogFragment =
            AddArticleCommentReplyDialogFragment()
        val fm = supportFragmentManager
        val _args = Bundle()
        _args.putString("action", "EDIT_COMMENT")
        _args.putInt("position", 0)
        _args.putParcelable("parentCommentData", commentList?.get(position))
        addArticleCommentReplyDialogFragment.arguments = _args
        addArticleCommentReplyDialogFragment.isCancelable = true
        addArticleCommentReplyDialogFragment.show(fm, "Add Comment")
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
                removeProgressDialog()
                commentList?.get(position)?.message = content
                articleCommentsRecyclerAdapter.setData(commentList)
                articleCommentsRecyclerAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.commentToolbarTextView) {
            onBackPressed()
        }
    }
}