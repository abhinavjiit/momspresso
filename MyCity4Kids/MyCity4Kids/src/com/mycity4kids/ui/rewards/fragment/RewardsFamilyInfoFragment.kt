package com.mycity4kids.ui.rewards.fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.RelativeLayout
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
class RewardsFamilyInfoFragment : Fragment() {
    private lateinit var containerView: View
    private lateinit var submitListener: SubmitListener
    private lateinit var layoutNumberOfKids: RelativeLayout
    private lateinit var layoutExptectedDate: RelativeLayout
    private lateinit var textAreYouExpecting: TextView
    private lateinit var layoutMotherExptectedDate: TextView

    companion object {
        @JvmStatic
        fun newInstance() =
                RewardsFamilyInfoFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.fragment_rewards_family_info, container, false)

        /*initialize XML components*/
        initializeXMLComponents()

        return containerView
    }

    private fun initializeXMLComponents() {
        layoutNumberOfKids = containerView.findViewById(R.id.layoutNumberOfKids)
        layoutExptectedDate = containerView.findViewById(R.id.layoutExptectedDate)
        layoutMotherExptectedDate = containerView.findViewById(R.id.layoutMotherExptectedDate)

        textAreYouExpecting = containerView.findViewById(R.id.textAreYouExpecting)

        containerView.findViewById<TextView>(R.id.textSubmit).setOnClickListener {
            submitListener.FamilyOnSubmit()
        }

        containerView.findViewById<RadioGroup>(R.id.radioGroupFamilyType)
                .setOnCheckedChangeListener { radioGroup, i ->
                    when (i) {
                        0 -> {

                        }

                        1 -> {

                        }

                    }
                }

        containerView.findViewById<RadioGroup>(R.id.radioGroupAreMother)
                .setOnCheckedChangeListener { radioGroup, i ->
                    when (i) {
                        R.id.radioNo -> {
                            layoutNumberOfKids.visibility = View.GONE
                            layoutExptectedDate.visibility = View.GONE
                            textAreYouExpecting.visibility = View.GONE
                            layoutMotherExptectedDate.visibility = View.GONE
                        }

                        R.id.radioYes -> {
                            layoutNumberOfKids.visibility = View.VISIBLE
                            layoutExptectedDate.visibility = View.GONE
                            textAreYouExpecting.visibility = View.VISIBLE
                            layoutMotherExptectedDate.visibility = View.GONE
                        }

                        R.id.radioExpecting -> {
                            layoutMotherExptectedDate.visibility = View.VISIBLE
                            layoutNumberOfKids.visibility = View.GONE
                            layoutExptectedDate.visibility = View.VISIBLE
                            textAreYouExpecting.visibility = View.GONE
                        }
                    }
                }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is RewardsContainerActivity) {
            submitListener = context
        }
    }

    interface SubmitListener {
        fun FamilyOnSubmit()
    }

}
