package com.mycity4kids.vlogs

import android.Manifest
import android.accounts.NetworkErrorException
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.crashlytics.android.Crashlytics
import com.google.android.material.snackbar.Snackbar
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.Topics
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI
import com.mycity4kids.ui.activity.VideoTrimmerActivity
import com.mycity4kids.ui.fragment.ChooseVideoUploadOptionDialogFragment
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity
import com.mycity4kids.utils.PermissionUtil
import com.mycity4kids.utils.StringUtils
import com.mycity4kids.videotrimmer.utils.FileUtils
import kotlinx.android.synthetic.main.video_category_challenge_selection_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoCategoryAndChallengeSelectionActivity : BaseActivity(),
    VideoCategoriesSelectionAdapter.RecyclerViewClickListener,
    VideoChallengeSelectionHorizontalAdapter.RecyclerViewClickListener {

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
    //    private var categoryChallengeHashMap = HashMap<String, ArrayList<Topics>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_category_challenge_selection_activity)

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
                Crashlytics.logException(nee)
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
                    Crashlytics.logException(e)
                    Log.d(
                        "MC4kException", Log.getStackTraceString(e)
                    )
                }
            }
        }

        override fun onFailure(call: Call<Topics>, t: Throwable) {
            Crashlytics.logException(t)
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
                    Crashlytics.logException(nee)
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
                        Crashlytics.logException(e)
                        Log.d(
                            "MC4kException", Log.getStackTraceString(e)
                        )
                    }
                }
            }

            override fun onFailure(call: Call<VlogsCategoryWiseChallengesResponse>, t: Throwable) {
                Crashlytics.logException(t)
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
                }
            }
        }
        videoChallengesVerticalAdapter.notifyDataSetChanged()
    }

    private fun processTopicsData(responseData: Topics) {
        responseData.child?.let {
            for (i in 0 until it.size) {
                //                if (it[i]?.id == AppConstants.VIDEO_CHALLENGE_ID) {
                //                    processChallengesData(it[i])
                //                }
                if (it[i]?.id != AppConstants.VIDEO_CHALLENGE_ID) {
                    if (it[i]?.publicVisibility == "1") {
                        categoryList.add(it[i])
                    }
                }
            }

            //            for (i in 0 until categoryList.size) {
            //                for (j in 0 until categoryList[i].child.size) {
            //                    categoryChallengeHashMap[categoryList[i].child[j].id]?.let { challenge ->
            //                        categoryList[i].taggedChallengeList.addAll(challenge)
            //                    }
            //                }
            //                categoryList[i].taggedChallengeList.sortByDescending { it.extraData[0].challenge.is_live }
            //            }
            //
            //            for (i in 0 until categoryList.size) {
            //                if (!categoryList[i].taggedChallengeList.isNullOrEmpty()) {
            //                    categoryWiseChallengeList.add(categoryList[i])
            //                }
            //            }

            videoCategoriesSelectionAdapter.notifyDataSetChanged()
            //            videoChallengesVerticalAdapter.notifyDataSetChanged()
        }
    }

    //    private fun processChallengesData(parentChallenge: Topics?) {
    //        parentChallenge?.let {
    //            // subcategory-challenge mapping
    //            for (i in 0 until it.child.size) {
    //                if (it.child[i].publicVisibility == "1") {
    //                    if (it.child[i].extraData[0].challenge.is_live == "1") {
    //                        if (categoryWiseChallengeList.isEmpty()) {
    //                            val liveChallengeTopic = Topics()
    //                            liveChallengeTopic.display_name =
    //                                getString(R.string.all_live_challenges)
    //                            liveChallengeTopic.taggedChallengeList.add(it.child[i])
    //                            categoryWiseChallengeList.add(liveChallengeTopic)
    //                        } else {
    //                            categoryWiseChallengeList[0].taggedChallengeList.add(it.child[i])
    //                        }
    //                    }
    //                    if (it.child[i].extraData[0].challenge.mapped_category.isNullOrBlank()) {
    //                        it.child[i].extraData[0].challenge.mapped_category =
    //                            "category-ec48b6f0737e44b3a3681f2cca1d5faf"
    //                    }
    //                    it.child[i].extraData[0].challenge.mapped_category?.let { mappedCategory ->
    //                        if (categoryChallengeHashMap[mappedCategory] == null) {
    //                            val arraylist = ArrayList<Topics>()
    //                            arraylist.add(it.child[i])
    //                            categoryChallengeHashMap[mappedCategory] = arraylist
    //                        } else {
    //                            categoryChallengeHashMap[mappedCategory]?.add(it.child[i])
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //    }

    override fun onCategoryItemClick(view: View, position: Int) {
        selectedCategory = categoryList[position]
        try {
            if (selectedCategory != null && !selectedCategory?.extraData.isNullOrEmpty() &&
                selectedCategory?.extraData?.get(0)?.max_duration != null &&
                selectedCategory?.extraData?.get(0)?.max_duration != 0) {
                duration = selectedCategory?.extraData?.get(0)?.max_duration!!
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d(
                "MC4kException", Log.getStackTraceString(e)
            )
        }
        launchAddVideoOptions(600)
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

    override fun onChallengeItemClick(view: View, topics: Topics) {
        when (view.id) {
            R.id.info -> {
                if (!StringUtils.isNullOrEmpty(topics.extraData[0].challenge.rules)) {
                    val dialog = Dialog(this)
                    dialog.setContentView(R.layout.challenge_rules_dialog)
                    dialog.setTitle("Title...")
                    val imageView =
                        dialog.findViewById<View>(R.id.closeEditorImageView) as ImageView
                    val webView =
                        dialog.findViewById<View>(R.id.videoChallengeRulesWebView) as WebView
                    webView.loadData(topics.extraData[0].challenge.rules, "text/html", "UTF-8")
                    imageView.setOnClickListener { view2: View? -> dialog.dismiss() }
                    dialog.show()
                    Utils.momVlogEvent(
                        this,
                        "Creation listing",
                        "Challenge_info",
                        "",
                        "android",
                        SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                        System.currentTimeMillis().toString(),
                        "Show_challenge_detail",
                        "",
                        topics.id
                    )
                }
            }
            R.id.tagImageView -> {
                val intent = Intent(
                    this,
                    NewVideoChallengeActivity::class.java
                )
                intent.putExtra("challenge", topics.id)
                intent.putExtra("comingFrom", "chooseVideoCategory")
                startActivity(intent)
                Utils.momVlogEvent(
                    this,
                    "Creation listing",
                    "Listing_challenge_container",
                    "",
                    "android",
                    SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                    SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId,
                    System.currentTimeMillis().toString(),
                    "Show_challenge_detail",
                    "",
                    topics.id
                )
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
}
