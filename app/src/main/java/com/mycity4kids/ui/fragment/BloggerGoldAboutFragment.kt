package com.mycity4kids.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.base.BaseFragment
import com.mycity4kids.utils.CustomTabsHelper

class BloggerGoldAboutFragment : BaseFragment() {

    private lateinit var webviewHack: TextView
    private lateinit var webviewTerms: TextView
    private lateinit var webviewFAQ: TextView
    private lateinit var webviewKnowMore: TextView
    private lateinit var bloggerGoldAboutText: TextView
    private lateinit var fabwhatsapp: FloatingActionButton
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.blogger_gold_about_fragment, container, false)
        webviewHack = view.findViewById(R.id.webview_hack)
        webviewTerms = view.findViewById(R.id.webview_terms)
        webviewFAQ = view.findViewById(R.id.webview_faq)
        fabwhatsapp = view.findViewById(R.id.fabwhatsapp)
        webviewKnowMore = view.findViewById(R.id.webview_know_more)
        bloggerGoldAboutText = view.findViewById(R.id.blogger_gold_about_text)
        webviewHack.setOnClickListener {
            launchChromeTabs("https://www.momspresso.com/birthdaybonanza/hack_to_get_more_page_views")
        }
        webviewTerms.setOnClickListener {
            launchChromeTabs("https://www.momspresso.com/birthdaybonanza/terms_and_conditions")
        }
        webviewFAQ.setOnClickListener {
            launchChromeTabs("https://www.momspresso.com/birthdaybonanza/faqs")
        }
        webviewKnowMore.setOnClickListener {
            launchChromeTabs("https://www.momspresso.com/birthdaybonanza/know_more")
        }
        fabwhatsapp.setOnClickListener {
            val shareIntent =
                Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.setPackage("com.whatsapp")
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.momspresso.com/birthdaybonanza")
            context!!.startActivity(Intent.createChooser(shareIntent, "Momspresso"))
        }
        return view
    }

    private fun launchChromeTabs(deepLinkUrl: String) {
        try {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            val packageName = CustomTabsHelper.getPackageNameToUse(activity)
            if (packageName != null) {
                customTabsIntent.intent.setPackage(packageName)
            }
            customTabsIntent.launchUrl(activity, Uri.parse(deepLinkUrl))
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
    }
}
