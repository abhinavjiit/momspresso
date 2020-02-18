package com.mycity4kids.ui.campaign.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Window
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import com.crashlytics.android.Crashlytics
import com.kelltontech.ui.BaseActivity
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.preference.SharedPrefUtils

class CampaignHowToVideoActivity : BaseActivity() {

    private var mediaController: MediaController? = null

    private lateinit var videoView: VideoView
    private lateinit var crossImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.campaign_howto_video_activity)
        videoView = findViewById(R.id.videoView)
        crossImageView = findViewById(R.id.crossImageView)


        crossImageView.setOnClickListener {
            videoView.stopPlayback()
            videoView.setMediaController(null)
            mediaController = null
            finish()
        }

        SharedPrefUtils.setDemoVideoSeen(BaseApplication.getAppContext(), true)
//        playVideo()
    }

    override fun onResume() {
        super.onResume()
        playVideo()
    }

    override fun onPause() {
        super.onPause()
        videoView.stopPlayback()
        videoView.setMediaController(null)
        mediaController = null
    }

    private fun setController() {
        if (mediaController == null) {
            mediaController = object : MediaController(this) {
                override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
                    if (event?.keyCode == KeyEvent.KEYCODE_BACK)
                        finish()

                    return super.dispatchKeyEvent(event)
                }
            }
            mediaController?.setAnchorView(videoView)
            videoView.setMediaController(mediaController)
        }
    }

    private fun playVideo() {
        try {
            showProgressDialog(resources.getString(R.string.please_wait))
            val videoUrl = "https://static.momspresso.com/mymoney/assets/how-to.mp4"
            val video = Uri.parse(videoUrl)
            videoView.setVideoURI(video)
            setController()
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Log.d("MC4kException", Log.getStackTraceString(e))
        }
        videoView.start()
        videoView.setOnPreparedListener {
            removeProgressDialog()
        }
        videoView.setOnCompletionListener { mp ->
            mp.isLooping = false
            finish()
        }
    }
}