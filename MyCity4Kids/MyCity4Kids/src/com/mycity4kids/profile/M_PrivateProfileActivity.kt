package com.mycity4kids.profile

import android.accounts.NetworkErrorException
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.kelltontech.utils.ConnectivityUtils
import com.kelltontech.utils.StringUtils
import com.mycity4kids.R
import com.mycity4kids.animation.MyCityAnimationsUtil
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.CollectionsModels.CollectionFeaturedListModel
import com.mycity4kids.models.response.ArticleListingResponse
import com.mycity4kids.models.response.ArticleListingResult
import com.mycity4kids.models.response.LanguageRanksModel
import com.mycity4kids.models.response.UserDetailResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI
import com.mycity4kids.ui.fragment.UserBioDialogFragment
import com.mycity4kids.utils.RoundedTransformation
import com.mycity4kids.widget.BadgesProfileWidget
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback

class M_PrivateProfileActivity : BaseActivity(),
        UserContentAdapter.RecyclerViewClickListener, View.OnClickListener, UsersFeaturedContentAdapter.RecyclerViewClickListener {
    private lateinit var profileShimmerLayout: ShimmerFrameLayout
    private lateinit var headerContainer: RelativeLayout
    private lateinit var profileImageView: ImageView
    private lateinit var followerContainer: LinearLayout
    private lateinit var followingContainer: LinearLayout
    private lateinit var rankContainer: LinearLayout
    private lateinit var postsCountContainer: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var followingCountTextView: TextView
    private lateinit var followerCountTextView: TextView
    private lateinit var rankCountTextView: TextView
    private lateinit var postsCountTextView: TextView
    private lateinit var rankLanguageTextView: TextView
    private lateinit var authorNameTextView: TextView
    private lateinit var authorBioTextView: TextView
    private lateinit var cityTextView: TextView
    private lateinit var contentLangTextView: TextView
    private lateinit var creatorTab: ImageView
    private lateinit var featuredTab: ImageView
    private lateinit var bookmarksTab: ImageView

    private lateinit var badgesContainer: BadgesProfileWidget

    private val multipleRankList = java.util.ArrayList<LanguageRanksModel>()
    private var isRewardsAdded: String? = null
    private var userContentList: ArrayList<ArticleListingResult>? = null
    private var userFeaturedList: ArrayList<CollectionFeaturedListModel.FeaturedListResult>? = null

    private val userContentAdapter: UserContentAdapter by lazy { UserContentAdapter(this) }
    private val usersFeaturedContentAdapter: UsersFeaturedContentAdapter by lazy { UsersFeaturedContentAdapter(this) }
    private lateinit var authorId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m_private_profile_activity)

        recyclerView = findViewById(R.id.recyclerView)
        profileShimmerLayout = findViewById(R.id.profileShimmerLayout)
        profileImageView = findViewById(R.id.profileImageView)
        followerContainer = findViewById(R.id.followerContainer)
        followingContainer = findViewById(R.id.followingContainer)
        rankContainer = findViewById(R.id.rankContainer)
        postsCountContainer = findViewById(R.id.postsCountContainer)
        headerContainer = findViewById(R.id.headerContainer)
        followingCountTextView = findViewById(R.id.followingCountTextView)
        followerCountTextView = findViewById(R.id.followerCountTextView)
        rankCountTextView = findViewById(R.id.rankCountTextView)
        postsCountTextView = findViewById(R.id.postsCountTextView)
        rankLanguageTextView = findViewById(R.id.rankLanguageTextView)
        authorNameTextView = findViewById(R.id.authorNameTextView)
        cityTextView = findViewById(R.id.cityTextView)
        authorBioTextView = findViewById(R.id.authorBioTextView)
        badgesContainer = findViewById(R.id.badgeContainer)
        contentLangTextView = findViewById(R.id.contentLangTextView)
        creatorTab = findViewById(R.id.creatorTab)
        featuredTab = findViewById(R.id.featuredTab)
        bookmarksTab = findViewById(R.id.bookmarksTab)

        val llm = LinearLayoutManager(this)
        llm.orientation = RecyclerView.VERTICAL

        recyclerView.layoutManager = llm
        recyclerView.adapter = userContentAdapter

        userContentList = ArrayList()
        userFeaturedList = ArrayList()

        authorId = SharedPrefUtils.getUserDetailModel(this).dynamoId
        profileShimmerLayout.startShimmerAnimation()
        val handler = Handler()
        handler.postDelayed(Runnable {
            getUserDetail(authorId)
        }, 2000)
        badgesContainer.getBadges(authorId)

        creatorTab.setOnClickListener(this)
        featuredTab.setOnClickListener(this)
        bookmarksTab.setOnClickListener(this)

        getUsersRecommendations(authorId)

    }

    private fun getUserDetail(authorId: String) {
        val retrofit = BaseApplication.getInstance().retrofit
        val bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI::class.java)
        val call = bloggerDashboardAPI.getBloggerData(authorId)
        call.enqueue(object : Callback<UserDetailResponse> {
            override fun onResponse(call: Call<UserDetailResponse>, response: retrofit2.Response<UserDetailResponse>) {
                if (null == response.body()) {
                    return
                }
                try {
                    profileShimmerLayout.visibility = View.GONE
                    headerContainer.visibility = View.VISIBLE
                    val responseData = response.body() as UserDetailResponse
                    if (responseData.code == 200 && Constants.SUCCESS == responseData.status) {
                        if (responseData.data != null && responseData.data[0] != null && responseData.data[0].result != null) {
                            isRewardsAdded = responseData.data[0].result.rewardsAdded
                        }
                    }

                    processCityInfo(responseData)
                    processContentLanguages(responseData)
                    processAuthorRank(responseData)
                    processAuthorsFollowingAndFollowership(responseData)

                    authorNameTextView.text = responseData.data[0].result.firstName + " " + responseData.data[0].result.lastName

                    if (!StringUtils.isNullOrEmpty(responseData.data[0].result.profilePicUrl.clientApp)) {
                        Picasso.with(this@M_PrivateProfileActivity).load(responseData.data[0].result.profilePicUrl.clientApp)
                                .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(profileImageView)
                    }

                    if (responseData.data[0].result.userBio == null || responseData.data[0].result.userBio.isEmpty()) {
                        authorBioTextView.visibility = View.GONE
                    } else {
                        authorBioTextView.text = responseData.data[0].result.userBio
                        authorBioTextView.visibility = View.VISIBLE
                        makeTextViewResizable(authorBioTextView, 2, "See More", true, responseData.data[0].result.userBio)
                    }

                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(call: Call<UserDetailResponse>, e: Throwable) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        })
    }

    private fun processContentLanguages(responseData: UserDetailResponse) {
        if (responseData.data[0].result.createrLangs.isEmpty()) {
            contentLangTextView.visibility = View.GONE
        } else {
            contentLangTextView.visibility = View.VISIBLE
            var contentLang: String = responseData.data[0].result.createrLangs[0]
            for (i in 1 until responseData.data[0].result.createrLangs.size) {
                contentLang = contentLang + " \u2022 " + responseData.data[0].result.createrLangs[i]
            }
            contentLangTextView.text = contentLang
        }
    }

    private fun processCityInfo(responseData: UserDetailResponse) {
        if (StringUtils.isNullOrEmpty(responseData.data[0].result.cityName)) {
            cityTextView.visibility = View.GONE
        } else {
            cityTextView.visibility = View.VISIBLE
            cityTextView.text = responseData.data[0].result.cityName
        }
    }

    private fun processAuthorsFollowingAndFollowership(responseData: UserDetailResponse) {
        val followerCount = Integer.parseInt(responseData.data[0].result.followersCount)
        if (followerCount > 999) {
            val singleFollowerCount = followerCount.toFloat() / 1000
            followerCountTextView.text = "" + singleFollowerCount + "k"
        } else {
            followerCountTextView.text = "" + followerCount
        }

        val followingCount = Integer.parseInt(responseData.data[0].result.followingCount)
        if (followingCount > 999) {
            val singleFollowingCount = followingCount.toFloat() / 1000
            followingCountTextView.text = "" + singleFollowingCount + "k"
        } else {
            followingCountTextView.text = "" + followingCount
        }
    }

    private fun processAuthorRank(responseData: UserDetailResponse) {
        if (responseData.data[0].result.ranks == null || responseData.data[0].result.ranks.size == 0) {
            rankCountTextView.text = "--"
            rankLanguageTextView.text = getString(R.string.myprofile_rank_label)
        } else if (responseData.data[0].result.ranks.size < 2) {
            rankCountTextView.text = "" + responseData.data[0].result.ranks[0].rank
            if (AppConstants.LANG_KEY_ENGLISH == responseData.data[0].result.ranks[0].langKey) {
                rankLanguageTextView.text = getString(R.string.blogger_profile_rank_in) + " ENGLISH"
            } else {
                rankLanguageTextView.text = (getString(R.string.blogger_profile_rank_in)
                        + " " + responseData.data[0].result.ranks[0].langValue.toUpperCase())
            }
        } else {
            for (i in 0 until responseData.data[0].result.ranks.size) {
                if (AppConstants.LANG_KEY_ENGLISH == responseData.data[0].result.ranks[i].langKey) {
                    multipleRankList.add(responseData.data[0].result.ranks[i])
                    break
                }
            }
            responseData.data[0].result.ranks.sort()
            for (i in 0 until responseData.data[0].result.ranks.size) {
                if (AppConstants.LANG_KEY_ENGLISH != responseData.data[0].result.ranks[i].langKey) {
                    multipleRankList.add(responseData.data[0].result.ranks[i])
                }
            }
            MyCityAnimationsUtil.animate(this@M_PrivateProfileActivity, rankContainer, multipleRankList, 0, true)
        }
    }


    private fun getUsersRecommendations(authorId: String) {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            showToast(getString(R.string.connectivity_unavailable))
            return
        }

        val retro = BaseApplication.getInstance().retrofit
        val bloggerDashboardAPI = retro.create(BloggerDashboardAPI::class.java)
        val call = bloggerDashboardAPI.getUsersRecommendation(this.authorId)
        call.enqueue(usersRecommendationsResponseListener)
    }


    private val usersRecommendationsResponseListener = object : Callback<ArticleListingResponse> {
        override fun onResponse(call: Call<ArticleListingResponse>, response: retrofit2.Response<ArticleListingResponse>) {
            if (null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                Crashlytics.logException(nee)
                return
            }
            try {
                val responseData = response.body()
                if (responseData!!.code == 200 && Constants.SUCCESS == responseData.status) {
                    processRecommendationsResponse(responseData)
                } else {
                }
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }

        }

        override fun onFailure(call: Call<ArticleListingResponse>, t: Throwable) {
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    private fun processRecommendationsResponse(responseData: ArticleListingResponse) {
        val dataList = responseData.data[0].result

        if (dataList.size == 0) {
        } else {
            userContentList?.addAll(dataList)
            userContentAdapter.setListData(userContentList)
            userContentAdapter.notifyDataSetChanged()
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
                obs.removeOnGlobalLayoutListener(this)
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

    override fun onClick(view: View?) {
        when {
            view?.id == R.id.creatorTab -> {
                creatorTab.isSelected = true
                featuredTab.isSelected = false
                bookmarksTab.isSelected = false
                recyclerView.adapter = userContentAdapter
            }
            view?.id == R.id.featuredTab -> {
                creatorTab.isSelected = false
                featuredTab.isSelected = true
                bookmarksTab.isSelected = false
                recyclerView.adapter = usersFeaturedContentAdapter
                getFeaturedContent()
            }
            view?.id == R.id.bookmarksTab -> {
                creatorTab.isSelected = false
                featuredTab.isSelected = false
                bookmarksTab.isSelected = true
            }
        }
    }

    private fun getFeaturedContent() {
        val retrofit = BaseApplication.getInstance().retrofitTest
        val featureListAPI = retrofit.create(CollectionsAPI::class.java)
        val call = featureListAPI.getFeaturedOnCollections("d7d981e2978b49b7b1748306967fc8da", 0, 10)
        call.enqueue(object : Callback<CollectionFeaturedListModel> {
            override fun onResponse(call: Call<CollectionFeaturedListModel>, response: retrofit2.Response<CollectionFeaturedListModel>) {
                if (null == response.body()) {
                    val nee = NetworkErrorException(response.raw().toString())
                    Crashlytics.logException(nee)
                    return
                }
                try {
                    val responseData = response.body() as CollectionFeaturedListModel
                    if (responseData.code == 200 && Constants.SUCCESS == responseData.status) {
                        userFeaturedList?.addAll(responseData.data[0].result)
                        usersFeaturedContentAdapter.setListData(userFeaturedList)
                        usersFeaturedContentAdapter.notifyDataSetChanged()
                    } else {
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }

            }

            override fun onFailure(call: Call<CollectionFeaturedListModel>, t: Throwable) {
                Crashlytics.logException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        })
    }


    override fun onClick(view: View, position: Int) {

    }

    override fun onFeaturedItemClick(view: View, position: Int) {

    }
}


