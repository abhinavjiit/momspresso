package com.mycity4kids.ui.activity

import android.Manifest
import android.accounts.NetworkErrorException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
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
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.ExploreTopicsModel
import com.mycity4kids.models.request.ShortStoryConfigRequest
import com.mycity4kids.models.request.ShortStoryDraftOrPublishRequest
import com.mycity4kids.models.response.*
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
import okhttp3.RequestBody
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
    private lateinit var backButton: ImageButton
    private var fragment: Fragment? = null
    private var isTextFragment: Boolean = false
    private lateinit var parent: RelativeLayout
    private lateinit var divider: View
    private var ssTopicsText: String? = null
    private lateinit var publishTextView: TextView
    private var source: String? = null
    private lateinit var mLayout: View
    private lateinit var path: String
    private lateinit var imageUriTemp: Uri
    private lateinit var MEDIA_TYPE_PNG: MediaType
    private lateinit var file: File
    private lateinit var challengeName: String
    private lateinit var challengeId: String
    private lateinit var runningrequest: String
    private lateinit var requestBodyFile: RequestBody
    private lateinit var imageType: RequestBody
    private var draftId: String = ""
    private var articleId: String = ""
    private lateinit var tagsList: ArrayList<Map<String, String>>
    private lateinit var listDraft: ArrayList<Map<String, String>>
    private lateinit var draftChallengeId: String
    private lateinit var draftChallengeName: String
    private lateinit var currentActiveChallenge: String
    private lateinit var currentActiveChallengeId: String
    private var flag: Boolean = false
    private var ssTopicsList: ArrayList<ExploreTopicsModel>? = null
    private lateinit var shortStoryDraftOrPublishRequest: ShortStoryDraftOrPublishRequest
    private var titleTvSize: Float = 0.0f
    private var storyTvSize: Float = 0.0f
    private var fontAlignment: String = "LEFT"
    private lateinit var font_Color: String
    private var categoryImageId: Int = 0
    private lateinit var shareUrl: String
    private var shortStoryId: String = "article-bd64fa1c15814b8cae079bf4cdadc126"
    private var count: Int = 0
    private lateinit var rlLayout: RelativeLayout
    private var categoryId: String = "category-743892a865774baf9c20cbcc5c01d35f"
    private var x: Int = 0
    private var y: Int = 0


    private var pageNumber = 0
    private var isLastPageReached = false
    private var isReuqestRunning = false
    private var dataList = ArrayList<Images>()

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
//        parent = findViewById(R.id.rl_layout)
        back = findViewById(R.id.back)
        storyTv = findViewById(R.id.short_text)
        divider = findViewById(R.id.divider)
        publishTextView = findViewById(R.id.publishTextView)
        collectionsViewPager = findViewById<ViewPager>(R.id.collectionsViewPager)

        val params: ViewGroup.LayoutParams = rlLayout.layoutParams
        params.width = resources.displayMetrics.widthPixels
        params.height = resources.displayMetrics.widthPixels
        rlLayout.layoutParams = params
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = intent.getStringExtra("title")
        story = intent.getStringExtra("story")
        if (intent.getStringExtra("ssTopicsText") != null)
            ssTopicsText = intent.getStringExtra("ssTopicsText")
        if (intent.getStringExtra("challengeName") != null)
            challengeName = intent.getStringExtra("challengeName")
        if (intent.getStringExtra("challengeId") != null)
            challengeId = intent.getStringExtra("challengeId")
        if (intent.getStringExtra("runningrequest") != null)
            runningrequest = intent.getStringExtra("runningrequest")
        if (intent.getStringExtra("draftId") != null)
            draftId = intent.getStringExtra("draftId")
        if (intent.getStringExtra("articleId") != null) {
            articleId = intent.getStringExtra("articleId")
            shortStoryId = articleId
        }
        if (intent.getStringExtra("source") != null)
            source = intent.getStringExtra("source")
        if (intent.getStringExtra("categoryId") != null)
            categoryId = intent.getStringExtra("categoryId")

        flag = intent.getBooleanExtra("flag", false)
        if (intent.getSerializableExtra("listDraft") != null)
            listDraft = intent.getSerializableExtra("listDraft") as ArrayList<Map<String, String>>
        if (intent.getSerializableExtra("tagsList") != null)
            tagsList = intent.getSerializableExtra("tagsList") as ArrayList<Map<String, String>>
        if (intent.getStringExtra("currentActiveChallengeId") != null)
            currentActiveChallengeId = intent.getStringExtra("currentActiveChallengeId")
        if (intent.getStringExtra("currentActiveChallenge") != null)
            currentActiveChallenge = intent.getStringExtra("currentActiveChallenge")
        if (intent.getParcelableArrayListExtra<ExploreTopicsModel>("ssTopicsList") != null)
            ssTopicsList = intent.getParcelableArrayListExtra<ExploreTopicsModel>("ssTopicsList")

        titleTv.text = title
        storyTv.text = story

        tabs.apply {
            addTab(tabs.newTab().setText(resources.getString(R.string.background)))
            addTab(tabs.newTab().setText(resources.getString(R.string.text)))
        }


        setEnabledDisabled(false)

        val root = tabs.getChildAt(0)
        if (root is LinearLayout) {
            (root as LinearLayout).showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            val drawable = GradientDrawable()
            drawable.setColor(resources.getColor(R.color.app_red))
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
//                checkViewAndUpdate();
            }
        });

//        checkViewAndUpdate()

    }

    fun setCategoryId(id: String) {
        categoryId = id
    }

    fun getCategoryId(): String {
        if ("publishedList".equals(source)) {
            for (i in 0 until tagsList.size) {
                val myMap = tagsList.get(i)
                for (entrySet in myMap.entries) {
                    for (j in 0 until ssTopicsList!!.size) {
                        if (ssTopicsList?.get(j)?.getDisplay_name().equals(entrySet.value)) {
                            categoryId = entrySet.key
                            return categoryId
                        }
                    }
                }
            }
        }

        return categoryId
    }

    private fun publishStory() {
        if ("publishedList" == source) {
            shortStoryDraftOrPublishRequest = ShortStoryDraftOrPublishRequest()
            shortStoryDraftOrPublishRequest.setTitle(titleTv.text.toString().trim({ it <= ' ' }))
            shortStoryDraftOrPublishRequest.setBody(storyTv.text.toString())
            shortStoryDraftOrPublishRequest.setUserAgent("android")
            shortStoryDraftOrPublishRequest.setType("0")
            if (AppConstants.LOCALE_ENGLISH == SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) {
                shortStoryDraftOrPublishRequest.setLang("0")
            } else if (AppConstants.LOCALE_HINDI == SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) {
                shortStoryDraftOrPublishRequest.setLang("1")
            } else if (AppConstants.LOCALE_MARATHI == SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) {
                shortStoryDraftOrPublishRequest.setLang("2")
            } else if (AppConstants.LOCALE_BENGALI == SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) {
                shortStoryDraftOrPublishRequest.setLang("3")
            } else if (AppConstants.LOCALE_TAMIL == SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) {
                shortStoryDraftOrPublishRequest.setLang("4")
            } else if (AppConstants.LOCALE_TELUGU == SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) {
                shortStoryDraftOrPublishRequest.setLang("5")
            } else {
                shortStoryDraftOrPublishRequest.setLang("0")
            }
            getBlogPage()
        } else {
            saveDraftBeforePublishRequest(titleTv.getText().toString().trim({ it <= ' ' }), storyTv.getText().toString().trim({ it <= ' ' }), draftId)
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
                        if (StringUtils.isNullOrEmpty(responseData.data[0].result.email)) {
                            val intent = Intent(this@ShortStoriesCardActivity, BlogSetupActivity::class.java)
                            intent.putExtra("BlogTitle", responseData.data[0].result.blogTitle)
                            intent.putExtra("email", responseData.data[0].result.email)
                            intent.putExtra("comingFrom", "ShortStoryAndArticle")
                            startActivity(intent)
                        } else if (!StringUtils.isNullOrEmpty(responseData.data[0].result.email)) {
                            val intent = Intent(this@ShortStoriesCardActivity, BlogSetupActivity::class.java)
                            intent.putExtra("BlogTitle", responseData.data[0].result.blogTitle)
                            intent.putExtra("email", responseData.data[0].result.email)
                            intent.putExtra("comingFrom", "ShortStoryAndArticle")
                            startActivity(intent)
                        }
                    } else if (!StringUtils.isNullOrEmpty(responseData.data[0].result.blogTitleSlug)) {
                        if (responseData.data[0].result.email == null || responseData.data[0].result.email.isEmpty()) {
                            val intent = Intent(this@ShortStoriesCardActivity, BlogSetupActivity::class.java)
                            intent.putExtra("BlogTitle", responseData.data[0].result.blogTitle)
                            intent.putExtra("email", responseData.data[0].result.email)
                            intent.putExtra("comingFrom", "ShortStoryAndArticle")
                            startActivity(intent)
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
//            finalBitmap = AppUtils.drawMultilineTextToBitmap(titleTv.getText().toString(), storyTv.getText().toString(),
//                    SharedPrefUtils.getUserDetailModel(this).first_name + " " + SharedPrefUtils.getUserDetailModel(this).last_name, true)
            finalBitmap = AppUtils.getBitmapFromView(rlLayout, "shortStory")
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }

        val retro = BaseApplication.getInstance().retrofit
        val imageUploadAPI = retro.create(ImageUploadAPI::class.java)
        path = MediaStore.Images.Media.insertImage(contentResolver, finalBitmap, "Title", null)
        Log.d("ShortStory", "Path = $path")

        if (path != null) {
            imageUriTemp = Uri.parse(path)
        }
        if (imageUriTemp != null) {
            file = FileUtils.getFile(this, imageUriTemp)
        }

        MEDIA_TYPE_PNG = MediaType.parse("image/png")!!
        if (file != null && MEDIA_TYPE_PNG != null) {
            requestBodyFile = RequestBody.create(MEDIA_TYPE_PNG, file)
        }
        imageType = RequestBody.create(MediaType.parse("text/plain"), "4")
        if (requestBodyFile != null) {
            val call = imageUploadAPI.uploadImage(imageType, requestBodyFile)
            call.enqueue(ssImageUploadCallback)
        }
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
        } else {
            shortStoryDraftOrPublishRequest.lang = "0"
        }
        shortStoryDraftOrPublishRequest.userAgent = "android"
        shortStoryDraftOrPublishRequest.storyImage = url
        if ("publishedList" == source) {
            draftId = articleId
            shortStoryDraftOrPublishRequest.tags = tagsList
        } else {
            if (ssTopicsText != null) {
                for (i in ssTopicsList?.indices!!) {
                    if (ssTopicsList?.get(i)?.getDisplay_name() == ssTopicsText) {
                        ssTopicsList?.get(i)?.setIsSelected(true)
                    }
                }
            }
            for (i in ssTopicsList!!.indices) {
                if (ssTopicsList!!.get(i).isSelected()) {
                    val map1 = HashMap<String, String>()
                    val list = ArrayList<Map<String, String>>()
                    val list1 = ArrayList<Map<String, String>>()
                    val list2 = ArrayList<Map<String, String>>()
                    val map = HashMap<String, String>()
                    ssTopicsList?.get(i)?.id = ssTopicsList?.get(i)?.getDisplay_name()
                    list.add(map)
                    if (runningrequest == "challenge") {
                        map1.put(challengeId, challengeName)
                        map1[challengeId] = challengeName
                        list1.add(map1)
                    } else if ("draftList" == source) {
                        if (!flag) {
                            if (!listDraft.isEmpty()) {
                                map1.put(draftChallengeId, draftChallengeName)
                                list1.add(map1)
                            } else {
                            }
                        } else {
                            map1.put(currentActiveChallengeId, currentActiveChallenge)
                            list1.add(map1)
                        }
                    }
                    list2.addAll(list1)
                    list2.addAll(list)
                    shortStoryDraftOrPublishRequest.tags = list2
                    break
                }
            }
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
            Utils.pushPublishStoryEvent(this@ShortStoriesCardActivity, "AddShortStoryScreen", SharedPrefUtils.getUserDetailModel(this@ShortStoriesCardActivity).dynamoId, "published")
            val intent = Intent(this@ShortStoriesCardActivity, ArticleModerationOrShareActivity::class.java)
            intent.putExtra("shareUrl", "" + shareUrl)
            intent.putExtra("source", "addStory")
            intent.putExtra("title", shortStoryDraftOrPublishRequest.title)
            intent.putExtra("body", shortStoryDraftOrPublishRequest.body)
            startActivity(intent)
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

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
                responseData?.font_size_body?.toFloat()?.let { storyTv.setTextSize(it) }
                responseData?.font_size_title?.toFloat()?.let { titleTv.setTextSize(it) }
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

    fun setDividerColor(color: String) {
        val dividerColor: GradientDrawable
        dividerColor = divider.background as GradientDrawable
        dividerColor.color = getHexColor(color)?.let { ColorStateList.valueOf(it) }
    }

    fun getHexColor(color: String): Int? {
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
                .fit().into(cardBg)
        setDividerColor(fontColor)
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

//        checkViewAndUpdate()
    }

    fun checkViewAndUpdate(): Boolean {
        var check = true
        val location = IntArray(2)
        shortLayout.getLocationOnScreen(location)
        x = location[0]
        y = location[1]
        if (rlLayout.height < (y - toolbar.height + ((titleTv.lineHeight*titleTv.lineCount) + (divider.measuredHeight) + (storyTv.lineHeight*storyTv.lineCount)))) {
            showToast("Your story is too long. Please reduce the font size or shorten the story.")
            return false
        }
        return true
    }

    fun decreaseTextSize() {
        titleTvSize = titleTv.textSize / resources.displayMetrics.scaledDensity
        storyTvSize = storyTv.textSize / resources.displayMetrics.scaledDensity
        if (titleTvSize > 10) {
            titleTvSize = titleTvSize - 2
            storyTvSize = storyTvSize - 2
            titleTv.textSize = titleTvSize
            storyTv.textSize = storyTvSize
        } else {
            showToast(getString(R.string.min_limit))
        }
    }

    fun textAlign(align: String) {
        if (align.equals("LEFT")) {
            fontAlignment = align
            shortLayout.gravity = Gravity.LEFT
            titleTv.gravity = Gravity.LEFT
            storyTv.gravity = Gravity.LEFT
        } else if (align.equals("CENTER")) {
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