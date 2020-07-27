package com.mycity4kids.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mycity4kids.R
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.editor.NewEditor
import com.mycity4kids.ui.adapter.ArticleChallengePagerAdapter
import com.mycity4kids.widget.MomspressoButtonWidget
import com.squareup.picasso.Picasso

class ArticleChallengeDetailActivity : BaseActivity() {

    private lateinit var back: ImageView
    private lateinit var thumbNail: ImageView
    private lateinit var tabs: TabLayout
    private lateinit var viewpager: ViewPager
    private var articleChallengeId: String? = null
    private lateinit var startWritingTextView: MomspressoButtonWidget

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.article_challenge_detail_activity)
        back = findViewById(R.id.back)
        thumbNail = findViewById(R.id.thumbNail)
        viewpager = findViewById(R.id.viewpager)
        tabs = findViewById(R.id.tabs)
        startWritingTextView = findViewById(R.id.startWritingTextView)
        articleChallengeId = intent.getStringExtra("articleChallengeId")
        val rules = intent.getStringExtra("rules")
        val challengeName = intent.getStringExtra("challengeName")
        val articleImageViewUrl = intent.getStringExtra("articleImageVIEW")
        articleImageViewUrl?.let {
            Picasso.get().load(it.trim()).error(R.drawable.default_article).into(thumbNail)
        } ?: run {
            thumbNail.setImageResource(R.drawable.default_article)
        }
        tabs.apply {
            addTab(tabs.newTab().setText(R.string.about_txt))
            addTab(tabs.newTab().setText(R.string.groups_sections_blogs))
        }
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        viewpager.adapter =
            ArticleChallengePagerAdapter(
                articleChallengeId,
                challengeName,
                rules,
                supportFragmentManager
            )
        viewpager.currentItem = 1
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
        back.setOnClickListener {
            onBackPressed()
        }
    }
}
