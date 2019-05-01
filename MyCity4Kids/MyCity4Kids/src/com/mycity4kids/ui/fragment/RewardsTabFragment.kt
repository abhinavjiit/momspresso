package com.mycity4kids.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.mycity4kids.R
import com.mycity4kids.ui.rewards.activity.RewardsContainerActivity

class RewardsTabFragment : Fragment() {
    private lateinit var textStartReward : TextView
    private lateinit var containerView : View
    private var isRewardsAdded = "0"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.fragment_rewards, container, false)

        textStartReward = containerView.findViewById(R.id.textStartReward)

        if(arguments!!.containsKey("isRewardsAdded")){
            isRewardsAdded = arguments!!.getString("isRewardsAdded", "0")
            if(isRewardsAdded.equals("1")){
                textStartReward.setText(resources.getString(R.string.update))
            }else{
                textStartReward.setText(resources.getString(R.string.rewards_start_now))
            }
        }

        textStartReward.setOnClickListener {
            startActivity(Intent(activity, RewardsContainerActivity::class.java))
        }

        return containerView
    }

}
