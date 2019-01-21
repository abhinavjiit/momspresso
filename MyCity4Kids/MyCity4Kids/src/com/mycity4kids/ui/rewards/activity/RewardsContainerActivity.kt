package com.mycity4kids.ui.rewards.activity

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.mycity4kids.R
import com.mycity4kids.ui.fragment.ChangePreferredLanguageDialogFragment
import com.mycity4kids.ui.rewards.fragment.RewardsFamilyInfoFragment
import com.mycity4kids.ui.rewards.fragment.RewardsPersonalInfoFragment
import com.mycity4kids.ui.rewards.fragment.RewardsSocialInfoFragment

class RewardsContainerActivity : AppCompatActivity(),
        RewardsPersonalInfoFragment.SaveAndContinueListener,
        RewardsSocialInfoFragment.SubmitListener,
        RewardsFamilyInfoFragment.SubmitListener {

    override fun FamilyOnSubmit() {
        addSocialFragment()
    }

    override fun socialOnSubmitListener() {
        this@RewardsContainerActivity.finish()
    }

    override fun profileOnSaveAndContinue() {
        addFamilyFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards_container)

        /*initialize XML components*/
        initializeXMLComponents()

        /*add fragement to container*/
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
        var fragment = RewardsPersonalInfoFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment,
                RewardsPersonalInfoFragment::class.java.simpleName)
                .commit()
    }

    private fun addFamilyFragment() {
        var fragment = RewardsFamilyInfoFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment,
                RewardsFamilyInfoFragment::class.java.simpleName)
                .commit()
    }

    private fun addSocialFragment() {
        var fragment = RewardsSocialInfoFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment,
                RewardsSocialInfoFragment::class.java.simpleName)
                .commit()
    }

}
