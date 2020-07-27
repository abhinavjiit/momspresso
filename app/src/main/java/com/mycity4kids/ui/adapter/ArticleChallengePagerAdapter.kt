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
    private var challengeName: String?,
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getItem(position: Int): Fragment {
        if (position == 1) {
            val articleChallengeDetailListingFragment = ArticleChallengeDetailListingFragment()
            challengeId?.let { challengeId ->
                val bundle = Bundle()
                bundle.putString("articleChallengeId", challengeId)
                articleChallengeDetailListingFragment.arguments = bundle
            }
            return articleChallengeDetailListingFragment
        } else {
            val articleChallengeDescriptionFragment = ArticleChallengeDescriptionFragment()
            rules?.let {
                val bundle = Bundle()
                bundle.putString("rules", rules)
                articleChallengeDescriptionFragment.arguments = bundle
            }
            return articleChallengeDescriptionFragment
        }
    }

    override fun getCount(): Int {
        return 2
    }
}
