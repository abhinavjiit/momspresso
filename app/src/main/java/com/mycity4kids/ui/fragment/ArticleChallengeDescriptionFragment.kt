package com.mycity4kids.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.mycity4kids.R
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.utils.ToastUtils

class ArticleChallengeDescriptionFragment : BaseFragment() {
    private lateinit var videoChallengeRulesWebView: WebView
    private var challengeRules: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.challenge_discription_layout, container, false)
        videoChallengeRulesWebView = view.findViewById(R.id.videoChallengeRulesWebView)
        challengeRules = arguments?.getString("rules")
        challengeRules?.let {
            videoChallengeRulesWebView.loadDataWithBaseURL("", it, "text/html", "UTF-8", "")
        } ?: run {
            ToastUtils.showToast(activity, "something went wrong")
        }
        return view
    }
}
