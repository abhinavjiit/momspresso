package com.mycity4kids.ui.rewards.activity

import android.content.Intent
import android.os.Bundle
import com.facebook.CallbackManager
import com.kelltontech.network.Response
import com.kelltontech.ui.BaseActivity
import com.mycity4kids.R
import com.mycity4kids.facebook.FacebookUtils
import com.mycity4kids.interfaces.IFacebookEvent
import com.mycity4kids.ui.fragment.ChangePreferredLanguageDialogFragment
import com.mycity4kids.ui.rewards.fragment.RewardsFamilyInfoFragment
import com.mycity4kids.ui.rewards.fragment.RewardsPersonalInfoFragment
import com.mycity4kids.ui.rewards.fragment.RewardsSocialInfoFragment

class RewardsContainerActivity : BaseActivity(),
        RewardsPersonalInfoFragment.SaveAndContinueListener,
        RewardsSocialInfoFragment.SubmitListener,
        RewardsFamilyInfoFragment.SubmitListener, IFacebookEvent {
    override fun onFacebookEventReceived(response: String?) {
        if (rewardsSocialInfoFragment != null) {
            rewardsSocialInfoFragment!!.updateFaceBookView()
        }
    }

    override fun updateUi(response: Response?) {
    }

    override fun FamilyOnSubmit() {
        addSocialFragment()
    }

    override fun socialOnSubmitListener() {
        this@RewardsContainerActivity.finish()
    }

    override fun profileOnSaveAndContinue() {
        addFamilyFragment()
    }

    private var callbackManager: CallbackManager? = null
    private var rewardsPersonalInfoFragment: RewardsPersonalInfoFragment? = null
    private var rewardsFamilyInfoFragment: RewardsFamilyInfoFragment? = null
    private var rewardsSocialInfoFragment: RewardsSocialInfoFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards_container)

        callbackManager = CallbackManager.Factory.create()

        /*initialize XML components*/
        initializeXMLComponents()

        /*add fragement to container*/
//        addSocialFragment()
        //addFamilyFragment()
        addProfileFragment()


    }

    private fun initializeXMLComponents() {
        findViewById(R.id.langTextView).setOnClickListener {
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
        rewardsPersonalInfoFragment = RewardsPersonalInfoFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.container, rewardsPersonalInfoFragment,
                RewardsPersonalInfoFragment::class.java.simpleName)
                .commit()
    }

    private fun addFamilyFragment() {
        rewardsFamilyInfoFragment = RewardsFamilyInfoFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.container, rewardsFamilyInfoFragment,
                RewardsFamilyInfoFragment::class.java.simpleName)
                .commit()
    }

    private fun addSocialFragment() {
        rewardsSocialInfoFragment = RewardsSocialInfoFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.container, rewardsSocialInfoFragment,
                RewardsSocialInfoFragment::class.java.simpleName)
                .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0) {
            removeProgressDialog()
        }
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        FacebookUtils.onActivityResult(this, requestCode, resultCode, data)
    }
}
