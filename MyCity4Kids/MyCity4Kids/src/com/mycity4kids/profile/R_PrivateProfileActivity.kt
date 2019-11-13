package com.mycity4kids.profile

import android.accounts.NetworkErrorException
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.kelltontech.utils.ConnectivityUtils
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.response.ArticleListingResponse
import com.mycity4kids.models.response.ArticleListingResult
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI
import com.mycity4kids.ui.fragment.UserBioDialogFragment
import kotlinx.android.synthetic.main.r_private_profile_activity.*
import retrofit2.Call
import retrofit2.Callback


class R_PrivateProfileActivity : BaseActivity(), StickyRecyclerViewAdapter.RecyclerViewClickListener {
    override fun onClick(view: View, position: Int) {

    }

    private var recommendationsList: ArrayList<ArticleListingResult>? = null
    lateinit var adapter: StickyRecyclerViewAdapter
    private lateinit var authorId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.r_private_profile_activity)

        recommendationsList = ArrayList()
        adapter = StickyRecyclerViewAdapter(this)
        var linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        setData(adapter)

//        recyclerView.addItemDecoration(object : MyItemDecoration(this) {})
        recyclerView.adapter = adapter

        recyclerView.layoutManager = linearLayoutManager
        authorId = SharedPrefUtils.getUserDetailModel(this).dynamoId
        getUsersRecommendations()
    }

    private fun getFeaturedPost() {

    }

    open class MyItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
        private var offset: Int

        init {
            this.offset = 10
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.set(offset, offset, offset, offset)
        }
    }


    private fun setData(adapter: StickyRecyclerViewAdapter) {
        val headerData1 = HeaderDataImpl(HeaderDataImpl.HEADER_TYPE_1, R.layout.test_card)
        val headerData2 = HeaderDataImpl(HeaderDataImpl.HEADER_TYPE_2, R.layout.header2_item_recycler)

        recommendationsList?.toMutableList()?.let { adapter.setHeaderAndData(it, headerData1) }
        recommendationsList?.toMutableList()?.let { adapter.setHeaderAndData(it, headerData2) }

    }

    private fun getUsersRecommendations() {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            showToast(getString(R.string.connectivity_unavailable))
            return
        }

        val retro = BaseApplication.getInstance().retrofit
        val bloggerDashboardAPI = retro.create(BloggerDashboardAPI::class.java)
        val call = bloggerDashboardAPI.getUsersRecommendation(authorId)
        call.enqueue(usersRecommendationsResponseListener)
    }

    private val usersRecommendationsResponseListener = object : Callback<ArticleListingResponse> {
        override fun onResponse(call: Call<ArticleListingResponse>, response: retrofit2.Response<ArticleListingResponse>) {
//            progressBar.setVisibility(View.GONE)
            if (response == null || null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                Crashlytics.logException(nee)
                //                showToast("Something went wrong from server");
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                    processRecommendationsResponse(responseData)
                } else {
                    //                    showToast(responseData.getReason());
                }
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
                //                showToast(getString(R.string.went_wrong));
            }

        }

        override fun onFailure(call: Call<ArticleListingResponse>, t: Throwable) {
//            progressBar.setVisibility(View.GONE)
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    private fun processRecommendationsResponse(responseData: ArticleListingResponse) {
        val dataList = responseData.data[0].result

        if (dataList.size == 0) {
//            noBlogsTextView.setVisibility(View.VISIBLE)
        } else {
            recommendationsList?.addAll(dataList)
            recommendationsList?.toMutableList()?.let { adapter.setHeaderAndData(it, null) }
            adapter.notifyDataSetChanged()
        }
    }

    override fun updateUi(response: Response?) {
    }

    fun makeTextViewResizable(tv: TextView, maxLine: Int, expandText: String, viewMore: Boolean, userBio: String) {
        if (tv.tag == null) {
            tv.tag = tv.text
        }
        val vto = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val obs = tv.viewTreeObserver
                obs.removeGlobalOnLayoutListener(this)
                if (maxLine == 0) {
                    val lineEndIndex = tv.layout.getLineEnd(0)
                    val text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1).toString() + " " + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.text.toString()), tv, maxLine, expandText,
                                    viewMore, userBio), TextView.BufferType.SPANNABLE)
                } else if (maxLine > 0 && tv.lineCount > maxLine) {
                    val lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                    if (lineEndIndex - expandText.length + 1 > 10) {
                        val text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1).toString() + " " + expandText
                        tv.text = text
                        tv.movementMethod = LinkMovementMethod.getInstance()
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.text.toString()), tv, maxLine, expandText,
                                        viewMore, userBio), TextView.BufferType.SPANNABLE)
                    } else {
                        val text = tv.text.subSequence(0, lineEndIndex).toString() + " " + expandText
                        tv.text = text
                        tv.movementMethod = LinkMovementMethod.getInstance()
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.text.toString()), tv, maxLine, expandText,
                                        viewMore, userBio), TextView.BufferType.SPANNABLE)
                    }
                } else {
                }
            }
        })
    }

    private fun addClickablePartTextViewResizable(strSpanned: Spanned, tv: TextView,
                                                  maxLine: Int, spanableText: String, viewMore: Boolean, userBio: String): SpannableStringBuilder {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)

        if (str.contains(spanableText)) {
            ssb.setSpan(object : MySpannable(false) {
                override fun onClick(widget: View) {
                    val userBioDialogFragment = UserBioDialogFragment()
                    val fm = supportFragmentManager
                    val _args = Bundle()
                    _args.putString("userBio", userBio)
                    userBioDialogFragment.arguments = _args
                    userBioDialogFragment.isCancelable = true
                    userBioDialogFragment.show(fm, "Choose video option")
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)

        }
        return ssb
    }

    open inner class MySpannable(isUnderline: Boolean) : ClickableSpan() {
        private var isUnderline = true

        init {
            this.isUnderline = isUnderline
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = isUnderline
            ds.color = Color.parseColor("#1b76d3")
        }

        override fun onClick(widget: View) {
        }
    }
}