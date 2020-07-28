package com.mycity4kids.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mycity4kids.ui.fragment.ArticleChallengeDescriptionFragment
import com.mycity4kids.ui.fragment.ArticleChallengeDetailListingFragment

class ArticleChallengePagerAdapter(
    private var challengeId: String?,
    private var rules: String?,
    private var challengeName: String?,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private var articleChallengeDetailListingFragment: ArticleChallengeDetailListingFragment? = null
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
        } else {
            if (articleChallengeDetailListingFragment == null) {
                articleChallengeDetailListingFragment = ArticleChallengeDetailListingFragment()
            }
            val bundle = Bundle()
            bundle.putString("articleChallengeId", challengeId)
            articleChallengeDetailListingFragment!!.arguments = bundle
            return articleChallengeDetailListingFragment as ArticleChallengeDetailListingFragment
        }
    }

    override fun getCount(): Int {
        return 2
    }
}
