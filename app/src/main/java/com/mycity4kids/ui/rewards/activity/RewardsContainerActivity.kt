package com.mycity4kids.ui.rewards.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.facebook.CallbackManager
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.constants.Constants
import com.mycity4kids.facebook.FacebookUtils
import com.mycity4kids.interfaces.IFacebookEvent
import com.mycity4kids.models.campaignmodels.ProofPostModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import com.mycity4kids.ui.campaign.fragment.CampaignPaymentModesFragment
import com.mycity4kids.ui.campaign.fragment.PanCardDetailsSubmissionFragment
import com.mycity4kids.ui.fragment.ChangePreferredLanguageDialogFragment
import com.mycity4kids.ui.rewards.fragment.ProfileInfoFragment
import com.mycity4kids.ui.rewards.fragment.RewardsFamilyInfoFragment
import com.mycity4kids.ui.rewards.fragment.RewardsPersonalInfoFragment
import com.mycity4kids.ui.rewards.fragment.RewardsSocialInfoFragment
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RewardsContainerActivity : BaseActivity(),
        RewardsPersonalInfoFragment.SaveAndContinueListener,
        ProfileInfoFragment.SaveAndContinueListener,
        RewardsSocialInfoFragment.SubmitListener, CampaignPaymentModesFragment.SubmitListener, PanCardDetailsSubmissionFragment.SubmitListener, IFacebookEvent {
    private var Id: Int = -1
    private var referralCode: String = " "
    override fun onPanCardDone() {
        this@RewardsContainerActivity.finish()
    }

    override fun onPaymentModeDone(paymentModeId: Int) {
        if (pageLimit == 4) {
            Id = paymentModeId
            if (Id != -1) {
                postApiForDefaultPaymantMode(Id)
            }
            if (Id == -1) {
                this@RewardsContainerActivity.finish()
            }
        } else {
            addPancardDetailFragment()
        }
    }

    override fun onFacebookEventReceived(response: String?) {
        if (rewardsSocialInfoFragment != null) {
            rewardsSocialInfoFragment!!.updateFaceBookView()
        }
    }

    override fun socialOnSubmitListener() {
        pageLimit!! + 1
        addPaymentModesFragment()
    }

    override fun profileOnSaveAndContinue() {
        addSocialFragment()
    }

    private var callbackManager: CallbackManager? = null
    private var rewardsPersonalInfoFragment: RewardsPersonalInfoFragment? = null
    private var profileInfoFragment: ProfileInfoFragment? = null
    private var rewardsFamilyInfoFragment: RewardsFamilyInfoFragment? = null
    private var rewardsSocialInfoFragment: RewardsSocialInfoFragment? = null
    private var paymentModesFragment: CampaignPaymentModesFragment? = null
    private var panCardDetailsSubmissionFragment: PanCardDetailsSubmissionFragment? = null
    private var pageLimit: Int? = null
    private var pageNumber: Int? = null
    private var isComingfromCampaign = false
    private var showProfileInfo = false
    private lateinit var root: RelativeLayout
    private lateinit var toolbarTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards_container)
        root = findViewById(R.id.root)
        toolbarTitle = findViewById(R.id.toolbarTitle)
        (application as BaseApplication).view = root

        (application as BaseApplication).activity = this

        if (intent != null) {
            if (intent.hasExtra("pageLimit")) {
                pageLimit = intent.getIntExtra("pageLimit", 5)
            } else {
                pageLimit = 5
            }
            if (intent.hasExtra("referral")) {
                referralCode = intent.getStringExtra("referral")
            }
            if (intent.hasExtra("pageNumber")) {
                pageNumber = intent.getIntExtra("pageNumber", 1)
            } else {
                pageNumber = 1
            }

            if (intent.hasExtra("isComingfromCampaign")) {
                isComingfromCampaign = intent.getBooleanExtra("isComingfromCampaign", false)
            } else {
                isComingfromCampaign = false
            }
            if (intent.hasExtra("showProfileInfo")) {
                showProfileInfo = intent.getBooleanExtra("showProfileInfo", false)
            } else {
                showProfileInfo = false
            }
        }

        if (showProfileInfo) {
            addProfileInfoFragment()
        } else {
            if (pageNumber == 1) {
                addProfileFragment()
            } else if (pageNumber == 3) {
                addSocialFragment()
            } else if (pageNumber == 4) {
                addPaymentModesFragment()
            } else if (pageNumber == 5) {
                addPancardDetailFragment()
            }
        }

        callbackManager = CallbackManager.Factory.create()

        /*initialize XML components*/
        initializeXMLComponents()
    }

    private fun initializeXMLComponents() {
        findViewById<TextView>(R.id.langTextView).setOnClickListener {
            val changePreferredLanguageDialogFragment = ChangePreferredLanguageDialogFragment()
            val fm = supportFragmentManager
            val _args = Bundle()
            _args.putString("activity", "dashboard")
            changePreferredLanguageDialogFragment.arguments = _args
            changePreferredLanguageDialogFragment.isCancelable = true
            changePreferredLanguageDialogFragment.show(fm, "Choose video option")
        }

        toolbarTitle.setOnClickListener {
            onBackPressed()
        }
    }

    private fun addProfileFragment() {
        if (pageLimit!! >= 1) {
            rewardsPersonalInfoFragment = RewardsPersonalInfoFragment.newInstance(isComingFromRewards = true, isComingfromCampaign = isComingfromCampaign, referralCode = referralCode)
            val rewardFrag = rewardsPersonalInfoFragment as Fragment
            supportFragmentManager.beginTransaction().replace(R.id.container, rewardFrag,
                    RewardsPersonalInfoFragment::class.java.simpleName)
                    .commit()
        } else {
            finish()
        }
    }

    private fun addProfileInfoFragment() {
        toolbarTitle.setText(resources.getString(R.string.personal_info))
        profileInfoFragment = ProfileInfoFragment()
        val rewardFrag = profileInfoFragment as Fragment
        supportFragmentManager.beginTransaction().replace(R.id.container, rewardFrag,
                ProfileInfoFragment::class.java.simpleName)
                .commit()
    }

    private fun addSocialFragment() {
        if (pageLimit!! >= 3) {
            toolbarTitle.setText(resources.getString(R.string.social_accounts))
            rewardsSocialInfoFragment = RewardsSocialInfoFragment.newInstance(isComingFromRewards = true)
            val rewardFrag = rewardsSocialInfoFragment as Fragment
            supportFragmentManager.beginTransaction().replace(R.id.container, rewardFrag,
                    RewardsSocialInfoFragment::class.java.simpleName)
                    .commit()
        } else if (isComingfromCampaign) {
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            finish()
        }
    }

    private fun addPaymentModesFragment() {
        if (pageLimit!! >= 4) {
            toolbarTitle.setText(resources.getString(R.string.payment_details))
            paymentModesFragment = CampaignPaymentModesFragment.newInstance(isComingFromRewards = true)
            val paymentFrag = paymentModesFragment as Fragment
            supportFragmentManager.beginTransaction().replace(R.id.container, paymentFrag,
                    CampaignPaymentModesFragment::class.java.simpleName)
                    .commit()
        } else {
            finish()
        }
    }

    private fun addPancardDetailFragment() {
        if (pageLimit!! >= 5) {
            panCardDetailsSubmissionFragment = PanCardDetailsSubmissionFragment.newInstance(isComingFromRewards = true)
            val pancardFrag = panCardDetailsSubmissionFragment as Fragment
            supportFragmentManager.beginTransaction().replace(R.id.container, pancardFrag,
                    PanCardDetailsSubmissionFragment::class.java.simpleName)
                    .commit()
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0) {
            removeProgressDialog()
        }
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        FacebookUtils.onActivityResult(this, requestCode, resultCode, data)
    }

    private fun postApiForDefaultPaymantMode(paymentModeId: Int) {
        val proofPostModel = ProofPostModel(id = paymentModeId.toString())
        showProgressDialog(resources.getString(R.string.please_wait))
        BaseApplication.getInstance().retrofit.create(CampaignAPI::class.java).postForDefaultAccount(proofPostModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponseGeneric<ProofPostModel>> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: BaseResponseGeneric<ProofPostModel>) {
                if (t != null) {
                    if (t.code == 200 && t.status == Constants.SUCCESS) {
                        removeProgressDialog()
                        this@RewardsContainerActivity.finish()
                    }
                } else {
                    removeProgressDialog()

                    Toast.makeText(this@RewardsContainerActivity, t.reason.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(e: Throwable) {
                removeProgressDialog()
                Log.e("exception in error", e.message.toString())
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
