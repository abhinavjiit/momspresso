package com.mycity4kids.ui.activity

import android.content.Intent
import android.os.Bundle
import com.mycity4kids.R
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.ui.fragment.BloggersYourFriendsFollowingFeedFragment
import com.mycity4kids.ui.fragment.FindFbFriendsFragment

class FindFbFriendsActivity : BaseActivity(), FindFbFriendsFragment.OnNextButtonClick {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_fb_friends)
        findFbFriendsFragment()
    }


    private fun findFbFriendsFragment() {
        val findFbFriendsFragment = FindFbFriendsFragment.instance()
        addFragment(findFbFriendsFragment, null)
    }


    private fun yourFriendsFollowingBlogger() {
        val friendsFollowingBloggerFragment = BloggersYourFriendsFollowingFeedFragment.instance()
        addFragment(friendsFollowingBloggerFragment, null, "")
    }

    override fun onClick() {
        yourFriendsFollowingBlogger()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
            finish()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

}