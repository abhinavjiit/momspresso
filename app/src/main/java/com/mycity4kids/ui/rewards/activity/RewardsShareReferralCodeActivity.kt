package com.mycity4kids.ui.rewards.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.campaignmodels.ReferralCodeResult
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.referral_code.*

class RewardsShareReferralCodeActivity : BaseActivity() {
    private lateinit var backText: TextView
    private lateinit var layoutFacebook: RelativeLayout
    private lateinit var layoutWhatsApp: RelativeLayout
    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null
    private lateinit var root: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.referral_code)
        root = findViewById(R.id.root)
        (application as BaseApplication).view = root
        (application as BaseApplication).activity = this

        layoutFacebook = findViewById(R.id.layoutFacebook)
        layoutWhatsApp = findViewById(R.id.layoutWhatsApp)
        myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?


        backText = findViewById(R.id.backToolbar)
        backText.setOnClickListener {
            finish()
        }

        layoutFacebook.setOnClickListener {
            shareViaFacebook()
        }

        layoutWhatsApp.setOnClickListener {
            shareViaWhatsApp()
        }

        imageShare.setOnClickListener {
            openSharingDialog()
        }
        textCode.setOnClickListener {

            myClip = ClipData.newPlainText("text", textCode.text.toString())
            myClipboard?.setPrimaryClip(myClip);

            Toast.makeText(this, "ReferralCode Copied", Toast.LENGTH_SHORT).show()

        }

        fetReferralCode()
        super.onCreate(savedInstanceState)
    }

    fun openSharingDialog() {
        val i = Intent(android.content.Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Register on Momspresso MyMoney with referral code ${textCode.text} and earn Rs. " +
                        "25. + Participate in campaigns by brands you love and use. Start earning MyMoney!")
        i.putExtra(android.content.Intent.EXTRA_TEXT, "Participate in campaigns by brands you love and use. Start earning MyMoney! \n https://www.momspresso.com/usersprofile/rewardsform?referrer=${textCode.text}")
        startActivity(Intent.createChooser(i, "Share via"))
    }

    fun shareViaFacebook() {
        var facebookAppFound = false
        var shareIntent = Intent(android.content.Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Register on Momspresso MyMoney with referral code ${textCode.text} and earn Rs. " +
                "25. + Participate in campaigns by brands you love and use. Start earning MyMoney!")
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Participate in campaigns by brands you love and use. Start earning MyMoney! \n https://www.momspresso.com/usersprofile/rewardsform?referrer=${textCode.text}")

        val pm = getPackageManager()
        val activityList = pm.queryIntentActivities(shareIntent, 0)
        for (app in activityList) {
            if (app.activityInfo.packageName.contains("com.facebook.katana")) {
                val activityInfo = app.activityInfo
                val name = ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name)
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                shareIntent.component = name
                facebookAppFound = true
                break
            }
        }
        if (!facebookAppFound) {
            val sharerUrl = "https://www.facebook.com/sharer/sharer.php?u= " +
                    " https://www.momspresso.com/usersprofile/rewardsform?referrer=${textCode.text}"
            shareIntent = Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl))
        }
        startActivity(shareIntent)
    }


    fun shareViaWhatsApp() {
        val whatsappIntent = Intent(ACTION_SEND)
        whatsappIntent.setType("text/plain")
        whatsappIntent.setPackage("com.whatsapp")
        whatsappIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Register on Momspresso MyMoney with referral code ${textCode.text} and earn Rs. " +
                        "25. + Participate in campaigns by brands you love and use. Start earning MyMoney!")
        whatsappIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Participate in campaigns by brands you love and use. Start earning MyMoney! \n https://www.momspresso.com/usersprofile/rewardsform?referrer=${textCode.text}")
        try {
            startActivity(Intent.createChooser(whatsappIntent, "Share Url:"))
            //  Objects.requireNonNull(this@RewardsShareReferralCodeActivity).startActivity(whatsappIntent)
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(this@RewardsShareReferralCodeActivity, getString(R.string.moderation_or_share_whatsapp_not_installed), Toast.LENGTH_SHORT).show()

        }
    }

    /*fetch data from server*/
    private fun fetReferralCode() {
        //var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(this@RewardsShareReferralCodeActivity)?.dynamoId
        var userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).dynamoId
        if (userId != null) {
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).getReferralCode(userId!!).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<ReferralCodeResult>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: BaseResponseGeneric<ReferralCodeResult>) {
                    if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null && response!!.data!!.result != null && !response!!.data!!.result.referral_code.isNullOrEmpty()) {
                        textCode.text = response.data!!.result.referral_code
                    }
                }

                override fun onError(e: Throwable) {
                    removeProgressDialog()
                    Log.e("exception in error", e.message.toString())
                }
            })
        }
    }

}
