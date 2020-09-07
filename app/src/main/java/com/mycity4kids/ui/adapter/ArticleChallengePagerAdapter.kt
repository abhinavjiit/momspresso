package com.mycity4kids.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mycity4kids.ui.fragment.ArticleChallengeDescriptionFragment
import com.mycity4kids.ui.fragment.ArticleChallengeDetailListingFragment

class ArticleChallengePagerAdapter(
    private var challengeId: String?,
    private var rules: String?,
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private var articleChallengeDescriptionFragment: ArticleChallengeDescriptionFragment? = null

    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            if (articleChallengeDescriptionFragment == null) {
                articleChallengeDescriptionFragment = ArticleChallengeDescriptionFragment()
            }
            val bundle = Bundle()
            bundle.putString("rules", rules)
            articleChallengeDescriptionFragment!!.arguments = bundle
            return articleChallengeDescriptionFragment as ArticleChallengeDescriptionFragment
        } else if (position == 1) {
            val articleChallengeDetailListingFragment = ArticleChallengeDetailListingFragment()
            val bundle = Bundle()
            bundle.putString("articleChallengeId", challengeId)
            bundle.putString("position", position.toString())
            articleChallengeDetailListingFragment.arguments = bundle
            return articleChallengeDetailListingFragment
        } else {
            val articleChallengeDetailListingFragment = ArticleChallengeDetailListingFragment()
            val bundle = Bundle()
            bundle.putString("articleChallengeId", challengeId)
            bundle.putString("position", position.toString())
            articleChallengeDetailListingFragment.arguments = bundle
            return articleChallengeDetailListingFragment
        }
    }

    override fun getCount(): Int {
        return 3
    }
}
