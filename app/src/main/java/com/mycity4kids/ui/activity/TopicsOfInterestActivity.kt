package com.mycity4kids.ui.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.models.Topics
import com.mycity4kids.models.response.ArticleDetailResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI
import com.mycity4kids.widget.ShareButtonWidget
import org.apmem.tools.layouts.FlowLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopicsOfInterestActivity : BaseActivity(), View.OnClickListener {

    private lateinit var back: TextView
    private lateinit var blogEditTextView: TextView
    private lateinit var blogAddTextView: TextView
    private lateinit var articleTopicsFlowLayout: FlowLayout

    private lateinit var vlogEditTextView: TextView
    private lateinit var vlogAddTextView: TextView
    private lateinit var vlogTopicsFlowLayout: FlowLayout

    private lateinit var storyEditTextView: TextView
    private lateinit var storyAddTextView: TextView
    private lateinit var storyTopicsFlowLayout: FlowLayout


    private var followArticleCategories: ArrayList<Topics>? = null
    private var followVideoCategories: ArrayList<Topics>? = null
    private var followStoryCategories: ArrayList<Topics>? = null

    private lateinit var addStoryTopicsTextView: TextView
    private lateinit var addVlogTopicsTextView: TextView
    private lateinit var addBlogTopicsTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics_of_interest)
        back = findViewById(R.id.back)

        addStoryTopicsTextView = findViewById(R.id.addStoryTopicsTextView)
        addVlogTopicsTextView = findViewById(R.id.addVlogTopicsTextView)
        addBlogTopicsTextView = findViewById(R.id.addBlogTopicsTextView)

        blogEditTextView = findViewById(R.id.blogEditTextView)
        blogAddTextView = findViewById(R.id.blogAddTextView)
        articleTopicsFlowLayout = findViewById(R.id.articleTopicsFlowLayout)

        vlogEditTextView = findViewById(R.id.vlogEditTextView)
        vlogAddTextView = findViewById(R.id.vlogAddTextView)
        vlogTopicsFlowLayout = findViewById(R.id.vlogTopicsFlowLayout)

        storyEditTextView = findViewById(R.id.storyEditTextView)
        storyAddTextView = findViewById(R.id.storyAddTextView)
        storyTopicsFlowLayout = findViewById(R.id.storyTopicsFlowLayout)

        getAllFollowedContentTopics()

        blogEditTextView.setOnClickListener(this)
        vlogEditTextView.setOnClickListener(this)
        storyEditTextView.setOnClickListener(this)
        blogAddTextView.setOnClickListener(this)
        vlogAddTextView.setOnClickListener(this)
        storyAddTextView.setOnClickListener(this)


        back.setOnClickListener {
            finish()
        }
    }

    private fun getAllFollowedContentTopics() {
        showProgressDialog("please wait")
        BaseApplication.getInstance().retrofit.create(ArticleDetailsAPI::class.java).getAllFollowedTopics(
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        ).enqueue(object : Callback<ArticleDetailResponse> {
            override fun onResponse(
                call: Call<ArticleDetailResponse>,
                response: Response<ArticleDetailResponse>
            ) {
                removeProgressDialog()
                if (null == response.body()) {
                    return
                }
                try {
                    val res = response.body()
                    if (res?.code == 200 && res.status == "success") {
                        res.data.result.followArticleCategories?.let {
                            if (it.isNotEmpty()) {
                                followArticleCategories = it
                                setArticleFollowedTopicsInToUi(it)
                            } else {
                                blogAddTextView.visibility = View.VISIBLE
                                blogEditTextView.visibility = View.GONE
                                addBlogTopicsTextView.visibility = View.VISIBLE
                            }
                        } ?: run {
                            blogAddTextView.visibility = View.VISIBLE
                            blogEditTextView.visibility = View.GONE
                            addBlogTopicsTextView.visibility = View.VISIBLE
                        }
                        res.data.result.followVideoCategories?.let {
                            if (it.isNotEmpty()) {
                                followVideoCategories = it
                                setVlogFollowedTopicsInToUi(it)
                            } else {
                                vlogAddTextView.visibility = View.VISIBLE
                                vlogEditTextView.visibility = View.GONE
                                addVlogTopicsTextView.visibility = View.VISIBLE

                            }
                        } ?: run {
                            vlogAddTextView.visibility = View.VISIBLE
                            vlogEditTextView.visibility = View.GONE
                            addVlogTopicsTextView.visibility = View.VISIBLE

                        }
                        res.data.result.followStoryCategories?.let {
                            if (it.isNotEmpty()) {
                                followStoryCategories = it
                                setStoryFollowedTopicsInToUi(it)
                            } else {
                                storyAddTextView.visibility = View.VISIBLE
                                storyEditTextView.visibility = View.GONE
                                addStoryTopicsTextView.visibility = View.VISIBLE

                            }
                        } ?: run {
                            storyAddTextView.visibility = View.VISIBLE
                            storyEditTextView.visibility = View.GONE
                            addStoryTopicsTextView.visibility = View.VISIBLE

                        }
                    }


                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<ArticleDetailResponse>, t: Throwable) {
                removeProgressDialog()
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        })
    }

    private fun setArticleFollowedTopicsInToUi(articleFollowedTopics: ArrayList<Topics>) {
        if (articleFollowedTopics.size > 5) {
            for (i in 0..4) {
                val shareButtonWidget = ShareButtonWidget(this)
                val shareTextView = shareButtonWidget.findViewById<TextView>(R.id.shareTextView)
                val layoutParams = shareTextView.layoutParams
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                shareTextView.layoutParams = layoutParams
                shareButtonWidget.tag = articleFollowedTopics[i].id
                shareButtonWidget.setText(articleFollowedTopics[i].display_name)
                shareButtonWidget.setButtonStartImage(null)
                shareButtonWidget.setTextSizeInSP(14)
                shareButtonWidget.setTextGravity(Gravity.CENTER)
                shareButtonWidget.setTextColor(ContextCompat.getColor(this, R.color.app_red))
                shareButtonWidget.setButtonRadiusInDP(4f)
                shareButtonWidget.setBorderColor(ContextCompat.getColor(this, R.color.app_red))
                shareButtonWidget.setBorderThicknessInDP(1f)
                shareButtonWidget.elevation = 0.0f
                shareButtonWidget.setButtonBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.followed_topics_background
                    )
                )

                val params: FlowLayout.LayoutParams = FlowLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(10, 20, 10, 20)
                shareButtonWidget.layoutParams = params
                articleTopicsFlowLayout.addView(shareButtonWidget)
            }
            val viewAllArticleTopicsButtonWidget = ShareButtonWidget(this)
            val shareTextView =
                viewAllArticleTopicsButtonWidget.findViewById<TextView>(R.id.shareTextView)
            val layoutParams = shareTextView.layoutParams
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            shareTextView.layoutParams = layoutParams
            viewAllArticleTopicsButtonWidget.tag = "viewAllBlog"
            viewAllArticleTopicsButtonWidget.setText("View All")
            viewAllArticleTopicsButtonWidget.setButtonStartImage(null)
            viewAllArticleTopicsButtonWidget.setTextSizeInSP(16)
            viewAllArticleTopicsButtonWidget.setTextGravity(Gravity.CENTER)
            viewAllArticleTopicsButtonWidget.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.app_red
                )
            )
            viewAllArticleTopicsButtonWidget.setBorderColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            viewAllArticleTopicsButtonWidget.setBorderThicknessInDP(1f)
            viewAllArticleTopicsButtonWidget.elevation = 0.0f
            val face = Typeface.createFromAsset(
                resources.assets,
                "fonts/Roboto-Bold.ttf"
            )
            shareTextView.typeface = face
            viewAllArticleTopicsButtonWidget.setButtonBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            viewAllArticleTopicsButtonWidget.setOnClickListener {
                removeAllViews(articleTopicsFlowLayout)
                viewAllTopics(articleFollowedTopics, articleTopicsFlowLayout)
            }
            val params: FlowLayout.LayoutParams = FlowLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(10, 20, 10, 20)
            viewAllArticleTopicsButtonWidget.layoutParams = params
            articleTopicsFlowLayout.addView(viewAllArticleTopicsButtonWidget)
        } else {
            articleFollowedTopics.forEach {
                val shareButtonWidget = ShareButtonWidget(this)
                val shareTextView = shareButtonWidget.findViewById<TextView>(R.id.shareTextView)
                val layoutParams = shareTextView.layoutParams
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                shareTextView.layoutParams = layoutParams
                shareButtonWidget.tag = it.id
                shareButtonWidget.setText(it.display_name)
                shareButtonWidget.setButtonStartImage(null)
                shareButtonWidget.setTextSizeInSP(14)
                shareButtonWidget.setTextGravity(Gravity.CENTER)
                shareButtonWidget.setTextColor(ContextCompat.getColor(this, R.color.app_red))
                shareButtonWidget.setButtonRadiusInDP(4f)
                shareButtonWidget.setBorderColor(ContextCompat.getColor(this, R.color.app_red))
                shareButtonWidget.setBorderThicknessInDP(1f)
                shareButtonWidget.elevation = 0.0f
                shareButtonWidget.setButtonBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.followed_topics_background
                    )
                )
                val params: FlowLayout.LayoutParams = FlowLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(10, 20, 10, 20)
                shareButtonWidget.layoutParams = params
                articleTopicsFlowLayout.addView(shareButtonWidget)
            }
        }
    }

    private fun setVlogFollowedTopicsInToUi(vlogFollowedTopics: ArrayList<Topics>) {
        if (vlogFollowedTopics.size > 5) {
            for (i in 0..4) {
                val shareButtonWidget = ShareButtonWidget(this)
                val shareTextView = shareButtonWidget.findViewById<TextView>(R.id.shareTextView)
                val layoutParams = shareTextView.layoutParams
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                shareTextView.layoutParams = layoutParams
                shareButtonWidget.tag = vlogFollowedTopics[i].id
                shareButtonWidget.setText(vlogFollowedTopics[i].display_name)
                shareButtonWidget.setButtonStartImage(null)
                shareButtonWidget.setTextSizeInSP(14)
                shareButtonWidget.setTextGravity(Gravity.CENTER)
                shareButtonWidget.setTextColor(ContextCompat.getColor(this, R.color.app_red))
                shareButtonWidget.setButtonRadiusInDP(4f)
                shareButtonWidget.setBorderColor(ContextCompat.getColor(this, R.color.app_red))
                shareButtonWidget.setBorderThicknessInDP(1f)
                shareButtonWidget.elevation = 0.0f
                shareButtonWidget.setButtonBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.followed_topics_background
                    )
                )
                val params: FlowLayout.LayoutParams = FlowLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(10, 20, 10, 20)
                shareButtonWidget.layoutParams = params
                vlogTopicsFlowLayout.addView(shareButtonWidget)
            }
            val viewAllVlogTopicsButtonWidget = ShareButtonWidget(this)
            val shareTextView =
                viewAllVlogTopicsButtonWidget.findViewById<TextView>(R.id.shareTextView)
            val layoutParams = shareTextView.layoutParams
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            shareTextView.layoutParams = layoutParams
            viewAllVlogTopicsButtonWidget.tag = "viewAllVlog"
            viewAllVlogTopicsButtonWidget.setText("View All")
            viewAllVlogTopicsButtonWidget.setButtonStartImage(null)
            viewAllVlogTopicsButtonWidget.setTextSizeInSP(16)
            viewAllVlogTopicsButtonWidget.setTextGravity(Gravity.CENTER)
            viewAllVlogTopicsButtonWidget.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.app_red
                )
            )
            viewAllVlogTopicsButtonWidget.setBorderColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            viewAllVlogTopicsButtonWidget.setBorderThicknessInDP(1f)
            viewAllVlogTopicsButtonWidget.elevation = 0.0f
            val face = Typeface.createFromAsset(
                resources.assets,
                "fonts/Roboto-Bold.ttf"
            )
            shareTextView.typeface = face
            viewAllVlogTopicsButtonWidget.setButtonBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            viewAllVlogTopicsButtonWidget.setOnClickListener {
                removeAllViews(vlogTopicsFlowLayout)
                viewAllTopics(vlogFollowedTopics, vlogTopicsFlowLayout)
            }
            val params: FlowLayout.LayoutParams = FlowLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(10, 20, 10, 20)
            viewAllVlogTopicsButtonWidget.layoutParams = params
            vlogTopicsFlowLayout.addView(viewAllVlogTopicsButtonWidget)
        } else {
            vlogFollowedTopics.forEach {
                val shareButtonWidget = ShareButtonWidget(this)
                val shareTextView = shareButtonWidget.findViewById<TextView>(R.id.shareTextView)
                val layoutParams = shareTextView.layoutParams
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                shareTextView.layoutParams = layoutParams
                shareButtonWidget.tag = it.id
                shareButtonWidget.setText(it.display_name)
                shareButtonWidget.setButtonStartImage(null)
                shareButtonWidget.setTextSizeInSP(14)
                shareButtonWidget.setTextGravity(Gravity.CENTER)
                shareButtonWidget.setTextColor(ContextCompat.getColor(this, R.color.app_red))
                shareButtonWidget.setButtonRadiusInDP(4f)
                shareButtonWidget.setBorderColor(ContextCompat.getColor(this, R.color.app_red))
                shareButtonWidget.setBorderThicknessInDP(1f)
                shareButtonWidget.elevation = 0.0f
                shareButtonWidget.setButtonBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.followed_topics_background
                    )
                )

                val params: FlowLayout.LayoutParams = FlowLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(10, 20, 10, 20)
                shareButtonWidget.layoutParams = params
                vlogTopicsFlowLayout.addView(shareButtonWidget)
            }
        }
    }

    private fun setStoryFollowedTopicsInToUi(storyFollowedTopics: ArrayList<Topics>) {
        if (storyFollowedTopics.size > 5) {
            for (i in 0..4) {
                val shareButtonWidget = ShareButtonWidget(this)
                val shareTextView = shareButtonWidget.findViewById<TextView>(R.id.shareTextView)
                val layoutParams = shareTextView.layoutParams
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                shareTextView.layoutParams = layoutParams
                shareButtonWidget.tag = storyFollowedTopics[i].id
                shareButtonWidget.setText(storyFollowedTopics[i].display_name)
                shareButtonWidget.setButtonStartImage(null)
                shareButtonWidget.setTextSizeInSP(14)
                shareButtonWidget.setTextGravity(Gravity.CENTER)
                shareButtonWidget.setTextColor(ContextCompat.getColor(this, R.color.app_red))
                shareButtonWidget.setButtonRadiusInDP(4f)
                shareButtonWidget.setBorderColor(ContextCompat.getColor(this, R.color.app_red))
                shareButtonWidget.setBorderThicknessInDP(1f)
                shareButtonWidget.elevation = 0.0f
                shareButtonWidget.setButtonBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.followed_topics_background
                    )
                )
                val params: FlowLayout.LayoutParams = FlowLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(10, 20, 10, 20)
                shareButtonWidget.layoutParams = params
                storyTopicsFlowLayout.addView(shareButtonWidget)

            }
            val viewAllStoryTopicsButtonWidget = ShareButtonWidget(this)
            val shareTextView =
                viewAllStoryTopicsButtonWidget.findViewById<TextView>(R.id.shareTextView)
            val layoutParams = shareTextView.layoutParams
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            shareTextView.layoutParams = layoutParams
            viewAllStoryTopicsButtonWidget.tag = "viewAllVlog"
            viewAllStoryTopicsButtonWidget.setText("View All")
            viewAllStoryTopicsButtonWidget.setButtonStartImage(null)
            viewAllStoryTopicsButtonWidget.setTextSizeInSP(16)
            viewAllStoryTopicsButtonWidget.setTextGravity(Gravity.CENTER)
            viewAllStoryTopicsButtonWidget.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.app_red
                )
            )
            viewAllStoryTopicsButtonWidget.setBorderColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            viewAllStoryTopicsButtonWidget.setBorderThicknessInDP(1f)
            viewAllStoryTopicsButtonWidget.elevation = 0.0f
            val face = Typeface.createFromAsset(
                resources.assets,
                "fonts/Roboto-Bold.ttf"
            )
            shareTextView.typeface = face
            viewAllStoryTopicsButtonWidget.setButtonBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            viewAllStoryTopicsButtonWidget.setOnClickListener {
                removeAllViews(articleTopicsFlowLayout)
                viewAllTopics(storyFollowedTopics, articleTopicsFlowLayout)
            }
            val params: FlowLayout.LayoutParams = FlowLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(10, 20, 10, 20)
            viewAllStoryTopicsButtonWidget.layoutParams = params
            storyTopicsFlowLayout.addView(viewAllStoryTopicsButtonWidget)

        } else {
            storyFollowedTopics.forEach {
                val shareButtonWidget = ShareButtonWidget(this)
                val shareTextView = shareButtonWidget.findViewById<TextView>(R.id.shareTextView)
                val layoutParams = shareTextView.layoutParams
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                shareTextView.layoutParams = layoutParams
                shareButtonWidget.tag = it.id
                shareButtonWidget.setText(it.display_name)
                shareButtonWidget.setButtonStartImage(null)
                shareButtonWidget.setTextSizeInSP(14)
                shareButtonWidget.setTextGravity(Gravity.CENTER)
                shareButtonWidget.setTextColor(ContextCompat.getColor(this, R.color.app_red))
                shareButtonWidget.setButtonRadiusInDP(4f)
                shareButtonWidget.setBorderColor(ContextCompat.getColor(this, R.color.app_red))
                shareButtonWidget.setBorderThicknessInDP(1f)
                shareButtonWidget.elevation = 0.0f
                shareButtonWidget.setButtonBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.followed_topics_background
                    )
                )
                val params: FlowLayout.LayoutParams = FlowLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(10, 20, 10, 20)
                shareButtonWidget.layoutParams = params
                storyTopicsFlowLayout.addView(shareButtonWidget)
            }
        }
    }

    private fun removeAllViews(flowLayout: FlowLayout) {
        flowLayout.removeAllViews()
    }

    private fun viewAllTopics(followedTopics: ArrayList<Topics>, flowLayout: FlowLayout) {
        followedTopics.forEach {
            val shareButtonWidget = ShareButtonWidget(this)
            val shareTextView = shareButtonWidget.findViewById<TextView>(R.id.shareTextView)
            val layoutParams = shareTextView.layoutParams
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            shareTextView.layoutParams = layoutParams
            shareButtonWidget.tag = it.id
            shareButtonWidget.setText(it.display_name)
            shareButtonWidget.setButtonStartImage(null)
            shareButtonWidget.setTextSizeInSP(14)
            shareButtonWidget.setTextGravity(Gravity.CENTER)
            shareButtonWidget.setTextColor(ContextCompat.getColor(this, R.color.app_red))
            shareButtonWidget.setButtonRadiusInDP(4f)
            shareButtonWidget.setBorderColor(ContextCompat.getColor(this, R.color.app_red))
            shareButtonWidget.setBorderThicknessInDP(1f)
            shareButtonWidget.elevation = 0.0f
            shareButtonWidget.setButtonBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.followed_topics_background
                )
            )
            val params: FlowLayout.LayoutParams = FlowLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(10, 20, 10, 20)
            shareButtonWidget.layoutParams = params
            flowLayout.addView(shareButtonWidget)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.blogAddTextView -> {
                val intent = Intent(this, EditorAddFollowedTopicsActivity::class.java)
                intent.putExtra("comingFor", "add")
                intent.putExtra("type", "blog")
                startActivity(intent)
            }
            R.id.vlogAddTextView -> {
                val intent = Intent(this, EditorAddFollowedTopicsActivity::class.java)
                intent.putExtra("comingFor", "add")
                intent.putExtra("type", "vlog")
                startActivity(intent)
            }
            R.id.storyAddTextView -> {
                val intent = Intent(this, EditorAddFollowedTopicsActivity::class.java)
                intent.putExtra("comingFor", "add")
                intent.putExtra("type", "story")
                startActivity(intent)
            }
            R.id.blogEditTextView -> {
                val intent = Intent(this, EditorAddFollowedTopicsActivity::class.java)
                intent.putExtra("comingFor", "edit")
                intent.putExtra("type", "0")
                intent.putParcelableArrayListExtra("followedCategories", followArticleCategories)

                startActivity(intent)
            }
            R.id.vlogEditTextView -> {
                val intent = Intent(this, EditorAddFollowedTopicsActivity::class.java)
                intent.putExtra("comingFor", "edit")
                intent.putExtra("type", "2")
                intent.putParcelableArrayListExtra("followedCategories", followVideoCategories)

                startActivity(intent)
            }
            R.id.storyEditTextView -> {
                val intent = Intent(this, EditorAddFollowedTopicsActivity::class.java)
                intent.putExtra("comingFor", "edit")
                intent.putExtra("type", "1")
                intent.putParcelableArrayListExtra("followedCategories", followStoryCategories)
                startActivity(intent)
            }

        }

    }


}