package com.mycity4kids.ui.activity

import android.Manifest
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.crashlytics.android.Crashlytics
import com.kelltontech.ui.BaseActivity
import com.kelltontech.utils.ToastUtils
import com.mycity4kids.BuildConfig
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.response.ShortStoryDetailResult
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI
import com.mycity4kids.utils.AppUtils
import com.mycity4kids.utils.SharingUtils
import com.mycity4kids.widget.ShareButtonWidget
import com.mycity4kids.widget.StoryShareCardWidget
import com.squareup.picasso.Picasso
import org.apache.commons.lang.WordUtils
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*


class ShortStoryModerationOrShareActivity : BaseActivity(), View.OnClickListener,
        EasyPermissions.PermissionCallbacks {

    private val RC_STORAGE_PERMISSION = 123
    private var storyShareCardWidget: StoryShareCardWidget? = null
    private var shareStoryImageView: ImageView? = null
    private var shareStoryAuthorTextView: TextView? = null
    private var storyImageView: ImageView? = null
    private var storyAuthorTextView: TextView? = null
    private var shareMedium: String? = null
    private var shareUrl: String? = null
    private var userId: String? = null
    private var authorName: String? = null
    private val storyCategoriesList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.short_story_moderation_share_activity)
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        val moderationContainer: RelativeLayout? = findViewById(R.id.moderationContainer)
        val publishContainer: ScrollView? = findViewById(R.id.publishedContainer)
        val okayTextView: TextView? = findViewById(R.id.okayTextView)
        val fbShareWidget: ShareButtonWidget? = findViewById(R.id.facebookShareWidget)
        val whatsAppShareWidget: ShareButtonWidget? = findViewById(R.id.whatsAppShareWidget)
        val instagramShareWidget: ShareButtonWidget? = findViewById(R.id.instagramShareWidget)
        val genericShareWidget: ShareButtonWidget? = findViewById(R.id.genericShareWidget)
        val includeShareLayout: CardView? = findViewById(R.id.includeShareLayout)
        storyImageView = includeShareLayout?.findViewById(R.id.storyImageView)
        storyAuthorTextView = includeShareLayout?.findViewById(R.id.storyAuthorTextView)
        storyShareCardWidget = findViewById(R.id.storyShareCardWidget)
        shareStoryImageView = storyShareCardWidget?.findViewById(R.id.storyImageView)
        shareStoryAuthorTextView = storyShareCardWidget?.findViewById(R.id.storyAuthorTextView)

        userId = SharedPrefUtils.getUserDetailModel(this).dynamoId
        authorName = SharedPrefUtils.getUserDetailModel(this).first_name + " " +
                SharedPrefUtils.getUserDetailModel(this).last_name
        shareUrl = intent.getStringExtra("shareUrl")
        val storyId: String? = intent.getStringExtra(Constants.ARTICLE_ID)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.cross_icon_back)
        supportActionBar?.title = getString(R.string.ad_bottom_bar_generic_share)
        fbShareWidget?.setOnClickListener(this)
        whatsAppShareWidget?.setOnClickListener(this)
        instagramShareWidget?.setOnClickListener(this)
        genericShareWidget?.setOnClickListener(this)
        okayTextView?.setOnClickListener(this)

        if (shareUrl == "https://www.momspresso.com/parenting/topic/short-stories") {
            moderationContainer?.visibility = View.VISIBLE
            publishContainer?.visibility = View.GONE
        } else {
            moderationContainer?.visibility = View.GONE
            publishContainer?.visibility = View.VISIBLE
        }

        showProgressDialog(getString(R.string.please_wait))
        Handler().postDelayed(Runnable { getShortStoryDetails(storyId) }, 4000)
    }

    private fun getShortStoryDetails(storyId: String?) {
        val retro = BaseApplication.getInstance().retrofit
        val shortStoryAPI = retro.create(ShortStoryAPI::class.java)
        val call: Call<ShortStoryDetailResult> = shortStoryAPI.getShortStoryDetails(storyId, "articleId")
        call.enqueue(storyDetailResponseCallbackRedis)
    }

    private var storyDetailResponseCallbackRedis = object : Callback<ShortStoryDetailResult> {
        override fun onResponse(call: Call<ShortStoryDetailResult>, response: retrofit2.Response<ShortStoryDetailResult>) {
            removeProgressDialog()
            if (response.body() == null) {
                return
            }
            try {
                val responseData = response.body()
                Picasso.get().load(responseData?.storyImage).into(storyImageView)
                Picasso.get().load(responseData?.storyImage).into(shareStoryImageView)
                storyAuthorTextView?.text = WordUtils.capitalizeFully(responseData?.userName)
                shareStoryAuthorTextView?.text = WordUtils.capitalizeFully(responseData?.userName)
                processTags(responseData?.tags)
            } catch (e: Exception) {
                removeProgressDialog()
                Crashlytics.logException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }

        override fun onFailure(call: Call<ShortStoryDetailResult>, t: Throwable) {
            removeProgressDialog()
            handleExceptions(t)
        }
    }

    private fun processTags(tags: ArrayList<MutableMap<String, String>>?) {
        tags?.let {
            for (i in 0 until it.size) {
                for ((j, k) in it[i]) {
                    storyCategoriesList.add(j)
                }
            }
        }
    }

    private fun handleExceptions(t: Throwable) {
        if (t is UnknownHostException) {
            showToast(getString(R.string.error_network))
        } else if (t is SocketTimeoutException) {
            showToast(getString(R.string.connection_timeout))
        }
        Crashlytics.logException(t)
        Log.d("MC4kException", Log.getStackTraceString(t))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.facebookShareWidget -> {
                shareMedium = AppConstants.MEDIUM_FACEBOOK
                if (!createSharableImageWhileCheckingPermissions()) {
                    return
                }
                shareStory()
            }
            R.id.whatsAppShareWidget -> {
                shareMedium = AppConstants.MEDIUM_WHATSAPP
                if (!createSharableImageWhileCheckingPermissions()) {
                    return
                }
                shareStory()
            }
            R.id.instagramShareWidget -> {
                try {
                    val hashtags = AppUtils.getHasTagFromCategoryList(storyCategoriesList)
                    AppUtils.copyToClipboard(hashtags)
                    ToastUtils.showToast(this, getString(R.string.all_insta_share_clipboard_msg))
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
                shareMedium = AppConstants.MEDIUM_INSTAGRAM
                if (!createSharableImageWhileCheckingPermissions()) {
                    return
                }
                shareStory()
            }
            R.id.genericShareWidget -> {
                shareMedium = AppConstants.MEDIUM_GENERIC
                if (!createSharableImageWhileCheckingPermissions()) {
                    return
                }
                shareStory()
            }
            R.id.okayTextView -> {
                launchHome()
            }
        }
    }

    private fun shareStory() {
        val uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() +
                "/MyCity4Kids/videos/" + AppConstants.STORY_SHARE_IMAGE_NAME + ".jpg")
        when (shareMedium) {
            AppConstants.MEDIUM_FACEBOOK -> {
                SharingUtils.shareViaFacebook(this)
                Utils.pushShareStoryEvent(this, "ShortStoryModerationOrShareActivity",
                        userId + "", shareUrl, "$userId~$authorName", "Facebook")
            }
            AppConstants.MEDIUM_WHATSAPP -> {
                if (AppUtils.shareImageWithWhatsApp(this, uri, getString(R.string.ss_follow_author,
                                authorName, AppConstants.USER_PROFILE_SHARE_BASE_URL + userId))) {
                    Utils.pushShareStoryEvent(this, "ShortStoryModerationOrShareActivity", userId + "", shareUrl,
                            "$userId~$authorName", "Whatsapp")
                }
            }
            AppConstants.MEDIUM_INSTAGRAM -> {
                if (AppUtils.shareImageWithInstagram(this, uri)) {
                    Utils.pushShareStoryEvent(this, "ShortStoryModerationOrShareActivity", userId + "", shareUrl,
                            "$userId~$authorName", "Instagram")
                }
            }
            AppConstants.MEDIUM_GENERIC -> {
                if (AppUtils.shareGenericImageAndOrLink(this, uri, getString(R.string.ss_follow_author,
                                authorName, AppConstants.USER_PROFILE_SHARE_BASE_URL + userId))) {
                    Utils.pushShareStoryEvent(this, "ShortStoryModerationOrShareActivity", userId + "", shareUrl,
                            "$userId~$authorName", "Generic")
                }
            }
        }
    }

    private fun hasStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun createSharableImageWhileCheckingPermissions(): Boolean {
        if (hasStoragePermission()) {
            if (!createSharableImage()) {
                return false
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.permission_storage_rationale),
                    RC_STORAGE_PERMISSION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return false
        }
        return true
    }

    private fun createSharableImage(): Boolean {
        try {
            val bitmap = (shareStoryImageView?.drawable as BitmapDrawable).bitmap
            shareStoryImageView?.setImageBitmap(SharingUtils.getRoundCornerBitmap(bitmap, AppUtils.dpTopx(4.0f)))
            AppUtils.getBitmapFromView(storyShareCardWidget, AppConstants.STORY_SHARE_IMAGE_NAME)
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String?>) {
        if (!createSharableImage()) {
            return
        }
        shareStory()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String?>) {
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            val yes = getString(R.string.lang_sel_yes)
            val no = getString(R.string.cancel)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                launchHome()
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun launchHome() {
        val intent = Intent(this@ShortStoryModerationOrShareActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("isFromShare", true)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (BuildConfig.DEBUG) {
            super.onBackPressed()
        }
    }
}
