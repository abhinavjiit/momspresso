package com.mycity4kids.ui.campaign.activity;

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.mycity4kids.R
import com.mycity4kids.ui.campaign.fragment.CampaignAddProofFragment
import com.mycity4kids.ui.campaign.fragment.CampaignCongratulationFragment
import com.mycity4kids.ui.campaign.fragment.CampaignPaymentModesFragment
import com.mycity4kids.ui.fragment.CampaignDetailFragment
import com.mycity4kids.ui.fragment.CampaignListFragment

class CampaignContainerActivity : AppCompatActivity(), CampaignAddProofFragment.SubmitListener, CampaignCongratulationFragment.SubmitListener {
    override fun congratulateScreenDone() {
        campaignListFragment()
    }

    override fun proofSubmitDone() {
        addPaymantMode()
    }

    private lateinit var toolbarTitle: TextView
    private var totalPay: Int = 0
    private var name: String? = null
    private var id: Int = 0
    private var campaignDetailFragment: CampaignDetailFragment? = null
    private var campaignListFragment: CampaignListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_container);

        var arrayList = ArrayList<Int>()
        arrayList.add(8)
        addAddProofFragment(15, arrayList)
            //campaignListFragment()
        //addPaymantMode()
        // campaignListFragment()
    }

    private fun campaignListFragment() {
        campaignListFragment = CampaignListFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.container, campaignListFragment,
                CampaignListFragment::class.java.simpleName).addToBackStack("campaignListFragment")
                .commit()
    }


    fun addCampaginDetailFragment(id: Int) {
        campaignDetailFragment = CampaignDetailFragment.newInstance(id)
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
        if (fragmentManager.backStackEntryCount == 1) {
            finish()
        } else
            super.onBackPressed()
    }


    fun setTotalPayOut(totalPayOut: Int) {
        totalPay = totalPayOut
    }

    fun getTotalPayOut(): Int {
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

}
