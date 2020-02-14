package com.mycity4kids.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.R

class ShortStoryBgBaseFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.story_base_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shortStoryThumbnailListFragment = ShortStoryThumbnailListFragment()

        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, shortStoryThumbnailListFragment)
        transaction?.commit()
    }
}