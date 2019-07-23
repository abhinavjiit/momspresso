package com.mycity4kids.ui.campaign.activity;

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Window
import android.widget.FrameLayout
import android.widget.TextView
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.campaign.PaymentModeListModal
import com.mycity4kids.ui.campaign.fragment.CampaignAddProofFragment
import com.mycity4kids.ui.campaign.fragment.CampaignCongratulationFragment
import com.mycity4kids.ui.campaign.fragment.CampaignPaymentModesFragment
import com.mycity4kids.ui.fragment.CampaignDetailFragment
import com.mycity4kids.ui.fragment.CampaignListFragment
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CampaignContainerActivity : BaseActivity(), CampaignAddProofFragment.SubmitListener, CampaignCongratulationFragment.SubmitListener, CampaignPaymentModesFragment.SubmitListener {


    override fun onPaymentModeDone(paymentModeId: Int) {

    }

    override fun updateUi(response: Response?) {
    }

    override fun congratulateScreenDone() {
        onBackPressed()

    }

    override fun proofSubmitDone() {
        fetchPaymentModes()
    }

    private lateinit var toolbarTitle: TextView
    private var totalPay: Double? = null
    private var name: String? = null
    private var id: Int = 0
    private var campaignDetailFragment: CampaignDetailFragment? = null
    private var campaignListFragment: CampaignListFragment? = null
    private var defaultdata: String? = null
    private var deeplinkCampaignId: Int = 0
    private lateinit var notificationCampaignId: String
    private lateinit var notificationCampaignSubmitProof: String
    private var submitProofCampaignId: Int = 0
    private lateinit var panCardNotification: String
    private var arrayList = mutableListOf<Int>()
    private var comingFrom: String = "deeplink"
    private var fromNotification: Boolean = false
    private lateinit var root: FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Activity", "onCreate")

        setContentView(R.layout.activity_campaign_container)
        root = findViewById(R.id.container)
        (application as BaseApplication).activity = this

        deeplinkCampaignId = intent.getIntExtra("campaignID", -1)

        fromNotification = if (intent.hasExtra("fromNotification")) {//fromNotification
            intent.getBooleanExtra("fromNotification", false)
        } else {
            false
        }

        if (intent.hasExtra("campaign_id") && intent.hasExtra("campaign_detail")) {
            notificationCampaignId = intent.getStringExtra("campaign_id")

            comingFrom = "campaign_detail"

        } else {
            notificationCampaignId = ""
        }

        if (intent.hasExtra("campaign_Id") && intent.hasExtra("campaign_submit_proof")) {
            notificationCampaignSubmitProof = intent.getStringExtra("campaign_Id")
            comingFrom = "campaign_submit_proof"

        } else {
            notificationCampaignSubmitProof = ""
        }



        if (!notificationCampaignSubmitProof.equals("", true)) {
            deeplinkCampaignId = notificationCampaignSubmitProof.toInt()
        }

        if (!notificationCampaignId.equals("", true)) {
            deeplinkCampaignId = notificationCampaignId.toInt()
        }

        if ((deeplinkCampaignId == -1 || deeplinkCampaignId == 0) && comingFrom.equals("deeplink")) {
            campaignListFragment()
        } else if (comingFrom.equals("campaign_detail")) {
            addCampaginDetailFragment(deeplinkCampaignId)

        } else if (comingFrom.equals("campaign_submit_proof")) {
            addAddProofFragment(deeplinkCampaignId, arrayList as ArrayList<Int>)
        } else {
            addCampaginDetailFragment(deeplinkCampaignId)
        }

    }

    override fun onResume() {
        super.onResume()
        (application as BaseApplication).view = root
    }

    private fun campaignListFragment() {
        campaignListFragment = CampaignListFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.container, campaignListFragment,
                CampaignListFragment::class.java.simpleName).addToBackStack("campaignListFragment")
                .commit()
    }


    fun addCampaginDetailFragment(id: Int) {
        campaignDetailFragment = CampaignDetailFragment.newInstance(id, fromNotification)
        supportFragmentManager.beginTransaction().replace(R.id.container, campaignDetailFragment,
                CampaignDetailFragment::class.java.simpleName).addToBackStack("campaignDetailFragment")
                .commit()
    }


    fun addAddProofFragment(id: Int, deliverableTypeList: ArrayList<Int>) {
        var campaignAddProofFragment = CampaignAddProofFragment.newInstance(id, deliverableTypeList)
        supportFragmentManager.beginTransaction().replace(R.id.container, campaignAddProofFragment,
                CampaignAddProofFragment::class.java.simpleName).addToBackStack("campaignAddProofFragment")
                .commit()
    }

    private fun addPaymantMode() {
        var campaignPaymentModesFragment = CampaignPaymentModesFragment.newInstance(isComingFromRewards = false)
        supportFragmentManager.beginTransaction().replace(R.id.container, campaignPaymentModesFragment,
                CampaignPaymentModesFragment::class.java.simpleName).addToBackStack("CampaignPaymentModesFragment")
                .commit()
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        val currentFragment: Fragment
        currentFragment = supportFragmentManager.findFragmentById(R.id.container)
        if (fragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            if (currentFragment is CampaignCongratulationFragment) {
                if (fromNotification) {
                    var count = fragmentManager.backStackEntryCount
                    for (i in 0..count) {
                        fragmentManager.popBackStack()
                    }

                    campaignListFragment = CampaignListFragment.newInstance()
                    supportFragmentManager.beginTransaction().add(R.id.container, campaignListFragment,
                            CampaignListFragment::class.java.simpleName).addToBackStack("campaignListFragment")
                            .commit()
                } else {
                    for (i in fragmentManager.backStackEntryCount downTo 2) {
                        supportFragmentManager.popBackStack()
                    }
                }
            } else {
                super.onBackPressed()
            }
        }
    }


    fun setTotalPayOut(totalPayOut: Double) {
        totalPay = totalPayOut
    }

    fun getTotalPayOut(): Double? {
        return totalPay
    }

    fun setNameSlug(nameSlug: String) {
        name = nameSlug
    }

    fun getNameSlug(): String {
        return name!!
    }

    fun setIdCamp(Id: Int) {
        id = Id
    }

    fun getIdCamp(): Int {
        return id
    }

    fun fetchPaymentModes() {
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).getPaymentModes().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<PaymentModeListModal>> {
            override fun onComplete() {
                removeProgressDialog()
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(response: BaseResponseGeneric<PaymentModeListModal>) {

                if (response.data!!.result.default != null) {

                    var campaignCongratulationFragment = CampaignCongratulationFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(R.id.container, campaignCongratulationFragment,
                            CampaignCongratulationFragment::class.java.simpleName).addToBackStack("CampaignCongratulationFragment")
                            .commit()
                } else {
                    showRewardDialog()

                }


            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
                Log.e("exception in error", e.message.toString())


            }


        })

    }

    private fun showRewardDialog() {
        if (this@CampaignContainerActivity != null) {
            val dialog = Dialog(this)
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.proof_submitted_congo_screen)
            dialog.setCancelable(false)
            val okBtn = dialog.findViewById<TextView>(R.id.click_ok)
            okBtn.setOnClickListener {
                addPaymantMode()
                dialog.cancel()
            }
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    }

}
