package com.mycity4kids.ui.activity

import android.Manifest
import android.accounts.NetworkErrorException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.crashlytics.android.Crashlytics
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.kelltontech.utils.StringUtils
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.BuildConfig
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.request.ShortStoryConfigRequest
import com.mycity4kids.models.request.ShortStoryDraftOrPublishRequest
import com.mycity4kids.models.response.ArticleDraftResponse
import com.mycity4kids.models.response.ImageUploadResponse
import com.mycity4kids.models.response.ShortStoryConfigData
import com.mycity4kids.models.response.UserDetailResponse
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI
import com.mycity4kids.ui.adapter.ShortStoriesThumbnailAdapter
import com.mycity4kids.ui.fragment.ShortStoryLibraryFragment
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.OnDragTouchListener
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class ShortStoriesCardActivity : BaseActivity() {
    private val REQUEST_INIT_PERMISSION = 1
    private val PERMISSIONS_INIT = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private lateinit var shortLayout: LinearLayout
    private lateinit var tabs: TabLayout
    private lateinit var collectionsViewPager: ViewPager
    private lateinit var adapter: ShortStoriesThumbnailAdapter
    private lateinit var cardBg: ImageView
    private lateinit var title: String
    private lateinit var story: String
    private lateinit var titleTv: TextView
    private lateinit var storyTv: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var back: ImageView
    private var fragment: Fragment? = null
    private var isTextFragment: Boolean = false
    private lateinit var divider: View
    private lateinit var taggedCategoryName: String
    private lateinit var publishTextView: TextView
    private var source: String? = null
    private lateinit var mLayout: View
    private lateinit var path: String
    private lateinit var imageUriTemp: Uri
    private lateinit var MEDIA_TYPE_PNG: MediaType
    private lateinit var file: File
    private var taggedChallengeName: String? = null
    private var taggedChallengeId: String? = null
    private lateinit var requestBodyFile: RequestBody
    private lateinit var imageType: RequestBody
    private var draftId: String = ""
    private var articleId: String = ""
    private lateinit var tagsList: ArrayList<Map<String, String>>
    private lateinit var shortStoryDraftOrPublishRequest: ShortStoryDraftOrPublishRequest
    private var titleTvSize: Float = 0.0f
    private var storyTvSize: Float = 0.0f
    private var fontAlignment: String = "LEFT"
    private lateinit var font_Color: String
    private var categoryImageId: Int = 0
    private lateinit var shareUrl: String
    private var shortStoryId: String? = ""
    private lateinit var rlLayout: RelativeLayout
    private var imagesCategoryId: String = ""
    private var taggedCategoryId: String = ""
    private var x: Int = 0
    private var y: Int = 0
    private var isImageLoaded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.short_story_card_activity)
        mLayout = findViewById(R.id.rootLayout)
        shortLayout = findViewById(R.id.short_layout)
        toolbar = findViewById(R.id.toolbar)
        tabs = findViewById(R.id.collectionTabLayout)
        cardBg = findViewById(R.id.card_bg)
        titleTv = findViewById(R.id.short_title)
        rlLayout = findViewById(R.id.rl_layout)
        back = findViewById(R.id.back)
        storyTv = findViewById(R.id.short_text)
        divider = findViewById(R.id.divider)
        publishTextView = findViewById(R.id.publishTextView)
        collectionsViewPager = findViewById(R.id.collectionsViewPager)

        val params: ViewGroup.LayoutParams = rlLayout.layoutParams
        params.width = resources.displayMetrics.widthPixels
        params.height = resources.displayMetrics.widthPixels
        rlLayout.layoutParams = params
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        if (!intent.getStringExtra("title").isNullOrEmpty()) {
            title = intent.getStringExtra("title")
            titleTv.text = title
        } else {
            titleTv.visibility = View.GONE
            divider.visibility = View.GONE
        }
        story = intent.getStringExtra("story")
        if (intent.getStringExtra("ssTopicsText") != null)
            taggedCategoryName = intent.getStringExtra("ssTopicsText")
        if (intent.getStringExtra("challengeName") != null)
            taggedChallengeName = intent.getStringExtra("challengeName")
        if (intent.getStringExtra("challengeId") != null)
            taggedChallengeId = intent.getStringExtra("challengeId")
        if (intent.getStringExtra("draftId") != null)
            draftId = intent.getStringExtra("draftId")
        if (intent.getStringExtra("articleId") != null) {
            articleId = intent.getStringExtra("articleId")
            shortStoryId = articleId
        }
        if (intent.getStringExtra("source") != null)
            source = intent.getStringExtra("source")
        if (intent.getStringExtra("categoryId") != null) {
            imagesCategoryId = intent.getStringExtra("categoryId")
            taggedCategoryId = intent.getStringExtra("categoryId")
        }
        if (intent.getSerializableExtra("tagsList") != null)
            tagsList = intent.getSerializableExtra("tagsList") as ArrayList<Map<String, String>>

        storyTv.text = story
        titleTvSize = (titleTv.textSize) / resources.displayMetrics.scaledDensity
        storyTvSize = (storyTv.textSize) / resources.displayMetrics.scaledDensity

        tabs.apply {
            addTab(tabs.newTab().setText(resources.getString(R.string.background)))
            addTab(tabs.newTab().setText(resources.getString(R.string.text)))
        }

        setEnabledDisabled(false)

        val root = tabs.getChildAt(0)
        if (root is LinearLayout) {
            (root as LinearLayout).showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            val drawable = GradientDrawable()
            drawable.setColor(resources.getColor(R.color.color_E2E2E2))
            drawable.setSize(5, 1)
            (root as LinearLayout).dividerPadding = 10
            (root as LinearLayout).dividerDrawable = drawable
        }

        if (!shortStoryId.isNullOrEmpty())
            getConfig()

        adapter = ShortStoriesThumbnailAdapter(supportFragmentManager)
        collectionsViewPager.adapter = adapter
        collectionsViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))

        shortLayout.setOnTouchListener(OnDragTouchListener(shortLayout, rlLayout))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                collectionsViewPager.currentItem = tab!!.position
                if (tab.position == 0) {
                    isTextFragment = false
                    if (fragment is ShortStoryLibraryFragment) {
                        setEnabledDisabled(true)
                    } else {
                        setEnabledDisabled(false)
                    }
                } else {
                    isTextFragment = true
                    setEnabledDisabled(false)
                }
            }
        })

        back.setOnClickListener {
            if (fragment is ShortStoryLibraryFragment) {
                onBackPressed()
            }
        }

        publishTextView.setOnClickListener {
            if (checkViewAndUpdate())
                publishStory()
        }

        rlLayout.getViewTreeObserver().addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val location = IntArray(2)
                shortLayout.getLocationOnScreen(location)
                x = location[0]
                y = location[1]
            }
        })
    }

    fun setCategoryId(id: String) {
        imagesCategoryId = id
    }

    fun getCategoryId(): String {
        return imagesCategoryId
    }

    private fun publishStory() {
        if ("publishedList" == source) {
            shortStoryDraftOrPublishRequest = ShortStoryDraftOrPublishRequest()
            shortStoryDraftOrPublishRequest.title = titleTv.text.toString().trim({ it <= ' ' })
            shortStoryDraftOrPublishRequest.body = storyTv.text.toString()
            shortStoryDraftOrPublishRequest.userAgent = "android"
            shortStoryDraftOrPublishRequest.type = "0"
            if (AppConstants.LOCALE_ENGLISH == SharedPrefUtils.getAppLocale(this)) {
                shortStoryDraftOrPublishRequest.lang = "0"
            } else if (AppConstants.LOCALE_HINDI == SharedPrefUtils.getAppLocale(this)) {
                shortStoryDraftOrPublishRequest.lang = "1"
            } else if (AppConstants.LOCALE_MARATHI == SharedPrefUtils.getAppLocale(this)) {
                shortStoryDraftOrPublishRequest.lang = "2"
            } else if (AppConstants.LOCALE_BENGALI == SharedPrefUtils.getAppLocale(this)) {
                shortStoryDraftOrPublishRequest.lang = "3"
            } else if (AppConstants.LOCALE_TAMIL == SharedPrefUtils.getAppLocale(this)) {
                shortStoryDraftOrPublishRequest.lang = "4"
            } else if (AppConstants.LOCALE_TELUGU == SharedPrefUtils.getAppLocale(this)) {
                shortStoryDraftOrPublishRequest.lang = "5"
            } else if (AppConstants.LOCALE_KANNADA == SharedPrefUtils.getAppLocale(this)) {
                shortStoryDraftOrPublishRequest.lang = "6"
            } else if (AppConstants.LOCALE_MALAYALAM == SharedPrefUtils.getAppLocale(this)) {
                shortStoryDraftOrPublishRequest.lang = "7"
            } else if (AppConstants.LOCALE_GUJARATI == SharedPrefUtils.getAppLocale(this)) {
                shortStoryDraftOrPublishRequest.lang = "8"
            } else if (AppConstants.LOCALE_PUNJABI == SharedPrefUtils.getAppLocale(this)) {
                shortStoryDraftOrPublishRequest.lang = "9"
            } else {
                shortStoryDraftOrPublishRequest.lang = "0"
            }
            getBlogPage()
        } else {
            saveDraftBeforePublishRequest(titleTv.text.toString().trim({ it <= ' ' }), storyTv.text.toString().trim({ it <= ' ' }), draftId)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

    private fun saveDraftBeforePublishRequest(title: String, body: String, draftId1: String) {
        showProgressDialog(resources.getString(R.string.please_wait))
        val retrofit = BaseApplication.getInstance().retrofit
        val shortStoryAPI = retrofit.create(ShortStoryAPI::class.java)
        shortStoryDraftOrPublishRequest = ShortStoryDraftOrPublishRequest()
        shortStoryDraftOrPublishRequest.title = title
        shortStoryDraftOrPublishRequest.body = body
        shortStoryDraftOrPublishRequest.userAgent = "android"
        shortStoryDraftOrPublishRequest.type = "0"
        if (AppConstants.LOCALE_ENGLISH == SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) {
            shortStoryDraftOrPublishRequest.lang = "0"
        } else if (AppConstants.LOCALE_HINDI == SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) {
            shortStoryDraftOrPublishRequest.lang = "1"
        } else if (AppConstants.LOCALE_MARATHI == SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) {
            shortStoryDraftOrPublishRequest.lang = "2"
        } else if (AppConstants.LOCALE_BENGALI == SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) {
            shortStoryDraftOrPublishRequest.lang = "3"
        } else if (AppConstants.LOCALE_TAMIL == SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) {
            shortStoryDraftOrPublishRequest.lang = "4"
        } else if (AppConstants.LOCALE_TELUGU == SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) {
            shortStoryDraftOrPublishRequest.lang = "5"
        } else if (AppConstants.LOCALE_KANNADA == SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) {
            shortStoryDraftOrPublishRequest.lang = "6"
        } else if (AppConstants.LOCALE_MALAYALAM == SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) {
            shortStoryDraftOrPublishRequest.lang = "7"
        } else if (AppConstants.LOCALE_GUJARATI == SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) {
            shortStoryDraftOrPublishRequest.lang = "8"
        } else if (AppConstants.LOCALE_PUNJABI == SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) {
            shortStoryDraftOrPublishRequest.lang = "9"
        } else {
            shortStoryDraftOrPublishRequest.lang = "0"
        }

        if (draftId1.isEmpty()) {
            val call = shortStoryAPI.saveOrPublishShortStory(shortStoryDraftOrPublishRequest)
            call.enqueue(saveDraftBeforePublishResponseListener)
        } else {
            val call = shortStoryAPI.updateOrPublishShortStory(draftId1, shortStoryDraftOrPublishRequest)
            call.enqueue(saveDraftBeforePublishResponseListener)
        }
    }

    private val saveDraftBeforePublishResponseListener = object : Callback<ArticleDraftResponse> {
        override fun onResponse(call: Call<ArticleDraftResponse>, response: retrofit2.Response<ArticleDraftResponse>) {
            if (response.body() == null) {
                return
            }
            try {
                val responseModel = response.body()
                if (responseModel!!.code == 200 && Constants.SUCCESS == responseModel.status) {
                    draftId = responseModel.data[0].result.id + ""
                    getBlogPage()
                } else {
                    removeProgressDialog()
                    if (StringUtils.isNullOrEmpty(responseModel.reason)) {
                        //                        showToast(getString(R.string.toast_response_error));
                    } else {
                        //                        showToast(responseModel.getReason());
                    }
                }
            } catch (e: Exception) {
                removeProgressDialog()
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }

        }

        override fun onFailure(call: Call<ArticleDraftResponse>, t: Throwable) {
            removeProgressDialog()
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    private fun getBlogPage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().destroyRetrofitInstance()
        val retrofit = BaseApplication.getInstance().retrofit
        val bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI::class.java)
        val call = bloggerDashboardAPI.getBloggerData(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId)
        call.enqueue(getUserDetailsResponseCallback)
    }

    private val getUserDetailsResponseCallback: Callback<UserDetailResponse> = object : Callback<UserDetailResponse> {
        override fun onResponse(call: Call<UserDetailResponse>, response: retrofit2.Response<UserDetailResponse>) {
            removeProgressDialog()
            if (response.body() == null) {
                showToast(getString(R.string.went_wrong))
                return
            }
            val responseData = response.body()
            if (responseData != null) {
                if (responseData.code == 200 && Constants.SUCCESS == responseData.status) {
                    if (StringUtils.isNullOrEmpty(responseData.data[0].result.blogTitleSlug)) {
                        launchBlogSetup(responseData)
                    } else if (!StringUtils.isNullOrEmpty(responseData.data[0].result.blogTitleSlug)) {
                        if (responseData.data[0].result.email == null || responseData.data[0].result.email.isEmpty()) {
                            launchBlogSetup(responseData)
                        } else if (!StringUtils.isNullOrEmpty(responseData.data[0].result.email)) {
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (ActivityCompat.checkSelfPermission(this@ShortStoriesCardActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this@ShortStoriesCardActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    Log.i("PERMISSIONS", "storage permissions has NOT been granted. Requesting permissions.")
                                    requestStoragePermissions()
                                } else {
                                    createAndUploadShareableImage()
                                }
                            } else {
                                createAndUploadShareableImage()
                            }
                        }
                    }
                }
            } else {
                ToastUtils.showToast(this@ShortStoriesCardActivity, "something went wrong")
            }
        }

        override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {
            removeProgressDialog()
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    private fun launchBlogSetup(responseData: UserDetailResponse) {
        val intent = Intent(this@ShortStoriesCardActivity, BlogSetupActivity::class.java)
        intent.putExtra("BlogTitle", responseData.data[0].result.blogTitle)
        intent.putExtra("email", responseData.data[0].result.email)
        intent.putExtra("comingFrom", "ShortStoryAndArticle")
        startActivity(intent)
    }

    fun requestStoragePermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.checkSelfPermission(this@ShortStoriesCardActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok) { requestUngrantedPermissions() }
                    .show()
        }
    }

    private fun requestUngrantedPermissions() {
        val permissionList = ArrayList<String>()
        for (i in PERMISSIONS_INIT.indices) {
            if (ActivityCompat.checkSelfPermission(this, PERMISSIONS_INIT[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(PERMISSIONS_INIT[i])
            }
        }
        val requiredPermission = permissionList.toTypedArray()
        ActivityCompat.requestPermissions(this, requiredPermission, REQUEST_INIT_PERMISSION)
    }

    private fun createAndUploadShareableImage() {
        var finalBitmap: Bitmap? = null
        try {
            finalBitmap = AppUtils.getBitmapFromView(rlLayout, "shortStory")
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }

        val retro = BaseApplication.getInstance().retrofit
        val imageUploadAPI = retro.create(ImageUploadAPI::class.java)
        path = MediaStore.Images.Media.insertImage(contentResolver, finalBitmap, "Title" + System.currentTimeMillis(), null)
        imageUriTemp = Uri.parse(path)
        file = FileUtils.getFile(this, imageUriTemp)

        MEDIA_TYPE_PNG = "image/png".toMediaTypeOrNull()!!
        requestBodyFile = file.asRequestBody(MEDIA_TYPE_PNG)
        imageType = "4".toRequestBody("text/plain".toMediaTypeOrNull())
        showProgressDialog(resources.getString(R.string.please_wait))
        val call = imageUploadAPI.uploadImage(imageType, requestBodyFile)
        call.enqueue(ssImageUploadCallback)
    }

    private val ssImageUploadCallback = object : Callback<ImageUploadResponse> {
        override fun onResponse(call: Call<ImageUploadResponse>, response: retrofit2.Response<ImageUploadResponse>) {
            if (response.body() == null) {
                removeProgressDialog()
                showToast(getString(R.string.went_wrong))
                return
            }
            val responseModel = response.body()
            if (responseModel!!.code == 200 && Constants.SUCCESS == responseModel.status) {
                publishArticleRequest(responseModel.data.result.url)
            } else {
                removeProgressDialog()
                if (StringUtils.isNullOrEmpty(responseModel.reason)) {
                    showToast(getString(R.string.toast_response_error))
                } else {
                    showToast(responseModel.reason)
                }
            }
        }

        override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
            removeProgressDialog()
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
            showToast(getString(R.string.went_wrong))
        }
    }

    private fun publishArticleRequest(url: String) {
        showProgressDialog(resources.getString(R.string.please_wait))
        val retrofit = BaseApplication.getInstance().retrofit
        // prepare call in Retrofit 2.0
        val shortStoryAPI = retrofit.create(ShortStoryAPI::class.java)

        shortStoryDraftOrPublishRequest.type = "1"
        if (AppConstants.LOCALE_ENGLISH == SharedPrefUtils.getAppLocale(this)) {
            shortStoryDraftOrPublishRequest.lang = "0"
        } else if (AppConstants.LOCALE_HINDI == SharedPrefUtils.getAppLocale(this)) {
            shortStoryDraftOrPublishRequest.lang = "1"
        } else if (AppConstants.LOCALE_MARATHI == SharedPrefUtils.getAppLocale(this)) {
            shortStoryDraftOrPublishRequest.lang = "2"
        } else if (AppConstants.LOCALE_BENGALI == SharedPrefUtils.getAppLocale(this)) {
            shortStoryDraftOrPublishRequest.lang = "3"
        } else if (AppConstants.LOCALE_TAMIL == SharedPrefUtils.getAppLocale(this)) {
            shortStoryDraftOrPublishRequest.lang = "4"
        } else if (AppConstants.LOCALE_TELUGU == SharedPrefUtils.getAppLocale(this)) {
            shortStoryDraftOrPublishRequest.lang = "5"
        } else if (AppConstants.LOCALE_KANNADA == SharedPrefUtils.getAppLocale(this)) {
            shortStoryDraftOrPublishRequest.lang = "6"
        } else if (AppConstants.LOCALE_MALAYALAM == SharedPrefUtils.getAppLocale(this)) {
            shortStoryDraftOrPublishRequest.lang = "7"
        } else if (AppConstants.LOCALE_GUJARATI == SharedPrefUtils.getAppLocale(this)) {
            shortStoryDraftOrPublishRequest.lang = "8"
        } else if (AppConstants.LOCALE_PUNJABI == SharedPrefUtils.getAppLocale(this)) {
            shortStoryDraftOrPublishRequest.lang = "9"
        } else {
            shortStoryDraftOrPublishRequest.lang = "0"
        }
        shortStoryDraftOrPublishRequest.userAgent = "android"
        shortStoryDraftOrPublishRequest.storyImage = url
        if ("publishedList" == source) {
            draftId = articleId
            shortStoryDraftOrPublishRequest.tags = tagsList
        } else {
            val taggedList = ArrayList<Map<String, String>>()
            val categoryMap = HashMap<String, String>()
            val challengeMap = HashMap<String, String>()
            categoryMap[taggedCategoryId] = taggedCategoryName
            taggedChallengeId?.let { tagChallengeId ->
                taggedChallengeName?.let {
                    challengeMap[tagChallengeId] = it
                }
            }

            taggedList.add(categoryMap)
            taggedList.add(challengeMap)
            shortStoryDraftOrPublishRequest.tags = taggedList
        }

        val call = shortStoryAPI.updateOrPublishShortStory(draftId, shortStoryDraftOrPublishRequest)
        call.enqueue(object : Callback<ArticleDraftResponse> {
            override fun onResponse(call: Call<ArticleDraftResponse>, response: retrofit2.Response<ArticleDraftResponse>) {
                removeProgressDialog()
                if (response.body() == null) {
                    showToast(getString(R.string.server_went_wrong))
                    return
                }
                val responseModel = response.body()
                if (responseModel!!.code == 200 && Constants.SUCCESS == responseModel.status) {
                    shareUrl = responseModel.data[0].result.url
                    postConfigAPI()
                } else {
                    if (!StringUtils.isNullOrEmpty(responseModel.reason)) {
                        showToast(responseModel.reason)
                    } else {
                        showToast(getString(R.string.toast_response_error))
                    }
                }
            }

            override fun onFailure(call: Call<ArticleDraftResponse>, t: Throwable) {
                removeProgressDialog()
                Crashlytics.logException(t)
                Log.d("MC4KException", Log.getStackTraceString(t))
                showToast(getString(R.string.went_wrong))
            }
        })
    }

    private fun postConfigAPI() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val retrofit = BaseApplication.getInstance().retrofit
        // prepare call in Retrofit 2.0
        val shortStoryAPI = retrofit.create(ShortStoryAPI::class.java)
        val shortStoryConfigRequest = ShortStoryConfigRequest()

        shortStoryConfigRequest.created_by = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        shortStoryConfigRequest.short_story_id = draftId
        shortStoryConfigRequest.font_size_title = titleTvSize.toInt()
        shortStoryConfigRequest.font_size_body = storyTvSize.toInt()
        shortStoryConfigRequest.font_alignment = fontAlignment
        shortStoryConfigRequest.font_colour = font_Color
        shortStoryConfigRequest.user_id = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        shortStoryConfigRequest.category_image = categoryImageId
        if ("publishedList".equals(source)) {
            val call = shortStoryAPI.updateConfig(draftId, shortStoryConfigRequest)
            call.enqueue(shortStoryConfig)
        } else {
            val call = shortStoryAPI.shortStoryConfig(shortStoryConfigRequest)
            call.enqueue(shortStoryConfig)
        }
    }

    private val shortStoryConfig = object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
            removeProgressDialog()
            Utils.pushPublishStoryEvent(this@ShortStoriesCardActivity, "ShortStoriesCardActivity",
                    SharedPrefUtils.getUserDetailModel(this@ShortStoriesCardActivity).dynamoId, "published")
            val intent = Intent(this@ShortStoriesCardActivity, ShortStoryModerationOrShareActivity::class.java)
            intent.putExtra("shareUrl", "" + shareUrl)
            intent.putExtra(Constants.ARTICLE_ID, draftId)
            startActivity(intent)
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            removeProgressDialog()
            Crashlytics.logException(t)
            Log.d("MC4KException", Log.getStackTraceString(t))
            showToast(getString(R.string.went_wrong))
        }
    }

    /*----------------------*/

    fun getConfig() {
        val retrofit = BaseApplication.getInstance().retrofit
        val shortStoryAPI = retrofit.create(ShortStoryAPI::class.java)
        val call = shortStoryAPI.getConfig(shortStoryId)
        call.enqueue(getConfigData)
    }

    override fun onBackPressed() {
        if (fragment is ShortStoryLibraryFragment && !isTextFragment) {
            setEnabledDisabled(false)
            fragment = null
        } else {
            finish()
        }
        super.onBackPressed()
    }

    private val getConfigData = object : Callback<ShortStoryConfigData> {
        override fun onResponse(call: Call<ShortStoryConfigData>, response: retrofit2.Response<ShortStoryConfigData>) {
            if (null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                Crashlytics.logException(nee)
                return
            }
            try {
                val responseData = response.body()!!.data?.result
                responseData?.font_size_body?.toFloat()?.let { storyTv.textSize = it }
                responseData?.font_size_title?.toFloat()?.let { titleTv.textSize = it }
                responseData?.font_alignment?.let { textAlign(it) }
                responseData?.category_image_url?.let { responseData?.font_colour?.let { it1 -> responseData?.category_image?.let { it2 -> setBackground(it, it1, it2) } } }
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<ShortStoryConfigData>, t: Throwable) {
            Crashlytics.logException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    private fun getHexColor(color: String): Int? {
        val colors = color.substring(5, color.length - 1).split(",")
        try {
            val red = colors.get(0).trim().toInt()
            val green = colors.get(1).trim().toInt()
            val blue = colors.get(2).trim().toInt()
            val alpha = colors.get(3).trim().toFloat()
            if (0 <= red && red <= 255 &&
                    0 <= green && green <= 255 &&
                    0 <= blue && blue <= 255 &&
                    0f <= alpha && alpha <= 1f) {
                return Color.argb((alpha * 255).toInt(), red, green, blue);
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
        return 0
    }

    override fun updateUi(response: Response?) {

    }

    fun setBackground(url: String, fontColor: String, imageId: Int) {
        Picasso.with(BaseApplication.getAppContext()).load(url).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                .fit().into(cardBg, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        isImageLoaded = true
                    }

                    override fun onError() {
                        isImageLoaded = false
                    }

                })
        getHexColor(fontColor)?.let { divider.setBackgroundColor(it) }
        getHexColor(fontColor)?.let { titleTv.setTextColor(it) }
        getHexColor(fontColor)?.let { storyTv.setTextColor(it) }
        font_Color = fontColor
        categoryImageId = imageId
    }

    fun setEnabledDisabled(isEnabled: Boolean) {
        if (isEnabled) {
            back.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_IN);
            back.isClickable = true
        } else {
            back.setColorFilter(ContextCompat.getColor(this, R.color.color_979797), PorterDuff.Mode.SRC_IN);
            back.isClickable = false
        }
    }

    fun currentFragment(fragment: Fragment) {
        this.fragment = fragment
    }

    fun increaseTextSize() {
        titleTvSize = (titleTv.textSize) / resources.displayMetrics.scaledDensity
        storyTvSize = (storyTv.textSize) / resources.displayMetrics.scaledDensity
        if (titleTvSize < 30) {
            titleTvSize = titleTvSize + 2
            storyTvSize = storyTvSize + 2
            titleTv.textSize = titleTvSize
            storyTv.textSize = storyTvSize
        } else {
            showToast(getString(R.string.max_limit))
        }
    }

    fun checkViewAndUpdate(): Boolean {
        if (!isImageLoaded) {
            showToast(getString(R.string.ss_image_loading_msg))
            return false
        }
        val location = IntArray(2)
        shortLayout.getLocationOnScreen(location)
        x = location[0]
        y = location[1]
        if (rlLayout.height < (y - toolbar.height + ((titleTv.lineHeight * titleTv.lineCount) + (divider.measuredHeight) + (storyTv.lineHeight * storyTv.lineCount)))) {
            showToast(resources.getString(R.string.stroy_overflow_layout))
            return false
        }
        return true
    }

    fun decreaseTextSize() {
        titleTvSize = titleTv.textSize / resources.displayMetrics.scaledDensity
        storyTvSize = storyTv.textSize / resources.displayMetrics.scaledDensity
        if (titleTvSize > 10) {
            titleTvSize -= 2
            storyTvSize -= 2
            titleTv.textSize = titleTvSize
            storyTv.textSize = storyTvSize
        } else {
            showToast(getString(R.string.min_limit))
        }
    }

    fun textAlign(align: String) {
        if (align == "LEFT") {
            fontAlignment = align
            shortLayout.gravity = Gravity.LEFT
            titleTv.gravity = Gravity.LEFT
            storyTv.gravity = Gravity.LEFT
        } else if (align == "CENTER") {
            fontAlignment = align
            shortLayout.gravity = Gravity.CENTER
            titleTv.gravity = Gravity.CENTER
            storyTv.gravity = Gravity.CENTER
        } else {
            fontAlignment = align
            shortLayout.gravity = Gravity.RIGHT
            titleTv.gravity = Gravity.RIGHT
            storyTv.gravity = Gravity.RIGHT
        }
    }
}
