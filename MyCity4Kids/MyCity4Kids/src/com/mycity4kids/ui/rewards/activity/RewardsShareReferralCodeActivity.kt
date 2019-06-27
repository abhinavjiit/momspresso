package com.mycity4kids.ui.rewards.activity

import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.os.Bundle
import android.util.Log
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.campaignmodels.ReferralCodeResult
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.retrofitAPIsInterfaces.RewardsAPI
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.share_referral_code.*
import java.util.*

class RewardsShareReferralCodeActivity : BaseActivity() {
    override fun updateUi(response: Response?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.share_referral_code)

        layoutFacebook.setOnClickListener {
            shareViaFacebook()
        }

        layoutWhatsapp.setOnClickListener {
            shareViaWhatsApp()
        }

        imageShare.setOnClickListener {
            openSharingDialog()
        }

        fetReferralCode()
        super.onCreate(savedInstanceState)
    }

    fun openSharingDialog(){
        val i = Intent(android.content.Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Register on Momspresso MyMoney with referral code ${textCode.text} and earn Rs. " +
                        "25. + Participate in campaigns by brands you love and use. Start earning MyMoney!")
        i.putExtra(android.content.Intent.EXTRA_TEXT, "Participate in campaigns by brands you love and use. Start earning MyMoney! \n https://www.momspresso.com/mymoney-register?referrer=${textCode.text}")
        startActivity(Intent.createChooser(i, "Share via"))
    }

    fun shareViaFacebook(){
        val intent = getPackageManager().getLaunchIntentForPackage("com.facebook.katana")
        if (intent != null) {
            // The application exists
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.setPackage("com.facebook.katana")

            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    "Register on Momspresso MyMoney with referral code ${textCode.text} and earn Rs. " +
                            "25. + Participate in campaigns by brands you love and use. Start earning MyMoney!")
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Participate in campaigns by brands you love and use. Start earning MyMoney! \n https://www.momspresso.com/mymoney-register?referrer=${textCode.text}")
            // Start the specific social application
            startActivity(shareIntent)
        } else {
            // The application does not exist
            // Open GooglePlay or use the default system picker
        }
    }

    fun shareViaWhatsApp() {
        val whatsappIntent = Intent(ACTION_SEND)
        whatsappIntent.setType("text/plain")
        whatsappIntent.setPackage("com.whatsapp")
        whatsappIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Register on Momspresso MyMoney with referral code ${textCode.text} and earn Rs. " +
                        "25. + Participate in campaigns by brands you love and use. Start earning MyMoney!")
        whatsappIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Participate in campaigns by brands you love and use. Start earning MyMoney! \n https://www.momspresso.com/mymoney-register?referrer=${textCode.text}")
        try {
            Objects.requireNonNull(this@RewardsShareReferralCodeActivity).startActivity(whatsappIntent)
        } catch (ex: android.content.ActivityNotFoundException) {
            //startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.whatsapp")))
        }
    }

    /*fetch data from server*/
    private fun fetReferralCode() {
        //var userId = com.mycity4kids.preference.SharedPrefUtils.getUserDetailModel(this@RewardsShareReferralCodeActivity)?.dynamoId
        var userId = "6351276833b94023ad18e7fab5b89199"
        if (userId != null) {
            showProgressDialog(resources.getString(R.string.please_wait))
            BaseApplication.getInstance().retrofit.create(RewardsAPI::class.java).getReferralCode(userId!!).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<ReferralCodeResult>> {
                override fun onComplete() {
                    removeProgressDialog()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: BaseResponseGeneric<ReferralCodeResult>) {
                    if (response != null && response.code == 200 && Constants.SUCCESS == response.status && response.data != null && response!!.data!!.result!=null && !response!!.data!!.result.referral_code.isNullOrEmpty()) {
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
