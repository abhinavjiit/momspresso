package com.mycity4kids.ui

import android.accounts.NetworkErrorException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
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
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.BlockUserModel
import com.mycity4kids.models.TopCommentData
import com.mycity4kids.models.request.AddEditCommentOrReplyRequest
import com.mycity4kids.models.response.CommentListData
import com.mycity4kids.models.response.CommentListResponse
import com.mycity4kids.models.response.LikeReactionModel
import com.mycity4kids.profile.UserProfileActivity
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI
import com.mycity4kids.tagging.Mentions
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity
import com.mycity4kids.ui.activity.ParallelFeedActivity
import com.mycity4kids.ui.activity.ShortStoryContainerActivity
import com.mycity4kids.ui.adapter.ArticleCommentsRecyclerAdapter
import com.mycity4kids.ui.fragment.AddArticleCommentReplyDialogFragment
import com.mycity4kids.ui.fragment.CommentOptionsDialogFragment
import com.mycity4kids.ui.fragment.ContentCommentReplyNotificationFragment
import com.mycity4kids.ui.fragment.ReportContentDialogFragment
import com.mycity4kids.utils.EndlessScrollListener
import com.mycity4kids.utils.ToastUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject
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
    private lateinit var rLayout: RelativeLayout
    private var blogWriterId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_comment_reply_notification_activity)
        commentsShimmerLayout = findViewById(R.id.CommentsShimmerLayout)
        commentToolbarTextView = findViewById(R.id.commentToolbarTextView)
        commentRecyclerView = findViewById(R.id.commentRecyclerView)
        rLayout = findViewById(R.id.rLayout)
        contentType = intent?.getStringExtra("contentType")
        articleId = intent?.getStringExtra("articleId")
        replyId = intent?.getStringExtra("replyId")
        commentId = intent?.getStringExtra("commentId")
        val show = intent?.getStringExtra("type")
        blogWriterId = intent?.getStringExtra("authorId")
        commentList = ArrayList()
        getComments(null)
        if ("comment" == show || replyId.isNullOrBlank()) {
            showCommentFragment(commentId)
        } else if ("reply" == show) {
            showReplyFragment()
        }
        articleCommentsRecyclerAdapter = ArticleCommentsRecyclerAdapter(this, this, blogWriterId)
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
        rLayout.setOnClickListener(this)
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
            bundle.putString("articleWriterId", blogWriterId)
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
        bundle.putString("articleWriterId", blogWriterId)
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
            R.id.topCommentMarkedTextView -> {
                if (!commentList?.get(position)?.isTopCommentMarked!!) {
                    articleId?.let { articleId ->
                        commentId?.let { commentId ->
                            val topCommentData =
                                TopCommentData(postId = articleId, id = commentId, status = true)
                            markedUnMarkedTopComment(topCommentData)
                            for (i in 0 until commentList?.size!!) {
                                commentList?.get(i)?.isTopCommentMarked = i == position
                                commentList?.get(i)?.isIs_top_comment = false
                            }
                        }
                    }
                    articleCommentsRecyclerAdapter.notifyDataSetChanged()
                }
            }
            R.id.commentorImageView -> {
                val intent = Intent(this, UserProfileActivity::class.java)
                intent.putExtra(Constants.USER_ID, commentList?.get(position)?.userId)
                startActivity(intent)
            }
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
                    pushEvent("CD_Like_Comment")
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
                args.putString("blogWriterId", blogWriterId)
                val commentOptionsDialogFragment =
                    CommentOptionsDialogFragment()
                commentOptionsDialogFragment.arguments = args
                commentOptionsDialogFragment.isCancelable = true
                val fm: FragmentManager = supportFragmentManager
                commentOptionsDialogFragment.show(fm, "Comment Options")
            }
            R.id.replyCommentTextView -> {
                pushEvent("CD_Reply_Comment")
                if (commentList?.get(position)?.repliesCount == 0) {
                    openAddCommentReplyDialog(commentList?.get(position))
                } else {
                    showCommentFragment(commentList?.get(position)?.id)
                }
            }
        }
    }

    private fun markedUnMarkedTopComment(topCommentData: TopCommentData) {
        BaseApplication.getInstance().retrofit.create(ArticleDetailsAPI::class.java).markedTopComment(
            topCommentData
        )
            .subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread()).subscribe(
                object : Observer<ResponseBody> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(responseBody: ResponseBody) {
                        Log.d("MARKED--UNMARKED", responseBody.toString())
                    }

                    override fun onError(e: Throwable) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }

                    override fun onComplete() {}
                })
    }

    fun openAddCommentReplyDialog(commentData: CommentListData?) {
        val args = Bundle()
        args.putParcelable("parentCommentData", commentData)
        args.putString("contentType", contentType)
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
                    intent.putExtra(Constants.VIDEO_ID, articleId)
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

    fun addReply(content: String?, commentId: String, mentionsMap: Map<String, Mentions>?) {
        commentList?.let { commentList ->

            for (i in 0 until commentList.size) {
                if (commentList[i].id == commentId) {
                    commentList[i].repliesCount = commentList[i].repliesCount.plus(1)
                    articleCommentsRecyclerAdapter.setData(commentList)
                    articleCommentsRecyclerAdapter.notifyDataSetChanged()
                    break
                }
            }
            content?.let { content ->
                showProgressDialog("Adding Reply")
                val addEditCommentOrReplyRequest =
                    AddEditCommentOrReplyRequest()
                addEditCommentOrReplyRequest.post_id = articleId
                addEditCommentOrReplyRequest.message = content
                addEditCommentOrReplyRequest.parent_id = commentId
                addEditCommentOrReplyRequest.mentions = mentionsMap
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
                val retrofit = BaseApplication.getInstance().retrofit
                val articleDetailArticle = retrofit.create(ArticleDetailsAPI::class.java)
                val call = articleDetailArticle.addCommentOrReply(addEditCommentOrReplyRequest)
                call.enqueue(object : Callback<CommentListResponse> {
                    override fun onFailure(call: Call<CommentListResponse>, t: Throwable) {
                    }

                    override fun onResponse(
                        call: Call<CommentListResponse>,
                        response: Response<CommentListResponse>
                    ) {
                        removeProgressDialog()
                    }
                })
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

    override fun onBlockUser(position: Int, responseType: String?) {
        showProgressDialog("please wait")
        val ret = BaseApplication.getInstance().retrofit
        val articleDetailsAPI = ret.create(ArticleDetailsAPI::class.java)
        val blockUserModel = BlockUserModel(blocked_user_id = commentList?.get(position)?.userId)
        val call = articleDetailsAPI.blockUserApi(blockUserModel)
        call.enqueue(blockUserCallBack)
        commentList?.removeAt(position)
        articleCommentsRecyclerAdapter.setData(commentList)
        articleCommentsRecyclerAdapter.notifyDataSetChanged()

    }

    private val blockUserCallBack = object : Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            removeProgressDialog()
            showToast("something went wrong")
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }

        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            removeProgressDialog()
            if (response.body() == null) {
                return
            }
            try {
                val resData = String(response.body()?.bytes()!!)
                val jsonObject = JSONObject(resData)
                if (jsonObject.get("code") == 200 && jsonObject.get("status") == Constants.SUCCESS) {
                    Toast.makeText(
                        this@ContentCommentReplyNotificationActivity,
                        jsonObject.getJSONObject("data").get("msg").toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }


            } catch (t: Exception) {
                removeProgressDialog()
                showToast("something went wrong")
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }

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
        _args.putString("contentType", contentType)
        _args.putParcelable("parentCommentData", commentList?.get(position))
        addArticleCommentReplyDialogFragment.arguments = _args
        addArticleCommentReplyDialogFragment.isCancelable = true
        addArticleCommentReplyDialogFragment.show(fm, "Add Comment")
    }

    fun editComment(
        content: String,
        responseId: String?,
        position: Int,
        mentions: MutableMap<String, Mentions>?
    ) {
        showProgressDialog("Editing your response")

        val addEditCommentOrReplyRequest =
            AddEditCommentOrReplyRequest()
        addEditCommentOrReplyRequest.post_id = articleId
        addEditCommentOrReplyRequest.message = content
        addEditCommentOrReplyRequest.mentions = mentions
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
        } else if (v?.id == R.id.rLayout) {
            pushEvent("CD_Comment")
            val args = Bundle()
            args.putString("contentType", contentType)
            val addArticleCommentReplyDialogFragment =
                AddArticleCommentReplyDialogFragment()
            addArticleCommentReplyDialogFragment.arguments = args
            addArticleCommentReplyDialogFragment.isCancelable = true
            val fm: FragmentManager = supportFragmentManager
            addArticleCommentReplyDialogFragment.show(fm, "Add Comment")
        }
    }

    private fun pushEvent(eventSuffix: String) {
        try {
            when (contentType) {
                "0" -> {
                    Utils.shareEventTracking(
                        this, "Article Detail", "Comment_Android",
                        "ArticleDetail_$eventSuffix"
                    )
                }
                "1" -> {
                    Utils.shareEventTracking(
                        this, "100WS Detail", "Comment_Android",
                        "StoryDetail_$eventSuffix"
                    )
                }
                "2" -> {
                    Utils.shareEventTracking(
                        this, "Video Detail", "Comment_Android",
                        "VlogDetail_$eventSuffix"
                    )
                }
            }
        } catch (e: java.lang.Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }

    fun addComment(content: String, mentionsMap: Map<String, Mentions>?) {
        showProgressDialog("Adding Comment")
        val addEditCommentOrReplyRequest =
            AddEditCommentOrReplyRequest()
        addEditCommentOrReplyRequest.post_id = articleId
        addEditCommentOrReplyRequest.message = content
        addEditCommentOrReplyRequest.mentions = mentionsMap
        addEditCommentOrReplyRequest.parent_id = "0"
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
        val retrofit = BaseApplication.getInstance().retrofit
        val articleDetailsApi = retrofit.create(ArticleDetailsAPI::class.java)
        val call: Call<CommentListResponse> =
            articleDetailsApi.addCommentOrReply(addEditCommentOrReplyRequest)
        call.enqueue(addCommentResponseListener)
    }

    private val addCommentResponseListener: Callback<CommentListResponse> =
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
                        "Failed to add comment. Please try again"
                    )

                    return
                }
                try {
                    val responseData = response.body()
                    if (responseData?.code == 200 && Constants.SUCCESS == responseData.status) {
                        val commentModel = CommentListData()
                        commentModel.id = responseData.data[0].id
                        commentModel.message = responseData.data[0].message
                        commentModel.createdTime = responseData.data[0].createdTime
                        commentModel.postId = responseData.data[0].postId
                        commentModel.mentions = responseData.data[0].mentions
                        commentModel.parentCommentId = "0"
                        commentModel.replies = ArrayList()
                        commentModel.repliesCount = 0
                        commentModel.userPic = responseData.data[0].userPic
                        commentModel.userName = responseData.data[0].userName
                        commentModel.userId = responseData.data[0].userId
                        if (commentList.isNullOrEmpty()) {
                            val commentData = ArrayList<CommentListData>()
                            commentData.add(commentModel)
                            commentList = commentData
                        } else {
                            commentList?.add(0, commentModel)
                        }
                        commentList?.let {
                            articleCommentsRecyclerAdapter.setData(it)
                            articleCommentsRecyclerAdapter.notifyDataSetChanged()
                        }
                    } else {
                        ToastUtils.showToast(
                            this@ContentCommentReplyNotificationActivity,
                            "Failed to add comment. Please try again"
                        )
                    }
                } catch (e: Exception) {
                    ToastUtils.showToast(
                        this@ContentCommentReplyNotificationActivity,
                        "Failed to add comment. Please try again"
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
}
