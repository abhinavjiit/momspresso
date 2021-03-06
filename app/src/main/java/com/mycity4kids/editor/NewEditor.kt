package com.mycity4kids.editor

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.mycity4kids.BuildConfig
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.listener.OnButtonClicked
import com.mycity4kids.models.request.SaveDraftRequest
import com.mycity4kids.models.response.ArticleDraftResponse
import com.mycity4kids.models.response.DraftListResult
import com.mycity4kids.models.response.ImageUploadResponse
import com.mycity4kids.models.response.PublishDraftObject
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDraftAPI
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI
import com.mycity4kids.ui.activity.AddArticleTopicsActivityNew
import com.mycity4kids.ui.activity.SpellCheckActivity
import com.mycity4kids.ui.fragment.SpellCheckDialogFragment
import com.mycity4kids.ui.fragment.SpellCheckDialogFragment.ISpellcheckResult
import com.mycity4kids.utils.ConnectivityUtils
import com.mycity4kids.utils.DateTimeUtils
import com.mycity4kids.utils.GenericFileProvider
import com.mycity4kids.utils.PermissionUtil
import com.mycity4kids.utils.StringUtils
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.HashMap
import java.util.Locale
import java.util.Random
import java.util.regex.Pattern
import kotlinx.android.synthetic.main.activity_new_editor.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.wordpress.android.util.AppLog
import org.wordpress.android.util.ToastUtils
import org.wordpress.android.util.helpers.MediaFile
import org.wordpress.aztec.Aztec
import org.wordpress.aztec.AztecAttributes
import org.wordpress.aztec.AztecExceptionHandler
import org.wordpress.aztec.AztecText
import org.wordpress.aztec.IHistoryListener
import org.wordpress.aztec.ITextFormat
import org.wordpress.aztec.demo.MediaToolbarCameraButton
import org.wordpress.aztec.demo.MediaToolbarGalleryButton
import org.wordpress.aztec.glideloader.GlideImageLoader
import org.wordpress.aztec.plugins.IMediaToolbarButton
import org.wordpress.aztec.plugins.shortcodes.AudioShortcodePlugin
import org.wordpress.aztec.plugins.shortcodes.extensions.ATTRIBUTE_VIDEOPRESS_HIDDEN_ID
import org.wordpress.aztec.plugins.shortcodes.extensions.ATTRIBUTE_VIDEOPRESS_HIDDEN_SRC
import org.wordpress.aztec.plugins.shortcodes.extensions.updateVideoPressThumb
import org.wordpress.aztec.plugins.wpcomments.HiddenGutenbergPlugin
import org.wordpress.aztec.plugins.wpcomments.WordPressCommentsPlugin
import org.wordpress.aztec.source.SourceViewEditText
import org.wordpress.aztec.toolbar.IAztecToolbarClickListener
import org.wordpress.aztec.util.AztecLog
import org.xml.sax.Attributes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val MAX_WORDS = 300

class NewEditor : BaseActivity(),
    AztecText.OnImeBackListener,
    AztecText.OnImageTappedListener,
    AztecText.OnVideoTappedListener,
    AztecText.OnAudioTappedListener,
    AztecText.OnMediaDeletedListener,
    AztecText.OnVideoInfoRequestedListener,
    IAztecToolbarClickListener,
    IHistoryListener,
    ActivityCompat.OnRequestPermissionsResultCallback,
    PopupMenu.OnMenuItemClickListener,
    View.OnTouchListener, View.OnClickListener, ISpellcheckResult {

    companion object {
        private val isRunningTest: Boolean by lazy {
            try {
                Class.forName("androidx.test.espresso.Espresso")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
        }
    }

    private val REQUEST_MEDIA_CAMERA_VIDEO: Int = 2002
    private val REQUEST_MEDIA_VIDEO: Int = 2004
    protected lateinit var aztec: Aztec
    private lateinit var mediaPath: String
    var mediaFile: MediaFile? = null

    private lateinit var invalidateOptionsHandler: Handler
    private lateinit var invalidateOptionsRunnable: Runnable

    private var mediaUploadDialog: AlertDialog? = null
    private var mediaMenu: PopupMenu? = null

    private var mIsKeyboardOpen = false
    private var mHideActionBarOnSoftKeyboardUp = false
    private val mHandler = MyHandler(this@NewEditor)
    private var titleTxt: EditText? = null
    private lateinit var editorGetHelp: TextView
    private val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    private val SPELL_CHECK_FLAG = "show_spell_check_flag"
    private val PERMISSIONS_INIT = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
    )

    private val REQUEST_INIT_PERMISSION = 1

    var imageUri: Uri? = null
    private var articleId: String? = null
    private var thumbnailUrl: String? = null
    private var moderation_status: String? = null
    var mCurrentPhotoPath: String? = null
    var absoluteImagePath: String? = null
    var photoFile: File? = null

    var draftObject: DraftListResult? = null
    var mediaId: String? = null
    var draftId = ""

    val ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111
    val ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE = 1113

    private var mFailedUploads: Map<String, String>? = null
    var title: String? = null
    var content: String? = null

    private var tag: String? = null
    private var cities: String? = null
    private var mToolbar: Toolbar? = null
    private var mLayout: View? = null
    private var imageSelectorType: String? = null

    private var closeEditorImageView: ImageView? = null
    private var publishTextView: TextView? = null
    private var lastSavedTextView: TextView? = null
    var periodicUpdate: Runnable? = null
    private var lastUpdatedTime: Long = 0
    private var spellCheckFlag = false
    private lateinit var wordCount: TextView
    private var imageWordCount = 0
    private var numberOfImages = 0
    private var taggedChallengeId: String? = null
    private var taggedChallengeName: String? = null
    var draftChallengeTagList: ArrayList<Map<String, String>>? = null
    var taggedChallengeMap: HashMap<String, String>? = null
    private lateinit var aztecCoachMark: RelativeLayout
    private lateinit var okgotit: TextView
    val HTML_PATTERN = "(?i)<p.*?>.*?</p>"
    var pattern: Pattern = Pattern.compile(HTML_PATTERN)
    private lateinit var bottomCoachMark: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_editor)
        Utils.pushOpenScreenEvent(
            this@NewEditor,
            "CreateArticleScreen",
            SharedPrefUtils.getUserDetailModel(this).dynamoId + ""
        )
        bottomCoachMark = findViewById(R.id.bottomCoachMark)
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        closeEditorImageView =
            findViewById<View>(R.id.closeEditorImageView) as ImageView
        lastSavedTextView = findViewById<View>(R.id.lastSavedTextView) as TextView
        publishTextView = findViewById<View>(R.id.publishTextView) as TextView
        aztecCoachMark = findViewById(R.id.aztecCoachMark)
        okgotit = findViewById(R.id.okgot)
        val sourceEditor = findViewById<SourceViewEditText>(R.id.source)
        titleTxt = findViewById<View>(R.id.title) as EditText
        editorGetHelp = findViewById(R.id.editor_get_help)
        wordCount = findViewById(R.id.wordCount)
        editor_get_help.setOnClickListener(this)
        closeEditorImageView!!.setOnClickListener(this)
        publishTextView!!.setOnClickListener(this)
        aztecCoachMark.setOnClickListener(this)
        taggedChallengeId = intent.getStringExtra("articleChallengeId")
        taggedChallengeName = intent.getStringExtra("challengeName")

        if (!checkCoachmarkFlagStatus("newEditor_bottom")) {
            aztecCoachMark.visibility = View.VISIBLE
        } else if (!checkCoachmarkFlagStatus("articleEditorPublish")) {
            Handler(Looper.getMainLooper()).postDelayed({
                showToolTip()
            }, 20000)
        }

        if (!taggedChallengeId.isNullOrBlank() && !taggedChallengeName.isNullOrBlank()) {
            draftChallengeTagList = ArrayList()
            taggedChallengeMap = HashMap()
            taggedChallengeMap!![taggedChallengeId!!] = taggedChallengeName!!
            draftChallengeTagList!!.add(taggedChallengeMap!!)
        }
        if (intent.getStringExtra("from") != null && intent.getStringExtra("from") == "draftList") {
            draftObject = intent.getSerializableExtra("draftItem") as DraftListResult
            title = draftObject!!.title
            content = draftObject!!.body
            draftId = draftObject!!.id
            draftChallengeTagList = draftObject!!.tags
            if (StringUtils.isNullOrEmpty(moderation_status)) {
                moderation_status = "0"
            }
            titleTxt!!.setText(title)
            content?.let {
                numberOfImages = getNumberOfImages(it)
                if (numberOfImages > 0) {
                    imageWordCount = 100
                }
            }
            initiatePeriodicDraftSave()
        } else if (intent.getStringExtra("from") != null && intent.getStringExtra("from") == "publishedList") {
            title = intent.getStringExtra("title")
            content = intent.getStringExtra("content")
            tag = intent.getStringExtra("tag")
            cities = intent.getStringExtra("cities")
            thumbnailUrl = intent.getStringExtra("thumbnailUrl")
            articleId = intent.getStringExtra("articleId")
            titleTxt!!.setText(title)
            content?.let {
                numberOfImages = getNumberOfImages(it)
                if (numberOfImages > 0) {
                    imageWordCount = 100
                }
            }
        } else {
            content = ""
            initiatePeriodicDraftSave()
        }

        if (null != draftObject) {
            try {
                showDraftSaveStatus(draftObject!!.updatedTime * 1000)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.d("MC4kException", Log.getStackTraceString(e))
            }
        }
        spellCheckFlag = mFirebaseRemoteConfig.getBoolean(SPELL_CHECK_FLAG)

        mLayout = findViewById(R.id.rootLayout)
        mFailedUploads = HashMap()

        visualEditor.externalLogger = object : AztecLog.ExternalLogger {
            override fun log(message: String) {
            }

            override fun logException(tr: Throwable) {
            }

            override fun logException(tr: Throwable, message: String) {
            }
        }

        val galleryButton = MediaToolbarGalleryButton(aztoolbar)
        galleryButton.setMediaToolbarButtonClickListener(object :
            IMediaToolbarButton.IMediaToolbarClickListener {
            override fun onClick(view: View) {
                mediaMenu = PopupMenu(this@NewEditor, view)
                mediaMenu?.setOnMenuItemClickListener(this@NewEditor)
                mediaMenu?.inflate(R.menu.menu_gallery)
                mediaMenu?.show()
                if (view is ToggleButton) {
                    view.isChecked = false
                }
            }
        })

        val cameraButton = MediaToolbarCameraButton(aztoolbar)
        cameraButton.setMediaToolbarButtonClickListener(object :
            IMediaToolbarButton.IMediaToolbarClickListener {
            override fun onClick(view: View) {
                mediaMenu = PopupMenu(this@NewEditor, view)
                mediaMenu?.setOnMenuItemClickListener(this@NewEditor)
                mediaMenu?.inflate(R.menu.menu_camera)
                mediaMenu?.show()
                if (view is ToggleButton) {
                    view.isChecked = false
                }
            }
        })

        titleTxt!!.onFocusChangeListener = focusListener

        aztec = Aztec.with(visualEditor, sourceEditor, aztoolbar, this)
            .setImageGetter(GlideImageLoader(this))
            .setOnImeBackListener(this)
            .setOnTouchListener(this)
            .setHistoryListener(this)
            .setOnImageTappedListener(this)
            .setOnVideoTappedListener(this)
            .setOnAudioTappedListener(this)
            .setOnMediaDeletedListener(this)
            .setOnVideoInfoRequestedListener(this)
            .addPlugin(WordPressCommentsPlugin(visualEditor))
            .addPlugin(AudioShortcodePlugin())
            .addPlugin(HiddenGutenbergPlugin(visualEditor))
            .addPlugin(galleryButton)
            .addPlugin(cameraButton)

        // initialize the plugins, text & HTML
        if (!isRunningTest) {
            aztec.visualEditor.enableCrashLogging(object :
                AztecExceptionHandler.ExceptionHandlerHelper {
                override fun shouldLog(ex: Throwable): Boolean {
                    return true
                }
            })
            aztec.visualEditor.setCalypsoMode(false)
            aztec.sourceEditor?.setCalypsoMode(false)
            aztec.sourceEditor?.displayStyledAndFormattedHtml(content.toString())
        }

        if (savedInstanceState == null) {
            if (!isRunningTest) {
                aztec.visualEditor.fromHtml(content.toString())
            }
            aztec.initSourceEditorHistory()
        }
        invalidateOptionsHandler = Handler()
        invalidateOptionsRunnable = Runnable { invalidateOptionsMenu() }
        aztec.visualEditor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val wordsLength = countWords(
                    s.toString().replace(
                        "&nbsp;",
                        " "
                    )
                ) + imageWordCount - numberOfImages
                wordCount.visibility = View.VISIBLE
                if (wordsLength <= MAX_WORDS) {
                    wordCount.text = ("" + (300 - wordsLength))
                    wordCount.background =
                        (resources.getDrawable(R.drawable.campaign_detail_red_bg, null))
                } else {
                    wordCount.visibility = View.GONE
                    wordCount.text = "" + (wordsLength - 300)
                    wordCount.background =
                        (resources.getDrawable(R.drawable.short_story_word_count_bg, null))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val wordsLength = countWords(
                    s.toString().replace(
                        "&nbsp;",
                        " "
                    )
                ) + imageWordCount - numberOfImages
                if (wordsLength > 0)
                    wordCount.visibility = View.VISIBLE
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        bottomCoachMark.setOnClickListener(this)
        okgotit.setOnClickListener(this)
    }

    private fun generateAttributesForMedia(
        mediaPath: String,
        isVideo: Boolean
    ): Pair<String, AztecAttributes> {
        val id = Random().nextInt(Integer.MAX_VALUE).toString()
        val attrs = AztecAttributes()
        attrs.setValue(
            "src",
            mediaPath
        ) // Temporary source value.  Replace with URL after uploaded.
        attrs.setValue("id", id)
        attrs.setValue("uploading", "true")

        if (isVideo) {
            attrs.setValue("video", "true")
        }

        return Pair(id, attrs)
    }

    private var focusListener: View.OnFocusChangeListener =
        View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                aztoolbar.enableFormatButtons(false)
            } else {
                aztoolbar.enableFormatButtons(true)
            }
        }

    private fun countWords(s: String): Int {
        val trim = s.trim()
        if (trim.isEmpty()) {
            return 0
        }
        return trim.split("\\s+".toRegex()).size
    }

    private fun getNumberOfImages(content: String): Int {
        val imageTag = "<img"
        var sCountInContent = 0
        val arrrayofContentString = content.split("\\s+".toRegex())
        arrrayofContentString.forEach {
            if (it == imageTag || it.contains(imageTag)) {
                sCountInContent++
            }
        }
        return sCountInContent
    }

    private fun initiatePeriodicDraftSave() {
        periodicUpdate = Runnable {
            mHandler.postDelayed(periodicUpdate, 5000)
            saveDraftsAsync(
                titleFormatting(titleTxt?.text.toString()),
                aztec.visualEditor.toFormattedHtml(),
                draftId
            )
        }
        mHandler.postDelayed(periodicUpdate, 5000)
    }

    private fun showDraftSaveStatus(lastUpdatedTime: Long) {
        val calendar1 = Calendar.getInstance()
        val sdf =
            SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val sdf1 =
            SimpleDateFormat("HH:mm", Locale.US)
        calendar1.timeInMillis = lastUpdatedTime
        val diff = System.currentTimeMillis() - lastUpdatedTime
        if (diff / (1000 * 60 * 60) > 24 && sdf.format(System.currentTimeMillis()) != sdf.format(
                lastUpdatedTime
            )
        ) {
            lastSavedTextView!!.text = getString(
                R.string.editor_last_saved_on,
                DateTimeUtils.getDateFromTimestamp(draftObject!!.updatedTime)
            )
        } else {
            lastSavedTextView!!.text = getString(
                R.string.editor_last_saved_at,
                sdf1.format(calendar1.time)
            )
        }
        lastSavedTextView!!.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        mHandler.removeCallbacksAndMessages(null)
        if (StringUtils.isEmpty(titleTxt?.text.toString().trim()) && StringUtils.isEmpty(
                aztec.visualEditor.toFormattedHtml().trim()
            ) || intent.getStringExtra(
                "from"
            ) != null && intent.getStringExtra(
                "from"
            ) == "publishedList"
        ) {
            super.onBackPressed()
            finish()
        } else {
            if (!ConnectivityUtils.isNetworkEnabled(this)) {
                showToast(getString(R.string.error_network))
                return
            }
            saveDraftRequest(
                titleFormatting(titleTxt?.text.toString()),
                aztec.visualEditor.toFormattedHtml(),
                draftId
            )
        }
    }

    override fun onPause() {
        super.onPause()
        mIsKeyboardOpen = false
    }

    override fun onResume() {
        super.onResume()

        showActionBarIfNeeded()
    }

    override fun onDestroy() {
        super.onDestroy()
        aztec.visualEditor.disableCrashLogging()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        aztec.initSourceEditorHistory()

        savedInstanceState?.let {
            if (savedInstanceState.getBoolean("isMediaUploadDialogVisible")) {
                showMediaUploadDialog()
            }
        }
    }

    /**
     * Returns true if a hardware keyboard is detected, otherwise false.
     */
    private fun isHardwareKeyboardPresent(): Boolean {
        val config = resources.configuration
        var returnValue = false
        if (config.keyboard != Configuration.KEYBOARD_NOKEYS) {
            returnValue = true
        }
        return returnValue
    }

    private fun hideActionBarIfNeeded() {

        val actionBar = supportActionBar
        if (actionBar != null &&
            !isHardwareKeyboardPresent() &&
            mHideActionBarOnSoftKeyboardUp &&
            mIsKeyboardOpen &&
            actionBar.isShowing
        ) {
            actionBar.hide()
        }
    }

    /**
     * Show the action bar if needed.
     */
    private fun showActionBarIfNeeded() {

        val actionBar = supportActionBar
        if (actionBar != null && !actionBar.isShowing) {
            actionBar.show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            // If the WebView or EditText has received a touch event, the keyboard will be displayed and the action bar
            // should hide
            mIsKeyboardOpen = true
            hideActionBarIfNeeded()
        }
        return false
    }

    /**
     * Intercept back button press while soft keyboard is visible.
     */
    override fun onImeBack() {
        mIsKeyboardOpen = false
        showActionBarIfNeeded()
    }

    override fun onRedoEnabled() {
        invalidateOptionsHandler.removeCallbacks(invalidateOptionsRunnable)
        invalidateOptionsHandler.postDelayed(
            invalidateOptionsRunnable,
            resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        )
    }

    override fun onUndoEnabled() {
        invalidateOptionsHandler.removeCallbacks(invalidateOptionsRunnable)
        invalidateOptionsHandler.postDelayed(
            invalidateOptionsRunnable,
            resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        )
    }

    override fun onUndo() {}

    override fun onRedo() {}

    override fun onToolbarCollapseButtonClicked() {
    }

    override fun onToolbarExpandButtonClicked() {
    }

    override fun onToolbarFormatButtonClicked(format: ITextFormat, isKeyboardShortcut: Boolean) {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(aztec.visualEditor, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onToolbarHeadingButtonClicked() {
    }

    override fun onToolbarHtmlButtonClicked() {
        val uploadingPredicate = object : AztecText.AttributePredicate {
            override fun matches(attrs: Attributes): Boolean {
                return attrs.getIndex("uploading") > -1
            }
        }

        val mediaPending =
            aztec.visualEditor.getAllElementAttributes(uploadingPredicate).isNotEmpty()

        if (mediaPending) {
            ToastUtils.showToast(this, R.string.media_upload_dialog_message)
        } else {
            aztec.toolbar.toggleEditorMode()
        }
    }

    override fun onToolbarListButtonClicked() {
    }

    override fun onToolbarMediaButtonClicked(): Boolean {
        return false
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        item?.isChecked = (item?.isChecked == false)
        var intent = Intent(Intent.ACTION_PICK)
        return when (item?.itemId) {
            R.id.take_photo -> {
                imageSelectorType = "CAMERA"
                if (Build.VERSION.SDK_INT >= 23) {
                    if ((ActivityCompat.checkSelfPermission(
                            this@NewEditor,
                            Manifest.permission.CAMERA
                        )
                            != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(
                            this@NewEditor,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                            != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(
                            this@NewEditor,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                            != PackageManager.PERMISSION_GRANTED)
                    ) {
                        requestPermissions()
                    } else {
                        loadImageFromCamera()
                    }
                } else {
                    loadImageFromCamera()
                }
                true
            }
            R.id.take_video -> {
                true
            }
            R.id.gallery_photo -> {
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                intent =
                    Intent.createChooser(intent, getString(R.string.select_image))
                imageSelectorType = "STORAGE"
                if (Build.VERSION.SDK_INT >= 23) {
                    if ((ActivityCompat.checkSelfPermission(
                            this@NewEditor,
                            Manifest.permission.CAMERA
                        )
                            != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(
                            this@NewEditor,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                            != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(
                            this@NewEditor,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                            != PackageManager.PERMISSION_GRANTED)
                    ) {
                        requestPermissions()
                    } else {
                        startActivityForResult(
                            intent,
                            ADD_MEDIA_ACTIVITY_REQUEST_CODE
                        )
                    }
                } else {
                    startActivityForResult(
                        intent,
                        ADD_MEDIA_ACTIVITY_REQUEST_CODE
                    )
                }
                true
            }
            R.id.gallery_video -> {
                true
            }
            else -> false
        }
    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) ||
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Snackbar.make(
                mLayout!!, R.string.permission_storage_rationale,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(
                    R.string.ok
                ) { requestUngrantedPermissions() }
                .show()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            )
        ) {
            Snackbar.make(
                mLayout!!, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(
                    R.string.ok
                ) { requestUngrantedPermissions() }
                .show()
        } else {
            requestUngrantedPermissions()
        }
    }

    private fun requestUngrantedPermissions() {
        val permissionList =
            ArrayList<String>()
        for (i in PERMISSIONS_INIT.indices) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    PERMISSIONS_INIT[i]
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(PERMISSIONS_INIT[i])
            }
        }
        val requiredPermission =
            permissionList.toTypedArray()
        ActivityCompat.requestPermissions(
            this,
            requiredPermission,
            REQUEST_INIT_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_INIT_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(
                    mLayout!!, R.string.permision_available_init,
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                if ("CAMERA" == imageSelectorType) {
                    loadImageFromCamera()
                } else if ("STORAGE" == imageSelectorType) {
                    val intent =
                        Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(
                        intent,
                        ADD_MEDIA_ACTIVITY_REQUEST_CODE
                    )
                }
            } else {
                Snackbar.make(
                    mLayout!!, R.string.permissions_not_granted,
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun loadImageFromCamera() {
        val cameraIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            photoFile = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                Log.i("TAG", "IOException")
            }
            if (photoFile != null) {
                try {
                    cameraIntent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        GenericFileProvider.getUriForFile(
                            this,
                            applicationContext.packageName + ".my.package.name.provider",
                            createImageFile()!!
                        )
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                startActivityForResult(
                    cameraIntent,
                    ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE
                )
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val dir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val image = File.createTempFile(
            imageFileName, // prefix
            ".jpg", // suffix
            dir // directory
        )
        mCurrentPhotoPath = "file:" + image.absolutePath
        absoluteImagePath = image.absolutePath
        return image
    }

    private fun rotateImage(
        source: Bitmap,
        angle: Float
    ): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height, matrix,
            true
        )
    }

    fun sendUploadProfileImageRequest(file: File?) {
        showProgressDialog(getString(R.string.please_wait))
        val MEDIA_TYPE_PNG: MediaType? = "image/png".toMediaTypeOrNull()
        val requestBodyFile: RequestBody = file?.let { RequestBody.create(MEDIA_TYPE_PNG, it) }!!
        val userId: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), 0.toString() + "")
        val imageType: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "2")
        val retro = BaseApplication.getInstance().retrofit
        // prepare call in Retrofit 2.0
        val imageUploadAPI = retro.create(
            ImageUploadAPI::class.java
        )
        val call = imageUploadAPI.uploadImage(
            imageType,
            requestBodyFile
        )
        call.enqueue(object : Callback<ImageUploadResponse?> {
            override fun onResponse(
                call: Call<ImageUploadResponse?>,
                response: Response<ImageUploadResponse?>
            ) {
                try {
                    removeProgressDialog()
                    if (response.body() == null) {
                        showToast(getString(R.string.went_wrong))
                        return
                    }
                    val responseModel = response.body()
                    if (responseModel != null) {
                        if (responseModel.code != 200) {
                            showToast(getString(R.string.toast_response_error))
                            removeProgressDialog()
                            return
                        } else {
                            imageWordCount = 100
                            numberOfImages++
                            mediaFile!!.fileURL = responseModel.data.result.url
                            val (id, attrs) = generateAttributesForMedia(
                                mediaFile!!.fileURL,
                                isVideo = false
                            )
                            aztec.visualEditor.insertImage(
                                BitmapDrawable(mediaFile!!.fileURL),
                                attrs
                            )
                            aztec.visualEditor.loadImages()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@NewEditor,
                        "Error while uploading image",
                        Toast.LENGTH_SHORT
                    ).show()
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                }
            }

            override fun onFailure(
                call: Call<ImageUploadResponse?>,
                t: Throwable
            ) {
                removeProgressDialog()
                FirebaseCrashlytics.getInstance().recordException(t)
                Toast.makeText(
                    this@NewEditor,
                    "Error while uploading image",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("MC4kException", Log.getStackTraceString(t))
            }
        }
        )
    }

    private fun showMediaUploadDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(org.wordpress.aztec.R.string.media_upload_dialog_message))
        builder.setPositiveButton(
            getString(org.wordpress.aztec.R.string.media_upload_dialog_positive),
            null
        )
        mediaUploadDialog = builder.create()
        mediaUploadDialog!!.show()
    }

    override fun onImageTapped(attrs: AztecAttributes, naturalWidth: Int, naturalHeight: Int) {
        //        ToastUtils.showToast(this, "Image tapped!")
    }

    override fun onVideoTapped(attrs: AztecAttributes) {
        val url = if (attrs.hasAttribute(ATTRIBUTE_VIDEOPRESS_HIDDEN_SRC)) {
            attrs.getValue(ATTRIBUTE_VIDEOPRESS_HIDDEN_SRC)
        } else {
            attrs.getValue("src")
        }

        url?.let {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.setDataAndType(Uri.parse(url), "video/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(browserIntent)
                } catch (e: ActivityNotFoundException) {
                    //                    ToastUtils.showToast(this, "Video tapped!")
                }
            }
        }
    }

    override fun onVideoInfoRequested(attrs: AztecAttributes) {
        if (attrs.hasAttribute(ATTRIBUTE_VIDEOPRESS_HIDDEN_ID)) {
            AppLog.d(
                AppLog.T.EDITOR, "Video Info Requested for shortcode " + attrs.getValue(
                ATTRIBUTE_VIDEOPRESS_HIDDEN_ID
            )
            )
            /*
            Here should go the Network request that retrieves additional info about the video.
            See: https://developer.wordpress.com/docs/api/1.1/get/videos/%24guid/
            The response has all info in it. We're skipping it here, and set the poster image directly
            */
            aztec.visualEditor.postDelayed({
                aztec.visualEditor.updateVideoPressThumb(
                    "https://videos.files.wordpress.com/OcobLTqC/img_5786_hd.original.jpg",
                    "https://videos.files.wordpress.com/OcobLTqC/img_5786.m4v",
                    attrs.getValue(ATTRIBUTE_VIDEOPRESS_HIDDEN_ID)
                )
            }, 500)
        }
    }

    override fun onAudioTapped(attrs: AztecAttributes) {
        val url = attrs.getValue("src")
        url?.let {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.setDataAndType(Uri.parse(url), "audio/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(browserIntent)
                } catch (e: ActivityNotFoundException) {
                    //                    ToastUtils.showToast(this, "Audio tapped!")
                }
            }
        }
    }

    override fun onMediaDeleted(attrs: AztecAttributes) {
        val url = attrs.getValue("src")
    }

    override fun onContinuePublish() {
        mHandler.removeCallbacksAndMessages(null)
        val publishObject = PublishDraftObject()
        var finalContent = aztec.visualEditor.toFormattedHtml().replace("\n<br>", "<br>")
        val re = Regex("<div[^>]*>")
        finalContent = re.replace(finalContent, "<p>").replace("<p>\n", "<p>")
        finalContent = finalContent.replace("</div>", "</p>").replace("\n</p>\n", "</p>")
        finalContent = finalContent.replace("\n", "")
        publishObject.body = contentFormatting(finalContent)
        publishObject.title =
            titleFormatting(titleTxt?.text.toString())
        if (intent.getStringExtra("from") != null &&
            intent.getStringExtra("from") == "publishedList" || "4" == moderation_status) {
            // coming from edit published articles
            val intent_1 = Intent(
                this@NewEditor,
                AddArticleTopicsActivityNew::class.java
            )
            publishObject.id = articleId
            intent_1.putExtra("draftItem", publishObject)
            intent_1.putExtra("imageUrl", thumbnailUrl)
            intent_1.putExtra("from", "publishedList")
            intent_1.putExtra("articleId", articleId)
            intent_1.putExtra("tag", tag)
            intent_1.putExtra("cities", cities)
            startActivity(intent_1)
        } else {
            val intent_3 = Intent(
                this@NewEditor,
                AddArticleTopicsActivityNew::class.java
            )
            if (!StringUtils.isNullOrEmpty(draftId)) {
                publishObject.id = draftId
            }
            intent_3.putExtra("draftItem", publishObject)
            intent_3.putExtra("from", "editor")
            if (draftChallengeTagList != null) {
                intent_3.putExtra("tag", Gson().toJson(draftChallengeTagList))
            }
            startActivity(intent_3)
        }
    }

    fun contentFormatting(content: String): String? {
        if (content.startsWith("<p>") && content.endsWith("</p>")) {
            return content
        } else {
            val pTag = "<p>"
            val newString = pTag + content
            var formattedString = newString.replace("\n\n", "</p><p>")
            formattedString = "$formattedString</p>"
            return formattedString
        }
    }

    fun titleFormatting(title: String?): String? {
        return android.text.Html.fromHtml(title).toString()
    }

    private fun saveDraftRequest(
        title: String?,
        body: String?,
        draftId1: String
    ) {
        var body = body
        showProgressDialog(resources.getString(R.string.please_wait))
        val retrofit = BaseApplication.getInstance().retrofit
        val articleDraftAPI = retrofit.create(
            ArticleDraftAPI::class.java
        )
        if (StringUtils.isNullOrEmpty(body)) { // dynamoDB can't handle empty spaces
            body = " "
        }
        if (draftId1.isEmpty()) {
            val call =
                articleDraftAPI.saveDraft(
                    title,
                    body,
                    "0",
                    null,
                    draftChallengeTagList
                )
            call.enqueue(object : Callback<ArticleDraftResponse?> {
                override fun onResponse(
                    call: Call<ArticleDraftResponse?>,
                    response: Response<ArticleDraftResponse?>
                ) {
                    removeProgressDialog()
                    if (response.body() == null) {
                        showToast(getString(R.string.server_went_wrong))
                        showAlertDialog(
                            getString(R.string.draft_oops),
                            getString(R.string.draft_not_saved),
                            OnButtonClicked { finish() })
                        return
                    }
                    try {
                        val responseModel = response.body()
                        if (responseModel!!.code == 200 && Constants.SUCCESS == responseModel.status) {
                            draftId = responseModel.data[0].result.id + ""
                            showToast(getString(R.string.draft_save_success))
                            finish()
                        } else {
                            if (StringUtils.isNullOrEmpty(responseModel.reason)) {
                                showToast(getString(R.string.toast_response_error))
                            } else {
                                showToast(responseModel.reason)
                            }
                        }
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                        showToast(getString(R.string.went_wrong))
                    }
                }

                override fun onFailure(
                    call: Call<ArticleDraftResponse?>,
                    t: Throwable
                ) {
                    FirebaseCrashlytics.getInstance().recordException(t)
                    Log.d("MC4kException", Log.getStackTraceString(t))
                    showToast(getString(R.string.went_wrong))
                }
            })
        } else {
            val saveDraftRequest = SaveDraftRequest()
            saveDraftRequest.title = title
            saveDraftRequest.body = body
            saveDraftRequest.articleType = "0"
            saveDraftRequest.userAgent1 = AppConstants.ANDROID_NEW_EDITOR
            saveDraftRequest.tags = draftChallengeTagList
            val call = articleDraftAPI.updateDrafts(
                AppConstants.LIVE_URL + "v1/articles/" + draftId1, saveDraftRequest
            )
            call.enqueue(object : Callback<ArticleDraftResponse?> {
                override fun onResponse(
                    call: Call<ArticleDraftResponse?>,
                    response: Response<ArticleDraftResponse?>
                ) {
                    removeProgressDialog()
                    if (response.body() == null) {
                        showToast(getString(R.string.went_wrong))
                        return
                    }
                    try {
                        val responseModel = response.body()
                        if (responseModel!!.code == 200 && Constants.SUCCESS == responseModel.status) {
                            draftId = responseModel.data[0].result.id + ""
                            showToast(getString(R.string.draft_save_success))
                            finish()
                        } else {
                            if (StringUtils.isNullOrEmpty(responseModel.reason)) {
                                showToast(getString(R.string.toast_response_error))
                            } else {
                                showToast(responseModel.reason)
                            }
                        }
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Log.d("MC4kException", Log.getStackTraceString(e))
                        showToast(getString(R.string.went_wrong))
                    }
                }

                override fun onFailure(
                    call: Call<ArticleDraftResponse?>,
                    t: Throwable
                ) {
                    FirebaseCrashlytics.getInstance().recordException(t)
                    Log.d("MC4kException", Log.getStackTraceString(t))
                    showToast(getString(R.string.went_wrong))
                }
            })
        }
    }

    private fun saveDraftsAsync(
        title: String?,
        body: String?,
        draftId1: String
    ) {
        var body = body
        if (titleTxt?.text.toString().isEmpty() && content!!.isEmpty()) {
            return
        }
        lastUpdatedTime = System.currentTimeMillis()
        lastSavedTextView!!.visibility = View.VISIBLE
        lastSavedTextView!!.text = getString(R.string.editor_saving)
        val retrofit = BaseApplication.getInstance().retrofit
        val articleDraftAPI = retrofit.create(
            ArticleDraftAPI::class.java
        )
        if (StringUtils.isNullOrEmpty(body)) { // dynamoDB can't handle empty spaces
            body = " "
        }
        if (draftId1.isEmpty()) {
            val call =
                articleDraftAPI.saveDraft(title, body, "0", null, draftChallengeTagList)
            call.enqueue(object : Callback<ArticleDraftResponse?> {
                override fun onResponse(
                    call: Call<ArticleDraftResponse?>,
                    response: Response<ArticleDraftResponse?>
                ) {
                    if (response.body() != null && response.isSuccessful) {
                        val responseModel = response.body()
                        if (responseModel!!.code == 200 && Constants.SUCCESS == responseModel.status) {
                            draftId = responseModel.data[0].result.id + ""
                            showDraftSaveStatus(lastUpdatedTime)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ArticleDraftResponse?>,
                    t: Throwable
                ) {
                    FirebaseCrashlytics.getInstance().recordException(t)
                    Log.d("MC4kException", Log.getStackTraceString(t))
                }
            })
        } else {
            val saveDraftRequest = SaveDraftRequest()
            saveDraftRequest.title = title
            saveDraftRequest.body = body
            saveDraftRequest.articleType = "0"
            saveDraftRequest.userAgent1 = AppConstants.ANDROID_NEW_EDITOR
            saveDraftRequest.tags = draftChallengeTagList
            val call = articleDraftAPI.updateDrafts(
                AppConstants.LIVE_URL + "v1/articles/" + draftId1, saveDraftRequest
            )
            call.enqueue(object : Callback<ArticleDraftResponse?> {
                override fun onResponse(
                    call: Call<ArticleDraftResponse?>,
                    response: Response<ArticleDraftResponse?>
                ) {
                    if (response.body() != null && response.isSuccessful) {
                        val responseModel = response.body()
                        if (responseModel!!.code == 200 && Constants.SUCCESS == responseModel.status) {
                            draftId = responseModel.data[0].result.id + ""
                            showDraftSaveStatus(lastUpdatedTime)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ArticleDraftResponse?>,
                    t: Throwable
                ) {
                    FirebaseCrashlytics.getInstance().recordException(t)
                    Log.d("MC4kException", Log.getStackTraceString(t))
                }
            })
        }
    }

    private fun showCustomToast(bodyWordCount: Int) {
        val toast = Toast.makeText(
            this, getString(
            R.string.article_editor_min_words_body, bodyWordCount
        ), Toast.LENGTH_SHORT
        )
        toast.setGravity(Gravity.CENTER, 0, 0)
        val toastLayout =
            toast.view as LinearLayout
        val toastTV = toastLayout.getChildAt(0) as TextView
        toastTV.gravity = Gravity.CENTER
        toastTV.setTextColor(
            ContextCompat.getColor(
                this,
                com.mycity4kids.R.color.white_color
            )
        )
        toastLayout.background.setColorFilter(
            ContextCompat.getColor(
                this,
                R.color.dark_grey
            ), PorterDuff.Mode.SRC_IN
        )
        toast.show()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.closeEditorImageView -> onBackPressed()
            R.id.okgot -> {
                Utils.shareEventTracking(
                    this,
                    "Create section",
                    "Create_Android",
                    "B_CM_Format_Image"
                )
                bottomCoachMark.visibility = View.GONE
                aztecCoachMark.visibility = View.GONE
                updateCoachmarkFlag("newEditor_bottom", true)
                Handler(Looper.getMainLooper()).postDelayed({
                    showToolTip()
                }, 20000)
            }
            R.id.editor_get_help -> {
                editorGetHelpDialog()
                closeEditorImageView?.setEnabled(false)
                publishTextView?.isEnabled = false
            }
            R.id.publishTextView -> if (StringUtils.isNullOrEmpty(titleTxt?.text.toString())) {
                showToast(getString(R.string.editor_title_empty))
            } else if (titleTxt?.text.toString().length > 150) {
                showToast(getString(R.string.editor_title_char_limit))
            } else if (aztec.visualEditor.text.isEmpty()) {
                showToast(getString(R.string.editor_body_empty))
            } else if (aztec.visualEditor.text.toString().replace(
                    "&nbsp;",
                    " "
                ).split("\\s+".toRegex()).size + imageWordCount - numberOfImages < 299 && !BuildConfig.DEBUG) {
                showCustomToast(
                    aztec.visualEditor.text.toString().replace(
                        "&nbsp;",
                        " "
                    ).split("\\s+".toRegex()).size + imageWordCount - numberOfImages
                )
            } else {
                if (intent.getStringExtra("from") != null && intent.getStringExtra("from") == "publishedList") {
                    launchSpellCheckDialog()
                } else {
                    saveDraftBeforePublishRequest(
                        titleTxt?.text.toString(),
                        aztec.visualEditor.toFormattedHtml(), draftId
                    )
                }
            }
        }
    }

    fun editorGetHelpDialog() {
        val dialog = Dialog(this)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.editor_get_help)
        dialog.setCancelable(false)
        val mailBtn = dialog.findViewById<TextView>(R.id.editor_help_mail)
        mailBtn.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "support@momspresso.com", null
            )
            )
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
            dialog.cancel()
        }
        val cancelBtnx = dialog.findViewById<TextView>(R.id.editor_help_cancel)
        cancelBtnx.setOnClickListener {
            closeEditorImageView?.setEnabled(true)
            publishTextView?.isEnabled = true
            dialog.dismiss()
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun saveDraftBeforePublishRequest(
        title: String,
        body: String,
        draftId1: String
    ) {
        showProgressDialog(resources.getString(R.string.please_wait))
        val retrofit = BaseApplication.getInstance().retrofit
        // prepare call in Retrofit 2.0
        val articleDraftAPI =
            retrofit.create(ArticleDraftAPI::class.java)
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog()
            showToast(getString(R.string.error_network))
            return
        }
        if (draftId1.isEmpty()) {
            val call =
                articleDraftAPI.saveDraft(title, body, "0", null, draftChallengeTagList)
            call.enqueue(saveDraftBeforePublishResponseListener)
        } else {
            val saveDraftRequest = SaveDraftRequest()
            saveDraftRequest.title = title
            saveDraftRequest.body = body
            saveDraftRequest.articleType = "0"
            saveDraftRequest.userAgent1 = AppConstants.ANDROID_NEW_EDITOR
            saveDraftRequest.tags = draftChallengeTagList
            val call = articleDraftAPI.updateDrafts(
                AppConstants.LIVE_URL + "v1/articles/" + draftId1,
                saveDraftRequest
            )
            call.enqueue(saveDraftBeforePublishResponseListener)
        }
    }

    private val saveDraftBeforePublishResponseListener: Callback<ArticleDraftResponse?> =
        object : Callback<ArticleDraftResponse?> {
            override fun onResponse(
                call: Call<ArticleDraftResponse?>,
                response: Response<ArticleDraftResponse?>
            ) {
                removeProgressDialog()
                if (response.body() == null) {
                    showToast(getString(R.string.server_went_wrong))
                    return
                }
                try {
                    val responseModel = response.body()
                    if (responseModel!!.code == 200 && Constants.SUCCESS == responseModel.status) {
                        draftId = responseModel.data[0].result.id + ""
                        mHandler.removeCallbacksAndMessages(null)
                        if (spellCheckFlag) {
                            val spellIntent = Intent(
                                this@NewEditor,
                                SpellCheckActivity::class.java
                            )
                            spellIntent.putExtra("draftId", draftId)
                            spellIntent.putExtra(
                                "titleContent",
                                titleTxt?.text.toString()
                            )
                            spellIntent.putExtra(
                                "bodyContent",
                                aztec.visualEditor.toFormattedHtml()
                            )
                            startActivity(spellIntent)
                        } else {
                            launchSpellCheckDialog()
                        }
                    } else {
                        if (StringUtils.isNullOrEmpty(responseModel.reason)) {
                            showToast(getString(R.string.toast_response_error))
                        } else {
                            showToast(responseModel.reason)
                        }
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Log.d("MC4kException", Log.getStackTraceString(e))
                    showToast(getString(R.string.went_wrong))
                }
            }

            override fun onFailure(
                call: Call<ArticleDraftResponse?>,
                t: Throwable
            ) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Log.d("MC4kException", Log.getStackTraceString(t))
                showToast(getString(R.string.went_wrong))
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mediaFile = MediaFile()
        mediaId = System.currentTimeMillis().toString()
        mediaFile!!.setMediaId(mediaId)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ADD_MEDIA_ACTIVITY_REQUEST_CODE -> {
                    if (data == null) {
                        return
                    }
                    imageUri = data.data
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            val imageBitmap =
                                MediaStore.Images.Media.getBitmap(
                                    this@NewEditor.getContentResolver(),
                                    imageUri
                                )
                            var actualHeight = imageBitmap.height.toFloat()
                            var actualWidth = imageBitmap.width.toFloat()
                            if (actualWidth < 720) {
                                showToast(getString(R.string.upload_min_width))
                                return
                            }
                            val maxHeight = 1300f
                            val maxWidth = 720f
                            var imgRatio = actualWidth / actualHeight
                            val maxRatio = maxWidth / maxHeight
                            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                                if (imgRatio < maxRatio) { // adjust width according to maxHeight
                                    imgRatio = maxHeight / actualHeight
                                    actualWidth = imgRatio * actualWidth
                                    actualHeight = maxHeight
                                } else if (imgRatio > maxRatio) { // adjust height according to maxWidth
                                    imgRatio = maxWidth / actualWidth
                                    actualHeight = imgRatio * actualHeight
                                    actualWidth = maxWidth
                                } else {
                                    actualHeight = maxHeight
                                    actualWidth = maxWidth
                                }
                            }
                            val finalBitmap = Bitmap.createScaledBitmap(
                                imageBitmap,
                                actualWidth.toInt(),
                                actualHeight.toInt(),
                                true
                            )
                            val stream = ByteArrayOutputStream()
                            finalBitmap.compress(Bitmap.CompressFormat.PNG, 75, stream)
                            val path =
                                MediaStore.Images.Media.insertImage(
                                    this@NewEditor.getContentResolver(),
                                    finalBitmap,
                                    "Title" + System.currentTimeMillis(),
                                    null
                                )
                            val imageUriTemp = Uri.parse(path)
                            val file2 = FileUtils.getFile(this, imageUriTemp)
                            sendUploadProfileImageRequest(file2)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE -> {

                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            var imageBitmap =
                                MediaStore.Images.Media.getBitmap(
                                    contentResolver,
                                    Uri.parse(mCurrentPhotoPath)
                                )
                            val ei =
                                ExifInterface(absoluteImagePath)
                            val orientation = ei.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED
                            )
                            when (orientation) {
                                ExifInterface.ORIENTATION_ROTATE_90 -> imageBitmap =
                                    rotateImage(imageBitmap, 90f)
                                ExifInterface.ORIENTATION_ROTATE_180 -> imageBitmap =
                                    rotateImage(imageBitmap, 180f)
                                ExifInterface.ORIENTATION_ROTATE_270 -> imageBitmap =
                                    rotateImage(imageBitmap, 270f)
                                ExifInterface.ORIENTATION_NORMAL -> {
                                }
                                else -> {
                                }
                            }
                            var actualHeight = imageBitmap.height.toFloat()
                            var actualWidth = imageBitmap.width.toFloat()
                            val maxHeight = 1300f
                            val maxWidth = 720f
                            var imgRatio = actualWidth / actualHeight
                            val maxRatio = maxWidth / maxHeight
                            // float compressionQuality = 0.5;//50 percent compression
                            if (actualWidth < 720) {
                                showToast(getString(R.string.upload_min_width))
                                return
                            }
                            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                                if (imgRatio < maxRatio) { // adjust width according to maxHeight
                                    imgRatio = maxHeight / actualHeight
                                    actualWidth = imgRatio * actualWidth
                                    actualHeight = maxHeight
                                } else if (imgRatio > maxRatio) { // adjust height according to maxWidth
                                    imgRatio = maxWidth / actualWidth
                                    actualHeight = imgRatio * actualHeight
                                    actualWidth = maxWidth
                                } else {
                                    actualHeight = maxHeight
                                    actualWidth = maxWidth
                                }
                            }
                            val finalBitmap =
                                Bitmap.createScaledBitmap(
                                    imageBitmap,
                                    actualWidth.toInt(),
                                    actualHeight.toInt(),
                                    true
                                )
                            val bytes =
                                ByteArrayOutputStream()
                            finalBitmap.compress(
                                Bitmap.CompressFormat.JPEG,
                                75,
                                bytes
                            )
                            val bitmapData = bytes.toByteArray()
                            val fos = FileOutputStream(photoFile)
                            fos.write(bitmapData)
                            fos.flush()
                            fos.close()
                            imageUri = Uri.fromFile(photoFile)
                            val file2 =
                                FileUtils.getFile(
                                    this,
                                    imageUri
                                )
                            sendUploadProfileImageRequest(file2)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                REQUEST_MEDIA_CAMERA_VIDEO -> {
                    mediaPath = data?.data.toString()
                }
                REQUEST_MEDIA_VIDEO -> {
                }
            }
        }
    }

    fun launchSpellCheckDialog() {
        val spellCheckDialogFragment = SpellCheckDialogFragment()
        val fm = supportFragmentManager
        val _args = Bundle()
        _args.putString("activity", "dashboard")
        spellCheckDialogFragment.arguments = _args
        spellCheckDialogFragment.isCancelable = true
        spellCheckDialogFragment.show(fm, "Spell Check")
    }

    private class MyHandler internal constructor(activity: NewEditor) :
        Handler() {
        private val mActivity: WeakReference<NewEditor>
        override fun handleMessage(msg: Message) {}

        init {
            mActivity = WeakReference(activity)
        }
    }

    private fun showToolTip() {
        var isTimeUp = false
        val tooltip = SimpleTooltip.Builder(this)
            .anchorView(publishTextView)
            .contentView(R.layout.article_publish_tooltip_layout)
            .margin(0f)
            .padding(0f)
            .arrowColor(ContextCompat.getColor(this, R.color.tooltip_border))
            .gravity(Gravity.BOTTOM)
            .arrowWidth(50f)
            .animated(false)
            .dismissOnOutsideTouch(false)
            .transparentOverlay(true)
            .onDismissListener {
                updateCoachmarkFlag("articleEditorPublish", true)
                if (!isTimeUp) {
                    Utils.shareEventTracking(
                        this,
                        "Create section",
                        "Create_Android",
                        "B_TT_Publish"
                    )
                }
            }
            .build()
        tooltip.show()

        Handler(Looper.getMainLooper()).postDelayed({
            isTimeUp = true
            tooltip.dismiss()
        }, 3000)
    }
}
