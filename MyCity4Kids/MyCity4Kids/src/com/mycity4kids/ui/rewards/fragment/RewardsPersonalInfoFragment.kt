package com.mycity4kids.ui.rewards.fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
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
class RewardsPersonalInfoFragment : Fragment() {
    lateinit  var containerView : View
    lateinit var textSaveAndContinue: TextView
    lateinit var saveAndContinueListener: SaveAndContinueListener

    companion object {
        @JvmStatic
        fun newInstance() =
                RewardsPersonalInfoFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        containerView=  inflater.inflate(R.layout.fragment_rewards_personal_info, container, false)

        /*initialize XML components*/
        initializeXMLComponents()
        return containerView
    }

    fun initializeXMLComponents(){
        (containerView.findViewById<TextView>(R.id.textSaveAndContinue)).setOnClickListener {
            saveAndContinueListener.profileOnSaveAndContinue()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is RewardsContainerActivity){
            saveAndContinueListener = context
        }
    }


    interface SaveAndContinueListener{
        fun profileOnSaveAndContinue()
    }
}
