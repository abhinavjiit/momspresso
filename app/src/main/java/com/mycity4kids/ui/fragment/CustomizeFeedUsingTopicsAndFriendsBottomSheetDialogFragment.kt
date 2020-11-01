package com.mycity4kids.ui.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mycity4kids.R
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.ui.activity.FindFbFriendsActivity
import com.mycity4kids.widget.MomspressoButtonWidget

class CustomizeFeedUsingTopicsAndFriendsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var isSkipped: Boolean = false
    private lateinit var chooseTopicsTextView: MomspressoButtonWidget
    private lateinit var findFriendsTextView: MomspressoButtonWidget
    private lateinit var noNeedToCustomize: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_layout, container, false)
        chooseTopicsTextView = view.findViewById(R.id.chooseTopicsTextView)
        findFriendsTextView = view.findViewById(R.id.findFriendsTextView)
        noNeedToCustomize = view.findViewById(R.id.noNeedToCustomize)
        Utils.shareEventTracking(activity, "Home screen", "Read_Android", "Read_Options")
        chooseTopicsTextView.setOnClickListener {
            Utils.shareEventTracking(activity, "Home screen", "Read_Android", "Read_Follow_Topics")
            val chooseContentTopicsBottomSheetDialogFragment =
                ChooseContentTopicsBottomSheetDialogFragment()
            chooseContentTopicsBottomSheetDialogFragment.show(
                activity?.supportFragmentManager!!,
                "choose_topic"
            )
            dismiss()
        }
        findFriendsTextView.setOnClickListener {
            activity?.let {
                Utils.shareEventTracking(
                    activity,
                    "Home screen",
                    "Read_Android",
                    "Read_Find_Friends"
                )
                val intent = Intent(it, FindFbFriendsActivity::class.java)
                startActivity(intent)
            }
            dismiss()
        }

        noNeedToCustomize.setOnClickListener {
            isSkipped = true
            Utils.shareEventTracking(activity, "Home screen", "Read_Android", "Read_Options_Skip")
            dismiss()
        }
        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!isSkipped) {
            Utils.shareEventTracking(activity, "Home screen", "Read_Android", "Read_Options_Back")
        }

    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
}