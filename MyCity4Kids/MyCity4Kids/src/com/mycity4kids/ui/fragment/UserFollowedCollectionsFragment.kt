package com.mycity4kids.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseFragment
import com.mycity4kids.R
import com.mycity4kids.ui.adapter.CollectionsAdapter
import com.mycity4kids.ui.videochallengenewui.ExpandableHeightGridView

class UserFollowedCollectionsFragment : BaseFragment() {

    private lateinit var userCreatedFollowedCollectionAdapter: CollectionsAdapter
    private lateinit var collectionGridView: ExpandableHeightGridView
    private var list = ArrayList<String>()
    override fun updateUi(response: Response?) {

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.user_followed_collections_fragment, container, false)

        collectionGridView = view.findViewById(R.id.collectionGridView)
        list.add("abhi1")
        list.add("abhi2")
        list.add("abhi3")
        list.add("abhi4")

        context?.run {
            userCreatedFollowedCollectionAdapter = CollectionsAdapter(context!!)
            collectionGridView.adapter = userCreatedFollowedCollectionAdapter
        }
        return view


    }
}