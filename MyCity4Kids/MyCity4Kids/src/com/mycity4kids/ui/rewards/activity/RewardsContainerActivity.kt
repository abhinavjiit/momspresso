package com.mycity4kids.ui.rewards.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.facebook.CallbackManager
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.mycity4kids.R
import com.mycity4kids.facebook.FacebookUtils
import com.mycity4kids.interfaces.IFacebookEvent
import com.mycity4kids.ui.campaign.fragment.CampaignPaymentModesFragment
import com.mycity4kids.ui.campaign.fragment.PanCardDetailsSubmissionFragment
import com.mycity4kids.ui.fragment.ChangePreferredLanguageDialogFragment
import com.mycity4kids.ui.rewards.fragment.RewardsFamilyInfoFragment
import com.mycity4kids.ui.rewards.fragment.RewardsPersonalInfoFragment
import com.mycity4kids.ui.rewards.fragment.RewardsSocialInfoFragment
import android.R.id.message
import android.util.Log
import android.widget.Toast
import com.mycity4kids.MessageEvent
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.Constants
import com.mycity4kids.models.campaignmodels.ProofPostModel
import com.mycity4kids.models.response.BaseResponseGeneric
import com.mycity4kids.retrofitAPIsInterfaces.CampaignAPI
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.Subscribe


class RewardsContainerActivity : BaseActivity(),
        RewardsPersonalInfoFragment.SaveAndContinueListener,
        RewardsSocialInfoFragment.SubmitListener, CampaignPaymentModesFragment.SubmitListener, PanCardDetailsSubmissionFragment.SubmitListener, IFacebookEvent {
    private var Id: Int = -1
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

    override fun updateUi(response: Response?) {
    }

    override fun socialOnSubmitListener() {
        pageLimit!! + 1
        addPaymentModesFragment()
//        this@RewardsContainerActivity.finish()
    }

    override fun profileOnSaveAndContinue() {
        addSocialFragment()
    }

    private var callbackManager: CallbackManager? = null
    private var rewardsPersonalInfoFragment: RewardsPersonalInfoFragment? = null
    private var rewardsFamilyInfoFragment: RewardsFamilyInfoFragment? = null
    private var rewardsSocialInfoFragment: RewardsSocialInfoFragment? = null
    private var paymentModesFragment: CampaignPaymentModesFragment? = null
    private var panCardDetailsSubmissionFragment: PanCardDetailsSubmissionFragment? = null
    private var pageLimit: Int? = null
    private var pageNumber: Int? = null
    private var isComingfromCampaign = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards_container)
        (application as BaseApplication).activity = this

        if (intent != null) {
            if (intent.hasExtra("pageLimit")) {
                pageLimit = intent.getIntExtra("pageLimit", 5)
            } else {
                pageLimit = 5
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
        }

        if (pageNumber == 1) {
            addProfileFragment()
        } else if (pageNumber == 3) {
            addSocialFragment()
        } else if (pageNumber == 4) {
            addPaymentModesFragment()
        } else if (pageNumber == 5) {
            addPancardDetailFragment()
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
    }

    private fun addProfileFragment() {
        if (pageLimit!! >= 1) {
            rewardsPersonalInfoFragment = RewardsPersonalInfoFragment.newInstance(isComingFromRewards = true, isComingfromCampaign = isComingfromCampaign)
            supportFragmentManager.beginTransaction().replace(R.id.container, rewardsPersonalInfoFragment,
                    RewardsPersonalInfoFragment::class.java.simpleName)
                    .commit()
        } else {
            finish()
        }
    }

    private fun addSocialFragment() {
        if (pageLimit!! >= 3) {
            rewardsSocialInfoFragment = RewardsSocialInfoFragment.newInstance(isComingFromRewards = true)
            supportFragmentManager.beginTransaction().replace(R.id.container, rewardsSocialInfoFragment,
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

            paymentModesFragment = CampaignPaymentModesFragment.newInstance(isComingFromRewards = true)
            supportFragmentManager.beginTransaction().replace(R.id.container, paymentModesFragment,
                    CampaignPaymentModesFragment::class.java.simpleName)
                    .commit()
        } else {
            finish()
        }
    }

    private fun addPancardDetailFragment() {
        if (pageLimit!! >= 5) {
            panCardDetailsSubmissionFragment = PanCardDetailsSubmissionFragment.newInstance(isComingFromRewards = true)
            supportFragmentManager.beginTransaction().replace(R.id.container, panCardDetailsSubmissionFragment,
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


}
