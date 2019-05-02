package com.mycity4kids.ui.campaign.activity;

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.mycity4kids.R
import com.mycity4kids.ui.campaign.fragment.CampaignAddProofFragment
import com.mycity4kids.ui.campaign.fragment.CampaignPaymentModesFragment
import com.mycity4kids.ui.fragment.CampaignDetailFragment
import com.mycity4kids.ui.fragment.CampaignListFragment

class CampaignContainerActivity : AppCompatActivity() {

    private lateinit var toolbarTitle: TextView

    private var campaignDetailFragment: CampaignDetailFragment? = null
    private var campaignListFragment: CampaignListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_container);
        //toolbarTitle = findViewById(R.id.toolbarTitle)

        //    campaignListFragment()

        //      addAddProofFragment()
//        addPaymantMode()

        //campaignListFragment()

        // addAddProofFragment()

//        campaignListFragment()

        addAddProofFragment(15, emptyList<Int>() as ArrayList<Int>)

    }

    private fun campaignListFragment() {
        campaignListFragment = CampaignListFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.container, campaignListFragment,
                CampaignListFragment::class.java.simpleName)
                .commit()
    }


    fun addCampaginDetailFragment(id: Int) {
        campaignDetailFragment = CampaignDetailFragment.newInstance(id)
        supportFragmentManager.beginTransaction().replace(R.id.container, campaignDetailFragment,
                CampaignDetailFragment::class.java.simpleName)
                .commit()
    }

    /* fun addAddProofFragment() {
         var campaignAddProofFragment = CampaignAddProofFragment.newInstance()
         supportFragmentManager.beginTransaction().replace(R.id.container, campaignAddProofFragment,
                 CampaignAddProofFragment::class.java.simpleName)
                 .commit()
     }*/


    private fun addPaymantMode() {
        var campaignPaymentModesFragment = CampaignPaymentModesFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.container, campaignPaymentModesFragment,
                CampaignPaymentModesFragment::class.java.simpleName)
                .commit()
    }

    fun addAddProofFragment(id: Int, deliverableTypeList: ArrayList<Int>) {
        var campaignAddProofFragment = CampaignAddProofFragment.newInstance(id,deliverableTypeList)
        supportFragmentManager.beginTransaction().replace(R.id.container, campaignAddProofFragment,
                CampaignAddProofFragment::class.java.simpleName)
                .commit()
    }

    /*private fun addAddProofFragment() {
        var campaignPaymentModesFragment = CampaignPaymentModesFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.container, campaignPaymentModesFragment,
                CampaignPaymentModesFragment::class.java.simpleName)
                .commit()
    }*/
}
