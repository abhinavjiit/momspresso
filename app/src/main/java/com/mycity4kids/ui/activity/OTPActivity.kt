package com.mycity4kids.ui.activity

import android.os.Bundle
import com.mycity4kids.R
import com.mycity4kids.base.BaseActivity
import com.mycity4kids.ui.activity.phoneLogin.SendSMSFragment

class OTPActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_container)

        val sendSMSFrag = SendSMSFragment()
        supportFragmentManager.beginTransaction().add(
            R.id.container, sendSMSFrag,
            SendSMSFragment::class.java.simpleName
        ).addToBackStack("sendSMSFrag")
            .commit()
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}
