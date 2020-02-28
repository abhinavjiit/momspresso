package com.mycity4kids.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mycity4kids.ui.fragment.AddMultipleUserCreatedArticlesInCollectionFragment
import com.mycity4kids.ui.fragment.AddMultipleUserReadArticleInCollectionFragment

class MultipleCollectionItemPagerAdapter(val fragmentManager: FragmentManager, val isCreatedArticle: Boolean, val collectionId: String) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_SET_USER_VISIBLE_HINT) {
    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putString("collectionId", collectionId)
        if (isCreatedArticle) {
            return when (position) {
                0 -> {
                    val addMultipleUserReadArticleInCollectionFragment = AddMultipleUserReadArticleInCollectionFragment()
                    addMultipleUserReadArticleInCollectionFragment.arguments = bundle
                    addMultipleUserReadArticleInCollectionFragment
                }
                else -> {
                    val addMultipleUserCreatedArticlesInCollectionFragment = AddMultipleUserCreatedArticlesInCollectionFragment()
                    addMultipleUserCreatedArticlesInCollectionFragment.arguments = bundle
                    addMultipleUserCreatedArticlesInCollectionFragment
                }
            }
        } else {
            val addMultipleUserReadArticleInCollectionFragment = AddMultipleUserReadArticleInCollectionFragment()
            addMultipleUserReadArticleInCollectionFragment.arguments = bundle
            return addMultipleUserReadArticleInCollectionFragment
        }
    }

    override fun getCount(): Int {
        return if (isCreatedArticle)
            2
        else
            1
    }
}