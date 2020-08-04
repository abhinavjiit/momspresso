package com.mycity4kids.ui.activity

import android.accounts.NetworkErrorException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.editor.NewEditor
import com.mycity4kids.models.Topics
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI
import com.mycity4kids.ui.adapter.ArticleChallengePagerAdapter
import com.mycity4kids.widget.MomspressoButtonWidget
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleChallengeDetailActivity : BaseActivity() {

    private var challengeSlug: String? = ""
    private var challengeName: String? = ""
    private lateinit var toolbarTitleTextView: TextView
    private lateinit var challengeNameTextView: TextView
    private lateinit var back: ImageView
    private lateinit var thumbNail: ImageView
    private lateinit var tabs: TabLayout
    private lateinit var viewpager: ViewPager
    private var articleChallengeId: String? = null
    private lateinit var startWritingTextView: MomspressoButtonWidget
    private lateinit var articleChallengeAdapter: ArticleChallengePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.article_challenge_detail_activity)
        back = findViewById(R.id.back)
        thumbNail = findViewById(R.id.thumbNail)
        viewpager = findViewById(R.id.viewpager)
        toolbarTitleTextView = findViewById(R.id.toolbarTitleTextView)
        challengeNameTextView = findViewById(R.id.toolbarTitleTextView)
        tabs = findViewById(R.id.tabs)
        startWritingTextView = findViewById(R.id.startWritingTextView)
        articleChallengeId = intent.getStringExtra("articleChallengeId")
        challengeSlug = intent.getStringExtra("challengeSlug")
        challengeName = intent.getStringExtra("challengeName")

        toolbarTitleTextView.text = challengeName
        challengeNameTextView.text = challengeName
        tabs.apply {
            addTab(tabs.newTab().setText(R.string.about_txt))
            addTab(tabs.newTab().setText(R.string.groups_sections_blogs))
        }

        if (!articleChallengeId.isNullOrBlank()) {
            getChallengeDetails
        } else if (!challengeSlug.isNullOrBlank()) {
            getChallengeDetailsFromSlug
        }

        back.setOnClickListener {
            onBackPressed()
        }
    }

    private val getChallengeDetails: Unit
        get() {
            val retrofit = BaseApplication.getInstance().retrofit
            val vlogsListingAndDetailsApi =
                retrofit.create(
                    VlogsListingAndDetailsAPI::class.java
                )
            val categoryDetailsApi =
                vlogsListingAndDetailsApi.getCategoryDetails(articleChallengeId)
            categoryDetailsApi.enqueue(blogsChallengeResponseCallBack)
        }

    private val getChallengeDetailsFromSlug: Unit
        get() {
            val retrofit = BaseApplication.getInstance().retrofit
            val vlogsListingAndDetailsApi =
                retrofit.create(
                    VlogsListingAndDetailsAPI::class.java
                )
            val categoryDetailsApi =
                vlogsListingAndDetailsApi.getCategoryDetailsFromSlug(challengeSlug)
            categoryDetailsApi.enqueue(blogsChallengeResponseCallBack)
        }

    private val blogsChallengeResponseCallBack: Callback<Topics> =
        object : Callback<Topics> {
            override fun onResponse(
                call: Call<Topics>,
                response: Response<Topics>
            ) {
                if (null == response.body()) {
                    val nee =
                        NetworkErrorException(response.raw().toString())
                    FirebaseCrashlytics.getInstance().recordException(nee)
                    return
                }
                if (response.isSuccessful) {
                    try {
                        val responseData = response.body()
                        responseData?.let { processChallengesData(it) }
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                    }
                }
            }

            override fun onFailure(
                call: Call<Topics>,
                t: Throwable
            ) {
            }
        }

    private fun processChallengesData(challengeDetail: Topics) {
        val rules = challengeDetail.extraData[0].challenge.rules
        val challengeName = challengeDetail.display_name
        toolbarTitleTextView.text = challengeName
        challengeNameTextView.text = challengeName
        val articleImageViewUrl = challengeDetail.extraData[0].challenge.imageUrl
        articleImageViewUrl?.let {
            Picasso.get().load(it.trim()).error(R.drawable.default_article).into(thumbNail)
        } ?: run {
            thumbNail.setImageResource(R.drawable.default_article)
        }
        articleChallengeId = challengeDetail.id
        articleChallengeAdapter = ArticleChallengePagerAdapter(
            articleChallengeId,
            rules,
            supportFragmentManager
        )
        viewpager.adapter = articleChallengeAdapter

        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewpager.currentItem = tab?.position!!
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewpager.currentItem = tab?.position!!
            }
        })
        startWritingTextView.setOnClickListener {
            val intent = Intent(this, NewEditor::class.java)
            intent.putExtra("articleChallengeId", articleChallengeId)
            intent.putExtra("challengeName", challengeName)
            startActivity(intent)
        }
    }
}
