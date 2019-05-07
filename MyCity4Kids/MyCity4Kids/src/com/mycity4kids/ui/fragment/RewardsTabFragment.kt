package com.mycity4kids.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.mycity4kids.R
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity

class RewardsTabFragment : Fragment() {
    private lateinit var textStartReward: TextView
    private lateinit var containerView: View
    private lateinit var textPersonalInfo: View
    private lateinit var textSocial: View
    private lateinit var textPaymentModes: View
    private lateinit var textPanDetails: View
    private lateinit var linearConnectivity: LinearLayout
    private lateinit var relativeParticipate: RelativeLayout

    private var isRewardsAdded = "0"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        containerView = inflater.inflate(R.layout.fragment_rewards, container, false)
        textStartReward = containerView.findViewById(R.id.textStartReward)
        textPersonalInfo = containerView.findViewById(R.id.textPersonalInfo)
        textSocial = containerView.findViewById(R.id.textSocial)
        textPaymentModes = containerView.findViewById(R.id.textPaymentModes)
        textPanDetails = containerView.findViewById(R.id.textPanDetails)
        linearConnectivity = containerView.findViewById(R.id.linearConnectivity)
        relativeParticipate = containerView.findViewById(R.id.relativeParticipate)

        textPersonalInfo.setOnClickListener {
            var intent = Intent(activity, RewardsContainerActivity::class.java)
            intent.putExtra("pageLimit", 2)
            intent.putExtra("pageNumber", 2)
            startActivity(intent)
        }

        textSocial.setOnClickListener {
            var intent = Intent(activity, RewardsContainerActivity::class.java)
            intent.putExtra("pageNumber", 3)
            intent.putExtra("pageLimit", 3)
            startActivity(intent)
        }

        textPaymentModes.setOnClickListener {
            var intent = Intent(activity, RewardsContainerActivity::class.java)
            intent.putExtra("pageNumber", 4)
            intent.putExtra("pageLimit", 4)
            startActivity(intent)
        }

        textPanDetails.setOnClickListener {
            var intent = Intent(activity, RewardsContainerActivity::class.java)
            intent.putExtra("pageNumber", 5)
            intent.putExtra("pageLimit", 5)
            startActivity(intent)
        }

        if (arguments != null) {
            isRewardsAdded = arguments!!.getString("isRewardsAdded", "0")
            if (isRewardsAdded.equals("1")) {
                textStartReward.setText(resources.getString(R.string.update))
                relativeParticipate.visibility = View.GONE
                linearConnectivity.visibility = View.VISIBLE
            } else {
                textStartReward.setText(resources.getString(R.string.rewards_start_now))
                relativeParticipate.visibility = View.VISIBLE
                linearConnectivity.visibility = View.GONE
            }
        }

        textStartReward.setOnClickListener {
            startActivity(Intent(activity, RewardsContainerActivity::class.java))
        }

        return containerView
    }

}
