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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class RewardsTabFragment : Fragment() {
    private lateinit var textStartReward : TextView
    private lateinit var containerView : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.fragment_rewards, container, false)
        textStartReward = containerView.findViewById(R.id.textStartReward)
        textStartReward.setOnClickListener {
            startActivity(Intent(activity, RewardsContainerActivity::class.java))
        }

        return containerView
    }


}
