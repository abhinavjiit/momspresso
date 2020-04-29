package com.mycity4kids.profile

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.gson.Gson
import com.mycity4kids.R
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.models.response.UserDetailResult
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.RoundedTransformation
import com.mycity4kids.utils.StringUtils
import com.mycity4kids.widget.BadgesProfileWidget
import com.mycity4kids.widget.ResizableTextView
import com.squareup.picasso.Picasso

class ProfileShareCardWidget : RelativeLayout {

    private lateinit var profileImageView: ImageView
    private lateinit var crownImageView: ImageView
    private lateinit var followerContainer: LinearLayout
    private lateinit var followingContainer: LinearLayout
    private lateinit var rankContainer: LinearLayout
    private lateinit var postsCountContainer: LinearLayout
    private lateinit var followingCountTextView: TextView
    private lateinit var followerCountTextView: TextView
    private lateinit var rankCountTextView: TextView
    private lateinit var postsCountTextView: TextView
    private lateinit var rankLanguageTextView: TextView
    private lateinit var authorNameTextView: TextView
    private lateinit var authorBioTextView: ResizableTextView
    private lateinit var cityTextView: TextView
    private lateinit var contentLangTextView: TextView
    private lateinit var contentLangContainer: LinearLayout
    private lateinit var badgesContainer: BadgesProfileWidget

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initializeView()
    }

    private fun initializeView() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.profile_share_card_view, this)

        profileImageView = findViewById(R.id.profileImageViewBitmap)
        crownImageView = findViewById(R.id.crownImageViewBitmap)
        followerContainer = findViewById(R.id.followerContainerBitmap)
        followingContainer = findViewById(R.id.followingContainerBitmap)
        rankContainer = findViewById(R.id.rankContainerBitmap)
        postsCountContainer = findViewById(R.id.postsCountContainerBitmap)
        followingCountTextView = findViewById(R.id.followingCountTextViewBitmap)
        followerCountTextView = findViewById(R.id.followerCountTextViewBitmap)
        rankCountTextView = findViewById(R.id.rankCountTextViewBitmap)
        postsCountTextView = findViewById(R.id.postsCountTextViewBitmap)
        rankLanguageTextView = findViewById(R.id.rankLanguageTextViewBitmap)
        authorNameTextView = findViewById(R.id.authorNameTextViewBitmap)
        cityTextView = findViewById(R.id.cityTextViewBitmap)
        authorBioTextView = findViewById(R.id.authorBioTextViewBitmap)
        badgesContainer = findViewById(R.id.badgeContainerBitmap)
        contentLangTextView = findViewById(R.id.contentLangTextViewBitmap)
        contentLangContainer = findViewById(R.id.contentLangContainerBitmap)
    }

    fun populateUserDetails(authorId: String, data: UserDetailResult) {
        badgesContainer.getBadges(authorId)
        processCityInfo(data)
        processContentLanguages(data)
        processAuthorRankAndCrown(data)
        processAuthorPostCount(data)
        processAuthorsFollowingAndFollowership(data)
        processAuthorPersonalDetails(data)
    }

    private fun processContentLanguages(responseData: UserDetailResult) {
        if (responseData.createrLangs.isEmpty()) {
            contentLangContainer.visibility = View.INVISIBLE
        } else {
            contentLangContainer.visibility = View.VISIBLE
            var contentLang: String = responseData.createrLangs[0]
            for (i in 1 until responseData.createrLangs.size) {
                contentLang = contentLang + " \u2022 " + responseData.createrLangs[i]
            }
            contentLangTextView.text = contentLang
        }
    }

    private fun processAuthorPersonalDetails(responseData: UserDetailResult) {
        authorNameTextView.text = responseData.firstName + " " + responseData.lastName
        if (!StringUtils.isNullOrEmpty(responseData.profilePicUrl.clientApp)) {
            Picasso.get().load(responseData.profilePicUrl.clientApp)
                    .placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(RoundedTransformation()).into(profileImageView)
        }
        if (responseData.userBio == null || responseData.userBio.isEmpty()) {
            authorBioTextView.visibility = View.GONE
        } else {
            authorBioTextView.text = responseData.userBio
            authorBioTextView.visibility = View.VISIBLE
            authorBioTextView.setUserBio(responseData.userBio, null)
        }
    }

    private fun processCityInfo(responseData: UserDetailResult) {
        if (StringUtils.isNullOrEmpty(responseData.cityName)) {
            cityTextView.visibility = View.GONE
        } else {
            cityTextView.visibility = View.VISIBLE
            cityTextView.text = responseData.cityName
        }
    }

    private fun processAuthorsFollowingAndFollowership(responseData: UserDetailResult) {
        val followerCount = Integer.parseInt(responseData.followersCount)
        followerCountTextView.text = AppUtils.withSuffix(followerCount.toLong())
        val followingCount = Integer.parseInt(responseData.followingCount)
        followingCountTextView.text = AppUtils.withSuffix(followingCount.toLong())
    }

    private fun processAuthorPostCount(responseData: UserDetailResult) {
        postsCountTextView.text = responseData.totalArticles
    }

    private fun processAuthorRankAndCrown(responseData: UserDetailResult) {
        var crown: Crown? = null
        try {
            val jsonObject = Gson().toJsonTree(responseData.crownData).asJsonObject
            crown = Gson().fromJson<Crown>(jsonObject, Crown::class.java)
            Picasso.get().load(crown.image_url).error(
                    R.drawable.family_xxhdpi).fit().into(crownImageView)
        } catch (e: Exception) {
            crownImageView.visibility = View.GONE
        }

        if (responseData.ranks == null || responseData.ranks.size == 0) {
            rankCountTextView.text = "--"
            rankLanguageTextView.text = context.getString(R.string.myprofile_rank_label)
        } else if (responseData.ranks.size < 2) {
            rankCountTextView.text = "" + responseData.ranks[0].rank
            if (AppConstants.LANG_KEY_ENGLISH == responseData.ranks[0].langKey) {
                rankLanguageTextView.text = context.getString(R.string.blogger_profile_rank_in, "ENGLISH")
            } else {
                rankLanguageTextView.text = context.getString(R.string.blogger_profile_rank_in,
                        responseData.ranks[0].langValue.toUpperCase())
            }
        } else {
            responseData.ranks.sort()
            rankCountTextView.text = "" + responseData.ranks[0].rank
            rankLanguageTextView.text = context.getString(R.string.blogger_profile_rank_in,
                    responseData.ranks[0].langValue.toUpperCase())
        }
    }
}
