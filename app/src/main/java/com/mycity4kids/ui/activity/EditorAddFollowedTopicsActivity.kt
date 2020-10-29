package com.mycity4kids.ui.activity

import android.content.Intent
import android.os.Bundle
import com.mycity4kids.R
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.models.Topics
import com.mycity4kids.ui.fragment.EditFollowedContentTopicsFragment
import com.mycity4kids.ui.fragment.SelectOrAddBlogTopicsFragment
import com.mycity4kids.ui.fragment.SelectOrAddStoryTopicsFragment
import com.mycity4kids.ui.fragment.SelectOrAddVlogTopicsFragment

class EditorAddFollowedTopicsActivity : BaseActivity() {
    private var followedTopics: ArrayList<Topics>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_followed_topics)


        val comingFor = intent.getStringExtra("comingFor")
        val contentType = intent.getStringExtra("type")
        followedTopics = intent.getParcelableArrayListExtra("followedCategories")


        if ("add" == comingFor) {
            val bundle = Bundle()
            bundle.putString("comingFor", comingFor)
            when (contentType) {
                "blog" -> {
                    val selectBlogTopicsFragment = SelectOrAddBlogTopicsFragment()
                    addFragment(selectBlogTopicsFragment, bundle)
                }
                "story" -> {
                    val selectStoryTopicsFragment = SelectOrAddStoryTopicsFragment()
                    addFragment(selectStoryTopicsFragment, bundle)
                }
                "vlog" -> {
                    val selectVlogTopicsFragment = SelectOrAddVlogTopicsFragment()
                    addFragment(selectVlogTopicsFragment, bundle)
                }
            }
        } else if ("edit" == comingFor) {
            followedTopics?.let {
                val editFollowedContentTopicsFragment =
                    EditFollowedContentTopicsFragment.newInstance()
                val bundle = Bundle()
                bundle.putString("type", contentType)
                addFragment(editFollowedContentTopicsFragment, bundle)
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
            finish()
        }
    }

    fun checkFollowedTopics(allTopicsList: ArrayList<Topics>) {
        allTopicsList.forEach { allTopics ->
            followedTopics?.forEach { followedTopics ->
                if (followedTopics.id == allTopics.id) {
                    allTopics.setIsSelected(true)
                }
            }
        }
    }

}