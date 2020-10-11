package com.mycity4kids.vlogs

import android.Manifest
import android.accounts.NetworkErrorException
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.Topics
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI
import com.mycity4kids.ui.activity.ContentCreationTutorialListingActivity
import com.mycity4kids.ui.activity.VideoTrimmerActivity
import com.mycity4kids.ui.fragment.ChooseVideoUploadOptionDialogFragment
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity
import com.mycity4kids.utils.PermissionUtil
import com.mycity4kids.utils.StringUtils
import com.mycity4kids.videotrimmer.utils.FileUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.video_category_challenge_selection_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoCategoryAndChallengeSelectionActivity : BaseActivity(),
    VideoCategoriesSelectionAdapter.RecyclerViewClickListener,
    ContentChallengeSelectionHorizontalAdapter.RecyclerViewClickListener, View.OnClickListener {

    private val REQUEST_CAMERA_PERMISSION = 1
    private val REQUEST_GALLERY_PERMISSION = 2
    private val PERMISSIONS_STORAGE_CAMERA = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
    )

    private var selectedCategory: Topics? = null
    private var duration: Int = 600
    private lateinit var videoCategoriesSelectionAdapter: VideoCategoriesSelectionAdapter
    private lateinit var videoChallengesVerticalAdapter: VideoChallengeSelectionVerticalAdapter
    private val categoryList: ArrayList<Topics> by lazy { ArrayList<Topics>() }
    private val categoryWiseChallengeList: ArrayList<Topics> by lazy { ArrayList<Topics>() }
    private lateinit var coachMark: FrameLayout
    private lateinit var transViewCoachMark: View
    private lateinit var tagImageViewCoachMark: ImageView
    private lateinit var categoryImageViewCoachMark: ImageView
    private lateinit var categoryCoachMarkContainer: FrameLayout
    private lateinit var coachMarkBottom: CardView
    private lateinit var secondTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_category_challenge_selection_activity)
        transViewCoachMark = findViewById(R.id.transViewCoachMark)
        coachMark = findViewById(R.id.coachMark)
        tagImageViewCoachMark = findViewById(R.id.tagImageViewCoachMark)
        categoryImageViewCoachMark = findViewById(R.id.categoryImageViewCoachMark)
        categoryCoachMarkContainer = findViewById(R.id.categoryCoachMarkContainer)
        coachMarkBottom = findViewById(R.id.coachMarkBottom)
        secondTextView = findViewById(R.id.secondTextView)

        vlogTutorialImageView.setOnClickListener(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        categoriesShimmerLayout.startShimmerAnimation()
        challengesShimmerLayout1.startShimmerAnimation()
        challengesShimmerLayout2.startShimmerAnimation()
        challengesShimmerLayout3.startShimmerAnimation()

        val horizontalLM = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categoriesRecyclerView.layoutManager = horizontalLM
        val verticalLM = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        challengesRecyclerView.layoutManager = verticalLM

        videoCategoriesSelectionAdapter = VideoCategoriesSelectionAdapter(this)
        categoriesRecyclerView.adapter = videoCategoriesSelectionAdapter

        videoChallengesVerticalAdapter =
            VideoChallengeSelectionVerticalAdapter(
                this
            )
        challengesRecyclerView.adapter = videoChallengesVerticalAdapter

        videoChallengesVerticalAdapter.setListData(categoryWiseChallengeList)
        videoChallengesVerticalAdapter.notifyDataSetChanged()

        videoCategoriesSelectionAdapter.setListData(categoryList)
        videoCategoriesSelectionAdapter.notifyDataSetChanged()

        getCategoriesData()
        getChallengeData()

        if (!SharedPrefUtils.getOriginalContentVideoClick(this)) {
            showOriginalContentDialog()
        }
        coachMark.setOnClickListener(this)
        transViewCoachMark.setOnClickListener(this)
        categoryImageViewCoachMark.setOnClickListener(this)
        secondTextView.setOnClickListener(this)
    }

    private fun getCategoriesData() {
        val retrofit = BaseApplication.getInstance().retrofit
        val vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI::class.java)
        val callRecentVideoArticles =
            vlogsListingAndDetailsAPI.getVlogCategoriesAndChallenges(AppConstants.HOME_VIDEOS_CATEGORYID)
        callRecentVideoArticles.enqueue(vlogCategoriesResponseCallBack)
    }

    private fun getChallengeData() {
        val retrofit = BaseApplication.getInstance().retrofit
        val vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI::class.java)
        val vlogsCategoryWiseChallengeListCall =
            vlogsListingAndDetailsAPI.getVlogsCategoryWiseChallenges()
        vlogsCategoryWiseChallengeListCall.enqueue(vlogsCategoryWiseChallengeListResponseCallBack)
    }

    private var vlogCategoriesResponseCallBack = object : Callback<Topics> {
        override fun onResponse(call: Call<Topics>, response: Response<Topics>) {
            if (null == response.body()) {
                val nee = NetworkErrorException(response.raw().toString())
                FirebaseCrashlytics.getInstance().recordException(nee)
                return
            }
            if (response.isSuccessful) {
                try {
                    categoriesShimmerLayout.visibility = View.GONE
                    categoriesRecyclerView.visibility = View.VISIBLE
                    val responseData = response.body()
                    responseData?.let {
                        processTopicsData(it)
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d(
                        "MC4kException", Log.getStackTraceString(e)
                    )
                }
            }
        }

        override fun onFailure(call: Call<Topics>, t: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(t)
            Log.d("MC4kException", Log.getStackTraceString(t))
        }
    }

    private var vlogsCategoryWiseChallengeListResponseCallBack =
        object : Callback<VlogsCategoryWiseChallengesResponse> {
            override fun onResponse(
                call: Call<VlogsCategoryWiseChallengesResponse>,
                response: Response<VlogsCategoryWiseChallengesResponse>
            ) {
                if (null == response.body()) {
                    val nee = NetworkErrorException(response.raw().toString())
                    FirebaseCrashlytics.getInstance().recordException(nee)
                    return
                }
                if (response.isSuccessful) {
                    try {
                        val responseData = response.body()
                        responseData?.let { resData ->
                            if (resData.code == 200) {
                                challengesShimmerLayout1.visibility = View.GONE
                                challengesShimmerLayout2.visibility = View.GONE
                                challengesShimmerLayout3.visibility = View.GONE
                                challengesRecyclerView.visibility = View.VISIBLE
                                resData.data.result?.let {
                                    processChallengesData(it)
                                }
                            } else {
                                showToast(responseData.reason)
                            }
                        }
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d(
                            "MC4kException", Log.getStackTraceString(e)
                        )
                    }
                }
            }

            override fun onFailure(call: Call<VlogsCategoryWiseChallengesResponse>, t: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }

    private fun processChallengesData(catWiseChallengeList: ArrayList<Topics>) {
        for (i in 0 until catWiseChallengeList.size) {
            val originalChallengeList = ArrayList<Topics>()
            originalChallengeList.addAll(catWiseChallengeList[i].child)
            categoryWiseChallengeList.add(catWiseChallengeList[i])
            categoryWiseChallengeList[i].child = ArrayList()
            for (j in 0 until originalChallengeList.size) {
                if (originalChallengeList[j].publicVisibility == "1") {
                    categoryWiseChallengeList[i].child.add(originalChallengeList[j])
                    if (i == 0 && !checkCoachmarkFlagStatus("videoOrChallengeSelectionScreen")) {
                        try {
                            transViewCoachMark.visibility = View.VISIBLE
                            coachMark.visibility = View.VISIBLE
                            coachMarkBottom.visibility = View.VISIBLE
                            Picasso.get().load(catWiseChallengeList[0].child[0].extraData[0].challenge.imageUrl).into(
                                tagImageViewCoachMark
                            )
                        } catch (e: Exception) {
                            Log.d("TAG", e.message!!)
                        }
                    }
                }
            }
        }
        videoChallengesVerticalAdapter.notifyDataSetChanged()
    }

    private fun processTopicsData(responseData: Topics) {
        responseData.child?.let {
            for (i in 0 until it.size) {
                if (it[i]?.id != AppConstants.VIDEO_CHALLENGE_ID) {
                    //                    if (it[i]?.publicVisibility == "1") {
                    //                        categoryList.add(it[i])
                    //                    }
                    if ("category-eed5fd2777a24bd48ba9a7e1e4dd4b47" == it[i].id ||
                        "category-958b29175e174f578c2d92a925451d4f" == it[i].id ||
                        "category-2ce9257cbf4c4794acacacb173feda13" == it[i].id ||
                        ("category-ee7ea82543bd4bc0a8dad288561f2beb" == it[i].id)) {
                        categoryList.add(it[i])
                    }
                }
            }
            try {
                if (categoryList.size > 0) {
                    if (!checkCoachmarkFlagStatus("videoOrChallengeSelectionScreen")) {
                        categoryCoachMarkContainer.visibility = View.VISIBLE
                        categoryImageViewCoachMark.visibility = View.VISIBLE
                        Picasso.get().load(categoryList[0].extraData[0].categoryBackImage.app).into(
                            categoryImageViewCoachMark
                        )
                    }
                }
            } catch (e: Exception) {
                categoryCoachMarkContainer.visibility = View.GONE
                categoryImageViewCoachMark.visibility = View.GONE
                coachMarkBottom.visibility = View.GONE
            }
            videoCategoriesSelectionAdapter.notifyDataSetChanged()
        }
    }

    override fun onCategoryItemClick(view: View, position: Int) {
        selectedCategory = categoryList[position]
        try {
            if (selectedCategory != null && !selectedCategory?.extraData.isNullOrEmpty() &&
                selectedCategory?.extraData?.get(0)?.max_duration != null &&
                selectedCategory?.extraData?.get(0)?.max_duration != 0) {
                duration = selectedCategory?.extraData?.get(0)?.max_duration!!
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d(
                "MC4kException", Log.getStackTraceString(e)
            )
        }
        launchAddVideoOptions(duration)
        Utils.momVlogEvent(
            this,
            "Creation listing",
            "Category_Name",
            "",
            "android",
            SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
            System.currentTimeMillis().toString(),
            "Show_upload_video",
            selectedCategory?.id,
            ""
        )
    }

    private fun launchAddVideoOptions(duration: Int) {
        val args = Bundle()
        args.putString("activity", "video_category_activity")
        args.putString("categoryId", selectedCategory?.id)
        args.putString("duration", "" + duration)
        val chooseVideoUploadOptionDialogFragment =
            ChooseVideoUploadOptionDialogFragment()
        chooseVideoUploadOptionDialogFragment.arguments = args
        chooseVideoUploadOptionDialogFragment.isCancelable = true
        val fm = supportFragmentManager
        chooseVideoUploadOptionDialogFragment.show(fm, "Choose video option")
    }

    override fun onChallengeItemClick(view: View, topics: Topics, parentCategoryId: String?) {
        when (view.id) {
            R.id.info -> {
                Utils.shareEventTracking(
                    this,
                    "Create section",
                    "Vlog_Challenges_Android",
                    "CS_Show_VCListing_Challenge"
                )
                if (!StringUtils.isNullOrEmpty(topics.extraData[0].challenge.rules)) {
                    val dialog = Dialog(this)
                    dialog.setContentView(R.layout.challenge_rules_dialog)
                    dialog.setTitle("Title...")
                    val imageView =
                        dialog.findViewById<View>(R.id.closeEditorImageView) as ImageView
                    val webView =
                        dialog.findViewById<View>(R.id.videoChallengeRulesWebView) as WebView
                    webView.loadDataWithBaseURL(
                        "",
                        topics.extraData[0].challenge.rules,
                        "text/html",
                        "UTF-8",
                        ""
                    )
                    imageView.setOnClickListener { view2: View? -> dialog.dismiss() }
                    dialog.show()
                }
            }
            R.id.tagImageView -> {
                when (parentCategoryId) {
                    "category-eed5fd2777a24bd48ba9a7e1e4dd4b47" -> {
                        Utils.shareEventTracking(
                            this,
                            "Create section",
                            "Vlog_Challenges_Android",
                            "CS_VCL_MP_Challenge"
                        )
                    }
                    "category-958b29175e174f578c2d92a925451d4f" -> {
                        Utils.shareEventTracking(
                            this,
                            "Create section",
                            "Vlog_Challenges_Android",
                            "CS_VCL_CT_Challenge"
                        )
                    }
                    "category-2ce9257cbf4c4794acacacb173feda13" -> {
                        Utils.shareEventTracking(
                            this,
                            "Create section",
                            "Vlog_Challenges_Android",
                            "CS_VCL_Aww_Challenge"
                        )
                    }
                    else -> {
                        Utils.shareEventTracking(
                            this,
                            "Create section",
                            "Vlog_Challenges_Android",
                            "CS_VCL_Live_Challenge"
                        )
                    }
                }
                val intent = Intent(
                    this,
                    NewVideoChallengeActivity::class.java
                )
                intent.putExtra("challenge", topics.id)
                intent.putExtra("comingFrom", "chooseVideoCategory")
                startActivity(intent)
            }
        }
    }

    fun requestPermissions(imageFrom: String) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) ||
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )) {
            Snackbar.make(
                rootLayout, R.string.permission_storage_rationale,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(
                    R.string.ok
                ) { requestUngrantedPermissions(imageFrom) }
                .show()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            )) {
            Snackbar.make(
                rootLayout, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(
                    R.string.ok
                ) { requestUngrantedPermissions(imageFrom) }
                .show()
        } else {
            requestUngrantedPermissions(imageFrom)
        }
    }

    private fun requestUngrantedPermissions(imageFrom: String) {
        val permissionList =
            java.util.ArrayList<String>()
        for (i in PERMISSIONS_STORAGE_CAMERA.indices) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    PERMISSIONS_STORAGE_CAMERA[i]
                ) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(PERMISSIONS_STORAGE_CAMERA[i])
            }
        }
        val requiredPermission =
            permissionList.toTypedArray()
        if ("gallery" == imageFrom) {
            ActivityCompat.requestPermissions(
                this,
                requiredPermission,
                REQUEST_GALLERY_PERMISSION
            )
        } else if ("camera" == imageFrom) {
            ActivityCompat.requestPermissions(
                this,
                requiredPermission,
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(
                    rootLayout, R.string.permision_available_init,
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                val videoCapture =
                    Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                startActivityForResult(videoCapture, AppConstants.REQUEST_VIDEO_TRIMMER)
            } else {
                Snackbar.make(
                    rootLayout, R.string.permissions_not_granted,
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        } else if (requestCode == REQUEST_GALLERY_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(
                    rootLayout, R.string.permision_available_init,
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                val intent = Intent()
                intent.type = "video/mp4"
                intent.action = Intent.ACTION_GET_CONTENT
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(
                    Intent.createChooser(
                        intent,
                        getString(R.string.label_select_video)
                    ), AppConstants.REQUEST_VIDEO_TRIMMER
                )
            } else {
                Snackbar.make(
                    rootLayout, R.string.permissions_not_granted,
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            AppConstants.REQUEST_VIDEO_TRIMMER -> {
                val selectedUri = data?.data
                if (selectedUri != null) {
                    startTrimActivity(selectedUri)
                } else {
                    Toast.makeText(
                        this,
                        R.string.toast_cannot_retrieve_selected_video,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun startTrimActivity(uri: Uri) {
        val intent = Intent(
            this,
            VideoTrimmerActivity::class.java
        )
        intent.putExtra("categoryId", "")
        intent.putExtra("selectedCategory", selectedCategory)
        intent.putExtra("duration", "" + duration)
        intent.putExtra("comingFrom", "notFromChallenge")
        intent.putExtra(
            "EXTRA_VIDEO_PATH",
            FileUtils.getPath(this, uri)
        )
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.vlogTutorialImageView) {
            Utils.shareEventTracking(this, "Create section", "Create_Android", "V_Tutorial_Video")
            val intent = Intent(this, ContentCreationTutorialListingActivity::class.java)
            intent.putExtra(AppConstants.COLLECTION_ID, AppConstants.MOM_VLOG_TUTORIAL_COLLECTION)
            startActivity(intent)
        } else if (view?.id == R.id.transViewCoachMark || view?.id == R.id.coachMark || view?.id == R.id.categoryImageViewCoachMark) {
            updateCoachmarkFlag("videoOrChallengeSelectionScreen", true)
            coachMark.visibility = View.GONE
            transViewCoachMark.visibility = View.GONE
            categoryImageViewCoachMark.visibility = View.GONE
            categoryCoachMarkContainer.visibility = View.GONE
            coachMarkBottom.visibility = View.GONE
        } else if (view?.id == R.id.secondTextView) {
            updateCoachmarkFlag("videoOrChallengeSelectionScreen", true)
            updateCoachmarkFlag("videoTrimmer", true)
            updateCoachmarkFlag("videoTitleAndTags", true)
            coachMark.visibility = View.GONE
            transViewCoachMark.visibility = View.GONE
            categoryImageViewCoachMark.visibility = View.GONE
            categoryCoachMarkContainer.visibility = View.GONE
            coachMarkBottom.visibility = View.GONE
        }
    }

    fun showOriginalContentDialog() {
        val dialog = Dialog(this)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_original_content)
        dialog.setCancelable(false)
        dialog.findViewById<View>(R.id.okBtn).setOnClickListener { view: View? ->
            SharedPrefUtils.setOriginalContentVideoClick(this, true)
            dialog.dismiss()
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
}
